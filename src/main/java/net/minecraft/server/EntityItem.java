package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.player.PlayerPickupItemEvent;
// CraftBukkit end

public class EntityItem extends Entity {

    public ItemStack itemStack;
    public int age = 0;
    public int pickupDelay;
    private int e = 5;
    public float d = (float) (Math.random() * 3.141592653589793D * 2.0D);
    private int lastTick = (int) (System.currentTimeMillis() / 50); // CraftBukkit

    public EntityItem(World world, double d0, double d1, double d2, ItemStack itemstack) {
        super(world);
        this.b(0.25F, 0.25F);
        this.height = this.length / 2.0F;
        this.setPosition(d0, d1, d2);
        this.itemStack = itemstack;
        // CraftBukkit start - infinite item fix & nullcheck
        if (this.itemStack == null) {
            throw new IllegalArgumentException("Can't create an EntityItem for a null item");
        }
        if (this.itemStack.count <= -1) {
            this.itemStack.count = 1;
        }
        // CraftBukkit end
        this.yaw = (float) (Math.random() * 360.0D);
        this.motX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D));
        this.motY = 0.20000000298023224D;
        this.motZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D));
    }

    protected boolean g_() {
        return false;
    }

    public EntityItem(World world) {
        super(world);
        this.b(0.25F, 0.25F);
        this.height = this.length / 2.0F;
    }

    protected void b() {}

    public void F_() {
        super.F_();
        // CraftBukkit start
        int currentTick = (int) (System.currentTimeMillis() / 50);
        this.pickupDelay -= (currentTick - this.lastTick);
        this.lastTick = currentTick;
        // CraftBukkit end

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.03999999910593033D;
        if (this.world.getMaterial(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) == Material.LAVA) {
            this.motY = 0.20000000298023224D;
            this.motX = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.motZ = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.world.makeSound(this, "random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

        this.g(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0D, this.locZ);
        this.move(this.motX, this.motY, this.motZ);
        float f = 0.98F;

        if (this.onGround) {
            f = 0.58800006F;
            int i = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));

            if (i > 0) {
                f = Block.byId[i].frictionFactor * 0.98F;
            }
        }

        this.motX *= (double) f;
        this.motY *= 0.9800000190734863D;
        this.motZ *= (double) f;
        if (this.onGround) {
            this.motY *= -0.5D;
        }

        ++this.age;
        if (this.age >= 6000) {
            // CraftBukkit start
            if (CraftEventFactory.callItemDespawnEvent(this).isCancelled()) {
                this.age = 0;
                return;
            }
            // CraftBukkit end
            this.die();
        }
    }

    public void k() {
        this.age = 4800;
    }

    public boolean h_() {
        return this.world.a(this.boundingBox, Material.WATER, this);
    }

    protected void burn(int i) {
        this.damageEntity(DamageSource.FIRE, i);
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        this.aW();
        this.e -= i;
        if (this.e <= 0) {
            this.die();
        }

        return false;
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) ((byte) this.e));
        nbttagcompound.setShort("Age", (short) this.age);
        if (this.itemStack != null) // CraftBukkit - Nullchex!
        nbttagcompound.setCompound("Item", this.itemStack.save(new NBTTagCompound()));
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.e = nbttagcompound.getShort("Health") & 255;
        this.age = nbttagcompound.getShort("Age");
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Item");

        this.itemStack = ItemStack.a(nbttagcompound1);
        if (this.itemStack == null) {
            this.die();
        }
    }

    public void a_(EntityHuman entityhuman) {
        if ((!this.world.isStatic) && (this.itemStack != null)) { // CraftBukkit - nullcheck
            int i = this.itemStack.count;

            // CraftBukkit start
            int canHold = entityhuman.inventory.canHold(this.itemStack);
            int remaining = this.itemStack.count - canHold;
            if (this.pickupDelay <= 0 && canHold > 0) {
                this.itemStack.count = canHold;
                PlayerPickupItemEvent event = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), (org.bukkit.entity.Item) this.getBukkitEntity(), remaining);
                this.world.getServer().getPluginManager().callEvent(event);
                this.itemStack.count = canHold + remaining;

                if (event.isCancelled()) {
                    return;
                }

                // Possibly < 0; fix here so we do not have to modify code below
                this.pickupDelay = 0;
            }
            // CraftBukkit end

            if (this.pickupDelay == 0 && entityhuman.inventory.pickup(this.itemStack)) {
                if (this.itemStack.id == Block.LOG.id) {
                    entityhuman.a((Statistic) AchievementList.g);
                }

                if (this.itemStack.id == Item.LEATHER.id) {
                    entityhuman.a((Statistic) AchievementList.t);
                }

                if (this.itemStack.id == Item.DIAMOND.id) {
                    entityhuman.a((Statistic) AchievementList.w);
                }

                if (this.itemStack.id == Item.BLAZE_ROD.id) {
                    entityhuman.a((Statistic) AchievementList.z);
                }

                this.world.makeSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityhuman.receive(this, i);
                if (this.itemStack.count <= 0) {
                    this.die();
                }
            }
        }
    }

    public String getLocalizedName() {
        if (this.itemStack == null) return LocaleI18n.get("item.unknown"); // CraftBukkit - nullcheck
        return LocaleI18n.get("item." + this.itemStack.k());
    }

    public boolean k_() {
        return false;
    }
}
