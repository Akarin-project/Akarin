package io.akarin.server.core;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

public class AkarinAsyncExecutor {
    private static final ExecutorService singleExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Akarin Single Async Executor Thread - %1$d").build());
    private static final ExecutorService asyncExecutor = Executors.newFixedThreadPool(getnThreads(), new ThreadFactoryBuilder().setNameFormat("Akarin Async Executor Thread - %1$d").build());

    private static int getnThreads(){
        int processors = Runtime.getRuntime().availableProcessors();
        if(processors > 2){
            return processors;
        }else {
            return 2;
        }
    }

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
    
    /**
     * Posts a task to be executed asynchronously in a single thread
     * @param run
     * @return 
     */
    public static <V> Future<V> scheduleSingleAsyncTask(Callable<V> run) {
        return singleExecutor.submit(run);
    }
    
    /**
     * Posts a task to be executed asynchronously
     * @param run
     */
    public static <V> Future<V> scheduleAsyncTask(Callable<V> run) {
        return asyncExecutor.submit(run);
    }
}