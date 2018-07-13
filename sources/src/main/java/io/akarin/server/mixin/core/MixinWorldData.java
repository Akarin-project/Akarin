package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.api.internal.Akari;
import io.akarin.api.internal.mixin.IMixinWorldData;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.EnumDifficulty;
import net.minecraft.server.PacketPlayOutServerDifficulty;
import net.minecraft.server.WorldData;
import net.minecraft.server.WorldServer;

@Mixin(value = WorldData.class, remap = false)
public abstract class MixinWorldData implements IMixinWorldData {
    @Shadow(aliases = "C") private volatile EnumDifficulty difficulty;
    @Shadow public WorldServer world;
    
    @Shadow abstract public EnumDifficulty getDifficulty();
    @Shadow abstract public boolean isDifficultyLocked();

    @Override
    public void setDifficultyAsync(EnumDifficulty diff) {
        difficulty = diff;
        
        PacketPlayOutServerDifficulty packet = new PacketPlayOutServerDifficulty(this.getDifficulty(), this.isDifficultyLocked());
        for (EntityHuman player : world.players) {
            Akari.sendPacket(((EntityPlayer) player).playerConnection, packet);
        }
    }
}
