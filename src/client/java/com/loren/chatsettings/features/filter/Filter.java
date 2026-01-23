package com.loren.chatsettings.features.filter;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import static com.loren.chatsettings.ChatSettings.MOD_ID;
import static com.loren.chatsettings.ChatSettingsClient.MOD_CONFIG_DIR;


public class Filter {

    public static final String FILTER_DIR = MOD_CONFIG_DIR + "/filter/";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static final List<Pattern> blacklistList = new ArrayList<>();
    private static final List<Pattern> whitelistList = new ArrayList<>();

    private static boolean registered = false; // to check if it's registered only once

    private Filter() {
    }

    public static void init() {
        try {
            initFilterFiles();
            readFilterFiles();
            registerChatFilter();
        } catch (IOException e) {
            LOGGER.error(() -> "An error occurred with filter setting:\n" + e.getMessage());
        }
    }

    private static void initFilterFiles() throws IOException {
        File file = new File(FILTER_DIR);
        if (!file.exists()) file.mkdirs(); // create the directories if they don't exist.

        File file1 = new File(ListType.BLACKLIST.file);
        File file2 = new File(ListType.WHITELIST.file);

        if (!file1.exists()) file1.createNewFile();
        if (!file2.exists()) file2.createNewFile();
    }

    public static void readFilterFiles() {
        try (BufferedReader br = Files.newBufferedReader(Paths.get(ListType.BLACKLIST.file))) {
            List<Pattern> patterns = compilePatterns(br.readAllLines());
            blacklistList.clear();
            blacklistList.addAll(patterns);
        } catch (IOException e) {
            LOGGER.warn(e::getMessage);
        }

        try (BufferedReader br = Files.newBufferedReader(Paths.get(ListType.WHITELIST.file))) {
            List<Pattern> patterns = compilePatterns(br.readAllLines());
            whitelistList.clear();
            whitelistList.addAll(patterns);
        } catch (IOException e) {
            LOGGER.warn(e::getMessage);
        }

        LOGGER.info(() -> "Blacklisted lines: \n" + blacklistList);
        LOGGER.info(() -> "Whitelisted lines: \n" + whitelistList);
    }

    public static void registerChatFilter() {
        if (registered) return;
        registered = true;

        ClientReceiveMessageEvents.ALLOW_GAME.register(Filter::filterMethod);
        ClientReceiveMessageEvents.ALLOW_CHAT.register(Filter::filterMethod);
    }

    private static boolean filterMethod(Component text, boolean _b) {
        String textString = text.getString();
        if (containsListSubstring(textString, whitelistList)) {
            return true;
        }
        return !containsListSubstring(textString, blacklistList);
    }

    private static boolean filterMethod(Component message, PlayerChatMessage playerChatMessage, GameProfile sender, ChatType.Bound boundChatType, Instant timeStamp) {
        return filterMethod(message, true);
    }

    public static Result addFilter(ListType type, String line) {
        if (line == null || line.trim().isEmpty()) return Result.INVALID_LINE;

        if (type.list.stream().anyMatch(p -> p.pattern().equals(line))) return Result.ALREADY_EXISTS;

        // check if there's a newline at the end, if not, add 1.
        boolean addNewLine = false;
        try (RandomAccessFile raf = new RandomAccessFile(type.file, "r")) {
            if (raf.length() > 0) {
                raf.seek(raf.length() - 1); // point to the last character
                if (raf.readByte() != '\n') {
                    addNewLine = true;
                }
            }
        } catch (IOException e) {
            LOGGER.warn(() -> "Failed to add filter:\n" + e.getMessage());
            return Result.IO_EXCEPTION;
        }

        try (BufferedWriter bw = Files.newBufferedWriter(Paths.get(type.file), StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            if (addNewLine) bw.newLine();
            bw.write(line);
            bw.newLine();
            type.list.add(Pattern.compile(line, Pattern.CASE_INSENSITIVE));
        } catch (PatternSyntaxException | IOException e) {
            LOGGER.warn(() -> "Failed to add filter:\n" + e.getMessage());
            return Result.IO_EXCEPTION;
        }
        return Result.SUCCESS;
    }

    public static Result removeFilter(ListType type, String lineToRemove) {
        if (lineToRemove == null || lineToRemove.trim().isEmpty()) return Result.INVALID_LINE;

        Path original = Paths.get(type.file);
        Path temp = Paths.get(type.file + ".tmp");

        Pattern patternToRemove = Pattern.compile(lineToRemove, Pattern.CASE_INSENSITIVE);

        try (BufferedReader br = Files.newBufferedReader(original);
             BufferedWriter bw = Files.newBufferedWriter(temp)) {
            String line;

            while ((line = br.readLine()) != null) {
                if (!patternToRemove.matcher(line).matches()) { //writing the lines that don't match in the temp file
                    bw.write(line);
                    bw.newLine();
                }
            }

            // remove from the list
            boolean exists = type.list.removeIf(p -> p.pattern().equals(lineToRemove));
            if (!exists) return Result.NOT_FOUND;

        } catch (IOException e) {
            LOGGER.warn(() -> "Failed to remove filter:\n" + e.getMessage());
            return Result.IO_EXCEPTION;
        }

        try {
            Files.move(temp, original, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.warn(() -> "Failed to replace original file:\n" + e.getMessage());
            type.list.add(patternToRemove); // re-add removed line.
            return Result.IO_EXCEPTION;
        }
        return Result.SUCCESS;
    }

    public static List<Pattern> getList(ListType type) {
        return Collections.unmodifiableList(type.list);
    }

    public static String getListAsString(ListType listType) {
        List<Pattern> list = Filter.getList(listType);
        if (list.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i != list.size() - 1) sb.append('\n');
        }
        return sb.toString();
    }

    private static boolean containsListSubstring(String msg, List<Pattern> list) {
        for (Pattern line : list) {
            if (line.matcher(msg).find()) {
                return true;
            }
        }
        return false;
    }

    // convert lists of String into a lists of Pattern
    // when using this function, do something to add it to the existing list objects instead of equaling to it
    private static List<Pattern> compilePatterns(List<String> stringList) {
        List<Pattern> patternList = new ArrayList<>();

        if (stringList == null) return patternList;

        for (String line : stringList) {
            if (line == null || line.trim().isEmpty()) continue;
            try {
                patternList.add(Pattern.compile(line, Pattern.CASE_INSENSITIVE));
            } catch (PatternSyntaxException e) {
                LOGGER.warn(() -> "Invalid pattern: " + line);
            }
        }
        return patternList;
    }

    public enum ListType {
        BLACKLIST(FILTER_DIR + "/blacklist.txt", blacklistList),
        WHITELIST(FILTER_DIR + "/whitelist.txt", whitelistList);

        private final String file;
        private final List<Pattern> list;

        ListType(String file, List<Pattern> list) {
            this.file = file;
            this.list = list;
        }
    }

    public enum Result {
        INVALID_LINE,
        IO_EXCEPTION,
        SUCCESS,
        NOT_FOUND,
        ALREADY_EXISTS
    }
}
