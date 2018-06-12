package io.akarin.server.mixin.optimization;

import java.util.List;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At.Shift;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PlayerChunk;

@Mixin(value = PlayerChunk.class, remap = false)
public class MixinPlayerChunk {
    @Shadow @Final public List<EntityPlayer> c; // PAIL: players
    
    @Inject(method = "b", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PacketPlayOutMapChunk.<init>(Lnet/minecraft/server/Chunk;I)V",
            opcode = Opcodes.INVOKESPECIAL,
            shift = Shift.BEFORE
    ))
    private void beforeCreateChunkPacket(CallbackInfo ci) {
        if (c.isEmpty()) ci.cancel(); // Akarin - Fixes MC-120780
    }
}
