package net.minecraft.server;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import javax.annotation.Nullable;

public class EntityEnderman extends EntityMonster {

    private static final UUID a = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier b = (new AttributeModifier(EntityEnderman.a, "Attacking speed boost", 0.15000000596046448D, 0)).a(false);
    private static final DataWatcherObject<Optional<IBlockData>> c = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.i);
    private int bD;
    private int bE;

    public EntityEnderman(World world) {
        super(EntityTypes.ENDERMAN, world);
        this.setSize(0.6F, 2.9F);
        this.Q = 1.0F;
        this.a(PathType.WATER, -1.0F);
    }

    protected void n() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(10, new EntityEnderman.PathfinderGoalEndermanPlaceBlock(this));
        this.goalSelector.a(11, new EntityEnderman.PathfinderGoalEndermanPickupBlock(this));
        this.targetSelector.a(1, new EntityEnderman.PathfinderGoalPlayerWhoLookedAtTarget(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, false, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, EntityEndermite::isPlayerSpawned));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0D);
    }

    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        // CraftBukkit start - fire event
        setGoalTarget(entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    @Override
    public boolean setGoalTarget(EntityLiving entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (!super.setGoalTarget(entityliving, reason, fireEvent)) {
            return false;
        }
        entityliving = getGoalTarget();
        // CraftBukkit end
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (entityliving == null) {
            this.bE = 0;
            this.datawatcher.set(EntityEnderman.bC, false);
            attributeinstance.c(EntityEnderman.b);
        } else {
            this.bE = this.ticksLived;
            this.datawatcher.set(EntityEnderman.bC, true);
            if (!attributeinstance.a(EntityEnderman.b)) {
                attributeinstance.b(EntityEnderman.b);
            }
        }
        return true;

    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityEnderman.c, Optional.empty());
        this.datawatcher.register(EntityEnderman.bC, false);
    }

    public void l() {
        if (this.ticksLived >= this.bD + 400) {
            this.bD = this.ticksLived;
            if (!this.isSilent()) {
                this.world.a(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, SoundEffects.ENTITY_ENDERMAN_STARE, this.bV(), 2.5F, 1.0F, false);
            }
        }

    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderman.bC.equals(datawatcherobject) && this.dB() && this.world.isClientSide) {
            this.l();
        }

        super.a(datawatcherobject);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            nbttagcompound.set("carriedBlockState", GameProfileSerializer.a(iblockdata));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        IBlockData iblockdata = null;

        if (nbttagcompound.hasKeyOfType("carriedBlockState", 10)) {
            iblockdata = GameProfileSerializer.d(nbttagcompound.getCompound("carriedBlockState"));
            if (iblockdata.isAir()) {
                iblockdata = null;
            }
        }

        this.setCarried(iblockdata);
    }

    private boolean f(EntityHuman entityhuman) {
        ItemStack itemstack = (ItemStack) entityhuman.inventory.armor.get(3);

        if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.getItem()) {
            return false;
        } else {
            Vec3D vec3d = entityhuman.f(1.0F).a();
            Vec3D vec3d1 = new Vec3D(this.locX - entityhuman.locX, this.getBoundingBox().minY + (double) this.getHeadHeight() - (entityhuman.locY + (double) entityhuman.getHeadHeight()), this.locZ - entityhuman.locZ);
            double d0 = vec3d1.b();

            vec3d1 = vec3d1.a();
            double d1 = vec3d.b(vec3d1);

            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    public float getHeadHeight() {
        return 2.55F;
    }

    public void movementTick() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(Particles.K, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length - 0.25D, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.bg = false;
        super.movementTick();
    }

    protected void mobTick() {
        if (this.ap()) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

        if (this.world.L() && this.ticksLived >= this.bE + 600) {
            float f = this.az();

            if (f > 0.5F && this.world.e(new BlockPosition(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F) {
                this.setGoalTarget((EntityLiving) null);
                this.dz();
            }
        }

        super.mobTick();
    }

    protected boolean dz() {
        double d0 = this.locX + (this.random.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.locY + (double) (this.random.nextInt(64) - 32);
        double d2 = this.locZ + (this.random.nextDouble() - 0.5D) * 64.0D;

        return this.k(d0, d1, d2);
    }

    protected boolean a(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX - entity.locX, this.getBoundingBox().minY + (double) (this.length / 2.0F) - entity.locY + (double) entity.getHeadHeight(), this.locZ - entity.locZ);

        vec3d = vec3d.a();
        double d0 = 16.0D;
        double d1 = this.locX + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.locY + (double) (this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.locZ + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;

        return this.k(d1, d2, d3);
    }

    private boolean k(double d0, double d1, double d2) {
        boolean flag = this.j(d0, d1, d2);

        if (flag) {
            this.world.a((EntityHuman) null, this.lastX, this.lastY, this.lastZ, SoundEffects.ENTITY_ENDERMAN_TELEPORT, this.bV(), 1.0F, 1.0F);
            this.a(SoundEffects.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }

        return flag;
    }

    protected SoundEffect D() {
        return this.dB() ? SoundEffects.ENTITY_ENDERMAN_SCREAM : SoundEffects.ENTITY_ENDERMAN_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_ENDERMAN_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_ENDERMAN_DEATH;
    }

    protected void dropEquipment(boolean flag, int i) {
        super.dropEquipment(flag, i);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            this.a((IMaterial) iblockdata.getBlock());
        }

    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.C;
    }

    public void setCarried(@Nullable IBlockData iblockdata) {
        this.datawatcher.set(EntityEnderman.c, Optional.ofNullable(iblockdata));
    }

    @Nullable
    public IBlockData getCarried() {
        return (IBlockData) ((Optional) this.datawatcher.get(EntityEnderman.c)).orElse((Object) null);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource instanceof EntityDamageSourceIndirect) {
            for (int i = 0; i < 64; ++i) {
                if (this.dz()) {
                    return true;
                }
            }

            return false;
        } else {
            boolean flag = super.damageEntity(damagesource, f);

            if (damagesource.ignoresArmor() && this.random.nextInt(10) != 0) {
                this.dz();
            }

            return flag;
        }
    }

    public boolean dB() {
        return (Boolean) this.datawatcher.get(EntityEnderman.bC);
    }

    static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPickupBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        public boolean a() {
            return this.enderman.getCarried() != null ? false : (!this.enderman.world.getGameRules().getBoolean("mobGriefing") ? false : this.enderman.getRandom().nextInt(20) == 0);
        }

        public void e() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.locX - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.locY + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.locZ - 2.0D + random.nextDouble() * 4.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();
            MovingObjectPosition movingobjectposition = world.rayTrace(new Vec3D((double) ((float) MathHelper.floor(this.enderman.locX) + 0.5F), (double) ((float) j + 0.5F), (double) ((float) MathHelper.floor(this.enderman.locZ) + 0.5F)), new Vec3D((double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F)), FluidCollisionOption.NEVER, true, false);
            boolean flag = movingobjectposition != null && movingobjectposition.getBlockPosition().equals(blockposition);

            if (block.a(TagsBlock.ENDERMAN_HOLDABLE) && flag) {
                // CraftBukkit start - Pickup event
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.enderman, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                    this.enderman.setCarried(iblockdata);
                    world.setAir(blockposition);
                }
                // CraftBukkit end
            }

        }
    }

    static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {

        private final EntityEnderman a;

        public PathfinderGoalEndermanPlaceBlock(EntityEnderman entityenderman) {
            this.a = entityenderman;
        }

        public boolean a() {
            return this.a.getCarried() == null ? false : (!this.a.world.getGameRules().getBoolean("mobGriefing") ? false : this.a.getRandom().nextInt(2000) == 0);
        }

        public void e() {
            Random random = this.a.getRandom();
            World world = this.a.world;
            int i = MathHelper.floor(this.a.locX - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.a.locY + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.a.locZ - 1.0D + random.nextDouble() * 2.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getType(blockposition);
            IBlockData iblockdata1 = world.getType(blockposition.down());
            IBlockData iblockdata2 = this.a.getCarried();

            if (iblockdata2 != null && this.a(world, blockposition, iblockdata2, iblockdata, iblockdata1)) {
                // CraftBukkit start - Place event
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.a, blockposition, iblockdata2).isCancelled()) {
                world.setTypeAndData(blockposition, iblockdata2, 3);
                this.a.setCarried((IBlockData) null);
                }
                // CraftBukkit end
            }

        }

        private boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2) {
            return iblockdata1.isAir() && !iblockdata2.isAir() && iblockdata2.g() && iblockdata.canPlace(iworldreader, blockposition);
        }
    }

    static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        private final EntityEnderman i;
        private EntityHuman j;
        private int k;
        private int l;

        public PathfinderGoalPlayerWhoLookedAtTarget(EntityEnderman entityenderman) {
            super(entityenderman, EntityHuman.class, false);
            this.i = entityenderman;
        }

        public boolean a() {
            double d0 = this.i();

            this.j = this.i.world.a(this.i.locX, this.i.locY, this.i.locZ, d0, d0, (Function) null, (entityhuman) -> {
                return entityhuman != null && this.i.f(entityhuman);
            });
            return this.j != null;
        }

        public void c() {
            this.k = 5;
            this.l = 0;
        }

        public void d() {
            this.j = null;
            super.d();
        }

        public boolean b() {
            if (this.j != null) {
                if (!this.i.f(this.j)) {
                    return false;
                } else {
                    this.i.a((Entity) this.j, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.d != null && ((EntityHuman) this.d).isAlive() ? true : super.b();
            }
        }

        public void e() {
            if (this.j != null) {
                if (--this.k <= 0) {
                    this.d = this.j;
                    this.j = null;
                    super.c();
                }
            } else {
                if (this.d != null) {
                    if (this.i.f((EntityHuman) this.d)) {
                        if (((EntityHuman) this.d).h(this.i) < 16.0D) {
                            this.i.dz();
                        }

                        this.l = 0;
                    } else if (((EntityHuman) this.d).h(this.i) > 256.0D && this.l++ >= 30 && this.i.a((Entity) this.d)) {
                        this.l = 0;
                    }
                }

                super.e();
            }

        }
    }
}
