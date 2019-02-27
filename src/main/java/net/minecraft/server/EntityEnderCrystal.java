package net.minecraft.server;

import java.util.Optional;
import javax.annotation.Nullable;

public class EntityEnderCrystal extends Entity {

    private static final DataWatcherObject<Optional<BlockPosition>> b = DataWatcher.a(EntityEnderCrystal.class, DataWatcherRegistry.m);
    private static final DataWatcherObject<Boolean> c = DataWatcher.a(EntityEnderCrystal.class, DataWatcherRegistry.i);
    public int a;

    public EntityEnderCrystal(World world) {
        super(EntityTypes.END_CRYSTAL, world);
        this.j = true;
        this.setSize(2.0F, 2.0F);
        this.a = this.random.nextInt(100000);
    }

    public EntityEnderCrystal(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void x_() {
        this.getDataWatcher().register(EntityEnderCrystal.b, Optional.empty());
        this.getDataWatcher().register(EntityEnderCrystal.c, true);
    }

    public void tick() {
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        ++this.a;
        if (!this.world.isClientSide) {
            BlockPosition blockposition = new BlockPosition(this);

            if (this.world.worldProvider instanceof WorldProviderTheEnd && this.world.getType(blockposition).isAir()) {
                this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
            }
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        if (this.getBeamTarget() != null) {
            nbttagcompound.set("BeamTarget", GameProfileSerializer.a(this.getBeamTarget()));
        }

        nbttagcompound.setBoolean("ShowBottom", this.isShowingBottom());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("BeamTarget", 10)) {
            this.setBeamTarget(GameProfileSerializer.c(nbttagcompound.getCompound("BeamTarget")));
        }

        if (nbttagcompound.hasKeyOfType("ShowBottom", 1)) {
            this.setShowingBottom(nbttagcompound.getBoolean("ShowBottom"));
        }

    }

    public boolean isInteractable() {
        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource.getEntity() instanceof EntityEnderDragon) {
            return false;
        } else {
            if (!this.dead && !this.world.isClientSide) {
                this.die();
                if (!this.world.isClientSide) {
                    if (!damagesource.isExplosion()) {
                        this.world.explode((Entity) null, this.locX, this.locY, this.locZ, 6.0F, true);
                    }

                    this.a(damagesource);
                }
            }

            return true;
        }
    }

    public void killEntity() {
        this.a(DamageSource.GENERIC);
        super.killEntity();
    }

    private void a(DamageSource damagesource) {
        if (this.world.worldProvider instanceof WorldProviderTheEnd) {
            WorldProviderTheEnd worldprovidertheend = (WorldProviderTheEnd) this.world.worldProvider;
            EnderDragonBattle enderdragonbattle = worldprovidertheend.r();

            if (enderdragonbattle != null) {
                enderdragonbattle.a(this, damagesource);
            }
        }

    }

    public void setBeamTarget(@Nullable BlockPosition blockposition) {
        this.getDataWatcher().set(EntityEnderCrystal.b, Optional.ofNullable(blockposition));
    }

    @Nullable
    public BlockPosition getBeamTarget() {
        return (BlockPosition) ((Optional) this.getDataWatcher().get(EntityEnderCrystal.b)).orElse((Object) null);
    }

    public void setShowingBottom(boolean flag) {
        this.getDataWatcher().set(EntityEnderCrystal.c, flag);
    }

    public boolean isShowingBottom() {
        return (Boolean) this.getDataWatcher().get(EntityEnderCrystal.c);
    }
}
