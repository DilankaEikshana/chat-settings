package com.loren.chatsettings;

import com.loren.chatsettings.features.FilterScreen;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

public class KeyBinds {
    public static final KeyMapping.Category CATEGORY = new KeyMapping.Category(
            Identifier.fromNamespaceAndPath(ChatSettings.MOD_ID, "main")
    );

    private static final KeyMapping openFilter = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "key.chat-settings.filter",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_J,
                    CATEGORY
            )
    );

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (KeyBinds.openFilter.consumeClick()) {
                client.setScreen(new FilterScreen(null, client.options, Component.empty()));
            }
        });
    }
}
