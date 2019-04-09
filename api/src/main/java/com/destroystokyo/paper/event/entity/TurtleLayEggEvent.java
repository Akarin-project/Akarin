package com.destroystokyo.paper.event.entity;

import org.bukkit.Location;
import org.bukkit.entity.Turtle;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Turtle lays eggs
 */
public class TurtleLayEggEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    @NotNull
    private final Location location;
    private int eggCount;

    public TurtleLayEggEvent(@NotNull Turtle turtle, @NotNull Location location, int eggCount) {
        super(turtle);
        this.location = location;
        this.eggCount = eggCount;
    }

    /**
     * The turtle laying the eggs
     *
     * @return The turtle
     */
    @NotNull
    public Turtle getEntity() {
        return (Turtle) entity;
    }

    /**
     * Get the location where the eggs are being laid
     *
     * @return Location of eggs
     */
    @NotNull
    public Location getLocation() {
        return location;
    }

    /**
     * Get the number of eggs being laid
     *
     * @return Number of eggs
     */
    public int getEggCount() {
        return eggCount;
    }

    /**
     * Set the number of eggs being laid
     *
     * @param eggCount Number of eggs
     */
    public void setEggCount(int eggCount) {
        if (eggCount < 1) {
            cancelled = true;
            return;
        }
        eggCount = Math.min(eggCount, 4);
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
