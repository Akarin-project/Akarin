package net.minecraft.server;

import java.util.Collection;

public class RecipeTippedArrow extends ShapedRecipes { // CraftBukkit

    // CraftBukkit start
    public RecipeTippedArrow(MinecraftKey minecraftkey) {
        super(minecraftkey, "", 3, 3, NonNullList.a(RecipeItemStack.a,
                RecipeItemStack.a(Items.ARROW), RecipeItemStack.a(Items.ARROW), RecipeItemStack.a(Items.ARROW),
                RecipeItemStack.a(Items.ARROW), RecipeItemStack.a(Items.LINGERING_POTION), RecipeItemStack.a(Items.ARROW),
                RecipeItemStack.a(Items.ARROW), RecipeItemStack.a(Items.ARROW), RecipeItemStack.a(Items.ARROW)),
                new ItemStack(Items.TIPPED_ARROW, 8));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        if (inventorycrafting.g() == 3 && inventorycrafting.f() == 3) {
            for (int i = 0; i < inventorycrafting.g(); ++i) {
                for (int j = 0; j < inventorycrafting.f(); ++j) {
                    ItemStack itemstack = inventorycrafting.getItem(i + j * inventorycrafting.g());

                    if (itemstack.isEmpty()) {
                        return false;
                    }

                    Item item = itemstack.getItem();

                    if (i == 1 && j == 1) {
                        if (item != Items.LINGERING_POTION) {
                            return false;
                        }
                    } else if (item != Items.ARROW) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = inventorycrafting.getItem(1 + inventorycrafting.g());

        if (itemstack.getItem() != Items.LINGERING_POTION) {
            return ItemStack.a;
        } else {
            ItemStack itemstack1 = new ItemStack(Items.TIPPED_ARROW, 8);

            PotionUtil.a(itemstack1, PotionUtil.d(itemstack));
            PotionUtil.a(itemstack1, (Collection) PotionUtil.b(itemstack));
            return itemstack1;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.j;
    }
}
