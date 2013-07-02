package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;

//CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;

public abstract class EntityInsentient extends EntityLiving {

    public int a_;
    protected int b;
    private ControllerLook h;
    private ControllerMove moveController;
    private ControllerJump lookController;
    private EntityAIBodyControl bn;
    private Navigation navigation;
    protected final PathfinderGoalSelector goalSelector;
    protected final PathfinderGoalSelector targetSelector;
    private EntityLiving goalTarget;
    private EntitySenses bq;
    private ItemStack[] equipment = new ItemStack[5];
    public float[] dropChances = new float[5]; // CraftBukkit - protected -> public
    public boolean canPickUpLoot; // CraftBukkit - private -> public
    public boolean persistent; // CraftBukkit - private -> public
    protected float f;
    private Entity bu;
    protected int g;
    private boolean bv;
    private Entity bw;
    private NBTTagCompound bx;

    public EntityInsentient(World world) {
        super(world);
        this.goalSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.targetSelector = new PathfinderGoalSelector(world != null && world.methodProfiler != null ? world.methodProfiler : null);
        this.h = new ControllerLook(this);
        this.moveController = new ControllerMove(this);
        this.lookController = new ControllerJump(this);
        this.bn = new EntityAIBodyControl(this);
        this.navigation = new Navigation(this, world);
        this.bq = new EntitySenses(this);

        for (int i = 0; i < this.dropChances.length; ++i) {
            this.dropChances[i] = 0.085F;
        }
    }

    protected void ax() {
        super.ax();
        this.aT().b(GenericAttributes.b).a(16.0D);
    }

    public ControllerLook getControllerLook() {
        return this.h;
    }

    public ControllerMove getControllerMove() {
        return this.moveController;
    }

    public ControllerJump getControllerJump() {
        return this.lookController;
    }

    public Navigation getNavigation() {
        return this.navigation;
    }

    public EntitySenses getEntitySenses() {
        return this.bq;
    }

    public EntityLiving getGoalTarget() {
        return this.goalTarget;
    }

    public void setGoalTarget(EntityLiving entityliving) {
        this.goalTarget = entityliving;
    }

    public boolean a(Class oclass) {
        return EntityCreeper.class != oclass && EntityGhast.class != oclass;
    }

    public void n() {}

    protected void a() {
        super.a();
        this.datawatcher.a(11, Byte.valueOf((byte) 0));
        this.datawatcher.a(10, "");
    }

    public int o() {
        return 80;
    }

    public void p() {
        String s = this.r();

        if (s != null) {
            this.makeSound(s, this.aW(), this.aX());
        }
    }

    public void x() {
        super.x();
        this.world.methodProfiler.a("mobBaseTick");
        if (this.isAlive() && this.random.nextInt(1000) < this.a_++) {
            this.a_ = -this.o();
            this.p();
        }

        this.world.methodProfiler.b();
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.b > 0) {
            int i = this.b;
            ItemStack[] aitemstack = this.getEquipment();

            for (int j = 0; j < aitemstack.length; ++j) {
                if (aitemstack[j] != null && this.dropChances[j] <= 1.0F) {
                    i += 1 + this.random.nextInt(3);
                }
            }

            return i;
        } else {
            return this.b;
        }
    }

    public void q() {
        for (int i = 0; i < 20; ++i) {
            double d0 = this.random.nextGaussian() * 0.02D;
            double d1 = this.random.nextGaussian() * 0.02D;
            double d2 = this.random.nextGaussian() * 0.02D;
            double d3 = 10.0D;

            this.world.addParticle("explode", this.locX + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width - d0 * d3, this.locY + (double) (this.random.nextFloat() * this.length) - d1 * d3, this.locZ + (double) (this.random.nextFloat() * this.width * 2.0F) - (double) this.width - d2 * d3, d0, d1, d2);
        }
    }

    public void l_() {
        super.l_();
        if (!this.world.isStatic) {
            this.bB();
        }
    }

    protected float f(float f, float f1) {
        if (this.bb()) {
            this.bn.a();
            return f1;
        } else {
            return super.f(f, f1);
        }
    }

    protected String r() {
        return null;
    }

    protected int getLootId() {
        return 0;
    }

    protected void dropDeathLoot(boolean flag, int i) {
        // CraftBukkit start - Whole method
        List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();
        int j = this.getLootId();

        if (j > 0) {
            int k = this.random.nextInt(3);

            if (i > 0) {
                k += this.random.nextInt(i + 1);
            }

            if (k > 0) {
                loot.add(new org.bukkit.inventory.ItemStack(j, k));
            }
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

        CraftEventFactory.callEntityDeathEvent(this, loot); // raise event even for those times when the entity does not drop loot
        // CraftBukkit end
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("CanPickUpLoot", this.bz());
        nbttagcompound.setBoolean("PersistenceRequired", this.persistent);
        NBTTagList nbttaglist = new NBTTagList();

        NBTTagCompound nbttagcompound1;

        for (int i = 0; i < this.equipment.length; ++i) {
            nbttagcompound1 = new NBTTagCompound();
            if (this.equipment[i] != null) {
                this.equipment[i].save(nbttagcompound1);
            }

            nbttaglist.add(nbttagcompound1);
        }

        nbttagcompound.set("Equipment", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();

        for (int j = 0; j < this.dropChances.length; ++j) {
            nbttaglist1.add(new NBTTagFloat(j + "", this.dropChances[j]));
        }

        nbttagcompound.set("DropChances", nbttaglist1);
        nbttagcompound.setString("CustomName", this.getCustomName());
        nbttagcompound.setBoolean("CustomNameVisible", this.getCustomNameVisible());
        nbttagcompound.setBoolean("Leashed", this.bv);
        if (this.bw != null) {
            nbttagcompound1 = new NBTTagCompound("Leash");
            if (this.bw instanceof EntityLiving) {
                nbttagcompound1.setLong("UUIDMost", this.bw.getUniqueID().getMostSignificantBits());
                nbttagcompound1.setLong("UUIDLeast", this.bw.getUniqueID().getLeastSignificantBits());
            } else if (this.bw instanceof EntityHanging) {
                EntityHanging entityhanging = (EntityHanging) this.bw;

                nbttagcompound1.setInt("X", entityhanging.x);
                nbttagcompound1.setInt("Y", entityhanging.y);
                nbttagcompound1.setInt("Z", entityhanging.z);
            }

            nbttagcompound.set("Leash", nbttagcompound1);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.h(nbttagcompound.getBoolean("CanPickUpLoot"));
        this.persistent = nbttagcompound.getBoolean("PersistenceRequired");

        // CraftBukkit start - If looting or persistence is false only use it if it was set after we started using it
        boolean data = nbttagcompound.getBoolean("CanPickUpLoot");
        if (isLevelAtLeast(nbttagcompound, 1) || data) {
            this.canPickUpLoot = data;
        }

        data = nbttagcompound.getBoolean("PersistenceRequired");
        if (isLevelAtLeast(nbttagcompound, 1) || data) {
            this.persistent = data;
        }
        // CraftBukkit end

        if (nbttagcompound.hasKey("CustomName") && nbttagcompound.getString("CustomName").length() > 0) {
            this.setCustomName(nbttagcompound.getString("CustomName"));
        }

        this.setCustomNameVisible(nbttagcompound.getBoolean("CustomNameVisible"));
        NBTTagList nbttaglist;
        int i;

        if (nbttagcompound.hasKey("Equipment")) {
            nbttaglist = nbttagcompound.getList("Equipment");

            for (i = 0; i < this.equipment.length; ++i) {
                this.equipment[i] = ItemStack.createStack((NBTTagCompound) nbttaglist.get(i));
            }
        }

        if (nbttagcompound.hasKey("DropChances")) {
            nbttaglist = nbttagcompound.getList("DropChances");

            for (i = 0; i < nbttaglist.size(); ++i) {
                this.dropChances[i] = ((NBTTagFloat) nbttaglist.get(i)).data;
            }
        }

        this.bv = nbttagcompound.getBoolean("Leashed");
        if (this.bv && nbttagcompound.hasKey("Leash")) {
            this.bx = nbttagcompound.getCompound("Leash");
        }
    }

    public void n(float f) {
        this.bf = f;
    }

    public void i(float f) {
        super.i(f);
        this.n(f);
    }

    public void c() {
        super.c();
        this.world.methodProfiler.a("looting");
        if (!this.world.isStatic && this.bz() && !this.aU && this.world.getGameRules().getBoolean("mobGriefing")) {
            List list = this.world.a(EntityItem.class, this.boundingBox.grow(1.0D, 0.0D, 1.0D));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityItem entityitem = (EntityItem) iterator.next();

                if (!entityitem.dead && entityitem.getItemStack() != null) {
                    ItemStack itemstack = entityitem.getItemStack();
                    int i = b(itemstack);

                    if (i > -1) {
                        boolean flag = true;
                        ItemStack itemstack1 = this.getEquipment(i);

                        if (itemstack1 != null) {
                            if (i == 0) {
                                if (itemstack.getItem() instanceof ItemSword && !(itemstack1.getItem() instanceof ItemSword)) {
                                    flag = true;
                                } else if (itemstack.getItem() instanceof ItemSword && itemstack1.getItem() instanceof ItemSword) {
                                    ItemSword itemsword = (ItemSword) itemstack.getItem();
                                    ItemSword itemsword1 = (ItemSword) itemstack1.getItem();

                                    if (itemsword.g() == itemsword1.g()) {
                                        flag = itemstack.getData() > itemstack1.getData() || itemstack.hasTag() && !itemstack1.hasTag();
                                    } else {
                                        flag = itemsword.g() > itemsword1.g();
                                    }
                                } else {
                                    flag = false;
                                }
                            } else if (itemstack.getItem() instanceof ItemArmor && !(itemstack1.getItem() instanceof ItemArmor)) {
                                flag = true;
                            } else if (itemstack.getItem() instanceof ItemArmor && itemstack1.getItem() instanceof ItemArmor) {
                                ItemArmor itemarmor = (ItemArmor) itemstack.getItem();
                                ItemArmor itemarmor1 = (ItemArmor) itemstack1.getItem();

                                if (itemarmor.c == itemarmor1.c) {
                                    flag = itemstack.getData() > itemstack1.getData() || itemstack.hasTag() && !itemstack1.hasTag();
                                } else {
                                    flag = itemarmor.c > itemarmor1.c;
                                }
                            } else {
                                flag = false;
                            }
                        }

                        if (flag) {
                            if (itemstack1 != null && this.random.nextFloat() - 0.1F < this.dropChances[i]) {
                                this.a(itemstack1, 0.0F);
                            }

                            this.setEquipment(i, itemstack);
                            this.dropChances[i] = 2.0F;
                            this.persistent = true;
                            this.receive(entityitem, 1);
                            entityitem.die();
                        }
                    }
                }
            }
        }

        this.world.methodProfiler.b();
    }

    protected boolean bb() {
        return false;
    }

    protected boolean isTypeNotPersistent() {
        return true;
    }

    protected void bk() {
        if (this.persistent) {
            this.aV = 0;
        } else {
            EntityHuman entityhuman = this.world.findNearbyPlayer(this, -1.0D);

            if (entityhuman != null) {
                double d0 = entityhuman.locX - this.locX;
                double d1 = entityhuman.locY - this.locY;
                double d2 = entityhuman.locZ - this.locZ;
                double d3 = d0 * d0 + d1 * d1 + d2 * d2;

                if (d3 > 16384.0D) { // CraftBukkit - remove isTypeNotPersistent() check
                    this.die();
                }

                if (this.aV > 600 && this.random.nextInt(800) == 0 && d3 > 1024.0D) { // CraftBukkit - remove isTypeNotPersistent() check
                    this.die();
                } else if (d3 < 1024.0D) {
                    this.aV = 0;
                }
            }
        }
    }

    protected void be() {
        ++this.aV;
        this.world.methodProfiler.a("checkDespawn");
        this.bk();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("sensing");
        this.bq.a();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("targetSelector");
        this.targetSelector.a();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("goalSelector");
        this.goalSelector.a();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("navigation");
        this.navigation.f();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("mob tick");
        this.bg();
        this.world.methodProfiler.b();
        this.world.methodProfiler.a("controls");
        this.world.methodProfiler.a("move");
        this.moveController.c();
        this.world.methodProfiler.c("look");
        this.h.a();
        this.world.methodProfiler.c("jump");
        this.lookController.b();
        this.world.methodProfiler.b();
        this.world.methodProfiler.b();
    }

    protected void bh() {
        super.bh();
        this.be = 0.0F;
        this.bf = 0.0F;
        this.bk();
        float f = 8.0F;

        if (this.random.nextFloat() < 0.02F) {
            EntityHuman entityhuman = this.world.findNearbyPlayer(this, (double) f);

            if (entityhuman != null) {
                this.bu = entityhuman;
                this.g = 10 + this.random.nextInt(20);
            } else {
                this.bg = (this.random.nextFloat() - 0.5F) * 20.0F;
            }
        }

        if (this.bu != null) {
            this.a(this.bu, 10.0F, (float) this.bl());
            if (this.g-- <= 0 || this.bu.dead || this.bu.e((Entity) this) > (double) (f * f)) {
                this.bu = null;
            }
        } else {
            if (this.random.nextFloat() < 0.05F) {
                this.bg = (this.random.nextFloat() - 0.5F) * 20.0F;
            }

            this.yaw += this.bg;
            this.pitch = this.f;
        }

        boolean flag = this.G();
        boolean flag1 = this.I();

        if (flag || flag1) {
            this.bd = this.random.nextFloat() < 0.8F;
        }
    }

    public int bl() {
        return 40;
    }

    public void a(Entity entity, float f, float f1) {
        double d0 = entity.locX - this.locX;
        double d1 = entity.locZ - this.locZ;
        double d2;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            d2 = entityliving.locY + (double) entityliving.getHeadHeight() - (this.locY + (double) this.getHeadHeight());
        } else {
            d2 = (entity.boundingBox.b + entity.boundingBox.e) / 2.0D - (this.locY + (double) this.getHeadHeight());
        }

        double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1);
        float f2 = (float) (Math.atan2(d1, d0) * 180.0D / 3.1415927410125732D) - 90.0F;
        float f3 = (float) (-(Math.atan2(d2, d3) * 180.0D / 3.1415927410125732D));

        this.pitch = this.b(this.pitch, f3, f1);
        this.yaw = this.b(this.yaw, f2, f);
    }

    private float b(float f, float f1, float f2) {
        float f3 = MathHelper.g(f1 - f);

        if (f3 > f2) {
            f3 = f2;
        }

        if (f3 < -f2) {
            f3 = -f2;
        }

        return f + f3;
    }

    public boolean canSpawn() {
        return this.world.b(this.boundingBox) && this.world.getCubes(this, this.boundingBox).isEmpty() && !this.world.containsLiquid(this.boundingBox);
    }

    public int br() {
        return 4;
    }

    public int aq() {
        if (this.getGoalTarget() == null) {
            return 3;
        } else {
            int i = (int) (this.getHealth() - this.getMaxHealth() * 0.33F);

            i -= (3 - this.world.difficulty) * 4;
            if (i < 0) {
                i = 0;
            }

            return i + 3;
        }
    }

    public ItemStack aV() {
        return this.equipment[0];
    }

    public ItemStack getEquipment(int i) {
        return this.equipment[i];
    }

    public ItemStack o(int i) {
        return this.equipment[i + 1];
    }

    public void setEquipment(int i, ItemStack itemstack) {
        this.equipment[i] = itemstack;
    }

    public ItemStack[] getEquipment() {
        return this.equipment;
    }

    protected void dropEquipment(boolean flag, int i) {
        for (int j = 0; j < this.getEquipment().length; ++j) {
            ItemStack itemstack = this.getEquipment(j);
            boolean flag1 = this.dropChances[j] > 1.0F;

            if (itemstack != null && (flag || flag1) && this.random.nextFloat() - (float) i * 0.01F < this.dropChances[j]) {
                if (!flag1 && itemstack.g()) {
                    int k = Math.max(itemstack.l() - 25, 1);
                    int l = itemstack.l() - this.random.nextInt(this.random.nextInt(k) + 1);

                    if (l > k) {
                        l = k;
                    }

                    if (l < 1) {
                        l = 1;
                    }

                    itemstack.setData(l);
                }

                this.a(itemstack, 0.0F);
            }
        }
    }

    protected void bs() {
        if (this.random.nextFloat() < 0.15F * this.world.b(this.locX, this.locY, this.locZ)) {
            int i = this.random.nextInt(2);
            float f = this.world.difficulty == 3 ? 0.1F : 0.25F;

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            if (this.random.nextFloat() < 0.095F) {
                ++i;
            }

            for (int j = 3; j >= 0; --j) {
                ItemStack itemstack = this.o(j);

                if (j < 3 && this.random.nextFloat() < f) {
                    break;
                }

                if (itemstack == null) {
                    Item item = a(j + 1, i);

                    if (item != null) {
                        this.setEquipment(j + 1, new ItemStack(item));
                    }
                }
            }
        }
    }

    public static int b(ItemStack itemstack) {
        if (itemstack.id != Block.PUMPKIN.id && itemstack.id != Item.SKULL.id) {
            if (itemstack.getItem() instanceof ItemArmor) {
                switch (((ItemArmor) itemstack.getItem()).b) {
                case 0:
                    return 4;

                case 1:
                    return 3;

                case 2:
                    return 2;

                case 3:
                    return 1;
                }
            }

            return 0;
        } else {
            return 4;
        }
    }

    public static Item a(int i, int j) {
        switch (i) {
        case 4:
            if (j == 0) {
                return Item.LEATHER_HELMET;
            } else if (j == 1) {
                return Item.GOLD_HELMET;
            } else if (j == 2) {
                return Item.CHAINMAIL_HELMET;
            } else if (j == 3) {
                return Item.IRON_HELMET;
            } else if (j == 4) {
                return Item.DIAMOND_HELMET;
            }

        case 3:
            if (j == 0) {
                return Item.LEATHER_CHESTPLATE;
            } else if (j == 1) {
                return Item.GOLD_CHESTPLATE;
            } else if (j == 2) {
                return Item.CHAINMAIL_CHESTPLATE;
            } else if (j == 3) {
                return Item.IRON_CHESTPLATE;
            } else if (j == 4) {
                return Item.DIAMOND_CHESTPLATE;
            }

        case 2:
            if (j == 0) {
                return Item.LEATHER_LEGGINGS;
            } else if (j == 1) {
                return Item.GOLD_LEGGINGS;
            } else if (j == 2) {
                return Item.CHAINMAIL_LEGGINGS;
            } else if (j == 3) {
                return Item.IRON_LEGGINGS;
            } else if (j == 4) {
                return Item.DIAMOND_LEGGINGS;
            }

        case 1:
            if (j == 0) {
                return Item.LEATHER_BOOTS;
            } else if (j == 1) {
                return Item.GOLD_BOOTS;
            } else if (j == 2) {
                return Item.CHAINMAIL_BOOTS;
            } else if (j == 3) {
                return Item.IRON_BOOTS;
            } else if (j == 4) {
                return Item.DIAMOND_BOOTS;
            }

        default:
            return null;
        }
    }

    protected void bt() {
        float f = this.world.b(this.locX, this.locY, this.locZ);

        if (this.aV() != null && this.random.nextFloat() < 0.25F * f) {
            EnchantmentManager.a(this.random, this.aV(), (int) (5.0F + f * (float) this.random.nextInt(18)));
        }

        for (int i = 0; i < 4; ++i) {
            ItemStack itemstack = this.o(i);

            if (itemstack != null && this.random.nextFloat() < 0.5F * f) {
                EnchantmentManager.a(this.random, itemstack, (int) (5.0F + f * (float) this.random.nextInt(18)));
            }
        }
    }

    public GroupDataEntity a(GroupDataEntity groupdataentity) {
        this.a(GenericAttributes.b).a(new AttributeModifier("Random spawn bonus", this.random.nextGaussian() * 0.05D, 1));
        return groupdataentity;
    }

    public boolean bu() {
        return false;
    }

    public String getLocalizedName() {
        return this.hasCustomName() ? this.getCustomName() : super.getLocalizedName();
    }

    public void bv() {
        this.persistent = true;
    }

    public void setCustomName(String s) {
        this.datawatcher.watch(10, s);
    }

    public String getCustomName() {
        return this.datawatcher.getString(10);
    }

    public boolean hasCustomName() {
        return this.datawatcher.getString(10).length() > 0;
    }

    public void setCustomNameVisible(boolean flag) {
        this.datawatcher.watch(11, Byte.valueOf((byte) (flag ? 1 : 0)));
    }

    public boolean getCustomNameVisible() {
        return this.datawatcher.getByte(11) == 1;
    }

    public void a(int i, float f) {
        this.dropChances[i] = f;
    }

    public boolean bz() {
        return this.canPickUpLoot;
    }

    public void h(boolean flag) {
        this.canPickUpLoot = flag;
    }

    public boolean bA() {
        return this.persistent;
    }

    public final boolean c(EntityHuman entityhuman) {
        if (this.bD() && this.bE() == entityhuman) {
            this.i(true);
            return true;
        } else {
            ItemStack itemstack = entityhuman.inventory.getItemInHand();

            if (itemstack != null && itemstack.id == Item.LEASH.id && this.bC()) {
                if (!(this instanceof EntityTameableAnimal) || !((EntityTameableAnimal) this).isTamed()) {
                    this.b(entityhuman, true);
                    --itemstack.count;
                    return true;
                }

                if (entityhuman.getName().equalsIgnoreCase(((EntityTameableAnimal) this).getOwnerName())) {
                    this.b(entityhuman, true);
                    --itemstack.count;
                    return true;
                }
            }

            return this.a(entityhuman) ? true : super.c(entityhuman);
        }
    }

    protected boolean a(EntityHuman entityhuman) {
        return false;
    }

    protected void bB() {
        if (this.bx != null) {
            this.bF();
        }

        if (this.bv) {
            if (this.bw == null || this.bw.dead) {
                this.i(true);
            }
        }
    }

    public void i(boolean flag) {
        if (this.bv) {
            this.bv = false;
            this.bw = null;
            if (!this.world.isStatic) {
                this.b(Item.LEASH.id, 1);
            }

            if (!this.world.isStatic && flag && this.world instanceof WorldServer) {
                ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new Packet39AttachEntity(1, this, (Entity) null)));
            }
        }
    }

    public boolean bC() {
        return !this.bD() && !(this instanceof IMonster);
    }

    public boolean bD() {
        return this.bv;
    }

    public Entity bE() {
        return this.bw;
    }

    public void b(Entity entity, boolean flag) {
        this.bv = true;
        this.bw = entity;
        if (!this.world.isStatic && flag && this.world instanceof WorldServer) {
            ((WorldServer) this.world).getTracker().a((Entity) this, (Packet) (new Packet39AttachEntity(1, this, this.bw)));
        }
    }

    private void bF() {
        if (this.bv && this.bx != null) {
            if (this.bx.hasKey("UUIDMost") && this.bx.hasKey("UUIDLeast")) {
                UUID uuid = new UUID(this.bx.getLong("UUIDMost"), this.bx.getLong("UUIDLeast"));
                List list = this.world.a(EntityLiving.class, this.boundingBox.grow(10.0D, 10.0D, 10.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityLiving entityliving = (EntityLiving) iterator.next();

                    if (entityliving.getUniqueID().equals(uuid)) {
                        this.bw = entityliving;
                        break;
                    }
                }
            } else if (this.bx.hasKey("X") && this.bx.hasKey("Y") && this.bx.hasKey("Z")) {
                int i = this.bx.getInt("X");
                int j = this.bx.getInt("Y");
                int k = this.bx.getInt("Z");
                EntityLeash entityleash = EntityLeash.b(this.world, i, j, k);

                if (entityleash == null) {
                    entityleash = EntityLeash.a(this.world, i, j, k);
                }

                this.bw = entityleash;
            } else {
                this.i(false);
            }
        }

        this.bx = null;
    }
}
