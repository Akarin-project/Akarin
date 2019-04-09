package com.destroystokyo.paper.event.player;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a player shoots a projectile
 */
public class PlayerLaunchProjectileEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    @NotNull private final Projectile projectile;
    @NotNull private final ItemStack itemStack;
    private boolean consumeItem = true;
    private boolean cancelled;

    public PlayerLaunchProjectileEvent(@NotNull Player shooter, @NotNull ItemStack itemStack, @NotNull Projectile projectile) {
        super(shooter);
        this.itemStack = itemStack;
        this.projectile = projectile;
    }

    /**
     * Gets the projectile which will be launched by this event
     *
     * @return the launched projectile
     */
    @NotNull
    public Projectile getProjectile() {
        return projectile;
    }

    /**
     * Get the ItemStack used to fire the projectile
     *
     * @return The ItemStack used
     */
    @NotNull
    public ItemStack getItemStack() {
        return itemStack;
    }

    /**
     * Get whether to consume the ItemStack or not
     *
     * @return True to consume
     */
    public boolean shouldConsume() {
        return consumeItem;
    }

    /**
     * Set whether to consume the ItemStack or not
     *
     * @param consumeItem True to consume
     */
    public void setShouldConsume(boolean consumeItem) {
        this.consumeItem = consumeItem;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancel) {
        cancelled = cancel;
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
