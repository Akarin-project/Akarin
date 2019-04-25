package org.bukkit.event.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import io.akarin.server.api.event.PlayerAttachedEvent;

public class PrepareItemCraftEvent extends InventoryEvent implements PlayerAttachedEvent { // Akarin
    private static final HandlerList handlers = new HandlerList();
    private boolean repair;
    private CraftingInventory matrix;
    // Akarin start
    @NotNull
    @Override
    public Player getPlayer() { return (Player) transaction.getPlayer(); }
    // Akarin end

    public PrepareItemCraftEvent(@NotNull CraftingInventory what, @NotNull InventoryView view, boolean isRepair) {
        super(view);
        this.matrix = what;
        this.repair = isRepair;
    }

    /**
     * Get the recipe that has been formed. If this event was triggered by a
     * tool repair, this will be a temporary shapeless recipe representing the
     * repair.
     *
     * @return The recipe being crafted.
     */
    @Nullable
    public Recipe getRecipe() {
        return matrix.getRecipe();
    }

    /**
     * @return The crafting inventory on which the recipe was formed.
     */
    @NotNull
    @Override
    public CraftingInventory getInventory() {
        return matrix;
    }

    /**
     * Check if this event was triggered by a tool repair operation rather
     * than a crafting recipe.
     *
     * @return True if this is a repair.
     */
    public boolean isRepair() {
        return repair;
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
