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
    public static DataInputStream read(File file, int i, int j) {
        RegionFile regionfile = a(file, i, j);

        return regionfile.a(i & 31, j & 31);
    }

    @Nullable
    public static DataOutputStream write(File file, int i, int j) {
        RegionFile regionfile = a(file, i, j);

        return regionfile.c(i & 31, j & 31);
    }
}
