package io.github.a5b84.darkloadingscreen.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Config {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve(DarkLoadingScreen.MOD_ID + ".json").toFile();

    /** Factor to convert UI time (seconds) to ms */
    // (Dividing by 2 because the game waits twice as long)
    public static final float FADE_DURATION_FACTOR = DarkLoadingScreen.VANILLA_FADE_OUT_DURATION / 2;
    /** Maximum fade duration (just in case) */
    public static final float MAX_FADE_DURATION = 5;

    // Colors
    public final int bg, bar, barBg, border, logo;
    // RGB channels of some colors
    public final float bgR, bgG, bgB;
    public final float logoR, logoG, logoB;

    // Fade durations
    public final float fadeIn, fadeOut;
    public final float fadeInMs, fadeOutMs;


    public static final Config DEFAULT = new Config(
            0x14181c, 0xe22837, 0x14181c, 0x303336, 0xffffff,
            DarkLoadingScreen.VANILLA_FADE_IN_DURATION / FADE_DURATION_FACTOR,
            DarkLoadingScreen.VANILLA_FADE_OUT_DURATION / FADE_DURATION_FACTOR
    );


    /** @param fadeIn Fade in time in seconds
     * @param fadeOut Fade out time in seconds */
    public Config(
            int bg, int bar, int barBg, int border, int logo,
            float fadeIn, float fadeOut
    ) {
        this.bg = bg;
        this.bar = bar;
        this.barBg = barBg;
        this.border = border;
        this.logo = logo;
        this.fadeIn = Math.min(fadeIn, MAX_FADE_DURATION);
        this.fadeOut = Math.min(fadeOut, MAX_FADE_DURATION);

        // Splitting some colors in floats
        bgR = getChannel(bg, 16);
        bgG = getChannel(bg, 8);
        bgB = getChannel(bg, 0);
        logoR = getChannel(logo, 16);
        logoG = getChannel(logo, 8);
        logoB = getChannel(logo, 0);

        // Calculate durations
        fadeInMs = fadeIn * FADE_DURATION_FACTOR;
        fadeOutMs = fadeOut * FADE_DURATION_FACTOR;
    }

    /** @param offset Channel offset in bits
     * @return the corresponding channel value between {@code 0} and {@code 1} */
    private static float getChannel(int color, int offset) {
        return ((color >> offset) & 0xff) / 255f;
    }


    /** Reads the config in the config folder */
    public static Config read() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonElement el = new JsonParser().parse(reader);
            if (!el.isJsonObject()) return DEFAULT;

            JsonObject o = el.getAsJsonObject();
            return new Config(
                    readColor(o, "background",    DEFAULT.bg),
                    readColor(o, "bar",           DEFAULT.bar),
                    readColor(o, "barBackground", DEFAULT.barBg),
                    readColor(o, "border",        DEFAULT.border),
                    readColor(o, "logo",          DEFAULT.logo),
                    readFloat(o, "fadeIn",        DEFAULT.fadeIn),
                    readFloat(o, "fadeOut",       DEFAULT.fadeOut)
            );
        } catch (FileNotFoundException e) {
            return DEFAULT;
        } catch (IOException | JsonSyntaxException e) {
            LOGGER.error("[Dark Loading Screen] Couldn't read " + CONFIG_FILE + ", using default settings instead");
            e.printStackTrace();
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
        } catch (NumberFormatException ignored) {}

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
                    LOGGER.error("[Dark Loading Screen] Couldn't delete settings file " + CONFIG_FILE);
                }
            } catch (SecurityException e) {
                LOGGER.error("[Dark Loading Screen] Couldn't delete settings file " + CONFIG_FILE);
                e.printStackTrace();
            }
            return;
        }

        // Writing
        try (
                FileWriter fileWriter = new FileWriter(CONFIG_FILE);
                JsonWriter jsonWriter = new JsonWriter(fileWriter)
        ) {
            jsonWriter.setIndent("    ");
            jsonWriter.beginObject()
            .name("background").value(colorToString(bg))
            .name("bar").value(colorToString(bar))
            .name("barBackground").value(colorToString(barBg))
            .name("border").value(colorToString(border))
            .name("logo").value(colorToString(logo))
            .name("fadeIn").value(fadeIn)
            .name("fadeOut").value(fadeOut)
            .endObject();
        } catch (IOException e) {
            LOGGER.error("[Dark Loading Screen] Couldn't write settings to " + CONFIG_FILE);
            e.printStackTrace();
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

        return bg == config.bg
                && bar == config.bar
                && barBg == config.barBg
                && border == config.border
                && logo == config.logo
                && fadeIn == config.fadeIn
                && fadeOut == config.fadeOut;
    }

}
