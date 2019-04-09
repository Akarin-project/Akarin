package com.destroystokyo.paper.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Turtle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Turtle starts digging to lay eggs
 */
public class TurtleStartDiggingEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    @NotNull private final Location location;

    public TurtleStartDiggingEvent(@NotNull Turtle turtle, @NotNull Location location) {
        super(turtle);
        this.location = location;
    }

    /**
     * The turtle digging
     *
     * @return The turtle
     */
    @NotNull
    public Turtle getEntity() {
        return (Turtle) entity;
    }

    /**
     * Get the location where the turtle is digging
     *
     * @return Location where digging
     */
    @NotNull
    public Location getLocation() {
        return location;
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
