package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.stream.Stream; // CraftBukkit

public class RecipeRepair extends ShapelessRecipes { // CraftBukkit - added extends

    // CraftBukkit start - Delegate to new parent class
    public RecipeRepair(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.LEATHER_HELMET), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.LEATHER_HELMET)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        List<ItemStack> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = (ItemStack) list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().usesDurability()) {
                        return false;
                    }
                }
            }
        }

        return list.size() == 2;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        List<ItemStack> list = Lists.newArrayList();

        ItemStack itemstack;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            itemstack = inventorycrafting.getItem(i);
            if (!itemstack.isEmpty()) {
                list.add(itemstack);
                if (list.size() > 1) {
                    ItemStack itemstack1 = (ItemStack) list.get(0);

                    if (itemstack.getItem() != itemstack1.getItem() || itemstack1.getCount() != 1 || itemstack.getCount() != 1 || !itemstack1.getItem().usesDurability()) {
                        return ItemStack.a;
                    }
                }
            }
        }

        if (list.size() == 2) {
            ItemStack itemstack2 = (ItemStack) list.get(0);

            itemstack = (ItemStack) list.get(1);
            if (itemstack2.getItem() == itemstack.getItem() && itemstack2.getCount() == 1 && itemstack.getCount() == 1 && itemstack2.getItem().usesDurability()) {
                Item item = itemstack2.getItem();
                int j = item.getMaxDurability() - itemstack2.getDamage();
                int k = item.getMaxDurability() - itemstack.getDamage();
                int l = j + k + item.getMaxDurability() * 5 / 100;
                int i1 = item.getMaxDurability() - l;

                if (i1 < 0) {
                    i1 = 0;
                }

                ItemStack itemstack3 = new ItemStack(itemstack2.getItem());

                itemstack3.setDamage(i1);
                // CraftBukkit start - Construct a dummy repair recipe
                NonNullList<RecipeItemStack> ingredients = NonNullList.a();
                ingredients.add(new RecipeItemStack(Stream.of(new RecipeItemStack.StackProvider(itemstack2.cloneItemStack()))));
                ingredients.add(new RecipeItemStack(Stream.of(new RecipeItemStack.StackProvider(itemstack.cloneItemStack()))));
                ShapelessRecipes recipe = new ShapelessRecipes(new MinecraftKey("repairitem"), "", itemstack3.cloneItemStack(), ingredients);
                inventorycrafting.setCurrentRecipe(recipe);
                itemstack3 = org.bukkit.craftbukkit.event.CraftEventFactory.callPreCraftEvent(inventorycrafting, inventorycrafting.resultInventory, itemstack3, inventorycrafting.container.getBukkitView(), true);
                // CraftBukkit end
                return itemstack3;
            }
        }

        return ItemStack.a;
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.o;
    }
}
