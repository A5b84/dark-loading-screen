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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.IntSupplier;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    @Mutable @Shadow private static @Final IntSupplier BRAND_ARGB;

    @Shadow @Final private boolean reloading;

    /** Changes the background color */
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo ci) {
        BRAND_ARGB = () -> config.bg;
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

    // Logo color
    // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
    // causes an ugly outline so we add/substract the difference between
    // the logo and the background instead

    /** Changes the logo color to render the parts brighter than the background */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashOverlay;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V", ordinal = 0))
    private void onBeforeRenderLogo(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        float[] shaderColor = RenderSystem.getShaderColor();
        shaderColor[0] = config.logoR - config.bgR;
        shaderColor[1] = config.logoG - config.bgG;
        shaderColor[2] = config.logoB - config.bgB;
    }

    /**
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
    private void drawLogoProxy(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci, int scaledWidth, int scaledHeight, long now, float fadeOutProgress, float fadeInProgress, float alpha, int x, int y, double height, int halfHeight, double width, int halfWidth) {
        float[] shaderColor = RenderSystem.getShaderColor();
        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        shaderColor[0] *= -1;
        shaderColor[1] *= -1;
        shaderColor[2] *= -1;
        DrawableHelper.drawTexture(matrices, x - halfWidth, y - halfHeight, halfWidth, (int) height, -0.0625F, 0, 120, 60, 120, 120);
        DrawableHelper.drawTexture(matrices, x, y - halfHeight, halfWidth, (int) height, 0.0625F, 60, 120, 60, 120, 120);

        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        shaderColor[0] = 1;
        shaderColor[1] = 1;
        shaderColor[2] = 1;
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


    // Hacky stuff about skipping rendering
    // TODO: find a way to remove this eventually

    @Unique private boolean skipNextLogoAndBarRendering;
    @Unique private boolean initialReloadComplete;

    /** Skips the next frame to prevent the logo from getting rendered twice
     * (for whatever reason) causing it to render twice as bright */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void onRenderScreen(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!initialReloadComplete) {
            initialReloadComplete = true;
            skipNextLogoAndBarRendering = true;
        }
    }

    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getWindow()Lnet/minecraft/client/util/Window;", ordinal = 2), cancellable = true)
    private void onBeforeBeforeLogo(CallbackInfo ci) {
        if (skipNextLogoAndBarRendering) {
            ci.cancel();
            skipNextLogoAndBarRendering = false;
        }
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        // The logo renders black the first frame when RenderSystem#blendEquation
        // is called, so we just skip the frame
        skipNextLogoAndBarRendering = !reloading;
        initialReloadComplete = reloading;
    }

}
