package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;


import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MethodProfiler {

    public static final boolean ENABLED = Boolean.getBoolean("enableDebugMethodProfiler"); // CraftBukkit - disable unless specified in JVM arguments
    private static final Logger b = LogManager.getLogger();
    private final ObjectArrayList<String> c = new ObjectArrayList<>(); // Dionysus
    private final LongArrayList d = new LongArrayList(); // Dionysus
    public boolean a;
    private String e = "";
    private final Object2LongOpenHashMap<String> f = new Object2LongOpenHashMap<>();

    public MethodProfiler() {}

    public void a() {
        if (!ENABLED) return;  // CraftBukkit
        this.f.clear();
        this.e = "";
        this.c.clear();
    }

    public void a(String s) {
        if (!ENABLED) return;  // CraftBukkit
        if (this.a) {
            if (!this.e.isEmpty()) {
                this.e = this.e + ".";
            }

            this.e = this.e + s;
            this.c.add(this.e);
            this.d.add(Long.valueOf(System.nanoTime()));
        }
    }

    public void a(Supplier<String> supplier) {
        if (!ENABLED) return;  // CraftBukkit
        if (this.a) {
            this.a((String) supplier.get());
        }
    }

    public void b() {
        if (!ENABLED) return;  // CraftBukkit
        if (this.a) {
            long i = System.nanoTime();
            long j = this.d.removeLong(this.d.size() - 1);

            this.c.remove(this.c.size() - 1);
            long k = i - j;

            if (this.f.containsKey(this.e)) {
                this.f.put(this.e, this.f.get(this.e) + k);
            } else {
                this.f.put(this.e, k);
            }

            if (k > 100000000L) {
                MethodProfiler.b.warn("Something\'s taking too long! \'{}\' took aprox {} ms", this.e, Double.valueOf((double) k / 1000000.0D));
            }

            this.e = this.c.isEmpty() ? "" : (String) this.c.get(this.c.size() - 1);
        }
    }

    public List<MethodProfiler.ProfilerInfo> b(String s) {
        if (!ENABLED || !this.a) {  // CraftBukkit
            return Collections.emptyList();
        } else {
            long i = this.f.getOrDefault("root", 0L);
            long j = this.f.getOrDefault(s, -1L);
            ArrayList<MethodProfiler.ProfilerInfo> arraylist = Lists.newArrayList();

            if (!s.isEmpty()) {
                s = s + ".";
            }

            long k = 0L;
            for (String s1 : this.f.keySet()) {
                if (s1.length() > s.length() && s1.startsWith(s) && s1.indexOf(".", s.length() + 1) < 0) {
                    k += this.f.getLong(s1);
                }
            }

            float f = (float) k;

            if (k < j) {
                k = j;
            }

            if (i < k) {
                i = k;
            }

            for (Object2LongMap.Entry<String> entry : this.f.object2LongEntrySet()) {
                String s2 = entry.getKey();
                if (s2.length() > s.length() && s2.startsWith(s) && s2.indexOf(".", s.length() + 1) < 0) {
                    long l = this.f.getLong(s2);
                    double d0 = (double) l * 100.0D / (double) k;
                    double d1 = (double) l * 100.0D / (double) i;
                    String s3 = s2.substring(s.length());

                    arraylist.add(new MethodProfiler.ProfilerInfo(s3, d0, d1));
                }
                entry.setValue(entry.getLongValue() * 999L / 1000L);
            }

            if ((float) k > f) {
                arraylist.add(new MethodProfiler.ProfilerInfo("unspecified", (double) ((float) k - f) * 100.0D / (double) k, (double) ((float) k - f) * 100.0D / (double) i));
            }

            Collections.sort(arraylist);
            arraylist.add(0, new MethodProfiler.ProfilerInfo(s, 100.0D, (double) k * 100.0D / (double) i));
            return arraylist;
        }
    }

    public void c(String s) {
        if (!ENABLED) return;  // CraftBukkit
        this.b();
        this.a(s);
    }

    public String c() {
        if (!ENABLED) return "[DISABLED]";  // CraftBukkit
        return this.c.isEmpty() ? "[UNKNOWN]" : (String) this.c.get(this.c.size() - 1);
    }

    public static final class ProfilerInfo implements Comparable<MethodProfiler.ProfilerInfo> {

        public double a;
        public double b;
        public String c;

        public ProfilerInfo(String s, double d0, double d1) {
            this.c = s;
            this.a = d0;
            this.b = d1;
        }

        public int a(MethodProfiler.ProfilerInfo methodprofiler_profilerinfo) {
            return methodprofiler_profilerinfo.a < this.a ? -1 : (methodprofiler_profilerinfo.a > this.a ? 1 : methodprofiler_profilerinfo.c.compareTo(this.c));
        }

        public int compareTo(MethodProfiler.ProfilerInfo object) { // CraftBukkit: decompile error
            return this.a((MethodProfiler.ProfilerInfo) object);
        }
    }
}
