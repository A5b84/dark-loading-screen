package io.github.a5b84.darkloadingscreen.config.gui.widget;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ResetButton extends ButtonWidget {

    protected static final Text RESET_TEXT = new TranslatableText("controls.reset");

    public ResetButton(int x, int y, TextFieldWidget input, String defaultValue) {
        super(
            x, y, getButtonWidth(), OptionListWidget.ENTRY_HEIGHT,
            RESET_TEXT, btn -> input.setText(defaultValue)
        );
    }



    // Fonction plutôt qu'un champ static final au cas où la langue change
    @SuppressWarnings("resource")
    public static int getButtonWidth() {
        return MinecraftClient.getInstance().textRenderer.getWidth(RESET_TEXT)
            + 12;
    }

}
