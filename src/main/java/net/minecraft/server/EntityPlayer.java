package net.minecraft.server;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryType;
// CraftBukkit end

public class EntityPlayer extends EntityHuman implements ICrafting {

    private LocaleLanguage locale = new LocaleLanguage("en_US");
    public NetServerHandler netServerHandler;
    public MinecraftServer server;
    public ItemInWorldManager itemInWorldManager;
    public double d;
    public double e;
    public final List chunkCoordIntPairQueue = new LinkedList();
    public final List g = new LinkedList();
    private int ch = -99999999;
    private int ci = -99999999;
    private boolean cj = true;
    public int lastSentExp = -99999999; // CraftBukkit - private -> public
    public int invulnerableTicks = 60; // CraftBukkit - private -> public
    private int cm = 0;
    private int cn = 0;
    private boolean co = true;
    private ItemStack[] cp = new ItemStack[] { null, null, null, null, null};
    private int containerCounter = 0;
    public boolean h;
    public int ping;
    public boolean viewingCredits = false;
    // CraftBukkit start
    public String displayName;
    public String listName;
    public org.bukkit.Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    // CraftBukkit end

    public EntityPlayer(MinecraftServer minecraftserver, World world, String s, ItemInWorldManager iteminworldmanager) {
        super(world);
        iteminworldmanager.player = this;
        this.itemInWorldManager = iteminworldmanager;
        this.cm = minecraftserver.getServerConfigurationManager().o();
        ChunkCoordinates chunkcoordinates = world.getSpawn();
        int i = chunkcoordinates.x;
        int j = chunkcoordinates.z;
        int k = chunkcoordinates.y;

        if (!world.worldProvider.e && world.getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
            i += this.random.nextInt(20) - 10;
            k = world.h(i, j);
            j += this.random.nextInt(20) - 10;
        }

        this.setPositionRotation((double) i + 0.5D, (double) k, (double) j + 0.5D, 0.0F, 0.0F);
        this.server = minecraftserver;
        this.W = 0.0F;
        this.name = s;
        this.height = 0.0F;
        this.displayName = this.name; // CraftBukkit
        this.listName = this.name; // CraftBukkit
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("playerGameType")) {
            this.itemInWorldManager.setGameMode(EnumGamemode.a(nbttagcompound.getInt("playerGameType")));
        }
        this.getBukkitEntity().readExtraData(nbttagcompound); // CraftBukkit
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("playerGameType", this.itemInWorldManager.getGameMode().a());
        this.getBukkitEntity().setExtraData(nbttagcompound); // CraftBukkit
    }

    // CraftBukkit start - world fallback code, either respawn location or global spawn
    public void spawnIn(World world) {
        super.spawnIn(world);
        if (world == null) {
            this.dead = false;
            ChunkCoordinates position = null;
            if (this.spawnWorld != null && !this.spawnWorld.equals("")) {
                CraftWorld cworld = (CraftWorld) Bukkit.getServer().getWorld(this.spawnWorld);
                if (cworld != null && this.getBed() != null) {
                    world = cworld.getHandle();
                    position = EntityHuman.getBed(cworld.getHandle(), this.getBed());
                }
            }
            if (world == null || position == null) {
                world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = world.getSpawn();
            }
            this.world = world;
            this.setPosition(position.x + 0.5, position.y, position.z + 0.5);
        }
        this.dimension = ((WorldServer) this.world).dimension;
        this.itemInWorldManager.a((WorldServer) world);
    }
    // CraftBukkit end

    public void levelDown(int i) {
        super.levelDown(i);
        this.lastSentExp = -1;
    }

    public void syncInventory() {
        this.activeContainer.addSlotListener(this);
    }

    public ItemStack[] getEquipment() {
        return this.cp;
    }

    protected void d_() {
        this.height = 0.0F;
    }

    public float getHeadHeight() {
        return 1.62F;
    }

    public void h_() {
        this.itemInWorldManager.a();
        --this.invulnerableTicks;
        this.activeContainer.b();

        int i;

        for (i = 0; i < 5; ++i) {
            ItemStack itemstack = this.b(i);

            if (itemstack != this.cp[i]) {
                this.q().getTracker().a(this, new Packet5EntityEquipment(this.id, i, itemstack));
                this.cp[i] = itemstack;
            }
        }

        if (!this.chunkCoordIntPairQueue.isEmpty()) {
            ArrayList arraylist = new ArrayList();
            Iterator iterator = this.chunkCoordIntPairQueue.iterator();
            ArrayList arraylist1 = new ArrayList();

            while (iterator.hasNext() && arraylist.size() < 5) {
                ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) iterator.next();

                iterator.remove();
                if (chunkcoordintpair != null && this.world.isLoaded(chunkcoordintpair.x << 4, 0, chunkcoordintpair.z << 4)) {
                    arraylist.add(this.world.getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z));
                    arraylist1.addAll(((WorldServer) this.world).getTileEntities(chunkcoordintpair.x * 16, 0, chunkcoordintpair.z * 16, chunkcoordintpair.x * 16 + 16, 256, chunkcoordintpair.z * 16 + 16));
                }
            }

            if (!arraylist.isEmpty()) {
                // CraftBukkit start - don't use map chunk bulk for now TODO: fix this
                for (Object object : arraylist) {
                    this.netServerHandler.sendPacket(new Packet51MapChunk((Chunk) object, true, 0xffff));
                }
                // this.netServerHandler.sendPacket(new Packet56MapChunkBulk(arraylist));
                // CraftBukkit end

                Iterator iterator1 = arraylist1.iterator();

                while (iterator1.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator1.next();

                    this.a(tileentity);
                }
            }
        }

        if (!this.g.isEmpty()) {
            i = Math.min(this.g.size(), 127);
            int[] aint = new int[i];
            Iterator iterator2 = this.g.iterator();
            int j = 0;

            while (iterator2.hasNext() && j < i) {
                aint[j++] = ((Integer) iterator2.next()).intValue();
                iterator2.remove();
            }

            this.netServerHandler.sendPacket(new Packet29DestroyEntity(aint));
        }
    }

    public void g() {
        super.h_();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (itemstack != null && Item.byId[itemstack.id].m_() && this.netServerHandler.lowPriorityCount() <= 2) {
                Packet packet = ((ItemWorldMapBase) Item.byId[itemstack.id]).c(itemstack, this.world, this);

                if (packet != null) {
                    this.netServerHandler.sendPacket(packet);
                }
            }
        }

        if (this.bX) {
            // if (this.server.getAllowNether()) { // CraftBukkit
                if (this.activeContainer != this.defaultContainer) {
                    this.closeInventory();
                }

                if (this.vehicle != null) {
                    this.mount(this.vehicle);
                } else {
                    this.bY += 0.0125F;
                    if (this.bY >= 1.0F) {
                        this.bY = 1.0F;
                        this.bW = 10;
                        boolean flag = false;
                        byte b0;

                        if (this.dimension == -1) {
                            b0 = 0;
                        } else {
                            b0 = -1;
                        }

                        this.server.getServerConfigurationManager().changeDimension(this, b0);
                        this.lastSentExp = -1;
                        this.ch = -1;
                        this.ci = -1;
                        this.a((Statistic) AchievementList.x);
                    }
                }

                this.bX = false;
            // } // CraftBukkit
        } else {
            if (this.bY > 0.0F) {
                this.bY -= 0.05F;
            }

            if (this.bY < 0.0F) {
                this.bY = 0.0F;
            }
        }

        if (this.bW > 0) {
            --this.bW;
        }

        if (this.getHealth() != this.ch || this.ci != this.foodData.a() || this.foodData.e() == 0.0F != this.cj) {
            this.netServerHandler.sendPacket(new Packet8UpdateHealth(this.getHealth(), this.foodData.a(), this.foodData.e()));
            this.ch = this.getHealth();
            this.ci = this.foodData.a();
            this.cj = this.foodData.e() == 0.0F;
        }

        if (this.expTotal != this.lastSentExp) {
            this.lastSentExp = this.expTotal;
            this.netServerHandler.sendPacket(new Packet43SetExperience(this.exp, this.expTotal, this.expLevel));
        }

        // CraftBukkit start
        if (this.oldLevel == -1) {
            this.oldLevel = this.expLevel;
        }

        if (this.oldLevel != this.expLevel) {
            CraftEventFactory.callPlayerLevelChangeEvent(this.world.getServer().getPlayer((EntityPlayer) this), this.oldLevel, this.expLevel);
            this.oldLevel = this.expLevel;
        }
        // CraftBukkit end
    }

    public ItemStack b(int i) {
        return i == 0 ? this.inventory.getItemInHand() : this.inventory.armor[i - 1];
    }

    public void die(DamageSource damagesource) {
        // CraftBukkit start
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>();

        for (int i = 0; i < this.inventory.items.length; ++i) {
            if (this.inventory.items[i] != null) {
                loot.add(new CraftItemStack(this.inventory.items[i]));
            }
        }

        for (int i = 0; i < this.inventory.armor.length; ++i) {
            if (this.inventory.armor[i] != null) {
                loot.add(new CraftItemStack(this.inventory.armor[i]));
            }
        }

        org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, damagesource.getLocalizedDeathMessage(this));

        String deathMessage = event.getDeathMessage();

        if (deathMessage != null && deathMessage.length() > 0) {
            this.server.getServerConfigurationManager().sendAll(new Packet3Chat(event.getDeathMessage()));
        }

        // CraftBukkit - we clean the player's inventory after the EntityDeathEvent is called so plugins can get the exact state of the inventory.
        for (int i = 0; i < this.inventory.items.length; ++i) {
            this.inventory.items[i] = null;
        }

        for (int i = 0; i < this.inventory.armor.length; ++i) {
            this.inventory.armor[i] = null;
        }

        this.closeInventory();
        // CraftBukkit end
    }

    public boolean damageEntity(DamageSource damagesource, int i) {
        if (this.invulnerableTicks > 0) {
            return false;
        } else {
            // CraftBukkit - this.server.getPvP() -> this.world.pvpMode
            if (!this.world.pvpMode && damagesource instanceof EntityDamageSource) {
                Entity entity = damagesource.getEntity();

                if (entity instanceof EntityHuman) {
                    return false;
                }

                if (entity instanceof EntityArrow) {
                    EntityArrow entityarrow = (EntityArrow) entity;

                    if (entityarrow.shooter instanceof EntityHuman) {
                        return false;
                    }
                }
            }

            return super.damageEntity(damagesource, i);
        }
    }

    protected boolean h() {
        return this.server.getPvP();
    }

    public void c(int i) {
        if (this.dimension == 1 && i == 1) {
            this.a((Statistic) AchievementList.C);
            this.world.kill(this);
            this.viewingCredits = true;
            this.netServerHandler.sendPacket(new Packet70Bed(4, 0));
        } else {
            this.a((Statistic) AchievementList.B);
            /* CraftBukkit start - removed to fix our handling of The End portals
            ChunkCoordinates chunkcoordinates = this.server.getWorldServer(i).getDimensionSpawn();

            if (chunkcoordinates != null) {
                this.netServerHandler.a((double) chunkcoordinates.x, (double) chunkcoordinates.y, (double) chunkcoordinates.z, 0.0F, 0.0F);
            }
            // CraftBukkit end */

            this.server.getServerConfigurationManager().changeDimension(this, 1);
            this.lastSentExp = -1;
            this.ch = -1;
            this.ci = -1;
        }
    }

    private void a(TileEntity tileentity) {
        if (tileentity != null) {
            Packet packet = tileentity.e();

            if (packet != null) {
                this.netServerHandler.sendPacket(packet);
            }
        }
    }

    public void receive(Entity entity, int i) {
        if (!entity.dead) {
            EntityTracker entitytracker = this.q().getTracker();

            if (entity instanceof EntityItem) {
                entitytracker.a(entity, new Packet22Collect(entity.id, this.id));
            }

            if (entity instanceof EntityArrow) {
                entitytracker.a(entity, new Packet22Collect(entity.id, this.id));
            }

            if (entity instanceof EntityExperienceOrb) {
                entitytracker.a(entity, new Packet22Collect(entity.id, this.id));
            }
        }

        super.receive(entity, i);
        this.activeContainer.b();
    }

    public void i() {
        if (!this.bH) {
            this.bI = -1;
            this.bH = true;
            this.q().getTracker().a(this, new Packet18ArmAnimation(this, 1));
        }
    }

    public EnumBedResult a(int i, int j, int k) {
        EnumBedResult enumbedresult = super.a(i, j, k);

        if (enumbedresult == EnumBedResult.OK) {
            Packet17EntityLocationAction packet17entitylocationaction = new Packet17EntityLocationAction(this, 0, i, j, k);

            this.q().getTracker().a(this, packet17entitylocationaction);
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.netServerHandler.sendPacket(packet17entitylocationaction);
        }

        return enumbedresult;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (this.isSleeping()) {
            this.q().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(this, 3));
        }

        super.a(flag, flag1, flag2);
        if (this.netServerHandler != null) {
            this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }
    }

    public void mount(Entity entity) {
        // CraftBukkit start
        this.setPassengerOf(entity);
    }

    public void setPassengerOf(Entity entity) {
        // mount(null) doesn't really fly for overloaded methods,
        // so this method is needed

        super.setPassengerOf(entity);
        // CraftBukkit end

        this.netServerHandler.sendPacket(new Packet39AttachEntity(this, this.vehicle));
        this.netServerHandler.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
    }

    protected void a(double d0, boolean flag) {}

    public void b(double d0, boolean flag) {
        super.a(d0, flag);
    }

    public int nextContainerCounter() { // CraftBukkit - private void -> public int
        this.containerCounter = this.containerCounter % 100 + 1;
        return this.containerCounter; // CraftBukkit
    }

    public void startCrafting(int i, int j, int k) {
        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerWorkbench(this.inventory, this.world, i, j, k));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 1, "Crafting", 9));
        this.activeContainer = container; // CraftBukkit - Use container we passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void startEnchanting(int i, int j, int k) {
        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerEnchantTable(this.inventory, this.world, i, j, k));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 4, "Enchanting", 9));
        this.activeContainer = container; // CraftBukkit - Use container we passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openContainer(IInventory iinventory) {
        if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
        }

        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerChest(this.inventory, iinventory));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 0, iinventory.getName(), iinventory.getSize()));
        this.activeContainer = container; // CraftBukkit - Use container passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openFurnace(TileEntityFurnace tileentityfurnace) {
        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerFurnace(this.inventory, tileentityfurnace));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 2, tileentityfurnace.getName(), tileentityfurnace.getSize()));
        this.activeContainer = container; // CraftBukkit - Use container passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openDispenser(TileEntityDispenser tileentitydispenser) {
        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerDispenser(this.inventory, tileentitydispenser));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 3, tileentitydispenser.getName(), tileentitydispenser.getSize()));
        this.activeContainer = container; // CraftBukkit - Use container passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openBrewingStand(TileEntityBrewingStand tileentitybrewingstand) {
        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerBrewingStand(this.inventory, tileentitybrewingstand));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 5, tileentitybrewingstand.getName(), tileentitybrewingstand.getSize()));
        this.activeContainer = container; // CraftBukkit - Use container passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void openTrade(IMerchant imerchant) {
        // CraftBukkit start - INVENTORY_OPEN hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerMerchant(this.inventory, imerchant, this.world));
        if(container == null) return;
        // CraftBukkit end

        this.nextContainerCounter();
        this.activeContainer = container; // CraftBukkit - User container passed to event
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
        InventoryMerchant inventorymerchant = ((ContainerMerchant) this.activeContainer).getMerchantInventory();

        this.netServerHandler.sendPacket(new Packet100OpenWindow(this.containerCounter, 6, inventorymerchant.getName(), inventorymerchant.getSize()));
        MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);

        if (merchantrecipelist != null) {
            try {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                DataOutputStream dataoutputstream = new DataOutputStream(bytearrayoutputstream);

                dataoutputstream.writeInt(this.containerCounter);
                merchantrecipelist.a(dataoutputstream);
                this.netServerHandler.sendPacket(new Packet250CustomPayload("MC|TrList", bytearrayoutputstream.toByteArray()));
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
            }
        }
    }

    public void a(Container container, int i, ItemStack itemstack) {
        if (!(container.getSlot(i) instanceof SlotResult)) {
            if (!this.h) {
                this.netServerHandler.sendPacket(new Packet103SetSlot(container.windowId, i, itemstack));
            }
        }
    }

    public void updateInventory(Container container) {
        this.a(container, container.a());
    }

    public void a(Container container, List list) {
        this.netServerHandler.sendPacket(new Packet104WindowItems(container.windowId, list));
        this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getCarried()));
        // CraftBukkit start - send a Set Slot to update the crafting result slot
        if (java.util.EnumSet.of(InventoryType.CRAFTING,InventoryType.WORKBENCH).contains(container.getBukkitView().getType())) {
            this.netServerHandler.sendPacket(new Packet103SetSlot(container.windowId, 0, container.getSlot(0).getItem()));
        }
        // CraftBukkit end
    }

    public void setContainerData(Container container, int i, int j) {
        this.netServerHandler.sendPacket(new Packet105CraftProgressBar(container.windowId, i, j));
    }

    public void closeInventory() {
        this.netServerHandler.sendPacket(new Packet101CloseWindow(this.activeContainer.windowId));
        this.l();
    }

    public void broadcastCarriedItem() {
        if (!this.h) {
            this.netServerHandler.sendPacket(new Packet103SetSlot(-1, -1, this.inventory.getCarried()));
        }
    }

    public void l() {
        this.activeContainer.a((EntityHuman) this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(Statistic statistic, int i) {
        if (statistic != null) {
            if (!statistic.f) {
                while (i > 100) {
                    this.netServerHandler.sendPacket(new Packet200Statistic(statistic.e, 100));
                    i -= 100;
                }

                this.netServerHandler.sendPacket(new Packet200Statistic(statistic.e, i));
            }
        }
    }

    public void m() {
        if (this.vehicle != null) {
            this.mount(this.vehicle);
        }

        if (this.passenger != null) {
            this.passenger.mount(this);
        }

        if (this.sleeping) {
            this.a(true, false, false);
        }
    }

    public void n() {
        this.ch = -99999999;
        this.lastSentExp = -1; // CraftBukkit - Added to reset
    }

    public void c(String s) {
        LocaleLanguage localelanguage = LocaleLanguage.a();
        String s1 = localelanguage.b(s);

        this.netServerHandler.sendPacket(new Packet3Chat(s1));
    }

    protected void o() {
        this.netServerHandler.sendPacket(new Packet38EntityStatus(this.id, (byte) 9));
        super.o();
    }

    public void a(ItemStack itemstack, int i) {
        super.a(itemstack, i);
        if (itemstack != null && itemstack.getItem() != null && itemstack.getItem().b(itemstack) == EnumAnimation.b) {
            this.q().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(this, 5));
        }
    }

    protected void a(MobEffect mobeffect) {
        super.a(mobeffect);
        this.netServerHandler.sendPacket(new Packet41MobEffect(this.id, mobeffect));
    }

    protected void b(MobEffect mobeffect) {
        super.b(mobeffect);
        this.netServerHandler.sendPacket(new Packet41MobEffect(this.id, mobeffect));
    }

    protected void c(MobEffect mobeffect) {
        super.c(mobeffect);
        this.netServerHandler.sendPacket(new Packet42RemoveMobEffect(this.id, mobeffect));
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.netServerHandler.a(d0, d1, d2, this.yaw, this.pitch);
    }

    public void b(Entity entity) {
        this.q().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(entity, 6));
    }

    public void c(Entity entity) {
        this.q().getTracker().sendPacketToEntity(this, new Packet18ArmAnimation(entity, 7));
    }

    public void updateAbilities() {
        if (this.netServerHandler != null) {
            this.netServerHandler.sendPacket(new Packet202Abilities(this.abilities));
        }
    }

    public WorldServer q() {
        return (WorldServer) this.world;
    }

    public void a(EnumGamemode enumgamemode) {
        this.itemInWorldManager.setGameMode(enumgamemode);
        this.netServerHandler.sendPacket(new Packet70Bed(3, enumgamemode.a()));
    }

    public void sendMessage(String s) {
        this.netServerHandler.sendPacket(new Packet3Chat(s));
    }

    public boolean b(String s) {
        return "seed".equals(s) && !this.server.S() ? true : this.server.getServerConfigurationManager().isOp(this.name);
    }

    public String r() {
        String s = this.netServerHandler.networkManager.getSocketAddress().toString();

        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void a(Packet204LocaleAndViewDistance packet204localeandviewdistance) {
        if (this.locale.b().containsKey(packet204localeandviewdistance.d())) {
            this.locale.a(packet204localeandviewdistance.d());
        }

        int i = 256 >> packet204localeandviewdistance.f();

        if (i > 3 && i < 15) {
            this.cm = i;
        }

        this.cn = packet204localeandviewdistance.g();
        this.co = packet204localeandviewdistance.h();
        if (this.server.H() && this.server.G().equals(this.name)) {
            this.server.c(packet204localeandviewdistance.i());
        }
    }

    public LocaleLanguage getLocale() {
        return this.locale;
    }

    public int getChatFlags() {
        return this.cn;
    }

    public void a(String s, int i) {
        String s1 = s + "\0" + i; // CraftBukkit - fix decompile error

        this.netServerHandler.sendPacket(new Packet250CustomPayload("MC|TPack", s1.getBytes()));
    }

    // CraftBukkit start
    public long timeOffset = 0;
    public boolean relativeTime = true;

    public long getPlayerTime() {
        if (this.relativeTime) {
            // Adds timeOffset to the current server time.
            return this.world.getTime() + this.timeOffset;
        } else {
            // Adds timeOffset to the beginning of this day.
            return this.world.getTime() - (this.world.getTime() % 24000) + this.timeOffset;
        }
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.name + " at " + this.locX + "," + this.locY + "," + this.locZ + ")";
    }

    public void reset() {
        float exp = 0;
        if (this.keepLevel) {
            exp = this.exp;
            this.newTotalExp = this.expTotal;
            this.newLevel = this.expLevel;
        }

        this.health = 20;
        this.fireTicks = 0;
        this.fallDistance = 0;
        this.foodData = new FoodMetaData();
        this.expLevel = this.newLevel;
        this.expTotal = this.newTotalExp;
        this.exp = 0;
        this.deathTicks = 0;
        effects.clear();
        this.activeContainer = this.defaultContainer;
        this.lastSentExp = -1;
        if (this.keepLevel) {
            this.exp = exp;
        } else {
            this.giveExp(this.newExp);
        }
        this.keepLevel = false;
    }

    @Override
    public CraftPlayer getBukkitEntity() {
        return (CraftPlayer) super.getBukkitEntity();
    }
    // CraftBukkit end
}
