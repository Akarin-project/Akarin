package io.akarin.api.internal.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class ReentrantSpinningLock {
    private final AtomicBoolean attemptLock = new AtomicBoolean(false);
    private volatile long heldThreadId = 0;

    public void lock() {
        long currentThreadId = Thread.currentThread().getId();
        attemptLock.getAndSet(true); // In case acquire one lock concurrently
        
        if (heldThreadId != 0 && heldThreadId != currentThreadId) {
            attemptLock.set(false);
            while (heldThreadId != 0) ; // The current thread is spinning here
            attemptLock.getAndSet(true);
        }
        
        heldThreadId = currentThreadId;
        attemptLock.set(false);
    }
    
    public void unlock() {
        heldThreadId = 0;
    }
}
