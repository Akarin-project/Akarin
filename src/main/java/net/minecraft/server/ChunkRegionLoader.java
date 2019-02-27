package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import it.unimi.dsi.fastutil.shorts.ShortList;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.BitSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkRegionLoader implements IChunkLoader, IAsyncChunkSaver {

    private static final Logger a = LogManager.getLogger();
    private final Map<ChunkCoordIntPair, NBTTagCompound> b = Maps.newHashMap();
    private final File c;
    private final DataFixer d;
    private PersistentStructureLegacy e;
    private boolean f;

    public ChunkRegionLoader(File file, DataFixer datafixer) {
        this.c = file;
        this.d = datafixer;
    }

    @Nullable
    private NBTTagCompound a(GeneratorAccess generatoraccess, int i, int j) throws IOException {
        return this.a(generatoraccess.o().getDimensionManager(), generatoraccess.h(), i, j);
    }

    @Nullable
    private NBTTagCompound a(DimensionManager dimensionmanager, @Nullable PersistentCollection persistentcollection, int i, int j) throws IOException {
        NBTTagCompound nbttagcompound = (NBTTagCompound) this.b.get(new ChunkCoordIntPair(i, j));

        if (nbttagcompound != null) {
            return nbttagcompound;
        } else {
            DataInputStream datainputstream = RegionFileCache.read(this.c, i, j);

            if (datainputstream == null) {
                return null;
            } else {
                NBTTagCompound nbttagcompound1 = NBTCompressedStreamTools.a(datainputstream);

                datainputstream.close();
                int k = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : -1;

                if (k < 1493) {
                    nbttagcompound1 = GameProfileSerializer.a(this.d, DataFixTypes.CHUNK, nbttagcompound1, k, 1493);
                    if (nbttagcompound1.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                        this.a(dimensionmanager, persistentcollection);
                        nbttagcompound1 = this.e.a(nbttagcompound1);
                    }
                }

                nbttagcompound1 = GameProfileSerializer.a(this.d, DataFixTypes.CHUNK, nbttagcompound1, Math.max(1493, k));
                if (k < 1631) {
                    nbttagcompound1.setInt("DataVersion", 1631);
                    this.a(new ChunkCoordIntPair(i, j), nbttagcompound1);
                }

                return nbttagcompound1;
            }
        }
    }

    public void a(DimensionManager dimensionmanager, @Nullable PersistentCollection persistentcollection) {
        if (this.e == null) {
            this.e = PersistentStructureLegacy.a(dimensionmanager, persistentcollection);
        }

    }

    @Nullable
    public Chunk a(GeneratorAccess generatoraccess, int i, int j, Consumer<Chunk> consumer) throws IOException {
        NBTTagCompound nbttagcompound = this.a(generatoraccess, i, j);

        if (nbttagcompound == null) {
            return null;
        } else {
            Chunk chunk = this.a(generatoraccess, i, j, nbttagcompound);

            if (chunk != null) {
                consumer.accept(chunk);
                this.loadEntities(nbttagcompound.getCompound("Level"), chunk);
            }

            return chunk;
        }
    }

    @Nullable
    public ProtoChunk b(GeneratorAccess generatoraccess, int i, int j, Consumer<IChunkAccess> consumer) throws IOException {
        NBTTagCompound nbttagcompound;

        try {
            nbttagcompound = this.a(generatoraccess, i, j);
        } catch (ReportedException reportedexception) {
            if (reportedexception.getCause() instanceof IOException) {
                throw (IOException) reportedexception.getCause();
            }

            throw reportedexception;
        }

        if (nbttagcompound == null) {
            return null;
        } else {
            ProtoChunk protochunk = this.b(generatoraccess, i, j, nbttagcompound);

            if (protochunk != null) {
                consumer.accept(protochunk);
            }

            return protochunk;
        }
    }

    @Nullable
    protected Chunk a(GeneratorAccess generatoraccess, int i, int j, NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Level", 10) && nbttagcompound.getCompound("Level").hasKeyOfType("Status", 8)) {
            ChunkStatus.Type chunkstatus_type = this.a(nbttagcompound);

            if (chunkstatus_type != ChunkStatus.Type.LEVELCHUNK) {
                return null;
            } else {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");

                if (!nbttagcompound1.hasKeyOfType("Sections", 9)) {
                    ChunkRegionLoader.a.error("Chunk file at {},{} is missing block data, skipping", i, j);
                    return null;
                } else {
                    Chunk chunk = this.a(generatoraccess, nbttagcompound1);

                    if (!chunk.a(i, j)) {
                        ChunkRegionLoader.a.error("Chunk file at {},{} is in the wrong location; relocating. (Expected {}, {}, got {}, {})", i, j, i, j, chunk.locX, chunk.locZ);
                        nbttagcompound1.setInt("xPos", i);
                        nbttagcompound1.setInt("zPos", j);
                        chunk = this.a(generatoraccess, nbttagcompound1);
                    }

                    return chunk;
                }
            }
        } else {
            ChunkRegionLoader.a.error("Chunk file at {},{} is missing level data, skipping", i, j);
            return null;
        }
    }

    @Nullable
    protected ProtoChunk b(GeneratorAccess generatoraccess, int i, int j, NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKeyOfType("Level", 10) && nbttagcompound.getCompound("Level").hasKeyOfType("Status", 8)) {
            ChunkStatus.Type chunkstatus_type = this.a(nbttagcompound);

            if (chunkstatus_type == ChunkStatus.Type.LEVELCHUNK) {
                return new ProtoChunkExtension(this.a(generatoraccess, i, j, nbttagcompound));
            } else {
                NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Level");

                return this.b(generatoraccess, nbttagcompound1);
            }
        } else {
            ChunkRegionLoader.a.error("Chunk file at {},{} is missing level data, skipping", i, j);
            return null;
        }
    }

    public void saveChunk(World world, IChunkAccess ichunkaccess) throws IOException, ExceptionWorldConflict {
        world.checkSession();

        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound.setInt("DataVersion", 1631);
            ChunkCoordIntPair chunkcoordintpair = ichunkaccess.getPos();

            nbttagcompound.set("Level", nbttagcompound1);
            if (ichunkaccess.i().d() == ChunkStatus.Type.LEVELCHUNK) {
                this.saveBody((Chunk) ichunkaccess, world, nbttagcompound1);
            } else {
                NBTTagCompound nbttagcompound2 = this.a(world, chunkcoordintpair.x, chunkcoordintpair.z);

                if (nbttagcompound2 != null && this.a(nbttagcompound2) == ChunkStatus.Type.LEVELCHUNK) {
                    return;
                }

                this.a((ProtoChunk) ichunkaccess, world, nbttagcompound1);
            }

            this.a(chunkcoordintpair, nbttagcompound);
        } catch (Exception exception) {
            ChunkRegionLoader.a.error("Failed to save chunk", exception);
        }

    }

    protected void a(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) {
        this.b.put(chunkcoordintpair, nbttagcompound);
        FileIOThread.a().a(this);
    }

    public boolean a() {
        Iterator<Entry<ChunkCoordIntPair, NBTTagCompound>> iterator = this.b.entrySet().iterator();

        if (!iterator.hasNext()) {
            if (this.f) {
                ChunkRegionLoader.a.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", this.c.getName());
            }

            return false;
        } else {
            Entry<ChunkCoordIntPair, NBTTagCompound> entry = (Entry) iterator.next();

            iterator.remove();
            ChunkCoordIntPair chunkcoordintpair = (ChunkCoordIntPair) entry.getKey();
            NBTTagCompound nbttagcompound = (NBTTagCompound) entry.getValue();

            if (nbttagcompound == null) {
                return true;
            } else {
                try {
                    DataOutputStream dataoutputstream = RegionFileCache.write(this.c, chunkcoordintpair.x, chunkcoordintpair.z);

                    NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
                    dataoutputstream.close();
                    if (this.e != null) {
                        this.e.a(chunkcoordintpair.a());
                    }
                } catch (Exception exception) {
                    ChunkRegionLoader.a.error("Failed to save chunk", exception);
                }

                return true;
            }
        }
    }

    private ChunkStatus.Type a(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null) {
            ChunkStatus chunkstatus = ChunkStatus.a(nbttagcompound.getCompound("Level").getString("Status"));

            if (chunkstatus != null) {
                return chunkstatus.d();
            }
        }

        return ChunkStatus.Type.PROTOCHUNK;
    }

    public void b() {
        try {
            this.f = true;

            while (true) {
                if (this.a()) {
                    continue;
                }
            }
        } finally {
            this.f = false;
        }

    }

    private void a(ProtoChunk protochunk, World world, NBTTagCompound nbttagcompound) {
        int i = protochunk.getPos().x;
        int j = protochunk.getPos().z;

        nbttagcompound.setInt("xPos", i);
        nbttagcompound.setInt("zPos", j);
        nbttagcompound.setLong("LastUpdate", world.getTime());
        nbttagcompound.setLong("InhabitedTime", protochunk.m());
        nbttagcompound.setString("Status", protochunk.i().b());
        ChunkConverter chunkconverter = protochunk.v();

        if (!chunkconverter.a()) {
            nbttagcompound.set("UpgradeData", chunkconverter.b());
        }

        ChunkSection[] achunksection = protochunk.getSections();
        NBTTagList nbttaglist = this.a(world, achunksection);

        nbttagcompound.set("Sections", nbttaglist);
        BiomeBase[] abiomebase = protochunk.getBiomeIndex();
        int[] aint = abiomebase != null ? new int[abiomebase.length] : new int[0];

        if (abiomebase != null) {
            for (int k = 0; k < abiomebase.length; ++k) {
                aint[k] = IRegistry.BIOME.a((Object) abiomebase[k]);
            }
        }

        nbttagcompound.setIntArray("Biomes", aint);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator = protochunk.s().iterator();

        NBTTagCompound nbttagcompound1;

        while (iterator.hasNext()) {
            nbttagcompound1 = (NBTTagCompound) iterator.next();
            nbttaglist1.add((NBTBase) nbttagcompound1);
        }

        nbttagcompound.set("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();
        Iterator iterator1 = protochunk.q().iterator();

        while (iterator1.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator1.next();
            TileEntity tileentity = protochunk.getTileEntity(blockposition);

            if (tileentity != null) {
                NBTTagCompound nbttagcompound2 = new NBTTagCompound();

                tileentity.save(nbttagcompound2);
                nbttaglist2.add((NBTBase) nbttagcompound2);
            } else {
                nbttaglist2.add((NBTBase) protochunk.g(blockposition));
            }
        }

        nbttagcompound.set("TileEntities", nbttaglist2);
        nbttagcompound.set("Lights", a(protochunk.p()));
        nbttagcompound.set("PostProcessing", a(protochunk.u()));
        nbttagcompound.set("ToBeTicked", protochunk.k().a());
        nbttagcompound.set("LiquidsToBeTicked", protochunk.l().a());
        nbttagcompound1 = new NBTTagCompound();
        Iterator iterator2 = protochunk.t().iterator();

        while (iterator2.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator2.next();

            nbttagcompound1.set(heightmap_type.b(), new NBTTagLongArray(protochunk.b(heightmap_type).b()));
        }

        nbttagcompound.set("Heightmaps", nbttagcompound1);
        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        WorldGenStage.Features[] aworldgenstage_features = WorldGenStage.Features.values();
        int l = aworldgenstage_features.length;

        for (int i1 = 0; i1 < l; ++i1) {
            WorldGenStage.Features worldgenstage_features = aworldgenstage_features[i1];

            nbttagcompound3.setByteArray(worldgenstage_features.toString(), protochunk.a(worldgenstage_features).toByteArray());
        }

        nbttagcompound.set("CarvingMasks", nbttagcompound3);
        nbttagcompound.set("Structures", this.a(i, j, protochunk.e(), protochunk.f()));
    }

    private void saveBody(Chunk chunk, World world, NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("xPos", chunk.locX);
        nbttagcompound.setInt("zPos", chunk.locZ);
        nbttagcompound.setLong("LastUpdate", world.getTime());
        nbttagcompound.setLong("InhabitedTime", chunk.m());
        nbttagcompound.setString("Status", chunk.i().b());
        ChunkConverter chunkconverter = chunk.F();

        if (!chunkconverter.a()) {
            nbttagcompound.set("UpgradeData", chunkconverter.b());
        }

        ChunkSection[] achunksection = chunk.getSections();
        NBTTagList nbttaglist = this.a(world, achunksection);

        nbttagcompound.set("Sections", nbttaglist);
        BiomeBase[] abiomebase = chunk.getBiomeIndex();
        int[] aint = new int[abiomebase.length];

        for (int i = 0; i < abiomebase.length; ++i) {
            aint[i] = IRegistry.BIOME.a((Object) abiomebase[i]);
        }

        nbttagcompound.setIntArray("Biomes", aint);
        chunk.f(false);
        NBTTagList nbttaglist1 = new NBTTagList();

        Iterator iterator;

        for (int j = 0; j < chunk.getEntitySlices().length; ++j) {
            iterator = chunk.getEntitySlices()[j].iterator();

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                if (entity.d(nbttagcompound1)) {
                    chunk.f(true);
                    nbttaglist1.add((NBTBase) nbttagcompound1);
                }
            }
        }

        nbttagcompound.set("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();

        iterator = chunk.t().iterator();

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();
            TileEntity tileentity = chunk.getTileEntity(blockposition);
            NBTTagCompound nbttagcompound2;

            if (tileentity != null) {
                nbttagcompound2 = new NBTTagCompound();
                tileentity.save(nbttagcompound2);
                nbttagcompound2.setBoolean("keepPacked", false);
                nbttaglist2.add((NBTBase) nbttagcompound2);
            } else {
                nbttagcompound2 = chunk.g(blockposition);
                if (nbttagcompound2 != null) {
                    nbttagcompound2.setBoolean("keepPacked", true);
                    nbttaglist2.add((NBTBase) nbttagcompound2);
                }
            }
        }

        nbttagcompound.set("TileEntities", nbttaglist2);
        if (world.getBlockTickList() instanceof TickListServer) {
            nbttagcompound.set("TileTicks", ((TickListServer) world.getBlockTickList()).a(chunk));
        }

        if (world.getFluidTickList() instanceof TickListServer) {
            nbttagcompound.set("LiquidTicks", ((TickListServer) world.getFluidTickList()).a(chunk));
        }

        nbttagcompound.set("PostProcessing", a(chunk.G()));
        if (chunk.k() instanceof ProtoChunkTickList) {
            nbttagcompound.set("ToBeTicked", ((ProtoChunkTickList) chunk.k()).a());
        }

        if (chunk.l() instanceof ProtoChunkTickList) {
            nbttagcompound.set("LiquidsToBeTicked", ((ProtoChunkTickList) chunk.l()).a());
        }

        NBTTagCompound nbttagcompound3 = new NBTTagCompound();
        Iterator iterator1 = chunk.A().iterator();

        while (iterator1.hasNext()) {
            HeightMap.Type heightmap_type = (HeightMap.Type) iterator1.next();

            if (heightmap_type.c() == HeightMap.Use.LIVE_WORLD) {
                nbttagcompound3.set(heightmap_type.b(), new NBTTagLongArray(chunk.b(heightmap_type).b()));
            }
        }

        nbttagcompound.set("Heightmaps", nbttagcompound3);
        nbttagcompound.set("Structures", this.a(chunk.locX, chunk.locZ, chunk.e(), chunk.f()));
    }

    private Chunk a(GeneratorAccess generatoraccess, NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("xPos");
        int j = nbttagcompound.getInt("zPos");
        BiomeBase[] abiomebase = new BiomeBase[256];
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        if (nbttagcompound.hasKeyOfType("Biomes", 11)) {
            int[] aint = nbttagcompound.getIntArray("Biomes");

            for (int k = 0; k < aint.length; ++k) {
                abiomebase[k] = (BiomeBase) IRegistry.BIOME.fromId(aint[k]);
                if (abiomebase[k] == null) {
                    abiomebase[k] = generatoraccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(blockposition_mutableblockposition.c((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
                }
            }
        } else {
            for (int l = 0; l < abiomebase.length; ++l) {
                abiomebase[l] = generatoraccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(blockposition_mutableblockposition.c((l & 15) + (i << 4), 0, (l >> 4 & 15) + (j << 4)), Biomes.PLAINS);
            }
        }

        ChunkConverter chunkconverter = nbttagcompound.hasKeyOfType("UpgradeData", 10) ? new ChunkConverter(nbttagcompound.getCompound("UpgradeData")) : ChunkConverter.a;
        ProtoChunkTickList<Block> protochunkticklist = new ProtoChunkTickList<>((block) -> {
            return block.getBlockData().isAir();
        }, IRegistry.BLOCK::getKey, IRegistry.BLOCK::getOrDefault, new ChunkCoordIntPair(i, j));
        ProtoChunkTickList<FluidType> protochunkticklist1 = new ProtoChunkTickList<>((fluidtype) -> {
            return fluidtype == FluidTypes.EMPTY;
        }, IRegistry.FLUID::getKey, IRegistry.FLUID::getOrDefault, new ChunkCoordIntPair(i, j));
        long i1 = nbttagcompound.getLong("InhabitedTime");
        Chunk chunk = new Chunk(generatoraccess.getMinecraftWorld(), i, j, abiomebase, chunkconverter, protochunkticklist, protochunkticklist1, i1);

        chunk.c(nbttagcompound.getString("Status"));
        NBTTagList nbttaglist = nbttagcompound.getList("Sections", 10);

        chunk.a(this.a((IWorldReader) generatoraccess, nbttaglist));
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Heightmaps");
        HeightMap.Type[] aheightmap_type = HeightMap.Type.values();
        int j1 = aheightmap_type.length;

        int k1;

        for (k1 = 0; k1 < j1; ++k1) {
            HeightMap.Type heightmap_type = aheightmap_type[k1];

            if (heightmap_type.c() == HeightMap.Use.LIVE_WORLD) {
                String s = heightmap_type.b();

                if (nbttagcompound1.hasKeyOfType(s, 12)) {
                    chunk.a(heightmap_type, nbttagcompound1.o(s));
                } else {
                    chunk.b(heightmap_type).a();
                }
            }
        }

        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("Structures");

        chunk.a(this.c(generatoraccess, nbttagcompound2));
        chunk.b(this.b(nbttagcompound2));
        NBTTagList nbttaglist1 = nbttagcompound.getList("PostProcessing", 9);

        for (k1 = 0; k1 < nbttaglist1.size(); ++k1) {
            NBTTagList nbttaglist2 = nbttaglist1.f(k1);

            for (int l1 = 0; l1 < nbttaglist2.size(); ++l1) {
                chunk.a(nbttaglist2.g(l1), k1);
            }
        }

        protochunkticklist.a(nbttagcompound.getList("ToBeTicked", 9));
        protochunkticklist1.a(nbttagcompound.getList("LiquidsToBeTicked", 9));
        if (nbttagcompound.getBoolean("shouldSave")) {
            chunk.a(true);
        }

        return chunk;
    }

    public void loadEntities(NBTTagCompound nbttagcompound, Chunk chunk) {
        NBTTagList nbttaglist = nbttagcompound.getList("Entities", 10);
        World world = chunk.getWorld();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);

            a(nbttagcompound1, world, chunk);
            chunk.f(true);
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("TileEntities", 10);

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            NBTTagCompound nbttagcompound2 = nbttaglist1.getCompound(j);
            boolean flag = nbttagcompound2.getBoolean("keepPacked");

            if (flag) {
                chunk.a(nbttagcompound2);
            } else {
                TileEntity tileentity = TileEntity.create(nbttagcompound2);

                if (tileentity != null) {
                    chunk.a(tileentity);
                }
            }
        }

        if (nbttagcompound.hasKeyOfType("TileTicks", 9) && world.getBlockTickList() instanceof TickListServer) {
            ((TickListServer) world.getBlockTickList()).a(nbttagcompound.getList("TileTicks", 10));
        }

        if (nbttagcompound.hasKeyOfType("LiquidTicks", 9) && world.getFluidTickList() instanceof TickListServer) {
            ((TickListServer) world.getFluidTickList()).a(nbttagcompound.getList("LiquidTicks", 10));
        }

    }

    private ProtoChunk b(GeneratorAccess generatoraccess, NBTTagCompound nbttagcompound) {
        int i = nbttagcompound.getInt("xPos");
        int j = nbttagcompound.getInt("zPos");
        BiomeBase[] abiomebase = new BiomeBase[256];
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        if (nbttagcompound.hasKeyOfType("Biomes", 11)) {
            int[] aint = nbttagcompound.getIntArray("Biomes");

            for (int k = 0; k < aint.length; ++k) {
                abiomebase[k] = (BiomeBase) IRegistry.BIOME.fromId(aint[k]);
                if (abiomebase[k] == null) {
                    abiomebase[k] = generatoraccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(blockposition_mutableblockposition.c((k & 15) + (i << 4), 0, (k >> 4 & 15) + (j << 4)), Biomes.PLAINS);
                }
            }
        } else {
            for (int l = 0; l < abiomebase.length; ++l) {
                abiomebase[l] = generatoraccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(blockposition_mutableblockposition.c((l & 15) + (i << 4), 0, (l >> 4 & 15) + (j << 4)), Biomes.PLAINS);
            }
        }

        ChunkConverter chunkconverter = nbttagcompound.hasKeyOfType("UpgradeData", 10) ? new ChunkConverter(nbttagcompound.getCompound("UpgradeData")) : ChunkConverter.a;
        ProtoChunk protochunk = new ProtoChunk(i, j, chunkconverter);

        protochunk.a(abiomebase);
        protochunk.b(nbttagcompound.getLong("InhabitedTime"));
        protochunk.c(nbttagcompound.getString("Status"));
        NBTTagList nbttaglist = nbttagcompound.getList("Sections", 10);

        protochunk.a(this.a((IWorldReader) generatoraccess, nbttaglist));
        NBTTagList nbttaglist1 = nbttagcompound.getList("Entities", 10);

        for (int i1 = 0; i1 < nbttaglist1.size(); ++i1) {
            protochunk.b(nbttaglist1.getCompound(i1));
        }

        NBTTagList nbttaglist2 = nbttagcompound.getList("TileEntities", 10);

        for (int j1 = 0; j1 < nbttaglist2.size(); ++j1) {
            NBTTagCompound nbttagcompound1 = nbttaglist2.getCompound(j1);

            protochunk.a(nbttagcompound1);
        }

        NBTTagList nbttaglist3 = nbttagcompound.getList("Lights", 9);

        for (int k1 = 0; k1 < nbttaglist3.size(); ++k1) {
            NBTTagList nbttaglist4 = nbttaglist3.f(k1);

            for (int l1 = 0; l1 < nbttaglist4.size(); ++l1) {
                protochunk.a(nbttaglist4.g(l1), k1);
            }
        }

        NBTTagList nbttaglist5 = nbttagcompound.getList("PostProcessing", 9);

        for (int i2 = 0; i2 < nbttaglist5.size(); ++i2) {
            NBTTagList nbttaglist6 = nbttaglist5.f(i2);

            for (int j2 = 0; j2 < nbttaglist6.size(); ++j2) {
                protochunk.b(nbttaglist6.g(j2), i2);
            }
        }

        protochunk.k().a(nbttagcompound.getList("ToBeTicked", 9));
        protochunk.l().a(nbttagcompound.getList("LiquidsToBeTicked", 9));
        NBTTagCompound nbttagcompound2 = nbttagcompound.getCompound("Heightmaps");
        Iterator iterator = nbttagcompound2.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            protochunk.a(HeightMap.Type.a(s), nbttagcompound2.o(s));
        }

        NBTTagCompound nbttagcompound3 = nbttagcompound.getCompound("Structures");

        protochunk.a(this.c(generatoraccess, nbttagcompound3));
        protochunk.b(this.b(nbttagcompound3));
        NBTTagCompound nbttagcompound4 = nbttagcompound.getCompound("CarvingMasks");
        Iterator iterator1 = nbttagcompound4.getKeys().iterator();

        while (iterator1.hasNext()) {
            String s1 = (String) iterator1.next();
            WorldGenStage.Features worldgenstage_features = WorldGenStage.Features.valueOf(s1);

            protochunk.a(worldgenstage_features, BitSet.valueOf(nbttagcompound4.getByteArray(s1)));
        }

        return protochunk;
    }

    private NBTTagList a(World world, ChunkSection[] achunksection) {
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = world.worldProvider.g();
        ChunkSection[] achunksection1 = achunksection;
        int i = achunksection.length;

        for (int j = 0; j < i; ++j) {
            ChunkSection chunksection = achunksection1[j];

            if (chunksection != Chunk.a) {
                NBTTagCompound nbttagcompound = new NBTTagCompound();

                nbttagcompound.setByte("Y", (byte) (chunksection.getYPosition() >> 4 & 255));
                chunksection.getBlocks().b(nbttagcompound, "Palette", "BlockStates");
                nbttagcompound.setByteArray("BlockLight", chunksection.getEmittedLightArray().asBytes());
                if (flag) {
                    nbttagcompound.setByteArray("SkyLight", chunksection.getSkyLightArray().asBytes());
                } else {
                    nbttagcompound.setByteArray("SkyLight", new byte[chunksection.getEmittedLightArray().asBytes().length]);
                }

                nbttaglist.add((NBTBase) nbttagcompound);
            }
        }

        return nbttaglist;
    }

    private ChunkSection[] a(IWorldReader iworldreader, NBTTagList nbttaglist) {
        boolean flag = true;
        ChunkSection[] achunksection = new ChunkSection[16];
        boolean flag1 = iworldreader.o().g();

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
            byte b0 = nbttagcompound.getByte("Y");
            ChunkSection chunksection = new ChunkSection(b0 << 4, flag1);

            chunksection.getBlocks().a(nbttagcompound, "Palette", "BlockStates");
            chunksection.a(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));
            if (flag1) {
                chunksection.b(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
            }

            chunksection.recalcBlockCounts();
            achunksection[b0] = chunksection;
        }

        return achunksection;
    }

    private NBTTagCompound a(int i, int j, Map<String, StructureStart> map, Map<String, LongSet> map1) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, StructureStart> entry = (Entry) iterator.next();

            nbttagcompound1.set((String) entry.getKey(), ((StructureStart) entry.getValue()).a(i, j));
        }

        nbttagcompound.set("Starts", nbttagcompound1);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        Iterator iterator1 = map1.entrySet().iterator();

        while (iterator1.hasNext()) {
            Entry<String, LongSet> entry1 = (Entry) iterator1.next();

            nbttagcompound2.set((String) entry1.getKey(), new NBTTagLongArray((LongSet) entry1.getValue()));
        }

        nbttagcompound.set("References", nbttagcompound2);
        return nbttagcompound;
    }

    private Map<String, StructureStart> c(GeneratorAccess generatoraccess, NBTTagCompound nbttagcompound) {
        Map<String, StructureStart> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("Starts");
        Iterator iterator = nbttagcompound1.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            map.put(s, WorldGenFactory.a(nbttagcompound1.getCompound(s), generatoraccess));
        }

        return map;
    }

    private Map<String, LongSet> b(NBTTagCompound nbttagcompound) {
        Map<String, LongSet> map = Maps.newHashMap();
        NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("References");
        Iterator iterator = nbttagcompound1.getKeys().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            map.put(s, new LongOpenHashSet(nbttagcompound1.o(s)));
        }

        return map;
    }

    public static NBTTagList a(ShortList[] ashortlist) {
        NBTTagList nbttaglist = new NBTTagList();
        ShortList[] ashortlist1 = ashortlist;
        int i = ashortlist.length;

        for (int j = 0; j < i; ++j) {
            ShortList shortlist = ashortlist1[j];
            NBTTagList nbttaglist1 = new NBTTagList();

            if (shortlist != null) {
                ShortListIterator shortlistiterator = shortlist.iterator();

                while (shortlistiterator.hasNext()) {
                    Short oshort = (Short) shortlistiterator.next();

                    nbttaglist1.add((NBTBase) (new NBTTagShort(oshort)));
                }
            }

            nbttaglist.add((NBTBase) nbttaglist1);
        }

        return nbttaglist;
    }

    @Nullable
    private static Entity a(NBTTagCompound nbttagcompound, World world, Function<Entity, Entity> function) {
        Entity entity = a(nbttagcompound, world);

        if (entity == null) {
            return null;
        } else {
            entity = (Entity) function.apply(entity);
            if (entity != null && nbttagcompound.hasKeyOfType("Passengers", 9)) {
                NBTTagList nbttaglist = nbttagcompound.getList("Passengers", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    Entity entity1 = a(nbttaglist.getCompound(i), world, function);

                    if (entity1 != null) {
                        entity1.a(entity, true);
                    }
                }
            }

            return entity;
        }
    }

    @Nullable
    public static Entity a(NBTTagCompound nbttagcompound, World world, Chunk chunk) {
        return a(nbttagcompound, world, (entity) -> {
            chunk.a(entity);
            return entity;
        });
    }

    @Nullable
    public static Entity a(NBTTagCompound nbttagcompound, World world, double d0, double d1, double d2, boolean flag) {
        return a(nbttagcompound, world, (entity) -> {
            entity.setPositionRotation(d0, d1, d2, entity.yaw, entity.pitch);
            return flag && !world.addEntity(entity) ? null : entity;
        });
    }

    @Nullable
    public static Entity a(NBTTagCompound nbttagcompound, World world, boolean flag) {
        return a(nbttagcompound, world, (entity) -> {
            return flag && !world.addEntity(entity) ? null : entity;
        });
    }

    @Nullable
    protected static Entity a(NBTTagCompound nbttagcompound, World world) {
        try {
            return EntityTypes.a(nbttagcompound, world);
        } catch (RuntimeException runtimeexception) {
            ChunkRegionLoader.a.warn("Exception loading entity: ", runtimeexception);
            return null;
        }
    }

    public static void a(Entity entity, GeneratorAccess generatoraccess) {
        if (generatoraccess.addEntity(entity) && entity.isVehicle()) {
            Iterator iterator = entity.bP().iterator();

            while (iterator.hasNext()) {
                Entity entity1 = (Entity) iterator.next();

                a(entity1, generatoraccess);
            }
        }

    }

    public boolean a(ChunkCoordIntPair chunkcoordintpair, DimensionManager dimensionmanager, PersistentCollection persistentcollection) {
        boolean flag = false;

        try {
            this.a(dimensionmanager, persistentcollection, chunkcoordintpair.x, chunkcoordintpair.z);

            while (this.a()) {
                flag = true;
            }
        } catch (IOException ioexception) {
            ;
        }

        return flag;
    }
}
