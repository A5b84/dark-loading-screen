package io.github.a5b84.darkloadingscreen;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import io.github.a5b84.darkloadingscreen.config.Config;
import io.github.a5b84.darkloadingscreen.mixin.GlCommandEncoderMixin;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.screen.SplashOverlay;
import net.minecraft.util.Identifier;

public class DarkLoadingScreen {

  public static final String MOD_ID = "dark-loading-screen";

  /**
   * Vanilla fade in duration
   *
   * @see SplashOverlay#render
   */
  public static final float VANILLA_FADE_IN_DURATION = 500;

  /**
   * Vanilla fade out duration
   *
   * @see SplashOverlay#render
   */
  public static final float VANILLA_FADE_OUT_DURATION = 1000;

  /**
   * Render pipeline for the part of the Mojang logo that are darker than the background.
   *
   * @see RenderPipelines#MOJANG_LOGO
   * @see GlCommandEncoderMixin
   */
  public static final RenderPipeline MOJANG_LOGO_SHADOWS =
      RenderPipelines.register(
          RenderPipeline.builder(RenderPipelines.POSITION_TEX_COLOR_SNIPPET)
              .withLocation(Identifier.of(MOD_ID, "pipeline/mojang_logo_shadows"))
              .withBlend(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE))
              .build());

  public static Config config = Config.read();
}
