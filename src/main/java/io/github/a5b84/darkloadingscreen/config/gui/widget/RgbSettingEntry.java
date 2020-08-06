package io.github.a5b84.darkloadingscreen.config.gui.widget;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.Text;

public class RgbSettingEntry extends SettingEntry {

    public RgbSettingEntry(OptionListWidget list, Text label, String defaultValue, TextRenderer textRenderer) {
        super(
            list, label, new RgbFieldWidget(textRenderer, 0, 0, label),
            defaultValue
        );
    }

}
