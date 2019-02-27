package net.minecraft.server;

public class RecipeBookClone extends IRecipeComplex {

    public RecipeBookClone(MinecraftKey minecraftkey) {
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
                    if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                        if (!itemstack.isEmpty()) {
                            return false;
                        }

                        itemstack = itemstack1;
                    } else {
                        if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                            return false;
                        }

                        ++i;
                    }
                }
            }

            return !itemstack.isEmpty() && itemstack.hasTag() && i > 0;
        }
    }

    public ItemStack craftItem(IInventory iinventory) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < iinventory.getSize(); ++j) {
            ItemStack itemstack1 = iinventory.getItem(j);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() == Items.WRITTEN_BOOK) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.a;
                    }

                    itemstack = itemstack1;
                } else {
                    if (itemstack1.getItem() != Items.WRITABLE_BOOK) {
                        return ItemStack.a;
                    }

                    ++i;
                }
            }
        }

        if (!itemstack.isEmpty() && itemstack.hasTag() && i >= 1 && ItemWrittenBook.e(itemstack) < 2) {
            ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK, i);
            NBTTagCompound nbttagcompound = itemstack.getTag().clone();

            nbttagcompound.setInt("generation", ItemWrittenBook.e(itemstack) + 1);
            itemstack2.setTag(nbttagcompound);
            return itemstack2;
        } else {
            return ItemStack.a;
        }
    }

    public NonNullList<ItemStack> b(IInventory iinventory) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(iinventory.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = iinventory.getItem(i);

            if (itemstack.getItem().p()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().o()));
            } else if (itemstack.getItem() instanceof ItemWrittenBook) {
                ItemStack itemstack1 = itemstack.cloneItemStack();

                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    public RecipeSerializer<?> a() {
        return RecipeSerializers.d;
    }
}
