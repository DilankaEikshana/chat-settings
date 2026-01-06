package com.loren.chatsettings;

import com.loren.chatsettings.features.Filter;
import com.loren.chatsettings.commands.CSCommands;
import net.fabricmc.api.ClientModInitializer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

import static com.loren.chatsettings.ChatSettings.MOD_ID;

public class ChatSettingsClient implements ClientModInitializer {
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public static final String MOD_CONFIG_DIR = "config/chat_settings/";

    @Override
    public void onInitializeClient() {
        // make main config folder
        File mainConfigDir = new File(MOD_CONFIG_DIR);
        if (!mainConfigDir.exists()) mainConfigDir.mkdirs();

        CSCommands.init();
        Filter.init();
    }
}