package io.github.a5b84.darkloadingscreen.config;

import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;

import java.util.concurrent.CompletableFuture;

/** ResourceReloadMonitor qui se complète seul après une certaine durée */
public class FakeResourceReloadMonitor implements ResourceReloadMonitor {

    protected final long start;
    protected final long duration;

    public FakeResourceReloadMonitor(long durationMs) {
        start = System.currentTimeMillis();
        duration = durationMs;
    }

    @Override
    public CompletableFuture<Unit> whenComplete() {
        return null;
    }

    @Override
    public float getProgress() {
        return MathHelper.clamp(
                (float) (System.currentTimeMillis() - start) / duration, 0, 1
        );
    }

    @Override
    public boolean isPrepareStageComplete() {
        return isApplyStageComplete();
    }

    @Override
    public boolean isApplyStageComplete() {
        return System.currentTimeMillis() - start >= duration;
    }

    @Override
    public void throwExceptions() {}

}
