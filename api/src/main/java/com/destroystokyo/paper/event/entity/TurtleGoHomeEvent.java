package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Turtle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Turtle decides to go home
 */
public class TurtleGoHomeEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    public TurtleGoHomeEvent(@NotNull Turtle turtle) {
        super(turtle);
    }

    /**
     * The turtle going home
     *
     * @return The turtle
     */
    @NotNull
    public Turtle getEntity() {
        return (Turtle) entity;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
