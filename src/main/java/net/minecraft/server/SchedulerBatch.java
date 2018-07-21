package net.minecraft.server;

import java.util.concurrent.CompletableFuture;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SchedulerBatch<K, T extends SchedulerTask<K, T>, R> {

    private static final Logger a = LogManager.getLogger();
    private final Scheduler<K, T, R> b;
    private boolean c;
    private int d = 1000;
    private final java.util.concurrent.locks.ReentrantLock lock = new java.util.concurrent.locks.ReentrantLock(true); // Paper

    public SchedulerBatch(Scheduler<K, T, R> scheduler) {
        this.b = scheduler;
    }

    public void a() throws InterruptedException {
        this.b.b();
    }

    public void startBatch() { b(); } // Paper - OBFHELPER
    public void b() {
        lock.lock(); // Paper
        if (false && this.c) { // Paper
            throw new RuntimeException("Batch already started.");
        } else {
            this.d = 1000;
            this.c = true;
        }
    }

    public CompletableFuture<R> add(K k0) { return a(k0); } // Paper - OBFHELPER
    public CompletableFuture<R> a(K k0) {
        if (!this.c) {
            throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
        } else {
            CompletableFuture<R> completablefuture = this.b.a(k0);

            --this.d;
            if (this.d == 0) {
                completablefuture = this.b.a();
                this.d = 1000;
            }

            return completablefuture;
        }
    }

    public CompletableFuture<R> executeBatch() { return c(); } // Paper - OBFHELPER
    public CompletableFuture<R> c() {
        // Paper start
        if (!lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Current thread does not hold the write lock");
        }
        try {// Paper end
        if (false && !this.c) { // Paper
            throw new RuntimeException("Batch not properly started. Please use startBatch to create a new batch.");
        } else {
            if (this.d != 1000) {
                this.b.a();
            }

            this.c = false;
            return this.b.c();
        }
        } finally { lock.unlock(); } // Paper
    }
}
