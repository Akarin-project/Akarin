package io.akarin.server.mixin.core;

import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = CraftServer.class, remap = false)
public class MixinCraftServer {
    @Shadow @Final @Mutable private String serverName;
    private boolean needApplyServerName = true;
    
    @Overwrite
    public String getName() {
        // We cannot apply the name modification in <init> method,
        // cause the initializer will be added to the tail
        if (needApplyServerName) {
            serverName = "Akarin";
            needApplyServerName = false;
        }
        return serverName;
    }
}
