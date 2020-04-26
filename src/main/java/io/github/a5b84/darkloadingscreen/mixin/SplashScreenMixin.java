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
 * @see SplashScreen
 */
public final class SplashScreenMixin {

    private SplashScreenMixin() {}

    private static final String FILL_DESCRIPTOR = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(Lnet/minecraft/client/util/math/MatrixStack;IIIII)V";



    /**
     * Fond
     * @see SplashScreen#render
     */
    public static class Bg {

        @Mixin(SplashScreen.class)
        public static abstract class a2529 {
            @ModifyArg(method = "render",
                at = @At(value = "INVOKE", target = FILL_DESCRIPTOR), index = 5)
            private int adjustBackground(int color) { return Mod.getBackground(color); }
        }

        @Mixin(SplashScreen.class)
        public static abstract class a2512b2529 {
            @ModifyArg(method = "Lnet/minecraft/class_425;render(IIF)V", remap = false,
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V"), index = 4)
            private int adjustBackground(int color) { return Mod.getBackground(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2512 {
            @ModifyArg(method = "Lnet/minecraft/class_425;render(IIF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V"), index = 4)
            private int adjustBackground(int color) { return Mod.getBackground(color); }
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
            @Inject(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V", at = @At("HEAD"))
            private void onRenderProgressBar(int minX, int minY, int maxX, int maxY, float progress, CallbackInfo ci) {
                Mod.endAnimProgress = progress;
            }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2210 {
            @Inject(method = "Lnet/minecraft/class_425;method_18103(IIIIFF)V", at = @At("HEAD"))
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
        public static abstract class a2529 {
            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESCRIPTOR, ordinal = 0), index = 5)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESCRIPTOR, ordinal = 1), index = 5)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "renderProgressBar",
                at = @At(value = "INVOKE", target = FILL_DESCRIPTOR, ordinal = 2), index = 5)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(SplashScreen.class)
        public static abstract class a2512b2529 {
            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V", remap = false,
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V", ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V", remap = false,
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V", ordinal = 1), index = 4)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V", remap = false,
                at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/SplashScreen;fill(IIIII)V", ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2210b2512 {
            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V", ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V", ordinal = 1), index = 4)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V", ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2210 {
            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIFF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V", ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIFF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V", ordinal = 1), index = 4)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "Lnet/minecraft/class_425;method_18103(IIIIFF)V",
                at = @At(value = "INVOKE", target = "Lnet/minecraft/class_425;fill(IIIII)V", ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }
    }

}
