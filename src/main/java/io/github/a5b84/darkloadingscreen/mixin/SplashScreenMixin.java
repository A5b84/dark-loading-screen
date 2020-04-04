package io.github.a5b84.darkloadingscreen.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.github.a5b84.darkloadingscreen.Mod;
import net.minecraft.client.gui.screen.SplashScreen;

/**
 * Grosse classe pour contenir des sous-classes qui contiennent les mixins
 * @see SplashScreen
 */
public final class SplashScreenMixin {

    private SplashScreenMixin() {}



    /**
     * Fond
     * @see SplashScreen#render
     */
    public static class Bg {

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2512 {
            @ModifyArg(method = "render_a2512", at = @At(value = "INVOKE", target = "fill_a2512"), index = 4)
            private int adjustBackground(int color) { return Mod.getBackground(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2512 {
            @ModifyArg(method = "render_b2512", at = @At(value = "INVOKE", target = "fill_b2512"), index = 4)
            private int adjustBackground(int color) { return Mod.getBackground(color); }
        }
    }



    /**
     * Màj des variables communes
     * @see SplashScreen#renderProgressBar
     */
    public static final class OnRenderBar {

        private OnRenderBar() {}

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2210 {
            @Inject(method = "renderProgressBar_a2210", at = @At("HEAD"))
            private void onRenderProgressBar(int minX, int minY, int maxX, int maxY, float progress, CallbackInfo ci) {
                Mod.endAnimProgress = progress;
            }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2210 {
            @Inject(method = "renderProgressBar_b2210", at = @At("HEAD"))
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

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2512 {
            @ModifyArg(method = "renderProgressBar_a2210", at = @At(value = "INVOKE", target = "fill_a2512", ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "renderProgressBar_a2210", at = @At(value = "INVOKE", target = "fill_a2512", ordinal = 1), index = 4)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "renderProgressBar_a2210", at = @At(value = "INVOKE", target = "fill_a2512", ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class a2210b2512 {
            @ModifyArg(method = "renderProgressBar_a2210", at = @At(value = "INVOKE", target = "fill_b2512", ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "renderProgressBar_a2210", at = @At(value = "INVOKE", target = "fill_b2512", ordinal = 1), index = 4)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "renderProgressBar_a2210", at = @At(value = "INVOKE", target = "fill_b2512", ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }

        @Mixin(value = SplashScreen.class, remap = false)
        public static abstract class b2210 {
            @ModifyArg(method = "renderProgressBar_b2210", at = @At(value = "INVOKE", target = "fill_b2512", ordinal = 0), index = 4)
            private int adjustBarBorder(int color) { return Mod.getBarBorder(color); }

            @ModifyArg(method = "renderProgressBar_b2210", at = @At(value = "INVOKE", target = "fill_b2512", ordinal = 1), index = 4)
            private int adjustBarBackground(int color) { return Mod.getBarBackground(color); }

            @ModifyArg(method = "renderProgressBar_b2210", at = @At(value = "INVOKE", target = "fill_b2512", ordinal = 2), index = 4)
            private int adjustBarColor(int color) { return Mod.getBarColor(color); }
        }
    }

}
