package net.minecraft.server;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements INamableTileEntity, ICommandListener {

    protected static final Logger i = LogManager.getLogger();
    private static final List<ItemStack> a = Collections.emptyList();
    private static final AxisAlignedBB b = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
    private static double c = 1.0D;
    private static int entityCount;
    private final EntityTypes<?> g;
    private int id;
    public boolean j;
    public final List<Entity> passengers;
    protected int k;
    private Entity vehicle;
    public boolean attachedToPlayer;
    public World world;
    public double lastX;
    public double lastY;
    public double lastZ;
    public double locX;
    public double locY;
    public double locZ;
    public double motX;
    public double motY;
    public double motZ;
    public float yaw;
    public float pitch;
    public float lastYaw;
    public float lastPitch;
    private AxisAlignedBB boundingBox;
    public boolean onGround;
    public boolean positionChanged;
    public boolean C;
    public boolean D;
    public boolean velocityChanged;
    protected boolean F;
    private boolean az;
    public boolean dead;
    public float width;
    public float length;
    public float J;
    public float K;
    public float L;
    public float fallDistance;
    private float aA;
    private float aB;
    public double N;
    public double O;
    public double P;
    public float Q;
    public boolean noclip;
    public float S;
    protected Random random;
    public int ticksLived;
    public int fireTicks;
    public boolean inWater;
    protected double W;
    protected boolean X;
    public int noDamageTicks;
    protected boolean justCreated;
    protected boolean fireProof;
    protected DataWatcher datawatcher;
    protected static final DataWatcherObject<Byte> ac = DataWatcher.a(Entity.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Integer> aD = DataWatcher.a(Entity.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Optional<IChatBaseComponent>> aE = DataWatcher.a(Entity.class, DataWatcherRegistry.f);
    private static final DataWatcherObject<Boolean> aF = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> aG = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> aH = DataWatcher.a(Entity.class, DataWatcherRegistry.i);
    public boolean inChunk;
    public int chunkX;
    public int chunkY;
    public int chunkZ;
    public boolean ak;
    public boolean impulse;
    public int portalCooldown;
    protected boolean an;
    protected int ao;
    public DimensionManager dimension;
    protected BlockPosition aq;
    protected Vec3D ar;
    protected EnumDirection as;
    private boolean invulnerable;
    protected UUID uniqueID;
    protected String au;
    public boolean glowing;
    private final Set<String> aJ;
    private boolean aK;
    private final double[] aL;
    private long aM;

    public Entity(EntityTypes<?> entitytypes, World world) {
        this.id = Entity.entityCount++;
        this.passengers = Lists.newArrayList();
        this.boundingBox = Entity.b;
        this.width = 0.6F;
        this.length = 1.8F;
        this.aA = 1.0F;
        this.aB = 1.0F;
        this.random = new Random();
        this.fireTicks = -this.getMaxFireTicks();
        this.justCreated = true;
        this.uniqueID = MathHelper.a(this.random);
        this.au = this.uniqueID.toString();
        this.aJ = Sets.newHashSet();
        this.aL = new double[] { 0.0D, 0.0D, 0.0D};
        this.g = entitytypes;
        this.world = world;
        this.setPosition(0.0D, 0.0D, 0.0D);
        if (world != null) {
            this.dimension = world.worldProvider.getDimensionManager();
        }

        this.datawatcher = new DataWatcher(this);
        this.datawatcher.register(Entity.ac, (byte) 0);
        this.datawatcher.register(Entity.aD, this.bf());
        this.datawatcher.register(Entity.aF, false);
        this.datawatcher.register(Entity.aE, Optional.empty());
        this.datawatcher.register(Entity.aG, false);
        this.datawatcher.register(Entity.aH, false);
        this.x_();
    }

    public EntityTypes<?> P() {
        return this.g;
    }

    public int getId() {
        return this.id;
    }

    public void e(int i) {
        this.id = i;
    }

    public Set<String> getScoreboardTags() {
        return this.aJ;
    }

    public boolean addScoreboardTag(String s) {
        return this.aJ.size() >= 1024 ? false : this.aJ.add(s);
    }

    public boolean removeScoreboardTag(String s) {
        return this.aJ.remove(s);
    }

    public void killEntity() {
        this.die();
    }

    protected abstract void x_();

    public DataWatcher getDataWatcher() {
        return this.datawatcher;
    }

    public boolean equals(Object object) {
        return object instanceof Entity ? ((Entity) object).id == this.id : false;
    }

    public int hashCode() {
        return this.id;
    }

    public void die() {
        this.dead = true;
    }

    public void b(boolean flag) {}

    public void setSize(float f, float f1) {
        if (f != this.width || f1 != this.length) {
            float f2 = this.width;

            this.width = f;
            this.length = f1;
            if (this.width < f2) {
                double d0 = (double) f / 2.0D;

                this.a(new AxisAlignedBB(this.locX - d0, this.locY, this.locZ - d0, this.locX + d0, this.locY + (double) this.length, this.locZ + d0));
                return;
            }

            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            this.a(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double) this.width, axisalignedbb.minY + (double) this.length, axisalignedbb.minZ + (double) this.width));
            if (this.width > f2 && !this.justCreated && !this.world.isClientSide) {
                this.move(EnumMoveType.SELF, (double) (f2 - this.width), 0.0D, (double) (f2 - this.width));
            }
        }

    }

    protected void setYawPitch(float f, float f1) {
        this.yaw = f % 360.0F;
        this.pitch = f1 % 360.0F;
    }

    public void setPosition(double d0, double d1, double d2) {
        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        float f = this.width / 2.0F;
        float f1 = this.length;

        this.a(new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) f1, d2 + (double) f));
    }

    public void tick() {
        if (!this.world.isClientSide) {
            this.setFlag(6, this.bc());
        }

        this.W();
    }

    public void W() {
        this.world.methodProfiler.enter("entityBaseTick");
        if (this.isPassenger() && this.getVehicle().dead) {
            this.stopRiding();
        }

        if (this.k > 0) {
            --this.k;
        }

        this.J = this.K;
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        if (!this.world.isClientSide && this.world instanceof WorldServer) {
            this.world.methodProfiler.enter("portal");
            if (this.an) {
                MinecraftServer minecraftserver = this.world.getMinecraftServer();

                if (minecraftserver.getAllowNether()) {
                    if (!this.isPassenger()) {
                        int i = this.X();

                        if (this.ao++ >= i) {
                            this.ao = i;
                            this.portalCooldown = this.aQ();
                            DimensionManager dimensionmanager;

                            if (this.world.worldProvider.getDimensionManager() == DimensionManager.NETHER) {
                                dimensionmanager = DimensionManager.OVERWORLD;
                            } else {
                                dimensionmanager = DimensionManager.NETHER;
                            }

                            this.a(dimensionmanager);
                        }
                    }

                    this.an = false;
                }
            } else {
                if (this.ao > 0) {
                    this.ao -= 4;
                }

                if (this.ao < 0) {
                    this.ao = 0;
                }
            }

            this.E();
            this.world.methodProfiler.exit();
        }

        this.av();
        this.r();
        if (this.world.isClientSide) {
            this.extinguish();
        } else if (this.fireTicks > 0) {
            if (this.fireProof) {
                this.fireTicks -= 4;
                if (this.fireTicks < 0) {
                    this.extinguish();
                }
            } else {
                if (this.fireTicks % 20 == 0) {
                    this.damageEntity(DamageSource.BURN, 1.0F);
                }

                --this.fireTicks;
            }
        }

        if (this.ax()) {
            this.burnFromLava();
            this.fallDistance *= 0.5F;
        }

        if (this.locY < -64.0D) {
            this.aa();
        }

        if (!this.world.isClientSide) {
            this.setFlag(0, this.fireTicks > 0);
        }

        this.justCreated = false;
        this.world.methodProfiler.exit();
    }

    protected void E() {
        if (this.portalCooldown > 0) {
            --this.portalCooldown;
        }

    }

    public int X() {
        return 1;
    }

    protected void burnFromLava() {
        if (!this.fireProof) {
            this.setOnFire(15);
            this.damageEntity(DamageSource.LAVA, 4.0F);
        }
    }

    public void setOnFire(int i) {
        int j = i * 20;

        if (this instanceof EntityLiving) {
            j = EnchantmentProtection.a((EntityLiving) this, j);
        }

        if (this.fireTicks < j) {
            this.fireTicks = j;
        }

    }

    public void extinguish() {
        this.fireTicks = 0;
    }

    protected void aa() {
        this.die();
    }

    public boolean c(double d0, double d1, double d2) {
        return this.b(this.getBoundingBox().d(d0, d1, d2));
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        return this.world.getCubes(this, axisalignedbb) && !this.world.containsLiquid(axisalignedbb);
    }

    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        if (this.noclip) {
            this.a(this.getBoundingBox().d(d0, d1, d2));
            this.recalcPosition();
        } else {
            if (enummovetype == EnumMoveType.PISTON) {
                long i = this.world.getTime();

                if (i != this.aM) {
                    Arrays.fill(this.aL, 0.0D);
                    this.aM = i;
                }

                int j;
                double d3;

                if (d0 != 0.0D) {
                    j = EnumDirection.EnumAxis.X.ordinal();
                    d3 = MathHelper.a(d0 + this.aL[j], -0.51D, 0.51D);
                    d0 = d3 - this.aL[j];
                    this.aL[j] = d3;
                    if (Math.abs(d0) <= 9.999999747378752E-6D) {
                        return;
                    }
                } else if (d1 != 0.0D) {
                    j = EnumDirection.EnumAxis.Y.ordinal();
                    d3 = MathHelper.a(d1 + this.aL[j], -0.51D, 0.51D);
                    d1 = d3 - this.aL[j];
                    this.aL[j] = d3;
                    if (Math.abs(d1) <= 9.999999747378752E-6D) {
                        return;
                    }
                } else {
                    if (d2 == 0.0D) {
                        return;
                    }

                    j = EnumDirection.EnumAxis.Z.ordinal();
                    d3 = MathHelper.a(d2 + this.aL[j], -0.51D, 0.51D);
                    d2 = d3 - this.aL[j];
                    this.aL[j] = d3;
                    if (Math.abs(d2) <= 9.999999747378752E-6D) {
                        return;
                    }
                }
            }

            this.world.methodProfiler.enter("move");
            double d4 = this.locX;
            double d5 = this.locY;
            double d6 = this.locZ;

            if (this.F) {
                this.F = false;
                d0 *= 0.25D;
                d1 *= 0.05000000074505806D;
                d2 *= 0.25D;
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            double d7 = d0;
            double d8 = d1;
            double d9 = d2;

            if ((enummovetype == EnumMoveType.SELF || enummovetype == EnumMoveType.PLAYER) && this.onGround && this.isSneaking() && this instanceof EntityHuman) {
                for (double d10 = 0.05D; d0 != 0.0D && this.world.getCubes(this, this.getBoundingBox().d(d0, (double) (-this.Q), 0.0D)); d7 = d0) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }
                }

                for (; d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().d(0.0D, (double) (-this.Q), d2)); d9 = d2) {
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }

                for (; d0 != 0.0D && d2 != 0.0D && this.world.getCubes(this, this.getBoundingBox().d(d0, (double) (-this.Q), d2)); d9 = d2) {
                    if (d0 < 0.05D && d0 >= -0.05D) {
                        d0 = 0.0D;
                    } else if (d0 > 0.0D) {
                        d0 -= 0.05D;
                    } else {
                        d0 += 0.05D;
                    }

                    d7 = d0;
                    if (d2 < 0.05D && d2 >= -0.05D) {
                        d2 = 0.0D;
                    } else if (d2 > 0.0D) {
                        d2 -= 0.05D;
                    } else {
                        d2 += 0.05D;
                    }
                }
            }

            AxisAlignedBB axisalignedbb = this.getBoundingBox();

            if (d0 != 0.0D || d1 != 0.0D || d2 != 0.0D) {
                StreamAccumulator<VoxelShape> streamaccumulator = new StreamAccumulator<>(this.world.a(this, this.getBoundingBox(), d0, d1, d2));

                if (d1 != 0.0D) {
                    d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.getBoundingBox(), streamaccumulator.a(), d1);
                    this.a(this.getBoundingBox().d(0.0D, d1, 0.0D));
                }

                if (d0 != 0.0D) {
                    d0 = VoxelShapes.a(EnumDirection.EnumAxis.X, this.getBoundingBox(), streamaccumulator.a(), d0);
                    if (d0 != 0.0D) {
                        this.a(this.getBoundingBox().d(d0, 0.0D, 0.0D));
                    }
                }

                if (d2 != 0.0D) {
                    d2 = VoxelShapes.a(EnumDirection.EnumAxis.Z, this.getBoundingBox(), streamaccumulator.a(), d2);
                    if (d2 != 0.0D) {
                        this.a(this.getBoundingBox().d(0.0D, 0.0D, d2));
                    }
                }
            }

            boolean flag = this.onGround || d1 != d1 && d1 < 0.0D;
            double d11;

            if (this.Q > 0.0F && flag && (d7 != d0 || d9 != d2)) {
                double d12 = d0;
                double d13 = d1;
                double d14 = d2;
                AxisAlignedBB axisalignedbb1 = this.getBoundingBox();

                this.a(axisalignedbb);
                d0 = d7;
                d1 = (double) this.Q;
                d2 = d9;
                if (d7 != 0.0D || d1 != 0.0D || d9 != 0.0D) {
                    StreamAccumulator<VoxelShape> streamaccumulator1 = new StreamAccumulator<>(this.world.a(this, this.getBoundingBox(), d7, d1, d9));
                    AxisAlignedBB axisalignedbb2 = this.getBoundingBox();
                    AxisAlignedBB axisalignedbb3 = axisalignedbb2.b(d7, 0.0D, d9);

                    d11 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb3, streamaccumulator1.a(), d1);
                    if (d11 != 0.0D) {
                        axisalignedbb2 = axisalignedbb2.d(0.0D, d11, 0.0D);
                    }

                    double d15 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb2, streamaccumulator1.a(), d7);

                    if (d15 != 0.0D) {
                        axisalignedbb2 = axisalignedbb2.d(d15, 0.0D, 0.0D);
                    }

                    double d16 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb2, streamaccumulator1.a(), d9);

                    if (d16 != 0.0D) {
                        axisalignedbb2 = axisalignedbb2.d(0.0D, 0.0D, d16);
                    }

                    AxisAlignedBB axisalignedbb4 = this.getBoundingBox();
                    double d17 = VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb4, streamaccumulator1.a(), d1);

                    if (d17 != 0.0D) {
                        axisalignedbb4 = axisalignedbb4.d(0.0D, d17, 0.0D);
                    }

                    double d18 = VoxelShapes.a(EnumDirection.EnumAxis.X, axisalignedbb4, streamaccumulator1.a(), d7);

                    if (d18 != 0.0D) {
                        axisalignedbb4 = axisalignedbb4.d(d18, 0.0D, 0.0D);
                    }

                    double d19 = VoxelShapes.a(EnumDirection.EnumAxis.Z, axisalignedbb4, streamaccumulator1.a(), d9);

                    if (d19 != 0.0D) {
                        axisalignedbb4 = axisalignedbb4.d(0.0D, 0.0D, d19);
                    }

                    double d20 = d15 * d15 + d16 * d16;
                    double d21 = d18 * d18 + d19 * d19;

                    if (d20 > d21) {
                        d0 = d15;
                        d2 = d16;
                        d1 = -d11;
                        this.a(axisalignedbb2);
                    } else {
                        d0 = d18;
                        d2 = d19;
                        d1 = -d17;
                        this.a(axisalignedbb4);
                    }

                    d1 = VoxelShapes.a(EnumDirection.EnumAxis.Y, this.getBoundingBox(), streamaccumulator1.a(), d1);
                    if (d1 != 0.0D) {
                        this.a(this.getBoundingBox().d(0.0D, d1, 0.0D));
                    }
                }

                if (d12 * d12 + d14 * d14 >= d0 * d0 + d2 * d2) {
                    d0 = d12;
                    d1 = d13;
                    d2 = d14;
                    this.a(axisalignedbb1);
                }
            }

            this.world.methodProfiler.exit();
            this.world.methodProfiler.enter("rest");
            this.recalcPosition();
            this.positionChanged = d7 != d0 || d9 != d2;
            this.C = d1 != d1;
            this.onGround = this.C && d8 < 0.0D;
            this.D = this.positionChanged || this.C;
            int k = MathHelper.floor(this.locX);
            int l = MathHelper.floor(this.locY - 0.20000000298023224D);
            int i1 = MathHelper.floor(this.locZ);
            BlockPosition blockposition = new BlockPosition(k, l, i1);
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.isAir()) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata1 = this.world.getType(blockposition1);
                Block block = iblockdata1.getBlock();

                if (block instanceof BlockFence || block instanceof BlockCobbleWall || block instanceof BlockFenceGate) {
                    iblockdata = iblockdata1;
                    blockposition = blockposition1;
                }
            }

            this.a(d1, this.onGround, iblockdata, blockposition);
            if (d7 != d0) {
                this.motX = 0.0D;
            }

            if (d9 != d2) {
                this.motZ = 0.0D;
            }

            Block block1 = iblockdata.getBlock();

            if (d8 != d1) {
                block1.a((IBlockAccess) this.world, this);
            }

            if (this.playStepSound() && (!this.onGround || !this.isSneaking() || !(this instanceof EntityHuman)) && !this.isPassenger()) {
                double d22 = this.locX - d4;
                double d23 = this.locY - d5;

                d11 = this.locZ - d6;
                if (block1 != Blocks.LADDER) {
                    d23 = 0.0D;
                }

                if (block1 != null && this.onGround) {
                    block1.stepOn(this.world, blockposition, this);
                }

                this.K = (float) ((double) this.K + (double) MathHelper.sqrt(d22 * d22 + d11 * d11) * 0.6D);
                this.L = (float) ((double) this.L + (double) MathHelper.sqrt(d22 * d22 + d23 * d23 + d11 * d11) * 0.6D);
                if (this.L > this.aA && !iblockdata.isAir()) {
                    this.aA = this.ab();
                    if (this.isInWater()) {
                        Entity entity = this.isVehicle() && this.bO() != null ? this.bO() : this;
                        float f = entity == this ? 0.35F : 0.4F;
                        float f1 = MathHelper.sqrt(entity.motX * entity.motX * 0.20000000298023224D + entity.motY * entity.motY + entity.motZ * entity.motZ * 0.20000000298023224D) * f;

                        if (f1 > 1.0F) {
                            f1 = 1.0F;
                        }

                        this.d(f1);
                    } else {
                        this.a(blockposition, iblockdata);
                    }
                } else if (this.L > this.aB && this.ah() && iblockdata.isAir()) {
                    this.aB = this.e(this.L);
                }
            }

            try {
                this.checkBlockCollisions();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Checking entity block collision");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being checked for collision");

                this.appendEntityCrashDetails(crashreportsystemdetails);
                throw new ReportedException(crashreport);
            }

            boolean flag1 = this.ap();

            if (this.world.b(this.getBoundingBox().shrink(0.001D))) {
                if (!flag1) {
                    ++this.fireTicks;
                    if (this.fireTicks == 0) {
                        this.setOnFire(8);
                    }
                }

                this.burn(1);
            } else if (this.fireTicks <= 0) {
                this.fireTicks = -this.getMaxFireTicks();
            }

            if (flag1 && this.isBurning()) {
                this.a(SoundEffects.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                this.fireTicks = -this.getMaxFireTicks();
            }

            this.world.methodProfiler.exit();
        }
    }

    protected float ab() {
        return (float) ((int) this.L + 1);
    }

    public void recalcPosition() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        this.locX = (axisalignedbb.minX + axisalignedbb.maxX) / 2.0D;
        this.locY = axisalignedbb.minY;
        this.locZ = (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D;
    }

    protected SoundEffect ad() {
        return SoundEffects.ENTITY_GENERIC_SWIM;
    }

    protected SoundEffect ae() {
        return SoundEffects.ENTITY_GENERIC_SPLASH;
    }

    protected SoundEffect af() {
        return SoundEffects.ENTITY_GENERIC_SPLASH;
    }

    protected void checkBlockCollisions() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox();
        BlockPosition.b blockposition_b = BlockPosition.b.d(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
        Throwable throwable = null;

        try {
            BlockPosition.b blockposition_b1 = BlockPosition.b.d(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
            Throwable throwable1 = null;

            try {
                BlockPosition.b blockposition_b2 = BlockPosition.b.r();
                Throwable throwable2 = null;

                try {
                    if (this.world.areChunksLoadedBetween(blockposition_b, blockposition_b1)) {
                        for (int i = blockposition_b.getX(); i <= blockposition_b1.getX(); ++i) {
                            for (int j = blockposition_b.getY(); j <= blockposition_b1.getY(); ++j) {
                                for (int k = blockposition_b.getZ(); k <= blockposition_b1.getZ(); ++k) {
                                    blockposition_b2.c(i, j, k);
                                    IBlockData iblockdata = this.world.getType(blockposition_b2);

                                    try {
                                        iblockdata.a(this.world, blockposition_b2, this);
                                        this.a(iblockdata);
                                    } catch (Throwable throwable3) {
                                        CrashReport crashreport = CrashReport.a(throwable3, "Colliding entity with block");
                                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being collided with");

                                        CrashReportSystemDetails.a(crashreportsystemdetails, blockposition_b2, iblockdata);
                                        throw new ReportedException(crashreport);
                                    }
                                }
                            }
                        }
                    }
                } catch (Throwable throwable4) {
                    throwable2 = throwable4;
                    throw throwable4;
                } finally {
                    if (blockposition_b2 != null) {
                        if (throwable2 != null) {
                            try {
                                blockposition_b2.close();
                            } catch (Throwable throwable5) {
                                throwable2.addSuppressed(throwable5);
                            }
                        } else {
                            blockposition_b2.close();
                        }
                    }

                }
            } catch (Throwable throwable6) {
                throwable1 = throwable6;
                throw throwable6;
            } finally {
                if (blockposition_b1 != null) {
                    if (throwable1 != null) {
                        try {
                            blockposition_b1.close();
                        } catch (Throwable throwable7) {
                            throwable1.addSuppressed(throwable7);
                        }
                    } else {
                        blockposition_b1.close();
                    }
                }

            }
        } catch (Throwable throwable8) {
            throwable = throwable8;
            throw throwable8;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable9) {
                        throwable.addSuppressed(throwable9);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }

    }

    protected void a(IBlockData iblockdata) {}

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            SoundEffectType soundeffecttype = this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW ? Blocks.SNOW.getStepSound() : iblockdata.getBlock().getStepSound();

            this.a(soundeffecttype.d(), soundeffecttype.a() * 0.15F, soundeffecttype.b());
        }
    }

    protected void d(float f) {
        this.a(this.ad(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
    }

    protected float e(float f) {
        return 0.0F;
    }

    protected boolean ah() {
        return false;
    }

    public void a(SoundEffect soundeffect, float f, float f1) {
        if (!this.isSilent()) {
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, soundeffect, this.bV(), f, f1);
        }

    }

    public boolean isSilent() {
        return (Boolean) this.datawatcher.get(Entity.aG);
    }

    public void setSilent(boolean flag) {
        this.datawatcher.set(Entity.aG, flag);
    }

    public boolean isNoGravity() {
        return (Boolean) this.datawatcher.get(Entity.aH);
    }

    public void setNoGravity(boolean flag) {
        this.datawatcher.set(Entity.aH, flag);
    }

    protected boolean playStepSound() {
        return true;
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (flag) {
            if (this.fallDistance > 0.0F) {
                iblockdata.getBlock().fallOn(this.world, blockposition, this, this.fallDistance);
            }

            this.fallDistance = 0.0F;
        } else if (d0 < 0.0D) {
            this.fallDistance = (float) ((double) this.fallDistance - d0);
        }

    }

    @Nullable
    public AxisAlignedBB al() {
        return null;
    }

    protected void burn(int i) {
        if (!this.fireProof) {
            this.damageEntity(DamageSource.FIRE, (float) i);
        }

    }

    public final boolean isFireProof() {
        return this.fireProof;
    }

    public void c(float f, float f1) {
        if (this.isVehicle()) {
            Iterator iterator = this.bP().iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                entity.c(f, f1);
            }
        }

    }

    public boolean isInWater() {
        return this.inWater;
    }

    private boolean p() {
        BlockPosition.b blockposition_b = BlockPosition.b.b(this);
        Throwable throwable = null;

        boolean flag;

        try {
            flag = this.world.isRainingAt(blockposition_b) || this.world.isRainingAt(blockposition_b.c(this.locX, this.locY + (double) this.length, this.locZ));
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }

        return flag;
    }

    private boolean q() {
        return this.world.getType(new BlockPosition(this)).getBlock() == Blocks.BUBBLE_COLUMN;
    }

    public boolean ao() {
        return this.isInWater() || this.p();
    }

    public boolean ap() {
        return this.isInWater() || this.p() || this.q();
    }

    public boolean aq() {
        return this.isInWater() || this.q();
    }

    public boolean ar() {
        return this.X && this.isInWater();
    }

    private void r() {
        this.at();
        this.s();
        this.as();
    }

    public void as() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
        } else {
            this.setSwimming(this.isSprinting() && this.ar() && !this.isPassenger());
        }

    }

    public boolean at() {
        if (this.getVehicle() instanceof EntityBoat) {
            this.inWater = false;
        } else if (this.b(TagsFluid.WATER)) {
            if (!this.inWater && !this.justCreated) {
                this.au();
            }

            this.fallDistance = 0.0F;
            this.inWater = true;
            this.extinguish();
        } else {
            this.inWater = false;
        }

        return this.inWater;
    }

    private void s() {
        this.X = this.a(TagsFluid.WATER);
    }

    protected void au() {
        Entity entity = this.isVehicle() && this.bO() != null ? this.bO() : this;
        float f = entity == this ? 0.2F : 0.9F;
        float f1 = MathHelper.sqrt(entity.motX * entity.motX * 0.20000000298023224D + entity.motY * entity.motY + entity.motZ * entity.motZ * 0.20000000298023224D) * f;

        if (f1 > 1.0F) {
            f1 = 1.0F;
        }

        if ((double) f1 < 0.25D) {
            this.a(this.ae(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        } else {
            this.a(this.af(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
        }

        float f2 = (float) MathHelper.floor(this.getBoundingBox().minY);

        float f3;
        float f4;
        int i;

        for (i = 0; (float) i < 1.0F + this.width * 20.0F; ++i) {
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            f4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            this.world.addParticle(Particles.e, this.locX + (double) f3, (double) (f2 + 1.0F), this.locZ + (double) f4, this.motX, this.motY - (double) (this.random.nextFloat() * 0.2F), this.motZ);
        }

        for (i = 0; (float) i < 1.0F + this.width * 20.0F; ++i) {
            f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            f4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.width;
            this.world.addParticle(Particles.R, this.locX + (double) f3, (double) (f2 + 1.0F), this.locZ + (double) f4, this.motX, this.motY, this.motZ);
        }

    }

    public void av() {
        if (this.isSprinting() && !this.isInWater()) {
            this.aw();
        }

    }

    protected void aw() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = this.world.getType(blockposition);

        if (iblockdata.i() != EnumRenderType.INVISIBLE) {
            this.world.addParticle(new ParticleParamBlock(Particles.d, iblockdata), this.locX + ((double) this.random.nextFloat() - 0.5D) * (double) this.width, this.getBoundingBox().minY + 0.1D, this.locZ + ((double) this.random.nextFloat() - 0.5D) * (double) this.width, -this.motX * 4.0D, 1.5D, -this.motZ * 4.0D);
        }

    }

    public boolean a(Tag<FluidType> tag) {
        if (this.getVehicle() instanceof EntityBoat) {
            return false;
        } else {
            double d0 = this.locY + (double) this.getHeadHeight();
            BlockPosition blockposition = new BlockPosition(this.locX, d0, this.locZ);
            Fluid fluid = this.world.getFluid(blockposition);

            return fluid.a(tag) && d0 < (double) ((float) blockposition.getY() + fluid.getHeight() + 0.11111111F);
        }
    }

    public boolean ax() {
        return this.world.a(this.getBoundingBox().f(0.10000000149011612D, 0.4000000059604645D, 0.10000000149011612D), Material.LAVA);
    }

    public void a(float f, float f1, float f2, float f3) {
        float f4 = f * f + f1 * f1 + f2 * f2;

        if (f4 >= 1.0E-4F) {
            f4 = MathHelper.c(f4);
            if (f4 < 1.0F) {
                f4 = 1.0F;
            }

            f4 = f3 / f4;
            f *= f4;
            f1 *= f4;
            f2 *= f4;
            float f5 = MathHelper.sin(this.yaw * 0.017453292F);
            float f6 = MathHelper.cos(this.yaw * 0.017453292F);

            this.motX += (double) (f * f6 - f2 * f5);
            this.motY += (double) f1;
            this.motZ += (double) (f2 * f6 + f * f5);
        }
    }

    public float az() {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(MathHelper.floor(this.locX), 0, MathHelper.floor(this.locZ));

        if (this.world.isLoaded(blockposition_mutableblockposition)) {
            blockposition_mutableblockposition.p(MathHelper.floor(this.locY + (double) this.getHeadHeight()));
            return this.world.A(blockposition_mutableblockposition);
        } else {
            return 0.0F;
        }
    }

    public void spawnIn(World world) {
        this.world = world;
    }

    public void setLocation(double d0, double d1, double d2, float f, float f1) {
        this.locX = MathHelper.a(d0, -3.0E7D, 3.0E7D);
        this.locY = d1;
        this.locZ = MathHelper.a(d2, -3.0E7D, 3.0E7D);
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        f1 = MathHelper.a(f1, -90.0F, 90.0F);
        this.yaw = f;
        this.pitch = f1;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        double d3 = (double) (this.lastYaw - f);

        if (d3 < -180.0D) {
            this.lastYaw += 360.0F;
        }

        if (d3 >= 180.0D) {
            this.lastYaw -= 360.0F;
        }

        this.setPosition(this.locX, this.locY, this.locZ);
        this.setYawPitch(f, f1);
    }

    public void setPositionRotation(BlockPosition blockposition, float f, float f1) {
        this.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, f, f1);
    }

    public void setPositionRotation(double d0, double d1, double d2, float f, float f1) {
        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.N = this.locX;
        this.O = this.locY;
        this.P = this.locZ;
        this.yaw = f;
        this.pitch = f1;
        this.setPosition(this.locX, this.locY, this.locZ);
    }

    public float g(Entity entity) {
        float f = (float) (this.locX - entity.locX);
        float f1 = (float) (this.locY - entity.locY);
        float f2 = (float) (this.locZ - entity.locZ);

        return MathHelper.c(f * f + f1 * f1 + f2 * f2);
    }

    public double d(double d0, double d1, double d2) {
        double d3 = this.locX - d0;
        double d4 = this.locY - d1;
        double d5 = this.locZ - d2;

        return d3 * d3 + d4 * d4 + d5 * d5;
    }

    public double c(BlockPosition blockposition) {
        return blockposition.distanceSquared(this.locX, this.locY, this.locZ);
    }

    public double d(BlockPosition blockposition) {
        return blockposition.g(this.locX, this.locY, this.locZ);
    }

    public double e(double d0, double d1, double d2) {
        double d3 = this.locX - d0;
        double d4 = this.locY - d1;
        double d5 = this.locZ - d2;

        return (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);
    }

    public double h(Entity entity) {
        double d0 = this.locX - entity.locX;
        double d1 = this.locY - entity.locY;
        double d2 = this.locZ - entity.locZ;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public double a(Vec3D vec3d) {
        double d0 = this.locX - vec3d.x;
        double d1 = this.locY - vec3d.y;
        double d2 = this.locZ - vec3d.z;

        return d0 * d0 + d1 * d1 + d2 * d2;
    }

    public void d(EntityHuman entityhuman) {}

    public void collide(Entity entity) {
        if (!this.x(entity)) {
            if (!entity.noclip && !this.noclip) {
                double d0 = entity.locX - this.locX;
                double d1 = entity.locZ - this.locZ;
                double d2 = MathHelper.a(d0, d1);

                if (d2 >= 0.009999999776482582D) {
                    d2 = (double) MathHelper.sqrt(d2);
                    d0 /= d2;
                    d1 /= d2;
                    double d3 = 1.0D / d2;

                    if (d3 > 1.0D) {
                        d3 = 1.0D;
                    }

                    d0 *= d3;
                    d1 *= d3;
                    d0 *= 0.05000000074505806D;
                    d1 *= 0.05000000074505806D;
                    d0 *= (double) (1.0F - this.S);
                    d1 *= (double) (1.0F - this.S);
                    if (!this.isVehicle()) {
                        this.f(-d0, 0.0D, -d1);
                    }

                    if (!entity.isVehicle()) {
                        entity.f(d0, 0.0D, d1);
                    }
                }

            }
        }
    }

    public void f(double d0, double d1, double d2) {
        this.motX += d0;
        this.motY += d1;
        this.motZ += d2;
        this.impulse = true;
    }

    protected void aA() {
        this.velocityChanged = true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.aA();
            return false;
        }
    }

    public final Vec3D f(float f) {
        return this.d(this.g(f), this.h(f));
    }

    public float g(float f) {
        return f == 1.0F ? this.pitch : this.lastPitch + (this.pitch - this.lastPitch) * f;
    }

    public float h(float f) {
        return f == 1.0F ? this.yaw : this.lastYaw + (this.yaw - this.lastYaw) * f;
    }

    protected final Vec3D d(float f, float f1) {
        float f2 = f * 0.017453292F;
        float f3 = -f1 * 0.017453292F;
        float f4 = MathHelper.cos(f3);
        float f5 = MathHelper.sin(f3);
        float f6 = MathHelper.cos(f2);
        float f7 = MathHelper.sin(f2);

        return new Vec3D((double) (f5 * f6), (double) (-f7), (double) (f4 * f6));
    }

    public Vec3D i(float f) {
        if (f == 1.0F) {
            return new Vec3D(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
        } else {
            double d0 = this.lastX + (this.locX - this.lastX) * (double) f;
            double d1 = this.lastY + (this.locY - this.lastY) * (double) f + (double) this.getHeadHeight();
            double d2 = this.lastZ + (this.locZ - this.lastZ) * (double) f;

            return new Vec3D(d0, d1, d2);
        }
    }

    public boolean isInteractable() {
        return false;
    }

    public boolean isCollidable() {
        return false;
    }

    public void a(Entity entity, int i, DamageSource damagesource) {
        if (entity instanceof EntityPlayer) {
            CriterionTriggers.c.a((EntityPlayer) entity, this, damagesource);
        }

    }

    public boolean c(NBTTagCompound nbttagcompound) {
        String s = this.getSaveID();

        if (!this.dead && s != null) {
            nbttagcompound.setString("id", s);
            this.save(nbttagcompound);
            return true;
        } else {
            return false;
        }
    }

    public boolean d(NBTTagCompound nbttagcompound) {
        return this.isPassenger() ? false : this.c(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        try {
            nbttagcompound.set("Pos", this.a(this.locX, this.locY, this.locZ));
            nbttagcompound.set("Motion", this.a(this.motX, this.motY, this.motZ));
            nbttagcompound.set("Rotation", this.a(this.yaw, this.pitch));
            nbttagcompound.setFloat("FallDistance", this.fallDistance);
            nbttagcompound.setShort("Fire", (short) this.fireTicks);
            nbttagcompound.setShort("Air", (short) this.getAirTicks());
            nbttagcompound.setBoolean("OnGround", this.onGround);
            nbttagcompound.setInt("Dimension", this.dimension.getDimensionID());
            nbttagcompound.setBoolean("Invulnerable", this.invulnerable);
            nbttagcompound.setInt("PortalCooldown", this.portalCooldown);
            nbttagcompound.a("UUID", this.getUniqueID());
            IChatBaseComponent ichatbasecomponent = this.getCustomName();

            if (ichatbasecomponent != null) {
                nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
            }

            if (this.getCustomNameVisible()) {
                nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
            }

            if (this.isSilent()) {
                nbttagcompound.setBoolean("Silent", this.isSilent());
            }

            if (this.isNoGravity()) {
                nbttagcompound.setBoolean("NoGravity", this.isNoGravity());
            }

            if (this.glowing) {
                nbttagcompound.setBoolean("Glowing", this.glowing);
            }

            NBTTagList nbttaglist;
            Iterator iterator;

            if (!this.aJ.isEmpty()) {
                nbttaglist = new NBTTagList();
                iterator = this.aJ.iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    nbttaglist.add((NBTBase) (new NBTTagString(s)));
                }

                nbttagcompound.set("Tags", nbttaglist);
            }

            this.b(nbttagcompound);
            if (this.isVehicle()) {
                nbttaglist = new NBTTagList();
                iterator = this.bP().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    if (entity.c(nbttagcompound1)) {
                        nbttaglist.add((NBTBase) nbttagcompound1);
                    }
                }

                if (!nbttaglist.isEmpty()) {
                    nbttagcompound.set("Passengers", nbttaglist);
                }
            }

            return nbttagcompound;
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Saving entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being saved");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    public void f(NBTTagCompound nbttagcompound) {
        try {
            NBTTagList nbttaglist = nbttagcompound.getList("Pos", 6);
            NBTTagList nbttaglist1 = nbttagcompound.getList("Motion", 6);
            NBTTagList nbttaglist2 = nbttagcompound.getList("Rotation", 5);

            this.motX = nbttaglist1.k(0);
            this.motY = nbttaglist1.k(1);
            this.motZ = nbttaglist1.k(2);
            if (Math.abs(this.motX) > 10.0D) {
                this.motX = 0.0D;
            }

            if (Math.abs(this.motY) > 10.0D) {
                this.motY = 0.0D;
            }

            if (Math.abs(this.motZ) > 10.0D) {
                this.motZ = 0.0D;
            }

            this.locX = nbttaglist.k(0);
            this.locY = nbttaglist.k(1);
            this.locZ = nbttaglist.k(2);
            this.N = this.locX;
            this.O = this.locY;
            this.P = this.locZ;
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.yaw = nbttaglist2.l(0);
            this.pitch = nbttaglist2.l(1);
            this.lastYaw = this.yaw;
            this.lastPitch = this.pitch;
            this.setHeadRotation(this.yaw);
            this.k(this.yaw);
            this.fallDistance = nbttagcompound.getFloat("FallDistance");
            this.fireTicks = nbttagcompound.getShort("Fire");
            this.setAirTicks(nbttagcompound.getShort("Air"));
            this.onGround = nbttagcompound.getBoolean("OnGround");
            if (nbttagcompound.hasKey("Dimension")) {
                this.dimension = DimensionManager.a(nbttagcompound.getInt("Dimension"));
            }

            this.invulnerable = nbttagcompound.getBoolean("Invulnerable");
            this.portalCooldown = nbttagcompound.getInt("PortalCooldown");
            if (nbttagcompound.b("UUID")) {
                this.uniqueID = nbttagcompound.a("UUID");
                this.au = this.uniqueID.toString();
            }

            this.setPosition(this.locX, this.locY, this.locZ);
            this.setYawPitch(this.yaw, this.pitch);
            if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
                this.setCustomName(IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName")));
            }

            this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
            this.setSilent(nbttagcompound.getBoolean("Silent"));
            this.setNoGravity(nbttagcompound.getBoolean("NoGravity"));
            this.h(nbttagcompound.getBoolean("Glowing"));
            if (nbttagcompound.hasKeyOfType("Tags", 9)) {
                this.aJ.clear();
                NBTTagList nbttaglist3 = nbttagcompound.getList("Tags", 8);
                int i = Math.min(nbttaglist3.size(), 1024);

                for (int j = 0; j < i; ++j) {
                    this.aJ.add(nbttaglist3.getString(j));
                }
            }

            this.a(nbttagcompound);
            if (this.aD()) {
                this.setPosition(this.locX, this.locY, this.locZ);
            }

        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Loading entity NBT");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity being loaded");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    protected boolean aD() {
        return true;
    }

    @Nullable
    public final String getSaveID() {
        EntityTypes<?> entitytypes = this.P();
        MinecraftKey minecraftkey = EntityTypes.getName(entitytypes);

        return entitytypes.a() && minecraftkey != null ? minecraftkey.toString() : null;
    }

    protected abstract void a(NBTTagCompound nbttagcompound);

    protected abstract void b(NBTTagCompound nbttagcompound);

    protected NBTTagList a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add((NBTBase) (new NBTTagDouble(d0)));
        }

        return nbttaglist;
    }

    protected NBTTagList a(float... afloat) {
        NBTTagList nbttaglist = new NBTTagList();
        float[] afloat1 = afloat;
        int i = afloat.length;

        for (int j = 0; j < i; ++j) {
            float f = afloat1[j];

            nbttaglist.add((NBTBase) (new NBTTagFloat(f)));
        }

        return nbttaglist;
    }

    @Nullable
    public EntityItem a(IMaterial imaterial) {
        return this.a(imaterial, 0);
    }

    @Nullable
    public EntityItem a(IMaterial imaterial, int i) {
        return this.a(new ItemStack(imaterial), (float) i);
    }

    @Nullable
    public EntityItem a_(ItemStack itemstack) {
        return this.a(itemstack, 0.0F);
    }

    @Nullable
    public EntityItem a(ItemStack itemstack, float f) {
        if (itemstack.isEmpty()) {
            return null;
        } else {
            EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY + (double) f, this.locZ, itemstack);

            entityitem.n();
            this.world.addEntity(entityitem);
            return entityitem;
        }
    }

    public boolean isAlive() {
        return !this.dead;
    }

    public boolean inBlock() {
        if (this.noclip) {
            return false;
        } else {
            BlockPosition.b blockposition_b = BlockPosition.b.r();
            Throwable throwable = null;

            try {
                for (int i = 0; i < 8; ++i) {
                    int j = MathHelper.floor(this.locY + (double) (((float) ((i >> 0) % 2) - 0.5F) * 0.1F) + (double) this.getHeadHeight());
                    int k = MathHelper.floor(this.locX + (double) (((float) ((i >> 1) % 2) - 0.5F) * this.width * 0.8F));
                    int l = MathHelper.floor(this.locZ + (double) (((float) ((i >> 2) % 2) - 0.5F) * this.width * 0.8F));

                    if (blockposition_b.getX() != k || blockposition_b.getY() != j || blockposition_b.getZ() != l) {
                        blockposition_b.c(k, j, l);
                        if (this.world.getType(blockposition_b).r()) {
                            boolean flag = true;

                            return flag;
                        }
                    }
                }

                return false;
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (blockposition_b != null) {
                    if (throwable != null) {
                        try {
                            blockposition_b.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_b.close();
                    }
                }

            }
        }
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        return false;
    }

    @Nullable
    public AxisAlignedBB j(Entity entity) {
        return null;
    }

    public void aH() {
        Entity entity = this.getVehicle();

        if (this.isPassenger() && entity.dead) {
            this.stopRiding();
        } else {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
            this.tick();
            if (this.isPassenger()) {
                entity.k(this);
            }
        }
    }

    public void k(Entity entity) {
        if (this.w(entity)) {
            entity.setPosition(this.locX, this.locY + this.aJ() + entity.aI(), this.locZ);
        }
    }

    public double aI() {
        return 0.0D;
    }

    public double aJ() {
        return (double) this.length * 0.75D;
    }

    public boolean startRiding(Entity entity) {
        return this.a(entity, false);
    }

    public boolean a(Entity entity, boolean flag) {
        for (Entity entity1 = entity; entity1.vehicle != null; entity1 = entity1.vehicle) {
            if (entity1.vehicle == this) {
                return false;
            }
        }

        if (!flag && (!this.n(entity) || !entity.q(this))) {
            return false;
        } else {
            if (this.isPassenger()) {
                this.stopRiding();
            }

            this.vehicle = entity;
            this.vehicle.addPassenger(this);
            return true;
        }
    }

    protected boolean n(Entity entity) {
        return this.k <= 0;
    }

    public void ejectPassengers() {
        for (int i = this.passengers.size() - 1; i >= 0; --i) {
            ((Entity) this.passengers.get(i)).stopRiding();
        }

    }

    public void stopRiding() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;

            this.vehicle = null;
            entity.removePassenger(this);
        }

    }

    protected void addPassenger(Entity entity) {
        if (entity.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        } else {
            if (!this.world.isClientSide && entity instanceof EntityHuman && !(this.bO() instanceof EntityHuman)) {
                this.passengers.add(0, entity);
            } else {
                this.passengers.add(entity);
            }

        }
    }

    protected void removePassenger(Entity entity) {
        if (entity.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        } else {
            this.passengers.remove(entity);
            entity.k = 60;
        }
    }

    protected boolean q(Entity entity) {
        return this.bP().size() < 1;
    }

    public float aM() {
        return 0.0F;
    }

    public Vec3D aN() {
        return this.d(this.pitch, this.yaw);
    }

    public Vec2F aO() {
        return new Vec2F(this.pitch, this.yaw);
    }

    public void e(BlockPosition blockposition) {
        if (this.portalCooldown > 0) {
            this.portalCooldown = this.aQ();
        } else {
            if (!this.world.isClientSide && !blockposition.equals(this.aq)) {
                this.aq = new BlockPosition(blockposition);
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = ((BlockPortal) Blocks.NETHER_PORTAL).c((GeneratorAccess) this.world, this.aq);
                double d0 = shapedetector_shapedetectorcollection.getFacing().k() == EnumDirection.EnumAxis.X ? (double) shapedetector_shapedetectorcollection.a().getZ() : (double) shapedetector_shapedetectorcollection.a().getX();
                double d1 = shapedetector_shapedetectorcollection.getFacing().k() == EnumDirection.EnumAxis.X ? this.locZ : this.locX;

                d1 = Math.abs(MathHelper.c(d1 - (double) (shapedetector_shapedetectorcollection.getFacing().e().c() == EnumDirection.EnumAxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double) shapedetector_shapedetectorcollection.d()));
                double d2 = MathHelper.c(this.locY - 1.0D, (double) shapedetector_shapedetectorcollection.a().getY(), (double) (shapedetector_shapedetectorcollection.a().getY() - shapedetector_shapedetectorcollection.e()));

                this.ar = new Vec3D(d1, d2, 0.0D);
                this.as = shapedetector_shapedetectorcollection.getFacing();
            }

            this.an = true;
        }
    }

    public int aQ() {
        return 300;
    }

    public Iterable<ItemStack> aS() {
        return Entity.a;
    }

    public Iterable<ItemStack> getArmorItems() {
        return Entity.a;
    }

    public Iterable<ItemStack> aU() {
        return Iterables.concat(this.aS(), this.getArmorItems());
    }

    public void setEquipment(EnumItemSlot enumitemslot, ItemStack itemstack) {}

    public boolean isBurning() {
        boolean flag = this.world != null && this.world.isClientSide;

        return !this.fireProof && (this.fireTicks > 0 || flag && this.getFlag(0));
    }

    public boolean isPassenger() {
        return this.getVehicle() != null;
    }

    public boolean isVehicle() {
        return !this.bP().isEmpty();
    }

    public boolean aY() {
        return true;
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public void setSneaking(boolean flag) {
        this.setFlag(1, flag);
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean flag) {
        this.setFlag(3, flag);
    }

    public boolean isSwimming() {
        return this.getFlag(4);
    }

    public void setSwimming(boolean flag) {
        this.setFlag(4, flag);
    }

    public boolean bc() {
        return this.glowing || this.world.isClientSide && this.getFlag(6);
    }

    public void h(boolean flag) {
        this.glowing = flag;
        if (!this.world.isClientSide) {
            this.setFlag(6, this.glowing);
        }

    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    @Nullable
    public ScoreboardTeamBase getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getName());
    }

    public boolean r(Entity entity) {
        return this.a(entity.getScoreboardTeam());
    }

    public boolean a(ScoreboardTeamBase scoreboardteambase) {
        return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isAlly(scoreboardteambase) : false;
    }

    public void setInvisible(boolean flag) {
        this.setFlag(5, flag);
    }

    public boolean getFlag(int i) {
        return ((Byte) this.datawatcher.get(Entity.ac) & 1 << i) != 0;
    }

    public void setFlag(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(Entity.ac);

        if (flag) {
            this.datawatcher.set(Entity.ac, (byte) (b0 | 1 << i));
        } else {
            this.datawatcher.set(Entity.ac, (byte) (b0 & ~(1 << i)));
        }

    }

    public int bf() {
        return 300;
    }

    public int getAirTicks() {
        return (Integer) this.datawatcher.get(Entity.aD);
    }

    public void setAirTicks(int i) {
        this.datawatcher.set(Entity.aD, i);
    }

    public void onLightningStrike(EntityLightning entitylightning) {
        ++this.fireTicks;
        if (this.fireTicks == 0) {
            this.setOnFire(8);
        }

        this.damageEntity(DamageSource.LIGHTNING, 5.0F);
    }

    public void j(boolean flag) {
        if (flag) {
            this.motY = Math.max(-0.9D, this.motY - 0.03D);
        } else {
            this.motY = Math.min(1.8D, this.motY + 0.1D);
        }

    }

    public void k(boolean flag) {
        if (flag) {
            this.motY = Math.max(-0.3D, this.motY - 0.03D);
        } else {
            this.motY = Math.min(0.7D, this.motY + 0.06D);
        }

        this.fallDistance = 0.0F;
    }

    public void b(EntityLiving entityliving) {}

    protected boolean i(double d0, double d1, double d2) {
        BlockPosition blockposition = new BlockPosition(d0, d1, d2);
        double d3 = d0 - (double) blockposition.getX();
        double d4 = d1 - (double) blockposition.getY();
        double d5 = d2 - (double) blockposition.getZ();

        if (this.world.getCubes((Entity) null, this.getBoundingBox())) {
            return false;
        } else {
            EnumDirection enumdirection = EnumDirection.UP;
            double d6 = Double.MAX_VALUE;

            if (!this.world.o(blockposition.west()) && d3 < d6) {
                d6 = d3;
                enumdirection = EnumDirection.WEST;
            }

            if (!this.world.o(blockposition.east()) && 1.0D - d3 < d6) {
                d6 = 1.0D - d3;
                enumdirection = EnumDirection.EAST;
            }

            if (!this.world.o(blockposition.north()) && d5 < d6) {
                d6 = d5;
                enumdirection = EnumDirection.NORTH;
            }

            if (!this.world.o(blockposition.south()) && 1.0D - d5 < d6) {
                d6 = 1.0D - d5;
                enumdirection = EnumDirection.SOUTH;
            }

            if (!this.world.o(blockposition.up()) && 1.0D - d4 < d6) {
                d6 = 1.0D - d4;
                enumdirection = EnumDirection.UP;
            }

            float f = this.random.nextFloat() * 0.2F + 0.1F;
            float f1 = (float) enumdirection.c().a();

            if (enumdirection.k() == EnumDirection.EnumAxis.X) {
                this.motX = (double) (f1 * f);
                this.motY *= 0.75D;
                this.motZ *= 0.75D;
            } else if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
                this.motX *= 0.75D;
                this.motY = (double) (f1 * f);
                this.motZ *= 0.75D;
            } else if (enumdirection.k() == EnumDirection.EnumAxis.Z) {
                this.motX *= 0.75D;
                this.motY *= 0.75D;
                this.motZ = (double) (f1 * f);
            }

            return true;
        }
    }

    public void bh() {
        this.F = true;
        this.fallDistance = 0.0F;
    }

    private static void c(IChatBaseComponent ichatbasecomponent) {
        ichatbasecomponent.a((chatmodifier) -> {
            chatmodifier.setChatClickable((ChatClickable) null);
        }).a().forEach(Entity::c);
    }

    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        if (ichatbasecomponent != null) {
            IChatBaseComponent ichatbasecomponent1 = ichatbasecomponent.h();

            c(ichatbasecomponent1);
            return ichatbasecomponent1;
        } else {
            return this.g.e();
        }
    }

    @Nullable
    public Entity[] bi() {
        return null;
    }

    public boolean s(Entity entity) {
        return this == entity;
    }

    public float getHeadRotation() {
        return 0.0F;
    }

    public void setHeadRotation(float f) {}

    public void k(float f) {}

    public boolean bk() {
        return true;
    }

    public boolean t(Entity entity) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getDisplayName().getText(), this.id, this.world == null ? "~NULL~" : this.world.getWorldData().getName(), this.locX, this.locY, this.locZ);
    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return this.invulnerable && damagesource != DamageSource.OUT_OF_WORLD && !damagesource.v();
    }

    public boolean bl() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean flag) {
        this.invulnerable = flag;
    }

    public void u(Entity entity) {
        this.setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
    }

    public void v(Entity entity) {
        NBTTagCompound nbttagcompound = entity.save(new NBTTagCompound());

        nbttagcompound.remove("Dimension");
        this.f(nbttagcompound);
        this.portalCooldown = entity.portalCooldown;
        this.aq = entity.aq;
        this.ar = entity.ar;
        this.as = entity.as;
    }

    @Nullable
    public Entity a(DimensionManager dimensionmanager) {
        if (!this.world.isClientSide && !this.dead) {
            this.world.methodProfiler.enter("changeDimension");
            MinecraftServer minecraftserver = this.bK();
            DimensionManager dimensionmanager1 = this.dimension;
            WorldServer worldserver = minecraftserver.getWorldServer(dimensionmanager1);
            WorldServer worldserver1 = minecraftserver.getWorldServer(dimensionmanager);

            this.dimension = dimensionmanager;
            if (dimensionmanager1 == DimensionManager.THE_END && dimensionmanager == DimensionManager.THE_END) {
                worldserver1 = minecraftserver.getWorldServer(DimensionManager.OVERWORLD);
                this.dimension = DimensionManager.OVERWORLD;
            }

            this.world.kill(this);
            this.dead = false;
            this.world.methodProfiler.enter("reposition");
            BlockPosition blockposition;

            if (dimensionmanager == DimensionManager.THE_END) {
                blockposition = worldserver1.getDimensionSpawn();
            } else {
                double d0 = this.locX;
                double d1 = this.locZ;
                double d2 = 8.0D;

                if (dimensionmanager == DimensionManager.NETHER) {
                    d0 = MathHelper.a(d0 / 8.0D, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
                    d1 = MathHelper.a(d1 / 8.0D, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
                } else if (dimensionmanager == DimensionManager.OVERWORLD) {
                    d0 = MathHelper.a(d0 * 8.0D, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
                    d1 = MathHelper.a(d1 * 8.0D, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
                }

                d0 = (double) MathHelper.clamp((int) d0, -29999872, 29999872);
                d1 = (double) MathHelper.clamp((int) d1, -29999872, 29999872);
                float f = this.yaw;

                this.setPositionRotation(d0, this.locY, d1, 90.0F, 0.0F);
                PortalTravelAgent portaltravelagent = worldserver1.getTravelAgent();

                portaltravelagent.b(this, f);
                blockposition = new BlockPosition(this);
            }

            worldserver.entityJoinedWorld(this, false);
            this.world.methodProfiler.exitEnter("reloading");
            Entity entity = this.P().a((World) worldserver1);

            if (entity != null) {
                entity.v(this);
                if (dimensionmanager1 == DimensionManager.THE_END && dimensionmanager == DimensionManager.THE_END) {
                    BlockPosition blockposition1 = worldserver1.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, worldserver1.getSpawn());

                    entity.setPositionRotation(blockposition1, entity.yaw, entity.pitch);
                } else {
                    entity.setPositionRotation(blockposition, entity.yaw, entity.pitch);
                }

                boolean flag = entity.attachedToPlayer;

                entity.attachedToPlayer = true;
                worldserver1.addEntity(entity);
                entity.attachedToPlayer = flag;
                worldserver1.entityJoinedWorld(entity, false);
            }

            this.dead = true;
            this.world.methodProfiler.exit();
            worldserver.p();
            worldserver1.p();
            this.world.methodProfiler.exit();
            return entity;
        } else {
            return null;
        }
    }

    public boolean bm() {
        return true;
    }

    public float a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid, float f) {
        return f;
    }

    public boolean a(Explosion explosion, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, float f) {
        return true;
    }

    public int bn() {
        return 3;
    }

    public Vec3D getPortalOffset() {
        return this.ar;
    }

    public EnumDirection getPortalDirection() {
        return this.as;
    }

    public boolean isIgnoreBlockTrigger() {
        return false;
    }

    public void appendEntityCrashDetails(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Entity Type", () -> {
            return EntityTypes.getName(this.P()) + " (" + this.getClass().getCanonicalName() + ")";
        });
        crashreportsystemdetails.a("Entity ID", (Object) this.id);
        crashreportsystemdetails.a("Entity Name", () -> {
            return this.getDisplayName().getString();
        });
        crashreportsystemdetails.a("Entity's Exact location", (Object) String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.locX, this.locY, this.locZ));
        crashreportsystemdetails.a("Entity's Block location", (Object) CrashReportSystemDetails.a(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)));
        crashreportsystemdetails.a("Entity's Momentum", (Object) String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.motX, this.motY, this.motZ));
        crashreportsystemdetails.a("Entity's Passengers", () -> {
            return this.bP().toString();
        });
        crashreportsystemdetails.a("Entity's Vehicle", () -> {
            return this.getVehicle().toString();
        });
    }

    public void a(UUID uuid) {
        this.uniqueID = uuid;
        this.au = this.uniqueID.toString();
    }

    public UUID getUniqueID() {
        return this.uniqueID;
    }

    public String bu() {
        return this.au;
    }

    public String getName() {
        return this.au;
    }

    public boolean bw() {
        return true;
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return ScoreboardTeam.a(this.getScoreboardTeam(), this.getDisplayName()).a((chatmodifier) -> {
            chatmodifier.setChatHoverable(this.bC()).setInsertion(this.bu());
        });
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.datawatcher.set(Entity.aE, Optional.ofNullable(ichatbasecomponent));
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return (IChatBaseComponent) ((Optional) this.datawatcher.get(Entity.aE)).orElse((Object) null);
    }

    public boolean hasCustomName() {
        return ((Optional) this.datawatcher.get(Entity.aE)).isPresent();
    }

    public void setCustomNameVisible(boolean flag) {
        this.datawatcher.set(Entity.aF, flag);
    }

    public boolean getCustomNameVisible() {
        return (Boolean) this.datawatcher.get(Entity.aF);
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.aK = true;
        this.setPositionRotation(d0, d1, d2, this.yaw, this.pitch);
        this.world.entityJoinedWorld(this, false);
    }

    public void a(DataWatcherObject<?> datawatcherobject) {}

    public EnumDirection getDirection() {
        return EnumDirection.fromAngle((double) this.yaw);
    }

    public EnumDirection getAdjustedDirection() {
        return this.getDirection();
    }

    protected ChatHoverable bC() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        MinecraftKey minecraftkey = EntityTypes.getName(this.P());

        nbttagcompound.setString("id", this.bu());
        if (minecraftkey != null) {
            nbttagcompound.setString("type", minecraftkey.toString());
        }

        nbttagcompound.setString("name", IChatBaseComponent.ChatSerializer.a(this.getDisplayName()));
        return new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ENTITY, new ChatComponentText(nbttagcompound.toString()));
    }

    public boolean a(EntityPlayer entityplayer) {
        return true;
    }

    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    public void a(AxisAlignedBB axisalignedbb) {
        this.boundingBox = axisalignedbb;
    }

    public float getHeadHeight() {
        return this.length * 0.85F;
    }

    public boolean bG() {
        return this.az;
    }

    public void n(boolean flag) {
        this.az = flag;
    }

    public boolean c(int i, ItemStack itemstack) {
        return false;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this);
    }

    public Vec3D bI() {
        return new Vec3D(this.locX, this.locY, this.locZ);
    }

    public World getWorld() {
        return this.world;
    }

    @Nullable
    public MinecraftServer bK() {
        return this.world.getMinecraftServer();
    }

    public EnumInteractionResult a(EntityHuman entityhuman, Vec3D vec3d, EnumHand enumhand) {
        return EnumInteractionResult.PASS;
    }

    public boolean bL() {
        return false;
    }

    protected void a(EntityLiving entityliving, Entity entity) {
        if (entity instanceof EntityLiving) {
            EnchantmentManager.a((EntityLiving) entity, (Entity) entityliving);
        }

        EnchantmentManager.b(entityliving, entity);
    }

    public void b(EntityPlayer entityplayer) {}

    public void c(EntityPlayer entityplayer) {}

    public float a(EnumBlockRotation enumblockrotation) {
        float f = MathHelper.g(this.yaw);

        switch (enumblockrotation) {
        case CLOCKWISE_180:
            return f + 180.0F;
        case COUNTERCLOCKWISE_90:
            return f + 270.0F;
        case CLOCKWISE_90:
            return f + 90.0F;
        default:
            return f;
        }
    }

    public float a(EnumBlockMirror enumblockmirror) {
        float f = MathHelper.g(this.yaw);

        switch (enumblockmirror) {
        case LEFT_RIGHT:
            return -f;
        case FRONT_BACK:
            return 180.0F - f;
        default:
            return f;
        }
    }

    public boolean bM() {
        return false;
    }

    public boolean bN() {
        boolean flag = this.aK;

        this.aK = false;
        return flag;
    }

    @Nullable
    public Entity bO() {
        return null;
    }

    public List<Entity> bP() {
        return (List) (this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
    }

    public boolean w(Entity entity) {
        Iterator iterator = this.bP().iterator();

        Entity entity1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity1 = (Entity) iterator.next();
        } while (!entity1.equals(entity));

        return true;
    }

    public boolean a(Class<? extends Entity> oclass) {
        Iterator iterator = this.bP().iterator();

        Entity entity;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity = (Entity) iterator.next();
        } while (!oclass.isAssignableFrom(entity.getClass()));

        return true;
    }

    public Collection<Entity> getAllPassengers() {
        Set<Entity> set = Sets.newHashSet();
        Iterator iterator = this.bP().iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            set.add(entity);
            entity.a(false, set);
        }

        return set;
    }

    public boolean bR() {
        Set<Entity> set = Sets.newHashSet();

        this.a(true, set);
        return set.size() == 1;
    }

    private void a(boolean flag, Set<Entity> set) {
        Entity entity;

        for (Iterator iterator = this.bP().iterator(); iterator.hasNext(); entity.a(flag, set)) {
            entity = (Entity) iterator.next();
            if (!flag || EntityPlayer.class.isAssignableFrom(entity.getClass())) {
                set.add(entity);
            }
        }

    }

    public Entity getRootVehicle() {
        Entity entity;

        for (entity = this; entity.isPassenger(); entity = entity.getVehicle()) {
            ;
        }

        return entity;
    }

    public boolean x(Entity entity) {
        return this.getRootVehicle() == entity.getRootVehicle();
    }

    public boolean y(Entity entity) {
        Iterator iterator = this.bP().iterator();

        Entity entity1;

        do {
            if (!iterator.hasNext()) {
                return false;
            }

            entity1 = (Entity) iterator.next();
            if (entity1.equals(entity)) {
                return true;
            }
        } while (!entity1.y(entity));

        return true;
    }

    public boolean bT() {
        Entity entity = this.bO();

        return entity instanceof EntityHuman ? ((EntityHuman) entity).dn() : !this.world.isClientSide;
    }

    @Nullable
    public Entity getVehicle() {
        return this.vehicle;
    }

    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.NORMAL;
    }

    public SoundCategory bV() {
        return SoundCategory.NEUTRAL;
    }

    public int getMaxFireTicks() {
        return 1;
    }

    public CommandListenerWrapper getCommandListener() {
        return new CommandListenerWrapper(this, new Vec3D(this.locX, this.locY, this.locZ), this.aO(), this.world instanceof WorldServer ? (WorldServer) this.world : null, this.y(), this.getDisplayName().getString(), this.getScoreboardDisplayName(), this.world.getMinecraftServer(), this);
    }

    protected int y() {
        return 0;
    }

    public boolean j(int i) {
        return this.y() >= i;
    }

    public boolean a() {
        return this.world.getGameRules().getBoolean("sendCommandFeedback");
    }

    public boolean b() {
        return true;
    }

    public boolean B_() {
        return true;
    }

    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        Vec3D vec3d1 = argumentanchor_anchor.a(this);
        double d0 = vec3d.x - vec3d1.x;
        double d1 = vec3d.y - vec3d1.y;
        double d2 = vec3d.z - vec3d1.z;
        double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);

        this.pitch = MathHelper.g((float) (-(MathHelper.c(d1, d3) * 57.2957763671875D)));
        this.yaw = MathHelper.g((float) (MathHelper.c(d2, d0) * 57.2957763671875D) - 90.0F);
        this.setHeadRotation(this.yaw);
        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
    }

    public boolean b(Tag<FluidType> tag) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().shrink(0.001D);
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);

        if (!this.world.isAreaLoaded(i, k, i1, j, l, j1, true)) {
            return false;
        } else {
            double d0 = 0.0D;
            boolean flag = this.bw();
            boolean flag1 = false;
            Vec3D vec3d = Vec3D.a;
            int k1 = 0;
            BlockPosition.b blockposition_b = BlockPosition.b.r();
            Throwable throwable = null;

            try {
                for (int l1 = i; l1 < j; ++l1) {
                    for (int i2 = k; i2 < l; ++i2) {
                        for (int j2 = i1; j2 < j1; ++j2) {
                            blockposition_b.c(l1, i2, j2);
                            Fluid fluid = this.world.getFluid(blockposition_b);

                            if (fluid.a(tag)) {
                                double d1 = (double) ((float) i2 + fluid.getHeight());

                                if (d1 >= axisalignedbb.minY) {
                                    flag1 = true;
                                    d0 = Math.max(d1 - axisalignedbb.minY, d0);
                                    if (flag) {
                                        Vec3D vec3d1 = fluid.a((IWorldReader) this.world, (BlockPosition) blockposition_b);

                                        if (d0 < 0.4D) {
                                            vec3d1 = vec3d1.a(d0);
                                        }

                                        vec3d = vec3d.e(vec3d1);
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
                if (blockposition_b != null) {
                    if (throwable != null) {
                        try {
                            blockposition_b.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_b.close();
                    }
                }

            }

            if (vec3d.b() > 0.0D) {
                if (k1 > 0) {
                    vec3d = vec3d.a(1.0D / (double) k1);
                }

                if (!(this instanceof EntityHuman)) {
                    vec3d = vec3d.a();
                }

                double d2 = 0.014D;

                this.motX += vec3d.x * 0.014D;
                this.motY += vec3d.y * 0.014D;
                this.motZ += vec3d.z * 0.014D;
            }

            this.W = d0;
            return flag1;
        }
    }

    public double bY() {
        return this.W;
    }
}
