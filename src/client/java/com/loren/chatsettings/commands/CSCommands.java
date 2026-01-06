package com.loren.chatsettings.commands;

import com.loren.chatsettings.Filter.Filter;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

public class CSCommands {
    public static void init() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) ->
                dispatcher.register(LiteralArgumentBuilder.<FabricClientCommandSource>literal("cs")
                        .executes(context -> {
                            context.getSource().sendFeedback(Component.literal("/cs <blacklist/whitelist> <string/regex>"));
                                    return 1;
                                }
                        )
                        .then(FilterCommands.commandList(Filter.ListType.BLACKLIST))
                        .then(FilterCommands.commandList(Filter.ListType.WHITELIST))
                )
        );
    }
}