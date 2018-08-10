package io.akarin.api.internal.utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class ReentrantSpinningLock {
    private final AtomicBoolean attemptLock = new AtomicBoolean(false);
    private final AtomicInteger reentrantLocks = new AtomicInteger(0);
    private volatile long heldThreadId = 0;

    public void lock() {
        long currentThreadId = Thread.currentThread().getId();
        if (heldThreadId != 0 && heldThreadId != currentThreadId) {
            while (heldThreadId != 0) ; // The current thread is spinning here
        }
        attemptLock.getAndSet(true); // In case acquire one lock concurrently
        if (heldThreadId == currentThreadId) reentrantLocks.getAndIncrement();
        heldThreadId = currentThreadId;
        attemptLock.set(false);
    }
    
    public void unlock() {
        if (reentrantLocks.get() > 0) {
            if (reentrantLocks.getAndDecrement() == 1) heldThreadId = 0;
        } else {
            heldThreadId = 0;
        }
    }
}
