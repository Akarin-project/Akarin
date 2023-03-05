package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.Material;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;

@Mixin(value = Entity.class, remap = false)
public abstract class MixinEntity {
    @Shadow public World world;
    @Shadow public abstract AxisAlignedBB getBoundingBox();
    
    private boolean isInLava;
    private int lastLavaCheck = Integer.MIN_VALUE;
    
    @Overwrite // OBFHELPER: isInLava
    public boolean au() {
        /*
         * This originally comes from Migot (https://github.com/Poweruser/Migot/commit/cafbf1707107d2a3aa6232879f305975bb1f0285)
         * Thanks @Poweruser
         */
        int currentTick = MinecraftServer.currentTick;
        if (this.lastLavaCheck != currentTick) {
            this.lastLavaCheck = currentTick;
            this.isInLava = this.world.a(this.getBoundingBox().grow(-0.10000000149011612D, -0.4000000059604645D, -0.10000000149011612D), Material.LAVA);
        }
        return this.isInLava;
    }
}