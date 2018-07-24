package io.akarin.server.mixin.core;

import java.util.List;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Entity;
import net.minecraft.server.World;

/**
 * Fixes MC-103516(https://bugs.mojang.com/browse/MC-103516)
 */
@Mixin(value = World.class, remap = false)
public abstract class MixinWorld {
    @Shadow public abstract List<Entity> getEntities(@Nullable Entity entity, AxisAlignedBB box);
    
    /**
     * Returns true if there are no solid, live entities in the specified AxisAlignedBB, excluding the given entity
     */
    @Overwrite
    public boolean a(AxisAlignedBB box, @Nullable Entity target) { // OBFHELPER: checkNoEntityCollision
        List<Entity> list = this.getEntities(null, box);
        
        for (Entity each : list) {
            if (!each.dead && each.i && each != target && (target == null || !each.x(target))) { // OBFHELPER: preventEntitySpawning - isRidingSameEntity
                return false;
            }
        }
        return true;
    }
}
