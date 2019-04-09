package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Slime decides to start pathfinding.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to start moving.
 */
public class SlimePathfindEvent extends EntityEvent implements Cancellable {
    public SlimePathfindEvent(@NotNull Slime slime) {
        super(slime);
    }

    /**
     * The Slime that is pathfinding.
     *
     * @return The Slime that is pathfinding.
     */
    @NotNull
    public Slime getEntity() {
        return (Slime) entity;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
