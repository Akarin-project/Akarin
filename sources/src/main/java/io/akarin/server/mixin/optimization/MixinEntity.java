package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.Material;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;

@Mixin(value = Entity.class, remap = false)
public abstract class MixinEntity {
    @Shadow public World world;
    @Shadow public abstract AxisAlignedBB getBoundingBox();
    
    @Shadow public boolean noclip;
    @Shadow public float R;
    @Shadow public double locX;
    @Shadow public double locZ;
    @Shadow public abstract boolean x(Entity entity);
    @Shadow public abstract boolean isVehicle();
    @Shadow public abstract void f(double d0, double d1, double d2);
    
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
    @Overwrite 
    public void collide(Entity entity) {
		if (entity.noclip || this.noclip || this.x(entity)) return; // NeonPaper - Test this earlier
        double d0 = entity.locX - this.locX;
        double d1 = entity.locZ - this.locZ;
        double d2 = MathHelper.a(d0, d1);

        if (d2 >= 0.009999999776482582D) {
            d2 = (double) MathHelper.sqrt(d2);
            d0 /= d2;
            d1 /= d2;
            double d3 = 1.0D / d2;

            if (d3 > 1.0D) {
                d3 = 1.0D;
            }

            d0 *= d3;
            d1 *= d3;
            d0 *= 0.05000000074505806D;
            d1 *= 0.05000000074505806D;
            d0 *= (double) (1.0F - this.R);
            d1 *= (double) (1.0F - this.R);
            if (!this.isVehicle()) {
                this.f(-d0, 0.0D, -d1);
            }

            if (!entity.isVehicle()) {
                entity.f(d0, 0.0D, d1);
            }
        }
    }
}