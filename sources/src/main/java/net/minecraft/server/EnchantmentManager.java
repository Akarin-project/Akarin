package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

/**
 * Akarin Changes Note
 * 1) Expose private members (cause mixin errors)
 */
public class EnchantmentManager {

    private static final EnchantmentManager.EnchantmentModifierProtection a = new EnchantmentManager.EnchantmentModifierProtection(null);
    private static final EnchantmentManager.EnchantmentModifierDamage b = new EnchantmentManager.EnchantmentModifierDamage(null);
    private static final EnchantmentManager.EnchantmentModifierThorns c = new EnchantmentManager.EnchantmentModifierThorns(null);
    private static final EnchantmentManager.EnchantmentModifierArthropods d = new EnchantmentManager.EnchantmentModifierArthropods(null);

    public static int getEnchantmentLevel(Enchantment enchantment, ItemStack itemstack) {
        if (itemstack.isEmpty()) {
            return 0;
        } else {
            NBTTagList nbttaglist = itemstack.getEnchantments();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.get(i);
                Enchantment enchantment1 = Enchantment.c(nbttagcompound.getShort("id"));
                short short0 = nbttagcompound.getShort("lvl");

                if (enchantment1 == enchantment) {
                    return short0;
                }
            }

            return 0;
        }
    }

    public static Map<Enchantment, Integer> a(ItemStack itemstack) {
        LinkedHashMap linkedhashmap = Maps.newLinkedHashMap();
        NBTTagList nbttaglist = itemstack.getItem() == Items.ENCHANTED_BOOK ? ItemEnchantedBook.h(itemstack) : itemstack.getEnchantments();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.get(i);
            Enchantment enchantment = Enchantment.c(nbttagcompound.getShort("id"));
            short short0 = nbttagcompound.getShort("lvl");

            linkedhashmap.put(enchantment, Integer.valueOf(short0));
        }

        return linkedhashmap;
    }

    public static void a(Map<Enchantment, Integer> map, ItemStack itemstack) {
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            Enchantment enchantment = (Enchantment) entry.getKey();

            if (enchantment != null) {
                int i = ((Integer) entry.getValue()).intValue();
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setShort("id", (short) Enchantment.getId(enchantment));
                nbttagcompound.setShort("lvl", (short) i);
                nbttaglist.add(nbttagcompound);
                if (itemstack.getItem() == Items.ENCHANTED_BOOK) {
                    ItemEnchantedBook.a(itemstack, new WeightedRandomEnchant(enchantment, i));
                }
            }
        }

        if (nbttaglist.isEmpty()) {
            if (itemstack.hasTag()) {
                itemstack.getTag().remove("ench");
            }
        } else if (itemstack.getItem() != Items.ENCHANTED_BOOK) {
            itemstack.a("ench", nbttaglist);
        }

    }

    private static void a(EnchantmentManager.EnchantmentModifier enchantmentmanager_enchantmentmodifier, ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            NBTTagList nbttaglist = itemstack.getEnchantments();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                short short0 = nbttaglist.get(i).getShort("id");
                short short1 = nbttaglist.get(i).getShort("lvl");

                if (Enchantment.c(short0) != null) {
                    enchantmentmanager_enchantmentmodifier.a(Enchantment.c(short0), short1);
                }
            }

        }
    }

    private static void a(EnchantmentManager.EnchantmentModifier enchantmentmanager_enchantmentmodifier, Iterable<ItemStack> iterable) {
        Iterator iterator = iterable.iterator();

        while (iterator.hasNext()) {
            ItemStack itemstack = (ItemStack) iterator.next();

            a(enchantmentmanager_enchantmentmodifier, itemstack);
        }

    }

    public static int a(Iterable<ItemStack> iterable, DamageSource damagesource) {
        EnchantmentManager.a.a = 0;
        EnchantmentManager.a.b = damagesource;
        a(EnchantmentManager.a, iterable);
        return EnchantmentManager.a.a;
    }

    public static float a(ItemStack itemstack, EnumMonsterType enummonstertype) {
        EnchantmentManager.b.a = 0.0F;
        EnchantmentManager.b.b = enummonstertype;
        a(EnchantmentManager.b, itemstack);
        return EnchantmentManager.b.a;
    }

    public static float a(EntityLiving entityliving) {
        int i = a(Enchantments.r, entityliving);

        return i > 0 ? EnchantmentSweeping.e(i) : 0.0F;
    }

    public static void a(EntityLiving entityliving, Entity entity) {
        EnchantmentManager.c.b = entity;
        EnchantmentManager.c.a = entityliving;
        if (entityliving != null) {
            a(EnchantmentManager.c, entityliving.aQ());
        }

        if (entity instanceof EntityHuman) {
            a(EnchantmentManager.c, entityliving.getItemInMainHand());
        }

    }

    public static void b(EntityLiving entityliving, Entity entity) {
        EnchantmentManager.d.a = entityliving;
        EnchantmentManager.d.b = entity;
        if (entityliving != null) {
            a(EnchantmentManager.d, entityliving.aQ());
        }

        if (entityliving instanceof EntityHuman) {
            a(EnchantmentManager.d, entityliving.getItemInMainHand());
        }

    }

    public static int a(Enchantment enchantment, EntityLiving entityliving) {
        List list = enchantment.a(entityliving);

        if (list == null) {
            return 0;
        } else {
            int i = 0;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();
                int j = getEnchantmentLevel(enchantment, itemstack);

                if (j > i) {
                    i = j;
                }
            }

            return i;
        }
    }

    public static int b(EntityLiving entityliving) {
        return a(Enchantments.KNOCKBACK, entityliving);
    }

    public static int getFireAspectEnchantmentLevel(EntityLiving entityliving) {
        return a(Enchantments.FIRE_ASPECT, entityliving);
    }

    public static int getOxygenEnchantmentLevel(EntityLiving entityliving) {
        return a(Enchantments.OXYGEN, entityliving);
    }

    public static int e(EntityLiving entityliving) {
        return a(Enchantments.DEPTH_STRIDER, entityliving);
    }

    public static int getDigSpeedEnchantmentLevel(EntityLiving entityliving) {
        return a(Enchantments.DIG_SPEED, entityliving);
    }

    public static int b(ItemStack itemstack) {
        return getEnchantmentLevel(Enchantments.LUCK, itemstack);
    }

    public static int c(ItemStack itemstack) {
        return getEnchantmentLevel(Enchantments.LURE, itemstack);
    }

    public static int g(EntityLiving entityliving) {
        return a(Enchantments.LOOT_BONUS_MOBS, entityliving);
    }

    public static boolean h(EntityLiving entityliving) {
        return a(Enchantments.WATER_WORKER, entityliving) > 0;
    }

    public static boolean i(EntityLiving entityliving) {
        return a(Enchantments.j, entityliving) > 0;
    }

    public static boolean d(ItemStack itemstack) {
        return getEnchantmentLevel(Enchantments.k, itemstack) > 0;
    }

    public static boolean shouldNotDrop(ItemStack itemstack) {
        return getEnchantmentLevel(Enchantments.D, itemstack) > 0;
    }

    public static ItemStack getRandomEquippedItemWithEnchant(Enchantment enchantment, EntityLiving entityliving) { return b(enchantment, entityliving); } // Paper - OBFHELPER
    public static ItemStack b(Enchantment enchantment, EntityLiving entityliving) {
        List list = enchantment.a(entityliving);

        if (list.isEmpty()) {
            return ItemStack.a;
        } else {
            ArrayList arraylist = Lists.newArrayList();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                ItemStack itemstack = (ItemStack) iterator.next();

                if (!itemstack.isEmpty() && getEnchantmentLevel(enchantment, itemstack) > 0) {
                    arraylist.add(itemstack);
                }
            }

            return arraylist.isEmpty() ? ItemStack.a : (ItemStack) arraylist.get(entityliving.getRandom().nextInt(arraylist.size()));
        }
    }

    public static int a(Random random, int i, int j, ItemStack itemstack) {
        Item item = itemstack.getItem();
        int k = item.c();

        if (k <= 0) {
            return 0;
        } else {
            if (j > 15) {
                j = 15;
            }

            int l = random.nextInt(8) + 1 + (j >> 1) + random.nextInt(j + 1);

            return i == 0 ? Math.max(l / 3, 1) : (i == 1 ? l * 2 / 3 + 1 : Math.max(l, j * 2));
        }
    }

    public static ItemStack a(Random random, ItemStack itemstack, int i, boolean flag) {
        List list = b(random, itemstack, i, flag);
        boolean flag1 = itemstack.getItem() == Items.BOOK;

        if (flag1) {
            itemstack = new ItemStack(Items.ENCHANTED_BOOK);
        }

        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            WeightedRandomEnchant weightedrandomenchant = (WeightedRandomEnchant) iterator.next();

            if (flag1) {
                ItemEnchantedBook.a(itemstack, weightedrandomenchant);
            } else {
                itemstack.addEnchantment(weightedrandomenchant.enchantment, weightedrandomenchant.level);
            }
        }

        return itemstack;
    }

    public static List<WeightedRandomEnchant> b(Random random, ItemStack itemstack, int i, boolean flag) {
        ArrayList arraylist = Lists.newArrayList();
        Item item = itemstack.getItem();
        int j = item.c();

        if (j <= 0) {
            return arraylist;
        } else {
            i += 1 + random.nextInt(j / 4 + 1) + random.nextInt(j / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;

            i = MathHelper.clamp(Math.round(i + i * f), 1, Integer.MAX_VALUE);
            List list = a(i, itemstack, flag);

            if (!list.isEmpty()) {
                arraylist.add(WeightedRandom.a(random, list));

                while (random.nextInt(50) <= i) {
                    a(list, (WeightedRandomEnchant) SystemUtils.a(arraylist));
                    if (list.isEmpty()) {
                        break;
                    }

                    arraylist.add(WeightedRandom.a(random, list));
                    i /= 2;
                }
            }

            return arraylist;
        }
    }

    public static void a(List<WeightedRandomEnchant> list, WeightedRandomEnchant weightedrandomenchant) {
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            if (!weightedrandomenchant.enchantment.c(((WeightedRandomEnchant) iterator.next()).enchantment)) {
                iterator.remove();
            }
        }

    }

    public static List<WeightedRandomEnchant> a(int i, ItemStack itemstack, boolean flag) {
        ArrayList arraylist = Lists.newArrayList();
        Item item = itemstack.getItem();
        boolean flag1 = itemstack.getItem() == Items.BOOK;
        Iterator iterator = Enchantment.enchantments.iterator();

        while (iterator.hasNext()) {
            Enchantment enchantment = (Enchantment) iterator.next();

            if ((!enchantment.isTreasure() || flag) && (enchantment.itemTarget.canEnchant(item) || flag1)) {
                for (int j = enchantment.getMaxLevel(); j > enchantment.getStartLevel() - 1; --j) {
                    if (i >= enchantment.a(j) && i <= enchantment.b(j)) {
                        arraylist.add(new WeightedRandomEnchant(enchantment, j));
                        break;
                    }
                }
            }
        }

        return arraylist;
    }

    public static final class EnchantmentModifierArthropods implements EnchantmentManager.EnchantmentModifier { // Akarin - private -> public

        public EntityLiving a;
        public Entity b;

        private EnchantmentModifierArthropods() {}

        @Override
        public void a(Enchantment enchantment, int i) {
            enchantment.a(this.a, this.b, i);
        }

        EnchantmentModifierArthropods(Object object) {
            this();
        }
    }

    public static final class EnchantmentModifierThorns implements EnchantmentManager.EnchantmentModifier { // Akarin - private -> public

        public EntityLiving a;
        public Entity b;

        private EnchantmentModifierThorns() {}

        @Override
        public void a(Enchantment enchantment, int i) {
            enchantment.b(this.a, this.b, i);
        }

        EnchantmentModifierThorns(Object object) {
            this();
        }
    }

    static final class EnchantmentModifierDamage implements EnchantmentManager.EnchantmentModifier {

        public float a;
        public EnumMonsterType b;

        private EnchantmentModifierDamage() {}

        @Override
        public void a(Enchantment enchantment, int i) {
            this.a += enchantment.a(i, this.b);
        }

        EnchantmentModifierDamage(Object object) {
            this();
        }
    }

    public static final class EnchantmentModifierProtection implements EnchantmentManager.EnchantmentModifier { // Akarin - private -> public

        public int a;
        public DamageSource b;

        private EnchantmentModifierProtection() {}

        @Override
        public void a(Enchantment enchantment, int i) {
            this.a += enchantment.a(i, this.b);
        }

        EnchantmentModifierProtection(Object object) {
            this();
        }
    }

    public interface EnchantmentModifier { // Akarin - private -> public

        void a(Enchantment enchantment, int i);
    }
}
