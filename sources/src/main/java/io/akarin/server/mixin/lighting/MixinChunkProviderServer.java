package io.akarin.server.mixin.lighting;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.akarin.api.IMixinChunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.WorldServer;

@Mixin(value = ChunkProviderServer.class, remap = false, priority = 1001)
public class MixinChunkProviderServer {
    @Shadow @Final public WorldServer world;

    @Redirect(method = "unloadChunks", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/Chunk;isUnloading()Z"
    ))
    public boolean shouldUnload(IMixinChunk chunk) {
        if (chunk.getPendingLightUpdates().get() > 0 || this.world.getTime() - chunk.getLightUpdateTime() < 20) {
            return false;
        }
        return true;
    }
}
