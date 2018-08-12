package io.akarin.server.mixin.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import co.aikar.timings.Timing;
import io.akarin.api.internal.mixin.IMixinTimingHandler;

@Mixin(targets = "co.aikar.timings.TimingHandler", remap = false)
public abstract class MixinTimingHandler implements IMixinTimingHandler {
    @Shadow @Final String name;
    @Shadow private boolean enabled;
    @Shadow private AtomicLong start;
    @Shadow private AtomicInteger timingDepth;
    
    @Shadow abstract void addDiff(long diff);
    @Shadow public abstract Timing startTiming();
    
    @Overwrite
    public Timing startTimingIfSync() {
        startTiming();
        return (Timing) this;
    }
    
    @Overwrite
    public void stopTimingIfSync() {
        //if (Akari.isPrimaryThread(false)) {
            stopTiming(true); // Avoid twice thread check
        //}
    }
    
    @Overwrite
    public void stopTiming() {
        stopTiming(false);
    }
    
    public void stopTiming(long start) {
        if (enabled) addDiff(System.nanoTime() - start);
    }
    
    public void stopTiming(boolean alreadySync) {
        if (!enabled || timingDepth.decrementAndGet() != 0 || start.get() == 0) return;
        /*if (!alreadySync) {
            Thread curThread = Thread.currentThread();
            if (curThread != MinecraftServer.getServer().primaryThread) {
                if (false && !AkarinGlobalConfig.silentAsyncTimings) {
                    Bukkit.getLogger().log(Level.SEVERE, "stopTiming called async for " + name);
                    Thread.dumpStack();
                }
                start = 0;
                return;
            }
        }*/
        
        // Safety ensured
        long prev = start.getAndSet(0); // Akarin
        addDiff(System.nanoTime() - prev); // Akarin
    }
}
