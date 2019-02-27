package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityShulker extends EntityGolem implements IMonster {

    private static final UUID bD = UUID.fromString("7E0292F2-9434-48D5-A29F-9583AF7DF27F");
    private static final AttributeModifier bE = (new AttributeModifier(EntityShulker.bD, "Covered armor bonus", 20.0D, 0)).a(false);
    protected static final DataWatcherObject<EnumDirection> a = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.n);
    protected static final DataWatcherObject<Optional<BlockPosition>> b = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.m);
    protected static final DataWatcherObject<Byte> c = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Byte> COLOR = DataWatcher.a(EntityShulker.class, DataWatcherRegistry.a);
    private float bF;
    private float bG;
    private BlockPosition bH;
    private int bI;

    public EntityShulker(World world) {
        super(EntityTypes.SHULKER, world);
        this.setSize(1.0F, 1.0F);
        this.aR = 180.0F;
        this.aQ = 180.0F;
        this.fireProof = true;
        this.bH = null;
        this.b_ = 5;
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.aQ = 180.0F;
        this.aR = 180.0F;
        this.yaw = 180.0F;
        this.lastYaw = 180.0F;
        this.aS = 180.0F;
        this.aT = 180.0F;
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    protected void n() {
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(4, new EntityShulker.a());
        this.goalSelector.a(7, new EntityShulker.e());
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(2, new EntityShulker.d(this));
        this.targetSelector.a(3, new EntityShulker.c(this));
    }

    protected boolean playStepSound() {
        return false;
    }

    public SoundCategory bV() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_SHULKER_AMBIENT;
    }

    public void A() {
        if (!this.dG()) {
            super.A();
        }

    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SHULKER_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return this.dG() ? SoundEffects.ENTITY_SHULKER_HURT_CLOSED : SoundEffects.ENTITY_SHULKER_HURT;
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityShulker.a, EnumDirection.DOWN);
        this.datawatcher.register(EntityShulker.b, Optional.empty());
        this.datawatcher.register(EntityShulker.c, (byte) 0);
        this.datawatcher.register(EntityShulker.COLOR, (byte) 16);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(30.0D);
    }

    protected EntityAIBodyControl o() {
        return new EntityShulker.b(this);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.datawatcher.set(EntityShulker.a, EnumDirection.fromType1(nbttagcompound.getByte("AttachFace")));
        this.datawatcher.set(EntityShulker.c, nbttagcompound.getByte("Peek"));
        this.datawatcher.set(EntityShulker.COLOR, nbttagcompound.getByte("Color"));
        if (nbttagcompound.hasKey("APX")) {
            int i = nbttagcompound.getInt("APX");
            int j = nbttagcompound.getInt("APY");
            int k = nbttagcompound.getInt("APZ");

            this.datawatcher.set(EntityShulker.b, Optional.of(new BlockPosition(i, j, k)));
        } else {
            this.datawatcher.set(EntityShulker.b, Optional.empty());
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("AttachFace", (byte) ((EnumDirection) this.datawatcher.get(EntityShulker.a)).a());
        nbttagcompound.setByte("Peek", (Byte) this.datawatcher.get(EntityShulker.c));
        nbttagcompound.setByte("Color", (Byte) this.datawatcher.get(EntityShulker.COLOR));
        BlockPosition blockposition = this.dz();

        if (blockposition != null) {
            nbttagcompound.setInt("APX", blockposition.getX());
            nbttagcompound.setInt("APY", blockposition.getY());
            nbttagcompound.setInt("APZ", blockposition.getZ());
        }

    }

    public void tick() {
        super.tick();
        BlockPosition blockposition = (BlockPosition) ((Optional) this.datawatcher.get(EntityShulker.b)).orElse((Object) null);

        if (blockposition == null && !this.world.isClientSide) {
            blockposition = new BlockPosition(this);
            this.datawatcher.set(EntityShulker.b, Optional.of(blockposition));
        }

        float f;

        if (this.isPassenger()) {
            blockposition = null;
            f = this.getVehicle().yaw;
            this.yaw = f;
            this.aQ = f;
            this.aR = f;
            this.bI = 0;
        } else if (!this.world.isClientSide) {
            IBlockData iblockdata = this.world.getType(blockposition);

            if (!iblockdata.isAir()) {
                EnumDirection enumdirection;

                if (iblockdata.getBlock() == Blocks.MOVING_PISTON) {
                    enumdirection = (EnumDirection) iblockdata.get(BlockPiston.FACING);
                    if (this.world.isEmpty(blockposition.shift(enumdirection))) {
                        blockposition = blockposition.shift(enumdirection);
                        this.datawatcher.set(EntityShulker.b, Optional.of(blockposition));
                    } else {
                        this.l();
                    }
                } else if (iblockdata.getBlock() == Blocks.PISTON_HEAD) {
                    enumdirection = (EnumDirection) iblockdata.get(BlockPistonExtension.FACING);
                    if (this.world.isEmpty(blockposition.shift(enumdirection))) {
                        blockposition = blockposition.shift(enumdirection);
                        this.datawatcher.set(EntityShulker.b, Optional.of(blockposition));
                    } else {
                        this.l();
                    }
                } else {
                    this.l();
                }
            }

            BlockPosition blockposition1 = blockposition.shift(this.dy());

            if (!this.world.q(blockposition1)) {
                boolean flag = false;
                EnumDirection[] aenumdirection = EnumDirection.values();
                int i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection1 = aenumdirection[j];

                    blockposition1 = blockposition.shift(enumdirection1);
                    if (this.world.q(blockposition1)) {
                        this.datawatcher.set(EntityShulker.a, enumdirection1);
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    this.l();
                }
            }

            BlockPosition blockposition2 = blockposition.shift(this.dy().opposite());

            if (this.world.q(blockposition2)) {
                this.l();
            }
        }

        f = (float) this.dA() * 0.01F;
        this.bF = this.bG;
        if (this.bG > f) {
            this.bG = MathHelper.a(this.bG - 0.05F, f, 1.0F);
        } else if (this.bG < f) {
            this.bG = MathHelper.a(this.bG + 0.05F, 0.0F, f);
        }

        if (blockposition != null) {
            if (this.world.isClientSide) {
                if (this.bI > 0 && this.bH != null) {
                    --this.bI;
                } else {
                    this.bH = blockposition;
                }
            }

            this.locX = (double) blockposition.getX() + 0.5D;
            this.locY = (double) blockposition.getY();
            this.locZ = (double) blockposition.getZ() + 0.5D;
            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            this.N = this.locX;
            this.O = this.locY;
            this.P = this.locZ;
            double d0 = 0.5D - (double) MathHelper.sin((0.5F + this.bG) * 3.1415927F) * 0.5D;
            double d1 = 0.5D - (double) MathHelper.sin((0.5F + this.bF) * 3.1415927F) * 0.5D;
            double d2 = d0 - d1;
            double d3 = 0.0D;
            double d4 = 0.0D;
            double d5 = 0.0D;
            EnumDirection enumdirection2 = this.dy();

            switch (enumdirection2) {
            case DOWN:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D + d0, this.locZ + 0.5D));
                d4 = d2;
                break;
            case UP:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY - d0, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D));
                d4 = -d2;
                break;
            case NORTH:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D + d0));
                d5 = d2;
                break;
            case SOUTH:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D - d0, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D));
                d5 = -d2;
                break;
            case WEST:
                this.a(new AxisAlignedBB(this.locX - 0.5D, this.locY, this.locZ - 0.5D, this.locX + 0.5D + d0, this.locY + 1.0D, this.locZ + 0.5D));
                d3 = d2;
                break;
            case EAST:
                this.a(new AxisAlignedBB(this.locX - 0.5D - d0, this.locY, this.locZ - 0.5D, this.locX + 0.5D, this.locY + 1.0D, this.locZ + 0.5D));
                d3 = -d2;
            }

            if (d2 > 0.0D) {
                List<Entity> list = this.world.getEntities(this, this.getBoundingBox());

                if (!list.isEmpty()) {
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        Entity entity = (Entity) iterator.next();

                        if (!(entity instanceof EntityShulker) && !entity.noclip) {
                            entity.move(EnumMoveType.SHULKER, d3, d4, d5);
                        }
                    }
                }
            }
        }

    }

    public void move(EnumMoveType enummovetype, double d0, double d1, double d2) {
        if (enummovetype == EnumMoveType.SHULKER_BOX) {
            this.l();
        } else {
            super.move(enummovetype, d0, d1, d2);
        }

    }

    public void setPosition(double d0, double d1, double d2) {
        super.setPosition(d0, d1, d2);
        if (this.datawatcher != null && this.ticksLived != 0) {
            Optional<BlockPosition> optional = (Optional) this.datawatcher.get(EntityShulker.b);
            Optional<BlockPosition> optional1 = Optional.of(new BlockPosition(d0, d1, d2));

            if (!optional1.equals(optional)) {
                this.datawatcher.set(EntityShulker.b, optional1);
                this.datawatcher.set(EntityShulker.c, (byte) 0);
                this.impulse = true;
            }

        }
    }

    protected boolean l() {
        if (!this.isNoAI() && this.isAlive()) {
            BlockPosition blockposition = new BlockPosition(this);

            for (int i = 0; i < 5; ++i) {
                BlockPosition blockposition1 = blockposition.a(8 - this.random.nextInt(17), 8 - this.random.nextInt(17), 8 - this.random.nextInt(17));

                if (blockposition1.getY() > 0 && this.world.isEmpty(blockposition1) && this.world.i((Entity) this) && this.world.getCubes(this, new AxisAlignedBB(blockposition1))) {
                    boolean flag = false;
                    EnumDirection[] aenumdirection = EnumDirection.values();
                    int j = aenumdirection.length;

                    for (int k = 0; k < j; ++k) {
                        EnumDirection enumdirection = aenumdirection[k];

                        if (this.world.q(blockposition1.shift(enumdirection))) {
                            this.datawatcher.set(EntityShulker.a, enumdirection);
                            flag = true;
                            break;
                        }
                    }

                    if (flag) {
                        this.a(SoundEffects.ENTITY_SHULKER_TELEPORT, 1.0F, 1.0F);
                        this.datawatcher.set(EntityShulker.b, Optional.of(blockposition1));
                        this.datawatcher.set(EntityShulker.c, (byte) 0);
                        this.setGoalTarget((EntityLiving) null);
                        return true;
                    }
                }
            }

            return false;
        } else {
            return true;
        }
    }

    public void movementTick() {
        super.movementTick();
        this.motX = 0.0D;
        this.motY = 0.0D;
        this.motZ = 0.0D;
        this.aR = 180.0F;
        this.aQ = 180.0F;
        this.yaw = 180.0F;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityShulker.b.equals(datawatcherobject) && this.world.isClientSide && !this.isPassenger()) {
            BlockPosition blockposition = this.dz();

            if (blockposition != null) {
                if (this.bH == null) {
                    this.bH = blockposition;
                } else {
                    this.bI = 6;
                }

                this.locX = (double) blockposition.getX() + 0.5D;
                this.locY = (double) blockposition.getY();
                this.locZ = (double) blockposition.getZ() + 0.5D;
                this.lastX = this.locX;
                this.lastY = this.locY;
                this.lastZ = this.locZ;
                this.N = this.locX;
                this.O = this.locY;
                this.P = this.locZ;
            }
        }

        super.a(datawatcherobject);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.dG()) {
            Entity entity = damagesource.j();

            if (entity instanceof EntityArrow) {
                return false;
            }
        }

        if (super.damageEntity(damagesource, f)) {
            if ((double) this.getHealth() < (double) this.getMaxHealth() * 0.5D && this.random.nextInt(4) == 0) {
                this.l();
            }

            return true;
        } else {
            return false;
        }
    }

    private boolean dG() {
        return this.dA() == 0;
    }

    @Nullable
    public AxisAlignedBB al() {
        return this.isAlive() ? this.getBoundingBox() : null;
    }

    public EnumDirection dy() {
        return (EnumDirection) this.datawatcher.get(EntityShulker.a);
    }

    @Nullable
    public BlockPosition dz() {
        return (BlockPosition) ((Optional) this.datawatcher.get(EntityShulker.b)).orElse((Object) null);
    }

    public void g(@Nullable BlockPosition blockposition) {
        this.datawatcher.set(EntityShulker.b, Optional.ofNullable(blockposition));
    }

    public int dA() {
        return (Byte) this.datawatcher.get(EntityShulker.c);
    }

    public void a(int i) {
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.h).c(EntityShulker.bE);
            if (i == 0) {
                this.getAttributeInstance(GenericAttributes.h).b(EntityShulker.bE);
                this.a(SoundEffects.ENTITY_SHULKER_CLOSE, 1.0F, 1.0F);
            } else {
                this.a(SoundEffects.ENTITY_SHULKER_OPEN, 1.0F, 1.0F);
            }
        }

        this.datawatcher.set(EntityShulker.c, (byte) i);
    }

    public float getHeadHeight() {
        return 0.5F;
    }

    public int K() {
        return 180;
    }

    public int L() {
        return 180;
    }

    public void collide(Entity entity) {}

    public float aM() {
        return 0.0F;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.F;
    }

    static class c extends PathfinderGoalNearestAttackableTarget<EntityLiving> {

        public c(EntityShulker entityshulker) {
            super(entityshulker, EntityLiving.class, 10, true, false, (entityliving) -> {
                return entityliving instanceof IMonster;
            });
        }

        public boolean a() {
            return this.e.getScoreboardTeam() == null ? false : super.a();
        }

        protected AxisAlignedBB a(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.e).dy();

            return enumdirection.k() == EnumDirection.EnumAxis.X ? this.e.getBoundingBox().grow(4.0D, d0, d0) : (enumdirection.k() == EnumDirection.EnumAxis.Z ? this.e.getBoundingBox().grow(d0, d0, 4.0D) : this.e.getBoundingBox().grow(d0, 4.0D, d0));
        }
    }

    class d extends PathfinderGoalNearestAttackableTarget<EntityHuman> {

        public d(EntityShulker entityshulker) {
            super(entityshulker, EntityHuman.class, true);
        }

        public boolean a() {
            return EntityShulker.this.world.getDifficulty() == EnumDifficulty.PEACEFUL ? false : super.a();
        }

        protected AxisAlignedBB a(double d0) {
            EnumDirection enumdirection = ((EntityShulker) this.e).dy();

            return enumdirection.k() == EnumDirection.EnumAxis.X ? this.e.getBoundingBox().grow(4.0D, d0, d0) : (enumdirection.k() == EnumDirection.EnumAxis.Z ? this.e.getBoundingBox().grow(d0, d0, 4.0D) : this.e.getBoundingBox().grow(d0, 4.0D, d0));
        }
    }

    class a extends PathfinderGoal {

        private int b;

        public a() {
            this.a(3);
        }

        public boolean a() {
            EntityLiving entityliving = EntityShulker.this.getGoalTarget();

            return entityliving != null && entityliving.isAlive() ? EntityShulker.this.world.getDifficulty() != EnumDifficulty.PEACEFUL : false;
        }

        public void c() {
            this.b = 20;
            EntityShulker.this.a(100);
        }

        public void d() {
            EntityShulker.this.a(0);
        }

        public void e() {
            if (EntityShulker.this.world.getDifficulty() != EnumDifficulty.PEACEFUL) {
                --this.b;
                EntityLiving entityliving = EntityShulker.this.getGoalTarget();

                EntityShulker.this.getControllerLook().a(entityliving, 180.0F, 180.0F);
                double d0 = EntityShulker.this.h(entityliving);

                if (d0 < 400.0D) {
                    if (this.b <= 0) {
                        this.b = 20 + EntityShulker.this.random.nextInt(10) * 20 / 2;
                        EntityShulkerBullet entityshulkerbullet = new EntityShulkerBullet(EntityShulker.this.world, EntityShulker.this, entityliving, EntityShulker.this.dy().k());

                        EntityShulker.this.world.addEntity(entityshulkerbullet);
                        EntityShulker.this.a(SoundEffects.ENTITY_SHULKER_SHOOT, 2.0F, (EntityShulker.this.random.nextFloat() - EntityShulker.this.random.nextFloat()) * 0.2F + 1.0F);
                    }
                } else {
                    EntityShulker.this.setGoalTarget((EntityLiving) null);
                }

                super.e();
            }
        }
    }

    class e extends PathfinderGoal {

        private int b;

        private e() {}

        public boolean a() {
            return EntityShulker.this.getGoalTarget() == null && EntityShulker.this.random.nextInt(40) == 0;
        }

        public boolean b() {
            return EntityShulker.this.getGoalTarget() == null && this.b > 0;
        }

        public void c() {
            this.b = 20 * (1 + EntityShulker.this.random.nextInt(3));
            EntityShulker.this.a(30);
        }

        public void d() {
            if (EntityShulker.this.getGoalTarget() == null) {
                EntityShulker.this.a(0);
            }

        }

        public void e() {
            --this.b;
        }
    }

    class b extends EntityAIBodyControl {

        public b(EntityLiving entityliving) {
            super(entityliving);
        }

        public void a() {}
    }
}
