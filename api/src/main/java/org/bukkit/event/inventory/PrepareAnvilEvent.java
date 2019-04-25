package org.bukkit.event.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.akarin.server.api.event.PlayerAttachedEvent;

/**
 * Called when an item is put in a slot for repair by an anvil.
 */
public class PrepareAnvilEvent extends InventoryEvent implements PlayerAttachedEvent { // Akarin

    private static final HandlerList handlers = new HandlerList();
    private ItemStack result;
    // Akarin start
    @NotNull
    @Override
    public Player getPlayer() { return (Player) transaction.getPlayer(); }
    // Akarin end

    public PrepareAnvilEvent(@NotNull InventoryView inventory, @Nullable ItemStack result) {
        super(inventory);
        this.result = result;
    }

    @NotNull
    @Override
    public AnvilInventory getInventory() {
        return (AnvilInventory) super.getInventory();
    }

    /**
     * Get result item, may be null.
     *
     * @return result item
     */
    @Nullable
    public ItemStack getResult() {
        return result;
    }

    public void setResult(@Nullable ItemStack result) {
        this.result = result;
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
