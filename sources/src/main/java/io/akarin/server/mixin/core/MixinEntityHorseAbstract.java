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
import net.minecraft.server.EntityHorseAbstract;
import net.minecraft.server.World;

@Mixin(value = EntityHorseAbstract.class, remap = false)
public abstract class MixinEntityHorseAbstract extends Entity {
    @Shadow @Final private static DataWatcherObject<Optional<UUID>> bJ;
    
    @Nullable private Optional<UUID> cachedOwnerId;
    
    @Overwrite
    @Nullable public UUID getOwnerUUID() {
        if (cachedOwnerId == null) cachedOwnerId = datawatcher.get(bJ);
        return cachedOwnerId.orNull();
    }
    
    @Overwrite
    public void setOwnerUUID(@Nullable UUID uuid) {
        cachedOwnerId = Optional.fromNullable(uuid);
        datawatcher.set(bJ, cachedOwnerId);
    }
    
    /**
     * Extends from superclass
     * @param world
     */
    public MixinEntityHorseAbstract(World world) {
        super(world);
    }
}
