package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.List;
import javax.annotation.Nullable;

public interface ChunkGenerator<C extends GeneratorSettings> {

    void createChunk(IChunkAccess ichunkaccess);

    void addFeatures(RegionLimitedWorldAccess regionlimitedworldaccess, WorldGenStage.Features worldgenstage_features);

    void addDecorations(RegionLimitedWorldAccess regionlimitedworldaccess);

    void addMobs(RegionLimitedWorldAccess regionlimitedworldaccess);

    List<BiomeBase.BiomeMeta> getMobsFor(EnumCreatureType enumcreaturetype, BlockPosition blockposition);

    @Nullable
    BlockPosition findNearestMapFeature(World world, String s, BlockPosition blockposition, int i, boolean flag);

    C getSettings();

    int a(World world, boolean flag, boolean flag1);

    boolean canSpawnStructure(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator);

    @Nullable
    WorldGenFeatureConfiguration getFeatureConfiguration(BiomeBase biomebase, StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator);

    Long2ObjectMap<StructureStart> getStructureStartCache(StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator);

    Long2ObjectMap<LongSet> getStructureCache(StructureGenerator<? extends WorldGenFeatureConfiguration> structuregenerator);

    WorldChunkManager getWorldChunkManager();

    long getSeed();

    int getSpawnHeight();

    int getGenerationDepth();
}
