package net.minecraft.server;

import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityAnimal extends EntityAgeable {

    public int loveTicks;
    public UUID breedCause;
    public ItemStack breedItem; // CraftBukkit - Add breedItem variable

    protected EntityAnimal(EntityTypes<? extends EntityAnimal> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void mobTick() {
        if (this.getAge() != 0) {
            this.loveTicks = 0;
        }

        super.mobTick();
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.getAge() != 0) {
            this.loveTicks = 0;
        }

        if (this.loveTicks > 0) {
            --this.loveTicks;
            if (this.loveTicks % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(Particles.HEART, this.locX + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.locY + 0.5D + (double) (this.random.nextFloat() * this.getHeight()), this.locZ + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d0, d1, d2);
            }
        }

    }

    /* CraftBukkit start
    // Function disabled as it has no special function anymore after
    // setSitting is disabled.
    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.loveTicks = 0;
            return super.damageEntity(damagesource, f);
        }
    }
    // CraftBukkit end */

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition.down()).getBlock() == Blocks.GRASS_BLOCK ? 10.0F : iworldreader.v(blockposition) - 0.5F;
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("InLove", this.loveTicks);
        if (this.breedCause != null) {
            nbttagcompound.a("LoveCause", this.breedCause);
        }

    }

    @Override
    public double aO() {
        return 0.14D;
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.loveTicks = nbttagcompound.getInt("InLove");
        this.breedCause = nbttagcompound.b("LoveCause") ? nbttagcompound.a("LoveCause") : null;
    }

    public static boolean b(EntityTypes<? extends EntityAnimal> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getType(blockposition.down()).getBlock() == Blocks.GRASS_BLOCK && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    @Override
    public int A() {
        return 120;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }

    @Override
    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    public boolean i(ItemStack itemstack) {
        return itemstack.getItem() == Items.WHEAT;
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (this.i(itemstack)) {
            if (this.getAge() == 0 && this.ea()) {
                this.a(entityhuman, itemstack);
                this.f(entityhuman);
                return true;
            }

            if (this.isBaby()) {
                this.a(entityhuman, itemstack);
                this.setAge((int) ((float) (-this.getAge() / 20) * 0.1F), true);
                return true;
            }
        }

        return super.a(entityhuman, enumhand);
    }

    protected void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (!entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
        }

    }

    public boolean ea() {
        return this.loveTicks <= 0;
    }

    public void f(@Nullable EntityHuman entityhuman) {
        this.loveTicks = 600;
        if (entityhuman != null) {
            this.breedCause = entityhuman.getUniqueID();
        }
        this.breedItem = entityhuman.inventory.getItemInHand(); // CraftBukkit

        this.world.broadcastEntityEffect(this, (byte) 18);
    }

    public void setLoveTicks(int i) {
        this.loveTicks = i;
    }

    @Nullable
    public EntityPlayer getBreedCause() {
        if (this.breedCause == null) {
            return null;
        } else {
            EntityHuman entityhuman = this.world.b(this.breedCause);

            return entityhuman instanceof EntityPlayer ? (EntityPlayer) entityhuman : null;
        }
    }

    public boolean isInLove() {
        return this.loveTicks > 0;
    }

    public void resetLove() {
        this.loveTicks = 0;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (entityanimal.getClass() != this.getClass() ? false : this.isInLove() && entityanimal.isInLove());
    }
}
