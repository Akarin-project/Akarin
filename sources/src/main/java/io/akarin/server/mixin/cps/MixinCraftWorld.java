package io.akarin.server.mixin.cps;

import java.util.Set;

import org.bukkit.craftbukkit.CraftWorld;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.server.Chunk;
import net.minecraft.server.PlayerChunk;

@Mixin(value = CraftWorld.class, remap = false)
public class MixinCraftWorld {
    @Inject(method = "processChunkGC()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/ChunkProviderServer.unload(Lnet/minecraft/server/Chunk;)V"
    ))
    public void cancelUnloading(Chunk chunk, CallbackInfo ci) {
        if (chunk.isUnloading()) ci.cancel();
    }
    
    @Redirect(method = "processChunkGC()V", at = @At(
            value = "INVOKE",
            target = "java/util/Set.contains(Ljava/lang/Object;)Z",
            opcode = Opcodes.INVOKEINTERFACE
    ))
    public boolean checkUnloading(Set<Long> set, Object chunkHash) {
        return false;
    }
    
    @Redirect(method = "regenerateChunk", at = @At(
            value = "INVOKE",
            target = "java/util/Set.remove(Ljava/lang/Object;)Z",
            opcode = Opcodes.INVOKEINTERFACE
    ))
    public boolean regenChunk(Set<Long> set, Object chunkHash) {
        return false;
    }
    
    @Inject(method = "processChunkGC()V", at = @At(
            value = "FIELD",
            target = "net/minecraft/server/PlayerChunk.chunk:Lnet/minecraft/server/Chunk;",
            opcode = Opcodes.PUTFIELD
    ))
    public void noUnload(PlayerChunk playerChunk, Chunk chunk, CallbackInfo ci) {
        chunk.setShouldUnload(false);
    }
}
