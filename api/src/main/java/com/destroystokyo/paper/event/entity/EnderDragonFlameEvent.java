package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when an EnderDragon spawns an AreaEffectCloud by shooting flames
 */
public class EnderDragonFlameEvent extends EntityEvent implements Cancellable {
    @NotNull private final AreaEffectCloud areaEffectCloud;

    public EnderDragonFlameEvent(@NotNull EnderDragon enderDragon, @NotNull AreaEffectCloud areaEffectCloud) {
        super(enderDragon);
        this.areaEffectCloud = areaEffectCloud;
    }

    /**
     * The enderdragon involved in this event
     */
    @NotNull
    @Override
    public EnderDragon getEntity() {
        return (EnderDragon) super.getEntity();
    }

    /**
     * @return The area effect cloud spawned in this collision
     */
    @NotNull
    public AreaEffectCloud getAreaEffectCloud() {
        return areaEffectCloud;
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
