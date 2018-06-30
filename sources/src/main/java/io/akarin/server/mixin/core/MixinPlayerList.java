package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.PlayerList;

@Mixin(value = PlayerList.class, remap = false)
public abstract class MixinPlayerList {
    @Overwrite
    public void tick() {} // Migrated to slack service
}
