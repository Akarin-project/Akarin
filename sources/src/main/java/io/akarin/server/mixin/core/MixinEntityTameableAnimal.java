package io.akarin.server.mixin.core;

import java.util.UUID;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.google.common.base.Optional;

import net.minecraft.server.DataWatcherObject;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityTameableAnimal;
import net.minecraft.server.World;

@Mixin(value = EntityTameableAnimal.class, remap = false)
public abstract class MixinEntityTameableAnimal extends Entity {
    @Shadow @Final protected static DataWatcherObject<Optional<UUID>> by;
    
    @Nullable private Optional<UUID> cachedOwnerId;
    
    @Overwrite
    @Nullable public UUID getOwnerUUID() {
        if (cachedOwnerId == null) cachedOwnerId = datawatcher.get(by);
        return cachedOwnerId.orNull();
    }
    
    @Overwrite
    public void setOwnerUUID(@Nullable UUID uuid) {
        cachedOwnerId = Optional.fromNullable(uuid);
        datawatcher.set(by, cachedOwnerId);
    }
    
    /**
     * Extends from superclass
     * @param world
     */
    public MixinEntityTameableAnimal(World world) {
        super(world);
    }
}
