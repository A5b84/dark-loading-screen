package io.github.a5b84.darkloadingscreen.config;

import java.util.concurrent.CompletableFuture;

import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;

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
        double progress = Math.min(
            (System.currentTimeMillis() - start) / duration,
            1 // Pour pas que ça dépasse et que ça redescende
        );
        return (float) (.5 * (1 - Math.cos(Math.PI * progress))); // Easing
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
