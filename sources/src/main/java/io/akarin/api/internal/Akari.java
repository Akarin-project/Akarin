package io.akarin.api.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Queue;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import io.akarin.api.internal.Akari.AssignableFactory;
import io.akarin.api.internal.Akari.TimingSignal;
import io.akarin.api.internal.utils.ReentrantSpinningLock;
import io.akarin.api.internal.utils.thread.SuspendableExecutorCompletionService;
import io.akarin.api.internal.utils.thread.SuspendableThreadPoolExecutor;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

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
        public AssignableThread() {
            super();
        }
    }
    
    public static class AssignableFactory implements ThreadFactory {
        private final String threadName;
        private int threadNumber;
        
        public AssignableFactory(String name) {
            threadName = name;
        }
        
        @Override
        public Thread newThread(Runnable run) {
            Thread thread = new AssignableThread(run);
            thread.setName(StringUtils.replaceChars(threadName, "$", String.valueOf(threadNumber++)));
            thread.setPriority(AkarinGlobalConfig.primaryThreadPriority); // Fair
            return thread;
        }
    }
    
    public static class TimingSignal {
        public final World tickedWorld;
        public final boolean isEntities;
        
        public TimingSignal(World world, boolean entities) {
            tickedWorld = world;
            isEntities = entities;
        }
    }
    
    public static SuspendableExecutorCompletionService<TimingSignal> STAGE_TICK;
    
    static {
        resizeTickExecutors(3);
    }
    
    public static void resizeTickExecutors(int worlds) {
        int parallelism;
        switch (AkarinGlobalConfig.parallelMode) {
            case -1:
                return;
            case 0:
                parallelism = 2;
                break;
            case 1:
                parallelism = worlds + 1;
                break;
            case 2:
            default:
                parallelism = worlds * 2;
                break;
        }
        STAGE_TICK = new SuspendableExecutorCompletionService<>(new SuspendableThreadPoolExecutor(parallelism, parallelism,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new AssignableFactory("Akarin Parallel Ticking Thread - $")));
    }
    
    public static boolean isPrimaryThread() {
        return isPrimaryThread(true);
    }
    
    public static boolean isPrimaryThread(boolean assign) {
        Thread current = Thread.currentThread();
        return current == MinecraftServer.getServer().primaryThread || (assign ? (current.getClass() == AssignableThread.class) : false);
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
    
    public final static Timing callbackTiming = getTiming("Akarin - Callback Queue");
    
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
