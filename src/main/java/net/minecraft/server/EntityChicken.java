package net.minecraft.server;

public class EntityChicken extends EntityAnimal {

    public float bp;
    public float bq;
    public float br;
    public float bs;
    public float bt = 1.0F;
    public int bu;

    public EntityChicken(World world) {
        super(world);
        this.a(0.3F, 0.7F);
        this.bu = this.random.nextInt(6000) + 6000;
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.4D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.0D, Item.SEEDS.id, false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    public boolean bb() {
        return true;
    }

    protected void ax() {
        super.ax();
        this.a(GenericAttributes.a).a(4.0D);
        this.a(GenericAttributes.d).a(0.25D);
    }

    public void c() {
        super.c();
        this.bs = this.bp;
        this.br = this.bq;
        this.bq = (float) ((double) this.bq + (double) (this.onGround ? -1 : 4) * 0.3D);
        if (this.bq < 0.0F) {
            this.bq = 0.0F;
        }

        if (this.bq > 1.0F) {
            this.bq = 1.0F;
        }

        if (!this.onGround && this.bt < 1.0F) {
            this.bt = 1.0F;
        }

        this.bt = (float) ((double) this.bt * 0.9D);
        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        this.bp += this.bt * 2.0F;
        if (!this.isBaby() && !this.world.isStatic && --this.bu <= 0) {
            this.makeSound("mob.chicken.plop", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            this.b(Item.EGG.id, 1);
            this.bu = this.random.nextInt(6000) + 6000;
        }
    }

    protected void b(float f) {}

    protected String r() {
        return "mob.chicken.say";
    }

    protected String aK() {
        return "mob.chicken.hurt";
    }

    protected String aL() {
        return "mob.chicken.hurt";
    }

    protected void a(int i, int j, int k, int l) {
        this.makeSound("mob.chicken.step", 0.15F, 1.0F);
    }

    protected int getLootId() {
        return Item.FEATHER.id;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start - Whole method
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();
        int j = this.random.nextInt(3) + this.random.nextInt(1 + i);

        if (j > 0) {
            loot.add(new org.bukkit.inventory.ItemStack(Item.FEATHER.id, j));
        }

        if (this.isBurning()) {
            loot.add(new org.bukkit.inventory.ItemStack(Item.COOKED_CHICKEN.id, 1));
        } else {
            loot.add(new org.bukkit.inventory.ItemStack(Item.RAW_CHICKEN.id, 1));
        }

        org.bukkit.craftbukkit.event.CraftEventFactory.callEntityDeathEvent(this, loot);
        // CraftBukkit end
    }

    public EntityChicken b(EntityAgeable entityageable) {
        return new EntityChicken(this.world);
    }

    public boolean c(ItemStack itemstack) {
        return itemstack != null && itemstack.getItem() instanceof ItemSeeds;
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        return this.b(entityageable);
    }
}
