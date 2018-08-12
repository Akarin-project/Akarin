package io.akarin.api.internal.mixin;

import java.util.Random;

import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;

public interface IMixinWorldServer {
    public Object lock();
    public Random rand();
    public ReentrantReadWriteUpdateLock trackerLock();
}