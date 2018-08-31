package io.akarin.server.mixin.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import co.aikar.timings.Timing;

@Mixin(targets = "co.aikar.timings.TimingHandler", remap = false)
public abstract class MixinTimingHandler {
    @Shadow @Final String name;
    @Shadow private boolean enabled;
    @Shadow private AtomicLong start;
    @Shadow private AtomicInteger timingDepth;
    
    @Shadow abstract void addDiff(long diff);
    @Shadow public abstract Timing startTiming();
    
    @Overwrite
    public void stopTiming() {
        stopTiming(false);
    }
    
    public void stopTiming(boolean alreadySync) {
        if (!enabled || start.get() == 0 || timingDepth.decrementAndGet() != 0) return;
        long prev = start.getAndSet(0); // Akarin
        addDiff(System.nanoTime() - prev); // Akarin
    }
}
