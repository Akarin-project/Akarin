package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
// CraftBukkit end

public class MobEffectList {

    private final Map<IAttribute, AttributeModifier> a = Maps.newHashMap();
    private final boolean b;
    private final int c;
    @Nullable
    private String d;
    private int e = -1;
    public double durationModifier;
    private boolean g;

    @Nullable
    public static MobEffectList fromId(int i) {
        return (MobEffectList) IRegistry.MOB_EFFECT.fromId(i);
    }

    public static int getId(MobEffectList mobeffectlist) {
        return IRegistry.MOB_EFFECT.a(mobeffectlist); // CraftBukkit - decompile error
    }

    protected MobEffectList(boolean flag, int i) {
        this.b = flag;
        if (flag) {
            this.durationModifier = 0.5D;
        } else {
            this.durationModifier = 1.0D;
        }

        this.c = i;
    }

    protected MobEffectList b(int i, int j) {
        this.e = i + j * 12;
        return this;
    }

    public void tick(EntityLiving entityliving, int i) {
        if (this == MobEffects.REGENERATION) {
            if (entityliving.getHealth() < entityliving.getMaxHealth()) {
                entityliving.heal(1.0F, RegainReason.MAGIC_REGEN); // CraftBukkit
            }
        } else if (this == MobEffects.POISON) {
            if (entityliving.getHealth() > 1.0F) {
                entityliving.damageEntity(CraftEventFactory.POISON, 1.0F);  // CraftBukkit - DamageSource.MAGIC -> CraftEventFactory.POISON
            }
        } else if (this == MobEffects.WITHER) {
            entityliving.damageEntity(DamageSource.WITHER, 1.0F);
        } else if (this == MobEffects.HUNGER && entityliving instanceof EntityHuman) {
            ((EntityHuman) entityliving).applyExhaustion(0.005F * (float) (i + 1));
        } else if (this == MobEffects.SATURATION && entityliving instanceof EntityHuman) {
            if (!entityliving.world.isClientSide) {
                // CraftBukkit start
                EntityHuman entityhuman = (EntityHuman) entityliving;
                int oldFoodLevel = entityhuman.getFoodData().foodLevel;

                org.bukkit.event.entity.FoodLevelChangeEvent event = CraftEventFactory.callFoodLevelChangeEvent(entityhuman, i + 1 + oldFoodLevel);

                if (!event.isCancelled()) {
                    entityhuman.getFoodData().eat(event.getFoodLevel() - oldFoodLevel, 1.0F);
                }

                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutUpdateHealth(((EntityPlayer) entityhuman).getBukkitEntity().getScaledHealth(), entityhuman.getFoodData().foodLevel, entityhuman.getFoodData().saturationLevel));
                // CraftBukkit end
            }
        } else if ((this != MobEffects.HEAL || entityliving.cp()) && (this != MobEffects.HARM || !entityliving.cp())) {
            if (this == MobEffects.HARM && !entityliving.cp() || this == MobEffects.HEAL && entityliving.cp()) {
                entityliving.damageEntity(DamageSource.MAGIC, (float) (6 << i));
            }
        } else {
            entityliving.heal((float) Math.max(4 << i, 0), RegainReason.MAGIC); // CraftBukkit
        }

    }

    public void applyInstantEffect(@Nullable Entity entity, @Nullable Entity entity1, EntityLiving entityliving, int i, double d0) {
        int j;

        if ((this != MobEffects.HEAL || entityliving.cp()) && (this != MobEffects.HARM || !entityliving.cp())) {
            if ((this != MobEffects.HARM || entityliving.cp()) && (this != MobEffects.HEAL || !entityliving.cp())) {
                this.tick(entityliving, i);
            } else {
                j = (int) (d0 * (double) (6 << i) + 0.5D);
                if (entity == null) {
                    entityliving.damageEntity(DamageSource.MAGIC, (float) j);
                } else {
                    entityliving.damageEntity(DamageSource.c(entity, entity1), (float) j);
                }
            }
        } else {
            j = (int) (d0 * (double) (4 << i) + 0.5D);
            entityliving.heal((float) j, RegainReason.MAGIC); // CraftBukkit
        }

    }

    public boolean a(int i, int j) {
        int k;

        if (this == MobEffects.REGENERATION) {
            k = 50 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.POISON) {
            k = 25 >> j;
            return k > 0 ? i % k == 0 : true;
        } else if (this == MobEffects.WITHER) {
            k = 40 >> j;
            return k > 0 ? i % k == 0 : true;
        } else {
            return this == MobEffects.HUNGER;
        }
    }

    public boolean isInstant() {
        return false;
    }

    protected String b() {
        if (this.d == null) {
            this.d = SystemUtils.a("effect", IRegistry.MOB_EFFECT.getKey(this));
        }

        return this.d;
    }

    public String c() {
        return this.b();
    }

    public IChatBaseComponent d() {
        return new ChatMessage(this.c(), new Object[0]);
    }

    protected MobEffectList a(double d0) {
        this.durationModifier = d0;
        return this;
    }

    public int getColor() {
        return this.c;
    }

    public MobEffectList a(IAttribute iattribute, String s, double d0, int i) {
        AttributeModifier attributemodifier = new AttributeModifier(UUID.fromString(s), this::c, d0, i);

        this.a.put(iattribute, attributemodifier);
        return this;
    }

    public void a(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<IAttribute, AttributeModifier> entry = (Entry) iterator.next();
            AttributeInstance attributeinstance = attributemapbase.a((IAttribute) entry.getKey());

            if (attributeinstance != null) {
                attributeinstance.c((AttributeModifier) entry.getValue());
            }
        }

    }

    public void b(EntityLiving entityliving, AttributeMapBase attributemapbase, int i) {
        Iterator iterator = this.a.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<IAttribute, AttributeModifier> entry = (Entry) iterator.next();
            AttributeInstance attributeinstance = attributemapbase.a((IAttribute) entry.getKey());

            if (attributeinstance != null) {
                AttributeModifier attributemodifier = (AttributeModifier) entry.getValue();

                attributeinstance.c(attributemodifier);
                attributeinstance.b(new AttributeModifier(attributemodifier.a(), this.c() + " " + i, this.a(i, attributemodifier), attributemodifier.c()));
            }
        }

    }

    public double a(int i, AttributeModifier attributemodifier) {
        return attributemodifier.d() * (double) (i + 1);
    }

    public MobEffectList l() {
        this.g = true;
        return this;
    }

    public static void m() {
        a(1, "speed", (new MobEffectList(false, 8171462)).b(0, 0).a(GenericAttributes.MOVEMENT_SPEED, "91AEAA56-376B-4498-935B-2F7F68070635", 0.20000000298023224D, 2).l());
        a(2, "slowness", (new MobEffectList(true, 5926017)).b(1, 0).a(GenericAttributes.MOVEMENT_SPEED, "7107DE5E-7CE8-4030-940E-514C1F160890", -0.15000000596046448D, 2));
        a(3, "haste", (new MobEffectList(false, 14270531)).b(2, 0).a(1.5D).l().a(GenericAttributes.g, "AF8B6E3F-3328-4C0A-AA36-5BA2BB9DBEF3", 0.10000000149011612D, 2));
        a(4, "mining_fatigue", (new MobEffectList(true, 4866583)).b(3, 0).a(GenericAttributes.g, "55FCED67-E92A-486E-9800-B47F202C4386", -0.10000000149011612D, 2));
        a(5, "strength", (new MobEffectAttackDamage(false, 9643043, 3.0D)).b(4, 0).a(GenericAttributes.ATTACK_DAMAGE, "648D7064-6A60-4F59-8ABE-C2C23A6DD7A9", 0.0D, 0).l());
        a(6, "instant_health", (new InstantMobEffect(false, 16262179)).l());
        a(7, "instant_damage", (new InstantMobEffect(true, 4393481)).l());
        a(8, "jump_boost", (new MobEffectList(false, 2293580)).b(2, 1).l());
        a(9, "nausea", (new MobEffectList(true, 5578058)).b(3, 1).a(0.25D));
        a(10, "regeneration", (new MobEffectList(false, 13458603)).b(7, 0).a(0.25D).l());
        a(11, "resistance", (new MobEffectList(false, 10044730)).b(6, 1).l());
        a(12, "fire_resistance", (new MobEffectList(false, 14981690)).b(7, 1).l());
        a(13, "water_breathing", (new MobEffectList(false, 3035801)).b(0, 2).l());
        a(14, "invisibility", (new MobEffectList(false, 8356754)).b(0, 1).l());
        a(15, "blindness", (new MobEffectList(true, 2039587)).b(5, 1).a(0.25D));
        a(16, "night_vision", (new MobEffectList(false, 2039713)).b(4, 1).l());
        a(17, "hunger", (new MobEffectList(true, 5797459)).b(1, 1));
        a(18, "weakness", (new MobEffectAttackDamage(true, 4738376, -4.0D)).b(5, 0).a(GenericAttributes.ATTACK_DAMAGE, "22653B89-116E-49DC-9B6B-9971489B5BE5", 0.0D, 0));
        a(19, "poison", (new MobEffectList(true, 5149489)).b(6, 0).a(0.25D));
        a(20, "wither", (new MobEffectList(true, 3484199)).b(1, 2).a(0.25D));
        a(21, "health_boost", (new MobEffectHealthBoost(false, 16284963)).b(7, 2).a(GenericAttributes.maxHealth, "5D6F0BA2-1186-46AC-B896-C61C5CEE99CC", 4.0D, 0).l());
        a(22, "absorption", (new MobEffectAbsorption(false, 2445989)).b(2, 2).l());
        a(23, "saturation", (new InstantMobEffect(false, 16262179)).l());
        a(24, "glowing", (new MobEffectList(false, 9740385)).b(4, 2));
        a(25, "levitation", (new MobEffectList(true, 13565951)).b(3, 2));
        a(26, "luck", (new MobEffectList(false, 3381504)).b(5, 2).l().a(GenericAttributes.j, "03C3C89D-7037-4B42-869F-B146BCB64D2E", 1.0D, 0));
        a(27, "unluck", (new MobEffectList(true, 12624973)).b(6, 2).a(GenericAttributes.j, "CC5AF142-2BD2-4215-B636-2605AED11727", -1.0D, 0));
        a(28, "slow_falling", (new MobEffectList(false, 16773073)).b(8, 0).l());
        a(29, "conduit_power", (new MobEffectList(false, 1950417)).b(9, 0).l());
        a(30, "dolphins_grace", (new MobEffectList(false, 8954814)).b(10, 0).l());
        // CraftBukkit start
        for (Object effect : IRegistry.MOB_EFFECT) {
            org.bukkit.potion.PotionEffectType.registerPotionEffectType(new org.bukkit.craftbukkit.potion.CraftPotionEffectType((MobEffectList) effect));
        }
        // CraftBukkit end
    }

    private static void a(int i, String s, MobEffectList mobeffectlist) {
        IRegistry.MOB_EFFECT.a(i, new MinecraftKey(s), mobeffectlist);
    }
}
