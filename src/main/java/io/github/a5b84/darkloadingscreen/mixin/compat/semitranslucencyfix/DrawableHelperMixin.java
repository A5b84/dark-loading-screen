package io.github.a5b84.darkloadingscreen.mixin.compat.semitranslucencyfix;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashOverlay;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DrawableHelper.class)
public class DrawableHelperMixin {

    /**
     * Adds compatibility for Semitranslucency Fix (https://modrinth.com/mod/semitranslucency)
     * by undoing what it does when the {@link SplashOverlay} is visible
     * (https://github.com/ruvaldak/Semitranslucency/blob/a70656c2e1b504417abc75e00a6a8797ed21471e/src/main/java/net/ims/semitranslucency/mixin/MixinDrawableHelper.java#L16)
     * @see SplashOverlay#render
     */
    @Inject(method = "drawTexturedQuad", at = @At(value = "HEAD", shift = At.Shift.AFTER))
    private static void drawTexturedQuad(CallbackInfo ci) {
        if (MinecraftClient.getInstance().getOverlay() instanceof SplashOverlay) {
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE);
        }
    }

}
