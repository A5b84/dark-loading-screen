package io.github.a5b84.darkloadingscreen.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import io.github.a5b84.darkloadingscreen.Util;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import net.fabricmc.loader.api.FabricLoader;
import org.jspecify.annotations.Nullable;

public class ConfigSerialization {

  private static final Path CONFIG_PATH =
      FabricLoader.getInstance().getConfigDir().resolve(DarkLoadingScreen.MOD_ID + ".json");

  private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

  private ConfigSerialization() {}

  public static Config read() {
    try (FileReader reader = new FileReader(CONFIG_PATH.toFile())) {
      ConfigDto dto = GSON.fromJson(reader, ConfigDto.class);
      return dto.toConfig();
    } catch (FileNotFoundException e) {
      return Config.DEFAULT;
    } catch (IOException | JsonParseException e) {
      DarkLoadingScreen.LOGGER.error(
          "[Dark Loading Screen] Couldn't read config at {}, using default config instead",
          CONFIG_PATH,
          e);
      return Config.DEFAULT;
    }
  }

  public static void write(Config config) {
    try (FileWriter writer = new FileWriter(CONFIG_PATH.toFile())) {
      ConfigDto dto = new ConfigDto(config);
      GSON.toJson(dto, writer);
    } catch (IOException | JsonIOException e) {
      DarkLoadingScreen.LOGGER.error(
          "[Dark Loading Screen] Couldn't write config to {}", CONFIG_PATH, e);
    }
  }

  private record ConfigDto(
      @Nullable String background,
      @Nullable String bar,
      @Nullable String barBackground,
      @Nullable String border,
      @Nullable String logo,
      float fadeIn,
      float fadeOut) {

    public ConfigDto(Config config) {
      this(
          stringifyColor(config.backgroundColor),
          stringifyColor(config.barColor),
          stringifyColor(config.barBackgroundColor),
          stringifyColor(config.barBorderColor),
          stringifyColor(config.logoColor),
          config.fadeInDuration,
          config.fadeOutDuration);
    }

    public Config toConfig() {
      return new Config(
          parseColor(background, Config.DEFAULT.backgroundColor),
          parseColor(bar, Config.DEFAULT.barColor),
          parseColor(barBackground, Config.DEFAULT.barBackgroundColor),
          parseColor(border, Config.DEFAULT.barBorderColor),
          parseColor(logo, Config.DEFAULT.logoColor),
          fadeIn,
          fadeOut);
    }

    private static String stringifyColor(int color) {
      return Util.padStart(Integer.toString(color, 16), 6, '0');
    }

    private static int parseColor(@Nullable String value, int fallback) {
      if (value == null) {
        return fallback;
      }

      try {
        return Integer.parseInt(value, 16);
      } catch (NumberFormatException ignored) {
        DarkLoadingScreen.LOGGER.warn(
            "[Dark Loading Screen] Failed to parse config color '{}'", value);
        return fallback;
      }
    }
  }
}
