package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecipeFireworksStar extends ShapelessRecipes { // CraftBukkit - added extends

    private static final RecipeItemStack a = RecipeItemStack.a(Items.FIRE_CHARGE, Items.FEATHER, Items.GOLD_NUGGET, Items.SKELETON_SKULL, Items.WITHER_SKELETON_SKULL, Items.CREEPER_HEAD, Items.PLAYER_HEAD, Items.DRAGON_HEAD, Items.ZOMBIE_HEAD);
    private static final RecipeItemStack b = RecipeItemStack.a(Items.DIAMOND);
    private static final RecipeItemStack c = RecipeItemStack.a(Items.GLOWSTONE_DUST);
    private static final Map<Item, ItemFireworks.EffectType> d = (Map) SystemUtils.a(Maps.newHashMap(), (hashmap) -> { // CraftBukkit - decompile error
        hashmap.put(Items.FIRE_CHARGE, ItemFireworks.EffectType.LARGE_BALL);
        hashmap.put(Items.FEATHER, ItemFireworks.EffectType.BURST);
        hashmap.put(Items.GOLD_NUGGET, ItemFireworks.EffectType.STAR);
        hashmap.put(Items.SKELETON_SKULL, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.WITHER_SKELETON_SKULL, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.CREEPER_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.PLAYER_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.DRAGON_HEAD, ItemFireworks.EffectType.CREEPER);
        hashmap.put(Items.ZOMBIE_HEAD, ItemFireworks.EffectType.CREEPER);
    });
    private static final RecipeItemStack e = RecipeItemStack.a(Items.GUNPOWDER);

    // CraftBukkit start - Delegate to new parent class with bogus info
    public RecipeFireworksStar(MinecraftKey minecraftkey) {
        super(minecraftkey, "", new ItemStack(Items.FIREWORK_STAR), NonNullList.a(RecipeItemStack.a, RecipeItemStack.a(Items.GUNPOWDER)));
    }
    // CraftBukkit end

    public boolean a(InventoryCrafting inventorycrafting, World world) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack = inventorycrafting.getItem(i);

            if (!itemstack.isEmpty()) {
                if (RecipeFireworksStar.a.test(itemstack)) {
                    if (flag2) {
                        return false;
                    }

                    flag2 = true;
                } else if (RecipeFireworksStar.c.test(itemstack)) {
                    if (flag4) {
                        return false;
                    }

                    flag4 = true;
                } else if (RecipeFireworksStar.b.test(itemstack)) {
                    if (flag3) {
                        return false;
                    }

                    flag3 = true;
                } else if (RecipeFireworksStar.e.test(itemstack)) {
                    if (flag) {
                        return false;
                    }

                    flag = true;
                } else {
                    if (!(itemstack.getItem() instanceof ItemDye)) {
                        return false;
                    }

                    flag1 = true;
                }
            }
        }

        return flag && flag1;
    }

    public ItemStack a(InventoryCrafting inventorycrafting) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
        NBTTagCompound nbttagcompound = itemstack.a("Explosion");
        ItemFireworks.EffectType itemfireworks_effecttype = ItemFireworks.EffectType.SMALL_BALL;
        List<Integer> list = Lists.newArrayList();

        for (int i = 0; i < inventorycrafting.getSize(); ++i) {
            ItemStack itemstack1 = inventorycrafting.getItem(i);

            if (!itemstack1.isEmpty()) {
                if (RecipeFireworksStar.a.test(itemstack1)) {
                    itemfireworks_effecttype = (ItemFireworks.EffectType) RecipeFireworksStar.d.get(itemstack1.getItem());
                } else if (RecipeFireworksStar.c.test(itemstack1)) {
                    nbttagcompound.setBoolean("Flicker", true);
                } else if (RecipeFireworksStar.b.test(itemstack1)) {
                    nbttagcompound.setBoolean("Trail", true);
                } else if (itemstack1.getItem() instanceof ItemDye) {
                    list.add(((ItemDye) itemstack1.getItem()).d().f());
                }
            }
        }

        nbttagcompound.b("Colors", (List) list);
        nbttagcompound.setByte("Type", (byte) itemfireworks_effecttype.a());
        return itemstack;
    }

    @Override
    public ItemStack c() {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    @Override
    public RecipeSerializer<?> getRecipeSerializer() {
        return RecipeSerializer.h;
    }
}
