package com.loren.chatsettings.features;

import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.network.chat.Component;

public class FilterScreen extends OptionsSubScreen {
    private FilterList list;

    public FilterScreen(Screen lastScreen, Options options, Component title) {
        super(lastScreen, options, title.getString().isEmpty() ? Component.literal("Filter Settings (Chat Settings)") : title);
    }

    @Override
    protected void addContents() {
        this.list = this.layout.addToContents(new FilterList(this.minecraft, this));
    }

    @Override
    protected void addOptions() {

    }
}
