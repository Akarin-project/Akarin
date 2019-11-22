package net.minecraft.server;

import it.unimi.dsi.fastutil.longs.Long2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nullable;
import com.destroystokyo.paper.PaperConfig; // Paper

import org.apache.logging.log4j.LogManager;

public abstract class RegionFileCache implements AutoCloseable {

    public final Long2ObjectLinkedOpenHashMap<RegionFile> cache = new Long2ObjectLinkedOpenHashMap();
    private final File a;
    // Paper start
    private final File templateWorld;
    private final File actualWorld;
    private boolean useAltWorld;
    // Paper end


    protected RegionFileCache(File file) {
        this.a = file;
        // Paper end

        this.actualWorld = file;
        if (com.destroystokyo.paper.PaperConfig.useVersionedWorld) {
            this.useAltWorld = true;
            String name = file.getName();
            File container = file.getParentFile().getParentFile();
            if (name.equals("DIM-1") || name.equals("DIM1")) {
                container = container.getParentFile();
            }
            this.templateWorld = new File(container, name);
            File region = new File(file, "region");
            if (!region.exists()) {
                region.mkdirs();
            }
        } else {
            this.useAltWorld = false;
            this.templateWorld = file;
        }
        // Paper start
    }

    // Paper start
    public synchronized RegionFile getRegionFileIfLoaded(ChunkCoordIntPair chunkcoordintpair) { // Paper - synchronize for async io
        return this.cache.getAndMoveToFirst(ChunkCoordIntPair.pair(chunkcoordintpair.getRegionX(), chunkcoordintpair.getRegionZ()));
    }
    // Paper end

    public RegionFile getRegionFile(ChunkCoordIntPair chunkcoordintpair, boolean existingOnly) throws IOException { return this.a(chunkcoordintpair, existingOnly); } // Paper - OBFHELPER
    private synchronized RegionFile a(ChunkCoordIntPair chunkcoordintpair, boolean existingOnly) throws IOException { // CraftBukkit // Paper - synchronize for async io
        long i = ChunkCoordIntPair.pair(chunkcoordintpair.getRegionX(), chunkcoordintpair.getRegionZ());
        RegionFile regionfile = (RegionFile) this.cache.getAndMoveToFirst(i);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (this.cache.size() >= PaperConfig.regionFileCacheSize) { // Paper - configurable
                ((RegionFile) this.cache.removeLast()).close();
            }

            if (!this.a.exists()) {
                this.a.mkdirs();
            }

            copyIfNeeded(chunkcoordintpair.x, chunkcoordintpair.z); // Paper
            File file = new File(this.a, "r." + chunkcoordintpair.getRegionX() + "." + chunkcoordintpair.getRegionZ() + ".mca");
            if (existingOnly && !file.exists()) return null; // CraftBukkit
            RegionFile regionfile1 = new RegionFile(file);

            this.cache.putAndMoveToFirst(i, regionfile1);
            return regionfile1;
        }
    }

    public static File getRegionFileName(File file, int i, int j) {
        File file1 = new File(file, "region");
        return new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
    }
    public synchronized boolean hasRegionFile(File file, int i, int j) {
        return cache.containsKey(ChunkCoordIntPair.pair(i, j));
    }
    // Paper start
    private static void printOversizedLog(String msg, File file, int x, int z) {
        org.apache.logging.log4j.LogManager.getLogger().fatal(msg + " (" + file.toString().replaceAll(".+[\\\\/]", "") + " - " + x + "," + z + ") Go clean it up to remove this message. /minecraft:tp " + (x<<4)+" 128 "+(z<<4) + " - DO NOT REPORT THIS TO PAPER - You may ask for help on Discord, but do not file an issue. These error messages can not be removed.");
    }

    private static final int DEFAULT_SIZE_THRESHOLD = 1024 * 8;
    private static final int OVERZEALOUS_TOTAL_THRESHOLD = 1024 * 64;
    private static final int OVERZEALOUS_THRESHOLD = 1024;
    private static int SIZE_THRESHOLD = DEFAULT_SIZE_THRESHOLD;
    private static void resetFilterThresholds() {
        SIZE_THRESHOLD = Math.max(1024 * 4, Integer.getInteger("Paper.FilterThreshhold", DEFAULT_SIZE_THRESHOLD));
    }
    static {
        resetFilterThresholds();
    }

    static boolean isOverzealous() {
        return SIZE_THRESHOLD == OVERZEALOUS_THRESHOLD;
    }

    private void writeRegion(ChunkCoordIntPair chunk, NBTTagCompound nbttagcompound) throws IOException {
        RegionFile regionfile = getRegionFile(chunk, false);

        int chunkX = chunk.x;
        int chunkZ = chunk.z;

        DataOutputStream out = regionfile.getWriteStream(chunk);
        try {
            NBTCompressedStreamTools.writeNBT(nbttagcompound, out);
            out.close();
            regionfile.setStatus(chunk.x, chunk.z, ChunkRegionLoader.getStatus(nbttagcompound)); // Paper - cache status on disk
            regionfile.setOversized(chunkX, chunkZ, false);
        } catch (RegionFile.ChunkTooLargeException ignored) {
            printOversizedLog("ChunkTooLarge! Someone is trying to duplicate.", regionfile.file, chunkX, chunkZ);
            // Clone as we are now modifying it, don't want to corrupt the pending save state
            nbttagcompound = nbttagcompound.clone();
            // Filter out TileEntities and Entities
            NBTTagCompound oversizedData = filterChunkData(nbttagcompound);
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (regionfile) {
                out = regionfile.getWriteStream(chunk);
                NBTCompressedStreamTools.writeNBT(nbttagcompound, out);
                try {
                    out.close();
                    // 2048 is below the min allowed, so it means we enter overzealous mode below
                    if (SIZE_THRESHOLD == OVERZEALOUS_THRESHOLD) {
                        resetFilterThresholds();
                    }
                    regionfile.setStatus(chunk.x, chunk.z, ChunkRegionLoader.getStatus(nbttagcompound)); // Paper - cache status on disk
                } catch (RegionFile.ChunkTooLargeException e) {
                    printOversizedLog("ChunkTooLarge even after reduction. Trying in overzealous mode.", regionfile.file, chunkX, chunkZ);
                    // Eek, major fail. We have retry logic, so reduce threshholds and fall back
                    SIZE_THRESHOLD = OVERZEALOUS_THRESHOLD;
                    throw e;
                }

                regionfile.writeOversizedData(chunkX, chunkZ, oversizedData);
            }
        }
    }

    private static NBTTagCompound filterChunkData(NBTTagCompound chunk) {
        NBTTagCompound oversizedLevel = new NBTTagCompound();
        NBTTagCompound level = chunk.getCompound("Level");
        filterChunkList(level, oversizedLevel, "Entities");
        filterChunkList(level, oversizedLevel, "TileEntities");
        NBTTagCompound oversized = new NBTTagCompound();
        oversized.set("Level", oversizedLevel);
        return oversized;
    }

    private static void filterChunkList(NBTTagCompound level, NBTTagCompound extra, String key) {
        NBTTagList list = level.getList(key, 10);
        NBTTagList newList = extra.getList(key, 10);
        int totalSize = 0;
        for (java.util.Iterator<NBTBase> iterator = list.list.iterator(); iterator.hasNext();) {
            NBTBase object = iterator.next();
            int nbtSize = getNBTSize(object);
            if (nbtSize > SIZE_THRESHOLD || (SIZE_THRESHOLD == OVERZEALOUS_THRESHOLD && totalSize > OVERZEALOUS_TOTAL_THRESHOLD)) {
                newList.add(object);
                iterator.remove();
            } else  {
                totalSize += nbtSize;
            }
        }
        level.set(key, list);
        extra.set(key, newList);
    }


    private static NBTTagCompound readOversizedChunk(RegionFile regionfile, ChunkCoordIntPair chunkCoordinate) throws IOException {
        synchronized (regionfile) {
            try (DataInputStream datainputstream = regionfile.getReadStream(chunkCoordinate)) {
                // Paper start - Handle bad chunks more gracefully - also handle similarly with oversized data
                NBTTagCompound oversizedData = null;

                try {
                    oversizedData = regionfile.getOversizedData(chunkCoordinate.x, chunkCoordinate.z);
                } catch (Exception ex) {}

                NBTTagCompound chunk;

                try {
                    chunk = NBTCompressedStreamTools.readNBT(datainputstream);
                } catch (final Exception ex) {
                    return null;
                }
                // Paper end
                if (oversizedData == null) {
                    return chunk;
                }
                NBTTagCompound oversizedLevel = oversizedData.getCompound("Level");
                NBTTagCompound level = chunk.getCompound("Level");

                mergeChunkList(level, oversizedLevel, "Entities");
                mergeChunkList(level, oversizedLevel, "TileEntities");

                chunk.set("Level", level);

                return chunk;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw throwable;
            }
        }
    }

    private static void mergeChunkList(NBTTagCompound level, NBTTagCompound oversizedLevel, String key) {
        NBTTagList levelList = level.getList(key, 10);
        NBTTagList oversizedList = oversizedLevel.getList(key, 10);

        if (!oversizedList.isEmpty()) {
            levelList.addAll(oversizedList);
            level.set(key, levelList);
        }
    }

    private static int getNBTSize(NBTBase nbtBase) {
        DataOutputStream test = new DataOutputStream(new org.apache.commons.io.output.NullOutputStream());
        try {
            nbtBase.write(test);
            return test.size();
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    // Paper End

    @Nullable
    public NBTTagCompound read(ChunkCoordIntPair chunkcoordintpair) throws IOException {
        RegionFile regionfile = this.a(chunkcoordintpair, false); // CraftBukkit
        DataInputStream datainputstream = regionfile.a(chunkcoordintpair);
        // Paper start
        if (regionfile.isOversized(chunkcoordintpair.x, chunkcoordintpair.z)) {
            printOversizedLog("Loading Oversized Chunk!", regionfile.file, chunkcoordintpair.x, chunkcoordintpair.z);
            return readOversizedChunk(regionfile, chunkcoordintpair);
        }
        // Paper end
        Throwable throwable = null;

        NBTTagCompound nbttagcompound;

        try {
            if (datainputstream != null) {
                // Paper start - Handle bad chunks more gracefully
                try {
                    return NBTCompressedStreamTools.a(datainputstream);
                } catch (Exception ex) {
                    return null;
                }
                // Paper end
            }

            nbttagcompound = null;
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (datainputstream != null) {
                if (throwable != null) {
                    try {
                        datainputstream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    datainputstream.close();
                }
            }

        }

        return nbttagcompound;
    }

    protected void write(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) throws IOException {
        int attempts = 0; Exception laste = null; while (attempts++ < 5) { try { // Paper
        // Paper start
        this.writeRegion(chunkcoordintpair, nbttagcompound);
//        RegionFile regionfile = this.a(chunkcoordintpair, false); // CraftBukkit
//        DataOutputStream dataoutputstream = regionfile.c(chunkcoordintpair);
//        Throwable throwable = null;
//
//        try {
//            NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
//        } catch (Throwable throwable1) {
//            throwable = throwable1;
//            throw throwable1;
//        } finally {
//            if (dataoutputstream != null) {
//                if (throwable != null) {
//                    try {
//                        dataoutputstream.close();
//                    } catch (Throwable throwable2) {
//                        throwable.addSuppressed(throwable2);
//                    }
//                } else {
//                    dataoutputstream.close();
//                }
//            }
//
//        }
        // Paper end

            // Paper start
            return;
        } catch (Exception ex)  {
            laste = ex;
        }
        }

        if (laste != null) {
            com.destroystokyo.paper.exception.ServerInternalException.reportInternalException(laste);
            MinecraftServer.LOGGER.error("Failed to save chunk", laste);
        }
        // Paper end
    }

    public void close() throws IOException {
        ObjectIterator objectiterator = this.cache.values().iterator();

        while (objectiterator.hasNext()) {
            RegionFile regionfile = (RegionFile) objectiterator.next();

            regionfile.close();
        }

    }

    // CraftBukkit start
    public synchronized boolean chunkExists(ChunkCoordIntPair pos) throws IOException { // Paper - synchronize
        copyIfNeeded(pos.x, pos.z); // Paper
        RegionFile regionfile = a(pos, true);

        return regionfile != null ? regionfile.d(pos) : false;
    }
    // CraftBukkit end

    private void copyIfNeeded(int x, int z) {
        if (!useAltWorld) {
            return;
        }
        synchronized (RegionFileCache.class) {
            if (hasRegionFile(this.actualWorld, x, z)) {
                return;
            }
            File actual = RegionFileCache.getRegionFileName(this.actualWorld, x, z);
            File template = RegionFileCache.getRegionFileName(this.templateWorld, x, z);
            if (!actual.exists() && template.exists()) {
                try {
                    net.minecraft.server.MinecraftServer.LOGGER.info("Copying" + template + " to " + actual);
                    java.nio.file.Files.copy(template.toPath(), actual.toPath(), java.nio.file.StandardCopyOption.COPY_ATTRIBUTES);
                } catch (IOException e1) {
                    LogManager.getLogger().error("Error copying " + template + " to " + actual, e1);
                    MinecraftServer.getServer().safeShutdown(false);
                    com.destroystokyo.paper.util.SneakyThrow.sneaky(e1);
                }
            }
        }
    }
}
