package io.github.a5b84.darkloadingscreen.config.gui.widget;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import io.github.a5b84.darkloadingscreen.config.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.EntryListWidget;

public class OptionListWidget extends EntryListWidget<OptionListWidget.Entry> {

    public final TextRenderer textRenderer = client.textRenderer;
    public static final int SPACING = 20;
    public static final int ENTRY_HEIGHT = 25;

    protected int labelWidth = 0;
    protected int inputWidth = 0;



    public OptionListWidget(MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom, ENTRY_HEIGHT);
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



    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return Util.unselectingMouseClicked(this, super::mouseClicked, mouseX, mouseY, button);
    }



    public static abstract class Entry extends EntryListWidget.Entry<Entry>
    implements ParentElement {

        public final OptionListWidget list;
        private boolean dragging = false;
        private @Nullable Element focused;

        public Entry(OptionListWidget list) {
            this.list = list;
        }

        public abstract int getLabelWidth();
        public abstract int getInputWidth();

        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            return Util.unselectingMouseClicked(
                this, ParentElement.super::mouseClicked, mouseX, mouseY, button
            );
        }

		@Override
		public boolean isDragging() { return dragging; }

		@Override
		public void setDragging(boolean dragging) { this.dragging = dragging; }

		@Override
		public Element getFocused() { return focused; }

		@Override
		public void setFocused(Element focused) { this.focused = focused; }

    }

}
