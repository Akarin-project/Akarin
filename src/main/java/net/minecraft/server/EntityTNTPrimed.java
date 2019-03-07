package net.minecraft.server;

import javax.annotation.Nullable;
import org.bukkit.event.entity.ExplosionPrimeEvent; // CraftBukkit

public class EntityTNTPrimed extends Entity {

    private static final DataWatcherObject<Integer> FUSE_TICKS = DataWatcher.a(EntityTNTPrimed.class, DataWatcherRegistry.b);
    @Nullable
    private EntityLiving source;
    private int c;
    public float yield = 4; // CraftBukkit - add field
    public boolean isIncendiary = false; // CraftBukkit - add field

    public EntityTNTPrimed(World world) {
        super(EntityTypes.TNT, world);
        this.c = 80;
        this.j = true;
        this.fireProof = true;
        this.setSize(0.98F, 0.98F);
    }

    public EntityTNTPrimed(World world, double d0, double d1, double d2, @Nullable EntityLiving entityliving) {
        this(world);
        this.setPosition(d0, d1, d2);
        float f = (float) (Math.random() * 6.2831854820251465D);

        this.motX = (double) (-((float) Math.sin((double) f)) * 0.02F);
        this.motY = 0.20000000298023224D;
        this.motZ = (double) (-((float) Math.cos((double) f)) * 0.02F);
        this.setFuseTicks(80);
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
        this.source = entityliving;
    }

    protected void x_() {
        this.datawatcher.register(EntityTNTPrimed.FUSE_TICKS, 80);
    }

    protected boolean playStepSound() {
        return false;
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public void tick() {
        if (world.spigotConfig.currentPrimedTnt++ > world.spigotConfig.maxTntTicksPerTick) { return; } // Spigot
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        if (!this.isNoGravity()) {
            this.motY -= 0.03999999910593033D;
        }

        this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);

        // Paper start - Configurable TNT entity height nerf
        if (this.world.paperConfig.entityTNTHeightNerf != 0 && this.locY > this.world.paperConfig.entityTNTHeightNerf) {
            this.die();
        }
        // Paper end

        this.motX *= 0.9800000190734863D;
        this.motY *= 0.9800000190734863D;
        this.motZ *= 0.9800000190734863D;
        if (this.onGround) {
            this.motX *= 0.699999988079071D;
            this.motZ *= 0.699999988079071D;
            this.motY *= -0.5D;
        }

        --this.c;
        if (this.c <= 0) {
            // CraftBukkit start - Need to reverse the order of the explosion and the entity death so we have a location for the event
            // this.die();
            if (!this.world.isClientSide) {
                this.explode();
            }
            this.die();
            // CraftBukkit end
        } else {
            this.at();
            this.world.addParticle(Particles.M, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }

    }

    private void explode() {
        // CraftBukkit start
        // float f = 4.0F;

        ExplosionPrimeEvent event = new ExplosionPrimeEvent((org.bukkit.entity.Explosive) this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            this.world.createExplosion(this, this.locX, this.locY + (double) (this.length / 16.0F), this.locZ, event.getRadius(), event.getFire(), true);
        }
        // CraftBukkit end
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Fuse", (short) this.getFuseTicks());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.setFuseTicks(nbttagcompound.getShort("Fuse"));
        // Paper start - Try and load origin location from the old NBT tags for backwards compatibility
        if (nbttagcompound.hasKey("SourceLoc_x")) {
            int srcX = nbttagcompound.getInt("SourceLoc_x");
            int srcY = nbttagcompound.getInt("SourceLoc_y");
            int srcZ = nbttagcompound.getInt("SourceLoc_z");
            origin = new org.bukkit.Location(world.getWorld(), srcX, srcY, srcZ);
        }
        // Paper end
    }

    @Nullable
    public EntityLiving getSource() {
        return this.source;
    }

    public float getHeadHeight() {
        return 0.0F;
    }

    public void setFuseTicks(int i) {
        this.datawatcher.set(EntityTNTPrimed.FUSE_TICKS, i);
        this.c = i;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityTNTPrimed.FUSE_TICKS.equals(datawatcherobject)) {
            this.c = this.i();
        }

    }

    public int i() {
        return (Integer) this.datawatcher.get(EntityTNTPrimed.FUSE_TICKS);
    }

    public int getFuseTicks() {
        return this.c;
    }

    // Paper start - Optional prevent TNT from moving in water
    @Override
    public boolean pushedByWater() {
        return !world.paperConfig.preventTntFromMovingInWater && super.pushedByWater();
    }

    /**
     * Author: Jedediah Smith <jedediah@silencegreys.com>
     */
    @Override
    public boolean doWaterMovement() {
        if (!world.paperConfig.preventTntFromMovingInWater) return super.doWaterMovement();

        // Preserve velocity while calling the super method
        double oldMotX = this.motX;
        double oldMotY = this.motY;
        double oldMotZ = this.motZ;

        super.doWaterMovement();

        this.motX = oldMotX;
        this.motY = oldMotY;
        this.motZ = oldMotZ;

        if (this.inWater) {
            // Send position and velocity updates to nearby players on every tick while the TNT is in water.
            // This does pretty well at keeping their clients in sync with the server.
            EntityTrackerEntry ete = ((WorldServer) this.getWorld()).getTracker().trackedEntities.get(this.getId());
            if (ete != null) {
                PacketPlayOutEntityVelocity velocityPacket = new PacketPlayOutEntityVelocity(this);
                PacketPlayOutEntityTeleport positionPacket = new PacketPlayOutEntityTeleport(this);

                ete.trackedPlayers.stream()
                        .filter(viewer -> (viewer.locX - this.locX) * (viewer.locY - this.locY) * (viewer.locZ - this.locZ) < 16 * 16)
                        .forEach(viewer -> {
                    viewer.playerConnection.sendPacket(velocityPacket);
                    viewer.playerConnection.sendPacket(positionPacket);
                });
            }
        }

        return this.inWater;
    }
    // Paper end
}
