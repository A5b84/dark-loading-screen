package io.github.a5b84.darkloadingscreen.config;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

/** {@link ReloadInstance} that automatically completes after some time. */
public class FakeReloadInstance implements ReloadInstance {

  private final long start;
  private final long duration;
  private final CompletableFuture<Void> future;

  public FakeReloadInstance(long durationMs) {
    start = Util.getMillis();
    duration = durationMs;
    future =
        CompletableFuture.runAsync(
            () -> {}, CompletableFuture.delayedExecutor(durationMs, TimeUnit.MILLISECONDS));
  }

  @Override
  public CompletableFuture<Void> done() {
    return future;
  }

  @Override
  public float getActualProgress() {
    float progress = (float) (Util.getMillis() - start) / duration;
    return Mth.clamp(progress, 0, 1);
  }
}
