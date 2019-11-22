package net.minecraft.server;

public class RecipeMapClone extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class
    public RecipeMapClone(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.MAP), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.MAP)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return false;
                    }

                    ++i;
                }
            }
        }

        return !itemstack.isEmpty() && i > 0;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.FILLED_MAP) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.a;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.MAP) {
                        return ItemStack.a;
                    }

                    ++i;
                }
            }
        }

        if (!itemstack.isEmpty() && i >= 1) {
            ItemStack itemstack2 = itemstack.cloneItemStack();

            itemstack2.setCount(i + 1);
            return itemstack2;
        } else {
            return ItemStack.a;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.e;
    }
}
