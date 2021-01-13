package io.github.a5b84.darkloadingscreen.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import io.github.a5b84.darkloadingscreen.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;

/** Paramètres du mods */
public class Config {

    public static final Config DEFAULT = new Config(0x14181c, 0xe22837, 0x14181c, 0x303336, 0xffffff);

    protected static final Logger LOGGER = LogManager.getLogger();
    protected static final String CONFIG_PATH = "./config/" + Mod.ID + ".json";

    public final int bg, bar, barBg, border, logo;
    // Canaux séparés de certaines couleurs
    public final float bgR, bgG, bgB;
    public final float logoR, logoG, logoB;



    public Config(int bg, int bar, int barBg, int border, int logo) {
        this.bg = bg;
        this.bar = bar;
        this.barBg = barBg;
        this.border = border;
        this.logo = logo;

        // Séparation de certaines couleurs en 3 floats
        bgR = ((bg >> 16) & 0xff) / 255f;
        bgG = ((bg >> 8) & 0xff) / 255f;
        bgB = (bg & 0xff) / 255f;
        logoR = ((logo >> 16) & 0xff) / 255f;
        logoG = ((logo >> 8) & 0xff) / 255f;
        logoB = (logo & 0xff) / 255f;
    }



    /** Lit une config depuis le dossier config */
    public static Config read() {
        try (final FileReader fr = new FileReader(CONFIG_PATH)) {
            final JsonElement el = new JsonParser().parse(fr);
            if (!el.isJsonObject()) return DEFAULT;

            final JsonObject o = el.getAsJsonObject();
            return new Config(
                    readColor(o, "background", DEFAULT.bg),
                    readColor(o, "bar", DEFAULT.bar),
                    readColor(o, "barBackground", DEFAULT.barBg),
                    readColor(o, "border", DEFAULT.border),
                    readColor(o, "logo", DEFAULT.logo)
            );
        } catch (FileNotFoundException | JsonSyntaxException e) {
            return DEFAULT;
        } catch (IOException e) {
            LOGGER.error("[Dark Loading Screen] Couldn't read " + CONFIG_PATH + ", using default settings instead");
            e.printStackTrace();
            return DEFAULT;
        }
    }

    /** Lit une couleur d'un objet json */
    private static int readColor(JsonObject o, String key, int fallback) {
        // Lecture
        final JsonElement el = o.get(key);
        if (el == null) return fallback;

        String str;
        try {
            str = el.getAsString();
        } catch (ClassCastException e) {
            return fallback;
        }

        // Conversion String -> int
        try {
            // Format `rgb` (rétro-compatibilité)
            if (str.length() == 3) {
                int color = Integer.parseInt(str, 16);
                // Transformation #rgb -> #rrggbb
                return (((color & 0xf00) << 8) + ((color & 0x0f0) << 4) + (color & 0x00f)) * 0x11;
            }

            // Format `rrggbb`
            if (str.length() == 6) {
                return Integer.parseInt(str, 16);
            }
        } catch (NumberFormatException ignored) {}

        return fallback; // Erreur ou format non reconnu
    }

    public void write() {
        if (equals(DEFAULT)) {
            // On supprime le fichier pour la config par défaut
            try {
                if (!new File(CONFIG_PATH).delete()) {
                    LOGGER.error("[Dark Loading Screen] Couldn't delete settings at " + CONFIG_PATH);
                }
            } catch (SecurityException e) {
                LOGGER.error("[Dark Loading Screen] Couldn't delete settings at " + CONFIG_PATH);
                e.printStackTrace();
            }
            return;
        }

        // Écriture
        try (
            final FileWriter fw = new FileWriter(CONFIG_PATH);
            final JsonWriter jw = new JsonWriter(fw)
        ) {
            jw.setIndent("    ");
            jw.beginObject()
            .name("background").value(colorToString(bg))
            .name("bar").value(colorToString(bar))
            .name("barBackground").value(colorToString(barBg))
            .name("border").value(colorToString(border))
            .name("logo").value(colorToString(logo))
            .endObject();

        } catch (IOException e) {
            LOGGER.error("[Dark Loading Screen] Couldn't write settings to " + CONFIG_PATH);
            e.printStackTrace();
        }
    }

    private static String colorToString(int color) {
        final String s = "00000" + Integer.toString(color, 16);
        return s.substring(s.length() - 6); // 6 premiers caractères
    }



    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Config)) return false;

        final Config config = (Config) o;
        return bg == config.bg
            && bar == config.bar
            && barBg == config.barBg
            && border == config.border
            && logo == config.logo;
    }

}
