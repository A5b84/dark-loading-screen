package io.github.a5b84.darkloadingscreen.mixin;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import io.github.a5b84.darkloadingscreen.DarkLoadingScreen;
import io.github.a5b84.darkloadingscreen.DrawTextureLambda;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashOverlay;
import java.util.function.IntSupplier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ARGB;
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

@Mixin(LoadingOverlay.class)
public abstract class LoadingOverlayMixin {

  @Mutable @Shadow private static @Final IntSupplier BRAND_BACKGROUND;

  /** Changes the background color */
  @Inject(method = "<clinit>", at = @At("RETURN"))
  private static void adjustBg(CallbackInfo ci) {
    BRAND_BACKGROUND = () -> config.backgroundColor;
  }

  // Progress bar

  /** Renders the bar background and changes the main bar color */
  @ModifyVariable(
      method = "drawProgressBar",
      at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/ARGB;color(IIII)I"),
      ordinal = 6)
  private int modifyBarColor(
      int barColor, GuiGraphics graphics, int x1, int y1, int x2, int y2, float opacity) {
    int alpha = barColor & 0xff000000;
    graphics.fill(x1 + 1, y1 + 1, x2 - 1, y2 - 1, config.barBackgroundColor | alpha);
    return config.barColor | alpha;
  }

  /** Changes the bar border color */
  @ModifyVariable(
      method = "drawProgressBar",
      at =
          @At(
              value = "INVOKE",
              target = "Lnet/minecraft/client/gui/GuiGraphics;fill(IIIII)V",
              ordinal = 0,
              shift = At.Shift.AFTER),
      ordinal = 6)
  private int modifyBarBorderColor(int color) {
    return config.barBorderColor | color & 0xff000000;
  }

  // Logo

  /** Changes the logo color */
  @WrapOperation(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/gui/GuiGraphics;blit(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/ResourceLocation;IIFFIIIIIII)V"))
  private void onDrawTexture(
      GuiGraphics graphics,
      RenderPipeline originalPipeline,
      ResourceLocation sprite,
      int x,
      int y,
      float u,
      float v,
      int width,
      int height,
      int regionWidth,
      int regionHeight,
      int textureWidth,
      int textureHeight,
      int color,
      Operation<Void> original) {
    // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
    // causes an ugly outline, so we render the logo twice (once for logo
    // channels that are brighter than the background, and another time
    // for those that are darker)

    int alpha = ARGB.alpha(color);

    DrawTextureLambda drawTexture =
        (pipeline, r, g, b) -> {
          if (r > 0 || g > 0 || b > 0) {
            original.call(
                graphics,
                pipeline,
                sprite,
                x,
                y,
                u,
                v,
                width,
                height,
                regionWidth,
                regionHeight,
                textureWidth,
                textureHeight,
                ARGB.color(
                    alpha,
                    ARGB.as8BitChannel(Math.max(r, 0)),
                    ARGB.as8BitChannel(Math.max(g, 0)),
                    ARGB.as8BitChannel(Math.max(b, 0))));
          }
        };

    // Order of draws is important because GlCommandEncoderMixin resets
    // the blend equation only when MOJANG_LOGO_SHADOWS is swapped out for
    // something else. One way to ensure this is to draw shadows before
    // highlights.
    drawTexture.call(
        DarkLoadingScreen.MOJANG_LOGO_SHADOWS,
        config.backgroundRed - config.logoRed,
        config.backgroundGreen - config.logoGreen,
        config.backgroundBlue - config.logoBlue);

    drawTexture.call(
        originalPipeline,
        config.logoRed - config.backgroundRed,
        config.logoGreen - config.backgroundGreen,
        config.logoBlue - config.backgroundBlue);
  }

  /** Calls {@link PreviewSplashOverlay#onRemoved()} when the overlay is removed */
  @Inject(
      method = "render",
      at =
          @At(
              value = "INVOKE",
              target =
                  "Lnet/minecraft/client/Minecraft;setOverlay(Lnet/minecraft/client/gui/screens/Overlay;)V"))
  private void onSetOverlay(CallbackInfo info) {
    //noinspection ConstantConditions
    if ((Object) this instanceof PreviewSplashOverlay previewScreen) {
      previewScreen.onRemoved();
    }
  }

  @ModifyConstant(
      method = "render",
      constant = @Constant(floatValue = DarkLoadingScreen.VANILLA_FADE_IN_DURATION))
  private float getFadeInTime(float old) {
    return config.fadeInMillis;
  }

  @ModifyConstant(
      method = "render",
      constant = @Constant(floatValue = DarkLoadingScreen.VANILLA_FADE_OUT_DURATION))
  private float getFadeOutTime(float old) {
    return config.fadeOutMillis;
  }
}
