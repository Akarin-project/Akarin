package com.destroystokyo.paper.event.player;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired after a player has respawned
 */
public class PlayerPostRespawnEvent extends PlayerEvent {
    private final static HandlerList handlers = new HandlerList();
    private final Location respawnedLocation;
    private final boolean isBedSpawn;

    public PlayerPostRespawnEvent(@NotNull final Player respawnPlayer, @NotNull final Location respawnedLocation, final boolean isBedSpawn) {
        super(respawnPlayer);
        this.respawnedLocation = respawnedLocation;
        this.isBedSpawn = isBedSpawn;
    }

    /**
     * Returns the location of the respawned player
     *
     * @return location of the respawned player
     */
    @NotNull
    public Location getRespawnedLocation() {
        return respawnedLocation.clone();
    }

    /**
     * Checks if the player respawned to their bed
     *
     * @return whether the player respawned to their bed
     */
    public boolean isBedSpawn() {
        return isBedSpawn;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
