package com.destroystokyo.paper.event.entity;

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LightningStrike;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityTransformEvent;

import java.util.Collections;
import org.jetbrains.annotations.NotNull;

/**
 *  Fired when lightning strikes an entity
 */
public class EntityZapEvent extends EntityTransformEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    @NotNull private final LightningStrike bolt;

    public EntityZapEvent(@NotNull final Entity entity, @NotNull final LightningStrike bolt, @NotNull final Entity replacementEntity) {
        super(entity, Collections.singletonList(replacementEntity), TransformReason.LIGHTNING);
        Validate.notNull(bolt);
        Validate.notNull(replacementEntity);
        this.bolt = bolt;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    /**
     * Gets the lightning bolt that is striking the entity.
     * @return The lightning bolt responsible for this event
     */
    @NotNull
    public LightningStrike getBolt() {
        return bolt;
    }

    /**
     * Gets the entity that will replace the struck entity.
     * @return The entity that will replace the struck entity
     */
    @NotNull
    public Entity getReplacementEntity() {
        return getTransformedEntity();
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
