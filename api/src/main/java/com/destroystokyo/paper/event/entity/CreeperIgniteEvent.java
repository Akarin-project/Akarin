package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Creeper;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a Creeper is ignite flag is changed (armed/disarmed to explode).
 */
public class CreeperIgniteEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean canceled;
    private boolean ignited;

    public CreeperIgniteEvent(@NotNull Creeper creeper, boolean ignited) {
        super(creeper);
        this.ignited = ignited;
    }

    @NotNull
    @Override
    public Creeper getEntity() {
        return (Creeper) entity;
    }

    public boolean isIgnited() {
        return ignited;
    }

    public void setIgnited(boolean ignited) {
        this.ignited = ignited;
    }

    public boolean isCancelled() {
        return canceled;
    }

    public void setCancelled(boolean cancel) {
        canceled = cancel;
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
