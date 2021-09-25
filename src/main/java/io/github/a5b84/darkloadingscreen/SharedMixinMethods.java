package io.github.a5b84.darkloadingscreen;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.opengl.GL14;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;

public final class SharedMixinMethods {

    private SharedMixinMethods() {}


    // Logo color
    // `RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)`
    // causes an ugly outline, so we add/substract the difference between
    // the logo and the background instead

    /** Sets the shader color to the logo highlights color */
    public static void setShaderColorToLogoHighlights() {
        float[] shaderColor = RenderSystem.getShaderColor();
        shaderColor[0] = config.logoR - config.bgR;
        shaderColor[1] = config.logoG - config.bgG;
        shaderColor[2] = config.logoB - config.bgB;
    }

    /** Prepares for drawing the logo shadows */
    public static void beforeDrawLogoShadows() {
        float[] shaderColor = RenderSystem.getShaderColor();
        shaderColor[0] *= -1;
        shaderColor[1] *= -1;
        shaderColor[2] *= -1;
        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
    }

    /** Cleans changes caused by {@link #setShaderColorToLogoHighlights()}
     * and {@link #beforeDrawLogoShadows()} */
    public static void afterDrawLogoShadows() {
        float[] shaderColor = RenderSystem.getShaderColor();
        shaderColor[0] = 1;
        shaderColor[1] = 1;
        shaderColor[2] = 1;
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
    }

}
