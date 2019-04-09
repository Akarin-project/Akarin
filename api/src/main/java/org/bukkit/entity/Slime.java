package org.bukkit.entity;

/**
 * Represents a Slime.
 */
public interface Slime extends Mob {

    /**
     * @return The size of the slime
     */
    public int getSize();

    /**
     * @param sz The new size of the slime.
     */
    public void setSize(int sz);

    // Paper start
    /**
     * Get whether this slime can randomly wander/jump around on its own
     *
     * @return true if can wander
     */
    public boolean canWander();

    /**
     * Set whether this slime can randomly wander/jump around on its own
     *
     * @param canWander true if can wander
     */
    public void setWander(boolean canWander);
    // Paper end
}
