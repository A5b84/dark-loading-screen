package io.github.a5b84.darkloadingscreen.config;

import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/** Button entry for Cloth Config */
public class ButtonEntry extends AbstractConfigListEntry<Void> {

    private static final int HEIGHT = 20;
    private final ButtonWidget button;

    public ButtonEntry(Text fieldName, ButtonWidget.PressAction onPress) {
        super(fieldName, false);
        int width = MinecraftClient.getInstance().textRenderer.getWidth(fieldName) + 24;
        button = new ButtonWidget(0, 0, width, HEIGHT, fieldName, onPress);
    }

    @Override
    public void render(MatrixStack matrices, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(matrices, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        button.x = x + (entryWidth - button.getWidth()) / 2;
        button.y = y + (entryHeight - HEIGHT) / 2;
        button.render(matrices, mouseX, mouseY, delta);
    }

    @Override
    public Void getValue() { return null; }

    @Override
    public Optional<Void> getDefaultValue() { return Optional.empty(); }

    @Override
    public void save() {}

    private List<ButtonWidget> ch() {
        return Collections.singletonList(button);
    }

    @Override
    public List<? extends Element> children() { return ch(); }

    @Override
    public List<? extends Selectable> narratables() { return ch(); }

}
