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
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings("restriction")
public abstract class Akari {
    /**
     * A common logger used by mixin classes
     */
    public final static Logger logger = LogManager.getLogger("Akarin");
    
    /**
     * A common thread pool factory
     */
    public static final ThreadFactory STAGE_FACTORY = new ThreadFactoryBuilder().setNameFormat("Akarin Parallel Registry Thread - %1$d").build();
    
    /**
     * Main thread callback tasks
     */
    public static final Queue<Runnable> callbackQueue = Queues.newConcurrentLinkedQueue();
    
    public static class AssignableThread extends Thread {
        public AssignableThread(Runnable run) {
            super(run);
        }
    }
    
    public static class AssignableFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable run) {
            Thread thread = new AssignableThread(run);
            thread.setName("Akarin Parallel Schedule Thread");
            thread.setPriority(AkarinGlobalConfig.primaryThreadPriority); // Fair
            return thread;
        }
    }
    
    /**
     * A common tick pool
     */
    public static final ExecutorCompletionService<?> STAGE_TICK = new ExecutorCompletionService<>(Executors.newSingleThreadExecutor(new AssignableFactory()));
    
    public static boolean isPrimaryThread() {
        return isPrimaryThread(true);
    }
    
    public static boolean isPrimaryThread(boolean assign) {
        Thread current = Thread.currentThread();
        return current == MinecraftServer.getServer().primaryThread || (assign ? current instanceof AssignableThread : false);
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
    
    private static final String serverVersion = Akari.class.getPackage().getImplementationVersion();
    
    public static String getServerVersion() {
        return serverVersion + " (MC: " + MinecraftServer.getServer().getVersion() + ")";
    }
    
    /*
     * Timings
     */
    public final static Timing worldTiming = getTiming("Akarin - Full World Tick");
    
    public final static Timing entityCallbackTiming = getTiming("Akarin - Entity Callback");
    
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
