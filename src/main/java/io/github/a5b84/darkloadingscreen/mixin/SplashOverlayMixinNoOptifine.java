package io.github.a5b84.darkloadingscreen.mixin;

import io.github.a5b84.darkloadingscreen.SharedMixinMethods;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixinNoOptifine {

    @Shadow @Final static Identifier LOGO;

    // Progress bar

    /** Renders the bar background and changes the main bar color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/ColorHelper$Argb;getArgb(IIII)I"),
            ordinal = 6)
    private int modifyBarColor(int barColor, DrawContext context, int x1, int y1, int x2, int y2, float opacity) {
        int alpha = barColor & 0xff000000;

        // Bar background
        context.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, config.barBg | alpha);

        return config.bar | alpha;
    }

    /** Changes the bar border color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 0, shift = At.Shift.AFTER),
            ordinal = 6)
    private int modifyBarBorderColor(int color) {
        return config.border | color & 0xff000000;
    }


    // Logo

    /** Prepares for drawing the logo highlights */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V", ordinal = 0))
    private void onBeforeRenderLogo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
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
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V", ordinal = 1, shift = At.Shift.AFTER),
            locals = LocalCapture.CAPTURE_FAILEXCEPTION)
    private void onAfterRenderLogo(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci, int scaledWidth, int scaledHeight, long now, float fadeOutProgress, float fadeInProgress, float alpha, int x, int y, double height, int halfHeight, double width, int halfWidth) {
        SharedMixinMethods.beforeDrawLogoShadows();
        context.drawTexture(LOGO, x - halfWidth, y - halfHeight, halfWidth, (int) height, -0.0625F, 0, 120, 60, 120, 120);
        context.drawTexture(LOGO, x, y - halfHeight, halfWidth, (int) height, 0.0625F, 60, 120, 60, 120, 120);
        SharedMixinMethods.afterDrawLogoShadows();
    }

}
