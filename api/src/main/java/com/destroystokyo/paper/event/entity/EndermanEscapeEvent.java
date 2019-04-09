package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

public class EndermanEscapeEvent extends EntityEvent implements Cancellable {
    @NotNull private final Reason reason;

    public EndermanEscapeEvent(@NotNull Enderman entity, @NotNull Reason reason) {
        super(entity);
        this.reason = reason;
    }

    @NotNull
    @Override
    public Enderman getEntity() {
        return (Enderman) super.getEntity();
    }

    /**
     * @return The reason the enderman is trying to escape
     */
    @NotNull
    public Reason getReason() {
        return reason;
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

    /**
     * Cancels the escape.
     *
     * If this escape normally would of resulted in damage avoidance such as indirect,
     * the enderman will now take damage.
     *
     * @param cancel true if you wish to cancel this event
     */
    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public enum Reason {
        /**
         * The enderman has stopped attacking and ran away
         */
        RUNAWAY,
        /**
         * The enderman has teleported away due to indirect damage (ranged)
         */
        INDIRECT,
        /**
         * The enderman has teleported away due to a critical hit
         */
        CRITICAL_HIT,
        /**
         * The enderman has teleported away due to the player staring at it during combat
         */
        STARE,
        /**
         * Specific case for CRITICAL_HIT where the enderman is taking rain damage
         */
        DROWN
    }
}
