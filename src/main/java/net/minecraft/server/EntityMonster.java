package net.minecraft.server;

import java.util.Random;
import java.util.function.Predicate;

public abstract class EntityMonster extends EntityCreature implements IMonster {

    public org.bukkit.craftbukkit.entity.CraftMonster getBukkitMonster() { return (org.bukkit.craftbukkit.entity.CraftMonster) super.getBukkitEntity(); } // Paper
    protected EntityMonster(EntityTypes<? extends EntityMonster> entitytypes, World world) {
        super(entitytypes, world);
        this.f = 5;
    }

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.HOSTILE;
    }

    @Override
    public void movementTick() {
        this.cO();
        this.eb();
        super.movementTick();
    }

    protected void eb() {
        float f = this.aF();

        if (f > 0.5F) {
            this.ticksFarFromPlayer += 2;
        }

    }

    @Override
    public void tick() {
        super.tick();
        if (!this.world.isClientSide && this.world.getDifficulty() == EnumDifficulty.PEACEFUL) {
            this.die();
        }

    }

    @Override
    protected SoundEffect getSoundSwim() {
        return SoundEffects.ENTITY_HOSTILE_SWIM;
    }

    @Override
    protected SoundEffect getSoundSplash() {
        return SoundEffects.ENTITY_HOSTILE_SPLASH;
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        return this.isInvulnerable(damagesource) ? false : super.damageEntity(damagesource, f);
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_HOSTILE_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_HOSTILE_DEATH;
    }

    @Override
    protected SoundEffect getSoundFall(int i) {
        return i > 4 ? SoundEffects.ENTITY_HOSTILE_BIG_FALL : SoundEffects.ENTITY_HOSTILE_SMALL_FALL;
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.5F - iworldreader.v(blockposition);
    }

    public static boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, Random random) {
        if (generatoraccess.getBrightness(EnumSkyBlock.SKY, blockposition) > random.nextInt(32)) {
            return false;
        } else {
            int i = generatoraccess.getMinecraftWorld().U() ? generatoraccess.d(blockposition, 10) : generatoraccess.getLightLevel(blockposition);

            return i <= random.nextInt(8);
        }
    }

    public static boolean c(EntityTypes<? extends EntityMonster> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && a(generatoraccess, blockposition, random) && a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
    }

    public static boolean d(EntityTypes<? extends EntityMonster> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getDifficulty() != EnumDifficulty.PEACEFUL && a(entitytypes, generatoraccess, enummobspawn, blockposition, random);
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(GenericAttributes.ATTACK_DAMAGE);
    }

    @Override
    protected boolean isDropExperience() {
        return true;
    }

    public boolean e(EntityHuman entityhuman) {
        return true;
    }

    @Override
    public ItemStack f(ItemStack itemstack) {
        if (itemstack.getItem() instanceof ItemProjectileWeapon) {
            Predicate<ItemStack> predicate = ((ItemProjectileWeapon) itemstack.getItem()).d();
            ItemStack itemstack1 = ItemProjectileWeapon.a((EntityLiving) this, predicate);

            return itemstack1.isEmpty() ? new ItemStack(Items.ARROW) : itemstack1;
        } else {
            return ItemStack.a;
        }
    }
}
