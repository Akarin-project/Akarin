package net.minecraft.server;

import java.util.List;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
// CraftBukkit end

public class EntityBoat extends Entity {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Float> c = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> d = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> e = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> g = DataWatcher.a(EntityBoat.class, DataWatcherRegistry.b);
    private final float[] h;
    private float aw;
    private float ax;
    private float ay;
    private int az;
    private double aA;
    private double aB;
    private double aC;
    private double aD;
    private double aE;
    private boolean aF;
    private boolean aG;
    private boolean aH;
    private boolean aI;
    private double aJ;
    private float aK;
    private EntityBoat.EnumStatus aL;
    private EntityBoat.EnumStatus aM;
    private double aN;
    private boolean aO;
    private boolean aP;
    private float aQ;
    private float aR;
    private float aS;

    // CraftBukkit start
    // PAIL: Some of these haven't worked since a few updates, and since 1.9 they are less and less applicable.
    public double maxSpeed = 0.4D;
    public double occupiedDeceleration = 0.2D;
    public double unoccupiedDeceleration = -1;
    public boolean landBoats = false;
    // CraftBukkit end

    public EntityBoat(World world) {
        super(EntityTypes.BOAT, world);
        this.h = new float[2];
        this.j = true;
        this.setSize(1.375F, 0.5625F);
    }

    public EntityBoat(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void x_() {
        this.datawatcher.register(EntityBoat.a, 0);
        this.datawatcher.register(EntityBoat.b, 1);
        this.datawatcher.register(EntityBoat.c, 0.0F);
        this.datawatcher.register(EntityBoat.d, EntityBoat.EnumBoatType.OAK.ordinal());
        this.datawatcher.register(EntityBoat.e, false);
        this.datawatcher.register(EntityBoat.f, false);
        this.datawatcher.register(EntityBoat.g, 0);
    }

    @Nullable
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Nullable
    public AxisAlignedBB al() {
        return this.getBoundingBox();
    }

    public boolean isCollidable() {
        return true;
    }

    public double aJ() {
        return -0.1D;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!this.world.isClientSide && !this.dead) {
            if (damagesource instanceof EntityDamageSourceIndirect && damagesource.getEntity() != null && this.w(damagesource.getEntity())) {
                return false;
            } else {
                // CraftBukkit start
                Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.entity.Entity attacker = (damagesource.getEntity() == null) ? null : damagesource.getEntity().getBukkitEntity();

                VehicleDamageEvent event = new VehicleDamageEvent(vehicle, attacker, (double) f);
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }
                // f = event.getDamage(); // TODO Why don't we do this?
                // CraftBukkit end

                this.c(-this.o());
                this.b(10);
                this.setDamage(this.m() + f * 10.0F);
                this.aA();
                boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

                if (flag || this.m() > 40.0F) {
                    // CraftBukkit start
                    VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, attacker);
                    this.world.getServer().getPluginManager().callEvent(destroyEvent);

                    if (destroyEvent.isCancelled()) {
                        this.setDamage(40F); // Maximize damage so this doesn't get triggered again right away
                        return true;
                    }
                    // CraftBukkit end
                    if (!flag && this.world.getGameRules().getBoolean("doEntityDrops")) {
                        this.a((IMaterial) this.f());
                    }

                    this.die();
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void j(boolean flag) {
        if (!this.world.isClientSide) {
            this.aO = true;
            this.aP = flag;
            if (this.z() == 0) {
                this.d(60);
            }
        }

        this.world.addParticle(Particles.R, this.locX + (double) this.random.nextFloat(), this.locY + 0.7D, this.locZ + (double) this.random.nextFloat(), 0.0D, 0.0D, 0.0D);
        if (this.random.nextInt(20) == 0) {
            this.world.a(this.locX, this.locY, this.locZ, this.ae(), this.bV(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
        }

    }

    public void collide(Entity entity) {
        if (entity instanceof EntityBoat) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                // CraftBukkit start
                VehicleEntityCollisionEvent event = new VehicleEntityCollisionEvent((Vehicle) this.getBukkitEntity(), entity.getBukkitEntity());
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
                // CraftBukkit end
                super.collide(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            // CraftBukkit start
            VehicleEntityCollisionEvent event = new VehicleEntityCollisionEvent((Vehicle) this.getBukkitEntity(), entity.getBukkitEntity());
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
            // CraftBukkit end
            super.collide(entity);
        }

    }

    public Item f() {
        switch (this.getType()) {
        case OAK:
        default:
            return Items.OAK_BOAT;
        case SPRUCE:
            return Items.SPRUCE_BOAT;
        case BIRCH:
            return Items.BIRCH_BOAT;
        case JUNGLE:
            return Items.JUNGLE_BOAT;
        case ACACIA:
            return Items.ACACIA_BOAT;
        case DARK_OAK:
            return Items.DARK_OAK_BOAT;
        }
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public EnumDirection getAdjustedDirection() {
        return this.getDirection().e();
    }

    private Location lastLocation; // CraftBukkit
    public void tick() {
        this.aM = this.aL;
        this.aL = this.s();
        if (this.aL != EntityBoat.EnumStatus.UNDER_WATER && this.aL != EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
            this.ax = 0.0F;
        } else {
            ++this.ax;
        }

        if (!this.world.isClientSide && this.ax >= 60.0F) {
            this.ejectPassengers();
        }

        if (this.n() > 0) {
            this.b(this.n() - 1);
        }

        if (this.m() > 0.0F) {
            this.setDamage(this.m() - 1.0F);
        }

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        super.tick();
        this.r();
        if (this.bT()) {
            if (this.bP().isEmpty() || !(this.bP().get(0) instanceof EntityHuman)) {
                this.a(false, false);
            }

            this.v();
            if (this.world.isClientSide) {
                this.x();
                this.world.a((Packet) (new PacketPlayInBoatMove(this.a(0), this.a(1))));
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
        } else {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
        }

        // CraftBukkit start
        org.bukkit.Server server = this.world.getServer();
        org.bukkit.World bworld = this.world.getWorld();

        Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        Vehicle vehicle = (Vehicle) this.getBukkitEntity();

        server.getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

        if (lastLocation != null && !lastLocation.equals(to)) {
            VehicleMoveEvent event = new VehicleMoveEvent(vehicle, lastLocation, to);
            server.getPluginManager().callEvent(event);
        }
        lastLocation = vehicle.getLocation();
        // CraftBukkit end

        this.q();

        for (int i = 0; i <= 1; ++i) {
            if (this.a(i)) {
                if (!this.isSilent() && (double) (this.h[i] % 6.2831855F) <= 0.7853981852531433D && ((double) this.h[i] + 0.39269909262657166D) % 6.2831854820251465D >= 0.7853981852531433D) {
                    SoundEffect soundeffect = this.i();

                    if (soundeffect != null) {
                        Vec3D vec3d = this.f(1.0F);
                        double d0 = i == 1 ? -vec3d.z : vec3d.z;
                        double d1 = i == 1 ? vec3d.x : -vec3d.x;

                        this.world.a((EntityHuman) null, this.locX + d0, this.locY, this.locZ + d1, soundeffect, this.bV(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
                    }
                }

                this.h[i] = (float) ((double) this.h[i] + 0.39269909262657166D);
            } else {
                this.h[i] = 0.0F;
            }
        }

        this.checkBlockCollisions();
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, -0.009999999776482582D, 0.20000000298023224D), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            boolean flag = !this.world.isClientSide && !(this.bO() instanceof EntityHuman);

            for (int j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                if (!entity.w(this)) {
                    if (flag && this.bP().size() < 2 && !entity.isPassenger() && entity.width < this.width && entity instanceof EntityLiving && !(entity instanceof EntityWaterAnimal) && !(entity instanceof EntityHuman)) {
                        entity.startRiding(this);
                    } else {
                        this.collide(entity);
                    }
                }
            }
        }

    }

    private void q() {
        int i;

        if (this.world.isClientSide) {
            i = this.z();
            if (i > 0) {
                this.aQ += 0.05F;
            } else {
                this.aQ -= 0.1F;
            }

            this.aQ = MathHelper.a(this.aQ, 0.0F, 1.0F);
            this.aS = this.aR;
            this.aR = 10.0F * (float) Math.sin((double) (0.5F * (float) this.world.getTime())) * this.aQ;
        } else {
            if (!this.aO) {
                this.d(0);
            }

            i = this.z();
            if (i > 0) {
                --i;
                this.d(i);
                int j = 60 - i - 1;

                if (j > 0 && i == 0) {
                    this.d(0);
                    if (this.aP) {
                        this.motY -= 0.7D;
                        this.ejectPassengers();
                    } else {
                        this.motY = this.a(EntityHuman.class) ? 2.7D : 0.6D;
                    }
                }

                this.aO = false;
            }
        }

    }

    @Nullable
    protected SoundEffect i() {
        switch (this.s()) {
        case IN_WATER:
        case UNDER_WATER:
        case UNDER_FLOWING_WATER:
            return SoundEffects.ENTITY_BOAT_PADDLE_WATER;
        case ON_LAND:
            return SoundEffects.ENTITY_BOAT_PADDLE_LAND;
        case IN_AIR:
        default:
            return null;
        }
    }

    private void r() {
        if (this.az > 0 && !this.bT()) {
            double d0 = this.locX + (this.aA - this.locX) / (double) this.az;
            double d1 = this.locY + (this.aB - this.locY) / (double) this.az;
            double d2 = this.locZ + (this.aC - this.locZ) / (double) this.az;
            double d3 = MathHelper.g(this.aD - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.az);
            this.pitch = (float) ((double) this.pitch + (this.aE - (double) this.pitch) / (double) this.az);
            --this.az;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        }
    }

    public void a(boolean flag, boolean flag1) {
        this.datawatcher.set(EntityBoat.e, flag);
        this.datawatcher.set(EntityBoat.f, flag1);
    }

    private EntityBoat.EnumStatus s() {
        EntityBoat.EnumStatus entityboat_enumstatus = this.u();

        if (entityboat_enumstatus != null) {
            this.aJ = this.getBoundingBox().maxY;
            return entityboat_enumstatus;
        } else if (this.t()) {
            return EntityBoat.EnumStatus.IN_WATER;
        } else {
            float f = this.l();

            if (f > 0.0F) {
                this.aK = f;
                return EntityBoat.EnumStatus.ON_LAND;
            } else {
                return EntityBoat.EnumStatus.IN_AIR;
            }
        }
    }

    public float k() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.f(axisalignedbb.maxY - this.aN);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            label161:
            for (int k1 = k; k1 < l; ++k1) {
                float f = 0.0F;

                for (int l1 = i; l1 < j; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.c(l1, k1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                        if (fluid.a(TagsFluid.WATER)) {
                            f = Math.max(f, (float) k1 + fluid.getHeight());
                        }

                        if (f >= 1.0F) {
                            continue label161;
                        }
                    }
                }

                if (f < 1.0F) {
                    float f1 = (float) blockposition_pooledblockposition.getY() + f;

                    return f1;
                }
            }

            float f2 = (float) (l + 1);

            return f2;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }
    }

    public float l() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY - 0.001D, axisalignedbb.minZ, axisalignedbb.maxX, axisalignedbb.minY, axisalignedbb.maxZ);
        int i = MathHelper.floor(axisalignedbb1.minX) - 1;
        int j = MathHelper.f(axisalignedbb1.maxX) + 1;
        int k = MathHelper.floor(axisalignedbb1.minY) - 1;
        int l = MathHelper.f(axisalignedbb1.maxY) + 1;
        int i1 = MathHelper.floor(axisalignedbb1.minZ) - 1;
        int j1 = MathHelper.f(axisalignedbb1.maxZ) + 1;
        VoxelShape voxelshape = VoxelShapes.a(axisalignedbb1);
        float f = 0.0F;
        int k1 = 0;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int l1 = i; l1 < j; ++l1) {
                for (int i2 = i1; i2 < j1; ++i2) {
                    int j2 = (l1 != i && l1 != j - 1 ? 0 : 1) + (i2 != i1 && i2 != j1 - 1 ? 0 : 1);

                    if (j2 != 2) {
                        for (int k2 = k; k2 < l; ++k2) {
                            if (j2 <= 0 || k2 != k && k2 != l - 1) {
                                blockposition_pooledblockposition.c(l1, k2, i2);
                                IBlockData iblockdata = this.world.getType(blockposition_pooledblockposition);

                                if (!(iblockdata.getBlock() instanceof BlockWaterLily) && VoxelShapes.c(iblockdata.getCollisionShape(this.world, blockposition_pooledblockposition).a((double) l1, (double) k2, (double) i2), voxelshape, OperatorBoolean.AND)) {
                                    f += iblockdata.getBlock().n();
                                    ++k1;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

        return f / (float) k1;
    }

    private boolean t() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.minY + 0.001D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        boolean flag = false;

        this.aJ = Double.MIN_VALUE;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.c(k1, l1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                        if (fluid.a(TagsFluid.WATER)) {
                            float f = (float) l1 + fluid.getHeight();

                            this.aJ = Math.max((double) f, this.aJ);
                            flag |= axisalignedbb.minY < (double) f;
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }

        return flag;
    }

    @Nullable
    private EntityBoat.EnumStatus u() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        double d0 = axisalignedbb.maxY + 0.001D;
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.maxY);
        int l = MathHelper.f(d0);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        boolean flag = false;
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        blockposition_pooledblockposition.c(k1, l1, i2);
                        Fluid fluid = this.world.getFluid(blockposition_pooledblockposition);

                        if (fluid.a(TagsFluid.WATER) && d0 < (double) ((float) blockposition_pooledblockposition.getY() + fluid.getHeight())) {
                            if (!fluid.d()) {
                                EntityBoat.EnumStatus entityboat_enumstatus = EntityBoat.EnumStatus.UNDER_FLOWING_WATER;

                                return entityboat_enumstatus;
                            }

                            flag = true;
                        }
                    }
                }
            }

            return flag ? EntityBoat.EnumStatus.UNDER_WATER : null;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_pooledblockposition != null) {
                if (throwable != null) {
                    try {
                        blockposition_pooledblockposition.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_pooledblockposition.close();
                }
            }

        }
    }

    private void v() {
        double d0 = -0.03999999910593033D;
        double d1 = this.isNoGravity() ? 0.0D : -0.03999999910593033D;
        double d2 = 0.0D;

        this.aw = 0.05F;
        if (this.aM == EntityBoat.EnumStatus.IN_AIR && this.aL != EntityBoat.EnumStatus.IN_AIR && this.aL != EntityBoat.EnumStatus.ON_LAND) {
            this.aJ = this.getBoundingBox().minY + (double) this.length;
            this.setPosition(this.locX, (double) (this.k() - this.length) + 0.101D, this.locZ);
            this.motY = 0.0D;
            this.aN = 0.0D;
            this.aL = EntityBoat.EnumStatus.IN_WATER;
        } else {
            if (this.aL == EntityBoat.EnumStatus.IN_WATER) {
                d2 = (this.aJ - this.getBoundingBox().minY) / (double) this.length;
                this.aw = 0.9F;
            } else if (this.aL == EntityBoat.EnumStatus.UNDER_FLOWING_WATER) {
                d1 = -7.0E-4D;
                this.aw = 0.9F;
            } else if (this.aL == EntityBoat.EnumStatus.UNDER_WATER) {
                d2 = 0.009999999776482582D;
                this.aw = 0.45F;
            } else if (this.aL == EntityBoat.EnumStatus.IN_AIR) {
                this.aw = 0.9F;
            } else if (this.aL == EntityBoat.EnumStatus.ON_LAND) {
                this.aw = this.aK;
                if (this.bO() instanceof EntityHuman) {
                    this.aK /= 2.0F;
                }
            }

            this.motX *= (double) this.aw;
            this.motZ *= (double) this.aw;
            this.ay *= this.aw;
            this.motY += d1;
            if (d2 > 0.0D) {
                double d3 = 0.65D;

                this.motY += d2 * 0.06153846016296973D;
                double d4 = 0.75D;

                this.motY *= 0.75D;
            }
        }

    }

    private void x() {
        if (this.isVehicle()) {
            float f = 0.0F;

            if (this.aF) {
                this.ay += -1.0F;
            }

            if (this.aG) {
                ++this.ay;
            }

            if (this.aG != this.aF && !this.aH && !this.aI) {
                f += 0.005F;
            }

            this.yaw += this.ay;
            if (this.aH) {
                f += 0.04F;
            }

            if (this.aI) {
                f -= 0.005F;
            }

            this.motX += (double) (MathHelper.sin(-this.yaw * 0.017453292F) * f);
            this.motZ += (double) (MathHelper.cos(this.yaw * 0.017453292F) * f);
            this.a(this.aG && !this.aF || this.aH, this.aF && !this.aG || this.aH);
        }
    }

    public void k(Entity entity) {
        if (this.w(entity)) {
            float f = 0.0F;
            float f1 = (float) ((this.dead ? 0.009999999776482582D : this.aJ()) + entity.aI());

            if (this.bP().size() > 1) {
                int i = this.bP().indexOf(entity);

                if (i == 0) {
                    f = 0.2F;
                } else {
                    f = -0.6F;
                }

                if (entity instanceof EntityAnimal) {
                    f = (float) ((double) f + 0.2D);
                }
            }

            Vec3D vec3d = (new Vec3D((double) f, 0.0D, 0.0D)).b(-this.yaw * 0.017453292F - 1.5707964F);

            entity.setPosition(this.locX + vec3d.x, this.locY + (double) f1, this.locZ + vec3d.z);
            entity.yaw += this.ay;
            entity.setHeadRotation(entity.getHeadRotation() + this.ay);
            this.a(entity);
            if (entity instanceof EntityAnimal && this.bP().size() > 1) {
                int j = entity.getId() % 2 == 0 ? 90 : 270;

                entity.k(((EntityAnimal) entity).aQ + (float) j);
                entity.setHeadRotation(entity.getHeadRotation() + (float) j);
            }

        }
    }

    protected void a(Entity entity) {
        entity.k(this.yaw);
        float f = MathHelper.g(entity.yaw - this.yaw);
        float f1 = MathHelper.a(f, -105.0F, 105.0F);

        entity.lastYaw += f1 - f;
        entity.yaw += f1 - f;
        entity.setHeadRotation(entity.yaw);
    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Type", this.getType().a());
    }

    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Type", 8)) {
            this.setType(EntityBoat.EnumBoatType.a(nbttagcompound.getString("Type")));
        }

    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        if (entityhuman.isSneaking()) {
            return false;
        } else {
            if (!this.world.isClientSide && this.ax < 60.0F) {
                entityhuman.startRiding(this);
            }

            return true;
        }
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        this.aN = this.motY;
        if (!this.isPassenger()) {
            if (flag) {
                if (this.fallDistance > 3.0F) {
                    if (this.aL != EntityBoat.EnumStatus.ON_LAND) {
                        this.fallDistance = 0.0F;
                        return;
                    }

                    this.c(this.fallDistance, 1.0F);
                    if (!this.world.isClientSide && !this.dead) {
                    // CraftBukkit start
                    Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                    VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, null);
                    this.world.getServer().getPluginManager().callEvent(destroyEvent);
                    if (!destroyEvent.isCancelled()) {
                        this.die();
                        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
                            int i;

                            for (i = 0; i < 3; ++i) {
                                this.a((IMaterial) this.getType().b());
                            }

                            for (i = 0; i < 2; ++i) {
                                this.a((IMaterial) Items.STICK);
                            }
                        }
                    }
                    } // CraftBukkit end
                }

                this.fallDistance = 0.0F;
            } else if (!this.world.getFluid((new BlockPosition(this)).down()).a(TagsFluid.WATER) && d0 < 0.0D) {
                this.fallDistance = (float) ((double) this.fallDistance - d0);
            }

        }
    }

    public boolean a(int i) {
        return (Boolean) this.datawatcher.get(i == 0 ? EntityBoat.e : EntityBoat.f) && this.bO() != null;
    }

    public void setDamage(float f) {
        this.datawatcher.set(EntityBoat.c, f);
    }

    public float m() {
        return (Float) this.datawatcher.get(EntityBoat.c);
    }

    public void b(int i) {
        this.datawatcher.set(EntityBoat.a, i);
    }

    public int n() {
        return (Integer) this.datawatcher.get(EntityBoat.a);
    }

    private void d(int i) {
        this.datawatcher.set(EntityBoat.g, i);
    }

    private int z() {
        return (Integer) this.datawatcher.get(EntityBoat.g);
    }

    public void c(int i) {
        this.datawatcher.set(EntityBoat.b, i);
    }

    public int o() {
        return (Integer) this.datawatcher.get(EntityBoat.b);
    }

    public void setType(EntityBoat.EnumBoatType entityboat_enumboattype) {
        this.datawatcher.set(EntityBoat.d, entityboat_enumboattype.ordinal());
    }

    public EntityBoat.EnumBoatType getType() {
        return EntityBoat.EnumBoatType.a((Integer) this.datawatcher.get(EntityBoat.d));
    }

    protected boolean q(Entity entity) {
        return this.bP().size() < 2 && !this.a(TagsFluid.WATER);
    }

    @Nullable
    public Entity bO() {
        List<Entity> list = this.bP();

        return list.isEmpty() ? null : (Entity) list.get(0);
    }

    public static enum EnumBoatType {

        OAK(Blocks.OAK_PLANKS, "oak"), SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"), BIRCH(Blocks.BIRCH_PLANKS, "birch"), JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"), ACACIA(Blocks.ACACIA_PLANKS, "acacia"), DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

        private final String g;
        private final Block h;

        private EnumBoatType(Block block, String s) {
            this.g = s;
            this.h = block;
        }

        public String a() {
            return this.g;
        }

        public Block b() {
            return this.h;
        }

        public String toString() {
            return this.g;
        }

        public static EntityBoat.EnumBoatType a(int i) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            if (i < 0 || i >= aentityboat_enumboattype.length) {
                i = 0;
            }

            return aentityboat_enumboattype[i];
        }

        public static EntityBoat.EnumBoatType a(String s) {
            EntityBoat.EnumBoatType[] aentityboat_enumboattype = values();

            for (int i = 0; i < aentityboat_enumboattype.length; ++i) {
                if (aentityboat_enumboattype[i].a().equals(s)) {
                    return aentityboat_enumboattype[i];
                }
            }

            return aentityboat_enumboattype[0];
        }
    }

    public static enum EnumStatus {

        IN_WATER, UNDER_WATER, UNDER_FLOWING_WATER, ON_LAND, IN_AIR;

        private EnumStatus() {}
    }
}
