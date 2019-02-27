package net.minecraft.server;

public abstract class EntityHorseChestedAbstract extends EntityHorseAbstract {

    private static final DataWatcherObject<Boolean> bM = DataWatcher.a(EntityHorseChestedAbstract.class, DataWatcherRegistry.i);

    protected EntityHorseChestedAbstract(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.bK = false;
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityHorseChestedAbstract.bM, false);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) this.ec());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.17499999701976776D);
        this.getAttributeInstance(EntityHorseChestedAbstract.attributeJumpStrength).setValue(0.5D);
    }

    public boolean isCarryingChest() {
        return (Boolean) this.datawatcher.get(EntityHorseChestedAbstract.bM);
    }

    public void setCarryingChest(boolean flag) {
        this.datawatcher.set(EntityHorseChestedAbstract.bM, flag);
    }

    protected int dA() {
        return this.isCarryingChest() ? 17 : super.dA();
    }

    public double aJ() {
        return super.aJ() - 0.25D;
    }

    protected SoundEffect dB() {
        super.dB();
        return SoundEffects.ENTITY_DONKEY_ANGRY;
    }

    public void die(DamageSource damagesource) {
        // super.die(damagesource); // CraftBukkit - moved down
        if (this.isCarryingChest()) {
            if (!this.world.isClientSide) {
                this.a((IMaterial) Blocks.CHEST);
            }

            // this.setCarryingChest(false); // CraftBukkit - moved down
        }
        // CraftBukkit start
        super.die(damagesource);
        this.setCarryingChest(false);
        // CraftBukkit end

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("ChestedHorse", this.isCarryingChest());
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = new NBTTagList();

            for (int i = 2; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (!itemstack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                    nbttagcompound1.setByte("Slot", (byte) i);
                    itemstack.save(nbttagcompound1);
                    nbttaglist.add((NBTBase) nbttagcompound1);
                }
            }

            nbttagcompound.set("Items", nbttaglist);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setCarryingChest(nbttagcompound.getBoolean("ChestedHorse"));
        if (this.isCarryingChest()) {
            NBTTagList nbttaglist = nbttagcompound.getList("Items", 10);

            this.loadChest();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                int j = nbttagcompound1.getByte("Slot") & 255;

                if (j >= 2 && j < this.inventoryChest.getSize()) {
                    this.inventoryChest.setItem(j, ItemStack.a(nbttagcompound1));
                }
            }
        }

        this.dS();
    }

    public boolean c(int i, ItemStack itemstack) {
        if (i == 499) {
            if (this.isCarryingChest() && itemstack.isEmpty()) {
                this.setCarryingChest(false);
                this.loadChest();
                return true;
            }

            if (!this.isCarryingChest() && itemstack.getItem() == Blocks.CHEST.getItem()) {
                this.setCarryingChest(true);
                this.loadChest();
                return true;
            }
        }

        return super.c(i, itemstack);
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!this.isBaby()) {
                if (this.isTamed() && entityhuman.isSneaking()) {
                    this.c(entityhuman);
                    return true;
                }

                if (this.isVehicle()) {
                    return super.a(entityhuman, enumhand);
                }
            }

            if (!itemstack.isEmpty()) {
                boolean flag = this.b(entityhuman, itemstack);

                if (!flag) {
                    if (!this.isTamed() || itemstack.getItem() == Items.NAME_TAG) {
                        if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                            return true;
                        }

                        this.dZ();
                        return true;
                    }

                    if (!this.isCarryingChest() && itemstack.getItem() == Blocks.CHEST.getItem()) {
                        this.setCarryingChest(true);
                        this.dC();
                        flag = true;
                        this.loadChest();
                    }

                    if (!this.isBaby() && !this.dV() && itemstack.getItem() == Items.SADDLE) {
                        this.c(entityhuman);
                        return true;
                    }
                }

                if (flag) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return true;
                }
            }

            if (this.isBaby()) {
                return super.a(entityhuman, enumhand);
            } else {
                this.g(entityhuman);
                return true;
            }
        }
    }

    protected void dC() {
        this.a(SoundEffects.ENTITY_DONKEY_CHEST, 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
    }

    public int dH() {
        return 5;
    }
}
