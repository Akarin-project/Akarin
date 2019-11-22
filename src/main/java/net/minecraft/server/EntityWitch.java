package net.minecraft.server;

// Paper start
import com.destroystokyo.paper.event.entity.WitchReadyPotionEvent;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Witch;
// Paper end

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class EntityWitch extends EntityRaider implements IRangedEntity {

    private static final UUID b = UUID.fromString("5CD17E52-A79A-43D3-A529-90FDE04B181E");
    private static final AttributeModifier bz = (new AttributeModifier(EntityWitch.b, "Drinking speed penalty", -0.25D, AttributeModifier.Operation.ADDITION)).a(false); private static final AttributeModifier DRINKING_SPEED = bz; // Paper - OBFHELPER
    private static final DataWatcherObject<Boolean> bA = DataWatcher.a(EntityWitch.class, DataWatcherRegistry.i);
    private int bB; public int getPotionUseTimeLeft() { return bB; } public void setPotionUseTimeLeft(int timeLeft) { bB = timeLeft; } // Paper - OBFHELPER
    private PathfinderGoalNearestHealableRaider<EntityRaider> bC;
    private PathfinderGoalNearestAttackableTargetWitch<EntityHuman> bD;

    public EntityWitch(EntityTypes<? extends EntityWitch> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.bC = new PathfinderGoalNearestHealableRaider<>(this, EntityRaider.class, true, (entityliving) -> {
            return entityliving != null && this.ek() && entityliving.getEntityType() != EntityTypes.WITCH;
        });
        this.bD = new PathfinderGoalNearestAttackableTargetWitch<>(this, EntityHuman.class, 10, true, false, (Predicate) null);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalArrowAttack(this, 1.0D, 60, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(3, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class}));
        this.targetSelector.a(2, this.bC);
        this.targetSelector.a(3, this.bD);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.getDataWatcher().register(EntityWitch.bA, false);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_WITCH_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_WITCH_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_WITCH_DEATH;
    }

    public void setDrinkingPotion(boolean drinkingPotion) { s(drinkingPotion); } // Paper - OBFHELPER
    public void s(boolean flag) {
        this.getDataWatcher().set(EntityWitch.bA, flag);
    }

    public boolean isDrinkingPotion() { return l(); } // Paper - OBFHELPER
    public boolean l() {
        return (Boolean) this.getDataWatcher().get(EntityWitch.bA);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(26.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Override
    public void movementTick() {
        if (!this.world.isClientSide && this.isAlive()) {
            this.bC.j();
            if (this.bC.h() <= 0) {
                this.bD.a(true);
            } else {
                this.bD.a(false);
            }

            if (this.l()) {
                if (this.bB-- <= 0) {
                    this.s(false);
                    ItemStack itemstack = this.getItemInMainHand();

                    this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                    if (itemstack.getItem() == Items.POTION) {
                        // Paper start
                        com.destroystokyo.paper.event.entity.WitchConsumePotionEvent event = new com.destroystokyo.paper.event.entity.WitchConsumePotionEvent((org.bukkit.entity.Witch) this.getBukkitEntity(), org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(itemstack));

                        List<MobEffect> list = event.callEvent() ? PotionUtil.getEffects(org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getPotion())) : null;
                        // Paper end

                        if (list != null) {
                            Iterator iterator = list.iterator();

                            while (iterator.hasNext()) {
                                MobEffect mobeffect = (MobEffect) iterator.next();

                                this.addEffect(new MobEffect(mobeffect), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.ATTACK); // CraftBukkit
                            }
                        }
                    }

                    this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).removeModifier(EntityWitch.bz);
                }
            } else {
                PotionRegistry potionregistry = null;

                if (this.random.nextFloat() < 0.15F && this.a(TagsFluid.WATER) && !this.hasEffect(MobEffects.WATER_BREATHING)) {
                    potionregistry = Potions.WATER_BREATHING;
                } else if (this.random.nextFloat() < 0.15F && (this.isBurning() || this.cE() != null && this.cE().p()) && !this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
                    potionregistry = Potions.FIRE_RESISTANCE;
                } else if (this.random.nextFloat() < 0.05F && this.getHealth() < this.getMaxHealth()) {
                    potionregistry = Potions.HEALING;
                } else if (this.random.nextFloat() < 0.5F && this.getGoalTarget() != null && !this.hasEffect(MobEffects.FASTER_MOVEMENT) && this.getGoalTarget().h((Entity) this) > 121.0D) {
                    potionregistry = Potions.SWIFTNESS;
                }

                if (potionregistry != null) {
                    // Paper start - move all this down into its own method
//                    ItemStack potion = PotionUtil.a(new ItemStack(Items.POTION), potionregistry);
//                    org.bukkit.inventory.ItemStack bukkitStack = com.destroystokyo.paper.event.entity.WitchReadyPotionEvent.process((org.bukkit.entity.Witch) this.getBukkitEntity(), org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(potion));
//                    this.setSlot(EnumItemSlot.MAINHAND, org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(bukkitStack));
//                    // Paper end
//                    this.bB = this.getItemInMainHand().k();
//                    this.s(true);
//                    this.world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_WITCH_DRINK, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
//                    AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
//
//                    attributeinstance.removeModifier(EntityWitch.bz);
//                    attributeinstance.addModifier(EntityWitch.bz);
                    this.setDrinkingPotion(PotionUtil.addPotionToItemStack(new ItemStack(Items.POTION), potionregistry));
                    // Paper end
                }
            }

            if (this.random.nextFloat() < 7.5E-4F) {
                this.world.broadcastEntityEffect(this, (byte) 15);
            }
        }

        super.movementTick();
    }

    // Paper start
    public void setDrinkingPotion(ItemStack potion) {
        setSlot(EnumItemSlot.MAINHAND, CraftItemStack.asNMSCopy(WitchReadyPotionEvent.process((Witch) getBukkitEntity(), CraftItemStack.asCraftMirror(potion))));
        setPotionUseTimeLeft(getItemInMainHand().getItemUseMaxDuration());
        setDrinkingPotion(true);
        world.sendSoundEffect(null, locX, locY, locZ, SoundEffects.ENTITY_WITCH_DRINK, getSoundCategory(), 1.0F, 0.8F + random.nextFloat() * 0.4F);
        AttributeInstance attributeinstance = getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        attributeinstance.removeModifier(EntityWitch.DRINKING_SPEED);
        attributeinstance.addModifier(EntityWitch.DRINKING_SPEED);
    }
    // Paper end

    @Override
    public SoundEffect dV() {
        return SoundEffects.ENTITY_WITCH_CELEBRATE;
    }

    @Override
    protected float applyMagicModifier(DamageSource damagesource, float f) {
        f = super.applyMagicModifier(damagesource, f);
        if (damagesource.getEntity() == this) {
            f = 0.0F;
        }

        if (damagesource.isMagic()) {
            f = (float) ((double) f * 0.15D);
        }

        return f;
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        if (!this.l()) {
            Vec3D vec3d = entityliving.getMot();
            double d0 = entityliving.locX + vec3d.x - this.locX;
            double d1 = entityliving.locY + (double) entityliving.getHeadHeight() - 1.100000023841858D - this.locY;
            double d2 = entityliving.locZ + vec3d.z - this.locZ;
            float f1 = MathHelper.sqrt(d0 * d0 + d2 * d2);
            PotionRegistry potionregistry = Potions.HARMING;

            if (entityliving instanceof EntityRaider) {
                if (entityliving.getHealth() <= 4.0F) {
                    potionregistry = Potions.HEALING;
                } else {
                    potionregistry = Potions.REGENERATION;
                }

                this.setGoalTarget((EntityLiving) null);
            } else if (f1 >= 8.0F && !entityliving.hasEffect(MobEffects.SLOWER_MOVEMENT)) {
                potionregistry = Potions.SLOWNESS;
            } else if (entityliving.getHealth() >= 8.0F && !entityliving.hasEffect(MobEffects.POISON)) {
                potionregistry = Potions.POISON;
            } else if (f1 <= 3.0F && !entityliving.hasEffect(MobEffects.WEAKNESS) && this.random.nextFloat() < 0.25F) {
                potionregistry = Potions.WEAKNESS;
            }

            // Paper start
            ItemStack potion = PotionUtil.a(new ItemStack(Items.SPLASH_POTION), potionregistry);
            com.destroystokyo.paper.event.entity.WitchThrowPotionEvent event = new com.destroystokyo.paper.event.entity.WitchThrowPotionEvent((org.bukkit.entity.Witch) this.getBukkitEntity(), (org.bukkit.entity.LivingEntity) entityliving.getBukkitEntity(), org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(potion));
            if (!event.callEvent()) {
                return;
            }
            potion = org.bukkit.craftbukkit.inventory.CraftItemStack.asNMSCopy(event.getPotion());
            EntityPotion entitypotion = new EntityPotion(this.world, this);
            entitypotion.setItem(potion);
            // Paper end
            entitypotion.pitch -= -20.0F;
            entitypotion.shoot(d0, d1 + (double) (f1 * 0.2F), d2, 0.75F, 8.0F);
            this.world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_WITCH_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.random.nextFloat() * 0.4F);
            this.world.addEntity(entitypotion);
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 1.62F;
    }

    @Override
    public void a(int i, boolean flag) {}

    @Override
    public boolean dX() {
        return false;
    }
}
