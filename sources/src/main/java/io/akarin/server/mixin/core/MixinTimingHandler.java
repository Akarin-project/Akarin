package io.akarin.server.mixin.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import co.aikar.timings.Timing;
import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.MinecraftServer;

@Mixin(targets = "co.aikar.timings.TimingHandler", remap = false)
public abstract class MixinTimingHandler {
    @Shadow @Final String name;
    @Shadow private boolean enabled;
    @Shadow private long start;
    @Shadow private int timingDepth;
    
    @Shadow abstract void addDiff(long diff);
    @Shadow public abstract Timing startTiming();
    
    @Overwrite
    public Timing startTimingIfSync() {
        startTiming();
        return (Timing) this;
    }
    
    @Overwrite
    public void stopTimingIfSync() {
        if (Akari.isPrimaryThread(false)) {
            stopTiming(true); // Avoid twice thread check
        }
    }
    
    @Overwrite
    public void stopTiming() {
        stopTiming(false);
    }
    
    public void stopTiming(long start) {
        if (enabled) addDiff(System.nanoTime() - start);
    }
    
    public void stopTiming(boolean alreadySync) {
        if (!enabled || --timingDepth != 0 || start == 0) return;
        if (!alreadySync) {
            Thread curThread = Thread.currentThread();
            if (curThread != MinecraftServer.getServer().primaryThread) {
                start = 0;
                return;
            }
        }
        
        // Safety ensured
        addDiff(System.nanoTime() - start);
        start = 0;
    }
}
