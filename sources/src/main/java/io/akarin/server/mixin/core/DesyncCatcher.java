package io.akarin.server.mixin.core;

import org.spigotmc.AsyncCatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = AsyncCatcher.class, remap = false)
public class DesyncCatcher {
    @Shadow public static boolean enabled;
    
    @Overwrite
    public static void catchOp(String reason) {
        ;
    }
}
