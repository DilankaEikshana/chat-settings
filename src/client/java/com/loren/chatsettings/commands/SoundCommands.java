package com.loren.chatsettings.commands;

import com.loren.chatsettings.ChatSettingsClient;
import com.loren.chatsettings.features.sounds.Sounds;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

public class SoundCommands {

    public static void registerSounds(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(literal(ChatSettingsClient.NAMESPACE)
                .then(literal("ping")
                        .then(literal("true")
                                .executes(context -> {
                                    Sounds.setAllowPings(true);
                                    context.getSource().sendFeedback(Component.literal("You will now hear pings!"));
                                    return 1;
                                }))
                        .then(literal("false")
                                .executes(context -> {
                                    Sounds.setAllowPings(false);
                                    context.getSource().sendFeedback(Component.literal("You will no longer hear pings!"));
                                    return 1;
                                })
                        )
                )
        );
    }
}
