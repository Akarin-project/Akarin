package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.PlayerConnection;

@Mixin(value = PlayerConnection.class, remap = false)
public class MixinPlayerConnection {
    @Overwrite
    private long d() {
        return System.currentTimeMillis(); // nanoTime() / 1000000L
    }
}
