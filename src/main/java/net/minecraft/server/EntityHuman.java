package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.entity.CraftItem;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
// CraftBukkit end

public abstract class EntityHuman extends EntityLiving implements ICommandListener {

    public PlayerInventory inventory = new PlayerInventory(this);
    private InventoryEnderChest enderChest = new InventoryEnderChest();
    public Container defaultContainer;
    public Container activeContainer;
    protected FoodMetaData foodData = new FoodMetaData();
    protected int bO = 0;
    public byte bP = 0;
    public float bQ;
    public float bR;
    public String name;
    public int bT = 0;
    public double bU;
    public double bV;
    public double bW;
    public double bX;
    public double bY;
    public double bZ;
    // CraftBukkit start
    public boolean sleeping; // protected -> public
    public boolean fauxSleeping;
    public String spawnWorld = "";

    @Override
    public CraftHumanEntity getBukkitEntity() {
        return (CraftHumanEntity) super.getBukkitEntity();
    }
    // CraftBukkit end

    public ChunkCoordinates cb;
    public int sleepTicks; // CraftBukkit - private -> public
    public float cc;
    public float cd;
    private ChunkCoordinates c;
    private boolean d;
    private ChunkCoordinates e;
    public PlayerAbilities abilities = new PlayerAbilities();
    public int oldLevel = -1; // CraftBukkit
    public int expLevel;
    public int expTotal;
    public float exp;
    private ItemStack f;
    private int g;
    protected float ci = 0.1F;
    protected float cj = 0.02F;
    private int h = 0;
    public EntityFishingHook hookedFish = null;

    public EntityHuman(World world) {
        super(world);
        this.defaultContainer = new ContainerPlayer(this.inventory, !world.isStatic, this);
        this.activeContainer = this.defaultContainer;
        this.height = 1.62F;
        ChunkCoordinates chunkcoordinates = world.getSpawn();

        this.setPositionRotation((double) chunkcoordinates.x + 0.5D, (double) (chunkcoordinates.y + 1), (double) chunkcoordinates.z + 0.5D, 0.0F, 0.0F);
        this.aK = "humanoid";
        this.aJ = 180.0F;
        this.maxFireTicks = 20;
        this.texture = "/mob/char.png";
    }

    public int getMaxHealth() {
        return 20;
    }

    protected void a() {
        super.a();
        this.datawatcher.a(16, Byte.valueOf((byte) 0));
        this.datawatcher.a(17, Byte.valueOf((byte) 0));
        this.datawatcher.a(18, Integer.valueOf(0));
    }

    public boolean bV() {
        return this.f != null;
    }

    public void bX() {
        if (this.f != null) {
            this.f.b(this.world, this, this.g);
        }

        this.bY();
    }

    public void bY() {
        this.f = null;
        this.g = 0;
        if (!this.world.isStatic) {
            this.e(false);
        }
    }

    public boolean bk() {
        return this.bV() && Item.byId[this.f.id].b_(this.f) == EnumAnimation.BLOCK;
    }

    public void l_() {
        if (this.f != null) {
            ItemStack itemstack = this.inventory.getItemInHand();

            if (itemstack == this.f) {
                if (this.g <= 25 && this.g % 4 == 0) {
                    this.c(itemstack, 5);
                }

                if (--this.g == 0 && !this.world.isStatic) {
                    this.m();
                }
            } else {
                this.bY();
            }
        }

        if (this.bT > 0) {
            --this.bT;
        }

        if (this.isSleeping()) {
            ++this.sleepTicks;
            if (this.sleepTicks > 100) {
                this.sleepTicks = 100;
            }

            if (!this.world.isStatic) {
                if (!this.i()) {
                    this.a(true, true, false);
                } else if (this.world.u()) {
                    this.a(false, true, true);
                }
            }
        } else if (this.sleepTicks > 0) {
            ++this.sleepTicks;
            if (this.sleepTicks >= 110) {
                this.sleepTicks = 0;
            }
        }

        super.l_();
        if (!this.world.isStatic && this.activeContainer != null && !this.activeContainer.a(this)) {
            this.closeInventory();
            this.activeContainer = this.defaultContainer;
        }

        if (this.isBurning() && this.abilities.isInvulnerable) {
            this.extinguish();
        }

        this.bU = this.bX;
        this.bV = this.bY;
        this.bW = this.bZ;
        double d0 = this.locX - this.bX;
        double d1 = this.locY - this.bY;
        double d2 = this.locZ - this.bZ;
        double d3 = 10.0D;

        if (d0 > d3) {
            this.bU = this.bX = this.locX;
        }

        if (d2 > d3) {
            this.bW = this.bZ = this.locZ;
        }

        if (d1 > d3) {
            this.bV = this.bY = this.locY;
        }

        if (d0 < -d3) {
            this.bU = this.bX = this.locX;
        }

        if (d2 < -d3) {
            this.bW = this.bZ = this.locZ;
        }

        if (d1 < -d3) {
            this.bV = this.bY = this.locY;
        }

        this.bX += d0 * 0.25D;
        this.bZ += d2 * 0.25D;
        this.bY += d1 * 0.25D;
        this.a(StatisticList.k, 1);
        if (this.vehicle == null) {
            this.e = null;
        }

        if (!this.world.isStatic) {
            this.foodData.a(this);
        }
    }

    public int y() {
        return this.abilities.isInvulnerable ? 0 : 80;
    }

    public int aa() {
        return 10;
    }

    public void makeSound(String s, float f, float f1) {
        this.world.a(this, s, f, f1);
    }

    protected void c(ItemStack itemstack, int i) {
        if (itemstack.o() == EnumAnimation.DRINK) {
            this.makeSound("random.drink", 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }

        if (itemstack.o() == EnumAnimation.EAT) {
            for (int j = 0; j < i; ++j) {
                Vec3D vec3d = this.world.getVec3DPool().create(((double) this.random.nextFloat() - 0.5D) * 0.1D, Math.random() * 0.1D + 0.1D, 0.0D);

                vec3d.a(-this.pitch * 3.1415927F / 180.0F);
                vec3d.b(-this.yaw * 3.1415927F / 180.0F);
                Vec3D vec3d1 = this.world.getVec3DPool().create(((double) this.random.nextFloat() - 0.5D) * 0.3D, (double) (-this.random.nextFloat()) * 0.6D - 0.3D, 0.6D);

                vec3d1.a(-this.pitch * 3.1415927F / 180.0F);
                vec3d1.b(-this.yaw * 3.1415927F / 180.0F);
                vec3d1 = vec3d1.add(this.locX, this.locY + (double) this.getHeadHeight(), this.locZ);
                this.world.addParticle("iconcrack_" + itemstack.getItem().id, vec3d1.c, vec3d1.d, vec3d1.e, vec3d.c, vec3d.d + 0.05D, vec3d.e);
            }

            this.makeSound("random.eat", 0.5F + 0.5F * (float) this.random.nextInt(2), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
        }
    }

    protected void m() {
        if (this.f != null) {
            this.c(this.f, 16);
            int i = this.f.count;

            // CraftBukkit start
            org.bukkit.inventory.ItemStack craftItem = CraftItemStack.asBukkitCopy(this.f);
            PlayerItemConsumeEvent event = new PlayerItemConsumeEvent((Player) this.getBukkitEntity(), craftItem);
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                // Update client
                if (this instanceof EntityPlayer) {
                    ((EntityPlayer) this).playerConnection.sendPacket(new Packet103SetSlot((byte) 0, activeContainer.a((IInventory) this.inventory, this.inventory.itemInHandIndex).index, this.f));
                }
                return;
            }

            // Plugin modified the item, process it but don't remove it
            if (!craftItem.equals(event.getItem())) {
                CraftItemStack.asNMSCopy(event.getItem()).b(this.world, this);

                // Update client
                if (this instanceof EntityPlayer) {
                    ((EntityPlayer) this).playerConnection.sendPacket(new Packet103SetSlot((byte) 0, activeContainer.a((IInventory) this.inventory, this.inventory.itemInHandIndex).index, this.f));
                }
                return;
            }
            // CraftBukkit end

            ItemStack itemstack = this.f.b(this.world, this);

            if (itemstack != this.f || itemstack != null && itemstack.count != i) {
                this.inventory.items[this.inventory.itemInHandIndex] = itemstack;
                if (itemstack.count == 0) {
                    this.inventory.items[this.inventory.itemInHandIndex] = null;
                }
            }

            this.bY();
        }
    }

    protected boolean bj() {
        return this.getHealth() <= 0 || this.isSleeping();
    }

    // CraftBukkit - protected -> public
    public void closeInventory() {
        this.activeContainer = this.defaultContainer;
    }

    public void mount(Entity entity) {
        // CraftBukkit start - mirror Entity mount changes
        this.setPassengerOf(entity);
    }

    public void setPassengerOf(Entity entity) {
        // CraftBukkit end
        if (this.vehicle == entity) {
            this.h(entity);
            if (this.vehicle != null) {
                this.vehicle.passenger = null;
            }

            this.vehicle = null;
        } else {
            super.setPassengerOf(entity); // CraftBukkit - call new parent
        }
    }

    public void T() {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;
        float f = this.yaw;
        float f1 = this.pitch;

        super.T();
        this.bQ = this.bR;
        this.bR = 0.0F;
        this.k(this.locX - d0, this.locY - d1, this.locZ - d2);
        if (this.vehicle instanceof EntityPig) {
            this.pitch = f1;
            this.yaw = f;
            this.ay = ((EntityPig) this.vehicle).ay;
        }
    }

    protected void bq() {
        this.br();
    }

    public void c() {
        if (this.bO > 0) {
            --this.bO;
        }

        // CraftBukkit - this.getMaxHealth() -> this.maxHealth
        if (this.world.difficulty == 0 && this.getHealth() < this.maxHealth && this.ticksLived % 20 * 12 == 0) {
            // CraftBukkit - added regain reason of "REGEN" for filtering purposes.
            this.heal(1, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.REGEN);
        }

        this.inventory.k();
        this.bQ = this.bR;
        super.c();
        this.aO = this.abilities.b();
        this.aP = this.cj;
        if (this.isSprinting()) {
            this.aO = (float) ((double) this.aO + (double) this.abilities.b() * 0.3D);
            this.aP = (float) ((double) this.aP + (double) this.cj * 0.3D);
        }

        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);
        // CraftBukkit - Math -> TrigMath
        float f1 = (float) org.bukkit.craftbukkit.TrigMath.atan(-this.motY * 0.20000000298023224D) * 15.0F;

        if (f > 0.1F) {
            f = 0.1F;
        }

        if (!this.onGround || this.getHealth() <= 0) {
            f = 0.0F;
        }

        if (this.onGround || this.getHealth() <= 0) {
            f1 = 0.0F;
        }

        this.bR += (f - this.bR) * 0.4F;
        this.bc += (f1 - this.bc) * 0.8F;
        if (this.getHealth() > 0) {
            List list = this.world.getEntities(this, this.boundingBox.grow(1.0D, 0.5D, 1.0D));

            if (list != null) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (!entity.dead) {
                        this.r(entity);
                    }
                }
            }
        }
    }

    private void r(Entity entity) {
        entity.b_(this);
    }

    public int getScore() {
        return this.datawatcher.getInt(18);
    }

    public void setScore(int i) {
        this.datawatcher.watch(18, Integer.valueOf(i));
    }

    public void addScore(int i) {
        int j = this.getScore();

        this.datawatcher.watch(18, Integer.valueOf(j + i));
    }

    public void die(DamageSource damagesource) {
        super.die(damagesource);
        this.a(0.2F, 0.2F);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.motY = 0.10000000149011612D;
        if (this.name.equals("Notch")) {
            this.a(new ItemStack(Item.APPLE, 1), true);
        }

        if (!this.world.getGameRules().getBoolean("keepInventory")) {
            this.inventory.m();
        }

        if (damagesource != null) {
            this.motX = (double) (-MathHelper.cos((this.aY + this.yaw) * 3.1415927F / 180.0F) * 0.1F);
            this.motZ = (double) (-MathHelper.sin((this.aY + this.yaw) * 3.1415927F / 180.0F) * 0.1F);
        } else {
            this.motX = this.motZ = 0.0D;
        }

        this.height = 0.1F;
        this.a(StatisticList.y, 1);
    }

    public void c(Entity entity, int i) {
        this.addScore(i);
        Collection collection = this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.e);

        if (entity instanceof EntityHuman) {
            this.a(StatisticList.A, 1);
            collection.addAll(this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.d));
        } else {
            this.a(StatisticList.z, 1);
        }

        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();
            ScoreboardScore scoreboardscore = this.getScoreboard().getPlayerScoreForObjective(this.getLocalizedName(), scoreboardobjective);

            scoreboardscore.incrementScore();
        }
    }

    public EntityItem a(boolean flag) {
        return this.a(this.inventory.splitStack(this.inventory.itemInHandIndex, flag && this.inventory.getItemInHand() != null ? this.inventory.getItemInHand().count : 1), false);
    }

    public EntityItem drop(ItemStack itemstack) {
        return this.a(itemstack, false);
    }

    public EntityItem a(ItemStack itemstack, boolean flag) {
        if (itemstack == null) {
            return null;
        } else {
            EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY - 0.30000001192092896D + (double) this.getHeadHeight(), this.locZ, itemstack);

            entityitem.pickupDelay = 40;
            float f = 0.1F;
            float f1;

            if (flag) {
                f1 = this.random.nextFloat() * 0.5F;
                float f2 = this.random.nextFloat() * 3.1415927F * 2.0F;

                entityitem.motX = (double) (-MathHelper.sin(f2) * f1);
                entityitem.motZ = (double) (MathHelper.cos(f2) * f1);
                entityitem.motY = 0.20000000298023224D;
            } else {
                f = 0.3F;
                entityitem.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
                entityitem.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F) * f);
                entityitem.motY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F) * f + 0.1F);
                f = 0.02F;
                f1 = this.random.nextFloat() * 3.1415927F * 2.0F;
                f *= this.random.nextFloat();
                entityitem.motX += Math.cos((double) f1) * (double) f;
                entityitem.motY += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                entityitem.motZ += Math.sin((double) f1) * (double) f;
            }

            // CraftBukkit start
            Player player = (Player) this.getBukkitEntity();
            CraftItem drop = new CraftItem(this.world.getServer(), entityitem);

            PlayerDropItemEvent event = new PlayerDropItemEvent(player, drop);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                player.getInventory().addItem(drop.getItemStack());
                return null;
            }
            // CraftBukkit end

            this.a(entityitem);
            this.a(StatisticList.v, 1);
            return entityitem;
        }
    }

    protected void a(EntityItem entityitem) {
        this.world.addEntity(entityitem);
    }

    public float a(Block block, boolean flag) {
        float f = this.inventory.a(block);

        if (f > 1.0F) {
            int i = EnchantmentManager.getDigSpeedEnchantmentLevel(this);
            ItemStack itemstack = this.inventory.getItemInHand();

            if (i > 0 && itemstack != null) {
                float f1 = (float) (i * i + 1);

                if (!itemstack.b(block) && f <= 1.0F) {
                    f += f1 * 0.08F;
                } else {
                    f += f1;
                }
            }
        }

        if (this.hasEffect(MobEffectList.FASTER_DIG)) {
            f *= 1.0F + (float) (this.getEffect(MobEffectList.FASTER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.hasEffect(MobEffectList.SLOWER_DIG)) {
            f *= 1.0F - (float) (this.getEffect(MobEffectList.SLOWER_DIG).getAmplifier() + 1) * 0.2F;
        }

        if (this.a(Material.WATER) && !EnchantmentManager.hasWaterWorkerEnchantment(this)) {
            f /= 5.0F;
        }

        if (!this.onGround) {
            f /= 5.0F;
        }

        return f;
    }

    public boolean a(Block block) {
        return this.inventory.b(block);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory");

        this.inventory.b(nbttaglist);
        this.inventory.itemInHandIndex = nbttagcompound.getInt("SelectedItemSlot");
        this.sleeping = nbttagcompound.getBoolean("Sleeping");
        this.sleepTicks = nbttagcompound.getShort("SleepTimer");
        this.exp = nbttagcompound.getFloat("XpP");
        this.expLevel = nbttagcompound.getInt("XpLevel");
        this.expTotal = nbttagcompound.getInt("XpTotal");
        this.setScore(nbttagcompound.getInt("Score"));
        if (this.sleeping) {
            this.cb = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            this.a(true, true, false);
        }

        // CraftBukkit start
        this.spawnWorld = nbttagcompound.getString("SpawnWorld");
        if ("".equals(spawnWorld)) {
            this.spawnWorld = this.world.getServer().getWorlds().get(0).getName();
        }
        // CraftBukkit end

        if (nbttagcompound.hasKey("SpawnX") && nbttagcompound.hasKey("SpawnY") && nbttagcompound.hasKey("SpawnZ")) {
            this.c = new ChunkCoordinates(nbttagcompound.getInt("SpawnX"), nbttagcompound.getInt("SpawnY"), nbttagcompound.getInt("SpawnZ"));
            this.d = nbttagcompound.getBoolean("SpawnForced");
        }

        this.foodData.a(nbttagcompound);
        this.abilities.b(nbttagcompound);
        if (nbttagcompound.hasKey("EnderItems")) {
            NBTTagList nbttaglist1 = nbttagcompound.getList("EnderItems");

            this.enderChest.a(nbttaglist1);
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("Inventory", this.inventory.a(new NBTTagList()));
        nbttagcompound.setInt("SelectedItemSlot", this.inventory.itemInHandIndex);
        nbttagcompound.setBoolean("Sleeping", this.sleeping);
        nbttagcompound.setShort("SleepTimer", (short) this.sleepTicks);
        nbttagcompound.setFloat("XpP", this.exp);
        nbttagcompound.setInt("XpLevel", this.expLevel);
        nbttagcompound.setInt("XpTotal", this.expTotal);
        nbttagcompound.setInt("Score", this.getScore());
        if (this.c != null) {
            nbttagcompound.setInt("SpawnX", this.c.x);
            nbttagcompound.setInt("SpawnY", this.c.y);
            nbttagcompound.setInt("SpawnZ", this.c.z);
            nbttagcompound.setBoolean("SpawnForced", this.d);
            nbttagcompound.setString("SpawnWorld", spawnWorld); // CraftBukkit - fixes bed spawns for multiworld worlds
        }

        this.foodData.b(nbttagcompound);
        this.abilities.a(nbttagcompound);
        nbttagcompound.set("EnderItems", this.enderChest.h());
    }

    public void openContainer(IInventory iinventory) {}

    public void openHopper(TileEntityHopper tileentityhopper) {}

    public void openMinecartHopper(EntityMinecartHopper entityminecarthopper) {}

    public void startEnchanting(int i, int j, int k, String s) {}

    public void openAnvil(int i, int j, int k) {}

    public void startCrafting(int i, int j, int k) {}

    public float getHeadHeight() {
        return 0.12F;
    }

    protected void e_() {
        this.height = 1.62F;
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        if (this.isInvulnerable()) {
            return false;
        } else if (this.abilities.isInvulnerable && !damagesource.ignoresInvulnerability()) {
            return false;
        } else {
            this.bC = 0;
            if (this.getHealth() <= 0) {
                return false;
            } else {
                if (this.isSleeping() && !this.world.isStatic) {
                    this.a(true, true, false);
                }

                if (damagesource.p()) {
                    if (this.world.difficulty == 0) {
                        return false; // CraftBukkit - i = 0 -> return false
                    }

                    if (this.world.difficulty == 1) {
                        i = i / 2 + 1;
                    }

                    if (this.world.difficulty == 3) {
                        i = i * 3 / 2;
                    }
                }

                if (false && i == 0) { // CraftBukkit - Don't filter out 0 damage
                    return false;
                } else {
                    Entity entity = damagesource.getEntity();

                    if (entity instanceof EntityArrow && ((EntityArrow) entity).shooter != null) {
                        entity = ((EntityArrow) entity).shooter;
                    }

                    if (entity instanceof EntityLiving) {
                        this.a((EntityLiving) entity, false);
                    }

                    this.a(StatisticList.x, i);
                    return super.damageEntity(damagesource, i);
                }
            }
        }
    }

    public boolean a(EntityHuman entityhuman) {
        ScoreboardTeam scoreboardteam = this.getScoreboardTeam();
        ScoreboardTeam scoreboardteam1 = entityhuman.getScoreboardTeam();

        return scoreboardteam != scoreboardteam1 ? true : (scoreboardteam != null ? scoreboardteam.allowFriendlyFire() : true);
    }

    protected void a(EntityLiving entityliving, boolean flag) {
        if (!(entityliving instanceof EntityCreeper) && !(entityliving instanceof EntityGhast)) {
            if (entityliving instanceof EntityWolf) {
                EntityWolf entitywolf = (EntityWolf) entityliving;

                if (entitywolf.isTamed() && this.name.equals(entitywolf.getOwnerName())) {
                    return;
                }
            }

            if (!(entityliving instanceof EntityHuman) || this.a((EntityHuman) entityliving)) {
                List list = this.world.a(EntityWolf.class, AxisAlignedBB.a().a(this.locX, this.locY, this.locZ, this.locX + 1.0D, this.locY + 1.0D, this.locZ + 1.0D).grow(16.0D, 4.0D, 16.0D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityWolf entitywolf1 = (EntityWolf) iterator.next();

                    if (entitywolf1.isTamed() && entitywolf1.l() == null && this.name.equals(entitywolf1.getOwnerName()) && (!flag || !entitywolf1.isSitting())) {
                        entitywolf1.setSitting(false);
                        entitywolf1.setTarget(entityliving);
                    }
                }
            }
        }
    }

    protected void k(int i) {
        this.inventory.g(i);
    }

    public int aZ() {
        return this.inventory.l();
    }

    public float ca() {
        int i = 0;
        ItemStack[] aitemstack = this.inventory.armor;
        int j = aitemstack.length;

        for (int k = 0; k < j; ++k) {
            ItemStack itemstack = aitemstack[k];

            if (itemstack != null) {
                ++i;
            }
        }

        return (float) i / (float) this.inventory.armor.length;
    }

    protected void d(DamageSource damagesource, int i) {
        if (!this.isInvulnerable()) {
            if (!damagesource.ignoresArmor() && this.bk()) {
                i = 1 + i >> 1;
            }

            i = this.b(damagesource, i);
            i = this.c(damagesource, i);
            this.j(damagesource.f());
            int j = this.getHealth();

            this.setHealth(this.getHealth() - i);
            this.bt.a(damagesource, j, i);
        }
    }

    public void openFurnace(TileEntityFurnace tileentityfurnace) {}

    public void openDispenser(TileEntityDispenser tileentitydispenser) {}

    public void a(TileEntity tileentity) {}

    public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {}

    public void openBeacon(TileEntityBeacon tileentitybeacon) {}

    public void openTrade(IMerchant imerchant, String s) {}

    public void d(ItemStack itemstack) {}

    public boolean p(Entity entity) {
        if (entity.a_(this)) {
            return true;
        } else {
            ItemStack itemstack = this.cb();

            if (itemstack != null && entity instanceof EntityLiving) {
                if (this.abilities.canInstantlyBuild) {
                    itemstack = itemstack.cloneItemStack();
                }

                if (itemstack.a((EntityLiving) entity)) {
                    // CraftBukkit - bypass infinite items; <= 0 -> == 0
                    if (itemstack.count == 0 && !this.abilities.canInstantlyBuild) {
                        this.cc();
                    }

                    return true;
                }
            }

            return false;
        }
    }

    public ItemStack cb() {
        return this.inventory.getItemInHand();
    }

    public void cc() {
        this.inventory.setItem(this.inventory.itemInHandIndex, (ItemStack) null);
    }

    public double V() {
        return (double) (this.height - 0.5F);
    }

    public void attack(Entity entity) {
        if (entity.ap()) {
            if (!entity.j(this)) {
                int i = this.inventory.a(entity);

                if (this.hasEffect(MobEffectList.INCREASE_DAMAGE)) {
                    i += 3 << this.getEffect(MobEffectList.INCREASE_DAMAGE).getAmplifier();
                }

                if (this.hasEffect(MobEffectList.WEAKNESS)) {
                    i -= 2 << this.getEffect(MobEffectList.WEAKNESS).getAmplifier();
                }

                int j = 0;
                int k = 0;

                if (entity instanceof EntityLiving) {
                    k = EnchantmentManager.a((EntityLiving) this, (EntityLiving) entity);
                    j += EnchantmentManager.getKnockbackEnchantmentLevel(this, (EntityLiving) entity);
                }

                if (this.isSprinting()) {
                    ++j;
                }

                if (i > 0 || k > 0) {
                    boolean flag = this.fallDistance > 0.0F && !this.onGround && !this.g_() && !this.G() && !this.hasEffect(MobEffectList.BLINDNESS) && this.vehicle == null && entity instanceof EntityLiving;

                    if (flag && i > 0) {
                        i += this.random.nextInt(i / 2 + 2);
                    }

                    i += k;
                    boolean flag1 = false;
                    int l = EnchantmentManager.getFireAspectEnchantmentLevel(this);

                    if (entity instanceof EntityLiving && l > 0 && !entity.isBurning()) {
                        flag1 = true;
                        entity.setOnFire(1);
                    }

                    boolean flag2 = entity.damageEntity(DamageSource.playerAttack(this), i);

                    // CraftBukkit start - Return when the damage fails so that the item will not lose durability
                    if (!flag2) {
                        if (flag1) {
                            entity.extinguish();
                        }
                        return;
                    }
                    // CraftBukkit end

                    if (flag2) {
                        if (j > 0) {
                            entity.g((double) (-MathHelper.sin(this.yaw * 3.1415927F / 180.0F) * (float) j * 0.5F), 0.1D, (double) (MathHelper.cos(this.yaw * 3.1415927F / 180.0F) * (float) j * 0.5F));
                            this.motX *= 0.6D;
                            this.motZ *= 0.6D;
                            this.setSprinting(false);
                        }

                        if (flag) {
                            this.b(entity);
                        }

                        if (k > 0) {
                            this.c(entity);
                        }

                        if (i >= 18) {
                            this.a((Statistic) AchievementList.E);
                        }

                        this.l(entity);
                        if (entity instanceof EntityLiving) {
                            EnchantmentThorns.a(this, (EntityLiving) entity, this.random);
                        }
                    }

                    ItemStack itemstack = this.cb();
                    Object object = entity;

                    if (entity instanceof EntityComplexPart) {
                        IComplex icomplex = ((EntityComplexPart) entity).owner;

                        if (icomplex != null && icomplex instanceof EntityLiving) {
                            object = (EntityLiving) icomplex;
                        }
                    }

                    if (itemstack != null && object instanceof EntityLiving) {
                        itemstack.a((EntityLiving) object, this);
                        // CraftBukkit - bypass infinite items; <= 0 -> == 0
                        if (itemstack.count == 0) {
                            this.cc();
                        }
                    }

                    if (entity instanceof EntityLiving) {
                        if (entity.isAlive()) {
                            this.a((EntityLiving) entity, true);
                        }

                        this.a(StatisticList.w, i);
                        if (l > 0 && flag2) {
                            // CraftBukkit start - Call a combust event when somebody hits with a fire enchanted item
                            EntityCombustByEntityEvent combustEvent = new EntityCombustByEntityEvent(this.getBukkitEntity(), entity.getBukkitEntity(), l * 4);
                            org.bukkit.Bukkit.getPluginManager().callEvent(combustEvent);

                            if (!combustEvent.isCancelled()) {
                                entity.setOnFire(combustEvent.getDuration());
                            }
                            // CraftBukkit end
                        } else if (flag1) {
                            entity.extinguish();
                        }
                    }

                    this.j(0.3F);
                }
            }
        }
    }

    public void b(Entity entity) {}

    public void c(Entity entity) {}

    public void die() {
        super.die();
        this.defaultContainer.b(this);
        if (this.activeContainer != null) {
            this.activeContainer.b(this);
        }
    }

    public boolean inBlock() {
        return !this.sleeping && super.inBlock();
    }

    public boolean ce() {
        return false;
    }

    public EnumBedResult a(int i, int j, int k) {
        if (!this.world.isStatic) {
            if (this.isSleeping() || !this.isAlive()) {
                return EnumBedResult.OTHER_PROBLEM;
            }

            if (!this.world.worldProvider.d()) {
                return EnumBedResult.NOT_POSSIBLE_HERE;
            }

            if (this.world.u()) {
                return EnumBedResult.NOT_POSSIBLE_NOW;
            }

            if (Math.abs(this.locX - (double) i) > 3.0D || Math.abs(this.locY - (double) j) > 2.0D || Math.abs(this.locZ - (double) k) > 3.0D) {
                return EnumBedResult.TOO_FAR_AWAY;
            }

            double d0 = 8.0D;
            double d1 = 5.0D;
            List list = this.world.a(EntityMonster.class, AxisAlignedBB.a().a((double) i - d0, (double) j - d1, (double) k - d0, (double) i + d0, (double) j + d1, (double) k + d0));

            if (!list.isEmpty()) {
                return EnumBedResult.NOT_SAFE;
            }
        }

        // CraftBukkit start
        if (this.getBukkitEntity() instanceof Player) {
            Player player = (Player) this.getBukkitEntity();
            org.bukkit.block.Block bed = this.world.getWorld().getBlockAt(i, j, k);

            PlayerBedEnterEvent event = new PlayerBedEnterEvent(player, bed);
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return EnumBedResult.OTHER_PROBLEM;
            }
        }
        // CraftBukkit end

        this.a(0.2F, 0.2F);
        this.height = 0.2F;
        if (this.world.isLoaded(i, j, k)) {
            int l = this.world.getData(i, j, k);
            int i1 = BlockBed.j(l);
            float f = 0.5F;
            float f1 = 0.5F;

            switch (i1) {
            case 0:
                f1 = 0.9F;
                break;

            case 1:
                f = 0.1F;
                break;

            case 2:
                f1 = 0.1F;
                break;

            case 3:
                f = 0.9F;
            }

            this.x(i1);
            this.setPosition((double) ((float) i + f), (double) ((float) j + 0.9375F), (double) ((float) k + f1));
        } else {
            this.setPosition((double) ((float) i + 0.5F), (double) ((float) j + 0.9375F), (double) ((float) k + 0.5F));
        }

        this.sleeping = true;
        this.sleepTicks = 0;
        this.cb = new ChunkCoordinates(i, j, k);
        this.motX = this.motZ = this.motY = 0.0D;
        if (!this.world.isStatic) {
            this.world.everyoneSleeping();
        }

        return EnumBedResult.OK;
    }

    private void x(int i) {
        this.cc = 0.0F;
        this.cd = 0.0F;
        switch (i) {
        case 0:
            this.cd = -1.8F;
            break;

        case 1:
            this.cc = 1.8F;
            break;

        case 2:
            this.cd = 1.8F;
            break;

        case 3:
            this.cc = -1.8F;
        }
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        this.a(0.6F, 1.8F);
        this.e_();
        ChunkCoordinates chunkcoordinates = this.cb;
        ChunkCoordinates chunkcoordinates1 = this.cb;

        if (chunkcoordinates != null && this.world.getTypeId(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z) == Block.BED.id) {
            BlockBed.a(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, false);
            chunkcoordinates1 = BlockBed.b(this.world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, 0);
            if (chunkcoordinates1 == null) {
                chunkcoordinates1 = new ChunkCoordinates(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z);
            }

            this.setPosition((double) ((float) chunkcoordinates1.x + 0.5F), (double) ((float) chunkcoordinates1.y + this.height + 0.1F), (double) ((float) chunkcoordinates1.z + 0.5F));
        }

        this.sleeping = false;
        if (!this.world.isStatic && flag1) {
            this.world.everyoneSleeping();
        }

        // CraftBukkit start
        if (this.getBukkitEntity() instanceof Player) {
            Player player = (Player) this.getBukkitEntity();

            org.bukkit.block.Block bed;
            if (chunkcoordinates != null) {
                bed = this.world.getWorld().getBlockAt(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z);
            } else {
                bed = this.world.getWorld().getBlockAt(player.getLocation());
            }

            PlayerBedLeaveEvent event = new PlayerBedLeaveEvent(player, bed);
            this.world.getServer().getPluginManager().callEvent(event);
        }
        // CraftBukkit end

        if (flag) {
            this.sleepTicks = 0;
        } else {
            this.sleepTicks = 100;
        }

        if (flag2) {
            this.setRespawnPosition(this.cb, false);
        }
    }

    private boolean i() {
        return this.world.getTypeId(this.cb.x, this.cb.y, this.cb.z) == Block.BED.id;
    }

    public static ChunkCoordinates getBed(World world, ChunkCoordinates chunkcoordinates, boolean flag) {
        IChunkProvider ichunkprovider = world.J();

        ichunkprovider.getChunkAt(chunkcoordinates.x - 3 >> 4, chunkcoordinates.z - 3 >> 4);
        ichunkprovider.getChunkAt(chunkcoordinates.x + 3 >> 4, chunkcoordinates.z - 3 >> 4);
        ichunkprovider.getChunkAt(chunkcoordinates.x - 3 >> 4, chunkcoordinates.z + 3 >> 4);
        ichunkprovider.getChunkAt(chunkcoordinates.x + 3 >> 4, chunkcoordinates.z + 3 >> 4);
        if (world.getTypeId(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z) == Block.BED.id) {
            ChunkCoordinates chunkcoordinates1 = BlockBed.b(world, chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z, 0);

            return chunkcoordinates1;
        } else {
            Material material = world.getMaterial(chunkcoordinates.x, chunkcoordinates.y, chunkcoordinates.z);
            Material material1 = world.getMaterial(chunkcoordinates.x, chunkcoordinates.y + 1, chunkcoordinates.z);
            boolean flag1 = !material.isBuildable() && !material.isLiquid();
            boolean flag2 = !material1.isBuildable() && !material1.isLiquid();

            return flag && flag1 && flag2 ? chunkcoordinates : null;
        }
    }

    public boolean isSleeping() {
        return this.sleeping;
    }

    public boolean isDeeplySleeping() {
        return this.sleeping && this.sleepTicks >= 100;
    }

    protected void b(int i, boolean flag) {
        byte b0 = this.datawatcher.getByte(16);

        if (flag) {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 | 1 << i)));
        } else {
            this.datawatcher.watch(16, Byte.valueOf((byte) (b0 & ~(1 << i))));
        }
    }

    public void b(String s) {}

    public ChunkCoordinates getBed() {
        return this.c;
    }

    public boolean isRespawnForced() {
        return this.d;
    }

    public void setRespawnPosition(ChunkCoordinates chunkcoordinates, boolean flag) {
        if (chunkcoordinates != null) {
            this.c = new ChunkCoordinates(chunkcoordinates);
            this.d = flag;
            this.spawnWorld = this.world.worldData.getName(); // CraftBukkit
        } else {
            this.c = null;
            this.d = false;
            this.spawnWorld = ""; // CraftBukkit
        }
    }

    public void a(Statistic statistic) {
        this.a(statistic, 1);
    }

    public void a(Statistic statistic, int i) {}

    protected void bl() {
        super.bl();
        this.a(StatisticList.u, 1);
        if (this.isSprinting()) {
            this.j(0.8F);
        } else {
            this.j(0.2F);
        }
    }

    public void e(float f, float f1) {
        double d0 = this.locX;
        double d1 = this.locY;
        double d2 = this.locZ;

        if (this.abilities.isFlying && this.vehicle == null) {
            double d3 = this.motY;
            float f2 = this.aP;

            this.aP = this.abilities.a();
            super.e(f, f1);
            this.motY = d3 * 0.6D;
            this.aP = f2;
        } else {
            super.e(f, f1);
        }

        this.checkMovement(this.locX - d0, this.locY - d1, this.locZ - d2);
    }

    public void checkMovement(double d0, double d1, double d2) {
        if (this.vehicle == null) {
            int i;

            if (this.a(Material.WATER)) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.q, i);
                    this.j(0.015F * (float) i * 0.01F);
                }
            } else if (this.G()) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.m, i);
                    this.j(0.015F * (float) i * 0.01F);
                }
            } else if (this.g_()) {
                if (d1 > 0.0D) {
                    this.a(StatisticList.o, (int) Math.round(d1 * 100.0D));
                }
            } else if (this.onGround) {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 0) {
                    this.a(StatisticList.l, i);
                    if (this.isSprinting()) {
                        this.j(0.099999994F * (float) i * 0.01F);
                    } else {
                        this.j(0.01F * (float) i * 0.01F);
                    }
                }
            } else {
                i = Math.round(MathHelper.sqrt(d0 * d0 + d2 * d2) * 100.0F);
                if (i > 25) {
                    this.a(StatisticList.p, i);
                }
            }
        }
    }

    private void k(double d0, double d1, double d2) {
        if (this.vehicle != null) {
            int i = Math.round(MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 100.0F);

            if (i > 0) {
                if (this.vehicle instanceof EntityMinecartAbstract) {
                    this.a(StatisticList.r, i);
                    if (this.e == null) {
                        this.e = new ChunkCoordinates(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
                    } else if ((double) this.e.e(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) >= 1000000.0D) {
                        this.a((Statistic) AchievementList.q, 1);
                    }
                } else if (this.vehicle instanceof EntityBoat) {
                    this.a(StatisticList.s, i);
                } else if (this.vehicle instanceof EntityPig) {
                    this.a(StatisticList.t, i);
                }
            }
        }
    }

    protected void a(float f) {
        if (!this.abilities.canFly) {
            if (f >= 2.0F) {
                this.a(StatisticList.n, (int) Math.round((double) f * 100.0D));
            }

            super.a(f);
        }
    }

    public void a(EntityLiving entityliving) {
        if (entityliving instanceof IMonster) {
            this.a((Statistic) AchievementList.s);
        }
    }

    public void al() {
        if (!this.abilities.isFlying) {
            super.al();
        }
    }

    public ItemStack q(int i) {
        return this.inventory.f(i);
    }

    protected void bH() {}

    protected void bI() {}

    public void giveExp(int i) {
        this.addScore(i);
        int j = Integer.MAX_VALUE - this.expTotal;

        if (i > j) {
            i = j;
        }

        this.exp += (float) i / (float) this.getExpToLevel();

        for (this.expTotal += i; this.exp >= 1.0F; this.exp /= (float) this.getExpToLevel()) {
            this.exp = (this.exp - 1.0F) * (float) this.getExpToLevel();
            this.levelDown(1);
        }
    }

    public void levelDown(int i) {
        this.expLevel += i;
        if (this.expLevel < 0) {
            this.expLevel = 0;
            this.exp = 0.0F;
            this.expTotal = 0;
        }

        if (i > 0 && this.expLevel % 5 == 0 && (float) this.h < (float) this.ticksLived - 100.0F) {
            float f = this.expLevel > 30 ? 1.0F : (float) this.expLevel / 30.0F;

            this.world.makeSound(this, "random.levelup", f * 0.75F, 1.0F);
            this.h = this.ticksLived;
        }
    }

    public int getExpToLevel() {
        return this.expLevel >= 30 ? 62 + (this.expLevel - 30) * 7 : (this.expLevel >= 15 ? 17 + (this.expLevel - 15) * 3 : 17);
    }

    public void j(float f) {
        if (!this.abilities.isInvulnerable) {
            if (!this.world.isStatic) {
                this.foodData.a(f);
            }
        }
    }

    public FoodMetaData getFoodData() {
        return this.foodData;
    }

    public boolean i(boolean flag) {
        return (flag || this.foodData.c()) && !this.abilities.isInvulnerable;
    }

    public boolean cm() {
        return this.getHealth() > 0 && this.getHealth() < this.maxHealth; // CraftBukkit - this.getMaxHealth() -> this.maxHealth
    }

    public void a(ItemStack itemstack, int i) {
        if (itemstack != this.f) {
            this.f = itemstack;
            this.g = i;
            if (!this.world.isStatic) {
                this.e(true);
            }
        }
    }

    public boolean e(int i, int j, int k) {
        if (this.abilities.mayBuild) {
            return true;
        } else {
            int l = this.world.getTypeId(i, j, k);

            if (l > 0) {
                Block block = Block.byId[l];

                if (block.material.q()) {
                    return true;
                }

                if (this.cb() != null) {
                    ItemStack itemstack = this.cb();

                    if (itemstack.b(block) || itemstack.a(block) > 1.0F) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean a(int i, int j, int k, int l, ItemStack itemstack) {
        return this.abilities.mayBuild ? true : (itemstack != null ? itemstack.y() : false);
    }

    protected int getExpValue(EntityHuman entityhuman) {
        if (this.world.getGameRules().getBoolean("keepInventory")) {
            return 0;
        } else {
            int i = this.expLevel * 7;

            return i > 100 ? 100 : i;
        }
    }

    protected boolean alwaysGivesExp() {
        return true;
    }

    public String getLocalizedName() {
        return this.name;
    }

    public boolean getCustomNameVisible() {
        return super.getCustomNameVisible();
    }

    /* CraftBukkit start - We use canPickUpLoot on players, can't have this
    public boolean bS() {
        return false;
    }
    // CraftBukkit end */

    public void copyTo(EntityHuman entityhuman, boolean flag) {
        if (flag) {
            this.inventory.b(entityhuman.inventory);
            this.health = entityhuman.health;
            this.foodData = entityhuman.foodData;
            this.expLevel = entityhuman.expLevel;
            this.expTotal = entityhuman.expTotal;
            this.exp = entityhuman.exp;
            this.setScore(entityhuman.getScore());
            this.as = entityhuman.as;
        } else if (this.world.getGameRules().getBoolean("keepInventory")) {
            this.inventory.b(entityhuman.inventory);
            this.expLevel = entityhuman.expLevel;
            this.expTotal = entityhuman.expTotal;
            this.exp = entityhuman.exp;
            this.setScore(entityhuman.getScore());
        }

        this.enderChest = entityhuman.enderChest;
    }

    protected boolean f_() {
        return !this.abilities.isFlying;
    }

    public void updateAbilities() {}

    public void a(EnumGamemode enumgamemode) {}

    public String getName() {
        return this.name;
    }

    public LocaleLanguage getLocale() {
        return LocaleLanguage.a();
    }

    public String a(String s, Object... aobject) {
        return this.getLocale().a(s, aobject);
    }

    public InventoryEnderChest getEnderChest() {
        return this.enderChest;
    }

    public ItemStack getEquipment(int i) {
        return i == 0 ? this.inventory.getItemInHand() : this.inventory.armor[i - 1];
    }

    public ItemStack bG() {
        return this.inventory.getItemInHand();
    }

    public void setEquipment(int i, ItemStack itemstack) {
        this.inventory.armor[i] = itemstack;
    }

    public ItemStack[] getEquipment() {
        return this.inventory.armor;
    }

    public boolean aw() {
        return !this.abilities.isFlying;
    }

    public Scoreboard getScoreboard() {
        return this.world.getScoreboard();
    }

    public ScoreboardTeam getScoreboardTeam() {
        return this.getScoreboard().getPlayerTeam(this.name);
    }

    public String getScoreboardDisplayName() {
        return ScoreboardTeam.getPlayerDisplayName(this.getScoreboardTeam(), this.name);
    }
}
