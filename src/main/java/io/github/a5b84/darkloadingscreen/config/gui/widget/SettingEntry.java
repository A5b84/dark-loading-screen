package io.github.a5b84.darkloadingscreen.config.gui.widget;

import javax.annotation.Nonnull;

import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class SettingEntry extends OptionListWidget.Entry {

    public final Text label;
    public final TextFieldWidget input;
    public final ButtonWidget resetButton;



    public SettingEntry(
        @Nonnull OptionListWidget list, @Nonnull Text label,
        @Nonnull TextFieldWidget input, @Nonnull String defaultValue
    ) {
        super(list);
        this.label = label;
        this.input = input;
        input.setText(defaultValue);
        resetButton = new ResetButton(input.x, input.y, input, defaultValue);

        input.setChangedListener(
            text -> resetButton.active = defaultValue.equals(text)
        );
    }



    @Override
    public void render(
        MatrixStack matrices, int index, int y, int x,
        int entryWidth, int entryHeight, int mouseX, int mouseY,
        boolean hovered, float tickDelta
    ) {
        // Label
        list.textRenderer.draw(
            matrices, label,
            x, y + (entryHeight + list.textRenderer.fontHeight) / 2,
            0xffffffff
        );

        // Inputs
        input.x = x + list.labelWidth + OptionListWidget.SPACING;
        input.y = y + (entryHeight - input.getHeight()) / 2;
        resetButton.x = input.x + list.inputWidth + OptionListWidget.SPACING;
        resetButton.y = y + (entryHeight - resetButton.getHeight()) / 2;
        input.render(matrices, mouseX, mouseY, tickDelta);
        resetButton.render(matrices, mouseX, mouseY, tickDelta);
    }



    @Override
    public int getLabelWidth() {
        return list.textRenderer.getWidth(label);
    }

    @Override
    public int getInputWidth() {
        return input.getWidth();
    }

}
