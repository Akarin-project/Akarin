package org.bukkit.entity;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a turtle.
 */
public interface Turtle extends Animals {
    // Paper start

    /**
     * Get the turtle's home location
     *
     * @return Home location
     */
    @NotNull
    Location getHome();

    /**
     * Set the turtle's home location
     *
     * @param location Home location
     */
    void setHome(@NotNull Location location);

    /**
     * Check if turtle is currently pathfinding to it's home
     *
     * @return True if going home
     */
    boolean isGoingHome();

    /**
     * Get if turtle is digging to lay eggs
     *
     * @return True if digging
     */
    boolean isDigging();

    /**
     * Get if turtle is carrying egg
     *
     * @return True if carrying egg
     */
    boolean hasEgg();

    /**
     * Set if turtle is carrying egg
     *
     * @param hasEgg True if carrying egg
     */
    void setHasEgg(boolean hasEgg);
    // Paper end
}
