package net.minecraft.server;

import com.google.common.util.concurrent.ListenableFuture;

public interface IAsyncTaskHandler {

    ListenableFuture<Object> postToMainThread(Runnable runnable);

    boolean isMainThread();
    // Akarin start
    public default void ensuresMainThread(Runnable runnable) {
        postToMainThread(runnable);
    }
    // Akarin end
}
