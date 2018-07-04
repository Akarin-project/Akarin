package io.akarin.server.mixin.cps;

import java.util.Set;

import org.bukkit.craftbukkit.CraftWorld;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import net.minecraft.server.Chunk;
import net.minecraft.server.WorldServer;

@Mixin(value = CraftWorld.class, remap = false)
public abstract class MixinCraftWorld {
    @Shadow @Final private WorldServer world;
    
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
        Chunk chunk = world.getChunkProviderServer().chunks.get(chunkHash);
        if (chunk != null) chunk.setShouldUnload(false);
        return true;
    }
}
