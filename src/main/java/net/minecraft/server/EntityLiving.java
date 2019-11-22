package net.minecraft.server;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

// CraftBukkit start
import java.util.ArrayList;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.craftbukkit.attribute.CraftAttributeMap;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageModifier;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityResurrectEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
// CraftBukkit end

import co.aikar.timings.MinecraftTimings; // Paper

public abstract class EntityLiving extends Entity {

    private static final UUID b = UUID.fromString("662A6B8D-DA3E-4C1C-8813-96EA6097278D");
    private static final AttributeModifier c = (new AttributeModifier(EntityLiving.b, "Sprinting speed boost", 0.30000001192092896D, AttributeModifier.Operation.MULTIPLY_TOTAL)).a(false);
    protected static final DataWatcherObject<Byte> ar = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.a);
    public static final DataWatcherObject<Float> HEALTH = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> e = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> f = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> g = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Optional<BlockPosition>> bs = DataWatcher.a(EntityLiving.class, DataWatcherRegistry.m);
    protected static final EntitySize as = EntitySize.c(0.2F, 0.2F);
    private AttributeMapBase attributeMap;
    public CombatTracker combatTracker = new CombatTracker(this);
    public final Map<MobEffectList, MobEffect> effects = Maps.newHashMap();
    private final NonNullList<ItemStack> bw;
    private final NonNullList<ItemStack> bx;
    public boolean at;
    public EnumHand au;
    public int av;
    public int aw;
    public int hurtTicks;
    public int hurtDuration;
    public float az;
    public int deathTicks;
    public float aB;
    public float aC;
    protected int aD;
    public float aE;
    public float aF;
    public float aG;
    public int maxNoDamageTicks;
    public final float aI;
    public final float aJ;
    public float aK;
    public float aL;
    public float aM;
    public float aN;
    public float aO;
    public EntityHuman killer;
    public int lastDamageByPlayerTime; // Paper - protected -> public
    protected boolean killed;
    protected int ticksFarFromPlayer;
    protected float aT;
    protected float aU;
    protected float aV;
    protected float aW;
    protected float aX;
    protected int aY; protected int getKillCount() { return this.aY; } // Paper - OBFHELPER
    public float lastDamage;
    protected boolean jumping;
    public float bb;
    public float bc;
    public float bd;
    public float be;
    protected int bf;
    protected double bg;
    protected double bh;
    protected double bi;
    protected double bj;
    protected double bk;
    protected double bl;
    protected int bm;
    public boolean updateEffects;
    @Nullable
    public EntityLiving lastDamager;
    public int hurtTimestamp;
    private EntityLiving bB;
    private int bC;
    private float bD;
    private int jumpTicks;
    private float bF;
    public ItemStack activeItem; // Paper - public
    protected int bo;
    protected int bp;
    private BlockPosition bG;
    private DamageSource bH;
    private long bI;
    protected int bq;
    private float bJ;
    private float bK;
    protected BehaviorController<?> br;
    // CraftBukkit start
    public int expToDrop;
    public int maxAirTicks = 300;
    boolean forceDrops;
    ArrayList<org.bukkit.inventory.ItemStack> drops = new ArrayList<org.bukkit.inventory.ItemStack>();
    public org.bukkit.craftbukkit.attribute.CraftAttributeMap craftAttributes;
    public boolean collides = true;
    public boolean canPickUpLoot;
    public org.bukkit.craftbukkit.entity.CraftLivingEntity getBukkitLivingEntity() { return (org.bukkit.craftbukkit.entity.CraftLivingEntity) super.getBukkitEntity(); } // Paper
    public boolean silentDeath = false; // Paper - mark entity as dying silently for cancellable death event

    @Override
    public float getBukkitYaw() {
        return getHeadRotation();
    }
    // CraftBukkit end
    // Spigot start
    public void inactiveTick()
    {
        super.inactiveTick();
        ++this.ticksFarFromPlayer; // Above all the floats
    }
    // Spigot end

    protected EntityLiving(EntityTypes<? extends EntityLiving> entitytypes, World world) {
        super(entitytypes, world);
        this.bw = NonNullList.a(2, ItemStack.a);
        this.bx = NonNullList.a(4, ItemStack.a);
        this.maxNoDamageTicks = 20;
        this.aO = 0.02F;
        this.updateEffects = true;
        this.activeItem = ItemStack.a;
        this.initAttributes();
        // CraftBukkit - setHealth(getMaxHealth()) inlined and simplified to skip the instanceof check for EntityPlayer, as getBukkitEntity() is not initialized in constructor
        this.datawatcher.set(EntityLiving.HEALTH, (float) this.getAttributeInstance(GenericAttributes.MAX_HEALTH).getValue());
        this.i = true;
        this.aJ = (float) ((Math.random() + 1.0D) * 0.009999999776482582D);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.aI = (float) Math.random() * 12398.0F;
        this.yaw = (float) (Math.random() * 6.2831854820251465D);
        this.aM = this.yaw;
        this.K = 0.6F;
        this.br = this.a(new Dynamic(DynamicOpsNBT.a, new NBTTagCompound()));
    }

    public BehaviorController<?> getBehaviorController() {
        return this.br;
    }

    protected BehaviorController<?> a(Dynamic<?> dynamic) {
        return new BehaviorController<>(ImmutableList.of(), ImmutableList.of(), dynamic);
    }

    @Override
    public void killEntity() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, Float.MAX_VALUE);
    }

    public boolean a(EntityTypes<?> entitytypes) {
        return true;
    }

    @Override
    protected void initDatawatcher() {
        this.datawatcher.register(EntityLiving.ar, (byte) 0);
        this.datawatcher.register(EntityLiving.e, 0);
        this.datawatcher.register(EntityLiving.f, false);
        this.datawatcher.register(EntityLiving.g, 0);
        this.datawatcher.register(EntityLiving.HEALTH, 1.0F);
        this.datawatcher.register(EntityLiving.bs, Optional.empty());
    }

    protected void initAttributes() {
        this.getAttributeMap().b(GenericAttributes.MAX_HEALTH);
        this.getAttributeMap().b(GenericAttributes.KNOCKBACK_RESISTANCE);
        this.getAttributeMap().b(GenericAttributes.MOVEMENT_SPEED);
        this.getAttributeMap().b(GenericAttributes.ARMOR);
        this.getAttributeMap().b(GenericAttributes.ARMOR_TOUGHNESS);
    }

    @Override
    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {
        if (!this.isInWater()) {
            this.ay();
        }

        if (!this.world.isClientSide && this.fallDistance > 3.0F && flag) {
            float f = (float) MathHelper.f(this.fallDistance - 3.0F);

            if (!iblockdata.isAir()) {
                double d1 = Math.min((double) (0.2F + f / 15.0F), 2.5D);
                int i = (int) (150.0D * d1);

                // CraftBukkit start - visiblity api
                if (this instanceof EntityPlayer) {
                    ((WorldServer) this.world).sendParticles((EntityPlayer) this, new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX, this.locY, this.locZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D, false);
                } else {
                    ((WorldServer) this.world).a(new ParticleParamBlock(Particles.BLOCK, iblockdata), this.locX, this.locY, this.locZ, i, 0.0D, 0.0D, 0.0D, 0.15000000596046448D);
                }
                // CraftBukkit end
            }
        }

        super.a(d0, flag, iblockdata, blockposition);
    }

    public boolean canBreatheUnderwater() { return this.cm(); } // Paper - OBFHELPER
    public boolean cm() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    @Override
    public void entityBaseTick() {
        this.aB = this.aC;
        if (this.justCreated) {
            this.getBedPosition().ifPresent(this::a);
        }

        super.entityBaseTick();
        this.world.getMethodProfiler().enter("livingEntityBaseTick");
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
                if (!this.canBreatheUnderwater() && !MobEffectUtil.c(this) && !flag1) {  // Paper - use OBFHELPER so it can be overridden
                    this.setAirTicks(this.l(this.getAirTicks()));
                    if (this.getAirTicks() == -20) {
                        this.setAirTicks(0);
                        Vec3D vec3d = this.getMot();

                        for (int i = 0; i < 8; ++i) {
                            float f = this.random.nextFloat() - this.random.nextFloat();
                            float f1 = this.random.nextFloat() - this.random.nextFloat();
                            float f2 = this.random.nextFloat() - this.random.nextFloat();

                            this.world.addParticle(Particles.BUBBLE, this.locX + (double) f, this.locY + (double) f1, this.locZ + (double) f2, vec3d.x, vec3d.y, vec3d.z);
                        }

                        this.damageEntity(DamageSource.DROWN, 2.0F);
                    }
                }

                if (!this.world.isClientSide && this.isPassenger() && this.getVehicle() != null && !this.getVehicle().bf()) {
                    this.stopRiding();
                }
            } else if (this.getAirTicks() < this.bp()) {
                this.setAirTicks(this.m(this.getAirTicks()));
            }

            if (!this.world.isClientSide) {
                BlockPosition blockposition = new BlockPosition(this);

                if (!Objects.equal(this.bG, blockposition)) {
                    this.bG = blockposition;
                    this.b(blockposition);
                }
            }
        }

        if (this.isAlive() && this.au()) {
            this.extinguish();
        }

        if (this.hurtTicks > 0) {
            --this.hurtTicks;
        }

        if (this.noDamageTicks > 0 && !(this instanceof EntityPlayer)) {
            --this.noDamageTicks;
        }

        if (this.getHealth() <= 0.0F) {
            this.co();
        }

        if (this.lastDamageByPlayerTime > 0) {
            --this.lastDamageByPlayerTime;
        } else {
            this.killer = null;
        }

        if (this.bB != null && !this.bB.isAlive()) {
            this.bB = null;
        }

        if (this.lastDamager != null) {
            if (!this.lastDamager.isAlive()) {
                this.setLastDamager((EntityLiving) null);
            } else if (this.ticksLived - this.hurtTimestamp > 100) {
                this.setLastDamager((EntityLiving) null);
            }
        }

        this.tickPotionEffects();
        this.aW = this.aV;
        this.aL = this.aK;
        this.aN = this.aM;
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
        this.world.getMethodProfiler().exit();
    }

    // CraftBukkit start
    public int getExpReward() {
        int exp = this.getExpValue(this.killer);

        if (!this.world.isClientSide && (this.lastDamageByPlayerTime > 0 || this.alwaysGivesExp()) && this.isDropExperience() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            return exp;
        } else {
            return 0;
        }
    }
    // CraftBukkit end

    protected void b(BlockPosition blockposition) {
        int i = EnchantmentManager.a(Enchantments.FROST_WALKER, this);

        if (i > 0) {
            EnchantmentFrostWalker.a(this, this.world, blockposition, i);
        }

    }

    public boolean isBaby() {
        return false;
    }

    public float cn() {
        return this.isBaby() ? 0.5F : 1.0F;
    }

    @Override
    public boolean bf() {
        return false;
    }

    protected void co() {
        ++this.deathTicks;
        if (this.deathTicks >= 20 && !this.dead) { // CraftBukkit - (this.deathTicks == 20) -> (this.deathTicks >= 20 && !this.dead)
            int i;

            // CraftBukkit start - Update getExpReward() above if the removed if() changes!
            i = this.expToDrop;
            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                EntityLiving attacker = killer != null ? killer : lastDamager; // Paper
                this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j, this instanceof EntityPlayer ? org.bukkit.entity.ExperienceOrb.SpawnReason.PLAYER_DEATH : org.bukkit.entity.ExperienceOrb.SpawnReason.ENTITY_DEATH, attacker, this)); // Paper
            }
            this.expToDrop = 0;
            // CraftBukkit end

            this.die();

            for (i = 0; i < 20; ++i) {
                double d0 = this.random.nextGaussian() * 0.02D;
                double d1 = this.random.nextGaussian() * 0.02D;
                double d2 = this.random.nextGaussian() * 0.02D;

                this.world.addParticle(Particles.POOF, this.locX + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), this.locY + (double) (this.random.nextFloat() * this.getHeight()), this.locZ + (double) (this.random.nextFloat() * this.getWidth() * 2.0F) - (double) this.getWidth(), d0, d1, d2);
            }
        }

    }

    protected boolean isDropExperience() {
        return !this.isBaby();
    }

    protected int l(int i) {
        int j = EnchantmentManager.getOxygenEnchantmentLevel(this);

        return j > 0 && this.random.nextInt(j + 1) > 0 ? i : i - 1;
    }

    protected int m(int i) {
        return Math.min(i + 4, this.bp());
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

    public int ct() {
        return this.hurtTimestamp;
    }

    public void setLastDamager(@Nullable EntityLiving entityliving) {
        this.lastDamager = entityliving;
        this.hurtTimestamp = this.ticksLived;
    }

    @Nullable
    public EntityLiving cu() {
        return this.bB;
    }

    public int cv() {
        return this.bC;
    }

    public void z(Entity entity) {
        if (entity instanceof EntityLiving) {
            this.bB = (EntityLiving) entity;
        } else {
            this.bB = null;
        }

        this.bC = this.ticksLived;
    }

    public int cw() {
        return this.ticksFarFromPlayer;
    }

    public void n(int i) {
        this.ticksFarFromPlayer = i;
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

    @Override
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

                nbttaglist.add(mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("ActiveEffects", nbttaglist);
        }

        nbttagcompound.setBoolean("FallFlying", this.isGliding());
        this.getBedPosition().ifPresent((blockposition) -> {
            nbttagcompound.setInt("SleepingX", blockposition.getX());
            nbttagcompound.setInt("SleepingY", blockposition.getY());
            nbttagcompound.setInt("SleepingZ", blockposition.getZ());
        });
        nbttagcompound.set("Brain", (NBTBase) this.br.a((DynamicOps) DynamicOpsNBT.a));
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        // Paper start - jvm keeps optimizing the setter
        float absorptionAmount = nbttagcompound.getFloat("AbsorptionAmount");
        if (Float.isNaN(absorptionAmount)) {
            absorptionAmount = 0;
        }
        this.setAbsorptionHearts(absorptionAmount);
        // Paper end
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

        // CraftBukkit start
        if (nbttagcompound.hasKey("Bukkit.MaxHealth")) {
            NBTBase nbtbase = nbttagcompound.get("Bukkit.MaxHealth");
            if (nbtbase.getTypeId() == 5) {
                this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(((NBTTagFloat) nbtbase).asDouble());
            } else if (nbtbase.getTypeId() == 3) {
                this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(((NBTTagInt) nbtbase).asDouble());
            }
        }
        // CraftBukkit end

        if (nbttagcompound.hasKeyOfType("Health", 99)) {
            this.setHealth(nbttagcompound.getFloat("Health"));
        }

        this.hurtTicks = nbttagcompound.getShort("HurtTime");
        this.deathTicks = nbttagcompound.getShort("DeathTime");
        this.hurtTimestamp = nbttagcompound.getInt("HurtByTimestamp");
        if (nbttagcompound.hasKeyOfType("Team", 8)) {
            String s = nbttagcompound.getString("Team");
            ScoreboardTeam scoreboardteam = this.world.getScoreboard().getTeam(s);
            if (!world.paperConfig.nonPlayerEntitiesOnScoreboards && !(this instanceof EntityHuman)) { scoreboardteam = null; } // Paper
            boolean flag = scoreboardteam != null && this.world.getScoreboard().addPlayerToTeam(this.getUniqueIDString(), scoreboardteam);

            if (!flag) {
                EntityLiving.LOGGER.warn("Unable to add mob to team \"{}\" (that team probably doesn't exist)", s);
            }
        }

        if (nbttagcompound.getBoolean("FallFlying")) {
            this.setFlag(7, true);
        }

        if (nbttagcompound.hasKeyOfType("SleepingX", 99) && nbttagcompound.hasKeyOfType("SleepingY", 99) && nbttagcompound.hasKeyOfType("SleepingZ", 99)) {
            BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("SleepingX"), nbttagcompound.getInt("SleepingY"), nbttagcompound.getInt("SleepingZ"));

            this.d(blockposition);
            this.datawatcher.set(EntityLiving.POSE, EntityPose.SLEEPING);
            if (!this.justCreated) {
                this.a(blockposition);
            }
        }

        if (nbttagcompound.hasKeyOfType("Brain", 10)) {
            this.br = this.a(new Dynamic(DynamicOpsNBT.a, nbttagcompound.get("Brain")));
        }

    }

    // CraftBukkit start
    private boolean isTickingEffects = false;
    private List<ProcessableEffect> effectsToProcess = Lists.newArrayList();

    private static class ProcessableEffect {

        private MobEffectList type;
        private MobEffect effect;
        private final EntityPotionEffectEvent.Cause cause;

        private ProcessableEffect(MobEffect effect, EntityPotionEffectEvent.Cause cause) {
            this.effect = effect;
            this.cause = cause;
        }

        private ProcessableEffect(MobEffectList type, EntityPotionEffectEvent.Cause cause) {
            this.type = type;
            this.cause = cause;
        }
    }
    // CraftBukkit end

    protected void tickPotionEffects() {
        Iterator iterator = this.effects.keySet().iterator();

        isTickingEffects = true; // CraftBukkit
        try {
            while (iterator.hasNext()) {
                MobEffectList mobeffectlist = (MobEffectList) iterator.next();
                MobEffect mobeffect = (MobEffect) this.effects.get(mobeffectlist);

                if (!mobeffect.tick(this)) {
                    if (!this.world.isClientSide) {
                        // CraftBukkit start
                        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(this, mobeffect, null, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.EXPIRATION);
                        if (event.isCancelled()) {
                            continue;
                        }
                        // CraftBukkit end
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
        // CraftBukkit start
        isTickingEffects = false;
        for (ProcessableEffect e : effectsToProcess) {
            if (e.effect != null) {
                addEffect(e.effect, e.cause);
            } else {
                removeEffect(e.type, e.cause);
            }
        }
        effectsToProcess.clear();
        // CraftBukkit end

        if (this.updateEffects) {
            if (!this.world.isClientSide) {
                this.C();
            }

            this.updateEffects = false;
        }

        int i = (Integer) this.datawatcher.get(EntityLiving.e);
        boolean flag = (Boolean) this.datawatcher.get(EntityLiving.f);

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

                this.world.addParticle(flag ? Particles.AMBIENT_ENTITY_EFFECT : Particles.ENTITY_EFFECT, this.locX + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), this.locY + this.random.nextDouble() * (double) this.getHeight(), this.locZ + (this.random.nextDouble() - 0.5D) * (double) this.getWidth(), d0, d1, d2);
            }
        }

    }

    protected void C() {
        if (this.effects.isEmpty()) {
            this.cy();
            this.setInvisible(false);
        } else {
            Collection<MobEffect> collection = this.effects.values();

            this.datawatcher.set(EntityLiving.f, c(collection));
            this.datawatcher.set(EntityLiving.e, PotionUtil.a(collection));
            this.setInvisible(this.hasEffect(MobEffects.INVISIBILITY));
        }

    }

    public double A(@Nullable Entity entity) {
        double d0 = 1.0D;

        if (this.isSneaking()) {
            d0 *= 0.8D;
        }

        if (this.isInvisible()) {
            float f = this.cT();

            if (f < 0.1F) {
                f = 0.1F;
            }

            d0 *= 0.7D * (double) f;
        }

        if (entity != null) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.HEAD);
            Item item = itemstack.getItem();
            EntityTypes<?> entitytypes = entity.getEntityType();

            if (entitytypes == EntityTypes.SKELETON && item == Items.SKELETON_SKULL || entitytypes == EntityTypes.ZOMBIE && item == Items.ZOMBIE_HEAD || entitytypes == EntityTypes.CREEPER && item == Items.CREEPER_HEAD) {
                d0 *= 0.5D;
            }
        }

        return d0;
    }

    public boolean c(EntityLiving entityliving) {
        return true;
    }

    public boolean a(EntityLiving entityliving, PathfinderTargetCondition pathfindertargetcondition) {
        return pathfindertargetcondition.a(this, entityliving);
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

    protected void cy() {
        this.datawatcher.set(EntityLiving.f, false);
        this.datawatcher.set(EntityLiving.e, 0);
    }

    // CraftBukkit start
    public boolean removeAllEffects() {
        return removeAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.UNKNOWN);
    }

    public boolean removeAllEffects(EntityPotionEffectEvent.Cause cause) {
        // CraftBukkit end
        if (this.world.isClientSide) {
            return false;
        } else {
            Iterator<MobEffect> iterator = this.effects.values().iterator();

            boolean flag;

            for (flag = false; iterator.hasNext(); flag = true) {
                // CraftBukkit start
                MobEffect effect = (MobEffect) iterator.next();
                EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(this, effect, null, cause, EntityPotionEffectEvent.Action.CLEARED);
                if (event.isCancelled()) {
                    continue;
                }
                this.b(effect);
                // CraftBukkit end
                iterator.remove();
            }

            return flag;
        }
    }

    public Collection<MobEffect> getEffects() {
        return this.effects.values();
    }

    public Map<MobEffectList, MobEffect> cB() {
        return this.effects;
    }

    public boolean hasEffect(MobEffectList mobeffectlist) {
        return this.effects.containsKey(mobeffectlist);
    }

    @Nullable
    public MobEffect getEffect(MobEffectList mobeffectlist) {
        return (MobEffect) this.effects.get(mobeffectlist);
    }

    // CraftBukkit start
    public boolean addEffect(MobEffect mobeffect) {
        return addEffect(mobeffect, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.UNKNOWN);
    }

    public boolean addEffect(MobEffect mobeffect, EntityPotionEffectEvent.Cause cause) {
        org.spigotmc.AsyncCatcher.catchOp("effect add"); // Spigot
        if (isTickingEffects) {
            effectsToProcess.add(new ProcessableEffect(mobeffect, cause));
            return true;
        }
        // CraftBukkit end

        if (!this.d(mobeffect)) {
            return false;
        } else {
            MobEffect mobeffect1 = (MobEffect) this.effects.get(mobeffect.getMobEffect());

            // CraftBukkit start
            boolean override = false;
            if (mobeffect1 != null) {
                override = new MobEffect(mobeffect1).a(mobeffect);
            }

            EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(this, mobeffect1, mobeffect, cause, override);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end

            if (mobeffect1 == null) {
                this.effects.put(mobeffect.getMobEffect(), mobeffect);
                this.a(mobeffect);
                return true;
                // CraftBukkit start
            } else if (event.isOverride()) {
                mobeffect1.a(mobeffect);
                this.a(mobeffect1, true);
                // CraftBukkit end
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

    public boolean cC() {
        return this.getMonsterType() == EnumMonsterType.UNDEAD;
    }

    // CraftBukkit start
    @Nullable
    public MobEffect c(@Nullable MobEffectList mobeffectlist) {
        return c(mobeffectlist, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.UNKNOWN);
    }

    @Nullable
    public MobEffect c(@Nullable MobEffectList mobeffectlist, EntityPotionEffectEvent.Cause cause) {
        if (isTickingEffects) {
            effectsToProcess.add(new ProcessableEffect(mobeffectlist, cause));
            return null;
        }

        MobEffect effect = this.effects.get(mobeffectlist);
        if (effect == null) {
            return null;
        }

        EntityPotionEffectEvent event = CraftEventFactory.callEntityPotionEffectChangeEvent(this, effect, null, cause);
        if (event.isCancelled()) {
            return null;
        }

        return (MobEffect) this.effects.remove(mobeffectlist);
    }

    public boolean removeEffect(MobEffectList mobeffectlist) {
        return removeEffect(mobeffectlist, org.bukkit.event.entity.EntityPotionEffectEvent.Cause.UNKNOWN);
    }

    public boolean removeEffect(MobEffectList mobeffectlist, EntityPotionEffectEvent.Cause cause) {
        MobEffect mobeffect = this.c(mobeffectlist, cause);
        // CraftBukkit end

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

    // CraftBukkit start - Delegate so we can handle providing a reason for health being regained
    public void heal(float f) {
        heal(f, EntityRegainHealthEvent.RegainReason.CUSTOM);
    }

    public void heal(float f, EntityRegainHealthEvent.RegainReason regainReason) {
        // Paper start - Forward
        heal(f, regainReason, false);
    }

    public void heal(float f, EntityRegainHealthEvent.RegainReason regainReason, boolean isFastRegen) {
        // Paper end
        float f1 = this.getHealth();

        if (f1 > 0.0F) {
            EntityRegainHealthEvent event = new EntityRegainHealthEvent(this.getBukkitEntity(), f, regainReason, isFastRegen); // Paper
            // Suppress during worldgen
            if (this.valid) {
                this.world.getServer().getPluginManager().callEvent(event);
            }

            if (!event.isCancelled()) {
                this.setHealth((float) (this.getHealth() + event.getAmount()));
            }
            // CraftBukkit end
        }

    }

    public float getHealth() {
        // CraftBukkit start - Use unscaled health
        if (this instanceof EntityPlayer) {
            return (float) ((EntityPlayer) this).getBukkitEntity().getHealth();
        }
        // CraftBukkit end
        return (Float) this.datawatcher.get(EntityLiving.HEALTH);
    }

    public void setHealth(float f) {
        // Paper start
        if (Float.isNaN(f)) { f = getMaxHealth(); if (this.valid) {
            System.err.println("[NAN-HEALTH] " + getName() + " had NaN health set");
        } } // Paper end
        // CraftBukkit start - Handle scaled health
        if (this instanceof EntityPlayer) {
            org.bukkit.craftbukkit.entity.CraftPlayer player = ((EntityPlayer) this).getBukkitEntity();
            // Squeeze
            if (f < 0.0F) {
                player.setRealHealth(0.0D);
            } else if (f > player.getMaxHealth()) {
                player.setRealHealth(player.getMaxHealth());
            } else {
                player.setRealHealth(f);
            }

            player.updateScaledHealth(false);
            return;
        }
        // CraftBukkit end
        this.datawatcher.set(EntityLiving.HEALTH, MathHelper.a(f, 0.0F, this.getMaxHealth()));
    }

    @Override
    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (this.world.isClientSide) {
            return false;
        } else if (this.dead || this.killed || this.getHealth() <= 0.0F) { // CraftBukkit - Don't allow entities that got set to dead/killed elsewhere to get damaged and die
            return false;
        } else if (damagesource.p() && this.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            return false;
        } else {
            if (this.isSleeping() && !this.world.isClientSide) {
                this.dy();
            }

            this.ticksFarFromPlayer = 0;
            float f1 = f;

            // CraftBukkit - Moved into damageEntity0(DamageSource, float)
            if (false && (damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && !this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
                this.getEquipment(EnumItemSlot.HEAD).damage((int) (f * 4.0F + this.random.nextFloat() * f * 2.0F), this, (entityliving) -> {
                    entityliving.c(EnumItemSlot.HEAD);
                });
                f *= 0.75F;
            }

            boolean flag = f > 0.0F && this.applyBlockingModifier(damagesource); // Copied from below
            float f2 = 0.0F;

            // CraftBukkit - Moved into damageEntity0(DamageSource, float)
            if (false && f > 0.0F && this.applyBlockingModifier(damagesource)) {
                this.damageShield(f);
                f2 = f;
                f = 0.0F;
                if (!damagesource.b()) {
                    Entity entity = damagesource.j();

                    if (entity instanceof EntityLiving) {
                        this.shieldBlock((EntityLiving) entity);
                    }
                }

                flag = true;
            }

            this.aF = 1.5F;
            boolean flag1 = true;

            if ((float) this.noDamageTicks > 10.0F) {
                if (f <= this.lastDamage) {
                    this.forceExplosionKnockback = true; // CraftBukkit - SPIGOT-949 - for vanilla consistency, cooldown does not prevent explosion knockback
                    return false;
                }

                // CraftBukkit start
                if (!this.damageEntity0(damagesource, f - this.lastDamage)) {
                    return false;
                }
                // CraftBukkit end
                this.lastDamage = f;
                flag1 = false;
            } else {
                // CraftBukkit start
                if (!this.damageEntity0(damagesource, f)) {
                    return false;
                }
                this.lastDamage = f;
                this.noDamageTicks = 20;
                // this.damageEntity0(damagesource, f);
                // CraftBukkit end
                this.hurtDuration = 10;
                this.hurtTicks = this.hurtDuration;
            }

            // CraftBukkit start
            if (this instanceof EntityAnimal) {
                ((EntityAnimal) this).resetLove();
                if (this instanceof EntityTameableAnimal) {
                    ((EntityTameableAnimal) this).getGoalSit().setSitting(false);
                }
            }
            // CraftBukkit end

            this.az = 0.0F;
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
                        EntityLiving entityliving = entitywolf.getOwner();

                        if (entityliving != null && entityliving.getEntityType() == EntityTypes.PLAYER) {
                            this.killer = (EntityHuman) entityliving;
                        } else {
                            this.killer = null;
                        }
                    }
                }
            }

                boolean knockbackCancelled = world.paperConfig.disableExplosionKnockback && damagesource.isExplosion() && this instanceof EntityHuman; // Paper - Disable explosion knockback
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
                    } else if (damagesource == DamageSource.SWEET_BERRY_BUSH) {
                        b0 = 44;
                    } else {
                        b0 = 2;
                    }

                        if (!knockbackCancelled) // Paper - Disable explosion knockback
                    this.world.broadcastEntityEffect(this, b0);
                }

                if (damagesource != DamageSource.DROWN && (!flag || f > 0.0F)) {
                    this.velocityChanged();
                }

                if (entity1 != null) {
                    double d0 = entity1.locX - this.locX;

                    double d1;

                    for (d1 = entity1.locZ - this.locZ; d0 * d0 + d1 * d1 < 1.0E-4D; d1 = (Math.random() - Math.random()) * 0.01D) {
                        d0 = (Math.random() - Math.random()) * 0.01D;
                    }

                    this.az = (float) (MathHelper.d(d1, d0) * 57.2957763671875D - (double) this.yaw);
                    this.a(entity1, 0.4F, d0, d1);
                } else {
                    this.az = (float) ((int) (Math.random() * 2.0D) * 180);
                }
            }

                if (knockbackCancelled) this.world.broadcastEntityEffect(this, (byte) 2); // Paper - Disable explosion knockback

            if (this.getHealth() <= 0.0F) {
                if (!this.f(damagesource)) {
                    // Paper start - moved into CraftEventFactory event caller for cancellable death event
                    //SoundEffect soundeffect = this.getSoundDeath();

                    //if (flag1 && soundeffect != null) {
                    //    this.a(soundeffect, this.getSoundVolume(), this.cV());
                    //}
                    this.silentDeath = !flag1; // mark entity as dying silently
                    // Paper end

                    this.die(damagesource);
                    this.silentDeath = false; // Paper - cancellable death event - reset to default
                }
            } else if (flag1) {
                this.c(damagesource);
            }

            boolean flag2 = !flag || f > 0.0F;

            if (flag2) {
                this.bH = damagesource;
                this.bI = this.world.getTime();
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

    protected void shieldBlock(EntityLiving entityliving) {
        entityliving.e(this);
    }

    protected void e(EntityLiving entityliving) {
        entityliving.a(this, 0.5F, entityliving.locX - this.locX, entityliving.locZ - this.locZ);
    }

    private boolean f(DamageSource damagesource) {
        if (damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            ItemStack itemstack = null;
            EnumHand[] aenumhand = EnumHand.values();
            int i = aenumhand.length;

            // CraftBukkit start
            ItemStack itemstack1 = ItemStack.a;
            for (int j = 0; j < i; ++j) {
                EnumHand enumhand = aenumhand[j];
                itemstack1 = this.b(enumhand);

                if (itemstack1.getItem() == Items.TOTEM_OF_UNDYING) {
                    itemstack = itemstack1.cloneItemStack();
                    // itemstack1.subtract(1); // CraftBukkit
                    break;
                }
            }

            EntityResurrectEvent event = new EntityResurrectEvent((LivingEntity) this.getBukkitEntity());
            event.setCancelled(itemstack == null);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                if (!itemstack1.isEmpty()) {
                    itemstack1.subtract(1);
                }
                if (itemstack != null && this instanceof EntityPlayer) {
                    // CraftBukkit end
                    EntityPlayer entityplayer = (EntityPlayer) this;

                    entityplayer.b(StatisticList.ITEM_USED.b(Items.TOTEM_OF_UNDYING));
                    CriterionTriggers.B.a(entityplayer, itemstack);
                }

                this.setHealth(1.0F);
                // CraftBukkit start
                this.removeAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
                this.addEffect(new MobEffect(MobEffects.REGENERATION, 900, 1), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
                this.addEffect(new MobEffect(MobEffects.ABSORBTION, 100, 1), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.TOTEM);
                // CraftBukkit end
                this.world.broadcastEntityEffect(this, (byte) 35);
            }

            return !event.isCancelled();
        }
    }

    @Nullable
    public DamageSource cE() {
        if (this.world.getTime() - this.bI > 40L) {
            this.bH = null;
        }

        return this.bH;
    }

    protected void c(DamageSource damagesource) {
        SoundEffect soundeffect = this.getSoundHurt(damagesource);

        if (soundeffect != null) {
            this.a(soundeffect, this.getSoundVolume(), this.cV());
        }

    }

    private boolean applyBlockingModifier(DamageSource damagesource) {
        Entity entity = damagesource.j();
        boolean flag = false;

        if (entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;

            if (entityarrow.getPierceLevel() > 0) {
                flag = true;
            }
        }

        if (!damagesource.ignoresArmor() && this.isBlocking() && !flag) {
            Vec3D vec3d = damagesource.w();

            if (vec3d != null) {
                Vec3D vec3d1 = this.f(1.0F);
                Vec3D vec3d2 = vec3d.a(new Vec3D(this.locX, this.locY, this.locZ)).d();

                vec3d2 = new Vec3D(vec3d2.x, 0.0D, vec3d2.z);
                if (vec3d2.b(vec3d1) < 0.0D) {
                    return true;
                }
            }
        }

        return false;
    }

    public void die(DamageSource damagesource) {
        if (!this.killed) {
            Entity entity = damagesource.getEntity();
            EntityLiving entityliving = this.getKillingEntity();

            /* // Paper - move down to make death event cancellable
            if (this.aY >= 0 && entityliving != null) {
                entityliving.a(this, this.aY, damagesource);
            }

            if (entity != null) {
                entity.b(this);
            }

            if (this.isSleeping()) {
                this.dy();
            }
            */ // Paper

            this.killed = true;
            //this.getCombatTracker().g();
            if (!this.world.isClientSide) {
                org.bukkit.event.entity.EntityDeathEvent deathEvent = this.d(damagesource);
                if (deathEvent == null || !deathEvent.isCancelled()) {
                    if (this.getKillCount() >= 0 && entityliving != null) {
                        entityliving.runKillTrigger(this, this.getKillCount(), damagesource);
                    }
                    if (entity != null) {
                        entity.onKill(this);
                    }
                    if (this.isSleeping()) {
                        this.dy();
                    }
                    this.getCombatTracker().reset();
                } else {
                    this.killed = false;
                    this.setHealth((float) deathEvent.getReviveHealth());
                }
                // Paper end

                boolean flag = false;

                if (this.killed && entityliving instanceof EntityWither) {
                    if (this.world.getGameRules().getBoolean(GameRules.MOB_GRIEFING)) {
                        BlockPosition blockposition = new BlockPosition(this.locX, this.locY, this.locZ);
                        IBlockData iblockdata = Blocks.WITHER_ROSE.getBlockData();

                        if (this.world.getType(blockposition).isAir() && iblockdata.canPlace(this.world, blockposition)) {
                            this.world.setTypeAndData(blockposition, iblockdata, 3);
                            flag = true;
                        }
                    }

                    if (!flag) {
                        EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, new ItemStack(Items.bg));

                        this.world.addEntity(entityitem);
                    }
                }
            }

            if (this.killed) { // Paper
            this.world.broadcastEntityEffect(this, (byte) 3);
            this.setPose(EntityPose.DYING);
            } // Paper
        }
    }

    protected org.bukkit.event.entity.EntityDeathEvent processDeath(DamageSource damagesource) { return d(damagesource); } // Paper - OBFHELPER
    protected org.bukkit.event.entity.EntityDeathEvent d(DamageSource damagesource) { // Paper
        Entity entity = damagesource.getEntity();
        int i;

        if (entity instanceof EntityHuman) {
            i = EnchantmentManager.g((EntityLiving) entity);
        } else {
            i = 0;
        }

        boolean flag = this.lastDamageByPlayerTime > 0;

        this.cF(); // CraftBukkit - from below // PAIL
        org.bukkit.event.entity.EntityDeathEvent deathEvent; // Paper
        if (this.isDropExperience() && this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT)) {
            this.a(damagesource, flag);
            this.dropDeathLoot(damagesource, i, flag);
            // CraftBukkit start - Call death event
            deathEvent = CraftEventFactory.callEntityDeathEvent(this, this.drops); // Paper
        } else {
            deathEvent = CraftEventFactory.callEntityDeathEvent(this); // Paper
            // CraftBukkit end
        }
        this.postDeathDropItems(deathEvent); // Paper
        this.drops = new ArrayList<>(); // Paper

        return deathEvent; // Paper
    }

    protected void cF() {}
    protected void postDeathDropItems(org.bukkit.event.entity.EntityDeathEvent event) {} // Paper - method for post death logic that cannot be ran before the event is potentially cancelled

    protected void dropDeathLoot(DamageSource damagesource, int i, boolean flag) {}

    public MinecraftKey cG() {
        return this.getEntityType().h();
    }

    protected void a(DamageSource damagesource, boolean flag) {
        MinecraftKey minecraftkey = this.cG();
        LootTable loottable = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(minecraftkey);
        LootTableInfo.Builder loottableinfo_builder = this.a(flag, damagesource);

        loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.ENTITY), this::a);
    }

    protected LootTableInfo.Builder a(boolean flag, DamageSource damagesource) {
        LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.world)).a(this.random).set(LootContextParameters.THIS_ENTITY, this).set(LootContextParameters.POSITION, new BlockPosition(this)).set(LootContextParameters.DAMAGE_SOURCE, damagesource).setOptional(LootContextParameters.KILLER_ENTITY, damagesource.getEntity()).setOptional(LootContextParameters.DIRECT_KILLER_ENTITY, damagesource.j());

        if (flag && this.killer != null) {
            loottableinfo_builder = loottableinfo_builder.set(LootContextParameters.LAST_DAMAGE_PLAYER, this.killer).a(this.killer.eb());
        }

        return loottableinfo_builder;
    }

    public void a(Entity entity, float f, double d0, double d1) {
        if (this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).getValue()) {
            this.impulse = true;
            Vec3D vec3d = this.getMot();
            Vec3D vec3d1 = (new Vec3D(d0, 0.0D, d1)).d().a((double) f);

            this.setMot(vec3d.x / 2.0D - vec3d1.x, this.onGround ? Math.min(0.4D, vec3d.y / 2.0D + (double) f) : vec3d.y, vec3d.z / 2.0D - vec3d1.z);

            // Paper start - call EntityKnockbackByEntityEvent
            Vec3D currentMot = this.getMot();
            org.bukkit.util.Vector delta = new org.bukkit.util.Vector(currentMot.x - vec3d.x, currentMot.y - vec3d.y, currentMot.z - vec3d.z);
            // Restore old velocity to be able to access it in the event
            this.setMot(vec3d);
            if (entity == null || new com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent((LivingEntity) getBukkitEntity(), entity.getBukkitEntity(), f, delta).callEvent()) {
                this.setMot(vec3d.x + delta.getX(), vec3d.y + delta.getY(), vec3d.z + delta.getZ());
            }
            // Paper end
        }
    }

    @Nullable
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_GENERIC_HURT;
    }

    public final SoundEffect getDeathSoundEffect() { return this.getSoundDeath(); } // Paper - OBFHELPER
    @Nullable
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_GENERIC_DEATH;
    }

    protected SoundEffect getSoundFall(int i) {
        return i > 4 ? SoundEffects.ENTITY_GENERIC_BIG_FALL : SoundEffects.ENTITY_GENERIC_SMALL_FALL;
    }

    protected SoundEffect c(ItemStack itemstack) {
        return SoundEffects.ENTITY_GENERIC_DRINK;
    }

    public SoundEffect d(ItemStack itemstack) {
        return SoundEffects.ENTITY_GENERIC_EAT;
    }

    public boolean isClimbing() {
        if (this.isSpectator()) {
            return false;
        } else {
            IBlockData iblockdata = this.cI();
            Block block = iblockdata.getBlock();

            return block != Blocks.LADDER && block != Blocks.VINE && block != Blocks.SCAFFOLDING ? block instanceof BlockTrapdoor && this.b(new BlockPosition(this), iblockdata) : true;
        }
    }

    public IBlockData cI() {
        return this.world.getType(new BlockPosition(this));
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

    @Override
    public boolean isAlive() {
        return !this.dead && this.getHealth() > 0.0F;
    }

    @Override
    public void b(float f, float f1) {
        super.b(f, f1);
        MobEffect mobeffect = this.getEffect(MobEffects.JUMP);
        float f2 = mobeffect == null ? 0.0F : (float) (mobeffect.getAmplifier() + 1);
        int i = MathHelper.f((f - 3.0F - f2) * f1);

        if (i > 0) {
            // CraftBukkit start
            if (!this.damageEntity(DamageSource.FALL, (float) i)) {
                return;
            }
            // CraftBukkit end
            this.a(this.getSoundFall(i), 1.0F, 1.0F);
            // this.damageEntity(DamageSource.FALL, (float) i); // CraftBukkit - moved up
            int j = MathHelper.floor(this.locX);
            int k = MathHelper.floor(this.locY - 0.20000000298023224D);
            int l = MathHelper.floor(this.locZ);
            IBlockData iblockdata = this.world.getType(new BlockPosition(j, k, l));

            if (!iblockdata.isAir()) {
                SoundEffectType soundeffecttype = iblockdata.r();

                this.a(soundeffecttype.g(), soundeffecttype.a() * 0.5F, soundeffecttype.b() * 0.75F);
            }
        }

    }

    public int getArmorStrength() {
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.ARMOR);

        return MathHelper.floor(attributeinstance.getValue());
    }

    protected void damageArmor(float f) {}

    protected void damageShield(float f) {}

    protected float applyArmorModifier(DamageSource damagesource, float f) {
        if (!damagesource.ignoresArmor()) {
            // this.damageArmor(f); // CraftBukkit - Moved into damageEntity0(DamageSource, float)
            f = CombatMath.a(f, (float) this.getArmorStrength(), (float) this.getAttributeInstance(GenericAttributes.ARMOR_TOUGHNESS).getValue());
        }

        return f;
    }

    protected float applyMagicModifier(DamageSource damagesource, float f) {
        if (damagesource.isStarvation()) {
            return f;
        } else {
            int i;

            // CraftBukkit - Moved to damageEntity0(DamageSource, float)
            if (false && this.hasEffect(MobEffects.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
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

    // CraftBukkit start
    protected boolean damageEntity0(final DamageSource damagesource, float f) { // void -> boolean, add final
       if (!this.isInvulnerable(damagesource)) {
            final boolean human = this instanceof EntityHuman;
            float originalDamage = f;
            Function<Double, Double> hardHat = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && !EntityLiving.this.getEquipment(EnumItemSlot.HEAD).isEmpty()) {
                        return -(f - (f * 0.75F));

                    }
                    return -0.0;
                }
            };
            float hardHatModifier = hardHat.apply((double) f).floatValue();
            f += hardHatModifier;

            Function<Double, Double> blocking = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -((EntityLiving.this.applyBlockingModifier(damagesource)) ? f : 0.0);
                }
            };
            float blockingModifier = blocking.apply((double) f).floatValue();
            f += blockingModifier;

            Function<Double, Double> armor = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -(f - EntityLiving.this.applyArmorModifier(damagesource, f.floatValue()));
                }
            };
            float armorModifier = armor.apply((double) f).floatValue();
            f += armorModifier;

            Function<Double, Double> resistance = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    if (!damagesource.isStarvation() && EntityLiving.this.hasEffect(MobEffects.RESISTANCE) && damagesource != DamageSource.OUT_OF_WORLD) {
                        int i = (EntityLiving.this.getEffect(MobEffects.RESISTANCE).getAmplifier() + 1) * 5;
                        int j = 25 - i;
                        float f1 = f.floatValue() * (float) j;
                        return -(f - (f1 / 25.0F));
                    }
                    return -0.0;
                }
            };
            float resistanceModifier = resistance.apply((double) f).floatValue();
            f += resistanceModifier;

            Function<Double, Double> magic = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -(f - EntityLiving.this.applyMagicModifier(damagesource, f.floatValue()));
                }
            };
            float magicModifier = magic.apply((double) f).floatValue();
            f += magicModifier;

            Function<Double, Double> absorption = new Function<Double, Double>() {
                @Override
                public Double apply(Double f) {
                    return -(Math.max(f - Math.max(f - EntityLiving.this.getAbsorptionHearts(), 0.0F), 0.0F));
                }
            };
            float absorptionModifier = absorption.apply((double) f).floatValue();

            EntityDamageEvent event = CraftEventFactory.handleLivingEntityDamageEvent(this, damagesource, originalDamage, hardHatModifier, blockingModifier, armorModifier, resistanceModifier, magicModifier, absorptionModifier, hardHat, blocking, armor, resistance, magic, absorption);
            if (event.isCancelled()) {
                return false;
            }

            f = (float) event.getFinalDamage();

            // Resistance
            if (event.getDamage(DamageModifier.RESISTANCE) < 0) {
                float f3 = (float) -event.getDamage(DamageModifier.RESISTANCE);
                if (f3 > 0.0F && f3 < 3.4028235E37F) {
                    if (this instanceof EntityPlayer) {
                        ((EntityPlayer) this).a(StatisticList.DAMAGE_RESISTED, Math.round(f3 * 10.0F));
                    } else if (damagesource.getEntity() instanceof EntityPlayer) {
                        ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_RESISTED, Math.round(f3 * 10.0F));
                    }
                }
            }

            // Apply damage to helmet
            if ((damagesource == DamageSource.ANVIL || damagesource == DamageSource.FALLING_BLOCK) && this.getEquipment(EnumItemSlot.HEAD) != null) {
                this.getEquipment(EnumItemSlot.HEAD).damage((int) (event.getDamage() * 4.0F + this.random.nextFloat() * event.getDamage() * 2.0F), this, (entityliving) -> {
                    entityliving.c(EnumItemSlot.HEAD);
                });
            }

            // Apply damage to armor
            if (!damagesource.ignoresArmor()) {
                float armorDamage = (float) (event.getDamage() + event.getDamage(DamageModifier.BLOCKING) + event.getDamage(DamageModifier.HARD_HAT));
                this.damageArmor(armorDamage);
            }

            // Apply blocking code // PAIL: steal from above
            if (event.getDamage(DamageModifier.BLOCKING) < 0) {
                this.world.broadcastEntityEffect(this, (byte) 29); // SPIGOT-4635 - shield damage sound
                this.damageShield((float) -event.getDamage(DamageModifier.BLOCKING));
                Entity entity = damagesource.j();

                if (entity instanceof EntityLiving) {
                    this.shieldBlock((EntityLiving) entity);
                }
            }

            absorptionModifier = (float) -event.getDamage(DamageModifier.ABSORPTION);
            this.setAbsorptionHearts(Math.max(this.getAbsorptionHearts() - absorptionModifier, 0.0F));
            float f2 = absorptionModifier;

            if (f2 > 0.0F && f2 < 3.4028235E37F && this instanceof EntityHuman) {
                ((EntityHuman) this).a(StatisticList.DAMAGE_ABSORBED, Math.round(f2 * 10.0F));
            }
            if (f2 > 0.0F && f2 < 3.4028235E37F && damagesource.getEntity() instanceof EntityPlayer) {
                ((EntityPlayer) damagesource.getEntity()).a(StatisticList.DAMAGE_DEALT_ABSORBED, Math.round(f2 * 10.0F));
            }

            if (f > 0 || !human) {
                if (human) {
                    // PAIL: Be sure to drag all this code from the EntityHuman subclass each update.
                    ((EntityHuman) this).applyExhaustion(damagesource.getExhaustionCost());
                    if (f < 3.4028235E37F) {
                        ((EntityHuman) this).a(StatisticList.DAMAGE_TAKEN, Math.round(f * 10.0F));
                    }
                }
                // CraftBukkit end
                float f3 = this.getHealth();

                this.setHealth(f3 - f);
                this.getCombatTracker().trackDamage(damagesource, f3, f);
                // CraftBukkit start
                if (!human) {
                    this.setAbsorptionHearts(this.getAbsorptionHearts() - f);
                }

                return true;
            } else {
                // Duplicate triggers if blocking
                if (event.getDamage(DamageModifier.BLOCKING) < 0) {
                    if (this instanceof EntityPlayer) {
                        CriterionTriggers.h.a((EntityPlayer) this, damagesource, f, originalDamage, true);
                        f2 = (float) -event.getDamage(DamageModifier.BLOCKING);
                        if (f2 > 0.0F && f2 < 3.4028235E37F) {
                            ((EntityPlayer) this).a(StatisticList.DAMAGE_BLOCKED_BY_SHIELD, Math.round(originalDamage * 10.0F));
                        }
                    }

                    if (damagesource.getEntity() instanceof EntityPlayer) {
                        CriterionTriggers.g.a((EntityPlayer) damagesource.getEntity(), this, damagesource, f, originalDamage, true);
                    }

                    return false;
                } else {
                    return originalDamage > 0;
                }
                // CraftBukkit end
            }
        }
        return false; // CraftBukkit
    }

    public CombatTracker getCombatTracker() {
        return this.combatTracker;
    }

    @Nullable
    public EntityLiving getKillingEntity() {
        return (EntityLiving) (this.combatTracker.c() != null ? this.combatTracker.c() : (this.killer != null ? this.killer : (this.lastDamager != null ? this.lastDamager : null)));
    }

    public final float getMaxHealth() {
        return (float) this.getAttributeInstance(GenericAttributes.MAX_HEALTH).getValue();
    }

    public final int getArrowCount() {
        return (Integer) this.datawatcher.get(EntityLiving.g);
    }

    public final void setArrowCount(int i) {
        this.datawatcher.set(EntityLiving.g, i);
    }

    private int l() {
        return MobEffectUtil.a(this) ? 6 - (1 + MobEffectUtil.b(this)) : (this.hasEffect(MobEffects.SLOWER_DIG) ? 6 + (1 + this.getEffect(MobEffects.SLOWER_DIG).getAmplifier()) * 2 : 6);
    }

    public void a(EnumHand enumhand) {
        if (!this.at || this.av >= this.l() / 2 || this.av < 0) {
            this.av = -1;
            this.at = true;
            this.au = enumhand;
            if (this.world instanceof WorldServer) {
                ((WorldServer) this.world).getChunkProvider().broadcast(this, new PacketPlayOutAnimation(this, enumhand == EnumHand.MAIN_HAND ? 0 : 3));
            }
        }

    }

    @Override
    protected void af() {
        this.damageEntity(DamageSource.OUT_OF_WORLD, 4.0F);
    }

    protected void cO() {
        int i = this.l();

        if (this.at) {
            ++this.av;
            if (this.av >= i) {
                this.av = 0;
                this.at = false;
            }
        } else {
            this.av = 0;
        }

        this.aC = (float) this.av / (float) i;
    }

    public AttributeInstance getAttributeInstance(IAttribute iattribute) {
        return this.getAttributeMap().a(iattribute);
    }

    public AttributeMapBase getAttributeMap() {
        if (this.attributeMap == null) {
            this.attributeMap = new AttributeMapServer();
            this.craftAttributes = new CraftAttributeMap(attributeMap); // CraftBukkit
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

    @Override
    public abstract Iterable<ItemStack> getArmorItems();

    public abstract ItemStack getEquipment(EnumItemSlot enumitemslot);

    public abstract void setSlot(EnumItemSlot enumitemslot, ItemStack itemstack);

    public float cT() {
        Iterable<ItemStack> iterable = this.getArmorItems();
        int i = 0;
        int j = 0;

        for (Iterator iterator = iterable.iterator(); iterator.hasNext(); ++i) {
            ItemStack itemstack = (ItemStack) iterator.next();

            if (!itemstack.isEmpty()) {
                ++j;
            }
        }

        return i > 0 ? (float) j / (float) i : 0.0F;
    }

    @Override
    public void setSprinting(boolean flag) {
        super.setSprinting(flag);
        AttributeInstance attributeinstance = this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);

        if (attributeinstance.a(EntityLiving.b) != null) {
            attributeinstance.removeModifier(EntityLiving.c);
        }

        if (flag) {
            attributeinstance.addModifier(EntityLiving.c);
        }

    }

    public final float getDeathSoundVolume() { return this.getSoundVolume(); } // Paper - OBFHELPER
    protected float getSoundVolume() {
        return 1.0F;
    }

    public float getSoundPitch() { return cV();} // Paper - OBFHELPER
    protected float cV() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.5F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    protected boolean isFrozen() {
        return this.getHealth() <= 0.0F;
    }

    @Override
    public void collide(Entity entity) {
        if (!this.isSleeping()) {
            super.collide(entity);
        }

    }

    public void B(Entity entity) {
        double d0;

        if (!(entity instanceof EntityBoat) && !(entity instanceof EntityHorseAbstract)) {
            double d1 = entity.locX;
            double d2 = entity.getBoundingBox().minY + (double) entity.getHeight();

            d0 = entity.locZ;
            EnumDirection enumdirection = entity.getAdjustedDirection();

            if (enumdirection != null) {
                EnumDirection enumdirection1 = enumdirection.e();
                int[][] aint = new int[][]{{0, 1}, {0, -1}, {-1, 1}, {-1, -1}, {1, 1}, {1, -1}, {-1, 0}, {1, 0}, {0, 1}};
                double d3 = Math.floor(this.locX) + 0.5D;
                double d4 = Math.floor(this.locZ) + 0.5D;
                double d5 = this.getBoundingBox().maxX - this.getBoundingBox().minX;
                double d6 = this.getBoundingBox().maxZ - this.getBoundingBox().minZ;
                AxisAlignedBB axisalignedbb = new AxisAlignedBB(d3 - d5 / 2.0D, entity.getBoundingBox().minY, d4 - d6 / 2.0D, d3 + d5 / 2.0D, Math.floor(entity.getBoundingBox().minY) + (double) this.getHeight(), d4 + d6 / 2.0D);
                int[][] aint1 = aint;
                int i = aint.length;

                for (int j = 0; j < i; ++j) {
                    int[] aint2 = aint1[j];
                    double d7 = (double) (enumdirection.getAdjacentX() * aint2[0] + enumdirection1.getAdjacentX() * aint2[1]);
                    double d8 = (double) (enumdirection.getAdjacentZ() * aint2[0] + enumdirection1.getAdjacentZ() * aint2[1]);
                    double d9 = d3 + d7;
                    double d10 = d4 + d8;
                    AxisAlignedBB axisalignedbb1 = axisalignedbb.d(d7, 0.0D, d8);
                    BlockPosition blockposition;

                    if (this.world.getCubes(this, axisalignedbb1)) {
                        blockposition = new BlockPosition(d9, this.locY, d10);
                        if (this.world.getType(blockposition).a((IBlockAccess) this.world, blockposition, (Entity) this)) {
                            this.enderTeleportTo(d9, this.locY + 1.0D, d10);
                            return;
                        }

                        BlockPosition blockposition1 = new BlockPosition(d9, this.locY - 1.0D, d10);

                        if (this.world.getType(blockposition1).a((IBlockAccess) this.world, blockposition1, (Entity) this) || this.world.getFluid(blockposition1).a(TagsFluid.WATER)) {
                            d1 = d9;
                            d2 = this.locY + 1.0D;
                            d0 = d10;
                        }
                    } else {
                        blockposition = new BlockPosition(d9, this.locY + 1.0D, d10);
                        if (this.world.getCubes(this, axisalignedbb1.d(0.0D, 1.0D, 0.0D)) && this.world.getType(blockposition).a((IBlockAccess) this.world, blockposition, (Entity) this)) {
                            d1 = d9;
                            d2 = this.locY + 2.0D;
                            d0 = d10;
                        }
                    }
                }
            }

            this.enderTeleportTo(d1, d2, d0);
        } else {
            double d11 = (double) (this.getWidth() / 2.0F + entity.getWidth() / 2.0F) + 0.4D;
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

            this.setPosition(d12, entity.locY + (double) entity.getHeight() + 0.001D, d13);
            if (!this.world.getCubes(this, this.getBoundingBox().b(entity.getBoundingBox()))) {
                this.setPosition(d12, entity.locY + (double) entity.getHeight() + 1.001D, d13);
                if (!this.world.getCubes(this, this.getBoundingBox().b(entity.getBoundingBox()))) {
                    this.setPosition(entity.locX, entity.locY + (double) this.getHeight() + 0.001D, entity.locZ);
                }
            }
        }
    }

    protected float cX() {
        return 0.42F;
    }

    protected void jump() {
        float f;

        if (this.hasEffect(MobEffects.JUMP)) {
            f = this.cX() + 0.1F * (float) (this.getEffect(MobEffects.JUMP).getAmplifier() + 1);
        } else {
            f = this.cX();
        }

        Vec3D vec3d = this.getMot();

        this.setMot(vec3d.x, (double) f, vec3d.z);
        if (this.isSprinting()) {
            float f1 = this.yaw * 0.017453292F;

            this.setMot(this.getMot().add((double) (-MathHelper.sin(f1) * 0.2F), 0.0D, (double) (MathHelper.cos(f1) * 0.2F)));
        }

        this.impulse = true;
    }

    protected void c(Tag<FluidType> tag) {
        this.setMot(this.getMot().add(0.0D, 0.03999999910593033D, 0.0D));
    }

    protected float da() {
        return 0.8F;
    }

    public void e(Vec3D vec3d) {
        double d0;
        float f;

        if (this.df() || this.ca()) {
            d0 = 0.08D;
            boolean flag = this.getMot().y <= 0.0D;

            if (flag && this.hasEffect(MobEffects.SLOW_FALLING)) {
                d0 = 0.01D;
                this.fallDistance = 0.0F;
            }

            double d1;
            float f1;
            double d2;

            if (this.isInWater() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d1 = this.locY;
                f1 = this.isSprinting() ? 0.9F : this.da();
                f = 0.02F;
                float f2 = (float) EnchantmentManager.e(this);

                if (f2 > 3.0F) {
                    f2 = 3.0F;
                }

                if (!this.onGround) {
                    f2 *= 0.5F;
                }

                if (f2 > 0.0F) {
                    f1 += (0.54600006F - f1) * f2 / 3.0F;
                    f += (this.db() - f) * f2 / 3.0F;
                }

                if (this.hasEffect(MobEffects.DOLPHINS_GRACE)) {
                    f1 = 0.96F;
                }

                this.a(f, vec3d);
                this.move(EnumMoveType.SELF, this.getMot());
                Vec3D vec3d1 = this.getMot();

                if (this.positionChanged && this.isClimbing()) {
                    vec3d1 = new Vec3D(vec3d1.x, 0.2D, vec3d1.z);
                }

                this.setMot(vec3d1.d((double) f1, 0.800000011920929D, (double) f1));
                Vec3D vec3d2;

                if (!this.isNoGravity() && !this.isSprinting()) {
                    vec3d2 = this.getMot();
                    if (flag && Math.abs(vec3d2.y - 0.005D) >= 0.003D && Math.abs(vec3d2.y - d0 / 16.0D) < 0.003D) {
                        d2 = -0.003D;
                    } else {
                        d2 = vec3d2.y - d0 / 16.0D;
                    }

                    this.setMot(vec3d2.x, d2, vec3d2.z);
                }

                vec3d2 = this.getMot();
                if (this.positionChanged && this.d(vec3d2.x, vec3d2.y + 0.6000000238418579D - this.locY + d1, vec3d2.z)) {
                    this.setMot(vec3d2.x, 0.30000001192092896D, vec3d2.z);
                }
            } else if (this.aD() && (!(this instanceof EntityHuman) || !((EntityHuman) this).abilities.isFlying)) {
                d1 = this.locY;
                this.a(0.02F, vec3d);
                this.move(EnumMoveType.SELF, this.getMot());
                this.setMot(this.getMot().a(0.5D));
                if (!this.isNoGravity()) {
                    this.setMot(this.getMot().add(0.0D, -d0 / 4.0D, 0.0D));
                }

                Vec3D vec3d3 = this.getMot();

                if (this.positionChanged && this.d(vec3d3.x, vec3d3.y + 0.6000000238418579D - this.locY + d1, vec3d3.z)) {
                    this.setMot(vec3d3.x, 0.30000001192092896D, vec3d3.z);
                }
            } else if (this.isGliding()) {
                Vec3D vec3d4 = this.getMot();

                if (vec3d4.y > -0.5D) {
                    this.fallDistance = 1.0F;
                }

                Vec3D vec3d5 = this.getLookDirection();

                f1 = this.pitch * 0.017453292F;
                double d3 = Math.sqrt(vec3d5.x * vec3d5.x + vec3d5.z * vec3d5.z);
                double d4 = Math.sqrt(b(vec3d4));

                d2 = vec3d5.f();
                float f3 = MathHelper.cos(f1);

                f3 = (float) ((double) f3 * (double) f3 * Math.min(1.0D, d2 / 0.4D));
                vec3d4 = this.getMot().add(0.0D, d0 * (-1.0D + (double) f3 * 0.75D), 0.0D);
                double d5;

                if (vec3d4.y < 0.0D && d3 > 0.0D) {
                    d5 = vec3d4.y * -0.1D * (double) f3;
                    vec3d4 = vec3d4.add(vec3d5.x * d5 / d3, d5, vec3d5.z * d5 / d3);
                }

                if (f1 < 0.0F && d3 > 0.0D) {
                    d5 = d4 * (double) (-MathHelper.sin(f1)) * 0.04D;
                    vec3d4 = vec3d4.add(-vec3d5.x * d5 / d3, d5 * 3.2D, -vec3d5.z * d5 / d3);
                }

                if (d3 > 0.0D) {
                    vec3d4 = vec3d4.add((vec3d5.x / d3 * d4 - vec3d4.x) * 0.1D, 0.0D, (vec3d5.z / d3 * d4 - vec3d4.z) * 0.1D);
                }

                this.setMot(vec3d4.d(0.9900000095367432D, 0.9800000190734863D, 0.9900000095367432D));
                this.move(EnumMoveType.SELF, this.getMot());
                if (this.positionChanged && !this.world.isClientSide) {
                    d5 = Math.sqrt(b(this.getMot()));
                    double d6 = d4 - d5;
                    float f4 = (float) (d6 * 10.0D - 3.0D);

                    if (f4 > 0.0F) {
                        this.a(this.getSoundFall((int) f4), 1.0F, 1.0F);
                        this.damageEntity(DamageSource.FLY_INTO_WALL, f4);
                    }
                }

                if (this.onGround && !this.world.isClientSide) {
                    if (getFlag(7) && !CraftEventFactory.callToggleGlideEvent(this, false).isCancelled()) // CraftBukkit
                    this.setFlag(7, false);
                }
            } else {
                BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().minY - 1.0D, this.locZ);
                float f5 = this.world.getType(blockposition).getBlock().m();

                f1 = this.onGround ? f5 * 0.91F : 0.91F;
                this.a(this.r(f5), vec3d);
                this.setMot(this.f(this.getMot()));
                this.move(EnumMoveType.SELF, this.getMot());
                Vec3D vec3d6 = this.getMot();

                if ((this.positionChanged || this.jumping) && this.isClimbing()) {
                    vec3d6 = new Vec3D(vec3d6.x, 0.2D, vec3d6.z);
                }

                double d7 = vec3d6.y;

                if (this.hasEffect(MobEffects.LEVITATION)) {
                    d7 += (0.05D * (double) (this.getEffect(MobEffects.LEVITATION).getAmplifier() + 1) - vec3d6.y) * 0.2D;
                    this.fallDistance = 0.0F;
                } else if (this.world.isClientSide && !this.world.isLoaded(blockposition)) {
                    if (this.locY > 0.0D) {
                        d7 = -0.1D;
                    } else {
                        d7 = 0.0D;
                    }
                } else if (!this.isNoGravity()) {
                    d7 -= d0;
                }

                this.setMot(vec3d6.x * (double) f1, d7 * 0.9800000190734863D, vec3d6.z * (double) f1);
            }
        }

        this.aE = this.aF;
        d0 = this.locX - this.lastX;
        double d8 = this.locZ - this.lastZ;
        double d9 = this instanceof EntityBird ? this.locY - this.lastY : 0.0D;

        f = MathHelper.sqrt(d0 * d0 + d9 * d9 + d8 * d8) * 4.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }

        this.aF += (f - this.aF) * 0.4F;
        this.aG += this.aF;
    }

    private Vec3D f(Vec3D vec3d) {
        if (this.isClimbing()) {
            this.fallDistance = 0.0F;
            float f = 0.15F;
            double d0 = MathHelper.a(vec3d.x, -0.15000000596046448D, 0.15000000596046448D);
            double d1 = MathHelper.a(vec3d.z, -0.15000000596046448D, 0.15000000596046448D);
            double d2 = Math.max(vec3d.y, -0.15000000596046448D);

            if (d2 < 0.0D && this.cI().getBlock() != Blocks.SCAFFOLDING && this.isSneaking() && this instanceof EntityHuman) {
                d2 = 0.0D;
            }

            vec3d = new Vec3D(d0, d2, d1);
        }

        return vec3d;
    }

    private float r(float f) {
        return this.onGround ? this.db() * (0.21600002F / (f * f * f)) : this.aO;
    }

    public float db() {
        return this.bD;
    }

    public void o(float f) {
        this.bD = f;
    }

    public boolean C(Entity entity) {
        this.z(entity);
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        this.o();
        this.p();
        if (!this.world.isClientSide) {
            int i = this.getArrowCount();

            if (i > 0) {
                if (this.aw <= 0) {
                    this.aw = 20 * (30 - i);
                }

                --this.aw;
                if (this.aw <= 0) {
                    this.setArrowCount(i - 1);
                }
            }

            updateEntityEquipment(); // Paper - split into own method

            if (this.ticksLived % 20 == 0) {
                this.getCombatTracker().g();
            }

            if (!this.glowing) {
                boolean flag = this.hasEffect(MobEffects.GLOWING);

                if (this.getFlag(6) != flag) {
                    this.setFlag(6, flag);
                }
            }

            if (this.isSleeping() && !this.r()) {
                this.dy();
            }
        }

        this.movementTick();
        double d0 = this.locX - this.lastX;
        double d1 = this.locZ - this.lastZ;
        float f = (float) (d0 * d0 + d1 * d1);
        float f1 = this.aK;
        float f2 = 0.0F;

        this.aT = this.aU;
        float f3 = 0.0F;

        if (f > 0.0025000002F) {
            f3 = 1.0F;
            f2 = (float) Math.sqrt((double) f) * 3.0F;
            float f4 = (float) MathHelper.d(d1, d0) * 57.295776F - 90.0F;
            float f5 = MathHelper.e(MathHelper.g(this.yaw) - f4);

            if (95.0F < f5 && f5 < 265.0F) {
                f1 = f4 - 180.0F;
            } else {
                f1 = f4;
            }
        }

        if (this.aC > 0.0F) {
            f1 = this.yaw;
        }

        if (!this.onGround) {
            f3 = 0.0F;
        }

        this.aU += (f3 - this.aU) * 0.3F;
        this.world.getMethodProfiler().enter("headTurn");
        f2 = this.e(f1, f2);
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("rangeChecks");

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        while (this.aK - this.aL < -180.0F) {
            this.aL -= 360.0F;
        }

        while (this.aK - this.aL >= 180.0F) {
            this.aL += 360.0F;
        }

        while (this.pitch - this.lastPitch < -180.0F) {
            this.lastPitch -= 360.0F;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.aM - this.aN < -180.0F) {
            this.aN -= 360.0F;
        }

        while (this.aM - this.aN >= 180.0F) {
            this.aN += 360.0F;
        }

        this.world.getMethodProfiler().exit();
        this.aV += f2;
        if (this.isGliding()) {
            ++this.bp;
        } else {
            this.bp = 0;
        }

        if (this.isSleeping()) {
            this.pitch = 0.0F;
        }
    }

    // Paper start - split into own method from above
    public void updateEntityEquipment() {
        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
        int j = aenumitemslot.length;

        for (int k = 0; k < j; ++k) {
            EnumItemSlot enumitemslot = aenumitemslot[k];
            ItemStack itemstack;

            switch (enumitemslot.a()) {
                case HAND:
                    itemstack = (ItemStack) this.bw.get(enumitemslot.b());
                    break;
                case ARMOR:
                    itemstack = (ItemStack) this.bx.get(enumitemslot.b());
                    break;
                default:
                    continue;
            }

            ItemStack itemstack1 = this.getEquipment(enumitemslot);

            if (!ItemStack.matches(itemstack1, itemstack)) {
                // Paper start - PlayerArmorChangeEvent
                if (this instanceof EntityPlayer && enumitemslot.getType() == EnumItemSlot.Function.ARMOR) {
                    final org.bukkit.inventory.ItemStack oldItem = CraftItemStack.asBukkitCopy(itemstack);
                    final org.bukkit.inventory.ItemStack newItem = CraftItemStack.asBukkitCopy(itemstack1);
                    new PlayerArmorChangeEvent((Player) this.getBukkitEntity(), PlayerArmorChangeEvent.SlotType.valueOf(enumitemslot.name()), oldItem, newItem).callEvent();
                }
                // Paper end
                ((WorldServer) this.world).getChunkProvider().broadcast(this, new PacketPlayOutEntityEquipment(this.getId(), enumitemslot, itemstack1));
                if (!itemstack.isEmpty()) {
                    this.getAttributeMap().a(itemstack.a(enumitemslot));
                }

                if (!itemstack1.isEmpty()) {
                    this.getAttributeMap().b(itemstack1.a(enumitemslot));
                }

                switch (enumitemslot.a()) {
                    case HAND:
                        this.bw.set(enumitemslot.b(), itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack());
                        break;
                    case ARMOR:
                        this.bx.set(enumitemslot.b(), itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack());
                }
            }
        }
    }
    // Paper end

    protected float e(float f, float f1) {
        float f2 = MathHelper.g(f - this.aK);

        this.aK += f2 * 0.3F;
        float f3 = MathHelper.g(this.yaw - this.aK);
        boolean flag = f3 < -90.0F || f3 >= 90.0F;

        if (f3 < -75.0F) {
            f3 = -75.0F;
        }

        if (f3 >= 75.0F) {
            f3 = 75.0F;
        }

        this.aK = this.yaw - f3;
        if (f3 * f3 > 2500.0F) {
            this.aK += f3 * 0.2F;
        }

        if (flag) {
            f1 *= -1.0F;
        }

        return f1;
    }

    public void movementTick() {
        if (this.jumpTicks > 0) {
            --this.jumpTicks;
        }

        if (this.bf > 0 && !this.ca()) {
            double d0 = this.locX + (this.bg - this.locX) / (double) this.bf;
            double d1 = this.locY + (this.bh - this.locY) / (double) this.bf;
            double d2 = this.locZ + (this.bi - this.locZ) / (double) this.bf;
            double d3 = MathHelper.g(this.bj - (double) this.yaw);

            this.yaw = (float) ((double) this.yaw + d3 / (double) this.bf);
            this.pitch = (float) ((double) this.pitch + (this.bk - (double) this.pitch) / (double) this.bf);
            --this.bf;
            this.setPosition(d0, d1, d2);
            this.setYawPitch(this.yaw, this.pitch);
        } else if (!this.df()) {
            this.setMot(this.getMot().a(0.98D));
        }

        if (this.bm > 0) {
            this.aM = (float) ((double) this.aM + MathHelper.g(this.bl - (double) this.aM) / (double) this.bm);
            --this.bm;
        }

        Vec3D vec3d = this.getMot();
        double d4 = vec3d.x;
        double d5 = vec3d.y;
        double d6 = vec3d.z;

        if (Math.abs(vec3d.x) < 0.003D) {
            d4 = 0.0D;
        }

        if (Math.abs(vec3d.y) < 0.003D) {
            d5 = 0.0D;
        }

        if (Math.abs(vec3d.z) < 0.003D) {
            d6 = 0.0D;
        }

        this.setMot(d4, d5, d6);
        this.world.getMethodProfiler().enter("ai");
        if (this.isFrozen()) {
            this.jumping = false;
            this.bb = 0.0F;
            this.bd = 0.0F;
            this.be = 0.0F;
        } else if (this.df()) {
            this.world.getMethodProfiler().enter("newAi");
            this.doTick();
            this.world.getMethodProfiler().exit();
        }

        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("jump");
        if (this.jumping) {
            if (this.Q > 0.0D && (!this.onGround || this.Q > 0.4D)) {
                this.c(TagsFluid.WATER);
            } else if (this.aD()) {
                this.c(TagsFluid.LAVA);
            } else if ((this.onGround || this.Q > 0.0D && this.Q <= 0.4D) && this.jumpTicks == 0) {
                this.jump();
                this.jumpTicks = 10;
            }
        } else {
            this.jumpTicks = 0;
        }

        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("travel");
        this.bb *= 0.98F;
        this.bd *= 0.98F;
        this.be *= 0.9F;
        this.n();
        AxisAlignedBB axisalignedbb = this.getBoundingBox();

        this.e(new Vec3D((double) this.bb, (double) this.bc, (double) this.bd));
        this.world.getMethodProfiler().exit();
        this.world.getMethodProfiler().enter("push");
        if (this.bq > 0) {
            --this.bq;
            this.a(axisalignedbb, this.getBoundingBox());
        }

        this.collideNearby();
        this.world.getMethodProfiler().exit();
    }

    private void n() {
        boolean flag = this.getFlag(7);

        if (flag && !this.onGround && !this.isPassenger()) {
            ItemStack itemstack = this.getEquipment(EnumItemSlot.CHEST);

            if (itemstack.getItem() == Items.ELYTRA && ItemElytra.e(itemstack)) {
                flag = true;
                if (!this.world.isClientSide && (this.bp + 1) % 20 == 0) {
                    itemstack.damage(1, this, (entityliving) -> {
                        entityliving.c(EnumItemSlot.CHEST);
                    });
                }
            } else {
                flag = false;
            }
        } else {
            flag = false;
        }

        if (!this.world.isClientSide) {
            if (flag != this.getFlag(7) && !CraftEventFactory.callToggleGlideEvent(this, flag).isCancelled()) // CraftBukkit
            this.setFlag(7, flag);
        }

    }

    protected void doTick() {}

    protected void collideNearby() {
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox(), IEntitySelector.a(this));

        if (!list.isEmpty()) {
            int i = this.world.getGameRules().getInt(GameRules.MAX_ENTITY_CRAMMING);
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

            numCollisions = Math.max(0, numCollisions - world.paperConfig.maxCollisionsPerEntity); // Paper
            for (j = 0; j < list.size() && numCollisions < world.paperConfig.maxCollisionsPerEntity; ++j) { // Paper
                Entity entity = (Entity) list.get(j);
                entity.numCollisions++; // Paper
                numCollisions++; // Paper

                this.D(entity);
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
                    this.f((EntityLiving) entity);
                    this.bq = 0;
                    this.setMot(this.getMot().a(-0.2D));
                    break;
                }
            }
        } else if (this.positionChanged) {
            this.bq = 0;
        }

        if (!this.world.isClientSide && this.bq <= 0) {
            this.c(4, false);
        }

    }

    protected void D(Entity entity) {
        entity.collide(this);
    }

    protected void f(EntityLiving entityliving) {}

    public void q(int i) {
        this.bq = i;
        if (!this.world.isClientSide) {
            this.c(4, true);
        }

    }

    public boolean isRiptiding() {
        return ((Byte) this.datawatcher.get(EntityLiving.ar) & 4) != 0;
    }

    // Paper start
    @Override public void stopRiding() { stopRiding(false); }
    @Override public void stopRiding(boolean suppressCancellation) {
        // Paper end
        Entity entity = this.getVehicle();

        super.stopRiding(suppressCancellation); // Paper - suppress
        if (entity != null && entity != this.getVehicle() && !this.world.isClientSide) {
            this.B(entity);
        }

    }

    @Override
    public void passengerTick() {
        super.passengerTick();
        this.aT = this.aU;
        this.aU = 0.0F;
        this.fallDistance = 0.0F;
    }

    public void setJumping(boolean flag) {
        this.jumping = flag;
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead && !this.world.isClientSide && (entity instanceof EntityItem || entity instanceof EntityArrow || entity instanceof EntityExperienceOrb)) {
            ((WorldServer) this.world).getChunkProvider().broadcast(entity, new PacketPlayOutCollect(entity.getId(), this.getId(), i));
        }

    }

    public boolean hasLineOfSight(Entity entity) {
        Vec3D vec3d = new Vec3D(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
        Vec3D vec3d1 = new Vec3D(entity.locX, entity.locY + (double) entity.getHeadHeight(), entity.locZ);

        return this.world.rayTrace(new RayTrace(vec3d, vec3d1, RayTrace.BlockCollisionOption.COLLIDER, RayTrace.FluidCollisionOption.NONE, this)).getType() == MovingObjectPosition.EnumMovingObjectType.MISS;
    }

    @Override
    public float h(float f) {
        return f == 1.0F ? this.aM : MathHelper.g(f, this.aN, this.aM);
    }

    public boolean df() {
        return !this.world.isClientSide;
    }

    @Override
    public boolean isInteractable() {
        return !this.dead && this.collides; // CraftBukkit
    }

    @Override
    public boolean isCollidable() {
        return this.isAlive() && !this.isClimbing() && this.collides; // CraftBukkit
    }

    @Override
    protected void velocityChanged() {
        this.velocityChanged = this.random.nextDouble() >= this.getAttributeInstance(GenericAttributes.KNOCKBACK_RESISTANCE).getValue();
    }

    @Override
    public float getHeadRotation() {
        return this.aM;
    }

    @Override
    public void setHeadRotation(float f) {
        this.aM = f;
    }

    @Override
    public void l(float f) {
        this.aK = f;
    }

    public float getAbsorptionHearts() {
        return this.bF;
    }

    public void setAbsorptionHearts(float f) {
        if (f < 0.0F || Float.isNaN(f)) { // Paper
            f = 0.0F;
        }

        this.bF = f;
    }

    public void enterCombat() {}

    public void exitCombat() {}

    protected void dh() {
        this.updateEffects = true;
    }

    public abstract EnumMainHand getMainHand();

    public boolean isHandRaised() {
        return ((Byte) this.datawatcher.get(EntityLiving.ar) & 1) > 0;
    }

    public EnumHand getRaisedHand() {
        return ((Byte) this.datawatcher.get(EntityLiving.ar) & 2) > 0 ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
    }

    private void o() {
        if (this.isHandRaised()) {
            if (ItemStack.d(this.b(this.getRaisedHand()), this.activeItem)) {
                this.activeItem.b(this.world, this, this.dm());
                if (this.dm() <= 25 && this.dm() % 4 == 0) {
                    this.b(this.activeItem, 5);
                }

                if (--this.bo == 0 && !this.world.isClientSide && !this.activeItem.m()) {
                    this.q();
                }
            } else {
                this.dp();
            }
        }

    }

    private void p() {
        this.bK = this.bJ;
        if (this.bk()) {
            this.bJ = Math.min(1.0F, this.bJ + 0.09F);
        } else {
            this.bJ = Math.max(0.0F, this.bJ - 0.09F);
        }

    }

    protected void c(int i, boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityLiving.ar);
        int j;

        if (flag) {
            j = b0 | i;
        } else {
            j = b0 & ~i;
        }

        this.datawatcher.set(EntityLiving.ar, (byte) j);
    }

    // Paper start -- OBFHELPER and forwarder to method with forceUpdate parameter
    public void c(EnumHand enumhand) { this.updateActiveItem(enumhand, false); }
    public void updateActiveItem(EnumHand enumhand, boolean forceUpdate) {
    // Paper end
        ItemStack itemstack = this.b(enumhand);

        if (!itemstack.isEmpty() && !this.isHandRaised() || forceUpdate) { // Paper use override flag
            this.activeItem = itemstack;
            this.bo = itemstack.k();
            if (!this.world.isClientSide) {
                this.c(1, true);
                this.c(2, enumhand == EnumHand.OFF_HAND);
            }

        }
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityLiving.bs.equals(datawatcherobject)) {
            if (this.world.isClientSide) {
                this.getBedPosition().ifPresent(this::a);
            }
        } else if (EntityLiving.ar.equals(datawatcherobject) && this.world.isClientSide) {
            if (this.isHandRaised() && this.activeItem.isEmpty()) {
                this.activeItem = this.b(this.getRaisedHand());
                if (!this.activeItem.isEmpty()) {
                    this.bo = this.activeItem.k();
                }
            } else if (!this.isHandRaised() && !this.activeItem.isEmpty()) {
                this.activeItem = ItemStack.a;
                this.bo = 0;
            }
        }

    }

    @Override
    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        super.a(argumentanchor_anchor, vec3d);
        this.aN = this.aM;
        this.aK = this.aM;
        this.aL = this.aK;
    }

    protected void b(ItemStack itemstack, int i) {
        if (!itemstack.isEmpty() && this.isHandRaised()) {
            if (itemstack.l() == EnumAnimation.DRINK) {
                this.a(this.c(itemstack), 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }

            if (itemstack.l() == EnumAnimation.EAT) {
                this.a(itemstack, i);
                this.a(this.d(itemstack), 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
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
            this.world.addParticle(new ParticleParamItem(Particles.ITEM, itemstack), vec3d1.x, vec3d1.y, vec3d1.z, vec3d.x, vec3d.y + 0.05D, vec3d.z);
        }

    }

    protected void q() {
        if (!this.activeItem.isEmpty() && this.isHandRaised()) {
            this.updateActiveItem(this.getRaisedHand(), true); // Paper
            PlayerItemConsumeEvent event = null; // Paper
            this.b(this.activeItem, 16);
            // CraftBukkit start - fire PlayerItemConsumeEvent
            ItemStack itemstack;
            if (this instanceof EntityPlayer) {
                org.bukkit.inventory.ItemStack craftItem = CraftItemStack.asBukkitCopy(this.activeItem);
                event = new PlayerItemConsumeEvent((Player) this.getBukkitEntity(), craftItem); // Paper
                world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    // Update client
                    ((EntityPlayer) this).getBukkitEntity().updateInventory();
                    ((EntityPlayer) this).getBukkitEntity().updateScaledHealth();
                    return;
                }

                itemstack = (craftItem.equals(event.getItem())) ? this.activeItem.a(this.world, this) : CraftItemStack.asNMSCopy(event.getItem()).a(world, this);
            } else {
                itemstack = this.activeItem.a(this.world, this);
            }

            // Paper start - save the default replacement item and change it if necessary
            final ItemStack defaultReplacement = itemstack;
            if (event != null && event.getReplacement() != null) {
                itemstack = CraftItemStack.asNMSCopy(event.getReplacement());
            }
            // Paper end
            this.a(this.getRaisedHand(), itemstack);
            // CraftBukkit end
            this.dp();
            // Paper start
            if (this instanceof EntityPlayer) {
                ((EntityPlayer) this).getBukkitEntity().updateInventory();
            }
            // Paper end
        }

    }

    public ItemStack dl() {
        return this.activeItem;
    }

    public int getItemUseRemainingTime() { return this.dm(); } // Paper - OBFHELPER
    public int dm() {
        return this.bo;
    }

    public int getHandRaisedTime() { return this.dn(); } // Paper - OBFHELPER
    public int dn() {
        return this.isHandRaised() ? this.activeItem.k() - this.dm() : 0;
    }

    public void clearActiveItem() {
        if (!this.activeItem.isEmpty()) {
            this.activeItem.a(this.world, this, this.dm());
            if (this.activeItem.m()) {
                this.o();
            }
        }

        this.dp();
    }

    public void dp() {
        if (!this.world.isClientSide) {
            this.c(1, false);
        }

        this.activeItem = ItemStack.a;
        this.bo = 0;
    }

    public boolean isBlocking() {
        if (this.isHandRaised() && !this.activeItem.isEmpty()) {
            Item item = this.activeItem.getItem();

            return item.e_(this.activeItem) != EnumAnimation.BLOCK ? false : item.f_(this.activeItem) - this.bo >= getShieldBlockingDelay(); // Paper - shieldBlockingDelay
        } else {
            return false;
        }
    }

    public boolean isGliding() {
        return this.getFlag(7);
    }

    @Override
    public boolean bk() {
        return super.bk() || !this.isGliding() && this.getPose() == EntityPose.FALL_FLYING;
    }

    public boolean a(double d0, double d1, double d2, boolean flag) {
        double d3 = this.locX;
        double d4 = this.locY;
        double d5 = this.locZ;

        this.locX = d0;
        this.locY = d1;
        this.locZ = d2;
        boolean flag1 = false;
        BlockPosition blockposition = new BlockPosition(this);
        World world = this.world;

        if (world.isLoaded(blockposition)) {
            boolean flag2 = false;

            while (!flag2 && blockposition.getY() > 0) {
                BlockPosition blockposition1 = blockposition.down();
                IBlockData iblockdata = world.getType(blockposition1);

                if (iblockdata.getMaterial().isSolid()) {
                    flag2 = true;
                } else {
                    --this.locY;
                    blockposition = blockposition1;
                }
            }

            if (flag2) {
                // CraftBukkit start - Teleport event
                // this.enderTeleportTo(this.locX, this.locY, this.locZ);
                EntityTeleportEvent teleport = new EntityTeleportEvent(this.getBukkitEntity(), new Location(this.world.getWorld(), d3, d4, d5), new Location(this.world.getWorld(), this.locX, this.locY, this.locZ));
                this.world.getServer().getPluginManager().callEvent(teleport);
                if (!teleport.isCancelled()) {
                    Location to = teleport.getTo();
                    this.enderTeleportTo(to.getX(), to.getY(), to.getZ());
                    if (world.getCubes(this) && !world.containsLiquid(this.getBoundingBox())) {
                        flag1 = true;
                    }
                }
                // CraftBukkit end
            }
        }

        if (!flag1) {
            this.enderTeleportTo(d3, d4, d5);
            return false;
        } else {
            if (flag) {
                world.broadcastEntityEffect(this, (byte) 46);
            }

            if (this instanceof EntityCreature) {
                ((EntityCreature) this).getNavigation().o();
            }

            return true;
        }
    }

    public boolean dt() {
        return true;
    }

    public boolean du() {
        return true;
    }

    public boolean e(ItemStack itemstack) {
        return false;
    }

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntityLiving(this);
    }

    @Override
    public EntitySize a(EntityPose entitypose) {
        return entitypose == EntityPose.SLEEPING ? EntityLiving.as : super.a(entitypose).a(this.cn());
    }

    public Optional<BlockPosition> getBedPosition() {
        return (Optional) this.datawatcher.get(EntityLiving.bs);
    }

    public void d(BlockPosition blockposition) {
        this.datawatcher.set(EntityLiving.bs, Optional.of(blockposition));
    }

    public void dw() {
        this.datawatcher.set(EntityLiving.bs, Optional.empty());
    }

    public boolean isSleeping() {
        return this.getBedPosition().isPresent();
    }

    public void e(BlockPosition blockposition) {
        if (this.isPassenger()) {
            this.stopRiding();
        }

        IBlockData iblockdata = this.world.getType(blockposition);

        if (iblockdata.getBlock() instanceof BlockBed) {
            this.world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBed.OCCUPIED, true), 3);
        }

        this.setPose(EntityPose.SLEEPING);
        this.a(blockposition);
        this.d(blockposition);
        this.setMot(Vec3D.a);
        this.impulse = true;
    }

    private void a(BlockPosition blockposition) {
        this.setPosition((double) blockposition.getX() + 0.5D, (double) ((float) blockposition.getY() + 0.6875F), (double) blockposition.getZ() + 0.5D);
    }

    private boolean r() {
        return (Boolean) this.getBedPosition().map((blockposition) -> {
            return this.world.getType(blockposition).getBlock() instanceof BlockBed;
        }).orElse(false);
    }

    public void dy() {
        Optional<BlockPosition> optional = this.getBedPosition(); // CraftBukkit - decompile error
        World world = this.world;

        this.world.getClass();
        optional.filter(world::isLoaded).ifPresent((blockposition) -> {
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.getBlock() instanceof BlockBed) {
                this.world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockBed.OCCUPIED, false), 3);
                Vec3D vec3d = (Vec3D) BlockBed.a(this.getEntityType(), (IWorldReader) this.world, blockposition, 0).orElseGet(() -> {
                    BlockPosition blockposition1 = blockposition.up();

                    return new Vec3D((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.1D, (double) blockposition1.getZ() + 0.5D);
                });

                this.setPosition(vec3d.x, vec3d.y, vec3d.z);
            }

        });
        this.setPose(EntityPose.STANDING);
        this.dw();
    }

    @Override
    public boolean inBlock() {
        return !this.isSleeping() && super.inBlock();
    }

    @Override
    protected final float getHeadHeight(EntityPose entitypose, EntitySize entitysize) {
        return entitypose == EntityPose.SLEEPING ? 0.2F : this.b(entitypose, entitysize);
    }

    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return super.getHeadHeight(entitypose, entitysize);
    }

    public ItemStack f(ItemStack itemstack) {
        return ItemStack.a;
    }

    public ItemStack a(World world, ItemStack itemstack) {
        if (itemstack.E()) {
            world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, this.d(itemstack), SoundCategory.NEUTRAL, 1.0F, 1.0F + (world.random.nextFloat() - world.random.nextFloat()) * 0.4F);
            this.a(itemstack, world, this);
            itemstack.subtract(1);
        }

        return itemstack;
    }

    private void a(ItemStack itemstack, World world, EntityLiving entityliving) {
        Item item = itemstack.getItem();

        if (item.isFood()) {
            List<Pair<MobEffect, Float>> list = item.getFoodInfo().f();
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Pair<MobEffect, Float> pair = (Pair) iterator.next();

                if (!world.isClientSide && pair.getLeft() != null && world.random.nextFloat() < (Float) pair.getRight()) {
                    entityliving.addEffect(new MobEffect((MobEffect) pair.getLeft()), EntityPotionEffectEvent.Cause.FOOD); // CraftBukkit
                }
            }
        }

    }

    private static byte d(EnumItemSlot enumitemslot) {
        switch (enumitemslot) {
            case MAINHAND:
                return 47;
            case OFFHAND:
                return 48;
            case HEAD:
                return 49;
            case CHEST:
                return 50;
            case FEET:
                return 52;
            case LEGS:
                return 51;
            default:
                return 47;
        }
    }

    public void c(EnumItemSlot enumitemslot) {
        this.world.broadcastEntityEffect(this, d(enumitemslot));
    }

    public void d(EnumHand enumhand) {
        this.c(enumhand == EnumHand.MAIN_HAND ? EnumItemSlot.MAINHAND : EnumItemSlot.OFFHAND);
    }
    // Paper start
    public MovingObjectPosition getRayTrace(int maxDistance) {
        return getRayTrace(maxDistance, RayTrace.FluidCollisionOption.NONE);
    }

    public MovingObjectPosition getRayTrace(int maxDistance, RayTrace.FluidCollisionOption fluidCollisionOption) {
        if (maxDistance < 1 || maxDistance > 120) {
            throw new IllegalArgumentException("maxDistance must be between 1-120");
        }

        Vec3D start = new Vec3D(locX, locY + getHeadHeight(), locZ);
        org.bukkit.util.Vector dir = getBukkitEntity().getLocation().getDirection().multiply(maxDistance);
        Vec3D end = new Vec3D(start.x + dir.getX(), start.y + dir.getY(), start.z + dir.getZ());
        RayTrace raytrace = new RayTrace(start, end, RayTrace.BlockCollisionOption.OUTLINE, fluidCollisionOption, this);

        return world.rayTrace(raytrace);
    }

    public MovingObjectPositionEntity getTargetEntity(int maxDistance) {
        if (maxDistance < 1 || maxDistance > 120) {
            throw new IllegalArgumentException("maxDistance must be between 1-120");
        }

        Vec3D start = this.getEyePosition(1.0F);
        Vec3D direction = this.getLookDirection();
        Vec3D end = start.add(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance);

        List<Entity> entityList = world.getEntities(this, getBoundingBox().expand(direction.x * maxDistance, direction.y * maxDistance, direction.z * maxDistance).grow(1.0D, 1.0D, 1.0D), IEntitySelector.notSpectator().and(Entity::isInteractable));

        double distance = 0.0D;
        MovingObjectPositionEntity result = null;

        for (Entity entity : entityList) {
            AxisAlignedBB aabb = entity.getBoundingBox().grow((double) entity.getCollisionBorderSize());
            Optional<Vec3D> rayTraceResult = aabb.calculateIntercept(start, end);

            if (rayTraceResult.isPresent()) {
                Vec3D rayTrace = rayTraceResult.get();
                double distanceTo = start.distanceSquared(rayTrace);
                if (distanceTo < distance || distance == 0.0D) {
                    result = new MovingObjectPositionEntity(entity, rayTrace);
                    distance = distanceTo;
                }
            }
        }

        return result;
    }

    public int shieldBlockingDelay = world.paperConfig.shieldBlockingDelay;

    public int getShieldBlockingDelay() {
        return shieldBlockingDelay;
    }

    public void setShieldBlockingDelay(int shieldBlockingDelay) {
        this.shieldBlockingDelay = shieldBlockingDelay;
    }
    // Paper end
}
