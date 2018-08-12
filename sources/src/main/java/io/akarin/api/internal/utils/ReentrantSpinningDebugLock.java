package io.akarin.api.internal.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReentrantSpinningDebugLock extends ReentrantSpinningLock {
    private final AtomicBoolean singleLock = new AtomicBoolean(false);
    private long heldThreadId = 0;
    private int reentrantLocks = 0;
    public StackTraceElement[] tStackTraceElements;

    public void lock() {
        long currentThreadId = Thread.currentThread().getId();
        if (heldThreadId == currentThreadId) {
            reentrantLocks++;
        } else {
            while (!singleLock.compareAndSet(false, true)) ; // In case acquire one lock concurrently
            heldThreadId = currentThreadId;
        }
        tStackTraceElements = Thread.currentThread().getStackTrace();
    }
    
    public void unlock() {
        if (reentrantLocks == 0) {
            heldThreadId = 0;
            singleLock.set(false);
        } else {
            --reentrantLocks;
        }
    }
}
