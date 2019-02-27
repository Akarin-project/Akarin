package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
// CraftBukkit end

public class EntityAreaEffectCloud extends Entity {

    private static final Logger a = LogManager.getLogger();
    private static final DataWatcherObject<Float> b = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.c);
    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Boolean> d = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<ParticleParam> e = DataWatcher.a(EntityAreaEffectCloud.class, DataWatcherRegistry.j);
    private PotionRegistry potionRegistry;
    public List<MobEffect> effects;
    private final Map<Entity, Integer> h;
    private int aw;
    public int waitTime;
    public int reapplicationDelay;
    private boolean hasColor;
    public int durationOnUse;
    public float radiusOnUse;
    public float radiusPerTick;
    private EntityLiving aD;
    private UUID aE;

    public EntityAreaEffectCloud(World world) {
        super(EntityTypes.AREA_EFFECT_CLOUD, world);
        this.potionRegistry = Potions.EMPTY;
        this.effects = Lists.newArrayList();
        this.h = Maps.newHashMap();
        this.aw = 600;
        this.waitTime = 20;
        this.reapplicationDelay = 20;
        this.noclip = true;
        this.fireProof = true;
        this.setRadius(3.0F);
    }

    public EntityAreaEffectCloud(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
    }

    protected void x_() {
        this.getDataWatcher().register(EntityAreaEffectCloud.c, 0);
        this.getDataWatcher().register(EntityAreaEffectCloud.b, 0.5F);
        this.getDataWatcher().register(EntityAreaEffectCloud.d, false);
        this.getDataWatcher().register(EntityAreaEffectCloud.e, Particles.s);
    }

    public void setRadius(float f) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;

        this.setSize(f * 2.0F, 0.5F);
        this.setPosition(d0, d1, d2);
        if (!this.world.isClientSide) {
            this.getDataWatcher().set(EntityAreaEffectCloud.b, f);
        }

    }

    public float getRadius() {
        return (Float) this.getDataWatcher().get(EntityAreaEffectCloud.b);
    }

    public void a(PotionRegistry potionregistry) {
        this.potionRegistry = potionregistry;
        if (!this.hasColor) {
            this.x();
        }

    }

    private void x() {
        if (this.potionRegistry == Potions.EMPTY && this.effects.isEmpty()) {
            this.getDataWatcher().set(EntityAreaEffectCloud.c, 0);
        } else {
            this.getDataWatcher().set(EntityAreaEffectCloud.c, PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects)));
        }

    }

    public void a(MobEffect mobeffect) {
        this.effects.add(mobeffect);
        if (!this.hasColor) {
            this.x();
        }

    }

    // CraftBukkit start accessor methods
    public void refreshEffects() {
        if (!this.hasColor) {
            this.getDataWatcher().set(EntityAreaEffectCloud.c, Integer.valueOf(PotionUtil.a((Collection) PotionUtil.a(this.potionRegistry, (Collection) this.effects)))); // PAIL: rename
        }
    }

    public String getType() {
        return ((MinecraftKey) IRegistry.POTION.getKey(this.potionRegistry)).toString();
    }

    public void setType(String string) {
        a(IRegistry.POTION.get(new MinecraftKey(string)));
    }
    // CraftBukkit end

    public int getColor() {
        return (Integer) this.getDataWatcher().get(EntityAreaEffectCloud.c);
    }

    public void setColor(int i) {
        this.hasColor = true;
        this.getDataWatcher().set(EntityAreaEffectCloud.c, i);
    }

    public ParticleParam getParticle() {
        return (ParticleParam) this.getDataWatcher().get(EntityAreaEffectCloud.e);
    }

    public void setParticle(ParticleParam particleparam) {
        this.getDataWatcher().set(EntityAreaEffectCloud.e, particleparam);
    }

    protected void a(boolean flag) {
        this.getDataWatcher().set(EntityAreaEffectCloud.d, flag);
    }

    public boolean l() {
        return (Boolean) this.getDataWatcher().get(EntityAreaEffectCloud.d);
    }

    public int getDuration() {
        return this.aw;
    }

    public void setDuration(int i) {
        this.aw = i;
    }

    public void tick() {
        super.tick();
        boolean flag = this.l();
        float f = this.getRadius();

        if (this.world.isClientSide) {
            ParticleParam particleparam = this.getParticle();
            float f1;
            float f2;
            float f3;
            int i;
            int j;
            int k;

            if (flag) {
                if (this.random.nextBoolean()) {
                    for (int l = 0; l < 2; ++l) {
                        float f4 = this.random.nextFloat() * 6.2831855F;

                        f1 = MathHelper.c(this.random.nextFloat()) * 0.2F;
                        f2 = MathHelper.cos(f4) * f1;
                        f3 = MathHelper.sin(f4) * f1;
                        if (particleparam.b() == Particles.s) {
                            int i1 = this.random.nextBoolean() ? 16777215 : this.getColor();

                            i = i1 >> 16 & 255;
                            j = i1 >> 8 & 255;
                            k = i1 & 255;
                            this.world.b(particleparam, this.locX + (double) f2, this.locY, this.locZ + (double) f3, (double) ((float) i / 255.0F), (double) ((float) j / 255.0F), (double) ((float) k / 255.0F));
                        } else {
                            this.world.b(particleparam, this.locX + (double) f2, this.locY, this.locZ + (double) f3, 0.0D, 0.0D, 0.0D);
                        }
                    }
                }
            } else {
                float f5 = 3.1415927F * f * f;

                for (int j1 = 0; (float) j1 < f5; ++j1) {
                    f1 = this.random.nextFloat() * 6.2831855F;
                    f2 = MathHelper.c(this.random.nextFloat()) * f;
                    f3 = MathHelper.cos(f1) * f2;
                    float f6 = MathHelper.sin(f1) * f2;

                    if (particleparam.b() == Particles.s) {
                        i = this.getColor();
                        j = i >> 16 & 255;
                        k = i >> 8 & 255;
                        int k1 = i & 255;

                        this.world.b(particleparam, this.locX + (double) f3, this.locY, this.locZ + (double) f6, (double) ((float) j / 255.0F), (double) ((float) k / 255.0F), (double) ((float) k1 / 255.0F));
                    } else {
                        this.world.b(particleparam, this.locX + (double) f3, this.locY, this.locZ + (double) f6, (0.5D - this.random.nextDouble()) * 0.15D, 0.009999999776482582D, (0.5D - this.random.nextDouble()) * 0.15D);
                    }
                }
            }
        } else {
            if (this.ticksLived >= this.waitTime + this.aw) {
                this.die();
                return;
            }

            boolean flag1 = this.ticksLived < this.waitTime;

            if (flag != flag1) {
                this.a(flag1);
            }

            if (flag1) {
                return;
            }

            if (this.radiusPerTick != 0.0F) {
                f += this.radiusPerTick;
                if (f < 0.5F) {
                    this.die();
                    return;
                }

                this.setRadius(f);
            }

            if (this.ticksLived % 5 == 0) {
                Iterator iterator = this.h.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<Entity, Integer> entry = (Entry) iterator.next();

                    if (this.ticksLived >= (Integer) entry.getValue()) {
                        iterator.remove();
                    }
                }

                List<MobEffect> list = Lists.newArrayList();
                Iterator iterator1 = this.potionRegistry.a().iterator();

                while (iterator1.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator1.next();

                    list.add(new MobEffect(mobeffect.getMobEffect(), mobeffect.getDuration() / 4, mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()));
                }

                list.addAll(this.effects);
                if (list.isEmpty()) {
                    this.h.clear();
                } else {
                    List<EntityLiving> list1 = this.world.a(EntityLiving.class, this.getBoundingBox());

                    if (!list1.isEmpty()) {
                        Iterator iterator2 = list1.iterator();

                        List<LivingEntity> entities = new java.util.ArrayList<LivingEntity>(); // CraftBukkit
                        while (iterator2.hasNext()) {
                            EntityLiving entityliving = (EntityLiving) iterator2.next();

                            if (!this.h.containsKey(entityliving) && entityliving.de()) {
                                double d0 = entityliving.locX - this.locX;
                                double d1 = entityliving.locZ - this.locZ;
                                double d2 = d0 * d0 + d1 * d1;

                                if (d2 <= (double) (f * f)) {
                                    // CraftBukkit start
                                    entities.add((LivingEntity) entityliving.getBukkitEntity());
                                }
                            }
                        }
                        org.bukkit.event.entity.AreaEffectCloudApplyEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callAreaEffectCloudApplyEvent(this, entities);
                        if (!event.isCancelled()) {
                            for (LivingEntity entity : event.getAffectedEntities()) {
                                if (entity instanceof CraftLivingEntity) {
                                    EntityLiving entityliving = ((CraftLivingEntity) entity).getHandle();
                                    // CraftBukkit end
                                    this.h.put(entityliving, this.ticksLived + this.reapplicationDelay);
                                    Iterator iterator3 = list.iterator();

                                    while (iterator3.hasNext()) {
                                        MobEffect mobeffect1 = (MobEffect) iterator3.next();

                                        if (mobeffect1.getMobEffect().isInstant()) {
                                            mobeffect1.getMobEffect().applyInstantEffect(this, this.getSource(), entityliving, mobeffect1.getAmplifier(), 0.5D);
                                        } else {
                                            entityliving.addEffect(new MobEffect(mobeffect1), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD); // CraftBukkit
                                        }
                                    }

                                    if (this.radiusOnUse != 0.0F) {
                                        f += this.radiusOnUse;
                                        if (f < 0.5F) {
                                            this.die();
                                            return;
                                        }

                                        this.setRadius(f);
                                    }

                                    if (this.durationOnUse != 0) {
                                        this.aw += this.durationOnUse;
                                        if (this.aw <= 0) {
                                            this.die();
                                            return;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public void setRadiusOnUse(float f) {
        this.radiusOnUse = f;
    }

    public void setRadiusPerTick(float f) {
        this.radiusPerTick = f;
    }

    public void setWaitTime(int i) {
        this.waitTime = i;
    }

    public void setSource(@Nullable EntityLiving entityliving) {
        this.aD = entityliving;
        this.aE = entityliving == null ? null : entityliving.getUniqueID();
    }

    @Nullable
    public EntityLiving getSource() {
        if (this.aD == null && this.aE != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntity(this.aE);

            if (entity instanceof EntityLiving) {
                this.aD = (EntityLiving) entity;
            }
        }

        return this.aD;
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.ticksLived = nbttagcompound.getInt("Age");
        this.aw = nbttagcompound.getInt("Duration");
        this.waitTime = nbttagcompound.getInt("WaitTime");
        this.reapplicationDelay = nbttagcompound.getInt("ReapplicationDelay");
        this.durationOnUse = nbttagcompound.getInt("DurationOnUse");
        this.radiusOnUse = nbttagcompound.getFloat("RadiusOnUse");
        this.radiusPerTick = nbttagcompound.getFloat("RadiusPerTick");
        this.setRadius(nbttagcompound.getFloat("Radius"));
        this.aE = nbttagcompound.a("OwnerUUID");
        if (nbttagcompound.hasKeyOfType("Particle", 8)) {
            try {
                this.setParticle(ArgumentParticle.b(new StringReader(nbttagcompound.getString("Particle"))));
            } catch (CommandSyntaxException commandsyntaxexception) {
                EntityAreaEffectCloud.a.warn("Couldn't load custom particle {}", nbttagcompound.getString("Particle"), commandsyntaxexception);
            }
        }

        if (nbttagcompound.hasKeyOfType("Color", 99)) {
            this.setColor(nbttagcompound.getInt("Color"));
        }

        if (nbttagcompound.hasKeyOfType("Potion", 8)) {
            this.a(PotionUtil.c(nbttagcompound));
        }

        if (nbttagcompound.hasKeyOfType("Effects", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Effects", 10);

            this.effects.clear();

            for (int i = 0; i < nbttaglist.size(); ++i) {
                MobEffect mobeffect = MobEffect.b(nbttaglist.getCompound(i));

                if (mobeffect != null) {
                    this.a(mobeffect);
                }
            }
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Age", this.ticksLived);
        nbttagcompound.setInt("Duration", this.aw);
        nbttagcompound.setInt("WaitTime", this.waitTime);
        nbttagcompound.setInt("ReapplicationDelay", this.reapplicationDelay);
        nbttagcompound.setInt("DurationOnUse", this.durationOnUse);
        nbttagcompound.setFloat("RadiusOnUse", this.radiusOnUse);
        nbttagcompound.setFloat("RadiusPerTick", this.radiusPerTick);
        nbttagcompound.setFloat("Radius", this.getRadius());
        nbttagcompound.setString("Particle", this.getParticle().a());
        if (this.aE != null) {
            nbttagcompound.a("OwnerUUID", this.aE);
        }

        if (this.hasColor) {
            nbttagcompound.setInt("Color", this.getColor());
        }

        if (this.potionRegistry != Potions.EMPTY && this.potionRegistry != null) {
            nbttagcompound.setString("Potion", IRegistry.POTION.getKey(this.potionRegistry).toString());
        }

        if (!this.effects.isEmpty()) {
            NBTTagList nbttaglist = new NBTTagList();
            Iterator iterator = this.effects.iterator();

            while (iterator.hasNext()) {
                MobEffect mobeffect = (MobEffect) iterator.next();

                nbttaglist.add((NBTBase) mobeffect.a(new NBTTagCompound()));
            }

            nbttagcompound.set("Effects", nbttaglist);
        }

    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityAreaEffectCloud.b.equals(datawatcherobject)) {
            this.setRadius(this.getRadius());
        }

        super.a(datawatcherobject);
    }

    public EnumPistonReaction getPushReaction() {
        return EnumPistonReaction.IGNORE;
    }
}
