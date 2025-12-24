package io.github.a5b84.darkloadingscreen.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.util.math.ColorHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Config {

  private static final Logger LOGGER = LogManager.getLogger();
  private static final File CONFIG_FILE =
      FabricLoader.getInstance()
          .getConfigDir()
          .resolve(DarkLoadingScreen.MOD_ID + ".json")
          .toFile();

  /** Factor to convert UI time (seconds) to ms */
  // (Dividing by 2 because the game waits twice as long)
  public static final float FADE_DURATION_FACTOR = DarkLoadingScreen.VANILLA_FADE_OUT_DURATION / 2;

  /** Maximum fade duration (just in case) */
  public static final float MAX_FADE_DURATION = 5;

  // Colors
  public final int backgroundColor;
  public final int barColor;
  public final int barBackgroundColor;
  public final int barBorderColor;
  public final int logoColor;

  // RGB channels of some colors
  public final float backgroundRed;
  public final float backgroundGreen;
  public final float backgroundBlue;
  public final float logoRed;
  public final float logoGreen;
  public final float logoBlue;

  // Fade durations
  public final float fadeInDuration;
  public final float fadeOutDuration;
  public final float fadeInMillis;
  public final float fadeOutMillis;

  public static final Config DEFAULT =
      new Config(
          0x14181c,
          0xe22837,
          0x14181c,
          0x303336,
          0xffffff,
          DarkLoadingScreen.VANILLA_FADE_IN_DURATION / FADE_DURATION_FACTOR,
          DarkLoadingScreen.VANILLA_FADE_OUT_DURATION / FADE_DURATION_FACTOR);

  /**
   * @param fadeInDuration Fade in time in seconds
   * @param fadeOutDuration Fade out time in seconds
   */
  public Config(
      int backgroundColor,
      int barColor,
      int barBackgroundColor,
      int barBorderColor,
      int logoColor,
      float fadeInDuration,
      float fadeOutDuration) {
    this.backgroundColor = backgroundColor;
    this.barColor = barColor;
    this.barBackgroundColor = barBackgroundColor;
    this.barBorderColor = barBorderColor;
    this.logoColor = logoColor;
    this.fadeInDuration = Math.min(fadeInDuration, MAX_FADE_DURATION);
    this.fadeOutDuration = Math.min(fadeOutDuration, MAX_FADE_DURATION);

    // Splitting some colors in floats
    backgroundRed = ColorHelper.getRed(backgroundColor);
    backgroundGreen = ColorHelper.getGreen(backgroundColor);
    backgroundBlue = ColorHelper.getBlue(backgroundColor);
    logoRed = ColorHelper.getRed(logoColor);
    logoGreen = ColorHelper.getGreen(logoColor);
    logoBlue = ColorHelper.getBlue(logoColor);

    // Calculate durations
    fadeInMillis = fadeInDuration * FADE_DURATION_FACTOR;
    fadeOutMillis = fadeOutDuration * FADE_DURATION_FACTOR;
  }

  /** Reads the config in the config folder */
  public static Config read() {
    try (FileReader reader = new FileReader(CONFIG_FILE)) {
      JsonElement el = JsonParser.parseReader(reader);
      if (!el.isJsonObject()) return DEFAULT;

      JsonObject o = el.getAsJsonObject();
      return new Config(
          readColor(o, "background", DEFAULT.backgroundColor),
          readColor(o, "bar", DEFAULT.barColor),
          readColor(o, "barBackground", DEFAULT.barBackgroundColor),
          readColor(o, "border", DEFAULT.barBorderColor),
          readColor(o, "logo", DEFAULT.logoColor),
          readFloat(o, "fadeIn", DEFAULT.fadeInDuration),
          readFloat(o, "fadeOut", DEFAULT.fadeOutDuration));
    } catch (FileNotFoundException e) {
      return DEFAULT;
    } catch (IOException | JsonSyntaxException e) {
      LOGGER.error(
          () ->
              "[Dark Loading Screen] Couldn't read "
                  + CONFIG_FILE
                  + ", using default settings instead",
          e);
      return DEFAULT;
    }
  }

  /** Reads a color from a {@link JsonObject} */
  private static int readColor(JsonObject o, String key, int fallback) {
    // Reading
    JsonElement el = o.get(key);
    if (el == null) return fallback;

    String str;
    try {
      str = el.getAsString();
    } catch (ClassCastException | IllegalStateException e) {
      return fallback;
    }

    // String -> int conversion
    try {
      // 0xRGB format (for older versions that didn't use Cloth Config)
      if (str.length() == 3) {
        int color = Integer.parseInt(str, 16);
        // 0xRGB -> 0xRRGGBB conversion
        return (((color & 0xf00) << 8) + ((color & 0x0f0) << 4) + (color & 0x00f)) * 0x11;
      }

      // 0xRRGGBB format (Cloth Config)
      if (str.length() == 6) {
        return Integer.parseInt(str, 16);
      }
    } catch (NumberFormatException ignored) {
    }

    // Error or unknown format
    LOGGER.warn("[Dark Loading Screen] Invalid color '{}' for option '{}'", str, key);
    return fallback;
  }

  private static float readFloat(JsonObject o, String key, float fallback) {
    JsonElement el = o.get(key);
    if (el == null) return fallback;

    try {
      return el.getAsFloat();
    } catch (ClassCastException | IllegalStateException e) {
      LOGGER.warn("[Dark Loading Screen] Invalid float '{}' for option '{}'", el, key);
      return fallback;
    }
  }

  public void write() {
    if (equals(DEFAULT)) {
      // Delete the config file when using the default one
      try {
        File file = CONFIG_FILE;
        if (file.exists() && !file.delete()) {
          LOGGER.error(() -> "[Dark Loading Screen] Couldn't delete settings file " + CONFIG_FILE);
        }
      } catch (SecurityException e) {
        LOGGER.error(() -> "[Dark Loading Screen] Couldn't delete settings file " + CONFIG_FILE, e);
      }
      return;
    }

    // Writing
    try (FileWriter fileWriter = new FileWriter(CONFIG_FILE);
        JsonWriter jsonWriter = new JsonWriter(fileWriter)) {
      jsonWriter.setIndent("    ");
      jsonWriter
          .beginObject()
          .name("background")
          .value(colorToString(backgroundColor))
          .name("bar")
          .value(colorToString(barColor))
          .name("barBackground")
          .value(colorToString(barBackgroundColor))
          .name("border")
          .value(colorToString(barBorderColor))
          .name("logo")
          .value(colorToString(logoColor))
          .name("fadeIn")
          .value(fadeInDuration)
          .name("fadeOut")
          .value(fadeOutDuration)
          .endObject();
    } catch (IOException e) {
      LOGGER.error(() -> "[Dark Loading Screen] Couldn't write settings to " + CONFIG_FILE, e);
    }
  }

  private static String colorToString(int color) {
    String s = Integer.toString(color, 16);
    int leadingZeroes = 6 - s.length();
    return leadingZeroes > 0 ? "0".repeat(leadingZeroes) + s : s;
    // leadingZeroes should always be >= 0 but just in case
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Config config)) return false;

    return backgroundColor == config.backgroundColor
        && barColor == config.barColor
        && barBackgroundColor == config.barBackgroundColor
        && barBorderColor == config.barBorderColor
        && logoColor == config.logoColor
        && fadeInDuration == config.fadeInDuration
        && fadeOutDuration == config.fadeOutDuration;
  }
}
