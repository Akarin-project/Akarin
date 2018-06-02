package io.akarin.server.mixin.core;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.api.LogWrapper;

@Mixin(targets = "co.aikar.timings.TimingHandler", remap = false)
public class MixinTimingHandler {
    @Shadow @Final String name;
    
    @Shadow private long start = 0;
    @Shadow private int timingDepth = 0;
    @Shadow private boolean enabled;
    
    @Shadow void addDiff(long diff) {}
    
    @Overwrite
    public void stopTiming() {
        if (enabled && --timingDepth == 0 && start != 0) {
            // Thread.currentThread() is an expensive operation, trying to avoid it
            if (LogWrapper.silentTiming) { // It must be off-main thread now
                start = 0;
                return;
            } else {
                if (!Bukkit.isPrimaryThread()) {
                    Bukkit.getLogger().log(Level.SEVERE, "stopTiming called async for " + name);
                    new Throwable().printStackTrace();
                    start = 0;
                    return;
                }
            }
            addDiff(System.nanoTime() - start);
            start = 0;
        }
    }
}
