package org.bukkit.entity;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Paper start
import java.util.UUID;
// Paper end

/**
 * Represents a dropped item.
 */
public interface Item extends Entity {

    /**
     * Gets the item stack associated with this item drop.
     *
     * @return An item stack.
     */
    @NotNull
    public ItemStack getItemStack();

    /**
     * Sets the item stack associated with this item drop.
     *
     * @param stack An item stack.
     */
    public void setItemStack(@Nullable ItemStack stack);

    /**
     * Gets the delay before this Item is available to be picked up by players
     *
     * @return Remaining delay
     */
    public int getPickupDelay();

    /**
     * Sets the delay before this Item is available to be picked up by players
     *
     * @param delay New delay
     */
    public void setPickupDelay(int delay);

    // Paper Start
    /**
     * Gets if non-player entities can pick this Item up
     *
     * @return True if non-player entities can pickup
     */
     public boolean canMobPickup();

    /**
     * Sets if non-player entities can pick this Item up
     *
     * @param canMobPickup True to allow non-player entity pickup
     */
    public void setCanMobPickup(boolean canMobPickup);

    /**
     * The owner of this item. Only the owner can pick up the item until it is within 10 seconds of despawning
     *
     * @return The owner's UUID
     */
    @Nullable
    public UUID getOwner();

    /**
     * Set the owner of this item. Only the owner can pick up the item until it is within 10 seconds of despawning
     *
     * @param owner The owner's UUID
     */
    public void setOwner(@Nullable UUID owner);

    /**
     * Get the thrower of this item.
     *
     * @return The thrower's UUID
     */
    @Nullable
    public UUID getThrower();

    /**
     * Set the thrower of this item.
     *
     * @param thrower The thrower's UUID
     */
    public void setThrower(@Nullable UUID thrower);
    // Paper end
}
