package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.MinecraftServer;

@Mixin(value = MinecraftServer.class, remap = false)
public class MixinMinecraftServer {
    @Overwrite
    public String getServerModName() {
        return "Akarin";
    }
}
