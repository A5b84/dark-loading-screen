package io.github.a5b84.darkloadingscreen.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import io.github.a5b84.darkloadingscreen.TriFloatConsumer;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashOverlay;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ColorHelper;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;
import java.util.function.IntSupplier;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    @Mutable @Shadow private static @Final IntSupplier BRAND_ARGB;

    /** Changes the background color */
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo ci) {
        BRAND_ARGB = () -> config.bg;
    }


    // Progress bar

    /** Renders the bar background and changes the main bar color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/math/ColorHelper;getArgb(IIII)I"),
            ordinal = 6)
    private int modifyBarColor(int barColor, DrawContext context, int x1, int y1, int x2, int y2, float opacity) {
        int alpha = barColor & 0xff000000;
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

    /** Changes the logo color */
    @WrapOperation(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIFFIIIIIII)V"))
    private void onDrawTexture(DrawContext context, Function<Identifier, RenderLayer> renderLayers, Identifier sprite, int x, int y, float u, float v, int width, int height, int regionWidth, int regionHeight, int textureWidth, int textureHeight, int color, Operation<Void> original) {
        // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
        // causes an ugly outline, so we render the logo twice (once for logo
        // channels that are brighter than the background, and another time
        // for those that are darker)

        int alpha = ColorHelper.getAlpha(color);

        TriFloatConsumer drawTexture = (r, g, b) -> {
            if (r < 0) r = 0;
            if (g < 0) g = 0;
            if (b < 0) b = 0;

            if (r > 0 || g > 0 || b > 0) {
                original.call(context, renderLayers, sprite, x, y, u, v, width, height, regionWidth, regionHeight, textureWidth, textureHeight, ColorHelper.getArgb(
                        alpha,
                        ColorHelper.channelFromFloat(r),
                        ColorHelper.channelFromFloat(g),
                        ColorHelper.channelFromFloat(b)
                ));
            }
        };

        drawTexture.accept(
                config.logoR - config.bgR,
                config.logoG - config.bgG,
                config.logoB - config.bgB
        );
        context.draw();

        GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        drawTexture.accept(
                config.bgR - config.logoR,
                config.bgG - config.logoG,
                config.bgB - config.logoB
        );
        context.draw(); // Draw now because draws are queued until later and
        // so wouldn't use the blend equation set here
        GL14.glBlendEquation(GL14.GL_FUNC_ADD);
    }


    /** Calls {@link PreviewSplashOverlay#onRemoved()} when the overlay is removed */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PreviewSplashOverlay previewScreen) {
            previewScreen.onRemoved();
        }
    }


    @ModifyConstant(method = "render", constant = @Constant(floatValue = DarkLoadingScreen.VANILLA_FADE_IN_DURATION))
    private float getFadeInTime(float old) {
        return config.fadeInMs;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = DarkLoadingScreen.VANILLA_FADE_OUT_DURATION))
    private float getFadeOutTime(float old) {
        return config.fadeOutMs;
    }
}
