package net.minecraft.server;

import java.util.EnumSet;
import javax.annotation.Nullable;

public class EntityLlamaTrader extends EntityLlama {

    private int bI = 47999;

    public EntityLlamaTrader(EntityTypes<? extends EntityLlamaTrader> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected EntityLlama eF() {
        return (EntityLlama) EntityTypes.TRADER_LLAMA.a(this.world);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("DespawnDelay", this.bI);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("DespawnDelay", 99)) {
            this.bI = nbttagcompound.getInt("DespawnDelay");
        }

    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.targetSelector.a(1, new EntityLlamaTrader.a(this));
    }

    @Override
    protected void g(EntityHuman entityhuman) {
        Entity entity = this.getLeashHolder();

        if (!(entity instanceof EntityVillagerTrader)) {
            super.g(entityhuman);
        }
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide) {
            this.eK();
        }

    }

    private void eK() {
        if (this.eL()) {
            this.bI = this.eM() ? ((EntityVillagerTrader) this.getLeashHolder()).ef() - 1 : this.bI - 1;
            if (this.bI <= 0) {
                this.unleash(true, false);
                this.die();
            }

        }
    }

    private boolean eL() {
        return !this.isTamed() && !this.eN() && !this.hasSinglePlayerPassenger();
    }

    private boolean eM() {
        return this.getLeashHolder() instanceof EntityVillagerTrader;
    }

    private boolean eN() {
        return this.isLeashed() && !this.eM();
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        GroupDataEntity groupdataentity1 = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);

        if (enummobspawn == EnumMobSpawn.EVENT) {
            this.setAgeRaw(0);
        }

        return groupdataentity1;
    }

    public class a extends PathfinderGoalTarget {

        private final EntityLlama b;
        private EntityLiving c;
        private int d;

        public a(EntityLlama entityllama) {
            super(entityllama, false);
            this.b = entityllama;
            this.a(EnumSet.of(PathfinderGoal.Type.TARGET));
        }

        @Override
        public boolean a() {
            if (!this.b.isLeashed()) {
                return false;
            } else {
                Entity entity = this.b.getLeashHolder();

                if (!(entity instanceof EntityVillagerTrader)) {
                    return false;
                } else {
                    EntityVillagerTrader entityvillagertrader = (EntityVillagerTrader) entity;

                    this.c = entityvillagertrader.getLastDamager();
                    int i = entityvillagertrader.ct();

                    return i != this.d && this.a(this.c, PathfinderTargetCondition.a);
                }
            }
        }

        @Override
        public void c() {
            this.e.setGoalTarget(this.c, org.bukkit.event.entity.EntityTargetEvent.TargetReason.TARGET_ATTACKED_OWNER, true); // CraftBukkit
            Entity entity = this.b.getLeashHolder();

            if (entity instanceof EntityVillagerTrader) {
                this.d = ((EntityVillagerTrader) entity).ct();
            }

            super.c();
        }
    }
}
