package io.github.a5b84.darkloadingscreen.mixin;

import io.github.a5b84.darkloadingscreen.SharedMixinMethods;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixinNoOptifine {

    // Progress bar

    /** Renders the bar background and changes the main bar color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/BackgroundHelper$ColorMixer;getArgb(IIII)I"),
            ordinal = 6)
    private int modifyBarColor(int barColor, MatrixStack matrices, int x1, int y1, int x2, int y2) {
        int alpha = barColor & 0xff000000;

        // Bar background
        DrawableHelper.fill(matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1, config.barBg | alpha);

        return config.bar | alpha;
    }

    /** Changes the bar border color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 0, shift = At.Shift.AFTER),
            ordinal = 6)
    private int modifyBarBorderColor(int color) {
        return config.border | color & 0xff000000;
    }


    // Logo

    /** Prepares for drawing the logo highlights */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V", ordinal = 0))
    private void onBeforeRenderLogo(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        SharedMixinMethods.setShaderColorToLogoHighlights();
    }

    /**
     * Draws the logo shadows
     * Original names for future reference:
     * @param scaledWidth i
     * @param scaledHeight j
     * @param now l
     * @param fadeOutProgress f
     * @param fadeInProgress g
     * @param alpha s
     * @param x t
     * @param y u
     * @param height d
     * @param halfHeight v
     * @param width e
     * @param halfWidth w
     */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V", ordinal = 1, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onAfterRenderLogo(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, int scaledWidth, int scaledHeight, long now, float fadeOutProgress, float fadeInProgress, float alpha, int x, int y, double height, int halfHeight, double width, int halfWidth) {
        SharedMixinMethods.beforeDrawLogoShadows();
        DrawableHelper.drawTexture(matrices, x - halfWidth, y - halfHeight, halfWidth, (int) height, -0.0625F, 0, 120, 60, 120, 120);
        DrawableHelper.drawTexture(matrices, x, y - halfHeight, halfWidth, (int) height, 0.0625F, 60, 120, 60, 120, 120);
        SharedMixinMethods.afterDrawLogoShadows();
    }

}
