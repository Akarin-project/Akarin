package net.minecraft.server;

public class EntityIronGolem extends EntityGolem {

    protected static final DataWatcherObject<Byte> b = DataWatcher.a(EntityIronGolem.class, DataWatcherRegistry.a);
    private int c;
    private int d;

    public EntityIronGolem(EntityTypes<? extends EntityIronGolem> entitytypes, World world) {
        super(entitytypes, world);
        this.K = 1.0F;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(1, new PathfinderGoalMeleeAttack(this, 1.0D, true));
        this.goalSelector.a(2, new PathfinderGoalMoveTowardsTarget(this, 0.9D, 32.0F));
        this.goalSelector.a(2, new PathfinderGoalStrollVillage(this, 0.6D));
        this.goalSelector.a(3, new PathfinderGoalMoveThroughVillage(this, 0.6D, false, 4, () -> {
            return false;
        }));
        this.goalSelector.a(5, new PathfinderGoalOfferFlower(this));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.6D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalDefendVillage(this));
        this.targetSelector.a(2, new PathfinderGoalHurtByTarget(this, new Class[0]));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 5, false, false, (entityliving) -> {
            return entityliving instanceof IMonster && !(entityliving instanceof EntityCreeper);
        }));
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityIronGolem.b, (byte) 0);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(100.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).setValue(1.0D);
    }

    @Override
    protected int l(int i) {
        return i;
    }

    @Override
    protected void D(Entity entity) {
        if (entity instanceof IMonster && !(entity instanceof EntityCreeper) && this.getRandom().nextInt(20) == 0) {
            this.setGoalTarget((EntityLiving) entity, org.bukkit.event.entity.EntityTargetLivingEntityEvent.TargetReason.COLLISION, true); // CraftBukkit - set reason
        }

        super.D(entity);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.c > 0) {
            --this.c;
        }

        if (this.d > 0) {
            --this.d;
        }

        if (b(this.getMot()) > 2.500000277905201E-7D && this.random.nextInt(5) == 0) {
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY - 0.20000000298023224D);
            int k = MathHelper.floor(this.locZ);
            IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));

            if (!iblockdata.isAir()) {
                this.world.addParticle(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX + ((double) this.random.nextFloat() - 0.5D) * (double) this.getWidth(), this.getBoundingBox().minY + 0.1D, this.locZ + ((double) this.random.nextFloat() - 0.5D) * (double) this.getWidth(), 4.0D * ((double) this.random.nextFloat() - 0.5D), 0.5D, ((double) this.random.nextFloat() - 0.5D) * 4.0D);
            }
        }

    }

    @Override
    public boolean a(EntityTypes<?> entitytypes) {
        return this.isPlayerCreated() && entitytypes == EntityTypes.PLAYER ? false : (entitytypes == EntityTypes.CREEPER ? false : super.a(entitytypes));
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("PlayerCreated", this.isPlayerCreated());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setPlayerCreated(nbttagcompound.getBoolean("PlayerCreated"));
    }

    @Override
    public boolean C(Entity entity) {
        this.c = 10;
        this.world.broadcastEntityEffect(this, (byte) 4);
        boolean flag = entity.damageEntity(DamageSource.mobAttack(this), (float) (7 + this.random.nextInt(15)));

        if (flag) {
            entity.setMot(entity.getMot().add(0.0D, 0.4000000059604645D, 0.0D));
            this.a((EntityLiving) this, entity);
        }

        this.a(SoundEffects.ENTITY_IRON_GOLEM_ATTACK, 1.0F, 1.0F);
        return flag;
    }

    public void r(boolean flag) {
        if (flag) {
            this.d = 400;
            this.world.broadcastEntityEffect(this, (byte) 11);
        } else {
            this.d = 0;
            this.world.broadcastEntityEffect(this, (byte) 34);
        }

    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_IRON_GOLEM_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_IRON_GOLEM_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_IRON_GOLEM_STEP, 1.0F, 1.0F);
    }

    public boolean isPlayerCreated() {
        return ((Byte) this.datawatcher.get(EntityIronGolem.b) & 1) != 0;
    }

    public void setPlayerCreated(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityIronGolem.b);

        if (flag) {
            this.datawatcher.set(EntityIronGolem.b, (byte) (b0 | 1));
        } else {
            this.datawatcher.set(EntityIronGolem.b, (byte) (b0 & -2));
        }

    }

    @Override
    public void die(DamageSource damagesource) {
        super.die(damagesource);
    }

    @Override
    public boolean a(IWorldReader iworldreader) {
        BlockPosition blockposition = new BlockPosition(this);
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = iworldreader.getType(blockposition1);

        if (!iblockdata.a((IBlockAccess) iworldreader, blockposition1, (Entity) this)) {
            return false;
        } else {
            for (int i = 1; i < 3; ++i) {
                BlockPosition blockposition2 = blockposition.up(i);
                IBlockData iblockdata1 = iworldreader.getType(blockposition2);

                if (!SpawnerCreature.a((IBlockAccess) iworldreader, blockposition2, iblockdata1, iblockdata1.p())) {
                    return false;
                }
            }

            return SpawnerCreature.a((IBlockAccess) iworldreader, blockposition, iworldreader.getType(blockposition), FluidTypes.EMPTY.i()) && iworldreader.i(this);
        }
    }
}
