package org.bukkit.event.entity;

import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import io.akarin.server.api.event.WorldAttachedEvent;

/**
 * Represents an Entity-related event
 */
public abstract class EntityEvent extends Event implements WorldAttachedEvent { // Akarin
    protected Entity entity;
    @Override @NotNull public World getWorld() { return entity.getWorld(); } // Akarin

    public EntityEvent(@NotNull final Entity what) {
        entity = what;
    }

    /**
     * Returns the Entity involved in this event
     *
     * @return Entity who is involved in this event
     */
    @NotNull
    public Entity getEntity() {
        return entity;
    }

    /**
     * Gets the EntityType of the Entity involved in this event.
     *
     * @return EntityType of the Entity involved in this event
     */
    @NotNull
    public EntityType getEntityType() {
        return entity.getType();
    }
}
