package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Slime decides to start jumping while swimming in water/lava.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to start jumping.
 */
public class SlimeSwimEvent extends SlimeWanderEvent implements Cancellable {
    public SlimeSwimEvent(@NotNull Slime slime) {
        super(slime);
    }
}
