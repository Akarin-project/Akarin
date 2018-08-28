package io.akarin.server.mixin.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import co.aikar.timings.Timing;
import io.akarin.api.internal.Akari;
import io.akarin.api.internal.Akari.AssignableThread;
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
    
    @Overwrite // Overwrite to avoid twice thread check
    public Timing startTimingIfSync() {
        startTiming();
        return (Timing) this;
    }
    
    @SuppressWarnings({"rawtypes", "unchecked"})
    @Inject(method = "startTiming", at = @At("HEAD"), cancellable = true)
    public void onStartTiming(CallbackInfoReturnable ci) {
        if (!Akari.isPrimaryThread(false)) ci.setReturnValue(this); // Avoid modify any field
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
        if (!enabled) return;
        if (!alreadySync) {
            Thread curThread = Thread.currentThread();
            if (curThread.getClass() == AssignableThread.class) return;
            if (curThread != MinecraftServer.getServer().primaryThread) {
                if (AkarinGlobalConfig.silentAsyncTimings) return;
                Bukkit.getLogger().log(Level.SEVERE, "stopTiming called async for " + name);
                Thread.dumpStack();
            }
        }
        
        // Main thread ensured
        if (--timingDepth == 0 && start != 0) {
            addDiff(System.nanoTime() - start);
            start = 0;
        }
    }
}
