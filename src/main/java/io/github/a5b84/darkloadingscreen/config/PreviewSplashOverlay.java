package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;

public class PreviewSplashOverlay extends LoadingOverlay {

  private final Runnable onRemoved;

  public PreviewSplashOverlay(long durationMs, Runnable onRemoved) {
    super(Minecraft.getInstance(), new FakeResourceReload(durationMs), optional -> {}, true);
    this.onRemoved = onRemoved;
  }

  public void onRemoved() {
    if (onRemoved != null) {
      onRemoved.run();
    }
  }
}
