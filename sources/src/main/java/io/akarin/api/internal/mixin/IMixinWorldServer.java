package io.akarin.api.internal.mixin;

import java.util.Random;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface IMixinWorldServer {
    public Object lock();
    public Random rand();
    public ReentrantReadWriteLock trackerLock();
}