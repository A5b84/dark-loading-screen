package io.github.a5b84.darkloadingscreen;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;

@FunctionalInterface
public interface BlitLambda {
  void call(RenderPipeline pipeline, float x, float y, float z);
}
