package net.minecraft.server;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import java.util.HashSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.craftbukkit.util.LazyPlayerSet;
import org.bukkit.craftbukkit.util.Waitable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAnimationEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.InventoryView;
import org.bukkit.util.NumberConversions;
import com.destroystokyo.paper.event.player.IllegalPacketEvent; // Paper
import com.destroystokyo.paper.event.player.PlayerJumpEvent; // Paper
import co.aikar.timings.MinecraftTimings; // Paper
// CraftBukkit end

/**
 * Akarin Changes Note
 * 1) Add volatile to fields (slack service)
 * 2) Expose private members (slack service)
 * 3) Removed keep-alive codes (slack service)
 * 4) Accessible keep-alive limit (feature, compatibility)
 */
public class PlayerConnection implements PacketListenerPlayIn, ITickable {

    private static final Logger LOGGER = LogManager.getLogger();
    public final NetworkManager networkManager;
    private final MinecraftServer minecraftServer;
    public EntityPlayer player;
    private int e;
    private long f = getCurrentMillis(); public void setLastPing(long lastPing) { this.f = lastPing;}; public long getLastPing() { return this.f;}; // Paper - OBFHELPER - set ping to delay initial // Akarin - private -> public
    private boolean g; public void setPendingPing(boolean isPending) { this.g = isPending;}; public boolean isPendingPing() { return this.g;}; // Paper - OBFHELPER // Akarin - private -> public
    private long h; public void setKeepAliveID(long keepAliveID) { this.h = keepAliveID;}; public long getKeepAliveID() {return this.h; };  // Paper - OBFHELPER // Akarin - private -> public
    // CraftBukkit start - multithreaded fields
    private volatile int chatThrottle;
    private static final AtomicIntegerFieldUpdater chatSpamField = AtomicIntegerFieldUpdater.newUpdater(PlayerConnection.class, "chatThrottle");
    private final java.util.concurrent.atomic.AtomicInteger tabSpamLimiter = new java.util.concurrent.atomic.AtomicInteger(); // Paper - configurable tab spam limits
    // CraftBukkit end
    private int j;
    private final IntHashMap<Short> k = new IntHashMap();
    private double l;
    private double m;
    private double n;
    private double o;
    private double p;
    private double q;
    private Entity r;
    private double s;
    private double t;
    private double u;
    private double v;
    private double w;
    private double x;
    private Vec3D teleportPos;
    private int teleportAwait;
    private int A;
    private boolean B;
    private int C;
    private boolean D;
    private int E;
    private int receivedMovePackets;
    private int processedMovePackets;
    private AutoRecipe H = new AutoRecipe();
    private static final long KEEPALIVE_LIMIT = /*Long.getLong("paper.playerconnection.keepalive", 30)*/ AkarinGlobalConfig.keepAliveTimeout * 1000; // Paper - provide property to set keepalive limit // Akarin - more accessible - keep changes

    public PlayerConnection(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        this.minecraftServer = minecraftserver;
        this.networkManager = networkmanager;
        networkmanager.setPacketListener(this);
        this.player = entityplayer;
        entityplayer.playerConnection = this;

        // CraftBukkit start - add fields and methods
        this.server = minecraftserver.server;
    }

    private final org.bukkit.craftbukkit.CraftServer server;
    public volatile boolean processedDisconnect; // Akarin - private -> public - volatile
    private int lastTick = MinecraftServer.currentTick;
    private int allowedPlayerTicks = 1;
    private int lastDropTick = MinecraftServer.currentTick;
    private int lastBookTick  = MinecraftServer.currentTick;
    private int dropCount = 0;
    private static final int SURVIVAL_PLACE_DISTANCE_SQUARED = 6 * 6;
    private static final int CREATIVE_PLACE_DISTANCE_SQUARED = 7 * 7;

    // Get position of last block hit for BlockDamageLevel.STOPPED
    private double lastPosX = Double.MAX_VALUE;
    private double lastPosY = Double.MAX_VALUE;
    private double lastPosZ = Double.MAX_VALUE;
    private float lastPitch = Float.MAX_VALUE;
    private float lastYaw = Float.MAX_VALUE;
    private boolean justTeleported = false;
    private boolean hasMoved; // Spigot

    public CraftPlayer getPlayer() {
        return (this.player == null) ? null : (CraftPlayer) this.player.getBukkitEntity();
    }
    private final static HashSet<Integer> invalidItems = new HashSet<Integer>(java.util.Arrays.asList(8, 9, 10, 11, 26, 34, 36, 43, 51, 55, 59, 62, 63, 64, 68, 71, 74, 75, 83, 90, 92, 93, 94, 104, 105, 115, 117, 118, 119, 125, 127, 132, 140, 141, 142, 144)); // TODO: Check after every update.
    // CraftBukkit end

    public void e() {
        this.syncPosition();
        this.player.playerTick();
        this.player.setLocation(this.l, this.m, this.n, this.player.yaw, this.player.pitch);
        ++this.e;
        this.processedMovePackets = this.receivedMovePackets;
        if (this.B) {
            if (++this.C > 80) {
                PlayerConnection.LOGGER.warn("{} was kicked for floating too long!", this.player.getName());
                this.disconnect(com.destroystokyo.paper.PaperConfig.flyingKickPlayerMessage); // Paper - use configurable kick message
                return;
            }
        } else {
            this.B = false;
            this.C = 0;
        }

        this.r = this.player.getVehicle();
        if (this.r != this.player && this.r.bE() == this.player) {
            this.s = this.r.locX;
            this.t = this.r.locY;
            this.u = this.r.locZ;
            this.v = this.r.locX;
            this.w = this.r.locY;
            this.x = this.r.locZ;
            if (this.D && this.player.getVehicle().bE() == this.player) {
                if (++this.E > 80) {
                    PlayerConnection.LOGGER.warn("{} was kicked for floating a vehicle too long!", this.player.getName());
                    this.disconnect(com.destroystokyo.paper.PaperConfig.flyingKickVehicleMessage); // Paper - use configurable kick message
                    return;
                }
            } else {
                this.D = false;
                this.E = 0;
            }
        } else {
            this.r = null;
            this.D = false;
            this.E = 0;
        }

        this.minecraftServer.methodProfiler.a("keepAlive");
        /* // Akarin
        // Paper Start - give clients a longer time to respond to pings as per pre 1.12.2 timings
        // This should effectively place the keepalive handling back to "as it was" before 1.12.2
        long currentTime = this.getCurrentMillis();
        long elapsedTime = currentTime - this.getLastPing();
        if (this.isPendingPing()) {
            // We're pending a ping from the client
            if (!this.processedDisconnect && elapsedTime >= KEEPALIVE_LIMIT) { // check keepalive limit, don't fire if already disconnected
                PlayerConnection.LOGGER.warn("{} was kicked due to keepalive timeout!", this.player.getName()); // more info
                this.disconnect(new ChatMessage("disconnect.timeout"));
            }
        } else {
            if (elapsedTime >= 15000L) { // 15 seconds
                this.setPendingPing(true);
                this.setLastPing(currentTime);
                this.setKeepAliveID(currentTime);
                this.sendPacket(new PacketPlayOutKeepAlive(this.getKeepAliveID()));
            }
        }
        // Paper end
        */ // Akarin

        this.minecraftServer.methodProfiler.b();
        // CraftBukkit start
        for (int spam; (spam = this.chatThrottle) > 0 && !chatSpamField.compareAndSet(this, spam, spam - 1); ) ;
        if (tabSpamLimiter.get() > 0) tabSpamLimiter.getAndDecrement(); // Paper - split to seperate variable
        /* Use thread-safe field access instead
        if (this.chatThrottle > 0) {
            --this.chatThrottle;
        }
        */
        // CraftBukkit end

        if (this.j > 0) {
            --this.j;
        }

        if (this.player.J() > 0L && this.minecraftServer.getIdleTimeout() > 0 && MinecraftServer.aw() - this.player.J() > (long) (this.minecraftServer.getIdleTimeout() * 1000 * 60)) {
            this.player.resetIdleTimer(); // CraftBukkit - SPIGOT-854
            this.disconnect(new ChatMessage("multiplayer.disconnect.idling", new Object[0]));
        }

    }

    public void syncPosition() {
        this.l = this.player.locX;
        this.m = this.player.locY;
        this.n = this.player.locZ;
        this.o = this.player.locX;
        this.p = this.player.locY;
        this.q = this.player.locZ;
    }

    public NetworkManager a() {
        return this.networkManager;
    }

    // CraftBukkit start
    @Deprecated
    public void disconnect(IChatBaseComponent ichatbasecomponent) {
        disconnect(CraftChatMessage.fromComponent(ichatbasecomponent, EnumChatFormat.WHITE));
    }
    // CraftBukkit end

    public void disconnect(String s) {
        // CraftBukkit start - fire PlayerKickEvent
        if (this.processedDisconnect) {
            return;
        }
        String leaveMessage = EnumChatFormat.YELLOW + this.player.getName() + " left the game.";

        PlayerKickEvent event = new PlayerKickEvent(this.server.getPlayer(this.player), s, leaveMessage);

        if (this.server.getServer().isRunning()) {
            this.server.getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            // Do not kick the player
            return;
        }
        // Send the possibly modified leave message
        s = event.getReason();
        // CraftBukkit end
        final ChatComponentText chatcomponenttext = new ChatComponentText(s);

        this.networkManager.sendPacket(new PacketPlayOutKickDisconnect(chatcomponenttext), new GenericFutureListener() {
            public void operationComplete(Future future) throws Exception { // CraftBukkit - decompile error
                PlayerConnection.this.networkManager.close(chatcomponenttext);
            }
        }, new GenericFutureListener[0]);
        this.a(chatcomponenttext); // CraftBukkit - fire quit instantly
        this.networkManager.stopReading();
        // CraftBukkit - Don't wait
        this.minecraftServer.postToMainThread(new Runnable() {
            public void run() {
                PlayerConnection.this.networkManager.handleDisconnection();
            }
        });
    }

    public void a(PacketPlayInSteerVehicle packetplayinsteervehicle) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsteervehicle, this, this.player.x());
        this.player.a(packetplayinsteervehicle.a(), packetplayinsteervehicle.b(), packetplayinsteervehicle.c(), packetplayinsteervehicle.d());
    }

    private static boolean b(PacketPlayInFlying packetplayinflying) {
        return Doubles.isFinite(packetplayinflying.a(0.0D)) && Doubles.isFinite(packetplayinflying.b(0.0D)) && Doubles.isFinite(packetplayinflying.c(0.0D)) && Floats.isFinite(packetplayinflying.b(0.0F)) && Floats.isFinite(packetplayinflying.a(0.0F)) ? Math.abs(packetplayinflying.a(0.0D)) > 3.0E7D || Math.abs(packetplayinflying.b(0.0D)) > 3.0E7D || Math.abs(packetplayinflying.c(0.0D)) > 3.0E7D : true;
    }

    private static boolean b(PacketPlayInVehicleMove packetplayinvehiclemove) {
        return !Doubles.isFinite(packetplayinvehiclemove.getX()) || !Doubles.isFinite(packetplayinvehiclemove.getY()) || !Doubles.isFinite(packetplayinvehiclemove.getZ()) || !Floats.isFinite(packetplayinvehiclemove.getPitch()) || !Floats.isFinite(packetplayinvehiclemove.getYaw());
    }

    public void a(PacketPlayInVehicleMove packetplayinvehiclemove) {
        PlayerConnectionUtils.ensureMainThread(packetplayinvehiclemove, this, this.player.x());
        if (b(packetplayinvehiclemove)) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_vehicle_movement", new Object[0]));
        } else {
            Entity entity = this.player.getVehicle();

            if (entity != this.player && entity.bE() == this.player && entity == this.r) {
                WorldServer worldserver = this.player.x();
                double d0 = entity.locX;
                double d1 = entity.locY;
                double d2 = entity.locZ;
                double d3 = packetplayinvehiclemove.getX();
                double d4 = packetplayinvehiclemove.getY();
                double d5 = packetplayinvehiclemove.getZ();
                float f = packetplayinvehiclemove.getYaw();
                float f1 = packetplayinvehiclemove.getPitch();
                double d6 = d3 - this.s;
                double d7 = d4 - this.t;
                double d8 = d5 - this.u;
                double d9 = entity.motX * entity.motX + entity.motY * entity.motY + entity.motZ * entity.motZ;
                double d10 = d6 * d6 + d7 * d7 + d8 * d8;


                // CraftBukkit start - handle custom speeds and skipped ticks
                this.allowedPlayerTicks += (System.currentTimeMillis() / 50) - this.lastTick;
                this.allowedPlayerTicks = Math.max(this.allowedPlayerTicks, 1);
                this.lastTick = (int) (System.currentTimeMillis() / 50);

                ++this.receivedMovePackets;
                int i = this.receivedMovePackets - this.processedMovePackets;
                if (i > Math.max(this.allowedPlayerTicks, 5)) {
                    PlayerConnection.LOGGER.debug(this.player.getName() + " is sending move packets too frequently (" + i + " packets since last tick)");
                    i = 1;
                }

                if (d10 > 0) {
                    allowedPlayerTicks -= 1;
                } else {
                    allowedPlayerTicks = 20;
                }
                float speed;
                if (player.abilities.isFlying) {
                    speed = player.abilities.flySpeed * 20f;
                } else {
                    speed = player.abilities.walkSpeed * 10f;
                }
                speed *= 2f; // TODO: Get the speed of the vehicle instead of the player

                if (d10 - d9 > Math.max(100.0D, Math.pow((double) (org.spigotmc.SpigotConfig.movedTooQuicklyMultiplier * (float) i * speed), 2)) && (!this.minecraftServer.R() || !this.minecraftServer.Q().equals(entity.getName()))) { // Spigot
                // CraftBukkit end
                    PlayerConnection.LOGGER.warn("{} (vehicle of {}) moved too quickly! {},{},{}", entity.getName(), this.player.getName(), Double.valueOf(d6), Double.valueOf(d7), Double.valueOf(d8));
                    this.networkManager.sendPacket(new PacketPlayOutVehicleMove(entity));
                    return;
                }

                boolean flag = worldserver.getCubes(entity, entity.getBoundingBox().shrink(0.0625D)).isEmpty();

                d6 = d3 - this.v;
                d7 = d4 - this.w - 1.0E-6D;
                d8 = d5 - this.x;
                entity.move(EnumMoveType.PLAYER, d6, d7, d8);
                double d11 = d7;

                d6 = d3 - entity.locX;
                d7 = d4 - entity.locY;
                if (d7 > -0.5D || d7 < 0.5D) {
                    d7 = 0.0D;
                }

                d8 = d5 - entity.locZ;
                d10 = d6 * d6 + d7 * d7 + d8 * d8;
                boolean flag1 = false;

                if (d10 > org.spigotmc.SpigotConfig.movedWronglyThreshold) { // Spigot
                    flag1 = true;
                    PlayerConnection.LOGGER.warn(entity.getName() + " (vehicle of " + this.player.getName() + ") moved wrongly!"); // Paper - More informative
                }

                entity.setLocation(d3, d4, d5, f, f1);
                boolean flag2 = worldserver.getCubes(entity, entity.getBoundingBox().shrink(0.0625D)).isEmpty();

                if (flag && (flag1 || !flag2)) {
                    entity.setLocation(d0, d1, d2, f, f1);
                    this.networkManager.sendPacket(new PacketPlayOutVehicleMove(entity));
                    return;
                }

                // CraftBukkit start - fire PlayerMoveEvent
                Player player = this.getPlayer();
                // Spigot Start
                if ( !hasMoved )
                {
                    Location curPos = player.getLocation();
                    lastPosX = curPos.getX();
                    lastPosY = curPos.getY();
                    lastPosZ = curPos.getZ();
                    lastYaw = curPos.getYaw();
                    lastPitch = curPos.getPitch();
                    hasMoved = true;
                }
                // Spigot End
                Location from = new Location(player.getWorld(), lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch); // Get the Players previous Event location.
                Location to = player.getLocation().clone(); // Start off the To location as the Players current location.

                // If the packet contains movement information then we update the To location with the correct XYZ.
                to.setX(packetplayinvehiclemove.getX());
                to.setY(packetplayinvehiclemove.getY());
                to.setZ(packetplayinvehiclemove.getZ());


                // If the packet contains look information then we update the To location with the correct Yaw & Pitch.
                to.setYaw(packetplayinvehiclemove.getYaw());
                to.setPitch(packetplayinvehiclemove.getPitch());

                // Prevent 40 event-calls for less than a single pixel of movement >.>
                double delta = Math.pow(this.lastPosX - to.getX(), 2) + Math.pow(this.lastPosY - to.getY(), 2) + Math.pow(this.lastPosZ - to.getZ(), 2);
                float deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());

                if ((delta > 1f / 256 || deltaAngle > 10f) && !this.player.isFrozen()) {
                    this.lastPosX = to.getX();
                    this.lastPosY = to.getY();
                    this.lastPosZ = to.getZ();
                    this.lastYaw = to.getYaw();
                    this.lastPitch = to.getPitch();

                    // Skip the first time we do this
                    if (true) { // Spigot - don't skip any move events
                        Location oldTo = to.clone();
                        PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
                        this.server.getPluginManager().callEvent(event);

                        // If the event is cancelled we move the player back to their old location.
                        if (event.isCancelled()) {
                            teleport(from);
                            return;
                        }

                        // If a Plugin has changed the To destination then we teleport the Player
                        // there to avoid any 'Moved wrongly' or 'Moved too quickly' errors.
                        // We only do this if the Event was not cancelled.
                        if (!oldTo.equals(event.getTo()) && !event.isCancelled()) {
                            this.player.getBukkitEntity().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            return;
                        }

                        // Check to see if the Players Location has some how changed during the call of the event.
                        // This can happen due to a plugin teleporting the player instead of using .setTo()
                        if (!from.equals(this.getPlayer().getLocation()) && this.justTeleported) {
                            this.justTeleported = false;
                            return;
                        }
                    }
                }
                // CraftBukkit end

                this.minecraftServer.getPlayerList().d(this.player);
                this.player.checkMovement(this.player.locX - d0, this.player.locY - d1, this.player.locZ - d2);
                this.D = d11 >= -0.03125D && !this.minecraftServer.getAllowFlight() && !worldserver.c(entity.getBoundingBox().g(0.0625D).b(0.0D, -0.55D, 0.0D));
                this.v = entity.locX;
                this.w = entity.locY;
                this.x = entity.locZ;
            }

        }
    }

    public void a(PacketPlayInTeleportAccept packetplayinteleportaccept) {
        PlayerConnectionUtils.ensureMainThread(packetplayinteleportaccept, this, this.player.x());
        if (packetplayinteleportaccept.a() == this.teleportAwait && this.teleportPos != null) { // CraftBukkit
            this.player.setLocation(this.teleportPos.x, this.teleportPos.y, this.teleportPos.z, this.player.yaw, this.player.pitch);
            if (this.player.L()) {
                this.o = this.teleportPos.x;
                this.p = this.teleportPos.y;
                this.q = this.teleportPos.z;
                this.player.M();
            }

            this.teleportPos = null;
        }

    }

    public void a(PacketPlayInRecipeDisplayed packetplayinrecipedisplayed) {
        PlayerConnectionUtils.ensureMainThread(packetplayinrecipedisplayed, this, this.player.x());
        if (packetplayinrecipedisplayed.a() == PacketPlayInRecipeDisplayed.Status.SHOWN) {
            this.player.F().f(packetplayinrecipedisplayed.b());
        } else if (packetplayinrecipedisplayed.a() == PacketPlayInRecipeDisplayed.Status.SETTINGS) {
            this.player.F().a(packetplayinrecipedisplayed.c());
            this.player.F().b(packetplayinrecipedisplayed.d());
        }

    }

    public void a(PacketPlayInAdvancements packetplayinadvancements) {
        PlayerConnectionUtils.ensureMainThread(packetplayinadvancements, this, this.player.x());
        if (packetplayinadvancements.b() == PacketPlayInAdvancements.Status.OPENED_TAB) {
            MinecraftKey minecraftkey = packetplayinadvancements.c();
            Advancement advancement = this.minecraftServer.getAdvancementData().a(minecraftkey);

            if (advancement != null) {
                this.player.getAdvancementData().a(advancement);
            }
        }

    }

    public void a(PacketPlayInFlying packetplayinflying) {
        PlayerConnectionUtils.ensureMainThread(packetplayinflying, this, this.player.x());
        if (b(packetplayinflying)) {
            this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_player_movement", new Object[0]));
        } else {
            WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);

            if (!this.player.viewingCredits && !this.player.isFrozen()) { // CraftBukkit
                if (this.e == 0) {
                    this.syncPosition();
                }

                if (this.teleportPos != null) {
                    if (this.e - this.A > 20) {
                        this.A = this.e;
                        this.a(this.teleportPos.x, this.teleportPos.y, this.teleportPos.z, this.player.yaw, this.player.pitch);
                    }
                    this.allowedPlayerTicks = 20; // CraftBukkit
                } else {
                    this.A = this.e;
                    if (this.player.isPassenger()) {
                        this.player.setLocation(this.player.locX, this.player.locY, this.player.locZ, packetplayinflying.a(this.player.yaw), packetplayinflying.b(this.player.pitch));
                        this.minecraftServer.getPlayerList().d(this.player);
                        this.allowedPlayerTicks = 20; // CraftBukkit
                    } else {
                        // CraftBukkit - Make sure the move is valid but then reset it for plugins to modify
                        double prevX = player.locX;
                        double prevY = player.locY;
                        double prevZ = player.locZ;
                        float prevYaw = player.yaw;
                        float prevPitch = player.pitch;
                        // CraftBukkit end
                        double d0 = this.player.locX;
                        double d1 = this.player.locY;
                        double d2 = this.player.locZ;
                        double d3 = this.player.locY;
                        double d4 = packetplayinflying.a(this.player.locX);
                        double d5 = packetplayinflying.b(this.player.locY);
                        double d6 = packetplayinflying.c(this.player.locZ);
                        float f = packetplayinflying.a(this.player.yaw);
                        float f1 = packetplayinflying.b(this.player.pitch);
                        double d7 = d4 - this.l;
                        double d8 = d5 - this.m;
                        double d9 = d6 - this.n;
                        double d10 = this.player.motX * this.player.motX + this.player.motY * this.player.motY + this.player.motZ * this.player.motZ;
                        double d11 = d7 * d7 + d8 * d8 + d9 * d9;

                        if (this.player.isSleeping()) {
                            if (d11 > 1.0D) {
                                this.a(this.player.locX, this.player.locY, this.player.locZ, packetplayinflying.a(this.player.yaw), packetplayinflying.b(this.player.pitch));
                            }

                        } else {
                            ++this.receivedMovePackets;
                            int i = this.receivedMovePackets - this.processedMovePackets;

                            // CraftBukkit start - handle custom speeds and skipped ticks
                            this.allowedPlayerTicks += (System.currentTimeMillis() / 50) - this.lastTick;
                            this.allowedPlayerTicks = Math.max(this.allowedPlayerTicks, 1);
                            this.lastTick = (int) (System.currentTimeMillis() / 50);

                            if (i > Math.max(this.allowedPlayerTicks, 5)) {
                                PlayerConnection.LOGGER.debug("{} is sending move packets too frequently ({} packets since last tick)", this.player.getName(), Integer.valueOf(i));
                                i = 1;
                            }

                            if (packetplayinflying.hasLook || d11 > 0) {
                                allowedPlayerTicks -= 1;
                            } else {
                                allowedPlayerTicks = 20;
                            }
                            float speed;
                            if (player.abilities.isFlying) {
                                speed = player.abilities.flySpeed * 20f;
                            } else {
                                speed = player.abilities.walkSpeed * 10f;
                            }

                            if (!this.player.L() && (!this.player.x().getGameRules().getBoolean("disableElytraMovementCheck") || !this.player.cP())) {
                                float f2 = this.player.cP() ? 300.0F : 100.0F;

                                if (d11 - d10 > Math.max(f2, Math.pow((double) (org.spigotmc.SpigotConfig.movedTooQuicklyMultiplier * (float) i * speed), 2)) && (!this.minecraftServer.R() || !this.minecraftServer.Q().equals(this.player.getName()))) { // Spigot
                                // CraftBukkit end
                                    PlayerConnection.LOGGER.warn("{} moved too quickly! {},{},{}", this.player.getName(), Double.valueOf(d7), Double.valueOf(d8), Double.valueOf(d9));
                                    this.a(this.player.locX, this.player.locY, this.player.locZ, this.player.yaw, this.player.pitch);
                                    return;
                                }
                            }

                            boolean flag = worldserver.getCubes(this.player, this.player.getBoundingBox().shrink(0.0625D)).isEmpty();

                            d7 = d4 - this.o;
                            d8 = d5 - this.p;
                            d9 = d6 - this.q;
                            if (this.player.onGround && !packetplayinflying.a() && d8 > 0.0D) {
                                // Paper start - Add player jump event
                                Player player = this.getPlayer();
                                Location from = new Location(player.getWorld(), lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch); // Get the Players previous Event location.
                                Location to = player.getLocation().clone(); // Start off the To location as the Players current location.

                                // If the packet contains movement information then we update the To location with the correct XYZ.
                                if (packetplayinflying.hasPos) {
                                    to.setX(packetplayinflying.x);
                                    to.setY(packetplayinflying.y);
                                    to.setZ(packetplayinflying.z);
                                }

                                // If the packet contains look information then we update the To location with the correct Yaw & Pitch.
                                if (packetplayinflying.hasLook) {
                                    to.setYaw(packetplayinflying.yaw);
                                    to.setPitch(packetplayinflying.pitch);
                                }

                                PlayerJumpEvent event = new PlayerJumpEvent(player, from, to);

                                if (event.callEvent()) {
                                    this.player.jump();
                                } else {
                                    from = event.getFrom();
                                    this.internalTeleport(from.getX(), from.getY(), from.getZ(), from.getYaw(), from.getPitch(), Collections.emptySet());
                                    return;
                                }
                                // Paper end
                            }

                            this.player.move(EnumMoveType.PLAYER, d7, d8, d9);
                            this.player.onGround = packetplayinflying.a();
                            double d12 = d8;

                            d7 = d4 - this.player.locX;
                            d8 = d5 - this.player.locY;
                            if (d8 > -0.5D || d8 < 0.5D) {
                                d8 = 0.0D;
                            }

                            d9 = d6 - this.player.locZ;
                            d11 = d7 * d7 + d8 * d8 + d9 * d9;
                            boolean flag1 = false;

                            if (!this.player.L() && d11 > org.spigotmc.SpigotConfig.movedWronglyThreshold && !this.player.isSleeping() && !this.player.playerInteractManager.isCreative() && this.player.playerInteractManager.getGameMode() != EnumGamemode.SPECTATOR) { // Spigot
                                flag1 = true;
                                PlayerConnection.LOGGER.warn("{} moved wrongly!", this.player.getName());
                            }

                            this.player.setLocation(d4, d5, d6, f, f1);
                            this.player.checkMovement(this.player.locX - d0, this.player.locY - d1, this.player.locZ - d2);
                            if (!this.player.noclip && !this.player.isSleeping()) {
                                boolean flag2 = worldserver.getCubes(this.player, this.player.getBoundingBox().shrink(0.0625D)).isEmpty();

                                if (flag && (flag1 || !flag2)) {
                                    this.a(d0, d1, d2, f, f1);
                                    return;
                                }
                            }

                            // CraftBukkit start - fire PlayerMoveEvent
                            // Rest to old location first
                            this.player.setLocation(prevX, prevY, prevZ, prevYaw, prevPitch);

                            Player player = this.getPlayer();
                            Location from = new Location(player.getWorld(), lastPosX, lastPosY, lastPosZ, lastYaw, lastPitch); // Get the Players previous Event location.
                            Location to = player.getLocation().clone(); // Start off the To location as the Players current location.

                            // If the packet contains movement information then we update the To location with the correct XYZ.
                            if (packetplayinflying.hasPos) {
                                to.setX(packetplayinflying.x);
                                to.setY(packetplayinflying.y);
                                to.setZ(packetplayinflying.z);
                            }

                            // If the packet contains look information then we update the To location with the correct Yaw & Pitch.
                            if (packetplayinflying.hasLook) {
                                to.setYaw(packetplayinflying.yaw);
                                to.setPitch(packetplayinflying.pitch);
                            }

                            // Prevent 40 event-calls for less than a single pixel of movement >.>
                            double delta = Math.pow(this.lastPosX - to.getX(), 2) + Math.pow(this.lastPosY - to.getY(), 2) + Math.pow(this.lastPosZ - to.getZ(), 2);
                            float deltaAngle = Math.abs(this.lastYaw - to.getYaw()) + Math.abs(this.lastPitch - to.getPitch());

                            if ((delta > 1f / 256 || deltaAngle > 10f) && !this.player.isFrozen()) {
                                this.lastPosX = to.getX();
                                this.lastPosY = to.getY();
                                this.lastPosZ = to.getZ();
                                this.lastYaw = to.getYaw();
                                this.lastPitch = to.getPitch();

                                // Skip the first time we do this
                                if (from.getX() != Double.MAX_VALUE) {
                                    Location oldTo = to.clone();
                                    PlayerMoveEvent event = new PlayerMoveEvent(player, from, to);
                                    this.server.getPluginManager().callEvent(event);

                                    // If the event is cancelled we move the player back to their old location.
                                    if (event.isCancelled()) {
                                        teleport(from);
                                        return;
                                    }

                                    // If a Plugin has changed the To destination then we teleport the Player
                                    // there to avoid any 'Moved wrongly' or 'Moved too quickly' errors.
                                    // We only do this if the Event was not cancelled.
                                    if (!oldTo.equals(event.getTo()) && !event.isCancelled()) {
                                        this.player.getBukkitEntity().teleport(event.getTo(), PlayerTeleportEvent.TeleportCause.PLUGIN);
                                        return;
                                    }

                                    // Check to see if the Players Location has some how changed during the call of the event.
                                    // This can happen due to a plugin teleporting the player instead of using .setTo()
                                    if (!from.equals(this.getPlayer().getLocation()) && this.justTeleported) {
                                        this.justTeleported = false;
                                        return;
                                    }
                                }
                            }
                            this.player.setLocation(d4, d5, d6, f, f1); // Copied from above
                            // CraftBukkit end

                            this.B = d12 >= -0.03125D;
                            this.B &= !this.minecraftServer.getAllowFlight() && !this.player.abilities.canFly;
                            this.B &= !this.player.hasEffect(MobEffects.LEVITATION) && !this.player.cP() && !worldserver.c(this.player.getBoundingBox().g(0.0625D).b(0.0D, -0.55D, 0.0D));
                            this.player.onGround = packetplayinflying.a();
                            this.minecraftServer.getPlayerList().d(this.player);
                            this.player.a(this.player.locY - d3, packetplayinflying.a());
                            this.o = this.player.locX;
                            this.p = this.player.locY;
                            this.q = this.player.locZ;
                        }
                    }
                }
            }
        }
    }

    public void a(double d0, double d1, double d2, float f, float f1) {
        this.a(d0, d1, d2, f, f1, Collections.<PacketPlayOutPosition.EnumPlayerTeleportFlags>emptySet());
    }

    // CraftBukkit start - Delegate to teleport(Location)
    public void a(double d0, double d1, double d2, float f, float f1, PlayerTeleportEvent.TeleportCause cause) {
        this.a(d0, d1, d2, f, f1, Collections.<PacketPlayOutPosition.EnumPlayerTeleportFlags>emptySet(), cause);
    }

    public void a(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        this.a(d0, d1, d2, f, f1, set, PlayerTeleportEvent.TeleportCause.UNKNOWN);
    }

    public void a(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set, PlayerTeleportEvent.TeleportCause cause) {
        Player player = this.getPlayer();
        Location from = player.getLocation();

        double x = d0;
        double y = d1;
        double z = d2;
        float yaw = f;
        float pitch = f1;
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X)) {
            x += from.getX();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y)) {
            y += from.getY();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z)) {
            z += from.getZ();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
            yaw += from.getYaw();
        }
        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
            pitch += from.getPitch();
        }


        Location to = new Location(this.getPlayer().getWorld(), x, y, z, yaw, pitch);
        PlayerTeleportEvent event = new PlayerTeleportEvent(player, from.clone(), to.clone(), cause);
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled() || !to.equals(event.getTo())) {
            set.clear(); // Can't relative teleport
            to = event.isCancelled() ? event.getFrom() : event.getTo();
            d0 = to.getX();
            d1 = to.getY();
            d2 = to.getZ();
            f = to.getYaw();
            f1 = to.getPitch();
        }

        this.internalTeleport(d0, d1, d2, f, f1, set);
    }

    public void teleport(Location dest) {
        internalTeleport(dest.getX(), dest.getY(), dest.getZ(), dest.getYaw(), dest.getPitch(), Collections.<PacketPlayOutPosition.EnumPlayerTeleportFlags>emptySet());
    }

    private void internalTeleport(double d0, double d1, double d2, float f, float f1, Set<PacketPlayOutPosition.EnumPlayerTeleportFlags> set) {
        // CraftBukkit start
        if (Float.isNaN(f)) {
            f = 0;
        }
        if (Float.isNaN(f1)) {
            f1 = 0;
        }

        this.justTeleported = true;
        // CraftBukkit end
        double d3 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X) ? this.player.locX : 0.0D;
        double d4 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y) ? this.player.locY : 0.0D;
        double d5 = set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Z) ? this.player.locZ : 0.0D;

        this.teleportPos = new Vec3D(d0 + d3, d1 + d4, d2 + d5);
        float f2 = f;
        float f3 = f1;

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.Y_ROT)) {
            f2 = f + this.player.yaw;
        }

        if (set.contains(PacketPlayOutPosition.EnumPlayerTeleportFlags.X_ROT)) {
            f3 = f1 + this.player.pitch;
        }

        // CraftBukkit start - update last location
        this.lastPosX = this.teleportPos.x;
        this.lastPosY = this.teleportPos.y;
        this.lastPosZ = this.teleportPos.z;
        this.lastYaw = f2;
        this.lastPitch = f3;
        // CraftBukkit end

        if (++this.teleportAwait == Integer.MAX_VALUE) {
            this.teleportAwait = 0;
        }

        this.A = this.e;
        this.player.setLocation(this.teleportPos.x, this.teleportPos.y, this.teleportPos.z, f2, f3);
        this.player.playerConnection.sendPacket(new PacketPlayOutPosition(d0, d1, d2, f, f1, set, this.teleportAwait));
    }

    public void a(PacketPlayInBlockDig packetplayinblockdig) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockdig, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        BlockPosition blockposition = packetplayinblockdig.a();

        this.player.resetIdleTimer();
        switch (packetplayinblockdig.c()) {
        case SWAP_HELD_ITEMS:
            if (!this.player.isSpectator()) {
                ItemStack itemstack = this.player.b(EnumHand.OFF_HAND);

                // CraftBukkit start
                PlayerSwapHandItemsEvent swapItemsEvent = new PlayerSwapHandItemsEvent(getPlayer(), CraftItemStack.asBukkitCopy(itemstack), CraftItemStack.asBukkitCopy(this.player.b(EnumHand.MAIN_HAND)));
                this.server.getPluginManager().callEvent(swapItemsEvent);
                if (swapItemsEvent.isCancelled()) {
                    return;
                }
                itemstack = CraftItemStack.asNMSCopy(swapItemsEvent.getMainHandItem());
                this.player.a(EnumHand.OFF_HAND, CraftItemStack.asNMSCopy(swapItemsEvent.getOffHandItem()));
                // CraftBukkit end
                this.player.a(EnumHand.MAIN_HAND, itemstack);
            }

            return;

        case DROP_ITEM:
            if (!this.player.isSpectator()) {
                // limit how quickly items can be dropped
                // If the ticks aren't the same then the count starts from 0 and we update the lastDropTick.
                if (this.lastDropTick != MinecraftServer.currentTick) {
                    this.dropCount = 0;
                    this.lastDropTick = MinecraftServer.currentTick;
                } else {
                    // Else we increment the drop count and check the amount.
                    this.dropCount++;
                    if (this.dropCount >= 20) {
                        LOGGER.warn(this.player.getName() + " dropped their items too quickly!");
                        this.disconnect("You dropped your items too quickly (Hacking?)");
                        return;
                    }
                }
                // CraftBukkit end
                this.player.a(false);
            }

            return;

        case DROP_ALL_ITEMS:
            if (!this.player.isSpectator()) {
                this.player.a(true);
            }

            return;

        case RELEASE_USE_ITEM:
            this.player.clearActiveItem();
            return;

        case START_DESTROY_BLOCK:
        case ABORT_DESTROY_BLOCK:
        case STOP_DESTROY_BLOCK:
            double d0 = this.player.locX - ((double) blockposition.getX() + 0.5D);
            double d1 = this.player.locY - ((double) blockposition.getY() + 0.5D) + 1.5D;
            double d2 = this.player.locZ - ((double) blockposition.getZ() + 0.5D);
            double d3 = d0 * d0 + d1 * d1 + d2 * d2;

            if (d3 > 36.0D) {
                if (worldserver.isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4, true)) // Paper - Fix block break desync - Don't send for unloaded chunks
                    this.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition)); // Paper - Fix block break desync
                return;
            } else if (blockposition.getY() >= this.minecraftServer.getMaxBuildHeight()) {
                return;
            } else {
                if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                    if (!this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
                        this.player.playerInteractManager.a(blockposition, packetplayinblockdig.b());
                    } else {
                        // CraftBukkit start - fire PlayerInteractEvent
                        CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, blockposition, packetplayinblockdig.b(), this.player.inventory.getItemInHand(), EnumHand.MAIN_HAND);
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
                        // Update any tile entity data for this block
                        TileEntity tileentity = worldserver.getTileEntity(blockposition);
                        if (tileentity != null) {
                            this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
                        }
                        // CraftBukkit end
                    }
                } else {
                    if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                        this.player.playerInteractManager.a(blockposition);
                    } else if (packetplayinblockdig.c() == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                        this.player.playerInteractManager.e();
                    }

                    if (worldserver.getType(blockposition).getMaterial() != Material.AIR) {
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
                    }
                }

                return;
            }

        default:
            throw new IllegalArgumentException("Invalid player action");
        }
        // CraftBukkit end
    }

    // Spigot start - limit place/interactions
    private int limitedPackets;
    private long lastLimitedPacket = -1;
    private static final int THRESHOLD = com.destroystokyo.paper.PaperConfig.packetInSpamThreshold; // Paper - Configurable threshold

    private boolean checkLimit(long timestamp) {
        if (lastLimitedPacket != -1 && timestamp - lastLimitedPacket < THRESHOLD && limitedPackets++ >= 8) { // Paper - Use threshold, raise packet limit to 8
            return false;
        }

        if (lastLimitedPacket == -1 || timestamp - lastLimitedPacket >= THRESHOLD) { // Paper
            lastLimitedPacket = timestamp;
            limitedPackets = 0;
            return true;
        }

        return true;
    }
    // Spigot end

    public void a(PacketPlayInUseItem packetplayinuseitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinuseitem, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        if (!checkLimit(packetplayinuseitem.timestamp)) return; // Spigot - check limit
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        EnumHand enumhand = packetplayinuseitem.c();
        ItemStack itemstack = this.player.b(enumhand);
        BlockPosition blockposition = packetplayinuseitem.a();
        EnumDirection enumdirection = packetplayinuseitem.b();

        this.player.resetIdleTimer();
        if (blockposition.getY() >= this.minecraftServer.getMaxBuildHeight() - 1 && (enumdirection == EnumDirection.UP || blockposition.getY() >= this.minecraftServer.getMaxBuildHeight())) {
            ChatMessage chatmessage = new ChatMessage("build.tooHigh", new Object[] { Integer.valueOf(this.minecraftServer.getMaxBuildHeight())});

            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.player.playerConnection.sendPacket(new PacketPlayOutChat(chatmessage, ChatMessageType.GAME_INFO));
        } else if (this.teleportPos == null && this.player.d((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D) < 64.0D && !this.minecraftServer.a(worldserver, blockposition, this.player) && worldserver.getWorldBorder().a(blockposition)) {
            // CraftBukkit start - Check if we can actually do something over this large a distance
            Location eyeLoc = this.getPlayer().getEyeLocation();
            double reachDistance = NumberConversions.square(eyeLoc.getX() - blockposition.getX()) + NumberConversions.square(eyeLoc.getY() - blockposition.getY()) + NumberConversions.square(eyeLoc.getZ() - blockposition.getZ());
            if (reachDistance > (this.getPlayer().getGameMode() == org.bukkit.GameMode.CREATIVE ? CREATIVE_PLACE_DISTANCE_SQUARED : SURVIVAL_PLACE_DISTANCE_SQUARED)) {
                return;
            }
            // CraftBukkit end
            this.player.playerInteractManager.a(this.player, worldserver, itemstack, enumhand, blockposition, enumdirection, packetplayinuseitem.d(), packetplayinuseitem.e(), packetplayinuseitem.f());
        }

        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition));
        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(worldserver, blockposition.shift(enumdirection)));
    }

    public void a(PacketPlayInBlockPlace packetplayinblockplace) {
        PlayerConnectionUtils.ensureMainThread(packetplayinblockplace, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        if (!checkLimit(packetplayinblockplace.timestamp)) return; // Spigot - check limit
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        EnumHand enumhand = packetplayinblockplace.a();
        ItemStack itemstack = this.player.b(enumhand);

        this.player.resetIdleTimer();
        if (!itemstack.isEmpty()) {
            // CraftBukkit start
            // Raytrace to look for 'rogue armswings'
            float f1 = this.player.pitch;
            float f2 = this.player.yaw;
            double d0 = this.player.locX;
            double d1 = this.player.locY + (double) this.player.getHeadHeight();
            double d2 = this.player.locZ;
            Vec3D vec3d = new Vec3D(d0, d1, d2);

            float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
            float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
            float f5 = -MathHelper.cos(-f1 * 0.017453292F);
            float f6 = MathHelper.sin(-f1 * 0.017453292F);
            float f7 = f4 * f5;
            float f8 = f3 * f5;
            double d3 = player.playerInteractManager.getGameMode()== EnumGamemode.CREATIVE ? 5.0D : 4.5D;
            Vec3D vec3d1 = vec3d.add((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
            MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d1, false);

            boolean cancelled;
            if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.RIGHT_CLICK_AIR, itemstack, enumhand);
                cancelled = event.useItemInHand() == Event.Result.DENY;
            } else {
                if (player.playerInteractManager.firedInteract) {
                    player.playerInteractManager.firedInteract = false;
                    cancelled = player.playerInteractManager.interactResult;
                } else {
                    org.bukkit.event.player.PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(player, Action.RIGHT_CLICK_BLOCK, movingobjectposition.a(), movingobjectposition.direction, itemstack, true, enumhand);
                    cancelled = event.useItemInHand() == Event.Result.DENY;
                }
            }

            if (cancelled) {
                this.player.getBukkitEntity().updateInventory(); // SPIGOT-2524
            } else {
                this.player.playerInteractManager.a(this.player, worldserver, itemstack, enumhand);
            }
            // CraftBukkit end
        }
    }

    public void a(PacketPlayInSpectate packetplayinspectate) {
        PlayerConnectionUtils.ensureMainThread(packetplayinspectate, this, this.player.x());
        if (this.player.isSpectator()) {
            Entity entity = null;
            WorldServer[] aworldserver = this.minecraftServer.worldServer;
            int i = aworldserver.length;

            // CraftBukkit - use the worlds array list
            for (WorldServer worldserver : minecraftServer.worlds) {

                if (worldserver != null) {
                    entity = packetplayinspectate.a(worldserver);
                    if (entity != null) {
                        break;
                    }
                }
            }

            if (entity != null) {
                this.player.setSpectatorTarget(this.player);
                this.player.stopRiding();

                /* CraftBukkit start - replace with bukkit handling for multi-world
                if (entity.world == this.player.world) {
                    this.player.enderTeleportTo(entity.locX, entity.locY, entity.locZ);
                } else {
                    WorldServer worldserver1 = this.player.x();
                    WorldServer worldserver2 = (WorldServer) entity.world;

                    this.player.dimension = entity.dimension;
                    this.sendPacket(new PacketPlayOutRespawn(this.player.dimension, worldserver1.getDifficulty(), worldserver1.getWorldData().getType(), this.player.playerInteractManager.getGameMode()));
                    this.minecraftServer.getPlayerList().f(this.player);
                    worldserver1.removeEntity(this.player);
                    this.player.dead = false;
                    this.player.setPositionRotation(entity.locX, entity.locY, entity.locZ, entity.yaw, entity.pitch);
                    if (this.player.isAlive()) {
                        worldserver1.entityJoinedWorld(this.player, false);
                        worldserver2.addEntity(this.player);
                        worldserver2.entityJoinedWorld(this.player, false);
                    }

                    this.player.spawnIn(worldserver2);
                    this.minecraftServer.getPlayerList().a(this.player, worldserver1);
                    this.player.enderTeleportTo(entity.locX, entity.locY, entity.locZ);
                    this.player.playerInteractManager.a(worldserver2);
                    this.minecraftServer.getPlayerList().b(this.player, worldserver2);
                    this.minecraftServer.getPlayerList().updateClient(this.player);
                }
                */
                this.player.getBukkitEntity().teleport(entity.getBukkitEntity(), PlayerTeleportEvent.TeleportCause.SPECTATE);
                // CraftBukkit end
            }
        }

    }

    // CraftBukkit start
    public void a(PacketPlayInResourcePackStatus packetplayinresourcepackstatus) {
        PlayerConnectionUtils.ensureMainThread(packetplayinresourcepackstatus, this, this.player.x());
        // Paper start
        //this.server.getPluginManager().callEvent(new PlayerResourcePackStatusEvent(getPlayer(), PlayerResourcePackStatusEvent.Status.values()[packetplayinresourcepackstatus.status.ordinal()]));
        final PlayerResourcePackStatusEvent.Status status = PlayerResourcePackStatusEvent.Status.values()[packetplayinresourcepackstatus.status.ordinal()];
        this.getPlayer().setResourcePackStatus(status);
        this.server.getPluginManager().callEvent(new PlayerResourcePackStatusEvent(getPlayer(), status));
        // Paper end
    }
    // CraftBukkit end

    public void a(PacketPlayInBoatMove packetplayinboatmove) {
        PlayerConnectionUtils.ensureMainThread(packetplayinboatmove, this, this.player.x());
        Entity entity = this.player.bJ();

        if (entity instanceof EntityBoat) {
            ((EntityBoat) entity).a(packetplayinboatmove.a(), packetplayinboatmove.b());
        }

    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        // CraftBukkit start - Rarely it would send a disconnect line twice
        if (this.processedDisconnect) {
            return;
        } else {
            this.processedDisconnect = true;
        }
        // CraftBukkit end
        PlayerConnection.LOGGER.info("{} lost connection: {}", this.player.getName(), ichatbasecomponent.toPlainText());
        // CraftBukkit start - Replace vanilla quit message handling with our own.
        /*
        this.minecraftServer.aD();
        ChatMessage chatmessage = new ChatMessage("multiplayer.player.left", new Object[] { this.player.getScoreboardDisplayName()});

        chatmessage.getChatModifier().setColor(EnumChatFormat.YELLOW);
        this.minecraftServer.getPlayerList().sendMessage(chatmessage);
        */

        this.player.s();
        String quitMessage = this.minecraftServer.getPlayerList().disconnect(this.player);
        if ((quitMessage != null) && (quitMessage.length() > 0)) {
            this.minecraftServer.getPlayerList().sendMessage(CraftChatMessage.fromString(quitMessage));
        }
        // CraftBukkit end
        if (this.minecraftServer.R() && this.player.getName().equals(this.minecraftServer.Q())) {
            PlayerConnection.LOGGER.info("Stopping singleplayer server as player logged out");
            this.minecraftServer.safeShutdown();
        }

    }

    public void sendPacket(final Packet<?> packet) {
        if (packet instanceof PacketPlayOutChat) {
            PacketPlayOutChat packetplayoutchat = (PacketPlayOutChat) packet;
            EntityHuman.EnumChatVisibility entityhuman_enumchatvisibility = this.player.getChatFlags();

            if (entityhuman_enumchatvisibility == EntityHuman.EnumChatVisibility.HIDDEN && packetplayoutchat.c() != ChatMessageType.GAME_INFO) {
                return;
            }

            if (entityhuman_enumchatvisibility == EntityHuman.EnumChatVisibility.SYSTEM && !packetplayoutchat.b()) {
                return;
            }
        }

        // CraftBukkit start
        if (packet == null || this.processedDisconnect) { // Spigot
            return;
        } else if (packet instanceof PacketPlayOutSpawnPosition) {
            PacketPlayOutSpawnPosition packet6 = (PacketPlayOutSpawnPosition) packet;
            this.player.compassTarget = new Location(this.getPlayer().getWorld(), packet6.position.getX(), packet6.position.getY(), packet6.position.getZ());
        }
        // CraftBukkit end

        try {
            this.networkManager.sendPacket(packet);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Sending packet");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Packet being sent");

            crashreportsystemdetails.a("Packet class", new CrashReportCallable() {
                public String a() throws Exception {
                    return packet.getClass().getCanonicalName();
                }

                public Object call() throws Exception {
                    return this.a();
                }
            });
            throw new ReportedException(crashreport);
        }
    }

    public void a(PacketPlayInHeldItemSlot packetplayinhelditemslot) {
        PlayerConnectionUtils.ensureMainThread(packetplayinhelditemslot, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        if (packetplayinhelditemslot.a() >= 0 && packetplayinhelditemslot.a() < PlayerInventory.getHotbarSize()) {
            PlayerItemHeldEvent event = new PlayerItemHeldEvent(this.getPlayer(), this.player.inventory.itemInHandIndex, packetplayinhelditemslot.a());
            this.server.getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                this.sendPacket(new PacketPlayOutHeldItemSlot(this.player.inventory.itemInHandIndex));
                this.player.resetIdleTimer();
                return;
            }
            // CraftBukkit end
            this.player.inventory.itemInHandIndex = packetplayinhelditemslot.a();
            this.player.resetIdleTimer();
        } else {
            PlayerConnection.LOGGER.warn("{} tried to set an invalid carried item", this.player.getName());
            this.disconnect("Invalid hotbar selection (Hacking?)"); // CraftBukkit //Spigot "Nope" -> Descriptive reason
        }
    }

    public void a(PacketPlayInChat packetplayinchat) {
        // CraftBukkit start - async chat
        // SPIGOT-3638
        if (this.minecraftServer.isStopped()) {
            return;
        }

        boolean isSync = packetplayinchat.a().startsWith("/");
        if (packetplayinchat.a().startsWith("/")) {
            PlayerConnectionUtils.ensureMainThread(packetplayinchat, this, this.player.x());
        }
        // CraftBukkit end
        if (this.player.dead || this.player.getChatFlags() == EntityHuman.EnumChatVisibility.HIDDEN) { // CraftBukkit - dead men tell no tales
            ChatMessage chatmessage = new ChatMessage("chat.cannotSend", new Object[0]);

            chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
            this.sendPacket(new PacketPlayOutChat(chatmessage));
        } else {
            this.player.resetIdleTimer();
            String s = packetplayinchat.a();

            s = StringUtils.normalizeSpace(s);

            for (int i = 0; i < s.length(); ++i) {
                if (!SharedConstants.isAllowedChatCharacter(s.charAt(i))) {
                    // CraftBukkit start - threadsafety
                    if (!isSync) {
                        Waitable waitable = new Waitable() {
                            @Override
                            protected Object evaluate() {
                                PlayerConnection.this.disconnect(new ChatMessage("multiplayer.disconnect.illegal_characters", new Object[0]));
                                return null;
                            }
                        };

                        this.minecraftServer.processQueue.add(waitable);

                        try {
                            waitable.get();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        } catch (ExecutionException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        this.disconnect(new ChatMessage("multiplayer.disconnect.illegal_characters", new Object[0]));
                    }
                    // CraftBukkit end
                    return;
                }
            }

            // CraftBukkit start
            if (isSync) {
                try {
                    this.minecraftServer.server.playerCommandState = true;
                    this.handleCommand(s);
                } finally {
                    this.minecraftServer.server.playerCommandState = false;
                }
            } else if (s.isEmpty()) {
                LOGGER.warn(this.player.getName() + " tried to send an empty message");
            } else if (getPlayer().isConversing()) {
                // Spigot start
                final String message = s;
                this.minecraftServer.processQueue.add( new Waitable()
                {
                    @Override
                    protected Object evaluate()
                    {
                        getPlayer().acceptConversationInput( message );
                        return null;
                    }
                } );
                // Spigot end
            } else if (this.player.getChatFlags() == EntityHuman.EnumChatVisibility.SYSTEM) { // Re-add "Command Only" flag check
                ChatMessage chatmessage = new ChatMessage("chat.cannotSend", new Object[0]);

                chatmessage.getChatModifier().setColor(EnumChatFormat.RED);
                this.sendPacket(new PacketPlayOutChat(chatmessage));
            } else if (true) {
                this.chat(s, true);
                // CraftBukkit end - the below is for reference. :)
            } else {
                ChatMessage chatmessage1 = new ChatMessage("chat.type.text", new Object[] { this.player.getScoreboardDisplayName(), s});

                this.minecraftServer.getPlayerList().sendMessage(chatmessage1, false);
            }

            // Spigot start - spam exclusions
            boolean counted = true;
            for ( String exclude : org.spigotmc.SpigotConfig.spamExclusions )
            {
                if ( exclude != null && s.startsWith( exclude ) )
                {
                    counted = false;
                    break;
                }
            }
            // Spigot end
            // CraftBukkit start - replaced with thread safe throttle
            // this.chatThrottle += 20;
            if (counted && chatSpamField.addAndGet(this, 20) > 200 && !this.minecraftServer.getPlayerList().isOp(this.player.getProfile())) { // Spigot
                if (!isSync) {
                    Waitable waitable = new Waitable() {
                        @Override
                        protected Object evaluate() {
                            PlayerConnection.this.disconnect(new ChatMessage("disconnect.spam", new Object[0]));
                            return null;
                        }
                    };

                    this.minecraftServer.processQueue.add(waitable);

                    try {
                        waitable.get();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } catch (ExecutionException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    this.disconnect(new ChatMessage("disconnect.spam", new Object[0]));
                }
                // CraftBukkit end
            }

        }
    }

    // CraftBukkit start - add method
    public void chat(String s, boolean async) {
        if (s.isEmpty() || this.player.getChatFlags() == EntityHuman.EnumChatVisibility.HIDDEN) {
            return;
        }

        if (!async && s.startsWith("/")) {
            // Paper Start
            if (!org.spigotmc.AsyncCatcher.shuttingDown && !Akari.isPrimaryThread()) { // Akarin
                final String fCommandLine = s;
                Akari.callbackQueue.add(() -> chat(fCommandLine,  false)); // Akarin
                /* // Akarin
                MinecraftServer.LOGGER.log(org.apache.logging.log4j.Level.ERROR, "Command Dispatched Async: " + fCommandLine);
                MinecraftServer.LOGGER.log(org.apache.logging.log4j.Level.ERROR, "Please notify author of plugin causing this execution to fix this bug! see: http://bit.ly/1oSiM6C", new Throwable());
                Waitable wait = new Waitable() {
                    @Override
                    protected Object evaluate() {
                        chat(fCommandLine, false);
                        return null;
                    }
                };
                minecraftServer.processQueue.add(wait);
                try {
                    wait.get();
                    return;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // This is proper habit for java. If we aren't handling it, pass it on!
                } catch (Exception e) {
                    throw new RuntimeException("Exception processing chat command", e.getCause());
                }
                */ // Akarin
            }
            // Paper End
            this.handleCommand(s);
        } else if (this.player.getChatFlags() == EntityHuman.EnumChatVisibility.SYSTEM) {
            // Do nothing, this is coming from a plugin
        } else {
            Player player = this.getPlayer();
            AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(async, player, s, new LazyPlayerSet(minecraftServer));
            this.server.getPluginManager().callEvent(event);

            if (PlayerChatEvent.getHandlerList().getRegisteredListeners().length != 0) {
                // Evil plugins still listening to deprecated event
                final PlayerChatEvent queueEvent = new PlayerChatEvent(player, event.getMessage(), event.getFormat(), event.getRecipients());
                queueEvent.setCancelled(event.isCancelled());
                Waitable waitable = new Waitable() {
                    @Override
                    protected Object evaluate() {
                        org.bukkit.Bukkit.getPluginManager().callEvent(queueEvent);

                        if (queueEvent.isCancelled()) {
                            return null;
                        }

                        String message = String.format(queueEvent.getFormat(), queueEvent.getPlayer().getDisplayName(), queueEvent.getMessage());
                        PlayerConnection.this.minecraftServer.console.sendMessage(message);
                        if (((LazyPlayerSet) queueEvent.getRecipients()).isLazy()) {
                            for (Object player : PlayerConnection.this.minecraftServer.getPlayerList().players) {
                                ((EntityPlayer) player).sendMessage(CraftChatMessage.fromString(message));
                            }
                        } else {
                            for (Player player : queueEvent.getRecipients()) {
                                player.sendMessage(message);
                            }
                        }
                        return null;
                    }};
                if (async) {
                    minecraftServer.processQueue.add(waitable);
                } else {
                    waitable.run();
                }
                try {
                    waitable.get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // This is proper habit for java. If we aren't handling it, pass it on!
                } catch (ExecutionException e) {
                    throw new RuntimeException("Exception processing chat event", e.getCause());
                }
            } else {
                if (event.isCancelled()) {
                    return;
                }

                // Paper Start - (Meh) Support for vanilla world scoreboard name coloring
                String displayName = event.getPlayer().getDisplayName();
                if (this.player.getWorld().paperConfig.useVanillaScoreboardColoring) {
                    displayName = ScoreboardTeam.getPlayerDisplayName(this.player.getTeam(), player.getDisplayName());
                }

                s = String.format(event.getFormat(), displayName, event.getMessage());
                // Paper end
                minecraftServer.console.sendMessage(s);
                if (((LazyPlayerSet) event.getRecipients()).isLazy()) {
                    for (Object recipient : minecraftServer.getPlayerList().players) {
                        ((EntityPlayer) recipient).sendMessage(CraftChatMessage.fromString(s));
                    }
                } else {
                    for (Player recipient : event.getRecipients()) {
                        recipient.sendMessage(s);
                    }
                }
            }
        }
    }
    // CraftBukkit end

    private void handleCommand(String s) {
        MinecraftTimings.playerCommandTimer.startTiming(); // Paper
       // CraftBukkit start - whole method
        if ( org.spigotmc.SpigotConfig.logCommands ) // Spigot
        this.LOGGER.info(this.player.getName() + " issued server command: " + s);

        CraftPlayer player = this.getPlayer();

        PlayerCommandPreprocessEvent event = new PlayerCommandPreprocessEvent(player, s, new LazyPlayerSet(minecraftServer));
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            MinecraftTimings.playerCommandTimer.stopTiming(); // Paper
            return;
        }

        try {
            if (this.server.dispatchCommand(event.getPlayer(), event.getMessage().substring(1))) {
                MinecraftTimings.playerCommandTimer.stopTiming(); // Paper
                return;
            }
        } catch (org.bukkit.command.CommandException ex) {
            player.sendMessage(org.bukkit.ChatColor.RED + "An internal error occurred while attempting to perform this command");
            java.util.logging.Logger.getLogger(PlayerConnection.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            MinecraftTimings.playerCommandTimer.stopTiming(); // Paper
            return;
        }
        MinecraftTimings.playerCommandTimer.stopTiming(); // Paper
        // this.minecraftServer.getCommandHandler().a(this.player, s);
        // CraftBukkit end
    }

    public void a(PacketPlayInArmAnimation packetplayinarmanimation) {
        PlayerConnectionUtils.ensureMainThread(packetplayinarmanimation, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        this.player.resetIdleTimer();
        // CraftBukkit start - Raytrace to look for 'rogue armswings'
        float f1 = this.player.pitch;
        float f2 = this.player.yaw;
        double d0 = this.player.locX;
        double d1 = this.player.locY + (double) this.player.getHeadHeight();
        double d2 = this.player.locZ;
        Vec3D vec3d = new Vec3D(d0, d1, d2);

        float f3 = MathHelper.cos(-f2 * 0.017453292F - 3.1415927F);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - 3.1415927F);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float f6 = MathHelper.sin(-f1 * 0.017453292F);
        float f7 = f4 * f5;
        float f8 = f3 * f5;
        double d3 = player.playerInteractManager.getGameMode()== EnumGamemode.CREATIVE ? 5.0D : 4.5D;
        Vec3D vec3d1 = vec3d.add((double) f7 * d3, (double) f6 * d3, (double) f8 * d3);
        MovingObjectPosition movingobjectposition = this.player.world.rayTrace(vec3d, vec3d1, false);

        if (movingobjectposition == null || movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_AIR, this.player.inventory.getItemInHand(), EnumHand.MAIN_HAND);
        }

        // Arm swing animation
        PlayerAnimationEvent event = new PlayerAnimationEvent(this.getPlayer());
        this.server.getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        // CraftBukkit end
        this.player.a(packetplayinarmanimation.a());
    }

    public void a(PacketPlayInEntityAction packetplayinentityaction) {
        PlayerConnectionUtils.ensureMainThread(packetplayinentityaction, this, this.player.x());
        // CraftBukkit start
        if (this.player.dead) return;
        switch (packetplayinentityaction.b()) {
            case START_SNEAKING:
            case STOP_SNEAKING:
                PlayerToggleSneakEvent event = new PlayerToggleSneakEvent(this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SNEAKING);
                this.server.getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
                break;
            case START_SPRINTING:
            case STOP_SPRINTING:
                PlayerToggleSprintEvent e2 = new PlayerToggleSprintEvent(this.getPlayer(), packetplayinentityaction.b() == PacketPlayInEntityAction.EnumPlayerAction.START_SPRINTING);
                this.server.getPluginManager().callEvent(e2);

                if (e2.isCancelled()) {
                    return;
                }
                break;
        }
        // CraftBukkit end
        this.player.resetIdleTimer();
        IJumpable ijumpable;

        switch (packetplayinentityaction.b()) {
        case START_SNEAKING:
            this.player.setSneaking(true);

            // Paper start - Hang on!
            if (this.player.world.paperConfig.parrotsHangOnBetter) {
                this.player.releaseShoulderEntities();
            }
            // Paper end

            break;

        case STOP_SNEAKING:
            this.player.setSneaking(false);
            break;

        case START_SPRINTING:
            this.player.setSprinting(true);
            break;

        case STOP_SPRINTING:
            this.player.setSprinting(false);
            break;

        case STOP_SLEEPING:
            if (this.player.isSleeping()) {
                this.player.a(false, true, true);
                this.teleportPos = new Vec3D(this.player.locX, this.player.locY, this.player.locZ);
            }
            break;

        case START_RIDING_JUMP:
            if (this.player.bJ() instanceof IJumpable) {
                ijumpable = (IJumpable) this.player.bJ();
                int i = packetplayinentityaction.c();

                if (ijumpable.a() && i > 0) {
                    ijumpable.b_(i);
                }
            }
            break;

        case STOP_RIDING_JUMP:
            if (this.player.bJ() instanceof IJumpable) {
                ijumpable = (IJumpable) this.player.bJ();
                ijumpable.r_();
            }
            break;

        case OPEN_INVENTORY:
            if (this.player.bJ() instanceof EntityHorseAbstract) {
                ((EntityHorseAbstract) this.player.bJ()).c((EntityHuman) this.player);
            }
            break;

        case START_FALL_FLYING:
            if (!this.player.onGround && this.player.motY < 0.0D && !this.player.cP() && !this.player.isInWater()) {
                ItemStack itemstack = this.player.getEquipment(EnumItemSlot.CHEST);

                if (itemstack.getItem() == Items.cS && ItemElytra.d(itemstack)) {
                    this.player.N();
                }
            } else {
                this.player.O();
            }
            break;

        default:
            throw new IllegalArgumentException("Invalid client command!");
        }

    }

    public void a(PacketPlayInUseEntity packetplayinuseentity) {
        PlayerConnectionUtils.ensureMainThread(packetplayinuseentity, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        Entity entity = packetplayinuseentity.a((World) worldserver);
        // Spigot Start
        if ( entity == player && !player.isSpectator() )
        {
            disconnect( "Cannot interact with self!" );
            return;
        }
        // Spigot End

        this.player.resetIdleTimer();
        if (entity != null) {
            boolean flag = this.player.hasLineOfSight(entity);
            double d0 = 36.0D;

            if (!flag) {
                d0 = 9.0D;
            }

            if (this.player.h(entity) < d0) {
                EnumHand enumhand;

                ItemStack itemInHand = this.player.b(packetplayinuseentity.b() == null ? EnumHand.MAIN_HAND : packetplayinuseentity.b()); // CraftBukkit

                if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT
                        || packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                    // CraftBukkit start
                    boolean triggerLeashUpdate = itemInHand != null && itemInHand.getItem() == Items.LEAD && entity instanceof EntityInsentient;
                    Item origItem = this.player.inventory.getItemInHand() == null ? null : this.player.inventory.getItemInHand().getItem();
                    PlayerInteractEntityEvent event;
                    if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                        event = new PlayerInteractEntityEvent((Player) this.getPlayer(), entity.getBukkitEntity(), (packetplayinuseentity.b() == EnumHand.OFF_HAND) ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
                    } else {
                        Vec3D target = packetplayinuseentity.c();
                        event = new PlayerInteractAtEntityEvent((Player) this.getPlayer(), entity.getBukkitEntity(), new org.bukkit.util.Vector(target.x, target.y, target.z), (packetplayinuseentity.b() == EnumHand.OFF_HAND) ? EquipmentSlot.OFF_HAND : EquipmentSlot.HAND);
                    }
                    this.server.getPluginManager().callEvent(event);

                    if (triggerLeashUpdate && (event.isCancelled() || this.player.inventory.getItemInHand() == null || this.player.inventory.getItemInHand().getItem() != Items.LEAD)) {
                        // Refresh the current leash state
                        this.sendPacket(new PacketPlayOutAttachEntity(entity, ((EntityInsentient) entity).getLeashHolder()));
                    }

                    if (event.isCancelled() || this.player.inventory.getItemInHand() == null || this.player.inventory.getItemInHand().getItem() != origItem) {
                        // Refresh the current entity metadata
                        this.sendPacket(new PacketPlayOutEntityMetadata(entity.getId(), entity.datawatcher, true));
                    }

                    if (event.isCancelled()) {
                        this.player.updateInventory(this.player.activeContainer); // Paper - Refresh player inventory
                        return;
                    }
                    // CraftBukkit end
                }

                if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT) {
                    enumhand = packetplayinuseentity.b();
                    this.player.a(entity, enumhand);
                    // CraftBukkit start
                    if (!itemInHand.isEmpty() && itemInHand.getCount() <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
                    enumhand = packetplayinuseentity.b();
                    entity.a(this.player, packetplayinuseentity.c(), enumhand);
                    // CraftBukkit start
                    if (!itemInHand.isEmpty() && itemInHand.getCount() <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                } else if (packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK) {
                    if (entity instanceof EntityItem || entity instanceof EntityExperienceOrb || entity instanceof EntityArrow || (entity == this.player && !player.isSpectator())) { // CraftBukkit
                        this.disconnect(new ChatMessage("multiplayer.disconnect.invalid_entity_attacked", new Object[0]));
                        this.minecraftServer.warning("Player " + this.player.getName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.player.attack(entity);

                    // CraftBukkit start
                    if (!itemInHand.isEmpty() && itemInHand.getCount() <= -1) {
                        this.player.updateInventory(this.player.activeContainer);
                    }
                    // CraftBukkit end
                }
            }
        }
        // Paper start - fire event
        else {
            this.server.getPluginManager().callEvent(new com.destroystokyo.paper.event.player.PlayerUseUnknownEntityEvent(
                this.getPlayer(),
                packetplayinuseentity.getEntityId(),
                packetplayinuseentity.a() == PacketPlayInUseEntity.EnumEntityUseAction.ATTACK,
                packetplayinuseentity.b() == EnumHand.MAIN_HAND ? EquipmentSlot.HAND : EquipmentSlot.OFF_HAND
            ));
        }
        // Paper end

    }

    public void a(PacketPlayInClientCommand packetplayinclientcommand) {
        PlayerConnectionUtils.ensureMainThread(packetplayinclientcommand, this, this.player.x());
        this.player.resetIdleTimer();
        PacketPlayInClientCommand.EnumClientCommand packetplayinclientcommand_enumclientcommand = packetplayinclientcommand.a();

        switch (packetplayinclientcommand_enumclientcommand) {
        case PERFORM_RESPAWN:
            if (this.player.viewingCredits) {
                this.player.viewingCredits = false;
                // this.player = this.minecraftServer.getPlayerList().moveToWorld(this.player, 0, true);
                this.minecraftServer.getPlayerList().changeDimension(this.player, 0, PlayerTeleportEvent.TeleportCause.END_PORTAL); // CraftBukkit - reroute logic through custom portal management
                CriterionTriggers.u.a(this.player, DimensionManager.THE_END, DimensionManager.OVERWORLD);
            } else {
                if (this.player.getHealth() > 0.0F) {
                    return;
                }

                this.player = this.minecraftServer.getPlayerList().moveToWorld(this.player, 0, false);
                if (this.minecraftServer.isHardcore()) {
                    this.player.a(EnumGamemode.SPECTATOR);
                    this.player.x().getGameRules().set("spectatorsGenerateChunks", "false");
                }
            }
            break;

        case REQUEST_STATS:
            this.player.getStatisticManager().a(this.player);
        }

    }

    public void a(PacketPlayInCloseWindow packetplayinclosewindow) {
        PlayerConnectionUtils.ensureMainThread(packetplayinclosewindow, this, this.player.x());

        if (this.player.isFrozen()) return; // CraftBukkit
        CraftEventFactory.handleInventoryCloseEvent(this.player, org.bukkit.event.inventory.InventoryCloseEvent.Reason.PLAYER); // CraftBukkit // Paper

        this.player.r();
    }

    public void a(PacketPlayInWindowClick packetplayinwindowclick) {
        PlayerConnectionUtils.ensureMainThread(packetplayinwindowclick, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        this.player.resetIdleTimer();
        if (this.player.activeContainer.windowId == packetplayinwindowclick.a() && this.player.activeContainer.c(this.player) && this.player.activeContainer.canUse(this.player)) { // CraftBukkit
            boolean cancelled = this.player.isSpectator(); // CraftBukkit - see below if
            if (false/*this.player.isSpectator()*/) { // CraftBukkit
                NonNullList nonnulllist = NonNullList.a();

                for (int i = 0; i < this.player.activeContainer.slots.size(); ++i) {
                    nonnulllist.add(((Slot) this.player.activeContainer.slots.get(i)).getItem());
                }

                this.player.a(this.player.activeContainer, nonnulllist);
            } else {
                // CraftBukkit start - Call InventoryClickEvent
                if (packetplayinwindowclick.b() < -1 && packetplayinwindowclick.b() != -999) {
                    return;
                }

                InventoryView inventory = this.player.activeContainer.getBukkitView();
                SlotType type = CraftInventoryView.getSlotType(inventory, packetplayinwindowclick.b());

                InventoryClickEvent event;
                ClickType click = ClickType.UNKNOWN;
                InventoryAction action = InventoryAction.UNKNOWN;

                ItemStack itemstack = ItemStack.a;

                switch (packetplayinwindowclick.f()) {
                    case PICKUP:
                        if (packetplayinwindowclick.c() == 0) {
                            click = ClickType.LEFT;
                        } else if (packetplayinwindowclick.c() == 1) {
                            click = ClickType.RIGHT;
                        }
                        if (packetplayinwindowclick.c() == 0 || packetplayinwindowclick.c() == 1) {
                            action = InventoryAction.NOTHING; // Don't want to repeat ourselves
                            if (packetplayinwindowclick.b() == -999) {
                                if (!player.inventory.getCarried().isEmpty()) {
                                    action = packetplayinwindowclick.c() == 0 ? InventoryAction.DROP_ALL_CURSOR : InventoryAction.DROP_ONE_CURSOR;
                                }
                            } else if (packetplayinwindowclick.b() < 0)  {
                                action = InventoryAction.NOTHING;
                            } else {
                                Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                                if (slot != null) {
                                    ItemStack clickedItem = slot.getItem();
                                    ItemStack cursor = player.inventory.getCarried();
                                    if (clickedItem.isEmpty()) {
                                        if (!cursor.isEmpty()) {
                                            action = packetplayinwindowclick.c() == 0 ? InventoryAction.PLACE_ALL : InventoryAction.PLACE_ONE;
                                        }
                                    } else if (slot.isAllowed(player)) {
                                        if (cursor.isEmpty()) {
                                            action = packetplayinwindowclick.c() == 0 ? InventoryAction.PICKUP_ALL : InventoryAction.PICKUP_HALF;
                                        } else if (slot.isAllowed(cursor)) {
                                            if (clickedItem.doMaterialsMatch(cursor) && ItemStack.equals(clickedItem, cursor)) {
                                                int toPlace = packetplayinwindowclick.c() == 0 ? cursor.getCount() : 1;
                                                toPlace = Math.min(toPlace, clickedItem.getMaxStackSize() - clickedItem.getCount());
                                                toPlace = Math.min(toPlace, slot.inventory.getMaxStackSize() - clickedItem.getCount());
                                                if (toPlace == 1) {
                                                    action = InventoryAction.PLACE_ONE;
                                                } else if (toPlace == cursor.getCount()) {
                                                    action = InventoryAction.PLACE_ALL;
                                                } else if (toPlace < 0) {
                                                    action = toPlace != -1 ? InventoryAction.PICKUP_SOME : InventoryAction.PICKUP_ONE; // this happens with oversized stacks
                                                } else if (toPlace != 0) {
                                                    action = InventoryAction.PLACE_SOME;
                                                }
                                            } else if (cursor.getCount() <= slot.getMaxStackSize()) {
                                                action = InventoryAction.SWAP_WITH_CURSOR;
                                            }
                                        } else if (cursor.getItem() == clickedItem.getItem() && (!cursor.usesData() || cursor.getData() == clickedItem.getData()) && ItemStack.equals(cursor, clickedItem)) {
                                            if (clickedItem.getCount() >= 0) {
                                                if (clickedItem.getCount() + cursor.getCount() <= cursor.getMaxStackSize()) {
                                                    // As of 1.5, this is result slots only
                                                    action = InventoryAction.PICKUP_ALL;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        break;
                    // TODO check on updates
                    case QUICK_MOVE:
                        if (packetplayinwindowclick.c() == 0) {
                            click = ClickType.SHIFT_LEFT;
                        } else if (packetplayinwindowclick.c() == 1) {
                            click = ClickType.SHIFT_RIGHT;
                        }
                        if (packetplayinwindowclick.c() == 0 || packetplayinwindowclick.c() == 1) {
                            if (packetplayinwindowclick.b() < 0) {
                                action = InventoryAction.NOTHING;
                            } else {
                                Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                                if (slot != null && slot.isAllowed(this.player) && slot.hasItem()) {
                                    action = InventoryAction.MOVE_TO_OTHER_INVENTORY;
                                } else {
                                    action = InventoryAction.NOTHING;
                                }
                            }
                        }
                        break;
                    case SWAP:
                        if (packetplayinwindowclick.c() >= 0 && packetplayinwindowclick.c() < 9) {
                            click = ClickType.NUMBER_KEY;
                            Slot clickedSlot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                            if (clickedSlot.isAllowed(player)) {
                                ItemStack hotbar = this.player.inventory.getItem(packetplayinwindowclick.c());
                                boolean canCleanSwap = hotbar.isEmpty() || (clickedSlot.inventory == player.inventory && clickedSlot.isAllowed(hotbar)); // the slot will accept the hotbar item
                                if (clickedSlot.hasItem()) {
                                    if (canCleanSwap) {
                                        action = InventoryAction.HOTBAR_SWAP;
                                    } else {
                                        action = InventoryAction.HOTBAR_MOVE_AND_READD;
                                    }
                                } else if (!clickedSlot.hasItem() && !hotbar.isEmpty() && clickedSlot.isAllowed(hotbar)) {
                                    action = InventoryAction.HOTBAR_SWAP;
                                } else {
                                    action = InventoryAction.NOTHING;
                                }
                            } else {
                                action = InventoryAction.NOTHING;
                            }
                        }
                        break;
                    case CLONE:
                        if (packetplayinwindowclick.c() == 2) {
                            click = ClickType.MIDDLE;
                            if (packetplayinwindowclick.b() < 0) { // Paper - GH-404
                                action = InventoryAction.NOTHING;
                            } else {
                                Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                                if (slot != null && slot.hasItem() && player.abilities.canInstantlyBuild && player.inventory.getCarried().isEmpty()) {
                                    action = InventoryAction.CLONE_STACK;
                                } else {
                                    action = InventoryAction.NOTHING;
                                }
                            }
                        } else {
                            click = ClickType.UNKNOWN;
                            action = InventoryAction.UNKNOWN;
                        }
                        break;
                    case THROW:
                        if (packetplayinwindowclick.b() >= 0) {
                            if (packetplayinwindowclick.c() == 0) {
                                click = ClickType.DROP;
                                Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                                if (slot != null && slot.hasItem() && slot.isAllowed(player) && !slot.getItem().isEmpty() && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                                    action = InventoryAction.DROP_ONE_SLOT;
                                } else {
                                    action = InventoryAction.NOTHING;
                                }
                            } else if (packetplayinwindowclick.c() == 1) {
                                click = ClickType.CONTROL_DROP;
                                Slot slot = this.player.activeContainer.getSlot(packetplayinwindowclick.b());
                                if (slot != null && slot.hasItem() && slot.isAllowed(player) && !slot.getItem().isEmpty() && slot.getItem().getItem() != Item.getItemOf(Blocks.AIR)) {
                                    action = InventoryAction.DROP_ALL_SLOT;
                                } else {
                                    action = InventoryAction.NOTHING;
                                }
                            }
                        } else {
                            // Sane default (because this happens when they are holding nothing. Don't ask why.)
                            click = ClickType.LEFT;
                            if (packetplayinwindowclick.c() == 1) {
                                click = ClickType.RIGHT;
                            }
                            action = InventoryAction.NOTHING;
                        }
                        break;
                    case QUICK_CRAFT:
                        itemstack = this.player.activeContainer.a(packetplayinwindowclick.b(), packetplayinwindowclick.c(), packetplayinwindowclick.f(), this.player);
                        break;
                    case PICKUP_ALL:
                        click = ClickType.DOUBLE_CLICK;
                        action = InventoryAction.NOTHING;
                        if (packetplayinwindowclick.b() >= 0 && !this.player.inventory.getCarried().isEmpty()) {
                            ItemStack cursor = this.player.inventory.getCarried();
                            action = InventoryAction.NOTHING;
                            // Quick check for if we have any of the item
                            if (inventory.getTopInventory().contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem()))) || inventory.getBottomInventory().contains(org.bukkit.Material.getMaterial(Item.getId(cursor.getItem())))) {
                                action = InventoryAction.COLLECT_TO_CURSOR;
                            }
                        }
                        break;
                    default:
                        break;
                }

                if (packetplayinwindowclick.f() != InventoryClickType.QUICK_CRAFT) {
                    if (click == ClickType.NUMBER_KEY) {
                        event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.b(), click, action, packetplayinwindowclick.c());
                    } else {
                        event = new InventoryClickEvent(inventory, type, packetplayinwindowclick.b(), click, action);
                    }

                    org.bukkit.inventory.Inventory top = inventory.getTopInventory();
                    if (packetplayinwindowclick.b() == 0 && top instanceof CraftingInventory) {
                        org.bukkit.inventory.Recipe recipe = ((CraftingInventory) top).getRecipe();
                        if (recipe != null) {
                            if (click == ClickType.NUMBER_KEY) {
                                event = new CraftItemEvent(recipe, inventory, type, packetplayinwindowclick.b(), click, action, packetplayinwindowclick.c());
                            } else {
                                event = new CraftItemEvent(recipe, inventory, type, packetplayinwindowclick.b(), click, action);
                            }
                        }
                    }

                    event.setCancelled(cancelled);
                    Container oldContainer = this.player.activeContainer; // SPIGOT-1224
                    server.getPluginManager().callEvent(event);
                    if (this.player.activeContainer != oldContainer) {
                        return;
                    }

                    switch (event.getResult()) {
                        case ALLOW:
                        case DEFAULT:
                            itemstack = this.player.activeContainer.a(packetplayinwindowclick.b(), packetplayinwindowclick.c(), packetplayinwindowclick.f(), this.player);
                            break;
                        case DENY:
                            /* Needs enum constructor in InventoryAction
                            if (action.modifiesOtherSlots()) {

                            } else {
                                if (action.modifiesCursor()) {
                                    this.player.playerConnection.sendPacket(new Packet103SetSlot(-1, -1, this.player.inventory.getCarried()));
                                }
                                if (action.modifiesClicked()) {
                                    this.player.playerConnection.sendPacket(new Packet103SetSlot(this.player.activeContainer.windowId, packet102windowclick.slot, this.player.activeContainer.getSlot(packet102windowclick.slot).getItem()));
                                }
                            }*/
                            switch (action) {
                                // Modified other slots
                                case PICKUP_ALL:
                                case MOVE_TO_OTHER_INVENTORY:
                                case HOTBAR_MOVE_AND_READD:
                                case HOTBAR_SWAP:
                                case COLLECT_TO_CURSOR:
                                case UNKNOWN:
                                    this.player.updateInventory(this.player.activeContainer);
                                    break;
                                // Modified cursor and clicked
                                case PICKUP_SOME:
                                case PICKUP_HALF:
                                case PICKUP_ONE:
                                case PLACE_ALL:
                                case PLACE_SOME:
                                case PLACE_ONE:
                                case SWAP_WITH_CURSOR:
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, packetplayinwindowclick.b(), this.player.activeContainer.getSlot(packetplayinwindowclick.b()).getItem()));
                                    break;
                                // Modified clicked only
                                case DROP_ALL_SLOT:
                                case DROP_ONE_SLOT:
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.player.activeContainer.windowId, packetplayinwindowclick.b(), this.player.activeContainer.getSlot(packetplayinwindowclick.b()).getItem()));
                                    break;
                                // Modified cursor only
                                case DROP_ALL_CURSOR:
                                case DROP_ONE_CURSOR:
                                case CLONE_STACK:
                                    this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, this.player.inventory.getCarried()));
                                    break;
                                // Nothing
                                case NOTHING:
                                    break;
                            }
                            return;
                    }

                    if (event instanceof CraftItemEvent) {
                        // Need to update the inventory on crafting to
                        // correctly support custom recipes
                        player.updateInventory(player.activeContainer);
                    }
                }
                // CraftBukkit end
                if (ItemStack.matches(packetplayinwindowclick.e(), itemstack)) {
                    this.player.playerConnection.sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), true));
                    this.player.f = true;
                    this.player.activeContainer.b();
                    this.player.broadcastCarriedItem();
                    this.player.f = false;
                } else {
                    this.k.a(this.player.activeContainer.windowId, Short.valueOf(packetplayinwindowclick.d()));
                    this.player.playerConnection.sendPacket(new PacketPlayOutTransaction(packetplayinwindowclick.a(), packetplayinwindowclick.d(), false));
                    this.player.activeContainer.a(this.player, false);
                    NonNullList nonnulllist1 = NonNullList.a();

                    for (int j = 0; j < this.player.activeContainer.slots.size(); ++j) {
                        ItemStack itemstack1 = ((Slot) this.player.activeContainer.slots.get(j)).getItem();
                        ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.a : itemstack1;

                        nonnulllist1.add(itemstack2);
                    }

                    this.player.a(this.player.activeContainer, nonnulllist1);
                }
            }
        }

    }

    public void a(PacketPlayInAutoRecipe packetplayinautorecipe) {
        PlayerConnectionUtils.ensureMainThread(packetplayinautorecipe, this, this.player.x());
        this.player.resetIdleTimer();
        if (!this.player.isSpectator() && this.player.activeContainer.windowId == packetplayinautorecipe.a() && this.player.activeContainer.c(this.player)) {
            this.H.a(this.player, packetplayinautorecipe.b(), packetplayinautorecipe.c());
        }
    }

    public void a(PacketPlayInEnchantItem packetplayinenchantitem) {
        PlayerConnectionUtils.ensureMainThread(packetplayinenchantitem, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        this.player.resetIdleTimer();
        if (this.player.activeContainer.windowId == packetplayinenchantitem.a() && this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, packetplayinenchantitem.b());
            this.player.activeContainer.b();
        }

    }

    public void a(PacketPlayInSetCreativeSlot packetplayinsetcreativeslot) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsetcreativeslot, this, this.player.x());
        if (this.player.playerInteractManager.isCreative()) {
            boolean flag = packetplayinsetcreativeslot.a() < 0;
            ItemStack itemstack = packetplayinsetcreativeslot.getItemStack();

            if (!itemstack.isEmpty() && itemstack.hasTag() && itemstack.getTag().hasKeyOfType("BlockEntityTag", 10)) {
                NBTTagCompound nbttagcompound = itemstack.getTag().getCompound("BlockEntityTag");

                if (nbttagcompound.hasKey("x") && nbttagcompound.hasKey("y") && nbttagcompound.hasKey("z")) {
                    BlockPosition blockposition = new BlockPosition(nbttagcompound.getInt("x"), nbttagcompound.getInt("y"), nbttagcompound.getInt("z"));
                    TileEntity tileentity = this.player.world.getTileEntity(blockposition);

                    if (tileentity != null) {
                        NBTTagCompound nbttagcompound1 = tileentity.save(new NBTTagCompound());

                        nbttagcompound1.remove("x");
                        nbttagcompound1.remove("y");
                        nbttagcompound1.remove("z");
                        itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound1);
                    }
                }
            }

            boolean flag1 = packetplayinsetcreativeslot.a() >= 1 && packetplayinsetcreativeslot.a() <= 45;
            // CraftBukkit - Add invalidItems check
            boolean flag2 = itemstack.isEmpty() || itemstack.getData() >= 0 && itemstack.getCount() <= 64 && !itemstack.isEmpty() && (!invalidItems.contains(Item.getId(itemstack.getItem())) || !org.spigotmc.SpigotConfig.filterCreativeItems); // Spigot
            if (flag || (flag1 && !ItemStack.matches(this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem(), packetplayinsetcreativeslot.getItemStack()))) { // Insist on valid slot
                // CraftBukkit start - Call click event
                InventoryView inventory = this.player.defaultContainer.getBukkitView();
                org.bukkit.inventory.ItemStack item = CraftItemStack.asBukkitCopy(packetplayinsetcreativeslot.getItemStack());

                SlotType type = SlotType.QUICKBAR;
                if (flag) {
                    type = SlotType.OUTSIDE;
                } else if (packetplayinsetcreativeslot.a() < 36) {
                    if (packetplayinsetcreativeslot.a() >= 5 && packetplayinsetcreativeslot.a() < 9) {
                        type = SlotType.ARMOR;
                    } else {
                        type = SlotType.CONTAINER;
                    }
                }
                InventoryCreativeEvent event = new InventoryCreativeEvent(inventory, type, flag ? -999 : packetplayinsetcreativeslot.a(), item);
                server.getPluginManager().callEvent(event);

                itemstack = CraftItemStack.asNMSCopy(event.getCursor());

                switch (event.getResult()) {
                case ALLOW:
                    // Plugin cleared the id / stacksize checks
                    flag2 = true;
                    break;
                case DEFAULT:
                    break;
                case DENY:
                    // Reset the slot
                    if (packetplayinsetcreativeslot.a() >= 0) {
                        this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(this.player.defaultContainer.windowId, packetplayinsetcreativeslot.a(), this.player.defaultContainer.getSlot(packetplayinsetcreativeslot.a()).getItem()));
                        this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-1, -1, ItemStack.a));
                    }
                    return;
                }
            }
            // CraftBukkit end

            if (flag1 && flag2) {
                if (itemstack.isEmpty()) {
                    this.player.defaultContainer.setItem(packetplayinsetcreativeslot.a(), ItemStack.a);
                } else {
                    this.player.defaultContainer.setItem(packetplayinsetcreativeslot.a(), itemstack);
                }

                this.player.defaultContainer.a(this.player, true);
            } else if (flag && flag2 && this.j < 200) {
                this.j += 20;
                EntityItem entityitem = this.player.drop(itemstack, true);

                if (entityitem != null) {
                    entityitem.j();
                }
            }
        }

    }

    public void a(PacketPlayInTransaction packetplayintransaction) {
        PlayerConnectionUtils.ensureMainThread(packetplayintransaction, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        Short oshort = (Short) this.k.get(this.player.activeContainer.windowId);

        if (oshort != null && packetplayintransaction.b() == oshort.shortValue() && this.player.activeContainer.windowId == packetplayintransaction.a() && !this.player.activeContainer.c(this.player) && !this.player.isSpectator()) {
            this.player.activeContainer.a(this.player, true);
        }

    }

    public void a(PacketPlayInUpdateSign packetplayinupdatesign) {
        PlayerConnectionUtils.ensureMainThread(packetplayinupdatesign, this, this.player.x());
        if (this.player.isFrozen()) return; // CraftBukkit
        this.player.resetIdleTimer();
        WorldServer worldserver = this.minecraftServer.getWorldServer(this.player.dimension);
        BlockPosition blockposition = packetplayinupdatesign.a();

        if (worldserver.isLoaded(blockposition)) {
            IBlockData iblockdata = worldserver.getType(blockposition);
            TileEntity tileentity = worldserver.getTileEntity(blockposition);

            if (!(tileentity instanceof TileEntitySign)) {
                return;
            }

            TileEntitySign tileentitysign = (TileEntitySign) tileentity;

            if (!tileentitysign.a() || tileentitysign.e() != this.player) {
                this.minecraftServer.warning("Player " + this.player.getName() + " just tried to change non-editable sign");
                this.sendPacket(tileentity.getUpdatePacket()); // CraftBukkit
                return;
            }

            String[] astring = packetplayinupdatesign.b();

            // CraftBukkit start
            Player player = this.server.getPlayer(this.player);
            int x = packetplayinupdatesign.a().getX();
            int y = packetplayinupdatesign.a().getY();
            int z = packetplayinupdatesign.a().getZ();
            String[] lines = new String[4];

            for (int i = 0; i < astring.length; ++i) {
                lines[i] = SharedConstants.a(astring[i]); //Paper - Replaced with anvil color stripping method to stop exploits that allow colored signs to be created.
            }
            SignChangeEvent event = new SignChangeEvent((org.bukkit.craftbukkit.block.CraftBlock) player.getWorld().getBlockAt(x, y, z), this.server.getPlayer(this.player), lines);
            this.server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                System.arraycopy(org.bukkit.craftbukkit.block.CraftSign.sanitizeLines(event.getLines()), 0, tileentitysign.lines, 0, 4);
                tileentitysign.isEditable = false;
             }
            // CraftBukkit end

            tileentitysign.update();
            worldserver.notify(blockposition, iblockdata, iblockdata, 3);
        }

    }

    public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
        //PlayerConnectionUtils.ensureMainThread(packetplayinkeepalive, this, this.player.x()); // CraftBukkit // Paper - This shouldn't be on the main thread
        if (this.g && packetplayinkeepalive.a() == this.h) {
            int i = (int) (this.d() - this.f);

            this.player.ping = (this.player.ping * 3 + i) / 4;
            this.g = false;
        } else if (!this.player.getName().equals(this.minecraftServer.Q())) {
            // Paper start - This needs to be handled on the main thread for plugins
            PlayerConnection.LOGGER.warn("{} sent an invalid keepalive! pending keepalive: {} got id: {} expected id: {}",
                    this.player.getName(), this.isPendingPing(), packetplayinkeepalive.a(), this.getKeepAliveID());
            minecraftServer.postToMainThread(() -> {
                    this.disconnect(new ChatMessage("disconnect.timeout", new Object[0]));
            });
            // Paper end
        }

    }

    private long getCurrentMillis() { return d(); } // Paper - OBFHELPER
    private long d() {
        return System.nanoTime() / 1000000L;
    }

    public void a(PacketPlayInAbilities packetplayinabilities) {
        PlayerConnectionUtils.ensureMainThread(packetplayinabilities, this, this.player.x());
        // CraftBukkit start
        if (this.player.abilities.canFly && this.player.abilities.isFlying != packetplayinabilities.isFlying()) {
            PlayerToggleFlightEvent event = new PlayerToggleFlightEvent(this.server.getPlayer(this.player), packetplayinabilities.isFlying());
            this.server.getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.player.abilities.isFlying = packetplayinabilities.isFlying(); // Actually set the player's flying status
            } else {
                this.player.updateAbilities(); // Tell the player their ability was reverted
            }
        }
        // CraftBukkit end
    }

    // Paper start - async tab completion
    public void a(PacketPlayInTabComplete packet) {
        // CraftBukkit start
        if (tabSpamLimiter.addAndGet(com.destroystokyo.paper.PaperConfig.tabSpamIncrement) > com.destroystokyo.paper.PaperConfig.tabSpamLimit && !this.minecraftServer.getPlayerList().isOp(this.player.getProfile())) { // Paper start - split and make configurable
            minecraftServer.postToMainThread(() -> this.disconnect(new ChatMessage("disconnect.spam", new Object[0])));
            return;
        }
        // CraftBukkit end

        com.destroystokyo.paper.event.server.AsyncTabCompleteEvent event;
        java.util.List<String> completions = new ArrayList<>();
        BlockPosition blockpos = packet.b();
        String buffer = packet.a();
        boolean isCommand = buffer.startsWith("/") || packet.c();
        event = new com.destroystokyo.paper.event.server.AsyncTabCompleteEvent(this.getPlayer(), completions,
            buffer, isCommand, blockpos != null ? MCUtil.toLocation(player.world, blockpos) : null);
        event.callEvent();
        completions = event.isCancelled() ? com.google.common.collect.ImmutableList.of() : event.getCompletions();
        if (event.isCancelled() || event.isHandled()) {
            // Still fire sync event with the provided completions, if someone is listening
            if (!event.isCancelled() && org.bukkit.event.server.TabCompleteEvent.getHandlerList().getRegisteredListeners().length > 0) {
                java.util.List<String> finalCompletions = completions;
                Waitable<java.util.List<String>> syncCompletions = new Waitable<java.util.List<String>>() {
                    @Override
                    protected java.util.List<String> evaluate() {
                        org.bukkit.event.server.TabCompleteEvent syncEvent = new org.bukkit.event.server.TabCompleteEvent(PlayerConnection.this.getPlayer(), buffer, finalCompletions, isCommand, blockpos != null ? MCUtil.toLocation(player.world, blockpos) : null);
                        return syncEvent.callEvent() ? syncEvent.getCompletions() : com.google.common.collect.ImmutableList.of();
                    }
                };
                server.getServer().processQueue.add(syncCompletions);
                try {
                    completions = syncCompletions.get();
                } catch (InterruptedException | ExecutionException e1) {
                    e1.printStackTrace();
                }
            }

            this.player.playerConnection.sendPacket(new PacketPlayOutTabComplete(completions.toArray(new String[completions.size()])));
            return;
        }
        minecraftServer.postToMainThread(() -> {
            java.util.List<String> syncCompletions = this.minecraftServer.tabCompleteCommand(this.player, buffer, blockpos, isCommand);
            this.player.playerConnection.sendPacket(new PacketPlayOutTabComplete(syncCompletions.toArray(new String[syncCompletions.size()])));
        });
        // Paper end
    }

    public void a(PacketPlayInSettings packetplayinsettings) {
        PlayerConnectionUtils.ensureMainThread(packetplayinsettings, this, this.player.x());
        this.player.a(packetplayinsettings);
    }

    public void a(PacketPlayInCustomPayload packetplayincustompayload) {
        PlayerConnectionUtils.ensureMainThread(packetplayincustompayload, this, this.player.x());
        String s = packetplayincustompayload.a();
        PacketDataSerializer packetdataserializer;
        ItemStack itemstack;
        ItemStack itemstack1;

        if ("MC|BEdit".equals(s)) {
            // CraftBukkit start
            if (this.lastBookTick + 20 > MinecraftServer.currentTick) {
                this.disconnect("Book edited too quickly!");
                return;
            }
            this.lastBookTick = MinecraftServer.currentTick;
            // CraftBukkit end
            packetdataserializer = packetplayincustompayload.b();

            try {
                itemstack = packetdataserializer.k();
                if (itemstack.isEmpty()) {
                    return;
                }

                if (!ItemBookAndQuill.b(itemstack.getTag())) {
                    throw new IOException("Invalid book tag!");
                }

                itemstack1 = this.player.getItemInMainHand();
                if (itemstack1.isEmpty()) {
                    return;
                }

                if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack.getItem() == itemstack1.getItem()) {
                    itemstack1 = new ItemStack(Items.WRITABLE_BOOK); // CraftBukkit
                    itemstack1.a("pages", (NBTBase) itemstack.getTag().getList("pages", 8));
                    CraftEventFactory.handleEditBookEvent(player, itemstack1); // CraftBukkit
                }
            } catch (Exception exception) {
                IllegalPacketEvent.process(player.getBukkitEntity(), "InvalidBookEdit", "Invalid book data!", exception); // Paper
            }
        } else {
            String s1;

            if ("MC|BSign".equals(s)) {
                // CraftBukkit start
                if (this.lastBookTick + 20 > MinecraftServer.currentTick) {
                    this.disconnect("Book edited too quickly!");
                    return;
                }
                this.lastBookTick = MinecraftServer.currentTick;
                // CraftBukkit end
                packetdataserializer = packetplayincustompayload.b();

                try {
                    itemstack = packetdataserializer.k();
                    if (itemstack.isEmpty()) {
                        return;
                    }

                    if (!ItemWrittenBook.b(itemstack.getTag())) {
                        throw new IOException("Invalid book tag!");
                    }

                    itemstack1 = this.player.getItemInMainHand();
                    if (itemstack1.isEmpty()) {
                        return;
                    }

                    if (itemstack.getItem() == Items.WRITABLE_BOOK && itemstack1.getItem() == Items.WRITABLE_BOOK) {
                        ItemStack itemstack2 = new ItemStack(Items.WRITTEN_BOOK);

                        itemstack2.a("author", (NBTBase) (new NBTTagString(this.player.getName())));
                        itemstack2.a("title", (NBTBase) (new NBTTagString(itemstack.getTag().getString("title"))));
                        NBTTagList nbttaglist = itemstack.getTag().getList("pages", 8);

                        for (int i = 0; i < nbttaglist.size(); ++i) {
                            s1 = nbttaglist.getString(i);
                            ChatComponentText chatcomponenttext = new ChatComponentText(s1);

                            s1 = IChatBaseComponent.ChatSerializer.a((IChatBaseComponent) chatcomponenttext);
                            nbttaglist.a(i, new NBTTagString(s1));
                        }

                        itemstack2.a("pages", (NBTBase) nbttaglist);
                        CraftEventFactory.handleEditBookEvent(player, itemstack2); // CraftBukkit
                    }
                } catch (Exception exception1) {
                    IllegalPacketEvent.process(player.getBukkitEntity(), "InvalidBookSign", "Invalid book data!", exception1); // Paper
                }
            } else if ("MC|TrSel".equals(s)) {
                try {
                    int j = packetplayincustompayload.b().readInt();
                    Container container = this.player.activeContainer;

                    if (container instanceof ContainerMerchant) {
                        ((ContainerMerchant) container).d(j);
                    }
                } catch (Exception exception2) {
                    IllegalPacketEvent.process(player.getBukkitEntity(), "InvalidTrade", "Invalid trade data!", exception2); // Paper
                }
            } else {
                TileEntity tileentity;

                if ("MC|AdvCmd".equals(s)) {
                    if (!this.minecraftServer.getEnableCommandBlock()) {
                        this.player.sendMessage(new ChatMessage("advMode.notEnabled", new Object[0]));
                        return;
                    }

                    if (!this.player.isCreativeAndOp()) {
                        this.player.sendMessage(new ChatMessage("advMode.notAllowed", new Object[0]));
                        return;
                    }

                    packetdataserializer = packetplayincustompayload.b();

                    try {
                        byte b0 = packetdataserializer.readByte();
                        CommandBlockListenerAbstract commandblocklistenerabstract = null;

                        if (b0 == 0) {
                            tileentity = this.player.world.getTileEntity(new BlockPosition(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt()));
                            if (tileentity instanceof TileEntityCommand) {
                                commandblocklistenerabstract = ((TileEntityCommand) tileentity).getCommandBlock();
                            }
                        } else if (b0 == 1) {
                            Entity entity = this.player.world.getEntity(packetdataserializer.readInt());

                            if (entity instanceof EntityMinecartCommandBlock) {
                                commandblocklistenerabstract = ((EntityMinecartCommandBlock) entity).getCommandBlock();
                            }
                        }

                        String s2 = packetdataserializer.e(packetdataserializer.readableBytes());
                        boolean flag = packetdataserializer.readBoolean();

                        if (commandblocklistenerabstract != null) {
                            commandblocklistenerabstract.setCommand(s2);
                            commandblocklistenerabstract.a(flag);
                            if (!flag) {
                                commandblocklistenerabstract.b((IChatBaseComponent) null);
                            }

                            commandblocklistenerabstract.i();
                            this.player.sendMessage(new ChatMessage("advMode.setCommand.success", new Object[] { s2}));
                        }
                    } catch (Exception exception3) {
                        PlayerConnection.LOGGER.error("Couldn\'t set command block", exception3);
                        this.disconnect("Invalid command data!"); // CraftBukkit
                    }
                } else if ("MC|AutoCmd".equals(s)) {
                    if (!this.minecraftServer.getEnableCommandBlock()) {
                        this.player.sendMessage(new ChatMessage("advMode.notEnabled", new Object[0]));
                        return;
                    }

                    if (!this.player.isCreativeAndOp()) {
                        this.player.sendMessage(new ChatMessage("advMode.notAllowed", new Object[0]));
                        return;
                    }

                    packetdataserializer = packetplayincustompayload.b();

                    try {
                        CommandBlockListenerAbstract commandblocklistenerabstract1 = null;
                        TileEntityCommand tileentitycommand = null;
                        BlockPosition blockposition = new BlockPosition(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt());
                        TileEntity tileentity1 = this.player.world.getTileEntity(blockposition);

                        if (tileentity1 instanceof TileEntityCommand) {
                            tileentitycommand = (TileEntityCommand) tileentity1;
                            commandblocklistenerabstract1 = tileentitycommand.getCommandBlock();
                        }

                        String s3 = packetdataserializer.e(packetdataserializer.readableBytes());
                        boolean flag1 = packetdataserializer.readBoolean();
                        TileEntityCommand.Type tileentitycommand_type = TileEntityCommand.Type.valueOf(packetdataserializer.e(16));
                        boolean flag2 = packetdataserializer.readBoolean();
                        boolean flag3 = packetdataserializer.readBoolean();

                        if (commandblocklistenerabstract1 != null) {
                            EnumDirection enumdirection = (EnumDirection) this.player.world.getType(blockposition).get(BlockCommand.a);
                            IBlockData iblockdata;

                            switch (tileentitycommand_type) {
                            case SEQUENCE:
                                iblockdata = Blocks.dd.getBlockData();
                                this.player.world.setTypeAndData(blockposition, iblockdata.set(BlockCommand.a, enumdirection).set(BlockCommand.b, Boolean.valueOf(flag2)), 2);
                                break;

                            case AUTO:
                                iblockdata = Blocks.dc.getBlockData();
                                this.player.world.setTypeAndData(blockposition, iblockdata.set(BlockCommand.a, enumdirection).set(BlockCommand.b, Boolean.valueOf(flag2)), 2);
                                break;

                            case REDSTONE:
                                iblockdata = Blocks.COMMAND_BLOCK.getBlockData();
                                this.player.world.setTypeAndData(blockposition, iblockdata.set(BlockCommand.a, enumdirection).set(BlockCommand.b, Boolean.valueOf(flag2)), 2);
                            }

                            tileentity1.A();
                            this.player.world.setTileEntity(blockposition, tileentity1);
                            commandblocklistenerabstract1.setCommand(s3);
                            commandblocklistenerabstract1.a(flag1);
                            if (!flag1) {
                                commandblocklistenerabstract1.b((IChatBaseComponent) null);
                            }

                            tileentitycommand.b(flag3);
                            commandblocklistenerabstract1.i();
                            if (!UtilColor.b(s3)) {
                                this.player.sendMessage(new ChatMessage("advMode.setCommand.success", new Object[] { s3}));
                            }
                        }
                    } catch (Exception exception4) {
                        PlayerConnection.LOGGER.error("Couldn\'t set command block", exception4);
                        this.disconnect("Invalid command data!"); // CraftBukkit
                    }
                } else {
                    int k;

                    if ("MC|Beacon".equals(s)) {
                        if (this.player.activeContainer instanceof ContainerBeacon) {
                            try {
                                packetdataserializer = packetplayincustompayload.b();
                                k = packetdataserializer.readInt();
                                int l = packetdataserializer.readInt();
                                ContainerBeacon containerbeacon = (ContainerBeacon) this.player.activeContainer;
                                Slot slot = containerbeacon.getSlot(0);

                                if (slot.hasItem()) {
                                    slot.a(1);
                                    IInventory iinventory = containerbeacon.e();

                                    iinventory.setProperty(1, k);
                                    iinventory.setProperty(2, l);
                                    iinventory.update();
                                }
                            } catch (Exception exception5) {
                                IllegalPacketEvent.process(player.getBukkitEntity(), "InvalidBeacon", "Invalid beacon data!", exception5); // Paper
                            }
                        }
                    } else if ("MC|ItemName".equals(s)) {
                        if (this.player.activeContainer instanceof ContainerAnvil) {
                            ContainerAnvil containeranvil = (ContainerAnvil) this.player.activeContainer;

                            if (packetplayincustompayload.b() != null && packetplayincustompayload.b().readableBytes() >= 1) {
                                String s4 = SharedConstants.a(packetplayincustompayload.b().e(32767));

                                if (s4.length() <= 35) {
                                    containeranvil.a(s4);
                                }
                            } else {
                                containeranvil.a("");
                            }
                        }
                    } else if ("MC|Struct".equals(s)) {
                        if (!this.player.isCreativeAndOp()) {
                            return;
                        }

                        packetdataserializer = packetplayincustompayload.b();

                        try {
                            BlockPosition blockposition1 = new BlockPosition(packetdataserializer.readInt(), packetdataserializer.readInt(), packetdataserializer.readInt());
                            IBlockData iblockdata1 = this.player.world.getType(blockposition1);

                            tileentity = this.player.world.getTileEntity(blockposition1);
                            if (tileentity instanceof TileEntityStructure) {
                                TileEntityStructure tileentitystructure = (TileEntityStructure) tileentity;
                                byte b1 = packetdataserializer.readByte();

                                s1 = packetdataserializer.e(32);
                                tileentitystructure.a(TileEntityStructure.UsageMode.valueOf(s1));
                                tileentitystructure.a(packetdataserializer.e(64));
                                int i1 = MathHelper.clamp(packetdataserializer.readInt(), -32, 32);
                                int j1 = MathHelper.clamp(packetdataserializer.readInt(), -32, 32);
                                int k1 = MathHelper.clamp(packetdataserializer.readInt(), -32, 32);

                                tileentitystructure.b(new BlockPosition(i1, j1, k1));
                                int l1 = MathHelper.clamp(packetdataserializer.readInt(), 0, 32);
                                int i2 = MathHelper.clamp(packetdataserializer.readInt(), 0, 32);
                                int j2 = MathHelper.clamp(packetdataserializer.readInt(), 0, 32);

                                tileentitystructure.c(new BlockPosition(l1, i2, j2));
                                String s5 = packetdataserializer.e(32);

                                tileentitystructure.b(EnumBlockMirror.valueOf(s5));
                                String s6 = packetdataserializer.e(32);

                                tileentitystructure.b(EnumBlockRotation.valueOf(s6));
                                tileentitystructure.b(packetdataserializer.e(128));
                                tileentitystructure.a(packetdataserializer.readBoolean());
                                tileentitystructure.e(packetdataserializer.readBoolean());
                                tileentitystructure.f(packetdataserializer.readBoolean());
                                tileentitystructure.a(MathHelper.a(packetdataserializer.readFloat(), 0.0F, 1.0F));
                                tileentitystructure.a(packetdataserializer.h());
                                String s7 = tileentitystructure.a();

                                if (b1 == 2) {
                                    if (tileentitystructure.q()) {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.save_success", new Object[] { s7})), false);
                                    } else {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.save_failure", new Object[] { s7})), false);
                                    }
                                } else if (b1 == 3) {
                                    if (!tileentitystructure.E()) {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.load_not_found", new Object[] { s7})), false);
                                    } else if (tileentitystructure.r()) {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.load_success", new Object[] { s7})), false);
                                    } else {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.load_prepare", new Object[] { s7})), false);
                                    }
                                } else if (b1 == 4) {
                                    if (tileentitystructure.p()) {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.size_success", new Object[] { s7})), false);
                                    } else {
                                        this.player.a((IChatBaseComponent) (new ChatMessage("structure_block.size_failure", new Object[0])), false);
                                    }
                                }

                                tileentitystructure.update();
                                this.player.world.notify(blockposition1, iblockdata1, iblockdata1, 3);
                            }
                        } catch (Exception exception6) {
                            PlayerConnection.LOGGER.error("Couldn\'t set structure block", exception6);
                            this.disconnect("Invalid structure data!"); // CraftBukkit
                        }
                    } else if ("MC|PickItem".equals(s)) {
                        packetdataserializer = packetplayincustompayload.b();

                        try {
                            k = packetdataserializer.g();
                            this.player.inventory.d(k);
                            this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-2, this.player.inventory.itemInHandIndex, this.player.inventory.getItem(this.player.inventory.itemInHandIndex)));
                            this.player.playerConnection.sendPacket(new PacketPlayOutSetSlot(-2, k, this.player.inventory.getItem(k)));
                            this.player.playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(this.player.inventory.itemInHandIndex));
                        } catch (Exception exception7) {
                            IllegalPacketEvent.process(player.getBukkitEntity(), "InvalidPickItem", "Invalid PickItem", exception7); // Paper
                        }
                    }
                    // CraftBukkit start
                    else if (packetplayincustompayload.a().equals("REGISTER")) {
                        try {
                            String channels = packetplayincustompayload.b().toString(com.google.common.base.Charsets.UTF_8);
                            for (String channel : channels.split("\0")) {
                                getPlayer().addChannel(channel);
                            }
                        } catch (Exception ex) {
                            PlayerConnection.LOGGER.error("Couldn\'t register custom payload", ex);
                            this.disconnect("Invalid payload REGISTER!");
                        }
                    } else if (packetplayincustompayload.a().equals("UNREGISTER")) {
                        try {
                            String channels = packetplayincustompayload.b().toString(com.google.common.base.Charsets.UTF_8);
                            for (String channel : channels.split("\0")) {
                                getPlayer().removeChannel(channel);
                            }
                        } catch (Exception ex) {
                            PlayerConnection.LOGGER.error("Couldn\'t unregister custom payload", ex);
                            this.disconnect("Invalid payload UNREGISTER!");
                        }
                    } else {
                        try {
                            byte[] data = new byte[packetplayincustompayload.b().readableBytes()];
                            packetplayincustompayload.b().readBytes(data);
                            server.getMessenger().dispatchIncomingMessage(player.getBukkitEntity(), packetplayincustompayload.a(), data);
                        } catch (Exception ex) {
                            PlayerConnection.LOGGER.error("Couldn\'t dispatch custom payload", ex);
                            this.disconnect("Invalid custom payload!");
                        }
                    }
                    // CraftBukkit end
                }
            }
        }

    }

    // CraftBukkit start - Add "isDisconnected" method
    public final boolean isDisconnected() {
        return (!this.player.joining && !this.networkManager.isConnected()) || this.processedDisconnect; // Paper
    }
}
