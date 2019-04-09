package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Slime decides to change direction to target a LivingEntity.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to start moving.
 */
public class SlimeTargetLivingEntityEvent extends SlimePathfindEvent implements Cancellable {
    @NotNull private final LivingEntity target;

    public SlimeTargetLivingEntityEvent(@NotNull Slime slime, @NotNull LivingEntity target) {
        super(slime);
        this.target = target;
    }

    /**
     * Get the targeted entity
     *
     * @return Targeted entity
     */
    @NotNull
    public LivingEntity getTarget() {
        return target;
    }
}
