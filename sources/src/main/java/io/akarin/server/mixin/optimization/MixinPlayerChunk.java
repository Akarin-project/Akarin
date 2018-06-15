package io.akarin.server.mixin.optimization;

import java.util.List;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerChunk;

@Mixin(value = PlayerChunk.class, remap = false)
public abstract class MixinPlayerChunk {
    @Shadow @Final public List<EntityPlayer> c; // PAIL: players
    
    @Inject(method = "b()Z", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PacketPlayOutMapChunk.<init>(Lnet/minecraft/server/Chunk;I)V"
    ))
    private void beforeCreateChunkPacket(CallbackInfoReturnable<Boolean> cir) {
        if (c.isEmpty()) cir.setReturnValue(true); // Akarin - Fixes MC-120780
    }
}
