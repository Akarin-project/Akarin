package io.akarin.server.mixin.core;

import org.bukkit.craftbukkit.CraftServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CraftServer.class, remap = false)
public class MixinCraftServer {
    @Shadow @Mutable @Final private final String serverName = "Akarin"; // Paper -> Akarin
}
