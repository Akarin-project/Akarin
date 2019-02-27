package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityEnderPearl extends EntityProjectile {

    private EntityLiving e;

    public EntityEnderPearl(World world) {
        super(EntityTypes.ENDER_PEARL, world);
    }

    public EntityEnderPearl(World world, EntityLiving entityliving) {
        super(EntityTypes.ENDER_PEARL, entityliving, world);
        this.e = entityliving;
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        EntityLiving entityliving = this.getShooter();

        if (movingobjectposition.entity != null) {
            if (movingobjectposition.entity == this.e) {
                return;
            }

            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, entityliving), 0.0F);
        }

        if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            BlockPosition blockposition = movingobjectposition.getBlockPosition();
            TileEntity tileentity = this.world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityEndGateway) {
                TileEntityEndGateway tileentityendgateway = (TileEntityEndGateway) tileentity;

                if (entityliving != null) {
                    if (entityliving instanceof EntityPlayer) {
                        CriterionTriggers.d.a((EntityPlayer) entityliving, this.world.getType(blockposition));
                    }

                    tileentityendgateway.a((Entity) entityliving);
                    this.die();
                    return;
                }

                tileentityendgateway.a((Entity) this);
                return;
            }
        }

        for (int i = 0; i < 32; ++i) {
            this.world.addParticle(Particles.K, this.locX, this.locY + this.random.nextDouble() * 2.0D, this.locZ, this.random.nextGaussian(), 0.0D, this.random.nextGaussian());
        }

        if (!this.world.isClientSide) {
            if (entityliving instanceof EntityPlayer) {
                EntityPlayer entityplayer = (EntityPlayer) entityliving;

                if (entityplayer.playerConnection.a().isConnected() && entityplayer.world == this.world && !entityplayer.isSleeping()) {
                    if (this.random.nextFloat() < 0.05F && this.world.getGameRules().getBoolean("doMobSpawning")) {
                        EntityEndermite entityendermite = new EntityEndermite(this.world);

                        entityendermite.setPlayerSpawned(true);
                        entityendermite.setPositionRotation(entityliving.locX, entityliving.locY, entityliving.locZ, entityliving.yaw, entityliving.pitch);
                        this.world.addEntity(entityendermite);
                    }

                    if (entityliving.isPassenger()) {
                        entityliving.stopRiding();
                    }

                    entityliving.enderTeleportTo(this.locX, this.locY, this.locZ);
                    entityliving.fallDistance = 0.0F;
                    entityliving.damageEntity(DamageSource.FALL, 5.0F);
                }
            } else if (entityliving != null) {
                entityliving.enderTeleportTo(this.locX, this.locY, this.locZ);
                entityliving.fallDistance = 0.0F;
            }

            this.die();
        }

    }

    public void tick() {
        EntityLiving entityliving = this.getShooter();

        if (entityliving != null && entityliving instanceof EntityHuman && !entityliving.isAlive()) {
            this.die();
        } else {
            super.tick();
        }

    }

    @Nullable
    public Entity a(DimensionManager dimensionmanager) {
        if (this.shooter.dimension != dimensionmanager) {
            this.shooter = null;
        }

        return super.a(dimensionmanager);
    }
}
