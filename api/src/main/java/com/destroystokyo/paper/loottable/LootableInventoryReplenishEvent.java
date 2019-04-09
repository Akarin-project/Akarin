package com.destroystokyo.paper.loottable;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class LootableInventoryReplenishEvent extends PlayerEvent implements Cancellable {
    @NotNull private final LootableInventory inventory;

    public LootableInventoryReplenishEvent(@NotNull Player player, @NotNull LootableInventory inventory) {
        super(player);
        this.inventory = inventory;
    }

    @NotNull
    public LootableInventory getInventory() {
        return inventory;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    public HandlerList getHandlers() {
        return handlers;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return handlers;
    }

    private boolean cancelled = false;

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
