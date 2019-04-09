package org.bukkit.entity;

/**
 * Represents a SkeletonHorse - variant of {@link AbstractHorse}.
 */
public interface SkeletonHorse extends AbstractHorse {
    // Paper start
    int getTrapTime();

    boolean isTrap();

    void setTrap(boolean trap);
    // Paper end
}
