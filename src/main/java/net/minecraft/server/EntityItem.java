package net.minecraft.server;

import java.util.Iterator;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityItem extends Entity {

    private static final DataWatcherObject<ItemStack> b = DataWatcher.a(EntityItem.class, DataWatcherRegistry.g);
    private int age;
    public int pickupDelay;
    private int e;
    private UUID f;
    private UUID g;
    public float a;

    public EntityItem(World world) {
        super(EntityTypes.ITEM, world);
        this.e = 5;
        this.a = (float) (Math.random() * 3.141592653589793D * 2.0D);
        this.setSize(0.25F, 0.25F);
    }

    public EntityItem(World world, double d0, double d1, double d2) {
        this(world);
        this.setPosition(d0, d1, d2);
        this.yaw = (float) (Math.random() * 360.0D);
        this.motX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motY = 0.20000000298023224D;
        this.motZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D));
    }

    public EntityItem(World world, double d0, double d1, double d2, ItemStack itemstack) {
        this(world, d0, d1, d2);
        this.setItemStack(itemstack);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void x_() {
        this.getDataWatcher().register(EntityItem.b, ItemStack.a);
    }

    public void tick() {
        if (this.getItemStack().isEmpty()) {
            this.die();
        } else {
            super.tick();
            if (this.pickupDelay > 0 && this.pickupDelay != 32767) {
                --this.pickupDelay;
            }

            this.lastX = this.locX;
            this.lastY = this.locY;
            this.lastZ = this.locZ;
            double d0 = this.motX;
            double d1 = this.motY;
            double d2 = this.motZ;

            if (this.a(TagsFluid.WATER)) {
                this.u();
            } else if (!this.isNoGravity()) {
                this.motY -= 0.03999999910593033D;
            }

            if (this.world.isClientSide) {
                this.noclip = false;
            } else {
                this.noclip = this.i(this.locX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.locZ);
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            boolean flag = (int) this.lastX != (int) this.locX || (int) this.lastY != (int) this.locY || (int) this.lastZ != (int) this.locZ;

            if (flag || this.ticksLived % 25 == 0) {
                if (this.world.getFluid(new BlockPosition(this)).a(TagsFluid.LAVA)) {
                    this.motY = 0.20000000298023224D;
                    this.motX = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                    this.motZ = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
                    this.a(SoundEffects.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
                }

                if (!this.world.isClientSide) {
                    this.v();
                }
            }

            float f = 0.98F;

            if (this.onGround) {
                f = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.locZ))).getBlock().n() * 0.98F;
            }

            this.motX *= (double) f;
            this.motY *= 0.9800000190734863D;
            this.motZ *= (double) f;
            if (this.onGround) {
                this.motY *= -0.5D;
            }

            if (this.age != -32768) {
                ++this.age;
            }

            this.impulse |= this.at();
            if (!this.world.isClientSide) {
                double d3 = this.motX - d0;
                double d4 = this.motY - d1;
                double d5 = this.motZ - d2;
                double d6 = d3 * d3 + d4 * d4 + d5 * d5;

                if (d6 > 0.01D) {
                    this.impulse = true;
                }
            }

            if (!this.world.isClientSide && this.age >= 6000) {
                this.die();
            }

        }
    }

    private void u() {
        if (this.motY < 0.05999999865889549D) {
            this.motY += 5.000000237487257E-4D;
        }

        this.motX *= 0.9900000095367432D;
        this.motZ *= 0.9900000095367432D;
    }

    private void v() {
        Iterator iterator = this.world.a(EntityItem.class, this.getBoundingBox().grow(0.5D, 0.0D, 0.5D)).iterator();

        while (iterator.hasNext()) {
            EntityItem entityitem = (EntityItem) iterator.next();

            this.a(entityitem);
        }

    }

    private boolean a(EntityItem entityitem) {
        if (entityitem == this) {
            return false;
        } else if (entityitem.isAlive() && this.isAlive()) {
            ItemStack itemstack = this.getItemStack();
            ItemStack itemstack1 = entityitem.getItemStack().cloneItemStack();

            if (this.pickupDelay != 32767 && entityitem.pickupDelay != 32767) {
                if (this.age != -32768 && entityitem.age != -32768) {
                    if (itemstack1.getItem() != itemstack.getItem()) {
                        return false;
                    } else if (itemstack1.hasTag() ^ itemstack.hasTag()) {
                        return false;
                    } else if (itemstack1.hasTag() && !itemstack1.getTag().equals(itemstack.getTag())) {
                        return false;
                    } else if (itemstack1.getItem() == null) {
                        return false;
                    } else if (itemstack1.getCount() < itemstack.getCount()) {
                        return entityitem.a(this);
                    } else if (itemstack1.getCount() + itemstack.getCount() > itemstack1.getMaxStackSize()) {
                        return false;
                    } else {
                        itemstack1.add(itemstack.getCount());
                        entityitem.pickupDelay = Math.max(entityitem.pickupDelay, this.pickupDelay);
                        entityitem.age = Math.min(entityitem.age, this.age);
                        entityitem.setItemStack(itemstack1);
                        this.die();
                        return true;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public void f() {
        this.age = 4800;
    }

    protected void burn(int i) {
        this.damageEntity(DamageSource.FIRE, (float) i);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else if (!this.getItemStack().isEmpty() && this.getItemStack().getItem() == Items.NETHER_STAR && damagesource.isExplosion()) {
            return false;
        } else {
            this.aA();
            this.e = (int) ((float) this.e - f);
            if (this.e <= 0) {
                this.die();
            }

            return false;
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) this.e);
        nbttagcompound.setShort("Age", (short) this.age);
        nbttagcompound.setShort("PickupDelay", (short) this.pickupDelay);
        if (this.l() != null) {
            nbttagcompound.set("Thrower", GameProfileSerializer.a(this.l()));
        }

        if (this.k() != null) {
            nbttagcompound.set("Owner", GameProfileSerializer.a(this.k()));
        }

        if (!this.getItemStack().isEmpty()) {
            nbttagcompound.set("Item", this.getItemStack().save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getShort("Health");
        this.age = nbttagcompound.getShort("Age");
        if (nbttagcompound.hasKey("PickupDelay")) {
            this.pickupDelay = nbttagcompound.getShort("PickupDelay");
        }

        if (nbttagcompound.hasKeyOfType("Owner", 10)) {
            this.g = GameProfileSerializer.b(nbttagcompound.getCompound("Owner"));
        }

        if (nbttagcompound.hasKeyOfType("Thrower", 10)) {
            this.f = GameProfileSerializer.b(nbttagcompound.getCompound("Thrower"));
        }

        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        this.setItemStack(ItemStack.a(nbttagcompound1));
        if (this.getItemStack().isEmpty()) {
            this.die();
        }

    }

    public void d(EntityHuman entityhuman) {
        if (!this.world.isClientSide) {
            ItemStack itemstack = this.getItemStack();
            Item item = itemstack.getItem();
            int i = itemstack.getCount();

            if (this.pickupDelay == 0 && (this.g == null || 6000 - this.age <= 200 || this.g.equals(entityhuman.getUniqueID())) && entityhuman.inventory.pickup(itemstack)) {
                entityhuman.receive(this, i);
                if (itemstack.isEmpty()) {
                    this.die();
                    itemstack.setCount(i);
                }

                entityhuman.a(StatisticList.ITEM_PICKED_UP.b(item), i);
            }

        }
    }

    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return (IChatBaseComponent) (ichatbasecomponent != null ? ichatbasecomponent : new ChatMessage(this.getItemStack().j(), new Object[0]));
    }

    public boolean bk() {
        return false;
    }

    @Nullable
    public Entity a(DimensionManager dimensionmanager) {
        Entity entity = super.a(dimensionmanager);

        if (!this.world.isClientSide && entity instanceof EntityItem) {
            ((EntityItem) entity).v();
        }

        return entity;
    }

    public ItemStack getItemStack() {
        return (ItemStack) this.getDataWatcher().get(EntityItem.b);
    }

    public void setItemStack(ItemStack itemstack) {
        this.getDataWatcher().set(EntityItem.b, itemstack);
    }

    @Nullable
    public UUID k() {
        return this.g;
    }

    public void b(@Nullable UUID uuid) {
        this.g = uuid;
    }

    @Nullable
    public UUID l() {
        return this.f;
    }

    public void c(@Nullable UUID uuid) {
        this.f = uuid;
    }

    public void n() {
        this.pickupDelay = 10;
    }

    public void o() {
        this.pickupDelay = 0;
    }

    public void p() {
        this.pickupDelay = 32767;
    }

    public void a(int i) {
        this.pickupDelay = i;
    }

    public boolean q() {
        return this.pickupDelay > 0;
    }

    public void s() {
        this.age = -6000;
    }

    public void t() {
        this.p();
        this.age = 5999;
    }
}
