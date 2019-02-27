package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityParrot extends EntityPerchable implements EntityBird {

    private static final DataWatcherObject<Integer> bL = DataWatcher.a(EntityParrot.class, DataWatcherRegistry.b);
    private static final Predicate<EntityInsentient> bM = new Predicate<EntityInsentient>() {
        public boolean test(@Nullable EntityInsentient entityinsentient) {
            return entityinsentient != null && EntityParrot.bP.containsKey(entityinsentient.P());
        }
    };
    private static final Item bN = Items.COOKIE;
    private static final Set<Item> bO = Sets.newHashSet(new Item[] { Items.WHEAT_SEEDS, Items.MELON_SEEDS, Items.PUMPKIN_SEEDS, Items.BEETROOT_SEEDS});
    private static final Map<EntityTypes<?>, SoundEffect> bP = (Map) SystemUtils.a((Object) Maps.newHashMap(), (hashmap) -> {
        hashmap.put(EntityTypes.BLAZE, SoundEffects.ENTITY_PARROT_IMITATE_BLAZE);
        hashmap.put(EntityTypes.CAVE_SPIDER, SoundEffects.ENTITY_PARROT_IMITATE_SPIDER);
        hashmap.put(EntityTypes.CREEPER, SoundEffects.ENTITY_PARROT_IMITATE_CREEPER);
        hashmap.put(EntityTypes.DROWNED, SoundEffects.ENTITY_PARROT_IMITATE_DROWNED);
        hashmap.put(EntityTypes.ELDER_GUARDIAN, SoundEffects.ENTITY_PARROT_IMITATE_ELDER_GUARDIAN);
        hashmap.put(EntityTypes.ENDER_DRAGON, SoundEffects.ENTITY_PARROT_IMITATE_ENDER_DRAGON);
        hashmap.put(EntityTypes.ENDERMAN, SoundEffects.ENTITY_PARROT_IMITATE_ENDERMAN);
        hashmap.put(EntityTypes.ENDERMITE, SoundEffects.ENTITY_PARROT_IMITATE_ENDERMITE);
        hashmap.put(EntityTypes.EVOKER, SoundEffects.ENTITY_PARROT_IMITATE_EVOKER);
        hashmap.put(EntityTypes.GHAST, SoundEffects.ENTITY_PARROT_IMITATE_GHAST);
        hashmap.put(EntityTypes.HUSK, SoundEffects.ENTITY_PARROT_IMITATE_HUSK);
        hashmap.put(EntityTypes.ILLUSIONER, SoundEffects.ENTITY_PARROT_IMITATE_ILLUSIONER);
        hashmap.put(EntityTypes.MAGMA_CUBE, SoundEffects.ENTITY_PARROT_IMITATE_MAGMA_CUBE);
        hashmap.put(EntityTypes.ZOMBIE_PIGMAN, SoundEffects.ENTITY_PARROT_IMITATE_ZOMBIE_PIGMAN);
        hashmap.put(EntityTypes.PHANTOM, SoundEffects.ENTITY_PARROT_IMITATE_PHANTOM);
        hashmap.put(EntityTypes.POLAR_BEAR, SoundEffects.ENTITY_PARROT_IMITATE_POLAR_BEAR);
        hashmap.put(EntityTypes.SHULKER, SoundEffects.ENTITY_PARROT_IMITATE_SHULKER);
        hashmap.put(EntityTypes.SILVERFISH, SoundEffects.ENTITY_PARROT_IMITATE_SILVERFISH);
        hashmap.put(EntityTypes.SKELETON, SoundEffects.ENTITY_PARROT_IMITATE_SKELETON);
        hashmap.put(EntityTypes.SLIME, SoundEffects.ENTITY_PARROT_IMITATE_SLIME);
        hashmap.put(EntityTypes.SPIDER, SoundEffects.ENTITY_PARROT_IMITATE_SPIDER);
        hashmap.put(EntityTypes.STRAY, SoundEffects.ENTITY_PARROT_IMITATE_STRAY);
        hashmap.put(EntityTypes.VEX, SoundEffects.ENTITY_PARROT_IMITATE_VEX);
        hashmap.put(EntityTypes.VINDICATOR, SoundEffects.ENTITY_PARROT_IMITATE_VINDICATOR);
        hashmap.put(EntityTypes.WITCH, SoundEffects.ENTITY_PARROT_IMITATE_WITCH);
        hashmap.put(EntityTypes.WITHER, SoundEffects.ENTITY_PARROT_IMITATE_WITHER);
        hashmap.put(EntityTypes.WITHER_SKELETON, SoundEffects.ENTITY_PARROT_IMITATE_WITHER_SKELETON);
        hashmap.put(EntityTypes.WOLF, SoundEffects.ENTITY_PARROT_IMITATE_WOLF);
        hashmap.put(EntityTypes.ZOMBIE, SoundEffects.ENTITY_PARROT_IMITATE_ZOMBIE);
        hashmap.put(EntityTypes.ZOMBIE_VILLAGER, SoundEffects.ENTITY_PARROT_IMITATE_ZOMBIE_VILLAGER);
    });
    public float bG;
    public float bH;
    public float bI;
    public float bJ;
    public float bK = 1.0F;
    private boolean bQ;
    private BlockPosition bR;

    public EntityParrot(World world) {
        super(EntityTypes.PARROT, world);
        this.setSize(0.5F, 0.9F);
        this.moveController = new ControllerMoveFlying(this);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setVariant(this.random.nextInt(5));
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    protected void n() {
        this.goalSit = new PathfinderGoalSit(this);
        this.goalSelector.a(0, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(2, this.goalSit);
        this.goalSelector.a(2, new PathfinderGoalFollowOwnerParrot(this, 1.0D, 5.0F, 1.0F));
        this.goalSelector.a(2, new PathfinderGoalRandomFly(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalPerch(this));
        this.goalSelector.a(3, new PathfinderGoalFollowEntity(this, 1.0D, 3.0F, 7.0F));
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.e);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(6.0D);
        this.getAttributeInstance(GenericAttributes.e).setValue(0.4000000059604645D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    protected NavigationAbstract b(World world) {
        NavigationFlying navigationflying = new NavigationFlying(this, world);

        navigationflying.a(false);
        navigationflying.d(true);
        navigationflying.b(true);
        return navigationflying;
    }

    public float getHeadHeight() {
        return this.length * 0.6F;
    }

    public void movementTick() {
        b(this.world, (Entity) this);
        if (this.bR == null || this.bR.distanceSquared(this.locX, this.locY, this.locZ) > 12.0D || this.world.getType(this.bR).getBlock() != Blocks.JUKEBOX) {
            this.bQ = false;
            this.bR = null;
        }

        super.movementTick();
        this.dL();
    }

    private void dL() {
        this.bJ = this.bG;
        this.bI = this.bH;
        this.bH = (float) ((double) this.bH + (double) (this.onGround ? -1 : 4) * 0.3D);
        this.bH = MathHelper.a(this.bH, 0.0F, 1.0F);
        if (!this.onGround && this.bK < 1.0F) {
            this.bK = 1.0F;
        }

        this.bK = (float) ((double) this.bK * 0.9D);
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        this.bG += this.bK * 2.0F;
    }

    private static boolean b(World world, Entity entity) {
        if (!entity.isSilent() && world.random.nextInt(50) == 0) {
            List<EntityInsentient> list = world.a(EntityInsentient.class, entity.getBoundingBox().g(20.0D), EntityParrot.bM);

            if (!list.isEmpty()) {
                EntityInsentient entityinsentient = (EntityInsentient) list.get(world.random.nextInt(list.size()));

                if (!entityinsentient.isSilent()) {
                    SoundEffect soundeffect = a(entityinsentient.P());

                    world.a((EntityHuman) null, entity.locX, entity.locY, entity.locZ, soundeffect, entity.bV(), 0.7F, b(world.random));
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.isTamed() && EntityParrot.bO.contains(itemstack.getItem())) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.isSilent()) {
                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_PARROT_EAT, this.bV(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            }

            if (!this.world.isClientSide) {
                if (this.random.nextInt(10) == 0) {
                    this.c(entityhuman);
                    this.s(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.s(false);
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return true;
        } else if (itemstack.getItem() == EntityParrot.bN) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            this.addEffect(new MobEffect(MobEffects.POISON, 900));
            if (entityhuman.u() || !this.bl()) {
                this.damageEntity(DamageSource.playerAttack(entityhuman), Float.MAX_VALUE);
            }

            return true;
        } else {
            if (!this.world.isClientSide && !this.F_() && this.isTamed() && this.f((EntityLiving) entityhuman)) {
                this.goalSit.setSitting(!this.isSitting());
            }

            return super.a(entityhuman, enumhand);
        }
    }

    public boolean f(ItemStack itemstack) {
        return false;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        Block block = generatoraccess.getType(blockposition.down()).getBlock();

        return block instanceof BlockLeaves || block == Blocks.GRASS || block instanceof BlockLogAbstract || block == Blocks.AIR && super.a(generatoraccess, flag);
    }

    public void c(float f, float f1) {}

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    public static void a(World world, Entity entity) {
        if (!entity.isSilent() && !b(world, entity) && world.random.nextInt(200) == 0) {
            world.a((EntityHuman) null, entity.locX, entity.locY, entity.locZ, a(world.random), entity.bV(), 1.0F, b(world.random));
        }

    }

    public boolean B(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
    }

    @Nullable
    public SoundEffect D() {
        return a(this.random);
    }

    private static SoundEffect a(Random random) {
        if (random.nextInt(1000) == 0) {
            List<EntityTypes<?>> list = Lists.newArrayList(EntityParrot.bP.keySet());

            return a((EntityTypes) list.get(random.nextInt(list.size())));
        } else {
            return SoundEffects.ENTITY_PARROT_AMBIENT;
        }
    }

    public static SoundEffect a(EntityTypes<?> entitytypes) {
        return (SoundEffect) EntityParrot.bP.getOrDefault(entitytypes, SoundEffects.ENTITY_PARROT_AMBIENT);
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_PARROT_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_PARROT_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_PARROT_STEP, 0.15F, 1.0F);
    }

    protected float e(float f) {
        this.a(SoundEffects.ENTITY_PARROT_FLY, 0.15F, 1.0F);
        return f + this.bH / 2.0F;
    }

    protected boolean ah() {
        return true;
    }

    protected float cE() {
        return b(this.random);
    }

    private static float b(Random random) {
        return (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundCategory bV() {
        return SoundCategory.NEUTRAL;
    }

    public boolean isCollidable() {
        return true;
    }

    protected void C(Entity entity) {
        if (!(entity instanceof EntityHuman)) {
            super.C(entity);
        }
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (this.goalSit != null) {
                this.goalSit.setSitting(false);
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public int getVariant() {
        return MathHelper.clamp((Integer) this.datawatcher.get(EntityParrot.bL), 0, 4);
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityParrot.bL, i);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityParrot.bL, 0);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aE;
    }

    public boolean F_() {
        return !this.onGround;
    }
}
