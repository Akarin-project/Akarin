package net.minecraft.server;

public class EntityLargeFireball extends EntityFireball {

    public int yield = 1;

    public EntityLargeFireball(World world) {
        super(EntityTypes.FIREBALL, world, 1.0F, 1.0F);
    }

    public EntityLargeFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(EntityTypes.FIREBALL, entityliving, d0, d1, d2, world, 1.0F, 1.0F);
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            if (movingobjectposition.entity != null) {
                movingobjectposition.entity.damageEntity(DamageSource.fireball(this, this.shooter), 6.0F);
                this.a(this.shooter, movingobjectposition.entity);
            }

            boolean flag = this.world.getGameRules().getBoolean("mobGriefing");

            this.world.createExplosion((Entity) null, this.locX, this.locY, this.locZ, (float) this.yield, flag, flag);
            this.die();
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("ExplosionPower", this.yield);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("ExplosionPower", 99)) {
            this.yield = nbttagcompound.getInt("ExplosionPower");
        }

    }
}
