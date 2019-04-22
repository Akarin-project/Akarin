package org.bukkit.event.hanging;

import org.bukkit.World;
import org.bukkit.entity.Hanging;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import io.akarin.server.api.event.WorldAttachedEvent;

/**
 * Represents a hanging entity-related event.
 */
public abstract class HangingEvent extends Event implements WorldAttachedEvent { // Akarin
    protected Hanging hanging;
    @Override @NotNull public World getWorld() { return hanging.getWorld(); } // Akarin

    protected HangingEvent(@NotNull final Hanging painting) {
        this.hanging = painting;
    }

    /**
     * Gets the hanging entity involved in this event.
     *
     * @return the hanging entity
     */
    @NotNull
    public Hanging getEntity() {
        return hanging;
    }
}
