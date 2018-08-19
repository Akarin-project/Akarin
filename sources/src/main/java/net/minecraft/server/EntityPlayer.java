package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;

import io.netty.buffer.Unpooled;
import java.util.ArrayDeque; // Paper
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque; // Paper
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

/**
 * Akarin Changes Note
 * 2) Add lock to player track (safety issue)
 */
public class EntityPlayer extends EntityHuman implements ICrafting {

    private static final Logger bV = LogManager.getLogger();
    public String locale = null; // PAIL: private -> public // Paper - default to null
    public long lastSave = MinecraftServer.currentTick; // Paper
    public PlayerConnection playerConnection;
    public final MinecraftServer server;
    public final PlayerInteractManager playerInteractManager;
    public double d;
    public double e;
    public final Deque<Integer> removeQueue = new ArrayDeque<>(); // Paper
    private final AdvancementDataPlayer bY;
    private final ServerStatisticManager bZ;
    private float ca = Float.MIN_VALUE;
    private int cb = Integer.MIN_VALUE;
    private int cc = Integer.MIN_VALUE;
    private int cd = Integer.MIN_VALUE;
    private int ce = Integer.MIN_VALUE;
    private int cf = Integer.MIN_VALUE;
    private float lastHealthSent = -1.0E8F;
    private int ch = -99999999;
    private boolean ci = true;
    public int lastSentExp = -99999999;
    public int invulnerableTicks = 60;
    private EntityHuman.EnumChatVisibility cl;
    private boolean cm = true;
    private long cn = System.currentTimeMillis();
    private Entity co;
    public boolean worldChangeInvuln;
    private boolean cq; private void setHasSeenCredits(boolean has) { this.cq = has; } // Paper - OBFHELPER
    private final RecipeBookServer cr = new RecipeBookServer();
    private Vec3D cs;
    private int ct;
    private boolean cu;
    private Vec3D cv;
    private int containerCounter;
    public boolean f;
    public int ping;
    public boolean viewingCredits;
    // Paper start - Player view distance API
    private int viewDistance = -1;
    public int getViewDistance() {
        return viewDistance == -1 ? ((WorldServer) world).getPlayerChunkMap().getViewDistance() : viewDistance;
    }
    public void setViewDistance(int viewDistance) {
        this.viewDistance = viewDistance;
    }
    // Paper end
    private int containerUpdateDelay; // Paper

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
    // CraftBukkit end

    public EntityPlayer(MinecraftServer minecraftserver, WorldServer worldserver, GameProfile gameprofile, PlayerInteractManager playerinteractmanager) {
        super(worldserver, gameprofile);
        playerinteractmanager.player = this;
        this.playerInteractManager = playerinteractmanager;
        // CraftBukkit start
        BlockPosition blockposition = getSpawnPoint(minecraftserver, worldserver);

        this.server = minecraftserver;
        this.bZ = minecraftserver.getPlayerList().getStatisticManager(this);
        this.bY = minecraftserver.getPlayerList().h(this);
        this.P = 1.0F;
        this.setPositionRotation(blockposition, 0.0F, 0.0F);
        // CraftBukkit end

        while (!worldserver.getCubes(this, this.getBoundingBox()).isEmpty() && this.locY < 255.0D) {
            this.setPosition(this.locX, this.locY + 1.0D, this.locZ);
        }

        // CraftBukkit start
        this.displayName = this.getName();
        this.canPickUpLoot = true;
        this.maxHealthCache = this.getMaxHealth();
        // CraftBukkit end
    }

    public final BlockPosition getSpawnPoint(MinecraftServer minecraftserver, WorldServer worldserver) {
        BlockPosition blockposition = worldserver.getSpawn();

        if (worldserver.worldProvider.m() && worldserver.getWorldData().getGameType() != EnumGamemode.ADVENTURE) {
            int i = Math.max(0, minecraftserver.a(worldserver));
            int j = MathHelper.floor(worldserver.getWorldBorder().b((double) blockposition.getX(), (double) blockposition.getZ()));

            if (j < i) {
                i = j;
            }

            if (j <= 1) {
                i = 1;
            }

            blockposition = worldserver.q(blockposition.a(this.random.nextInt(i * 2 + 1) - i, 0, this.random.nextInt(i * 2 + 1) - i));
        }

        return blockposition;
    }
    // CraftBukkit end

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (this.locY > 300) this.locY = 257; // Paper - bring down to a saner Y level if out of world
        if (nbttagcompound.hasKeyOfType("playerGameType", 99)) {
            if (this.C_().getForceGamemode()) {
                this.playerInteractManager.setGameMode(this.C_().getGamemode());
            } else {
                this.playerInteractManager.setGameMode(EnumGamemode.getById(nbttagcompound.getInt("playerGameType")));
            }
        }

        if (nbttagcompound.hasKeyOfType("enteredNetherPosition", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("enteredNetherPosition");

            this.cv = new Vec3D(nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("y"), nbttagcompound1.getDouble("z"));
        }

        this.cq = nbttagcompound.getBoolean("seenCredits");
        if (nbttagcompound.hasKeyOfType("recipeBook", 10)) {
            this.cr.a(nbttagcompound.getCompound("recipeBook"));
        }
        this.getBukkitEntity().readExtraData(nbttagcompound); // CraftBukkit

    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.PLAYER, new DataInspector() {
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                if (nbttagcompound.hasKeyOfType("RootVehicle", 10)) {
                    NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("RootVehicle");

                    if (nbttagcompound1.hasKeyOfType("Entity", 10)) {
                        nbttagcompound1.set("Entity", dataconverter.a(DataConverterTypes.ENTITY, nbttagcompound1.getCompound("Entity"), i));
                    }
                }

                return nbttagcompound;
            }
        });
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("playerGameType", this.playerInteractManager.getGameMode().getId());
        nbttagcompound.setBoolean("seenCredits", this.cq);
        if (this.cv != null) {
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.setDouble("x", this.cv.x);
            nbttagcompound1.setDouble("y", this.cv.y);
            nbttagcompound1.setDouble("z", this.cv.z);
            nbttagcompound.set("enteredNetherPosition", nbttagcompound1);
        }

        Entity entity = this.getVehicle();
        Entity entity1 = this.bJ();

        if (entity1 != null && entity != this && entity.b(EntityPlayer.class).size() == 1) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            NBTTagCompound nbttagcompound3 = new NBTTagCompound();

            entity.d(nbttagcompound3);
            nbttagcompound2.a("Attach", entity1.getUniqueID());
            nbttagcompound2.set("Entity", nbttagcompound3);
            nbttagcompound.set("RootVehicle", nbttagcompound2);
        }

        nbttagcompound.set("recipeBook", this.cr.c());
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

    protected ItemCooldown l() {
        return new ItemCooldownPlayer(this);
    }

    public void B_() {
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
            Iterator iterator = this.removeQueue.iterator();
            int j = 0;

            // Paper start
            /* while (iterator.hasNext() && j < i) {
                aint[j++] = ((Integer) iterator.next()).intValue();
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
                this.server.getPlayerList().d(this);
                if (this.isSneaking()) {
                    this.setSpectatorTarget(this);
                }
            } else {
                this.setSpectatorTarget(this);
            }
        }

        CriterionTriggers.v.a(this);
        if (this.cs != null) {
            CriterionTriggers.t.a(this, this.cs, this.ticksLived - this.ct);
        }

        this.bY.b(this);
    }

    public void playerTick() {
        try {
            super.B_();

            for (int i = 0; i < this.inventory.getSize(); ++i) {
                ItemStack itemstack = this.inventory.getItem(i);

                if (!itemstack.isEmpty() && itemstack.getItem().f()) {
                    Packet packet = ((ItemWorldMapBase) itemstack.getItem()).a(itemstack, this.world, (EntityHuman) this);

                    if (packet != null) {
                        this.playerConnection.sendPacket(packet);
                    }
                }
            }

            if (this.getHealth() != this.lastHealthSent || this.ch != this.foodData.getFoodLevel() || this.foodData.getSaturationLevel() == 0.0F != this.ci) {
                this.playerConnection.sendPacket(new PacketPlayOutUpdateHealth(this.getBukkitEntity().getScaledHealth(), this.foodData.getFoodLevel(), this.foodData.getSaturationLevel())); // CraftBukkit
                this.lastHealthSent = this.getHealth();
                this.ch = this.foodData.getFoodLevel();
                this.ci = this.foodData.getSaturationLevel() == 0.0F;
            }

            if (this.getHealth() + this.getAbsorptionHearts() != this.ca) {
                this.ca = this.getHealth() + this.getAbsorptionHearts();
                this.a(IScoreboardCriteria.g, MathHelper.f(this.ca));
            }

            if (this.foodData.getFoodLevel() != this.cb) {
                this.cb = this.foodData.getFoodLevel();
                this.a(IScoreboardCriteria.h, MathHelper.f((float) this.cb));
            }

            if (this.getAirTicks() != this.cc) {
                this.cc = this.getAirTicks();
                this.a(IScoreboardCriteria.i, MathHelper.f((float) this.cc));
            }

            // CraftBukkit start - Force max health updates
            if (this.maxHealthCache != this.getMaxHealth()) {
                this.getBukkitEntity().updateScaledHealth();
            }
            // CraftBukkit end

            if (this.getArmorStrength() != this.cd) {
                this.cd = this.getArmorStrength();
                this.a(IScoreboardCriteria.j, MathHelper.f((float) this.cd));
            }

            if (this.expTotal != this.cf) {
                this.cf = this.expTotal;
                this.a(IScoreboardCriteria.k, MathHelper.f((float) this.cf));
            }

            if (this.expLevel != this.ce) {
                this.ce = this.expLevel;
                this.a(IScoreboardCriteria.l, MathHelper.f((float) this.ce));
            }

            if (this.expTotal != this.lastSentExp) {
                this.lastSentExp = this.expTotal;
                this.playerConnection.sendPacket(new PacketPlayOutExperience(this.exp, this.expTotal, this.expLevel));
            }

            if (this.ticksLived % 20 == 0) {
                CriterionTriggers.o.a(this);
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
        Collection collection = this.world.getServer().getScoreboardManager().getScoreboardScores(iscoreboardcriteria, this.getName(), new java.util.ArrayList<ScoreboardScore>()); // CraftBukkit - Use our scores instead
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next(); // CraftBukkit - Use our scores instead

            scoreboardscore.setScore(i);
        }

    }

    public void die(DamageSource damagesource) {
        boolean flag = this.world.getGameRules().getBoolean("showDeathMessages");

        this.playerConnection.sendPacket(new PacketPlayOutCombatEvent(this.getCombatTracker(), PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED, flag));
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

        IChatBaseComponent chatmessage = this.getCombatTracker().getDeathMessage();

        String deathmessage = chatmessage.toPlainText();
        org.bukkit.event.entity.PlayerDeathEvent event = CraftEventFactory.callPlayerDeathEvent(this, loot, deathmessage, keepInventory);

        String deathMessage = event.getDeathMessage();

        if (deathMessage != null && deathMessage.length() > 0 && flag) { // TODO: allow plugins to override?
            if (deathMessage.equals(deathmessage)) {
                ScoreboardTeamBase scoreboardteambase = this.aY();

                if (scoreboardteambase != null && scoreboardteambase.getDeathMessageVisibility() != ScoreboardTeamBase.EnumNameTagVisibility.ALWAYS) {
                    if (scoreboardteambase.getDeathMessageVisibility() == ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OTHER_TEAMS) {
                        this.server.getPlayerList().a((EntityHuman) this, chatmessage);
                    } else if (scoreboardteambase.getDeathMessageVisibility() == ScoreboardTeamBase.EnumNameTagVisibility.HIDE_FOR_OWN_TEAM) {
                        this.server.getPlayerList().b((EntityHuman) this, chatmessage);
                    }
                } else {
                    this.server.getPlayerList().sendMessage(chatmessage);
                }
            } else {
                this.server.getPlayerList().sendMessage(org.bukkit.craftbukkit.util.CraftChatMessage.fromString(deathMessage));
            }
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
        Collection collection = this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.d, this.getName(), new java.util.ArrayList<ScoreboardScore>());
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            ScoreboardScore scoreboardscore = (ScoreboardScore) iterator.next(); // CraftBukkit - Use our scores instead

            scoreboardscore.incrementScore();
        }

        EntityLiving entityliving = this.ci();

        if (entityliving != null) {
            EntityTypes.MonsterEggInfo entitytypes_monsteregginfo = (EntityTypes.MonsterEggInfo) EntityTypes.eggInfo.get(EntityTypes.a((Entity) entityliving));

            if (entitytypes_monsteregginfo != null) {
                this.b(entitytypes_monsteregginfo.killedByEntityStatistic);
            }

            entityliving.a(this, this.bb, damagesource);
        }

        this.b(StatisticList.A);
        this.a(StatisticList.h);
        this.extinguish();
        this.setFlag(0, false);
        this.getCombatTracker().g();
    }

    public void a(Entity entity, int i, DamageSource damagesource) {
        if (entity != this) {
            super.a(entity, i, damagesource);
            this.addScore(i);
            // CraftBukkit - Get our scores instead
            Collection<ScoreboardScore> collection = this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.f, this.getName(), new java.util.ArrayList<ScoreboardScore>());

            if (entity instanceof EntityHuman) {
                this.b(StatisticList.D);
                // CraftBukkit - Get our scores instead
                this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.e, this.getName(), collection);
                // collection.addAll(this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.e));
                // CraftBukkit end
            } else {
                this.b(StatisticList.B);
            }

            collection.addAll(this.E(entity));
            Iterator<ScoreboardScore> iterator = collection.iterator(); // CraftBukkit

            while (iterator.hasNext()) {
                // CraftBukkit start
                // ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                // this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).incrementScore();
                iterator.next().incrementScore();
                // CraftBukkit end
            }

            CriterionTriggers.b.a(this, entity, damagesource);
        }
    }

    private Collection<ScoreboardScore> E(Entity entity) { // CraftBukkit
        String s = entity instanceof EntityHuman ? entity.getName() : entity.bn();
        ScoreboardTeam scoreboardteam = this.getScoreboard().getPlayerTeam(this.getName());

        if (scoreboardteam != null) {
            int i = scoreboardteam.getColor().b();

            if (i >= 0 && i < IScoreboardCriteria.n.length) {
                Iterator iterator = this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.n[i]).iterator();

                while (iterator.hasNext()) {
                    ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();
                    ScoreboardScore scoreboardscore = this.getScoreboard().getPlayerScoreForObjective(s, scoreboardobjective);

                    scoreboardscore.incrementScore();
                }
            }
        }

        ScoreboardTeam scoreboardteam1 = this.getScoreboard().getPlayerTeam(s);

        if (scoreboardteam1 != null) {
            int j = scoreboardteam1.getColor().b();

            if (j >= 0 && j < IScoreboardCriteria.m.length) {
                // CraftBukkit - Get our scores instead
                return this.world.getServer().getScoreboardManager().getScoreboardScores(IScoreboardCriteria.m[j], this.getName(), new java.util.ArrayList<ScoreboardScore>());
                // return this.getScoreboard().getObjectivesForCriteria(IScoreboardCriteria.m[j]);
                // CraftBukkit end
            }
        }

        return Lists.newArrayList();
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            boolean flag = this.server.aa() && this.canPvP() && "fall".equals(damagesource.translationIndex);

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

                        if (entityarrow.shooter instanceof EntityHuman && !this.a((EntityHuman) entityarrow.shooter)) {
                            return false;
                        }
                    }
                }

                return super.damageEntity(damagesource, f);
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
    public Entity b(int i) {
        if (this.isSleeping()) return this; // CraftBukkit - SPIGOT-3154
        // this.worldChangeInvuln = true; // CraftBukkit - Moved down and into PlayerList#changeDimension
        if (this.dimension == 0 && i == -1) {
            this.cv = new Vec3D(this.locX, this.locY, this.locZ);
        } else if (this.dimension != -1 && i != 0) {
            this.cv = null;
        }

        if (this.dimension == 1 && i == 1) {
            this.worldChangeInvuln = true; // CraftBukkit - Moved down from above
            this.world.kill(this);
            if (!this.viewingCredits) {
                this.viewingCredits = true;
                if (world.paperConfig.disableEndCredits) this.setHasSeenCredits(true); // Paper - Toggle to always disable end credits
                this.playerConnection.sendPacket(new PacketPlayOutGameStateChange(4, this.cq ? 0.0F : 1.0F));
                this.cq = true;
            }

            return this;
        } else {
            if (this.dimension == 0 && i == 1) {
                i = 1;
            }

            // CraftBukkit start
            TeleportCause cause = (this.dimension == 1 || i == 1) ? TeleportCause.END_PORTAL : TeleportCause.NETHER_PORTAL;
            this.server.getPlayerList().changeDimension(this, i, cause); // PAIL: check all this
            // CraftBukkit end
            this.playerConnection.sendPacket(new PacketPlayOutWorldEvent(1032, BlockPosition.ZERO, 0, false));
            this.lastSentExp = -1;
            this.lastHealthSent = -1.0F;
            this.ch = -1;
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
            this.b(StatisticList.ab);
            PacketPlayOutBed packetplayoutbed = new PacketPlayOutBed(this, blockposition);

            this.x().getTracker().a((Entity) this, (Packet) packetplayoutbed);
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            this.playerConnection.sendPacket(packetplayoutbed);
            CriterionTriggers.p.a(this);
        }

        return entityhuman_enumbedresult;
    }

    public void a(boolean flag, boolean flag1, boolean flag2) {
        if (!this.sleeping) return; // CraftBukkit - Can't leave bed if not in one!
        if (this.isSleeping()) {
            this.x().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(this, 2));
        }

        super.a(flag, flag1, flag2);
        if (this.playerConnection != null) {
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }

    }

    public boolean a(Entity entity, boolean flag) {
        Entity entity1 = this.bJ();

        if (!super.a(entity, flag)) {
            return false;
        } else {
            Entity entity2 = this.bJ();

            if (entity2 != entity1 && this.playerConnection != null) {
                this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
            }

            return true;
        }
    }

    public void stopRiding() {
        Entity entity = this.bJ();

        super.stopRiding();
        Entity entity1 = this.bJ();

        if (entity1 != entity && this.playerConnection != null) {
            this.playerConnection.a(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
        }
        // Paper start - "Fixes" an issue in which the vehicle player would not be notified that the passenger dismounted
        if (entity instanceof EntityPlayer) {
            WorldServer worldServer = (WorldServer) entity.getWorld();
            worldServer.tracker.untrackEntity(this);
            worldServer.tracker.entriesLock.writeLock().lock(); // Akarin - ProtocolSupport will overwrite track method
            worldServer.tracker.track(this);
            worldServer.tracker.entriesLock.writeLock().unlock(); // Akarin - ProtocolSupport will overwrite track method
        }
        // Paper end

    }

    public boolean isInvulnerable(DamageSource damagesource) {
        return super.isInvulnerable(damagesource) || this.L();
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

        if (iblockdata.getMaterial() == Material.AIR) {
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
        if (false && itileentitycontainer instanceof ILootable && ((ILootable) itileentitycontainer).b() != null && this.isSpectator()) {
            this.a((new ChatMessage("container.spectatorCantOpen", new Object[0])).setChatModifier((new ChatModifier()).setColor(EnumChatFormat.RED)), true);
        } else {
            boolean cancelled = itileentitycontainer instanceof ILootable && ((ILootable) itileentitycontainer).b() != null && this.isSpectator();
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

        if (iinventory instanceof ILootable && ((ILootable) iinventory).b() != null && this.isSpectator()) {
            this.a((new ChatMessage("container.spectatorCantOpen", new Object[0])).setChatModifier((new ChatModifier()).setColor(EnumChatFormat.RED)), true);
        } else {
            if (this.activeContainer != this.defaultContainer) {
                this.closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.OPEN_NEW); // Paper
            }

            if (iinventory instanceof ITileInventory) {
                ITileInventory itileinventory = (ITileInventory) iinventory;

                if (itileinventory.isLocked() && !this.a(itileinventory.getLock()) && !this.isSpectator()) {
                    this.playerConnection.sendPacket(new PacketPlayOutChat(new ChatMessage("container.isLocked", new Object[] { iinventory.getScoreboardDisplayName()}), ChatMessageType.GAME_INFO));
                    this.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.ab, SoundCategory.BLOCKS, this.locX, this.locY, this.locZ, 1.0F, 1.0F));
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
        this.activeContainer.windowId = this.containerCounter;
        this.activeContainer.addSlotListener(this);
        InventoryMerchant inventorymerchant = ((ContainerMerchant) this.activeContainer).e();
        IChatBaseComponent ichatbasecomponent = imerchant.getScoreboardDisplayName();

        this.playerConnection.sendPacket(new PacketPlayOutOpenWindow(this.containerCounter, "minecraft:villager", ichatbasecomponent, inventorymerchant.getSize()));
        MerchantRecipeList merchantrecipelist = imerchant.getOffers(this);

        if (merchantrecipelist != null) {
            PacketDataSerializer packetdataserializer = new PacketDataSerializer(Unpooled.buffer());

            packetdataserializer.writeInt(this.containerCounter);
            merchantrecipelist.a(packetdataserializer);
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|TrList", packetdataserializer));
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
            this.playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|BOpen", packetdataserializer));
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
        this.r();
    }

    public void broadcastCarriedItem() {
        if (!this.f) {
            this.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.inventory.getCarried()));
        }
    }

    public void r() {
        this.activeContainer.b((EntityHuman) this);
        this.activeContainer = this.defaultContainer;
    }

    public void a(float f, float f1, boolean flag, boolean flag1) {
        if (this.isPassenger()) {
            if (f >= -1.0F && f <= 1.0F) {
                this.be = f;
            }

            if (f1 >= -1.0F && f1 <= 1.0F) {
                this.bg = f1;
            }

            this.bd = flag;
            this.setSneaking(flag1);
        }

    }

    public void a(Statistic statistic, int i) {
        if (statistic != null) {
            this.bZ.b(this, statistic, i);
            Iterator iterator = this.getScoreboard().getObjectivesForCriteria(statistic.f()).iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).addScore(i);
            }

        }
    }

    public void a(Statistic statistic) {
        if (statistic != null) {
            this.bZ.setStatistic(this, statistic, 0);
            Iterator iterator = this.getScoreboard().getObjectivesForCriteria(statistic.f()).iterator();

            while (iterator.hasNext()) {
                ScoreboardObjective scoreboardobjective = (ScoreboardObjective) iterator.next();

                this.getScoreboard().getPlayerScoreForObjective(this.getName(), scoreboardobjective).setScore(0);
            }

        }
    }

    public void a(List<IRecipe> list) {
        this.cr.a(list, this);
    }

    public void a(MinecraftKey[] aminecraftkey) {
        ArrayList arraylist = Lists.newArrayList();
        MinecraftKey[] aminecraftkey1 = aminecraftkey;
        int i = aminecraftkey.length;

        for (int j = 0; j < i; ++j) {
            MinecraftKey minecraftkey = aminecraftkey1[j];

            // CraftBukkit start
            if (CraftingManager.a(minecraftkey) == null) {
                Bukkit.getLogger().warning("Ignoring grant of non existent recipe " + minecraftkey);
                continue;
            }
            // CraftBukit end
            arraylist.add(CraftingManager.a(minecraftkey));
        }

        this.a((List<IRecipe>) arraylist); // CraftBukkit - decompile error
    }

    public void b(List<IRecipe> list) {
        this.cr.b(list, this);
    }

    public void s() {
        this.cu = true;
        this.ejectPassengers();
        if (this.sleeping) {
            this.a(true, false, false);
        }

    }

    public boolean t() {
        return this.cu;
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

    protected void v() {
        if (!this.activeItem.isEmpty() && this.isHandRaised()) {
            this.playerConnection.sendPacket(new PacketPlayOutEntityStatus(this, (byte) 9));
            super.v();
        }

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
            this.an = entityplayer.an;
            this.ao = entityplayer.ao;
            this.ap = entityplayer.ap;
        } else if (this.world.getGameRules().getBoolean("keepInventory") || entityplayer.isSpectator()) {
            this.inventory.a(entityplayer.inventory);
            this.expLevel = entityplayer.expLevel;
            this.expTotal = entityplayer.expTotal;
            this.exp = entityplayer.exp;
            this.setScore(entityplayer.getScore());
        }

        this.bS = entityplayer.bS;
        this.enderChest = entityplayer.enderChest;
        this.getDataWatcher().set(EntityPlayer.br, entityplayer.getDataWatcher().get(EntityPlayer.br));
        this.lastSentExp = -1;
        this.lastHealthSent = -1.0F;
        this.ch = -1;
        // this.cr.a((RecipeBook) entityplayer.cr); // CraftBukkit
        // Paper start - Optimize remove queue
        //this.removeQueue.addAll(entityplayer.removeQueue);
        if (this.removeQueue != entityplayer.removeQueue) {
            this.removeQueue.addAll(entityplayer.removeQueue);
        }
        this.cq = entityplayer.cq;
        this.cv = entityplayer.cv;
        this.setShoulderEntityLeft(entityplayer.getShoulderEntityLeft());
        this.setShoulderEntityRight(entityplayer.getShoulderEntityRight());
    }

    protected void a(MobEffect mobeffect) {
        super.a(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
        if (mobeffect.getMobEffect() == MobEffects.LEVITATION) {
            this.ct = this.ticksLived;
            this.cs = new Vec3D(this.locX, this.locY, this.locZ);
        }

        CriterionTriggers.z.a(this);
    }

    protected void a(MobEffect mobeffect, boolean flag) {
        super.a(mobeffect, flag);
        this.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.getId(), mobeffect));
        CriterionTriggers.z.a(this);
    }

    protected void b(MobEffect mobeffect) {
        super.b(mobeffect);
        this.playerConnection.sendPacket(new PacketPlayOutRemoveEntityEffect(this.getId(), mobeffect.getMobEffect()));
        if (mobeffect.getMobEffect() == MobEffects.LEVITATION) {
            this.cs = null;
        }

        CriterionTriggers.z.a(this);
    }

    public void enderTeleportTo(double d0, double d1, double d2) {
        this.playerConnection.a(d0, d1, d2, this.yaw, this.pitch);
    }

    public void a(Entity entity) {
        this.x().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 4));
    }

    public void b(Entity entity) {
        this.x().getTracker().sendPacketToEntity(this, new PacketPlayOutAnimation(entity, 5));
    }

    public void updateAbilities() {
        if (this.playerConnection != null) {
            this.playerConnection.sendPacket(new PacketPlayOutAbilities(this.abilities));
            this.G();
        }
    }

    public WorldServer x() {
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
        this.cE();
    }

    public boolean isSpectator() {
        return this.playerInteractManager.getGameMode() == EnumGamemode.SPECTATOR;
    }

    public boolean z() {
        return this.playerInteractManager.getGameMode() == EnumGamemode.CREATIVE;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.playerConnection.sendPacket(new PacketPlayOutChat(ichatbasecomponent));
    }

    public boolean a(int i, String s) {
        /* CraftBukkit start
        if ("seed".equals(s) && !this.server.aa()) {
            return true;
        } else if (!"tell".equals(s) && !"help".equals(s) && !"me".equals(s) && !"trigger".equals(s)) {
            if (this.server.getPlayerList().isOp(this.getProfile())) {
                OpListEntry oplistentry = (OpListEntry) this.server.getPlayerList().getOPs().get(this.getProfile());

                return oplistentry != null ? oplistentry.a() >= i : this.server.q() >= i;
            } else {
                return false;
            }
        } else {
            return true;
        }
        */
        if ("@".equals(s)) {
            return getBukkitEntity().hasPermission("minecraft.command.selector");
        }
        if ("".equals(s)) {
            return getBukkitEntity().isOp();
        }
        return getBukkitEntity().hasPermission("minecraft.command." + s);
        // CraftBukkit end
    }

    public String A() {
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

        // Paper start - add PlayerLocaleChangeEvent
        // Since the field is initialized to null, this event should always fire the first time the packet is received
        String oldLocale = this.locale;
        this.locale = packetplayinsettings.a();
        if (!this.locale.equals(oldLocale)) {
            new com.destroystokyo.paper.event.player.PlayerLocaleChangeEvent(this.getBukkitEntity(), oldLocale, this.locale).callEvent();
        }

        // Compat with Bukkit
        oldLocale = oldLocale != null ? oldLocale : "en_us";
        // Paper end

        if (!oldLocale.equals(packetplayinsettings.a())) {
            PlayerLocaleChangeEvent event = new PlayerLocaleChangeEvent(getBukkitEntity(), packetplayinsettings.a());
            this.server.server.getPluginManager().callEvent(event);
        }
        // CraftBukkit end
        this.cl = packetplayinsettings.c();
        this.cm = packetplayinsettings.d();
        this.getDataWatcher().set(EntityPlayer.br, Byte.valueOf((byte) packetplayinsettings.e()));
        this.getDataWatcher().set(EntityPlayer.bs, Byte.valueOf((byte) (packetplayinsettings.getMainHand() == EnumMainHand.LEFT ? 0 : 1)));
    }

    public EntityHuman.EnumChatVisibility getChatFlags() {
        return this.cl;
    }

    public void setResourcePack(String s, String s1) {
        this.playerConnection.sendPacket(new PacketPlayOutResourcePackSend(s, s1));
    }

    public BlockPosition getChunkCoordinates() {
        return new BlockPosition(this.locX, this.locY + 0.5D, this.locZ);
    }

    public void resetIdleTimer() {
        this.cn = MinecraftServer.aw();
    }

    public ServerStatisticManager getStatisticManager() {
        return this.bZ;
    }

    public RecipeBookServer F() {
        return this.cr;
    }

    public void c(Entity entity) {
        if (entity instanceof EntityHuman) {
            this.playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[] { entity.getId()}));
        } else {
            this.removeQueue.add(Integer.valueOf(entity.getId()));
        }

    }

    public void d(Entity entity) {
        this.removeQueue.remove(Integer.valueOf(entity.getId()));
    }

    protected void G() {
        if (this.isSpectator()) {
            this.bY();
            this.setInvisible(true);
        } else {
            super.G();
        }

        this.x().getTracker().a(this);
    }

    public Entity getSpecatorTarget() {
        return (Entity) (this.co == null ? this : this.co);
    }

    public void setSpectatorTarget(Entity entity) {
        Entity entity1 = this.getSpecatorTarget();

        this.co = (Entity) (entity == null ? this : entity);
        if (entity1 != this.co) {
            this.playerConnection.sendPacket(new PacketPlayOutCamera(this.co));
            this.playerConnection.a(this.co.locX, this.co.locY, this.co.locZ, this.yaw, this.pitch, TeleportCause.SPECTATE); // CraftBukkit
        }

    }

    protected void I() {
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

    public long J() {
        return this.cn;
    }

    @Nullable
    public IChatBaseComponent getPlayerListName() {
        return listName; // CraftBukkit
    }

    public void a(EnumHand enumhand) {
        super.a(enumhand);
        this.ds();
    }

    public boolean L() {
        return this.worldChangeInvuln;
    }

    public void M() {
        this.worldChangeInvuln = false;
    }

    public void N() {
        if (!CraftEventFactory.callToggleGlideEvent(this, true).isCancelled()) // CraftBukkit
        this.setFlag(7, true);
    }

    public void O() {
        // CraftBukkit start
        if (!CraftEventFactory.callToggleGlideEvent(this, false).isCancelled()) {
        this.setFlag(7, true);
        this.setFlag(7, false);
        }
        // CraftBukkit end
    }

    public AdvancementDataPlayer getAdvancementData() {
        return this.bY;
    }

    @Nullable
    public Vec3D Q() {
        return this.cv;
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
        this.fireTicks = 0;
        this.fallDistance = 0;
        this.foodData = new FoodMetaData(this);
        this.expLevel = this.newLevel;
        this.expTotal = this.newTotalExp;
        this.exp = 0;
        this.deathTicks = 0;
        this.setArrowCount(0);
        this.removeAllEffects();
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
