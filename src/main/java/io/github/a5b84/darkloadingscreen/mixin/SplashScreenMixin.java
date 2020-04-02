package io.github.a5b84.darkloadingscreen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.util.math.MathHelper;

/**
 * Ajuste les couleurs.
 * @see SplashScreen
 */
@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin extends DrawableHelper {

    @Unique private static final int BG_COLOR = 0x181818;
    @Unique private static final int BAR_COLOR = 0xe22837; // Couleur vanilla
    @Unique private static final int BORDER_COLOR = 0x303030;



    // Fond

    /// @see 
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "fill", remap = false), index = 4, remap = false)
    private int adjustBackground(int color) {
        return BG_COLOR | (color & 0xff000000);
    }

    //      1.15-
    @ModifyArg(method = "render1_15", at = @At(value = "INVOKE", target = "fill1_15", remap = false), index = 4, remap = false)
    private int adjustBackground1_15(int color) {
        return adjustBackground(color);
    }



    // Barre

    @Unique private float progress;
    @Unique private float endAnimProgress;

    //      Variables communes
    //          1.15+
    @Inject(method = "renderProgressBar", at = @At("HEAD"))
    private void onRenderProgressBar(int minX, int minY, int maxX, int maxY, float progress, CallbackInfo ci) {
        this.progress = this.endAnimProgress = progress;
    }

    //          1.14-
    @Inject(method = "renderProgressBar1_14", at = @At("HEAD"), remap = false)
    private void onRenderProgressBar1_14(int minX, int minY, int maxX, int maxY, float progress, float endAnimProgress, CallbackInfo ci) {
        this.progress = progress;
        this.endAnimProgress = endAnimProgress;
    }

    //      Bordures
    //          1.16+
    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "fill", ordinal = 0, remap = false), index = 4)
    private int adjustBarBorder(int color) {
        return colorLerp(endAnimProgress, BG_COLOR, BORDER_COLOR) | 0xff000000;
    }

    //          1.15-
    @ModifyArg(method = {"renderProgressBar", "renderProgressBar1_14"}, at = @At(value = "INVOKE", target = "fill1_15", ordinal = 0, remap = false), index = 4)
    private int adjustBarBorder1_15(int color) { return adjustBarBorder(color); }

    //      Fond
    //          1.16+
    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "fill", ordinal = 1, remap = false), index = 4)
    private int adjustBarBackground(int color) {
        return BG_COLOR | 0xff000000;
    }

    //          1.15-
    @ModifyArg(method = {"renderProgressBar", "renderProgressBar1_14"}, at = @At(value = "INVOKE", target = "fill1_15", ordinal = 1, remap = false), index = 4)
    private int adjustBarBackground1_15(int color) { return adjustBarBackground(color); }

    //      Barre
    //          1.16+
    @ModifyArg(method = "renderProgressBar", at = @At(value = "INVOKE", target = "fill", ordinal = 2, remap = false), index = 4)
    private int adjustBarColor(int color) {
        return colorLerp(endAnimProgress, BG_COLOR, BAR_COLOR) | 0xff000000;
    }

    //          1.15-
    @ModifyArg(method = {"renderProgressBar", "renderProgressBar1_14"}, at = @At(value = "INVOKE", target = "fill1_15", ordinal = 2, remap = false), index = 4)
    private int adjustBarColor1_15(int color) { return adjustBarColor(color); }



    /** Interpolation linéaire entre deux couleurs */
    @Unique
    private static int colorLerp(float val, int col1, int col2) {
        return channelLerp(val, col1, col2, 16)
            | channelLerp(val, col1, col2, 8)
            | channelLerp(val, col1, col2, 0);
    }

    /** Interpolation linéaire entre deux cannaux de 8 bits de deux couleurs */
    @Unique
    private static int channelLerp(float val, int col1, int col2, int offset) {
        return Math.round(
            MathHelper.lerp(val, (col1 >> offset) & 0xff, (col2 >> offset) & 0xff)
        ) << offset;
    }

}
