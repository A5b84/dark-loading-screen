package io.github.a5b84.darkloadingscreen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.a5b84.darkloadingscreen.Mod;
import net.minecraft.client.gui.screen.SplashScreen;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Grosse classe pour contenir des sous-classes qui contiennent les mixins
 * qui changent les couleurs de l'écran de chargement
 *
 * Liste des dataVersions :
 * https://minecraft.gamepedia.com/Data_version#List_of_data_versions
 *
 * @see SplashScreen
 */
public final class SplashScreenMixin {

    private SplashScreenMixin() {}

    /**
     * Descripteurs pour les mixins
     * 
     * Format : {nom}_{version} avec nom le nom (duh) (mais en camelCase)
     * et version la première version où c'est utilisé (1.14 minimum)
     */
    private static final String
        fill_20w17a = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V",
        fill_20w10a = "Lnet/minecraft/class_425;method_25294(IIIII)V",
        fill_1_14 = "Lnet/minecraft/class_425;fill(IIIII)V",
        render_20w10a = "Lnet/minecraft/class_425;method_25394(IIF)V",
        render_1_14 = "Lnet/minecraft/class_425;render(IIF)V",
        renderProgressBar_19w41a = "Lnet/minecraft/class_425;method_18103(IIIIF)V",
        renderProgressBar_1_14 = "Lnet/minecraft/class_425;method_18103(IIIIFF)V";




    /**
     * Fond
     * @see SplashScreen#render
     */
    public static class Bg {

        @Mixin(SplashScreen.class)
        public static abstract class a2529 {
            @ModifyArg(method = "render",
                at = @At(value = "INVOKE", target = fill_20w17a), index = 5)
            private int adjustBg(int color) { return Mod.getBg(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2512b2529 {
            @ModifyArg(method = render_20w10a,
                at = @At(value = "INVOKE", target = fill_20w10a), index = 4)
            private int adjustBg(int color) { return Mod.getBg(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2512 {
            @ModifyArg(method = render_1_14,
                at = @At(value = "INVOKE", target = fill_1_14), index = 4)
            private int adjustBg(int color) { return Mod.getBg(color); }
        }
    }



    /**
     * Màj des variables communes
     * @see SplashScreen#renderProgressBar
     */
    public static final class OnRenderBar {

        private OnRenderBar() {}

        @Mixin(SplashScreen.class)
        public static abstract class a2529 {
            @Inject(method = "renderProgressBar", at = @At("HEAD"))
            private void onRenderProgressBar(MatrixStack stack, int minX, int minY, int maxX, int maxY, float progress, CallbackInfo ci) {
                Mod.endAnimProgress = progress;
            }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2210b2529 {
            @Inject(method = renderProgressBar_19w41a, at = @At("HEAD"))
            private void onRenderProgressBar(int minX, int minY, int maxX, int maxY, float progress, CallbackInfo ci) {
                Mod.endAnimProgress = progress;
            }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2210 {
            @Inject(method = renderProgressBar_1_14, at = @At("HEAD"))
            private void onRenderProgressBar(int minX, int minY, int maxX, int maxY, float progress, float endAnimProgress, CallbackInfo ci) {
                Mod.endAnimProgress = endAnimProgress;
            }
        }
    }



    /**
     * Couleurs de la barre
     * @see SplashScreen#renderProgressBar
     */
    public static final class Bar {

        private Bar() {}

        @Mixin(SplashScreen.class)
        public static abstract class a2555 {
            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = fill_20w17a), index = 5)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = fill_20w17a, ordinal = 4), index = 5)
            private int adjustBarBg(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(SplashScreen.class)
        public static abstract class a2529b2555 {
            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = fill_20w17a, ordinal = 0), index = 5)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = fill_20w17a, ordinal = 1), index = 5)
            private int adjustBarBg(int color) { return Mod.getBarBg(color); }

            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = fill_20w17a, ordinal = 2), index = 5)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(SplashScreen.class)
        public static abstract class a2512b2529 {
            @ModifyArg(method = renderProgressBar_19w41a, remap = false,
                at = @At(value = "INVOKE", target = fill_20w10a, ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = renderProgressBar_19w41a, remap = false,
                at = @At(value = "INVOKE", target = fill_20w10a, ordinal = 1), index = 4)
            private int adjustBarBg(int color) { return Mod.getBarBg(color); }

            @ModifyArg(method = renderProgressBar_19w41a, remap = false,
                at = @At(value = "INVOKE", target = fill_20w10a, ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2210b2512 {
            @ModifyArg(method = renderProgressBar_19w41a,
                at = @At(value = "INVOKE", target = fill_1_14, ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = renderProgressBar_19w41a,
                at = @At(value = "INVOKE", target = fill_1_14, ordinal = 1), index = 4)
            private int adjustBarBg(int color) { return Mod.getBarBg(color); }

            @ModifyArg(method = renderProgressBar_19w41a,
                at = @At(value = "INVOKE", target = fill_1_14, ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2210 {
            @ModifyArg(method = renderProgressBar_1_14,
                at = @At(value = "INVOKE", target = fill_1_14, ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = renderProgressBar_1_14,
                at = @At(value = "INVOKE", target = fill_1_14, ordinal = 1), index = 4)
            private int adjustBarBg(int color) { return Mod.getBarBg(color); }

            @ModifyArg(method = renderProgressBar_1_14,
                at = @At(value = "INVOKE", target = fill_1_14, ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }
    }

}
