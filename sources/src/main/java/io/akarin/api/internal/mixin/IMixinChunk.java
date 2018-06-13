package io.akarin.api.internal.mixin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import net.minecraft.server.Chunk;
import net.minecraft.server.EnumSkyBlock;

public interface IMixinChunk {
    AtomicInteger getPendingLightUpdates();
    
    long getLightUpdateTime();
    
    boolean areNeighborsLoaded();
    
    @Nullable Chunk getNeighborChunk(int index);
    
    CopyOnWriteArrayList<Short> getQueuedLightingUpdates(EnumSkyBlock type);
    
    List<Chunk> getNeighbors();
    
    void setNeighborChunk(int index, @Nullable Chunk chunk);
    
    void setLightUpdateTime(long time);
}