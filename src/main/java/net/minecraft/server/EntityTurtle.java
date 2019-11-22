package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityTurtle extends EntityAnimal {

    private static final DataWatcherObject<BlockPosition> bA = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.l);
    private static final DataWatcherObject<Boolean> bB = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<BlockPosition> bD = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.l);
    private static final DataWatcherObject<Boolean> bE = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Boolean> bF = DataWatcher.a(EntityTurtle.class, DataWatcherRegistry.i);
    private int bG;
    public static final Predicate<EntityLiving> bz = (entityliving) -> {
        return entityliving.isBaby() && !entityliving.isInWater();
    };

    public EntityTurtle(EntityTypes<? extends EntityTurtle> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntityTurtle.e(this);
        this.K = 1.0F;
    }

    public final void setHome(BlockPosition pos) { g(pos); } // Paper - OBFHELPER
    public void g(BlockPosition blockposition) {
        this.datawatcher.set(EntityTurtle.bA, blockposition);
    }

    public final BlockPosition getHome() { return this.dX(); } // Paper - OBFHELPER
    private BlockPosition dX() {
        return (BlockPosition) this.datawatcher.get(EntityTurtle.bA);
    }

    public final void setTravelPos(BlockPosition pos) { this.h(pos); } // Paper - OBFHELPER
    private void h(BlockPosition blockposition) {
        this.datawatcher.set(EntityTurtle.bD, blockposition);
    }

    public final BlockPosition getTravelPos() { return this.dY(); } // Paper - OBFHELPER
    private BlockPosition dY() {
        return (BlockPosition) this.datawatcher.get(EntityTurtle.bD);
    }

    public final boolean hasEgg() { return this.dV(); } // Paper - OBFHELPER
    public boolean dV() {
        return (Boolean) this.datawatcher.get(EntityTurtle.bB);
    }

    public final void setHasEgg(boolean hasEgg) { this.r(hasEgg); } // Paper - OBFHELPER
    private void r(boolean flag) {
        this.datawatcher.set(EntityTurtle.bB, flag);
    }

    public final boolean isDigging() { return this.dW(); } // Paper - OBFHELPER
    public boolean dW() {
        return (Boolean) this.datawatcher.get(EntityTurtle.bC);
    }

    public final void setDigging(boolean digging) { this.s(digging); } // Paper - OBFHELPER
    private void s(boolean flag) {
        this.bG = flag ? 1 : 0;
        this.datawatcher.set(EntityTurtle.bC, flag);
    }

    public final boolean isGoingHome() { return this.dZ(); } // Paper - OBFHELPER
    private boolean dZ() {
        return (Boolean) this.datawatcher.get(EntityTurtle.bE);
    }

    public final void setGoingHome(boolean goingHome) { this.t(goingHome); } // Paper - OBFHELPER
    private void t(boolean flag) {
        this.datawatcher.set(EntityTurtle.bE, flag);
    }

    public final boolean isTravelling() { return this.ee(); } // Paper - OBFHELPER
    private boolean ee() {
        return (Boolean) this.datawatcher.get(EntityTurtle.bF);
    }

    public final void setTravelling(boolean travelling) { this.u(travelling); } // Paper - OBFHELPER
    private void u(boolean flag) {
        this.datawatcher.set(EntityTurtle.bF, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityTurtle.bA, BlockPosition.ZERO);
        this.datawatcher.register(EntityTurtle.bB, false);
        this.datawatcher.register(EntityTurtle.bD, BlockPosition.ZERO);
        this.datawatcher.register(EntityTurtle.bE, false);
        this.datawatcher.register(EntityTurtle.bF, false);
        this.datawatcher.register(EntityTurtle.bC, false);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("HomePosX", this.dX().getX());
        nbttagcompound.setInt("HomePosY", this.dX().getY());
        nbttagcompound.setInt("HomePosZ", this.dX().getZ());
        nbttagcompound.setBoolean("HasEgg", this.dV());
        nbttagcompound.setInt("TravelPosX", this.dY().getX());
        nbttagcompound.setInt("TravelPosY", this.dY().getY());
        nbttagcompound.setInt("TravelPosZ", this.dY().getZ());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("HomePosX");
        int j = nbttagcompound.getInt("HomePosY");
        int k = nbttagcompound.getInt("HomePosZ");

        this.g(new BlockPosition(i, j, k));
        super.a(nbttagcompound);
        this.r(nbttagcompound.getBoolean("HasEgg"));
        int l = nbttagcompound.getInt("TravelPosX");
        int i1 = nbttagcompound.getInt("TravelPosY");
        int j1 = nbttagcompound.getInt("TravelPosZ");

        this.h(new BlockPosition(l, i1, j1));
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.g(new BlockPosition(this));
        this.h(BlockPosition.ZERO);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    public static boolean c(EntityTypes<EntityTurtle> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return blockposition.getY() < generatoraccess.getSeaLevel() + 4 && generatoraccess.getType(blockposition.down()).getBlock() == Blocks.SAND && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new EntityTurtle.f(this, 1.2D));
        this.goalSelector.a(1, new EntityTurtle.a(this, 1.0D));
        this.goalSelector.a(1, new EntityTurtle.d(this, 1.0D));
        this.goalSelector.a(2, new EntityTurtle.i(this, 1.1D, Blocks.SEAGRASS.getItem()));
        this.goalSelector.a(3, new EntityTurtle.c(this, 1.0D));
        this.goalSelector.a(4, new EntityTurtle.b(this, 1.0D));
        this.goalSelector.a(7, new EntityTurtle.j(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(9, new EntityTurtle.h(this, 1.0D, 100));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(30.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
    }

    @Override
    public boolean bE() {
        return false;
    }

    @Override
    public boolean cm() {
        return true;
    }

    @Override
    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.e;
    }

    @Override
    public int A() {
        return 200;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return !this.isInWater() && this.onGround && !this.isBaby() ? SoundEffects.ENTITY_TURTLE_AMBIENT_LAND : super.getSoundAmbient();
    }

    @Override
    protected void d(float f) {
        super.d(f * 1.5F);
    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_TURTLE_SWIM;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return this.isBaby() ? SoundEffects.ENTITY_TURTLE_HURT_BABY : SoundEffects.ENTITY_TURTLE_HURT;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return this.isBaby() ? SoundEffects.ENTITY_TURTLE_DEATH_BABY : SoundEffects.ENTITY_TURTLE_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        SoundEffect soundeffect = this.isBaby() ? SoundEffects.ENTITY_TURTLE_SHAMBLE_BABY : SoundEffects.ENTITY_TURTLE_SHAMBLE;

        this.a(soundeffect, 0.15F, 1.0F);
    }

    @Override
    public boolean ea() {
        return super.ea() && !this.dV();
    }

    @Override
    protected float ag() {
        return this.F + 0.15F;
    }

    @Override
    public float cn() {
        return this.isBaby() ? 0.3F : 1.0F;
    }

    @Override
    protected NavigationAbstract b(World world) {
        return new EntityTurtle.g(this, world);
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return (EntityAgeable) EntityTypes.TURTLE.a(this.world);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return itemstack.getItem() == Blocks.SEAGRASS.getItem();
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return !this.dZ() && iworldreader.getFluid(blockposition).a(TagsFluid.WATER) ? 10.0F : (iworldreader.getType(blockposition.down()).getBlock() == Blocks.SAND ? 10.0F : iworldreader.v(blockposition) - 0.5F);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.isAlive() && this.dW() && this.bG >= 1 && this.bG % 5 == 0) {
            BlockPosition blockposition = new BlockPosition(this);

            if (this.world.getType(blockposition.down()).getBlock() == Blocks.SAND) {
                this.world.triggerEffect(2001, blockposition, Block.getCombinedId(Blocks.SAND.getBlockData()));
            }
        }

    }

    @Override
    protected void l() {
        super.l();
        if (!this.isBaby() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.forceDrops = true; // CraftBukkit
            this.a((IMaterial) Items.SCUTE, 1);
            this.forceDrops = false; // CraftBukkit
        }

    }

    @Override
    public void e(Vec3D vec3d) {
        if (this.df() && this.isInWater()) {
            this.a(0.1F, vec3d);
            this.move(EnumMoveType.SELF, this.getMot());
            this.setMot(this.getMot().a(0.9D));
            if (this.getGoalTarget() == null && (!this.dZ() || !this.dX().a((IPosition) this.getPositionVector(), 20.0D))) {
                this.setMot(this.getMot().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.e(vec3d);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        org.bukkit.craftbukkit.event.CraftEventFactory.entityDamage = entitylightning; // CraftBukkit
        this.damageEntity(DamageSource.LIGHTNING, Float.MAX_VALUE);
        org.bukkit.craftbukkit.event.CraftEventFactory.entityDamage = null; // CraftBukkit
    }

    static class g extends NavigationGuardian {

        g(EntityTurtle entityturtle, World world) {
            super(entityturtle, world);
        }

        @Override
        protected boolean a() {
            return true;
        }

        @Override
        protected Pathfinder a(int i) {
            return new Pathfinder(new PathfinderTurtle(), i);
        }

        @Override
        public boolean a(BlockPosition blockposition) {
            if (this.a instanceof EntityTurtle) {
                EntityTurtle entityturtle = (EntityTurtle) this.a;

                if (entityturtle.ee()) {
                    return this.b.getType(blockposition).getBlock() == Blocks.WATER;
                }
            }

            return !this.b.getType(blockposition.down()).isAir();
        }
    }

    static class e extends ControllerMove {

        private final EntityTurtle i;

        e(EntityTurtle entityturtle) {
            super(entityturtle);
            this.i = entityturtle;
        }

        private void g() {
            if (this.i.isInWater()) {
                this.i.setMot(this.i.getMot().add(0.0D, 0.005D, 0.0D));
                if (!this.i.dX().a((IPosition) this.i.getPositionVector(), 16.0D)) {
                    this.i.o(Math.max(this.i.db() / 2.0F, 0.08F));
                }

                if (this.i.isBaby()) {
                    this.i.o(Math.max(this.i.db() / 3.0F, 0.06F));
                }
            } else if (this.i.onGround) {
                this.i.o(Math.max(this.i.db() / 2.0F, 0.06F));
            }

        }

        @Override
        public void a() {
            this.g();
            if (this.h == ControllerMove.Operation.MOVE_TO && !this.i.getNavigation().n()) {
                double d0 = this.b - this.i.locX;
                double d1 = this.c - this.i.locY;
                double d2 = this.d - this.i.locZ;
                double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);

                d1 /= d3;
                float f = (float) (MathHelper.d(d2, d0) * 57.2957763671875D) - 90.0F;

                this.i.yaw = this.a(this.i.yaw, f, 90.0F);
                this.i.aK = this.i.yaw;
                float f1 = (float) (this.e * this.i.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());

                this.i.o(MathHelper.g(0.125F, this.i.db(), f1));
                this.i.setMot(this.i.getMot().add(0.0D, (double) this.i.db() * d1 * 0.1D, 0.0D));
            } else {
                this.i.o(0.0F);
            }
        }
    }

    static class c extends PathfinderGoalGotoTarget {

        private final EntityTurtle g;

        private c(EntityTurtle entityturtle, double d0) {
            super(entityturtle, entityturtle.isBaby() ? 2.0D : d0, 24);
            this.g = entityturtle;
            this.f = -1;
        }

        @Override
        public boolean b() {
            return !this.g.isInWater() && this.d <= 1200 && this.a(this.g.world, this.e);
        }

        @Override
        public boolean a() {
            return this.g.isBaby() && !this.g.isInWater() ? super.a() : (!this.g.dZ() && !this.g.isInWater() && !this.g.dV() ? super.a() : false);
        }

        @Override
        public boolean j() {
            return this.d % 160 == 0;
        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            Block block = iworldreader.getType(blockposition).getBlock();

            return block == Blocks.WATER;
        }
    }

    static class h extends PathfinderGoalRandomStroll {

        private final EntityTurtle h;

        private h(EntityTurtle entityturtle, double d0, int i) {
            super(entityturtle, d0, i);
            this.h = entityturtle;
        }

        @Override
        public boolean a() {
            return !this.a.isInWater() && !this.h.dZ() && !this.h.dV() ? super.a() : false;
        }
    }

    static class d extends PathfinderGoalGotoTarget {

        private final EntityTurtle g;

        d(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0, 16);
            this.g = entityturtle;
        }

        @Override
        public boolean a() {
            return this.g.dV() && this.g.dX().a((IPosition) this.g.getPositionVector(), 9.0D) ? super.a() : false;
        }

        @Override
        public boolean b() {
            return super.b() && this.g.dV() && this.g.dX().a((IPosition) this.g.getPositionVector(), 9.0D);
        }

        @Override
        public void e() {
            super.e();
            BlockPosition blockposition = new BlockPosition(this.g);

            if (!this.g.isInWater() && this.k()) {
                if (this.g.bG < 1) {
                    this.g.setDigging(new com.destroystokyo.paper.event.entity.TurtleStartDiggingEvent((org.bukkit.entity.Turtle) this.g.getBukkitEntity(), MCUtil.toLocation(this.g.world, this.e)).callEvent()); // Paper
                } else if (this.g.bG > 200) {
                    World world = this.g.world;

                    // CraftBukkit start
                    // Paper start
                    int eggCount = this.g.random.nextInt(4) + 1;
                    com.destroystokyo.paper.event.entity.TurtleLayEggEvent layEggEvent = new com.destroystokyo.paper.event.entity.TurtleLayEggEvent((org.bukkit.entity.Turtle) this.g.getBukkitEntity(), MCUtil.toLocation(this.g.world, this.e.up()), eggCount);
                    if (layEggEvent.callEvent() && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.g, this.e.up(), Blocks.TURTLE_EGG.getBlockData().set(BlockTurtleEgg.b, layEggEvent.getEggCount())).isCancelled()) {
                    world.playSound((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_LAY_EGG, SoundCategory.BLOCKS, 0.3F, 0.9F + world.random.nextFloat() * 0.2F);
                    world.setTypeAndData(this.e.up(), (IBlockData) Blocks.TURTLE_EGG.getBlockData().set(BlockTurtleEgg.b, layEggEvent.getEggCount()), 3);
                    }
                    // CraftBukkit end
                    this.g.r(false);
                    this.g.s(false);
                    this.g.setLoveTicks(600);
                }

                if (this.g.dW()) {
                    this.g.bG++;
                }
            }

        }

        @Override
        protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
            if (!iworldreader.isEmpty(blockposition.up())) {
                return false;
            } else {
                Block block = iworldreader.getType(blockposition).getBlock();

                return block == Blocks.SAND;
            }
        }
    }

    static class a extends PathfinderGoalBreed {

        private final EntityTurtle d;

        a(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0);
            this.d = entityturtle;
        }

        @Override
        public boolean a() {
            return super.a() && !this.d.dV();
        }

        @Override
        protected void g() {
            EntityPlayer entityplayer = this.animal.getBreedCause();

            if (entityplayer == null && this.partner.getBreedCause() != null) {
                entityplayer = this.partner.getBreedCause();
            }

            if (entityplayer != null) {
                entityplayer.a(StatisticList.ANIMALS_BRED);
                CriterionTriggers.o.a(entityplayer, this.animal, this.partner, (EntityAgeable) null);
            }

            this.d.r(true);
            this.animal.resetLove();
            this.partner.resetLove();
            Random random = this.animal.getRandom();

            if (this.b.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
                this.b.addEntity(new EntityExperienceOrb(this.b, this.animal.locX, this.animal.locY, this.animal.locZ, random.nextInt(7) + 1, org.bukkit.entity.ExperienceOrb.SpawnReason.BREED, entityplayer)); // Paper;
            }

        }
    }

    static class i extends PathfinderGoal {

        private static final PathfinderTargetCondition a = (new PathfinderTargetCondition()).a(10.0D).b().a();
        private final EntityTurtle b;
        private final double c;
        private EntityHuman d;
        private int e;
        private final Set<Item> f;

        i(EntityTurtle entityturtle, double d0, Item item) {
            this.b = entityturtle;
            this.c = d0;
            this.f = Sets.newHashSet(new Item[]{item});
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        }

        @Override
        public boolean a() {
            if (this.e > 0) {
                --this.e;
                return false;
            } else {
                this.d = this.b.world.a(this.a, (EntityLiving) this.b); // CraftBukkit - decompile error
                return this.d == null ? false : this.a(this.d.getItemInMainHand()) || this.a(this.d.getItemInOffHand());
            }
        }

        private boolean a(ItemStack itemstack) {
            return this.f.contains(itemstack.getItem());
        }

        @Override
        public boolean b() {
            return this.a();
        }

        @Override
        public void d() {
            this.d = null;
            this.b.getNavigation().o();
            this.e = 100;
        }

        @Override
        public void e() {
            this.b.getControllerLook().a(this.d, (float) (this.b.dA() + 20), (float) this.b.M());
            if (this.b.h((Entity) this.d) < 6.25D) {
                this.b.getNavigation().o();
            } else {
                this.b.getNavigation().a((Entity) this.d, this.c);
            }

        }
    }

    static class b extends PathfinderGoal {

        private final EntityTurtle a;
        private final double b;
        private boolean c;
        private int d;

        b(EntityTurtle entityturtle, double d0) {
            this.a = entityturtle;
            this.b = d0;
        }

        @Override
        public boolean a() {
            return this.a.isBaby() ? false : (this.a.dW() ? true : (this.a.getRandom().nextInt(700) != 0 ? false : !this.a.dY().a((IPosition) this.a.getPositionVector(), 64.0D))) && new com.destroystokyo.paper.event.entity.TurtleGoHomeEvent((org.bukkit.entity.Turtle) this.a.getBukkitEntity()).callEvent(); // Paper
        }

        @Override
        public void c() {
            this.a.t(true);
            this.c = false;
            this.d = 0;
        }

        @Override
        public void d() {
            this.a.t(false);
        }

        @Override
        public boolean b() {
            return !this.a.dX().a((IPosition) this.a.getPositionVector(), 7.0D) && !this.c && this.d <= 600;
        }

        @Override
        public void e() {
            BlockPosition blockposition = this.a.dX();
            boolean flag = blockposition.a((IPosition) this.a.getPositionVector(), 16.0D);

            if (flag) {
                ++this.d;
            }

            if (this.a.getNavigation().n()) {
                Vec3D vec3d = RandomPositionGenerator.a((EntityCreature) this.a, 16, 3, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()), 0.3141592741012573D);

                if (vec3d == null) {
                    vec3d = RandomPositionGenerator.a(this.a, 8, 7, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
                }

                if (vec3d != null && !flag && this.a.world.getType(new BlockPosition(vec3d)).getBlock() != Blocks.WATER) {
                    vec3d = RandomPositionGenerator.a(this.a, 16, 5, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
                }

                if (vec3d == null) {
                    this.c = true;
                    return;
                }

                this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, this.b);
            }

        }
    }

    static class j extends PathfinderGoal {

        private final EntityTurtle a;
        private final double b;
        private boolean c;

        j(EntityTurtle entityturtle, double d0) {
            this.a = entityturtle;
            this.b = d0;
        }

        @Override
        public boolean a() {
            return !this.a.dZ() && !this.a.dV() && this.a.isInWater();
        }

        @Override
        public void c() {
            boolean flag = true;
            boolean flag1 = true;
            Random random = this.a.random;
            int i = random.nextInt(1025) - 512;
            int j = random.nextInt(9) - 4;
            int k = random.nextInt(1025) - 512;

            if ((double) j + this.a.locY > (double) (this.a.world.getSeaLevel() - 1)) {
                j = 0;
            }

            BlockPosition blockposition = new BlockPosition((double) i + this.a.locX, (double) j + this.a.locY, (double) k + this.a.locZ);

            this.a.h(blockposition);
            this.a.u(true);
            this.c = false;
        }

        @Override
        public void e() {
            if (this.a.getNavigation().n()) {
                BlockPosition blockposition = this.a.dY();
                Vec3D vec3d = RandomPositionGenerator.a((EntityCreature) this.a, 16, 3, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()), 0.3141592741012573D);

                if (vec3d == null) {
                    vec3d = RandomPositionGenerator.a(this.a, 8, 7, new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
                }

                if (vec3d != null) {
                    int i = MathHelper.floor(vec3d.x);
                    int j = MathHelper.floor(vec3d.z);
                    boolean flag = true;

                    if (!this.a.world.isAreaLoaded(i - 34, 0, j - 34, i + 34, 0, j + 34)) {
                        vec3d = null;
                    }
                }

                if (vec3d == null) {
                    this.c = true;
                    return;
                }

                this.a.getNavigation().a(vec3d.x, vec3d.y, vec3d.z, this.b);
            }

        }

        @Override
        public boolean b() {
            return !this.a.getNavigation().n() && !this.c && !this.a.dZ() && !this.a.isInLove() && !this.a.dV();
        }

        @Override
        public void d() {
            this.a.u(false);
            super.d();
        }
    }

    static class f extends PathfinderGoalPanic {

        f(EntityTurtle entityturtle, double d0) {
            super(entityturtle, d0);
        }

        @Override
        public boolean a() {
            if (this.a.getLastDamager() == null && !this.a.isBurning()) {
                return false;
            } else {
                BlockPosition blockposition = this.a(this.a.world, this.a, 7, 4);

                if (blockposition != null) {
                    this.c = (double) blockposition.getX();
                    this.d = (double) blockposition.getY();
                    this.e = (double) blockposition.getZ();
                    return true;
                } else {
                    return this.g();
                }
            }
        }
    }
}
