package net.minecraft.server;

public class RecipeMapClone extends IRecipeComplex {

    public RecipeMapClone(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            int i = 0;
            ItemStack itemstack = ItemStack.a;

            for (int j = 0; j < iinventory.getSize(); ++j) {
                ItemStack itemstack1 = iinventory.getItem(j);

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
    }

    public ItemStack craftItem(IInventory iinventory) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < iinventory.getSize(); ++j) {
            ItemStack itemstack1 = iinventory.getItem(j);

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

    public RecipeSerializer<?> a() {
        return RecipeSerializers.e;
    }
}
