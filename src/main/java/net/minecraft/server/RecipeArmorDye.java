package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;

public class RecipeArmorDye extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeArmorDye(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.LEATHER_HELMET), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.BONE_MEAL)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        ItemStack itemstack = ItemStack.a;
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (itemstack1.getItem() instanceof IDyeable) {
                    if (!itemstack.isEmpty()) {
                        return false;
                    }

                    itemstack = itemstack1;
                } else {
                    if (!(itemstack1.getItem() instanceof ItemDye)) {
                        return false;
                    }

                    list.add(itemstack1);
                }
            }
        }

        return !itemstack.isEmpty() && !list.isEmpty();
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        List<ItemDye> list = Lists.newArrayList();
        ItemStack itemstack = ItemStack.a;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                Item item = itemstack1.getItem();

                if (item instanceof IDyeable) {
                    if (!itemstack.isEmpty()) {
                        return ItemStack.a;
                    }

                    itemstack = itemstack1.cloneItemStack();
                } else {
                    if (!(item instanceof ItemDye)) {
                        return ItemStack.a;
                    }

                    list.add((ItemDye) item);
                }
            }
        }

        if (!itemstack.isEmpty() && !list.isEmpty()) {
            return IDyeable.a(itemstack, list);
        } else {
            return ItemStack.a;
        }
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.c;
    }
}
