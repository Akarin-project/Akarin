package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class EntityAgeable extends EntityCreature {

    private static final DataWatcherObject<Boolean> bz = DataWatcher.a(EntityAgeable.class, DataWatcherRegistry.i);
    protected int b;
    protected int c;
    protected int d;
    public boolean ageLocked; // CraftBukkit

    protected EntityAgeable(EntityTypes<? extends EntityAgeable> entitytypes, World world) {
        super(entitytypes, world);
    }

    // Spigot start
    @Override
    public void inactiveTick()
    {
        super.inactiveTick();
        if ( this.world.isClientSide || this.ageLocked )
        { // CraftBukkit
            this.updateSize();
        } else
        {
            int i = this.getAge();

            if ( i < 0 )
            {
                ++i;
                this.setAgeRaw( i );
            } else if ( i > 0 )
            {
                --i;
                this.setAgeRaw( i );
            }
        }
    }
    // Spigot end

    @Nullable
    public abstract EntityAgeable createChild(EntityAgeable entityageable);

    protected void a(EntityHuman entityhuman, EntityAgeable entityageable) {}

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (item instanceof ItemMonsterEgg && ((ItemMonsterEgg) item).a(itemstack.getTag(), this.getEntityType())) {
            if (!this.world.isClientSide) {
                EntityAgeable entityageable = this.createChild(this);

                if (entityageable != null) {
                    entityageable.setAgeRaw(-24000);
                    entityageable.setPositionRotation(this.locX, this.locY, this.locZ, 0.0F, 0.0F);
                    this.world.addEntity(entityageable, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG); // CraftBukkit
                    if (itemstack.hasName()) {
                        entityageable.setCustomName(itemstack.getName());
                    }

                    this.a(entityhuman, entityageable);
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

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityAgeable.bz, false);
    }

    public int getAge() {
        return this.world.isClientSide ? ((Boolean) this.datawatcher.get(EntityAgeable.bz) ? -1 : 1) : this.b;
    }

    public void setAge(int i, boolean flag) {
        if (ageLocked) return; // Paper - GH-1459
        int j = this.getAge();
        int k = j;

        j += i * 20;
        if (j > 0) {
            j = 0;
        }

        int l = j - k;

        this.setAgeRaw(j);
        if (flag) {
            this.c += l;
            if (this.d == 0) {
                this.d = 40;
            }
        }

        if (this.getAge() == 0) {
            this.setAgeRaw(this.c);
        }

    }

    public void setAge(int i) {
        this.setAge(i, false);
    }

    public void setAgeRaw(int i) {
        int j = this.b;

        this.b = i;
        if (j < 0 && i >= 0 || j >= 0 && i < 0) {
            this.datawatcher.set(EntityAgeable.bz, i < 0);
            this.l();
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Age", this.getAge());
        nbttagcompound.setInt("ForcedAge", this.c);
        nbttagcompound.setBoolean("AgeLocked", this.ageLocked); // CraftBukkit
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setAgeRaw(nbttagcompound.getInt("Age"));
        this.c = nbttagcompound.getInt("ForcedAge");
        this.ageLocked = nbttagcompound.getBoolean("AgeLocked"); // CraftBukkit
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAgeable.bz.equals(datawatcherobject)) {
            this.updateSize();
        }

        super.a(datawatcherobject);
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (this.world.isClientSide || ageLocked) { // CraftBukkit
            if (this.d > 0) {
                if (this.d % 4 == 0) {
                    this.world.addParticle(Particles.HAPPY_VILLAGER, this.locX + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.locY + 0.5D + (double) (this.random.nextFloat() * this.getHeight()), this.locZ + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), 0.0D, 0.0D, 0.0D);
                }

                --this.d;
            }
        } else if (this.isAlive()) {
            int i = this.getAge();

            if (i < 0) {
                ++i;
                this.setAgeRaw(i);
            } else if (i > 0) {
                --i;
                this.setAgeRaw(i);
            }
        }

    }

    protected void l() {}

    @Override
    public boolean isBaby() {
        return this.getAge() < 0;
    }
}
