package io.github.a5b84.darkloadingscreen.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import io.github.a5b84.darkloadingscreen.DrawTextureLambda;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashOverlay;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {

  @Mutable @Shadow private static @Final IntSupplier BRAND_BACKGROUND;

  @Inject(method = "<clinit>", at = @At("RETURN"))
  private static void modifyBackgroundColor(CallbackInfo ci) {
    BRAND_BACKGROUND = () -> DarkLoadingScreen.config.backgroundColor;
  }

  @Definition(id = "color", method = "Lnet/minecraft/util/ARGB;color(IIII)I")
  @Expression("? = color(?, ?, ?, ?)")
  @ModifyVariable(
      method = "extractProgressBar",
      at = @At(value = "MIXINEXTRAS:EXPRESSION", shift = At.Shift.AFTER),
      name = "white")
  private int modifyBarColorAndFillBackground(
      int white, GuiGraphicsExtractor graphics, int x0, int y0, int x1, int y1, float fade) {
    int alpha = white & 0xff000000;
    graphics.fill(
        x0 + 1, y0 + 1, x1 - 1, y1 - 1, DarkLoadingScreen.config.barBackgroundColor | alpha);
    return DarkLoadingScreen.config.barColor | alpha;
  }

  @ModifyVariable(
      method = "extractProgressBar",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;fill(IIIII)V",
              ordinal = 0,
              shift = At.Shift.AFTER),
      name = "white")
  private int modifyBarBorderColor(int white) {
    return DarkLoadingScreen.config.barBorderColor | white & 0xff000000;
  }

  /** Changes the logo color. */
  @WrapOperation(
      method = "extractRenderState",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIIIIII)V"))
  private void onBlitLogo(
      GuiGraphicsExtractor graphics,
      RenderPipeline renderPipeline,
      Identifier texture,
      int x,
      int y,
      float u,
      float v,
      int width,
      int height,
      int srcWidth,
      int srcHeight,
      int textureWidth,
      int textureHeight,
      int color,
      Operation<Void> original) {
    // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
    // causes an ugly outline, so we render the logo twice:
    // - once for channels that are brighter than the background
    // - once for those that are darker)

    int alpha = ARGB.alpha(color);

    DrawTextureLambda drawTexture =
        (pipeline, r, g, b) -> {
          if (r > 0 || g > 0 || b > 0) {
            original.call(
                graphics,
                pipeline,
                texture,
                x,
                y,
                u,
                v,
                width,
                height,
                srcWidth,
                srcHeight,
                textureWidth,
                textureHeight,
                ARGB.color(
                    alpha,
                    ARGB.as8BitChannel(Math.max(r, 0)),
                    ARGB.as8BitChannel(Math.max(g, 0)),
                    ARGB.as8BitChannel(Math.max(b, 0))));
          }
        };

    drawTexture.call(
        DarkLoadingScreen.MOJANG_LOGO_SHADOWS,
        DarkLoadingScreen.config.backgroundRed - DarkLoadingScreen.config.logoRed,
        DarkLoadingScreen.config.backgroundGreen - DarkLoadingScreen.config.logoGreen,
        DarkLoadingScreen.config.backgroundBlue - DarkLoadingScreen.config.logoBlue);

    drawTexture.call(
        renderPipeline,
        DarkLoadingScreen.config.logoRed - DarkLoadingScreen.config.backgroundRed,
        DarkLoadingScreen.config.logoGreen - DarkLoadingScreen.config.backgroundGreen,
        DarkLoadingScreen.config.logoBlue - DarkLoadingScreen.config.backgroundBlue);
  }

  @Inject(
      method = "extractRenderState",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/Gui;setOverlay(Lnet/minecraft/client/gui/screens/Overlay;)V"))
  private void onOverlayRemoved(CallbackInfo info) {
    //noinspection ConstantConditions
    if ((Object) this instanceof PreviewSplashOverlay previewScreen) {
      previewScreen.onRemoved();
    }
  }

  @ModifyVariable(method = "extractRenderState", at = @At(value = "STORE"), name = "fadeInAnim")
  private float modifyFadeInAnim(float fadeInAnim) {
    return modifyFadeAnim(
        fadeInAnim, LoadingOverlay.FADE_IN_TIME, DarkLoadingScreen.config.fadeInMillis);
  }

  @ModifyVariable(method = "extractRenderState", at = @At(value = "STORE"), name = "fadeOutAnim")
  private float modifyFadeOutAnim(float fadeOutAnim) {
    return modifyFadeAnim(
        fadeOutAnim, LoadingOverlay.FADE_OUT_TIME, DarkLoadingScreen.config.fadeOutMillis);
  }

  @Unique
  private float modifyFadeAnim(float value, float vanillaValue, float newValue) {
    return value >= 0 ? value * vanillaValue / newValue : value;
  }
}
