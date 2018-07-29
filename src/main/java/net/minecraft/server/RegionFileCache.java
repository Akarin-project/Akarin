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

        DataInputStream datainputstream = regionfile.a(i & 31, j & 31);

        if (datainputstream == null) {
            return null;
        }

        return NBTCompressedStreamTools.a(datainputstream);
    }

    @Nullable
    public static void write(File file, int i, int j, NBTTagCompound nbttagcompound) throws IOException {
        int attempts = 0; Exception laste = null; while (attempts++ < 5) { try { // Paper
        RegionFile regionfile = a(file, i, j);

        DataOutputStream dataoutputstream = regionfile.c(i & 31, j & 31);
        NBTCompressedStreamTools.a(nbttagcompound, (java.io.DataOutput) dataoutputstream);
        dataoutputstream.close();
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
