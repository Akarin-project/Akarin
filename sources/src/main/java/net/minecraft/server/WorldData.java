package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.event.weather.ThunderChangeEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
// CraftBukkit end

/**
 * <b>Akarin Changes Note</b><br>
 * <br>
 * 1) Add volatile to fields<br>
 * @author cakoyo
 */
public class WorldData {

    private String b;
    private int c;
    private boolean d;
    public static final EnumDifficulty a = EnumDifficulty.NORMAL;
    private long e;
    private WorldType f;
    private String g;
    private int h;
    private int i;
    private int j;
    private volatile long k; // Akarin - volatile - PAIL: time
    private volatile long l; // Akarin - volatile - PAIL: dayTime
    private long m;
    private long n;
    private NBTTagCompound o;
    private int p;
    private String levelName;
    private int r;
    private int s;
    private boolean t;
    private int u;
    private boolean v;
    private int w;
    private EnumGamemode x;
    private boolean y;
    private boolean z;
    private boolean A;
    private boolean B;
    private EnumDifficulty C;
    private boolean D;
    private double E;
    private double F;
    private double G;
    private long H;
    private double I;
    private double J;
    private double K;
    private int L;
    private int M;
    private final Map<DimensionManager, NBTTagCompound> N;
    private GameRules O;
    public WorldServer world; // CraftBukkit

    protected WorldData() {
        this.f = WorldType.NORMAL;
        this.g = "";
        this.G = 6.0E7D;
        this.J = 5.0D;
        this.K = 0.2D;
        this.L = 5;
        this.M = 15;
        this.N = Maps.newEnumMap(DimensionManager.class);
        this.O = new GameRules();
    }

    public static void a(DataConverterManager dataconvertermanager) {
        dataconvertermanager.a(DataConverterTypes.LEVEL, new DataInspector() {
            @Override
            public NBTTagCompound a(DataConverter dataconverter, NBTTagCompound nbttagcompound, int i) {
                if (nbttagcompound.hasKeyOfType("Player", 10)) {
                    nbttagcompound.set("Player", dataconverter.a(DataConverterTypes.PLAYER, nbttagcompound.getCompound("Player"), i));
                }

                return nbttagcompound;
            }
        });
    }

    public WorldData(NBTTagCompound nbttagcompound) {
        this.f = WorldType.NORMAL;
        this.g = "";
        this.G = 6.0E7D;
        this.J = 5.0D;
        this.K = 0.2D;
        this.L = 5;
        this.M = 15;
        this.N = Maps.newEnumMap(DimensionManager.class);
        this.O = new GameRules();
        NBTTagCompound nbttagcompound1;

        if (nbttagcompound.hasKeyOfType("Version", 10)) {
            nbttagcompound1 = nbttagcompound.getCompound("Version");
            this.b = nbttagcompound1.getString("Name");
            this.c = nbttagcompound1.getInt("Id");
            this.d = nbttagcompound1.getBoolean("Snapshot");
        }

        this.e = nbttagcompound.getLong("RandomSeed");
        if (nbttagcompound.hasKeyOfType("generatorName", 8)) {
            String s = nbttagcompound.getString("generatorName");

            this.f = WorldType.getType(s);
            if (this.f == null) {
                this.f = WorldType.NORMAL;
            } else if (this.f.f()) {
                int i = 0;

                if (nbttagcompound.hasKeyOfType("generatorVersion", 99)) {
                    i = nbttagcompound.getInt("generatorVersion");
                }

                this.f = this.f.a(i);
            }

            if (nbttagcompound.hasKeyOfType("generatorOptions", 8)) {
                this.g = nbttagcompound.getString("generatorOptions");
            }
        }

        this.x = EnumGamemode.getById(nbttagcompound.getInt("GameType"));
        if (nbttagcompound.hasKeyOfType("MapFeatures", 99)) {
            this.y = nbttagcompound.getBoolean("MapFeatures");
        } else {
            this.y = true;
        }

        this.h = nbttagcompound.getInt("SpawnX");
        this.i = nbttagcompound.getInt("SpawnY");
        this.j = nbttagcompound.getInt("SpawnZ");
        this.k = nbttagcompound.getLong("Time");
        if (nbttagcompound.hasKeyOfType("DayTime", 99)) {
            this.l = nbttagcompound.getLong("DayTime");
        } else {
            this.l = this.k;
        }

        this.m = nbttagcompound.getLong("LastPlayed");
        this.n = nbttagcompound.getLong("SizeOnDisk");
        this.levelName = nbttagcompound.getString("LevelName");
        this.r = nbttagcompound.getInt("version");
        this.s = nbttagcompound.getInt("clearWeatherTime");
        this.u = nbttagcompound.getInt("rainTime");
        this.t = nbttagcompound.getBoolean("raining");
        this.w = nbttagcompound.getInt("thunderTime");
        this.v = nbttagcompound.getBoolean("thundering");
        this.z = nbttagcompound.getBoolean("hardcore");
        if (nbttagcompound.hasKeyOfType("initialized", 99)) {
            this.B = nbttagcompound.getBoolean("initialized");
        } else {
            this.B = true;
        }

        if (nbttagcompound.hasKeyOfType("allowCommands", 99)) {
            this.A = nbttagcompound.getBoolean("allowCommands");
        } else {
            this.A = this.x == EnumGamemode.CREATIVE;
        }

        if (nbttagcompound.hasKeyOfType("Player", 10)) {
            this.o = nbttagcompound.getCompound("Player");
            this.p = this.o.getInt("Dimension");
        }

        if (nbttagcompound.hasKeyOfType("GameRules", 10)) {
            this.O.a(nbttagcompound.getCompound("GameRules"));
        }

        if (nbttagcompound.hasKeyOfType("Difficulty", 99)) {
            this.C = EnumDifficulty.getById(nbttagcompound.getByte("Difficulty"));
        }

        if (nbttagcompound.hasKeyOfType("DifficultyLocked", 1)) {
            this.D = nbttagcompound.getBoolean("DifficultyLocked");
        }

        if (nbttagcompound.hasKeyOfType("BorderCenterX", 99)) {
            this.E = nbttagcompound.getDouble("BorderCenterX");
        }

        if (nbttagcompound.hasKeyOfType("BorderCenterZ", 99)) {
            this.F = nbttagcompound.getDouble("BorderCenterZ");
        }

        if (nbttagcompound.hasKeyOfType("BorderSize", 99)) {
            this.G = nbttagcompound.getDouble("BorderSize");
        }

        if (nbttagcompound.hasKeyOfType("BorderSizeLerpTime", 99)) {
            this.H = nbttagcompound.getLong("BorderSizeLerpTime");
        }

        if (nbttagcompound.hasKeyOfType("BorderSizeLerpTarget", 99)) {
            this.I = nbttagcompound.getDouble("BorderSizeLerpTarget");
        }

        if (nbttagcompound.hasKeyOfType("BorderSafeZone", 99)) {
            this.J = nbttagcompound.getDouble("BorderSafeZone");
        }

        if (nbttagcompound.hasKeyOfType("BorderDamagePerBlock", 99)) {
            this.K = nbttagcompound.getDouble("BorderDamagePerBlock");
        }

        if (nbttagcompound.hasKeyOfType("BorderWarningBlocks", 99)) {
            this.L = nbttagcompound.getInt("BorderWarningBlocks");
        }

        if (nbttagcompound.hasKeyOfType("BorderWarningTime", 99)) {
            this.M = nbttagcompound.getInt("BorderWarningTime");
        }

        if (nbttagcompound.hasKeyOfType("DimensionData", 10)) {
            nbttagcompound1 = nbttagcompound.getCompound("DimensionData");
            Iterator iterator = nbttagcompound1.c().iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                this.N.put(DimensionManager.a(Integer.parseInt(s1)), nbttagcompound1.getCompound(s1));
            }
        }

    }

    public WorldData(WorldSettings worldsettings, String s) {
        this.f = WorldType.NORMAL;
        this.g = "";
        this.G = 6.0E7D;
        this.J = 5.0D;
        this.K = 0.2D;
        this.L = 5;
        this.M = 15;
        this.N = Maps.newEnumMap(DimensionManager.class);
        this.O = new GameRules();
        this.a(worldsettings);
        this.levelName = s;
        this.C = WorldData.a;
        this.B = false;
    }

    public void a(WorldSettings worldsettings) {
        this.e = worldsettings.d();
        this.x = worldsettings.e();
        this.y = worldsettings.g();
        this.z = worldsettings.f();
        this.f = worldsettings.h();
        this.g = worldsettings.j();
        this.A = worldsettings.i();
    }

    public WorldData(WorldData worlddata) {
        this.f = WorldType.NORMAL;
        this.g = "";
        this.G = 6.0E7D;
        this.J = 5.0D;
        this.K = 0.2D;
        this.L = 5;
        this.M = 15;
        this.N = Maps.newEnumMap(DimensionManager.class);
        this.O = new GameRules();
        this.e = worlddata.e;
        this.f = worlddata.f;
        this.g = worlddata.g;
        this.x = worlddata.x;
        this.y = worlddata.y;
        this.h = worlddata.h;
        this.i = worlddata.i;
        this.j = worlddata.j;
        this.k = worlddata.k;
        this.l = worlddata.l;
        this.m = worlddata.m;
        this.n = worlddata.n;
        this.o = worlddata.o;
        this.p = worlddata.p;
        this.levelName = worlddata.levelName;
        this.r = worlddata.r;
        this.u = worlddata.u;
        this.t = worlddata.t;
        this.w = worlddata.w;
        this.v = worlddata.v;
        this.z = worlddata.z;
        this.A = worlddata.A;
        this.B = worlddata.B;
        this.O = worlddata.O;
        this.C = worlddata.C;
        this.D = worlddata.D;
        this.E = worlddata.E;
        this.F = worlddata.F;
        this.G = worlddata.G;
        this.H = worlddata.H;
        this.I = worlddata.I;
        this.J = worlddata.J;
        this.K = worlddata.K;
        this.M = worlddata.M;
        this.L = worlddata.L;
    }

    public NBTTagCompound a(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound == null) {
            nbttagcompound = this.o;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.a(nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void a(NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.setString("Name", "1.12.2");
        nbttagcompound2.setInt("Id", 1343);
        nbttagcompound2.setBoolean("Snapshot", false);
        nbttagcompound.set("Version", nbttagcompound2);
        nbttagcompound.setInt("DataVersion", 1343);
        nbttagcompound.setLong("RandomSeed", this.e);
        nbttagcompound.setString("generatorName", this.f.name());
        nbttagcompound.setInt("generatorVersion", this.f.getVersion());
        nbttagcompound.setString("generatorOptions", this.g);
        nbttagcompound.setInt("GameType", this.x.getId());
        nbttagcompound.setBoolean("MapFeatures", this.y);
        nbttagcompound.setInt("SpawnX", this.h);
        nbttagcompound.setInt("SpawnY", this.i);
        nbttagcompound.setInt("SpawnZ", this.j);
        nbttagcompound.setLong("Time", this.k);
        nbttagcompound.setLong("DayTime", this.l);
        nbttagcompound.setLong("SizeOnDisk", this.n);
        nbttagcompound.setLong("LastPlayed", MinecraftServer.aw());
        nbttagcompound.setString("LevelName", this.levelName);
        nbttagcompound.setInt("version", this.r);
        nbttagcompound.setInt("clearWeatherTime", this.s);
        nbttagcompound.setInt("rainTime", this.u);
        nbttagcompound.setBoolean("raining", this.t);
        nbttagcompound.setInt("thunderTime", this.w);
        nbttagcompound.setBoolean("thundering", this.v);
        nbttagcompound.setBoolean("hardcore", this.z);
        nbttagcompound.setBoolean("allowCommands", this.A);
        nbttagcompound.setBoolean("initialized", this.B);
        nbttagcompound.setDouble("BorderCenterX", this.E);
        nbttagcompound.setDouble("BorderCenterZ", this.F);
        nbttagcompound.setDouble("BorderSize", this.G);
        nbttagcompound.setLong("BorderSizeLerpTime", this.H);
        nbttagcompound.setDouble("BorderSafeZone", this.J);
        nbttagcompound.setDouble("BorderDamagePerBlock", this.K);
        nbttagcompound.setDouble("BorderSizeLerpTarget", this.I);
        nbttagcompound.setDouble("BorderWarningBlocks", this.L);
        nbttagcompound.setDouble("BorderWarningTime", this.M);
        if (this.C != null) {
            nbttagcompound.setByte("Difficulty", (byte) this.C.a());
        }

        nbttagcompound.setBoolean("DifficultyLocked", this.D);
        nbttagcompound.set("GameRules", this.O.a());
        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        Iterator iterator = this.N.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();

            nbttagcompound3.set(String.valueOf(((DimensionManager) entry.getKey()).getDimensionID()), (NBTBase) entry.getValue());
        }

        nbttagcompound.set("DimensionData", nbttagcompound3);
        if (nbttagcompound1 != null) {
            nbttagcompound.set("Player", nbttagcompound1);
        }

    }

    public long getSeed() {
        return this.e;
    }

    public int b() {
        return this.h;
    }

    public int c() {
        return this.i;
    }

    public int d() {
        return this.j;
    }

    public long getTime() {
        return this.k;
    }

    public long getDayTime() {
        return this.l;
    }

    public NBTTagCompound h() {
        return this.o;
    }

    public void setTime(long i) {
        this.k = i;
    }

    public void setDayTime(long i) {
        this.l = i;
    }

    public void setSpawn(BlockPosition blockposition) {
        this.h = blockposition.getX();
        this.i = blockposition.getY();
        this.j = blockposition.getZ();
    }

    public String getName() {
        return this.levelName;
    }

    public void a(String s) {
        this.levelName = s;
    }

    public int k() {
        return this.r;
    }

    public void e(int i) {
        this.r = i;
    }

    public int z() {
        return this.s;
    }

    public void i(int i) {
        this.s = i;
    }

    public boolean isThundering() {
        return this.v;
    }

    public void setThundering(boolean flag) {
        // CraftBukkit start
        org.bukkit.World world = Bukkit.getWorld(getName());
        if (world != null) {
            ThunderChangeEvent thunder = new ThunderChangeEvent(world, flag);
            Bukkit.getServer().getPluginManager().callEvent(thunder);
            if (thunder.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end
        this.v = flag;
    }

    public int getThunderDuration() {
        return this.w;
    }

    public void setThunderDuration(int i) {
        this.w = i;
    }

    public boolean hasStorm() {
        return this.t;
    }

    public void setStorm(boolean flag) {
        // CraftBukkit start
        org.bukkit.World world = Bukkit.getWorld(getName());
        if (world != null) {
            WeatherChangeEvent weather = new WeatherChangeEvent(world, flag);
            Bukkit.getServer().getPluginManager().callEvent(weather);
            if (weather.isCancelled()) {
                return;
            }
        }
        // CraftBukkit end
        this.t = flag;
    }

    public int getWeatherDuration() {
        return this.u;
    }

    public void setWeatherDuration(int i) {
        this.u = i;
    }

    public EnumGamemode getGameType() {
        return this.x;
    }

    public boolean shouldGenerateMapFeatures() {
        return this.y;
    }

    public void f(boolean flag) {
        this.y = flag;
    }

    public void setGameType(EnumGamemode enumgamemode) {
        this.x = enumgamemode;
    }

    public boolean isHardcore() {
        return this.z;
    }

    public void g(boolean flag) {
        this.z = flag;
    }

    public WorldType getType() {
        return this.f;
    }

    public void a(WorldType worldtype) {
        this.f = worldtype;
    }

    public String getGeneratorOptions() {
        return this.g == null ? "" : this.g;
    }

    public boolean u() {
        return this.A;
    }

    public void c(boolean flag) {
        this.A = flag;
    }

    public boolean v() {
        return this.B;
    }

    public void d(boolean flag) {
        this.B = flag;
    }

    public GameRules w() {
        return this.O;
    }

    public double B() {
        return this.E;
    }

    public double C() {
        return this.F;
    }

    public double D() {
        return this.G;
    }

    public void a(double d0) {
        this.G = d0;
    }

    public long E() {
        return this.H;
    }

    public void e(long i) {
        this.H = i;
    }

    public double F() {
        return this.I;
    }

    public void b(double d0) {
        this.I = d0;
    }

    public void c(double d0) {
        this.F = d0;
    }

    public void d(double d0) {
        this.E = d0;
    }

    public double G() {
        return this.J;
    }

    public void e(double d0) {
        this.J = d0;
    }

    public double H() {
        return this.K;
    }

    public void f(double d0) {
        this.K = d0;
    }

    public int I() {
        return this.L;
    }

    public int J() {
        return this.M;
    }

    public void j(int i) {
        this.L = i;
    }

    public void k(int i) {
        this.M = i;
    }

    public EnumDifficulty getDifficulty() {
        return this.C;
    }

    public void setDifficulty(EnumDifficulty enumdifficulty) {
        this.C = enumdifficulty;
        // CraftBukkit start
        PacketPlayOutServerDifficulty packet = new PacketPlayOutServerDifficulty(this.getDifficulty(), this.isDifficultyLocked());
        for (EntityPlayer player : (java.util.List<EntityPlayer>) (java.util.List) world.players) {
            player.playerConnection.sendPacket(packet);
        }
        // CraftBukkit end
    }

    public boolean isDifficultyLocked() {
        return this.D;
    }

    public void e(boolean flag) {
        this.D = flag;
    }

    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Level seed", new CrashReportCallable() {
            public String a() throws Exception {
                return String.valueOf(WorldData.this.getSeed());
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level generator", new CrashReportCallable() {
            public String a() throws Exception {
                return String.format("ID %02d - %s, ver %d. Features enabled: %b", new Object[] { Integer.valueOf(WorldData.this.f.g()), WorldData.this.f.name(), Integer.valueOf(WorldData.this.f.getVersion()), Boolean.valueOf(WorldData.this.y)});
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level generator options", new CrashReportCallable() {
            public String a() throws Exception {
                return WorldData.this.g;
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level spawn location", new CrashReportCallable() {
            public String a() throws Exception {
                return CrashReportSystemDetails.a(WorldData.this.h, WorldData.this.i, WorldData.this.j);
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level time", new CrashReportCallable() {
            public String a() throws Exception {
                return String.format("%d game time, %d day time", new Object[] { Long.valueOf(WorldData.this.k), Long.valueOf(WorldData.this.l)});
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level dimension", new CrashReportCallable() {
            public String a() throws Exception {
                return String.valueOf(WorldData.this.p);
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level storage version", new CrashReportCallable() {
            public String a() throws Exception {
                String s = "Unknown?";

                try {
                    switch (WorldData.this.r) {
                    case 19132:
                        s = "McRegion";
                        break;

                    case 19133:
                        s = "Anvil";
                    }
                } catch (Throwable throwable) {
                    ;
                }

                return String.format("0x%05X - %s", new Object[] { Integer.valueOf(WorldData.this.r), s});
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level weather", new CrashReportCallable() {
            public String a() throws Exception {
                return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", new Object[] { Integer.valueOf(WorldData.this.u), Boolean.valueOf(WorldData.this.t), Integer.valueOf(WorldData.this.w), Boolean.valueOf(WorldData.this.v)});
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
        crashreportsystemdetails.a("Level game mode", new CrashReportCallable() {
            public String a() throws Exception {
                return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", new Object[] { WorldData.this.x.b(), Integer.valueOf(WorldData.this.x.getId()), Boolean.valueOf(WorldData.this.z), Boolean.valueOf(WorldData.this.A)});
            }

            @Override
            public Object call() throws Exception {
                return this.a();
            }
        });
    }

    public NBTTagCompound a(DimensionManager dimensionmanager) {
        NBTTagCompound nbttagcompound = this.N.get(dimensionmanager);

        return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
    }

    public void a(DimensionManager dimensionmanager, NBTTagCompound nbttagcompound) {
        this.N.put(dimensionmanager, nbttagcompound);
    }

    // CraftBukkit start - Check if the name stored in NBT is the correct one
    public void checkName( String name ) {
        if ( !this.levelName.equals( name ) ) {
            this.levelName = name;
        }
    }
    // CraftBukkit end
}
