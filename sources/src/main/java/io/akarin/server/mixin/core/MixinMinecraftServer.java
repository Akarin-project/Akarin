package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MojangStatisticsGenerator;

@Mixin(value = MinecraftServer.class, remap = false)
public class MixinMinecraftServer {
    @Overwrite
    public String getServerModName() {
        return "Akarin";
    }
    
    /*
     * Forcely disable snooper
     */
    @Overwrite
    public void a(MojangStatisticsGenerator generator) {}
    
    @Overwrite
    public void b(MojangStatisticsGenerator generator) {}
}
