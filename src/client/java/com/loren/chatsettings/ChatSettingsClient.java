package com.loren.chatsettings;

import com.loren.chatsettings.Filter.Filter;
import com.loren.chatsettings.commands.CSCommands;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
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

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("foo")
                .executes(context -> {
                            context.getSource().sendFeedback(Component.literal("Called foo without bar"));
                            return 1;
                        }
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("bar")
                        .executes(context -> {
                            context.getSource().sendFeedback(Component.literal("Called foo with bar"));
                            return 1;
                        })
                )
        ));
    }
}