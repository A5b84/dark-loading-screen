package io.github.a5b84.darkloadingscreen.config.gui;

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
        double progress = (System.currentTimeMillis() - start) / duration;
        progress = Math.min(MathHelper.lerp(progress, .1, 1), 1);
        //      lerp pour compenser un bug vanilla (barre qui sort à gauche)
        //      Pas besoin de max(..., 0) parce que c'est forcément positif

        return (float) progress;
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
