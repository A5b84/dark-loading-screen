package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.resource.ResourceReload;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.CompletableFuture;

/** {@link ResourceReload} that automatically completes after some time */
public class FakeResourceReload implements ResourceReload {

    protected final long start;
    protected final long duration;

    public FakeResourceReload(long durationMs) {
        start = Util.getMeasuringTimeMs();
        duration = durationMs;
    }

    @Override
    public CompletableFuture<Unit> whenComplete() {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getProgress() {
        return MathHelper.clamp(
                (float) (Util.getMeasuringTimeMs() - start) / duration, 0, 1
        );
    }

    @Override
    public boolean isComplete() {
        return Util.getMeasuringTimeMs() - start >= duration;
    }

    @Override
    public void throwException() {}

}
