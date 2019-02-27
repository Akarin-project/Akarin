package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityTNTPrimed extends Entity {

    private static final DataWatcherObject<Integer> FUSE_TICKS = DataWatcher.a(EntityTNTPrimed.class, DataWatcherRegistry.b);
    @Nullable
    private EntityLiving source;
    private int c;

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
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        if (!this.isNoGravity()) {
            this.motY -= 0.03999999910593033D;
        }

        this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
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
            this.die();
            if (!this.world.isClientSide) {
                this.explode();
            }
        } else {
            this.at();
            this.world.addParticle(Particles.M, this.locX, this.locY + 0.5D, this.locZ, 0.0D, 0.0D, 0.0D);
        }

    }

    private void explode() {
        float f = 4.0F;

        this.world.explode(this, this.locX, this.locY + (double) (this.length / 16.0F), this.locZ, 4.0F, true);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Fuse", (short) this.getFuseTicks());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.setFuseTicks(nbttagcompound.getShort("Fuse"));
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
}
