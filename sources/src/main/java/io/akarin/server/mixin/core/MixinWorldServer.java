package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.WorldServer;

@Mixin(value = WorldServer.class, remap = false)
public class MixinWorldServer {
    @Redirect(method = "doTick", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PlayerChunkMap.flush()V"
    ))
    public void onFlush() {} // Migrated to main thread
}
