package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BiomeBase {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Set<BiomeBase> b = Sets.newHashSet();
    public static final RegistryBlockID<BiomeBase> c = new RegistryBlockID<>();
    protected static final NoiseGenerator3 d = new NoiseGenerator3(new Random(1234L), 1);
    public static final NoiseGenerator3 e = new NoiseGenerator3(new Random(2345L), 1);
    @Nullable
    protected String f;
    protected final float g;
    protected final float h;
    protected final float i;
    protected final float j;
    protected final int k;
    protected final int l;
    @Nullable
    protected final String m;
    protected final WorldGenSurfaceComposite<?> n;
    protected final BiomeBase.Geography o;
    protected final BiomeBase.Precipitation p;
    protected final Map<WorldGenStage.Features, List<WorldGenCarverWrapper<?>>> q = Maps.newHashMap();
    protected final Map<WorldGenStage.Decoration, List<WorldGenFeatureConfigured<?>>> r = Maps.newHashMap();
    protected final List<WorldGenFeatureConfigured<?>> s = Lists.newArrayList();
    protected final Map<StructureGenerator<?>, WorldGenFeatureConfiguration> t = Maps.newHashMap();
    private final java.util.EnumMap<EnumCreatureType, List<BiomeBase.BiomeMeta>> u = Maps.newEnumMap(EnumCreatureType.class); // Paper
    private final ThreadLocal<Long2FloatLinkedOpenHashMap> v = ThreadLocal.withInitial(() -> {
        return (Long2FloatLinkedOpenHashMap) SystemUtils.a(() -> {
            Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = new Long2FloatLinkedOpenHashMap(1024, 0.25F) {
                protected void rehash(int i) {}
            };

            long2floatlinkedopenhashmap.defaultReturnValue(Float.NaN);
            return long2floatlinkedopenhashmap;
        });
    });

    @Nullable
    public static BiomeBase a(BiomeBase biomebase) {
        return (BiomeBase) BiomeBase.c.fromId(IRegistry.BIOME.a(biomebase)); // Paper - decompile fix
    }

    public static <C extends WorldGenCarverConfiguration> WorldGenCarverWrapper<C> a(WorldGenCarverAbstract<C> worldgencarverabstract, C c0) {
        return new WorldGenCarverWrapper<>(worldgencarverabstract, c0);
    }

    public static <F extends WorldGenFeatureConfiguration, D extends WorldGenFeatureDecoratorConfiguration> WorldGenFeatureConfigured<?> a(WorldGenerator<F> worldgenerator, F f0, WorldGenDecorator<D> worldgendecorator, D d0) {
        WorldGenerator<WorldGenFeatureCompositeConfiguration> worldgenerator1 = worldgenerator instanceof WorldGenFlowers ? WorldGenerator.DECORATED_FLOWER : WorldGenerator.DECORATED;

        return new WorldGenFeatureConfigured<>(worldgenerator1, new WorldGenFeatureCompositeConfiguration(worldgenerator, f0, worldgendecorator, d0));
    }

    protected BiomeBase(BiomeBase.a biomebase_a) {
        if (biomebase_a.a != null && biomebase_a.b != null && biomebase_a.c != null && biomebase_a.d != null && biomebase_a.e != null && biomebase_a.f != null && biomebase_a.g != null && biomebase_a.h != null && biomebase_a.i != null) {
            this.n = biomebase_a.a;
            this.p = biomebase_a.b;
            this.o = biomebase_a.c;
            this.g = biomebase_a.d;
            this.h = biomebase_a.e;
            this.i = biomebase_a.f;
            this.j = biomebase_a.g;
            this.k = biomebase_a.h;
            this.l = biomebase_a.i;
            this.m = biomebase_a.j;
            WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
            int i = aworldgenstage_decoration.length;

            int j;

            for (j = 0; j < i; ++j) {
                WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[j];

                this.r.put(worldgenstage_decoration, Lists.newArrayList());
            }

            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();

            i = aenumcreaturetype.length;

            for (j = 0; j < i; ++j) {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[j];

                this.u.put(enumcreaturetype, new MobList()); // Paper
            }

        } else {
            throw new IllegalStateException("You are missing parameters to build a proper biome for " + this.getClass().getSimpleName() + "\n" + biomebase_a);
        }
    }

    public boolean a() {
        return this.m != null;
    }

    protected void a(EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta) {
        ((List) this.u.get(enumcreaturetype)).add(biomebase_biomemeta);
    }

    public List<BiomeBase.BiomeMeta> getMobs(EnumCreatureType enumcreaturetype) {
        return (List) this.u.get(enumcreaturetype);
    }

    public BiomeBase.Precipitation b() {
        return this.p;
    }

    public boolean c() {
        return this.getHumidity() > 0.85F;
    }

    public float d() {
        return 0.1F;
    }

    protected float c(BlockPosition blockposition) {
        if (blockposition.getY() > 64) {
            float f = (float) (BiomeBase.d.a((double) ((float) blockposition.getX() / 8.0F), (double) ((float) blockposition.getZ() / 8.0F)) * 4.0D);

            return this.getTemperature() - (f + (float) blockposition.getY() - 64.0F) * 0.05F / 30.0F;
        } else {
            return this.getTemperature();
        }
    }

    public final float getAdjustedTemperature(BlockPosition blockposition) {
        long i = blockposition.asLong();
        Long2FloatLinkedOpenHashMap long2floatlinkedopenhashmap = (Long2FloatLinkedOpenHashMap) this.v.get();
        float f = long2floatlinkedopenhashmap.get(i);

        if (!Float.isNaN(f)) {
            return f;
        } else {
            float f1 = this.c(blockposition);

            if (long2floatlinkedopenhashmap.size() == 1024) {
                long2floatlinkedopenhashmap.removeFirstFloat();
            }

            long2floatlinkedopenhashmap.put(i, f1);
            return f1;
        }
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.a(iworldreader, blockposition, true);
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition, boolean flag) {
        if (this.getAdjustedTemperature(blockposition) >= 0.15F) {
            return false;
        } else {
            if (blockposition.getY() >= 0 && blockposition.getY() < 256 && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getType(blockposition);
                Fluid fluid = iworldreader.getFluid(blockposition);

                if (fluid.getType() == FluidTypes.WATER && iblockdata.getBlock() instanceof BlockFluids) {
                    if (!flag) {
                        return true;
                    }

                    boolean flag1 = iworldreader.x(blockposition.west()) && iworldreader.x(blockposition.east()) && iworldreader.x(blockposition.north()) && iworldreader.x(blockposition.south());

                    if (!flag1) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public boolean b(IWorldReader iworldreader, BlockPosition blockposition) {
        if (this.getAdjustedTemperature(blockposition) >= 0.15F) {
            return false;
        } else {
            if (blockposition.getY() >= 0 && blockposition.getY() < 256 && iworldreader.getBrightness(EnumSkyBlock.BLOCK, blockposition) < 10) {
                IBlockData iblockdata = iworldreader.getType(blockposition);

                if (iblockdata.isAir() && Blocks.SNOW.getBlockData().canPlace(iworldreader, blockposition)) {
                    return true;
                }
            }

            return false;
        }
    }

    public void a(WorldGenStage.Decoration worldgenstage_decoration, WorldGenFeatureConfigured<?> worldgenfeatureconfigured) {
        if (worldgenfeatureconfigured.a == WorldGenerator.DECORATED_FLOWER) {
            this.s.add(worldgenfeatureconfigured);
        }

        ((List) this.r.get(worldgenstage_decoration)).add(worldgenfeatureconfigured);
    }

    public <C extends WorldGenCarverConfiguration> void a(WorldGenStage.Features worldgenstage_features, WorldGenCarverWrapper<C> worldgencarverwrapper) {
        ((List) this.q.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return Lists.newArrayList();
        })).add(worldgencarverwrapper);
    }

    public List<WorldGenCarverWrapper<?>> a(WorldGenStage.Features worldgenstage_features) {
        return (List) this.q.computeIfAbsent(worldgenstage_features, (worldgenstage_features1) -> {
            return Lists.newArrayList();
        });
    }

    public <C extends WorldGenFeatureConfiguration> void a(StructureGenerator<C> structuregenerator, C c0) {
        this.t.put(structuregenerator, c0);
    }

    public <C extends WorldGenFeatureConfiguration> boolean a(StructureGenerator<C> structuregenerator) {
        return this.t.containsKey(structuregenerator);
    }

    @Nullable
    public <C extends WorldGenFeatureConfiguration> C b(StructureGenerator<C> structuregenerator) {
        return (C) this.t.get(structuregenerator); // Paper - decompile fix
    }

    public List<WorldGenFeatureConfigured<?>> e() {
        return this.s;
    }

    public List<WorldGenFeatureConfigured<?>> a(WorldGenStage.Decoration worldgenstage_decoration) {
        return (List) this.r.get(worldgenstage_decoration);
    }

    public void a(WorldGenStage.Decoration worldgenstage_decoration, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, GeneratorAccess generatoraccess, long i, SeededRandom seededrandom, BlockPosition blockposition) {
        int j = 0;

        for (Iterator iterator = ((List) this.r.get(worldgenstage_decoration)).iterator(); iterator.hasNext(); ++j) {
            WorldGenFeatureConfigured<?> worldgenfeatureconfigured = (WorldGenFeatureConfigured) iterator.next();

            seededrandom.b(i, j, worldgenstage_decoration.ordinal());

            try {
                worldgenfeatureconfigured.a(generatoraccess, chunkgenerator, seededrandom, blockposition);
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.a(exception, "Feature placement");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Feature").a("Id", (Object) IRegistry.FEATURE.getKey(worldgenfeatureconfigured.a));
                WorldGenerator worldgenerator = worldgenfeatureconfigured.a;

                worldgenfeatureconfigured.a.getClass();
                crashreportsystemdetails.a("Description", worldgenerator::toString);
                throw new ReportedException(crashreport);
            }
        }

    }

    public void a(Random random, IChunkAccess ichunkaccess, int i, int j, int k, double d0, IBlockData iblockdata, IBlockData iblockdata1, int l, long i1) {
        this.n.a(i1);
        this.n.a(random, ichunkaccess, this, i, j, k, d0, iblockdata, iblockdata1, l, i1);
    }

    public BiomeBase.EnumTemperature f() {
        return this.o == BiomeBase.Geography.OCEAN ? BiomeBase.EnumTemperature.OCEAN : ((double) this.getTemperature() < 0.2D ? BiomeBase.EnumTemperature.COLD : ((double) this.getTemperature() < 1.0D ? BiomeBase.EnumTemperature.MEDIUM : BiomeBase.EnumTemperature.WARM));
    }

    public final float g() {
        return this.g;
    }

    public final float getHumidity() {
        return this.j;
    }

    public String j() {
        if (this.f == null) {
            this.f = SystemUtils.a("biome", IRegistry.BIOME.getKey(this));
        }

        return this.f;
    }

    public final float k() {
        return this.h;
    }

    public final float getTemperature() {
        return this.i;
    }

    public final int m() {
        return this.k;
    }

    public final int n() {
        return this.l;
    }

    public final BiomeBase.Geography o() {
        return this.o;
    }

    public WorldGenSurfaceComposite<?> p() {
        return this.n;
    }

    public WorldGenSurfaceConfiguration q() {
        return this.n.a();
    }

    @Nullable
    public String r() {
        return this.m;
    }

    // Paper start - keep track of data in a pair set to give O(1) contains calls - we have to hook removals incase plugins mess with it
    public static class MobList extends java.util.ArrayList<BiomeMeta> {
        java.util.Set<BiomeMeta> biomes = new java.util.HashSet<>();

        @Override
        public boolean contains(Object o) {
            return biomes.contains(o);
        }

        @Override
        public boolean add(BiomeMeta biomeMeta) {
            biomes.add(biomeMeta);
            return super.add(biomeMeta);
        }

        @Override
        public BiomeMeta remove(int index) {
            BiomeMeta removed = super.remove(index);
            if (removed != null) {
                biomes.remove(removed);
            }
            return removed;
        }

        @Override
        public void clear() {
            biomes.clear();
            super.clear();
        }
    }
    // Paper end

    public static class a {

        @Nullable
        private WorldGenSurfaceComposite<?> a;
        @Nullable
        private BiomeBase.Precipitation b;
        @Nullable
        private BiomeBase.Geography c;
        @Nullable
        private Float d;
        @Nullable
        private Float e;
        @Nullable
        private Float f;
        @Nullable
        private Float g;
        @Nullable
        private Integer h;
        @Nullable
        private Integer i;
        @Nullable
        private String j;

        public a() {}

        public <SC extends WorldGenSurfaceConfiguration> BiomeBase.a a(WorldGenSurface<SC> worldgensurface, SC sc) {
            this.a = new WorldGenSurfaceComposite<>(worldgensurface, sc);
            return this;
        }

        public BiomeBase.a a(WorldGenSurfaceComposite<?> worldgensurfacecomposite) {
            this.a = worldgensurfacecomposite;
            return this;
        }

        public BiomeBase.a a(BiomeBase.Precipitation biomebase_precipitation) {
            this.b = biomebase_precipitation;
            return this;
        }

        public BiomeBase.a a(BiomeBase.Geography biomebase_geography) {
            this.c = biomebase_geography;
            return this;
        }

        public BiomeBase.a a(float f) {
            this.d = f;
            return this;
        }

        public BiomeBase.a b(float f) {
            this.e = f;
            return this;
        }

        public BiomeBase.a c(float f) {
            this.f = f;
            return this;
        }

        public BiomeBase.a d(float f) {
            this.g = f;
            return this;
        }

        public BiomeBase.a a(int i) {
            this.h = i;
            return this;
        }

        public BiomeBase.a b(int i) {
            this.i = i;
            return this;
        }

        public BiomeBase.a a(@Nullable String s) {
            this.j = s;
            return this;
        }

        public String toString() {
            return "BiomeBuilder{\nsurfaceBuilder=" + this.a + ",\nprecipitation=" + this.b + ",\nbiomeCategory=" + this.c + ",\ndepth=" + this.d + ",\nscale=" + this.e + ",\ntemperature=" + this.f + ",\ndownfall=" + this.g + ",\nwaterColor=" + this.h + ",\nwaterFogColor=" + this.i + ",\nparent='" + this.j + '\'' + "\n" + '}';
        }
    }

    public static class BiomeMeta extends WeightedRandom.WeightedRandomChoice {

        public final EntityTypes<?> b;
        public final int c;
        public final int d;

        public BiomeMeta(EntityTypes<?> entitytypes, int i, int j, int k) {
            super(i);
            this.b = entitytypes;
            this.c = j;
            this.d = k;
        }

        public String toString() {
            return EntityTypes.getName(this.b) + "*(" + this.c + "-" + this.d + "):" + this.a;
        }
    }

    public static enum Precipitation {

        NONE("none"), RAIN("rain"), SNOW("snow");

        private static final Map<String, BiomeBase.Precipitation> d = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Precipitation::a, (biomebase_precipitation) -> {
            return biomebase_precipitation;
        }));
        private final String e;

        private Precipitation(String s) {
            this.e = s;
        }

        public String a() {
            return this.e;
        }
    }

    public static enum Geography {

        NONE("none"), TAIGA("taiga"), EXTREME_HILLS("extreme_hills"), JUNGLE("jungle"), MESA("mesa"), PLAINS("plains"), SAVANNA("savanna"), ICY("icy"), THEEND("the_end"), BEACH("beach"), FOREST("forest"), OCEAN("ocean"), DESERT("desert"), RIVER("river"), SWAMP("swamp"), MUSHROOM("mushroom"), NETHER("nether");

        private static final Map<String, BiomeBase.Geography> r = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.Geography::a, (biomebase_geography) -> {
            return biomebase_geography;
        }));
        private final String s;

        private Geography(String s) {
            this.s = s;
        }

        public String a() {
            return this.s;
        }
    }

    public static enum EnumTemperature {

        OCEAN("ocean"), COLD("cold"), MEDIUM("medium"), WARM("warm");

        private static final Map<String, BiomeBase.EnumTemperature> e = (Map) Arrays.stream(values()).collect(Collectors.toMap(BiomeBase.EnumTemperature::a, (biomebase_enumtemperature) -> {
            return biomebase_enumtemperature;
        }));
        private final String f;

        private EnumTemperature(String s) {
            this.f = s;
        }

        public String a() {
            return this.f;
        }
    }
}
