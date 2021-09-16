package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;

public class PreviewSplashOverlay extends SplashOverlay {

    private final Runnable onRemoved;

    public PreviewSplashOverlay(long durationMs, Runnable onRemoved) {
        super(
                MinecraftClient.getInstance(), new FakeResourceReload(durationMs),
                optional -> {}, true
        );
        this.onRemoved = onRemoved;
    }

    public void onRemoved() {
        if (onRemoved != null) onRemoved.run();
    }

}
