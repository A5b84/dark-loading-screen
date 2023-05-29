package io.github.a5b84.darkloadingscreen.mixin.compat.optifine;

import io.github.a5b84.darkloadingscreen.SharedMixinMethods;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    @Shadow private static int withAlpha(int color, int alpha) {
        return 0;
    }


    // Progress bar

    @Inject(method = "renderProgressBar", at = @At("HEAD"))
    private void onRenderProgressBar(DrawContext context, int x1, int y1, int x2, int y2, float opacity, CallbackInfo ci) {
        // Bar background
        context.fill(
                x1 + 1, y1 + 1, x2 - 1, y2 - 1,
                withAlpha(config.barBg, Math.round(opacity * 255))
        );
        // (For some reason, putting this in the method bellow causes the
        // background to render in front of the bar even though it's called
        // earlier)
    }

    /** Changes the progress bar border color and draws its background */
    @Redirect(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/ColorHelper$Argb;getArgb(IIII)I"))
    private int progressBarBorderProxy(int a, int r, int g, int b, DrawContext context, int x1, int y1, int x2, int y2, float opacity) {
        // Bar border
        return withAlpha(config.border, a);
    }

    /** Modifies the bar color */
    @ModifyArg(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;fill(IIIII)V", ordinal = 1),
            index = 5)
    private int adjustBarColor(int color) {
        return config.bar | color & 0xff000000;
    }


    // Logo

    /** Changes the color of the logo */
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIFFIIII)V"))
    private void drawLogoProxy(DrawContext context, Identifier logo, int x, int y, int width, int height, float u, float v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        // Highlights
        SharedMixinMethods.setShaderColorToLogoHighlights();
        context.drawTexture(logo, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);

        // Shadows
        SharedMixinMethods.beforeDrawLogoShadows();
        context.drawTexture(logo, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        SharedMixinMethods.afterDrawLogoShadows();
    }

}
