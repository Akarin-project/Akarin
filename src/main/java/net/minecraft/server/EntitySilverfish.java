package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class EntitySilverfish extends EntityMonster {

    private EntitySilverfish.PathfinderGoalSilverfishWakeOthers a;

    public EntitySilverfish(World world) {
        super(EntityTypes.SILVERFISH, world);
        this.setSize(0.4F, 0.3F);
    }

    protected void n() {
        this.a = new EntitySilverfish.PathfinderGoalSilverfishWakeOthers(this);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(3, this.a);
        this.goalSelector.a(4, new PathfinderGoalMeleeAttack(this, 1.0D, false));
        this.goalSelector.a(5, new EntitySilverfish.PathfinderGoalSilverfishHideInBlock(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, true, new Class[0]));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
    }

    public double aI() {
        return 0.1D;
    }

    public float getHeadHeight() {
        return 0.1F;
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.25D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(1.0D);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_SILVERFISH_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_SILVERFISH_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SILVERFISH_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_SILVERFISH_STEP, 0.15F, 1.0F);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if ((damagesource instanceof EntityDamageSource || damagesource == DamageSource.MAGIC) && this.a != null) {
                this.a.g();
            }

            return super.damageEntity(damagesource, f);
        }
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.B;
    }

    public void tick() {
        this.aQ = this.yaw;
        super.tick();
    }

    public void k(float f) {
        this.yaw = f;
        super.k(f);
    }

    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return BlockMonsterEggs.k(iworldreader.getType(blockposition.down())) ? 10.0F : super.a(blockposition, iworldreader);
    }

    protected boolean K_() {
        return true;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        if (super.a(generatoraccess, flag)) {
            EntityHuman entityhuman = generatoraccess.b(this, 5.0D);

            return entityhuman == null;
        } else {
            return false;
        }
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.ARTHROPOD;
    }

    static class PathfinderGoalSilverfishHideInBlock extends PathfinderGoalRandomStroll {

        private EnumDirection h;
        private boolean i;

        public PathfinderGoalSilverfishHideInBlock(EntitySilverfish entitysilverfish) {
            super(entitysilverfish, 1.0D, 10);
            this.a(1);
        }

        public boolean a() {
            if (this.a.getGoalTarget() != null) {
                return false;
            } else if (!this.a.getNavigation().p()) {
                return false;
            } else {
                Random random = this.a.getRandom();

                if (this.a.world.getGameRules().getBoolean("mobGriefing") && random.nextInt(10) == 0) {
                    this.h = EnumDirection.a(random);
                    BlockPosition blockposition = (new BlockPosition(this.a.locX, this.a.locY + 0.5D, this.a.locZ)).shift(this.h);
                    IBlockData iblockdata = this.a.world.getType(blockposition);

                    if (BlockMonsterEggs.k(iblockdata)) {
                        this.i = true;
                        return true;
                    }
                }

                this.i = false;
                return super.a();
            }
        }

        public boolean b() {
            return this.i ? false : super.b();
        }

        public void c() {
            if (!this.i) {
                super.c();
            } else {
                World world = this.a.world;
                BlockPosition blockposition = (new BlockPosition(this.a.locX, this.a.locY + 0.5D, this.a.locZ)).shift(this.h);
                IBlockData iblockdata = world.getType(blockposition);

                if (BlockMonsterEggs.k(iblockdata)) {
                    world.setTypeAndData(blockposition, BlockMonsterEggs.f(iblockdata.getBlock()), 3);
                    this.a.doSpawnEffect();
                    this.a.die();
                }

            }
        }
    }

    static class PathfinderGoalSilverfishWakeOthers extends PathfinderGoal {

        private final EntitySilverfish silverfish;
        private int b;

        public PathfinderGoalSilverfishWakeOthers(EntitySilverfish entitysilverfish) {
            this.silverfish = entitysilverfish;
        }

        public void g() {
            if (this.b == 0) {
                this.b = 20;
            }

        }

        public boolean a() {
            return this.b > 0;
        }

        public void e() {
            --this.b;
            if (this.b <= 0) {
                World world = this.silverfish.world;
                Random random = this.silverfish.getRandom();
                BlockPosition blockposition = new BlockPosition(this.silverfish);

                for (int i = 0; i <= 5 && i >= -5; i = (i <= 0 ? 1 : 0) - i) {
                    for (int j = 0; j <= 10 && j >= -10; j = (j <= 0 ? 1 : 0) - j) {
                        for (int k = 0; k <= 10 && k >= -10; k = (k <= 0 ? 1 : 0) - k) {
                            BlockPosition blockposition1 = blockposition.a(j, i, k);
                            IBlockData iblockdata = world.getType(blockposition1);
                            Block block = iblockdata.getBlock();

                            if (block instanceof BlockMonsterEggs) {
                                if (world.getGameRules().getBoolean("mobGriefing")) {
                                    world.setAir(blockposition1, true);
                                } else {
                                    world.setTypeAndData(blockposition1, ((BlockMonsterEggs) block).d().getBlockData(), 3);
                                }

                                if (random.nextBoolean()) {
                                    return;
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}
