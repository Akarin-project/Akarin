package com.destroystokyo.paper.event.player;

import org.bukkit.advancement.Advancement;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player is granted a criteria in an advancement.
 */
public class PlayerAdvancementCriterionGrantEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @NotNull private final Advancement advancement;
    @NotNull private final String criterion;
    private boolean cancel = false;

    public PlayerAdvancementCriterionGrantEvent(@NotNull Player who, @NotNull Advancement advancement, @NotNull String criterion) {
        super(who);
        this.advancement = advancement;
        this.criterion = criterion;
    }

    /**
     * Get the advancement which has been affected.
     *
     * @return affected advancement
     */
    @NotNull
    public Advancement getAdvancement() {
        return advancement;
    }

    /**
     * Get the criterion which has been granted.
     *
     * @return granted criterion
     */
    @NotNull
    public String getCriterion() {
        return criterion;
    }

    public boolean isCancelled() {
        return cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }
}
