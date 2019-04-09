package com.destroystokyo.paper.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Called when a phantom is spawned for an exhausted player
 */
public class PhantomPreSpawnEvent extends PreCreatureSpawnEvent {
    @NotNull private final Entity entity;

    public PhantomPreSpawnEvent(@NotNull Location location, @NotNull Entity entity, @NotNull CreatureSpawnEvent.SpawnReason reason) {
        super(location, EntityType.PHANTOM, reason);
        this.entity = entity;
    }

    /**
     * Get the entity this phantom is spawning for
     *
     * @return Entity
     */
    @Nullable
    public Entity getSpawningEntity() {
        return entity;
    }
}
