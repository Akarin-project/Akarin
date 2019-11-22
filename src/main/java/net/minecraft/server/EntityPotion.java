package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import java.util.HashMap;
import java.util.Map;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
// CraftBukkit end

public class EntityPotion extends EntityProjectile {

    private static final DataWatcherObject<ItemStack> f = DataWatcher.a(EntityPotion.class, DataWatcherRegistry.g);
    private static final Logger LOGGER = LogManager.getLogger();
    public static final Predicate<EntityLiving> e = EntityPotion::a;

    public EntityPotion(EntityTypes<? extends EntityPotion> entitytypes, World world) {
        super(entitytypes, world);
    }

    public EntityPotion(World world, EntityLiving entityliving) {
        super(EntityTypes.POTION, entityliving, world);
    }

    public EntityPotion(World world, double d0, double d1, double d2) {
        super(EntityTypes.POTION, d0, d1, d2, world);
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityPotion.f, ItemStack.a);
    }

    public ItemStack getItem() {
        ItemStack itemstack = (ItemStack) this.getDataWatcher().get(EntityPotion.f);

        if (itemstack.getItem() != Items.SPLASH_POTION && itemstack.getItem() != Items.LINGERING_POTION) {
            if (this.world != null) {
                EntityPotion.LOGGER.error("ThrownPotion entity {} has no item?!", this.getId());
            }

            return new ItemStack(Items.SPLASH_POTION);
        } else {
            return itemstack;
        }
    }

    public void setItem(ItemStack itemstack) {
        this.getDataWatcher().set(EntityPotion.f, itemstack.cloneItemStack());
    }

    @Override
    protected float l() {
        return 0.05F;
    }

    @Override
    protected void a(MovingObjectPosition movingobjectposition) {
        if (!this.world.isClientSide) {
            ItemStack itemstack = this.getItem();
            PotionRegistry potionregistry = PotionUtil.d(itemstack);
            List<MobEffect> list = PotionUtil.getEffects(itemstack);
            boolean flag = potionregistry == Potions.WATER && list.isEmpty();

            if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK && flag) {
                MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) movingobjectposition;
                EnumDirection enumdirection = movingobjectpositionblock.getDirection();
                BlockPosition blockposition = movingobjectpositionblock.getBlockPosition().shift(enumdirection);

                this.a(blockposition, enumdirection);
                this.a(blockposition.shift(enumdirection.opposite()), enumdirection);
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection1 = (EnumDirection) iterator.next();

                    this.a(blockposition.shift(enumdirection1), enumdirection1);
                }
            }

            if (flag) {
                this.splash();
            } else if (true || !list.isEmpty()) { // CraftBukkit - Call event even if no effects to apply
                if (this.isLingering()) {
                    this.a(itemstack, potionregistry);
                } else {
                    this.a(list, movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY ? ((MovingObjectPositionEntity) movingobjectposition).getEntity() : null);
                }
            }

            int i = potionregistry.b() ? 2007 : 2002;

            this.world.triggerEffect(i, new BlockPosition(this), PotionUtil.c(itemstack));
            this.die();
        }
    }

    private void splash() {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLiving> list = this.world.a(EntityLiving.class, axisalignedbb, EntityPotion.e);

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();
                double d0 = this.h(entityliving);

                if (d0 < 16.0D && a(entityliving)) {
                    entityliving.damageEntity(DamageSource.c(entityliving, this.getShooter()), 1.0F);
                }
            }
        }

    }

    private void a(List<MobEffect> list, @Nullable Entity entity) {
        AxisAlignedBB axisalignedbb = this.getBoundingBox().grow(4.0D, 2.0D, 4.0D);
        List<EntityLiving> list1 = this.world.a(EntityLiving.class, axisalignedbb);
        Map<LivingEntity, Double> affected = new HashMap<LivingEntity, Double>(); // CraftBukkit

        if (!list1.isEmpty()) {
            Iterator iterator = list1.iterator();

            while (iterator.hasNext()) {
                EntityLiving entityliving = (EntityLiving) iterator.next();

                if (entityliving.dt()) {
                    double d0 = this.h(entityliving);

                    if (d0 < 16.0D) {
                        double d1 = 1.0D - Math.sqrt(d0) / 4.0D;

                        if (entityliving == entity) {
                            d1 = 1.0D;
                        }

                        // CraftBukkit start
                        affected.put((LivingEntity) entityliving.getBukkitEntity(), d1);
                    }
                }
            }
        }

        org.bukkit.event.entity.PotionSplashEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPotionSplashEvent(this, affected);
        if (!event.isCancelled() && list != null && !list.isEmpty()) { // do not process effects if there are no effects to process
            for (LivingEntity victim : event.getAffectedEntities()) {
                if (!(victim instanceof CraftLivingEntity)) {
                    continue;
                }

                EntityLiving entityliving = ((CraftLivingEntity) victim).getHandle();
                double d1 = event.getIntensity(victim);
                // CraftBukkit end

                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    MobEffect mobeffect = (MobEffect) iterator1.next();
                    MobEffectList mobeffectlist = mobeffect.getMobEffect();
                    // CraftBukkit start - Abide by PVP settings - for players only!
                    if (!this.world.pvpMode && this.getShooter() instanceof EntityPlayer && entityliving instanceof EntityPlayer && entityliving != this.getShooter()) {
                        int i = MobEffectList.getId(mobeffectlist);
                        // Block SLOWER_MOVEMENT, SLOWER_DIG, HARM, BLINDNESS, HUNGER, WEAKNESS and POISON potions
                        if (i == 2 || i == 4 || i == 7 || i == 15 || i == 17 || i == 18 || i == 19) {
                            continue;
                        }
                    }
                    // CraftBukkit end

                    if (mobeffectlist.isInstant()) {
                        mobeffectlist.applyInstantEffect(this, this.getShooter(), entityliving, mobeffect.getAmplifier(), d1);
                    } else {
                        int i = (int) (d1 * (double) mobeffect.getDuration() + 0.5D);

                        if (i > 20) {
                            entityliving.addEffect(new MobEffect(mobeffectlist, i, mobeffect.getAmplifier(), mobeffect.isAmbient(), mobeffect.isShowParticles()), org.bukkit.event.entity.EntityPotionEffectEvent.Cause.POTION_SPLASH); // CraftBukkit
                        }
                    }
                }
            }
        }

    }

    private void a(ItemStack itemstack, PotionRegistry potionregistry) {
        EntityAreaEffectCloud entityareaeffectcloud = new EntityAreaEffectCloud(this.world, this.locX, this.locY, this.locZ);

        entityareaeffectcloud.setSource(this.getShooter());
        entityareaeffectcloud.setRadius(3.0F);
        entityareaeffectcloud.setRadiusOnUse(-0.5F);
        entityareaeffectcloud.setWaitTime(10);
        entityareaeffectcloud.setRadiusPerTick(-entityareaeffectcloud.getRadius() / (float) entityareaeffectcloud.getDuration());
        entityareaeffectcloud.a(potionregistry);
        Iterator iterator = PotionUtil.b(itemstack).iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            entityareaeffectcloud.addEffect(new MobEffect(mobeffect));
        }

        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("CustomPotionColor", 99)) {
            entityareaeffectcloud.setColor(nbttagcompound.getInt("CustomPotionColor"));
        }

        // CraftBukkit start
        org.bukkit.event.entity.LingeringPotionSplashEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callLingeringPotionSplashEvent(this, entityareaeffectcloud);
        if (!(event.isCancelled() || entityareaeffectcloud.dead)) {
            this.world.addEntity(entityareaeffectcloud);
        } else {
            entityareaeffectcloud.dead = true;
        }
        // CraftBukkit end
    }

    public boolean isLingering() {
        return this.getItem().getItem() == Items.LINGERING_POTION;
    }

    private void a(BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = this.world.getType(blockposition);
        Block block = iblockdata.getBlock();

        if (block == Blocks.FIRE) {
            this.world.douseFire((EntityHuman) null, blockposition.shift(enumdirection), enumdirection.opposite());
        } else if (block == Blocks.CAMPFIRE && (Boolean) iblockdata.get(BlockCampfire.b)) {
            this.world.a((EntityHuman) null, 1009, blockposition, 0);
            this.world.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(BlockCampfire.b, false));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("Potion"));

        if (itemstack.isEmpty()) {
            this.die();
        } else {
            this.setItem(itemstack);
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        ItemStack itemstack = this.getItem();

        if (!itemstack.isEmpty()) {
            nbttagcompound.set("Potion", itemstack.save(new NBTTagCompound()));
        }

    }

    private static boolean a(EntityLiving entityliving) {
        return entityliving instanceof EntityEnderman || entityliving instanceof EntityBlaze;
    }
}
