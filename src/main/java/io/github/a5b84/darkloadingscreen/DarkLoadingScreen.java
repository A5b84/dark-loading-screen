package io.github.a5b84.darkloadingscreen;

import io.github.a5b84.darkloadingscreen.config.Config;
import net.minecraft.client.gui.screen.SplashOverlay;

public class DarkLoadingScreen {

    public static final String MOD_ID = "dark-loading-screen";

    /**
     * Vanilla fade in/out durations
     * @see SplashOverlay#render
     */
    public static final float
            VANILLA_FADE_IN_DURATION = 500,
            VANILLA_FADE_OUT_DURATION = 1000;

    public static Config config = Config.read();
}
