package io.akarin.api;

import java.util.concurrent.atomic.AtomicInteger;

public interface IMixinChunk {
    AtomicInteger getPendingLightUpdates();
    
    long getLightUpdateTime();
}