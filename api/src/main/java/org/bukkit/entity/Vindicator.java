package org.bukkit.entity;

/**
 * Represents a Vindicator.
 */
public interface Vindicator extends Illager {
    // Paper start
    /**
     * Check if this Vindicator is set to Johnny mode.
     * <p>
     * When in Johnny mode the Vindicator will be hostile to any kind of mob, except
     * for evokers, ghasts, illusioners and other vindicators. It will even be hostile
     * to vexes. All mobs, except for endermites, phantoms, guardians, slimes and
     * magma cubes, will try to attack the vindicator in return.
     *
     * @return True if in Johnny mode
     */
    boolean isJohnny();

    /**
     * Set this Vindicator's Johnny mode.
     * <p>
     * When in Johnny mode the Vindicator will be hostile to any kind of mob, except
     * for evokers, ghasts, illusioners and other vindicators. It will even be hostile
     * to vexes. All mobs, except for endermites, phantoms, guardians, slimes and
     * magma cubes, will try to attack the vindicator in return.
     *
     * @param johnny True to enable Johnny mode
     */
    void setJohnny(boolean johnny);
    // Paper end
}
