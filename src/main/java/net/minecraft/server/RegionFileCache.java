package net.minecraft.server;

import com.destroystokyo.paper.exception.ServerInternalException;
import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import com.destroystokyo.paper.PaperConfig; // Paper
import java.util.LinkedHashMap; // Paper

public class RegionFileCache {

    public static final Map<File, RegionFile> cache = new LinkedHashMap(PaperConfig.regionFileCacheSize, 0.75f, true); // Paper - HashMap -> LinkedHashMap

    public static synchronized RegionFile getRegionFile(File file, int i, int j) { return a(file, i, j); } // Paper - OBFHELPER
    public static synchronized RegionFile a(File file, int i, int j) {
        File file1 = new File(file, "region");
        File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
        RegionFile regionfile = (RegionFile) RegionFileCache.cache.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else {
            if (!file1.exists()) {
                file1.mkdirs();
            }

            if (RegionFileCache.cache.size() >= 256) {
                trimCache();
            }

            RegionFile regionfile1 = new RegionFile(file2);

            RegionFileCache.cache.put(file2, regionfile1);
            return regionfile1;
        }
    }

    // CraftBukkit start
    public static synchronized RegionFile b(File file, int i, int j) {
        File file1 = new File(file, "region");
        File file2 = new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
        RegionFile regionfile = (RegionFile) RegionFileCache.cache.get(file2);

        if (regionfile != null) {
            return regionfile;
        } else if (file1.exists() && file2.exists()) {
            if (RegionFileCache.cache.size() >= 256) {
                a();
            }

            RegionFile regionfile1 = new RegionFile(file2);

            RegionFileCache.cache.put(file2, regionfile1);
            return regionfile1;
        } else {
            return null;
        }
    }
    // CraftBukkit end

    // Paper Start
    private static synchronized void trimCache() {
        Iterator<Map.Entry<File, RegionFile>> itr = RegionFileCache.cache.entrySet().iterator();
        int count = RegionFileCache.cache.size() - PaperConfig.regionFileCacheSize;
        while (count-- >= 0 && itr.hasNext()) {
            try {
                itr.next().getValue().close();
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                ServerInternalException.reportInternalException(ioexception);
            }
            itr.remove();
        }
    }
    public static synchronized File getRegionFileName(File file, int i, int j) {
        File file1 = new File(file, "region");
        return new File(file1, "r." + (i >> 5) + "." + (j >> 5) + ".mca");
    }
    public static synchronized boolean hasRegionFile(File file, int i, int j) {
        return RegionFileCache.cache.containsKey(getRegionFileName(file, i, j));
    }
    private static void printOversizedLog(String msg, File file, int x, int z) {
        org.apache.logging.log4j.LogManager.getLogger().fatal(msg + " (" + file.toString().replaceAll(".+[\\\\/]", "") + " - " + x + "," + z + ") Go clean it up to remove this message. /minecraft:tp " + (x<<4)+" 128 "+(z<<4) + " - DO NOT REPORT THIS TO PAPER - You may ask for help on Discord, but do not file an issue. These error messages can not be removed.");
    }

    private static final int DEFAULT_SIZE_THRESHOLD = 1024 * 8;
    private static final int OVERZEALOUS_THRESHOLD = 1024 * 2;
    private static int SIZE_THRESHOLD = DEFAULT_SIZE_THRESHOLD;
    private static void resetFilterThresholds() {
        SIZE_THRESHOLD = Math.max(1024 * 4, Integer.getInteger("Paper.FilterThreshhold", DEFAULT_SIZE_THRESHOLD));
    }
    static {
        resetFilterThresholds();
    }
    private static void writeRegion(File file, int x, int z, NBTTagCompound nbttagcompound) throws IOException {
        RegionFile regionfile = getRegionFile(file, x, z);

        DataOutputStream out = regionfile.getWriteStream(x & 31, z & 31);
        try {
            NBTCompressedStreamTools.writeNBT(nbttagcompound, out);
            out.close();
            regionfile.setOversized(x, z, false);
        } catch (RegionFile.ChunkTooLargeException ignored) {
            printOversizedLog("ChunkTooLarge! Someone is trying to duplicate.", file, x, z);
            // Clone as we are now modifying it, don't want to corrupt the pending save state
            nbttagcompound = nbttagcompound.clone();
            // Filter out TileEntities and Entities
            NBTTagCompound oversizedData = filterChunkData(nbttagcompound);
            //noinspection SynchronizationOnLocalVariableOrMethodParameter
            synchronized (regionfile) {
                out = regionfile.getWriteStream(x & 31, z & 31);
                NBTCompressedStreamTools.writeNBT(nbttagcompound, out);
                try {
                    out.close();
                    // 2048 is below the min allowed, so it means we enter overzealous mode below
                    if (SIZE_THRESHOLD == OVERZEALOUS_THRESHOLD) {
                        resetFilterThresholds();
                    }
                } catch (RegionFile.ChunkTooLargeException e) {
                    printOversizedLog("ChunkTooLarge even after reduction. Trying in overzealous mode.", file, x, z);
                    // Eek, major fail. We have retry logic, so reduce threshholds and fall back
                    SIZE_THRESHOLD = OVERZEALOUS_THRESHOLD;
                    throw e;
                }

                regionfile.writeOversizedData(x, z, oversizedData);
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
        for (Iterator<NBTBase> iterator = list.list.iterator(); iterator.hasNext(); ) {
            NBTBase object = iterator.next();
            if (getNBTSize(object) > SIZE_THRESHOLD) {
                newList.add(object);
                iterator.remove();
            }
        }
        level.set(key, list);
        extra.set(key, newList);
    }


    private static NBTTagCompound readOversizedChunk(RegionFile regionfile, int i, int j) throws IOException {
        synchronized (regionfile) {
            try (DataInputStream datainputstream = regionfile.getReadStream(i & 31, j & 31)) {
                NBTTagCompound oversizedData = regionfile.getOversizedData(i, j);
                NBTTagCompound chunk = NBTCompressedStreamTools.readNBT(datainputstream);
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

    public static synchronized void a() {
        Iterator iterator = RegionFileCache.cache.values().iterator();

        while (iterator.hasNext()) {
            RegionFile regionfile = (RegionFile) iterator.next();

            try {
                if (regionfile != null) {
                    regionfile.close();
                }
            } catch (IOException ioexception) {
                ioexception.printStackTrace();
                ServerInternalException.reportInternalException(ioexception); // Paper
            }
        }

        RegionFileCache.cache.clear();
    }

    @Nullable
    // CraftBukkit start - call sites hoisted for synchronization
    public static NBTTagCompound read(File file, int i, int j) throws IOException { // Paper - remove synchronization
        RegionFile regionfile = a(file, i, j);
        // Paper start
        if (regionfile.isOversized(i, j)) {
            printOversizedLog("Loading Oversized Chunk!", file, i, j);
            return readOversizedChunk(regionfile, i, j);
        }
        // Paper end

        DataInputStream datainputstream = regionfile.a(i & 31, j & 31);

        if (datainputstream == null) {
            return null;
        }

        return NBTCompressedStreamTools.a(datainputstream);
    }

    @Nullable
    public static void write(File file, int i, int j, NBTTagCompound nbttagcompound) throws IOException {
        int attempts = 0; Exception laste = null; while (attempts++ < 5) { try { // Paper
        writeRegion(file, i, j, nbttagcompound); // Paper - moved to own method
        // Paper start
//      RegionFile regionfile = a(file, i, j);
//
//      DataOutputStream dataoutputstream = regionfile.c(i & 31, j & 31);
//      NBTCompressedStreamTools.a(nbttagcompound, (java.io.DataOutput) dataoutputstream);
//      dataoutputstream.close();
        // Paper end
        // Paper start
            laste = null; break; // Paper
            } catch (Exception exception) {
                //ChunkRegionLoader.a.error("Failed to save chunk", exception); // Paper
                laste = exception; // Paper
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (laste != null) {
            com.destroystokyo.paper.exception.ServerInternalException.reportInternalException(laste);
            MinecraftServer.LOGGER.error("Failed to save chunk", laste);
        }
        // Paper end
    }

    public static boolean chunkExists(File file, int i, int j) { // Paper - remove synchronization
        RegionFile regionfile = b(file, i, j);

        return regionfile != null ? regionfile.d(i & 31, j & 31) : false;
    }
    // CraftBukkit end
}
