package io.github.a5b84.darkloadingscreen.config;

import java.util.concurrent.CompletableFuture;
import net.minecraft.Util;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;

/** {@link ReloadInstance} that automatically completes after some time */
public class FakeResourceReload implements ReloadInstance {

  protected final long start;
  protected final long duration;

  public FakeResourceReload(long durationMs) {
    start = Util.getMillis();
    duration = durationMs;
  }

  @Override
  public CompletableFuture<Unit> done() {
    throw new UnsupportedOperationException();
  }

  @Override
  public float getActualProgress() {
    return Mth.clamp((float) (Util.getMillis() - start) / duration, 0, 1);
  }

  @Override
  public boolean isDone() {
    return Util.getMillis() - start >= duration;
  }

  @Override
  public void checkExceptions() {}
}
