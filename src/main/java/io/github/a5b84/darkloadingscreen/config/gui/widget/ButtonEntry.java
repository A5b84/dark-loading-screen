package io.github.a5b84.darkloadingscreen.config.gui.widget;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import io.github.a5b84.darkloadingscreen.config.gui.ConfigScreen;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ButtonWidget.PressAction;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class ButtonEntry extends OptionListWidget.Entry {

    public final ButtonWidget button;
    private final List<? extends Element> children;

    public ButtonEntry(OptionListWidget list, int height, Text message, @Nullable PressAction onPress) {
        super(list);
        button = new ButtonWidget(
            0, 0, ConfigScreen.BUTTON_WIDTH, height,
            message, onPress
        );
        children = Collections.singletonList(button);
    }



    @Override
    public void render(
        MatrixStack matrices, int index, int y, int x,
        int entryWidth, int entryHeight, int mouseX, int mouseY,
        boolean hovered, float tickDelta
    ) {
        button.x = x + (entryWidth - button.getWidth()) / 2;
        button.y = y;
        button.render(matrices, mouseX, mouseY, tickDelta);
    }



    // Contribue pas aux trucs de largeur
    @Override
    public int getLabelWidth() { return 0; }

    @Override
    public int getInputWidth() { return 0; }



    @Override
    public List<? extends Element> children() {
        return children;
    }

}
