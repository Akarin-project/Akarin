package com.destroystokyo.paper.event.entity;


import com.google.common.base.Preconditions;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called before an entity is spawned into a world by a spawner.
 *
 * This only includes the spawner's location and not the full BlockState snapshot for performance reasons.
 * If you really need it you have to get the spawner yourself.
 */

public class PreSpawnerSpawnEvent extends PreCreatureSpawnEvent {
    @NotNull private final Location spawnerLocation;

    public PreSpawnerSpawnEvent(@NotNull Location location, @NotNull EntityType type, @NotNull Location spawnerLocation) {
        super(location, type, CreatureSpawnEvent.SpawnReason.SPAWNER);
        this.spawnerLocation = Preconditions.checkNotNull(spawnerLocation, "Spawner location may not be null");
    }

    @NotNull
    public Location getSpawnerLocation() {
        return spawnerLocation;
    }
}
