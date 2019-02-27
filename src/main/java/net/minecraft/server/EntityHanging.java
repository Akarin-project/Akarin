package net.minecraft.server;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;

// CraftBukkit start
import org.bukkit.entity.Hanging;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
// CraftBukkit end

public abstract class EntityHanging extends Entity {

    protected static final Predicate<Entity> a = (entity) -> {
        return entity instanceof EntityHanging;
    };
    private int d;
    public BlockPosition blockPosition;
    @Nullable
    public EnumDirection direction;

    protected EntityHanging(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.setSize(0.5F, 0.5F);
    }

    protected EntityHanging(EntityTypes<?> entitytypes, World world, BlockPosition blockposition) {
        this(entitytypes, world);
        this.blockPosition = blockposition;
    }

    protected void x_() {}

    public void setDirection(EnumDirection enumdirection) {
        Validate.notNull(enumdirection);
        Validate.isTrue(enumdirection.k().c());
        this.direction = enumdirection;
        this.yaw = (float) (this.direction.get2DRotationValue() * 90);
        this.lastYaw = this.yaw;
        this.updateBoundingBox();
    }

    // CraftBukkit start - break out BB calc into own method
    public static AxisAlignedBB calculateBoundingBox(Entity entity, BlockPosition blockPosition, EnumDirection direction, int width, int height) {
        double d0 = (double) blockPosition.getX() + 0.5D;
        double d1 = (double) blockPosition.getY() + 0.5D;
        double d2 = (double) blockPosition.getZ() + 0.5D;
        double d3 = 0.46875D;
        double d4 = a(width);
        double d5 = a(height);

        d0 -= (double) direction.getAdjacentX() * 0.46875D;
        d2 -= (double) direction.getAdjacentZ() * 0.46875D;
        d1 += d5;
        EnumDirection enumdirection = direction.f();

        d0 += d4 * (double) enumdirection.getAdjacentX();
        d2 += d4 * (double) enumdirection.getAdjacentZ();
        if (entity != null) {
            entity.locX = d0;
            entity.locY = d1;
            entity.locZ = d2;
        }
        double d6 = (double) width;
        double d7 = (double) height;
        double d8 = (double) width;

        if (direction.k() == EnumDirection.EnumAxis.Z) {
            d8 = 1.0D;
        } else {
            d6 = 1.0D;
        }

        d6 /= 32.0D;
        d7 /= 32.0D;
        d8 /= 32.0D;
        return new AxisAlignedBB(d0 - d6, d1 - d7, d2 - d8, d0 + d6, d1 + d7, d2 + d8);
    }

    protected void updateBoundingBox() {
        if (this.direction != null) {
            // CraftBukkit start code moved in to calculateBoundingBox
            this.a(calculateBoundingBox(this, this.blockPosition, this.direction, this.getWidth(), this.getHeight()));
            // CraftBukkit end
        }
    }

    private static double a(int i) { // CraftBukkit - static
        return i % 32 == 0 ? 0.5D : 0.0D;
    }

    public void tick() {
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        if (this.d++ == 100 && !this.world.isClientSide) {
            this.d = 0;
            if (!this.dead && !this.survives()) {
                // CraftBukkit start - fire break events
                Material material = this.world.getType(new BlockPosition(this)).getMaterial();
                HangingBreakEvent.RemoveCause cause;

                if (!material.equals(Material.AIR)) {
                    // TODO: This feels insufficient to catch 100% of suffocation cases
                    cause = HangingBreakEvent.RemoveCause.OBSTRUCTION;
                } else {
                    cause = HangingBreakEvent.RemoveCause.PHYSICS;
                }

                HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), cause);
                this.world.getServer().getPluginManager().callEvent(event);

                if (dead || event.isCancelled()) {
                    return;
                }
                // CraftBukkit end
                this.die();
                this.a((Entity) null);
            }
        }

    }

    public boolean survives() {
        if (!this.world.getCubes(this, this.getBoundingBox())) {
            return false;
        } else {
            int i = Math.max(1, this.getWidth() / 16);
            int j = Math.max(1, this.getHeight() / 16);
            BlockPosition blockposition = this.blockPosition.shift(this.direction.opposite());
            EnumDirection enumdirection = this.direction.f();
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = 0; k < i; ++k) {
                for (int l = 0; l < j; ++l) {
                    int i1 = (i - 1) / -2;
                    int j1 = (j - 1) / -2;

                    blockposition_mutableblockposition.g(blockposition).c(enumdirection, k + i1).c(EnumDirection.UP, l + j1);
                    IBlockData iblockdata = this.world.getType(blockposition_mutableblockposition);

                    if (!iblockdata.getMaterial().isBuildable() && !BlockDiodeAbstract.isDiode(iblockdata)) {
                        return false;
                    }
                }
            }

            return this.world.getEntities(this, this.getBoundingBox(), EntityHanging.a).isEmpty();
        }
    }

    public boolean isInteractable() {
        return true;
    }

    public boolean t(Entity entity) {
        return entity instanceof EntityHuman ? this.damageEntity(DamageSource.playerAttack((EntityHuman) entity), 0.0F) : false;
    }

    public EnumDirection getDirection() {
        return this.direction;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (!this.dead && !this.world.isClientSide) {
                // CraftBukkit start - fire break events
                HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.DEFAULT);
                if (damagesource.getEntity() != null) {
                    event = new HangingBreakByEntityEvent((Hanging) this.getBukkitEntity(), damagesource.getEntity() == null ? null : damagesource.getEntity().getBukkitEntity(), damagesource.isExplosion() ? HangingBreakEvent.RemoveCause.EXPLOSION : HangingBreakEvent.RemoveCause.ENTITY);
                } else if (damagesource.isExplosion()) {
                    event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.EXPLOSION);
                }

                this.world.getServer().getPluginManager().callEvent(event);

                if (this.dead || event.isCancelled()) {
                    return true;
                }
                // CraftBukkit end

                this.die();
                this.aA();
                this.a(damagesource.getEntity());
            }

            return true;
        }
    }

    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        if (!this.world.isClientSide && !this.dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0D) {
            if (this.dead) return; // CraftBukkit

            // CraftBukkit start - fire break events
            // TODO - Does this need its own cause? Seems to only be triggered by pistons
            HangingBreakEvent event = new HangingBreakEvent((Hanging) this.getBukkitEntity(), HangingBreakEvent.RemoveCause.PHYSICS);
            this.world.getServer().getPluginManager().callEvent(event);

            if (this.dead || event.isCancelled()) {
                return;
            }
            // CraftBukkit end

            this.die();
            this.a((Entity) null);
        }

    }

    public void f(double d0, double d1, double d2) {
        if (false && !this.world.isClientSide && !this.dead && d0 * d0 + d1 * d1 + d2 * d2 > 0.0D) { // CraftBukkit - not needed
            this.die();
            this.a((Entity) null);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Facing", (byte) this.direction.get2DRotationValue());
        BlockPosition blockposition = this.getBlockPosition();

        nbttagcompound.setInt("TileX", blockposition.getX());
        nbttagcompound.setInt("TileY", blockposition.getY());
        nbttagcompound.setInt("TileZ", blockposition.getZ());
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.blockPosition = new BlockPosition(nbttagcompound.getInt("TileX"), nbttagcompound.getInt("TileY"), nbttagcompound.getInt("TileZ"));
        this.setDirection(EnumDirection.fromType2(nbttagcompound.getByte("Facing")));
    }

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void a(@Nullable Entity entity);

    public abstract void m();

    public EntityItem a(ItemStack itemstack, float f) {
        EntityItem entityitem = new EntityItem(this.world, this.locX + (double) ((float) this.direction.getAdjacentX() * 0.15F), this.locY + (double) f, this.locZ + (double) ((float) this.direction.getAdjacentZ() * 0.15F), itemstack);

        entityitem.n();
        this.world.addEntity(entityitem);
        return entityitem;
    }

    protected boolean aD() {
        return false;
    }

    public void setPosition(double d0, double d1, double d2) {
        this.blockPosition = new BlockPosition(d0, d1, d2);
        this.updateBoundingBox();
        this.impulse = true;
    }

    public BlockPosition getBlockPosition() {
        return this.blockPosition;
    }

    public float a(EnumBlockRotation enumblockrotation) {
        if (this.direction != null && this.direction.k() != EnumDirection.EnumAxis.Y) {
            switch (enumblockrotation) {
            case CLOCKWISE_180:
                this.direction = this.direction.opposite();
                break;
            case COUNTERCLOCKWISE_90:
                this.direction = this.direction.f();
                break;
            case CLOCKWISE_90:
                this.direction = this.direction.e();
            }
        }

        float f = MathHelper.g(this.yaw);

        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return f + 180.0F;
        case COUNTERCLOCKWISE_90:
            return f + 90.0F;
        case CLOCKWISE_90:
            return f + 270.0F;
        default:
            return f;
        }
    }

    public float a(EnumBlockMirror enumblockmirror) {
        return this.a(enumblockmirror.a(this.direction));
    }

    public void onLightningStrike(EntityLightning entitylightning) {}
}
