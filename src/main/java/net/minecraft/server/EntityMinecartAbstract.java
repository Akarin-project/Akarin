package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.vehicle.VehicleDamageEvent;
import org.bukkit.event.vehicle.VehicleDestroyEvent;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.bukkit.util.Vector;
// CraftBukkit end

public abstract class EntityMinecartAbstract extends Entity {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Float> d = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> f = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> g = DataWatcher.a(EntityMinecartAbstract.class, DataWatcherRegistry.i);
    private boolean ar;
    private static final int[][][] as = new int[][][]{{{0, 0, -1}, {0, 0, 1}}, {{-1, 0, 0}, {1, 0, 0}}, {{-1, -1, 0}, {1, 0, 0}}, {{-1, 0, 0}, {1, -1, 0}}, {{0, 0, -1}, {0, -1, 1}}, {{0, -1, -1}, {0, 0, 1}}, {{0, 0, 1}, {1, 0, 0}}, {{0, 0, 1}, {-1, 0, 0}}, {{0, 0, -1}, {-1, 0, 0}}, {{0, 0, -1}, {1, 0, 0}}};
    private int at;
    private double au;
    private double av;
    private double aw;
    private double ax;
    private double ay;

    // CraftBukkit start
    public boolean slowWhenEmpty = true;
    private double derailedX = 0.5;
    private double derailedY = 0.5;
    private double derailedZ = 0.5;
    private double flyingX = 0.949999988079071D; // Paper - restore vanilla precision
    private double flyingY = 0.949999988079071D; // Paper - restore vanilla precision
    private double flyingZ = 0.949999988079071D; // Paper - restore vanilla precision
    public double maxSpeed = 0.4D;
    // CraftBukkit end

    protected EntityMinecartAbstract(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.i = true;
    }

    protected EntityMinecartAbstract(EntityTypes<?> entitytypes, World world, double d0, double d1, double d2) {
        this(entitytypes, world);
        this.setPosition(d0, d1, d2);
        this.setMot(Vec3D.a);
        this.lastX = d0;
        this.lastY = d1;
        this.lastZ = d2;
    }

    public static EntityMinecartAbstract a(World world, double d0, double d1, double d2, EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype) {
        return (EntityMinecartAbstract) (entityminecartabstract_enumminecarttype == EntityMinecartAbstract.EnumMinecartType.CHEST ? new EntityMinecartChest(world, d0, d1, d2) : (entityminecartabstract_enumminecarttype == EntityMinecartAbstract.EnumMinecartType.FURNACE ? new EntityMinecartFurnace(world, d0, d1, d2) : (entityminecartabstract_enumminecarttype == EntityMinecartAbstract.EnumMinecartType.TNT ? new EntityMinecartTNT(world, d0, d1, d2) : (entityminecartabstract_enumminecarttype == EntityMinecartAbstract.EnumMinecartType.SPAWNER ? new EntityMinecartMobSpawner(world, d0, d1, d2) : (entityminecartabstract_enumminecarttype == EntityMinecartAbstract.EnumMinecartType.HOPPER ? new EntityMinecartHopper(world, d0, d1, d2) : (entityminecartabstract_enumminecarttype == EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK ? new EntityMinecartCommandBlock(world, d0, d1, d2) : new EntityMinecartRideable(world, d0, d1, d2)))))));
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityMinecartAbstract.b, 0);
        this.datawatcher.register(EntityMinecartAbstract.c, 1);
        this.datawatcher.register(EntityMinecartAbstract.d, 0.0F);
        this.datawatcher.register(EntityMinecartAbstract.e, Block.getCombinedId(Blocks.AIR.getBlockData()));
        this.datawatcher.register(EntityMinecartAbstract.f, 6);
        this.datawatcher.register(EntityMinecartAbstract.g, false);
    }

    @Nullable
    @Override
    public AxisAlignedBB j(Entity entity) {
        return entity.isCollidable() ? entity.getBoundingBox() : null;
    }

    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public double aP() {
        return 0.0D;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (!this.world.isClientSide && !this.dead) {
            if (this.isInvulnerable(damagesource)) {
                return false;
            } else {
                // CraftBukkit start - fire VehicleDamageEvent
                Vehicle vehicle = (Vehicle) this.getBukkitEntity();
                org.bukkit.entity.Entity passenger = (damagesource.getEntity() == null) ? null : damagesource.getEntity().getBukkitEntity();

                VehicleDamageEvent event = new VehicleDamageEvent(vehicle, passenger, f);
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }

                f = (float) event.getDamage();
                // CraftBukkit end
                this.d(-this.n());
                this.c(10);
                this.velocityChanged();
                this.setDamage(this.getDamage() + f * 10.0F);
                boolean flag = damagesource.getEntity() instanceof EntityHuman && ((EntityHuman) damagesource.getEntity()).abilities.canInstantlyBuild;

                if (flag || this.getDamage() > 40.0F) {
                    // CraftBukkit start
                    VehicleDestroyEvent destroyEvent = new VehicleDestroyEvent(vehicle, passenger);
                    this.world.getServer().getPluginManager().callEvent(destroyEvent);

                    if (destroyEvent.isCancelled()) {
                        this.setDamage(40); // Maximize damage so this doesn't get triggered again right away
                        return true;
                    }
                    // CraftBukkit end
                    this.ejectPassengers();
                    if (flag && !this.hasCustomName()) {
                        this.die();
                    } else {
                        this.a(damagesource);
                    }
                }

                return true;
            }
        } else {
            return true;
        }
    }

    public void a(DamageSource damagesource) {
        this.die();
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack itemstack = new ItemStack(Items.MINECART);

            if (this.hasCustomName()) {
                itemstack.a(this.getCustomName());
            }

            this.a(itemstack);
        }

    }

    @Override
    public boolean isInteractable() {
        return !this.dead;
    }

    @Override
    public EnumDirection getAdjustedDirection() {
        return this.ar ? this.getDirection().opposite().e() : this.getDirection().e();
    }

    @Override
    public void tick() {
        // CraftBukkit start
        double prevX = this.locX;
        double prevY = this.locY;
        double prevZ = this.locZ;
        float prevYaw = this.yaw;
        float prevPitch = this.pitch;
        // CraftBukkit end

        if (this.getType() > 0) {
            this.c(this.getType() - 1);
        }

        if (this.getDamage() > 0.0F) {
            this.setDamage(this.getDamage() - 1.0F);
        }

        // Paper start - Configurable nether ceiling damage
        // Extracted to own function
        /*
        if (this.locY < -64.0D) {
            this.af();
        }
        */
        this.performVoidDamage();
        // Paper end

        // this.doPortalTick(); // CraftBukkit - handled in postTick
        if (this.world.isClientSide) {
            if (this.at > 0) {
                double d0 = this.locX + (this.au - this.locX) / (double) this.at;
                double d1 = this.locY + (this.av - this.locY) / (double) this.at;
                double d2 = this.locZ + (this.aw - this.locZ) / (double) this.at;
                double d3 = MathHelper.g(this.ax - (double) this.yaw);

                this.yaw = (float) ((double) this.yaw + d3 / (double) this.at);
                this.pitch = (float) ((double) this.pitch + (this.ay - (double) this.pitch) / (double) this.at);
                --this.at;
                this.setPosition(d0, d1, d2);
                this.setYawPitch(this.yaw, this.pitch);
            } else {
                this.setPosition(this.locX, this.locY, this.locZ);
                this.setYawPitch(this.yaw, this.pitch);
            }

        } else {
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            if (!this.isNoGravity()) {
                this.setMot(this.getMot().add(0.0D, -0.04D, 0.0D));
            }

            int i = MathHelper.floor(this.locX);
            int j = MathHelper.floor(this.locY);
            int k = MathHelper.floor(this.locZ);

            if (this.world.getType(new BlockPosition(i, j - 1, k)).a(TagsBlock.RAILS)) {
                --j;
            }

            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.a(TagsBlock.RAILS)) {
                this.b(blockposition, iblockdata);
                if (iblockdata.getBlock() == Blocks.ACTIVATOR_RAIL) {
                    this.a(i, j, k, (Boolean) iblockdata.get(BlockPoweredRail.POWERED));
                }
            } else {
                this.i();
            }

            this.checkBlockCollisions();
            this.pitch = 0.0F;
            double d4 = this.lastX - this.locX;
            double d5 = this.lastZ - this.locZ;

            if (d4 * d4 + d5 * d5 > 0.001D) {
                this.yaw = (float) (MathHelper.d(d5, d4) * 180.0D / 3.141592653589793D);
                if (this.ar) {
                    this.yaw += 180.0F;
                }
            }

            double d6 = (double) MathHelper.g(this.yaw - this.lastYaw);

            if (d6 < -170.0D || d6 >= 170.0D) {
                this.yaw += 180.0F;
                this.ar = !this.ar;
            }

            this.setYawPitch(this.yaw, this.pitch);
            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            Location from = new Location(bworld, prevX, prevY, prevZ, prevYaw, prevPitch);
            Location to = new Location(bworld, this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            Vehicle vehicle = (Vehicle) this.getBukkitEntity();

            this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleUpdateEvent(vehicle));

            if (!from.equals(to)) {
                this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.vehicle.VehicleMoveEvent(vehicle, from, to));
            }
            // CraftBukkit end
            if (this.getMinecartType() == EntityMinecartAbstract.EnumMinecartType.RIDEABLE && b(this.getMot()) > 0.01D) {
                List<Entity> list = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D), IEntitySelector.a(this));

                if (!list.isEmpty()) {
                    for (int l = 0; l < list.size(); ++l) {
                        Entity entity = (Entity) list.get(l);

                        if (!(entity instanceof EntityHuman) && !(entity instanceof EntityIronGolem) && !(entity instanceof EntityMinecartAbstract) && !this.isVehicle() && !entity.isPassenger()) {
                            // CraftBukkit start
                            VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, entity.getBukkitEntity());
                            this.world.getServer().getPluginManager().callEvent(collisionEvent);

                            if (collisionEvent.isCancelled()) {
                                continue;
                            }
                            // CraftBukkit end
                            entity.startRiding(this);
                        } else {
                            // CraftBukkit start
                            VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, entity.getBukkitEntity());
                            this.world.getServer().getPluginManager().callEvent(collisionEvent);

                            if (collisionEvent.isCancelled()) {
                                continue;
                            }
                            // CraftBukkit end
                            entity.collide(this);
                        }
                    }
                }
            } else {
                Iterator iterator = this.world.getEntities(this, this.getBoundingBox().grow(0.20000000298023224D, 0.0D, 0.20000000298023224D)).iterator();

                while (iterator.hasNext()) {
                    Entity entity1 = (Entity) iterator.next();

                    if (!this.w(entity1) && entity1.isCollidable() && entity1 instanceof EntityMinecartAbstract) {
                        // CraftBukkit start
                        VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent(vehicle, entity1.getBukkitEntity());
                        this.world.getServer().getPluginManager().callEvent(collisionEvent);

                        if (collisionEvent.isCancelled()) {
                            continue;
                        }
                        // CraftBukkit end
                        entity1.collide(this);
                    }
                }
            }

            this.ay();
        }
    }

    protected double getMaxSpeed() {
        return this.maxSpeed; // CraftBukkit
    }

    public void a(int i, int j, int k, boolean flag) {}

    protected void i() {
        double d0 = this.getMaxSpeed();
        Vec3D vec3d = this.getMot();

        this.setMot(MathHelper.a(vec3d.x, -d0, d0), vec3d.y, MathHelper.a(vec3d.z, -d0, d0));
        if (this.onGround) {
            // CraftBukkit start - replace magic numbers with our variables
            this.setMot(new Vec3D(this.getMot().x * this.derailedX, this.getMot().y * this.derailedY, this.getMot().z * this.derailedZ));
            // CraftBukkit end
        }

        this.move(EnumMoveType.SELF, this.getMot());
        if (!this.onGround) {
            // CraftBukkit start - replace magic numbers with our variables
            this.setMot(new Vec3D(this.getMot().x * this.flyingX, this.getMot().y * this.flyingY, this.getMot().z * this.flyingZ));
            // CraftBukkit end
        }

    }

    protected void b(BlockPosition blockposition, IBlockData iblockdata) {
        this.fallDistance = 0.0F;
        Vec3D vec3d = this.l(this.locX, this.locY, this.locZ);

        this.locY = (double) blockposition.getY();
        boolean flag = false;
        boolean flag1 = false;
        BlockMinecartTrackAbstract blockminecarttrackabstract = (BlockMinecartTrackAbstract) iblockdata.getBlock();

        if (blockminecarttrackabstract == Blocks.POWERED_RAIL) {
            flag = (Boolean) iblockdata.get(BlockPoweredRail.POWERED);
            flag1 = !flag;
        }

        double d0 = 0.0078125D;
        Vec3D vec3d1 = this.getMot();
        BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(blockminecarttrackabstract.e());

        switch (blockpropertytrackposition) {
            case ASCENDING_EAST:
                this.setMot(vec3d1.add(-0.0078125D, 0.0D, 0.0D));
                ++this.locY;
                break;
            case ASCENDING_WEST:
                this.setMot(vec3d1.add(0.0078125D, 0.0D, 0.0D));
                ++this.locY;
                break;
            case ASCENDING_NORTH:
                this.setMot(vec3d1.add(0.0D, 0.0D, 0.0078125D));
                ++this.locY;
                break;
            case ASCENDING_SOUTH:
                this.setMot(vec3d1.add(0.0D, 0.0D, -0.0078125D));
                ++this.locY;
        }

        vec3d1 = this.getMot();
        int[][] aint = EntityMinecartAbstract.as[blockpropertytrackposition.a()];
        double d1 = (double) (aint[1][0] - aint[0][0]);
        double d2 = (double) (aint[1][2] - aint[0][2]);
        double d3 = Math.sqrt(d1 * d1 + d2 * d2);
        double d4 = vec3d1.x * d1 + vec3d1.z * d2;

        if (d4 < 0.0D) {
            d1 = -d1;
            d2 = -d2;
        }

        double d5 = Math.min(2.0D, Math.sqrt(b(vec3d1)));

        vec3d1 = new Vec3D(d5 * d1 / d3, vec3d1.y, d5 * d2 / d3);
        this.setMot(vec3d1);
        Entity entity = this.getPassengers().isEmpty() ? null : (Entity) this.getPassengers().get(0);

        if (entity instanceof EntityHuman) {
            Vec3D vec3d2 = entity.getMot();
            double d6 = b(vec3d2);
            double d7 = b(this.getMot());

            if (d6 > 1.0E-4D && d7 < 0.01D) {
                this.setMot(this.getMot().add(vec3d2.x * 0.1D, 0.0D, vec3d2.z * 0.1D));
                flag1 = false;
            }
        }

        double d8;

        if (flag1) {
            d8 = Math.sqrt(b(this.getMot()));
            if (d8 < 0.03D) {
                this.setMot(Vec3D.a);
            } else {
                this.setMot(this.getMot().d(0.5D, 0.0D, 0.5D));
            }
        }

        d8 = (double) blockposition.getX() + 0.5D + (double) aint[0][0] * 0.5D;
        double d9 = (double) blockposition.getZ() + 0.5D + (double) aint[0][2] * 0.5D;
        double d10 = (double) blockposition.getX() + 0.5D + (double) aint[1][0] * 0.5D;
        double d11 = (double) blockposition.getZ() + 0.5D + (double) aint[1][2] * 0.5D;

        d1 = d10 - d8;
        d2 = d11 - d9;
        double d12;
        double d13;
        double d14;

        if (d1 == 0.0D) {
            this.locX = (double) blockposition.getX() + 0.5D;
            d12 = this.locZ - (double) blockposition.getZ();
        } else if (d2 == 0.0D) {
            this.locZ = (double) blockposition.getZ() + 0.5D;
            d12 = this.locX - (double) blockposition.getX();
        } else {
            d13 = this.locX - d8;
            d14 = this.locZ - d9;
            d12 = (d13 * d1 + d14 * d2) * 2.0D;
        }

        this.locX = d8 + d1 * d12;
        this.locZ = d9 + d2 * d12;
        this.setPosition(this.locX, this.locY, this.locZ);
        d13 = this.isVehicle() ? 0.75D : 1.0D;
        d14 = this.getMaxSpeed();
        vec3d1 = this.getMot();
        this.move(EnumMoveType.SELF, new Vec3D(MathHelper.a(d13 * vec3d1.x, -d14, d14), 0.0D, MathHelper.a(d13 * vec3d1.z, -d14, d14)));
        if (aint[0][1] != 0 && MathHelper.floor(this.locX) - blockposition.getX() == aint[0][0] && MathHelper.floor(this.locZ) - blockposition.getZ() == aint[0][2]) {
            this.setPosition(this.locX, this.locY + (double) aint[0][1], this.locZ);
        } else if (aint[1][1] != 0 && MathHelper.floor(this.locX) - blockposition.getX() == aint[1][0] && MathHelper.floor(this.locZ) - blockposition.getZ() == aint[1][2]) {
            this.setPosition(this.locX, this.locY + (double) aint[1][1], this.locZ);
        }

        this.decelerate();
        Vec3D vec3d3 = this.l(this.locX, this.locY, this.locZ);
        Vec3D vec3d4;
        double d15;

        if (vec3d3 != null && vec3d != null) {
            double d16 = (vec3d.y - vec3d3.y) * 0.05D;

            vec3d4 = this.getMot();
            d15 = Math.sqrt(b(vec3d4));
            if (d15 > 0.0D) {
                this.setMot(vec3d4.d((d15 + d16) / d15, 1.0D, (d15 + d16) / d15));
            }

            this.setPosition(this.locX, vec3d3.y, this.locZ);
        }

        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locZ);

        if (i != blockposition.getX() || j != blockposition.getZ()) {
            vec3d4 = this.getMot();
            d15 = Math.sqrt(b(vec3d4));
            this.setMot(d15 * (double) (i - blockposition.getX()), vec3d4.y, d15 * (double) (j - blockposition.getZ()));
        }

        if (flag) {
            vec3d4 = this.getMot();
            d15 = Math.sqrt(b(vec3d4));
            if (d15 > 0.01D) {
                double d17 = 0.06D;

                this.setMot(vec3d4.add(vec3d4.x / d15 * 0.06D, 0.0D, vec3d4.z / d15 * 0.06D));
            } else {
                Vec3D vec3d5 = this.getMot();
                double d18 = vec3d5.x;
                double d19 = vec3d5.z;

                if (blockpropertytrackposition == BlockPropertyTrackPosition.EAST_WEST) {
                    if (this.a(blockposition.west())) {
                        d18 = 0.02D;
                    } else if (this.a(blockposition.east())) {
                        d18 = -0.02D;
                    }
                } else {
                    if (blockpropertytrackposition != BlockPropertyTrackPosition.NORTH_SOUTH) {
                        return;
                    }

                    if (this.a(blockposition.north())) {
                        d19 = 0.02D;
                    } else if (this.a(blockposition.south())) {
                        d19 = -0.02D;
                    }
                }

                this.setMot(d18, vec3d5.y, d19);
            }
        }

    }

    private boolean a(BlockPosition blockposition) {
        return this.world.getType(blockposition).isOccluding(this.world, blockposition);
    }

    protected void decelerate() {
        double d0 = this.isVehicle() || !this.slowWhenEmpty ? 0.997D : 0.96D; // CraftBukkit - add !this.slowWhenEmpty

        this.setMot(this.getMot().d(d0, 0.0D, d0));
    }

    @Nullable
    public Vec3D l(double d0, double d1, double d2) {
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);

        if (this.world.getType(new BlockPosition(i, j - 1, k)).a(TagsBlock.RAILS)) {
            --j;
        }

        IBlockData iblockdata = this.world.getType(new BlockPosition(i, j, k));

        if (iblockdata.a(TagsBlock.RAILS)) {
            BlockPropertyTrackPosition blockpropertytrackposition = (BlockPropertyTrackPosition) iblockdata.get(((BlockMinecartTrackAbstract) iblockdata.getBlock()).e());
            int[][] aint = EntityMinecartAbstract.as[blockpropertytrackposition.a()];
            double d3 = (double) i + 0.5D + (double) aint[0][0] * 0.5D;
            double d4 = (double) j + 0.0625D + (double) aint[0][1] * 0.5D;
            double d5 = (double) k + 0.5D + (double) aint[0][2] * 0.5D;
            double d6 = (double) i + 0.5D + (double) aint[1][0] * 0.5D;
            double d7 = (double) j + 0.0625D + (double) aint[1][1] * 0.5D;
            double d8 = (double) k + 0.5D + (double) aint[1][2] * 0.5D;
            double d9 = d6 - d3;
            double d10 = (d7 - d4) * 2.0D;
            double d11 = d8 - d5;
            double d12;

            if (d9 == 0.0D) {
                d12 = d2 - (double) k;
            } else if (d11 == 0.0D) {
                d12 = d0 - (double) i;
            } else {
                double d13 = d0 - d3;
                double d14 = d2 - d5;

                d12 = (d13 * d9 + d14 * d11) * 2.0D;
            }

            d0 = d3 + d9 * d12;
            d1 = d4 + d10 * d12;
            d2 = d5 + d11 * d12;
            if (d10 < 0.0D) {
                ++d1;
            }

            if (d10 > 0.0D) {
                d1 += 0.5D;
            }

            return new Vec3D(d0, d1, d2);
        } else {
            return null;
        }
    }

    @Override
    protected void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.getBoolean("CustomDisplayTile")) {
            this.setDisplayBlock(GameProfileSerializer.d(nbttagcompound.getCompound("DisplayState")));
            this.setDisplayBlockOffset(nbttagcompound.getInt("DisplayOffset"));
        }

    }

    @Override
    protected void b(NBTTagCompound nbttagcompound) {
        if (this.u()) {
            nbttagcompound.setBoolean("CustomDisplayTile", true);
            nbttagcompound.set("DisplayState", GameProfileSerializer.a(this.getDisplayBlock()));
            nbttagcompound.setInt("DisplayOffset", this.getDisplayBlockOffset());
        }

    }

    @Override
    public void collide(Entity entity) {
        if (!this.world.isClientSide) {
            if (!entity.noclip && !this.noclip) {
                if (!this.w(entity)) {
                    // CraftBukkit start
                    VehicleEntityCollisionEvent collisionEvent = new VehicleEntityCollisionEvent((Vehicle) this.getBukkitEntity(), entity.getBukkitEntity());
                    this.world.getServer().getPluginManager().callEvent(collisionEvent);

                    if (collisionEvent.isCancelled()) {
                        return;
                    }
                    // CraftBukkit end
                    double d0 = entity.locX - this.locX;
                    double d1 = entity.locZ - this.locZ;
                    double d2 = d0 * d0 + d1 * d1;

                    if (d2 >= 9.999999747378752E-5D) {
                        d2 = (double) MathHelper.sqrt(d2);
                        d0 /= d2;
                        d1 /= d2;
                        double d3 = 1.0D / d2;

                        if (d3 > 1.0D) {
                            d3 = 1.0D;
                        }

                        d0 *= d3;
                        d1 *= d3;
                        d0 *= 0.10000000149011612D;
                        d1 *= 0.10000000149011612D;
                        d0 *= (double) (1.0F - this.M);
                        d1 *= (double) (1.0F - this.M);
                        d0 *= 0.5D;
                        d1 *= 0.5D;
                        if (entity instanceof EntityMinecartAbstract) {
                            double d4 = entity.locX - this.locX;
                            double d5 = entity.locZ - this.locZ;
                            Vec3D vec3d = (new Vec3D(d4, 0.0D, d5)).d();
                            Vec3D vec3d1 = (new Vec3D((double) MathHelper.cos(this.yaw * 0.017453292F), 0.0D, (double) MathHelper.sin(this.yaw * 0.017453292F))).d();
                            double d6 = Math.abs(vec3d.b(vec3d1));

                            if (d6 < 0.800000011920929D) {
                                return;
                            }

                            Vec3D vec3d2 = this.getMot();
                            Vec3D vec3d3 = entity.getMot();

                            if (((EntityMinecartAbstract) entity).getMinecartType() == EntityMinecartAbstract.EnumMinecartType.FURNACE && this.getMinecartType() != EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                                this.setMot(vec3d2.d(0.2D, 1.0D, 0.2D));
                                this.f(vec3d3.x - d0, 0.0D, vec3d3.z - d1);
                                entity.setMot(vec3d3.d(0.95D, 1.0D, 0.95D));
                            } else if (((EntityMinecartAbstract) entity).getMinecartType() != EntityMinecartAbstract.EnumMinecartType.FURNACE && this.getMinecartType() == EntityMinecartAbstract.EnumMinecartType.FURNACE) {
                                entity.setMot(vec3d3.d(0.2D, 1.0D, 0.2D));
                                entity.f(vec3d2.x + d0, 0.0D, vec3d2.z + d1);
                                this.setMot(vec3d2.d(0.95D, 1.0D, 0.95D));
                            } else {
                                double d7 = (vec3d3.x + vec3d2.x) / 2.0D;
                                double d8 = (vec3d3.z + vec3d2.z) / 2.0D;

                                this.setMot(vec3d2.d(0.2D, 1.0D, 0.2D));
                                this.f(d7 - d0, 0.0D, d8 - d1);
                                entity.setMot(vec3d3.d(0.2D, 1.0D, 0.2D));
                                entity.f(d7 + d0, 0.0D, d8 + d1);
                            }
                        } else {
                            this.f(-d0, 0.0D, -d1);
                            entity.f(d0 / 4.0D, 0.0D, d1 / 4.0D);
                        }
                    }

                }
            }
        }
    }

    public void setDamage(float f) {
        this.datawatcher.set(EntityMinecartAbstract.d, f);
    }

    public float getDamage() {
        return (Float) this.datawatcher.get(EntityMinecartAbstract.d);
    }

    public void c(int i) {
        this.datawatcher.set(EntityMinecartAbstract.b, i);
    }

    public int getType() {
        return (Integer) this.datawatcher.get(EntityMinecartAbstract.b);
    }

    public void d(int i) {
        this.datawatcher.set(EntityMinecartAbstract.c, i);
    }

    public int n() {
        return (Integer) this.datawatcher.get(EntityMinecartAbstract.c);
    }

    public abstract EntityMinecartAbstract.EnumMinecartType getMinecartType();

    public IBlockData getDisplayBlock() {
        return !this.u() ? this.q() : Block.getByCombinedId((Integer) this.getDataWatcher().get(EntityMinecartAbstract.e));
    }

    public IBlockData q() {
        return Blocks.AIR.getBlockData();
    }

    public int getDisplayBlockOffset() {
        return !this.u() ? this.s() : (Integer) this.getDataWatcher().get(EntityMinecartAbstract.f);
    }

    public int s() {
        return 6;
    }

    public void setDisplayBlock(IBlockData iblockdata) {
        this.getDataWatcher().set(EntityMinecartAbstract.e, Block.getCombinedId(iblockdata));
        this.a(true);
    }

    public void setDisplayBlockOffset(int i) {
        this.getDataWatcher().set(EntityMinecartAbstract.f, i);
        this.a(true);
    }

    public boolean u() {
        return (Boolean) this.getDataWatcher().get(EntityMinecartAbstract.g);
    }

    public void a(boolean flag) {
        this.getDataWatcher().set(EntityMinecartAbstract.g, flag);
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntity(this);
    }

    public static enum EnumMinecartType {

        RIDEABLE, CHEST, FURNACE, TNT, SPAWNER, HOPPER, COMMAND_BLOCK;

        private EnumMinecartType() {}
    }

    // CraftBukkit start - Methods for getting and setting flying and derailed velocity modifiers
    public Vector getFlyingVelocityMod() {
        return new Vector(flyingX, flyingY, flyingZ);
    }

    public void setFlyingVelocityMod(Vector flying) {
        flyingX = flying.getX();
        flyingY = flying.getY();
        flyingZ = flying.getZ();
    }

    public Vector getDerailedVelocityMod() {
        return new Vector(derailedX, derailedY, derailedZ);
    }

    public void setDerailedVelocityMod(Vector derailed) {
        derailedX = derailed.getX();
        derailedY = derailed.getY();
        derailedZ = derailed.getZ();
    }
    // CraftBukkit end
}
