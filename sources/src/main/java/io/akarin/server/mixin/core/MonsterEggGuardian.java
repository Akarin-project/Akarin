package io.akarin.server.mixin.core;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
import net.minecraft.server.ItemMonsterEgg;

@Mixin(value = ItemMonsterEgg.class, remap = false)
public abstract class MonsterEggGuardian {
    @Redirect(method = "a", at = @At(
            value = "FIELD",
            target = "net/minecraft/server/Blocks.MOB_SPAWNER:Lnet/minecraft/server/Block;",
            opcode = Opcodes.GETSTATIC
    ))
    private boolean configurable(Block target) {
        return target == Blocks.MOB_SPAWNER && AkarinGlobalConfig.allowSpawnerModify;
    }
}
