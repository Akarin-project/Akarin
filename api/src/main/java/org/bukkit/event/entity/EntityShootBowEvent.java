package org.bukkit.event.entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile; // Paper
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Called when a LivingEntity shoots a bow firing an arrow
 */
public class EntityShootBowEvent extends EntityEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private final ItemStack bow;
    private Entity projectile;
    private final float force;
    private boolean cancelled;
    // Paper start
    private boolean consumeArrow = true;
    private final ItemStack arrowItem;
    public void setConsumeArrow(boolean consumeArrow) {
        this.consumeArrow = consumeArrow;
    }
    public boolean getConsumeArrow() {
        return consumeArrow;
    }

    @NotNull
    public ItemStack getArrowItem() {
        return arrowItem;
    }

    @Deprecated
    public EntityShootBowEvent(@NotNull final LivingEntity shooter, @Nullable final ItemStack bow, @NotNull final Entity projectile, final float force) {
        this(shooter, bow, new ItemStack(org.bukkit.Material.AIR), projectile, force);
    }

    public EntityShootBowEvent(@NotNull final LivingEntity shooter, @Nullable final ItemStack bow, @NotNull ItemStack arrowItem, @NotNull final Entity projectile, final float force) {
        super(shooter);
        this.arrowItem = arrowItem;
        // Paper end
        this.bow = bow;
        this.projectile = projectile;
        this.force = force;
    }

    @NotNull
    @Override
    public LivingEntity getEntity() {
        return (LivingEntity) entity;
    }

    /**
     * Gets the bow ItemStack used to fire the arrow.
     *
     * @return the bow involved in this event
     */
    @Nullable
    public ItemStack getBow() {
        return bow;
    }

    /**
     * Gets the projectile which will be launched by this event
     *
     * @return the launched projectile
     */
    @NotNull
    public Entity getProjectile() {
        return projectile;
    }

    /**
     * Replaces the projectile which will be launched
     *
     * @param projectile the new projectile
     */
    public void setProjectile(@NotNull Entity projectile) {
        this.projectile = projectile;
    }

    /**
     * Gets the force the arrow was launched with
     *
     * @return bow shooting force, up to 1.0
     */
    public float getForce() {
        return force;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
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
