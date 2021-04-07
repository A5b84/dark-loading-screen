package io.github.a5b84.darkloadingscreen;

import io.github.a5b84.darkloadingscreen.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;

public class Mod implements ClientModInitializer {

    public static final String ID = "dark-loading-screen";

    /** Vanilla fade in/out durations
     * @see SplashScreen#render(MatrixStack, int, int, float) */
    public static final float
            VANILLA_FADE_IN_DURATION = 500,
            VANILLA_FADE_OUT_DURATION = 1000;

    public static Config config;

    @Override
    public void onInitializeClient() {
        config = Config.read();
    }



    // Stuff shared between multiple mixins

    /** Progress bar alpha in {@code 0xAA000000} format */
    public static int progressBarAlpha;

    /** @return la couleur de la barre avec transparence */
    public static int getBarColor() {
        return config.bar | progressBarAlpha;
    }

    /** @return la couleur du contour la barre avec transparence */
    public static int getBarBorderColor() {
        return config.border | progressBarAlpha;
    }

}
