package net.minecraft.server;

public class EntityOcelot extends EntityTameableAnimal {

    private PathfinderGoalTempt bq;

    public EntityOcelot(World world) {
        super(world);
        this.a(0.6F, 0.8F);
        this.getNavigation().a(true);
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, this.bp);
        this.goalSelector.a(3, this.bq = new PathfinderGoalTempt(this, 0.6D, Items.RAW_FISH, true));
        this.goalSelector.a(4, new PathfinderGoalAvoidPlayer(this, EntityHuman.class, 16.0F, 0.8D, 1.33D));
        this.goalSelector.a(5, new PathfinderGoalFollowOwner(this, 1.0D, 10.0F, 5.0F));
        this.goalSelector.a(6, new PathfinderGoalJumpOnBlock(this, 1.33D));
        this.goalSelector.a(7, new PathfinderGoalLeapAtTarget(this, 0.3F));
        this.goalSelector.a(8, new PathfinderGoalOcelotAttack(this));
        this.goalSelector.a(9, new PathfinderGoalBreed(this, 0.8D));
        this.goalSelector.a(10, new PathfinderGoalRandomStroll(this, 0.8D));
        this.goalSelector.a(11, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 10.0F));
        this.targetSelector.a(1, new PathfinderGoalRandomTargetNonTamed(this, EntityChicken.class, 750, false));
    }

    protected void c() {
        super.c();
        this.datawatcher.a(18, Byte.valueOf((byte) 0));
    }

    public void bo() {
        if (this.getControllerMove().a()) {
            double d0 = this.getControllerMove().b();

            if (d0 == 0.6D) {
                this.setSneaking(true);
                this.setSprinting(false);
            } else if (d0 == 1.33D) {
                this.setSneaking(false);
                this.setSprinting(true);
            } else {
                this.setSneaking(false);
                this.setSprinting(false);
            }
        } else {
            this.setSneaking(false);
            this.setSprinting(false);
        }
    }

    protected boolean isTypeNotPersistent() {
        return !this.isTamed() /*&& this.ticksLived > 2400*/; // CraftBukkit
    }

    public boolean bj() {
        return true;
    }

    protected void aC() {
        super.aC();
        this.getAttributeInstance(GenericAttributes.a).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.d).setValue(0.30000001192092896D);
    }

    protected void b(float f) {}

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("CatType", this.getCatType());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setCatType(nbttagcompound.getInt("CatType"));
    }

    protected String t() {
        return this.isTamed() ? (this.ce() ? "mob.cat.purr" : (this.random.nextInt(4) == 0 ? "mob.cat.purreow" : "mob.cat.meow")) : "";
    }

    protected String aS() {
        return "mob.cat.hitt";
    }

    protected String aT() {
        return "mob.cat.hitt";
    }

    protected float be() {
        return 0.4F;
    }

    protected Item getLoot() {
        return Items.LEATHER;
    }

    public boolean n(Entity entity) {
        return entity.damageEntity(DamageSource.mobAttack(this), 3.0F);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable()) {
            return false;
        } else {
            this.bp.setSitting(false);
            return super.damageEntity(damagesource, f);
        }
    }

    protected void dropDeathLoot(boolean flag, int i) {
        org.bukkit.craftbukkit.event.CraftEventFactory.callEntityDeathEvent(this); // CraftBukkit - Call EntityDeathEvent
    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (this.isTamed()) {
            if (this.e(entityhuman) && !this.world.isStatic && !this.c(itemstack)) {
                this.bp.setSitting(!this.isSitting());
            }
        } else if (this.bq.f() && itemstack != null && itemstack.getItem() == Items.RAW_FISH && entityhuman.f(this) < 9.0D) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                --itemstack.count;
            }

            if (itemstack.count <= 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, (ItemStack) null);
            }

            if (!this.world.isStatic) {
                // CraftBukkit - added event call and isCancelled check
                if (this.random.nextInt(3) == 0 && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityTameEvent(this, entityhuman).isCancelled()) {
                    this.setTamed(true);
                    this.setCatType(1 + this.world.random.nextInt(3));
                    this.setOwnerUUID(entityhuman.getUniqueID().toString());
                    this.i(true);
                    this.bp.setSitting(true);
                    this.world.broadcastEntityEffect(this, (byte) 7);
                } else {
                    this.i(false);
                    this.world.broadcastEntityEffect(this, (byte) 6);
                }
            }

            return true;
        }

        return super.a(entityhuman);
    }

    public EntityOcelot b(EntityAgeable entityageable) {
        EntityOcelot entityocelot = new EntityOcelot(this.world);

        if (this.isTamed()) {
            entityocelot.setOwnerUUID(this.getOwnerUUID());
            entityocelot.setTamed(true);
            entityocelot.setCatType(this.getCatType());
        }

        return entityocelot;
    }

    public boolean c(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() == Items.RAW_FISH;
    }

    public boolean mate(EntityAnimal entityanimal) {
        if (entityanimal == this) {
            return false;
        } else if (!this.isTamed()) {
            return false;
        } else if (!(entityanimal instanceof EntityOcelot)) {
            return false;
        } else {
            EntityOcelot entityocelot = (EntityOcelot) entityanimal;

            return !entityocelot.isTamed() ? false : this.ce() && entityocelot.ce();
        }
    }

    public int getCatType() {
        return this.datawatcher.getByte(18);
    }

    public void setCatType(int i) {
        this.datawatcher.watch(18, Byte.valueOf((byte) i));
    }

    public boolean canSpawn() {
        if (this.world.random.nextInt(3) == 0) {
            return false;
        } else {
            if (this.world.b(this.boundingBox) && this.world.getCubes(this, this.boundingBox).isEmpty() && !this.world.containsLiquid(this.boundingBox)) {
                int i = MathHelper.floor(this.locX);
                int j = MathHelper.floor(this.boundingBox.b);
                int k = MathHelper.floor(this.locZ);

                if (j < 63) {
                    return false;
                }

                Block block = this.world.getType(i, j - 1, k);

                if (block == Blocks.GRASS || block.getMaterial() == Material.LEAVES) {
                    return true;
                }
            }

            return false;
        }
    }

    public String getName() {
        return this.hasCustomName() ? this.getCustomName() : (this.isTamed() ? LocaleI18n.get("entity.Cat.name") : super.getName());
    }

    public GroupDataEntity a(GroupDataEntity groupdataentity) {
        groupdataentity = super.a(groupdataentity);
        if (this.world.random.nextInt(7) == 0) {
            for (int i = 0; i < 2; ++i) {
                EntityOcelot entityocelot = new EntityOcelot(this.world);

                entityocelot.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, 0.0F);
                entityocelot.setAge(-24000);
                this.world.addEntity(entityocelot, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.OCELOT_BABY); // CraftBukkit - add SpawnReason
            }
        }

        return groupdataentity;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
