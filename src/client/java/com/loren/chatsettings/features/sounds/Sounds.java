package com.loren.chatsettings.features.sounds;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.sounds.SoundEvents;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

import static com.loren.chatsettings.ChatSettings.MOD_ID;
import static com.loren.chatsettings.ChatSettingsClient.MOD_CONFIG_DIR;

public class Sounds {
    public static final String SOUND_FILE = MOD_CONFIG_DIR + "/sounds.txt";
    private static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    private static String playerName;

    private static boolean allowPings = true;

    // update values based on file
    public static void init() {
        initFiles();
        registerPings();
    }



    private static void initFiles() {
        Path path = Paths.get(SOUND_FILE);
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path.getParent());
                Files.writeString(path, "ping_player=true");
            } catch (IOException e) {
                LOGGER.warn(() -> "Failed to read sounds config: " + e.getMessage());
            }
            allowPings = true;
            return;
        }

        try(BufferedReader br = Files.newBufferedReader(path)) {


            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;

                String[] parts = line.split("=", 2);
                if (parts.length != 2) continue;

                // change if to switch when adding more sound functions
                if (parts[0].equals("ping_player")) {
                    allowPings = Boolean.parseBoolean(parts[1]);
                }

            }

        } catch (IOException e) {
            LOGGER.warn(() -> "Failed to read ping status: " + e.getMessage());
            allowPings = true;
        }
    }

    private static void registerPings() {
        ClientReceiveMessageEvents.GAME.register(Sounds::pingMethod);
        ClientReceiveMessageEvents.CHAT.register(Sounds::pingMethod);
    }

    private static void pingMethod(Component component, PlayerChatMessage playerChatMessage, GameProfile gameProfile, ChatType.Bound bound, Instant instant) {
        pingMethod(component, true);
    }

    private static void pingMethod(Component component, boolean b) {
        updatePlayerName();
        if (!allowPings) return;
        if (component.getString().toLowerCase().contains(playerName.toLowerCase())) {
            pingPlayer();
        }
    }

    public static void pingPlayer() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            client.player.playSound(
                    SoundEvents.EXPERIENCE_ORB_PICKUP,
                    1.0f,
                    1.0f
            );
        }
    }

    private static void updatePlayerName() {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            playerName = client.player.getName().getString();
        }

    }

    public static void setAllowPings(boolean allowPings) {
        Sounds.allowPings = allowPings;
        updateFile();
    }

    public static boolean getAllowPings() {
        return allowPings;
    }

    private static void updateFile() {
        try(BufferedWriter bw = Files.newBufferedWriter(Paths.get(SOUND_FILE))) {
            bw.write("ping_player=" + allowPings);
            bw.newLine();

        } catch (IOException e) {
            LOGGER.warn(() -> "Failed to update sound settings: " + e.getMessage());
        }
    }
}
