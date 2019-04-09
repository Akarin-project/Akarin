package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Slime decides to change it's facing direction.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to change direction.
 */
public class SlimeChangeDirectionEvent extends SlimePathfindEvent implements Cancellable {
    private float yaw;

    public SlimeChangeDirectionEvent(@NotNull Slime slime, float yaw) {
        super(slime);
        this.yaw = yaw;
    }

    /**
     * Get the new chosen yaw
     *
     * @return Chosen yaw
     */
    public float getNewYaw() {
        return yaw;
    }

    /**
     * Set the new chosen yaw
     *
     * @param yaw Chosen yaw
     */
    public void setNewYaw(float yaw) {
        this.yaw = yaw;
    }
}
