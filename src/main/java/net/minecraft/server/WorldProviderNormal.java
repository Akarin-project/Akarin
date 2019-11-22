package net.minecraft.server;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import javax.annotation.Nullable;

public class WorldProviderNormal extends WorldProvider {

    public WorldProviderNormal(World world, DimensionManager dimensionmanager) {
        super(world, dimensionmanager);
    }

    // CraftBukkit start
    /*
    @Override
    public DimensionManager getDimensionManager() {
        return DimensionManager.OVERWORLD;
    }
    */
    // CraftBukkit end

    @Override
    public ChunkGenerator<? extends GeneratorSettingsDefault> getChunkGenerator() {
        WorldType worldtype = this.b.getWorldData().getType();
        ChunkGeneratorType<GeneratorSettingsFlat, ChunkProviderFlat> chunkgeneratortype = ChunkGeneratorType.e;
        ChunkGeneratorType<GeneratorSettingsDebug, ChunkProviderDebug> chunkgeneratortype1 = ChunkGeneratorType.d;
        ChunkGeneratorType<GeneratorSettingsNether, ChunkProviderHell> chunkgeneratortype2 = ChunkGeneratorType.b;
        ChunkGeneratorType<GeneratorSettingsEnd, ChunkProviderTheEnd> chunkgeneratortype3 = ChunkGeneratorType.c;
        ChunkGeneratorType<GeneratorSettingsOverworld, ChunkProviderGenerate> chunkgeneratortype4 = ChunkGeneratorType.a;
        BiomeLayout<BiomeLayoutFixedConfiguration, WorldChunkManagerHell> biomelayout = BiomeLayout.b;
        BiomeLayout<BiomeLayoutOverworldConfiguration, WorldChunkManagerOverworld> biomelayout1 = BiomeLayout.c;
        BiomeLayout<BiomeLayoutCheckerboardConfiguration, WorldChunkManagerCheckerBoard> biomelayout2 = BiomeLayout.a;

        if (worldtype == WorldType.FLAT) {
            GeneratorSettingsFlat generatorsettingsflat = GeneratorSettingsFlat.a(new Dynamic(DynamicOpsNBT.a, this.b.getWorldData().getGeneratorOptions()));
            BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration = ((BiomeLayoutFixedConfiguration) biomelayout.a()).a(generatorsettingsflat.v());

            return chunkgeneratortype.create(this.b, biomelayout.a(biomelayoutfixedconfiguration), generatorsettingsflat);
        } else if (worldtype == WorldType.DEBUG_ALL_BLOCK_STATES) {
            BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration1 = ((BiomeLayoutFixedConfiguration) biomelayout.a()).a(Biomes.PLAINS);

            return chunkgeneratortype1.create(this.b, biomelayout.a(biomelayoutfixedconfiguration1), chunkgeneratortype1.a());
        } else if (worldtype != WorldType.g) {
            GeneratorSettingsOverworld generatorsettingsoverworld = (GeneratorSettingsOverworld) chunkgeneratortype4.a();
            BiomeLayoutOverworldConfiguration biomelayoutoverworldconfiguration = ((BiomeLayoutOverworldConfiguration) biomelayout1.a()).a(this.b.getWorldData()).a(generatorsettingsoverworld);

            return chunkgeneratortype4.create(this.b, biomelayout1.a(biomelayoutoverworldconfiguration), generatorsettingsoverworld);
        } else {
            WorldChunkManager worldchunkmanager = null;
            JsonElement jsonelement = (JsonElement) Dynamic.convert(DynamicOpsNBT.a, JsonOps.INSTANCE, this.b.getWorldData().getGeneratorOptions());
            JsonObject jsonobject = jsonelement.getAsJsonObject();
            JsonObject jsonobject1 = jsonobject.getAsJsonObject("biome_source");

            if (jsonobject1 != null && jsonobject1.has("type") && jsonobject1.has("options")) {
                BiomeLayout<?, ?> biomelayout3 = (BiomeLayout) IRegistry.BIOME_SOURCE_TYPE.get(new MinecraftKey(jsonobject1.getAsJsonPrimitive("type").getAsString()));
                JsonObject jsonobject2 = jsonobject1.getAsJsonObject("options");
                BiomeBase[] abiomebase = new BiomeBase[]{Biomes.OCEAN};

                if (jsonobject2.has("biomes")) {
                    JsonArray jsonarray = jsonobject2.getAsJsonArray("biomes");

                    abiomebase = jsonarray.size() > 0 ? new BiomeBase[jsonarray.size()] : new BiomeBase[]{Biomes.OCEAN};

                    for (int i = 0; i < jsonarray.size(); ++i) {
                        abiomebase[i] = (BiomeBase) IRegistry.BIOME.getOptional(new MinecraftKey(jsonarray.get(i).getAsString())).orElse(Biomes.OCEAN);
                    }
                }

                if (BiomeLayout.b == biomelayout3) {
                    BiomeLayoutFixedConfiguration biomelayoutfixedconfiguration2 = ((BiomeLayoutFixedConfiguration) biomelayout.a()).a(abiomebase[0]);

                    worldchunkmanager = biomelayout.a(biomelayoutfixedconfiguration2);
                }

                if (BiomeLayout.a == biomelayout3) {
                    int j = jsonobject2.has("size") ? jsonobject2.getAsJsonPrimitive("size").getAsInt() : 2;
                    BiomeLayoutCheckerboardConfiguration biomelayoutcheckerboardconfiguration = ((BiomeLayoutCheckerboardConfiguration) biomelayout2.a()).a(abiomebase).a(j);

                    worldchunkmanager = biomelayout2.a(biomelayoutcheckerboardconfiguration);
                }

                if (BiomeLayout.c == biomelayout3) {
                    BiomeLayoutOverworldConfiguration biomelayoutoverworldconfiguration1 = ((BiomeLayoutOverworldConfiguration) biomelayout1.a()).a(new GeneratorSettingsOverworld()).a(this.b.getWorldData());

                    worldchunkmanager = biomelayout1.a(biomelayoutoverworldconfiguration1);
                }
            }

            if (worldchunkmanager == null) {
                worldchunkmanager = biomelayout.a(((BiomeLayoutFixedConfiguration) biomelayout.a()).a(Biomes.OCEAN));
            }

            IBlockData iblockdata = Blocks.STONE.getBlockData();
            IBlockData iblockdata1 = Blocks.WATER.getBlockData();
            JsonObject jsonobject3 = jsonobject.getAsJsonObject("chunk_generator");

            if (jsonobject3 != null && jsonobject3.has("options")) {
                JsonObject jsonobject4 = jsonobject3.getAsJsonObject("options");
                String s;

                if (jsonobject4.has("default_block")) {
                    s = jsonobject4.getAsJsonPrimitive("default_block").getAsString();
                    iblockdata = ((Block) IRegistry.BLOCK.get(new MinecraftKey(s))).getBlockData();
                }

                if (jsonobject4.has("default_fluid")) {
                    s = jsonobject4.getAsJsonPrimitive("default_fluid").getAsString();
                    iblockdata1 = ((Block) IRegistry.BLOCK.get(new MinecraftKey(s))).getBlockData();
                }
            }

            if (jsonobject3 != null && jsonobject3.has("type")) {
                ChunkGeneratorType<?, ?> chunkgeneratortype5 = (ChunkGeneratorType) IRegistry.CHUNK_GENERATOR_TYPE.get(new MinecraftKey(jsonobject3.getAsJsonPrimitive("type").getAsString()));

                if (ChunkGeneratorType.b == chunkgeneratortype5) {
                    GeneratorSettingsNether generatorsettingsnether = (GeneratorSettingsNether) chunkgeneratortype2.a();

                    generatorsettingsnether.a(iblockdata);
                    generatorsettingsnether.b(iblockdata1);
                    return chunkgeneratortype2.create(this.b, worldchunkmanager, generatorsettingsnether);
                }

                if (ChunkGeneratorType.c == chunkgeneratortype5) {
                    GeneratorSettingsEnd generatorsettingsend = (GeneratorSettingsEnd) chunkgeneratortype3.a();

                    generatorsettingsend.a(new BlockPosition(0, 64, 0));
                    generatorsettingsend.a(iblockdata);
                    generatorsettingsend.b(iblockdata1);
                    return chunkgeneratortype3.create(this.b, worldchunkmanager, generatorsettingsend);
                }
            }

            GeneratorSettingsOverworld generatorsettingsoverworld1 = (GeneratorSettingsOverworld) chunkgeneratortype4.a();

            generatorsettingsoverworld1.a(iblockdata);
            generatorsettingsoverworld1.b(iblockdata1);
            return chunkgeneratortype4.create(this.b, worldchunkmanager, generatorsettingsoverworld1);
        }
    }

    @Nullable
    @Override
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        for (int i = chunkcoordintpair.d(); i <= chunkcoordintpair.f(); ++i) {
            for (int j = chunkcoordintpair.e(); j <= chunkcoordintpair.g(); ++j) {
                BlockPosition blockposition = this.a(i, j, flag);

                if (blockposition != null) {
                    return blockposition;
                }
            }
        }

        return null;
    }

    @Nullable
    @Override
    public BlockPosition a(int i, int j, boolean flag) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i, 0, j);
        BiomeBase biomebase = this.b.getBiome(blockposition_mutableblockposition);
        IBlockData iblockdata = biomebase.q().a();

        if (flag && !iblockdata.getBlock().a(TagsBlock.VALID_SPAWN)) {
            return null;
        } else {
            Chunk chunk = this.b.getChunkAt(i >> 4, j >> 4);
            int k = chunk.a(HeightMap.Type.MOTION_BLOCKING, i & 15, j & 15);

            if (k < 0) {
                return null;
            } else if (chunk.a(HeightMap.Type.WORLD_SURFACE, i & 15, j & 15) > chunk.a(HeightMap.Type.OCEAN_FLOOR, i & 15, j & 15)) {
                return null;
            } else {
                for (int l = k + 1; l >= 0; --l) {
                    blockposition_mutableblockposition.d(i, l, j);
                    IBlockData iblockdata1 = this.b.getType(blockposition_mutableblockposition);

                    if (!iblockdata1.p().isEmpty()) {
                        break;
                    }

                    if (iblockdata1.equals(iblockdata)) {
                        return blockposition_mutableblockposition.up().immutableCopy();
                    }
                }

                return null;
            }
        }
    }

    @Override
    public float a(long i, float f) {
        double d0 = MathHelper.h((double) i / 24000.0D - 0.25D);
        double d1 = 0.5D - Math.cos(d0 * 3.141592653589793D) / 2.0D;

        return (float) (d0 * 2.0D + d1) / 3.0F;
    }

    @Override
    public boolean isOverworld() {
        return true;
    }

    @Override
    public boolean canRespawn() {
        return true;
    }
}
