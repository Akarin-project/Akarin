package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DedicatedServer extends MinecraftServer implements IMinecraftServer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Pattern h = Pattern.compile("^[a-fA-F0-9]{40}$");
    private final List<ServerCommand> serverCommandQueue = Collections.synchronizedList(Lists.newArrayList());
    private RemoteStatusListener j;
    public final RemoteControlCommandListener remoteControlCommandListener = new RemoteControlCommandListener(this);
    private RemoteControlListener l;
    public PropertyManager propertyManager;
    private EULA eula;
    private boolean generateStructures;
    private EnumGamemode p;
    private boolean q;

    public DedicatedServer(File file, DataFixer datafixer, YggdrasilAuthenticationService yggdrasilauthenticationservice, MinecraftSessionService minecraftsessionservice, GameProfileRepository gameprofilerepository, UserCache usercache) {
        super(file, Proxy.NO_PROXY, datafixer, new CommandDispatcher(true), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, usercache);
        Thread thread = new Thread("Server Infinisleeper") {
            {
                this.setDaemon(true);
                this.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
                this.start();
            }

            public void run() {
                while (true) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException interruptedexception) {
                        ;
                    }
                }
            }
        };
    }

    protected boolean init() throws IOException {
        Thread thread = new Thread("Server console handler") {
            public void run() {
                BufferedReader bufferedreader = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));

                String s;

                try {
                    while (!DedicatedServer.this.isStopped() && DedicatedServer.this.isRunning() && (s = bufferedreader.readLine()) != null) {
                        DedicatedServer.this.issueCommand(s, DedicatedServer.this.getServerCommandListener());
                    }
                } catch (IOException ioexception) {
                    DedicatedServer.LOGGER.error("Exception handling console input", ioexception);
                }

            }
        };

        thread.setDaemon(true);
        thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(DedicatedServer.LOGGER));
        thread.start();
        DedicatedServer.LOGGER.info("Starting minecraft server version 1.13.2");
        if (Runtime.getRuntime().maxMemory() / 1024L / 1024L < 512L) {
            DedicatedServer.LOGGER.warn("To start the server with more ram, launch it as \"java -Xmx1024M -Xms1024M -jar minecraft_server.jar\"");
        }

        DedicatedServer.LOGGER.info("Loading properties");
        this.propertyManager = new PropertyManager(new File("server.properties"));
        this.eula = new EULA(new File("eula.txt"));
        if (!this.eula.a()) {
            DedicatedServer.LOGGER.info("You need to agree to the EULA in order to run the server. Go to eula.txt for more info.");
            this.eula.b();
            return false;
        } else {
            if (this.H()) {
                this.b("127.0.0.1");
            } else {
                this.setOnlineMode(this.propertyManager.getBoolean("online-mode", true));
                this.f(this.propertyManager.getBoolean("prevent-proxy-connections", false));
                this.b(this.propertyManager.getString("server-ip", ""));
            }

            this.setSpawnAnimals(this.propertyManager.getBoolean("spawn-animals", true));
            this.setSpawnNPCs(this.propertyManager.getBoolean("spawn-npcs", true));
            this.setPVP(this.propertyManager.getBoolean("pvp", true));
            this.setAllowFlight(this.propertyManager.getBoolean("allow-flight", false));
            this.setResourcePack(this.propertyManager.getString("resource-pack", ""), this.aT());
            this.setMotd(this.propertyManager.getString("motd", "A Minecraft Server"));
            this.setForceGamemode(this.propertyManager.getBoolean("force-gamemode", false));
            this.setIdleTimeout(this.propertyManager.getInt("player-idle-timeout", 0));
            this.l(this.propertyManager.getBoolean("enforce-whitelist", false));
            if (this.propertyManager.getInt("difficulty", 1) < 0) {
                this.propertyManager.setProperty("difficulty", 0);
            } else if (this.propertyManager.getInt("difficulty", 1) > 3) {
                this.propertyManager.setProperty("difficulty", 3);
            }

            this.generateStructures = this.propertyManager.getBoolean("generate-structures", true);
            int i = this.propertyManager.getInt("gamemode", EnumGamemode.SURVIVAL.getId());

            this.p = WorldSettings.a(i);
            DedicatedServer.LOGGER.info("Default game type: {}", this.p);
            InetAddress inetaddress = null;

            if (!this.getServerIp().isEmpty()) {
                inetaddress = InetAddress.getByName(this.getServerIp());
            }

            if (this.getPort() < 0) {
                this.setPort(this.propertyManager.getInt("server-port", 25565));
            }

            DedicatedServer.LOGGER.info("Generating keypair");
            this.a(MinecraftEncryption.b());
            DedicatedServer.LOGGER.info("Starting Minecraft server on {}:{}", this.getServerIp().isEmpty() ? "*" : this.getServerIp(), this.getPort());

            try {
                this.getServerConnection().a(inetaddress, this.getPort());
            } catch (IOException ioexception) {
                DedicatedServer.LOGGER.warn("**** FAILED TO BIND TO PORT!");
                DedicatedServer.LOGGER.warn("The exception was: {}", ioexception.toString());
                DedicatedServer.LOGGER.warn("Perhaps a server is already running on that port?");
                return false;
            }

            if (!this.getOnlineMode()) {
                DedicatedServer.LOGGER.warn("**** SERVER IS RUNNING IN OFFLINE/INSECURE MODE!");
                DedicatedServer.LOGGER.warn("The server will make no attempt to authenticate usernames. Beware.");
                DedicatedServer.LOGGER.warn("While this makes the game possible to play without internet access, it also opens up the ability for hackers to connect with any username they choose.");
                DedicatedServer.LOGGER.warn("To change this, set \"online-mode\" to \"true\" in the server.properties file.");
            }

            if (this.aX()) {
                this.getUserCache().c();
            }

            if (!NameReferencingFileConverter.a(this.propertyManager)) {
                return false;
            } else {
                this.a((PlayerList) (new DedicatedPlayerList(this)));
                long j = SystemUtils.getMonotonicNanos();

                if (this.getWorld() == null) {
                    this.setWorld(this.propertyManager.getString("level-name", "world"));
                }

                String s = this.propertyManager.getString("level-seed", "");
                String s1 = this.propertyManager.getString("level-type", "DEFAULT");
                String s2 = this.propertyManager.getString("generator-settings", "");
                long k = (new Random()).nextLong();

                if (!s.isEmpty()) {
                    try {
                        long l = Long.parseLong(s);

                        if (l != 0L) {
                            k = l;
                        }
                    } catch (NumberFormatException numberformatexception) {
                        k = (long) s.hashCode();
                    }
                }

                WorldType worldtype = WorldType.getType(s1);

                if (worldtype == null) {
                    worldtype = WorldType.NORMAL;
                }

                this.getEnableCommandBlock();
                this.j();
                this.getSnooperEnabled();
                this.aw();
                this.b(this.propertyManager.getInt("max-build-height", 256));
                this.b((this.getMaxBuildHeight() + 8) / 16 * 16);
                this.b(MathHelper.clamp(this.getMaxBuildHeight(), 64, 256));
                this.propertyManager.setProperty("max-build-height", this.getMaxBuildHeight());
                TileEntitySkull.a(this.getUserCache());
                TileEntitySkull.a(this.ap());
                UserCache.a(this.getOnlineMode());
                DedicatedServer.LOGGER.info("Preparing level \"{}\"", this.getWorld());
                JsonObject jsonobject = new JsonObject();

                if (worldtype == WorldType.FLAT) {
                    jsonobject.addProperty("flat_world_options", s2);
                } else if (!s2.isEmpty()) {
                    jsonobject = ChatDeserializer.a(s2);
                }

                this.a(this.getWorld(), this.getWorld(), k, worldtype, jsonobject);
                long i1 = SystemUtils.getMonotonicNanos() - j;
                String s3 = String.format(Locale.ROOT, "%.3fs", (double) i1 / 1.0E9D);

                DedicatedServer.LOGGER.info("Done ({})! For help, type \"help\"", s3);
                if (this.propertyManager.a("announce-player-achievements")) {
                    this.getGameRules().set("announceAdvancements", this.propertyManager.getBoolean("announce-player-achievements", true) ? "true" : "false", this);
                    this.propertyManager.b("announce-player-achievements");
                    this.propertyManager.savePropertiesFile();
                }

                if (this.propertyManager.getBoolean("enable-query", false)) {
                    DedicatedServer.LOGGER.info("Starting GS4 status listener");
                    this.j = new RemoteStatusListener(this);
                    this.j.a();
                }

                if (this.propertyManager.getBoolean("enable-rcon", false)) {
                    DedicatedServer.LOGGER.info("Starting remote control listener");
                    this.l = new RemoteControlListener(this);
                    this.l.a();
                }

                if (this.getMaxTickTime() > 0L) {
                    Thread thread1 = new Thread(new ThreadWatchdog(this));

                    thread1.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(DedicatedServer.LOGGER));
                    thread1.setName("Server Watchdog");
                    thread1.setDaemon(true);
                    thread1.start();
                }

                Items.AIR.a(CreativeModeTab.g, NonNullList.a());
                return true;
            }
        }
    }

    public String aT() {
        if (this.propertyManager.a("resource-pack-hash")) {
            if (this.propertyManager.a("resource-pack-sha1")) {
                DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated and found along side resource-pack-sha1. resource-pack-hash will be ignored.");
            } else {
                DedicatedServer.LOGGER.warn("resource-pack-hash is deprecated. Please use resource-pack-sha1 instead.");
                this.propertyManager.getString("resource-pack-sha1", this.propertyManager.getString("resource-pack-hash", ""));
                this.propertyManager.b("resource-pack-hash");
            }
        }

        String s = this.propertyManager.getString("resource-pack-sha1", "");

        if (!s.isEmpty() && !DedicatedServer.h.matcher(s).matches()) {
            DedicatedServer.LOGGER.warn("Invalid sha1 for ressource-pack-sha1");
        }

        if (!this.propertyManager.getString("resource-pack", "").isEmpty() && s.isEmpty()) {
            DedicatedServer.LOGGER.warn("You specified a resource pack without providing a sha1 hash. Pack will be updated on the client only if you change the name of the pack.");
        }

        return s;
    }

    public void setGamemode(EnumGamemode enumgamemode) {
        super.setGamemode(enumgamemode);
        this.p = enumgamemode;
    }

    public boolean getGenerateStructures() {
        return this.generateStructures;
    }

    public EnumGamemode getGamemode() {
        return this.p;
    }

    public EnumDifficulty getDifficulty() {
        return EnumDifficulty.getById(this.propertyManager.getInt("difficulty", EnumDifficulty.NORMAL.a()));
    }

    public boolean isHardcore() {
        return this.propertyManager.getBoolean("hardcore", false);
    }

    public CrashReport b(CrashReport crashreport) {
        crashreport = super.b(crashreport);
        crashreport.g().a("Is Modded", () -> {
            String s = this.getServerModName();

            return !"vanilla".equals(s) ? "Definitely; Server brand changed to '" + s + "'" : "Unknown (can't tell)";
        });
        crashreport.g().a("Type", () -> {
            return "Dedicated Server (map_server.txt)";
        });
        return crashreport;
    }

    protected void t() {
        System.exit(0);
    }

    protected void b(BooleanSupplier booleansupplier) {
        super.b(booleansupplier);
        this.handleCommandQueue();
    }

    public boolean getAllowNether() {
        return this.propertyManager.getBoolean("allow-nether", true);
    }

    public boolean getSpawnMonsters() {
        return this.propertyManager.getBoolean("spawn-monsters", true);
    }

    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.a("whitelist_enabled", this.getPlayerList().getHasWhitelist());
        mojangstatisticsgenerator.a("whitelist_count", this.getPlayerList().getWhitelisted().length);
        super.a(mojangstatisticsgenerator);
    }

    public boolean getSnooperEnabled() {
        if (this.propertyManager.getBoolean("snooper-enabled", true)) {
            ;
        }

        return false;
    }

    public void issueCommand(String s, CommandListenerWrapper commandlistenerwrapper) {
        this.serverCommandQueue.add(new ServerCommand(s, commandlistenerwrapper));
    }

    public void handleCommandQueue() {
        while (!this.serverCommandQueue.isEmpty()) {
            ServerCommand servercommand = (ServerCommand) this.serverCommandQueue.remove(0);

            this.getCommandDispatcher().a(servercommand.source, servercommand.command);
        }

    }

    public boolean Q() {
        return true;
    }

    public boolean V() {
        return this.propertyManager.getBoolean("use-native-transport", true);
    }

    public DedicatedPlayerList getPlayerList() {
        return (DedicatedPlayerList) super.getPlayerList();
    }

    public boolean ad() {
        return true;
    }

    public int a(String s, int i) {
        return this.propertyManager.getInt(s, i);
    }

    public String a(String s, String s1) {
        return this.propertyManager.getString(s, s1);
    }

    public boolean a(String s, boolean flag) {
        return this.propertyManager.getBoolean(s, flag);
    }

    public void a(String s, Object object) {
        this.propertyManager.setProperty(s, object);
    }

    public void c_() {
        this.propertyManager.savePropertiesFile();
    }

    public String d_() {
        File file = this.propertyManager.c();

        return file != null ? file.getAbsolutePath() : "No settings file";
    }

    public String e() {
        return this.getServerIp();
    }

    public int e_() {
        return this.getPort();
    }

    public String m() {
        return this.getMotd();
    }

    public void aW() {
        ServerGUI.a(this);
        this.q = true;
    }

    public boolean ag() {
        return this.q;
    }

    public boolean a(EnumGamemode enumgamemode, boolean flag, int i) {
        return false;
    }

    public boolean getEnableCommandBlock() {
        return this.propertyManager.getBoolean("enable-command-block", false);
    }

    public int getSpawnProtection() {
        return this.propertyManager.getInt("spawn-protection", super.getSpawnProtection());
    }

    public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (world.worldProvider.getDimensionManager() != DimensionManager.OVERWORLD) {
            return false;
        } else if (this.getPlayerList().getOPs().isEmpty()) {
            return false;
        } else if (this.getPlayerList().isOp(entityhuman.getProfile())) {
            return false;
        } else if (this.getSpawnProtection() <= 0) {
            return false;
        } else {
            BlockPosition blockposition1 = world.getSpawn();
            int i = MathHelper.a(blockposition.getX() - blockposition1.getX());
            int j = MathHelper.a(blockposition.getZ() - blockposition1.getZ());
            int k = Math.max(i, j);

            return k <= this.getSpawnProtection();
        }
    }

    public int j() {
        return this.propertyManager.getInt("op-permission-level", 4);
    }

    public void setIdleTimeout(int i) {
        super.setIdleTimeout(i);
        this.propertyManager.setProperty("player-idle-timeout", i);
        this.c_();
    }

    public boolean k() {
        return this.propertyManager.getBoolean("broadcast-rcon-to-ops", true);
    }

    public boolean B_() {
        return this.propertyManager.getBoolean("broadcast-console-to-ops", true);
    }

    public int au() {
        int i = this.propertyManager.getInt("max-world-size", super.au());

        if (i < 1) {
            i = 1;
        } else if (i > super.au()) {
            i = super.au();
        }

        return i;
    }

    public int aw() {
        return this.propertyManager.getInt("network-compression-threshold", super.aw());
    }

    protected boolean aX() {
        boolean flag = false;

        int i;

        for (i = 0; !flag && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the user banlist, retrying in a few seconds");
                this.ba();
            }

            flag = NameReferencingFileConverter.a((MinecraftServer) this);
        }

        boolean flag1 = false;

        for (i = 0; !flag1 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the ip banlist, retrying in a few seconds");
                this.ba();
            }

            flag1 = NameReferencingFileConverter.b((MinecraftServer) this);
        }

        boolean flag2 = false;

        for (i = 0; !flag2 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the op list, retrying in a few seconds");
                this.ba();
            }

            flag2 = NameReferencingFileConverter.c((MinecraftServer) this);
        }

        boolean flag3 = false;

        for (i = 0; !flag3 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the whitelist, retrying in a few seconds");
                this.ba();
            }

            flag3 = NameReferencingFileConverter.d((MinecraftServer) this);
        }

        boolean flag4 = false;

        for (i = 0; !flag4 && i <= 2; ++i) {
            if (i > 0) {
                DedicatedServer.LOGGER.warn("Encountered a problem while converting the player save files, retrying in a few seconds");
                this.ba();
            }

            flag4 = NameReferencingFileConverter.a(this, this.propertyManager);
        }

        return flag || flag1 || flag2 || flag3 || flag4;
    }

    private void ba() {
        try {
            Thread.sleep(5000L);
        } catch (InterruptedException interruptedexception) {
            ;
        }
    }

    public long getMaxTickTime() {
        return this.propertyManager.getLong("max-tick-time", TimeUnit.MINUTES.toMillis(1L));
    }

    public String getPlugins() {
        return "";
    }

    public String executeRemoteCommand(String s) {
        this.remoteControlCommandListener.clearMessages();
        this.getCommandDispatcher().a(this.remoteControlCommandListener.f(), s);
        return this.remoteControlCommandListener.getMessages();
    }
}
