package net.minecraft.server;

import java.util.EnumSet;
import java.util.Optional;
import com.destroystokyo.paper.event.entity.EndermanEscapeEvent; // Paper
import java.util.Random;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityEnderman extends EntityMonster {

    private static final UUID b = UUID.fromString("020E0DFB-87AE-4653-9556-831010E291A0");
    private static final AttributeModifier c = (new AttributeModifier(EntityEnderman.b, "Attacking speed boost", 0.15000000596046448D, AttributeModifier.Operation.ADDITION)).a(false);
    private static final DataWatcherObject<Optional<IBlockData>> d = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.h);
    private static final DataWatcherObject<Boolean> bz = DataWatcher.a(EntityEnderman.class, DataWatcherRegistry.i);
    private static final Predicate<EntityLiving> bA = (entityliving) -> {
        return entityliving instanceof EntityEndermite && ((EntityEndermite) entityliving).isPlayerSpawned();
    };
    private int bB;
    private int bC;

    public EntityEnderman(EntityTypes<? extends EntityEnderman> entitytypes, World world) {
        super(entitytypes, world);
        this.K = 1.0F;
        this.a(PathType.WATER, -1.0F);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new EntityEnderman.a(this));
        this.goalSelector.a(2, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(7, new PathfinderGoalRandomStrollLand(this, 1.0D, 0.0F));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(10, new EntityEnderman.PathfinderGoalEndermanPlaceBlock(this));
        this.goalSelector.a(11, new EntityEnderman.PathfinderGoalEndermanPickupBlock(this));
        this.targetSelector.a(1, new EntityEnderman.PathfinderGoalPlayerWhoLookedAtTarget(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityEndermite.class, 10, true, false, EntityEnderman.bA));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(40.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.30000001192092896D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(7.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(64.0D);
    }

    @Override
    public void setGoalTarget(@Nullable EntityLiving entityliving) {
        // CraftBukkit start - fire event
        setGoalTarget(entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason.UNKNOWN, true);
    }

    // Paper start
    private boolean tryEscape(EndermanEscapeEvent.Reason reason) {
        return new EndermanEscapeEvent((org.bukkit.craftbukkit.entity.CraftEnderman) this.getBukkitEntity(), reason).callEvent();
    }
    // Paper end

    @Override
    public boolean setGoalTarget(EntityLiving entityliving, org.bukkit.event.entity.EntityTargetEvent.TargetReason reason, boolean fireEvent) {
        if (!super.setGoalTarget(entityliving, reason, fireEvent)) {
            return false;
        }
        entityliving = getGoalTarget();
        // CraftBukkit end
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (entityliving == null) {
            this.bC = 0;
            this.datawatcher.set(EntityEnderman.bz, false);
            attributeinstance.removeModifier(EntityEnderman.c);
        } else {
            this.bC = this.ticksLived;
            this.datawatcher.set(EntityEnderman.bz, true);
            if (!attributeinstance.a(EntityEnderman.c)) {
                attributeinstance.addModifier(EntityEnderman.c);
            }
        }
        return true;

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityEnderman.d, Optional.empty());
        this.datawatcher.register(EntityEnderman.bz, false);
    }

    public void l() {
        if (this.ticksLived >= this.bB + 400) {
            this.bB = this.ticksLived;
            if (!this.isSilent()) {
                this.world.a(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ, SoundEffects.ENTITY_ENDERMAN_STARE, this.getSoundCategory(), 2.5F, 1.0F, false);
            }
        }

    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderman.bz.equals(datawatcherobject) && this.dX() && this.world.isClientSide) {
            this.l();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            nbttagcompound.set("carriedBlockState", GameProfileSerializer.a(iblockdata));
        }

    }

    @Override
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

    // Paper start - OBFHELPER - ok not really, but verify this on updates
    private boolean f(EntityHuman entityhuman) {
        boolean shouldAttack = f_real(entityhuman);
        com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent event = new com.destroystokyo.paper.event.entity.EndermanAttackPlayerEvent((org.bukkit.entity.Enderman) getBukkitEntity(), (org.bukkit.entity.Player) entityhuman.getBukkitEntity());
        event.setCancelled(!shouldAttack);
        return event.callEvent();
    }
    private boolean f_real(EntityHuman entityhuman) {
        // Paper end
        ItemStack itemstack = (ItemStack) entityhuman.inventory.armor.get(3);

        if (itemstack.getItem() == Blocks.CARVED_PUMPKIN.getItem()) {
            return false;
        } else {
            Vec3D vec3d = entityhuman.f(1.0F).d();
            Vec3D vec3d1 = new Vec3D(this.locX - entityhuman.locX, this.getBoundingBox().minY + (double) this.getHeadHeight() - (entityhuman.locY + (double) entityhuman.getHeadHeight()), this.locZ - entityhuman.locZ);
            double d0 = vec3d1.f();

            vec3d1 = vec3d1.d();
            double d1 = vec3d.b(vec3d1);

            return d1 > 1.0D - 0.025D / d0 ? entityhuman.hasLineOfSight(this) : false;
        }
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 2.55F;
    }

    @Override
    public void movementTick() {
        if (this.world.isClientSide) {
            for (int i = 0; i < 2; ++i) {
                this.world.addParticle(Particles.PORTAL, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), this.locY + this.random.nextDouble() * (double) this.getHeight() - 0.25D, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), (this.random.nextDouble() - 0.5D) * 2.0D, -this.random.nextDouble(), (this.random.nextDouble() - 0.5D) * 2.0D);
            }
        }

        this.jumping = false;
        super.movementTick();
    }

    @Override
    protected void mobTick() {
        if (this.au()) {
            this.damageEntity(DamageSource.DROWN, 1.0F);
        }

        if (this.world.J() && this.ticksLived >= this.bC + 600) {
            float f = this.aF();

            if (f > 0.5F && this.world.f(new BlockPosition(this)) && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.tryEscape(EndermanEscapeEvent.Reason.RUNAWAY)) { // Paper
                this.setGoalTarget((EntityLiving) null);
                this.dV();
            }
        }

        super.mobTick();
    }

    public final boolean teleportRandomly() { return this.dV(); } // Paper - OBFHELPER
    protected boolean dV() {
        double d0 = this.locX + (this.random.nextDouble() - 0.5D) * 64.0D;
        double d1 = this.locY + (double) (this.random.nextInt(64) - 32);
        double d2 = this.locZ + (this.random.nextDouble() - 0.5D) * 64.0D;

        return this.l(d0, d1, d2);
    }

    private boolean a(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX - entity.locX, this.getBoundingBox().minY + (double) (this.getHeight() / 2.0F) - entity.locY + (double) entity.getHeadHeight(), this.locZ - entity.locZ);

        vec3d = vec3d.d();
        double d0 = 16.0D;
        double d1 = this.locX + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
        double d2 = this.locY + (double) (this.random.nextInt(16) - 8) - vec3d.y * 16.0D;
        double d3 = this.locZ + (this.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;

        return this.l(d1, d2, d3);
    }

    private boolean l(double d0, double d1, double d2) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(d0, d1, d2);

        while (blockposition_mutableblockposition.getY() > 0 && !this.world.getType(blockposition_mutableblockposition).getMaterial().isSolid()) {
            blockposition_mutableblockposition.c(EnumDirection.DOWN);
        }

        if (!this.world.getType(blockposition_mutableblockposition).getMaterial().isSolid()) {
            return false;
        } else {
            boolean flag = this.a(d0, d1, d2, true);

            if (flag) {
                this.world.playSound((EntityHuman) null, this.lastX, this.lastY, this.lastZ, SoundEffects.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 1.0F, 1.0F);
                this.a(SoundEffects.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
            }

            return flag;
        }
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.dX() ? SoundEffects.ENTITY_ENDERMAN_SCREAM : SoundEffects.ENTITY_ENDERMAN_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_ENDERMAN_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_ENDERMAN_DEATH;
    }

    @Override
    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {
        super.dropDeathLoot(damagesource, i, flag);
        IBlockData iblockdata = this.getCarried();

        if (iblockdata != null) {
            this.a((IMaterial) iblockdata.getBlock());
        }

    }

    public void setCarried(@Nullable IBlockData iblockdata) {
        this.datawatcher.set(EntityEnderman.d, Optional.ofNullable(iblockdata));
    }

    @Nullable
    public IBlockData getCarried() {
        return (IBlockData) ((Optional) this.datawatcher.get(EntityEnderman.d)).orElse((Object) null);
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!(damagesource instanceof EntityDamageSourceIndirect) && damagesource != DamageSource.FIREWORKS) {
            boolean flag = super.damageEntity(damagesource, f);

            if (damagesource.ignoresArmor() && this.random.nextInt(10) != 0 && this.tryEscape(damagesource == DamageSource.DROWN ? EndermanEscapeEvent.Reason.DROWN : EndermanEscapeEvent.Reason.CRITICAL_HIT)) { // Paper
                this.dV();
            }

            return flag;
        } else {
            if (this.tryEscape(EndermanEscapeEvent.Reason.INDIRECT)) { // Paper start
            for (int i = 0; i < 64; ++i) {
                if (this.dV()) {
                    return true;
                }
            }
            } // Paper end

            return false;
        }
    }

    public boolean dX() {
        return (Boolean) this.datawatcher.get(EntityEnderman.bz);
    }

    static class PathfinderGoalEndermanPickupBlock extends PathfinderGoal {

        private final EntityEnderman enderman;

        public PathfinderGoalEndermanPickupBlock(EntityEnderman entityenderman) {
            this.enderman = entityenderman;
        }

        @Override
        public boolean a() {
            return this.enderman.getCarried() != null ? false : (!this.enderman.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? false : this.enderman.getRandom().nextInt(20) == 0);
        }

        @Override
        public void e() {
            Random random = this.enderman.getRandom();
            World world = this.enderman.world;
            int i = MathHelper.floor(this.enderman.locX - 2.0D + random.nextDouble() * 4.0D);
            int j = MathHelper.floor(this.enderman.locY + random.nextDouble() * 3.0D);
            int k = MathHelper.floor(this.enderman.locZ - 2.0D + random.nextDouble() * 4.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getTypeIfLoaded(blockposition); // Paper
            if (iblockdata == null) return; // Paper
            Block block = iblockdata.getBlock();
            Vec3D vec3d = new Vec3D((double) MathHelper.floor(this.enderman.locX) + 0.5D, (double) j + 0.5D, (double) MathHelper.floor(this.enderman.locZ) + 0.5D);
            Vec3D vec3d1 = new Vec3D((double) i + 0.5D, (double) j + 0.5D, (double) k + 0.5D);
            MovingObjectPositionBlock movingobjectpositionblock = world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this.enderman));
            boolean flag = movingobjectpositionblock.getType() != MovingObjectPosition.EnumMovingObjectType.MISS && movingobjectpositionblock.getBlockPosition().equals(blockposition);

            if (block.a(TagsBlock.ENDERMAN_HOLDABLE) && flag) {
                // CraftBukkit start - Pickup event
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.enderman, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                    //this.enderman.setCarried(iblockdata); // Paper - moved down
                    world.a(blockposition, false);
                    this.enderman.setCarried(Block.getValidBlockForPosition(iblockdata, this.enderman.world, blockposition)); // Paper - Fix MC-124320
                }
                // CraftBukkit end
            }

        }
    }

    static class PathfinderGoalEndermanPlaceBlock extends PathfinderGoal {

        private EntityEnderman getEnderman() { return this.a; } // Paper - OBFHELPER
        private final EntityEnderman a;

        public PathfinderGoalEndermanPlaceBlock(EntityEnderman entityenderman) {
            this.a = entityenderman;
        }

        @Override
        public boolean a() {
            return this.a.getCarried() == null ? false : (!this.a.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING) ? false : this.a.getRandom().nextInt(2000) == 0);
        }

        @Override
        public void e() {
            Random random = this.a.getRandom();
            World world = this.a.world;
            int i = MathHelper.floor(this.a.locX - 1.0D + random.nextDouble() * 2.0D);
            int j = MathHelper.floor(this.a.locY + random.nextDouble() * 2.0D);
            int k = MathHelper.floor(this.a.locZ - 1.0D + random.nextDouble() * 2.0D);
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = world.getTypeIfLoaded(blockposition); // Paper
            if (iblockdata == null) return; // Paper
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = world.getType(blockposition1);
            IBlockData iblockdata2 = Block.getValidBlockForPosition(getEnderman().getCarried(), getEnderman().world, blockposition); // Paper - Fix MC-124320

            if (iblockdata2 != null && this.a(world, blockposition, iblockdata2, iblockdata, iblockdata1, blockposition1)) {
                // CraftBukkit start - Place event
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.a, blockposition, iblockdata2).isCancelled()) {
                world.setTypeAndData(blockposition, iblockdata2, 3);
                this.a.setCarried((IBlockData) null);
                }
                // CraftBukkit end
            }

        }

        private boolean a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, IBlockData iblockdata2, BlockPosition blockposition1) {
            return iblockdata1.isAir() && !iblockdata2.isAir() && iblockdata2.o(iworldreader, blockposition1) && iblockdata.canPlace(iworldreader, blockposition);
        }
    }

    static class a extends PathfinderGoal {

        private final EntityEnderman a;

        public a(EntityEnderman entityenderman) {
            this.a = entityenderman;
            this.a(EnumSet.of(PathfinderGoal.Type.JUMP, PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            EntityLiving entityliving = this.a.getGoalTarget();

            if (!(entityliving instanceof EntityHuman)) {
                return false;
            } else {
                double d0 = entityliving.h((Entity) this.a);

                return d0 > 256.0D ? false : this.a.f((EntityHuman) entityliving);
            }
        }

        @Override
        public void c() {
            this.a.getNavigation().o();
        }
    }

    static class PathfinderGoalPlayerWhoLookedAtTarget extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        private final EntityEnderman i; public final EntityEnderman getEnderman() { return this.i; } // Paper - OBFHELPER
        private EntityHuman j;
        private int k;
        private int l;
        private final PathfinderTargetCondition m;
        private final PathfinderTargetCondition n = (new PathfinderTargetCondition()).c();

        public PathfinderGoalPlayerWhoLookedAtTarget(EntityEnderman entityenderman) {
            super(entityenderman, EntityHuman.class, false);
            this.i = entityenderman;
            this.m = (new PathfinderTargetCondition()).a(this.k()).a((entityliving) -> {
                return entityenderman.f((EntityHuman) entityliving);
            });
        }

        @Override
        public boolean a() {
            this.j = this.i.world.a(this.m, (EntityLiving) this.i);
            return this.j != null;
        }

        @Override
        public void c() {
            this.k = 5;
            this.l = 0;
        }

        @Override
        public void d() {
            this.j = null;
            super.d();
        }

        @Override
        public boolean b() {
            if (this.j != null) {
                if (!this.i.f(this.j)) {
                    return false;
                } else {
                    this.i.a((Entity) this.j, 10.0F, 10.0F);
                    return true;
                }
            } else {
                return this.c != null && this.n.a(this.i, this.c) ? true : super.b();
            }
        }

        @Override
        public void e() {
            if (this.j != null) {
                if (--this.k <= 0) {
                    this.c = this.j;
                    this.j = null;
                    super.c();
                }
            } else {
                if (this.c != null && !this.i.isPassenger()) {
                    if (this.i.f((EntityHuman) this.c)) {
                        if (this.c.h((Entity) this.i) < 16.0D && this.getEnderman().tryEscape(EndermanEscapeEvent.Reason.STARE)) {
                            this.i.dV();
                        }

                        this.l = 0;
                    } else if (this.c.h((Entity) this.i) > 256.0D && this.l++ >= 30 && this.i.a((Entity) this.c)) {
                        this.l = 0;
                    }
                }

                super.e();
            }

        }
    }
}
