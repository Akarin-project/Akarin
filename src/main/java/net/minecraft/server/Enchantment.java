package net.minecraft.server;

import java.util.ArrayList;

public abstract class Enchantment {

    // CraftBukkit - update CraftEnchant.getName(i) if this changes
    public static final Enchantment[] byId = new Enchantment[256];
    public static final Enchantment[] c;
    public static final Enchantment PROTECTION_ENVIRONMENTAL = new EnchantmentProtection(0, 10, 0);
    public static final Enchantment PROTECTION_FIRE = new EnchantmentProtection(1, 5, 1);
    public static final Enchantment PROTECTION_FALL = new EnchantmentProtection(2, 5, 2);
    public static final Enchantment PROTECTION_EXPLOSIONS = new EnchantmentProtection(3, 2, 3);
    public static final Enchantment PROTECTION_PROJECTILE = new EnchantmentProtection(4, 5, 4);
    public static final Enchantment OXYGEN = new EnchantmentOxygen(5, 2);
    public static final Enchantment WATER_WORKER = new EnchantmentWaterWorker(6, 2);
    public static final Enchantment THORNS = new EnchantmentThorns(7, 1);
    public static final Enchantment DAMAGE_ALL = new EnchantmentWeaponDamage(16, 10, 0);
    public static final Enchantment DAMAGE_UNDEAD = new EnchantmentWeaponDamage(17, 5, 1);
    public static final Enchantment DAMAGE_ARTHROPODS = new EnchantmentWeaponDamage(18, 5, 2);
    public static final Enchantment KNOCKBACK = new EnchantmentKnockback(19, 5);
    public static final Enchantment FIRE_ASPECT = new EnchantmentFire(20, 2);
    public static final Enchantment LOOT_BONUS_MOBS = new EnchantmentLootBonus(21, 2, EnchantmentSlotType.WEAPON);
    public static final Enchantment DIG_SPEED = new EnchantmentDigging(32, 10);
    public static final Enchantment SILK_TOUCH = new EnchantmentSilkTouch(33, 1);
    public static final Enchantment DURABILITY = new EnchantmentDurability(34, 5);
    public static final Enchantment LOOT_BONUS_BLOCKS = new EnchantmentLootBonus(35, 2, EnchantmentSlotType.DIGGER);
    public static final Enchantment ARROW_DAMAGE = new EnchantmentArrowDamage(48, 10);
    public static final Enchantment ARROW_KNOCKBACK = new EnchantmentArrowKnockback(49, 2);
    public static final Enchantment ARROW_FIRE = new EnchantmentFlameArrows(50, 2);
    public static final Enchantment ARROW_INFINITE = new EnchantmentInfiniteArrows(51, 1);
    public static final Enchantment LUCK = new EnchantmentLootBonus(61, 2, EnchantmentSlotType.FISHING_ROD);
    public static final Enchantment LURE = new EnchantmentLure(62, 2, EnchantmentSlotType.FISHING_ROD);
    public final int id;
    private final int weight;
    public EnchantmentSlotType slot;
    protected String name;

    protected Enchantment(int i, int j, EnchantmentSlotType enchantmentslottype) {
        this.id = i;
        this.weight = j;
        this.slot = enchantmentslottype;
        if (byId[i] != null) {
            throw new IllegalArgumentException("Duplicate enchantment id!");
        } else {
            byId[i] = this;
        }

        org.bukkit.enchantments.Enchantment.registerEnchantment(new org.bukkit.craftbukkit.enchantments.CraftEnchantment(this)); // CraftBukkit
    }

    public int getRandomWeight() {
        return this.weight;
    }

    public int getStartLevel() {
        return 1;
    }

    public int getMaxLevel() {
        return 1;
    }

    public int a(int i) {
        return 1 + i * 10;
    }

    public int b(int i) {
        return this.a(i) + 5;
    }

    public int a(int i, DamageSource damagesource) {
        return 0;
    }

    public float a(int i, EnumMonsterType enummonstertype) {
        return 0.0F;
    }

    public boolean a(Enchantment enchantment) {
        return this != enchantment;
    }

    public Enchantment b(String s) {
        this.name = s;
        return this;
    }

    public String a() {
        return "enchantment." + this.name;
    }

    public String c(int i) {
        String s = LocaleI18n.get(this.a());

        return s + " " + LocaleI18n.get("enchantment.level." + i);
    }

    public boolean canEnchant(ItemStack itemstack) {
        return this.slot.canEnchant(itemstack.getItem());
    }

    public void a(EntityLiving entityliving, Entity entity, int i) {}

    public void b(EntityLiving entityliving, Entity entity, int i) {}

    static {
        ArrayList arraylist = new ArrayList();
        Enchantment[] aenchantment = byId;
        int i = aenchantment.length;

        for (int j = 0; j < i; ++j) {
            Enchantment enchantment = aenchantment[j];

            if (enchantment != null) {
                arraylist.add(enchantment);
            }
        }

        c = (Enchantment[]) arraylist.toArray(new Enchantment[0]);
    }
}
