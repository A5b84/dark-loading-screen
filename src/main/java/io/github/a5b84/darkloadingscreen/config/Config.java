package io.github.a5b84.darkloadingscreen.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;
import io.github.a5b84.darkloadingscreen.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** Paramètres du mods */
public class Config {

    protected static final Logger LOGGER = LogManager.getLogger();
    protected static final String CONFIG_PATH = "./config/" + Mod.ID + ".json";

    // Couleurs
    public final int bg, bar, barBg, border, logo;
    // Canaux séparés de certaines couleurs
    public final float bgR, bgG, bgB;
    public final float logoR, logoG, logoB;

    // Durées
    public final float fadeIn, fadeOut;
    public final float fadeInMs, fadeOutMs;
    /** Facteur pour passer d'une durée de fondu de l'UI (sec) en ms
     * ({@code 1000 / 2} puisque le jeu attend le double de la durée) */
    public static final float FADE_TIME_FACTOR = 1000f / 2;
    /** Durée maximale des fondus (en sec, pour éviter les regrets) */
    public static final float MAX_FADE_TIME = 5;



    public static final Config DEFAULT = new Config(
            0x14181c, 0xe22837, 0x14181c, 0x303336, 0xffffff,
            Mod.VANILLA_FADE_IN_TIME / FADE_TIME_FACTOR,
            Mod.VANILLA_FADE_OUT_TIME / FADE_TIME_FACTOR
    );


    /** @param fadeIn Durée d'apparition en secondes
     * @param fadeOut Durée de disparition en secondes */
    public Config(
            int bg, int bar, int barBg, int border, int logo,
            float fadeIn, float fadeOut
    ) {
        this.bg = bg;
        this.bar = bar;
        this.barBg = barBg;
        this.border = border;
        this.logo = logo;
        this.fadeIn = Math.min(fadeIn, MAX_FADE_TIME);
        this.fadeOut = Math.min(fadeOut, MAX_FADE_TIME);

        // Séparation de certaines couleurs en 3 floats
        bgR = getChannel(bg, 16);
        bgG = getChannel(bg, 8);
        bgB = getChannel(bg, 0);
        logoR = getChannel(logo, 16);
        logoG = getChannel(logo, 8);
        logoB = getChannel(logo, 0);

        // Calculs de durées
        fadeInMs = fadeIn * FADE_TIME_FACTOR;
        fadeOutMs = fadeOut * FADE_TIME_FACTOR;
    }

    /**
     * Récupère un canal de 8 bit d'une couleur
     * @param offset Décalage du canal en nombre de bits
     * @return la valeur du canal entre {@code 0} et {@code 1}
     */
    private static float getChannel(int color, int offset) {
        return ((color >> offset) & 0xff) / 255f;
    }



    /** Lit une config depuis le dossier config */
    public static Config read() {
        try (final FileReader fr = new FileReader(CONFIG_PATH)) {
            final JsonElement el = new JsonParser().parse(fr);
            if (!el.isJsonObject()) return DEFAULT;

            final JsonObject o = el.getAsJsonObject();
            return new Config(
                    readColor(o, "background",    DEFAULT.bg),
                    readColor(o, "bar",           DEFAULT.bar),
                    readColor(o, "barBackground", DEFAULT.barBg),
                    readColor(o, "border",        DEFAULT.border),
                    readColor(o, "logo",          DEFAULT.logo),
                    readFloat(o, "fadeIn",        DEFAULT.fadeIn),
                    readFloat(o, "fadeOut",       DEFAULT.fadeOut)
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
        } catch (ClassCastException | IllegalStateException e) {
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

    private static float readFloat(JsonObject o, String key, float fallback) {
        // Lecture
        final JsonElement el = o.get(key);
        if (el == null) return fallback;

        try {
            return el.getAsFloat();
        } catch (ClassCastException | IllegalStateException e) {
            return fallback;
        }
    }



    public void write() {
        if (equals(DEFAULT)) {
            // On supprime le fichier pour la config par défaut
            try {
                final File file = new File(CONFIG_PATH);
                if (file.exists() && !file.delete()) {
                    LOGGER.error("[Dark Loading Screen] Couldn't delete settings file " + CONFIG_PATH);
                }
            } catch (SecurityException e) {
                LOGGER.error("[Dark Loading Screen] Couldn't delete settings file " + CONFIG_PATH);
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
            .name("fadeIn").value(fadeIn)
            .name("fadeOut").value(fadeOut)
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
            && logo == config.logo
            && fadeIn == config.fadeIn
            && fadeOut == config.fadeOut;
    }

}
