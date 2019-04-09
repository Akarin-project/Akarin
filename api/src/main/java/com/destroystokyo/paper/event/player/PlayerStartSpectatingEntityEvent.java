package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Triggered when a player starts spectating an entity in spectator mode.
 */
public class PlayerStartSpectatingEntityEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @NotNull private final Entity currentSpectatorTarget;
    @NotNull private final Entity newSpectatorTarget;

    public PlayerStartSpectatingEntityEvent(@NotNull Player player, @NotNull Entity currentSpectatorTarget, @NotNull Entity newSpectatorTarget) {
        super(player);
        this.currentSpectatorTarget = currentSpectatorTarget;
        this.newSpectatorTarget = newSpectatorTarget;
    }

    /**
     * Gets the entity that the player is currently spectating or themselves if they weren't spectating anything
     *
     * @return The entity the player is currently spectating (before they start spectating the new target).
     */
    @NotNull
    public Entity getCurrentSpectatorTarget() {
        return currentSpectatorTarget;
    }

    /**
     * Gets the new entity that the player will now be spectating
     *
     * @return The entity the player is now going to be spectating.
     */
    @NotNull
    public Entity getNewSpectatorTarget() {
        return newSpectatorTarget;
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

