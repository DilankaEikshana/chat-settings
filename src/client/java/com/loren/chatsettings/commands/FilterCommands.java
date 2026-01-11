package com.loren.chatsettings.commands;

import com.loren.chatsettings.ChatSettingsClient;
import com.loren.chatsettings.features.Filter;
import com.loren.chatsettings.features.FilterScreen;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.regex.Pattern;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal;

class FilterCommands {

    public static final String ARG_PATTERN = "pattern";

    public static void registerLists(CommandDispatcher<FabricClientCommandSource> dispatcher, CommandBuildContext commandBuildContext) {
        dispatcher.register(literal(ChatSettingsClient.NAMESPACE)

                .then(literal("blacklist")
                        .then(literal("add").then(argument(ARG_PATTERN, StringArgumentType.greedyString())
                                .executes(context -> addFilter(context, Filter.ListType.BLACKLIST))))
                        .then(literal("remove").then(argument(ARG_PATTERN, StringArgumentType.greedyString())
                                .executes(context -> removeFilter(context, Filter.ListType.BLACKLIST))))
                        .then(literal("list").executes(context -> displayList(context, Filter.ListType.BLACKLIST)))
                )

                .then(literal("whitelist")
                        .then(literal("add").then(argument(ARG_PATTERN, StringArgumentType.greedyString())
                                .executes(context -> addFilter(context, Filter.ListType.WHITELIST))))
                        .then(literal("remove").then(argument(ARG_PATTERN, StringArgumentType.greedyString())
                                .executes(context -> removeFilter(context, Filter.ListType.WHITELIST))))
                        .then(literal("list").executes(context -> displayList(context, Filter.ListType.WHITELIST)))
                )

                .then(literal("filter").executes(context -> {
                    Minecraft client = context.getSource().getClient();
                    client.execute(() -> client.setScreen(new FilterScreen(null, client.options, Component.literal("Filter Settings"))));
                    return 1;
                }))
        );
    }

    private static int addFilter(CommandContext<FabricClientCommandSource> context, Filter.ListType listType) {
        Filter.Result value = Filter.addFilter(listType, StringArgumentType.getString(context, ARG_PATTERN));
        switch (value) {
            case SUCCESS ->
                    context.getSource().sendFeedback(Component.literal("Added to " + listType.name().toLowerCase() + " successfully!"));
            case INVALID_LINE -> context.getSource().sendFeedback(Component.literal("Invalid filter line!"));
            case ALREADY_EXISTS -> context.getSource().sendFeedback(Component.literal("Line already exists!"));
            case IO_EXCEPTION ->
                    context.getSource().sendFeedback(Component.literal("Failed to add the filter! (io exception)"));
        }
        return 1;
    }

    private static int removeFilter(CommandContext<FabricClientCommandSource> context, Filter.ListType listType) {
        Filter.Result value = Filter.removeFilter(listType, StringArgumentType.getString(context, ARG_PATTERN));
        switch (value) {
            case SUCCESS ->
                    context.getSource().sendFeedback(Component.literal("Removed from " + listType.name().toLowerCase() + " successfully!"));
            case INVALID_LINE -> context.getSource().sendFeedback(Component.literal("Invalid filter line!"));
            case NOT_FOUND -> context.getSource().sendFeedback(Component.literal("Line not found!"));
            case IO_EXCEPTION ->
                    context.getSource().sendFeedback(Component.literal("Failed to add the filter! (io exception)"));
        }
        return 1;
    }

    private static int displayList(CommandContext<FabricClientCommandSource> context, Filter.ListType listType) {
        List<Pattern> list = Filter.getList(listType);
        StringBuilder sb = new StringBuilder();
        if (list.isEmpty()) {
            sb.append("There are no ").append(listType.name().toLowerCase()).append("ed phrases");
        } else {
            sb.append(listType.name().toLowerCase()).append("ed phrases:");
            for (Pattern p : list) {
                sb.append("\n » ").append(p.toString());
            }
        }

        context.getSource().sendFeedback(Component.literal(sb.toString()));
        return 1;
    }
}
