package com.destroystokyo.paper.event.entity;

import org.bukkit.entity.Witch;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a witch consumes the potion in their hand to buff themselves.
 */
public class WitchConsumePotionEvent extends EntityEvent implements Cancellable {
    @Nullable private ItemStack potion;

    public WitchConsumePotionEvent(@NotNull Witch witch, @Nullable ItemStack potion) {
        super(witch);
        this.potion = potion;
    }

    @NotNull
    @Override
    public Witch getEntity() {
        return (Witch) super.getEntity();
    }

    /**
     * @return the potion the witch will consume and have the effects applied.
     */
    @Nullable
    public ItemStack getPotion() {
        return potion;
    }

    /**
     * Sets the potion to be consumed and applied to the witch.
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
