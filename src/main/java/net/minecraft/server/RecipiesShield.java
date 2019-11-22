package net.minecraft.server;

public class RecipiesShield extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipiesShield(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.SHIELD), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.WHITE_BANNER)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        ItemStack itemstack = ItemStack.a;
        ItemStack itemstack1 = ItemStack.a;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack2 = inventorycrafting.getItem(i);

            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemBanner) {
                    if (!itemstack1.isEmpty()) {
                        return false;
                    }

                    itemstack1 = itemstack2;
                } else {
                    if (itemstack2.getItem() != Items.SHIELD) {
                        return false;
                    }

                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    if (itemstack2.b("BlockEntityTag") != null) {
                        return false;
                    }

                    itemstack = itemstack2;
                }
            }
        }

        if (!itemstack.isEmpty() && !itemstack1.isEmpty()) {
            return true;
        } else {
            return false;
        }
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.a;
        ItemStack itemstack1 = ItemStack.a;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack2 = inventorycrafting.getItem(i);

            if (!itemstack2.isEmpty()) {
                if (itemstack2.getItem() instanceof ItemBanner) {
                    itemstack = itemstack2;
                } else if (itemstack2.getItem() == Items.SHIELD) {
                    itemstack1 = itemstack2.cloneItemStack();
                }
            }
        }

        if (itemstack1.isEmpty()) {
            return itemstack1;
        } else {
            NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");
            NBTTagCompound nbttagcompound1 = nbttagcompound == null ? new NBTTagCompound() : nbttagcompound.clone();

            nbttagcompound1.setInt("Base", ((ItemBanner) itemstack.getItem()).b().getColorIndex());
            itemstack1.a("BlockEntityTag", (NBTBase) nbttagcompound1);
            return itemstack1;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.l;
    }
}
