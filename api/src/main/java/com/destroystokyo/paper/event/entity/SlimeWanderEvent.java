package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Slime;
import org.bukkit.event.Cancellable;
import org.jetbrains.annotations.NotNull;

/**
 * Fired when a Slime decides to start wandering.
 * <p>
 * This event does not fire for the entity's actual movement. Only when it
 * is choosing to start moving.
 */
public class SlimeWanderEvent extends SlimePathfindEvent implements Cancellable {
    public SlimeWanderEvent(@NotNull Slime slime) {
        super(slime);
    }
}
