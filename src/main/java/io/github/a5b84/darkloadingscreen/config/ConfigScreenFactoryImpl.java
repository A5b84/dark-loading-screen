package io.github.a5b84.darkloadingscreen.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.gui.entries.ColorEntry;
import me.shedaniel.clothconfig2.gui.entries.FloatListEntry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ConfigScreenFactoryImpl implements ConfigScreenFactory<Screen> {

  @Override
  public Screen create(Screen parent) {
    ConfigBuilder builder =
        ConfigBuilder.create()
            .setParentScreen(parent)
            .setTitle(Component.translatable("darkLoadingScreen.config.title"));

    Config oldConfig = DarkLoadingScreen.config;

    ConfigCategory category = builder.getOrCreateCategory(Component.empty());
    ConfigEntries entries = new ConfigEntries(builder.entryBuilder(), category);
    category.addEntry(
        new ButtonEntry(
            Component.translatable("darkLoadingScreen.config.entry.preview"),
            _ -> {
              DarkLoadingScreen.config = entries.createConfig();
              Minecraft.getInstance()
                  .gui
                  .setOverlay(
                      new PreviewSplashOverlay(500, () -> DarkLoadingScreen.config = oldConfig));
            }));

    builder.setSavingRunnable(
        () -> {
          Config config = DarkLoadingScreen.config = entries.createConfig();
          ConfigSerialization.write(config);
        });

    return builder.build();
  }

  private static class ConfigEntries {

    private final ConfigEntryBuilder builder;
    private final ConfigCategory category;
    private final ColorEntry backgroundColorField;
    private final ColorEntry barColorField;
    private final ColorEntry barBackgroundColorField;
    private final ColorEntry barBorderColorField;
    private final ColorEntry logoColorField;
    private final FloatListEntry fadeInDurationField;
    private final FloatListEntry fadeOutDurationField;

    /** Creates all the fields and adds them to {@code category}. */
    public ConfigEntries(ConfigEntryBuilder builder, ConfigCategory category) {
      this.builder = builder;
      this.category = category;

      backgroundColorField =
          createColorField(
              Component.translatable("darkLoadingScreen.config.entry.background"),
              DarkLoadingScreen.config.backgroundColor,
              Config.DEFAULT.backgroundColor);
      barColorField =
          createColorField(
              Component.translatable("darkLoadingScreen.config.entry.bar"),
              DarkLoadingScreen.config.barColor,
              Config.DEFAULT.barColor);
      barBackgroundColorField =
          createColorField(
              Component.translatable("darkLoadingScreen.config.entry.barBackground"),
              DarkLoadingScreen.config.barBackgroundColor,
              Config.DEFAULT.barBackgroundColor);
      barBorderColorField =
          createColorField(
              Component.translatable("darkLoadingScreen.config.entry.border"),
              DarkLoadingScreen.config.barBorderColor,
              Config.DEFAULT.barBorderColor);
      logoColorField =
          createColorField(
              Component.translatable("darkLoadingScreen.config.entry.logo"),
              DarkLoadingScreen.config.logoColor,
              Config.DEFAULT.logoColor);
      fadeInDurationField =
          createFadeTimeField(
              Component.translatable("darkLoadingScreen.config.entry.fadeIn"),
              DarkLoadingScreen.config.fadeInDuration,
              Config.DEFAULT.fadeInDuration);
      fadeOutDurationField =
          createFadeTimeField(
              Component.translatable("darkLoadingScreen.config.entry.fadeOut"),
              DarkLoadingScreen.config.fadeOutDuration,
              Config.DEFAULT.fadeOutDuration);
    }

    public Config createConfig() {
      return new Config(
          backgroundColorField.getValue(),
          barColorField.getValue(),
          barBackgroundColorField.getValue(),
          barBorderColorField.getValue(),
          logoColorField.getValue(),
          fadeInDurationField.getValue(),
          fadeOutDurationField.getValue());
    }

    private ColorEntry createColorField(Component name, int value, int defaultValue) {
      ColorEntry entry = builder.startColorField(name, value).setDefaultValue(defaultValue).build();
      category.addEntry(entry);
      return entry;
    }

    private FloatListEntry createFadeTimeField(Component name, float value, float defaultValue) {
      FloatListEntry entry =
          builder
              .startFloatField(name, value)
              .setDefaultValue(defaultValue)
              .setMin(0)
              .setMax(Config.MAX_FADE_DURATION)
              .build();
      category.addEntry(entry);
      return entry;
    }
  }
}
