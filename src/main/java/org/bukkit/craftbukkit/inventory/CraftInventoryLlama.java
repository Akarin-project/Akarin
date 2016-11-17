package org.bukkit.craftbukkit.inventory;

import net.minecraft.server.IInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.LlamaInventory;

public class CraftInventoryLlama extends CraftInventory implements LlamaInventory {

    public CraftInventoryLlama(IInventory inventory) {
        super(inventory);
    }

    @Override
    public ItemStack getDecor() {
        return getItem(1);
    }

    @Override
    public void setDecor(ItemStack stack) {
        setItem(1, stack);
    }
}
