package io.github.a5b84.darkloadingscreen;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.util.math.MathHelper;

public class Mod implements ClientModInitializer {

    public static final String ID = "dark-loading-screen";

    @Override
    public void onInitializeClient() {}



    // Trucs communs

    public static final int BG_COLOR = 0x14181c;
    public static final int BORDER_COLOR = 0x303336;
    public static final int BAR_COLOR = 0xe22837; // Couleur vanilla

    public static float endAnimProgress; // Un peu sale mais bon



    public static int getBackground(int color) {
        return BG_COLOR | (color & 0xff000000);
    }

    public static int getBarBorder(int color) {
        return colorLerp(endAnimProgress, BG_COLOR, BORDER_COLOR) | 0xff000000;
    }

    public static int getBarBackground(int color) {
        return BG_COLOR | 0xff000000;
    }

    public static int getBarColor(int color) {
        return colorLerp(endAnimProgress, BG_COLOR, BAR_COLOR) | 0xff000000;
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
