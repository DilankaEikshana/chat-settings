package com.loren.chatsettings;

import com.loren.chatsettings.constants.Constants;
import com.loren.chatsettings.features.filter.Filter;
import com.loren.chatsettings.commands.CSCommands;
import com.loren.chatsettings.features.sounds.Sounds;
import net.fabricmc.api.ClientModInitializer;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;

import java.io.File;


public class ChatSettingsClient implements ClientModInitializer {

    public static final String NAMESPACE = "cs";
    public static final String MOD_CONFIG_DIR = "config/chat_settings/";

    private boolean joinMessageShown = false;

    @Override
    public void onInitializeClient() {
        // make main config folder
        File mainConfigDir = new File(MOD_CONFIG_DIR);
        if (!mainConfigDir.exists()) mainConfigDir.mkdirs();

        CSCommands.init();
        Sounds.init();


        Filter.init();

        displayJoinMessage();

        KeyBinds.register();
    }

    private void displayJoinMessage() {
        ClientPlayConnectionEvents.JOIN.register((_, _, client) -> {
            if (!joinMessageShown && client.player != null) {
                client.player.displayClientMessage(Constants.PREFIX.get()
                        .append(" ")
                        .append(Component.literal("[View blacklist]").withStyle(style -> style
                                .withClickEvent(new ClickEvent.RunCommand("/" + NAMESPACE + " blacklist list"))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(Filter.getListAsString(Filter.ListType.BLACKLIST))))
                                .withColor(ChatFormatting.GRAY))
                        )
                        .append(" ")
                        .append(Component.literal("[View whitelist]").withStyle(style -> style
                                .withClickEvent(new ClickEvent.RunCommand("/" + NAMESPACE + " whitelist list"))
                                .withHoverEvent(new HoverEvent.ShowText(Component.literal(Filter.getListAsString(Filter.ListType.WHITELIST))))
                                .withColor(ChatFormatting.GRAY))
                        ), false);
                joinMessageShown = true;
            }
        });
    }
}