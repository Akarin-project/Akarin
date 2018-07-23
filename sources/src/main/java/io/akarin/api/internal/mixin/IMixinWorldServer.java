package io.akarin.api.internal.mixin;

import java.util.List;
import java.util.concurrent.ExecutorService;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.Chunk;
import net.minecraft.server.EnumSkyBlock;

public interface IMixinWorldServer {
    boolean updateLightAsync(EnumSkyBlock lightType, BlockPosition pos, Chunk chunk);
    
    boolean checkLightAsync(EnumSkyBlock lightType, BlockPosition pos, Chunk currentChunk, List<Chunk> neighbors);
    
    ExecutorService getLightingExecutor();
}