package io.akarin.api.internal.mixin;

public interface IMixinTimingHandler {
    public Object lock();
    
    public void stopTiming(long start);
}