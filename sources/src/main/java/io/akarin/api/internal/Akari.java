package io.akarin.api.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings("restriction")
public abstract class Akari {
    /**
     * A common logger used by mixin classes
     */
    public final static Logger logger = LogManager.getLogger("Akarin");
    
    /**
     * Temporarily disable desync timings error, moreover it's worthless to trace async operation
     */
    public static volatile boolean silentTiming;
    
    /**
     * A common thread pool factory
     */
    public static final ThreadFactory STAGE_FACTORY = new ThreadFactoryBuilder().setNameFormat("Akarin Schedule Thread").build();
    
    /**
     * Main thread callback tasks
     */
    public static final Queue<Runnable> callbackQueue = Queues.newConcurrentLinkedQueue();
    
    /**
     * A common tick pool
     */
    public static final ExecutorCompletionService<?> STAGE_TICK = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor(Akari.STAGE_FACTORY));
    
    public static boolean mayEnableAsyncCathcer;
    
    public static boolean isPrimaryThread() {
        return Thread.currentThread().equals(MinecraftServer.getServer().primaryThread);
    }
    
    public static final String EMPTY_STRING = "";
    
    /*
     * The unsafe
     */
    public final static sun.misc.Unsafe UNSAFE = getUnsafe();
    
    private static sun.misc.Unsafe getUnsafe() {
        try {
            Field theUnsafe = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            theUnsafe.setAccessible(true);
            return (sun.misc.Unsafe) theUnsafe.get(null);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
    
    /*
     * Timings
     */
    public final static Timing worldTiming = getTiming("Akarin - World");
    
    public final static Timing callbackTiming = getTiming("Akarin - Callback");
    
    private static Timing getTiming(String name) {
        try {
            Method ofSafe = Timings.class.getDeclaredMethod("ofSafe", String.class);
            ofSafe.setAccessible(true);
            return (Timing) ofSafe.invoke(null, name);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }
}
