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

public class Config {

    protected static final Logger LOGGER = LogManager.getLogger();

    protected static final String CONFIG_PATH = "./config/" + Mod.ID + ".json";

    public static final Config DEFAULT = new Config(
        "14181c", "e22837", "303336", "ffffff"
    );

    // On garde les strings pour la sérialisation + l'écran de config
    public final String bgStr;
    public final String barStr;
    public final String borderStr;
    public final String logoStr;

    public final int bgColor;
    public final int barColor;
    public final int borderColor;
    public final int logoColor;

    public final float bgR;
    public final float bgG;
    public final float bgB;
    public final float logoR;
    public final float logoG;
    public final float logoB;



    public Config(String bg, String bar, String border, String logo) {
        bgStr = bg;
        barStr = bar;
        borderStr = border;
        logoStr = logo;

        if (DEFAULT != null) {
            bgColor = Util.parseColor(bgStr, DEFAULT.bgColor);
            barColor = Util.parseColor(barStr, DEFAULT.barColor);
            borderColor = Util.parseColor(borderStr, DEFAULT.borderColor);
            logoColor = Util.parseColor(logoStr, DEFAULT.logoColor);
        } else {
            // Cas à part parce que DEFAULT peut pas se référencer dans son
            // constructeur
            bgColor = Util.parseColor(bgStr);
            barColor = Util.parseColor(barStr);
            borderColor = Util.parseColor(borderStr);
            logoColor = Util.parseColor(logoStr);
        }

        bgR = ((bgColor >> 16) & 0xff) / 255f;
        bgG = ((bgColor >> 8) & 0xff) / 255f;
        bgB = (bgColor & 0xff) / 255f;
        logoR = ((logoColor >> 16) & 0xff) / 255f;
        logoG = ((logoColor >> 8) & 0xff) / 255f;
        logoB = (logoColor & 0xff) / 255f;
    }



    /** Lit une config depuis le dossier config */
    public static Config read() {
        try (final FileReader fr = new FileReader(CONFIG_PATH)) {
            final JsonElement el = new JsonParser().parse(fr);
            if (!el.isJsonObject()) return DEFAULT;

            final JsonObject o = el.getAsJsonObject();
            return new Config(
                readColor(o, "background", DEFAULT.bgStr),
                readColor(o, "bar", DEFAULT.barStr),
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
            .name("border").value(borderStr)
            .name("logo").value(logoStr)
            .endObject();
        } catch (IOException e) {
            LOGGER.error("[Dark Loading Screen] Couldn't write settings to " + CONFIG_PATH);
            e.printStackTrace();
        }
    }



    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof Config)) return false;
        final Config config = (Config) obj;
        return bgStr.equals(config.bgStr)
            && barStr.equals(config.barStr)
            && borderStr.equals(config.borderStr)
            && logoStr.equals(config.logoStr);
    }

}
