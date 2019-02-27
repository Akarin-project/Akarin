package net.minecraft.server;

import javax.annotation.Nullable;

public class EntitySnowman extends EntityGolem implements IRangedEntity {

    private static final DataWatcherObject<Byte> a = DataWatcher.a(EntitySnowman.class, DataWatcherRegistry.a);

    public EntitySnowman(World world) {
        super(EntityTypes.SNOW_GOLEM, world);
        this.setSize(0.7F, 1.9F);
    }

    protected void n() {
        this.goalSelector.a(1, new PathfinderGoalArrowAttack(this, 1.25D, 20, 10.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomStrollLand(this, 1.0D, 1.0000001E-5F));
        this.goalSelector.a(3, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(4, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalNearestAttackableTarget<>(this, EntityInsentient.class, 10, true, false, IMonster.d));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(4.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntitySnowman.a, (byte) 16);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Pumpkin", this.hasPumpkin());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("Pumpkin")) {
            this.setHasPumpkin(nbttagcompound.getBoolean("Pumpkin"));
        }

    }

    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide) {
            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY);
            int k = MathHelper.floor(this.locZ);

            if (this.ap()) {
                this.damageEntity(DamageSource.DROWN, 1.0F);
            }

            if (this.world.getBiome(new BlockPosition(i, 0, k)).getAdjustedTemperature(new BlockPosition(i, j, k)) > 1.0F) {
                this.damageEntity(DamageSource.BURN, 1.0F);
            }

            if (!this.world.getGameRules().getBoolean("mobGriefing")) {
                return;
            }

            IBlockData iblockdata = Blocks.SNOW.getBlockData();

            for (int l = 0; l < 4; ++l) {
                i = MathHelper.floor(this.locX + (double) ((float) (l % 2 * 2 - 1) * 0.25F));
                j = MathHelper.floor(this.locY);
                k = MathHelper.floor(this.locZ + (double) ((float) (l / 2 % 2 * 2 - 1) * 0.25F));
                BlockPosition blockposition = new BlockPosition(i, j, k);

                if (this.world.getType(blockposition).isAir() && this.world.getBiome(blockposition).getAdjustedTemperature(blockposition) < 0.8F && iblockdata.canPlace(this.world, blockposition)) {
                    this.world.setTypeUpdate(blockposition, iblockdata);
                }
            }
        }

    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.H;
    }

    public void a(EntityLiving entityliving, float f) {
        EntitySnowball entitysnowball = new EntitySnowball(this.world, this);
        double d0 = entityliving.locY + (double) entityliving.getHeadHeight() - 1.100000023841858D;
        double d1 = entityliving.locX - this.locX;
        double d2 = d0 - entitysnowball.locY;
        double d3 = entityliving.locZ - this.locZ;
        float f1 = MathHelper.sqrt(d1 * d1 + d3 * d3) * 0.2F;

        entitysnowball.shoot(d1, d2 + (double) f1, d3, 1.6F, 12.0F);
        this.a(SoundEffects.ENTITY_SNOW_GOLEM_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(entitysnowball);
    }

    public float getHeadHeight() {
        return 1.7F;
    }

    protected boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.SHEARS && this.hasPumpkin() && !this.world.isClientSide) {
            this.setHasPumpkin(false);
            itemstack.damage(1, entityhuman);
        }

        return super.a(entityhuman, enumhand);
    }

    public boolean hasPumpkin() {
        return ((Byte) this.datawatcher.get(EntitySnowman.a) & 16) != 0;
    }

    public void setHasPumpkin(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntitySnowman.a);

        if (flag) {
            this.datawatcher.set(EntitySnowman.a, (byte) (b0 | 16));
        } else {
            this.datawatcher.set(EntitySnowman.a, (byte) (b0 & -17));
        }

    }

    @Nullable
    protected SoundEffect D() {
        return SoundEffects.ENTITY_SNOW_GOLEM_AMBIENT;
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_SNOW_GOLEM_HURT;
    }

    @Nullable
    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SNOW_GOLEM_DEATH;
    }

    public void s(boolean flag) {}
}
