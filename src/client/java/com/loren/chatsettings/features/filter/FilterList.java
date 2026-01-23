package com.loren.chatsettings.features.filter;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.StringWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.regex.Pattern;

public class FilterList extends ContainerObjectSelectionList<FilterList.@NotNull Entry> {
    private static final int ITEM_HEIGHT = 24;
    private final FilterScreen filterScreen;

    // created in FilterList to not make it recreate everytime the screen reloads to preserve the written message
    private final CreateFilterEntry blackListCreateFilterEntry;
    private final CreateFilterEntry whitelistCreateFilterEntry;

    private boolean blacklistOpen = false;
    private boolean whitelistOpen = false;


    public FilterList(Minecraft minecraft, FilterScreen filterScreen) {
        super(minecraft, filterScreen.width, filterScreen.layout.getContentHeight(), filterScreen.layout.getHeaderHeight(), ITEM_HEIGHT);
        this.filterScreen = filterScreen;

        blackListCreateFilterEntry  = new CreateFilterEntry(Filter.ListType.BLACKLIST);
        whitelistCreateFilterEntry = new CreateFilterEntry(Filter.ListType.WHITELIST);

        rebuild();
    }

    private void rebuild() {
        clearEntries();

        addEntry(new CategoryEntry(
                "blacklist",
                blacklistOpen,
                () -> {
                    blacklistOpen = !blacklistOpen;
                    rebuild();
                }
        ));

        addEntry(blackListCreateFilterEntry);

        if (blacklistOpen) {
            for (Pattern p : Filter.getList(Filter.ListType.BLACKLIST)) {
                String phrase = p.pattern();
                addEntry(new FilterList.PhraseEntry(
                        phrase,
                        () -> {
                            Filter.removeFilter(Filter.ListType.BLACKLIST, phrase);
                            rebuild();
                        }
                ));
            }
        }

        addEntry(new CategoryEntry(
                "whitelist",
                whitelistOpen,
                () -> {
                    whitelistOpen = !whitelistOpen;
                    rebuild();
                }
        ));

        addEntry(whitelistCreateFilterEntry);

        if (whitelistOpen) {
            for (Pattern p : Filter.getList(Filter.ListType.WHITELIST)) {
                String phrase = p.pattern();
                addEntry(new FilterList.PhraseEntry(
                        phrase,
                        () -> {
                            Filter.removeFilter(Filter.ListType.WHITELIST, phrase);
                            rebuild();
                        }
                ));
            }
        }
    }

    @Override
    public int getRowWidth() {
        return 400;
    }

    // blacklist and whitelist categories
    public static class CategoryEntry extends FilterList.Entry {
        private final Button toggle;
        private final StringWidget title;

        CategoryEntry(String title, boolean open, Runnable toggleFunc) {
            this.title = new StringWidget(Component.literal(title).withStyle(ChatFormatting.BOLD), Minecraft.getInstance().font);
            this.toggle = Button.builder(Component.literal(open ? "▼" : "▲"), (_) -> toggleFunc.run())
                    .bounds(0, 0, 20, 20)
                    .build();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(toggle, title);
        }

        @Override
        public void renderContent(GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float a) {

            graphics.fill(getContentX() - 5, getContentY() - 2,
                    getContentX() + getContentWidth() + 5, getContentY() + getContentWidth() + 5,
                    0xb6b6b6);

            toggle.setPosition(getContentX(), getContentY());
            toggle.render(graphics, mouseX, mouseY, a);

            title.setPosition(getContentX() + 40, getContentY() + 5);
            title.setMaxWidth(200);
            title.render(graphics, mouseX, mouseY, a);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(toggle, title);
        }
    }

    // for the edit boxes and the add button to add new filters
    public class CreateFilterEntry extends FilterList.Entry {
        private final EditBox filter;
        private final Button add;

        CreateFilterEntry(Filter.ListType listType) {
            this.add = Button.builder(Component.literal("add (+)"), (_) -> addFilter(listType))
                    .bounds(0, 0, 60, 20)
                    .build();

            this.filter = new EditBox(Minecraft.getInstance().font, 0, 0, Component.literal("Enter filter"));
        }

        private void addFilter(Filter.ListType listType) {
            if (filter.getValue().isEmpty()) {
                filter.setFocused(true);
                return;
            }
            Filter.addFilter(listType, filter.getValue());
            filter.setValue("");
            rebuild();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(filter, add);
        }

        @Override
        public void renderContent(@NotNull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float a) {
            filter.setPosition(getContentX(), getContentY());
            filter.setSize(getContentWidth() - 70, 20);
            filter.setHint(Component.literal("Enter phrase"));
            filter.setEditable(true);
            filter.render(graphics, mouseX, mouseY, a);

            add.setPosition(getContentX() + getContentWidth() - 60, getContentY());
            add.render(graphics, mouseX, mouseY, a);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(filter, add);
        }
    }

    // for the list of phrases in each blacklist and whitelist
    public static class PhraseEntry extends FilterList.Entry {
        private final StringWidget phraseWidget;
        private final Button copy;
        private final Button trash;

        PhraseEntry(String phrase, Runnable trashFunc) {

            this.phraseWidget = new StringWidget(Component.literal(phrase).withStyle(style -> style
                    .withHoverEvent(new HoverEvent.ShowText(Component.literal(phrase)))

            ), Minecraft.getInstance().font);

            this.copy = Button.builder(Component.literal("\uD83D\uDCCB"), (_) -> Minecraft.getInstance().keyboardHandler.setClipboard(phrase))
                    .bounds(0, 0, 20, 20)
                    .build();

            this.trash = Button.builder(Component.literal("\uD83D\uDDD1"), (_) -> trashFunc.run())
                    .bounds(0, 0, 20, 20)
                    .build();
        }

        @Override
        public @NotNull List<? extends NarratableEntry> narratables() {
            return List.of(trash, phraseWidget, copy);
        }

        @Override
        public void renderContent(@NotNull GuiGraphics graphics, int mouseX, int mouseY, boolean hovered, float a) {
            phraseWidget.setPosition(getContentX() + 40, getContentY());
            phraseWidget.setMaxWidth(300);
            phraseWidget.render(graphics, mouseX, mouseY, a);

            copy.setPosition(getContentX() + 300 + 30, getContentY());
            copy.render(graphics, mouseX, mouseY, a);

            trash.setPosition(getContentX() + 300 + 60, getContentY());
            trash.render(graphics, mouseX, mouseY, a);
        }

        @Override
        public @NotNull List<? extends GuiEventListener> children() {
            return List.of(trash, phraseWidget, copy);
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<FilterList.@NotNull Entry> {
    }
}
