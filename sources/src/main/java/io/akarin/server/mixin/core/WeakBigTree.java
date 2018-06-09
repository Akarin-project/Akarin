package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.World;
import net.minecraft.server.WorldGenBigTree;

/**
 * Fixes MC-128547(https://bugs.mojang.com/browse/MC-128547)
 */
@Mixin(value = WorldGenBigTree.class, remap = false)
public class WeakBigTree {
    @Shadow private World l;
    
    @Inject(method = "generate(Lnet/minecraft/server/World;Ljava/util/Random;Lnet/minecraft/server/BlockPosition;)Z", at = @At("RETURN"))
    private void clearWorldRef(CallbackInfo info) {
        l = null; // Akarin - remove references to world objects to avoid memory leaks
    }
}
