package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class EntityWitch extends EntityMonster implements IRangedEntity {

    private static final UUID bp = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier bq = (new AttributeModifier(bp, "Drinking speed penalty", -0.25D, 0)).a(false);
    private static final Item[] br = new Item[] { Items.GLOWSTONE_DUST, Items.SUGAR, Items.REDSTONE, Items.SPIDER_EYE, Items.GLASS_BOTTLE, Items.SULPHUR, Items.STICK, Items.STICK};
    private int bs;

    public EntityWitch(World world) {
        super(world);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 60, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 0, true));
    }

    protected void c() {
        super.c();
        this.getDataWatcher().a(21, Byte.valueOf((byte) 0));
    }

    protected String t() {
        return "mob.witch.idle";
    }

    protected String aS() {
        return "mob.witch.hurt";
    }

    protected String aT() {
        return "mob.witch.death";
    }

    public void a(boolean flag) {
        this.getDataWatcher().watch(21, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public boolean bZ() {
        return this.getDataWatcher().getByte(21) == 1;
    }

    protected void aC() {
        super.aC();
        this.getAttributeInstance(GenericAttributes.a).setValue(26.0D);
        this.getAttributeInstance(GenericAttributes.d).setValue(0.25D);
    }

    public boolean bj() {
        return true;
    }

    public void e() {
        if (!this.world.isStatic) {
            if (this.bZ()) {
                if (this.bs-- <= 0) {
                    this.a(false);
                    ItemStack itemstack = this.bd();

                    this.setEquipment(0, (ItemStack) null);
                    if (itemstack != null && itemstack.getItem() == Items.POTION) {
                        List list = Items.POTION.g(itemstack);

                        if (list != null) {
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                MobEffect mobeffect = (MobEffect) iterator.next();

                                this.addEffect(new MobEffect(mobeffect));
                            }
                        }
                    }

                    this.getAttributeInstance(GenericAttributes.d).b(bq);
                }
            } else {
                short short1 = -1;

                if (this.random.nextFloat() < 0.15F && this.a(Material.WATER) && !this.hasEffect(MobEffectList.WATER_BREATHING)) {
                    short1 = 8237;
                } else if (this.random.nextFloat() < 0.15F && this.isBurning() && !this.hasEffect(MobEffectList.FIRE_RESISTANCE)) {
                    short1 = 16307;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    short1 = 16341;
                } else if (this.random.nextFloat() < 0.25F && this.getGoalTarget() != null && !this.hasEffect(MobEffectList.FASTER_MOVEMENT) && this.getGoalTarget().f(this) > 121.0D) {
                    short1 = 16274;
                } else if (this.random.nextFloat() < 0.25F && this.getGoalTarget() != null && !this.hasEffect(MobEffectList.FASTER_MOVEMENT) && this.getGoalTarget().f(this) > 121.0D) {
                    short1 = 16274;
                }

                if (short1 > -1) {
                    this.setEquipment(0, new ItemStack(Items.POTION, 1, short1));
                    this.bs = this.bd().n();
                    this.a(true);
                    AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.d);

                    attributeinstance.b(bq);
                    attributeinstance.a(bq);
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.world.broadcastEntityEffect(this, (byte) 15);
            }
        }

        super.e();
    }

    protected float c(DamageSource damagesource, float f) {
        f = super.c(damagesource, f);
        if (damagesource.getEntity() == this) {
            f = 0.0F;
        }

        if (damagesource.s()) {
            f = (float) ((double) f * 0.15D);
        }

        return f;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();

        int j = this.random.nextInt(3) + 1;

        for (int k = 0; k < j; ++k) {
            int l = this.random.nextInt(3);
            Item item = br[this.random.nextInt(br.length)];

            if (i > 0) {
                l += this.random.nextInt(i + 1);
            }

            if (l > 0) {
                loot.add(new org.bukkit.inventory.ItemStack(org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(item), l));
            }
        }

        org.bukkit.craftbukkit.event.CraftEventFactory.callEntityDeathEvent(this, loot);
        // CraftBukkit end
    }

    public void a(EntityLiving entityliving, float f) {
        if (!this.bZ()) {
            EntityPotion entitypotion = new EntityPotion(this.world, this, 32732);

            entitypotion.pitch -= -20.0F;
            double d0 = entityliving.locX + entityliving.motX - this.locX;
            double d1 = entityliving.locY + (double) entityliving.getHeadHeight() - 1.100000023841858D - this.locY;
            double d2 = entityliving.locZ + entityliving.motZ - this.locZ;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2);

            if (f1 >= 8.0F && !entityliving.hasEffect(MobEffectList.SLOWER_MOVEMENT)) {
                entitypotion.setPotionValue(32698);
            } else if (entityliving.getHealth() >= 8.0F && !entityliving.hasEffect(MobEffectList.POISON)) {
                entitypotion.setPotionValue(32660);
            } else if (f1 <= 3.0F && !entityliving.hasEffect(MobEffectList.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                entitypotion.setPotionValue(32696);
            }

            entitypotion.shoot(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
            this.world.addEntity(entitypotion);
        }
    }
}
