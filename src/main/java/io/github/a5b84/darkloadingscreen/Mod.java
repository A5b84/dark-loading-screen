package io.github.a5b84.darkloadingscreen;

import io.github.a5b84.darkloadingscreen.config.Config;
import net.fabricmc.api.ClientModInitializer;

public class Mod implements ClientModInitializer {

    public static final String ID = "dark-loading-screen";
    public static Config config;

    /** Transparence de la barre de chargement au format {@code 0xAA000000} */
    // (Stocké ici vu qu'on en a besoin dans plusieurs mixins mais qu'elles
    // peuvent pas s'entre-référencer)
    public static int progressBarAlpha;



    @Override
    public void onInitializeClient() {
        config = Config.read();
    }



    /** @return la couleur de la barre avec transparence */
    public static int getBarColor() {
        return config.bar | progressBarAlpha;
    }

    /** @return la couleur du contour la barre avec transparence */
    public static int getBarBorderColor() {
        return config.border | progressBarAlpha;
    }

}
