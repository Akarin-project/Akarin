package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityAgeable extends EntityCreature {

    private static final DataWatcherObject<Boolean> bC = DataWatcher.a(EntityAgeable.class, DataWatcherRegistry.i);
    protected int a;
    protected int b;
    protected int c;
    private float bD = -1.0F;
    private float bE;

    protected EntityAgeable(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Nullable
    public abstract EntityAgeable createChild(EntityAgeable entityageable);

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (item instanceof ItemMonsterEgg && ((ItemMonsterEgg) item).a(itemstack.getTag(), this.P())) {
            if (!this.world.isClientSide) {
                EntityAgeable entityageable = this.createChild(this);

                if (entityageable != null) {
                    entityageable.setAgeRaw(-24000);
                    entityageable.setPositionRotation(this.locX, this.locY, this.locZ, 0.0F, 0.0F);
                    this.world.addEntity(entityageable);
                    if (itemstack.hasName()) {
                        entityageable.setCustomName(itemstack.getName());
                    }

                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityAgeable.bC, false);
    }

    public int getAge() {
        return this.world.isClientSide ? ((Boolean) this.datawatcher.get(EntityAgeable.bC) ? -1 : 1) : this.a;
    }

    public void setAge(int i, boolean flag) {
        int j = this.getAge();
        int k = j;

        j += i * 20;
        if (j > 0) {
            j = 0;
            if (k < 0) {
                this.l();
            }
        }

        int l = j - k;

        this.setAgeRaw(j);
        if (flag) {
            this.b += l;
            if (this.c == 0) {
                this.c = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAgeRaw(this.b);
        }

    }

    public void setAge(int i) {
        this.setAge(i, false);
    }

    public void setAgeRaw(int i) {
        this.datawatcher.set(EntityAgeable.bC, i < 0);
        this.a = i;
        this.a(this.isBaby());
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Age", this.getAge());
        nbttagcompound.setInt("ForcedAge", this.b);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAgeRaw(nbttagcompound.getInt("Age"));
        this.b = nbttagcompound.getInt("ForcedAge");
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAgeable.bC.equals(datawatcherobject)) {
            this.a(this.isBaby());
        }

        super.a(datawatcherobject);
    }

    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide) {
            if (this.c > 0) {
                if (this.c % 4 == 0) {
                    this.world.addParticle(Particles.z, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D);
                }

                --this.c;
            }
        } else {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAgeRaw(i);
                if (i == 0) {
                    this.l();
                }
            } else if (i > 0) {
                --i;
                this.setAgeRaw(i);
            }
        }

    }

    protected void l() {}

    public boolean isBaby() {
        return this.getAge() < 0;
    }

    public void a(boolean flag) {
        this.a(flag ? 0.5F : 1.0F);
    }

    public final void setSize(float f, float f1) {
        this.bD = f;
        this.bE = f1;
        this.a(1.0F);
    }

    protected final void a(float f) {
        super.setSize(this.bD * f, this.bE * f);
    }
}
