package io.github.a5b84.darkloadingscreen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.util.math.MathHelper;

/**
 * @see SplashScreen
 */
@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin extends DrawableHelper {

    private static final int BG_COLOR = 0x181818;
    private static final int BAR_COLOR = 0xe22837; // Couleur vanilla
    private static final int BORDER_COLOR = 0x303030;

    @Shadow private float progress;

    /** Change la couleur de l'arrière-plan.
     * @see SplashScreen#render */
    @ModifyArg(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V"), index = 4)
    private int adjustBackground(int color) {
        return BG_COLOR | (color & 0xff000000);
    }

    /** Change les couleur de la barre, plus simple que de le faire
     * avec des ModifyArg et tout vu que c'est court.
     * @see SplashScreen#renderProgressBar */
    @Inject(method = "renderProgressBar", at = @At("HEAD"), cancellable = true)
    private void onRenderProgressBar(int minX, int minY, int maxX, int maxY, float progress, CallbackInfo ci) {
        final int progressWidth = MathHelper.ceil((maxX - minX - 1) * this.progress);

        // Bordures
        fill(
            minX - 1, minY - 1, maxX + 1, maxY + 1,
            colorLerp(progress, BG_COLOR, BORDER_COLOR) | 0xff000000
        );

        // Fond
        fill(minX, minY, maxX, maxY, BG_COLOR | 0xff000000);

        // Barre
        fill(
            minX + 1, minY + 1, minX + progressWidth, maxY - 1,
            colorLerp(progress, BG_COLOR, BAR_COLOR) | 0xff000000
        );

        ci.cancel();
    }



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
