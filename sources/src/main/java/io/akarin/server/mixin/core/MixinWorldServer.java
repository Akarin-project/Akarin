package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.akarin.api.internal.mixin.IMixinLockProvider;
import net.minecraft.server.WorldServer;

@Mixin(value = WorldServer.class, remap = false)
public abstract class MixinWorldServer implements IMixinLockProvider {
    @Redirect(method = "doTick", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PlayerChunkMap.flush()V"
    ))
    public void onFlush() {} // Migrated to main thread
    
    private final Object tickLock = new Object();

    @Override
    public Object lock() {
        return tickLock;
    }
}
