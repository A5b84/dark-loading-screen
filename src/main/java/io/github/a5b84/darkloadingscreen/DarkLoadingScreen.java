package io.github.a5b84.darkloadingscreen;

import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.ColorTargetState;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.BlendFactor;
import com.mojang.blaze3d.platform.BlendOp;
import io.github.a5b84.darkloadingscreen.config.Config;
import io.github.a5b84.darkloadingscreen.config.ConfigSerialization;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DarkLoadingScreen {

  public static final String MOD_ID = "dark-loading-screen";
  public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

  /**
   * Render pipeline for the part of the Mojang logo that are darker than the background.
   *
   * @see RenderPipelines#MOJANG_LOGO
   */
  public static final RenderPipeline MOJANG_LOGO_SHADOWS =
      RenderPipelines.register(
          RenderPipeline.builder(RenderPipelines.GUI_TEXTURED_SNIPPET)
              .withLocation(Identifier.fromNamespaceAndPath(MOD_ID, "pipeline/mojang_logo_shadows"))
              .withColorTargetState(
                  new ColorTargetState(
                      new BlendFunction(
                          BlendFactor.SRC_ALPHA, BlendFactor.ONE, BlendOp.REVERSE_SUBTRACT)))
              .build());

  public static Config config = ConfigSerialization.read();
}
