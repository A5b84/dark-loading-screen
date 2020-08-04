package io.github.a5b84.darkloadingscreen;

import com.mojang.blaze3d.systems.RenderSystem;

import io.github.a5b84.darkloadingscreen.config.Config;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.MathHelper;

public class Mod implements ClientModInitializer {

    public static final String ID = "dark-loading-screen";

    @Override
    public void onInitializeClient() {
        config = Config.read();
    }



    // Trucs communs

    public static Config config;

    public static float endAnimProgress; // Un peu sale mais bon
    public static float alpha;



    // TODO: enlever colorLerp pour utiliser l'alpha à la place quand y aura
    // plus besoin de support pour la 1.14/1.15

    public static int getBg(int color) {
        return config.bgColor | (color & 0xff000000);
    }

    public static int getBarColor(int color) {
        return colorLerp(endAnimProgress, config.bgColor, config.barColor) | 0xff000000;
    }

    public static int getBarBorder(int color) {
        return colorLerp(endAnimProgress, config.bgColor, config.borderColor) | 0xff000000;
    }

    /** Utilisé qu'avant la 1.16 */
    public static int getBarBg(int color) {
        return config.bgColor | 0xff000000;
    }

    public static void logoAddColor4f() {
        RenderSystem.color4f(
            config.logoR - config.bgR,
            config.logoG - config.bgG,
            config.logoB - config.bgB,
            alpha
        );
    }

    public static void logoSubstractColor4f() {
        RenderSystem.color4f(
            config.bgR - config.logoR,
            config.bgG - config.logoG,
            config.bgB - config.logoB,
            alpha
        );
    }



    // Méthodes sur les couleurs

    /** Interpolation linéaire entre deux couleurs */
    private static int colorLerp(float val, int col1, int col2) {
        return channelLerp(val, col1, col2, 16)
            | channelLerp(val, col1, col2, 8)
            | channelLerp(val, col1, col2, 0);
    }

    /** Interpolation linéaire entre deux cannaux de 8 bits de deux couleurs */
    private static int channelLerp(float val, int col1, int col2, int offset) {
        return Math.round(
            MathHelper.lerp(val, (col1 >> offset) & 0xff, (col2 >> offset) & 0xff)
        ) << offset;
    }

}
