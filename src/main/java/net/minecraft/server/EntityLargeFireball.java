package net.minecraft.server;

import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityLargeFireball extends EntityFireball {

    public int yield = 1;

    public EntityLargeFireball(World world) {
        super(EntityTypes.FIREBALL, world, 1.0F, 1.0F);
        isIncendiary = this.world.getGameRules().getBoolean("mobGriefing"); // CraftBukkit
    }

    public EntityLargeFireball(World world, EntityLiving entityliving, double d0, double d1, double d2) {
        super(EntityTypes.FIREBALL, entityliving, d0, d1, d2, world, 1.0F, 1.0F);
        isIncendiary = this.world.getGameRules().getBoolean("mobGriefing"); // CraftBukkit
    }

    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            if (movingobjectposition.entity != null) {
                movingobjectposition.entity.damageEntity(DamageSource.fireball(this, this.shooter), 6.0F);
                this.a(this.shooter, movingobjectposition.entity);
            }

            boolean flag = this.world.getGameRules().getBoolean("mobGriefing");

            // CraftBukkit start - fire ExplosionPrimeEvent
            ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) this.getBukkitEntity());
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                // give 'this' instead of (Entity) null so we know what causes the damage
                this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius(), event.getFire(), isIncendiary);
            }
            // CraftBukkit end
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
            // CraftBukkit - set bukkitYield when setting explosionpower
            bukkitYield = this.yield = nbttagcompound.getInt("ExplosionPower");
        }

    }
}
