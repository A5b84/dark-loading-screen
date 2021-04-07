package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.SplashScreen;

public class PreviewSplashScreen extends SplashScreen {

    private final Runnable onDone;

    public PreviewSplashScreen(long durationMs, Runnable onDone) {
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
