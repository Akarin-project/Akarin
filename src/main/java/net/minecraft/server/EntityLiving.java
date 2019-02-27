package net.minecraft.server;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class EntityLiving extends Entity {

    private static final Logger a = LogManager.getLogger();
    private static final UUID b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier c = (new AttributeModifier(EntityLiving.b, "Sprinting speed boost", 0.30000001192092896D, 2)).a(false);
    protected static final DataWatcherObject<Byte> aw = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Float> HEALTH = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> g = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> h = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> bx = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private AttributeMapBase attributeMap;
    public CombatTracker combatTracker = new CombatTracker(this);
    public final Map<MobEffectList, MobEffect> effects = Maps.newHashMap();
    private final NonNullList<ItemStack> bB;
    private final NonNullList<ItemStack> bC;
    public boolean ax;
    public EnumHand ay;
    public int az;
    public int aA;
    public int hurtTicks;
    public int aC;
    public float aD;
    public int deathTicks;
    public float aF;
    public float aG;
    protected int aH;
    public float aI;
    public float aJ;
    public float aK;
    public int maxNoDamageTicks;
    public float aM;
    public float aN;
    public float aO;
    public float aP;
    public float aQ;
    public float aR;
    public float aS;
    public float aT;
    public float aU;
    public EntityHuman killer;
    protected int lastDamageByPlayerTime;
    protected boolean killed;
    protected int ticksFarFromPlayer;
    protected float aZ;
    protected float ba;
    protected float bb;
    protected float bc;
    protected float bd;
    protected int be;
    public float lastDamage;
    protected boolean bg;
    public float bh;
    public float bi;
    public float bj;
    public float bk;
    protected int bl;
    protected double bm;
    protected double bn;
    protected double bo;
    protected double bp;
    protected double bq;
    protected double br;
    protected int bs;
    public boolean updateEffects;
    public EntityLiving lastDamager;
    public int hurtTimestamp;
    private EntityLiving bG;
    private int bH;
    private float bI;
    private int bJ;
    private float bK;
    protected ItemStack activeItem;
    protected int bu;
    protected int bv;
    private BlockPosition bL;
    private DamageSource bM;
    private long bN;
    protected int bw;
    private float bO;
    private float bP;

    protected EntityLiving(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.bB = NonNullList.a(2, ItemStack.a);
        this.bC = NonNullList.a(4, ItemStack.a);
        this.maxNoDamageTicks = 20;
        this.aU = 0.02F;
        this.updateEffects = true;
        this.activeItem = ItemStack.a;
        this.initAttributes();
        this.setHealth(this.getMaxHealth());
        this.j = true;
        this.aP = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.aO = (float) Math.random() * 12398.0F;
        this.yaw = (float) (Math.random() * 6.2831854820251465D);
        this.aS = this.yaw;
        this.Q = 0.6F;
    }

    public void killEntity() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    protected void x_() {
        this.datawatcher.register(EntityLiving.aw, (byte) 0);
        this.datawatcher.register(EntityLiving.g, 0);
        this.datawatcher.register(EntityLiving.h, false);
        this.datawatcher.register(EntityLiving.bx, 0);
        this.datawatcher.register(EntityLiving.HEALTH, 1.0F);
    }

    protected void initAttributes() {
        this.getAttributeMap().b(GenericAttributes.maxHealth);
        this.getAttributeMap().b(GenericAttributes.c);
        this.getAttributeMap().b(GenericAttributes.MOVEMENT_SPEED);
        this.getAttributeMap().b(GenericAttributes.h);
        this.getAttributeMap().b(GenericAttributes.i);
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (!this.isInWater()) {
            this.at();
        }

        if (!this.world.isClientSide && this.fallDistance > 3.0F && flag) {
            float f = (float) MathHelper.f(this.fallDistance - 3.0F);

            if (!iblockdata.isAir()) {
                double d1 = Math.min((double) (0.2F + f / 15.0F), 2.5D);
                int i = (int) (150.0D * d1);

                ((WorldServer) this.world).a(new ParticleParamBlock(Particles.d, iblockdata), this.locX, this.locY, this.locZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
            }
        }

        super.a(d0, flag, iblockdata, blockposition);
    }

    public boolean ca() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    public void W() {
        this.aF = this.aG;
        super.W();
        this.world.methodProfiler.enter("livingEntityBaseTick");
        boolean flag = this instanceof EntityHuman;

        if (this.isAlive()) {
            if (this.inBlock()) {
                this.damageEntity(DamageSource.STUCK, 1.0F);
            } else if (flag && !this.world.getWorldBorder().a(this.getBoundingBox())) {
                double d0 = this.world.getWorldBorder().a((Entity) this) + this.world.getWorldBorder().getDamageBuffer();

                if (d0 < 0.0D) {
                    double d1 = this.world.getWorldBorder().getDamageAmount();

                    if (d1 > 0.0D) {
                        this.damageEntity(DamageSource.STUCK, (float) Math.max(1, MathHelper.floor(-d0 * d1)));
                    }
                }
            }
        }

        if (this.isFireProof() || this.world.isClientSide) {
            this.extinguish();
        }

        boolean flag1 = flag && ((EntityHuman) this).abilities.isInvulnerable;

        if (this.isAlive()) {
            if (this.a(TagsFluid.WATER) && this.world.getType(new BlockPosition(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ)).getBlock() != Blocks.BUBBLE_COLUMN) {
                if (!this.ca() && !MobEffectUtil.c(this) && !flag1) {
                    this.setAirTicks(this.k(this.getAirTicks()));
                    if (this.getAirTicks() == -20) {
                        this.setAirTicks(0);

                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float f1 = this.random.nextFloat() - this.random.nextFloat();
                            float f2 = this.random.nextFloat() - this.random.nextFloat();

                            this.world.addParticle(Particles.e, this.locX + (double) f, this.locY + (double) f1, this.locZ + (double) f2, this.motX, this.motY, this.motZ);
                        }

                        this.damageEntity(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.world.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().aY()) {
                    this.stopRiding();
                }
            } else if (this.getAirTicks() < this.bf()) {
                this.setAirTicks(this.l(this.getAirTicks()));
            }

            if (!this.world.isClientSide) {
                BlockPosition blockposition = new BlockPosition(this);

                if (!Objects.equal(this.bL, blockposition)) {
                    this.bL = blockposition;
                    this.b(blockposition);
                }
            }
        }

        if (this.isAlive() && this.ap()) {
            this.extinguish();
        }

        this.aM = this.aN;
        if (this.hurtTicks > 0) {
            --this.hurtTicks;
        }

        if (this.noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
            --this.noDamageTicks;
        }

        if (this.getHealth() <= 0.0F) {
            this.cb();
        }

        if (this.lastDamageByPlayerTime > 0) {
            --this.lastDamageByPlayerTime;
        } else {
            this.killer = null;
        }

        if (this.bG != null && !this.bG.isAlive()) {
            this.bG = null;
        }

        if (this.lastDamager != null) {
            if (!this.lastDamager.isAlive()) {
                this.setLastDamager((EntityLiving) null);
            } else if (this.ticksLived - this.hurtTimestamp > 100) {
                this.setLastDamager((EntityLiving) null);
            }
        }

        this.tickPotionEffects();
        this.bc = this.bb;
        this.aR = this.aQ;
        this.aT = this.aS;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.world.methodProfiler.exit();
    }

    protected void b(BlockPosition blockposition) {
        int i = EnchantmentManager.a(Enchantments.FROST_WALKER, this);

        if (i > 0) {
            EnchantmentFrostWalker.a(this, this.world, blockposition, i);
        }

    }

    public boolean isBaby() {
        return false;
    }

    public boolean aY() {
        return false;
    }

    protected void cb() {
        ++this.deathTicks;
        if (this.deathTicks == 20) {
            int i;

            if (!this.world.isClientSide && (this.alwaysGivesExp() || this.lastDamageByPlayerTime > 0 && this.isDropExperience() && this.world.getGameRules().getBoolean("doMobLoot"))) {
                i = this.getExpValue(this.killer);

                while (i > 0) {
                    int j = EntityExperienceOrb.getOrbValue(i);

                    i -= j;
                    this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
                }
            }

            this.die();

            for (i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(Particles.J, this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, this.locY + (double) (this.random.nextFloat() * this.length), this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width, d0, d1, d2);
            }
        }

    }

    protected boolean isDropExperience() {
        return !this.isBaby();
    }

    protected int k(int i) {
        int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

        return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int l(int i) {
        return Math.min(i + 4, this.bf());
    }

    protected int getExpValue(EntityHuman entityhuman) {
        return 0;
    }

    protected boolean alwaysGivesExp() {
        return false;
    }

    public Random getRandom() {
        return this.random;
    }

    @Nullable
    public EntityLiving getLastDamager() {
        return this.lastDamager;
    }

    public int cg() {
        return this.hurtTimestamp;
    }

    public void setLastDamager(@Nullable EntityLiving entityliving) {
        this.lastDamager = entityliving;
        this.hurtTimestamp = this.ticksLived;
    }

    public EntityLiving ch() {
        return this.bG;
    }

    public int ci() {
        return this.bH;
    }

    public void z(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.bG = (EntityLiving) entity;
        } else {
            this.bG = null;
        }

        this.bH = this.ticksLived;
    }

    public int cj() {
        return this.ticksFarFromPlayer;
    }

    protected void b(ItemStack itemstack) {
        if (!itemstack.isEmpty()) {
            SoundEffect soundeffect = SoundEffects.ITEM_ARMOR_EQUIP_GENERIC;
            Item item = itemstack.getItem();

            if (item instanceof ItemArmor) {
                soundeffect = ((ItemArmor) item).d().b();
            } else if (item == Items.ELYTRA) {
                soundeffect = SoundEffects.ITEM_ARMOR_EQUIP_ELYTRA;
            }

            this.a(soundeffect, 1.0F, 1.0F);
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setFloat("Health", this.getHealth());
        nbttagcompound.setShort("HurtTime", (short) this.hurtTicks);
        nbttagcompound.setInt("HurtByTimestamp", this.hurtTimestamp);
        nbttagcompound.setShort("DeathTime", (short) this.deathTicks);
        nbttagcompound.setFloat("AbsorptionAmount", this.getAbsorptionHearts());
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int i = aenumitemslot.length;

        EnumItemSlot enumitemslot;
        int j;
        ItemStack itemstack;

        for (j = 0; j < i; ++j) {
            enumitemslot = aenumitemslot[j];
            itemstack = this.getEquipment(enumitemslot);
            if (!itemstack.isEmpty()) {
                this.getAttributeMap().a(itemstack.a(enumitemslot));
            }
        }

        nbttagcompound.set("Attributes", GenericAttributes.a(this.getAttributeMap()));
        aenumitemslot = EnumItemSlot.values();
        i = aenumitemslot.length;

        for (j = 0; j < i; ++j) {
            enumitemslot = aenumitemslot[j];
            itemstack = this.getEquipment(enumitemslot);
            if (!itemstack.isEmpty()) {
                this.getAttributeMap().b(itemstack.a(enumitemslot));
            }
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.values().iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add((NBTBase) mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("ActiveEffects", nbttaglist);
        }

        nbttagcompound.setBoolean("FallFlying", this.dc());
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.setAbsorptionHearts(nbttagcompound.getFloat("AbsorptionAmount"));
        if (nbttagcompound.hasKeyOfType("Attributes", 9) && this.world != null && !this.world.isClientSide) {
            GenericAttributes.a(this.getAttributeMap(), nbttagcompound.getList("Attributes", 10));
        }

        if (nbttagcompound.hasKeyOfType("ActiveEffects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("ActiveEffects", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                MobEffect mobeffect = MobEffect.b(nbttagcompound1);

                if (mobeffect != null) {
                    this.effects.put(mobeffect.getMobEffect(), mobeffect);
                }
            }
        }

        if (nbttagcompound.hasKeyOfType("Health", 99)) {
            this.setHealth(nbttagcompound.getFloat("Health"));
        }

        this.hurtTicks = nbttagcompound.getShort("HurtTime");
        this.deathTicks = nbttagcompound.getShort("DeathTime");
        this.hurtTimestamp = nbttagcompound.getInt("HurtByTimestamp");
        if (nbttagcompound.hasKeyOfType("Team", 8)) {
            String s = nbttagcompound.getString("Team");
            ScoreboardTeam scoreboardteam = this.world.getScoreboard().getTeam(s);
            boolean flag = scoreboardteam != null && this.world.getScoreboard().addPlayerToTeam(this.bu(), scoreboardteam);

            if (!flag) {
                EntityLiving.a.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", s);
            }
        }

        if (nbttagcompound.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

    }

    protected void tickPotionEffects() {
        Iterator iterator = this.effects.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                MobEffect mobeffect = (MobEffect) this.effects.get(mobeffectlist);

                if (!mobeffect.tick(this)) {
                    if (!this.world.isClientSide) {
                        iterator.remove();
                        this.b(mobeffect);
                    }
                } else if (mobeffect.getDuration() % 600 == 0) {
                    this.a(mobeffect, false);
                }
            }
        } catch (ConcurrentModificationException concurrentmodificationexception) {
            ;
        }

        if (this.updateEffects) {
            if (!this.world.isClientSide) {
                this.C();
            }

            this.updateEffects = false;
        }

        int i = (Integer) this.datawatcher.get(EntityLiving.g);
        boolean flag = (Boolean) this.datawatcher.get(EntityLiving.h);

        if (i > 0) {
            boolean flag1;

            if (this.isInvisible()) {
                flag1 = this.random.nextInt(15) == 0;
            } else {
                flag1 = this.random.nextBoolean();
            }

            if (flag) {
                flag1 &= this.random.nextInt(5) == 0;
            }

            if (flag1 && i > 0) {
                double d0 = (double) (i >> 16 & 255) / 255.0D;
                double d1 = (double) (i >> 8 & 255) / 255.0D;
                double d2 = (double) (i >> 0 & 255) / 255.0D;

                this.world.addParticle(flag ? Particles.a : Particles.s, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.width, this.locY + this.random.nextDouble() * (double) this.length, this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.width, d0, d1, d2);
            }
        }

    }

    protected void C() {
        if (this.effects.isEmpty()) {
            this.cl();
            this.setInvisible(false);
        } else {
            Collection<MobEffect> collection = this.effects.values();

            this.datawatcher.set(EntityLiving.h, c(collection));
            this.datawatcher.set(EntityLiving.g, PotionUtil.a(collection));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }

    }

    public static boolean c(Collection<MobEffect> collection) {
        Iterator iterator = collection.iterator();

        MobEffect mobeffect;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            mobeffect = (MobEffect) iterator.next();
        } while (mobeffect.isAmbient());

        return false;
    }

    protected void cl() {
        this.datawatcher.set(EntityLiving.h, false);
        this.datawatcher.set(EntityLiving.g, 0);
    }

    public boolean removeAllEffects() {
        if (this.world.isClientSide) {
            return false;
        } else {
            Iterator<MobEffect> iterator = this.effects.values().iterator();

            boolean flag;

            for (flag = false; iterator.hasNext(); flag = true) {
                this.b((MobEffect) iterator.next());
                iterator.remove();
            }

            return flag;
        }
    }

    public Collection<MobEffect> getEffects() {
        return this.effects.values();
    }

    public Map<MobEffectList, MobEffect> co() {
        return this.effects;
    }

    public boolean hasEffect(MobEffectList mobeffectlist) {
        return this.effects.containsKey(mobeffectlist);
    }

    @Nullable
    public MobEffect getEffect(MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.get(mobeffectlist);
    }

    public boolean addEffect(MobEffect mobeffect) {
        if (!this.d(mobeffect)) {
            return false;
        } else {
            MobEffect mobeffect1 = (MobEffect) this.effects.get(mobeffect.getMobEffect());

            if (mobeffect1 == null) {
                this.effects.put(mobeffect.getMobEffect(), mobeffect);
                this.a(mobeffect);
                return true;
            } else if (mobeffect1.a(mobeffect)) {
                this.a(mobeffect1, true);
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean d(MobEffect mobeffect) {
        if (this.getMonsterType() == EnumMonsterType.UNDEAD) {
            MobEffectList mobeffectlist = mobeffect.getMobEffect();

            if (mobeffectlist == MobEffects.REGENERATION || mobeffectlist == MobEffects.POISON) {
                return false;
            }
        }

        return true;
    }

    public boolean cp() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    @Nullable
    public MobEffect c(@Nullable MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.remove(mobeffectlist);
    }

    public boolean removeEffect(MobEffectList mobeffectlist) {
        MobEffect mobeffect = this.c(mobeffectlist);

        if (mobeffect != null) {
            this.b(mobeffect);
            return true;
        } else {
            return false;
        }
    }

    protected void a(MobEffect mobeffect) {
        this.updateEffects = true;
        if (!this.world.isClientSide) {
            mobeffect.getMobEffect().b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void a(MobEffect mobeffect, boolean flag) {
        this.updateEffects = true;
        if (flag && !this.world.isClientSide) {
            MobEffectList mobeffectlist = mobeffect.getMobEffect();

            mobeffectlist.a(this, this.getAttributeMap(), mobeffect.getAmplifier());
            mobeffectlist.b(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    protected void b(MobEffect mobeffect) {
        this.updateEffects = true;
        if (!this.world.isClientSide) {
            mobeffect.getMobEffect().a(this, this.getAttributeMap(), mobeffect.getAmplifier());
        }

    }

    public void heal(float f) {
        float f1 = this.getHealth();

        if (f1 > 0.0F) {
            this.setHealth(f1 + f);
        }

    }

    public float getHealth() {
        return (Float) this.datawatcher.get(EntityLiving.HEALTH);
    }

    public void setHealth(float f) {
        this.datawatcher.set(EntityLiving.HEALTH, MathHelper.a(f, 0.0F, this.getMaxHealth()));
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.world.isClientSide) {
            return false;
        } else if (this.getHealth() <= 0.0F) {
            return false;
        } else if (damagesource.p() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            this.ticksFarFromPlayer = 0;
            float f1 = f;

            if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && !this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
                this.getEquipment(EnumItemSlot.HEAD).damage((int) (f * 4.0F + this.random.nextFloat() * f * 2.0F), this);
                f *= 0.75F;
            }

            boolean flag = false;
            float f2 = 0.0F;

            if (f > 0.0F && this.applyBlockingModifier(damagesource)) {
                this.damageShield(f);
                f2 = f;
                f = 0.0F;
                if (!damagesource.b()) {
                    Entity entity = damagesource.j();

                    if (entity instanceof EntityLiving) {
                        this.c((EntityLiving) entity);
                    }
                }

                flag = true;
            }

            this.aJ = 1.5F;
            boolean flag1 = true;

            if ((float) this.noDamageTicks > (float) this.maxNoDamageTicks / 2.0F) {
                if (f <= this.lastDamage) {
                    return false;
                }

                this.damageEntity0(damagesource, f - this.lastDamage);
                this.lastDamage = f;
                flag1 = false;
            } else {
                this.lastDamage = f;
                this.noDamageTicks = this.maxNoDamageTicks;
                this.damageEntity0(damagesource, f);
                this.aC = 10;
                this.hurtTicks = this.aC;
            }

            this.aD = 0.0F;
            Entity entity1 = damagesource.getEntity();

            if (entity1 != null) {
                if (entity1 instanceof EntityLiving) {
                    this.setLastDamager((EntityLiving) entity1);
                }

                if (entity1 instanceof EntityHuman) {
                    this.lastDamageByPlayerTime = 100;
                    this.killer = (EntityHuman) entity1;
                } else if (entity1 instanceof EntityWolf) {
                    EntityWolf entitywolf = (EntityWolf) entity1;

                    if (entitywolf.isTamed()) {
                        this.lastDamageByPlayerTime = 100;
                        this.killer = null;
                    }
                }
            }

            if (flag1) {
                if (flag) {
                    this.world.broadcastEntityEffect(this, (byte) 29);
                } else if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).y()) {
                    this.world.broadcastEntityEffect(this, (byte) 33);
                } else {
                    byte b0;

                    if (damagesource == DamageSource.DROWN) {
                        b0 = 36;
                    } else if (damagesource.p()) {
                        b0 = 37;
                    } else {
                        b0 = 2;
                    }

                    this.world.broadcastEntityEffect(this, b0);
                }

                if (damagesource != DamageSource.DROWN && (!flag || f > 0.0F)) {
                    this.aA();
                }

                if (entity1 != null) {
                    double d0 = entity1.locX - this.locX;

                    double d1;

                    for (d1 = entity1.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.aD = (float) (MathHelper.c(d1, d0) * 57.2957763671875D - (double) this.yaw);
                    this.a(entity1, 0.4F, d0, d1);
                } else {
                    this.aD = (float) ((int) (Math.random() * 2.0D) * 180);
                }
            }

            if (this.getHealth() <= 0.0F) {
                if (!this.e(damagesource)) {
                    SoundEffect soundeffect = this.cs();

                    if (flag1 && soundeffect != null) {
                        this.a(soundeffect, this.cD(), this.cE());
                    }

                    this.die(damagesource);
                }
            } else if (flag1) {
                this.c(damagesource);
            }

            boolean flag2 = !flag || f > 0.0F;

            if (flag2) {
                this.bM = damagesource;
                this.bN = this.world.getTime();
            }

            if (this instanceof EntityPlayer) {
                CriterionTriggers.h.a((EntityPlayer) this, damagesource, f1, f, flag);
                if (f2 > 0.0F && f2 < 3.4028235E37F) {
                    ((EntityPlayer) this).a(StatisticList.DAMAGE_BLOCKED_BY_SHIELD, Math.round(f2 * 10.0F));
                }
            }

            if (entity1 instanceof EntityPlayer) {
                CriterionTriggers.g.a((EntityPlayer) entity1, this, damagesource, f1, f, flag);
            }

            return flag2;
        }
    }

    protected void c(EntityLiving entityliving) {
        entityliving.a(this, 0.5F, this.locX - entityliving.locX, this.locZ - entityliving.locZ);
    }

    private boolean e(DamageSource damagesource) {
        if (damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            ItemStack itemstack = null;
            EnumHand[] aenumhand = EnumHand.values();
            int i = aenumhand.length;

            for (int j = 0; j < i; ++j) {
                EnumHand enumhand = aenumhand[j];
                ItemStack itemstack1 = this.b(enumhand);

                if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
                    itemstack = itemstack1.cloneItemStack();
                    itemstack1.subtract(1);
                    break;
                }
            }

            if (itemstack != null) {
                if (this instanceof EntityPlayer) {
                    EntityPlayer entityplayer = (EntityPlayer) this;

                    entityplayer.b(StatisticList.ITEM_USED.b(Items.TOTEM_OF_UNDYING));
                    CriterionTriggers.B.a(entityplayer, itemstack);
                }

                this.setHealth(1.0F);
                this.removeAllEffects();
                this.addEffect(new MobEffect(MobEffects.REGENERATION, 900, 1));
                this.addEffect(new MobEffect(MobEffects.ABSORBTION, 100, 1));
                this.world.broadcastEntityEffect(this, (byte) 35);
            }

            return itemstack != null;
        }
    }

    @Nullable
    public DamageSource cr() {
        if (this.world.getTime() - this.bN > 40L) {
            this.bM = null;
        }

        return this.bM;
    }

    protected void c(DamageSource damagesource) {
        SoundEffect soundeffect = this.d(damagesource);

        if (soundeffect != null) {
            this.a(soundeffect, this.cD(), this.cE());
        }

    }

    private boolean applyBlockingModifier(DamageSource damagesource) {
        if (!damagesource.ignoresArmor() && this.isBlocking()) {
            Vec3D vec3d = damagesource.w();

            if (vec3d != null) {
                Vec3D vec3d1 = this.f(1.0F);
                Vec3D vec3d2 = vec3d.a(new Vec3D(this.locX, this.locY, this.locZ)).a();

                vec3d2 = new Vec3D(vec3d2.x, 0.0D, vec3d2.z);
                if (vec3d2.b(vec3d1) < 0.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    public void c(ItemStack itemstack) {
        super.a(SoundEffects.ENTITY_ITEM_BREAK, 0.8F, 0.8F + this.world.random.nextFloat() * 0.4F);
        this.a(itemstack, 5);
    }

    public void die(DamageSource damagesource) {
        if (!this.killed) {
            Entity entity = damagesource.getEntity();
            EntityLiving entityliving = this.cv();

            if (this.be >= 0 && entityliving != null) {
                entityliving.a(this, this.be, damagesource);
            }

            if (entity != null) {
                entity.b(this);
            }

            this.killed = true;
            this.getCombatTracker().g();
            if (!this.world.isClientSide) {
                int i = 0;

                if (entity instanceof EntityHuman) {
                    i = EnchantmentManager.g((EntityLiving) entity);
                }

                if (this.isDropExperience() && this.world.getGameRules().getBoolean("doMobLoot")) {
                    boolean flag = this.lastDamageByPlayerTime > 0;

                    this.a(flag, i, damagesource);
                }
            }

            this.world.broadcastEntityEffect(this, (byte) 3);
        }
    }

    protected void a(boolean flag, int i, DamageSource damagesource) {
        this.dropDeathLoot(flag, i);
        this.dropEquipment(flag, i);
    }

    protected void dropEquipment(boolean flag, int i) {}

    public void a(Entity entity, float f, double d0, double d1) {
        if (this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue()) {
            this.impulse = true;
            float f1 = MathHelper.sqrt(d0 * d0 + d1 * d1);

            this.motX /= 2.0D;
            this.motZ /= 2.0D;
            this.motX -= d0 / (double) f1 * (double) f;
            this.motZ -= d1 / (double) f1 * (double) f;
            if (this.onGround) {
                this.motY /= 2.0D;
                this.motY += (double) f;
                if (this.motY > 0.4000000059604645D) {
                    this.motY = 0.4000000059604645D;
                }
            }

        }
    }

    @Nullable
    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_GENERIC_HURT;
    }

    @Nullable
    protected SoundEffect cs() {
        return SoundEffects.ENTITY_GENERIC_DEATH;
    }

    protected SoundEffect m(int i) {
        return i > 4 ? SoundEffects.ENTITY_GENERIC_BIG_FALL : SoundEffects.ENTITY_GENERIC_SMALL_FALL;
    }

    protected void dropDeathLoot(boolean flag, int i) {}

    public boolean z_() {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.getBoundingBox().minY);
        int k = MathHelper.floor(this.locZ);

        if (this instanceof EntityHuman && ((EntityHuman) this).isSpectator()) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(i, j, k);
            IBlockData iblockdata = this.world.getType(blockposition);
            Block block = iblockdata.getBlock();

            return block != Blocks.LADDER && block != Blocks.VINE ? block instanceof BlockTrapdoor && this.b(blockposition, iblockdata) : true;
        }
    }

    private boolean b(BlockPosition blockposition, IBlockData iblockdata) {
        if ((Boolean) iblockdata.get(BlockTrapdoor.OPEN)) {
            IBlockData iblockdata1 = this.world.getType(blockposition.down());

            if (iblockdata1.getBlock() == Blocks.LADDER && iblockdata1.get(BlockLadder.FACING) == iblockdata.get(BlockTrapdoor.FACING)) {
                return true;
            }
        }

        return false;
    }

    public boolean isAlive() {
        return !this.dead && this.getHealth() > 0.0F;
    }

    public void c(float f, float f1) {
        super.c(f, f1);
        MobEffect mobeffect = this.getEffect(MobEffects.JUMP);
        float f2 = mobeffect == null ? 0.0F : (float) (mobeffect.getAmplifier() + 1);
        int i = MathHelper.f((f - 3.0F - f2) * f1);

        if (i > 0) {
            this.a(this.m(i), 1.0F, 1.0F);
            this.damageEntity(DamageSource.FALL, (float) i);
            int j = MathHelper.floor(this.locX);
            int k = MathHelper.floor(this.locY - 0.20000000298023224D);
            int l = MathHelper.floor(this.locZ);
            IBlockData iblockdata = this.world.getType(new BlockPosition(j, k, l));

            if (!iblockdata.isAir()) {
                SoundEffectType soundeffecttype = iblockdata.getBlock().getStepSound();

                this.a(soundeffecttype.g(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }
        }

    }

    public int getArmorStrength() {
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.h);

        return MathHelper.floor(attributeinstance.getValue());
    }

    protected void damageArmor(float f) {}

    protected void damageShield(float f) {}

    protected float applyArmorModifier(DamageSource damagesource, float f) {
        if (!damagesource.ignoresArmor()) {
            this.damageArmor(f);
            f = CombatMath.a(f, (float) this.getArmorStrength(), (float) this.getAttributeInstance(GenericAttributes.i).getValue());
        }

        return f;
    }

    protected float applyMagicModifier(DamageSource damagesource, float f) {
        if (damagesource.isStarvation()) {
            return f;
        } else {
            int i;

            if (this.hasEffect(MobEffects.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
                i = (this.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                int j = 25 - i;
                float f1 = f * (float) j;
                float f2 = f;

                f = Math.max(f1 / 25.0F, 0.0F);
                float f3 = f2 - f;

                if (f3 > 0.0F && f3 < 3.4028235E37F) {
                    if (this instanceof EntityPlayer) {
                        ((EntityPlayer) this).a(StatisticList.DAMAGE_RESISTED, Math.round(f3 * 10.0F));
                    } else if (damagesource.getEntity() instanceof EntityPlayer) {
                        ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_RESISTED, Math.round(f3 * 10.0F));
                    }
                }
            }

            if (f <= 0.0F) {
                return 0.0F;
            } else {
                i = EnchantmentManager.a(this.getArmorItems(), damagesource);
                if (i > 0) {
                    f = CombatMath.a(f, (float) i);
                }

                return f;
            }
        }
    }

    protected void damageEntity0(DamageSource damagesource, float f) {
        if (!this.isInvulnerable(damagesource)) {
            f = this.applyArmorModifier(damagesource, f);
            f = this.applyMagicModifier(damagesource, f);
            float f1 = f;

            f = Math.max(f - this.getAbsorptionHearts(), 0.0F);
            this.setAbsorptionHearts(this.getAbsorptionHearts() - (f1 - f));
            float f2 = f1 - f;

            if (f2 > 0.0F && f2 < 3.4028235E37F && damagesource.getEntity() instanceof EntityPlayer) {
                ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f != 0.0F) {
                float f3 = this.getHealth();

                this.setHealth(f3 - f);
                this.getCombatTracker().trackDamage(damagesource, f3, f);
                this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
            }
        }
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public EntityLiving cv() {
        return (EntityLiving) (this.combatTracker.c() != null ? this.combatTracker.c() : (this.killer != null ? this.killer : (this.lastDamager != null ? this.lastDamager : null)));
    }

    public final float getMaxHealth() {
        return (float) this.getAttributeInstance(GenericAttributes.maxHealth).getValue();
    }

    public final int getArrowCount() {
        return (Integer) this.datawatcher.get(EntityLiving.bx);
    }

    public final void setArrowCount(int i) {
        this.datawatcher.set(EntityLiving.bx, i);
    }

    private int l() {
        return MobEffectUtil.a(this) ? 6 - (1 + MobEffectUtil.b(this)) : (this.hasEffect(MobEffects.SLOWER_DIG) ? 6 + (1 + this.getEffect(MobEffects.SLOWER_DIG).getAmplifier()) * 2 : 6);
    }

    public void a(EnumHand enumhand) {
        if (!this.ax || this.az >= this.l() / 2 || this.az < 0) {
            this.az = -1;
            this.ax = true;
            this.ay = enumhand;
            if (this.world instanceof WorldServer) {
                ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutAnimation(this, enumhand == EnumHand.MAIN_HAND ? 0 : 3)));
            }
        }

    }

    protected void aa() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void cy() {
        int i = this.l();

        if (this.ax) {
            ++this.az;
            if (this.az >= i) {
                this.az = 0;
                this.ax = false;
            }
        } else {
            this.az = 0;
        }

        this.aG = (float) this.az / (float) i;
    }

    public AttributeInstance getAttributeInstance(IAttribute iattribute) {
        return this.getAttributeMap().a(iattribute);
    }

    public AttributeMapBase getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new AttributeMapServer();
        }

        return this.attributeMap;
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEFINED;
    }

    public ItemStack getItemInMainHand() {
        return this.getEquipment(EnumItemSlot.MAINHAND);
    }

    public ItemStack getItemInOffHand() {
        return this.getEquipment(EnumItemSlot.OFFHAND);
    }

    public ItemStack b(EnumHand enumhand) {
        if (enumhand == EnumHand.MAIN_HAND) {
            return this.getEquipment(EnumItemSlot.MAINHAND);
        } else if (enumhand == EnumHand.OFF_HAND) {
            return this.getEquipment(EnumItemSlot.OFFHAND);
        } else {
            throw new IllegalArgumentException("Invalid hand " + enumhand);
        }
    }

    public void a(EnumHand enumhand, ItemStack itemstack) {
        if (enumhand == EnumHand.MAIN_HAND) {
            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
        } else {
            if (enumhand != EnumHand.OFF_HAND) {
                throw new IllegalArgumentException("Invalid hand " + enumhand);
            }

            this.setSlot(EnumItemSlot.OFFHAND, itemstack);
        }

    }

    public boolean a(EnumItemSlot enumitemslot) {
        return !this.getEquipment(enumitemslot).isEmpty();
    }

    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquipment(EnumItemSlot enumitemslot);

    public abstract void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack);

    public void setSprinting(boolean flag) {
        super.setSprinting(flag);
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributeinstance.a(EntityLiving.b) != null) {
            attributeinstance.c(EntityLiving.c);
        }

        if (flag) {
            attributeinstance.b(EntityLiving.c);
        }

    }

    protected float cD() {
        return 1.0F;
    }

    protected float cE() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isFrozen() {
        return this.getHealth() <= 0.0F;
    }

    public void A(Entity entity) {
        double d0;

        if (!(entity instanceof EntityBoat) && !(entity instanceof EntityHorseAbstract)) {
            double d1 = entity.locX;
            double d2 = entity.getBoundingBox().minY + (double) entity.length;

            d0 = entity.locZ;
            EnumDirection enumdirection = entity.getAdjustedDirection();

            if (enumdirection != null) {
                EnumDirection enumdirection1 = enumdirection.e();
                int[][] aint = new int[][] { { 0, 1}, { 0, -1}, { -1, 1}, { -1, -1}, { 1, 1}, { 1, -1}, { -1, 0}, { 1, 0}, { 0, 1}};
                double d3 = Math.floor(this.locX) + 0.5D;
                double d4 = Math.floor(this.locZ) + 0.5D;
                double d5 = this.getBoundingBox().maxX - this.getBoundingBox().minX;
                double d6 = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
                AxisAlignedBB axisalignedbb = new AxisAlignedBB(d3 - d5 / 2.0D, entity.getBoundingBox().minY, d4 - d6 / 2.0D, d3 + d5 / 2.0D, Math.floor(entity.getBoundingBox().minY) + (double) this.length, d4 + d6 / 2.0D);
                int[][] aint1 = aint;
                int i = aint.length;

                for (int j = 0; j < i; ++j) {
                    int[] aint2 = aint1[j];
                    double d7 = (double) (enumdirection.getAdjacentX() * aint2[0] + enumdirection1.getAdjacentX() * aint2[1]);
                    double d8 = (double) (enumdirection.getAdjacentZ() * aint2[0] + enumdirection1.getAdjacentZ() * aint2[1]);
                    double d9 = d3 + d7;
                    double d10 = d4 + d8;
                    AxisAlignedBB axisalignedbb1 = axisalignedbb.d(d7, 0.0D, d8);

                    if (this.world.getCubes(this, axisalignedbb1)) {
                        if (this.world.getType(new BlockPosition(d9, this.locY, d10)).q()) {
                            this.enderTeleportTo(d9, this.locY + 1.0D, d10);
                            return;
                        }

                        BlockPosition blockposition = new BlockPosition(d9, this.locY - 1.0D, d10);

                        if (this.world.getType(blockposition).q() || this.world.getFluid(blockposition).a(TagsFluid.WATER)) {
                            d1 = d9;
                            d2 = this.locY + 1.0D;
                            d0 = d10;
                        }
                    } else if (this.world.getCubes(this, axisalignedbb1.d(0.0D, 1.0D, 0.0D)) && this.world.getType(new BlockPosition(d9, this.locY + 1.0D, d10)).q()) {
                        d1 = d9;
                        d2 = this.locY + 2.0D;
                        d0 = d10;
                    }
                }
            }

            this.enderTeleportTo(d1, d2, d0);
        } else {
            double d11 = (double) (this.width / 2.0F + entity.width / 2.0F) + 0.4D;
            float f;

            if (entity instanceof EntityBoat) {
                f = 0.0F;
            } else {
                f = 1.5707964F * (float) (this.getMainHand() == EnumMainHand.RIGHT ? -1 : 1);
            }

            float f1 = -MathHelper.sin(-this.yaw * 0.017453292F - 3.1415927F + f);
            float f2 = -MathHelper.cos(-this.yaw * 0.017453292F - 3.1415927F + f);

            d0 = Math.abs(f1) > Math.abs(f2) ? d11 / (double) Math.abs(f1) : d11 / (double) Math.abs(f2);
            double d12 = this.locX + (double) f1 * d0;
            double d13 = this.locZ + (double) f2 * d0;

            this.setPosition(d12, entity.locY + (double) entity.length + 0.001D, d13);
            if (!this.world.getCubes(this, this.getBoundingBox().b(entity.getBoundingBox()))) {
                this.setPosition(d12, entity.locY + (double) entity.length + 1.001D, d13);
                if (!this.world.getCubes(this, this.getBoundingBox().b(entity.getBoundingBox()))) {
                    this.setPosition(entity.locX, entity.locY + (double) this.length + 0.001D, entity.locZ);
                }
            }
        }
    }

    protected float cG() {
        return 0.42F;
    }

    protected void cH() {
        this.motY = (double) this.cG();
        if (this.hasEffect(MobEffects.JUMP)) {
            this.motY += (double) ((float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
        }

        if (this.isSprinting()) {
            float f = this.yaw * 0.017453292F;

            this.motX -= (double) (MathHelper.sin(f) * 0.2F);
            this.motZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        this.impulse = true;
    }

    protected void c(Tag<FluidType> tag) {
        this.motY += 0.03999999910593033D;
    }

    protected float cJ() {
        return 0.8F;
    }

    public void a(float f, float f1, float f2) {
        double d0;
        double d1;
        float f3;
        double d2;

        if (this.cP() || this.bT()) {
            d0 = 0.08D;
            if (this.motY <= 0.0D && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }

            float f4;

            if (this.isInWater() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d1 = this.locY;
                float f5 = this.isSprinting() ? 0.9F : this.cJ();

                f4 = 0.02F;
                f3 = (float) EnchantmentManager.e(this);
                if (f3 > 3.0F) {
                    f3 = 3.0F;
                }

                if (!this.onGround) {
                    f3 *= 0.5F;
                }

                if (f3 > 0.0F) {
                    f5 += (0.54600006F - f5) * f3 / 3.0F;
                    f4 += (this.cK() - f4) * f3 / 3.0F;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f5 = 0.96F;
                }

                this.a(f, f1, f2, f4);
                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                this.motX *= (double) f5;
                this.motY *= 0.800000011920929D;
                this.motZ *= (double) f5;
                if (!this.isNoGravity() && !this.isSprinting()) {
                    if (this.motY <= 0.0D && Math.abs(this.motY - 0.005D) >= 0.003D && Math.abs(this.motY - d0 / 16.0D) < 0.003D) {
                        this.motY = -0.003D;
                    } else {
                        this.motY -= d0 / 16.0D;
                    }
                }

                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d1, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else if (this.ax() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d1 = this.locY;
                this.a(f, f1, f2, 0.02F);
                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                this.motX *= 0.5D;
                this.motY *= 0.5D;
                this.motZ *= 0.5D;
                if (!this.isNoGravity()) {
                    this.motY -= d0 / 4.0D;
                }

                if (this.positionChanged && this.c(this.motX, this.motY + 0.6000000238418579D - this.locY + d1, this.motZ)) {
                    this.motY = 0.30000001192092896D;
                }
            } else if (this.dc()) {
                if (this.motY > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3D vec3d = this.aN();
                float f6 = this.pitch * 0.017453292F;

                d2 = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
                double d3 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                double d4 = vec3d.b();
                float f7 = MathHelper.cos(f6);

                f7 = (float) ((double) f7 * (double) f7 * Math.min(1.0D, d4 / 0.4D));
                this.motY += d0 * (-1.0D + (double) f7 * 0.75D);
                double d5;

                if (this.motY < 0.0D && d2 > 0.0D) {
                    d5 = this.motY * -0.1D * (double) f7;
                    this.motY += d5;
                    this.motX += vec3d.x * d5 / d2;
                    this.motZ += vec3d.z * d5 / d2;
                }

                if (f6 < 0.0F && d2 > 0.0D) {
                    d5 = d3 * (double) (-MathHelper.sin(f6)) * 0.04D;
                    this.motY += d5 * 3.2D;
                    this.motX -= vec3d.x * d5 / d2;
                    this.motZ -= vec3d.z * d5 / d2;
                }

                if (d2 > 0.0D) {
                    this.motX += (vec3d.x / d2 * d3 - this.motX) * 0.1D;
                    this.motZ += (vec3d.z / d2 * d3 - this.motZ) * 0.1D;
                }

                this.motX *= 0.9900000095367432D;
                this.motY *= 0.9800000190734863D;
                this.motZ *= 0.9900000095367432D;
                this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                if (this.positionChanged && !this.world.isClientSide) {
                    d5 = Math.sqrt(this.motX * this.motX + this.motZ * this.motZ);
                    double d6 = d3 - d5;
                    float f8 = (float) (d6 * 10.0D - 3.0D);

                    if (f8 > 0.0F) {
                        this.a(this.m((int) f8), 1.0F, 1.0F);
                        this.damageEntity(DamageSource.FLY_INTO_WALL, f8);
                    }
                }

                if (this.onGround && !this.world.isClientSide) {
                    this.setFlag(7, false);
                }
            } else {
                float f9 = 0.91F;
                BlockPosition.b blockposition_b = BlockPosition.b.d(this.locX, this.getBoundingBox().minY - 1.0D, this.locZ);
                Throwable throwable = null;

                try {
                    if (this.onGround) {
                        f9 = this.world.getType(blockposition_b).getBlock().n() * 0.91F;
                    }

                    f4 = 0.16277137F / (f9 * f9 * f9);
                    if (this.onGround) {
                        f3 = this.cK() * f4;
                    } else {
                        f3 = this.aU;
                    }

                    this.a(f, f1, f2, f3);
                    f9 = 0.91F;
                    if (this.onGround) {
                        f9 = this.world.getType(blockposition_b.c(this.locX, this.getBoundingBox().minY - 1.0D, this.locZ)).getBlock().n() * 0.91F;
                    }

                    if (this.z_()) {
                        float f10 = 0.15F;

                        this.motX = MathHelper.a(this.motX, -0.15000000596046448D, 0.15000000596046448D);
                        this.motZ = MathHelper.a(this.motZ, -0.15000000596046448D, 0.15000000596046448D);
                        this.fallDistance = 0.0F;
                        if (this.motY < -0.15D) {
                            this.motY = -0.15D;
                        }

                        boolean flag = this.isSneaking() && this instanceof EntityHuman;

                        if (flag && this.motY < 0.0D) {
                            this.motY = 0.0D;
                        }
                    }

                    this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                    if (this.positionChanged && this.z_()) {
                        this.motY = 0.2D;
                    }

                    if (this.hasEffect(MobEffects.LEVITATION)) {
                        this.motY += (0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - this.motY) * 0.2D;
                        this.fallDistance = 0.0F;
                    } else {
                        blockposition_b.c(this.locX, 0.0D, this.locZ);
                        if (this.world.isClientSide && (!this.world.isLoaded(blockposition_b) || !this.world.getChunkAtWorldCoords(blockposition_b).y())) {
                            if (this.locY > 0.0D) {
                                this.motY = -0.1D;
                            } else {
                                this.motY = 0.0D;
                            }
                        } else if (!this.isNoGravity()) {
                            this.motY -= d0;
                        }
                    }

                    this.motY *= 0.9800000190734863D;
                    this.motX *= (double) f9;
                    this.motZ *= (double) f9;
                } catch (Throwable throwable1) {
                    throwable = throwable1;
                    throw throwable1;
                } finally {
                    if (blockposition_b != null) {
                        if (throwable != null) {
                            try {
                                blockposition_b.close();
                            } catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        } else {
                            blockposition_b.close();
                        }
                    }

                }
            }
        }

        this.aI = this.aJ;
        d0 = this.locX - this.lastX;
        d1 = this.locZ - this.lastZ;
        d2 = this instanceof EntityBird ? this.locY - this.lastY : 0.0D;
        f3 = MathHelper.sqrt(d0 * d0 + d2 * d2 + d1 * d1) * 4.0F;
        if (f3 > 1.0F) {
            f3 = 1.0F;
        }

        this.aJ += (f3 - this.aJ) * 0.4F;
        this.aK += this.aJ;
    }

    public float cK() {
        return this.bI;
    }

    public void o(float f) {
        this.bI = f;
    }

    public boolean B(Entity entity) {
        this.z(entity);
        return false;
    }

    public boolean isSleeping() {
        return false;
    }

    public void tick() {
        super.tick();
        this.cV();
        this.o();
        if (!this.world.isClientSide) {
            int i = this.getArrowCount();

            if (i > 0) {
                if (this.aA <= 0) {
                    this.aA = 20 * (30 - i);
                }

                --this.aA;
                if (this.aA <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
            int j = aenumitemslot.length;

            for (int k = 0; k < j; ++k) {
                EnumItemSlot enumitemslot = aenumitemslot[k];
                ItemStack itemstack;

                switch (enumitemslot.a()) {
                case HAND:
                    itemstack = (ItemStack) this.bB.get(enumitemslot.b());
                    break;
                case ARMOR:
                    itemstack = (ItemStack) this.bC.get(enumitemslot.b());
                    break;
                default:
                    continue;
                }

                ItemStack itemstack1 = this.getEquipment(enumitemslot);

                if (!ItemStack.matches(itemstack1, itemstack)) {
                    ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new PacketPlayOutEntityEquipment(this.getId(), enumitemslot, itemstack1)));
                    if (!itemstack.isEmpty()) {
                        this.getAttributeMap().a(itemstack.a(enumitemslot));
                    }

                    if (!itemstack1.isEmpty()) {
                        this.getAttributeMap().b(itemstack1.a(enumitemslot));
                    }

                    switch (enumitemslot.a()) {
                    case HAND:
                        this.bB.set(enumitemslot.b(), itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack());
                        break;
                    case ARMOR:
                        this.bC.set(enumitemslot.b(), itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack());
                    }
                }
            }

            if (this.ticksLived % 20 == 0) {
                this.getCombatTracker().g();
            }

            if (!this.glowing) {
                boolean flag = this.hasEffect(MobEffects.GLOWING);

                if (this.getFlag(6) != flag) {
                    this.setFlag(6, flag);
                }
            }
        }

        this.movementTick();
        double d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.aQ;
        float f2 = 0.0F;

        this.aZ = this.ba;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) MathHelper.c(d1, d0) * 57.295776F - 90.0F;
            float f5 = MathHelper.e(MathHelper.g(this.yaw) - f4);

            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.aG > 0.0F) {
            f1 = this.yaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.ba += (f3 - this.ba) * 0.3F;
        this.world.methodProfiler.enter("headTurn");
        f2 = this.e(f1, f2);
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("rangeChecks");

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        while (this.aQ - this.aR < -180.0F) {
            this.aR -= 360.0F;
        }

        while (this.aQ - this.aR >= 180.0F) {
            this.aR += 360.0F;
        }

        while (this.pitch - this.lastPitch < -180.0F) {
            this.lastPitch -= 360.0F;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.aS - this.aT < -180.0F) {
            this.aT -= 360.0F;
        }

        while (this.aS - this.aT >= 180.0F) {
            this.aT += 360.0F;
        }

        this.world.methodProfiler.exit();
        this.bb += f2;
        if (this.dc()) {
            ++this.bv;
        } else {
            this.bv = 0;
        }

    }

    protected float e(float f, float f1) {
        float f2 = MathHelper.g(f - this.aQ);

        this.aQ += f2 * 0.3F;
        float f3 = MathHelper.g(this.yaw - this.aQ);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.aQ = this.yaw - f3;
        if (f3 * f3 > 2500.0F) {
            this.aQ += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void movementTick() {
        if (this.bJ > 0) {
            --this.bJ;
        }

        if (this.bl > 0 && !this.bT()) {
            double d0 = this.locX + (this.bm - this.locX) / (double) this.bl;
            double d1 = this.locY + (this.bn - this.locY) / (double) this.bl;
            double d2 = this.locZ + (this.bo - this.locZ) / (double) this.bl;
            double d3 = MathHelper.g(this.bp - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.bl);
            this.pitch = (float) ((double) this.pitch + (this.bq - (double) this.pitch) / (double) this.bl);
            --this.bl;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else if (!this.cP()) {
            this.motX *= 0.98D;
            this.motY *= 0.98D;
            this.motZ *= 0.98D;
        }

        if (this.bs > 0) {
            this.aS = (float) ((double) this.aS + MathHelper.g(this.br - (double) this.aS) / (double) this.bs);
            --this.bs;
        }

        if (Math.abs(this.motX) < 0.003D) {
            this.motX = 0.0D;
        }

        if (Math.abs(this.motY) < 0.003D) {
            this.motY = 0.0D;
        }

        if (Math.abs(this.motZ) < 0.003D) {
            this.motZ = 0.0D;
        }

        this.world.methodProfiler.enter("ai");
        if (this.isFrozen()) {
            this.bg = false;
            this.bh = 0.0F;
            this.bj = 0.0F;
            this.bk = 0.0F;
        } else if (this.cP()) {
            this.world.methodProfiler.enter("newAi");
            this.doTick();
            this.world.methodProfiler.exit();
        }

        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("jump");
        if (this.bg) {
            if (this.W > 0.0D && (!this.onGround || this.W > 0.4D)) {
                this.c(TagsFluid.WATER);
            } else if (this.ax()) {
                this.c(TagsFluid.LAVA);
            } else if ((this.onGround || this.W > 0.0D && this.W <= 0.4D) && this.bJ == 0) {
                this.cH();
                this.bJ = 10;
            }
        } else {
            this.bJ = 0;
        }

        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("travel");
        this.bh *= 0.98F;
        this.bj *= 0.98F;
        this.bk *= 0.9F;
        this.n();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        this.a(this.bh, this.bi, this.bj);
        this.world.methodProfiler.exit();
        this.world.methodProfiler.enter("push");
        if (this.bw > 0) {
            --this.bw;
            this.a(axisalignedbb, this.getBoundingBox());
        }

        this.cN();
        this.world.methodProfiler.exit();
    }

    private void n() {
        boolean flag = this.getFlag(7);

        if (flag && !this.onGround && !this.isPassenger()) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.CHEST);

            if (itemstack.getItem() == Items.ELYTRA && ItemElytra.e(itemstack)) {
                flag = true;
                if (!this.world.isClientSide && (this.bv + 1) % 20 == 0) {
                    itemstack.damage(1, this);
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.world.isClientSide) {
            this.setFlag(7, flag);
        }

    }

    protected void doTick() {}

    protected void cN() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            int i = this.world.getGameRules().c("maxEntityCramming");
            int j;

            if (i > 0 && list.size() > i - 1 && this.random.nextInt(4) == 0) {
                j = 0;

                for (int k = 0; k < list.size(); ++k) {
                    if (!((Entity) list.get(k)).isPassenger()) {
                        ++j;
                    }
                }

                if (j > i - 1) {
                    this.damageEntity(DamageSource.CRAMMING, 6.0F);
                }
            }

            for (j = 0; j < list.size(); ++j) {
                Entity entity = (Entity) list.get(j);

                this.C(entity);
            }
        }

    }

    protected void a(AxisAlignedBB axisalignedbb, AxisAlignedBB axisalignedbb1) {
        AxisAlignedBB axisalignedbb2 = axisalignedbb.b(axisalignedbb1);
        List<Entity> list = this.world.getEntities(this, axisalignedbb2);

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                Entity entity = (Entity) list.get(i);

                if (entity instanceof EntityLiving) {
                    this.d((EntityLiving) entity);
                    this.bw = 0;
                    this.motX *= -0.2D;
                    this.motY *= -0.2D;
                    this.motZ *= -0.2D;
                    break;
                }
            }
        } else if (this.positionChanged) {
            this.bw = 0;
        }

        if (!this.world.isClientSide && this.bw <= 0) {
            this.c(4, false);
        }

    }

    protected void C(Entity entity) {
        entity.collide(this);
    }

    protected void d(EntityLiving entityliving) {}

    public void o(int i) {
        this.bw = i;
        if (!this.world.isClientSide) {
            this.c(4, true);
        }

    }

    public boolean isRiptiding() {
        return ((Byte) this.datawatcher.get(EntityLiving.aw) & 4) != 0;
    }

    public void stopRiding() {
        Entity entity = this.getVehicle();

        super.stopRiding();
        if (entity != null && entity != this.getVehicle() && !this.world.isClientSide) {
            this.A(entity);
        }

    }

    public void aH() {
        super.aH();
        this.aZ = this.ba;
        this.ba = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void o(boolean flag) {
        this.bg = flag;
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead && !this.world.isClientSide) {
            EntityTracker entitytracker = ((WorldServer) this.world).getTracker();

            if (entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityExperienceOrb) {
                entitytracker.a(entity, (Packet) (new PacketPlayOutCollect(entity.getId(), this.getId(), i)));
            }
        }

    }

    public boolean hasLineOfSight(Entity entity) {
        return this.world.rayTrace(new Vec3D(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ), new Vec3D(entity.locX, entity.locY + (double) entity.getHeadHeight(), entity.locZ), FluidCollisionOption.NEVER, true, false) == null;
    }

    public float h(float f) {
        return f == 1.0F ? this.aS : this.aT + (this.aS - this.aT) * f;
    }

    public boolean cP() {
        return !this.world.isClientSide;
    }

    public boolean isInteractable() {
        return !this.dead;
    }

    public boolean isCollidable() {
        return this.isAlive() && !this.z_();
    }

    protected void aA() {
        this.velocityChanged = this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.c).getValue();
    }

    public float getHeadRotation() {
        return this.aS;
    }

    public void setHeadRotation(float f) {
        this.aS = f;
    }

    public void k(float f) {
        this.aQ = f;
    }

    public float getAbsorptionHearts() {
        return this.bK;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F) {
            f = 0.0F;
        }

        this.bK = f;
    }

    public void enterCombat() {}

    public void exitCombat() {}

    protected void cR() {
        this.updateEffects = true;
    }

    public abstract EnumMainHand getMainHand();

    public boolean isHandRaised() {
        return ((Byte) this.datawatcher.get(EntityLiving.aw) & 1) > 0;
    }

    public EnumHand cU() {
        return ((Byte) this.datawatcher.get(EntityLiving.aw) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    protected void cV() {
        if (this.isHandRaised()) {
            if (this.b(this.cU()) == this.activeItem) {
                if (this.cX() <= 25 && this.cX() % 4 == 0) {
                    this.b(this.activeItem, 5);
                }

                if (--this.bu == 0 && !this.world.isClientSide) {
                    this.q();
                }
            } else {
                this.da();
            }
        }

    }

    private void o() {
        this.bP = this.bO;
        if (this.isSwimming()) {
            this.bO = Math.min(1.0F, this.bO + 0.09F);
        } else {
            this.bO = Math.max(0.0F, this.bO - 0.09F);
        }

    }

    protected void c(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityLiving.aw);
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.datawatcher.set(EntityLiving.aw, (byte) j);
    }

    public void c(EnumHand enumhand) {
        ItemStack itemstack = this.b(enumhand);

        if (!itemstack.isEmpty() && !this.isHandRaised()) {
            this.activeItem = itemstack;
            this.bu = itemstack.k();
            if (!this.world.isClientSide) {
                this.c(1, true);
                this.c(2, enumhand == EnumHand.OFF_HAND);
            }

        }
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityLiving.aw.equals(datawatcherobject) && this.world.isClientSide) {
            if (this.isHandRaised() && this.activeItem.isEmpty()) {
                this.activeItem = this.b(this.cU());
                if (!this.activeItem.isEmpty()) {
                    this.bu = this.activeItem.k();
                }
            } else if (!this.isHandRaised() && !this.activeItem.isEmpty()) {
                this.activeItem = ItemStack.a;
                this.bu = 0;
            }
        }

    }

    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        super.a(argumentanchor_anchor, vec3d);
        this.aT = this.aS;
        this.aQ = this.aS;
        this.aR = this.aQ;
    }

    protected void b(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty() && this.isHandRaised()) {
            if (itemstack.l() == EnumAnimation.DRINK) {
                this.a(SoundEffects.ENTITY_GENERIC_DRINK, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (itemstack.l() == EnumAnimation.EAT) {
                this.a(itemstack, i);
                this.a(SoundEffects.ENTITY_GENERIC_EAT, 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

        }
    }

    private void a(ItemStack itemstack, int i) {
        for (int j = 0; j < i; ++j) {
            Vec3D vec3d = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

            vec3d = vec3d.a(-this.pitch * 0.017453292F);
            vec3d = vec3d.b(-this.yaw * 0.017453292F);
            double d0 = (double) (-this.random.nextFloat()) * 0.6D - 0.3D;
            Vec3D vec3d1 = new Vec3D(((double) this.random.nextFloat() - 0.5D) * 0.3D, d0, 0.6D);

            vec3d1 = vec3d1.a(-this.pitch * 0.017453292F);
            vec3d1 = vec3d1.b(-this.yaw * 0.017453292F);
            vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
            this.world.addParticle(new ParticleParamItem(Particles.C, itemstack), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
        }

    }

    protected void q() {
        if (!this.activeItem.isEmpty() && this.isHandRaised()) {
            this.b(this.activeItem, 16);
            this.a(this.cU(), this.activeItem.a(this.world, this));
            this.da();
        }

    }

    public ItemStack cW() {
        return this.activeItem;
    }

    public int cX() {
        return this.bu;
    }

    public int cY() {
        return this.isHandRaised() ? this.activeItem.k() - this.cX() : 0;
    }

    public void clearActiveItem() {
        if (!this.activeItem.isEmpty()) {
            this.activeItem.a(this.world, this, this.cX());
        }

        this.da();
    }

    public void da() {
        if (!this.world.isClientSide) {
            this.c(1, false);
        }

        this.activeItem = ItemStack.a;
        this.bu = 0;
    }

    public boolean isBlocking() {
        if (this.isHandRaised() && !this.activeItem.isEmpty()) {
            Item item = this.activeItem.getItem();

            return item.d(this.activeItem) != EnumAnimation.BLOCK ? false : item.c(this.activeItem) - this.bu >= 5;
        } else {
            return false;
        }
    }

    public boolean dc() {
        return this.getFlag(7);
    }

    public boolean j(double d0, double d1, double d2) {
        double d3 = this.locX;
        double d4 = this.locY;
        double d5 = this.locZ;

        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        boolean flag = false;
        BlockPosition blockposition = new BlockPosition(this);
        World world = this.world;
        Random random = this.getRandom();
        boolean flag1;

        if (world.isLoaded(blockposition)) {
            flag1 = false;

            while (!flag1 && blockposition.getY() > 0) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getMaterial().isSolid()) {
                    flag1 = true;
                } else {
                    --this.locY;
                    blockposition = blockposition1;
                }
            }

            if (flag1) {
                this.enderTeleportTo(this.locX, this.locY, this.locZ);
                if (world.getCubes(this, this.getBoundingBox()) && !world.containsLiquid(this.getBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            this.enderTeleportTo(d3, d4, d5);
            return false;
        } else {
            flag1 = true;

            for (int i = 0; i < 128; ++i) {
                double d6 = (double) i / 127.0D;
                float f = (random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (random.nextFloat() - 0.5F) * 0.2F;
                double d7 = d3 + (this.locX - d3) * d6 + (random.nextDouble() - 0.5D) * (double) this.width * 2.0D;
                double d8 = d4 + (this.locY - d4) * d6 + random.nextDouble() * (double) this.length;
                double d9 = d5 + (this.locZ - d5) * d6 + (random.nextDouble() - 0.5D) * (double) this.width * 2.0D;

                world.addParticle(Particles.K, d7, d8, d9, (double) f, (double) f1, (double) f2);
            }

            if (this instanceof EntityCreature) {
                ((EntityCreature) this).getNavigation().q();
            }

            return true;
        }
    }

    public boolean de() {
        return true;
    }

    public boolean df() {
        return true;
    }
}
