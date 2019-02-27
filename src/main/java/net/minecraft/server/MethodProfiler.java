package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MethodProfiler {

    public static final boolean ENABLED = Boolean.getBoolean("enableDebugMethodProfiler"); // CraftBukkit - disable unless specified in JVM arguments
    private static final Logger a = LogManager.getLogger();
    private final List<String> b = Lists.newArrayList();
    private final List<Long> c = Lists.newArrayList();
    private boolean d;
    private String e = "";
    private final Map<String, Long> f = Maps.newHashMap();
    private long g;
    private int h;

    public MethodProfiler() {}

    public boolean a() {
        return this.d;
    }

    public void b() {
        this.d = false;
    }

    public long c() {
        return this.g;
    }

    public int d() {
        return this.h;
    }

    public void a(int i) {
        if (!ENABLED) return; // CraftBukkit
        if (!this.d) {
            this.d = true;
            this.f.clear();
            this.e = "";
            this.b.clear();
            this.h = i;
            this.g = SystemUtils.getMonotonicNanos();
        }
    }

    public void enter(String s) {
        if (!ENABLED) return; // CraftBukkit
        if (this.d) {
            if (!this.e.isEmpty()) {
                this.e = this.e + ".";
            }

            this.e = this.e + s;
            this.b.add(this.e);
            this.c.add(SystemUtils.getMonotonicNanos());
        }
    }

    public void a(Supplier<String> supplier) {
        if (!ENABLED) return; // CraftBukkit
        if (this.d) {
            this.enter((String) supplier.get());
        }
    }

    public void exit() {
        if (!ENABLED) return; // CraftBukkit
        if (this.d && !this.c.isEmpty()) {
            long i = SystemUtils.getMonotonicNanos();
            long j = (Long) this.c.remove(this.c.size() - 1);

            this.b.remove(this.b.size() - 1);
            long k = i - j;

            if (this.f.containsKey(this.e)) {
                this.f.put(this.e, (Long) this.f.get(this.e) + k);
            } else {
                this.f.put(this.e, k);
            }

            if (k > 100000000L) {
                MethodProfiler.a.warn("Something's taking too long! '{}' took aprox {} ms", this.e, (double) k / 1000000.0D);
            }

            this.e = this.b.isEmpty() ? "" : (String) this.b.get(this.b.size() - 1);
        }
    }

    public List<MethodProfiler.ProfilerInfo> b(String s) {
        if (!ENABLED) return Collections.emptyList(); // CraftBukkit
        long i = this.f.containsKey("root") ? (Long) this.f.get("root") : 0L;
        long j = this.f.containsKey(s) ? (Long) this.f.get(s) : -1L;
        List<MethodProfiler.ProfilerInfo> list = Lists.newArrayList();

        if (!s.isEmpty()) {
            s = s + ".";
        }

        long k = 0L;
        Iterator iterator = this.f.keySet().iterator();

        while (iterator.hasNext()) {
            String s1 = (String) iterator.next();

            if (s1.length() > s.length() && s1.startsWith(s) && s1.indexOf(".", s.length() + 1) < 0) {
                k += (Long) this.f.get(s1);
            }
        }

        float f = (float) k;

        if (k < j) {
            k = j;
        }

        if (i < k) {
            i = k;
        }

        Iterator iterator1 = this.f.keySet().iterator();

        String s2;

        while (iterator1.hasNext()) {
            s2 = (String) iterator1.next();
            if (s2.length() > s.length() && s2.startsWith(s) && s2.indexOf(".", s.length() + 1) < 0) {
                long l = (Long) this.f.get(s2);
                double d0 = (double) l * 100.0D / (double) k;
                double d1 = (double) l * 100.0D / (double) i;
                String s3 = s2.substring(s.length());

                list.add(new MethodProfiler.ProfilerInfo(s3, d0, d1));
            }
        }

        iterator1 = this.f.keySet().iterator();

        while (iterator1.hasNext()) {
            s2 = (String) iterator1.next();
            this.f.put(s2, (Long) this.f.get(s2) * 999L / 1000L);
        }

        if ((float) k > f) {
            list.add(new MethodProfiler.ProfilerInfo("unspecified", (double) ((float) k - f) * 100.0D / (double) k, (double) ((float) k - f) * 100.0D / (double) i));
        }

        Collections.sort(list);
        list.add(0, new MethodProfiler.ProfilerInfo(s, 100.0D, (double) k * 100.0D / (double) i));
        return list;
    }

    public void exitEnter(String s) {
        if (!ENABLED) return; // CraftBukkit
        this.exit();
        this.enter(s);
    }

    public String f() {
        if (!ENABLED) return "[DISABLED]"; // CraftBukkit
        return this.b.isEmpty() ? "[UNKNOWN]" : (String) this.b.get(this.b.size() - 1);
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

        public int compareTo(MethodProfiler.ProfilerInfo methodprofiler_profilerinfo) {
            return methodprofiler_profilerinfo.a < this.a ? -1 : (methodprofiler_profilerinfo.a > this.a ? 1 : methodprofiler_profilerinfo.c.compareTo(this.c));
        }
    }
}
