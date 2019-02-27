package net.minecraft.server;

public class RecipeBannerDuplicate extends IRecipeComplex {

    public RecipeBannerDuplicate(MinecraftKey minecraftkey) {
        super(minecraftkey);
    }

    public boolean a(IInventory iinventory, World world) {
        if (!(iinventory instanceof InventoryCrafting)) {
            return false;
        } else {
            EnumColor enumcolor = null;
            ItemStack itemstack = null;
            ItemStack itemstack1 = null;

            for (int i = 0; i < iinventory.getSize(); ++i) {
                ItemStack itemstack2 = iinventory.getItem(i);
                Item item = itemstack2.getItem();

                if (item instanceof ItemBanner) {
                    ItemBanner itembanner = (ItemBanner) item;

                    if (enumcolor == null) {
                        enumcolor = itembanner.b();
                    } else if (enumcolor != itembanner.b()) {
                        return false;
                    }

                    boolean flag = TileEntityBanner.a(itemstack2) > 0;

                    if (flag) {
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
    }

    public ItemStack craftItem(IInventory iinventory) {
        for (int i = 0; i < iinventory.getSize(); ++i) {
            ItemStack itemstack = iinventory.getItem(i);

            if (!itemstack.isEmpty() && TileEntityBanner.a(itemstack) > 0) {
                ItemStack itemstack1 = itemstack.cloneItemStack();

                itemstack1.setCount(1);
                return itemstack1;
            }
        }

        return ItemStack.a;
    }

    public NonNullList<ItemStack> b(IInventory iinventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(iinventory.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = iinventory.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem().p()) {
                    nonnulllist.set(i, new ItemStack(itemstack.getItem().o()));
                } else if (itemstack.hasTag() && TileEntityBanner.a(itemstack) > 0) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();

                    itemstack1.setCount(1);
                    nonnulllist.set(i, itemstack1);
                }
            }
        }

        return nonnulllist;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.l;
    }
}
