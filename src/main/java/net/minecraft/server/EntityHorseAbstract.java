package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public abstract class EntityHorseAbstract extends EntityAnimal implements IInventoryListener, IJumpable {

    private static final Predicate<Entity> bM = (entity) -> {
        return entity instanceof EntityHorseAbstract && ((EntityHorseAbstract) entity).hasReproduced();
    };
    public static final IAttribute attributeJumpStrength = (new AttributeRanged((IAttribute) null, "horse.jumpStrength", 0.7D, 0.0D, 2.0D)).a("Jump Strength").a(true);
    private static final DataWatcherObject<Byte> bN = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.a);
    private static final DataWatcherObject<Optional<UUID>> bO = DataWatcher.a(EntityHorseAbstract.class, DataWatcherRegistry.o);
    private int bP;
    private int bQ;
    private int bR;
    public int bD;
    public int bE;
    protected boolean bG;
    public InventoryHorseChest inventoryChest;
    protected int bI;
    protected float jumpPower;
    private boolean canSlide;
    private float bT;
    private float bU;
    private float bV;
    private float bW;
    private float bX;
    private float bY;
    protected boolean bK = true;
    protected int bL;

    protected EntityHorseAbstract(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.setSize(1.3964844F, 1.6F);
        this.Q = 1.0F;
        this.loadChest();
    }

    protected void n() {
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.2D));
        this.goalSelector.a(1, new PathfinderGoalTame(this, 1.2D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D, EntityHorseAbstract.class));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 0.7D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
        this.dI();
    }

    protected void dI() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityHorseAbstract.bN, (byte) 0);
        this.datawatcher.register(EntityHorseAbstract.bO, Optional.empty());
    }

    protected boolean p(int i) {
        return ((Byte) this.datawatcher.get(EntityHorseAbstract.bN) & i) != 0;
    }

    protected void d(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityHorseAbstract.bN);

        if (flag) {
            this.datawatcher.set(EntityHorseAbstract.bN, (byte) (b0 | i));
        } else {
            this.datawatcher.set(EntityHorseAbstract.bN, (byte) (b0 & ~i));
        }

    }

    public boolean isTamed() {
        return this.p(2);
    }

    @Nullable
    public UUID getOwnerUUID() {
        return (UUID) ((Optional) this.datawatcher.get(EntityHorseAbstract.bO)).orElse((Object) null);
    }

    public void setOwnerUUID(@Nullable UUID uuid) {
        this.datawatcher.set(EntityHorseAbstract.bO, Optional.ofNullable(uuid));
    }

    public float dL() {
        return 0.5F;
    }

    public void a(boolean flag) {
        this.a(flag ? this.dL() : 1.0F);
    }

    public boolean dM() {
        return this.bG;
    }

    public void setTamed(boolean flag) {
        this.d(2, flag);
    }

    public void v(boolean flag) {
        this.bG = flag;
    }

    public boolean a(EntityHuman entityhuman) {
        return super.a(entityhuman) && this.getMonsterType() != EnumMonsterType.UNDEAD;
    }

    protected void u(float f) {
        if (f > 6.0F && this.dN()) {
            this.y(false);
        }

    }

    public boolean dN() {
        return this.p(16);
    }

    public boolean dO() {
        return this.p(32);
    }

    public boolean hasReproduced() {
        return this.p(8);
    }

    public void w(boolean flag) {
        this.d(8, flag);
    }

    public void x(boolean flag) {
        this.d(4, flag);
    }

    public int getTemper() {
        return this.bI;
    }

    public void setTemper(int i) {
        this.bI = i;
    }

    public int r(int i) {
        int j = MathHelper.clamp(this.getTemper() + i, 0, this.getMaxDomestication());

        this.setTemper(j);
        return j;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        Entity entity = damagesource.getEntity();

        return this.isVehicle() && entity != null && this.y(entity) ? false : super.damageEntity(damagesource, f);
    }

    public boolean isCollidable() {
        return !this.isVehicle();
    }

    private void dy() {
        this.dC();
        if (!this.isSilent()) {
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_HORSE_EAT, this.bV(), 1.0F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
        }

    }

    public void c(float f, float f1) {
        if (f > 1.0F) {
            this.a(SoundEffects.ENTITY_HORSE_LAND, 0.4F, 1.0F);
        }

        int i = MathHelper.f((f * 0.5F - 3.0F) * f1);

        if (i > 0) {
            this.damageEntity(DamageSource.FALL, (float) i);
            if (this.isVehicle()) {
                Iterator iterator = this.getAllPassengers().iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.damageEntity(DamageSource.FALL, (float) i);
                }
            }

            IBlockData iblockdata = this.world.getType(new BlockPosition(this.locX, this.locY - 0.2D - (double) this.lastYaw, this.locZ));
            Block block = iblockdata.getBlock();

            if (!iblockdata.isAir() && !this.isSilent()) {
                SoundEffectType soundeffecttype = block.getStepSound();

                this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, soundeffecttype.d(), this.bV(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }

        }
    }

    protected int dA() {
        return 2;
    }

    public void loadChest() {
        InventoryHorseChest inventoryhorsechest = this.inventoryChest;

        this.inventoryChest = new InventoryHorseChest(this.getDisplayName(), this.dA());
        this.inventoryChest.a(this.getCustomName());
        if (inventoryhorsechest != null) {
            inventoryhorsechest.b(this);
            int i = Math.min(inventoryhorsechest.getSize(), this.inventoryChest.getSize());

            for (int j = 0; j < i; ++j) {
                ItemStack itemstack = inventoryhorsechest.getItem(j);

                if (!itemstack.isEmpty()) {
                    this.inventoryChest.setItem(j, itemstack.cloneItemStack());
                }
            }
        }

        this.inventoryChest.a((IInventoryListener) this);
        this.dS();
    }

    protected void dS() {
        if (!this.world.isClientSide) {
            this.x(!this.inventoryChest.getItem(0).isEmpty() && this.dU());
        }
    }

    public void a(IInventory iinventory) {
        boolean flag = this.dV();

        this.dS();
        if (this.ticksLived > 20 && !flag && this.dV()) {
            this.a(SoundEffects.ENTITY_HORSE_SADDLE, 0.5F, 1.0F);
        }

    }

    @Nullable
    protected EntityHorseAbstract a(Entity entity, double d0) {
        double d1 = Double.MAX_VALUE;
        Entity entity1 = null;
        List<Entity> list = this.world.getEntities(entity, entity.getBoundingBox().b(d0, d0, d0), EntityHorseAbstract.bM);
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity2 = (Entity) iterator.next();
            double d2 = entity2.d(entity.locX, entity.locY, entity.locZ);

            if (d2 < d1) {
                entity1 = entity2;
                d1 = d2;
            }
        }

        return (EntityHorseAbstract) entity1;
    }

    public double getJumpStrength() {
        return this.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).getValue();
    }

    @Nullable
    protected SoundEffect cs() {
        return null;
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        if (this.random.nextInt(3) == 0) {
            this.dH();
        }

        return null;
    }

    @Nullable
    protected SoundEffect D() {
        if (this.random.nextInt(10) == 0 && !this.isFrozen()) {
            this.dH();
        }

        return null;
    }

    public boolean dU() {
        return true;
    }

    public boolean dV() {
        return this.p(4);
    }

    @Nullable
    protected SoundEffect dB() {
        this.dH();
        return null;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        if (!iblockdata.getMaterial().isLiquid()) {
            SoundEffectType soundeffecttype = iblockdata.getBlock().getStepSound();

            if (this.world.getType(blockposition.up()).getBlock() == Blocks.SNOW) {
                soundeffecttype = Blocks.SNOW.getStepSound();
            }

            if (this.isVehicle() && this.bK) {
                ++this.bL;
                if (this.bL > 5 && this.bL % 3 == 0) {
                    this.a(soundeffecttype);
                } else if (this.bL <= 5) {
                    this.a(SoundEffects.ENTITY_HORSE_STEP_WOOD, soundeffecttype.a() * 0.15F, soundeffecttype.b());
                }
            } else if (soundeffecttype == SoundEffectType.a) {
                this.a(SoundEffects.ENTITY_HORSE_STEP_WOOD, soundeffecttype.a() * 0.15F, soundeffecttype.b());
            } else {
                this.a(SoundEffects.ENTITY_HORSE_STEP, soundeffecttype.a() * 0.15F, soundeffecttype.b());
            }

        }
    }

    protected void a(SoundEffectType soundeffecttype) {
        this.a(SoundEffects.ENTITY_HORSE_GALLOP, soundeffecttype.a() * 0.15F, soundeffecttype.b());
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeMap().b(EntityHorseAbstract.attributeJumpStrength);
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(53.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.22499999403953552D);
    }

    public int dg() {
        return 6;
    }

    public int getMaxDomestication() {
        return 100;
    }

    protected float cD() {
        return 0.8F;
    }

    public int z() {
        return 400;
    }

    public void c(EntityHuman entityhuman) {
        if (!this.world.isClientSide && (!this.isVehicle() || this.w(entityhuman)) && this.isTamed()) {
            this.inventoryChest.a(this.getCustomName());
            entityhuman.openHorseInventory(this, this.inventoryChest);
        }

    }

    protected boolean b(EntityHuman entityhuman, ItemStack itemstack) {
        boolean flag = false;
        float f = 0.0F;
        short short0 = 0;
        byte b0 = 0;
        Item item = itemstack.getItem();

        if (item == Items.WHEAT) {
            f = 2.0F;
            short0 = 20;
            b0 = 3;
        } else if (item == Items.SUGAR) {
            f = 1.0F;
            short0 = 30;
            b0 = 3;
        } else if (item == Blocks.HAY_BLOCK.getItem()) {
            f = 20.0F;
            short0 = 180;
        } else if (item == Items.APPLE) {
            f = 3.0F;
            short0 = 60;
            b0 = 3;
        } else if (item == Items.GOLDEN_CARROT) {
            f = 4.0F;
            short0 = 60;
            b0 = 5;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.f(entityhuman);
            }
        } else if (item == Items.GOLDEN_APPLE || item == Items.ENCHANTED_GOLDEN_APPLE) {
            f = 10.0F;
            short0 = 240;
            b0 = 10;
            if (this.isTamed() && this.getAge() == 0 && !this.isInLove()) {
                flag = true;
                this.f(entityhuman);
            }
        }

        if (this.getHealth() < this.getMaxHealth() && f > 0.0F) {
            this.heal(f);
            flag = true;
        }

        if (this.isBaby() && short0 > 0) {
            this.world.addParticle(Particles.z, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + 0.5D + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, 0.0D, 0.0D, 0.0D);
            if (!this.world.isClientSide) {
                this.setAge(short0);
            }

            flag = true;
        }

        if (b0 > 0 && (flag || !this.isTamed()) && this.getTemper() < this.getMaxDomestication()) {
            flag = true;
            if (!this.world.isClientSide) {
                this.r(b0);
            }
        }

        if (flag) {
            this.dy();
        }

        return flag;
    }

    protected void g(EntityHuman entityhuman) {
        this.y(false);
        this.setStanding(false);
        if (!this.world.isClientSide) {
            entityhuman.yaw = this.yaw;
            entityhuman.pitch = this.pitch;
            entityhuman.startRiding(this);
        }

    }

    protected boolean isFrozen() {
        return super.isFrozen() && this.isVehicle() && this.dV() || this.dN() || this.dO();
    }

    public boolean f(ItemStack itemstack) {
        return false;
    }

    private void dz() {
        this.bD = 1;
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (!this.world.isClientSide && this.inventoryChest != null) {
            for (int i = 0; i < this.inventoryChest.getSize(); ++i) {
                ItemStack itemstack = this.inventoryChest.getItem(i);

                if (!itemstack.isEmpty()) {
                    this.a_(itemstack);
                }
            }

        }
    }

    public void movementTick() {
        if (this.random.nextInt(200) == 0) {
            this.dz();
        }

        super.movementTick();
        if (!this.world.isClientSide) {
            if (this.random.nextInt(900) == 0 && this.deathTicks == 0) {
                this.heal(1.0F);
            }

            if (this.dY()) {
                if (!this.dN() && !this.isVehicle() && this.random.nextInt(300) == 0 && this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.locY) - 1, MathHelper.floor(this.locZ))).getBlock() == Blocks.GRASS_BLOCK) {
                    this.y(true);
                }

                if (this.dN() && ++this.bP > 50) {
                    this.bP = 0;
                    this.y(false);
                }
            }

            this.dX();
        }
    }

    protected void dX() {
        if (this.hasReproduced() && this.isBaby() && !this.dN()) {
            EntityHorseAbstract entityhorseabstract = this.a(this, 16.0D);

            if (entityhorseabstract != null && this.h((Entity) entityhorseabstract) > 4.0D) {
                this.navigation.a((Entity) entityhorseabstract);
            }
        }

    }

    public boolean dY() {
        return true;
    }

    public void tick() {
        super.tick();
        if (this.bQ > 0 && ++this.bQ > 30) {
            this.bQ = 0;
            this.d(64, false);
        }

        if ((this.bT() || this.cP()) && this.bR > 0 && ++this.bR > 20) {
            this.bR = 0;
            this.setStanding(false);
        }

        if (this.bD > 0 && ++this.bD > 8) {
            this.bD = 0;
        }

        if (this.bE > 0) {
            ++this.bE;
            if (this.bE > 300) {
                this.bE = 0;
            }
        }

        this.bU = this.bT;
        if (this.dN()) {
            this.bT += (1.0F - this.bT) * 0.4F + 0.05F;
            if (this.bT > 1.0F) {
                this.bT = 1.0F;
            }
        } else {
            this.bT += (0.0F - this.bT) * 0.4F - 0.05F;
            if (this.bT < 0.0F) {
                this.bT = 0.0F;
            }
        }

        this.bW = this.bV;
        if (this.dO()) {
            this.bT = 0.0F;
            this.bU = this.bT;
            this.bV += (1.0F - this.bV) * 0.4F + 0.05F;
            if (this.bV > 1.0F) {
                this.bV = 1.0F;
            }
        } else {
            this.canSlide = false;
            this.bV += (0.8F * this.bV * this.bV * this.bV - this.bV) * 0.6F - 0.05F;
            if (this.bV < 0.0F) {
                this.bV = 0.0F;
            }
        }

        this.bY = this.bX;
        if (this.p(64)) {
            this.bX += (1.0F - this.bX) * 0.7F + 0.05F;
            if (this.bX > 1.0F) {
                this.bX = 1.0F;
            }
        } else {
            this.bX += (0.0F - this.bX) * 0.7F - 0.05F;
            if (this.bX < 0.0F) {
                this.bX = 0.0F;
            }
        }

    }

    private void dC() {
        if (!this.world.isClientSide) {
            this.bQ = 1;
            this.d(64, true);
        }

    }

    public void y(boolean flag) {
        this.d(16, flag);
    }

    public void setStanding(boolean flag) {
        if (flag) {
            this.y(false);
        }

        this.d(32, flag);
    }

    private void dH() {
        if (this.bT() || this.cP()) {
            this.bR = 1;
            this.setStanding(true);
        }

    }

    public void dZ() {
        this.dH();
        SoundEffect soundeffect = this.dB();

        if (soundeffect != null) {
            this.a(soundeffect, this.cD(), this.cE());
        }

    }

    public boolean h(EntityHuman entityhuman) {
        this.setOwnerUUID(entityhuman.getUniqueID());
        this.setTamed(true);
        if (entityhuman instanceof EntityPlayer) {
            CriterionTriggers.x.a((EntityPlayer) entityhuman, (EntityAnimal) this);
        }

        this.world.broadcastEntityEffect(this, (byte) 7);
        return true;
    }

    public void a(float f, float f1, float f2) {
        if (this.isVehicle() && this.dh() && this.dV()) {
            EntityLiving entityliving = (EntityLiving) this.bO();

            this.yaw = entityliving.yaw;
            this.lastYaw = this.yaw;
            this.pitch = entityliving.pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aQ = this.yaw;
            this.aS = this.aQ;
            f = entityliving.bh * 0.5F;
            f2 = entityliving.bj;
            if (f2 <= 0.0F) {
                f2 *= 0.25F;
                this.bL = 0;
            }

            if (this.onGround && this.jumpPower == 0.0F && this.dO() && !this.canSlide) {
                f = 0.0F;
                f2 = 0.0F;
            }

            if (this.jumpPower > 0.0F && !this.dM() && this.onGround) {
                this.motY = this.getJumpStrength() * (double) this.jumpPower;
                if (this.hasEffect(MobEffects.JUMP)) {
                    this.motY += (double) ((float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
                }

                this.v(true);
                this.impulse = true;
                if (f2 > 0.0F) {
                    float f3 = MathHelper.sin(this.yaw * 0.017453292F);
                    float f4 = MathHelper.cos(this.yaw * 0.017453292F);

                    this.motX += (double) (-0.4F * f3 * this.jumpPower);
                    this.motZ += (double) (0.4F * f4 * this.jumpPower);
                    this.ea();
                }

                this.jumpPower = 0.0F;
            }

            this.aU = this.cK() * 0.1F;
            if (this.bT()) {
                this.o((float) this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue());
                super.a(f, f1, f2);
            } else if (entityliving instanceof EntityHuman) {
                this.motX = 0.0D;
                this.motY = 0.0D;
                this.motZ = 0.0D;
            }

            if (this.onGround) {
                this.jumpPower = 0.0F;
                this.v(false);
            }

            this.aI = this.aJ;
            double d0 = this.locX - this.lastX;
            double d1 = this.locZ - this.lastZ;
            float f5 = MathHelper.sqrt(d0 * d0 + d1 * d1) * 4.0F;

            if (f5 > 1.0F) {
                f5 = 1.0F;
            }

            this.aJ += (f5 - this.aJ) * 0.4F;
            this.aK += this.aJ;
        } else {
            this.aU = 0.02F;
            super.a(f, f1, f2);
        }
    }

    protected void ea() {
        this.a(SoundEffects.ENTITY_HORSE_JUMP, 0.4F, 1.0F);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("EatingHaystack", this.dN());
        nbttagcompound.setBoolean("Bred", this.hasReproduced());
        nbttagcompound.setInt("Temper", this.getTemper());
        nbttagcompound.setBoolean("Tame", this.isTamed());
        if (this.getOwnerUUID() != null) {
            nbttagcompound.setString("OwnerUUID", this.getOwnerUUID().toString());
        }

        if (!this.inventoryChest.getItem(0).isEmpty()) {
            nbttagcompound.set("SaddleItem", this.inventoryChest.getItem(0).save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.y(nbttagcompound.getBoolean("EatingHaystack"));
        this.w(nbttagcompound.getBoolean("Bred"));
        this.setTemper(nbttagcompound.getInt("Temper"));
        this.setTamed(nbttagcompound.getBoolean("Tame"));
        String s;

        if (nbttagcompound.hasKeyOfType("OwnerUUID", 8)) {
            s = nbttagcompound.getString("OwnerUUID");
        } else {
            String s1 = nbttagcompound.getString("Owner");

            s = NameReferencingFileConverter.a(this.bK(), s1);
        }

        if (!s.isEmpty()) {
            this.setOwnerUUID(UUID.fromString(s));
        }

        AttributeInstance attributeinstance = this.getAttributeMap().a("Speed");

        if (attributeinstance != null) {
            this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(attributeinstance.b() * 0.25D);
        }

        if (nbttagcompound.hasKeyOfType("SaddleItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("SaddleItem"));

            if (itemstack.getItem() == Items.SADDLE) {
                this.inventoryChest.setItem(0, itemstack);
            }
        }

        this.dS();
    }

    public boolean mate(EntityAnimal entityanimal) {
        return false;
    }

    protected boolean eb() {
        return !this.isVehicle() && !this.isPassenger() && this.isTamed() && !this.isBaby() && this.getHealth() >= this.getMaxHealth() && this.isInLove();
    }

    @Nullable
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    protected void a(EntityAgeable entityageable, EntityHorseAbstract entityhorseabstract) {
        double d0 = this.getAttributeInstance(GenericAttributes.maxHealth).b() + entityageable.getAttributeInstance(GenericAttributes.maxHealth).b() + (double) this.ec();

        entityhorseabstract.getAttributeInstance(GenericAttributes.maxHealth).setValue(d0 / 3.0D);
        double d1 = this.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).b() + entityageable.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).b() + this.ed();

        entityhorseabstract.getAttributeInstance(EntityHorseAbstract.attributeJumpStrength).setValue(d1 / 3.0D);
        double d2 = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).b() + entityageable.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).b() + this.ee();

        entityhorseabstract.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(d2 / 3.0D);
    }

    public boolean dh() {
        return this.bO() instanceof EntityLiving;
    }

    public boolean G_() {
        return this.dV();
    }

    public void b(int i) {
        this.canSlide = true;
        this.dH();
    }

    public void I_() {}

    public void k(Entity entity) {
        super.k(entity);
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;

            this.aQ = entityinsentient.aQ;
        }

        if (this.bW > 0.0F) {
            float f = MathHelper.sin(this.aQ * 0.017453292F);
            float f1 = MathHelper.cos(this.aQ * 0.017453292F);
            float f2 = 0.7F * this.bW;
            float f3 = 0.15F * this.bW;

            entity.setPosition(this.locX + (double) (f2 * f), this.locY + this.aJ() + entity.aI() + (double) f3, this.locZ - (double) (f2 * f1));
            if (entity instanceof EntityLiving) {
                ((EntityLiving) entity).aQ = this.aQ;
            }
        }

    }

    protected float ec() {
        return 15.0F + (float) this.random.nextInt(8) + (float) this.random.nextInt(9);
    }

    protected double ed() {
        return 0.4000000059604645D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D + this.random.nextDouble() * 0.2D;
    }

    protected double ee() {
        return (0.44999998807907104D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D + this.random.nextDouble() * 0.3D) * 0.25D;
    }

    public boolean z_() {
        return false;
    }

    public float getHeadHeight() {
        return this.length;
    }

    public boolean ef() {
        return false;
    }

    public boolean g(ItemStack itemstack) {
        return false;
    }

    public boolean c(int i, ItemStack itemstack) {
        int j = i - 400;

        if (j >= 0 && j < 2 && j < this.inventoryChest.getSize()) {
            if (j == 0 && itemstack.getItem() != Items.SADDLE) {
                return false;
            } else if (j == 1 && (!this.ef() || !this.g(itemstack))) {
                return false;
            } else {
                this.inventoryChest.setItem(j, itemstack);
                this.dS();
                return true;
            }
        } else {
            int k = i - 500 + 2;

            if (k >= 2 && k < this.inventoryChest.getSize()) {
                this.inventoryChest.setItem(k, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    @Nullable
    public Entity bO() {
        return this.bP().isEmpty() ? null : (Entity) this.bP().get(0);
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        if (this.random.nextInt(5) == 0) {
            this.setAgeRaw(-24000);
        }

        return groupdataentity;
    }
}
