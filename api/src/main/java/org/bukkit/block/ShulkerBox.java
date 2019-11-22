package org.bukkit.block;

import com.destroystokyo.paper.loottable.LootableBlockInventory;
import org.bukkit.DyeColor;
import org.bukkit.loot.Lootable;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a captured state of a ShulkerBox.
 */
public interface ShulkerBox extends Container, LootableBlockInventory { // Paper

    /**
     * Get the {@link DyeColor} corresponding to this ShulkerBox
     *
     * @return the {@link DyeColor} of this ShulkerBox
     */
    @NotNull
    public DyeColor getColor();
}
