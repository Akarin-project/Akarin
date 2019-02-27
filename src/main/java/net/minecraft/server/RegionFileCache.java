package net.minecraft.server;

import com.google.common.collect.Maps;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;

public class RegionFileCache {

    public static final Map<File, RegionFile> cache = Maps.newHashMap();

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
                a();
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
            }
        }

        RegionFileCache.cache.clear();
    }

    @Nullable
    // CraftBukkit start - call sites hoisted for synchronization
    public static synchronized NBTTagCompound read(File file, int i, int j) throws IOException {
        RegionFile regionfile = a(file, i, j);

        DataInputStream datainputstream = regionfile.a(i & 31, j & 31);

        if (datainputstream == null) {
            return null;
        }

        return NBTCompressedStreamTools.a(datainputstream);
    }

    @Nullable
    public static synchronized void write(File file, int i, int j, NBTTagCompound nbttagcompound) throws IOException {
        RegionFile regionfile = a(file, i, j);

        DataOutputStream dataoutputstream = regionfile.c(i & 31, j & 31);
        NBTCompressedStreamTools.a(nbttagcompound, (java.io.DataOutput) dataoutputstream);
        dataoutputstream.close();
    }

    public static synchronized boolean chunkExists(File file, int i, int j) {
        RegionFile regionfile = b(file, i, j);

        return regionfile != null ? regionfile.d(i & 31, j & 31) : false;
    }
    // CraftBukkit end
}
