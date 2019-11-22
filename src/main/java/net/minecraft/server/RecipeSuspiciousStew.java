package net.minecraft.server;

public class RecipeSuspiciousStew extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeSuspiciousStew(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.SUSPICIOUS_STEW), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.BOWL)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (itemstack.getItem() == Blocks.BROWN_MUSHROOM.getItem() && !flag2) {
                    flag2 = true;
                } else if (itemstack.getItem() == Blocks.RED_MUSHROOM.getItem() && !flag1) {
                    flag1 = true;
                } else if (itemstack.getItem().a(TagsItem.SMALL_FLOWERS) && !flag) {
                    flag = true;
                } else {
                    if (itemstack.getItem() != Items.BOWL || flag3) {
                        return false;
                    }

                    flag3 = true;
                }
            }
        }

        return flag && flag2 && flag1 && flag3;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = ItemStack.a;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty() && itemstack1.getItem().a(TagsItem.SMALL_FLOWERS)) {
                itemstack = itemstack1;
                break;
            }
        }

        ItemStack itemstack2 = new ItemStack(Items.SUSPICIOUS_STEW, 1);

        if (itemstack.getItem() instanceof ItemBlock && ((ItemBlock) itemstack.getItem()).getBlock() instanceof BlockFlowers) {
            BlockFlowers blockflowers = (BlockFlowers) ((ItemBlock) itemstack.getItem()).getBlock();
            MobEffectList mobeffectlist = blockflowers.d();

            ItemSuspiciousStew.a(itemstack2, mobeffectlist, blockflowers.e());
        }

        return itemstack2;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.n;
    }
}
