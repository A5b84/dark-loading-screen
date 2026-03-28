package io.github.a5b84.darkloadingscreen;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DestFactor;
import com.mojang.blaze3d.platform.SourceFactor;
import io.github.a5b84.darkloadingscreen.config.Config;
import io.github.a5b84.darkloadingscreen.mixin.GlCommandEncoderMixin;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;

public class DarkLoadingScreen {

  public static final String MOD_ID = "dark-loading-screen";

  /**
   * Render pipeline for the part of the Mojang logo that are darker than the background.
   *
   * @see RenderPipelines#MOJANG_LOGO
   * @see GlCommandEncoderMixin
   */
  public static final RenderPipeline MOJANG_LOGO_SHADOWS =
      RenderPipelines.register(
          RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
              .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/mojang_logo_shadows"))
              .withColorTargetState(
                  new ColorTargetState(new BlendFunction(SourceFactor.SRC_ALPHA, DestFactor.ONE)))
              .build());

  public static Config config = Config.read();
}
