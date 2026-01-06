package com.loren.chatsettings.commands;

import com.loren.chatsettings.features.Filter;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.regex.Pattern;

class FilterCommands {

    public static LiteralArgumentBuilder<FabricClientCommandSource> commandList(Filter.ListType listType) {
        return LiteralArgumentBuilder.<FabricClientCommandSource>literal(listType.commandName)
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("add")
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("pattern", StringArgumentType.greedyString())
                                .executes(context -> {
                                    Filter.Result value = Filter.addFilter(listType, StringArgumentType.getString(context, "pattern"));
                                    switch (value) {
                                        case SUCCESS ->
                                                context.getSource().sendFeedback(Component.literal("Added to " + listType.name().toLowerCase() + " successfully!"));
                                        case INVALID_LINE ->
                                                context.getSource().sendFeedback(Component.literal("Invalid filter line!"));
                                        case ALREADY_EXISTS ->
                                                context.getSource().sendFeedback(Component.literal("Line already exists!"));
                                        case IO_EXCEPTION ->
                                                context.getSource().sendFeedback(Component.literal("Failed to add the filter! (io exception)"));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("remove")
                        .then(RequiredArgumentBuilder.<FabricClientCommandSource, String>argument("pattern", StringArgumentType.greedyString())
                                .executes(context -> {
                                    Filter.Result value = Filter.removeFilter(listType, StringArgumentType.getString(context, "pattern"));
                                    switch (value) {
                                        case SUCCESS ->
                                                context.getSource().sendFeedback(Component.literal("Removed from " + listType.name().toLowerCase() + " successfully!"));
                                        case INVALID_LINE ->
                                                context.getSource().sendFeedback(Component.literal("Invalid filter line!"));
                                        case NOT_FOUND ->
                                                context.getSource().sendFeedback(Component.literal("Line not found!"));
                                        case IO_EXCEPTION ->
                                                context.getSource().sendFeedback(Component.literal("Failed to add the filter! (io exception)"));
                                    }
                                    return 1;
                                })
                        )
                )
                .then(LiteralArgumentBuilder.<FabricClientCommandSource>literal("list")
                        .executes(context -> {
                            List<Pattern> list = Filter.getList(listType);
                            StringBuilder sb = new StringBuilder();
                            if (list.isEmpty()) {
                                sb.append("There are no ").append(listType.name().toLowerCase()).append("ed phrases");
                            } else {
                                sb.append(listType.name().toLowerCase()).append("ed phrases:");
                                for (Pattern p : list) {
                                    sb.append('\n');
                                    sb.append(" » ").append(p.pattern());
                                }
                            }

                            context.getSource().sendFeedback(Component.literal(sb.toString()));
                            return 1;
                        })
                )
                .executes(context -> {
                    context.getSource().sendFeedback(Component.literal("/cs <blacklist/whitelist> <string/regex>"));
                    return 1;
                });

    }
}
