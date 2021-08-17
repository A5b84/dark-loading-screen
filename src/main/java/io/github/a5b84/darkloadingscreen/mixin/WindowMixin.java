package io.github.a5b84.darkloadingscreen.mixin;

import net.minecraft.client.util.Window;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLCapabilities;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import static io.github.a5b84.darkloadingscreen.DarkLoadingScreen.config;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;

/** Changes the screen color before the splash overlay shows up */
@Mixin(Window.class)
public abstract class WindowMixin {

    @Shadow private @Final long handle;

    @Redirect(method = "<init>",
        at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL;createCapabilities()Lorg/lwjgl/opengl/GLCapabilities;", remap = false))
    private GLCapabilities onCreateCapabilities() {
        glfwSwapBuffers(handle); // Hack (?) that makes the window black
        //      while `GL.createCapabilities()` is being called
        //      (takes a couple seconds)

        GLCapabilities result = GL.createCapabilities();

        glClearColor(config.bgR, config.bgG, config.bgB, 1);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        glfwSwapBuffers(handle);

        return result;
    }

}
