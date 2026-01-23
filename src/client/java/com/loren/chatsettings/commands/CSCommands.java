package com.loren.chatsettings.commands;


import com.loren.chatsettings.ChatSettingsClient;
import com.loren.chatsettings.constants.CSColor;
import com.loren.chatsettings.constants.Constants;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;


public class CSCommands {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register(CSCommands::displayAllCommands);
        ClientCommandRegistrationCallback.EVENT.register(FilterCommands::registerLists);
        ClientCommandRegistrationCallback.EVENT.register(SoundCommands::registerSounds);

        ClientCommandRegistrationCallback.EVENT.register(CSCommands::displayAllCommands);
    }

    private static void displayAllCommands(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(literal(ChatSettingsClient.NAMESPACE).executes(context -> {
            context.getSource().sendFeedback(Constants.PREFIX.get().append(Component.literal(" Commands").withStyle(ChatFormatting.GRAY))
                    .append(createCommandDescription("/cs filter", Component.translatable("chat-settings.filter")))
                    .append(createCommandDescription("/cs blacklist/whitelist add <line>", Component.translatable("chat-settings.filter.add")))
                    .append(createCommandDescription("/cs blacklist/whitelist remove <line>", Component.translatable("chat-settings.filter.remove")))
                    .append(createCommandDescription("/cs blacklist/whitelist list", Component.translatable("chat-settings.filter.list")))

            );
            return 1;
        }));
    }

    private static MutableComponent createCommandDescription(String command, MutableComponent description) {
        return Component.literal("\n")
                .append(command)
                .append(Component.literal(" -> ").withStyle(ChatFormatting.GRAY))
                .append(description.withColor(CSColor.LIGHT_GRAY));

    }
}