package net.minecraft.server;

// NeonPaper start
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.List;
// NeonPaper end

public class IntCache {

    private static int a = 256;
    // NeonPaper start - Refactored IntCache to be thread local instead of static
    private static final ThreadLocal<IntCache> caches = new ThreadLocal<IntCache>() {
        @Override
        protected IntCache initialValue() {
            IntCache cache = new IntCache();
            synchronized (ALL_CACHES) {
                ALL_CACHES.add(new WeakReference<>(cache));
            }
            return new IntCache();
        }
    };

    private static final List<WeakReference<IntCache>> ALL_CACHES = new ObjectArrayList<>();

    private int a = 256;
    private final List<int[]> b = new ObjectArrayList<>();
    private final List<int[]> c = new ObjectArrayList<>();
    private final List<int[]> d = new ObjectArrayList<>();
    private final List<int[]> e = new ObjectArrayList<>();

    private final int cacheLimit = org.spigotmc.SpigotConfig.intCacheLimit;

    public static int[] a(int i) {
        return caches.get().aNonStatic(i);
    }

    public int[] aNonStatic(int i) {
        int[] aint;

        if (i <= 256) {
            if (this.b.isEmpty()) {
                aint = new int[256];
				if (c.size() < cacheLimit) this.c.add(aint);
                return aint;
            } else {
                aint = this.b.remove(this.b.size() - 1);
                if (c.size() < cacheLimit) this.c.add(aint);
                return aint;
            }
        } else if (i > this.a) {
            this.a = i;
            this.d.clear();
            this.e.clear();
            aint = new int[this.a];
            if (e.size() < cacheLimit) this.e.add(aint);
            return aint;
        } else if (this.d.isEmpty()) {
            aint = new int[this.a];
            if (e.size() < cacheLimit) this.e.add(aint);
            return aint;
        } else {
            aint = this.d.remove(this.d.size() - 1);
            if (e.size() < cacheLimit) this.e.add(aint);
            return aint;
        }
    }

    public static void a() {
        caches.get().aNonStatic();
    }

    public void aNonStatic() {
        if (!this.d.isEmpty()) {
            this.d.remove(this.d.size() - 1);
        }

        if (!this.b.isEmpty()) {
            this.b.remove(this.b.size() - 1);
        }

        this.d.addAll(this.e);
        this.b.addAll(this.c);
        this.e.clear();
        this.c.clear();
    }

    public static String b() {
        int cache = 0;
        int tcache = 0;
        int allocated = 0;
        int tallocated = 0;
        int numberOfCaches;

        synchronized (ALL_CACHES) {
            numberOfCaches = ALL_CACHES.size();
            Iterator<WeakReference<IntCache>> iter = ALL_CACHES.iterator();
            while (iter.hasNext()) {
                WeakReference<IntCache> reference = iter.next();
                IntCache intcache = reference.get();
                if (intcache != null) {
                    cache += intcache.d.size();
                    tcache += intcache.b.size();
                    allocated += intcache.e.size();
                    tallocated += intcache.c.size();
                } else {
                    iter.remove();
                }
            }
        }
        return numberOfCaches + " IntCaches. In Total => cache: " + cache + ", tcache: " + tcache + ", allocated: " + allocated + ", tallocated: " + tallocated;
     }
// NeonPaper end
    }
}
