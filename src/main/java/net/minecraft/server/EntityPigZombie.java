package net.minecraft.server;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityPigZombie extends EntityZombie {

    private static final UUID b = UUID.fromString("49455A49-7EC5-45BA-B886-3B90B23A1718");
    private static final AttributeModifier c = (new AttributeModifier(EntityPigZombie.b, "Attacking speed boost", 0.05D, AttributeModifier.Operation.ADDITION)).a(false);
    public int angerLevel;
    private int soundDelay;
    private UUID hurtBy;

    public EntityPigZombie(EntityTypes<? extends EntityPigZombie> entitytypes, World world) {
        super(entitytypes, world);
        this.a(PathType.LAVA, 8.0F);
    }

    @Override
    public void setLastDamager(@Nullable EntityLiving entityliving) {
        super.setLastDamager(entityliving);
        if (entityliving != null) {
            this.hurtBy = entityliving.getUniqueID();
        }

    }

    @Override
    protected void l() {
        this.goalSelector.a(2, new PathfinderGoalZombieAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, new EntityPigZombie.PathfinderGoalAngerOther(this));
        this.targetSelector.a(2, new EntityPigZombie.PathfinderGoalAnger(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(EntityPigZombie.d).setValue(0.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(5.0D);
    }

    @Override
    protected boolean dY() {
        return false;
    }

    @Override
    protected void mobTick() {
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        EntityLiving entityliving = this.getLastDamager();

        if (this.ef()) {
            if (!this.isBaby() && !attributeinstance.a(EntityPigZombie.c)) {
                attributeinstance.addModifier(EntityPigZombie.c);
            }

            --this.angerLevel;
            EntityLiving entityliving1 = entityliving != null ? entityliving : this.getGoalTarget();

            if (!this.ef() && entityliving1 != null) {
                if (!this.hasLineOfSight(entityliving1)) {
                    this.setLastDamager((EntityLiving) null);
                    this.setGoalTarget((EntityLiving) null);
                } else {
                    this.angerLevel = this.ee();
                }
            }
        } else if (attributeinstance.a(EntityPigZombie.c)) {
            attributeinstance.removeModifier(EntityPigZombie.c);
        }

        if (this.soundDelay > 0 && --this.soundDelay == 0) {
            this.a(SoundEffects.ENTITY_ZOMBIE_PIGMAN_ANGRY, this.getSoundVolume() * 2.0F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 1.8F);
        }

        if (this.ef() && this.hurtBy != null && entityliving == null) {
            EntityHuman entityhuman = this.world.b(this.hurtBy);

            this.setLastDamager(entityhuman);
            this.killer = entityhuman;
            this.lastDamageByPlayerTime = this.ct();
        }

        super.mobTick();
    }

    public static boolean b(EntityTypes<EntityPigZombie> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        return iworldreader.i(this) && !iworldreader.containsLiquid(this.getBoundingBox());
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setShort("Anger", (short) this.angerLevel);
        if (this.hurtBy != null) {
            nbttagcompound.setString("HurtBy", this.hurtBy.toString());
        } else {
            nbttagcompound.setString("HurtBy", "");
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.angerLevel = nbttagcompound.getShort("Anger");
        String s = nbttagcompound.getString("HurtBy");

        if (!s.isEmpty()) {
            this.hurtBy = UUID.fromString(s);
            EntityHuman entityhuman = this.world.b(this.hurtBy);

            this.setLastDamager(entityhuman);
            if (entityhuman != null) {
                this.killer = entityhuman;
                this.lastDamageByPlayerTime = this.ct();
            }
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            Entity entity = damagesource.getEntity();

            // CraftBukkit start
            boolean result = super.damageEntity(damagesource, f);

            if (result && entity instanceof EntityHuman && !((EntityHuman) entity).isCreative() && this.hasLineOfSight(entity)) {
                this.a(entity);
            }

            return result;
            // CraftBukkit end
        }
    }

    private boolean a(Entity entity) {
        // CraftBukkit start
        org.bukkit.event.entity.PigZombieAngerEvent event = new org.bukkit.event.entity.PigZombieAngerEvent((org.bukkit.entity.PigZombie) this.getBukkitEntity(), (entity == null) ? null : entity.getBukkitEntity(), this.ee());
        this.world.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }
        this.angerLevel = event.getNewAnger();
        // CraftBukkit end
        this.soundDelay = this.random.nextInt(40);
        if (entity instanceof EntityLiving) {
            this.setLastDamager((EntityLiving) entity);
        }

        return true;
    }

    private int ee() {
        return 400 + this.random.nextInt(400);
    }

    private boolean ef() {
        return this.angerLevel > 0;
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_ZOMBIE_PIGMAN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_PIGMAN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ZOMBIE_PIGMAN_DEATH;
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        return false;
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
    }

    @Override
    protected ItemStack dX() {
        return ItemStack.a;
    }

    @Override
    public boolean e(EntityHuman entityhuman) {
        return this.ef();
    }

    static class PathfinderGoalAnger extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public PathfinderGoalAnger(EntityPigZombie entitypigzombie) {
            super(entitypigzombie, EntityHuman.class, true);
        }

        @Override
        public boolean a() {
            return ((EntityPigZombie) this.e).ef() && super.a();
        }
    }

    static class PathfinderGoalAngerOther extends PathfinderGoalHurtByTarget {

        public PathfinderGoalAngerOther(EntityPigZombie entitypigzombie) {
            super(entitypigzombie);
            this.a(new Class[]{EntityZombie.class});
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPigZombie && this.e.hasLineOfSight(entityliving) && ((EntityPigZombie) entityinsentient).a((Entity) entityliving)) {
                entityinsentient.setGoalTarget(entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_NEARBY_ENTITY, true); // CraftBukkit
            }

        }
    }
}
