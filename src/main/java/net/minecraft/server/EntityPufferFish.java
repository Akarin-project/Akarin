package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPufferFish extends EntityFish {

    private static final DataWatcherObject<Integer> a = DataWatcher.a(EntityPufferFish.class, DataWatcherRegistry.b);
    private int b;
    private int c;
    private static final Predicate<EntityLiving> bC = (entityliving) -> {
        return entityliving == null ? false : (entityliving instanceof EntityHuman && (((EntityHuman) entityliving).isSpectator() || ((EntityHuman) entityliving).u()) ? false : entityliving.getMonsterType() != EnumMonsterType.e);
    };
    private float bD = -1.0F;
    private float bE;

    public EntityPufferFish(World world) {
        super(EntityTypes.PUFFERFISH, world);
        this.setSize(0.7F, 0.7F);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityPufferFish.a, 0);
    }

    public int getPuffState() {
        return (Integer) this.datawatcher.get(EntityPufferFish.a);
    }

    public void setPuffState(int i) {
        this.datawatcher.set(EntityPufferFish.a, i);
        this.d(i);
    }

    private void d(int i) {
        float f = 1.0F;

        if (i == 1) {
            f = 0.7F;
        } else if (i == 0) {
            f = 0.5F;
        }

        this.a(f);
    }

    public final void setSize(float f, float f1) {
        boolean flag = this.bD > 0.0F;

        this.bD = f;
        this.bE = f1;
        if (!flag) {
            this.a(1.0F);
        }

    }

    private void a(float f) {
        super.setSize(this.bD * f, this.bE * f);
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        this.d(this.getPuffState());
        super.a(datawatcherobject);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("PuffState", this.getPuffState());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setPuffState(nbttagcompound.getInt("PuffState"));
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aF;
    }

    protected ItemStack l() {
        return new ItemStack(Items.PUFFERFISH_BUCKET);
    }

    protected void n() {
        super.n();
        this.goalSelector.a(1, new EntityPufferFish.a(this));
    }

    public void tick() {
        if (this.isAlive() && !this.world.isClientSide) {
            if (this.b > 0) {
                if (this.getPuffState() == 0) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_UP, this.cD(), this.cE());
                    this.setPuffState(1);
                } else if (this.b > 40 && this.getPuffState() == 1) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_UP, this.cD(), this.cE());
                    this.setPuffState(2);
                }

                ++this.b;
            } else if (this.getPuffState() != 0) {
                if (this.c > 60 && this.getPuffState() == 2) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_OUT, this.cD(), this.cE());
                    this.setPuffState(1);
                } else if (this.c > 100 && this.getPuffState() == 1) {
                    this.a(SoundEffects.ENTITY_PUFFER_FISH_BLOW_OUT, this.cD(), this.cE());
                    this.setPuffState(0);
                }

                ++this.c;
            }
        }

        super.tick();
    }

    public void movementTick() {
        super.movementTick();
        if (this.getPuffState() > 0) {
            List<EntityInsentient> list = this.world.a(EntityInsentient.class, this.getBoundingBox().g(0.3D), EntityPufferFish.bC);
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityInsentient entityinsentient = (EntityInsentient) iterator.next();

                if (entityinsentient.isAlive()) {
                    this.a(entityinsentient);
                }
            }
        }

    }

    private void a(EntityInsentient entityinsentient) {
        int i = this.getPuffState();

        if (entityinsentient.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            entityinsentient.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0));
            this.a(SoundEffects.ENTITY_PUFFER_FISH_STING, 1.0F, 1.0F);
        }

    }

    public void d(EntityHuman entityhuman) {
        int i = this.getPuffState();

        if (entityhuman instanceof EntityPlayer && i > 0 && entityhuman.damageEntity(DamageSource.mobAttack(this), (float) (1 + i))) {
            ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutGameStateChange(9, 0.0F));
            entityhuman.addEffect(new MobEffect(MobEffects.POISON, 60 * i, 0));
        }

    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_PUFFER_FISH_AMBIENT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_PUFFER_FISH_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_PUFFER_FISH_HURT;
    }

    protected SoundEffect dz() {
        return SoundEffects.ENTITY_PUFFER_FISH_FLOP;
    }

    static class a extends PathfinderGoal {

        private final EntityPufferFish a;

        public a(EntityPufferFish entitypufferfish) {
            this.a = entitypufferfish;
        }

        public boolean a() {
            List<EntityLiving> list = this.a.world.a(EntityLiving.class, this.a.getBoundingBox().g(2.0D), EntityPufferFish.bC);

            return !list.isEmpty();
        }

        public void c() {
            this.a.b = 1;
            this.a.c = 0;
        }

        public void d() {
            this.a.b = 0;
        }

        public boolean b() {
            List<EntityLiving> list = this.a.world.a(EntityLiving.class, this.a.getBoundingBox().g(2.0D), EntityPufferFish.bC);

            return !list.isEmpty();
        }
    }
}
