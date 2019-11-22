package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
// CraftBukkit end

public class WorldData {

    private String b;
    private int c;
    private boolean d;
    public static final EnumDifficulty a = EnumDifficulty.NORMAL;
    private long e;
    private WorldType f;
    private NBTTagCompound g;
    @Nullable
    private String h;
    private int i;
    private int j;
    private int k;
    private long l;
    private long m;
    private long n;
    private long o;
    @Nullable
    private final DataFixer p;
    private final int q;
    private boolean r;
    private NBTTagCompound s;
    private String levelName;
    private int u;
    private int clearWeatherTime;
    private boolean raining;
    private int rainTime;
    private boolean thundering;
    private int thunderTime;
    private EnumGamemode A;
    private boolean B;
    private boolean C;
    private boolean D;
    private boolean E;
    private EnumDifficulty F;
    private boolean G;
    private double H;
    private double I;
    private double J;
    private long K;
    private double L;
    private double M;
    private double N;
    private int O;
    private int P;
    private final Set<String> Q;
    private final Set<String> R;
    private final Map<DimensionManager, NBTTagCompound> S;
    private NBTTagCompound T;
    private int U;
    private int V;
    private UUID W;
    private final GameRules X;
    private final CustomFunctionCallbackTimerQueue<MinecraftServer> Y;
    public WorldServer world; // CraftBukkit

    protected WorldData() {
        this.f = WorldType.NORMAL;
        this.g = new NBTTagCompound();
        this.J = 6.0E7D;
        this.M = 5.0D;
        this.N = 0.2D;
        this.O = 5;
        this.P = 15;
        this.Q = Sets.newHashSet();
        this.R = Sets.newLinkedHashSet();
        this.S = Maps.newIdentityHashMap();
        this.X = new GameRules();
        this.Y = new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.a);
        this.p = null;
        this.q = SharedConstants.a().getWorldVersion();
        this.b(new NBTTagCompound());
    }

    public WorldData(NBTTagCompound nbttagcompound, DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound1) {
        this.f = WorldType.NORMAL;
        this.g = new NBTTagCompound();
        this.J = 6.0E7D;
        this.M = 5.0D;
        this.N = 0.2D;
        this.O = 5;
        this.P = 15;
        this.Q = Sets.newHashSet();
        this.R = Sets.newLinkedHashSet();
        this.S = Maps.newIdentityHashMap();
        this.X = new GameRules();
        this.Y = new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.a);
        this.p = datafixer;
        NBTTagCompound nbttagcompound2;

        if (nbttagcompound.hasKeyOfType("Version", 10)) {
            nbttagcompound2 = nbttagcompound.getCompound("Version");
            this.b = nbttagcompound2.getString("Name");
            this.c = nbttagcompound2.getInt("Id");
            this.d = nbttagcompound2.getBoolean("Snapshot");
        }

        this.e = com.destroystokyo.paper.PaperConfig.seedOverride.getOrDefault(nbttagcompound.getString("LevelName"), nbttagcompound.getLong("RandomSeed")); // Paper
        if (nbttagcompound.hasKeyOfType("generatorName", 8)) {
            String s = nbttagcompound.getString("generatorName");

            this.f = WorldType.getType(s);
            if (this.f == null) {
                this.f = WorldType.NORMAL;
            } else if (this.f == WorldType.CUSTOMIZED) {
                this.h = nbttagcompound.getString("generatorOptions");
            } else if (this.f.h()) {
                int j = 0;

                if (nbttagcompound.hasKeyOfType("generatorVersion", 99)) {
                    j = nbttagcompound.getInt("generatorVersion");
                }

                this.f = this.f.a(j);
            }

            this.b(nbttagcompound.getCompound("generatorOptions"));
        }

        this.A = EnumGamemode.getById(nbttagcompound.getInt("GameType"));
        if (nbttagcompound.hasKeyOfType("legacy_custom_options", 8)) {
            this.h = nbttagcompound.getString("legacy_custom_options");
        }

        if (nbttagcompound.hasKeyOfType("MapFeatures", 99)) {
            this.B = nbttagcompound.getBoolean("MapFeatures");
        } else {
            this.B = true;
        }

        this.i = nbttagcompound.getInt("SpawnX");
        this.j = nbttagcompound.getInt("SpawnY");
        this.k = nbttagcompound.getInt("SpawnZ");
        this.l = nbttagcompound.getLong("Time");
        if (nbttagcompound.hasKeyOfType("DayTime", 99)) {
            this.m = nbttagcompound.getLong("DayTime");
        } else {
            this.m = this.l;
        }

        this.n = nbttagcompound.getLong("LastPlayed");
        this.o = nbttagcompound.getLong("SizeOnDisk");
        this.levelName = nbttagcompound.getString("LevelName");
        this.u = nbttagcompound.getInt("version");
        this.clearWeatherTime = nbttagcompound.getInt("clearWeatherTime");
        this.rainTime = nbttagcompound.getInt("rainTime");
        this.raining = nbttagcompound.getBoolean("raining");
        this.thunderTime = nbttagcompound.getInt("thunderTime");
        this.thundering = nbttagcompound.getBoolean("thundering");
        this.C = nbttagcompound.getBoolean("hardcore");
        if (nbttagcompound.hasKeyOfType("initialized", 99)) {
            this.E = nbttagcompound.getBoolean("initialized");
        } else {
            this.E = true;
        }

        if (nbttagcompound.hasKeyOfType("allowCommands", 99)) {
            this.D = nbttagcompound.getBoolean("allowCommands");
        } else {
            this.D = this.A == EnumGamemode.CREATIVE;
        }

        this.q = i;
        if (nbttagcompound1 != null) {
            this.s = nbttagcompound1;
        }

        if (nbttagcompound.hasKeyOfType("GameRules", 10)) {
            this.X.a(nbttagcompound.getCompound("GameRules"));
        }

        if (nbttagcompound.hasKeyOfType("Difficulty", 99)) {
            this.F = EnumDifficulty.getById(nbttagcompound.getByte("Difficulty"));
        }

        if (nbttagcompound.hasKeyOfType("DifficultyLocked", 1)) {
            this.G = nbttagcompound.getBoolean("DifficultyLocked");
        }

        if (nbttagcompound.hasKeyOfType("BorderCenterX", 99)) {
            this.H = nbttagcompound.getDouble("BorderCenterX");
        }

        if (nbttagcompound.hasKeyOfType("BorderCenterZ", 99)) {
            this.I = nbttagcompound.getDouble("BorderCenterZ");
        }

        if (nbttagcompound.hasKeyOfType("BorderSize", 99)) {
            this.J = nbttagcompound.getDouble("BorderSize");
        }

        if (nbttagcompound.hasKeyOfType("BorderSizeLerpTime", 99)) {
            this.K = nbttagcompound.getLong("BorderSizeLerpTime");
        }

        if (nbttagcompound.hasKeyOfType("BorderSizeLerpTarget", 99)) {
            this.L = nbttagcompound.getDouble("BorderSizeLerpTarget");
        }

        if (nbttagcompound.hasKeyOfType("BorderSafeZone", 99)) {
            this.M = nbttagcompound.getDouble("BorderSafeZone");
        }

        if (nbttagcompound.hasKeyOfType("BorderDamagePerBlock", 99)) {
            this.N = nbttagcompound.getDouble("BorderDamagePerBlock");
        }

        if (nbttagcompound.hasKeyOfType("BorderWarningBlocks", 99)) {
            this.O = nbttagcompound.getInt("BorderWarningBlocks");
        }

        if (nbttagcompound.hasKeyOfType("BorderWarningTime", 99)) {
            this.P = nbttagcompound.getInt("BorderWarningTime");
        }

        if (nbttagcompound.hasKeyOfType("DimensionData", 10)) {
            nbttagcompound2 = nbttagcompound.getCompound("DimensionData");
            Iterator iterator = nbttagcompound2.getKeys().iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                this.S.put(DimensionManager.a(Integer.parseInt(s1)), nbttagcompound2.getCompound(s1));
            }
        }

        if (nbttagcompound.hasKeyOfType("DataPacks", 10)) {
            nbttagcompound2 = nbttagcompound.getCompound("DataPacks");
            NBTTagList nbttaglist = nbttagcompound2.getList("Disabled", 8);

            for (int k = 0; k < nbttaglist.size(); ++k) {
                this.Q.add(nbttaglist.getString(k));
            }

            NBTTagList nbttaglist1 = nbttagcompound2.getList("Enabled", 8);

            for (int l = 0; l < nbttaglist1.size(); ++l) {
                this.R.add(nbttaglist1.getString(l));
            }
        }

        if (nbttagcompound.hasKeyOfType("CustomBossEvents", 10)) {
            this.T = nbttagcompound.getCompound("CustomBossEvents");
        }

        if (nbttagcompound.hasKeyOfType("ScheduledEvents", 9)) {
            this.Y.a(nbttagcompound.getList("ScheduledEvents", 10));
        }

        if (nbttagcompound.hasKeyOfType("WanderingTraderSpawnDelay", 99)) {
            this.U = nbttagcompound.getInt("WanderingTraderSpawnDelay");
        }

        if (nbttagcompound.hasKeyOfType("WanderingTraderSpawnChance", 99)) {
            this.V = nbttagcompound.getInt("WanderingTraderSpawnChance");
        }

        if (nbttagcompound.hasKeyOfType("WanderingTraderId", 8)) {
            this.W = UUID.fromString(nbttagcompound.getString("WanderingTraderId"));
        }

    }

    public WorldData(WorldSettings worldsettings, String s) {
        this.f = WorldType.NORMAL;
        this.g = new NBTTagCompound();
        this.J = 6.0E7D;
        this.M = 5.0D;
        this.N = 0.2D;
        this.O = 5;
        this.P = 15;
        this.Q = Sets.newHashSet();
        this.R = Sets.newLinkedHashSet();
        this.S = Maps.newIdentityHashMap();
        this.X = new GameRules();
        this.Y = new CustomFunctionCallbackTimerQueue<>(CustomFunctionCallbackTimers.a);
        this.p = null;
        this.q = SharedConstants.a().getWorldVersion();
        this.a(worldsettings);
        this.levelName = s;
        this.F = WorldData.a;
        this.E = false;
    }

    public void a(WorldSettings worldsettings) {
        this.e = worldsettings.d();
        this.A = worldsettings.e();
        this.B = worldsettings.g();
        this.C = worldsettings.f();
        this.f = worldsettings.h();
        this.b((NBTTagCompound) Dynamic.convert(JsonOps.INSTANCE, DynamicOpsNBT.a, worldsettings.j()));
        this.D = worldsettings.i();
    }

    public NBTTagCompound a(@Nullable NBTTagCompound nbttagcompound) {
        this.T();
        if (nbttagcompound == null) {
            nbttagcompound = this.s;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.a(nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void a(NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.setString("Name", SharedConstants.a().getName());
        nbttagcompound2.setInt("Id", SharedConstants.a().getWorldVersion());
        nbttagcompound2.setBoolean("Snapshot", !SharedConstants.a().isStable());
        nbttagcompound.set("Version", nbttagcompound2);
        nbttagcompound.setInt("DataVersion", SharedConstants.a().getWorldVersion());
        nbttagcompound.setLong("RandomSeed", this.e);
        nbttagcompound.setString("generatorName", this.f.b());
        nbttagcompound.setInt("generatorVersion", this.f.getVersion());
        if (!this.g.isEmpty()) {
            nbttagcompound.set("generatorOptions", this.g);
        }

        if (this.h != null) {
            nbttagcompound.setString("legacy_custom_options", this.h);
        }

        nbttagcompound.setInt("GameType", this.A.getId());
        nbttagcompound.setBoolean("MapFeatures", this.B);
        nbttagcompound.setInt("SpawnX", this.i);
        nbttagcompound.setInt("SpawnY", this.j);
        nbttagcompound.setInt("SpawnZ", this.k);
        nbttagcompound.setLong("Time", this.l);
        nbttagcompound.setLong("DayTime", this.m);
        nbttagcompound.setLong("SizeOnDisk", this.o);
        nbttagcompound.setLong("LastPlayed", SystemUtils.getTimeMillis());
        nbttagcompound.setString("LevelName", this.levelName);
        nbttagcompound.setInt("version", this.u);
        nbttagcompound.setInt("clearWeatherTime", this.clearWeatherTime);
        nbttagcompound.setInt("rainTime", this.rainTime);
        nbttagcompound.setBoolean("raining", this.raining);
        nbttagcompound.setInt("thunderTime", this.thunderTime);
        nbttagcompound.setBoolean("thundering", this.thundering);
        nbttagcompound.setBoolean("hardcore", this.C);
        nbttagcompound.setBoolean("allowCommands", this.D);
        nbttagcompound.setBoolean("initialized", this.E);
        nbttagcompound.setDouble("BorderCenterX", this.H);
        nbttagcompound.setDouble("BorderCenterZ", this.I);
        nbttagcompound.setDouble("BorderSize", this.J);
        nbttagcompound.setLong("BorderSizeLerpTime", this.K);
        nbttagcompound.setDouble("BorderSafeZone", this.M);
        nbttagcompound.setDouble("BorderDamagePerBlock", this.N);
        nbttagcompound.setDouble("BorderSizeLerpTarget", this.L);
        nbttagcompound.setDouble("BorderWarningBlocks", (double) this.O);
        nbttagcompound.setDouble("BorderWarningTime", (double) this.P);
        if (this.F != null) {
            nbttagcompound.setByte("Difficulty", (byte) this.F.a());
        }

        nbttagcompound.setBoolean("DifficultyLocked", this.G);
        nbttagcompound.set("GameRules", this.X.a());
        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        Iterator iterator = this.S.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<DimensionManager, NBTTagCompound> entry = (Entry) iterator.next();

            nbttagcompound3.set(String.valueOf(((DimensionManager) entry.getKey()).getDimensionID()), (NBTBase) entry.getValue());
        }

        nbttagcompound.set("DimensionData", nbttagcompound3);
        if (nbttagcompound1 != null) {
            nbttagcompound.set("Player", nbttagcompound1);
        }

        NBTTagCompound nbttagcompound4 = new NBTTagCompound();
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator1 = this.R.iterator();

        while (iterator1.hasNext()) {
            String s = (String) iterator1.next();

            nbttaglist.add(new NBTTagString(s));
        }

        nbttagcompound4.set("Enabled", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator2 = this.Q.iterator();

        while (iterator2.hasNext()) {
            String s1 = (String) iterator2.next();

            nbttaglist1.add(new NBTTagString(s1));
        }

        nbttagcompound4.set("Disabled", nbttaglist1);
        nbttagcompound.set("DataPacks", nbttagcompound4);
        if (this.T != null) {
            nbttagcompound.set("CustomBossEvents", this.T);
        }

        nbttagcompound.set("ScheduledEvents", this.Y.b());
        nbttagcompound.setInt("WanderingTraderSpawnDelay", this.U);
        nbttagcompound.setInt("WanderingTraderSpawnChance", this.V);
        if (this.W != null) {
            nbttagcompound.setString("WanderingTraderId", this.W.toString());
        }

        nbttagcompound.setString("Bukkit.Version", Bukkit.getName() + "/" + Bukkit.getVersion() + "/" + Bukkit.getBukkitVersion()); // CraftBukkit
    }

    public long getSeed() {
        return this.e;
    }

    public int b() {
        return this.i;
    }

    public int c() {
        return this.j;
    }

    public int d() {
        return this.k;
    }

    public long getTime() {
        return this.l;
    }

    public long getDayTime() {
        return this.m;
    }

    private void T() {
        if (!this.r && this.s != null) {
            if (this.q < SharedConstants.a().getWorldVersion()) {
                if (this.p == null) {
                    throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
                }

                this.s = GameProfileSerializer.a(this.p, DataFixTypes.PLAYER, this.s, this.q);
            }

            this.r = true;
        }
    }

    public NBTTagCompound h() {
        this.T();
        return this.s;
    }

    public void setTime(long i) {
        this.l = i;
    }

    public void setDayTime(long i) {
        this.m = i;
    }

    public void setSpawn(BlockPosition blockposition) {
        this.i = blockposition.getX();
        this.j = blockposition.getY();
        this.k = blockposition.getZ();
    }

    public String getName() {
        return this.levelName;
    }

    public void setName(String s) {
        this.levelName = s;
    }

    public int j() {
        return this.u;
    }

    public void d(int i) {
        this.u = i;
    }

    public int z() {
        return this.clearWeatherTime;
    }

    public void g(int i) {
        this.clearWeatherTime = i;
    }

    public boolean isThundering() {
        return this.thundering;
    }

    public void setThundering(boolean flag) {
        // CraftBukkit start
        if (this.thundering == flag) {
            return;
        }

        org.bukkit.World world = Bukkit.getWorld(getName());
        if (world != null) {
            ThunderChangeEvent thunder = new ThunderChangeEvent(world, flag);
            Bukkit.getServer().getPluginManager().callEvent(thunder);
            if (thunder.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end
        this.thundering = flag;
    }

    public int getThunderDuration() {
        return this.thunderTime;
    }

    public void setThunderDuration(int i) {
        this.thunderTime = i;
    }

    public boolean hasStorm() {
        return this.raining;
    }

    public void setStorm(boolean flag) {
        // CraftBukkit start
        if (this.raining == flag) {
            return;
        }

        org.bukkit.World world = Bukkit.getWorld(getName());
        if (world != null) {
            WeatherChangeEvent weather = new WeatherChangeEvent(world, flag);
            Bukkit.getServer().getPluginManager().callEvent(weather);
            if (weather.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end
        this.raining = flag;
    }

    public int getWeatherDuration() {
        return this.rainTime;
    }

    public void setWeatherDuration(int i) {
        this.rainTime = i;
    }

    public EnumGamemode getGameType() {
        return this.A;
    }

    public boolean shouldGenerateMapFeatures() {
        return this.B;
    }

    public void f(boolean flag) {
        this.B = flag;
    }

    public void setGameType(EnumGamemode enumgamemode) {
        this.A = enumgamemode;
    }

    public boolean isHardcore() {
        return this.C;
    }

    public void g(boolean flag) {
        this.C = flag;
    }

    public WorldType getType() {
        return this.f;
    }

    public void a(WorldType worldtype) {
        this.f = worldtype;
    }

    public NBTTagCompound getGeneratorOptions() {
        return this.g;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.g = nbttagcompound;
    }

    public boolean t() {
        return this.D;
    }

    public void c(boolean flag) {
        this.D = flag;
    }

    public boolean u() {
        return this.E;
    }

    public void d(boolean flag) {
        this.E = flag;
    }

    public GameRules v() {
        return this.X;
    }

    public double B() {
        return this.H;
    }

    public double C() {
        return this.I;
    }

    public double D() {
        return this.J;
    }

    public void a(double d0) {
        this.J = d0;
    }

    public long E() {
        return this.K;
    }

    public void c(long i) {
        this.K = i;
    }

    public double F() {
        return this.L;
    }

    public void b(double d0) {
        this.L = d0;
    }

    public void c(double d0) {
        this.I = d0;
    }

    public void d(double d0) {
        this.H = d0;
    }

    public double G() {
        return this.M;
    }

    public void e(double d0) {
        this.M = d0;
    }

    public double H() {
        return this.N;
    }

    public void f(double d0) {
        this.N = d0;
    }

    public int I() {
        return this.O;
    }

    public int J() {
        return this.P;
    }

    public void h(int i) {
        this.O = i;
    }

    public void i(int i) {
        this.P = i;
    }

    public EnumDifficulty getDifficulty() {
        return this.F;
    }

    public void setDifficulty(EnumDifficulty enumdifficulty) {
        this.F = enumdifficulty;
        // CraftBukkit start
        PacketPlayOutServerDifficulty packet = new PacketPlayOutServerDifficulty(this.getDifficulty(), this.isDifficultyLocked());
        for (EntityPlayer player : (java.util.List<EntityPlayer>) (java.util.List) world.getPlayers()) {
            player.playerConnection.sendPacket(packet);
        }
        // CraftBukkit end
    }

    public boolean isDifficultyLocked() {
        return this.G;
    }

    public void e(boolean flag) {
        this.G = flag;
    }

    public CustomFunctionCallbackTimerQueue<MinecraftServer> y() {
        return this.Y;
    }

    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Level name", () -> {
            return this.levelName;
        });
        crashreportsystemdetails.a("Level seed", () -> {
            return String.valueOf(this.e);
        });
        crashreportsystemdetails.a("Level generator", () -> {
            return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.f.i(), this.f.name(), this.f.getVersion(), this.B);
        });
        crashreportsystemdetails.a("Level generator options", () -> {
            return this.g.toString();
        });
        crashreportsystemdetails.a("Level spawn location", () -> {
            return CrashReportSystemDetails.a(this.i, this.j, this.k);
        });
        crashreportsystemdetails.a("Level time", () -> {
            return String.format("%d game time, %d day time", this.l, this.m);
        });
        crashreportsystemdetails.a("Level storage version", () -> {
            String s = "Unknown?";

            try {
                switch (this.u) {
                    case 19132:
                        s = "McRegion";
                        break;
                    case 19133:
                        s = "Anvil";
                }
            } catch (Throwable throwable) {
                ;
            }

            return String.format("0x%05X - %s", this.u, s);
        });
        crashreportsystemdetails.a("Level weather", () -> {
            return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.rainTime, this.raining, this.thunderTime, this.thundering);
        });
        crashreportsystemdetails.a("Level game mode", () -> {
            return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.A.b(), this.A.getId(), this.C, this.D);
        });
    }

    public NBTTagCompound a(DimensionManager dimensionmanager) {
        NBTTagCompound nbttagcompound = (NBTTagCompound) this.S.get(dimensionmanager);

        return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
    }

    public void a(DimensionManager dimensionmanager, NBTTagCompound nbttagcompound) {
        this.S.put(dimensionmanager, nbttagcompound);
    }

    public Set<String> N() {
        return this.Q;
    }

    public Set<String> O() {
        return this.R;
    }

    @Nullable
    public NBTTagCompound getCustomBossEvents() {
        return this.T;
    }

    public void c(@Nullable NBTTagCompound nbttagcompound) {
        this.T = nbttagcompound;
    }

    public int Q() {
        return this.U;
    }

    public void j(int i) {
        this.U = i;
    }

    public int R() {
        return this.V;
    }

    public void k(int i) {
        this.V = i;
    }

    public void a(UUID uuid) {
        this.W = uuid;
    }

    // CraftBukkit start - Check if the name stored in NBT is the correct one
    public void checkName( String name ) {
        if ( !this.levelName.equals( name ) ) {
            this.levelName = name;
        }
    }
    // CraftBukkit end
}
