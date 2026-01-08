package com.loren.chatsettings.constants;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public class Constants {
    private Constants() {
    }
    public static final Supplier<MutableComponent> PREFIX = () -> {
        MutableComponent component = Component.empty().append(Component.literal("[").withStyle(ChatFormatting.BLACK));
        String prefix = "Chat Settings";
        for (int i = 0; i < prefix.length(); i++) {
            component.append(Component.literal(String.valueOf(prefix.charAt(i))).withColor(i % 2 == 0 ? CSColor.LIGHT_GRAY : CSColor.WHITE));
        }
        component.append(Component.literal("]").withStyle(ChatFormatting.BLACK));
        return component;
    };
}
