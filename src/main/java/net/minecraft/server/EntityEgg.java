package net.minecraft.server;

public class EntityEgg extends EntityProjectile {

    public EntityEgg(World world) {
        super(EntityTypes.EGG, world);
    }

    public EntityEgg(World world, EntityLiving entityliving) {
        super(EntityTypes.EGG, entityliving, world);
    }

    public EntityEgg(World world, double d0, double d1, double d2) {
        super(EntityTypes.EGG, d0, d1, d2, world);
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (movingobjectposition.entity != null) {
            movingobjectposition.entity.damageEntity(DamageSource.projectile(this, this.getShooter()), 0.0F);
        }

        if (!this.world.isClientSide) {
            if (this.random.nextInt(8) == 0) {
                byte b0 = 1;

                if (this.random.nextInt(32) == 0) {
                    b0 = 4;
                }

                for (int i = 0; i < b0; ++i) {
                    EntityChicken entitychicken = new EntityChicken(this.world);

                    entitychicken.setAgeRaw(-24000);
                    entitychicken.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                    this.world.addEntity(entitychicken);
                }
            }

            this.world.broadcastEntityEffect(this, (byte) 3);
            this.die();
        }

    }
}
