package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import java.util.ArrayDeque; // Paper
import java.util.Collection;
import java.util.Deque; // Paper
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerChangedMainHandEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.MainHand;
// CraftBukkit end

public class EntityPlayer extends EntityHuman implements ICrafting {

    private static final Logger cc = LogManager.getLogger();
    public String locale = null; // CraftBukkit - lowercase // Paper - default to null
    public long lastSave = MinecraftServer.currentTick; // Paper
    public PlayerConnection playerConnection;
    public final MinecraftServer server;
    public final PlayerInteractManager playerInteractManager;
    public double d;
    public double e;
    public final Deque<Integer> removeQueue = new ArrayDeque<>(); // Paper
    private final AdvancementDataPlayer cf;
    private final ServerStatisticManager cg;
    private float ch = Float.MIN_VALUE;
    private int ci = Integer.MIN_VALUE;
    private int cj = Integer.MIN_VALUE;
    private int ck = Integer.MIN_VALUE;
    private int cl = Integer.MIN_VALUE;
    private int cm = Integer.MIN_VALUE;
    private float lastHealthSent = -1.0E8F;
    private int lastFoodSent = -99999999;
    private boolean cp = true;
    public int lastSentExp = -99999999;
    public int invulnerableTicks = 60;
    private EntityHuman.EnumChatVisibility cs;
    private boolean ct = true;
    private long cu = SystemUtils.getMonotonicMillis();
    private Entity spectatedEntity; private void setSpectatorTargetField(Entity e) { this.spectatedEntity = e; } // Paper - OBFHELPER
    public boolean worldChangeInvuln;
    private boolean cx; private void setHasSeenCredits(boolean has) { this.cx = has; } // Paper - OBFHELPER
    private final RecipeBookServer recipeBook;
    private Vec3D cz;
    private int cA;
    private boolean cB;
    private Vec3D cC;
    private int containerCounter;
    public boolean f;
    public int ping;
    public boolean viewingCredits;
    private int containerUpdateDelay; // Paper
    public long loginTime; // Paper
    // Paper start - cancellable death event
    public boolean queueHealthUpdatePacket = false;
    public net.minecraft.server.PacketPlayOutUpdateHealth queuedHealthUpdatePacket;
    // Paper end

    // CraftBukkit start
    public String displayName;
    public IChatBaseComponent listName;
    public org.bukkit.Location compassTarget;
    public int newExp = 0;
    public int newLevel = 0;
    public int newTotalExp = 0;
    public boolean keepLevel = false;
    public double maxHealthCache;
    public boolean joining = true;
    public boolean sentListPacket = false;
    public Integer clientViewDistance;
    // CraftBukkit end

    public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super((World) worldserver, gameprofile);
        playerinteractmanager.player = this;
        this.playerInteractManager = playerinteractmanager;
        this.server = minecraftserver;
        this.recipeBook = new RecipeBookServer(minecraftserver.getCraftingManager());
        this.cg = minecraftserver.getPlayerList().getStatisticManager(this);
        this.cf = minecraftserver.getPlayerList().h(this);
        this.Q = 1.0F;
        this.a(worldserver);

        // CraftBukkit start
        this.displayName = this.getName();
        this.canPickUpLoot = true;
        this.maxHealthCache = this.getMaxHealth();
    }

    // Yes, this doesn't match Vanilla, but it's the best we can do for now.
    // If this is an issue, PRs are welcome
    public final BlockPosition getSpawnPoint(WorldServer worldserver) {
        BlockPosition blockposition = worldserver.getSpawn();

        if (worldserver.worldProvider.g() && worldserver.getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
            int i = Math.max(0, this.server.a(worldserver));
            int j = MathHelper.floor(worldserver.getWorldBorder().b((double) blockposition.getX(), (double) blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            int k = (i * 2 + 1) * (i * 2 + 1);
            int l = this.r(k);
            int i1 = (new Random()).nextInt(k);

            for (int j1 = 0; j1 < k; ++j1) {
                int k1 = (i1 + l * j1) % k;
                int l1 = k1 % (i * 2 + 1);
                int i2 = k1 / (i * 2 + 1);
                BlockPosition blockposition1 = worldserver.o().a(blockposition.getX() + l1 - i, blockposition.getZ() + i2 - i, false);

                if (blockposition1 != null) {
                    return blockposition1;
                }
            }
        }

        return blockposition;
    }
    // CraftBukkit end

    private void a(WorldServer worldserver) {
        BlockPosition blockposition = worldserver.getSpawn();

        if (worldserver.worldProvider.g() && worldserver.getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
            int i = Math.max(0, this.server.a(worldserver));
            int j = MathHelper.floor(worldserver.getWorldBorder().b((double) blockposition.getX(), (double) blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            int k = (i * 2 + 1) * (i * 2 + 1);
            int l = this.r(k);
            int i1 = (new Random()).nextInt(k);

            for (int j1 = 0; j1 < k; ++j1) {
                int k1 = (i1 + l * j1) % k;
                int l1 = k1 % (i * 2 + 1);
                int i2 = k1 / (i * 2 + 1);
                BlockPosition blockposition1 = worldserver.o().a(blockposition.getX() + l1 - i, blockposition.getZ() + i2 - i, false);

                if (blockposition1 != null) {
                    this.setPositionRotation(blockposition1, 0.0F, 0.0F);
                    if (worldserver.getCubes(this, this.getBoundingBox())) {
                        break;
                    }
                }
            }
        } else {
            this.setPositionRotation(blockposition, 0.0F, 0.0F);

            while (!worldserver.getCubes(this, this.getBoundingBox()) && this.locY < 255.0D) {
                this.setPosition(this.locX, this.locY + 1.0D, this.locZ);
            }
        }

    }

    private int r(int i) {
        return i <= 16 ? i - 1 : 17;
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (this.locY > 300) this.locY = 257; // Paper - bring down to a saner Y level if out of world
        if (nbttagcompound.hasKeyOfType("playerGameType", 99)) {
            if (this.bK().getForceGamemode()) {
                this.playerInteractManager.setGameMode(this.bK().getGamemode());
            } else {
                this.playerInteractManager.setGameMode(EnumGamemode.getById(nbttagcompound.getInt("playerGameType")));
            }
        }

        if (nbttagcompound.hasKeyOfType("enteredNetherPosition", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("enteredNetherPosition");

            this.cC = new Vec3D(nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("y"), nbttagcompound1.getDouble("z"));
        }

        this.cx = nbttagcompound.getBoolean("seenCredits");
        if (nbttagcompound.hasKeyOfType("recipeBook", 10)) {
            this.recipeBook.a(nbttagcompound.getCompound("recipeBook"));
        }
        this.getBukkitEntity().readExtraData(nbttagcompound); // CraftBukkit

    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("playerGameType", this.playerInteractManager.getGameMode().getId());
        nbttagcompound.setBoolean("seenCredits", this.cx);
        if (this.cC != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.setDouble("x", this.cC.x);
            nbttagcompound1.setDouble("y", this.cC.y);
            nbttagcompound1.setDouble("z", this.cC.z);
            nbttagcompound.set("enteredNetherPosition", nbttagcompound1);
        }

        Entity entity = this.getRootVehicle();
        Entity entity1 = this.getVehicle();

        // CraftBukkit start - handle non-persistent vehicles
        boolean persistVehicle = true;
        if (entity1 != null) {
            Entity vehicle;
            for (vehicle = entity1; vehicle != null; vehicle = vehicle.getVehicle()) {
                if (!vehicle.persist) {
                    persistVehicle = false;
                    break;
                }
            }
        }

        if (persistVehicle && entity1 != null && entity != this && entity.bR()) {
            // CraftBukkit end
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();

            entity.d(nbttagcompound3);
            nbttagcompound2.a("Attach", entity1.getUniqueID());
            nbttagcompound2.set("Entity", nbttagcompound3);
            nbttagcompound.set("RootVehicle", nbttagcompound2);
        }

        nbttagcompound.set("recipeBook", this.recipeBook.e());
        this.getBukkitEntity().setExtraData(nbttagcompound); // CraftBukkit
    }

    // CraftBukkit start - World fallback code, either respawn location or global spawn
    public void spawnIn(World world) {
        super.spawnIn(world);
        if (world == null) {
            this.dead = false;
            BlockPosition position = null;
            if (this.spawnWorld != null && !this.spawnWorld.equals("")) {
                CraftWorld cworld = (CraftWorld) Bukkit.getServer().getWorld(this.spawnWorld);
                if (cworld != null && this.getBed() != null) {
                    world = cworld.getHandle();
                    position = EntityHuman.getBed(cworld.getHandle(), this.getBed(), false);
                }
            }
            if (world == null || position == null) {
                world = ((CraftWorld) Bukkit.getServer().getWorlds().get(0)).getHandle();
                position = world.getSpawn();
            }
            this.world = world;
            this.setPosition(position.getX() + 0.5, position.getY(), position.getZ() + 0.5);
        }
        this.dimension = ((WorldServer) this.world).dimension;
        this.playerInteractManager.a((WorldServer) world);
    }
    // CraftBukkit end

    public void a(int i) {
        float f = (float) this.getExpToLevel();
        float f1 = (f - 1.0F) / f;

        this.exp = MathHelper.a((float) i / f, 0.0F, f1);
        this.lastSentExp = -1;
    }

    public void b(int i) {
        this.expLevel = i;
        this.lastSentExp = -1;
    }

    public void levelDown(int i) {
        super.levelDown(i);
        this.lastSentExp = -1;
    }

    public void enchantDone(ItemStack itemstack, int i) {
        super.enchantDone(itemstack, i);
        this.lastSentExp = -1;
    }

    public void syncInventory() {
        this.activeContainer.addSlotListener(this);
    }

    public void enterCombat() {
        super.enterCombat();
        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTER_COMBAT));
    }

    public void exitCombat() {
        super.exitCombat();
        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT));
    }

    protected void a(IBlockData iblockdata) {
        CriterionTriggers.d.a(this, iblockdata);
    }

    protected ItemCooldown g() {
        return new ItemCooldownPlayer(this);
    }

    public void tick() {
        // CraftBukkit start
        if (this.joining) {
            this.joining = false;
        }
        // CraftBukkit end
        this.playerInteractManager.a();
        --this.invulnerableTicks;
        if (this.noDamageTicks > 0) {
            --this.noDamageTicks;
        }

        // Paper start - Configurable container update tick rate
        if (--containerUpdateDelay <= 0) {
            this.activeContainer.b();
            containerUpdateDelay = world.paperConfig.containerUpdateTickRate;
        }
        // Paper end
        if (!this.world.isClientSide && !this.activeContainer.canUse(this)) {
            this.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.CANT_USE); // Paper
            this.activeContainer = this.defaultContainer;
        }

        while (!this.removeQueue.isEmpty()) {
            int i = Math.min(this.removeQueue.size(), Integer.MAX_VALUE);
            int[] aint = new int[i];
            //Iterator<Integer> iterator = this.removeQueue.iterator(); // Paper
            int j = 0;

            // Paper start
            /* while (iterator.hasNext() && j < i) {
                aint[j++] = (Integer) iterator.next();
                iterator.remove();
            } */

            Integer integer;
            while (j < i && (integer = this.removeQueue.poll()) != null) {
                aint[j++] = integer.intValue();
            }
            // Paper end

            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(aint));
        }

        Entity entity = this.getSpecatorTarget();

        if (entity != this) {
            if (entity.isAlive()) {
                this.setLocation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
                this.server.getPlayerList().updateChunks(this);
                if (this.isSneaking()) {
                    this.setSpectatorTarget(this);
                }
            } else {
                this.setSpectatorTarget(this);
            }
        }

        CriterionTriggers.w.a(this);
        if (this.cz != null) {
            CriterionTriggers.u.a(this, this.cz, this.ticksLived - this.cA);
        }

        this.cf.b(this);
    }

    public void playerTick() {
        try {
            super.tick();

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (itemstack.getItem().W_()) {
                    Packet<?> packet = ((ItemWorldMapBase) itemstack.getItem()).a(itemstack, this.world, (EntityHuman) this);

                    if (packet != null) {
                        this.playerConnection.sendPacket(packet);
                    }
                }
            }

            if (this.getHealth() != this.lastHealthSent || this.lastFoodSent != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.cp) {
                this.playerConnection.sendPacket(new PacketPlayOutUpdateHealth(this.getBukkitEntity().getScaledHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel())); // CraftBukkit
                this.lastHealthSent = this.getHealth();
                this.lastFoodSent = this.foodData.getFoodLevel();
                this.cp = this.foodData.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionHearts() != this.ch) {
                this.ch = this.getHealth() + this.getAbsorptionHearts();
                this.a(IScoreboardCriteria.HEALTH, MathHelper.f(this.ch));
            }

            if (this.foodData.getFoodLevel() != this.ci) {
                this.ci = this.foodData.getFoodLevel();
                this.a(IScoreboardCriteria.FOOD, MathHelper.f((float) this.ci));
            }

            if (this.getAirTicks() != this.cj) {
                this.cj = this.getAirTicks();
                this.a(IScoreboardCriteria.AIR, MathHelper.f((float) this.cj));
            }

            if (this.getArmorStrength() != this.ck) {
                this.ck = this.getArmorStrength();
                this.a(IScoreboardCriteria.ARMOR, MathHelper.f((float) this.ck));
            }

            if (this.expTotal != this.cm) {
                this.cm = this.expTotal;
                this.a(IScoreboardCriteria.XP, MathHelper.f((float) this.cm));
            }

            // CraftBukkit start - Force max health updates
            if (this.maxHealthCache != this.getMaxHealth()) {
                this.getBukkitEntity().updateScaledHealth();
            }
            // CraftBukkit end

            if (this.expLevel != this.cl) {
                this.cl = this.expLevel;
                this.a(IScoreboardCriteria.LEVEL, MathHelper.f((float) this.cl));
            }

            if (this.expTotal != this.lastSentExp) {
                this.lastSentExp = this.expTotal;
                this.playerConnection.sendPacket(new PacketPlayOutExperience(this.exp, this.expTotal, this.expLevel));
            }

            if (this.ticksLived % 20 == 0) {
                CriterionTriggers.p.a(this);
            }

            // CraftBukkit start - initialize oldLevel and fire PlayerLevelChangeEvent
            if (this.oldLevel == -1) {
                this.oldLevel = this.expLevel;
            }

            if (this.oldLevel != this.expLevel) {
                CraftEventFactory.callPlayerLevelChangeEvent(this.world.getServer().getPlayer((EntityPlayer) this), this.oldLevel, this.expLevel);
                this.oldLevel = this.expLevel;
            }
            // CraftBukkit end
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Ticking player");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Player being ticked");

            this.appendEntityCrashDetails(crashreportsystemdetails);
            throw new ReportedException(crashreport);
        }
    }

    private void a(IScoreboardCriteria iscoreboardcriteria, int i) {
        // CraftBukkit - Use our scores instead
        this.world.getServer().getScoreboardManager().getScoreboardScores(iscoreboardcriteria, this.getName(), (scoreboardscore) -> {
            scoreboardscore.setScore(i);
        });
    }

    public void die(DamageSource damagesource) {
        boolean flag = this.world.getGameRules().getBoolean("showDeathMessages");
        // CraftBukkit start - fire PlayerDeathEvent
        if (this.dead) {
            return;
        }
        java.util.List<org.bukkit.inventory.ItemStack> loot = new java.util.ArrayList<org.bukkit.inventory.ItemStack>(this.inventory.getSize());
        boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory") || this.isSpectator();

        if (!keepInventory) {
            for (ItemStack item : this.inventory.getContents()) {
                if (!item.isEmpty() && !EnchantmentManager.shouldNotDrop(item)) {
                    loot.add(CraftItemStack.asCraftMirror(item));
                }
            }
        }

        IChatBaseComponent defaultMessage = this.getCombatTracker().getDeathMessage();

        String deathmessage = defaultMessage.getString();
        org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, deathmessage, keepInventory);
        // Paper start - cancellable death event
        if (event.isCancelled()) {
            // make compatible with plugins that might have already set the health in an event listener
            if (this.getHealth() <= 0) {
                this.setHealth((float) event.getReviveHealth());
            }
            return;
        }
        // Paper end

        // SPIGOT-943 - only call if they have an inventory open
        if (this.activeContainer != this.defaultContainer) {
            this.closeInventory();
        }

        String deathMessage = event.getDeathMessage();

        if (deathMessage != null && deathMessage.length() > 0 && flag) { // TODO: allow plugins to override?
            IChatBaseComponent ichatbasecomponent;
            if (deathMessage.equals(deathmessage)) {
                ichatbasecomponent = this.getCombatTracker().getDeathMessage();
            } else {
                ichatbasecomponent = org.bukkit.craftbukkit.util.CraftChatMessage.fromStringOrNull(deathMessage);
            }

            this.playerConnection.a((Packet) (new PacketPlayOutCombatEvent(this.getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED, ichatbasecomponent)), (future) -> {
                if (!future.isSuccess()) {
                    boolean flag1 = true;
                    String s = ichatbasecomponent.a(256);
                    ChatMessage chatmessage = new ChatMessage("death.attack.message_too_long", new Object[] { (new ChatComponentText(s)).a(EnumChatFormat.YELLOW)});
                    IChatBaseComponent ichatbasecomponent1 = (new ChatMessage("death.attack.even_more_magic", new Object[] { this.getScoreboardDisplayName()})).a((chatmodifier) -> {
                        chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, chatmessage));
                    });

                    this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED, ichatbasecomponent1));
                }

            });
            ScoreboardTeamBase scoreboardteambase = this.getScoreboardTeam();

            if (scoreboardteambase != null && scoreboardteambase.getDeathMessageVisibility() != ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS) {
                if (scoreboardteambase.getDeathMessageVisibility() == ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS) {
                    this.server.getPlayerList().a((EntityHuman) this, ichatbasecomponent);
                } else if (scoreboardteambase.getDeathMessageVisibility() == ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM) {
                    this.server.getPlayerList().b((EntityHuman) this, ichatbasecomponent);
                }
            } else {
                this.server.getPlayerList().sendMessage(ichatbasecomponent);
            }
        } else {
            this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED));
        }

        this.releaseShoulderEntities();
        // we clean the player's inventory after the EntityDeathEvent is called so plugins can get the exact state of the inventory.
        if (!event.getKeepInventory()) {
            this.inventory.clear();
        }
        this.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.DEATH); // Paper
        this.setSpectatorTarget(this); // Remove spectated target
        // CraftBukkit end

        // CraftBukkit - Get our scores instead
        this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.DEATH_COUNT, this.getName(), ScoreboardScore::incrementScore);
        EntityLiving entityliving = this.cv();

        if (entityliving != null) {
            this.b(StatisticList.ENTITY_KILLED_BY.b(entityliving.P()));
            entityliving.a(this, this.be, damagesource);
        }

        this.a(StatisticList.DEATHS);
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_DEATH));
        this.a(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST));
        this.extinguish();
        this.setFlag(0, false);
        this.getCombatTracker().g();
    }

    public void a(Entity entity, int i, DamageSource damagesource) {
        if (entity != this) {
            super.a(entity, i, damagesource);
            this.addScore(i);
            String s = this.getName();
            String s1 = entity.getName();

            // CraftBukkit - Get our scores instead
            this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.TOTAL_KILL_COUNT, s, ScoreboardScore::incrementScore);
            if (entity instanceof EntityHuman) {
                this.a(StatisticList.PLAYER_KILLS);
                // CraftBukkit - Get our scores instead
                this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.PLAYER_KILL_COUNT, s, ScoreboardScore::incrementScore);
            } else {
                this.a(StatisticList.MOB_KILLS);
            }

            this.a(s, s1, IScoreboardCriteria.m);
            this.a(s1, s, IScoreboardCriteria.n);
            CriterionTriggers.b.a(this, entity, damagesource);
        }
    }

    private void a(String s, String s1, IScoreboardCriteria[] aiscoreboardcriteria) {
        ScoreboardTeam scoreboardteam = this.getScoreboard().getPlayerTeam(s1);

        if (scoreboardteam != null) {
            int i = scoreboardteam.getColor().b();

            if (i >= 0 && i < aiscoreboardcriteria.length) {
                // CraftBukkit - Get our scores instead
                this.world.getServer().getScoreboardManager().getScoreboardScores(aiscoreboardcriteria[i], s, ScoreboardScore::incrementScore);
            }
        }

    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            boolean flag = this.server.Q() && this.canPvP() && "fall".equals(damagesource.translationIndex);

            if (!flag && this.invulnerableTicks > 0 && damagesource != DamageSource.OUT_OF_WORLD) {
                return false;
            } else {
                if (damagesource instanceof EntityDamageSource) {
                    Entity entity = damagesource.getEntity();

                    if (entity instanceof EntityHuman && !this.a((EntityHuman) entity)) {
                        return false;
                    }

                    if (entity instanceof EntityArrow) {
                        EntityArrow entityarrow = (EntityArrow) entity;
                        Entity entity1 = entityarrow.getShooter();

                        if (entity1 instanceof EntityHuman && !this.a((EntityHuman) entity1)) {
                            return false;
                        }
                    }
                }
                // Paper start - cancellable death events
                //return super.damageEntity(damagesource, f);
                this.queueHealthUpdatePacket = true;
                boolean damaged = super.damageEntity(damagesource, f);
                this.queueHealthUpdatePacket = false;
                if (this.queuedHealthUpdatePacket != null) {
                    this.playerConnection.sendPacket(this.queuedHealthUpdatePacket);
                    this.queuedHealthUpdatePacket = null;
                }
                return damaged;
                // Paper end
            }
        }
    }

    public boolean a(EntityHuman entityhuman) {
        return !this.canPvP() ? false : super.a(entityhuman);
    }

    private boolean canPvP() {
        // CraftBukkit - this.server.getPvP() -> this.world.pvpMode
        return this.world.pvpMode;
    }

    @Nullable
    public Entity a(DimensionManager dimensionmanager) {
        if (this.isSleeping()) return this; // CraftBukkit - SPIGOT-3154
        // this.worldChangeInvuln = true; // CraftBukkit - Moved down and into PlayerList#changeDimension
        if (this.dimension == DimensionManager.OVERWORLD && dimensionmanager == DimensionManager.NETHER) {
            this.cC = new Vec3D(this.locX, this.locY, this.locZ);
        } else if (this.dimension != DimensionManager.NETHER && dimensionmanager != DimensionManager.OVERWORLD) {
            this.cC = null;
        }

        if (this.dimension == DimensionManager.THE_END && dimensionmanager == DimensionManager.THE_END) {
            this.worldChangeInvuln = true; // CraftBukkit - Moved down from above
            this.world.kill(this);
            if (!this.viewingCredits) {
                this.viewingCredits = true;
                if (world.paperConfig.disableEndCredits) this.setHasSeenCredits(true); // Paper - Toggle to always disable end credits
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, this.cx ? 0.0F : 1.0F));
                this.cx = true;
            }

            return this;
        } else {
            if (this.dimension == DimensionManager.OVERWORLD && dimensionmanager == DimensionManager.THE_END) {
                dimensionmanager = DimensionManager.THE_END;
            }

            // CraftBukkit start
            TeleportCause cause = (this.dimension == DimensionManager.THE_END || dimensionmanager == DimensionManager.THE_END) ? TeleportCause.END_PORTAL : TeleportCause.NETHER_PORTAL;
            this.server.getPlayerList().changeDimension(this, dimensionmanager, cause); // PAIL: check all this
            // CraftBukkit end
            this.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1032, BlockPosition.ZERO, 0, false));
            this.lastSentExp = -1;
            this.lastHealthSent = -1.0F;
            this.lastFoodSent = -1;
            return this;
        }
    }

    public boolean a(EntityPlayer entityplayer) {
        return entityplayer.isSpectator() ? this.getSpecatorTarget() == this : (this.isSpectator() ? false : super.a(entityplayer));
    }

    private void a(TileEntity tileentity) {
        if (tileentity != null) {
            PacketPlayOutTileEntityData packetplayouttileentitydata = tileentity.getUpdatePacket();

            if (packetplayouttileentitydata != null) {
                this.playerConnection.sendPacket(packetplayouttileentitydata);
            }
        }

    }

    public void receive(Entity entity, int i) {
        super.receive(entity, i);
        this.activeContainer.b();
    }

    public EntityHuman.EnumBedResult a(BlockPosition blockposition) {
        EntityHuman.EnumBedResult entityhuman_enumbedresult = super.a(blockposition);

        if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.OK) {
            this.a(StatisticList.SLEEP_IN_BED);
            Packet<?> packet = new PacketPlayOutBed(this, blockposition);

            this.getWorldServer().getTracker().a((Entity) this, (Packet) packet);
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.playerConnection.sendPacket(packet);
            CriterionTriggers.q.a(this);
        }

        return entityhuman_enumbedresult;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (!this.sleeping) return; // CraftBukkit - Can't leave bed if not in one!
        if (this.isSleeping()) {
            this.getWorldServer().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 2));
        }

        super.a(flag, flag1, flag2);
        if (this.playerConnection != null) {
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }

    }

    public boolean a(Entity entity, boolean flag) {
        Entity entity1 = this.getVehicle();

        if (!super.a(entity, flag)) {
            return false;
        } else {
            Entity entity2 = this.getVehicle();

            if (entity2 != entity1 && this.playerConnection != null) {
                this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            }

            return true;
        }
    }

    // Paper start
    public void stopRiding() { stopRiding(false); }
    public void stopRiding(boolean suppressCancellation) {
    // paper end
        Entity entity = this.getVehicle();

        super.stopRiding(suppressCancellation); // Paper
        Entity entity1 = this.getVehicle();

        if (entity1 != entity && this.playerConnection != null) {
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }
        // Paper start - "Fixes" an issue in which the vehicle player would not be notified that the passenger dismounted
        if (entity instanceof EntityPlayer) {
            WorldServer worldServer = (WorldServer) entity.getWorld();
            worldServer.tracker.untrackEntity(this);
            worldServer.tracker.track(this);
        }
        // Paper end

    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return super.isInvulnerable(damagesource) || this.H();
    }

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    protected void b(BlockPosition blockposition) {
        if (!this.isSpectator()) {
            super.b(blockposition);
        }

    }

    public void a(double d0, boolean flag) {
        int i = MathHelper.floor(this.locX);
        int j = MathHelper.floor(this.locY - 0.20000000298023224D);
        int k = MathHelper.floor(this.locZ);
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = this.world.getType(blockposition);

        if (iblockdata.isAir()) {
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = this.world.getType(blockposition1);
            Block block = iblockdata1.getBlock();

            if (block instanceof BlockFence || block instanceof BlockCobbleWall || block instanceof BlockFenceGate) {
                blockposition = blockposition1;
                iblockdata = iblockdata1;
            }
        }

        super.a(d0, flag, iblockdata, blockposition);
    }

    public void openSign(TileEntitySign tileentitysign) {
        tileentitysign.a((EntityHuman) this);
        this.playerConnection.sendPacket(new PacketPlayOutOpenSignEditor(tileentitysign.getPosition()));
    }

    public int nextContainerCounter() { // CraftBukkit - void -> int
        this.containerCounter = this.containerCounter % 100 + 1;
        return containerCounter; // CraftBukkit
    }

    public void openTileEntity(ITileEntityContainer itileentitycontainer) {
        // CraftBukkit start - Inventory open hook
        if (false && itileentitycontainer instanceof ILootable && ((ILootable) itileentitycontainer).getLootTable() != null && this.isSpectator()) {
            this.a((new ChatMessage("container.spectatorCantOpen", new Object[0])).a(EnumChatFormat.RED), true);
        } else {
            boolean cancelled = itileentitycontainer instanceof ILootable && ((ILootable) itileentitycontainer).getLootTable()!= null && this.isSpectator();
            Container container = CraftEventFactory.callInventoryOpenEvent(this, itileentitycontainer.createContainer(this.inventory, this), cancelled);
            if (container == null) {
                return;
            }
            this.nextContainerCounter();
            this.activeContainer = container;
            this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, itileentitycontainer.getContainerName(), itileentitycontainer.getScoreboardDisplayName()));
            // CraftBukkit end
            this.activeContainer.windowId = this.containerCounter;
            this.activeContainer.addSlotListener(this);
        }
    }

    public void openContainer(IInventory iinventory) {
        // CraftBukkit start - Inventory open hook
        // Copied from below
        boolean cancelled = false;
        if (iinventory instanceof ITileInventory) {
            ITileInventory itileinventory = (ITileInventory) iinventory;
            cancelled = itileinventory.isLocked() && !this.a(itileinventory.getLock()) && !this.isSpectator();
        }

        Container container;
        if (iinventory instanceof ITileEntityContainer) {
            if (iinventory instanceof TileEntity) {
                Preconditions.checkArgument(((TileEntity) iinventory).getWorld() != null, "Container must have world to be opened");
            }
            container = ((ITileEntityContainer) iinventory).createContainer(this.inventory, this);
        } else {
            container = new ContainerChest(this.inventory, iinventory, this);
        }
        container = CraftEventFactory.callInventoryOpenEvent(this, container, cancelled);
        if (container == null && !cancelled) { // Let pre-cancelled events fall through
            iinventory.closeContainer(this);
            return;
        }
        // CraftBukkit end

        if (iinventory instanceof ILootable && ((ILootable) iinventory).getLootTable() != null && this.isSpectator()) {
            this.a((new ChatMessage("container.spectatorCantOpen", new Object[0])).a(EnumChatFormat.RED), true);
        } else {
            if (this.activeContainer != this.defaultContainer) {
                this.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.OPEN_NEW); // Paper
            }

            if (iinventory instanceof ITileInventory) {
                ITileInventory itileinventory = (ITileInventory) iinventory;

                if (itileinventory.isLocked() && !this.a(itileinventory.getLock()) && !this.isSpectator()) {
                    this.playerConnection.sendPacket(new PacketPlayOutChat(new ChatMessage("container.isLocked", new Object[] { iinventory.getScoreboardDisplayName()}), ChatMessageType.GAME_INFO));
                    this.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.BLOCK_CHEST_LOCKED, SoundCategory.BLOCKS, this.locX, this.locY, this.locZ, 1.0F, 1.0F));
                    iinventory.closeContainer(this); // CraftBukkit
                    return;
                }
            }

            this.nextContainerCounter();
            // CraftBukkit start
            if (iinventory instanceof ITileEntityContainer) {
                this.activeContainer = container;
                this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, ((ITileEntityContainer) iinventory).getContainerName(), iinventory.getScoreboardDisplayName(), iinventory.getSize()));
            } else {
                this.activeContainer = container;
                this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:container", iinventory.getScoreboardDisplayName(), iinventory.getSize()));
            }
            // CraftBukkit end

            this.activeContainer.windowId = this.containerCounter;
            this.activeContainer.addSlotListener(this);
        }
    }

    public void openTrade(IMerchant imerchant) {
        // CraftBukkit start - Inventory open hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerMerchant(this.inventory, imerchant, this.world));
        if (container == null) {
            return;
        }
        // CraftBukkit end
        this.nextContainerCounter();
        this.activeContainer = container; // CraftBukkit
        // CraftBukkit start - moved down (SPIGOT-4619)
        // this.activeContainer.windowId = this.containerCounter;
        // this.activeContainer.addSlotListener(this);
        // CraftBukkit end

        InventoryMerchant inventorymerchant = ((ContainerMerchant) this.activeContainer).d();
        IChatBaseComponent ichatbasecomponent = imerchant.getScoreboardDisplayName();

        this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
        // CraftBukkit start
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
        // CraftBukkit end
        MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);

        if (merchantrecipelist != null) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

            packetdataserializer.writeInt(this.containerCounter);
            merchantrecipelist.a(packetdataserializer);
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload(PacketPlayOutCustomPayload.a, packetdataserializer));
        }

    }

    public void openHorseInventory(EntityHorseAbstract entityhorseabstract, IInventory iinventory) {
        // CraftBukkit start - Inventory open hook
        Container container = CraftEventFactory.callInventoryOpenEvent(this, new ContainerHorse(this.inventory, iinventory, entityhorseabstract, this));
        if (container == null) {
            iinventory.closeContainer(this);
            return;
        }
        // CraftBukkit end
        if (this.activeContainer != this.defaultContainer) {
            this.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.OPEN_NEW); // Paper
        }

        this.nextContainerCounter();
        this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "EntityHorse", iinventory.getScoreboardDisplayName(), iinventory.getSize(), entityhorseabstract.getId()));
        this.activeContainer = container; // CraftBukkit
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
    }

    public void a(ItemStack itemstack, EnumHand enumhand) {
        Item item = itemstack.getItem();

        if (item == Items.WRITTEN_BOOK) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

            packetdataserializer.a((Enum) enumhand);
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload(PacketPlayOutCustomPayload.c, packetdataserializer));
        }

    }

    public void a(TileEntityCommand tileentitycommand) {
        tileentitycommand.c(true);
        this.a((TileEntity) tileentitycommand);
    }

    public void a(Container container, int i, ItemStack itemstack) {
        if (!(container.getSlot(i) instanceof SlotResult)) {
            if (container == this.defaultContainer) {
                CriterionTriggers.e.a(this, this.inventory);
            }

            if (!this.f) {
                this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, i, itemstack));
            }
        }
    }

    public void updateInventory(Container container) {
        this.a(container, container.a());
    }

    public void a(Container container, NonNullList<ItemStack> nonnulllist) {
        this.playerConnection.sendPacket(new PacketPlayOutWindowItems(container.windowId, nonnulllist));
        this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        // CraftBukkit start - Send a Set Slot to update the crafting result slot
        if (java.util.EnumSet.of(InventoryType.CRAFTING,InventoryType.WORKBENCH).contains(container.getBukkitView().getType())) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(container.windowId, 0, container.getSlot(0).getItem()));
        }
        // CraftBukkit end
    }

    public void setContainerData(Container container, int i, int j) {
        this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, j));
    }

    public void setContainerData(Container container, IInventory iinventory) {
        for (int i = 0; i < iinventory.h(); ++i) {
            this.playerConnection.sendPacket(new PacketPlayOutWindowData(container.windowId, i, iinventory.getProperty(i)));
        }

    }

    public void closeInventory() {
        // Paper start
        closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.UNKNOWN);
    }
    public void closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason reason) {
        CraftEventFactory.handleInventoryCloseEvent(this, reason); // CraftBukkit
        // Paper end
        this.playerConnection.sendPacket(new PacketPlayOutCloseWindow(this.activeContainer.windowId));
        this.m();
    }

    public void broadcastCarriedItem() {
        if (!this.f) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        }
    }

    public void m() {
        this.activeContainer.b((EntityHuman) this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(float f, float f1, boolean flag, boolean flag1) {
        if (this.isPassenger()) {
            if (f >= -1.0F && f <= 1.0F) {
                this.bh = f;
            }

            if (f1 >= -1.0F && f1 <= 1.0F) {
                this.bj = f1;
            }

            this.bg = flag;
            this.setSneaking(flag1);
        }

    }

    public void a(Statistic<?> statistic, int i) {
        this.cg.b(this, statistic, i);
        this.world.getServer().getScoreboardManager().getScoreboardScores(statistic, this.getName(), (scoreboardscore) -> { // CraftBukkit - Get our scores instead
            scoreboardscore.addScore(i);
        });
    }

    public void a(Statistic<?> statistic) {
        this.cg.setStatistic(this, statistic, 0);
        this.world.getServer().getScoreboardManager().getScoreboardScores(statistic, this.getName(), ScoreboardScore::c); // CraftBukkit - Get our scores instead
    }

    public int discoverRecipes(Collection<IRecipe> collection) {
        return this.recipeBook.a(collection, this);
    }

    public void a(MinecraftKey[] aminecraftkey) {
        List<IRecipe> list = Lists.newArrayList();
        MinecraftKey[] aminecraftkey1 = aminecraftkey;
        int i = aminecraftkey.length;

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = aminecraftkey1[j];
            IRecipe irecipe = this.server.getCraftingManager().a(minecraftkey);

            if (irecipe != null) {
                list.add(irecipe);
            }
        }

        this.discoverRecipes(list);
    }

    public int undiscoverRecipes(Collection<IRecipe> collection) {
        return this.recipeBook.b(collection, this);
    }

    public void giveExp(int i) {
        super.giveExp(i);
        this.lastSentExp = -1;
    }

    public void n() {
        this.cB = true;
        this.ejectPassengers();

        // Paper start - Workaround an issue where the vehicle doesn't track the passenger disconnection dismount.
        if (this.isPassenger() && this.getVehicle() instanceof EntityPlayer) {
            this.stopRiding();
        }
        // Paper end

        if (this.sleeping) {
            this.a(true, false, false);
        }

    }

    public boolean o() {
        return this.cB;
    }

    public void triggerHealthUpdate() {
        this.lastHealthSent = -1.0E8F;
        this.lastSentExp = -1; // CraftBukkit - Added to reset
    }

    // CraftBukkit start - Support multi-line messages
    public void sendMessage(IChatBaseComponent[] ichatbasecomponent) {
        for (IChatBaseComponent component : ichatbasecomponent) {
            this.sendMessage(component);
        }
    }
    // CraftBukkit end

    public void a(IChatBaseComponent ichatbasecomponent, boolean flag) {
        this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent, flag ? ChatMessageType.GAME_INFO : ChatMessageType.CHAT));
    }

    protected void q() {
        if (!this.activeItem.isEmpty() && this.isHandRaised()) {
            this.playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 9));
            super.q();
        }

    }

    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Vec3D vec3d) {
        super.a(argumentanchor_anchor, vec3d);
        this.playerConnection.sendPacket(new PacketPlayOutLookAt(argumentanchor_anchor, vec3d.x, vec3d.y, vec3d.z));
    }

    public void a(ArgumentAnchor.Anchor argumentanchor_anchor, Entity entity, ArgumentAnchor.Anchor argumentanchor_anchor1) {
        Vec3D vec3d = argumentanchor_anchor1.a(entity);

        super.a(argumentanchor_anchor, vec3d);
        this.playerConnection.sendPacket(new PacketPlayOutLookAt(argumentanchor_anchor, entity, argumentanchor_anchor1));
    }

    public void copyFrom(EntityPlayer entityplayer, boolean flag) {
        if (flag) {
            this.inventory.a(entityplayer.inventory);
            this.setHealth(entityplayer.getHealth());
            this.foodData = entityplayer.foodData;
            this.expLevel = entityplayer.expLevel;
            this.expTotal = entityplayer.expTotal;
            this.exp = entityplayer.exp;
            this.setScore(entityplayer.getScore());
            this.aq = entityplayer.aq;
            this.ar = entityplayer.ar;
            this.as = entityplayer.as;
        } else if (this.world.getGameRules().getBoolean("keepInventory") || entityplayer.isSpectator()) {
            this.inventory.a(entityplayer.inventory);
            this.expLevel = entityplayer.expLevel;
            this.expTotal = entityplayer.expTotal;
            this.exp = entityplayer.exp;
            this.setScore(entityplayer.getScore());
        }

        this.bZ = entityplayer.bZ;
        this.enderChest = entityplayer.enderChest;
        this.getDataWatcher().set(EntityPlayer.bx, entityplayer.getDataWatcher().get(EntityPlayer.bx));
        this.lastSentExp = -1;
        this.lastHealthSent = -1.0F;
        this.lastFoodSent = -1;
        // this.recipeBook.a((RecipeBook) entityplayer.recipeBook); // CraftBukkit
        // Paper start - Optimize remove queue - vanilla copies player objects, but CB doesn't. This method currently only
        // Applies to the same player, so we need to not duplicate our removal queue. The rest of this method does "resetting"
        // type logic so it does need to be called, maybe? This is silly.
        //this.removeQueue.addAll(entityplayer.removeQueue);
        if (this.removeQueue != entityplayer.removeQueue) {
            this.removeQueue.addAll(entityplayer.removeQueue);
        }
        // Paper end
        this.cx = entityplayer.cx;
        this.cC = entityplayer.cC;
        this.setShoulderEntityLeft(entityplayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(entityplayer.getShoulderEntityRight());
    }

    protected void a(MobEffect mobeffect) {
        super.a(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
        if (mobeffect.getMobEffect() == MobEffects.LEVITATION) {
            this.cA = this.ticksLived;
            this.cz = new Vec3D(this.locX, this.locY, this.locZ);
        }

        CriterionTriggers.A.a(this);
    }

    protected void a(MobEffect mobeffect, boolean flag) {
        super.a(mobeffect, flag);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
        CriterionTriggers.A.a(this);
    }

    protected void b(MobEffect mobeffect) {
        super.b(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutRemoveEntityEffect(this.getId(), mobeffect.getMobEffect()));
        if (mobeffect.getMobEffect() == MobEffects.LEVITATION) {
            this.cz = null;
        }

        CriterionTriggers.A.a(this);
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.playerConnection.a(d0, d1, d2, this.yaw, this.pitch);
    }

    public void a(Entity entity) {
        this.getWorldServer().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 4));
    }

    public void b(Entity entity) {
        this.getWorldServer().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 5));
    }

    public void updateAbilities() {
        if (this.playerConnection != null) {
            this.playerConnection.sendPacket(new PacketPlayOutAbilities(this.abilities));
            this.C();
        }
    }

    public WorldServer getWorldServer() {
        return (WorldServer) this.world;
    }

    public void a(EnumGamemode enumgamemode) {
        // CraftBukkit start
        if (enumgamemode == this.playerInteractManager.getGameMode()) {
            return;
        }

        PlayerGameModeChangeEvent event = new PlayerGameModeChangeEvent(getBukkitEntity(), GameMode.getByValue(enumgamemode.getId()));
        world.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        // CraftBukkit end

        this.playerInteractManager.setGameMode(enumgamemode);
        this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(3, (float) enumgamemode.getId()));
        if (enumgamemode == EnumGamemode.SPECTATOR) {
            this.releaseShoulderEntities();
            this.stopRiding();
        } else {
            this.setSpectatorTarget(this);
        }

        this.updateAbilities();
        this.cR();
    }

    public boolean isSpectator() {
        return this.playerInteractManager.getGameMode() == EnumGamemode.SPECTATOR;
    }

    public boolean u() {
        return this.playerInteractManager.getGameMode() == EnumGamemode.CREATIVE;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.a(ichatbasecomponent, ChatMessageType.SYSTEM);
    }

    public void a(IChatBaseComponent ichatbasecomponent, ChatMessageType chatmessagetype) {
        this.playerConnection.a((Packet) (new PacketPlayOutChat(ichatbasecomponent, chatmessagetype)), (future) -> {
            if (!future.isSuccess() && (chatmessagetype == ChatMessageType.GAME_INFO || chatmessagetype == ChatMessageType.SYSTEM)) {
                boolean flag = true;
                String s = ichatbasecomponent.a(256);
                IChatBaseComponent ichatbasecomponent1 = (new ChatComponentText(s)).a(EnumChatFormat.YELLOW);

                this.playerConnection.sendPacket(new PacketPlayOutChat((new ChatMessage("multiplayer.message_not_delivered", new Object[] { ichatbasecomponent1})).a(EnumChatFormat.RED), ChatMessageType.SYSTEM));
            }

        });
    }

    public String v() {
        String s = this.playerConnection.networkManager.getSocketAddress().toString();

        s = s.substring(s.indexOf("/") + 1);
        s = s.substring(0, s.indexOf(":"));
        return s;
    }

    public void a(PacketPlayInSettings packetplayinsettings) {
        // CraftBukkit start
        if (getMainHand() != packetplayinsettings.getMainHand()) {
            PlayerChangedMainHandEvent event = new PlayerChangedMainHandEvent(getBukkitEntity(), getMainHand() == EnumMainHand.LEFT ? MainHand.LEFT : MainHand.RIGHT);
            this.server.server.getPluginManager().callEvent(event);
        }
        if (this.locale == null || !this.locale.equals(packetplayinsettings.b())) { // Paper - check for null
            PlayerLocaleChangeEvent event = new PlayerLocaleChangeEvent(getBukkitEntity(), packetplayinsettings.b());
            this.server.server.getPluginManager().callEvent(event);
        }
        this.clientViewDistance = packetplayinsettings.viewDistance;
        // CraftBukkit end
        // Paper start - add PlayerLocaleChangeEvent
        // Since the field is initialized to null, this event should always fire the first time the packet is received
        String oldLocale = this.locale;
        this.locale = packetplayinsettings.b();
        if (!this.locale.equals(oldLocale)) {
            new com.destroystokyo.paper.event.player.PlayerLocaleChangeEvent(this.getBukkitEntity(), oldLocale, this.locale).callEvent();
        }
        // Paper end
        this.cs = packetplayinsettings.d();
        this.ct = packetplayinsettings.e();
        this.getDataWatcher().set(EntityPlayer.bx, (byte) packetplayinsettings.f());
        this.getDataWatcher().set(EntityPlayer.by, (byte) (packetplayinsettings.getMainHand() == EnumMainHand.LEFT ? 0 : 1));
    }

    public EntityHuman.EnumChatVisibility getChatFlags() {
        return this.cs;
    }

    public void setResourcePack(String s, String s1) {
        this.playerConnection.sendPacket(new PacketPlayOutResourcePackSend(s, s1));
    }

    protected int y() {
        return this.server.a(this.getProfile());
    }

    public void resetIdleTimer() {
        this.cu = SystemUtils.getMonotonicMillis();
    }

    public ServerStatisticManager getStatisticManager() {
        return this.cg;
    }

    public RecipeBookServer B() {
        return this.recipeBook;
    }

    public void c(Entity entity) {
        if (entity instanceof EntityHuman) {
            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { entity.getId()}));
        } else {
            this.removeQueue.add((Integer) entity.getId()); // CraftBukkit - decompile error
        }

    }

    public void d(Entity entity) {
        this.removeQueue.remove((Integer) entity.getId()); // CraftBukkit - decompile error
    }

    protected void C() {
        if (this.isSpectator()) {
            this.cl();
            this.setInvisible(true);
        } else {
            super.C();
        }

        this.getWorldServer().getTracker().a(this);
    }

    public Entity getSpecatorTarget() {
        return (Entity) (this.spectatedEntity == null ? this : this.spectatedEntity);
    }

    public void setSpectatorTarget(Entity newSpectatorTarget) {
        // Paper start - Add PlayerStartSpectatingEntityEvent and PlayerStopSpectatingEntity Event
        Entity entity1 = this.getSpecatorTarget();

        if (newSpectatorTarget == null) {
            newSpectatorTarget = this;
        }

        if (entity1 == newSpectatorTarget) return; // new spec target is the current spec target

        if (newSpectatorTarget == this) {
            com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent playerStopSpectatingEntityEvent = new com.destroystokyo.paper.event.player.PlayerStopSpectatingEntityEvent(this.getBukkitEntity(), entity1.getBukkitEntity());

            if (!playerStopSpectatingEntityEvent.callEvent()) {
                return;
            }
        } else {
            com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent playerStartSpectatingEntityEvent = new com.destroystokyo.paper.event.player.PlayerStartSpectatingEntityEvent(this.getBukkitEntity(), entity1.getBukkitEntity(), newSpectatorTarget.getBukkitEntity());

            if (!playerStartSpectatingEntityEvent.callEvent()) {
                return;
            }
        }

        setSpectatorTargetField(newSpectatorTarget);

        this.playerConnection.sendPacket(new PacketPlayOutCamera(newSpectatorTarget));
        this.playerConnection.a(this.spectatedEntity.locX, this.spectatedEntity.locY, this.spectatedEntity.locZ, this.yaw, this.pitch, TeleportCause.SPECTATE); // CraftBukkit
        // Paper end
    }

    protected void E() {
        if (this.portalCooldown > 0 && !this.worldChangeInvuln) {
            --this.portalCooldown;
        }

    }

    public void attack(Entity entity) {
        if (this.playerInteractManager.getGameMode() == EnumGamemode.SPECTATOR) {
            this.setSpectatorTarget(entity);
        } else {
            super.attack(entity);
        }

    }

    public long F() {
        return this.cu;
    }

    @Nullable
    public IChatBaseComponent getPlayerListName() {
        return listName; // CraftBukkit
    }

    public void a(EnumHand enumhand) {
        super.a(enumhand);
        this.dH();
    }

    public boolean H() {
        return this.worldChangeInvuln;
    }

    public void I() {
        this.worldChangeInvuln = false;
    }

    public void J() {
        if (!CraftEventFactory.callToggleGlideEvent(this, true).isCancelled()) // CraftBukkit
        this.setFlag(7, true);
    }

    public void K() {
        // CraftBukkit start
        if (!CraftEventFactory.callToggleGlideEvent(this, false).isCancelled()) {
        this.setFlag(7, true);
        this.setFlag(7, false);
        }
        // CraftBukkit end
    }

    public AdvancementDataPlayer getAdvancementData() {
        return this.cf;
    }

    @Nullable
    public Vec3D M() {
        return this.cC;
    }

    // CraftBukkit start
    public void a(WorldServer worldserver, double d0, double d1, double d2, float f, float f1) {
        this.a(worldserver, d0, d1, d2, f, f1, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    public void a(WorldServer worldserver, double d0, double d1, double d2, float f, float f1, org.bukkit.event.player.PlayerTeleportEvent.TeleportCause cause) {
        // CraftBukkit end
        this.setSpectatorTarget(this);
        this.stopRiding();
        /* CraftBukkit start - replace with bukkit handling for multi-world
        if (worldserver == this.world) {
            this.playerConnection.a(d0, d1, d2, f, f1);
        } else {
            WorldServer worldserver1 = this.getWorldServer();

            this.dimension = worldserver.worldProvider.getDimensionManager();
            this.playerConnection.sendPacket(new PacketPlayOutRespawn(this.dimension, worldserver1.getDifficulty(), worldserver1.getWorldData().getType(), this.playerInteractManager.getGameMode()));
            this.server.getPlayerList().f(this);
            worldserver1.removeEntity(this);
            this.dead = false;
            this.setPositionRotation(d0, d1, d2, f, f1);
            if (this.isAlive()) {
                worldserver1.entityJoinedWorld(this, false);
                worldserver.addEntity(this);
                worldserver.entityJoinedWorld(this, false);
            }

            this.spawnIn(worldserver);
            this.server.getPlayerList().a(this, worldserver1);
            this.playerConnection.a(d0, d1, d2, f, f1);
            this.playerInteractManager.a(worldserver);
            this.server.getPlayerList().b(this, worldserver);
            this.server.getPlayerList().updateClient(this);
        }
        */
        this.getBukkitEntity().teleport(new Location(worldserver.getWorld(), d0, d1, d2, f, f1), cause);
        // CraftBukkit end

    }

    // CraftBukkit start - Add per-player time and weather.
    public long timeOffset = 0;
    public boolean relativeTime = true;

    public long getPlayerTime() {
        if (this.relativeTime) {
            // Adds timeOffset to the current server time.
            return this.world.getDayTime() + this.timeOffset;
        } else {
            // Adds timeOffset to the beginning of this day.
            return this.world.getDayTime() - (this.world.getDayTime() % 24000) + this.timeOffset;
        }
    }

    public WeatherType weather = null;

    public WeatherType getPlayerWeather() {
        return this.weather;
    }

    public void setPlayerWeather(WeatherType type, boolean plugin) {
        if (!plugin && this.weather != null) {
            return;
        }

        if (plugin) {
            this.weather = type;
        }

        if (type == WeatherType.DOWNFALL) {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(2, 0));
        } else {
            this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(1, 0));
        }
    }

    private float pluginRainPosition;
    private float pluginRainPositionPrevious;

    public void updateWeather(float oldRain, float newRain, float oldThunder, float newThunder) {
        if (this.weather == null) {
            // Vanilla
            if (oldRain != newRain) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, newRain));
            }
        } else {
            // Plugin
            if (pluginRainPositionPrevious != pluginRainPosition) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, pluginRainPosition));
            }
        }

        if (oldThunder != newThunder) {
            if (weather == WeatherType.DOWNFALL || weather == null) {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, newThunder));
            } else {
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, 0));
            }
        }
    }

    public void tickWeather() {
        if (this.weather == null) return;

        pluginRainPositionPrevious = pluginRainPosition;
        if (weather == WeatherType.DOWNFALL) {
            pluginRainPosition += 0.01;
        } else {
            pluginRainPosition -= 0.01;
        }

        pluginRainPosition = MathHelper.a(pluginRainPosition, 0.0F, 1.0F);
    }

    public void resetPlayerWeather() {
        this.weather = null;
        this.setPlayerWeather(this.world.getWorldData().hasStorm() ? WeatherType.DOWNFALL : WeatherType.CLEAR, false);
    }

    @Override
    public String toString() {
        return super.toString() + "(" + this.getName() + " at " + this.locX + "," + this.locY + "," + this.locZ + ")";
    }

    // SPIGOT-1903, MC-98153
    public void forceSetPositionRotation(double x, double y, double z, float yaw, float pitch) {
        this.setPositionRotation(x, y, z, yaw, pitch);
        this.playerConnection.syncPosition();
    }

    @Override
    protected boolean isFrozen() {
        return super.isFrozen() || (this.playerConnection != null && this.playerConnection.isDisconnected()); // Paper
    }

    @Override
    public Scoreboard getScoreboard() {
        return getBukkitEntity().getScoreboard().getHandle();
    }

    public void reset() {
        float exp = 0;
        boolean keepInventory = this.world.getGameRules().getBoolean("keepInventory");

        if (this.keepLevel || keepInventory) {
            exp = this.exp;
            this.newTotalExp = this.expTotal;
            this.newLevel = this.expLevel;
        }

        this.setHealth(this.getMaxHealth());
        this.setAirTicks(this.getMaxAirTicks()); // Paper
        this.fireTicks = 0;
        this.fallDistance = 0;
        this.foodData = new FoodMetaData(this);
        this.expLevel = this.newLevel;
        this.expTotal = this.newTotalExp;
        this.exp = 0;
        this.deathTicks = 0;
        this.setArrowCount(0);
        this.removeAllEffects(org.bukkit.event.entity.EntityPotionEffectEvent.Cause.DEATH);
        this.updateEffects = true;
        this.activeContainer = this.defaultContainer;
        this.killer = null;
        this.lastDamager = null;
        this.combatTracker = new CombatTracker(this);
        this.lastSentExp = -1;
        if (this.keepLevel || keepInventory) {
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
