package net.minecraft.server;

import java.util.Random;

public class EnchantmentThorns extends Enchantment {

    public EnchantmentThorns(Enchantment.Rarity enchantment_rarity, EnumItemSlot... aenumitemslot) {
        super(enchantment_rarity, EnchantmentSlotType.ARMOR_CHEST, aenumitemslot);
    }

    public int a(int i) {
        return 10 + 20 * (i - 1);
    }

    public int b(int i) {
        return super.a(i) + 50;
    }

    public int getMaxLevel() {
        return 3;
    }

    public boolean canEnchant(ItemStack itemstack) {
        return itemstack.getItem() instanceof ItemArmor ? true : super.canEnchant(itemstack);
    }

    public void b(EntityLiving entityliving, Entity entity, int i) {
        Random random = entityliving.getRandom();
        ItemStack itemstack = EnchantmentManager.b(Enchantments.THORNS, entityliving);

        if (entity != null && a(i, random)) { // CraftBukkit
            if (entity != null) {
                entity.damageEntity(DamageSource.a(entityliving), (float) b(i, random));
            }

            if (!itemstack.isEmpty()) {
                itemstack.damage(3, entityliving);
            }
        } else if (!itemstack.isEmpty()) {
            itemstack.damage(1, entityliving);
        }

    }

    public static boolean a(int i, Random random) {
        return i <= 0 ? false : random.nextFloat() < 0.15F * (float) i;
    }

    public static int b(int i, Random random) {
        return i > 10 ? i - 10 : 1 + random.nextInt(4);
    }
}
