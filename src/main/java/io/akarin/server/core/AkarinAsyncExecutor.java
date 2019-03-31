package io.akarin.server.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import co.aikar.timings.ThreadAssertion;

public class AkarinAsyncExecutor {
    private static final ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Akarin Single Async Executor Thread - %1$d").build());
    private static final ExecutorService asyncExecutor = Executors.newFixedThreadPool(getNThreads(), new ThreadFactoryBuilder().setNameFormat("Akarin Async Executor Thread - %1$d").build());

    private static int getNThreads() {
        int processors = Runtime.getRuntime().availableProcessors() / 2;
        if (processors < 2)
            return 2;
        if (processors > 8)
            return 8;
        return processors;
    }

    /**
     * Posts a task to be executed asynchronously in a single thread
     * @param run
     */
    public static void scheduleSingleAsyncTask(Runnable run) {
        ThreadAssertion.close();
        singleExecutor.execute(run);
    }
    
    /**
     * Posts a task to be executed asynchronously
     * @param run
     */
    public static void scheduleAsyncTask(Runnable run) {
        ThreadAssertion.close();
        asyncExecutor.execute(run);
    }
    
    /**
     * Posts a task to be executed asynchronously in a single thread
     * @param run
     * @return 
     */
    public static <V> Future<V> scheduleSingleAsyncTask(Callable<V> run) {
        ThreadAssertion.close();
        return singleExecutor.submit(run);
    }
    
    /**
     * Posts a task to be executed asynchronously
     * @param run
     */
    public static <V> Future<V> scheduleAsyncTask(Callable<V> run) {
        ThreadAssertion.close();
        return asyncExecutor.submit(run);
    }
}