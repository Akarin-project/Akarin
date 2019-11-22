package net.minecraft.server;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import it.unimi.dsi.fastutil.Hash.Strategy;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SystemUtils {

    private static final AtomicInteger b = new AtomicInteger(1);
    private static final ExecutorService c = k();
    public static LongSupplier a = System::nanoTime;
    private static final Logger LOGGER = LogManager.getLogger();

    public static <K, V> Collector<Entry<? extends K, ? extends V>, ?, Map<K, V>> a() {
        return Collectors.toMap(Entry::getKey, Entry::getValue);
    }

    public static <T extends Comparable<T>> String a(IBlockState<T> iblockstate, T object) { // Paper - decompile fix
        return iblockstate.a(object); // Paper - decompile fix
    }

    public static String a(String s, @Nullable MinecraftKey minecraftkey) {
        return minecraftkey == null ? s + ".unregistered_sadface" : s + '.' + minecraftkey.getNamespace() + '.' + minecraftkey.getKey().replace('/', '.');
    }

    public static long getMonotonicMillis() {
        return getMonotonicNanos() / 1000000L;
    }

    public static long getMonotonicNanos() {
        return System.nanoTime(); // Paper
    }

    public static long getTimeMillis() {
        return Instant.now().toEpochMilli();
    }

    private static ExecutorService k() {
        int i = Math.min(6, Math.max(Runtime.getRuntime().availableProcessors() - 2, 2)); // Paper - use more reasonable default - 2 is hard minimum to avoid using unlimited threads
        Object object;

        if (i <= 0) {
            object = MoreExecutors.newDirectExecutorService();
        } else {
            object = new ForkJoinPool(i, (forkjoinpool) -> {
                ForkJoinWorkerThread forkjoinworkerthread = new ForkJoinWorkerThread(forkjoinpool) {
                };

                forkjoinworkerthread.setName("Server-Worker-" + SystemUtils.b.getAndIncrement());
                return forkjoinworkerthread;
            }, (thread, throwable) -> {
                if (throwable instanceof CompletionException) {
                    throwable = throwable.getCause();
                }

                if (throwable instanceof ReportedException) {
                    DispenserRegistry.a(((ReportedException) throwable).a().e());
                    System.exit(-1);
                }

                SystemUtils.LOGGER.error(String.format("Caught exception in thread %s", thread), throwable);
            }, true);
        }

        return (ExecutorService) object;
    }

    public static Executor e() {
        return SystemUtils.c;
    }

    public static void f() {
        SystemUtils.c.shutdown();

        boolean flag;

        try {
            flag = SystemUtils.c.awaitTermination(3L, TimeUnit.SECONDS);
        } catch (InterruptedException interruptedexception) {
            flag = false;
        }

        if (!flag) {
            SystemUtils.c.shutdownNow();
        }

    }

    public static SystemUtils.OS g() {
        String s = System.getProperty("os.name").toLowerCase(Locale.ROOT);

        return s.contains("win") ? SystemUtils.OS.WINDOWS : (s.contains("mac") ? SystemUtils.OS.OSX : (s.contains("solaris") ? SystemUtils.OS.SOLARIS : (s.contains("sunos") ? SystemUtils.OS.SOLARIS : (s.contains("linux") ? SystemUtils.OS.LINUX : (s.contains("unix") ? SystemUtils.OS.LINUX : SystemUtils.OS.UNKNOWN)))));
    }

    public static Stream<String> h() {
        RuntimeMXBean runtimemxbean = ManagementFactory.getRuntimeMXBean();

        return runtimemxbean.getInputArguments().stream().filter((s) -> {
            return s.startsWith("-X");
        });
    }

    public static <T> T a(List<T> list) {
        return list.get(list.size() - 1);
    }

    public static <T> T a(Iterable<T> iterable, @Nullable T t0) {
        Iterator<T> iterator = iterable.iterator();
        T t1 = iterator.next();

        if (t0 != null) {
            Object object = t1;

            while (object != t0) {
                if (iterator.hasNext()) {
                    object = iterator.next();
                }
            }

            if (iterator.hasNext()) {
                return iterator.next();
            }
        }

        return t1;
    }

    public static <T> T b(Iterable<T> iterable, @Nullable T t0) {
        Iterator<T> iterator = iterable.iterator();

        T object; // Paper - decompile fix
        T object1; // Paper - decompile fix

        for (object1 = null; iterator.hasNext(); object1 = object) {
            object = iterator.next();
            if (object == t0) {
                if (object1 == null) {
                    object1 = iterator.hasNext() ? Iterators.getLast(iterator) : t0;
                }
                break;
            }
        }

        return object1;
    }

    public static <T> T a(Supplier<T> supplier) {
        return supplier.get();
    }

    public static <T> T a(T t0, Consumer<T> consumer) {
        consumer.accept(t0);
        return t0;
    }

    public static <K> Strategy<K> i() {
        return (Strategy<K>) SystemUtils.IdentityHashingStrategy.INSTANCE; // Paper - decompile fix
    }

    public static <V> CompletableFuture<List<V>> b(List<? extends CompletableFuture<? extends V>> list) {
        List<V> list1 = Lists.newArrayListWithCapacity(list.size());
        CompletableFuture<?>[] acompletablefuture = new CompletableFuture[list.size()];
        CompletableFuture<Void> completablefuture = new CompletableFuture();

        list.forEach((completablefuture1) -> {
            int i = list1.size();

            list1.add(null); // Paper - decompile fix
            acompletablefuture[i] = completablefuture1.whenComplete((object, throwable) -> {
                if (throwable != null) {
                    completablefuture.completeExceptionally(throwable);
                } else {
                    list1.set(i, object);
                }

            });
        });
        return CompletableFuture.allOf(acompletablefuture).applyToEither(completablefuture, (ovoid) -> {
            return list1;
        });
    }

    public static <T> Stream<T> a(Optional<? extends T> optional) {
        return (Stream) DataFixUtils.orElseGet(optional.map(Stream::of), Stream::empty);
    }

    public static <T> Optional<T> a(Optional<T> optional, Consumer<T> consumer, Runnable runnable) {
        if (optional.isPresent()) {
            consumer.accept(optional.get());
        } else {
            runnable.run();
        }

        return optional;
    }

    public static Runnable a(Runnable runnable, Supplier<String> supplier) {
        return runnable;
    }

    public static Optional<UUID> a(String s, Dynamic<?> dynamic) {
        return dynamic.get(s + "Most").asNumber().flatMap((number) -> {
            return dynamic.get(s + "Least").asNumber().map((number1) -> {
                return new UUID(number.longValue(), number1.longValue());
            });
        });
    }

    public static <T> Dynamic<T> a(String s, UUID uuid, Dynamic<T> dynamic) {
        return dynamic.set(s + "Most", dynamic.createLong(uuid.getMostSignificantBits())).set(s + "Least", dynamic.createLong(uuid.getLeastSignificantBits()));
    }

    static enum IdentityHashingStrategy implements Strategy<Object> {

        INSTANCE;

        private IdentityHashingStrategy() {}

        public int hashCode(Object object) {
            return System.identityHashCode(object);
        }

        public boolean equals(Object object, Object object1) {
            return object == object1;
        }
    }

    public static enum OS {

        LINUX, SOLARIS, WINDOWS {
        },
        OSX {
        },
        UNKNOWN;

        private OS() {}
    }
}
