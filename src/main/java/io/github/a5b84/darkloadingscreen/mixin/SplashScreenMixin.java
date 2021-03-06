package io.github.a5b84.darkloadingscreen.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.a5b84.darkloadingscreen.Mod;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.IntSupplier;

@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin {

    /** Descriptor for {@link SplashScreen#fill(MatrixStack, int, int, int, int, int)} */
    private static final String FILL_DESC = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V";



    @Mutable @Shadow private static @Final IntSupplier BRAND_ARGB;

    @Shadow private static int withAlpha(int color, int alpha) {
        return 0;
    }



    /** Background */
    @SuppressWarnings("UnresolvedMixinReference")
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void adjustBg(CallbackInfo ci) {
        BRAND_ARGB = () -> Mod.config.bg | 0xff000000;
    }



    // Progress bar

    /** Changes the progress bar border color and draws its background */
    @Redirect(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/hud/BackgroundHelper$ColorMixer;getArgb(IIII)I"))
    private int progressBarBorderProxy(int a, int r, int g, int b, MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity) {
        // Bar background
        DrawableHelper.fill(
                matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1,
                withAlpha(Mod.config.barBg, a)
        );

        // Bar border
        return withAlpha(Mod.config.border, a);
    }

    /** Modifies the bar color */
    @ModifyArg(method = "renderProgressBar",
            at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 0), index = 5)
    private int adjustBarColor(int color) {
        return Mod.config.bar | color & 0xff000000;
    }



    /** Changes the logo color */
    // @Redirect(method = "render",
    //         at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V"))
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
                Mod.config.logoR - Mod.config.bgR,
                Mod.config.logoG - Mod.config.bgG,
                Mod.config.logoB - Mod.config.bgB,
                alpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);

        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        RenderSystem.setShaderColor(
                Mod.config.bgR - Mod.config.logoR,
                Mod.config.bgG - Mod.config.logoG,
                Mod.config.bgB - Mod.config.logoB,
                alpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);

        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.setShaderColor(1, 1, 1, alpha);
    }



    /** Calls {@link PreviewSplashScreen#onDone()} when the screen disappears */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PreviewSplashScreen previewScreen) {
            // Casting because SplashScreenMixin doesn't extend PreviewSplashScreen
            previewScreen.onDone();
        }
    }



    @ModifyConstant(method = "render", constant = @Constant(floatValue = Mod.VANILLA_FADE_IN_DURATION))
    private float getFadeInTime(float old) {
        return Mod.config.fadeInMs;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = Mod.VANILLA_FADE_OUT_DURATION))
    private float getFadeOutTime(float old) {
        return Mod.config.fadeOutMs;
    }

}
