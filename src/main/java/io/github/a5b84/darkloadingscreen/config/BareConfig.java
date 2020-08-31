package io.github.a5b84.darkloadingscreen.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import io.github.a5b84.darkloadingscreen.Mod;

/** Config sans tous les trucs utiles qui ont besoin d'être calculés */
public class BareConfig {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected static final String CONFIG_PATH = "./config/" + Mod.ID + ".json";

    public static final Config DEFAULT = new Config("14181c", "e22837", "14181c", "303336", "ffffff");

    // Strings pour la sérialisation + l'écran de config
    public final String bgStr;
    public final String barStr;
    public final String barBgStr;
    public final String borderStr;
    public final String logoStr;



    public BareConfig(String bg, String bar, String barBg, String border, String logo) {
        bgStr = bg;
        barStr = bar;
        barBgStr = barBg;
        borderStr = border;
        logoStr = logo;
    }



    /** Lit une config depuis le dossier config */
    public static Config read() {
        try (final FileReader fr = new FileReader(CONFIG_PATH)) {
            final JsonElement el = new JsonParser().parse(fr);
            if (!el.isJsonObject()) return DEFAULT;

            final JsonObject o = el.getAsJsonObject();
            final String bgStr = readColor(o, "background", DEFAULT.bgStr);
            return new Config(
                bgStr,
                readColor(o, "bar", DEFAULT.barStr),
                readColor(o, "barBackground", bgStr), // Pour pas casser les
                //      vieilles configs en mettant le mod à jour
                readColor(o, "border", DEFAULT.borderStr),
                readColor(o, "logo", DEFAULT.logoStr)
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
    protected static String readColor(JsonObject json, String key, String fallback) {
        final JsonElement el = json.get(key);
        if (el == null || !el.isJsonPrimitive()) return fallback;
        try {
            return el.getAsString();
        } catch (ClassCastException e) {
            return fallback;
        }
    }



    public void write() {
        if (equals(DEFAULT)) {
            // On supprime le fichier pour la config par défaut
            try {
                new File(CONFIG_PATH).delete();
            } catch (SecurityException e) {
                LOGGER.error("[Dark Loading Screen] Couldn't delete settings at " + CONFIG_PATH);
                e.printStackTrace();
            }
            return;
        }

        // Écriture
        try (
            final FileWriter fw = new FileWriter(CONFIG_PATH);
            final JsonWriter jw = new JsonWriter(fw);
        ) {
            jw.setIndent("    ");
            jw.beginObject()
            .name("background").value(bgStr)
            .name("bar").value(barStr)
            .name("barBackground").value(barBgStr)
            .name("border").value(borderStr)
            .name("logo").value(logoStr)
            .endObject();

        } catch (IOException e) {
            LOGGER.error("[Dark Loading Screen] Couldn't write settings to " + CONFIG_PATH);
            e.printStackTrace();
        }
    }



    public boolean equals(BareConfig config) {
        if (config == null) return false;
        if (config == this) return true;

        return bgStr.equals(config.bgStr)
            && barStr.equals(config.barStr)
            && barBgStr.equals(config.barBgStr)
            && borderStr.equals(config.borderStr)
            && logoStr.equals(config.logoStr);
    }

}
