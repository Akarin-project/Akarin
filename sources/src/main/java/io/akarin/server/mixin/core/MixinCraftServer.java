package io.akarin.server.mixin.core;

import org.bukkit.craftbukkit.CraftServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.MinecraftServer;

@Mixin(value = CraftServer.class, remap = false)
public abstract class MixinCraftServer {
    @Shadow @Final @Mutable private String serverName;
    @Shadow @Final protected MinecraftServer console;
    private boolean needApplyServerName = true;
    
    @Overwrite
    public String getName() {
        // We cannot apply the name modification in <init> method,
        // cause the initializer will be added to the tail
        if (needApplyServerName) {
            serverName = AkarinGlobalConfig.serverBrandName.equals(Akari.EMPTY_STRING) ? "Akarin" : AkarinGlobalConfig.serverBrandName;
            needApplyServerName = false;
        }
        return serverName;
    }
    
    @Overwrite
    public boolean isPrimaryThread() {
        return Akari.isPrimaryThread();
    }
}
