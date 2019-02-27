package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;

public abstract class Enchantment {

    private final EnumItemSlot[] a;
    private final Enchantment.Rarity d;
    @Nullable
    public EnchantmentSlotType itemTarget;
    @Nullable
    protected String c;

    protected Enchantment(Enchantment.Rarity enchantment_rarity, EnchantmentSlotType enchantmentslottype, EnumItemSlot[] aenumitemslot) {
        this.d = enchantment_rarity;
        this.itemTarget = enchantmentslottype;
        this.a = aenumitemslot;
    }

    public List<ItemStack> a(EntityLiving entityliving) {
        List<ItemStack> list = Lists.newArrayList();
        EnumItemSlot[] aenumitemslot = this.a;
        int i = aenumitemslot.length;

        for (int j = 0; j < i; ++j) {
            EnumItemSlot enumitemslot = aenumitemslot[j];
            ItemStack itemstack = entityliving.getEquipment(enumitemslot);

            if (!itemstack.isEmpty()) {
                list.add(itemstack);
            }
        }

        return list;
    }

    public Enchantment.Rarity d() {
        return this.d;
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

    public final boolean b(Enchantment enchantment) {
        return this.a(enchantment) && enchantment.a(this);
    }

    protected boolean a(Enchantment enchantment) {
        return this != enchantment;
    }

    protected String f() {
        if (this.c == null) {
            this.c = SystemUtils.a("enchantment", IRegistry.ENCHANTMENT.getKey(this));
        }

        return this.c;
    }

    public String g() {
        return this.f();
    }

    public IChatBaseComponent d(int i) {
        ChatMessage chatmessage = new ChatMessage(this.g(), new Object[0]);

        if (this.c()) {
            chatmessage.a(EnumChatFormat.RED);
        } else {
            chatmessage.a(EnumChatFormat.GRAY);
        }

        if (i != 1 || this.getMaxLevel() != 1) {
            chatmessage.a(" ").addSibling(new ChatMessage("enchantment.level." + i, new Object[0]));
        }

        return chatmessage;
    }

    public boolean canEnchant(ItemStack itemstack) {
        return this.itemTarget.canEnchant(itemstack.getItem());
    }

    public void a(EntityLiving entityliving, Entity entity, int i) {}

    public void b(EntityLiving entityliving, Entity entity, int i) {}

    public boolean isTreasure() {
        return false;
    }

    public boolean c() {
        return false;
    }

    public static void h() {
        EnumItemSlot[] aenumitemslot = new EnumItemSlot[] { EnumItemSlot.HEAD, EnumItemSlot.CHEST, EnumItemSlot.LEGS, EnumItemSlot.FEET};

        a("protection", new EnchantmentProtection(Enchantment.Rarity.COMMON, EnchantmentProtection.DamageType.ALL, aenumitemslot));
        a("fire_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FIRE, aenumitemslot));
        a("feather_falling", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.FALL, aenumitemslot));
        a("blast_protection", new EnchantmentProtection(Enchantment.Rarity.RARE, EnchantmentProtection.DamageType.EXPLOSION, aenumitemslot));
        a("projectile_protection", new EnchantmentProtection(Enchantment.Rarity.UNCOMMON, EnchantmentProtection.DamageType.PROJECTILE, aenumitemslot));
        a("respiration", new EnchantmentOxygen(Enchantment.Rarity.RARE, aenumitemslot));
        a("aqua_affinity", new EnchantmentWaterWorker(Enchantment.Rarity.RARE, aenumitemslot));
        a("thorns", new EnchantmentThorns(Enchantment.Rarity.VERY_RARE, aenumitemslot));
        a("depth_strider", new EnchantmentDepthStrider(Enchantment.Rarity.RARE, aenumitemslot));
        a("frost_walker", new EnchantmentFrostWalker(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.FEET}));
        a("binding_curse", new EnchantmentBinding(Enchantment.Rarity.VERY_RARE, aenumitemslot));
        a("sharpness", new EnchantmentWeaponDamage(Enchantment.Rarity.COMMON, 0, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("smite", new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 1, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("bane_of_arthropods", new EnchantmentWeaponDamage(Enchantment.Rarity.UNCOMMON, 2, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("knockback", new EnchantmentKnockback(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("fire_aspect", new EnchantmentFire(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("looting", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.WEAPON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("sweeping", new EnchantmentSweeping(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("efficiency", new EnchantmentDigging(Enchantment.Rarity.COMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("silk_touch", new EnchantmentSilkTouch(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("unbreaking", new EnchantmentDurability(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("fortune", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.DIGGER, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("power", new EnchantmentArrowDamage(Enchantment.Rarity.COMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("punch", new EnchantmentArrowKnockback(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("flame", new EnchantmentFlameArrows(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("infinity", new EnchantmentInfiniteArrows(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("luck_of_the_sea", new EnchantmentLootBonus(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("lure", new EnchantmentLure(Enchantment.Rarity.RARE, EnchantmentSlotType.FISHING_ROD, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("loyalty", new EnchantmentTridentLoyalty(Enchantment.Rarity.UNCOMMON, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("impaling", new EnchantmentTridentImpaling(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("riptide", new EnchantmentTridentRiptide(Enchantment.Rarity.RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("channeling", new EnchantmentTridentChanneling(Enchantment.Rarity.VERY_RARE, new EnumItemSlot[] { EnumItemSlot.MAINHAND}));
        a("mending", new EnchantmentMending(Enchantment.Rarity.RARE, EnumItemSlot.values()));
        a("vanishing_curse", new EnchantmentVanishing(Enchantment.Rarity.VERY_RARE, EnumItemSlot.values()));
    }

    private static void a(String s, Enchantment enchantment) {
        IRegistry.ENCHANTMENT.a(new MinecraftKey(s), (Object) enchantment);
    }

    public static enum Rarity {

        COMMON(10), UNCOMMON(5), RARE(2), VERY_RARE(1);

        private final int e;

        private Rarity(int i) {
            this.e = i;
        }

        public int a() {
            return this.e;
        }
    }
}
