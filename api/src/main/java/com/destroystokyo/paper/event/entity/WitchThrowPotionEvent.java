package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Witch;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a witch throws a potion at a player
 */
public class WitchThrowPotionEvent extends EntityEvent implements Cancellable {
    @NotNull private final LivingEntity target;
    @Nullable private ItemStack potion;

    public WitchThrowPotionEvent(@NotNull Witch witch, @NotNull LivingEntity target, @Nullable ItemStack potion) {
        super(witch);
        this.target = target;
        this.potion = potion;
    }

    @NotNull
    @Override
    public Witch getEntity() {
        return (Witch) super.getEntity();
    }

    /**
     * @return The target of the potion
     */
    @NotNull
    public LivingEntity getTarget() {
        return target;
    }

    /**
     * @return The potion the witch will throw at a player
     */
    @Nullable
    public ItemStack getPotion() {
        return potion;
    }

    /**
     * Sets the potion to be thrown at a player
     * @param potion The potion
     */
    public void setPotion(@Nullable ItemStack potion) {
        this.potion = potion != null ? potion.clone() : null;
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

    /**
     * @return Event was cancelled or potion was null
     */
    @Override
    public boolean isCancelled() {
        return cancelled || potion == null;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }
}
