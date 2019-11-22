package net.minecraft.server;

public class RecipeBookClone extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeBookClone(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.WRITTEN_BOOK), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.WRITABLE_BOOK)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

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

    public ItemStack a(InventoryCrafting inventorycrafting) {
        int i = 0;
        ItemStack itemstack = ItemStack.a;

        for (int j = 0; j < inventorycrafting.getSize(); ++j) {
            ItemStack itemstack1 = inventorycrafting.getItem(j);

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

    public NonNullList<ItemStack> b(InventoryCrafting inventorycrafting) {
        NonNullList<ItemStack> nonnulllist = NonNullList.a(inventorycrafting.getSize(), ItemStack.a);

        for (int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (itemstack.getItem().o()) {
                nonnulllist.set(i, new ItemStack(itemstack.getItem().n()));
            } else if (itemstack.getItem() instanceof ItemWrittenBook) {
                ItemStack itemstack1 = itemstack.cloneItemStack();

                itemstack1.setCount(1);
                nonnulllist.set(i, itemstack1);
                break;
            }
        }

        return nonnulllist;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.d;
    }
}
