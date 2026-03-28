package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.LoadingOverlay;
import org.jspecify.annotations.Nullable;

public class PreviewSplashOverlay extends LoadingOverlay {

  private final @Nullable Runnable onRemoved;

  public PreviewSplashOverlay(long durationMs, @Nullable Runnable onRemoved) {
    super(Minecraft.getInstance(), new FakeReloadInstance(durationMs), _ -> {}, true);
    this.onRemoved = onRemoved;
  }

  public void onRemoved() {
    if (onRemoved != null) {
      onRemoved.run();
    }
  }
}
