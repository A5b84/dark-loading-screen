package io.github.a5b84.darkloadingscreen.config;

import java.util.concurrent.CompletableFuture;

import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;
import net.minecraft.util.math.MathHelper;

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
        double progress = MathHelper.clamp(
            (System.currentTimeMillis() - start) / duration,
            .1, 1 // min pour compenser un bug vanilla
            // max pour pas casser la fonction
        );

        return (float) (1 - Math.cos(Math.PI * progress)) / 2; // Easing
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
