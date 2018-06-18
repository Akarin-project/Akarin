package io.akarin.server.mixin.core;

import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;

@Mixin(value = AsyncCatcher.class, remap = false)
public abstract class MixinAsyncCatcher {
    @Shadow public static boolean enabled;
    
    @Overwrite
    public static void catchOp(String reason) {
        if (enabled) {
            if (Akari.isPrimaryThread()) return;
            
            if (AkarinGlobalConfig.throwOnAsyncCaught) {
                throw new IllegalStateException("Asynchronous " + reason + "!");
            } else {
                Akari.logger.warn("Asynchronous " + reason + "!");
                Thread.dumpStack();
            }
        }
    }
}
