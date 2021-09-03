package io.github.a5b84.darkloadingscreen.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashOverlay;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    @Mutable @Shadow private static @Final IntSupplier BRAND_ARGB;


    /** Changes the background color */
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo ci) {
        BRAND_ARGB = () -> config.bg | 0xff000000;
    }


    // Progress bar

    /** Renders the bar background and changes the color to the main bar color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/client/gui/hud/BackgroundHelper$ColorMixer;getArgb(IIII)I"),
            ordinal = 6)
    private int modifyBarColor(int barColor, MatrixStack matrices, int x1, int y1, int x2, int y2) {
        int alpha = barColor & 0xff000000;

        // Bar background
        DrawableHelper.fill(matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1,
                config.barBg | alpha);

        return config.bar | alpha;
    }

    /** Changes the color to the bar border color */
    @ModifyVariable(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V", ordinal = 0, shift = At.Shift.AFTER),
            ordinal = 6)
    private int modifyBarBorderColor(int color) {
        return config.border | color & 0xff000000;
    }


    /** Changes the logo color */
    // TODO Fix logo recoloring (broke in 1.17)
    // @Redirect(method = "render",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V"))
    @Unique
    private void drawLogoProxy(
        MatrixStack matrices, int x, int y, int width, int height,
        float u, float v, int regionWidth, int regionHeight,
        int textureWidth, int textureHeight
    ) {
        // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
        // causes an ugly outline so we add/substract the difference between
        // the logo and the background instead

        float alpha = RenderSystem.getShaderColor()[3];

        RenderSystem.setShaderColor(
                config.logoR - config.bgR,
                config.logoG - config.bgG,
                config.logoB - config.bgB,
                alpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);

        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        RenderSystem.setShaderColor(
                config.bgR - config.logoR,
                config.bgG - config.logoG,
                config.bgB - config.logoB,
                alpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);

        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.setShaderColor(1, 1, 1, alpha);
    }


    /** Calls {@link PreviewSplashOverlay#onDone()} when the screen disappears */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PreviewSplashOverlay previewScreen) {
            previewScreen.onDone();
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
