package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public abstract class EntityAnimal extends EntityAgeable implements IAnimal {

    protected Block bF;
    public int bC; // CraftBukkit - private -> public
    public UUID breedCause;
    public ItemStack breedItem; // CraftBukkit - Add breedItem variable

    protected EntityAnimal(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.bF = Blocks.GRASS_BLOCK;
    }

    protected void mobTick() {
        if (this.getAge() != 0) {
            this.bC = 0;
        }

        super.mobTick();
    }

    public void movementTick() {
        super.movementTick();
        if (this.getAge() != 0) {
            this.bC = 0;
        }

        if (this.bC > 0) {
            --this.bC;
            if (this.bC % 10 == 0) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(Particles.A, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }
        }

    }

    /* CraftBukkit start
    // Function disabled as it has no special function anymore after
    // setSitting is disabled.
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.bC = 0;
            return super.damageEntity(damagesource, f);
        }
    }
    // CraftBukkit end */

    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition.down()).getBlock() == this.bF ? 10.0F : iworldreader.A(blockposition) - 0.5F;
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("InLove", this.bC);
        if (this.breedCause != null) {
            nbttagcompound.a("LoveCause", this.breedCause);
        }

    }

    public double aI() {
        return 0.14D;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.bC = nbttagcompound.getInt("InLove");
        this.breedCause = nbttagcompound.b("LoveCause") ? nbttagcompound.a("LoveCause") : null;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);

        return generatoraccess.getType(blockposition.down()).getBlock() == this.bF && generatoraccess.getLightLevel(blockposition, 0) > 8 && super.a(generatoraccess, flag);
    }

    public int z() {
        return 120;
    }

    public boolean isTypeNotPersistent() {
        return false;
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 1 + this.world.random.nextInt(3);
    }

    public boolean f(ItemStack itemstack) {
        return itemstack.getItem() == Items.WHEAT;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (this.f(itemstack)) {
            if (this.getAge() == 0 && this.dD()) {
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

    public boolean dD() {
        return this.bC <= 0;
    }

    public void f(@Nullable EntityHuman entityhuman) {
        this.bC = 600;
        if (entityhuman != null) {
            this.breedCause = entityhuman.getUniqueID();
        }
        this.breedItem = entityhuman.inventory.getItemInHand(); // CraftBukkit

        this.world.broadcastEntityEffect(this, (byte) 18);
    }

    public void d(int i) {
        this.bC = i;
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
        return this.bC > 0;
    }

    public void resetLove() {
        this.bC = 0;
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (entityanimal.getClass() != this.getClass() ? false : this.isInLove() && entityanimal.isInLove());
    }
}
