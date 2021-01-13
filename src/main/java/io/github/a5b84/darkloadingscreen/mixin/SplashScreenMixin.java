package io.github.a5b84.darkloadingscreen.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.a5b84.darkloadingscreen.Mod;
import io.github.a5b84.darkloadingscreen.config.PreviewSplashScreen;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(SplashScreen.class)
public abstract class SplashScreenMixin {

    private SplashScreenMixin() {}

    /** Descripteur de {@link SplashScreen#fill(MatrixStack, int, int, int, int, int)} */
    private static final String FILL_DESC = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V";

    /** Transparence du logo (entre {@code 0} et {@code 1} */
    @Unique private float logoAlpha = 1f;



    /** Fond */
    @ModifyArg(method = "render",
            at = @At(value = "INVOKE", target = FILL_DESC), index = 5)
    private int adjustBg(int color) {
        return colorWithAlpha(Mod.config.bg, color);
    }



    /** Calcule la transparence de la barre et affiche le fond */
    @Inject(method = "renderProgressBar", at = @At("HEAD"))
    private void onRenderProgressBar(MatrixStack matrices, int x1, int y1, int x2, int y2, float opacity, CallbackInfo info) {
        Mod.progressBarAlpha = (int) (0xff * opacity) << 24;
        // TODO séparer en 4 fill() pour pas afficher derrière la barre
        DrawableHelper.fill(
                matrices, x1 + 1, y1 + 1, x2 - 1, y2 - 1,
                Mod.config.barBg | Mod.progressBarAlpha
        );
    }



    // Logo

    /** Récupère la transparence du logo */
    @Inject(method = "render", locals = LocalCapture.CAPTURE_FAILSOFT,
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;enableBlend()V"))
    private void storeLogoAlpha(
            MatrixStack matrices, int mouseX, int mouseY, float delta,
            CallbackInfo info, int scaledWidth, int scaledHeight,
            long measuringTime, float f, float g, float alpha, int p, int q, double d, int r, double e, int s
    ) {
        logoAlpha = alpha;
    }

    /** Modifie la couleur du logo */
    @Redirect(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIFFIIII)V"))
    private void drawTextureProxy(
        MatrixStack matrices, int x, int y, int width, int height,
        float u, float v, int regionWidth, int regionHeight,
        int textureWidth, int textureHeight
    ) {
        // On ajoute/soustrait la différence entre le logo et le fond
        // (selon que ça soit plus clair ou plus sombre)
        // On peut pas juste utiliser
        // RenderSystem.blendFunc(GL_SRC_ALPHA, Gl_ONE_MINUS_SOURCE_ALPHA)
        // parce que ça fait un contour moche qui est visible :(

        //noinspection deprecation
        RenderSystem.color4f(
                Mod.config.logoR - Mod.config.bgR,
                Mod.config.logoG - Mod.config.bgG,
                Mod.config.logoB - Mod.config.bgB,
                logoAlpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        RenderSystem.blendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
        //noinspection deprecation
        RenderSystem.color4f(
                Mod.config.bgR - Mod.config.logoR,
                Mod.config.bgG - Mod.config.logoG,
                Mod.config.bgB - Mod.config.logoB,
                logoAlpha
        );
        DrawableHelper.drawTexture(matrices, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
    }



    /** Appelle {@link PreviewSplashScreen#onDone()} quand l'écran de
     * chargement est fermé */
    @Inject(method = "render",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/MinecraftClient;setOverlay(Lnet/minecraft/client/gui/screen/Overlay;)V"))
    private void onSetOverlay(CallbackInfo info) {
        //noinspection ConstantConditions
        if ((Object) this instanceof PreviewSplashScreen) {
            // Cast en objet pour qu'IntelliJ fasse pas chier
            ((PreviewSplashScreen) (Object) this).onDone();
        }
    }



    /** @return une couleur avec la transparence d'une autre */
    private static int colorWithAlpha(int color, int alpha) {
        return color | (alpha & 0xff000000);
    }



    @ModifyConstant(method = "render", constant = @Constant(floatValue = Mod.VANILLA_FADE_IN_TIME))
    private float getFadeInTime(float old) {
        return Mod.config.fadeInMs;
    }

    @ModifyConstant(method = "render", constant = @Constant(floatValue = Mod.VANILLA_FADE_OUT_TIME))
    private float getFadeOutTime(float old) {
        return Mod.config.fadeOutMs;
    }



    /** Mixins appliquées que si OptiFine est installé */
    @Mixin(SplashScreen.class)
    public static abstract class OptifineOnly {
        /** Contour de la barre */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC), index = 5)
        private int adjustBarBorder(int color) { return Mod.getBarBorderColor(); }

        /** Contenu de la barre */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 5), index = 5)
        private int adjustBarColor(int color) { return Mod.getBarColor(); }
    }

    /** Mixins appliquées que si OptiFine est pas installé */
    @Mixin(SplashScreen.class)
    public static abstract class NoOptifine {
        /** Contour de la barre */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC), index = 5)
        private int adjustBarBorder(int color) { return Mod.getBarBorderColor(); }

        /** Contenu de la barre */
        @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESC, ordinal = 4), index = 5)
        private int adjustBarColor(int color) { return Mod.getBarColor(); }
    }

}
