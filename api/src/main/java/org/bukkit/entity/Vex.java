package org.bukkit.entity;

import org.jetbrains.annotations.Nullable;

/**
 * Represents a Vex.
 */
public interface Vex extends Monster {

    /**
     * Gets the charging state of this entity.
     *
     * When this entity is charging it will having a glowing red texture.
     *
     * @return charging state
     */
    boolean isCharging();

    /**
     * Sets the charging state of this entity.
     *
     * When this entity is charging it will having a glowing red texture.
     *
     * @param charging new state
     */
    void setCharging(boolean charging);

    // Paper start
    /**
     * Get the Mob that summoned this vex
     *
     * @return Mob that summoned this vex
     */
    @Nullable
    Mob getSummoner();

    /**
     * Set the summoner of this vex
     *
     * @param summoner New summoner
     */
    void setSummoner(@Nullable Mob summoner);
    // Paper end
}
