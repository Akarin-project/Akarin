package io.akarin.server.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class AkarinAsyncExecutor {
    private static final Executor singleExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Akarin Single Async Executor Thread - %1$d").build());
    private static final Executor asyncExecutor = Executors.newFixedThreadPool(4, new ThreadFactoryBuilder().setNameFormat("Akarin Async Executor Thread - %1$d").build());
    
    /**
     * Posts a task to be executed asynchronously in a single thread
     * @param run
     */
    public static void scheduleSingleAsyncTask(Runnable run) {
        singleExecutor.execute(run);
    }
    
    /**
     * Posts a task to be executed asynchronously
     * @param run
     */
    public static void scheduleAsyncTask(Runnable run) {
        asyncExecutor.execute(run);
    }
}