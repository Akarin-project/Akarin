package net.minecraft.server;

import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;

import org.bukkit.event.entity.EntityTargetEvent; // CraftBukkit

public class EntityPanda extends EntityAnimal {

    private static final DataWatcherObject<Integer> bA = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bB = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bC = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Byte> bD = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Byte> bE = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Byte> bF = DataWatcher.a(EntityPanda.class, DataWatcherRegistry.a);
    private boolean bG;
    private boolean bH;
    public int bz;
    private Vec3D bI;
    private float bJ;
    private float bK;
    private float bL;
    private float bM;
    private float bN;
    private float bO;
    private static final Predicate<EntityItem> PICKUP_PREDICATE = (entityitem) -> {
        Item item = entityitem.getItemStack().getItem();

        return (item == Blocks.BAMBOO.getItem() || item == Blocks.CAKE.getItem()) && entityitem.isAlive() && !entityitem.q();
    };

    public EntityPanda(EntityTypes<? extends EntityPanda> entitytypes, World world) {
        super(entitytypes, world);
        this.moveController = new EntityPanda.i(this);
        if (!this.isBaby()) {
            this.setCanPickupLoot(true);
        }

    }

    @Override
    public boolean e(ItemStack itemstack) {
        EnumItemSlot enumitemslot = EntityInsentient.h(itemstack);

        return !this.getEquipment(enumitemslot).isEmpty() ? false : enumitemslot == EnumItemSlot.MAINHAND && super.e(itemstack);
    }

    public int dV() {
        return (Integer) this.datawatcher.get(EntityPanda.bA);
    }

    public void s(int i) {
        this.datawatcher.set(EntityPanda.bA, i);
    }

    public boolean dW() {
        return this.v(2);
    }

    public boolean dX() {
        return this.v(8);
    }

    public void r(boolean flag) {
        this.d(8, flag);
    }

    public boolean dY() {
        return this.v(16);
    }

    public void s(boolean flag) {
        this.d(16, flag);
    }

    public boolean dZ() {
        return (Integer) this.datawatcher.get(EntityPanda.bC) > 0;
    }

    public void t(boolean flag) {
        this.datawatcher.set(EntityPanda.bC, flag ? 1 : 0);
    }

    private int es() {
        return (Integer) this.datawatcher.get(EntityPanda.bC);
    }

    private void u(int i) {
        this.datawatcher.set(EntityPanda.bC, i);
    }

    public void u(boolean flag) {
        this.d(2, flag);
        if (!flag) {
            this.t(0);
        }

    }

    public int ee() {
        return (Integer) this.datawatcher.get(EntityPanda.bB);
    }

    public void t(int i) {
        this.datawatcher.set(EntityPanda.bB, i);
    }

    public EntityPanda.Gene getMainGene() {
        return EntityPanda.Gene.a((Byte) this.datawatcher.get(EntityPanda.bD));
    }

    public void setMainGene(EntityPanda.Gene entitypanda_gene) {
        if (entitypanda_gene.a() > 6) {
            entitypanda_gene = EntityPanda.Gene.a(this.random);
        }

        this.datawatcher.set(EntityPanda.bD, (byte) entitypanda_gene.a());
    }

    public EntityPanda.Gene getHiddenGene() {
        return EntityPanda.Gene.a((Byte) this.datawatcher.get(EntityPanda.bE));
    }

    public void setHiddenGene(EntityPanda.Gene entitypanda_gene) {
        if (entitypanda_gene.a() > 6) {
            entitypanda_gene = EntityPanda.Gene.a(this.random);
        }

        this.datawatcher.set(EntityPanda.bE, (byte) entitypanda_gene.a());
    }

    public boolean eh() {
        return this.v(4);
    }

    public void v(boolean flag) {
        this.d(4, flag);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPanda.bA, 0);
        this.datawatcher.register(EntityPanda.bB, 0);
        this.datawatcher.register(EntityPanda.bD, (byte) 0);
        this.datawatcher.register(EntityPanda.bE, (byte) 0);
        this.datawatcher.register(EntityPanda.bF, (byte) 0);
        this.datawatcher.register(EntityPanda.bC, 0);
    }

    private boolean v(int i) {
        return ((Byte) this.datawatcher.get(EntityPanda.bF) & i) != 0;
    }

    private void d(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityPanda.bF);

        if (flag) {
            this.datawatcher.set(EntityPanda.bF, (byte) (b0 | i));
        } else {
            this.datawatcher.set(EntityPanda.bF, (byte) (b0 & ~i));
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setString("MainGene", this.getMainGene().b());
        nbttagcompound.setString("HiddenGene", this.getHiddenGene().b());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setMainGene(EntityPanda.Gene.a(nbttagcompound.getString("MainGene")));
        this.setHiddenGene(EntityPanda.Gene.a(nbttagcompound.getString("HiddenGene")));
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        EntityPanda entitypanda = (EntityPanda) EntityTypes.PANDA.a(this.world);

        if (entityageable instanceof EntityPanda) {
            entitypanda.a(this, (EntityPanda) entityageable);
        }

        entitypanda.ep();
        return entitypanda;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new EntityPanda.j(this, 2.0D));
        this.goalSelector.a(2, new EntityPanda.d(this, 1.0D));
        this.goalSelector.a(3, new EntityPanda.b(this, 1.2000000476837158D, true));
        this.goalSelector.a(4, new PathfinderGoalTempt(this, 1.0D, RecipeItemStack.a(Blocks.BAMBOO.getItem()), false));
        this.goalSelector.a(6, new EntityPanda.c<>(this, EntityHuman.class, 8.0F, 2.0D, 2.0D));
        this.goalSelector.a(6, new EntityPanda.c<>(this, EntityMonster.class, 4.0F, 2.0D, 2.0D));
        this.goalSelector.a(7, new EntityPanda.l());
        this.goalSelector.a(8, new EntityPanda.g(this));
        this.goalSelector.a(8, new EntityPanda.m(this));
        this.goalSelector.a(9, new EntityPanda.h(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(10, new PathfinderGoalRandomLookaround(this));
        this.goalSelector.a(12, new EntityPanda.k(this));
        this.goalSelector.a(13, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(14, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.targetSelector.a(1, (new EntityPanda.f(this, new Class[0])).a(new Class[0]));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.15000000596046448D);
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE).setValue(6.0D);
    }

    public EntityPanda.Gene ei() {
        return EntityPanda.Gene.b(this.getMainGene(), this.getHiddenGene());
    }

    public boolean ej() {
        return this.ei() == EntityPanda.Gene.LAZY;
    }

    public boolean ek() {
        return this.ei() == EntityPanda.Gene.WORRIED;
    }

    public boolean el() {
        return this.ei() == EntityPanda.Gene.PLAYFUL;
    }

    public boolean en() {
        return this.ei() == EntityPanda.Gene.WEAK;
    }

    @Override
    public boolean dR() {
        return this.ei() == EntityPanda.Gene.AGGRESSIVE;
    }

    @Override
    public boolean a(EntityHuman entityhuman) {
        return false;
    }

    @Override
    public boolean C(Entity entity) {
        this.a(SoundEffects.ENTITY_PANDA_BITE, 1.0F, 1.0F);
        if (!this.dR()) {
            this.bH = true;
        }

        return super.C(entity);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.ek()) {
            if (this.world.U() && !this.isInWater()) {
                this.r(true);
                this.t(false);
            } else if (!this.dZ()) {
                this.r(false);
            }
        }

        if (this.getGoalTarget() == null) {
            this.bG = false;
            this.bH = false;
        }

        if (this.dV() > 0) {
            if (this.getGoalTarget() != null) {
                this.a((Entity) this.getGoalTarget(), 90.0F, 90.0F);
            }

            if (this.dV() == 29 || this.dV() == 14) {
                this.a(SoundEffects.ENTITY_PANDA_CANT_BREED, 1.0F, 1.0F);
            }

            this.s(this.dV() - 1);
        }

        if (this.dW()) {
            this.t(this.ee() + 1);
            if (this.ee() > 20) {
                this.u(false);
                this.ez();
            } else if (this.ee() == 1) {
                this.a(SoundEffects.ENTITY_PANDA_PRE_SNEEZE, 1.0F, 1.0F);
            }
        }

        if (this.eh()) {
            this.ey();
        } else {
            this.bz = 0;
        }

        if (this.dX()) {
            this.pitch = 0.0F;
        }

        this.ev();
        this.et();
        this.ew();
        this.ex();
    }

    public boolean eo() {
        return this.ek() && this.world.U();
    }

    private void et() {
        if (!this.dZ() && this.dX() && !this.eo() && !this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() && this.random.nextInt(80) == 1) {
            this.t(true);
        } else if (this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() || !this.dX()) {
            this.t(false);
        }

        if (this.dZ()) {
            this.eu();
            if (!this.world.isClientSide && this.es() > 80 && this.random.nextInt(20) == 1) {
                if (this.es() > 100 && this.j(this.getEquipment(EnumItemSlot.MAINHAND))) {
                    if (!this.world.isClientSide) {
                        this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                    }

                    this.r(false);
                }

                this.t(false);
                return;
            }

            this.u(this.es() + 1);
        }

    }

    private void eu() {
        if (this.es() % 5 == 0) {
            this.a(SoundEffects.ENTITY_PANDA_EAT, 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);

            for (int i = 0; i < 6; ++i) {
                Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, ((double) this.random.nextFloat() - 0.5D) * 0.1D);

                vec3d = vec3d.a(-this.pitch * 0.017453292F);
                vec3d = vec3d.b(-this.yaw * 0.017453292F);
                double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
                Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.8D, d0, 1.0D + ((double) this.random.nextFloat() - 0.5D) * 0.4D);

                vec3d1 = vec3d1.b(-this.aK * 0.017453292F);
                vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight() + 1.0D, this.locZ);
                this.world.addParticle(new ParticleParamItem(Particles.ITEM, this.getEquipment(EnumItemSlot.MAINHAND)), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
            }
        }

    }

    private void ev() {
        this.bK = this.bJ;
        if (this.dX()) {
            this.bJ = Math.min(1.0F, this.bJ + 0.15F);
        } else {
            this.bJ = Math.max(0.0F, this.bJ - 0.19F);
        }

    }

    private void ew() {
        this.bM = this.bL;
        if (this.dY()) {
            this.bL = Math.min(1.0F, this.bL + 0.15F);
        } else {
            this.bL = Math.max(0.0F, this.bL - 0.19F);
        }

    }

    private void ex() {
        this.bO = this.bN;
        if (this.eh()) {
            this.bN = Math.min(1.0F, this.bN + 0.15F);
        } else {
            this.bN = Math.max(0.0F, this.bN - 0.19F);
        }

    }

    private void ey() {
        ++this.bz;
        if (this.bz > 32) {
            this.v(false);
        } else {
            if (!this.world.isClientSide) {
                Vec3D vec3d = this.getMot();

                if (this.bz == 1) {
                    float f = this.yaw * 0.017453292F;
                    float f1 = this.isBaby() ? 0.1F : 0.2F;

                    this.bI = new Vec3D(vec3d.x + (double) (-MathHelper.sin(f) * f1), 0.0D, vec3d.z + (double) (MathHelper.cos(f) * f1));
                    this.setMot(this.bI.add(0.0D, 0.27D, 0.0D));
                } else if ((float) this.bz != 7.0F && (float) this.bz != 15.0F && (float) this.bz != 23.0F) {
                    this.setMot(this.bI.x, vec3d.y, this.bI.z);
                } else {
                    this.setMot(0.0D, this.onGround ? 0.27D : vec3d.y, 0.0D);
                }
            }

        }
    }

    private void ez() {
        Vec3D vec3d = this.getMot();

        this.world.addParticle(Particles.SNEEZE, this.locX - (double) (this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.sin(this.aK * 0.017453292F), this.locY + (double) this.getHeadHeight() - 0.10000000149011612D, this.locZ + (double) (this.getWidth() + 1.0F) * 0.5D * (double) MathHelper.cos(this.aK * 0.017453292F), vec3d.x, 0.0D, vec3d.z);
        this.a(SoundEffects.ENTITY_PANDA_SNEEZE, 1.0F, 1.0F);
        List<EntityPanda> list = this.world.a(EntityPanda.class, this.getBoundingBox().g(10.0D));
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            EntityPanda entitypanda = (EntityPanda) iterator.next();

            if (!entitypanda.isBaby() && entitypanda.onGround && !entitypanda.isInWater() && entitypanda.eq()) {
                entitypanda.jump();
            }
        }

        if (!this.world.e() && this.random.nextInt(700) == 0 && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.a((IMaterial) Items.SLIME_BALL);
        }

    }

    @Override
    protected void a(EntityItem entityitem) {
        if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPickupItemEvent(this, entityitem, 0, !(this.getEquipment(EnumItemSlot.MAINHAND).isEmpty() && EntityPanda.PICKUP_PREDICATE.test(entityitem))).isCancelled()) { // CraftBukkit
            ItemStack itemstack = entityitem.getItemStack();

            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
            this.dropChanceHand[EnumItemSlot.MAINHAND.b()] = 2.0F;
            this.receive(entityitem, itemstack.getCount());
            entityitem.die();
        }

    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        this.r(false);
        return super.damageEntity(damagesource, f);
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);

        this.setMainGene(EntityPanda.Gene.a(this.random));
        this.setHiddenGene(EntityPanda.Gene.a(this.random));
        this.ep();
        if (object instanceof EntityPanda.e) {
            if (this.random.nextInt(5) == 0) {
                this.setAgeRaw(-24000);
            }
        } else {
            object = new EntityPanda.e();
        }

        return (GroupDataEntity) object;
    }

    public void a(EntityPanda entitypanda, @Nullable EntityPanda entitypanda1) {
        if (entitypanda1 == null) {
            if (this.random.nextBoolean()) {
                this.setMainGene(entitypanda.eA());
                this.setHiddenGene(EntityPanda.Gene.a(this.random));
            } else {
                this.setMainGene(EntityPanda.Gene.a(this.random));
                this.setHiddenGene(entitypanda.eA());
            }
        } else if (this.random.nextBoolean()) {
            this.setMainGene(entitypanda.eA());
            this.setHiddenGene(entitypanda1.eA());
        } else {
            this.setMainGene(entitypanda1.eA());
            this.setHiddenGene(entitypanda.eA());
        }

        if (this.random.nextInt(32) == 0) {
            this.setMainGene(EntityPanda.Gene.a(this.random));
        }

        if (this.random.nextInt(32) == 0) {
            this.setHiddenGene(EntityPanda.Gene.a(this.random));
        }

    }

    private EntityPanda.Gene eA() {
        return this.random.nextBoolean() ? this.getMainGene() : this.getHiddenGene();
    }

    public void ep() {
        if (this.en()) {
            this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        }

        if (this.ej()) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.07000000029802322D);
        }

    }

    private void eB() {
        if (!this.isInWater()) {
            this.r(0.0F);
            this.getNavigation().o();
            this.r(true);
        }

    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else if (this.eo()) {
            return false;
        } else if (this.dY()) {
            this.s(false);
            return true;
        } else if (this.i(itemstack)) {
            if (this.getGoalTarget() != null) {
                this.bG = true;
            }

            if (this.isBaby()) {
                this.a(entityhuman, itemstack);
                this.setAge((int) ((float) (-this.getAge() / 20) * 0.1F), true);
            } else if (!this.world.isClientSide && this.getAge() == 0 && this.ea()) {
                this.a(entityhuman, itemstack);
                this.f(entityhuman);
            } else {
                if (this.world.isClientSide || this.dX() || this.isInWater()) {
                    return false;
                }

                this.eB();
                this.t(true);
                ItemStack itemstack1 = this.getEquipment(EnumItemSlot.MAINHAND);

                if (!itemstack1.isEmpty() && !entityhuman.abilities.canInstantlyBuild) {
                    this.a(itemstack1);
                }

                this.setSlot(EnumItemSlot.MAINHAND, new ItemStack(itemstack.getItem(), 1));
                this.a(entityhuman, itemstack);
            }

            return true;
        } else {
            return false;
        }
    }

    @Nullable
    @Override
    protected SoundEffect getSoundAmbient() {
        return this.dR() ? SoundEffects.ENTITY_PANDA_AGGRESSIVE_AMBIENT : (this.ek() ? SoundEffects.ENTITY_PANDA_WORRIED_AMBIENT : SoundEffects.ENTITY_PANDA_AMBIENT);
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_PANDA_STEP, 0.15F, 1.0F);
    }

    @Override
    public boolean i(ItemStack itemstack) {
        return itemstack.getItem() == Blocks.BAMBOO.getItem();
    }

    private boolean j(ItemStack itemstack) {
        return this.i(itemstack) || itemstack.getItem() == Blocks.CAKE.getItem();
    }

    @Nullable
    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PANDA_DEATH;
    }

    @Nullable
    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PANDA_HURT;
    }

    public boolean eq() {
        return !this.dY() && !this.eo() && !this.dZ() && !this.eh() && !this.dX();
    }

    static class j extends PathfinderGoalPanic {

        private final EntityPanda f;

        public j(EntityPanda entitypanda, double d0) {
            super(entitypanda, d0);
            this.f = entitypanda;
        }

        @Override
        public boolean a() {
            if (!this.f.isBurning()) {
                return false;
            } else {
                BlockPosition blockposition = this.a(this.a.world, this.a, 5, 4);

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

        @Override
        public boolean b() {
            if (this.f.dX()) {
                this.f.getNavigation().o();
                return false;
            } else {
                return super.b();
            }
        }
    }

    static class f extends PathfinderGoalHurtByTarget {

        private final EntityPanda a;

        public f(EntityPanda entitypanda, Class<?>... aclass) {
            super(entitypanda, aclass);
            this.a = entitypanda;
        }

        @Override
        public boolean b() {
            if (!this.a.bG && !this.a.bH) {
                return super.b();
            } else {
                this.a.setGoalTarget((EntityLiving) null);
                return false;
            }
        }

        @Override
        protected void a(EntityInsentient entityinsentient, EntityLiving entityliving) {
            if (entityinsentient instanceof EntityPanda && ((EntityPanda) entityinsentient).dR()) {
                entityinsentient.setGoalTarget(entityliving, EntityTargetEvent.TargetReason.TARGET_ATTACKED_ENTITY, true); // CraftBukkit
            }

        }
    }

    static class g extends PathfinderGoal {

        private final EntityPanda a;
        private int b;

        public g(EntityPanda entitypanda) {
            this.a = entitypanda;
        }

        @Override
        public boolean a() {
            return this.b < this.a.ticksLived && this.a.ej() && this.a.eq() && this.a.random.nextInt(400) == 1;
        }

        @Override
        public boolean b() {
            return !this.a.isInWater() && (this.a.ej() || this.a.random.nextInt(600) != 1) ? this.a.random.nextInt(2000) != 1 : false;
        }

        @Override
        public void c() {
            this.a.s(true);
            this.b = 0;
        }

        @Override
        public void d() {
            this.a.s(false);
            this.b = this.a.ticksLived + 200;
        }
    }

    class l extends PathfinderGoal {

        private int b;

        public l() {
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public boolean a() {
            if (this.b <= EntityPanda.this.ticksLived && !EntityPanda.this.isBaby() && !EntityPanda.this.isInWater() && EntityPanda.this.eq() && EntityPanda.this.dV() <= 0) {
                List<EntityItem> list = EntityPanda.this.world.a(EntityItem.class, EntityPanda.this.getBoundingBox().grow(6.0D, 6.0D, 6.0D), EntityPanda.PICKUP_PREDICATE);

                return !list.isEmpty() || !EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty();
            } else {
                return false;
            }
        }

        @Override
        public boolean b() {
            return !EntityPanda.this.isInWater() && (EntityPanda.this.ej() || EntityPanda.this.random.nextInt(600) != 1) ? EntityPanda.this.random.nextInt(2000) != 1 : false;
        }

        @Override
        public void e() {
            if (!EntityPanda.this.dX() && !EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.eB();
            }

        }

        @Override
        public void c() {
            List<EntityItem> list = EntityPanda.this.world.a(EntityItem.class, EntityPanda.this.getBoundingBox().grow(8.0D, 8.0D, 8.0D), EntityPanda.PICKUP_PREDICATE);

            if (!list.isEmpty() && EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.getNavigation().a((Entity) list.get(0), 1.2000000476837158D);
            } else if (!EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND).isEmpty()) {
                EntityPanda.this.eB();
            }

            this.b = 0;
        }

        @Override
        public void d() {
            ItemStack itemstack = EntityPanda.this.getEquipment(EnumItemSlot.MAINHAND);

            if (!itemstack.isEmpty()) {
                EntityPanda.this.a(itemstack);
                EntityPanda.this.setSlot(EnumItemSlot.MAINHAND, ItemStack.a);
                int i = EntityPanda.this.ej() ? EntityPanda.this.random.nextInt(50) + 10 : EntityPanda.this.random.nextInt(150) + 10;

                this.b = EntityPanda.this.ticksLived + i * 20;
            }

            EntityPanda.this.r(false);
        }
    }

    static class c<T extends EntityLiving> extends PathfinderGoalAvoidTarget<T> {

        private final EntityPanda i;

        public c(EntityPanda entitypanda, Class<T> oclass, float f, double d0, double d1) {
            // Predicate predicate = IEntitySelector.f; // CraftBukkit - decompile error

            super(entitypanda, oclass, f, d0, d1, IEntitySelector.f::test); // CraftBukkit - decompile error
            this.i = entitypanda;
        }

        @Override
        public boolean a() {
            return this.i.ek() && this.i.eq() && super.a();
        }
    }

    static class d extends PathfinderGoalBreed {

        private static final PathfinderTargetCondition d = (new PathfinderTargetCondition()).a(8.0D).b().a();
        private final EntityPanda e;
        private int f;

        public d(EntityPanda entitypanda, double d0) {
            super(entitypanda, d0);
            this.e = entitypanda;
        }

        @Override
        public boolean a() {
            if (super.a() && this.e.dV() == 0) {
                if (!this.h()) {
                    if (this.f <= this.e.ticksLived) {
                        this.e.s(32);
                        this.f = this.e.ticksLived + 600;
                        if (this.e.df()) {
                            EntityHuman entityhuman = this.b.a(d, (EntityLiving) this.e); // CraftBukkit - decompile error

                            this.e.setGoalTarget(entityhuman, EntityTargetEvent.TargetReason.CLOSEST_PLAYER, true); // CraftBukkit
                        }
                    }

                    return false;
                } else {
                    return true;
                }
            } else {
                return false;
            }
        }

        private boolean h() {
            BlockPosition blockposition = new BlockPosition(this.e);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 8; ++j) {
                    for (int k = 0; k <= j; k = k > 0 ? -k : 1 - k) {
                        for (int l = k < j && k > -j ? j : 0; l <= j; l = l > 0 ? -l : 1 - l) {
                            blockposition_mutableblockposition.g(blockposition).e(k, i, l);
                            if (this.b.getType(blockposition_mutableblockposition).getBlock() == Blocks.BAMBOO) {
                                return true;
                            }
                        }
                    }
                }
            }

            return false;
        }
    }

    static class m extends PathfinderGoal {

        private final EntityPanda a;

        public m(EntityPanda entitypanda) {
            this.a = entitypanda;
        }

        @Override
        public boolean a() {
            return this.a.isBaby() && this.a.eq() ? (this.a.en() && this.a.random.nextInt(500) == 1 ? true : this.a.random.nextInt(6000) == 1) : false;
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void c() {
            this.a.u(true);
        }
    }

    static class k extends PathfinderGoal {

        private final EntityPanda a;

        public k(EntityPanda entitypanda) {
            this.a = entitypanda;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK, PathfinderGoal.Type.JUMP));
        }

        @Override
        public boolean a() {
            if ((this.a.isBaby() || this.a.el()) && this.a.onGround) {
                if (!this.a.eq()) {
                    return false;
                } else {
                    float f = this.a.yaw * 0.017453292F;
                    int i = 0;
                    int j = 0;
                    float f1 = -MathHelper.sin(f);
                    float f2 = MathHelper.cos(f);

                    if ((double) Math.abs(f1) > 0.5D) {
                        i = (int) ((float) i + f1 / Math.abs(f1));
                    }

                    if ((double) Math.abs(f2) > 0.5D) {
                        j = (int) ((float) j + f2 / Math.abs(f2));
                    }

                    return this.a.world.getType((new BlockPosition(this.a)).b(i, -1, j)).isAir() ? true : (this.a.el() && this.a.random.nextInt(60) == 1 ? true : this.a.random.nextInt(500) == 1);
                }
            } else {
                return false;
            }
        }

        @Override
        public boolean b() {
            return false;
        }

        @Override
        public void c() {
            this.a.v(true);
        }

        @Override
        public boolean C_() {
            return false;
        }
    }

    static class h extends PathfinderGoalLookAtPlayer {

        private final EntityPanda f;

        public h(EntityPanda entitypanda, Class<? extends EntityLiving> oclass, float f) {
            super(entitypanda, oclass, f);
            this.f = entitypanda;
        }

        @Override
        public boolean a() {
            return this.f.eq() && super.a();
        }
    }

    static class b extends PathfinderGoalMeleeAttack {

        private final EntityPanda d;

        public b(EntityPanda entitypanda, double d0, boolean flag) {
            super(entitypanda, d0, flag);
            this.d = entitypanda;
        }

        @Override
        public boolean a() {
            return this.d.eq() && super.a();
        }
    }

    static class e implements GroupDataEntity {

        private e() {}
    }

    static class i extends ControllerMove {

        private final EntityPanda i;

        public i(EntityPanda entitypanda) {
            super(entitypanda);
            this.i = entitypanda;
        }

        @Override
        public void a() {
            if (this.i.eq()) {
                super.a();
            }
        }
    }

    public static enum Gene {

        NORMAL(0, "normal", false), LAZY(1, "lazy", false), WORRIED(2, "worried", false), PLAYFUL(3, "playful", false), BROWN(4, "brown", true), WEAK(5, "weak", true), AGGRESSIVE(6, "aggressive", false);

        private static final EntityPanda.Gene[] h = (EntityPanda.Gene[]) Arrays.stream(values()).sorted(Comparator.comparingInt(EntityPanda.Gene::a)).toArray((i) -> {
            return new EntityPanda.Gene[i];
        });
        private final int i;
        private final String j;
        private final boolean k;

        private Gene(int i, String s, boolean flag) {
            this.i = i;
            this.j = s;
            this.k = flag;
        }

        public int a() {
            return this.i;
        }

        public String b() {
            return this.j;
        }

        public boolean isRecessive() {
            return this.k;
        }

        private static EntityPanda.Gene b(EntityPanda.Gene entitypanda_gene, EntityPanda.Gene entitypanda_gene1) {
            return entitypanda_gene.isRecessive() ? (entitypanda_gene == entitypanda_gene1 ? entitypanda_gene : EntityPanda.Gene.NORMAL) : entitypanda_gene;
        }

        public static EntityPanda.Gene a(int i) {
            if (i < 0 || i >= EntityPanda.Gene.h.length) {
                i = 0;
            }

            return EntityPanda.Gene.h[i];
        }

        public static EntityPanda.Gene a(String s) {
            EntityPanda.Gene[] aentitypanda_gene = values();
            int i = aentitypanda_gene.length;

            for (int j = 0; j < i; ++j) {
                EntityPanda.Gene entitypanda_gene = aentitypanda_gene[j];

                if (entitypanda_gene.j.equals(s)) {
                    return entitypanda_gene;
                }
            }

            return EntityPanda.Gene.NORMAL;
        }

        public static EntityPanda.Gene a(Random random) {
            int i = random.nextInt(16);

            return i == 0 ? EntityPanda.Gene.LAZY : (i == 1 ? EntityPanda.Gene.WORRIED : (i == 2 ? EntityPanda.Gene.PLAYFUL : (i == 4 ? EntityPanda.Gene.AGGRESSIVE : (i < 9 ? EntityPanda.Gene.WEAK : (i < 11 ? EntityPanda.Gene.BROWN : EntityPanda.Gene.NORMAL)))));
        }
    }
}
