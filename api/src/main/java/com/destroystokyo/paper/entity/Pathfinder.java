package com.destroystokyo.paper.entity;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Handles pathfinding operations for an Entity
 */
public interface Pathfinder {

    /**
     *
     * @return The entity that is controlled by this pathfinder
     */
    @NotNull
    Mob getEntity();

    /**
     * Instructs the Entity to stop trying to navigate to its current desired location
     */
    void stopPathfinding();

    /**
     * If the entity is currently trying to navigate to a destination, this will return true
     * @return true if the entity is navigating to a destination
     */
    boolean hasPath();

    /**
     * @return The location the entity is trying to navigate to, or null if there is no destination
     */
    @Nullable
    PathResult getCurrentPath();

    /**
     * Calculates a destination for the Entity to navigate to, but does not set it
     * as the current target. Useful for calculating what would happen before setting it.
     * @param loc Location to navigate to
     * @return The closest Location the Entity can get to for this navigation, or null if no path could be calculated
     */
    @Nullable PathResult findPath(@NotNull Location loc);

    /**
     * Calculates a destination for the Entity to navigate to to reach the target entity,
     * but does not set it as the current target.
     * Useful for calculating what would happen before setting it.
     *
     * The behavior of this PathResult is subject to the games pathfinding rules, and may
     * result in the pathfinding automatically updating to follow the target Entity.
     *
     * However, this behavior is not guaranteed, and is subject to the games behavior.
     *
     * @param target the Entity to navigate to
     * @return The closest Location the Entity can get to for this navigation, or null if no path could be calculated
     */
    @Nullable PathResult findPath(@NotNull LivingEntity target);

    /**
     * Calculates a destination for the Entity to navigate to, and sets it with default speed
     * as the current target.
     * @param loc Location to navigate to
     * @return If the pathfinding was successfully started
     */
    default boolean moveTo(@NotNull Location loc) {
        return moveTo(loc, 1);
    }

    /**
     * Calculates a destination for the Entity to navigate to, with desired speed
     * as the current target.
     * @param loc Location to navigate to
     * @param speed Speed multiplier to navigate at, where 1 is 'normal'
     * @return If the pathfinding was successfully started
     */
    default boolean moveTo(@NotNull Location loc, double speed) {
        PathResult path = findPath(loc);
        return path != null && moveTo(path, speed);
    }

    /**
     * Calculates a destination for the Entity to navigate to to reach the target entity,
     * and sets it with default speed.
     *
     * The behavior of this PathResult is subject to the games pathfinding rules, and may
     * result in the pathfinding automatically updating to follow the target Entity.
     *
     * However, this behavior is not guaranteed, and is subject to the games behavior.
     *
     * @param target the Entity to navigate to
     * @return If the pathfinding was successfully started
     */
    default boolean moveTo(@NotNull LivingEntity target) {
        return moveTo(target, 1);
    }

    /**
     * Calculates a destination for the Entity to navigate to to reach the target entity,
     * and sets it with specified speed.
     *
     * The behavior of this PathResult is subject to the games pathfinding rules, and may
     * result in the pathfinding automatically updating to follow the target Entity.
     *
     * However, this behavior is not guaranteed, and is subject to the games behavior.
     *
     * @param target the Entity to navigate to
     * @param speed Speed multiplier to navigate at, where 1 is 'normal'
     * @return If the pathfinding was successfully started
     */
    default boolean moveTo(@NotNull LivingEntity target, double speed) {
        PathResult path = findPath(target);
        return path != null && moveTo(path, speed);
    }

    /**
     * Takes the result of a previous pathfinding calculation and sets it
     * as the active pathfinding with default speed.
     *
     * @param path The Path to start following
     * @return If the pathfinding was successfully started
     */
    default boolean moveTo(@NotNull PathResult path) {
        return moveTo(path, 1);
    }

    /**
     * Takes the result of a previous pathfinding calculation and sets it
     * as the active pathfinding,
     *
     * @param path The Path to start following
     * @param speed Speed multiplier to navigate at, where 1 is 'normal'
     * @return If the pathfinding was successfully started
     */
    boolean moveTo(@NotNull PathResult path, double speed);

    /**
     * Represents the result of a pathfinding calculation
     */
    interface PathResult {

        /**
         * All currently calculated points to follow along the path to reach the destination location
         *
         * Will return points the entity has already moved past, see {@link #getNextPointIndex()}
         * @return List of points
         */
        @NotNull
        List<Location> getPoints();

        /**
         * @return Returns the index of the current point along the points returned in {@link #getPoints()} the entity
         * is trying to reach, or null if we are done with this pathfinding.
         */
        int getNextPointIndex();

        /**
         * @return The next location in the path points the entity is trying to reach, or null if there is no next point
         */
        @Nullable Location getNextPoint();

        /**
         * @return The closest point the path can get to the target location
         */
        @Nullable Location getFinalPoint();
    }
}
