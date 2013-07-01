package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
// CraftBukkit end

public class EntityCow extends EntityAnimal {

    public EntityCow(World world) {
        super(world);
        this.a(0.9F, 1.3F);
        this.getNavigation().a(true);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, Item.WHEAT.id, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    public boolean bb() {
        return true;
    }

    protected void ax() {
        super.ax();
        this.a(GenericAttributes.a).a(10.0D);
        this.a(GenericAttributes.d).a(0.20000000298023224D);
    }

    protected String r() {
        return "mob.cow.say";
    }

    protected String aK() {
        return "mob.cow.hurt";
    }

    protected String aL() {
        return "mob.cow.hurt";
    }

    protected void a(int i, int j, int k, int l) {
        this.makeSound("mob.cow.step", 0.15F, 1.0F);
    }

    protected float aW() {
        return 0.4F;
    }

    protected int getLootId() {
        return Item.LEATHER.id;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start - Whole method
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();
        int j = this.random.nextInt(3) + this.random.nextInt(1 + i);

        int k;

        if (j > 0) {
            loot.add(new org.bukkit.inventory.ItemStack(Item.LEATHER.id, j));
        }

        j = this.random.nextInt(3) + 1 + this.random.nextInt(1 + i);

        if (j > 0) {
            loot.add(new org.bukkit.inventory.ItemStack(this.isBurning() ? Item.COOKED_BEEF.id : Item.RAW_BEEF.id, j));
        }

        CraftEventFactory.callEntityDeathEvent(this, loot);
        // CraftBukkit end
    }

    public boolean a(EntityHuman entityhuman) {
        ItemStack itemstack = entityhuman.inventory.getItemInHand();

        if (itemstack != null && itemstack.id == Item.BUCKET.id && !entityhuman.abilities.canInstantlyBuild) {
            // CraftBukkit start - Got milk?
            org.bukkit.Location loc = this.getBukkitEntity().getLocation();
            org.bukkit.event.player.PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), -1, itemstack, Item.MILK_BUCKET);

            if (event.isCancelled()) {
                return false;
            }

            if (--itemstack.count <= 0) {
                entityhuman.inventory.setItem(entityhuman.inventory.itemInHandIndex, CraftItemStack.asNMSCopy(event.getItemStack()));
            } else if (!entityhuman.inventory.pickup(new ItemStack(Item.MILK_BUCKET))) {
                entityhuman.drop(CraftItemStack.asNMSCopy(event.getItemStack()));
            }
            // CraftBukkit end

            return true;
        } else {
            return super.a(entityhuman);
        }
    }

    public EntityCow b(EntityAgeable entityageable) {
        return new EntityCow(this.world);
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
