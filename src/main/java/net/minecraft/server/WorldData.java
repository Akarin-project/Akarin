package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
    private int t;
    private String levelName;
    private int v;
    private int w;
    private boolean x;
    private int y;
    private boolean z;
    private int A;
    private EnumGamemode B;
    private boolean C;
    private boolean D;
    private boolean E;
    private boolean F;
    private EnumDifficulty G;
    private boolean H;
    private double I;
    private double J;
    private double K;
    private long L;
    private double M;
    private double N;
    private double O;
    private int P;
    private int Q;
    private final Set<String> R;
    private final Set<String> S;
    private final Map<DimensionManager, NBTTagCompound> T;
    private NBTTagCompound U;
    private final GameRules V;
    public WorldServer world; // CraftBukkit

    protected WorldData() {
        this.f = WorldType.NORMAL;
        this.g = new NBTTagCompound();
        this.K = 6.0E7D;
        this.N = 5.0D;
        this.O = 0.2D;
        this.P = 5;
        this.Q = 15;
        this.R = Sets.newHashSet();
        this.S = Sets.newLinkedHashSet();
        this.T = Maps.newIdentityHashMap();
        this.V = new GameRules();
        this.p = null;
        this.q = 1631;
        this.b(new NBTTagCompound());
    }

    public WorldData(NBTTagCompound nbttagcompound, DataFixer datafixer, int i, @Nullable NBTTagCompound nbttagcompound1) {
        this.f = WorldType.NORMAL;
        this.g = new NBTTagCompound();
        this.K = 6.0E7D;
        this.N = 5.0D;
        this.O = 0.2D;
        this.P = 5;
        this.Q = 15;
        this.R = Sets.newHashSet();
        this.S = Sets.newLinkedHashSet();
        this.T = Maps.newIdentityHashMap();
        this.V = new GameRules();
        this.p = datafixer;
        NBTTagCompound nbttagcompound2;

        if (nbttagcompound.hasKeyOfType("Version", 10)) {
            nbttagcompound2 = nbttagcompound.getCompound("Version");
            this.b = nbttagcompound2.getString("Name");
            this.c = nbttagcompound2.getInt("Id");
            this.d = nbttagcompound2.getBoolean("Snapshot");
        }

        this.e = nbttagcompound.getLong("RandomSeed");
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

        this.B = EnumGamemode.getById(nbttagcompound.getInt("GameType"));
        if (nbttagcompound.hasKeyOfType("legacy_custom_options", 8)) {
            this.h = nbttagcompound.getString("legacy_custom_options");
        }

        if (nbttagcompound.hasKeyOfType("MapFeatures", 99)) {
            this.C = nbttagcompound.getBoolean("MapFeatures");
        } else {
            this.C = true;
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
        this.v = nbttagcompound.getInt("version");
        this.w = nbttagcompound.getInt("clearWeatherTime");
        this.y = nbttagcompound.getInt("rainTime");
        this.x = nbttagcompound.getBoolean("raining");
        this.A = nbttagcompound.getInt("thunderTime");
        this.z = nbttagcompound.getBoolean("thundering");
        this.D = nbttagcompound.getBoolean("hardcore");
        if (nbttagcompound.hasKeyOfType("initialized", 99)) {
            this.F = nbttagcompound.getBoolean("initialized");
        } else {
            this.F = true;
        }

        if (nbttagcompound.hasKeyOfType("allowCommands", 99)) {
            this.E = nbttagcompound.getBoolean("allowCommands");
        } else {
            this.E = this.B == EnumGamemode.CREATIVE;
        }

        this.q = i;
        if (nbttagcompound1 != null) {
            this.s = nbttagcompound1;
        }

        if (nbttagcompound.hasKeyOfType("GameRules", 10)) {
            this.V.a(nbttagcompound.getCompound("GameRules"));
        }

        if (nbttagcompound.hasKeyOfType("Difficulty", 99)) {
            this.G = EnumDifficulty.getById(nbttagcompound.getByte("Difficulty"));
        }

        if (nbttagcompound.hasKeyOfType("DifficultyLocked", 1)) {
            this.H = nbttagcompound.getBoolean("DifficultyLocked");
        }

        if (nbttagcompound.hasKeyOfType("BorderCenterX", 99)) {
            this.I = nbttagcompound.getDouble("BorderCenterX");
        }

        if (nbttagcompound.hasKeyOfType("BorderCenterZ", 99)) {
            this.J = nbttagcompound.getDouble("BorderCenterZ");
        }

        if (nbttagcompound.hasKeyOfType("BorderSize", 99)) {
            this.K = nbttagcompound.getDouble("BorderSize");
        }

        if (nbttagcompound.hasKeyOfType("BorderSizeLerpTime", 99)) {
            this.L = nbttagcompound.getLong("BorderSizeLerpTime");
        }

        if (nbttagcompound.hasKeyOfType("BorderSizeLerpTarget", 99)) {
            this.M = nbttagcompound.getDouble("BorderSizeLerpTarget");
        }

        if (nbttagcompound.hasKeyOfType("BorderSafeZone", 99)) {
            this.N = nbttagcompound.getDouble("BorderSafeZone");
        }

        if (nbttagcompound.hasKeyOfType("BorderDamagePerBlock", 99)) {
            this.O = nbttagcompound.getDouble("BorderDamagePerBlock");
        }

        if (nbttagcompound.hasKeyOfType("BorderWarningBlocks", 99)) {
            this.P = nbttagcompound.getInt("BorderWarningBlocks");
        }

        if (nbttagcompound.hasKeyOfType("BorderWarningTime", 99)) {
            this.Q = nbttagcompound.getInt("BorderWarningTime");
        }

        if (nbttagcompound.hasKeyOfType("DimensionData", 10)) {
            nbttagcompound2 = nbttagcompound.getCompound("DimensionData");
            Iterator iterator = nbttagcompound2.getKeys().iterator();

            while (iterator.hasNext()) {
                String s1 = (String) iterator.next();

                this.T.put(DimensionManager.a(Integer.parseInt(s1)), nbttagcompound2.getCompound(s1));
            }
        }

        if (nbttagcompound.hasKeyOfType("DataPacks", 10)) {
            nbttagcompound2 = nbttagcompound.getCompound("DataPacks");
            NBTTagList nbttaglist = nbttagcompound2.getList("Disabled", 8);

            for (int k = 0; k < nbttaglist.size(); ++k) {
                this.R.add(nbttaglist.getString(k));
            }

            NBTTagList nbttaglist1 = nbttagcompound2.getList("Enabled", 8);

            for (int l = 0; l < nbttaglist1.size(); ++l) {
                this.S.add(nbttaglist1.getString(l));
            }
        }

        if (nbttagcompound.hasKeyOfType("CustomBossEvents", 10)) {
            this.U = nbttagcompound.getCompound("CustomBossEvents");
        }

    }

    public WorldData(WorldSettings worldsettings, String s) {
        this.f = WorldType.NORMAL;
        this.g = new NBTTagCompound();
        this.K = 6.0E7D;
        this.N = 5.0D;
        this.O = 0.2D;
        this.P = 5;
        this.Q = 15;
        this.R = Sets.newHashSet();
        this.S = Sets.newLinkedHashSet();
        this.T = Maps.newIdentityHashMap();
        this.V = new GameRules();
        this.p = null;
        this.q = 1631;
        this.a(worldsettings);
        this.levelName = s;
        this.G = WorldData.a;
        this.F = false;
    }

    public void a(WorldSettings worldsettings) {
        this.e = worldsettings.d();
        this.B = worldsettings.e();
        this.C = worldsettings.g();
        this.D = worldsettings.f();
        this.f = worldsettings.h();
        this.b((NBTTagCompound) Dynamic.convert(JsonOps.INSTANCE, DynamicOpsNBT.a, worldsettings.j()));
        this.E = worldsettings.i();
    }

    public NBTTagCompound a(@Nullable NBTTagCompound nbttagcompound) {
        this.Q();
        if (nbttagcompound == null) {
            nbttagcompound = this.s;
        }

        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        this.a(nbttagcompound1, nbttagcompound);
        return nbttagcompound1;
    }

    private void a(NBTTagCompound nbttagcompound, NBTTagCompound nbttagcompound1) {
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.setString("Name", "1.13.2");
        nbttagcompound2.setInt("Id", 1631);
        nbttagcompound2.setBoolean("Snapshot", false);
        nbttagcompound.set("Version", nbttagcompound2);
        nbttagcompound.setInt("DataVersion", 1631);
        if (org.bukkit.craftbukkit.util.CraftMagicNumbers.INSTANCE.getDataVersion() != 1631) throw new AssertionError(); // CraftBukkit - sentinel
        nbttagcompound.setLong("RandomSeed", this.e);
        nbttagcompound.setString("generatorName", this.f.b());
        nbttagcompound.setInt("generatorVersion", this.f.getVersion());
        if (!this.g.isEmpty()) {
            nbttagcompound.set("generatorOptions", this.g);
        }

        if (this.h != null) {
            nbttagcompound.setString("legacy_custom_options", this.h);
        }

        nbttagcompound.setInt("GameType", this.B.getId());
        nbttagcompound.setBoolean("MapFeatures", this.C);
        nbttagcompound.setInt("SpawnX", this.i);
        nbttagcompound.setInt("SpawnY", this.j);
        nbttagcompound.setInt("SpawnZ", this.k);
        nbttagcompound.setLong("Time", this.l);
        nbttagcompound.setLong("DayTime", this.m);
        nbttagcompound.setLong("SizeOnDisk", this.o);
        nbttagcompound.setLong("LastPlayed", SystemUtils.getTimeMillis());
        nbttagcompound.setString("LevelName", this.levelName);
        nbttagcompound.setInt("version", this.v);
        nbttagcompound.setInt("clearWeatherTime", this.w);
        nbttagcompound.setInt("rainTime", this.y);
        nbttagcompound.setBoolean("raining", this.x);
        nbttagcompound.setInt("thunderTime", this.A);
        nbttagcompound.setBoolean("thundering", this.z);
        nbttagcompound.setBoolean("hardcore", this.D);
        nbttagcompound.setBoolean("allowCommands", this.E);
        nbttagcompound.setBoolean("initialized", this.F);
        nbttagcompound.setDouble("BorderCenterX", this.I);
        nbttagcompound.setDouble("BorderCenterZ", this.J);
        nbttagcompound.setDouble("BorderSize", this.K);
        nbttagcompound.setLong("BorderSizeLerpTime", this.L);
        nbttagcompound.setDouble("BorderSafeZone", this.N);
        nbttagcompound.setDouble("BorderDamagePerBlock", this.O);
        nbttagcompound.setDouble("BorderSizeLerpTarget", this.M);
        nbttagcompound.setDouble("BorderWarningBlocks", (double) this.P);
        nbttagcompound.setDouble("BorderWarningTime", (double) this.Q);
        if (this.G != null) {
            nbttagcompound.setByte("Difficulty", (byte) this.G.a());
        }

        nbttagcompound.setBoolean("DifficultyLocked", this.H);
        nbttagcompound.set("GameRules", this.V.a());
        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        Iterator iterator = this.T.entrySet().iterator();

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
        Iterator iterator1 = this.S.iterator();

        while (iterator1.hasNext()) {
            String s = (String) iterator1.next();

            nbttaglist.add((NBTBase) (new NBTTagString(s)));
        }

        nbttagcompound4.set("Enabled", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator2 = this.R.iterator();

        while (iterator2.hasNext()) {
            String s1 = (String) iterator2.next();

            nbttaglist1.add((NBTBase) (new NBTTagString(s1)));
        }

        nbttagcompound4.set("Disabled", nbttaglist1);
        nbttagcompound.set("DataPacks", nbttagcompound4);
        if (this.U != null) {
            nbttagcompound.set("CustomBossEvents", this.U);
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

    private void Q() {
        if (!this.r && this.s != null) {
            if (this.q < 1631) {
                if (this.p == null) {
                    throw new NullPointerException("Fixer Upper not set inside LevelData, and the player tag is not upgraded.");
                }

                this.s = GameProfileSerializer.a(this.p, DataFixTypes.PLAYER, this.s, this.q);
            }

            this.t = this.s.getInt("Dimension");
            this.r = true;
        }
    }

    public NBTTagCompound h() {
        this.Q();
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

    public void a(String s) {
        this.levelName = s;
    }

    public int k() {
        return this.v;
    }

    public void d(int i) {
        this.v = i;
    }

    public int z() {
        return this.w;
    }

    public void g(int i) {
        this.w = i;
    }

    public boolean isThundering() {
        return this.z;
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
        this.z = flag;
    }

    public int getThunderDuration() {
        return this.A;
    }

    public void setThunderDuration(int i) {
        this.A = i;
    }

    public boolean hasStorm() {
        return this.x;
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
        this.x = flag;
    }

    public int getWeatherDuration() {
        return this.y;
    }

    public void setWeatherDuration(int i) {
        this.y = i;
    }

    public EnumGamemode getGameType() {
        return this.B;
    }

    public boolean shouldGenerateMapFeatures() {
        return this.C;
    }

    public void f(boolean flag) {
        this.C = flag;
    }

    public void setGameType(EnumGamemode enumgamemode) {
        this.B = enumgamemode;
    }

    public boolean isHardcore() {
        return this.D;
    }

    public void g(boolean flag) {
        this.D = flag;
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

    public boolean u() {
        return this.E;
    }

    public void c(boolean flag) {
        this.E = flag;
    }

    public boolean v() {
        return this.F;
    }

    public void d(boolean flag) {
        this.F = flag;
    }

    public GameRules w() {
        return this.V;
    }

    public double B() {
        return this.I;
    }

    public double C() {
        return this.J;
    }

    public double D() {
        return this.K;
    }

    public void a(double d0) {
        this.K = d0;
    }

    public long E() {
        return this.L;
    }

    public void c(long i) {
        this.L = i;
    }

    public double F() {
        return this.M;
    }

    public void b(double d0) {
        this.M = d0;
    }

    public void c(double d0) {
        this.J = d0;
    }

    public void d(double d0) {
        this.I = d0;
    }

    public double G() {
        return this.N;
    }

    public void e(double d0) {
        this.N = d0;
    }

    public double H() {
        return this.O;
    }

    public void f(double d0) {
        this.O = d0;
    }

    public int I() {
        return this.P;
    }

    public int J() {
        return this.Q;
    }

    public void h(int i) {
        this.P = i;
    }

    public void i(int i) {
        this.Q = i;
    }

    public EnumDifficulty getDifficulty() {
        return this.G;
    }

    public void setDifficulty(EnumDifficulty enumdifficulty) {
        this.G = enumdifficulty;
        // CraftBukkit start
        PacketPlayOutServerDifficulty packet = new PacketPlayOutServerDifficulty(this.getDifficulty(), this.isDifficultyLocked());
        for (EntityPlayer player : (java.util.List<EntityPlayer>) (java.util.List) world.players) {
            player.playerConnection.sendPacket(packet);
        }
        // CraftBukkit end
    }

    public boolean isDifficultyLocked() {
        return this.H;
    }

    public void e(boolean flag) {
        this.H = flag;
    }

    public void a(CrashReportSystemDetails crashreportsystemdetails) {
        crashreportsystemdetails.a("Level seed", () -> {
            return String.valueOf(this.getSeed());
        });
        crashreportsystemdetails.a("Level generator", () -> {
            return String.format("ID %02d - %s, ver %d. Features enabled: %b", this.f.i(), this.f.name(), this.f.getVersion(), this.C);
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
        crashreportsystemdetails.a("Level dimension", () -> {
            return String.valueOf(this.t);
        });
        crashreportsystemdetails.a("Level storage version", () -> {
            String s = "Unknown?";

            try {
                switch (this.v) {
                case 19132:
                    s = "McRegion";
                    break;
                case 19133:
                    s = "Anvil";
                }
            } catch (Throwable throwable) {
                ;
            }

            return String.format("0x%05X - %s", this.v, s);
        });
        crashreportsystemdetails.a("Level weather", () -> {
            return String.format("Rain time: %d (now: %b), thunder time: %d (now: %b)", this.y, this.x, this.A, this.z);
        });
        crashreportsystemdetails.a("Level game mode", () -> {
            return String.format("Game mode: %s (ID %d). Hardcore: %b. Cheats: %b", this.B.b(), this.B.getId(), this.D, this.E);
        });
    }

    public NBTTagCompound a(DimensionManager dimensionmanager) {
        NBTTagCompound nbttagcompound = (NBTTagCompound) this.T.get(dimensionmanager);

        return nbttagcompound == null ? new NBTTagCompound() : nbttagcompound;
    }

    public void a(DimensionManager dimensionmanager, NBTTagCompound nbttagcompound) {
        this.T.put(dimensionmanager, nbttagcompound);
    }

    public Set<String> N() {
        return this.R;
    }

    public Set<String> O() {
        return this.S;
    }

    @Nullable
    public NBTTagCompound P() {
        return this.U;
    }

    public void c(@Nullable NBTTagCompound nbttagcompound) {
        this.U = nbttagcompound;
    }

    // CraftBukkit start - Check if the name stored in NBT is the correct one
    public void checkName( String name ) {
        if ( !this.levelName.equals( name ) ) {
            this.levelName = name;
        }
    }
    // CraftBukkit end
}
