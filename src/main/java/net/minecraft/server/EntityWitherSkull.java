package net.minecraft.server;

public class EntityWitherSkull extends EntityFireball {

    private static final DataWatcherObject<Boolean> e = DataWatcher.a(EntityWitherSkull.class, DataWatcherRegistry.i);

    public EntityWitherSkull(World world) {
        super(EntityTypes.WITHER_SKULL, world, 0.3125F, 0.3125F);
    }

    public EntityWitherSkull(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(EntityTypes.WITHER_SKULL, entityliving, d0, d1, d2, world, 0.3125F, 0.3125F);
    }

    protected float k() {
        return this.isCharged() ? 0.73F : super.k();
    }

    public boolean isBurning() {
        return false;
    }

    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return this.isCharged() && EntityWither.a(iblockdata.getBlock()) ? Math.min(0.8F, f) : f;
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            if (movingobjectposition.entity != null) {
                if (this.shooter != null) {
                    if (movingobjectposition.entity.damageEntity(DamageSource.mobAttack(this.shooter), 8.0F)) {
                        if (movingobjectposition.entity.isAlive()) {
                            this.a(this.shooter, movingobjectposition.entity);
                        } else {
                            this.shooter.heal(5.0F);
                        }
                    }
                } else {
                    movingobjectposition.entity.damageEntity(DamageSource.MAGIC, 5.0F);
                }

                if (movingobjectposition.entity instanceof EntityLiving) {
                    byte b0 = 0;

                    if (this.world.getDifficulty() == EnumDifficulty.NORMAL) {
                        b0 = 10;
                    } else if (this.world.getDifficulty() == EnumDifficulty.HARD) {
                        b0 = 40;
                    }

                    if (b0 > 0) {
                        ((EntityLiving) movingobjectposition.entity).addEffect(new MobEffect(MobEffects.WITHER, 20 * b0, 1));
                    }
                }
            }

            this.world.createExplosion(this, this.locX, this.locY, this.locZ, 1.0F, false, this.world.getGameRules().getBoolean("mobGriefing"));
            this.die();
        }

    }

    public boolean isInteractable() {
        return false;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        return false;
    }

    protected void x_() {
        this.datawatcher.register(EntityWitherSkull.e, false);
    }

    public boolean isCharged() {
        return (Boolean) this.datawatcher.get(EntityWitherSkull.e);
    }

    public void setCharged(boolean flag) {
        this.datawatcher.set(EntityWitherSkull.e, flag);
    }

    protected boolean f() {
        return false;
    }
}
