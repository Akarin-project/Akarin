package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

public class EntityPhantom extends EntityFlying implements IMonster {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityPhantom.class, DataWatcherRegistry.b);
    private Vec3D b;
    private BlockPosition c;
    private EntityPhantom.AttackPhase bC;

    public EntityPhantom(World world) {
        super(EntityTypes.PHANTOM, world);
        this.b = Vec3D.a;
        this.c = BlockPosition.ZERO;
        this.bC = EntityPhantom.AttackPhase.CIRCLE;
        this.b_ = 5;
        this.setSize(0.9F, 0.5F);
        this.moveController = new EntityPhantom.g(this);
        this.lookController = new EntityPhantom.f(this);
    }

    protected EntityAIBodyControl o() {
        return new EntityPhantom.d(this);
    }

    protected void n() {
        this.goalSelector.a(1, new EntityPhantom.c());
        this.goalSelector.a(2, new EntityPhantom.i());
        this.goalSelector.a(3, new EntityPhantom.e());
        this.targetSelector.a(1, new EntityPhantom.b());
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityPhantom.a, 0);
    }

    public void setSize(int i) {
        if (i < 0) {
            i = 0;
        } else if (i > 64) {
            i = 64;
        }

        this.datawatcher.set(EntityPhantom.a, i);
        this.l();
    }

    public void l() {
        int i = (Integer) this.datawatcher.get(EntityPhantom.a);

        this.setSize(0.9F + 0.2F * (float) i, 0.5F + 0.1F * (float) i);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue((double) (6 + i));
    }

    public int getSize() {
        return (Integer) this.datawatcher.get(EntityPhantom.a);
    }

    public float getHeadHeight() {
        return this.length * 0.35F;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityPhantom.a.equals(datawatcherobject)) {
            this.l();
        }

        super.a(datawatcherobject);
    }

    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            float f = MathHelper.cos((float) (this.getId() * 3 + this.ticksLived) * 0.13F + 3.1415927F);
            float f1 = MathHelper.cos((float) (this.getId() * 3 + this.ticksLived + 1) * 0.13F + 3.1415927F);

            if (f > 0.0F && f1 <= 0.0F) {
                this.world.a(this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PHANTOM_FLAP, this.bV(), 0.95F + this.random.nextFloat() * 0.05F, 0.95F + this.random.nextFloat() * 0.05F, false);
            }

            int i = this.getSize();
            float f2 = MathHelper.cos(this.yaw * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f3 = MathHelper.sin(this.yaw * 0.017453292F) * (1.3F + 0.21F * (float) i);
            float f4 = (0.3F + f * 0.45F) * ((float) i * 0.2F + 1.0F);

            this.world.addParticle(Particles.H, this.locX + (double) f2, this.locY + (double) f4, this.locZ + (double) f3, 0.0D, 0.0D, 0.0D);
            this.world.addParticle(Particles.H, this.locX - (double) f2, this.locY + (double) f4, this.locZ - (double) f3, 0.0D, 0.0D, 0.0D);
        }

        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    public void movementTick() {
        if (this.dq()) {
            this.setOnFire(8);
        }

        super.movementTick();
    }

    protected void mobTick() {
        super.mobTick();
    }

    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.c = (new BlockPosition(this)).up(5);
        this.setSize(0);
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("AX")) {
            this.c = new BlockPosition(nbttagcompound.getInt("AX"), nbttagcompound.getInt("AY"), nbttagcompound.getInt("AZ"));
        }

        this.setSize(nbttagcompound.getInt("Size"));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("AX", this.c.getX());
        nbttagcompound.setInt("AY", this.c.getY());
        nbttagcompound.setInt("AZ", this.c.getZ());
        nbttagcompound.setInt("Size", this.getSize());
    }

    public SoundCategory bV() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_PHANTOM_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_PHANTOM_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_PHANTOM_DEATH;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.K;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    protected float cD() {
        return 1.0F;
    }

    public boolean b(Class<? extends EntityLiving> oclass) {
        return true;
    }

    class b extends PathfinderGoal {

        private int b;

        private b() {
            this.b = 20;
        }

        public boolean a() {
            if (this.b > 0) {
                --this.b;
                return false;
            } else {
                this.b = 60;
                AxisAlignedBB axisalignedbb = EntityPhantom.this.getBoundingBox().grow(16.0D, 64.0D, 16.0D);
                List<EntityHuman> list = EntityPhantom.this.world.a(EntityHuman.class, axisalignedbb);

                if (!list.isEmpty()) {
                    list.sort((entityhuman, entityhuman1) -> {
                        return entityhuman.locY > entityhuman1.locY ? -1 : 1;
                    });
                    Iterator iterator = list.iterator();

                    while (iterator.hasNext()) {
                        EntityHuman entityhuman = (EntityHuman) iterator.next();

                        if (PathfinderGoalTarget.a(EntityPhantom.this, entityhuman, false, false)) {
                            EntityPhantom.this.setGoalTarget(entityhuman);
                            return true;
                        }
                    }
                }

                return false;
            }
        }

        public boolean b() {
            return PathfinderGoalTarget.a(EntityPhantom.this, EntityPhantom.this.getGoalTarget(), false, false);
        }
    }

    class c extends PathfinderGoal {

        private int b;

        private c() {}

        public boolean a() {
            return PathfinderGoalTarget.a(EntityPhantom.this, EntityPhantom.this.getGoalTarget(), false, false);
        }

        public void c() {
            this.b = 10;
            EntityPhantom.this.bC = EntityPhantom.AttackPhase.CIRCLE;
            this.g();
        }

        public void d() {
            EntityPhantom.this.c = EntityPhantom.this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, EntityPhantom.this.c).up(10 + EntityPhantom.this.random.nextInt(20));
        }

        public void e() {
            if (EntityPhantom.this.bC == EntityPhantom.AttackPhase.CIRCLE) {
                --this.b;
                if (this.b <= 0) {
                    EntityPhantom.this.bC = EntityPhantom.AttackPhase.SWOOP;
                    this.g();
                    this.b = (8 + EntityPhantom.this.random.nextInt(4)) * 20;
                    EntityPhantom.this.a(SoundEffects.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + EntityPhantom.this.random.nextFloat() * 0.1F);
                }
            }

        }

        private void g() {
            EntityPhantom.this.c = (new BlockPosition(EntityPhantom.this.getGoalTarget())).up(20 + EntityPhantom.this.random.nextInt(20));
            if (EntityPhantom.this.c.getY() < EntityPhantom.this.world.getSeaLevel()) {
                EntityPhantom.this.c = new BlockPosition(EntityPhantom.this.c.getX(), EntityPhantom.this.world.getSeaLevel() + 1, EntityPhantom.this.c.getZ());
            }

        }
    }

    class i extends EntityPhantom.h {

        private i() {
            super();
        }

        public boolean a() {
            return EntityPhantom.this.getGoalTarget() != null && EntityPhantom.this.bC == EntityPhantom.AttackPhase.SWOOP;
        }

        public boolean b() {
            EntityLiving entityliving = EntityPhantom.this.getGoalTarget();

            return entityliving == null ? false : (!entityliving.isAlive() ? false : (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).u()) ? false : this.a()));
        }

        public void c() {}

        public void d() {
            EntityPhantom.this.setGoalTarget((EntityLiving) null);
            EntityPhantom.this.bC = EntityPhantom.AttackPhase.CIRCLE;
        }

        public void e() {
            EntityLiving entityliving = EntityPhantom.this.getGoalTarget();

            EntityPhantom.this.b = new Vec3D(entityliving.locX, entityliving.locY + (double) entityliving.length * 0.5D, entityliving.locZ);
            if (EntityPhantom.this.getBoundingBox().g(0.20000000298023224D).c(entityliving.getBoundingBox())) {
                EntityPhantom.this.B(entityliving);
                EntityPhantom.this.bC = EntityPhantom.AttackPhase.CIRCLE;
                EntityPhantom.this.world.triggerEffect(1039, new BlockPosition(EntityPhantom.this), 0);
            } else if (EntityPhantom.this.positionChanged || EntityPhantom.this.hurtTicks > 0) {
                EntityPhantom.this.bC = EntityPhantom.AttackPhase.CIRCLE;
            }

        }
    }

    class e extends EntityPhantom.h {

        private float c;
        private float d;
        private float e;
        private float f;

        private e() {
            super();
        }

        public boolean a() {
            return EntityPhantom.this.getGoalTarget() == null || EntityPhantom.this.bC == EntityPhantom.AttackPhase.CIRCLE;
        }

        public void c() {
            this.d = 5.0F + EntityPhantom.this.random.nextFloat() * 10.0F;
            this.e = -4.0F + EntityPhantom.this.random.nextFloat() * 9.0F;
            this.f = EntityPhantom.this.random.nextBoolean() ? 1.0F : -1.0F;
            this.i();
        }

        public void e() {
            if (EntityPhantom.this.random.nextInt(350) == 0) {
                this.e = -4.0F + EntityPhantom.this.random.nextFloat() * 9.0F;
            }

            if (EntityPhantom.this.random.nextInt(250) == 0) {
                ++this.d;
                if (this.d > 15.0F) {
                    this.d = 5.0F;
                    this.f = -this.f;
                }
            }

            if (EntityPhantom.this.random.nextInt(450) == 0) {
                this.c = EntityPhantom.this.random.nextFloat() * 2.0F * 3.1415927F;
                this.i();
            }

            if (this.g()) {
                this.i();
            }

            if (EntityPhantom.this.b.y < EntityPhantom.this.locY && !EntityPhantom.this.world.isEmpty((new BlockPosition(EntityPhantom.this)).down(1))) {
                this.e = Math.max(1.0F, this.e);
                this.i();
            }

            if (EntityPhantom.this.b.y > EntityPhantom.this.locY && !EntityPhantom.this.world.isEmpty((new BlockPosition(EntityPhantom.this)).up(1))) {
                this.e = Math.min(-1.0F, this.e);
                this.i();
            }

        }

        private void i() {
            if (BlockPosition.ZERO.equals(EntityPhantom.this.c)) {
                EntityPhantom.this.c = new BlockPosition(EntityPhantom.this);
            }

            this.c += this.f * 15.0F * 0.017453292F;
            EntityPhantom.this.b = (new Vec3D(EntityPhantom.this.c)).add((double) (this.d * MathHelper.cos(this.c)), (double) (-4.0F + this.e), (double) (this.d * MathHelper.sin(this.c)));
        }
    }

    abstract class h extends PathfinderGoal {

        public h() {
            this.a(1);
        }

        protected boolean g() {
            return EntityPhantom.this.b.c(EntityPhantom.this.locX, EntityPhantom.this.locY, EntityPhantom.this.locZ) < 4.0D;
        }
    }

    class f extends ControllerLook {

        public f(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        public void a() {}
    }

    class d extends EntityAIBodyControl {

        public d(EntityLiving entityliving) {
            super(entityliving);
        }

        public void a() {
            EntityPhantom.this.aS = EntityPhantom.this.aQ;
            EntityPhantom.this.aQ = EntityPhantom.this.yaw;
        }
    }

    class g extends ControllerMove {

        private float j = 0.1F;

        public g(EntityInsentient entityinsentient) {
            super(entityinsentient);
        }

        public void a() {
            if (EntityPhantom.this.positionChanged) {
                EntityPhantom.this.yaw += 180.0F;
                this.j = 0.1F;
            }

            float f = (float) (EntityPhantom.this.b.x - EntityPhantom.this.locX);
            float f1 = (float) (EntityPhantom.this.b.y - EntityPhantom.this.locY);
            float f2 = (float) (EntityPhantom.this.b.z - EntityPhantom.this.locZ);
            double d0 = (double) MathHelper.c(f * f + f2 * f2);
            double d1 = 1.0D - (double) MathHelper.e(f1 * 0.7F) / d0;

            f = (float) ((double) f * d1);
            f2 = (float) ((double) f2 * d1);
            d0 = (double) MathHelper.c(f * f + f2 * f2);
            double d2 = (double) MathHelper.c(f * f + f2 * f2 + f1 * f1);
            float f3 = EntityPhantom.this.yaw;
            float f4 = (float) MathHelper.c((double) f2, (double) f);
            float f5 = MathHelper.g(EntityPhantom.this.yaw + 90.0F);
            float f6 = MathHelper.g(f4 * 57.295776F);

            EntityPhantom.this.yaw = MathHelper.c(f5, f6, 4.0F) - 90.0F;
            EntityPhantom.this.aQ = EntityPhantom.this.yaw;
            if (MathHelper.d(f3, EntityPhantom.this.yaw) < 3.0F) {
                this.j = MathHelper.b(this.j, 1.8F, 0.005F * (1.8F / this.j));
            } else {
                this.j = MathHelper.b(this.j, 0.2F, 0.025F);
            }

            float f7 = (float) (-(MathHelper.c((double) (-f1), d0) * 57.2957763671875D));

            EntityPhantom.this.pitch = f7;
            float f8 = EntityPhantom.this.yaw + 90.0F;
            double d3 = (double) (this.j * MathHelper.cos(f8 * 0.017453292F)) * Math.abs((double) f / d2);
            double d4 = (double) (this.j * MathHelper.sin(f8 * 0.017453292F)) * Math.abs((double) f2 / d2);
            double d5 = (double) (this.j * MathHelper.sin(f7 * 0.017453292F)) * Math.abs((double) f1 / d2);

            EntityPhantom.this.motX += (d3 - EntityPhantom.this.motX) * 0.2D;
            EntityPhantom.this.motY += (d5 - EntityPhantom.this.motY) * 0.2D;
            EntityPhantom.this.motZ += (d4 - EntityPhantom.this.motZ) * 0.2D;
        }
    }

    static enum AttackPhase {

        CIRCLE, SWOOP;

        private AttackPhase() {}
    }
}
