package io.akarin.server.mixin.core;

import org.bukkit.craftbukkit.CraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(value = CraftServer.class, remap = false)
public class MixinCraftServer {
    @Overwrite
    public String getName() {
        return "Akarin";
    }
}
