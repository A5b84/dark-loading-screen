package io.github.a5b84.darkloadingscreen;

import com.mojang.blaze3d.pipeline.RenderPipeline;

@FunctionalInterface
public interface DrawTextureLambda {
    void call(RenderPipeline pipeline, float x, float y, float z);
}
