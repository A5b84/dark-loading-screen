package io.github.a5b84.darkloadingscreen.config.gui.widget;

import javax.annotation.Nonnull;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.widget.EntryListWidget;

public class OptionListWidget extends EntryListWidget<OptionListWidget.Entry> {

    public final TextRenderer textRenderer = client.textRenderer;
    public static final int SPACING = 20;
    public static final int ENTRY_HEIGHT = 20;

    protected int labelWidth = 0;
    protected int inputWidth = 0;



    public OptionListWidget(MinecraftClient client, int width, int height, int top, int bottom, int itemHeight) {
        super(client, width, height, top, bottom, itemHeight);
    }



    public int add(@Nonnull Entry entry) {
        final int entryLabelWidth = entry.getLabelWidth();
        if (entryLabelWidth > labelWidth) labelWidth = entryLabelWidth;
        final int entryInputWidth = entry.getInputWidth();
        if (entryInputWidth > inputWidth) inputWidth = entryInputWidth;

        return super.addEntry(entry);
    }



    @Override
    public int getRowWidth() {
        return labelWidth + SPACING + inputWidth
            + SPACING + ResetButton.getButtonWidth();
    }

    public int getLabelWidth() { return labelWidth; }
    public int getInputWidth() { return inputWidth; }



    public static abstract class Entry extends EntryListWidget.Entry<Entry> {

        public final OptionListWidget list;

        public Entry(OptionListWidget list) {
            this.list = list;
        }

        public abstract int getLabelWidth();
        public abstract int getInputWidth();

    }

}
