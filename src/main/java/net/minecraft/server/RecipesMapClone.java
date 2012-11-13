package net.minecraft.server;

// CraftBukkit start
import java.util.List;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.inventory.CraftShapelessRecipe;
// CraftBukkit end

public class RecipesMapClone extends ShapelessRecipes implements IRecipe { // CraftBukkit - added extends

    // CraftBukkit start - delegate to new parent class
    public RecipesMapClone() {
        super(new ItemStack(Item.MAP, 0, -1), java.util.Arrays.asList(new ItemStack(Item.MAP_EMPTY, 0, 0)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (itemstack1 != null) {
                if (itemstack1.id == Item.MAP.id) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.id != Item.MAP_EMPTY.id) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return itemstack != null && i > 0;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = null;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (itemstack1 != null) {
                if (itemstack1.id == Item.MAP.id) {
                    if (itemstack != null) {
                        return null;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.id != Item.MAP_EMPTY.id) {
                        return null;
                    }

                    ++i;
                }
            }
        }

        if (itemstack != null && i >= 1) {
            ItemStack itemstack2 = new ItemStack(Item.MAP, i + 1, itemstack.getData());

            if (itemstack.s()) {
                itemstack2.c(itemstack.r());
            }

            return itemstack2;
        } else {
            return null;
        }
    }

    public int a() {
        return 9;
    }

    public ItemStack b() {
        return null;
    }
}
