package net.minecraft.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class EntityFishingHook extends Entity {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.b);
    private boolean isInGround;
    private int d;
    public EntityHuman owner;
    private int f;
    private int g;
    private int h;
    private int aw;
    private float ax;
    public Entity hooked;
    private EntityFishingHook.HookState ay;
    private int az;
    private int aA;

    private EntityFishingHook(World world) {
        super(EntityTypes.FISHING_BOBBER, world);
        this.ay = EntityFishingHook.HookState.FLYING;
    }

    public EntityFishingHook(World world, EntityHuman entityhuman) {
        this(world);
        this.a(entityhuman);
        this.k();
    }

    private void a(EntityHuman entityhuman) {
        this.setSize(0.25F, 0.25F);
        this.ak = true;
        this.owner = entityhuman;
        this.owner.hookedFish = this;
    }

    public void a(int i) {
        this.aA = i;
    }

    public void b(int i) {
        this.az = i;
    }

    private void k() {
        float f = this.owner.pitch;
        float f1 = this.owner.yaw;
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        double d0 = this.owner.locX - (double) f3 * 0.3D;
        double d1 = this.owner.locY + (double) this.owner.getHeadHeight();
        double d2 = this.owner.locZ - (double) f2 * 0.3D;

        this.setPositionRotation(d0, d1, d2, f1, f);
        this.motX = (double) (-f3);
        this.motY = (double) MathHelper.a(-(f5 / f4), -5.0F, 5.0F);
        this.motZ = (double) (-f2);
        float f6 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);

        this.motX *= 0.6D / (double) f6 + 0.5D + this.random.nextGaussian() * 0.0045D;
        this.motY *= 0.6D / (double) f6 + 0.5D + this.random.nextGaussian() * 0.0045D;
        this.motZ *= 0.6D / (double) f6 + 0.5D + this.random.nextGaussian() * 0.0045D;
        float f7 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.c(this.motY, (double) f7) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    protected void x_() {
        this.getDataWatcher().register(EntityFishingHook.b, 0);
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityFishingHook.b.equals(datawatcherobject)) {
            int i = (Integer) this.getDataWatcher().get(EntityFishingHook.b);

            this.hooked = i > 0 ? this.world.getEntity(i - 1) : null;
        }

        super.a(datawatcherobject);
    }

    public void tick() {
        super.tick();
        if (this.owner == null) {
            this.die();
        } else if (this.world.isClientSide || !this.l()) {
            if (this.isInGround) {
                ++this.d;
                if (this.d >= 1200) {
                    this.die();
                    return;
                }
            }

            float f = 0.0F;
            BlockPosition blockposition = new BlockPosition(this);
            Fluid fluid = this.world.getFluid(blockposition);

            if (fluid.a(TagsFluid.WATER)) {
                f = fluid.getHeight();
            }

            double d0;

            if (this.ay == EntityFishingHook.HookState.FLYING) {
                if (this.hooked != null) {
                    this.motX = 0.0D;
                    this.motY = 0.0D;
                    this.motZ = 0.0D;
                    this.ay = EntityFishingHook.HookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (f > 0.0F) {
                    this.motX *= 0.3D;
                    this.motY *= 0.2D;
                    this.motZ *= 0.3D;
                    this.ay = EntityFishingHook.HookState.BOBBING;
                    return;
                }

                if (!this.world.isClientSide) {
                    this.n();
                }

                if (!this.isInGround && !this.onGround && !this.positionChanged) {
                    ++this.f;
                } else {
                    this.f = 0;
                    this.motX = 0.0D;
                    this.motY = 0.0D;
                    this.motZ = 0.0D;
                }
            } else {
                if (this.ay == EntityFishingHook.HookState.HOOKED_IN_ENTITY) {
                    if (this.hooked != null) {
                        if (this.hooked.dead) {
                            this.hooked = null;
                            this.ay = EntityFishingHook.HookState.FLYING;
                        } else {
                            this.locX = this.hooked.locX;
                            double d1 = (double) this.hooked.length;

                            this.locY = this.hooked.getBoundingBox().minY + d1 * 0.8D;
                            this.locZ = this.hooked.locZ;
                            this.setPosition(this.locX, this.locY, this.locZ);
                        }
                    }

                    return;
                }

                if (this.ay == EntityFishingHook.HookState.BOBBING) {
                    this.motX *= 0.9D;
                    this.motZ *= 0.9D;
                    d0 = this.locY + this.motY - (double) blockposition.getY() - (double) f;
                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.motY -= d0 * (double) this.random.nextFloat() * 0.2D;
                    if (!this.world.isClientSide && f > 0.0F) {
                        this.a(blockposition);
                    }
                }
            }

            if (!fluid.a(TagsFluid.WATER)) {
                this.motY -= 0.03D;
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.m();
            d0 = 0.92D;
            this.motX *= 0.92D;
            this.motY *= 0.92D;
            this.motZ *= 0.92D;
            this.setPosition(this.locX, this.locY, this.locZ);
        }
    }

    private boolean l() {
        ItemStack itemstack = this.owner.getItemInMainHand();
        ItemStack itemstack1 = this.owner.getItemInOffHand();
        boolean flag = itemstack.getItem() == Items.FISHING_ROD;
        boolean flag1 = itemstack1.getItem() == Items.FISHING_ROD;

        if (!this.owner.dead && this.owner.isAlive() && (flag || flag1) && this.h(this.owner) <= 1024.0D) {
            return false;
        } else {
            this.die();
            return true;
        }
    }

    private void m() {
        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.c(this.motY, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
            ;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
    }

    private void n() {
        Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, FluidCollisionOption.NEVER, true, false);

        vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.x, movingobjectposition.pos.y, movingobjectposition.pos.z);
        }

        Entity entity = null;
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox().b(this.motX, this.motY, this.motZ).g(1.0D));
        double d0 = 0.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            if (this.a(entity1) && (entity1 != this.owner || this.f >= 5)) {
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().g(0.30000001192092896D);
                MovingObjectPosition movingobjectposition1 = axisalignedbb.b(vec3d, vec3d1);

                if (movingobjectposition1 != null) {
                    double d1 = vec3d.distanceSquared(movingobjectposition1.pos);

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }

        if (movingobjectposition != null && movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.MISS) {
            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                this.hooked = movingobjectposition.entity;
                this.o();
            } else {
                this.isInGround = true;
            }
        }

    }

    private void o() {
        this.getDataWatcher().set(EntityFishingHook.b, this.hooked.getId() + 1);
    }

    private void a(BlockPosition blockposition) {
        WorldServer worldserver = (WorldServer) this.world;
        int i = 1;
        BlockPosition blockposition1 = blockposition.up();

        if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(blockposition1)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.world.e(blockposition1)) {
            --i;
        }

        if (this.g > 0) {
            --this.g;
            if (this.g <= 0) {
                this.h = 0;
                this.aw = 0;
            } else {
                this.motY -= 0.2D * (double) this.random.nextFloat() * (double) this.random.nextFloat();
            }
        } else {
            float f;
            float f1;
            float f2;
            double d0;
            double d1;
            double d2;
            Block block;

            if (this.aw > 0) {
                this.aw -= i;
                if (this.aw > 0) {
                    this.ax = (float) ((double) this.ax + this.random.nextGaussian() * 4.0D);
                    f = this.ax * 0.017453292F;
                    f1 = MathHelper.sin(f);
                    f2 = MathHelper.cos(f);
                    d0 = this.locX + (double) (f1 * (float) this.aw * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
                    d2 = this.locZ + (double) (f2 * (float) this.aw * 0.1F);
                    block = worldserver.getType(new BlockPosition(d0, d1 - 1.0D, d2)).getBlock();
                    if (block == Blocks.WATER) {
                        if (this.random.nextFloat() < 0.15F) {
                            worldserver.a(Particles.e, d0, d1 - 0.10000000149011612D, d2, 1, (double) f1, 0.1D, (double) f2, 0.0D);
                        }

                        float f3 = f1 * 0.04F;
                        float f4 = f2 * 0.04F;

                        worldserver.a(Particles.x, d0, d1, d2, 0, (double) f4, 0.01D, (double) (-f3), 1.0D);
                        worldserver.a(Particles.x, d0, d1, d2, 0, (double) (-f4), 0.01D, (double) f3, 1.0D);
                    }
                } else {
                    this.motY = (double) (-0.4F * MathHelper.a(this.random, 0.6F, 1.0F));
                    this.a(SoundEffects.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.getBoundingBox().minY + 0.5D;

                    worldserver.a(Particles.e, this.locX, d3, this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                    worldserver.a(Particles.x, this.locX, d3, this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D);
                    this.g = MathHelper.nextInt(this.random, 20, 40);
                }
            } else if (this.h > 0) {
                this.h -= i;
                f = 0.15F;
                if (this.h < 20) {
                    f = (float) ((double) f + (double) (20 - this.h) * 0.05D);
                } else if (this.h < 40) {
                    f = (float) ((double) f + (double) (40 - this.h) * 0.02D);
                } else if (this.h < 60) {
                    f = (float) ((double) f + (double) (60 - this.h) * 0.01D);
                }

                if (this.random.nextFloat() < f) {
                    f1 = MathHelper.a(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f2 = MathHelper.a(this.random, 25.0F, 60.0F);
                    d0 = this.locX + (double) (MathHelper.sin(f1) * f2 * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
                    d2 = this.locZ + (double) (MathHelper.cos(f1) * f2 * 0.1F);
                    block = worldserver.getType(new BlockPosition((int) d0, (int) d1 - 1, (int) d2)).getBlock();
                    if (block == Blocks.WATER) {
                        worldserver.a(Particles.R, d0, d1, d2, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                    }
                }

                if (this.h <= 0) {
                    this.ax = MathHelper.a(this.random, 0.0F, 360.0F);
                    this.aw = MathHelper.nextInt(this.random, 20, 80);
                }
            } else {
                this.h = MathHelper.nextInt(this.random, 100, 600);
                this.h -= this.aA * 20 * 5;
            }
        }

    }

    protected boolean a(Entity entity) {
        return entity.isInteractable() || entity instanceof EntityItem;
    }

    public void b(NBTTagCompound nbttagcompound) {}

    public void a(NBTTagCompound nbttagcompound) {}

    public int b(ItemStack itemstack) {
        if (!this.world.isClientSide && this.owner != null) {
            int i = 0;

            if (this.hooked != null) {
                this.f();
                CriterionTriggers.D.a((EntityPlayer) this.owner, itemstack, this, Collections.emptyList());
                this.world.broadcastEntityEffect(this, (byte) 31);
                i = this.hooked instanceof EntityItem ? 3 : 5;
            } else if (this.g > 0) {
                LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.world)).position(new BlockPosition(this));

                loottableinfo_builder.luck((float) this.az + this.owner.dJ());
                List<ItemStack> list = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.aO).populateLoot(this.random, loottableinfo_builder.build());

                CriterionTriggers.D.a((EntityPlayer) this.owner, itemstack, this, list);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    ItemStack itemstack1 = (ItemStack) iterator.next();
                    EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack1);
                    double d0 = this.owner.locX - this.locX;
                    double d1 = this.owner.locY - this.locY;
                    double d2 = this.owner.locZ - this.locZ;
                    double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    double d4 = 0.1D;

                    entityitem.motX = d0 * 0.1D;
                    entityitem.motY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
                    entityitem.motZ = d2 * 0.1D;
                    this.world.addEntity(entityitem);
                    this.owner.world.addEntity(new EntityExperienceOrb(this.owner.world, this.owner.locX, this.owner.locY + 0.5D, this.owner.locZ + 0.5D, this.random.nextInt(6) + 1));
                    if (itemstack1.getItem().a(TagsItem.FISHES)) {
                        this.owner.a(StatisticList.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (this.isInGround) {
                i = 2;
            }

            this.die();
            return i;
        } else {
            return 0;
        }
    }

    protected void f() {
        if (this.owner != null) {
            double d0 = this.owner.locX - this.locX;
            double d1 = this.owner.locY - this.locY;
            double d2 = this.owner.locZ - this.locZ;
            double d3 = 0.1D;

            this.hooked.motX += d0 * 0.1D;
            this.hooked.motY += d1 * 0.1D;
            this.hooked.motZ += d2 * 0.1D;
        }
    }

    protected boolean playStepSound() {
        return false;
    }

    public void die() {
        super.die();
        if (this.owner != null) {
            this.owner.hookedFish = null;
        }

    }

    public EntityHuman i() {
        return this.owner;
    }

    public boolean bm() {
        return false;
    }

    static enum HookState {

        FLYING, HOOKED_IN_ENTITY, BOBBING;

        private HookState() {}
    }
}
