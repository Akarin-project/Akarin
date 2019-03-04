package net.minecraft.server;

import javax.annotation.Nullable;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityItemFrame extends EntityHanging {

    private static final Logger d = LogManager.getLogger();
    private static final DataWatcherObject<ItemStack> e = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.g);
    private static final DataWatcherObject<Integer> f = DataWatcher.a(EntityItemFrame.class, DataWatcherRegistry.b);
    private float g = 1.0F;

    public EntityItemFrame(World world) {
        super(EntityTypes.ITEM_FRAME, world);
    }

    public EntityItemFrame(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        super(EntityTypes.ITEM_FRAME, world, blockposition);
        this.setDirection(enumdirection);
    }

    public float getHeadHeight() {
        return 0.0F;
    }

    protected void x_() {
        this.getDataWatcher().register(EntityItemFrame.e, ItemStack.a);
        this.getDataWatcher().register(EntityItemFrame.f, 0);
    }

    public void setDirection(EnumDirection enumdirection) {
        Validate.notNull(enumdirection);
        this.direction = enumdirection;
        if (enumdirection.k().c()) {
            this.pitch = 0.0F;
            this.yaw = (float) (this.direction.get2DRotationValue() * 90);
        } else {
            this.pitch = (float) (-90 * enumdirection.c().a());
            this.yaw = 0.0F;
        }

        this.lastPitch = this.pitch;
        this.lastYaw = this.yaw;
        this.updateBoundingBox();
    }

    protected void updateBoundingBox() {
        if (this.direction != null) {
            double d0 = 0.46875D;

            this.locX = (double) this.blockPosition.getX() + 0.5D - (double) this.direction.getAdjacentX() * 0.46875D;
            this.locY = (double) this.blockPosition.getY() + 0.5D - (double) this.direction.getAdjacentY() * 0.46875D;
            this.locZ = (double) this.blockPosition.getZ() + 0.5D - (double) this.direction.getAdjacentZ() * 0.46875D;
            double d1 = (double) this.getWidth();
            double d2 = (double) this.getHeight();
            double d3 = (double) this.getWidth();
            EnumDirection.EnumAxis enumdirection_enumaxis = this.direction.k();

            switch (enumdirection_enumaxis) {
            case X:
                d1 = 1.0D;
                break;
            case Y:
                d2 = 1.0D;
                break;
            case Z:
                d3 = 1.0D;
            }

            d1 /= 32.0D;
            d2 /= 32.0D;
            d3 /= 32.0D;
            this.a(new AxisAlignedBB(this.locX - d1, this.locY - d2, this.locZ - d3, this.locX + d1, this.locY + d2, this.locZ + d3));
        }
    }

    public boolean survives() {
        if (!this.world.getCubes(this, this.getBoundingBox())) {
            return false;
        } else {
            IBlockData iblockdata = this.world.getType(this.blockPosition.shift(this.direction.opposite()));

            return !iblockdata.getMaterial().isBuildable() && (!this.direction.k().c() || !BlockDiodeAbstract.isDiode(iblockdata)) ? false : this.world.getEntities(this, this.getBoundingBox(), EntityItemFrame.a).isEmpty();
        }
    }

    public float aM() {
        return 0.0F;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!damagesource.isExplosion() && !this.getItem().isEmpty()) {
            if (!this.world.isClientSide) {
                // CraftBukkit start - fire EntityDamageEvent
                if (org.bukkit.craftbukkit.event.CraftEventFactory.handleNonLivingEntityDamageEvent(this, damagesource, f, false) || this.dead) {
                    return true;
                }
                // CraftBukkit end
                this.b(damagesource.getEntity(), false);
                this.a(SoundEffects.ENTITY_ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.damageEntity(damagesource, f);
        }
    }

    public int getWidth() {
        return 12;
    }

    public int getHeight() {
        return 12;
    }

    public void a(@Nullable Entity entity) {
        this.a(SoundEffects.ENTITY_ITEM_FRAME_BREAK, 1.0F, 1.0F);
        this.b(entity, true);
    }

    public void m() {
        this.a(SoundEffects.ENTITY_ITEM_FRAME_PLACE, 1.0F, 1.0F);
    }

    public void b(@Nullable Entity entity, boolean flag) {
        if (this.world.getGameRules().getBoolean("doEntityDrops")) {
            ItemStack itemstack = this.getItem();

            this.setItem(ItemStack.a);
            if (entity instanceof EntityHuman) {
                EntityHuman entityhuman = (EntityHuman) entity;

                if (entityhuman.abilities.canInstantlyBuild) {
                    this.c(itemstack);
                    return;
                }
            }

            if (flag) {
                this.a((IMaterial) Items.ITEM_FRAME);
            }

            if (!itemstack.isEmpty() && this.random.nextFloat() < this.g) {
                itemstack = itemstack.cloneItemStack();
                this.c(itemstack);
                this.a_(itemstack);
            }

        }
    }

    private void c(ItemStack itemstack) {
        if (itemstack.getItem() == Items.FILLED_MAP) {
            WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.world);

            worldmap.a(this.blockPosition, this.getId());
        }

        itemstack.a((EntityItemFrame) null);
    }

    public ItemStack getItem() {
        return (ItemStack) this.getDataWatcher().get(EntityItemFrame.e);
    }

    public void setItem(ItemStack itemstack) {
        this.setItem(itemstack, true);
    }

    public void setItem(ItemStack itemstack, boolean flag) {
        // CraftBukkit start
        this.setItem(itemstack, flag, true);
    }

    public void setItem(ItemStack itemstack, boolean flag, boolean playSound) {
        // CraftBukkit end
        if (!itemstack.isEmpty()) {
            itemstack = itemstack.cloneItemStack();
            itemstack.setCount(1);
            itemstack.a(this);
        }

        this.getDataWatcher().set(EntityItemFrame.e, itemstack);
        if (!itemstack.isEmpty() && playSound) { // CraftBukkit
            this.a(SoundEffects.ENTITY_ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
        }

        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (datawatcherobject.equals(EntityItemFrame.e)) {
            ItemStack itemstack = this.getItem();

            if (!itemstack.isEmpty() && itemstack.y() != this) {
                itemstack.a(this);
            }
        }

    }

    public int getRotation() {
        return (Integer) this.getDataWatcher().get(EntityItemFrame.f);
    }

    public void setRotation(int i) {
        this.setRotation(i, true);
    }

    private void setRotation(int i, boolean flag) {
        this.getDataWatcher().set(EntityItemFrame.f, i % 8);
        if (flag && this.blockPosition != null) {
            this.world.updateAdjacentComparators(this.blockPosition, Blocks.AIR);
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (!this.getItem().isEmpty()) {
            nbttagcompound.set("Item", this.getItem().save(new NBTTagCompound()));
            nbttagcompound.setByte("ItemRotation", (byte) this.getRotation());
            nbttagcompound.setFloat("ItemDropChance", this.g);
        }

        nbttagcompound.setByte("Facing", (byte) this.direction.a());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        if (nbttagcompound1 != null && !nbttagcompound1.isEmpty()) {
            ItemStack itemstack = ItemStack.a(nbttagcompound1);

            if (itemstack.isEmpty()) {
                EntityItemFrame.d.warn("Unable to load item from: {}", nbttagcompound1);
            }

            this.setItem(itemstack, false);
            this.setRotation(nbttagcompound.getByte("ItemRotation"), false);
            if (nbttagcompound.hasKeyOfType("ItemDropChance", 99)) {
                this.g = nbttagcompound.getFloat("ItemDropChance");
            }
        }

        this.setDirection(EnumDirection.fromType1(nbttagcompound.getByte("Facing")));
    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (!this.world.isClientSide) {
            if (this.getItem().isEmpty()) {
                if (!itemstack.isEmpty()) {
                    this.setItem(itemstack);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }
                }
            } else {
                this.a(SoundEffects.ENTITY_ITEM_FRAME_ROTATE_ITEM, 1.0F, 1.0F);
                this.setRotation(this.getRotation() + 1);
            }
        }

        return true;
    }

    public int q() {
        return this.getItem().isEmpty() ? 0 : this.getRotation() % 8 + 1;
    }
}
