package io.github.a5b84.darkloadingscreen.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.a5b84.darkloadingscreen.Mod;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin {

    private SplashScreenMixin() {}

    /** Descriptor for {@link SplashScreen#fill(MatrixStack, int, int, int, int, int)} */
    private static final String FILL_DESC = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V";

    @Unique private float logoAlpha = 1f;



    /** Background */
    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = FILL_DESC), index = 5)
    private int adjustBg(int color) {
        return colorWithAlpha(Mod.config.bg, color);
    }



    /** Updates the bar alpha and renders its background */
    @Inject(method = "renderProgressBar", at = @At("HEAD"))
    private void onRenderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity, CallbackInfo info) {
        Mod.progressBarAlpha = (int) (0xff * opacity) << 24;
        // TODO split in 4 fill()'s
        DrawableHelper.fill(
                matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1,
                Mod.config.barBg | Mod.progressBarAlpha
        );
    }



    // Logo

    /** Gets the logo alpha */
    @Inject(method = "render", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"))
    private void storeLogoAlpha(
            MatrixStack matrices, int mouseX, int mouseY, float delta,
            CallbackInfo info, int scaledWidth, int scaledHeight,
            long measuringTime, float f, float g, float alpha, int p, int q, double d, int r, double e, int s
    ) {
        logoAlpha = alpha;
    }

    /** Changes the logo color */
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V"))
    private void drawTextureProxy(
        MatrixStack matrices, int x, int y, int width, int height,
        float u, float v, int regionWidth, int regionHeight,
        int textureWidth, int textureHeight
    ) {
        // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
        // causes an ugly outline so we add/substract the difference between
        // the logo and the background instead

        //noinspection deprecation
        RenderSystem.color4f(
                Mod.config.logoR - Mod.config.bgR,
                Mod.config.logoG - Mod.config.bgG,
                Mod.config.logoB - Mod.config.bgB,
                logoAlpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        //noinspection deprecation
        RenderSystem.color4f(
                Mod.config.bgR - Mod.config.logoR,
                Mod.config.bgG - Mod.config.logoG,
                Mod.config.bgB - Mod.config.logoB,
                logoAlpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
    }



    /** Calls {@link PreviewSplashScreen#onDone()} when the screen disappears */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PreviewSplashScreen) {
            // Cast en objet pour qu'IntelliJ fasse pas chier
            ((PreviewSplashScreen) (Object) this).onDone();
        }
    }



    /** @return {@code color} with the alpha channel of {@code alpha} */
    private static int colorWithAlpha(int color, int alpha) {
        return color | (alpha & 0xff000000);
    }



    @ModifyConstant(method = "render", constant = @Constant(floatValue = Mod.VANILLA_FADE_IN_DURATION))
    private float getFadeInTime(float old) {
        return Mod.config.fadeInMs;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = Mod.VANILLA_FADE_OUT_DURATION))
    private float getFadeOutTime(float old) {
        return Mod.config.fadeOutMs;
    }



    /** Mixins applied only with OptiFine */
    @Mixin(SplashScreen.class)
    public static abstract class OptifineOnly {
        /** Bar border */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC), index = 5)
        private int adjustBarBorder(int color) { return Mod.getBarBorderColor(); }

        /** Bar content */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 5), index = 5)
        private int adjustBarColor(int color) { return Mod.getBarColor(); }
    }

    /** Mixins applied only without OptiFine */
    @Mixin(SplashScreen.class)
    public static abstract class NoOptifine {
        /** Bar border */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC), index = 5)
        private int adjustBarBorder(int color) { return Mod.getBarBorderColor(); }

        /** Bar content */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 4), index = 5)
        private int adjustBarColor(int color) { return Mod.getBarColor(); }
    }

}
