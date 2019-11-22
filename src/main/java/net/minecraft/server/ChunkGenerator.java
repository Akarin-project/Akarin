package net.minecraft.server;

import java.util.BitSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public abstract class ChunkGenerator<C extends GeneratorSettingsDefault> {

    protected final GeneratorAccess a;
    protected final long seed;
    protected final WorldChunkManager c;
    protected final C settings;

    public ChunkGenerator(GeneratorAccess generatoraccess, WorldChunkManager worldchunkmanager, C c0) {
        this.a = generatoraccess;
        this.seed = generatoraccess.getSeed();
        this.c = worldchunkmanager;
        this.settings = c0;
    }

    public void createBiomes(IChunkAccess ichunkaccess) {
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        BiomeBase[] abiomebase = this.c.getBiomeBlock(i * 16, j * 16, 16, 16);

        ichunkaccess.a(abiomebase);
    }

    protected BiomeBase getCarvingBiome(IChunkAccess ichunkaccess) {
        return ichunkaccess.getBiome(BlockPosition.ZERO);
    }

    protected BiomeBase getDecoratingBiome(RegionLimitedWorldAccess regionlimitedworldaccess, BlockPosition blockposition) {
        return this.c.getBiome(blockposition);
    }

    public void doCarving(IChunkAccess ichunkaccess, WorldGenStage.Features worldgenstage_features) {
        SeededRandom seededrandom = new SeededRandom();
        boolean flag = true;
        ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
        int i = chunkcoordintpair.x;
        int j = chunkcoordintpair.z;
        BitSet bitset = ichunkaccess.a(worldgenstage_features);

        for (int k = i - 8; k <= i + 8; ++k) {
            for (int l = j - 8; l <= j + 8; ++l) {
                List<WorldGenCarverWrapper<?>> list = this.getCarvingBiome(ichunkaccess).a(worldgenstage_features);
                ListIterator listiterator = list.listIterator();

                while (listiterator.hasNext()) {
                    int i1 = listiterator.nextIndex();
                    WorldGenCarverWrapper<?> worldgencarverwrapper = (WorldGenCarverWrapper) listiterator.next();

                    seededrandom.c(this.seed + (long) i1, k, l);
                    if (worldgencarverwrapper.a(seededrandom, k, l)) {
                        worldgencarverwrapper.a(ichunkaccess, seededrandom, this.getSeaLevel(), k, l, i, j, bitset);
                    }
                }
            }
        }

    }

    @Nullable
    public BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, int i, boolean flag) {
        StructureGenerator<?> structuregenerator = (StructureGenerator) WorldGenerator.aP.get(s.toLowerCase(Locale.ROOT));

        return structuregenerator != null ? structuregenerator.getNearestGeneratedFeature(world, this, blockposition, i, flag) : null;
    }

    public void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess) {
        int i = regionlimitedworldaccess.a();
        int j = regionlimitedworldaccess.b();
        int k = i * 16;
        int l = j * 16;
        BlockPosition blockposition = new BlockPosition(k, 0, l);
        BiomeBase biomebase = this.getDecoratingBiome(regionlimitedworldaccess, blockposition.b(8, 8, 8));
        SeededRandom seededrandom = new SeededRandom();
        long i1 = seededrandom.a(regionlimitedworldaccess.getSeed(), k, l);
        WorldGenStage.Decoration[] aworldgenstage_decoration = WorldGenStage.Decoration.values();
        int j1 = aworldgenstage_decoration.length;

        for (int k1 = 0; k1 < j1; ++k1) {
            WorldGenStage.Decoration worldgenstage_decoration = aworldgenstage_decoration[k1];

            try {
                biomebase.a(worldgenstage_decoration, this, regionlimitedworldaccess, i1, seededrandom, blockposition);
            } catch (Exception exception) {
                CrashReport crashreport = CrashReport.a(exception, "Biome decoration");

                crashreport.a("Generation").a("CenterX", (Object) i).a("CenterZ", (Object) j).a("Step", (Object) worldgenstage_decoration).a("Seed", (Object) i1).a("Biome", (Object) IRegistry.BIOME.getKey(biomebase));
                throw new ReportedException(crashreport);
            }
        }

    }

    public abstract void buildBase(IChunkAccess ichunkaccess);

    public void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess) {}

    public C getSettings() {
        return this.settings;
    }

    public abstract int getSpawnHeight();

    public void doMobSpawning(WorldServer worldserver, boolean flag, boolean flag1) {}

    public boolean canSpawnStructure(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator) {
        return biomebase.a(structuregenerator);
    }

    @Nullable
    public <C extends WorldGenFeatureConfiguration> C getFeatureConfiguration(BiomeBase biomebase, StructureGenerator<C> structuregenerator) {
        return biomebase.b(structuregenerator);
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.c;
    }

    public long getSeed() {
        return this.seed;
    }

    public int getGenerationDepth() {
        return 256;
    }

    public List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition) {
        return this.a.getBiome(blockposition).getMobs(enumcreaturetype);
    }

    public void createStructures(IChunkAccess ichunkaccess, ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager) {
        Iterator iterator = WorldGenerator.aP.values().iterator();

        while (iterator.hasNext()) {
            StructureGenerator<?> structuregenerator = (StructureGenerator) iterator.next();

            if (chunkgenerator.getWorldChunkManager().a(structuregenerator)) {
                SeededRandom seededrandom = new SeededRandom();
                ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();
                StructureStart structurestart = StructureStart.a;

                // CraftBukkit start
                if (structuregenerator == WorldGenerator.STRONGHOLD) {
                    synchronized (structuregenerator) {
                        if (structuregenerator.a(chunkgenerator, seededrandom, chunkcoordintpair.x, chunkcoordintpair.z)) {
                            BiomeBase biomebase = this.getWorldChunkManager().getBiome(new BlockPosition(chunkcoordintpair.d() + 9, 0, chunkcoordintpair.e() + 9));
                            StructureStart structurestart1 = structuregenerator.a().create(structuregenerator, chunkcoordintpair.x, chunkcoordintpair.z, biomebase, StructureBoundingBox.a(), 0, chunkgenerator.getSeed());

                            structurestart1.a(this, definedstructuremanager, chunkcoordintpair.x, chunkcoordintpair.z, biomebase);
                            structurestart = structurestart1.e() ? structurestart1 : StructureStart.a;
                        }
                    }
                } else
                // CraftBukkit end
                if (structuregenerator.a(chunkgenerator, seededrandom, chunkcoordintpair.x, chunkcoordintpair.z)) {
                    BiomeBase biomebase = this.getWorldChunkManager().getBiome(new BlockPosition(chunkcoordintpair.d() + 9, 0, chunkcoordintpair.e() + 9));
                    StructureStart structurestart1 = structuregenerator.a().create(structuregenerator, chunkcoordintpair.x, chunkcoordintpair.z, biomebase, StructureBoundingBox.a(), 0, chunkgenerator.getSeed());

                    structurestart1.a(this, definedstructuremanager, chunkcoordintpair.x, chunkcoordintpair.z, biomebase);
                    structurestart = structurestart1.e() ? structurestart1 : StructureStart.a;
                }

                ichunkaccess.a(structuregenerator.b(), structurestart);
            }
        }

    }

    public void storeStructures(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess) {
        boolean flag = true;
        int i = ichunkaccess.getPos().x;
        int j = ichunkaccess.getPos().z;
        int k = i << 4;
        int l = j << 4;

        for (int i1 = i - 8; i1 <= i + 8; ++i1) {
            for (int j1 = j - 8; j1 <= j + 8; ++j1) {
                long k1 = ChunkCoordIntPair.pair(i1, j1);
                Iterator iterator = generatoraccess.getChunkAt(i1, j1).h().entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<String, StructureStart> entry = (Entry) iterator.next();
                    StructureStart structurestart = (StructureStart) entry.getValue();

                    if (structurestart != StructureStart.a && structurestart.c().a(k, l, k + 15, l + 15)) {
                        ichunkaccess.a((String) entry.getKey(), k1);
                        PacketDebug.a(generatoraccess, structurestart);
                    }
                }
            }
        }

    }

    public abstract void buildNoise(GeneratorAccess generatoraccess, IChunkAccess ichunkaccess);

    public int getSeaLevel() {
        return 63;
    }

    public abstract int getBaseHeight(int i, int j, HeightMap.Type heightmap_type);

    public int b(int i, int j, HeightMap.Type heightmap_type) {
        return this.getBaseHeight(i, j, heightmap_type);
    }

    public int c(int i, int j, HeightMap.Type heightmap_type) {
        return this.getBaseHeight(i, j, heightmap_type) - 1;
    }

    // Spigot start
    public World getWorld() {
        return this.a.getMinecraftWorld();
    }
    // Spigot end
}
