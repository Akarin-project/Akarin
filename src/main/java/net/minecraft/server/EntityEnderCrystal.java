package net.minecraft.server;

import java.util.Optional;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.ExplosionPrimeEvent;
// CraftBukkit end

public class EntityEnderCrystal extends Entity {

    private static final DataWatcherObject<Optional<BlockPosition>> c = DataWatcher.a(EntityEnderCrystal.class, DataWatcherRegistry.m);
    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityEnderCrystal.class, DataWatcherRegistry.i);
    public int b;

    public EntityEnderCrystal(EntityTypes<? extends EntityEnderCrystal> entitytypes, World world) {
        super(entitytypes, world);
        this.i = true;
        this.b = this.random.nextInt(100000);
    }

    public EntityEnderCrystal(World world, double d0, double d1, double d2) {
        this(EntityTypes.END_CRYSTAL, world);
        this.setPosition(d0, d1, d2);
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityEnderCrystal.c, Optional.empty());
        this.getDataWatcher().register(EntityEnderCrystal.d, true);
    }

    @Override
    public void tick() {
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        ++this.b;
        if (!this.world.isClientSide) {
            BlockPosition blockposition = new BlockPosition(this);

            if (this.world.worldProvider instanceof WorldProviderTheEnd && this.world.getType(blockposition).isAir()) {
                // CraftBukkit start
                if (!CraftEventFactory.callBlockIgniteEvent(this.world, blockposition, this).isCancelled()) {
                    this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                }
                // CraftBukkit end
            }
        }

    }

    @Override
    protected void b(NBTTagCompound nbttagcompound) {
        if (this.getBeamTarget() != null) {
            nbttagcompound.set("BeamTarget", GameProfileSerializer.a(this.getBeamTarget()));
        }

        nbttagcompound.setBoolean("ShowBottom", this.isShowingBottom());
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("BeamTarget", 10)) {
            this.setBeamTarget(GameProfileSerializer.c(nbttagcompound.getCompound("BeamTarget")));
        }

        if (nbttagcompound.hasKeyOfType("ShowBottom", 1)) {
            this.setShowingBottom(nbttagcompound.getBoolean("ShowBottom"));
        }

    }

    @Override
    public boolean isInteractable() {
        return true;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (damagesource.getEntity() instanceof EntityEnderDragon) {
            return false;
        } else {
            if (!this.dead && !this.world.isClientSide) {
                // CraftBukkit start - All non-living entities need this
                if (CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f)) {
                    return false;
                }
                // CraftBukkit end
                this.die();
                if (!damagesource.isExplosion()) {
                    // CraftBukkit start
                    ExplosionPrimeEvent event = new ExplosionPrimeEvent(this.getBukkitEntity(), 6.0F, false);
                    this.world.getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()) {
                        this.dead = false;
                        return false;
                    }
                    this.world.createExplosion(this, this.locX, this.locY, this.locZ, event.getRadius(), event.getFire(), Explosion.Effect.DESTROY);
                    // CraftBukkit end
                }

                this.a(damagesource);
            }

            return true;
        }
    }

    @Override
    public void killEntity() {
        this.a(DamageSource.GENERIC);
        super.killEntity();
    }

    private void a(DamageSource damagesource) {
        if (this.world.worldProvider instanceof WorldProviderTheEnd) {
            WorldProviderTheEnd worldprovidertheend = (WorldProviderTheEnd) this.world.worldProvider;
            EnderDragonBattle enderdragonbattle = worldprovidertheend.q();

            if (enderdragonbattle != null) {
                enderdragonbattle.a(this, damagesource);
            }
        }

    }

    public void setBeamTarget(@Nullable BlockPosition blockposition) {
        this.getDataWatcher().set(EntityEnderCrystal.c, Optional.ofNullable(blockposition));
    }

    @Nullable
    public BlockPosition getBeamTarget() {
        return (BlockPosition) ((Optional) this.getDataWatcher().get(EntityEnderCrystal.c)).orElse((Object) null);
    }

    public void setShowingBottom(boolean flag) {
        this.getDataWatcher().set(EntityEnderCrystal.d, flag);
    }

    public boolean isShowingBottom() {
        return (Boolean) this.getDataWatcher().get(EntityEnderCrystal.d);
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntity(this);
    }
}
