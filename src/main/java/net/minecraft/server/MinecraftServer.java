package net.minecraft.server;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
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
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.awt.GraphicsEnvironment;
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
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import java.util.function.BooleanSupplier;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements IAsyncTaskHandler, IMojangStatistics, ICommandListener, Runnable {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final File a = new File("usercache.json");
    public Convertable convertable;
    private final MojangStatisticsGenerator snooper = new MojangStatisticsGenerator("server", this, SystemUtils.getMonotonicMillis());
    public File universe;
    private final List<ITickable> k = Lists.newArrayList();
    public final MethodProfiler methodProfiler = new MethodProfiler();
    private final ServerConnection serverConnection;
    private final ServerPing m = new ServerPing();
    private final Random n = new Random();
    public final DataFixer dataConverterManager;
    private String serverIp;
    private int q = -1;
    public final Map<DimensionManager, WorldServer> worldServer = Maps.newIdentityHashMap();
    private PlayerList playerList;
    private boolean isRunning = true;
    private boolean isStopped;
    private int ticks;
    protected final Proxy c;
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
    public final long[] d = new long[100];
    protected final Map<DimensionManager, long[]> e = Maps.newIdentityHashMap();
    private KeyPair H;
    private String I;
    private String J;
    private boolean demoMode;
    private boolean M;
    private String N = "";
    private String O = "";
    private boolean P;
    private long lastOverloadTime;
    private IChatBaseComponent R;
    private boolean S;
    private boolean T;
    private final YggdrasilAuthenticationService U;
    private final MinecraftSessionService V;
    private final GameProfileRepository W;
    private final UserCache X;
    private long Y;
    protected final Queue<FutureTask<?>> f = Queues.newConcurrentLinkedQueue();
    private Thread serverThread;
    private long nextTick = SystemUtils.getMonotonicMillis();
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

    public MinecraftServer(@Nullable File file, Proxy proxy, DataFixer datafixer, CommandDispatcher commanddispatcher, YggdrasilAuthenticationService yggdrasilauthenticationservice, MinecraftSessionService minecraftsessionservice, GameProfileRepository gameprofilerepository, UserCache usercache) {
        this.ac = new ResourceManager(EnumResourcePackType.SERVER_DATA);
        this.resourcePackRepository = new ResourcePackRepository<>(ResourcePackLoader::new);
        this.ag = new CraftingManager();
        this.ah = new TagRegistry();
        this.ai = new ScoreboardServer(this);
        this.aj = new BossBattleCustomData(this);
        this.ak = new LootTableRegistry();
        this.al = new AdvancementDataWorld();
        this.am = new CustomFunctionData(this);
        this.c = proxy;
        this.commandDispatcher = commanddispatcher;
        this.U = yggdrasilauthenticationservice;
        this.V = minecraftsessionservice;
        this.W = gameprofilerepository;
        this.X = usercache;
        this.universe = file;
        this.serverConnection = file == null ? null : new ServerConnection(this);
        this.convertable = file == null ? null : new WorldLoaderServer(file.toPath(), file.toPath().resolve("../backups"), datafixer);
        this.dataConverterManager = datafixer;
        this.ac.a((IResourcePackListener) this.ah);
        this.ac.a((IResourcePackListener) this.ag);
        this.ac.a((IResourcePackListener) this.ak);
        this.ac.a((IResourcePackListener) this.am);
        this.ac.a((IResourcePackListener) this.al);
    }

    public abstract boolean init() throws IOException;

    public void convertWorld(String s) {
        if (this.getConvertable().isConvertable(s)) {
            MinecraftServer.LOGGER.info("Converting map!");
            this.b((IChatBaseComponent) (new ChatMessage("menu.convertingLevel", new Object[0])));
            this.getConvertable().convert(s, new IProgressUpdate() {
                private long b = SystemUtils.getMonotonicMillis();

                public void a(IChatBaseComponent ichatbasecomponent) {}

                public void a(int i) {
                    if (SystemUtils.getMonotonicMillis() - this.b >= 1000L) {
                        this.b = SystemUtils.getMonotonicMillis();
                        MinecraftServer.LOGGER.info("Converting... {}%", i);
                    }

                }

                public void c(IChatBaseComponent ichatbasecomponent) {}
            });
        }

        if (this.forceUpgrade) {
            MinecraftServer.LOGGER.info("Forcing world upgrade!");
            WorldData worlddata = this.getConvertable().c(this.getWorld());

            if (worlddata != null) {
                WorldUpgrader worldupgrader = new WorldUpgrader(this.getWorld(), this.getConvertable(), worlddata);
                IChatBaseComponent ichatbasecomponent = null;

                while (!worldupgrader.b()) {
                    IChatBaseComponent ichatbasecomponent1 = worldupgrader.g();

                    if (ichatbasecomponent != ichatbasecomponent1) {
                        ichatbasecomponent = ichatbasecomponent1;
                        MinecraftServer.LOGGER.info(worldupgrader.g().getString());
                    }

                    int i = worldupgrader.d();

                    if (i > 0) {
                        int j = worldupgrader.e() + worldupgrader.f();

                        MinecraftServer.LOGGER.info("{}% completed ({} / {} chunks)...", MathHelper.d((float) j / (float) i * 100.0F), j, i);
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
        this.convertWorld(s);
        this.b((IChatBaseComponent) (new ChatMessage("menu.loadingLevel", new Object[0])));
        IDataManager idatamanager = this.getConvertable().a(s, this);

        this.a(this.getWorld(), idatamanager);
        WorldData worlddata = idatamanager.getWorldData();
        WorldSettings worldsettings;

        if (worlddata == null) {
            if (this.L()) {
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
        PersistentCollection persistentcollection = new PersistentCollection(idatamanager);

        this.a(idatamanager, persistentcollection, worlddata, worldsettings);
        this.a(this.getDifficulty());
        this.a(persistentcollection);
    }

    protected void a(IDataManager idatamanager, PersistentCollection persistentcollection, WorldData worlddata, WorldSettings worldsettings) {
        if (this.L()) {
            this.worldServer.put(DimensionManager.OVERWORLD, (new DemoWorldServer(this, idatamanager, persistentcollection, worlddata, DimensionManager.OVERWORLD, this.methodProfiler)).i_());
        } else {
            this.worldServer.put(DimensionManager.OVERWORLD, (new WorldServer(this, idatamanager, persistentcollection, worlddata, DimensionManager.OVERWORLD, this.methodProfiler)).i_());
        }

        WorldServer worldserver = this.getWorldServer(DimensionManager.OVERWORLD);

        worldserver.a(worldsettings);
        worldserver.addIWorldAccess(new WorldManager(this, worldserver));
        if (!this.H()) {
            worldserver.getWorldData().setGameType(this.getGamemode());
        }

        SecondaryWorldServer secondaryworldserver = (new SecondaryWorldServer(this, idatamanager, DimensionManager.NETHER, worldserver, this.methodProfiler)).i_();

        this.worldServer.put(DimensionManager.NETHER, secondaryworldserver);
        secondaryworldserver.addIWorldAccess(new WorldManager(this, secondaryworldserver));
        if (!this.H()) {
            secondaryworldserver.getWorldData().setGameType(this.getGamemode());
        }

        SecondaryWorldServer secondaryworldserver1 = (new SecondaryWorldServer(this, idatamanager, DimensionManager.THE_END, worldserver, this.methodProfiler)).i_();

        this.worldServer.put(DimensionManager.THE_END, secondaryworldserver1);
        secondaryworldserver1.addIWorldAccess(new WorldManager(this, secondaryworldserver1));
        if (!this.H()) {
            secondaryworldserver1.getWorldData().setGameType(this.getGamemode());
        }

        this.getPlayerList().setPlayerFileData(worldserver);
        if (worlddata.P() != null) {
            this.getBossBattleCustomData().a(worlddata.P());
        }

    }

    protected void a(File file, WorldData worlddata) {
        this.resourcePackRepository.a((ResourcePackSource) (new ResourcePackSourceVanilla()));
        this.resourcePackFolder = new ResourcePackSourceFolder(new File(file, "datapacks"));
        this.resourcePackRepository.a((ResourcePackSource) this.resourcePackFolder);
        this.resourcePackRepository.a();
        List<ResourcePackLoader> list = Lists.newArrayList();
        Iterator iterator = worlddata.O().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();
            ResourcePackLoader resourcepackloader = this.resourcePackRepository.a(s);

            if (resourcepackloader != null) {
                list.add(resourcepackloader);
            } else {
                MinecraftServer.LOGGER.warn("Missing data pack {}", s);
            }
        }

        this.resourcePackRepository.a((Collection) list);
        this.a(worlddata);
    }

    protected void a(PersistentCollection persistentcollection) {
        boolean flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;

        this.b((IChatBaseComponent) (new ChatMessage("menu.generatingTerrain", new Object[0])));
        WorldServer worldserver = this.getWorldServer(DimensionManager.OVERWORLD);

        MinecraftServer.LOGGER.info("Preparing start region for dimension " + DimensionManager.a(worldserver.worldProvider.getDimensionManager()));
        BlockPosition blockposition = worldserver.getSpawn();
        List<ChunkCoordIntPair> list = Lists.newArrayList();
        Set<ChunkCoordIntPair> set = Sets.newConcurrentHashSet();
        Stopwatch stopwatch = Stopwatch.createStarted();

        for (int i = -192; i <= 192 && this.isRunning(); i += 16) {
            for (int j = -192; j <= 192 && this.isRunning(); j += 16) {
                list.add(new ChunkCoordIntPair(blockposition.getX() + i >> 4, blockposition.getZ() + j >> 4));
            }

            CompletableFuture completablefuture = worldserver.getChunkProvider().a((Iterable) list, (chunk) -> {
                set.add(chunk.getPos());
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
                    this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / 625);
                }
            }

            this.a(new ChatMessage("menu.preparingSpawn", new Object[0]), set.size() * 100 / 625);
        }

        MinecraftServer.LOGGER.info("Time elapsed: {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        Iterator iterator = DimensionManager.b().iterator();

        while (iterator.hasNext()) {
            DimensionManager dimensionmanager = (DimensionManager) iterator.next();
            ForcedChunk forcedchunk = (ForcedChunk) persistentcollection.get(dimensionmanager, ForcedChunk::new, "chunks");

            if (forcedchunk != null) {
                WorldServer worldserver1 = this.getWorldServer(dimensionmanager);
                LongIterator longiterator = forcedchunk.a().iterator();

                while (longiterator.hasNext()) {
                    this.a(new ChatMessage("menu.loadingForcedChunks", new Object[] { dimensionmanager}), forcedchunk.a().size() * 100 / 625);
                    long k = longiterator.nextLong();
                    ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(k);

                    worldserver1.getChunkProvider().getChunkAt(chunkcoordintpair.x, chunkcoordintpair.z, true, true);
                }
            }
        }

        this.l();
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

    public abstract int j();

    public abstract boolean k();

    protected void a(IChatBaseComponent ichatbasecomponent, int i) {
        this.w = ichatbasecomponent;
        this.x = i;
        MinecraftServer.LOGGER.info("{}: {}%", ichatbasecomponent.getString(), i);
    }

    protected void l() {
        this.w = null;
        this.x = 0;
    }

    protected void saveChunks(boolean flag) {
        Iterator iterator = this.getWorlds().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver != null) {
                if (!flag) {
                    MinecraftServer.LOGGER.info("Saving chunks for level '{}'/{}", worldserver.getWorldData().getName(), DimensionManager.a(worldserver.worldProvider.getDimensionManager()));
                }

                try {
                    worldserver.save(true, (IProgressUpdate) null);
                } catch (ExceptionWorldConflict exceptionworldconflict) {
                    MinecraftServer.LOGGER.warn(exceptionworldconflict.getMessage());
                }
            }
        }

    }

    protected void stop() {
        MinecraftServer.LOGGER.info("Stopping server");
        if (this.getServerConnection() != null) {
            this.getServerConnection().b();
        }

        if (this.playerList != null) {
            MinecraftServer.LOGGER.info("Saving players");
            this.playerList.savePlayers();
            this.playerList.u();
        }

        MinecraftServer.LOGGER.info("Saving worlds");
        Iterator iterator = this.getWorlds().iterator();

        WorldServer worldserver;

        while (iterator.hasNext()) {
            worldserver = (WorldServer) iterator.next();
            if (worldserver != null) {
                worldserver.savingDisabled = false;
            }
        }

        this.saveChunks(false);
        iterator = this.getWorlds().iterator();

        while (iterator.hasNext()) {
            worldserver = (WorldServer) iterator.next();
            if (worldserver != null) {
                worldserver.close();
            }
        }

        if (this.snooper.d()) {
            this.snooper.e();
        }

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

    public void safeShutdown() {
        this.isRunning = false;
    }

    private boolean canSleepForTick() {
        return SystemUtils.getMonotonicMillis() < this.nextTick;
    }

    public void run() {
        try {
            if (this.init()) {
                this.nextTick = SystemUtils.getMonotonicMillis();
                this.m.setMOTD(new ChatComponentText(this.motd));
                this.m.setServerInfo(new ServerPing.ServerData("1.13.2", 404));
                this.a(this.m);

                while (this.isRunning) {
                    long i = SystemUtils.getMonotonicMillis() - this.nextTick;

                    if (i > 2000L && this.nextTick - this.lastOverloadTime >= 15000L) {
                        long j = i / 50L;

                        MinecraftServer.LOGGER.warn("Can't keep up! Is the server overloaded? Running {}ms or {} ticks behind", i, j);
                        this.nextTick += j * 50L;
                        this.lastOverloadTime = this.nextTick;
                    }

                    this.a(this::canSleepForTick);
                    this.nextTick += 50L;

                    while (this.canSleepForTick()) {
                        Thread.sleep(1L);
                    }

                    this.P = true;
                }
            } else {
                this.a((CrashReport) null);
            }
        } catch (Throwable throwable) {
            MinecraftServer.LOGGER.error("Encountered an unexpected exception", throwable);
            CrashReport crashreport;

            if (throwable instanceof ReportedException) {
                crashreport = this.b(((ReportedException) throwable).a());
            } else {
                crashreport = this.b(new CrashReport("Exception in server tick loop", throwable));
            }

            File file = new File(new File(this.s(), "crash-reports"), "crash-" + (new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss")).format(new Date()) + "-server.txt");

            if (crashreport.a(file)) {
                MinecraftServer.LOGGER.error("This crash report has been saved to: {}", file.getAbsolutePath());
            } else {
                MinecraftServer.LOGGER.error("We were unable to save this crash report to disk.");
            }

            this.a(crashreport);
        } finally {
            try {
                this.isStopped = true;
                this.stop();
            } catch (Throwable throwable1) {
                MinecraftServer.LOGGER.error("Exception stopping the server", throwable1);
            } finally {
                this.t();
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
                MinecraftServer.LOGGER.error("Couldn't load server icon", exception);
            } finally {
                bytebuf.release();
            }
        }

    }

    public File s() {
        return new File(".");
    }

    protected void a(CrashReport crashreport) {}

    public void t() {}

    protected void a(BooleanSupplier booleansupplier) {
        long i = SystemUtils.getMonotonicNanos();

        ++this.ticks;
        if (this.S) {
            this.S = false;
            this.methodProfiler.a(this.ticks);
        }

        this.methodProfiler.enter("root");
        this.b(booleansupplier);
        if (i - this.Y >= 5000000000L) {
            this.Y = i;
            this.m.setPlayerSample(new ServerPing.ServerPingPlayerSample(this.getMaxPlayers(), this.getPlayerCount()));
            GameProfile[] agameprofile = new GameProfile[Math.min(this.getPlayerCount(), 12)];
            int j = MathHelper.nextInt(this.n, 0, this.getPlayerCount() - agameprofile.length);

            for (int k = 0; k < agameprofile.length; ++k) {
                agameprofile[k] = ((EntityPlayer) this.playerList.v().get(j + k)).getProfile();
            }

            Collections.shuffle(Arrays.asList(agameprofile));
            this.m.b().a(agameprofile);
        }

        if (this.ticks % 900 == 0) {
            this.methodProfiler.enter("save");
            this.playerList.savePlayers();
            this.saveChunks(true);
            this.methodProfiler.exit();
        }

        this.methodProfiler.enter("snooper");
        if (!this.snooper.d() && this.ticks > 100) {
            this.snooper.a();
        }

        if (this.ticks % 6000 == 0) {
            this.snooper.b();
        }

        this.methodProfiler.exit();
        this.methodProfiler.enter("tallying");
        long l = this.d[this.ticks % 100] = SystemUtils.getMonotonicNanos() - i;

        this.ap = this.ap * 0.8F + (float) l / 1000000.0F * 0.19999999F;
        this.methodProfiler.exit();
        this.methodProfiler.exit();
    }

    public void b(BooleanSupplier booleansupplier) {
        this.methodProfiler.enter("jobs");

        FutureTask futuretask;

        while ((futuretask = (FutureTask) this.f.poll()) != null) {
            SystemUtils.a(futuretask, MinecraftServer.LOGGER);
        }

        this.methodProfiler.exitEnter("commandFunctions");
        this.getFunctionData().tick();
        this.methodProfiler.exitEnter("levels");

        WorldServer worldserver;
        long i;

        for (Iterator iterator = this.getWorlds().iterator(); iterator.hasNext();((long[]) this.e.computeIfAbsent(worldserver.worldProvider.getDimensionManager(), (dimensionmanager) -> {
            return new long[100];
        }))[this.ticks % 100] = SystemUtils.getMonotonicNanos() - i) {
            worldserver = (WorldServer) iterator.next();
            i = SystemUtils.getMonotonicNanos();
            if (worldserver.worldProvider.getDimensionManager() == DimensionManager.OVERWORLD || this.getAllowNether()) {
                this.methodProfiler.a(() -> {
                    return "dim-" + worldserver.worldProvider.getDimensionManager().getDimensionID();
                });
                if (this.ticks % 20 == 0) {
                    this.methodProfiler.enter("timeSync");
                    this.playerList.a((Packet) (new PacketPlayOutUpdateTime(worldserver.getTime(), worldserver.getDayTime(), worldserver.getGameRules().getBoolean("doDaylightCycle"))), worldserver.worldProvider.getDimensionManager());
                    this.methodProfiler.exit();
                }

                this.methodProfiler.enter("tick");

                CrashReport crashreport;

                try {
                    worldserver.doTick(booleansupplier);
                } catch (Throwable throwable) {
                    crashreport = CrashReport.a(throwable, "Exception ticking world");
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                try {
                    worldserver.tickEntities();
                } catch (Throwable throwable1) {
                    crashreport = CrashReport.a(throwable1, "Exception ticking world entities");
                    worldserver.a(crashreport);
                    throw new ReportedException(crashreport);
                }

                this.methodProfiler.exit();
                this.methodProfiler.enter("tracker");
                worldserver.getTracker().updatePlayers();
                this.methodProfiler.exit();
                this.methodProfiler.exit();
            }
        }

        this.methodProfiler.exitEnter("connection");
        this.getServerConnection().c();
        this.methodProfiler.exitEnter("players");
        this.playerList.tick();
        this.methodProfiler.exitEnter("tickables");

        for (int j = 0; j < this.k.size(); ++j) {
            ((ITickable) this.k.get(j)).tick();
        }

        this.methodProfiler.exit();
    }

    public boolean getAllowNether() {
        return true;
    }

    public void a(ITickable itickable) {
        this.k.add(itickable);
    }

    public static void main(String[] astring) {
        DispenserRegistry.c();

        try {
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

            YggdrasilAuthenticationService yggdrasilauthenticationservice = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
            MinecraftSessionService minecraftsessionservice = yggdrasilauthenticationservice.createMinecraftSessionService();
            GameProfileRepository gameprofilerepository = yggdrasilauthenticationservice.createProfileRepository();
            UserCache usercache = new UserCache(gameprofilerepository, new File(s1, MinecraftServer.a.getName()));
            final DedicatedServer dedicatedserver = new DedicatedServer(new File(s1), DataConverterRegistry.a(), yggdrasilauthenticationservice, minecraftsessionservice, gameprofilerepository, usercache);

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
                dedicatedserver.aW();
            }

            if (flag3) {
                dedicatedserver.setForceUpgrade(true);
            }

            dedicatedserver.v();
            Thread thread = new Thread("Server Shutdown Thread") {
                public void run() {
                    dedicatedserver.stop();
                }
            };

            thread.setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(MinecraftServer.LOGGER));
            Runtime.getRuntime().addShutdownHook(thread);
        } catch (Exception exception) {
            MinecraftServer.LOGGER.fatal("Failed to start the minecraft server", exception);
        }

    }

    protected void setForceUpgrade(boolean flag) {
        this.forceUpgrade = flag;
    }

    public void v() {
        this.serverThread = new Thread(this, "Server thread");
        this.serverThread.setUncaughtExceptionHandler((thread, throwable) -> {
            MinecraftServer.LOGGER.error(throwable);
        });
        this.serverThread.start();
    }

    public File c(String s) {
        return new File(this.s(), s);
    }

    public void info(String s) {
        MinecraftServer.LOGGER.info(s);
    }

    public void warning(String s) {
        MinecraftServer.LOGGER.warn(s);
    }

    public WorldServer getWorldServer(DimensionManager dimensionmanager) {
        return (WorldServer) this.worldServer.get(dimensionmanager);
    }

    public Iterable<WorldServer> getWorlds() {
        return this.worldServer.values();
    }

    public String getVersion() {
        return "1.13.2";
    }

    public int getPlayerCount() {
        return this.playerList.getPlayerCount();
    }

    public int getMaxPlayers() {
        return this.playerList.getMaxPlayers();
    }

    public String[] getPlayers() {
        return this.playerList.f();
    }

    public boolean isDebugging() {
        return false;
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
        return "vanilla";
    }

    public CrashReport b(CrashReport crashreport) {
        crashreport.g().a("Profiler Position", () -> {
            return this.methodProfiler.a() ? this.methodProfiler.f() : "N/A (disabled)";
        });
        if (this.playerList != null) {
            crashreport.g().a("Player Count", () -> {
                return this.playerList.getPlayerCount() + " / " + this.playerList.getMaxPlayers() + "; " + this.playerList.v();
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

    public boolean D() {
        return this.universe != null;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        MinecraftServer.LOGGER.info(ichatbasecomponent.getString());
    }

    public KeyPair E() {
        return this.H;
    }

    public int getPort() {
        return this.q;
    }

    public void setPort(int i) {
        this.q = i;
    }

    public String G() {
        return this.I;
    }

    public void h(String s) {
        this.I = s;
    }

    public boolean H() {
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
        Iterator iterator = this.getWorlds().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver.getWorldData().isHardcore()) {
                worldserver.getWorldData().setDifficulty(EnumDifficulty.HARD);
                worldserver.setSpawnFlags(true, true);
            } else if (this.H()) {
                worldserver.getWorldData().setDifficulty(enumdifficulty);
                worldserver.setSpawnFlags(worldserver.getDifficulty() != EnumDifficulty.PEACEFUL, true);
            } else {
                worldserver.getWorldData().setDifficulty(enumdifficulty);
                worldserver.setSpawnFlags(this.getSpawnMonsters(), this.spawnAnimals);
            }
        }

    }

    public boolean getSpawnMonsters() {
        return true;
    }

    public boolean L() {
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
        mojangstatisticsgenerator.a("whitelist_enabled", false);
        mojangstatisticsgenerator.a("whitelist_count", 0);
        if (this.playerList != null) {
            mojangstatisticsgenerator.a("players_current", this.getPlayerCount());
            mojangstatisticsgenerator.a("players_max", this.getMaxPlayers());
            mojangstatisticsgenerator.a("players_seen", this.playerList.getSeenPlayers().length);
        }

        mojangstatisticsgenerator.a("uses_auth", this.onlineMode);
        mojangstatisticsgenerator.a("gui_state", this.ag() ? "enabled" : "disabled");
        mojangstatisticsgenerator.a("run_time", (SystemUtils.getMonotonicMillis() - mojangstatisticsgenerator.g()) / 60L * 1000L);
        mojangstatisticsgenerator.a("avg_tick_ms", (int) (MathHelper.a(this.d) * 1.0E-6D));
        int i = 0;
        Iterator iterator = this.getWorlds().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            if (worldserver != null) {
                WorldData worlddata = worldserver.getWorldData();

                mojangstatisticsgenerator.a("world[" + i + "][dimension]", worldserver.worldProvider.getDimensionManager());
                mojangstatisticsgenerator.a("world[" + i + "][mode]", worlddata.getGameType());
                mojangstatisticsgenerator.a("world[" + i + "][difficulty]", worldserver.getDifficulty());
                mojangstatisticsgenerator.a("world[" + i + "][hardcore]", worlddata.isHardcore());
                mojangstatisticsgenerator.a("world[" + i + "][generator_name]", worlddata.getType().name());
                mojangstatisticsgenerator.a("world[" + i + "][generator_version]", worlddata.getType().getVersion());
                mojangstatisticsgenerator.a("world[" + i + "][height]", this.F);
                mojangstatisticsgenerator.a("world[" + i + "][chunks_loaded]", worldserver.getChunkProvider().g());
                ++i;
            }
        }

        mojangstatisticsgenerator.a("worlds", i);
    }

    public boolean getSnooperEnabled() {
        return true;
    }

    public abstract boolean Q();

    public boolean getOnlineMode() {
        return this.onlineMode;
    }

    public void setOnlineMode(boolean flag) {
        this.onlineMode = flag;
    }

    public boolean S() {
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

    public abstract boolean V();

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

    public void b(int i) {
        this.F = i;
    }

    public boolean isStopped() {
        return this.isStopped;
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    public void a(PlayerList playerlist) {
        this.playerList = playerlist;
    }

    public abstract boolean ad();

    public void setGamemode(EnumGamemode enumgamemode) {
        Iterator iterator = this.getWorlds().iterator();

        while (iterator.hasNext()) {
            WorldServer worldserver = (WorldServer) iterator.next();

            worldserver.getWorldData().setGameType(enumgamemode);
        }

    }

    public ServerConnection getServerConnection() {
        return this.serverConnection;
    }

    public boolean ag() {
        return false;
    }

    public abstract boolean a(EnumGamemode enumgamemode, boolean flag, int i);

    public int ah() {
        return this.ticks;
    }

    public void ai() {
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

    public MinecraftSessionService ap() {
        return this.V;
    }

    public GameProfileRepository getGameProfileRepository() {
        return this.W;
    }

    public UserCache getUserCache() {
        return this.X;
    }

    public ServerPing getServerPing() {
        return this.m;
    }

    public void at() {
        this.Y = 0L;
    }

    public int au() {
        return 29999984;
    }

    public <V> ListenableFuture<V> a(Callable<V> callable) {
        Validate.notNull(callable);
        if (!this.isMainThread() && !this.isStopped()) {
            ListenableFutureTask<V> listenablefuturetask = ListenableFutureTask.create(callable);

            this.f.add(listenablefuturetask);
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

    public int aw() {
        return 256;
    }

    public long ax() {
        return this.nextTick;
    }

    public Thread ay() {
        return this.serverThread;
    }

    public DataFixer az() {
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
            this.a(this.getWorldServer(DimensionManager.OVERWORLD).getWorldData());
            this.getPlayerList().reload();
        }
    }

    private void a(WorldData worlddata) {
        List<ResourcePackLoader> list = Lists.newArrayList(this.resourcePackRepository.d());
        Iterator iterator = this.resourcePackRepository.b().iterator();

        while (iterator.hasNext()) {
            ResourcePackLoader resourcepackloader = (ResourcePackLoader) iterator.next();

            if (!worlddata.N().contains(resourcepackloader.e()) && !list.contains(resourcepackloader)) {
                MinecraftServer.LOGGER.info("Found new data pack {}, loading it automatically", resourcepackloader.e());
                resourcepackloader.h().a(list, resourcepackloader, (resourcepackloader1) -> {
                    return resourcepackloader1;
                }, false);
            }
        }

        this.resourcePackRepository.a((Collection) list);
        List<IResourcePack> list1 = Lists.newArrayList();

        this.resourcePackRepository.d().forEach((resourcepackloader1) -> {
            list1.add(resourcepackloader1.d());
        });
        this.ac.a((List) list1);
        worlddata.O().clear();
        worlddata.N().clear();
        this.resourcePackRepository.d().forEach((resourcepackloader1) -> {
            worlddata.O().add(resourcepackloader1.e());
        });
        this.resourcePackRepository.b().forEach((resourcepackloader1) -> {
            if (!this.resourcePackRepository.d().contains(resourcepackloader1)) {
                worlddata.N().add(resourcepackloader1.e());
            }

        });
    }

    public void a(CommandListenerWrapper commandlistenerwrapper) {
        if (this.aQ()) {
            PlayerList playerlist = commandlistenerwrapper.getServer().getPlayerList();
            WhiteList whitelist = playerlist.getWhitelist();

            if (whitelist.isEnabled()) {
                List<EntityPlayer> list = Lists.newArrayList(playerlist.v());
                Iterator iterator = list.iterator();

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
        return new CommandListenerWrapper(this, this.getWorldServer(DimensionManager.OVERWORLD) == null ? Vec3D.a : new Vec3D(this.getWorldServer(DimensionManager.OVERWORLD).getSpawn()), Vec2F.a, this.getWorldServer(DimensionManager.OVERWORLD), 4, "Server", new ChatComponentText("Server"), this, (Entity) null);
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

    public LootTableRegistry getLootTableRegistry() {
        return this.ak;
    }

    public GameRules getGameRules() {
        return this.getWorldServer(DimensionManager.OVERWORLD).getGameRules();
    }

    public BossBattleCustomData getBossBattleCustomData() {
        return this.aj;
    }

    public boolean aQ() {
        return this.an;
    }

    public void l(boolean flag) {
        this.an = flag;
    }

    public int a(GameProfile gameprofile) {
        if (this.getPlayerList().isOp(gameprofile)) {
            OpListEntry oplistentry = (OpListEntry) this.getPlayerList().getOPs().get(gameprofile);

            return oplistentry != null ? oplistentry.a() : (this.H() ? (this.G().equals(gameprofile.getName()) ? 4 : (this.getPlayerList().x() ? 4 : 0)) : this.j());
        } else {
            return 0;
        }
    }
}
