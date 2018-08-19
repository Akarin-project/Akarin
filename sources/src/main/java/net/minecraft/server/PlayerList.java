package net.minecraft.server;

import co.aikar.timings.MinecraftTimings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.util.Vector;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;
// CraftBukkit end

/**
 * Akarin Changes Note
 * 1) Changes (dis)connect messages (feature)
 */
public abstract class PlayerList {

    public static final File a = new File("banned-players.json");
    public static final File b = new File("banned-ips.json");
    public static final File c = new File("ops.json");
    public static final File d = new File("whitelist.json");
    private static final Logger f = LogManager.getLogger();
    private static final SimpleDateFormat g = new SimpleDateFormat("yyyy-MM-dd \'at\' HH:mm:ss z");
    private final MinecraftServer server;
    public final List<EntityPlayer> players = new java.util.concurrent.CopyOnWriteArrayList(); // CraftBukkit - ArrayList -> CopyOnWriteArrayList: Iterator safety
    private final Map<UUID, EntityPlayer> j = Maps.newHashMap();
    private final GameProfileBanList k;
    private final IpBanList l;
    private final OpList operators;
    private final WhiteList whitelist;
    // CraftBukkit start
    // private final Map<UUID, ServerStatisticManager> o;
    // private final Map<UUID, AdvancementDataPlayer> p;
    // CraftBukkit end
    public IPlayerFileData playerFileData;
    private boolean hasWhitelist;
    protected int maxPlayers;
    private int s;
    private EnumGamemode t;
    private boolean u;
    private int v;

    // CraftBukkit start
    private CraftServer cserver;
    private final Map<String,EntityPlayer> playersByName = new org.spigotmc.CaseInsensitiveMap<EntityPlayer>();
    @Nullable String collideRuleTeamName; // Paper - Team name used for collideRule

    public PlayerList(MinecraftServer minecraftserver) {
        this.cserver = minecraftserver.server = new CraftServer(minecraftserver, this);
        minecraftserver.console = new com.destroystokyo.paper.console.TerminalConsoleCommandSender(); // Paper
        // CraftBukkit end

        this.k = new GameProfileBanList(PlayerList.a);
        this.l = new IpBanList(PlayerList.b);
        this.operators = new OpList(PlayerList.c);
        this.whitelist = new WhiteList(PlayerList.d);
        // CraftBukkit start
        // this.o = Maps.newHashMap();
        // this.p = Maps.newHashMap();
        // CraftBukkit end
        this.server = minecraftserver;
        this.k.a(false);
        this.l.a(false);
        this.maxPlayers = 8;
    }

    public void a(NetworkManager networkmanager, EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getProfile();
        UserCache usercache = this.server.getUserCache();
        GameProfile gameprofile1 = usercache.a(gameprofile.getId());
        String s = gameprofile1 == null ? gameprofile.getName() : gameprofile1.getName();

        usercache.a(gameprofile);
        NBTTagCompound nbttagcompound = this.a(entityplayer);
        // CraftBukkit start - Better rename detection
        if (nbttagcompound != null && nbttagcompound.hasKey("bukkit")) {
            NBTTagCompound bukkit = nbttagcompound.getCompound("bukkit");
            s = bukkit.hasKeyOfType("lastKnownName", 8) ? bukkit.getString("lastKnownName") : s;
        }
        // CraftBukkit end

        // Paper start - support PlayerInitialSpawnEvent
        Location originalLoc = new Location(entityplayer.world.getWorld(), entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
        com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent event = new com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent(entityplayer.getBukkitEntity(), originalLoc);
        this.server.server.getPluginManager().callEvent(event);

        Location newLoc = event.getSpawnLocation();
        entityplayer.world = ((CraftWorld) newLoc.getWorld()).getHandle();
        entityplayer.locX = newLoc.getX();
        entityplayer.locY = newLoc.getY();
        entityplayer.locZ = newLoc.getZ();
        entityplayer.yaw = newLoc.getYaw();
        entityplayer.pitch = newLoc.getPitch();
        entityplayer.dimension = ((CraftWorld) newLoc.getWorld()).getHandle().dimension;
        // Paper end

        entityplayer.spawnIn(this.server.getWorldServer(entityplayer.dimension));
        entityplayer.playerInteractManager.a((WorldServer) entityplayer.world);
        String s1 = "local";

        if (networkmanager.getSocketAddress() != null) {
            s1 = networkmanager.getSocketAddress().toString();
        }

        // Spigot start - spawn location event
        Player bukkitPlayer = entityplayer.getBukkitEntity();
        PlayerSpawnLocationEvent ev = new PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.getLocation());
        Bukkit.getPluginManager().callEvent(ev);

        Location loc = ev.getSpawnLocation();
        WorldServer world = ((CraftWorld) loc.getWorld()).getHandle();

        entityplayer.spawnIn(world);
        entityplayer.setPosition(loc.getX(), loc.getY(), loc.getZ());
        entityplayer.setYawPitch(loc.getYaw(), loc.getPitch()); 
        // Spigot end

        // CraftBukkit - Moved message to after join
        // PlayerList.f.info("{}[{}] logged in with entity id {} at ({}, {}, {})", entityplayer.getName(), s1, Integer.valueOf(entityplayer.getId()), Double.valueOf(entityplayer.locX), Double.valueOf(entityplayer.locY), Double.valueOf(entityplayer.locZ));
        WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);
        WorldData worlddata = worldserver.getWorldData();

        this.a(entityplayer, (EntityPlayer) null, worldserver);
        PlayerConnection playerconnection = new PlayerConnection(this.server, networkmanager, entityplayer);

        playerconnection.sendPacket(new PacketPlayOutLogin(entityplayer.getId(), entityplayer.playerInteractManager.getGameMode(), worlddata.isHardcore(), worldserver.worldProvider.getDimensionManager().getDimensionID(), worldserver.getDifficulty(), this.getMaxPlayers(), worlddata.getType(), worldserver.getGameRules().getBoolean("reducedDebugInfo")));
        entityplayer.getBukkitEntity().sendSupportedChannels(); // CraftBukkit
        playerconnection.sendPacket(new PacketPlayOutCustomPayload("MC|Brand", (new PacketDataSerializer(Unpooled.buffer())).a(this.getServer().getServerModName())));
        playerconnection.sendPacket(new PacketPlayOutServerDifficulty(worlddata.getDifficulty(), worlddata.isDifficultyLocked()));
        playerconnection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
        playerconnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
        playerconnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) (worldserver.getGameRules().getBoolean("reducedDebugInfo") ? 22 : 23))); // Paper - fix this rule not being initialized on the client
        this.f(entityplayer);
        entityplayer.getStatisticManager().c();
        entityplayer.F().a(entityplayer);
        this.sendScoreboard((ScoreboardServer) worldserver.getScoreboard(), entityplayer);
        this.server.aD();
        // CraftBukkit start - login message is handled in the event
        // ChatMessage chatmessage;

        String joinMessage;
        if (entityplayer.getName().equalsIgnoreCase(s)) {
            // chatmessage = new ChatMessage("multiplayer.player.joined", new Object[] { entityplayer.getScoreboardDisplayName()});
            joinMessage = LocaleI18n.a(AkarinGlobalConfig.messageJoin, entityplayer.getName()); // Akarin
        } else {
            // chatmessage = new ChatMessage("multiplayer.player.joined.renamed", new Object[] { entityplayer.getScoreboardDisplayName(), s});
            joinMessage = LocaleI18n.a(AkarinGlobalConfig.messageJoinRenamed, entityplayer.getName(), s); // Akarin
        }

        // chatmessage.getChatModifier().setColor(EnumChatFormat.YELLOW);
        // this.sendMessage(chatmessage);
        this.onPlayerJoin(entityplayer, joinMessage);
        // CraftBukkit end
        worldserver = server.getWorldServer(entityplayer.dimension);  // CraftBukkit - Update in case join event changed it
        playerconnection.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
        this.b(entityplayer, worldserver);
        if (!this.server.getResourcePack().isEmpty()) {
            entityplayer.setResourcePack(this.server.getResourcePack(), this.server.getResourcePackHash());
        }

        Iterator iterator = entityplayer.getEffects().iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            playerconnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), mobeffect));
        }

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("RootVehicle", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("RootVehicle");
            Entity entity = ChunkRegionLoader.a(nbttagcompound1.getCompound("Entity"), worldserver, true);

            if (entity != null) {
                UUID uuid = nbttagcompound1.a("Attach");
                Iterator iterator1;
                Entity entity1;

                if (entity.getUniqueID().equals(uuid)) {
                    entityplayer.a(entity, true);
                } else {
                    iterator1 = entity.bG().iterator();

                    while (iterator1.hasNext()) {
                        entity1 = (Entity) iterator1.next();
                        if (entity1.getUniqueID().equals(uuid)) {
                            entityplayer.a(entity1, true);
                            break;
                        }
                    }
                }

                if (!entityplayer.isPassenger()) {
                    PlayerList.f.warn("Couldn\'t reattach entity to player");
                    worldserver.removeEntity(entity);
                    iterator1 = entity.bG().iterator();

                    while (iterator1.hasNext()) {
                        entity1 = (Entity) iterator1.next();
                        worldserver.removeEntity(entity1);
                    }
                }
            }
        }

        entityplayer.syncInventory();
        // Paper start - Add to collideRule team if needed
        final Scoreboard scoreboard = this.getServer().getWorld().getScoreboard();
        if (this.collideRuleTeamName != null && scoreboard.getTeam(collideRuleTeamName) != null && entityplayer.getTeam() == null) {
            scoreboard.addPlayerToTeam(entityplayer.getName(), collideRuleTeamName);
        }
        // Paper end
        // CraftBukkit - Moved from above, added world
        PlayerList.f.info(entityplayer.getName() + "[" + s1 + "] logged in with entity id " + entityplayer.getId() + " at ([" + entityplayer.world.worldData.getName() + "]" + entityplayer.locX + ", " + entityplayer.locY + ", " + entityplayer.locZ + ")");
    }

    public void sendScoreboard(ScoreboardServer scoreboardserver, EntityPlayer entityplayer) {
        HashSet hashset = Sets.newHashSet();
        Iterator iterator = scoreboardserver.getTeams().iterator();

        while (iterator.hasNext()) {
            ScoreboardTeam scoreboardteam = (ScoreboardTeam) iterator.next();

            entityplayer.playerConnection.sendPacket(new PacketPlayOutScoreboardTeam(scoreboardteam, 0));
        }

        for (int i = 0; i < 19; ++i) {
            ScoreboardObjective scoreboardobjective = scoreboardserver.getObjectiveForSlot(i);

            if (scoreboardobjective != null && !hashset.contains(scoreboardobjective)) {
                List list = scoreboardserver.getScoreboardScorePacketsForObjective(scoreboardobjective);
                Iterator iterator1 = list.iterator();

                while (iterator1.hasNext()) {
                    Packet packet = (Packet) iterator1.next();

                    entityplayer.playerConnection.sendPacket(packet);
                }

                hashset.add(scoreboardobjective);
            }
        }

    }

    public void setPlayerFileData(WorldServer[] aworldserver) {
        if (playerFileData != null) return; // CraftBukkit
        this.playerFileData = aworldserver[0].getDataManager().getPlayerFileData();
        aworldserver[0].getWorldBorder().a(new IWorldBorderListener() {
            @Override
            public void a(WorldBorder worldborder, double d0) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE), worldborder.world);
            }

            @Override
            public void a(WorldBorder worldborder, double d0, double d1, long i) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE), worldborder.world);
            }

            @Override
            public void a(WorldBorder worldborder, double d0, double d1) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER), worldborder.world);
            }

            @Override
            public void a(WorldBorder worldborder, int i) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_TIME), worldborder.world);
            }

            @Override
            public void b(WorldBorder worldborder, int i) {
                PlayerList.this.sendAll(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS), worldborder.world);
            }

            @Override
            public void b(WorldBorder worldborder, double d0) {}

            @Override
            public void c(WorldBorder worldborder, double d0) {}
        });
    }

    public void a(EntityPlayer entityplayer, @Nullable WorldServer worldserver) {
        WorldServer worldserver1 = entityplayer.x();

        if (worldserver != null) {
            worldserver.getPlayerChunkMap().removePlayer(entityplayer);
        }

        worldserver1.getPlayerChunkMap().addPlayer(entityplayer);
        worldserver1.getChunkProviderServer().getChunkAt((int) entityplayer.locX >> 4, (int) entityplayer.locZ >> 4);
        if (worldserver != null) {
            CriterionTriggers.u.a(entityplayer, worldserver.worldProvider.getDimensionManager(), worldserver1.worldProvider.getDimensionManager());
            if (worldserver.worldProvider.getDimensionManager() == DimensionManager.NETHER && entityplayer.world.worldProvider.getDimensionManager() == DimensionManager.OVERWORLD && entityplayer.Q() != null) {
                CriterionTriggers.B.a(entityplayer, entityplayer.Q());
            }
        }

    }

    public int d() {
        return PlayerChunkMap.getFurthestViewableBlock(this.s());
    }

    @Nullable
    public NBTTagCompound a(EntityPlayer entityplayer) {
        NBTTagCompound nbttagcompound = this.server.worlds.get(0).getWorldData().h(); // CraftBukkit
        NBTTagCompound nbttagcompound1;

        if (entityplayer.getName().equals(this.server.Q()) && nbttagcompound != null) {
            nbttagcompound1 = nbttagcompound;
            entityplayer.f(nbttagcompound);
            PlayerList.f.debug("loading single player");
        } else {
            nbttagcompound1 = this.playerFileData.load(entityplayer);
        }

        return nbttagcompound1;
    }

    protected void savePlayerFile(EntityPlayer entityplayer) {
        entityplayer.lastSave = MinecraftServer.currentTick; // Paper
        this.playerFileData.save(entityplayer);
        ServerStatisticManager serverstatisticmanager = entityplayer.getStatisticManager(); // CraftBukkit

        if (serverstatisticmanager != null) {
            serverstatisticmanager.b();
        }

        AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancementData(); // CraftBukkit

        if (advancementdataplayer != null) {
            advancementdataplayer.c();
        }

    }

    public void onPlayerJoin(EntityPlayer entityplayer, String joinMessage) { // CraftBukkit added param
        this.players.add(entityplayer);
        this.playersByName.put(entityplayer.getName(), entityplayer); // Spigot
        this.j.put(entityplayer.getUniqueID(), entityplayer);
        // this.sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { entityplayer})); // CraftBukkit - replaced with loop below
        WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);

        // CraftBukkit start
        PlayerJoinEvent playerJoinEvent = new PlayerJoinEvent(cserver.getPlayer(entityplayer), joinMessage);
        cserver.getPluginManager().callEvent(playerJoinEvent);

        if (!entityplayer.playerConnection.networkManager.isConnected()) {
            return;
        }

        joinMessage = playerJoinEvent.getJoinMessage();

        if (joinMessage != null && joinMessage.length() > 0) {
            for (IChatBaseComponent line : org.bukkit.craftbukkit.util.CraftChatMessage.fromString(joinMessage)) {
                server.getPlayerList().sendAll(new PacketPlayOutChat(line));
            }
        }

        ChunkIOExecutor.adjustPoolSize(getPlayerCount());
        // CraftBukkit end

        // CraftBukkit start - sendAll above replaced with this loop
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityplayer);

        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer1 = this.players.get(i);

            if (entityplayer1.getBukkitEntity().canSee(entityplayer.getBukkitEntity())) {
                entityplayer1.playerConnection.sendPacket(packet);
            }

            if (!entityplayer.getBukkitEntity().canSee(entityplayer1.getBukkitEntity())) {
                continue;
            }

            entityplayer.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, new EntityPlayer[] { entityplayer1}));
        }
        entityplayer.sentListPacket = true;
        // CraftBukkit end

        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(entityplayer.getId(), entityplayer.datawatcher, true)); // CraftBukkit - BungeeCord#2321, send complete data to self on spawn

        // CraftBukkit start - Only add if the player wasn't moved in the event
        if (entityplayer.world == worldserver && !worldserver.players.contains(entityplayer)) {
            worldserver.addEntity(entityplayer);
            this.a(entityplayer, (WorldServer) null);
        }
        // CraftBukkit end
    }

    public void d(EntityPlayer entityplayer) {
        entityplayer.x().getPlayerChunkMap().movePlayer(entityplayer);
    }

    public String disconnect(EntityPlayer entityplayer) { // CraftBukkit - return string
        WorldServer worldserver = entityplayer.x();

        entityplayer.b(StatisticList.f);

        // CraftBukkit start - Quitting must be before we do final save of data, in case plugins need to modify it
        org.bukkit.craftbukkit.event.CraftEventFactory.handleInventoryCloseEvent(entityplayer, org.bukkit.event.inventory.InventoryCloseEvent.Reason.DISCONNECT); // Paper

        PlayerQuitEvent playerQuitEvent = new PlayerQuitEvent(cserver.getPlayer(entityplayer), LocaleI18n.a(AkarinGlobalConfig.messagePlayerQuit, entityplayer.getName())); // Akarin - use I18n to replace name
        cserver.getPluginManager().callEvent(playerQuitEvent);
        entityplayer.getBukkitEntity().disconnect(playerQuitEvent.getQuitMessage());

        
        entityplayer.playerTick();// SPIGOT-924
        // CraftBukkit end

        // Paper start - Remove from collideRule team if needed
        if (this.collideRuleTeamName != null) {
            final Scoreboard scoreBoard = this.server.getWorld().getScoreboard();
            final ScoreboardTeam team = scoreBoard.getTeam(this.collideRuleTeamName);
            if (entityplayer.getTeam() == team && team != null) {
                scoreBoard.removePlayerFromTeam(entityplayer.getName(), team);
            }
        }
        // Paper end

        this.savePlayerFile(entityplayer);
        if (entityplayer.isPassenger()) {
            Entity entity = entityplayer.getVehicle();

            if (entity.b(EntityPlayer.class).size() == 1) {
                PlayerList.f.debug("Removing player mount");
                entityplayer.stopRiding();
                worldserver.removeEntity(entity);
                Iterator iterator = entity.bG().iterator();

                while (iterator.hasNext()) {
                    Entity entity1 = (Entity) iterator.next();

                    worldserver.removeEntity(entity1);
                }

                worldserver.getChunkAt(entityplayer.ab, entityplayer.ad).markDirty();
            }
        }

        worldserver.kill(entityplayer);
        worldserver.getPlayerChunkMap().removePlayer(entityplayer);
        entityplayer.getAdvancementData().a();
        this.players.remove(entityplayer);
        this.playersByName.remove(entityplayer.getName()); // Spigot
        UUID uuid = entityplayer.getUniqueID();
        EntityPlayer entityplayer1 = this.j.get(uuid);

        if (entityplayer1 == entityplayer) {
            this.j.remove(uuid);
            // CraftBukkit start
            // this.o.remove(uuid);
            // this.p.remove(uuid);
            // CraftBukkit end
        }

        // CraftBukkit start
        //  this.sendAll(new PacketPlayOutPlayerInfo(EnumPlayerInfoAction.REMOVE_PLAYER, new EntityPlayer[] { entityplayer}));
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityplayer);
        for (int i = 0; i < players.size(); i++) {
            EntityPlayer entityplayer2 = this.players.get(i);

            if (entityplayer2.getBukkitEntity().canSee(entityplayer.getBukkitEntity())) {
                entityplayer2.playerConnection.sendPacket(packet);
            } else {
                entityplayer2.getBukkitEntity().removeDisconnectingPlayer(entityplayer.getBukkitEntity());
            }
        }
        // This removes the scoreboard (and player reference) for the specific player in the manager
        cserver.getScoreboardManager().removePlayer(entityplayer.getBukkitEntity());
        // CraftBukkit end

        ChunkIOExecutor.adjustPoolSize(this.getPlayerCount()); // CraftBukkit

        return playerQuitEvent.getQuitMessage(); // CraftBukkit
    }

    // CraftBukkit start - Whole method, SocketAddress to LoginListener, added hostname to signature, return EntityPlayer
    public EntityPlayer attemptLogin(LoginListener loginlistener, GameProfile gameprofile, String hostname) {
        // Moved from processLogin
        UUID uuid = EntityHuman.a(gameprofile);
        ArrayList arraylist = Lists.newArrayList();

        EntityPlayer entityplayer;

        for (int i = 0; i < this.players.size(); ++i) {
            entityplayer = this.players.get(i);
            if (entityplayer.getUniqueID().equals(uuid)) {
                arraylist.add(entityplayer);
            }
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            entityplayer = (EntityPlayer) iterator.next();
            savePlayerFile(entityplayer); // CraftBukkit - Force the player's inventory to be saved
            entityplayer.playerConnection.disconnect(AkarinGlobalConfig.messageDupLogin); // Akarin
        }

        // Instead of kicking then returning, we need to store the kick reason
        // in the event, check with plugins to see if it's ok, and THEN kick
        // depending on the outcome.
        SocketAddress socketaddress = loginlistener.networkManager.getSocketAddress();

        EntityPlayer entity = new EntityPlayer(server, server.getWorldServer(0), gameprofile, new PlayerInteractManager(server.getWorldServer(0)));
        Player player = entity.getBukkitEntity();
        PlayerLoginEvent event = new PlayerLoginEvent(player, hostname, ((java.net.InetSocketAddress) socketaddress).getAddress(), ((java.net.InetSocketAddress) loginlistener.networkManager.getRawAddress()).getAddress());
        String s;

        if (getProfileBans().isBanned(gameprofile) && !getProfileBans().get(gameprofile).hasExpired()) {
            GameProfileBanEntry gameprofilebanentry = this.k.get(gameprofile);

            s = LocaleI18n.a(AkarinGlobalConfig.messageBan,
                    gameprofilebanentry.getReason().equals(Akari.EMPTY_STRING) ? Akari.EMPTY_STRING : AkarinGlobalConfig.messageBanReason + gameprofilebanentry.getReason(),
                    gameprofilebanentry.getExpires() == null ? Akari.EMPTY_STRING : AkarinGlobalConfig.messageBanExpires + PlayerList.g.format(gameprofilebanentry.getExpires())); // Akarin - modify message
            
            // return s;
            if (!gameprofilebanentry.hasExpired()) event.disallow(PlayerLoginEvent.Result.KICK_BANNED, s); // Spigot
        } else if (!this.isWhitelisted(gameprofile, event)) { // Paper
            // return "You are not white-listed on this server!";
            //event.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, org.spigotmc.SpigotConfig.whitelistMessage); // Spigot // Paper - moved to isWhitelisted
        } else if (getIPBans().isBanned(socketaddress) && !getIPBans().get(socketaddress).hasExpired()) {
            IpBanEntry ipbanentry = this.l.get(socketaddress);

            s = LocaleI18n.a(AkarinGlobalConfig.messageBan,
                    ipbanentry.getReason().equals(Akari.EMPTY_STRING) ? Akari.EMPTY_STRING : AkarinGlobalConfig.messageBanReason + ipbanentry.getReason(),
                    ipbanentry.getExpires() == null ? Akari.EMPTY_STRING : AkarinGlobalConfig.messageBanExpires + PlayerList.g.format(ipbanentry.getExpires())); // Akarin - modify message
            
            // return s;
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, s);
        } else {
            // return this.players.size() >= this.maxPlayers && !this.f(gameprofile) ? "The server is full!" : null;
            if (this.players.size() >= this.maxPlayers && !this.f(gameprofile)) {
                event.disallow(PlayerLoginEvent.Result.KICK_FULL, org.spigotmc.SpigotConfig.serverFullMessage); // Spigot
            }
        }

        cserver.getPluginManager().callEvent(event);
        if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
            loginlistener.disconnect(event.getKickMessage());
            return null;
        }
        return entity;
    }

    public EntityPlayer processLogin(GameProfile gameprofile, EntityPlayer player) { // CraftBukkit - added EntityPlayer
        /* CraftBukkit startMoved up
        UUID uuid = EntityHuman.a(gameprofile);
        ArrayList arraylist = Lists.newArrayList();

        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

            if (entityplayer.getUniqueID().equals(uuid)) {
                arraylist.add(entityplayer);
            }
        }

        EntityPlayer entityplayer1 = (EntityPlayer) this.j.get(gameprofile.getId());

        if (entityplayer1 != null && !arraylist.contains(entityplayer1)) {
            arraylist.add(entityplayer1);
        }

        Iterator iterator = arraylist.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer2 = (EntityPlayer) iterator.next();

            entityplayer2.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.duplicate_login", new Object[0]));
        }

        Object object;

        if (this.server.V()) {
            object = new DemoPlayerInteractManager(this.server.getWorldServer(0));
        } else {
            object = new PlayerInteractManager(this.server.getWorldServer(0));
        }

        return new EntityPlayer(this.server, this.server.getWorldServer(0), gameprofile, (PlayerInteractManager) object);
        */
        return player;
        // CraftBukkit end
    }

    // CraftBukkit start
    public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, boolean flag) {
        return this.moveToWorld(entityplayer, i, flag, null, true);
    }

    public EntityPlayer moveToWorld(EntityPlayer entityplayer, int i, boolean flag, Location location, boolean avoidSuffocation) {
        entityplayer.stopRiding(); // CraftBukkit
        entityplayer.x().getTracker().untrackPlayer(entityplayer);
        // entityplayer.x().getTracker().untrackEntity(entityplayer); // CraftBukkit
        entityplayer.x().getPlayerChunkMap().removePlayer(entityplayer);
        this.players.remove(entityplayer);
        this.playersByName.remove(entityplayer.getName()); // Spigot
        this.server.getWorldServer(entityplayer.dimension).removeEntity(entityplayer);
        BlockPosition blockposition = entityplayer.getBed();
        boolean flag1 = entityplayer.isRespawnForced();

        /* CraftBukkit start
        entityplayer.dimension = i;
        Object object;

        if (this.server.V()) {
            object = new DemoPlayerInteractManager(this.server.getWorldServer(entityplayer.dimension));
        } else {
            object = new PlayerInteractManager(this.server.getWorldServer(entityplayer.dimension));
        }

        EntityPlayer entityplayer1 = new EntityPlayer(this.server, this.server.getWorldServer(entityplayer.dimension), entityplayer.getProfile(), (PlayerInteractManager) object);
        // */
        EntityPlayer entityplayer1 = entityplayer;
        org.bukkit.World fromWorld = entityplayer.getBukkitEntity().getWorld();
        entityplayer.viewingCredits = false;
        // CraftBukkit end

        entityplayer1.playerConnection = entityplayer.playerConnection;
        entityplayer1.copyFrom(entityplayer, flag);
        entityplayer1.h(entityplayer.getId());
        entityplayer1.v(entityplayer);
        entityplayer1.a(entityplayer.getMainHand());
        Iterator iterator = entityplayer.getScoreboardTags().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            entityplayer1.addScoreboardTag(s);
        }

        // WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);  // CraftBukkit - handled later

        // this.a(entityplayer1, entityplayer, worldserver); // CraftBukkit - removed
        BlockPosition blockposition1;

        // CraftBukkit start - fire PlayerRespawnEvent
        if (location == null) {
            boolean isBedSpawn = false;
            CraftWorld cworld = (CraftWorld) this.server.server.getWorld(entityplayer.spawnWorld);
            if (cworld != null && blockposition != null) {
                blockposition1 = EntityHuman.getBed(cworld.getHandle(), blockposition, flag1);
                if (blockposition1 != null) {
                    isBedSpawn = true;
                    location = new Location(cworld, blockposition1.getX() + 0.5F, blockposition1.getY() + 0.1F, blockposition1.getZ() + 0.5F);
                } else {
                    entityplayer1.setRespawnPosition(null, true);
                    entityplayer1.playerConnection.sendPacket(new PacketPlayOutGameStateChange(0, 0.0F));
                }
            }

            if (location == null) {
                cworld = (CraftWorld) this.server.server.getWorlds().get(0);
                blockposition = entityplayer1.getSpawnPoint(this.server, cworld.getHandle());
                location = new Location(cworld, blockposition.getX() + 0.5F, blockposition.getY() + 0.1F, blockposition.getZ() + 0.5F);
            }

            Player respawnPlayer = cserver.getPlayer(entityplayer1);
            PlayerRespawnEvent respawnEvent = new PlayerRespawnEvent(respawnPlayer, location, isBedSpawn);
            cserver.getPluginManager().callEvent(respawnEvent);
            // Spigot Start
            if (entityplayer.playerConnection.isDisconnected()) {
                return entityplayer;
            }
            // Spigot End

            location = respawnEvent.getRespawnLocation();
            entityplayer.reset();
        } else {
            location.setWorld(server.getWorldServer(i).getWorld());
        }
        WorldServer worldserver = ((CraftWorld) location.getWorld()).getHandle();
        entityplayer1.forceSetPositionRotation(location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
        // CraftBukkit end

        worldserver.getChunkProviderServer().getChunkAt((int) entityplayer1.locX >> 4, (int) entityplayer1.locZ >> 4);

        while (avoidSuffocation && !worldserver.getCubes(entityplayer1, entityplayer1.getBoundingBox()).isEmpty() && entityplayer1.locY < 256.0D) {
            entityplayer1.setPosition(entityplayer1.locX, entityplayer1.locY + 1.0D, entityplayer1.locZ);
        }
        // CraftBukkit start
        byte actualDimension = (byte) (worldserver.getWorld().getEnvironment().getId());
        // Force the client to refresh their chunk cache
        if (fromWorld.getEnvironment() == worldserver.getWorld().getEnvironment()) {
            entityplayer1.playerConnection.sendPacket(new PacketPlayOutRespawn((byte) (actualDimension >= 0 ? -1 : 0), worldserver.getDifficulty(), worldserver.getWorldData().getType(), entityplayer.playerInteractManager.getGameMode()));
        }

        entityplayer1.playerConnection.sendPacket(new PacketPlayOutRespawn(actualDimension, worldserver.getDifficulty(), worldserver.getWorldData().getType(), entityplayer1.playerInteractManager.getGameMode()));
        entityplayer1.spawnIn(worldserver);
        entityplayer1.dead = false;
        entityplayer1.playerConnection.teleport(new Location(worldserver.getWorld(), entityplayer1.locX, entityplayer1.locY, entityplayer1.locZ, entityplayer1.yaw, entityplayer1.pitch));
        entityplayer1.setSneaking(false);
        blockposition1 = worldserver.getSpawn();
        // entityplayer1.playerConnection.a(entityplayer1.locX, entityplayer1.locY, entityplayer1.locZ, entityplayer1.yaw, entityplayer1.pitch);
        entityplayer1.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(blockposition1));
        entityplayer1.playerConnection.sendPacket(new PacketPlayOutExperience(entityplayer1.exp, entityplayer1.expTotal, entityplayer1.expLevel));
        this.b(entityplayer1, worldserver);
        this.f(entityplayer1);
        if (!entityplayer.playerConnection.isDisconnected()) {
            worldserver.getPlayerChunkMap().addPlayer(entityplayer1);
            worldserver.addEntity(entityplayer1);
            this.players.add(entityplayer1);
            this.playersByName.put(entityplayer1.getName(), entityplayer1); // Spigot
            this.j.put(entityplayer1.getUniqueID(), entityplayer1);
        }
        // entityplayer1.syncInventory();
        entityplayer1.setHealth(entityplayer1.getHealth());
        // Added from changeDimension
        updateClient(entityplayer); // Update health, etc...
        entityplayer.updateAbilities();
        for (Object o1 : entityplayer.getEffects()) {
            MobEffect mobEffect = (MobEffect) o1;
            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), mobEffect));
        }

        // Fire advancement trigger
        CriterionTriggers.u.a(entityplayer, ((CraftWorld) fromWorld).getHandle().worldProvider.getDimensionManager(), worldserver.worldProvider.getDimensionManager());
        if (((CraftWorld) fromWorld).getHandle().worldProvider.getDimensionManager() == DimensionManager.NETHER && worldserver.worldProvider.getDimensionManager() == DimensionManager.OVERWORLD && entityplayer.Q() != null) {
            CriterionTriggers.B.a(entityplayer, entityplayer.Q());
        }

        // Don't fire on respawn
        if (fromWorld != location.getWorld()) {
            PlayerChangedWorldEvent event = new PlayerChangedWorldEvent(entityplayer.getBukkitEntity(), fromWorld);
            server.server.getPluginManager().callEvent(event);
        }

        // Save player file again if they were disconnected
        if (entityplayer.playerConnection.isDisconnected()) {
            this.savePlayerFile(entityplayer);
        }
        // CraftBukkit end
        return entityplayer1;
    }

    // CraftBukkit start - Replaced the standard handling of portals with a more customised method.
    public void changeDimension(EntityPlayer entityplayer, int i, TeleportCause cause) {
        WorldServer exitWorld = null;
        if (entityplayer.dimension < CraftWorld.CUSTOM_DIMENSION_OFFSET) { // plugins must specify exit from custom Bukkit worlds
            // only target existing worlds (compensate for allow-nether/allow-end as false)
            for (WorldServer world : this.server.worlds) {
                if (world.dimension == i) {
                    exitWorld = world;
                }
            }
        }

        Location enter = entityplayer.getBukkitEntity().getLocation();
        Location exit = null;
        boolean useTravelAgent = false; // don't use agent for custom worlds or return from THE_END
        if (exitWorld != null) {
            if ((cause == TeleportCause.END_PORTAL) && (i == 0)) {
                // THE_END -> NORMAL; use bed if available, otherwise default spawn
                exit = entityplayer.getBukkitEntity().getBedSpawnLocation();
                if (exit == null || ((CraftWorld) exit.getWorld()).getHandle().dimension != 0) {
                    BlockPosition randomSpawn = entityplayer.getSpawnPoint(server, exitWorld);
                    exit = new Location(exitWorld.getWorld(), randomSpawn.getX(), randomSpawn.getY(), randomSpawn.getZ());
                } else {
                    exit = exit.add(0.5F, 0.1F, 0.5F); // SPIGOT-3879
                }
            } else {
                // NORMAL <-> NETHER or NORMAL -> THE_END
                exit = this.calculateTarget(enter, exitWorld);
                useTravelAgent = true;
            }
        }

        TravelAgent agent = exit != null ? (TravelAgent) ((CraftWorld) exit.getWorld()).getHandle().getTravelAgent() : org.bukkit.craftbukkit.CraftTravelAgent.DEFAULT; // return arbitrary TA to compensate for implementation dependent plugins
        PlayerPortalEvent event = new PlayerPortalEvent(entityplayer.getBukkitEntity(), enter, exit, agent, cause);
        event.useTravelAgent(useTravelAgent);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled() || event.getTo() == null) {
            return;
        }

        exit = event.useTravelAgent() ? event.getPortalTravelAgent().findOrCreate(event.getTo()) : event.getTo();
        if (exit == null) {
            return;
        }
        exitWorld = ((CraftWorld) exit.getWorld()).getHandle();

        org.bukkit.event.player.PlayerTeleportEvent tpEvent = new org.bukkit.event.player.PlayerTeleportEvent(entityplayer.getBukkitEntity(), enter, exit, cause);
        Bukkit.getServer().getPluginManager().callEvent(tpEvent);
        if (tpEvent.isCancelled() || tpEvent.getTo() == null) {
            return;
        }

        Vector velocity = entityplayer.getBukkitEntity().getVelocity();
        exitWorld.getTravelAgent().adjustExit(entityplayer, exit, velocity);

        entityplayer.worldChangeInvuln = true; // CraftBukkit - Set teleport invulnerability only if player changing worlds
        this.moveToWorld(entityplayer, exitWorld.dimension, true, exit, true); // SPIGOT-3864
        if (entityplayer.motX != velocity.getX() || entityplayer.motY != velocity.getY() || entityplayer.motZ != velocity.getZ()) {
            entityplayer.getBukkitEntity().setVelocity(velocity);
        }
    }

    public void f(EntityPlayer entityplayer) {
        GameProfile gameprofile = entityplayer.getProfile();
        int i = this.isOp(gameprofile) ? this.operators.a(gameprofile) : 0;

        i = this.server.R() && this.server.worldServer[0].getWorldData().u() ? 4 : i;
        i = this.u ? 4 : i;
        this.b(entityplayer, i);
    }

    public void a(EntityPlayer entityplayer, int i) {
        int j = entityplayer.dimension;
        WorldServer worldserver = this.server.getWorldServer(entityplayer.dimension);

        entityplayer.dimension = i;
        WorldServer worldserver1 = this.server.getWorldServer(entityplayer.dimension);

        entityplayer.playerConnection.sendPacket(new PacketPlayOutRespawn(entityplayer.dimension, entityplayer.world.getDifficulty(), entityplayer.world.getWorldData().getType(), entityplayer.playerInteractManager.getGameMode()));
        this.f(entityplayer);
        worldserver.removeEntity(entityplayer);
        entityplayer.dead = false;
        this.changeWorld(entityplayer, j, worldserver, worldserver1);
        this.a(entityplayer, worldserver);
        entityplayer.playerConnection.a(entityplayer.locX, entityplayer.locY, entityplayer.locZ, entityplayer.yaw, entityplayer.pitch);
        entityplayer.playerInteractManager.a(worldserver1);
        entityplayer.playerConnection.sendPacket(new PacketPlayOutAbilities(entityplayer.abilities));
        this.b(entityplayer, worldserver1);
        this.updateClient(entityplayer);
        Iterator iterator = entityplayer.getEffects().iterator();

        while (iterator.hasNext()) {
            MobEffect mobeffect = (MobEffect) iterator.next();

            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(entityplayer.getId(), mobeffect));
        }

    }

    public void changeWorld(Entity entity, int i, WorldServer worldserver, WorldServer worldserver1) {
        // CraftBukkit start - Split into modular functions
        Location exit = calculateTarget(entity.getBukkitEntity().getLocation(), worldserver1);
        repositionEntity(entity, exit, true);
    }

    // Copy of original changeWorld(Entity, int, WorldServer, WorldServer) method with only location calculation logic
    public Location calculateTarget(Location enter, World target) {
        WorldServer worldserver = ((CraftWorld) enter.getWorld()).getHandle();
        WorldServer worldserver1 = target.getWorld().getHandle();
        int i = worldserver.dimension;

        double y = enter.getY();
        float yaw = enter.getYaw();
        float pitch = enter.getPitch();
        double d0 = enter.getX();
        double d1 = enter.getZ();
         double d2 = 8.0D;
        /*
        double d0 = entity.locX;
        double d1 = entity.locZ;
        double d2 = 8.0D;
        float f = entity.yaw;
        */

        worldserver.methodProfiler.a("moving");
        if (worldserver1.dimension == -1) {
            d0 = MathHelper.a(d0 / d2, worldserver1.getWorldBorder().b()+ 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 / d2, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
            /*
            entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
            */
        } else if (worldserver1.dimension == 0) {
            d0 = MathHelper.a(d0 * d2, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 * d2, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
            /*
            entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
            */
        } else {
            BlockPosition blockposition;

            if (i == 1) {
                // use default NORMAL world spawn instead of target
                worldserver1 = this.server.worlds.get(0);
                blockposition = worldserver1.getSpawn();
            } else {
                blockposition = worldserver1.getDimensionSpawn();
            }

            d0 = blockposition.getX();
            y = blockposition.getY();
            d1 = blockposition.getZ();
            /*
            entity.setPositionRotation(d0, entity.locY, d1, 90.0F, 0.0F);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
            */
        }

        worldserver.methodProfiler.b();
        if (i != 1) {
            worldserver.methodProfiler.a("placing");
            d0 = MathHelper.clamp((int) d0, -29999872, 29999872);
            d1 = MathHelper.clamp((int) d1, -29999872, 29999872);
            /*
            if (entity.isAlive()) {
                entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
                worldserver1.getTravelAgent().a(entity, f);
                worldserver1.addEntity(entity);
                worldserver1.entityJoinedWorld(entity, false);
            }
            */

            worldserver.methodProfiler.b();
        }

        // entity.spawnIn(worldserver1);
        return new Location(worldserver1.getWorld(), d0, y, d1, yaw, pitch);
    }

    // copy of original a(Entity, int, WorldServer, WorldServer) method with only entity repositioning logic
    public void repositionEntity(Entity entity, Location exit, boolean portal) {
        WorldServer worldserver = (WorldServer) entity.world;
        WorldServer worldserver1 = ((CraftWorld) exit.getWorld()).getHandle();
        int i = worldserver.dimension;

        /*
        double d0 = entity.locX;
        double d1 = entity.locZ;
        double d2 = 8.0D;
        float f = entity.yaw;
        */

        worldserver.methodProfiler.a("moving");
        entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
        if (entity.isAlive()) {
            worldserver.entityJoinedWorld(entity, false);
        }
        /*
        if (entity.dimension == -1) {
            d0 = MathHelper.a(d0 / 8.0D, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 / 8.0D, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
            entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
        } else if (entity.dimension == 0) {
            d0 = MathHelper.a(d0 * 8.0D, worldserver1.getWorldBorder().b() + 16.0D, worldserver1.getWorldBorder().d() - 16.0D);
            d1 = MathHelper.a(d1 * 8.0D, worldserver1.getWorldBorder().c() + 16.0D, worldserver1.getWorldBorder().e() - 16.0D);
            entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
        } else {
            BlockPosition blockposition;

            if (i == 1) {
                // use default NORMAL world spawn instead of target
                worldserver1 = this.server.worlds.get(0);
                blockposition = worldserver1.getSpawn();
            } else {
                blockposition = worldserver1.getDimensionSpawn();
            }

            d0 = (double) blockposition.getX();
            entity.locY = (double) blockposition.getY();
            d1 = (double) blockposition.getZ();
            entity.setPositionRotation(d0, entity.locY, d1, 90.0F, 0.0F);
            if (entity.isAlive()) {
                worldserver.entityJoinedWorld(entity, false);
            }
        }
        */

        worldserver.methodProfiler.b();
        if (i != 1) {
            worldserver.methodProfiler.a("placing");
            /*
            d0 = (double) MathHelper.clamp((int) d0, -29999872, 29999872);
            d1 = (double) MathHelper.clamp((int) d1, -29999872, 29999872);
            */
            if (entity.isAlive()) {
                // entity.setPositionRotation(d0, entity.locY, d1, entity.yaw, entity.pitch);
                // worldserver1.getTravelAgent().a(entity, f);
                if (portal) {
                    Vector velocity = entity.getBukkitEntity().getVelocity();
                    worldserver1.getTravelAgent().adjustExit(entity, exit, velocity);
                    entity.setPositionRotation(exit.getX(), exit.getY(), exit.getZ(), exit.getYaw(), exit.getPitch());
                    if (entity.motX != velocity.getX() || entity.motY != velocity.getY() || entity.motZ != velocity.getZ()) {
                        entity.getBukkitEntity().setVelocity(velocity);
                    }
                }
                // worldserver1.addEntity(entity);
                worldserver1.entityJoinedWorld(entity, false);
            }

            worldserver.methodProfiler.b();
        }

        entity.spawnIn(worldserver1);
        // CraftBukkit end
    }

    public void tick() {
        if (++this.v > 600) {
            // CraftBukkit start
            for (int i = 0; i < this.players.size(); ++i) {
                final EntityPlayer target = this.players.get(i);

                target.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, Iterables.filter(this.players, new Predicate<EntityPlayer>() {
                    @Override
                    public boolean apply(EntityPlayer input) {
                        return target.getBukkitEntity().canSee(input.getBukkitEntity());
                    }
                })));
            }
            // CraftBukkit end
            this.v = 0;
        }

    }

    public void sendAll(Packet<?> packet) {
        for (int i = 0; i < this.players.size(); ++i) {
            this.players.get(i).playerConnection.sendPacket(packet);
        }

    }

    // CraftBukkit start - add a world/entity limited version
    public void sendAll(Packet packet, EntityHuman entityhuman) {
        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer =  this.players.get(i);
            if (entityhuman != null && entityhuman instanceof EntityPlayer && !entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity())) {
                continue;
            }
            this.players.get(i).playerConnection.sendPacket(packet);
        }
    }

    public void sendAll(Packet packet, World world) {
        for (int i = 0; i < world.players.size(); ++i) {
            ((EntityPlayer) world.players.get(i)).playerConnection.sendPacket(packet);
        }

    }
    // CraftBukkit end

    public void a(Packet<?> packet, int i) {
        for (int j = 0; j < this.players.size(); ++j) {
            EntityPlayer entityplayer = this.players.get(j);

            if (entityplayer.dimension == i) {
                entityplayer.playerConnection.sendPacket(packet);
            }
        }

    }

    public void a(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.aY();

        if (scoreboardteambase != null) {
            Collection collection = scoreboardteambase.getPlayerNameSet();
            Iterator iterator = collection.iterator();

            while (iterator.hasNext()) {
                String s = (String) iterator.next();
                EntityPlayer entityplayer = this.getPlayer(s);

                if (entityplayer != null && entityplayer != entityhuman) {
                    entityplayer.sendMessage(ichatbasecomponent);
                }
            }

        }
    }

    public void b(EntityHuman entityhuman, IChatBaseComponent ichatbasecomponent) {
        ScoreboardTeamBase scoreboardteambase = entityhuman.aY();

        if (scoreboardteambase == null) {
            this.sendMessage(ichatbasecomponent);
        } else {
            for (int i = 0; i < this.players.size(); ++i) {
                EntityPlayer entityplayer = this.players.get(i);

                if (entityplayer.aY() != scoreboardteambase) {
                    entityplayer.sendMessage(ichatbasecomponent);
                }
            }

        }
    }

    public String b(boolean flag) {
        String s = "";
        ArrayList arraylist = Lists.newArrayList(this.players);

        for (int i = 0; i < arraylist.size(); ++i) {
            if (i > 0) {
                s = s + ", ";
            }

            s = s + ((EntityPlayer) arraylist.get(i)).getName();
            if (flag) {
                s = s + " (" + ((EntityPlayer) arraylist.get(i)).bn() + ")";
            }
        }

        return s;
    }

    public String[] f() {
        String[] astring = new String[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i) {
            astring[i] = this.players.get(i).getName();
        }

        return astring;
    }

    public GameProfile[] g() {
        GameProfile[] agameprofile = new GameProfile[this.players.size()];

        for (int i = 0; i < this.players.size(); ++i) {
            agameprofile[i] = this.players.get(i).getProfile();
        }

        return agameprofile;
    }

    public GameProfileBanList getProfileBans() {
        return this.k;
    }

    public IpBanList getIPBans() {
        return this.l;
    }

    public void addOp(GameProfile gameprofile) {
        int i = this.server.q();

        this.operators.add(new OpListEntry(gameprofile, this.server.q(), this.operators.b(gameprofile)));
        this.b(this.a(gameprofile.getId()), i);
        // CraftBukkit start
        Player player = server.server.getPlayer(gameprofile.getId());
        if (player != null) {
           player.recalculatePermissions();
        }
        // CraftBukkit end
    }

    public void removeOp(GameProfile gameprofile) {
        this.operators.remove(gameprofile);
        this.b(this.a(gameprofile.getId()), 0);
        // CraftBukkit start
        Player player = server.server.getPlayer(gameprofile.getId());
        if (player != null) {
            player.recalculatePermissions();
        }
        // CraftBukkit end
    }

    private void b(EntityPlayer entityplayer, int i) {
        if (entityplayer != null && entityplayer.playerConnection != null) {
            byte b0;

            if (i <= 0) {
                b0 = 24;
            } else if (i >= 4) {
                b0 = 28;
            } else {
                b0 = (byte) (24 + i);
            }

            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, b0));
        }

    }

    // Paper start
    public boolean isWhitelisted(GameProfile gameprofile) {
        return isWhitelisted(gameprofile, null);
    }
    public boolean isWhitelisted(GameProfile gameprofile, org.bukkit.event.player.PlayerLoginEvent loginEvent) {
        boolean isOp = this.operators.d(gameprofile);
        boolean isWhitelisted = !this.hasWhitelist || isOp || this.whitelist.d(gameprofile);
        final com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent event;
        event = new com.destroystokyo.paper.event.profile.ProfileWhitelistVerifyEvent(MCUtil.toBukkit(gameprofile), this.hasWhitelist, isWhitelisted, isOp, org.spigotmc.SpigotConfig.whitelistMessage);
        event.callEvent();
        if (!event.isWhitelisted()) {
            if (loginEvent != null) {
                loginEvent.disallow(PlayerLoginEvent.Result.KICK_WHITELIST, event.getKickMessage() == null ? org.spigotmc.SpigotConfig.whitelistMessage : event.getKickMessage());
            }
            return false;
        }
        return true;
    }
    // Paper end

    public boolean isOp(GameProfile gameprofile) {
        return this.operators.d(gameprofile) || this.server.R() && this.server.worlds.get(0).getWorldData().u() && this.server.Q().equalsIgnoreCase(gameprofile.getName()) || this.u; // CraftBukkit
    }

    @Nullable
    public EntityPlayer getPlayer(String s) {
        return this.playersByName.get(s); // Spigot
    }

    public void sendPacketNearby(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, double d3, int i, Packet<?> packet) {
     // Paper start - Use world list instead of server list where preferable
        sendPacketNearby(entityhuman, d0, d1, d2, d3, i, null, packet); // Retained for compatibility
    }

    public void sendPacketNearby(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, double d3, WorldServer world, Packet<?> packet) {
        sendPacketNearby(entityhuman, d0, d1, d2, d3, world.dimension, world, packet);
    }

    public void sendPacketNearby(@Nullable EntityHuman entityhuman, double d0, double d1, double d2, double d3, int i, @Nullable WorldServer world, Packet<?> packet) {
        if (world == null && entityhuman != null && entityhuman.world instanceof WorldServer) {
            world = (WorldServer) entityhuman.world;
        }

        List<? extends EntityHuman> players1 = world == null ? players : world.players;
        for (int j = 0; j < players1.size(); ++j) {
            EntityHuman entity = players1.get(j);
            if (!(entity instanceof EntityPlayer)) continue;
            EntityPlayer entityplayer = (EntityPlayer) players1.get(j);
            // Paper end

            // CraftBukkit start - Test if player receiving packet can see the source of the packet
            if (entityhuman != null && entityhuman instanceof EntityPlayer && !entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity())) {
               continue;
            }
            // CraftBukkit end

            if (entityplayer != entityhuman && (world != null || entityplayer.dimension == i)) { // Paper
                double d4 = d0 - entityplayer.locX;
                double d5 = d1 - entityplayer.locY;
                double d6 = d2 - entityplayer.locZ;

                if (d4 * d4 + d5 * d5 + d6 * d6 < d3 * d3) {
                    entityplayer.playerConnection.sendPacket(packet);
                }
            }
        }

    }

    // Paper start
    public void savePlayers() {
        savePlayers(null);
    }

    public void savePlayers(Integer interval) {
        MCUtil.ensureMain("Save Players", () -> { // Paper - ensure main
        long now = MinecraftServer.currentTick;
        MinecraftTimings.savePlayers.startTiming(); // Paper
        int numSaved = 0; // Paper
        for (int i = 0; i < this.players.size(); ++i) {
            EntityPlayer entityplayer = this.players.get(i);
            if (interval == null || now - entityplayer.lastSave >= interval) {
                this.savePlayerFile(entityplayer);
                if (interval != null && ++numSaved <= com.destroystokyo.paper.PaperConfig.maxPlayerAutoSavePerTick) { break; } // Paper
            }
        }
        MinecraftTimings.savePlayers.stopTiming(); // Paper
        return null; }); // Paper - ensure main
    }
    // Paper end

    public void addWhitelist(GameProfile gameprofile) {
        this.whitelist.add(new WhiteListEntry(gameprofile));
    }

    public void removeWhitelist(GameProfile gameprofile) {
        this.whitelist.remove(gameprofile);
    }

    public WhiteList getWhitelist() {
        return this.whitelist;
    }

    public String[] getWhitelisted() {
        return this.whitelist.getEntries();
    }

    public OpList getOPs() {
        return this.operators;
    }

    public String[] n() {
        return this.operators.getEntries();
    }

    public void reloadWhitelist() {}

    public void b(EntityPlayer entityplayer, WorldServer worldserver) {
        WorldBorder worldborder = entityplayer.world.getWorldBorder(); // CraftBukkit

        entityplayer.playerConnection.sendPacket(new PacketPlayOutWorldBorder(worldborder, PacketPlayOutWorldBorder.EnumWorldBorderAction.INITIALIZE));
        entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle")));
        BlockPosition blockposition = worldserver.getSpawn();

        entityplayer.playerConnection.sendPacket(new PacketPlayOutSpawnPosition(blockposition));
        if (worldserver.isRaining()) {
            // CraftBukkit start - handle player weather
            // entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(1, 0.0F));
            // entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(7, worldserver.j(1.0F)));
            // entityplayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(8, worldserver.h(1.0F)));
            entityplayer.setPlayerWeather(org.bukkit.WeatherType.DOWNFALL, false);
            entityplayer.updateWeather(-worldserver.o, worldserver.o, -worldserver.q, worldserver.q);
            // CraftBukkit end
        }

    }

    public void updateClient(EntityPlayer entityplayer) {
        entityplayer.updateInventory(entityplayer.defaultContainer);
        // entityplayer.triggerHealthUpdate();
        entityplayer.getBukkitEntity().updateScaledHealth(); // CraftBukkit - Update scaled health on respawn and worldchange
        entityplayer.playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(entityplayer.inventory.itemInHandIndex));
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String[] getSeenPlayers() {
        return this.server.worlds.get(0).getDataManager().getPlayerFileData().getSeenPlayers(); // CraftBukkit
    }

    public boolean getHasWhitelist() {
        return this.hasWhitelist;
    }

    public void setHasWhitelist(boolean flag) {
        this.hasWhitelist = flag;
    }

    public List<EntityPlayer> b(String s) {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.A().equals(s)) {
                arraylist.add(entityplayer);
            }
        }

        return arraylist;
    }

    public int s() {
        return this.s;
    }

    public MinecraftServer getServer() {
        return this.server;
    }

    public NBTTagCompound t() {
        return null;
    }

    private void a(EntityPlayer entityplayer, EntityPlayer entityplayer1, World world) {
        if (entityplayer1 != null) {
            entityplayer.playerInteractManager.setGameMode(entityplayer1.playerInteractManager.getGameMode());
        } else if (this.t != null) {
            entityplayer.playerInteractManager.setGameMode(this.t);
        }

        entityplayer.playerInteractManager.b(world.getWorldData().getGameType());
    }

    // Paper start - Extract method to allow for restarting flag
    public void u() {
        u(false);
    }

    public void u(boolean isRestarting) {
        // CraftBukkit start - disconnect safely
        for (EntityPlayer player : this.players) {
            player.playerConnection.disconnect(!isRestarting ? this.server.server.getShutdownMessage() : org.spigotmc.SpigotConfig.restartMessage); // CraftBukkit - add custom shutdown message // Paper - add isRestarting flag
        }
        // CraftBukkit end
        // Paper start - Remove collideRule team if it exists
        if (this.collideRuleTeamName != null) {
            final Scoreboard scoreboard = this.getServer().getWorld().getScoreboard();
            final ScoreboardTeam team = scoreboard.getTeam(this.collideRuleTeamName);
            if (team != null) scoreboard.removeTeam(team);
        }
        // Paper end
    }
    // Paper end

    // CraftBukkit start
    public void sendMessage(IChatBaseComponent[] iChatBaseComponents) {
        for (IChatBaseComponent component : iChatBaseComponents) {
            sendMessage(component, true);
        }
    }
    // CraftBukkit end

    public void sendMessage(IChatBaseComponent ichatbasecomponent, boolean flag) {
        this.server.sendMessage(ichatbasecomponent);
        ChatMessageType chatmessagetype = flag ? ChatMessageType.SYSTEM : ChatMessageType.CHAT;

        // CraftBukkit start - we run this through our processor first so we can get web links etc
        this.sendAll(new PacketPlayOutChat(CraftChatMessage.fixComponent(ichatbasecomponent), chatmessagetype));
        // CraftBukkit end
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.sendMessage(ichatbasecomponent, true);
    }

    public ServerStatisticManager getStatisticManager(EntityPlayer entityhuman) {
        UUID uuid = entityhuman.getUniqueID();
        ServerStatisticManager serverstatisticmanager = uuid == null ? null : (ServerStatisticManager) entityhuman.getStatisticManager();
        // CraftBukkit end

        if (serverstatisticmanager == null) {
            File file = new File(this.server.getWorldServer(0).getDataManager().getDirectory(), "stats");
            File file1 = new File(file, uuid + ".json");

            if (!file1.exists()) {
                File file2 = new File(file, entityhuman.getName() + ".json");

                if (file2.exists() && file2.isFile()) {
                    file2.renameTo(file1);
                }
            }

            serverstatisticmanager = new ServerStatisticManager(this.server, file1);
            serverstatisticmanager.a();
            // this.o.put(uuid, serverstatisticmanager); // CraftBukkit
        }

        return serverstatisticmanager;
    }

    public AdvancementDataPlayer h(EntityPlayer entityplayer) {
        UUID uuid = entityplayer.getUniqueID();
        AdvancementDataPlayer advancementdataplayer = entityplayer.getAdvancementData(); // CraftBukkit

        if (advancementdataplayer == null) {
            File file = new File(this.server.getWorldServer(0).getDataManager().getDirectory(), "advancements");
            File file1 = new File(file, uuid + ".json");

            advancementdataplayer = new AdvancementDataPlayer(this.server, file1, entityplayer);
            // this.p.put(uuid, advancementdataplayer); // CraftBukkit
        }

        advancementdataplayer.a(entityplayer);
        return advancementdataplayer;
    }

    public void a(int i) {
        this.s = i;
        if (this.server.worldServer != null) {
            WorldServer[] aworldserver = this.server.worldServer;
            int j = aworldserver.length;

            // CraftBukkit start
            for (int k = 0; k < server.worlds.size(); ++k) {
                WorldServer worldserver = server.worlds.get(0);
                // CraftBukkit end

                if (worldserver != null) {
                    worldserver.getPlayerChunkMap().a(i);
                    worldserver.getTracker().a(i);
                }
            }

        }
    }

    public List<EntityPlayer> v() {
        return this.players;
    }

    public EntityPlayer a(UUID uuid) {
        return this.j.get(uuid);
    }

    public boolean f(GameProfile gameprofile) {
        return false;
    }

    public void reload() {
        // CraftBukkit start
        /*Iterator iterator = this.p.values().iterator();

        while (iterator.hasNext()) {
            AdvancementDataPlayer advancementdataplayer = (AdvancementDataPlayer) iterator.next();

            advancementdataplayer.b();
        }*/

        for (EntityPlayer player : players) {
            player.getAdvancementData().b();
            player.getAdvancementData().b(player); // CraftBukkit - trigger immediate flush of advancements
        }
        // CraftBukkit end

    }
}
