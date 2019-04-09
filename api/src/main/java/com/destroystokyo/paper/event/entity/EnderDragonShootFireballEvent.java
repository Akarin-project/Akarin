package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.EnderDragon;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when an EnderDragon shoots a fireball
 */
public class EnderDragonShootFireballEvent extends EntityEvent implements Cancellable {
    @NotNull private final DragonFireball fireball;

    public EnderDragonShootFireballEvent(@NotNull EnderDragon entity, @NotNull DragonFireball fireball) {
        super(entity);
        this.fireball = fireball;
    }

    /**
     * The enderdragon shooting the fireball
     */
    @NotNull
    @Override
    public EnderDragon getEntity() {
        return (EnderDragon) super.getEntity();
    }

    /**
     * @return The fireball being shot
     */
    @NotNull
    public DragonFireball getFireball() {
        return fireball;
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
