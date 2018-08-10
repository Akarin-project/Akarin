package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.datafixers.DataFixer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
// CraftBukkit start
import joptsimple.OptionSet;
// CraftBukkit end
import org.spigotmc.SlackActivityAccountant; // Spigot
import co.aikar.timings.MinecraftTimings; // Paper

/**
 * Akarin Changes Note
 * 1) Make worlds list thread-safe (slack service)
 */
public abstract class MinecraftServer implements IAsyncTaskHandler, IMojangStatistics, ICommandListener, Runnable {

    private static MinecraftServer SERVER; // Paper
    public static final Logger LOGGER = LogManager.getLogger();
    public static final File a = new File("usercache.json");
    public Convertable convertable;
    private final MojangStatisticsGenerator j = new MojangStatisticsGenerator("server", this, SystemUtils.b());
    public File universe;
    private final List<ITickable> l = Lists.newArrayList();
    public final MethodProfiler methodProfiler = new MethodProfiler();
    private ServerConnection m; // Spigot
    private final ServerPing n = new ServerPing();
    private final Random o = new Random();
    public final DataFixer dataConverterManager;
    private String serverIp;
    private int r = -1;
    public WorldServer[] worldServer;
    private PlayerList s;
    private boolean isRunning = true;
    private boolean isRestarting = false; // Paper - flag to signify we're attempting to restart
    private boolean isStopped;
    private int ticks;
    protected final Proxy d;
    private IChatBaseComponent w;
    private int x;
    private boolean onlineMode;
    private boolean z;
    private boolean spawnAnimals;
    private boolean spawnNPCs;
    private boolean pvpMode;
    private boolean allowFlight;
    private String motd;
    private int F;
    private int G;
    public final long[] e = new long[100];
    public long[][] f;
    private KeyPair H;
    private String I;
    private String J;
    private boolean demoMode;
    private boolean M;
    private String N = "";
    private String O = "";
    private boolean P;
    private long Q;
    private IChatBaseComponent R;
    private boolean S;
    private boolean T;
    private final YggdrasilAuthenticationService U;
    private final MinecraftSessionService V;
    private final GameProfileRepository W;
    private final UserCache X;
    private long Y;
    protected final Queue<FutureTask<?>> g = new com.destroystokyo.paper.utils.CachedSizeConcurrentLinkedQueue<>(); // Spigot, PAIL: Rename // Paper - Make size() constant-time
    private Thread serverThread;
    private long aa = SystemUtils.b();
    private final IReloadableResourceManager ac;
    private final ResourcePackRepository<ResourcePackLoader> resourcePackRepository;
    private ResourcePackSourceFolder resourcePackFolder;
    public CommandDispatcher commandDispatcher;
    private final CraftingManager ag;
    private final TagRegistry ah;
    private final ScoreboardServer ai;
    private final BossBattleCustomData aj;
    private final LootTableRegistry ak;
    private final AdvancementDataWorld al;
    private final CustomFunctionData am;
    private boolean an;
    private boolean forceUpgrade;
    private float ap;

    // CraftBukkit start
    public List<WorldServer> worlds = Lists.newCopyOnWriteArrayList(); // new ArrayList<WorldServer>(); // Akarin
    public org.bukkit.craftbukkit.CraftServer server;
    public OptionSet options;
    public org.bukkit.command.ConsoleCommandSender console;
    public org.bukkit.command.RemoteConsoleCommandSender remoteConsole;
    //public ConsoleReader reader; // Paper
    public static int currentTick = 0; // Paper - Further improve tick loop
    public boolean serverAutoSave = false; // Paper
    public final Thread primaryThread;
    public java.util.Queue<Runnable> processQueue = new java.util.concurrent.ConcurrentLinkedQueue<Runnable>();
    public int autosavePeriod;
    public File bukkitDataPackFolder;
    public CommandDispatcher vanillaCommandDispatcher;
    // CraftBukkit end
    // Spigot start
    public final SlackActivityAccountant slackActivityAccountant = new SlackActivityAccountant();
    // Spigot end

    public MinecraftServer(OptionSet options, Proxy proxy, DataFixer datafixer, CommandDispatcher commanddispatcher, YggdrasilAuthenticationService yggdrasilauthenticationservice, MinecraftSessionService minecraftsessionservice, GameProfileRepository gameprofilerepository, UserCache usercache) {
        SERVER = this; // Paper - better singleton
        this.commandDispatcher = commanddispatcher; // CraftBukkit
        this.ac = new ResourceManager(EnumResourcePackType.SERVER_DATA);
        this.resourcePackRepository = new ResourcePackRepository(ResourcePackLoader::new);
        this.ag = new CraftingManager();
        this.ah = new TagRegistry();
        this.ai = new ScoreboardServer(this);
        this.aj = new BossBattleCustomData(this);
        this.ak = new LootTableRegistry();
        this.al = new AdvancementDataWorld();
        this.am = new CustomFunctionData(this);
        this.d = proxy;
        this.commandDispatcher = this.vanillaCommandDispatcher = commanddispatcher; // CraftBukkit
        this.U = yggdrasilauthenticationservice;
        this.V = minecraftsessionservice;
        this.W = gameprofilerepository;
        this.X = usercache;
        // this.universe = file; // CraftBukkit
        // this.m = new ServerConnection(this); // CraftBukkit // Spigot
        // this.convertable = file == null ? null : new WorldLoaderServer(file.toPath(), file.toPath().resolve("../backups"), datafixer); // CraftBukkit - moved to DedicatedServer.init
        this.dataConverterManager = datafixer;
        this.ac.a((IResourcePackListener) this.ah);
        this.ac.a((IResourcePackListener) this.ag);
        this.ac.a((IResourcePackListener) this.ak);
        this.ac.a((IResourcePackListener) this.am);
        this.ac.a((IResourcePackListener) this.al);
        // CraftBukkit start
        this.options = options;
        // Paper start - Handled by TerminalConsoleAppender
        // Try to see if we're actually running in a terminal, disable jline if not
        /*
        if (System.console() == null && System.getProperty("jline.terminal") == null) {
            System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
            Main.useJline = false;
        }

        try {
            reader = new ConsoleReader(System.in, System.out);
            reader.setExpandEvents(false); // Avoid parsing exceptions for uncommonly used event designators
        } catch (Throwable e) {
            try {
                // Try again with jline disabled for Windows users without C++ 2008 Redistributable
                System.setProperty("jline.terminal", "jline.UnsupportedTerminal");
                System.setProperty("user.language", "en");
                Main.useJline = false;
                reader = new ConsoleReader(System.in, System.out);
                reader.setExpandEvents(false);
            } catch (IOException ex) {
                LOGGER.warn((String) null, ex);
            }
        }
        */
        // Paper end
        Runtime.getRuntime().addShutdownHook(new org.bukkit.craftbukkit.util.ServerShutdownThread(this));

        this.serverThread = primaryThread = new Thread(this, "Server thread"); // Moved from main
    }

    public abstract PropertyManager getPropertyManager();
    // CraftBukkit end

    public abstract boolean init() throws IOException;

    public void convertWorld(String s) {
        if (this.getConvertable().isConvertable(s)) {
            MinecraftServer.LOGGER.info("Converting map!");
            this.b((IChatBaseComponent) (new ChatMessage("menu.convertingLevel", new Object[0])));
            this.getConvertable().convert(s, new IProgressUpdate() {
                private long b = SystemUtils.b();

                public void a(IChatBaseComponent ichatbasecomponent) {}

                public void a(int i) {
                    if (SystemUtils.b() - this.b >= 1000L) {
                        this.b = SystemUtils.b();
                        MinecraftServer.LOGGER.info("Converting... {}%", Integer.valueOf(i));
                    }

                }

                public void c(IChatBaseComponent ichatbasecomponent) {}
            });
        }

        if (this.forceUpgrade) {
            MinecraftServer.LOGGER.info("Forcing world upgrade! {}", s); // CraftBukkit
            WorldData worlddata = this.getConvertable().c(s); // CraftBukkit

            if (worlddata != null) {
                WorldUpgrader worldupgrader = new WorldUpgrader(s, this.getConvertable(), worlddata); // CraftBukkit
                IChatBaseComponent ichatbasecomponent = null;

                while (!worldupgrader.b()) {
                    IChatBaseComponent ichatbasecomponent1 = worldupgrader.m();

                    if (ichatbasecomponent != ichatbasecomponent1) {
                        ichatbasecomponent = ichatbasecomponent1;
                        MinecraftServer.LOGGER.info(worldupgrader.m().getString());
                    }

                    int i = worldupgrader.j();

                    if (i > 0) {
                        int j = worldupgrader.k() + worldupgrader.l();

                        MinecraftServer.LOGGER.info("{}% completed ({} / {} chunks)...", Integer.valueOf(MathHelper.d((float) j / (float) i * 100.0F)), Integer.valueOf(j), Integer.valueOf(i));
                    }

                    if (this.isStopped()) {
                        worldupgrader.a();
                    } else {
                        try {
                            Thread.sleep(1000L);
                        } catch (InterruptedException interruptedexception) {
                            ;
                        }
                    }
                }
            }
        }

    }

    protected synchronized void b(IChatBaseComponent ichatbasecomponent) {
        this.R = ichatbasecomponent;
    }

    public void a(String s, String s1, long i, WorldType worldtype, JsonElement jsonelement) {
        // this.convertWorld(s); // CraftBukkit - moved down
        this.b((IChatBaseComponent) (new ChatMessage("menu.loadingLevel", new Object[0])));
        this.worldServer = new WorldServer[3];
        /* CraftBukkit start - Remove ticktime arrays and worldsettings
        this.f = new long[this.worldServer.length][100];
        IDataManager idatamanager = this.convertable.a(s, this);

        this.a(this.getWorld(), idatamanager);
        WorldData worlddata = idatamanager.getWorldData();
        WorldSettings worldsettings;

        if (worlddata == null) {
            if (this.N()) {
                worldsettings = DemoWorldServer.a;
            } else {
                worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), worldtype);
                worldsettings.setGeneratorSettings(jsonelement);
                if (this.M) {
                    worldsettings.a();
                }
            }

            worlddata = new WorldData(worldsettings, s1);
        } else {
            worlddata.a(s1);
            worldsettings = new WorldSettings(worlddata);
        }

        this.a(idatamanager.getDirectory(), worlddata);
        */
        int worldCount = 3;

        for (int j = 0; j < worldCount; ++j) {
            WorldServer world;
            WorldData worlddata;
            byte dimension = 0;

            if (j == 1) {
                if (getAllowNether()) {
                    dimension = -1;
                } else {
                    continue;
                }
            }

            if (j == 2) {
                if (server.getAllowEnd()) {
                    dimension = 1;
                } else {
                    continue;
                }
            }

            String worldType = org.bukkit.World.Environment.getEnvironment(dimension).toString().toLowerCase();
            String name = (dimension == 0) ? s : s + "_" + worldType;
            this.convertWorld(name); // Run conversion now

            org.bukkit.generator.ChunkGenerator gen = this.server.getGenerator(name);
            WorldSettings worldsettings = new WorldSettings(i, this.getGamemode(), this.getGenerateStructures(), this.isHardcore(), worldtype);
            worldsettings.setGeneratorSettings(jsonelement);

            if (j == 0) {
                IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), s1, this, this.dataConverterManager);
                worlddata = idatamanager.getWorldData();
                if (worlddata == null) {
                    worlddata = new WorldData(worldsettings, s1);
                }
                worlddata.checkName(s1); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to take the last loaded world as respawn (in this case the end)
                this.a(idatamanager.getDirectory(), worlddata);
                if (this.N()) {
                    world = (WorldServer) (new DemoWorldServer(this, idatamanager, worlddata, dimension, this.methodProfiler)).b();
                } else {
                    world = (WorldServer) (new WorldServer(this, idatamanager, worlddata, dimension, this.methodProfiler, org.bukkit.World.Environment.getEnvironment(dimension), gen)).b();
                }

                world.a(worldsettings);
                this.server.scoreboardManager = new org.bukkit.craftbukkit.scoreboard.CraftScoreboardManager(this, world.getScoreboard());
            } else {
                String dim = "DIM" + dimension;

                File newWorld = new File(new File(name), dim);
                File oldWorld = new File(new File(s), dim);

                if ((!newWorld.isDirectory()) && (oldWorld.isDirectory())) {
                    MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder required ----");
                    MinecraftServer.LOGGER.info("Unfortunately due to the way that Minecraft implemented multiworld support in 1.6, Bukkit requires that you move your " + worldType + " folder to a new location in order to operate correctly.");
                    MinecraftServer.LOGGER.info("We will move this folder for you, but it will mean that you need to move it back should you wish to stop using Bukkit in the future.");
                    MinecraftServer.LOGGER.info("Attempting to move " + oldWorld + " to " + newWorld + "...");

                    if (newWorld.exists()) {
                        MinecraftServer.LOGGER.warn("A file or folder already exists at " + newWorld + "!");
                        MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                    } else if (newWorld.getParentFile().mkdirs()) {
                        if (oldWorld.renameTo(newWorld)) {
                            MinecraftServer.LOGGER.info("Success! To restore " + worldType + " in the future, simply move " + newWorld + " to " + oldWorld);
                            // Migrate world data too.
                            try {
                                com.google.common.io.Files.copy(new File(new File(s), "level.dat"), new File(new File(name), "level.dat"));
                                org.apache.commons.io.FileUtils.copyDirectory(new File(new File(s), "data"), new File(new File(name), "data"));
                            } catch (IOException exception) {
                                MinecraftServer.LOGGER.warn("Unable to migrate world data.");
                            }
                            MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder complete ----");
                        } else {
                            MinecraftServer.LOGGER.warn("Could not move folder " + oldWorld + " to " + newWorld + "!");
                            MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                        }
                    } else {
                        MinecraftServer.LOGGER.warn("Could not create path for " + newWorld + "!");
                        MinecraftServer.LOGGER.info("---- Migration of old " + worldType + " folder failed ----");
                    }
                }

                IDataManager idatamanager = new ServerNBTManager(server.getWorldContainer(), name, this, this.dataConverterManager);
                // world =, b0 to dimension, s1 to name, added Environment and gen
                worlddata = idatamanager.getWorldData();
                if (worlddata == null) {
                    worlddata = new WorldData(worldsettings, name);
                }
                worlddata.checkName(name); // CraftBukkit - Migration did not rewrite the level.dat; This forces 1.8 to take the last loaded world as respawn (in this case the end)
                world = (WorldServer) new SecondaryWorldServer(this, idatamanager, dimension, this.worlds.get(0), this.methodProfiler, worlddata, org.bukkit.World.Environment.getEnvironment(dimension), gen).b();
            }

            this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldInitEvent(world.getWorld()));

            world.addIWorldAccess(new WorldManager(this, world));
            if (!this.J()) {
                world.getWorldData().setGameType(this.getGamemode());
            }

            worlds.add(world);
            getPlayerList().setPlayerFileData(worlds.toArray(new WorldServer[worlds.size()]));

            if (worlddata.P() != null) {
                this.aR().a(worlddata.P());
            }
        }
        this.s.setPlayerFileData(this.worldServer);
        // CraftBukkit end

        this.a(this.getDifficulty());
        this.g_();

        // Paper start - Handle collideRule team for player collision toggle
        final Scoreboard scoreboard = this.getScoreboard();
        final java.util.Collection<String> toRemove = scoreboard.getTeams().stream().filter(team -> team.getName().startsWith("collideRule_")).map(ScoreboardTeam::getName).collect(java.util.stream.Collectors.toList());
        for (String teamName : toRemove) {
            scoreboard.removeTeam(scoreboard.getTeam(teamName)); // Clean up after ourselves
        }

        if (!com.destroystokyo.paper.PaperConfig.enablePlayerCollisions) {
            this.getPlayerList().collideRuleTeamName = org.apache.commons.lang3.StringUtils.left("collideRule_" + worlds.get(0).random.nextInt(), 16);
            ScoreboardTeam collideTeam = scoreboard.createTeam(this.getPlayerList().collideRuleTeamName);
            collideTeam.setCanSeeFriendlyInvisibles(false); // Because we want to mimic them not being on a team at all
        }
        // Paper end
    }

    protected void a(File file, WorldData worlddata) {
        this.resourcePackRepository.a((ResourcePackSource) (new ResourcePackSourceVanilla()));
        this.resourcePackFolder = new ResourcePackSourceFolder(new File(file, "datapacks"));
        // CraftBukkit start
        bukkitDataPackFolder = new File(new File(file, "datapacks"), "bukkit");
        if (!bukkitDataPackFolder.exists()) {
            bukkitDataPackFolder.mkdirs();
        }
        File mcMeta = new File(bukkitDataPackFolder, "pack.mcmeta");
        if (!mcMeta.exists()) {
            try {
                com.google.common.io.Files.write("{\n"
                        + "    \"pack\": {\n"
                        + "        \"description\": \"Data pack for resources provided by Bukkit plugins\",\n"
                        + "        \"pack_format\": 1\n"
                        + "    }\n"
                        + "}", mcMeta, com.google.common.base.Charsets.UTF_8);
            } catch (IOException ex) {
                throw new RuntimeException("Could not initialize Bukkit datapack", ex);
            }
        }
        // CraftBukkit end
        this.resourcePackRepository.a((ResourcePackSource) this.resourcePackFolder);
        this.resourcePackRepository.a();
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = worlddata.O().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            ResourcePackLoader resourcepackloader = this.resourcePackRepository.a(s);

            if (resourcepackloader != null) {
                arraylist.add(resourcepackloader);
            } else {
                MinecraftServer.LOGGER.warn("Missing data pack {}", s);
            }
        }

        this.resourcePackRepository.a((Collection) arraylist);
        this.a(worlddata);
    }

    protected void g_() {
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;

        this.b((IChatBaseComponent) (new ChatMessage("menu.generatingTerrain", new Object[0])));
        boolean flag5 = false;

        // CraftBukkit start - fire WorldLoadEvent and handle whether or not to keep the spawn in memory
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int m = 0; m < worlds.size(); m++) {
            WorldServer worldserver = this.worlds.get(m);
            MinecraftServer.LOGGER.info("Preparing start region for level " + m + " (Seed: " + worldserver.getSeed() + ")");
            if (!worldserver.getWorld().getKeepSpawnInMemory()) {
                continue;
            }

            BlockPosition blockposition = worldserver.getSpawn();
            ArrayList arraylist = Lists.newArrayList();
            Set set = Sets.newConcurrentHashSet();

            // Paper start
            short radius = worldserver.paperConfig.keepLoadedRange;
            for (int i = -radius; i <= radius && this.isRunning(); i += 16) {
                for (int j = -radius; j <= radius && this.isRunning(); j += 16) {
                    // Paper end
                    arraylist.add(new ChunkCoordIntPair(blockposition.getX() + i >> 4, blockposition.getZ() + j >> 4));
                }
            } // Paper
            if (this.isRunning()) { // Paper
                int expected = arraylist.size(); // Paper


                CompletableFuture completablefuture = worldserver.getChunkProviderServer().a((Iterable) arraylist, (chunk) -> {
                    set.add(chunk.getPos());
                    if (set.size() < expected && set.size() % 25 == 0) this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / expected); // Paper
                });

                while (!completablefuture.isDone()) {
                    try {
                        completablefuture.get(1L, TimeUnit.SECONDS);
                    } catch (InterruptedException interruptedexception) {
                        throw new RuntimeException(interruptedexception);
                    } catch (ExecutionException executionexception) {
                        if (executionexception.getCause() instanceof RuntimeException) {
                            throw (RuntimeException) executionexception.getCause();
                        }

                        throw new RuntimeException(executionexception.getCause());
                    } catch (TimeoutException timeoutexception) {
                        this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / expected); // Paper
                    }
                }

                this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / expected); // Paper
            }
        }

        for (WorldServer world : this.worlds) {
            this.server.getPluginManager().callEvent(new org.bukkit.event.world.WorldLoadEvent(world.getWorld()));
        }
        // CraftBukkit end
        this.m();
        MinecraftServer.LOGGER.info("Time elapsed: {} ms", Long.valueOf(stopwatch.elapsed(TimeUnit.MILLISECONDS)));
    }

    protected void a(String s, IDataManager idatamanager) {
        File file = new File(idatamanager.getDirectory(), "resources.zip");

        if (file.isFile()) {
            try {
                this.setResourcePack("level://" + URLEncoder.encode(s, StandardCharsets.UTF_8.toString()) + "/" + "resources.zip", "");
            } catch (UnsupportedEncodingException unsupportedencodingexception) {
                MinecraftServer.LOGGER.warn("Something went wrong url encoding {}", s);
            }
        }

    }

    public abstract boolean getGenerateStructures();

    public abstract EnumGamemode getGamemode();

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int k();

    public abstract boolean l();

    protected void a(IChatBaseComponent ichatbasecomponent, int i) {
        this.w = ichatbasecomponent;
        this.x = i;
        MinecraftServer.LOGGER.info("{}: {}%", ichatbasecomponent.getString(), Integer.valueOf(i));
    }

    protected void m() {
        this.w = null;
        this.x = 0;
        this.server.enablePlugins(org.bukkit.plugin.PluginLoadOrder.POSTWORLD); // CraftBukkit
    }

    protected void saveChunks(boolean flag) {
        WorldServer[] aworldserver = this.worldServer;
        int i = aworldserver.length;

        // CraftBukkit start
        for (int j = 0; j < worlds.size(); ++j) {
            WorldServer worldserver = worlds.get(j);
            // CraftBukkit end

            if (worldserver != null) {
                if (!flag) {
                    MinecraftServer.LOGGER.info("Saving chunks for level \'{}\'/{}", worldserver.getWorldData().getName(), worldserver.worldProvider.getDimensionManager().b());
                }

                try {
                    worldserver.save(true, (IProgressUpdate) null);
                } catch (ExceptionWorldConflict exceptionworldconflict) {
                    MinecraftServer.LOGGER.warn(exceptionworldconflict.getMessage());
                }
            }
        }

    }

    // CraftBukkit start
    private boolean hasStopped = false;
    private final Object stopLock = new Object();
    // CraftBukkit end

    public void stop() throws ExceptionWorldConflict { // CraftBukkit - added throws
        // CraftBukkit start - prevent double stopping on multiple threads
        synchronized(stopLock) {
            if (hasStopped) return;
            hasStopped = true;
        }
        // CraftBukkit end
        MinecraftServer.LOGGER.info("Stopping server");
        MinecraftTimings.stopServer(); // Paper
        // CraftBukkit start
        if (this.server != null) {
            this.server.disablePlugins();
        }
        // CraftBukkit end
        if (this.getServerConnection() != null) {
            this.getServerConnection().b();
        }

        if (this.s != null) {
            MinecraftServer.LOGGER.info("Saving players");
            this.s.savePlayers();
            this.s.u(isRestarting);;
            try { Thread.sleep(100); } catch (InterruptedException ex) {} // CraftBukkit - SPIGOT-625 - give server at least a chance to send packets
        }

        if (this.worldServer != null) {
            MinecraftServer.LOGGER.info("Saving worlds");
            WorldServer[] aworldserver = this.worldServer;
            int i = aworldserver.length;

            int j;
            WorldServer worldserver;

            // CraftBukkit start
            for (j = 0; j < worlds.size(); ++j) {
                worldserver = worlds.get(j);
                // CraftBukkit end
                if (worldserver != null) {
                    worldserver.savingDisabled = false;
                }
            }

            this.saveChunks(false);
            aworldserver = this.worldServer;
            i = aworldserver.length;

            // CraftBukkit start
            for (j = 0; j < worlds.size(); ++j) {
                worldserver = worlds.get(j);
                // CraftBukkit end
                if (worldserver != null) {
                    worldserver.close();
                }
            }
        }

        if (this.j.d()) {
            this.j.e();
        }

        // Spigot start
        if (org.spigotmc.SpigotConfig.saveUserCacheOnStopOnly) {
            LOGGER.info("Saving usercache.json");
            this.getUserCache().c(false); // Paper
        }
        // Spigot end
    }

    public String getServerIp() {
        return this.serverIp;
    }

    public void b(String s) {
        this.serverIp = s;
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    // Paper start - allow passing of the intent to restart
    public void safeShutdown() {
        safeShutdown(false);
    }

    public void safeShutdown(boolean isRestarting) {
        this.isRunning = false;
        this.isRestarting = isRestarting;
    }

    // Paper end

    // Paper start - Further improve server tick loop
    private static final int TPS = 20;
    private static final long SEC_IN_NANO = 1000000000;
    public static final long TICK_TIME = SEC_IN_NANO / TPS;
    private static final long MAX_CATCHUP_BUFFER = TICK_TIME * TPS * 60L;
    private static final int SAMPLE_INTERVAL = 20;
    public final RollingAverage tps1 = new RollingAverage(60);
    public final RollingAverage tps5 = new RollingAverage(60 * 5);
    public final RollingAverage tps15 = new RollingAverage(60 * 15);
    public double[] recentTps = new double[3]; // Paper - Fine have your darn compat with bad plugins

    public static class RollingAverage {
        private final int size;
        private long time;
        private double total;
        private int index = 0;
        private final double[] samples;
        private final long[] times;

        RollingAverage(int size) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = TPS * SEC_IN_NANO * size;
            this.samples = new double[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = TPS;
                this.times[i] = SEC_IN_NANO;
            }
        }

        public void add(double x, long t) {
            time -= times[index];
            total -= samples[index] * times[index];
            samples[index] = x;
            times[index] = t;
            time += t;
            total += x * t;
            if (++index == size) {
                index = 0;
            }
        }

        public double getAverage() {
            return total / time;
        }
    }
    // Paper End

    public void run() {
        try {
            if (this.init()) {
                this.aa = SystemUtils.b();
                this.n.setMOTD(new ChatComponentText(this.motd));
                this.n.setServerInfo(new ServerPing.ServerData("1.13", 393));
                this.a(this.n);

                // Spigot start
                Arrays.fill( recentTps, 20 );
                long start = System.nanoTime(), lastTick = start - TICK_TIME, catchupTime = 0, curTime, wait, tickSection = start; // Paper - Further improve server tick loop
                while (this.isRunning) {
                    curTime = System.nanoTime();
                    // Paper start - Further improve server tick loop
                    wait = TICK_TIME - (curTime - lastTick);
                    if (wait > 0) {
                        if (catchupTime < 2E6) {
                            wait += Math.abs(catchupTime);
                        } else if (wait < catchupTime) {
                            catchupTime -= wait;
                            wait = 0;
                        } else {
                            wait -= catchupTime;
                            catchupTime = 0;
                        }
                    }
                    if (wait > 0) {
                        Thread.sleep(wait / 1000000);
                        curTime = System.nanoTime();
                        wait = TICK_TIME - (curTime - lastTick);
                    }

                    catchupTime = Math.min(MAX_CATCHUP_BUFFER, catchupTime - wait);
                    if ( ++MinecraftServer.currentTick % SAMPLE_INTERVAL == 0 )
                    {
                        final long diff = curTime - tickSection;
                        double currentTps = 1E9 / diff * SAMPLE_INTERVAL;
                        tps1.add(currentTps, diff);
                        tps5.add(currentTps, diff);
                        tps15.add(currentTps, diff);
                        // Backwards compat with bad plugins
                        recentTps[0] = tps1.getAverage();
                        recentTps[1] = tps5.getAverage();
                        recentTps[2] = tps15.getAverage();
                        // Paper end
                        tickSection = curTime;
                    }
                    lastTick = curTime;

                    MinecraftServer.currentTick = (int) (System.currentTimeMillis() / 50); // CraftBukkit
                    this.v();
                    this.aa += 50L;
                    this.P = true;
                }
                // Spigot end
            } else {
                this.a((CrashReport) null);
            }
        } catch (Throwable throwable) {
            MinecraftServer.LOGGER.error("Encountered an unexpected exception", throwable);
            // Spigot Start
            if ( throwable.getCause() != null )
            {
                MinecraftServer.LOGGER.error( "\tCause of unexpected exception was", throwable.getCause() );
            }
            // Spigot End
            CrashReport crashreport;
            
            if (throwable instanceof ReportedException) {
                crashreport = this.b(((ReportedException) throwable).a());
            } else {
                crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
            }
            
            File file = new File(new File(this.t(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");
            
            if (crashreport.a(file)) {
                MinecraftServer.LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
            } else {
                MinecraftServer.LOGGER.error("We were unable to save this crash report to disk.");
            }
            
            this.a(crashreport);
        } finally {
            try {
                org.spigotmc.WatchdogThread.doStop();
                this.isStopped = true;
                this.stop();
            } catch (Throwable throwable1) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable1);
            } finally {
                // CraftBukkit start - Restore terminal to original settings
                try {
                    net.minecrell.terminalconsole.TerminalConsoleAppender.close(); // Paper - Use TerminalConsoleAppender
                } catch (Exception ignored) {
                }
                // CraftBukkit end
                this.u();
            }

        }

    }

    public void a(ServerPing serverping) {
        File file = this.c("server-icon.png");

        if (!file.exists()) {
            file = this.getConvertable().b(this.getWorld(), "icon.png");
        }

        if (file.isFile()) {
            ByteBuf bytebuf = Unpooled.buffer();

            try {
                BufferedImage bufferedimage = ImageIO.read(file);

                Validate.validState(bufferedimage.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
                Validate.validState(bufferedimage.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
                ImageIO.write(bufferedimage, "PNG", new ByteBufOutputStream(bytebuf));
                ByteBuffer bytebuffer = Base64.getEncoder().encode(bytebuf.nioBuffer());

                serverping.setFavicon("data:image/png;base64," + StandardCharsets.UTF_8.decode(bytebuffer));
            } catch (Exception exception) {
                MinecraftServer.LOGGER.error("Couldn\'t load server icon", exception);
            } finally {
                bytebuf.release();
            }
        }

    }

    public File t() {
        return new File(".");
    }

    protected void a(CrashReport crashreport) {}

    public void u() {}

    protected void v() {
        co.aikar.timings.TimingsManager.FULL_SERVER_TICK.startTiming(); // Paper
        this.slackActivityAccountant.tickStarted(); // Spigot
        long i = SystemUtils.c(); long startTime = i; // Paper

        ++this.ticks;
        if (this.S) {
            this.S = false;
            this.methodProfiler.a(this.ticks);
        }

        this.methodProfiler.a("root");
        this.w();
        if (i - this.Y >= 5000000000L) {
            this.Y = i;
            this.n.setPlayerSample(new ServerPing.ServerPingPlayerSample(this.B(), this.A()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.A(), org.spigotmc.SpigotConfig.playerSample)]; // Paper
            int j = MathHelper.nextInt(this.o, 0, this.A() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k) {
                agameprofile[k] = ((EntityPlayer) this.s.v().get(j + k)).getProfile();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.n.b().a(agameprofile);
        }

            this.methodProfiler.a("save");

        serverAutoSave = (autosavePeriod > 0 && this.ticks % autosavePeriod == 0); // Paper
        int playerSaveInterval = com.destroystokyo.paper.PaperConfig.playerAutoSaveRate;
        if (playerSaveInterval < 0) {
            playerSaveInterval = autosavePeriod;
        }
        if (playerSaveInterval > 0) { // CraftBukkit // Paper
            this.s.savePlayers(playerSaveInterval);
            // Spigot Start
        } // Paper - Incremental Auto Saving

            // We replace this with saving each individual world as this.saveChunks(...) is broken,
            // and causes the main thread to sleep for random amounts of time depending on chunk activity
            // Also pass flag to only save modified chunks
            server.playerCommandState = true;
            for (World world : worlds) {
                if (world.paperConfig.autoSavePeriod > 0) world.getWorld().save(false); // Paper - Incremental / Configurable Auto Saving
            }
            server.playerCommandState = false;
            // this.saveChunks(true);
            // Spigot End
            this.methodProfiler.e();
        //} // Paper - Incremental Auto Saving

        this.methodProfiler.a("snooper");
        if (getSnooperEnabled() && !this.j.d() && this.ticks > 100) { // Spigot
            this.j.a();
        }

        if (getSnooperEnabled() && this.ticks % 6000 == 0) { // Spigot
            this.j.b();
        }

        this.methodProfiler.e();
        this.methodProfiler.a("tallying");
        long l = this.e[this.ticks % 100] = SystemUtils.c() - i;

        this.ap = this.ap * 0.8F + (float) l / 1000000.0F * 0.19999999F;
        this.methodProfiler.e();
        this.methodProfiler.e();
        org.spigotmc.WatchdogThread.tick(); // Spigot
        PaperLightingQueue.processQueue(startTime); // Paper
        this.slackActivityAccountant.tickEnded(l); // Spigot
        co.aikar.timings.TimingsManager.FULL_SERVER_TICK.stopTiming(); // Paper
    }

    public void w() {
        MinecraftTimings.bukkitSchedulerTimer.startTiming(); // Paper
        this.server.getScheduler().mainThreadHeartbeat(this.ticks); // CraftBukkit
        MinecraftTimings.bukkitSchedulerTimer.stopTiming(); // Paper
        MinecraftTimings.minecraftSchedulerTimer.startTiming(); // Paper
        this.methodProfiler.a("jobs");

        FutureTask futuretask;

        while ((futuretask = (FutureTask) this.g.poll()) != null) {
            SystemUtils.a(futuretask, MinecraftServer.LOGGER);
        }
        MinecraftTimings.minecraftSchedulerTimer.stopTiming(); // Paper

        this.methodProfiler.c("commandFunctions");
        MinecraftTimings.commandFunctionsTimer.startTiming(); // Spigot
        this.getFunctionData().Y_();
        MinecraftTimings.commandFunctionsTimer.stopTiming(); // Spigot
        this.methodProfiler.c("levels");

        // CraftBukkit start
        // Run tasks that are waiting on processing
        MinecraftTimings.processQueueTimer.startTiming(); // Spigot
        while (!processQueue.isEmpty()) {
            processQueue.remove().run();
        }
        MinecraftTimings.processQueueTimer.stopTiming(); // Spigot

        MinecraftTimings.chunkIOTickTimer.startTiming(); // Spigot
        org.bukkit.craftbukkit.chunkio.ChunkIOExecutor.tick();
        MinecraftTimings.chunkIOTickTimer.stopTiming(); // Spigot

        MinecraftTimings.timeUpdateTimer.startTiming(); // Spigot
        // Send time updates to everyone, it will get the right time from the world the player is in.
        if (this.ticks % 20 == 0) {
            for (int i = 0; i < this.getPlayerList().players.size(); ++i) {
                EntityPlayer entityplayer = (EntityPlayer) this.getPlayerList().players.get(i);
                entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateTime(entityplayer.world.getTime(), entityplayer.getPlayerTime(), entityplayer.world.getGameRules().getBoolean("doDaylightCycle"))); // Add support for per player time
            }
        }
        MinecraftTimings.timeUpdateTimer.stopTiming(); // Spigot

        int i;

        for (i = 0; i < this.worlds.size(); ++i) { // CraftBukkit
            long j = SystemUtils.c();

            if (true || i == 0 || this.getAllowNether()) { // CraftBukkit
                WorldServer worldserver = this.worlds.get(i);

                this.methodProfiler.a(() -> {
                    return worldserver.getWorldData().getName();
                });
                /* Drop global time updates
                if (this.ticks % 20 == 0) {
                    this.methodProfiler.a("timeSync");
                    this.s.a((Packet) (new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle"))), worldserver.worldProvider.getDimensionManager().getDimensionID());
                    this.methodProfiler.e();
                }
                // CraftBukkit end */

                this.methodProfiler.a("tick");

                CrashReport crashreport;

                try {
                    worldserver.timings.doTick.startTiming(); // Spigot
                    worldserver.doTick();
                    worldserver.timings.doTick.stopTiming(); // Spigot
                } catch (Throwable throwable) {
                    // Spigot Start
                    try {
                    crashreport = CrashReport.a(throwable, "Exception ticking world");
                    } catch (Throwable t){
                        throw new RuntimeException("Error generating crash report", t);
                    }
                    // Spigot End
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                try {
                    worldserver.timings.tickEntities.startTiming(); // Spigot
                    worldserver.tickEntities();
                    worldserver.timings.tickEntities.stopTiming(); // Spigot
                } catch (Throwable throwable1) {
                    // Spigot Start
                    try {
                    crashreport = CrashReport.a(throwable1, "Exception ticking world entities");
                    } catch (Throwable t){
                        throw new RuntimeException("Error generating crash report", t);
                    }
                    // Spigot End
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                this.methodProfiler.e();
                this.methodProfiler.a("tracker");
                worldserver.getTracker().updatePlayers();
                this.methodProfiler.e();
                this.methodProfiler.e();
                worldserver.explosionDensityCache.clear(); // Paper - Optimize explosions
            }

            // this.f[i][this.ticks % 100] = SystemUtils.c() - j; // CraftBukkit
        }

        this.methodProfiler.c("connection");
        MinecraftTimings.connectionTimer.startTiming(); // Spigot
        this.getServerConnection().c();
        MinecraftTimings.connectionTimer.stopTiming(); // Spigot
        this.methodProfiler.c("players");
        MinecraftTimings.playerListTimer.startTiming(); // Spigot
        this.s.tick();
        MinecraftTimings.playerListTimer.stopTiming(); // Spigot
        this.methodProfiler.c("tickables");
        MinecraftTimings.tickablesTimer.startTiming(); // Spigot
        for (i = 0; i < this.l.size(); ++i) {
            ((ITickable) this.l.get(i)).Y_();
        }
        MinecraftTimings.tickablesTimer.stopTiming(); // Spigot

        this.methodProfiler.e();
    }

    public boolean getAllowNether() {
        return true;
    }

    public void a(ITickable itickable) {
        this.l.add(itickable);
    }

    public static void main(final OptionSet options) { // CraftBukkit - replaces main(String[] astring)
        DispenserRegistry.c();

        try {
            /* CraftBukkit start - Replace everything
            boolean flag = true;
            String s = null;
            String s1 = ".";
            String s2 = null;
            boolean flag1 = false;
            boolean flag2 = false;
            boolean flag3 = false;
            int i = -1;

            for (int j = 0; j < astring.length; ++j) {
                String s3 = astring[j];
                String s4 = j == astring.length - 1 ? null : astring[j + 1];
                boolean flag4 = false;

                if (!"nogui".equals(s3) && !"--nogui".equals(s3)) {
                    if ("--port".equals(s3) && s4 != null) {
                        flag4 = true;

                        try {
                            i = Integer.parseInt(s4);
                        } catch (NumberFormatException numberformatexception) {
                            ;
                        }
                    } else if ("--singleplayer".equals(s3) && s4 != null) {
                        flag4 = true;
                        s = s4;
                    } else if ("--universe".equals(s3) && s4 != null) {
                        flag4 = true;
                        s1 = s4;
                    } else if ("--world".equals(s3) && s4 != null) {
                        flag4 = true;
                        s2 = s4;
                    } else if ("--demo".equals(s3)) {
                        flag1 = true;
                    } else if ("--bonusChest".equals(s3)) {
                        flag2 = true;
                    } else if ("--forceUpgrade".equals(s3)) {
                        flag3 = true;
                    }
                } else {
                    flag = false;
                }

                if (flag4) {
                    ++j;
                }
            }
            */ // CraftBukkit end

            String s1 = "."; // PAIL?
            YggdrasilAuthenticationService yggdrasilauthenticationservice = new com.destroystokyo.paper.profile.PaperAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString()); // Paper
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            UserCache usercache = new UserCache(gameprofilerepository, new File(s1, MinecraftServer.a.getName()));
            final DedicatedServer dedicatedserver = new DedicatedServer(options, DataConverterRegistry.a(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, usercache);

            /* CraftBukkit start
            if (s != null) {
                dedicatedserver.h(s);
            }

            if (s2 != null) {
                dedicatedserver.setWorld(s2);
            }

            if (i >= 0) {
                dedicatedserver.setPort(i);
            }

            if (flag1) {
                dedicatedserver.c(true);
            }

            if (flag2) {
                dedicatedserver.d(true);
            }

            if (flag && !GraphicsEnvironment.isHeadless()) {
                dedicatedserver.aY();
            }

            if (flag3) {
                dedicatedserver.setForceUpgrade(true);
            }

            dedicatedserver.y();
            Thread thread = new Thread("Server Shutdown Thread") {
                public void run() {
                    dedicatedserver.stop();
                }
            };

            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(MinecraftServer.LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
            */

            if (options.has("port")) {
                int port = (Integer) options.valueOf("port");
                if (port > 0) {
                    dedicatedserver.setPort(port);
                }
            }

            if (options.has("universe")) {
                dedicatedserver.universe = (File) options.valueOf("universe");
            }

            if (options.has("world")) {
                dedicatedserver.setWorld((String) options.valueOf("world"));
            }

            if (options.has("forceUpgrade")) {
                dedicatedserver.setForceUpgrade(true);
            }

            dedicatedserver.primaryThread.start();
            // CraftBukkit end
        } catch (Exception exception) {
            MinecraftServer.LOGGER.fatal("Failed to start the minecraft server", exception);
        }

    }

    protected void setForceUpgrade(boolean flag) {
        this.forceUpgrade = flag;
    }

    public void y() {
        /* CraftBukkit start - prevent abuse
        this.serverThread = new Thread(this, "Server thread");
        this.serverThread.setUncaughtExceptionHandler((thread, throwable) -> {
            MinecraftServer.LOGGER.error(throwable);
        });
        this.serverThread.start();
        // CraftBukkit end */
    }

    public File c(String s) {
        return new File(this.t(), s);
    }

    public void info(String s) {
        MinecraftServer.LOGGER.info(s);
    }

    public void warning(String s) {
        MinecraftServer.LOGGER.warn(s);
    }

    public WorldServer getWorldServer(int i) {
        // CraftBukkit start
        for (WorldServer world : worlds) {
            if (world.dimension == i) {
                return world;
            }
        }
        return worlds.get(0);
        // CraftBukkit end
    }

    public WorldServer a(DimensionManager dimensionmanager) {
        return dimensionmanager == DimensionManager.NETHER ? this.worlds.get(1) : (dimensionmanager == DimensionManager.THE_END ? this.worlds.get(2) : this.worlds.get(0)); // CraftBukkit
    }

    public String getVersion() {
        return "1.13";
    }

    public int getPlayerCount() { return A(); } // Paper - OBFHELPER
    public int A() {
        return this.s.getPlayerCount();
    }

    public int getMaxPlayers() { return B(); } // Paper - OBFHELPER
    public int B() {
        return this.s.getMaxPlayers();
    }

    public String[] getPlayers() {
        return this.s.f();
    }

    public boolean isDebugging() {
        return this.getPropertyManager().getBoolean("debug", false); // CraftBukkit - don't hardcode
    }

    public void f(String s) {
        MinecraftServer.LOGGER.error(s);
    }

    public void g(String s) {
        if (this.isDebugging()) {
            MinecraftServer.LOGGER.info(s);
        }

    }

    public String getServerModName() {
        return "Paper"; //Paper - Paper > // Spigot - Spigot > // CraftBukkit - cb > vanilla!
    }

    public CrashReport b(CrashReport crashreport) {
        crashreport.g().a("Profiler Position", () -> {
            return this.methodProfiler.a() ? this.methodProfiler.f() : "N/A (disabled)";
        });
        if (this.s != null) {
            crashreport.g().a("Player Count", () -> {
                return this.s.getPlayerCount() + " / " + this.s.getMaxPlayers() + "; " + this.s.v();
            });
        }

        crashreport.g().a("Data Packs", () -> {
            StringBuilder stringbuilder = new StringBuilder();
            Iterator iterator = this.resourcePackRepository.d().iterator();

            while (iterator.hasNext()) {
                ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

                if (stringbuilder.length() > 0) {
                    stringbuilder.append(", ");
                }

                stringbuilder.append(resourcepackloader.e());
                if (!resourcepackloader.c().a()) {
                    stringbuilder.append(" (incompatible)");
                }
            }

            return stringbuilder.toString();
        });
        return crashreport;
    }

    public boolean F() {
        return true; // CraftBukkit
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        MinecraftServer.LOGGER.info(org.bukkit.craftbukkit.util.CraftChatMessage.fromComponent(ichatbasecomponent, net.minecraft.server.EnumChatFormat.WHITE));// Paper - Log message with colors
    }

    public KeyPair G() {
        return this.H;
    }

    public int H() {
        return this.r;
    }

    public void setPort(int i) {
        this.r = i;
    }

    public String I() {
        return this.I;
    }

    public void h(String s) {
        this.I = s;
    }

    public boolean J() {
        return this.I != null;
    }

    public String getWorld() {
        return this.J;
    }

    public void setWorld(String s) {
        this.J = s;
    }

    public void a(KeyPair keypair) {
        this.H = keypair;
    }

    public void a(EnumDifficulty enumdifficulty) {
        // CraftBukkit start
        // WorldServer[] aworldserver = this.worldServer;
        int i = this.worlds.size();

        for (int j = 0; j < i; ++j) {
            WorldServer worldserver = this.worlds.get(j);
            // CraftBukkit end

            if (worldserver != null) {
                if (worldserver.getWorldData().isHardcore()) {
                    worldserver.getWorldData().setDifficulty(EnumDifficulty.HARD);
                    worldserver.setSpawnFlags(true, true);
                } else if (this.J()) {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(worldserver.getDifficulty() != EnumDifficulty.PEACEFUL, true);
                } else {
                    worldserver.getWorldData().setDifficulty(enumdifficulty);
                    worldserver.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
                }
            }
        }

    }

    public boolean getSpawnMonsters() {
        return true;
    }

    public boolean N() {
        return this.demoMode;
    }

    public void c(boolean flag) {
        this.demoMode = flag;
    }

    public void d(boolean flag) {
        this.M = flag;
    }

    public Convertable getConvertable() {
        return this.convertable;
    }

    public String getResourcePack() {
        return this.N;
    }

    public String getResourcePackHash() {
        return this.O;
    }

    public void setResourcePack(String s, String s1) {
        this.N = s;
        this.O = s1;
    }

    public void a(MojangStatisticsGenerator mojangstatisticsgenerator) {
        mojangstatisticsgenerator.a("whitelist_enabled", Boolean.valueOf(false));
        mojangstatisticsgenerator.a("whitelist_count", Integer.valueOf(0));
        if (this.s != null) {
            mojangstatisticsgenerator.a("players_current", Integer.valueOf(this.A()));
            mojangstatisticsgenerator.a("players_max", Integer.valueOf(this.B()));
            mojangstatisticsgenerator.a("players_seen", Integer.valueOf(this.s.getSeenPlayers().length));
        }

        mojangstatisticsgenerator.a("uses_auth", Boolean.valueOf(this.onlineMode));
        mojangstatisticsgenerator.a("gui_state", this.ai() ? "enabled" : "disabled");
        mojangstatisticsgenerator.a("run_time", Long.valueOf((SystemUtils.b() - mojangstatisticsgenerator.g()) / 60L * 1000L));
        mojangstatisticsgenerator.a("avg_tick_ms", Integer.valueOf((int) (MathHelper.a(this.e) * 1.0E-6D)));
        int i = 0;

        if (this.worldServer != null) {
            // CraftBukkit start
            for (int j = 0; j < this.worlds.size(); ++j) {
                WorldServer worldserver = this.worlds.get(j);
                if (worldserver != null) {
                    // CraftBukkit end
                    WorldData worlddata = worldserver.getWorldData();

                    mojangstatisticsgenerator.a("world[" + i + "][dimension]", Integer.valueOf(worldserver.worldProvider.getDimensionManager().getDimensionID()));
                    mojangstatisticsgenerator.a("world[" + i + "][mode]", worlddata.getGameType());
                    mojangstatisticsgenerator.a("world[" + i + "][difficulty]", worldserver.getDifficulty());
                    mojangstatisticsgenerator.a("world[" + i + "][hardcore]", Boolean.valueOf(worlddata.isHardcore()));
                    mojangstatisticsgenerator.a("world[" + i + "][generator_name]", worlddata.getType().name());
                    mojangstatisticsgenerator.a("world[" + i + "][generator_version]", Integer.valueOf(worlddata.getType().getVersion()));
                    mojangstatisticsgenerator.a("world[" + i + "][height]", Integer.valueOf(this.F));
                    mojangstatisticsgenerator.a("world[" + i + "][chunks_loaded]", Integer.valueOf(worldserver.getChunkProviderServer().h()));
                    ++i;
                }
            }
        }

        mojangstatisticsgenerator.a("worlds", Integer.valueOf(i));
    }

    public boolean getSnooperEnabled() {
        return true;
    }

    public abstract boolean S();

    public boolean getOnlineMode() {
        return server.getOnlineMode(); // CraftBukkit
    }

    public void setOnlineMode(boolean flag) {
        this.onlineMode = flag;
    }

    public boolean U() {
        return this.z;
    }

    public void f(boolean flag) {
        this.z = flag;
    }

    public boolean getSpawnAnimals() {
        return this.spawnAnimals;
    }

    public void setSpawnAnimals(boolean flag) {
        this.spawnAnimals = flag;
    }

    public boolean getSpawnNPCs() {
        return this.spawnNPCs;
    }

    public abstract boolean X();

    public void setSpawnNPCs(boolean flag) {
        this.spawnNPCs = flag;
    }

    public boolean getPVP() {
        return this.pvpMode;
    }

    public void setPVP(boolean flag) {
        this.pvpMode = flag;
    }

    public boolean getAllowFlight() {
        return this.allowFlight;
    }

    public void setAllowFlight(boolean flag) {
        this.allowFlight = flag;
    }

    public abstract boolean getEnableCommandBlock();

    public String getMotd() {
        return this.motd;
    }

    public void setMotd(String s) {
        this.motd = s;
    }

    public int getMaxBuildHeight() {
        return this.F;
    }

    public void c(int i) {
        this.F = i;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public PlayerList getPlayerList() {
        return this.s;
    }

    public void a(PlayerList playerlist) {
        this.s = playerlist;
    }

    public abstract boolean af();

    public void setGamemode(EnumGamemode enumgamemode) {
        // CraftBukkit start
        for (int i = 0; i < this.worlds.size(); ++i) {
            worlds.get(i).getWorldData().setGameType(enumgamemode);
        }

    }

    public ServerConnection getServerConnection() {
        return this.m == null ? this.m = new ServerConnection(this) : this.m; // Spigot
    }

    public boolean ai() {
        return false;
    }

    public abstract boolean a(EnumGamemode enumgamemode, boolean flag, int i);

    public int aj() {
        return this.ticks;
    }

    public void ak() {
        this.S = true;
    }

    public int getSpawnProtection() {
        return 16;
    }

    public boolean a(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return false;
    }

    public void setForceGamemode(boolean flag) {
        this.T = flag;
    }

    public boolean getForceGamemode() {
        return this.T;
    }

    public int getIdleTimeout() {
        return this.G;
    }

    public void setIdleTimeout(int i) {
        this.G = i;
    }

    public MinecraftSessionService getSessionService() { return ar(); } // Paper - OBFHELPER
    public MinecraftSessionService ar() {
        return this.V;
    }

    public GameProfileRepository getGameProfileRepository() {
        return this.W;
    }

    public UserCache getUserCache() {
        return this.X;
    }

    public ServerPing getServerPing() {
        return this.n;
    }

    public void av() {
        this.Y = 0L;
    }

    public int aw() {
        return 29999984;
    }

    public <V> ListenableFuture<V> a(Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isMainThread()) { // CraftBukkit && !this.isStopped()) {
            ListenableFutureTask listenablefuturetask = ListenableFutureTask.create(callable);

            this.g.add(listenablefuturetask);
            return listenablefuturetask;
        } else {
            try {
                return Futures.immediateFuture(callable.call());
            } catch (Exception exception) {
                return Futures.immediateFailedCheckedFuture(exception);
            }
        }
    }

    public ListenableFuture<Object> postToMainThread(Runnable runnable) {
        Validate.notNull(runnable);
        return this.a(Executors.callable(runnable));
    }

    public boolean isMainThread() {
        return Thread.currentThread() == this.serverThread;
    }

    public int ay() {
        return 256;
    }

    public long az() {
        return this.aa;
    }

    public final Thread getServerThread() { return this.aA(); } // Paper - OBFHELPER
    public Thread aA() {
        return this.serverThread;
    }

    public DataFixer aB() {
        return this.dataConverterManager;
    }

    public int a(@Nullable WorldServer worldserver) {
        return worldserver != null ? worldserver.getGameRules().c("spawnRadius") : 10;
    }

    public AdvancementDataWorld getAdvancementData() {
        return this.al;
    }

    public CustomFunctionData getFunctionData() {
        return this.am;
    }

    public void reload() {
        if (!this.isMainThread()) {
            this.postToMainThread(this::reload);
        } else {
            this.getPlayerList().savePlayers();
            this.resourcePackRepository.a();
            this.a(this.worlds.get(0).getWorldData()); // CraftBukkit
            this.getPlayerList().reload();
        }
    }

    private void a(WorldData worlddata) {
        ArrayList arraylist = Lists.newArrayList(this.resourcePackRepository.d());
        Iterator iterator = this.resourcePackRepository.b().iterator();

        while (iterator.hasNext()) {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

            if (!worlddata.N().contains(resourcepackloader.e()) && !arraylist.contains(resourcepackloader)) {
                MinecraftServer.LOGGER.info("Found new data pack {}, loading it automatically", resourcepackloader.e());
                resourcepackloader.h().a(arraylist, resourcepackloader, (resourcepackloader1) -> { // CraftBukkit - decompile error
                    return resourcepackloader1; // CraftBukkit - decompile error
                }, false);
            }
        }

        this.resourcePackRepository.a((Collection) arraylist);
        ArrayList arraylist1 = Lists.newArrayList();

        this.resourcePackRepository.d().forEach((resourcepackloader) -> {
            arraylist1.add(resourcepackloader.d()); // CraftBukkit - decompile error
        });
        this.ac.a((List) arraylist1);
        worlddata.O().clear();
        worlddata.N().clear();
        this.resourcePackRepository.d().forEach((resourcepackloader) -> {
            worlddata.O().add(resourcepackloader.e());
        });
        this.resourcePackRepository.b().forEach((resourcepackloader) -> {
            if (!this.resourcePackRepository.d().contains(resourcepackloader)) {
                worlddata.N().add(resourcepackloader.e());
            }

        });
    }

    public void a(CommandListenerWrapper commandlistenerwrapper) {
        if (this.aS()) {
            PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();
            WhiteList whitelist = playerlist.getWhitelist();

            if (whitelist.isEnabled()) {
                ArrayList arraylist = Lists.newArrayList(playerlist.v());
                Iterator iterator = arraylist.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    if (!whitelist.isWhitelisted(entityplayer.getProfile())) {
                        entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.not_whitelisted", new Object[0]));
                    }
                }

            }
        }
    }

    public IReloadableResourceManager getResourceManager() {
        return this.ac;
    }

    public ResourcePackRepository<ResourcePackLoader> getResourcePackRepository() {
        return this.resourcePackRepository;
    }

    public CommandDispatcher getCommandDispatcher() {
        return this.commandDispatcher;
    }

    public CommandListenerWrapper getServerCommandListener() {
        return new CommandListenerWrapper(this, this.worlds.isEmpty() ? Vec3D.a : new Vec3D(this.worlds.get(0).getSpawn()), Vec2F.a, this.worlds.isEmpty() ? null : this.worlds.get(0), 4, "Server", new ChatComponentText("Server"), this, (Entity) null); // CraftBukkit
    }

    public boolean a() {
        return true;
    }

    public boolean b() {
        return true;
    }

    public CraftingManager getCraftingManager() {
        return this.ag;
    }

    public TagRegistry getTagRegistry() {
        return this.ah;
    }

    public ScoreboardServer getScoreboard() {
        return this.ai;
    }

    public LootTableRegistry aP() {
        return this.ak;
    }

    public GameRules aQ() {
        return this.worlds.get(0).getGameRules(); // CraftBukkit
    }

    public BossBattleCustomData aR() {
        return this.aj;
    }

    public boolean aS() {
        return this.an;
    }

    public void l(boolean flag) {
        this.an = flag;
    }

    public int a(GameProfile gameprofile) {
        if (this.getPlayerList().isOp(gameprofile)) {
            OpListEntry oplistentry = (OpListEntry) this.getPlayerList().getOPs().get(gameprofile);

            return oplistentry != null ? oplistentry.a() : (this.J() ? (this.I().equals(gameprofile.getName()) ? 4 : (this.getPlayerList().x() ? 4 : 0)) : this.k());
        } else {
            return 0;
        }
    }

    // CraftBukkit start
    @Deprecated
    public static MinecraftServer getServer() {
        return SERVER;
    }
    // CraftBukkit end
}
