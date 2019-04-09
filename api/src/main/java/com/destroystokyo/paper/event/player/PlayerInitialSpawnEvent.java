package com.destroystokyo.paper.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInitialSpawnEvent extends PlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    @NotNull private Location spawnLocation;

    public PlayerInitialSpawnEvent(@NotNull final Player player, @NotNull final Location spawnLocation) {
        super(player);
        this.spawnLocation = spawnLocation;
    }

    /**
     * Gets the current spawn location
     *
     * @return Location current spawn location
     */
    @NotNull
    public Location getSpawnLocation() {
        return this.spawnLocation;
    }

    /**
     * Sets the new spawn location
     *
     * @param spawnLocation new location for the spawn
     */
    public void setSpawnLocation(@NotNull Location spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
