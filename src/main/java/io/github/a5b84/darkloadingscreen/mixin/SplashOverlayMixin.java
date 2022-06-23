package io.github.a5b84.darkloadingscreen.mixin;

import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashOverlay;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

@Mixin(SplashOverlay.class)
public abstract class SplashOverlayMixin {

    @Mutable @Shadow private static @Final IntSupplier BRAND_ARGB;

    @Shadow @Final private boolean reloading;

    /** Changes the background color */
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo ci) {
        BRAND_ARGB = () -> config.bg;
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


    // Hacky stuff about skipping rendering
    // TODO: find a way to remove this eventually

    @Unique private boolean skipNextLogoAndBarRendering;
    @Unique private static boolean initialReloadComplete = false;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CallbackInfo ci) {
        // The logo renders black the first frame when RenderSystem#blendEquation
        // is called, so we just skip the frame
        skipNextLogoAndBarRendering = !reloading;
    }

    /**
     * Skips the frame when the overlay starts fading out to prevent the logo
     * from getting rendered twice (for whatever reason) causing it to appear
     * twice as bright
     */
    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;render(Lnet/minecraft/client/util/math/MatrixStack;IIF)V"))
    private void onRenderScreen(MatrixStack matrices, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (!initialReloadComplete) {
            initialReloadComplete = true;
            skipNextLogoAndBarRendering = true;
        }
    }

    /** Skips rendering when {@link #skipNextLogoAndBarRendering} is true */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;getWindow()Lnet/minecraft/client/util/Window;", ordinal = 2), cancellable = true)
    private void onBeforeBeforeLogo(CallbackInfo ci) {
        if (skipNextLogoAndBarRendering) {
            ci.cancel();
            skipNextLogoAndBarRendering = false;
        }
    }

}
