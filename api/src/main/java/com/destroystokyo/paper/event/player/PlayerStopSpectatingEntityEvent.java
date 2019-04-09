package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when a player stops spectating an entity in spectator mode.
 */
public class PlayerStopSpectatingEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @NotNull private final Entity spectatorTarget;

    public PlayerStopSpectatingEntityEvent(@NotNull Player player, @NotNull Entity spectatorTarget) {
        super(player);
        this.spectatorTarget = spectatorTarget;
    }

    /**
     * Gets the entity that the player is spectating
     *
     * @return The entity the player is currently spectating (before they will stop).
     */
    @NotNull
    public Entity getSpectatorTarget() {
        return spectatorTarget;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
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
