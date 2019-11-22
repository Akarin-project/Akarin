package net.minecraft.server;

public class RecipeBannerDuplicate extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeBannerDuplicate(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.WHITE_BANNER), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.WHITE_BANNER)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        EnumColor enumcolor = null;
        ItemStack itemstack = null;
        ItemStack itemstack1 = null;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack2 = inventorycrafting.getItem(i);
            Item item = itemstack2.getItem();

            if (item instanceof ItemBanner) {
                ItemBanner itembanner = (ItemBanner) item;

                if (enumcolor == null) {
                    enumcolor = itembanner.b();
                } else if (enumcolor != itembanner.b()) {
                    return false;
                }

                int j = TileEntityBanner.a(itemstack2);

                if (j > 6) {
                    return false;
                }

                if (j > 0) {
                    if (itemstack != null) {
                        return false;
                    }

                    itemstack = itemstack2;
                } else {
                    if (itemstack1 != null) {
                        return false;
                    }

                    itemstack1 = itemstack2;
                }
            }
        }

        return itemstack != null && itemstack1 != null;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                int j = TileEntityBanner.a(itemstack);

                if (j > 0 && j <= 6) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    return itemstack1;
                }
            }
        }

        return ItemStack.a;
    }

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().o()) {
                    nonnulllist.set(i, new ItemStack(itemstack.getItem().n()));
                } else if (itemstack.hasTag() && TileEntityBanner.a(itemstack) > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    nonnulllist.set(i, itemstack1);
                }
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.k;
    }
}
