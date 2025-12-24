package io.github.a5b84.darkloadingscreen.config;

import java.util.List;
import java.util.Optional;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

/** Clickable button entry for Cloth Config that doesn't hold any data */
public class ButtonEntry extends AbstractConfigListEntry<Void> {

  private final Button button;

  public ButtonEntry(Component fieldName, Button.OnPress onPress) {
    super(fieldName, false);
    int width = Minecraft.getInstance().font.width(fieldName) + 24;
    button = Button.builder(fieldName, onPress).width(width).build();
  }

  @Override
  public void render(
      GuiGraphics graphics,
      int index,
      int y,
      int x,
      int entryWidth,
      int entryHeight,
      int mouseX,
      int mouseY,
      boolean isHovered,
      float delta) {
    super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
    button.setX(x + (entryWidth - button.getWidth()) / 2);
    button.setY(y + (entryHeight - button.getHeight()) / 2);
    button.render(graphics, mouseX, mouseY, delta);
  }

  @Override
  public Void getValue() {
    return null;
  }

  @Override
  public Optional<Void> getDefaultValue() {
    return Optional.empty();
  }

  @Override
  public void save() {}

  @Override
  @NotNull
  public List<? extends GuiEventListener> children() {
    return List.of(button);
  }

  @Override
  public List<? extends NarratableEntry> narratables() {
    return List.of(button);
  }
}
