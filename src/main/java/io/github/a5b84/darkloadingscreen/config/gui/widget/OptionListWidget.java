package io.github.a5b84.darkloadingscreen.config.gui.widget;

import javax.annotation.Nullable;

import io.github.a5b84.darkloadingscreen.config.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.widget.EntryListWidget;

public class OptionListWidget extends EntryListWidget<OptionListWidget.Entry> {

    public static final int SPACING = 20;

    public final TextRenderer textRenderer = client.textRenderer;

    protected int labelWidth = 0;
    protected int inputWidth = 0;



    public OptionListWidget(MinecraftClient client, int width, int height, int top, int bottom) {
        super(client, width, height, top, bottom, 25);
    }



    @Override
    public int addEntry(Entry entry) {
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

    @Override
    protected int getScrollbarPositionX() {
        // Affiche la barre pour scroller tout à droite
        // (vanilla c'est à une distance fixe du milieu, donc pas bon)
        return width - Util.SCROLLBAR_WIDTH;
    }



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
		public @Nullable Element getFocused() { return focused; }

		@Override
		public void setFocused(@Nullable Element focused) { this.focused = focused; }

    }

}
