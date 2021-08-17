package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashOverlay;

public class PreviewSplashOverlay extends SplashOverlay {

    private final Runnable onDone;

    public PreviewSplashOverlay(long durationMs, Runnable onDone) {
        super(
                MinecraftClient.getInstance(), new FakeResourceReloadMonitor(durationMs),
                optional -> {}, true
        );
        this.onDone = onDone;
    }

    public void onDone() {
        if (onDone != null) onDone.run();
    }

}
