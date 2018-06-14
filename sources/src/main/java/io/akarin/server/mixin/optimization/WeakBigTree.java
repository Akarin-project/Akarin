package io.akarin.server.mixin.optimization;

import java.util.Random;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.World;
import net.minecraft.server.WorldGenBigTree;

/**
 * Fixes MC-128547(https://bugs.mojang.com/browse/MC-128547)
 */
@Mixin(value = WorldGenBigTree.class, remap = false)
public abstract class WeakBigTree {
    @Shadow(aliases = "l") private World worldReference;
    
    @Inject(method = "generate", at = @At("RETURN"))
    private void clearWorldRef(World world, Random random, BlockPosition pos, CallbackInfoReturnable<?> info) {
        world = null; // Akarin - remove references to world objects to avoid memory leaks
    }
}
