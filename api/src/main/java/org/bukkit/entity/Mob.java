package org.bukkit.entity;

import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Mob. Mobs are living entities with simple AI.
 */
public interface Mob extends LivingEntity, Lootable {

    // Paper start
    /**
     * Enables access to control the pathing of an Entity
     * @return Pathfinding Manager for this entity
     */
    @NotNull
    com.destroystokyo.paper.entity.Pathfinder getPathfinder();

    /**
     * Check if this mob is exposed to daylight
     *
     * @return True if mob is exposed to daylight
     */
    boolean isInDaylight();
    // Paper end

    /**
     * Instructs this Mob to set the specified LivingEntity as its target.
     * <p>
     * Hostile creatures may attack their target, and friendly creatures may
     * follow their target.
     *
     * @param target New LivingEntity to target, or null to clear the target
     */
    public void setTarget(@Nullable LivingEntity target);

    /**
     * Gets the current target of this Mob
     *
     * @return Current target of this creature, or null if none exists
     */
    @Nullable
    public LivingEntity getTarget();
}
