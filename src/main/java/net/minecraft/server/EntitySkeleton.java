package net.minecraft.server;

import java.util.Calendar;

import org.bukkit.event.entity.EntityCombustEvent; // CraftBukkit

public class EntitySkeleton extends EntityMonster implements IRangedEntity {

    private PathfinderGoalArrowAttack d = new PathfinderGoalArrowAttack(this, 0.25F, 60, 10.0F);
    private PathfinderGoalMeleeAttack e = new PathfinderGoalMeleeAttack(this, EntityHuman.class, 0.31F, false);

    public EntitySkeleton(World world) {
        super(world);
        this.texture = "/mob/skeleton.png";
        this.bI = 0.25F;
        this.goalSelector.a(1, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new PathfinderGoalRestrictSun(this));
        this.goalSelector.a(3, new PathfinderGoalFleeSun(this, this.bI));
        this.goalSelector.a(5, new PathfinderGoalRandomStroll(this, this.bI));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 8.0F));
        this.goalSelector.a(6, new PathfinderGoalRandomLookaround(this));
        this.targetSelector.a(1, new PathfinderGoalHurtByTarget(this, false));
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget(this, EntityHuman.class, 16.0F, 0, true));
        if (world != null && !world.isStatic) {
            this.m();
        }
    }

    protected void a() {
        super.a();
        this.datawatcher.a(13, new Byte((byte) 0));
    }

    public boolean bh() {
        return true;
    }

    public int getMaxHealth() {
        return 20;
    }

    protected String bb() {
        return "mob.skeleton.say";
    }

    protected String bc() {
        return "mob.skeleton.hurt";
    }

    protected String bd() {
        return "mob.skeleton.death";
    }

    protected void a(int i, int j, int k, int l) {
        this.makeSound("mob.skeleton.step", 0.15F, 1.0F);
    }

    public boolean m(Entity entity) {
        if (super.m(entity)) {
            if (this.getSkeletonType() == 1 && entity instanceof EntityLiving) {
                ((EntityLiving) entity).addEffect(new MobEffect(MobEffectList.WITHER.id, 200));
            }

            return true;
        } else {
            return false;
        }
    }

    public int c(Entity entity) {
        if (this.getSkeletonType() == 1) {
            ItemStack itemstack = this.bG();
            int i = 4;

            if (itemstack != null) {
                i += itemstack.a((Entity) this);
            }

            return i;
        } else {
            return super.c(entity);
        }
    }

    public EnumMonsterType getMonsterType() {
        return EnumMonsterType.UNDEAD;
    }

    public void c() {
        if (this.world.u() && !this.world.isStatic) {
            float f = this.c(1.0F);

            if (f > 0.5F && this.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && this.world.l(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ))) {
                boolean flag = true;
                ItemStack itemstack = this.getEquipment(4);

                if (itemstack != null) {
                    if (itemstack.g()) {
                        itemstack.setData(itemstack.j() + this.random.nextInt(2));
                        if (itemstack.j() >= itemstack.l()) {
                            this.a(itemstack);
                            this.setEquipment(4, (ItemStack) null);
                        }
                    }

                    flag = false;
                }

                if (flag) {
                    // CraftBukkit start
                EntityCombustEvent event = new EntityCombustEvent(this.getBukkitEntity(), 8);
                this.world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    this.setOnFire(event.getDuration());
                }
                // CraftBukkit end
                }
            }
        }

        if (this.world.isStatic && this.getSkeletonType() == 1) {
            this.a(0.72F, 2.34F);
        }

        super.c();
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        if (damagesource.h() instanceof EntityArrow && damagesource.getEntity() instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) damagesource.getEntity();
            double d0 = entityhuman.locX - this.locX;
            double d1 = entityhuman.locZ - this.locZ;

            if (d0 * d0 + d1 * d1 >= 2500.0D) {
                entityhuman.a((Statistic) AchievementList.v);
            }
        }
    }

    protected int getLootId() {
        return Item.ARROW.id;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start - whole method
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();

        if (this.getSkeletonType() == 1) {
            int count = this.random.nextInt(3 + i) - 1;
            if (count > 0) {
                loot.add(new org.bukkit.inventory.ItemStack(org.bukkit.Material.COAL, count));
            }
        } else {
            int count = this.random.nextInt(3 + i);
            if (count > 0) {
                loot.add(new org.bukkit.inventory.ItemStack(org.bukkit.Material.ARROW, count));
            }
        }

        int count = this.random.nextInt(3 + i);
        if (count > 0) {
            loot.add(new org.bukkit.inventory.ItemStack(org.bukkit.Material.BONE, count));
        }

        // Determine rare item drops and add them to the loot
        if (this.lastDamageByPlayerTime > 0) {
            int k = this.random.nextInt(200) - i;

            if (k < 5) {
                ItemStack itemstack = this.l(k <= 0 ? 1 : 0);
                if (itemstack != null) {
                    loot.add(org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(itemstack));
                }
            }
        }

        org.bukkit.craftbukkit.event.CraftEventFactory.callEntityDeathEvent(this, loot);
        // CraftBukkit end
    }

    // CraftBukkit - return rare dropped item instead of dropping it
    protected ItemStack l(int i) {
        if (this.getSkeletonType() == 1) {
            return new ItemStack(Item.SKULL.id, 1, 1); // CraftBukkit
        }

        return null;
    }

    protected void bH() {
        super.bH();
        this.setEquipment(0, new ItemStack(Item.BOW));
    }

    public void bJ() {
        if (this.world.worldProvider instanceof WorldProviderHell && this.aE().nextInt(5) > 0) {
            this.goalSelector.a(4, this.e);
            this.setSkeletonType(1);
            this.setEquipment(0, new ItemStack(Item.STONE_SWORD));
        } else {
            this.goalSelector.a(4, this.d);
            this.bH();
            this.bI();
        }

        this.h(this.random.nextFloat() < au[this.world.difficulty]);
        if (this.getEquipment(4) == null) {
            Calendar calendar = this.world.U();

            if (calendar.get(2) + 1 == 10 && calendar.get(5) == 31 && this.random.nextFloat() < 0.25F) {
                this.setEquipment(4, new ItemStack(this.random.nextFloat() < 0.1F ? Block.JACK_O_LANTERN : Block.PUMPKIN));
                this.dropChances[4] = 0.0F;
            }
        }
    }

    public void m() {
        this.goalSelector.a((PathfinderGoal) this.e);
        this.goalSelector.a((PathfinderGoal) this.d);
        ItemStack itemstack = this.bG();

        if (itemstack != null && itemstack.id == Item.BOW.id) {
            this.goalSelector.a(4, this.d);
        } else {
            this.goalSelector.a(4, this.e);
        }
    }

    public void a(EntityLiving entityliving, float f) {
        EntityArrow entityarrow = new EntityArrow(this.world, this, entityliving, 1.6F, (float) (14 - this.world.difficulty * 4));
        int i = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_DAMAGE.id, this.bG());
        int j = EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_KNOCKBACK.id, this.bG());

        entityarrow.b((double) (f * 2.0F) + this.random.nextGaussian() * 0.25D + (double) ((float) this.world.difficulty * 0.11F));
        if (i > 0) {
            entityarrow.b(entityarrow.c() + (double) i * 0.5D + 0.5D);
        }

        if (j > 0) {
            entityarrow.a(j);
        }

        if (EnchantmentManager.getEnchantmentLevel(Enchantment.ARROW_FIRE.id, this.bG()) > 0 || this.getSkeletonType() == 1) {
            entityarrow.setOnFire(100);
        }

        this.makeSound("random.bow", 1.0F, 1.0F / (this.aE().nextFloat() * 0.4F + 0.8F));
        this.world.addEntity(entityarrow);
    }

    public int getSkeletonType() {
        return this.datawatcher.getByte(13);
    }

    public void setSkeletonType(int i) {
        this.datawatcher.watch(13, Byte.valueOf((byte) i));
        this.fireProof = i == 1;
        if (i == 1) {
            this.a(0.72F, 2.34F);
        } else {
            this.a(0.6F, 1.8F);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("SkeletonType")) {
            byte b0 = nbttagcompound.getByte("SkeletonType");

            this.setSkeletonType(b0);
        }

        this.m();
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("SkeletonType", (byte) this.getSkeletonType());
    }

    public void setEquipment(int i, ItemStack itemstack) {
        super.setEquipment(i, itemstack);
        if (!this.world.isStatic && i == 0) {
            this.m();
        }
    }
}
