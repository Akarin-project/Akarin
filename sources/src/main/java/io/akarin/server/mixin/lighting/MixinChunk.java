/*
 * This file is part of Sponge, licensed under the MIT License (MIT).
 *
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package io.akarin.server.mixin.lighting;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import io.akarin.api.internal.Akari;
import io.akarin.api.internal.mixin.IMixinChunk;
import io.akarin.api.internal.mixin.IMixinWorldServer;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Blocks;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkSection;
import net.minecraft.server.EnumDirection;
import net.minecraft.server.EnumSkyBlock;
import net.minecraft.server.IBlockData;
import net.minecraft.server.MCUtil;
import net.minecraft.server.TileEntity;
import net.minecraft.server.World;
import net.minecraft.server.BlockPosition.MutableBlockPosition;

@Mixin(value = Chunk.class, remap = false, priority = 1001)
public abstract class MixinChunk implements IMixinChunk {
    
    // Keeps track of block positions in this chunk currently queued for sky light update
    private CopyOnWriteArrayList<Short> queuedSkyLightingUpdates = new CopyOnWriteArrayList<>();
    // Keeps track of block positions in this chunk currently queued for block light update
    private CopyOnWriteArrayList<Short> queuedBlockLightingUpdates = new CopyOnWriteArrayList<>();
    private AtomicInteger pendingLightUpdates = new AtomicInteger();
    private long lightUpdateTime;
    private static ExecutorService lightExecutorService;
    
    @Shadow(aliases = "m") private boolean isGapLightingUpdated;
    @Shadow(aliases = "r") private boolean ticked;
    @Shadow @Final private ChunkSection[] sections;
    @Shadow @Final public int locX;
    @Shadow @Final public int locZ;
    @Shadow @Final public World world;
    @Shadow @Final public int[] heightMap;
    /** Which columns need their skylightMaps updated. */
    @Shadow(aliases = "i") @Final private boolean[] updateSkylightColumns;
    /** Queue containing the BlockPosition of tile entities queued for creation */
    @Shadow(aliases = "y") @Final private ConcurrentLinkedQueue<BlockPosition> tileEntityPosQueue;
    /** Boolean value indicating if the terrain is populated. */
    @Shadow(aliases = "done") private boolean isTerrainPopulated;
    @Shadow(aliases = "lit") private boolean isLightPopulated;
    /** Lowest value in the heightmap. */
    @Shadow(aliases = "v") private int heightMapMinimum;
    
    @Shadow(aliases = "b") public abstract int getHeightValue(int x, int z);
    @Shadow(aliases = "g") @Nullable public abstract TileEntity createNewTileEntity(BlockPosition pos);
    @Shadow(aliases = "a") @Nullable public abstract TileEntity getTileEntity(BlockPosition pos, Chunk.EnumTileEntityState state);
    @Shadow @Final public abstract IBlockData getBlockData(BlockPosition pos);
    @Shadow @Final public abstract IBlockData getBlockData(int x, int y, int z);
    @Shadow public abstract boolean isUnloading();
    /** Checks the height of a block next to a sky-visible block and schedules a lighting update as necessary */
    @Shadow(aliases = "b") public abstract void checkSkylightNeighborHeight(int x, int z, int maxValue);
    @Shadow(aliases = "a") public abstract void updateSkylightNeighborHeight(int x, int z, int startY, int endY);
    @Shadow(aliases = "z") public abstract void setSkylightUpdated();
    @Shadow(aliases = "g") public abstract int getTopFilledSegment();
    @Shadow public abstract void markDirty();
    
    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstruct(World worldIn, int x, int z, CallbackInfo ci) {
        lightExecutorService = ((IMixinWorldServer) worldIn).getLightingExecutor();
    }
    
    @Override
    public AtomicInteger getPendingLightUpdates() {
        return this.pendingLightUpdates;
    }
    
    @Override
    public long getLightUpdateTime() {
        return this.lightUpdateTime;
    }
    
    @Override
    public void setLightUpdateTime(long time) {
        this.lightUpdateTime = time;
    }
    
    @Inject(method = "b(Z)V", at = @At("HEAD"), cancellable = true)
    private void onTickHead(boolean skipRecheckGaps, CallbackInfo ci) {
        final List<Chunk> neighbors = this.getSurroundingChunks();
        if (this.isGapLightingUpdated && this.world.worldProvider.m() && !skipRecheckGaps && !neighbors.isEmpty()) { // PAIL: hasSkyLight
            lightExecutorService.execute(() -> {
                this.recheckGapsAsync(neighbors);
            });
            this.isGapLightingUpdated = false;
        }
        
        this.ticked = true;
        
        if (!this.isLightPopulated && this.isTerrainPopulated && !neighbors.isEmpty()) {
            lightExecutorService.execute(() -> {
                this.checkLightAsync(neighbors);
            });
            // set to true to avoid requeuing the same task when not finished
            this.isLightPopulated = true;
        }
        
        while (!this.tileEntityPosQueue.isEmpty()) {
            BlockPosition blockpos = this.tileEntityPosQueue.poll();
            
            if (this.getTileEntity(blockpos, Chunk.EnumTileEntityState.CHECK) == null && this.getBlockData(blockpos).getBlock().isTileEntity()) { // PAIL: getTileEntity
                TileEntity tileentity = this.createNewTileEntity(blockpos);
                this.world.setTileEntity(blockpos, tileentity);
                this.world.b(blockpos, blockpos); // PAIL: markBlockRangeForRenderUpdate
            }
        }
        ci.cancel();
    }
    
    @Redirect(method = "b(III)V", at = @At(value = "INVOKE", target = "net/minecraft/server/World.getHighestBlockYAt(Lnet/minecraft/server/BlockPosition;)Lnet/minecraft/server/BlockPosition;"))
    private BlockPosition onCheckSkylightGetHeight(World world, BlockPosition pos) {
        final Chunk chunk = this.getLightChunk(pos.getX() >> 4, pos.getZ() >> 4, null);
        if (chunk == null) {
            return BlockPosition.ZERO;
        }
        
        return new BlockPosition(pos.getX(), chunk.b(pos.getX() & 15, pos.getZ() & 15), pos.getZ()); // PAIL: getHeightValue
    }
    
    @Redirect(method = "a(IIII)V", at = @At(value = "INVOKE", target = "net/minecraft/server/World.areChunksLoaded(Lnet/minecraft/server/BlockPosition;I)Z"))
    private boolean onAreaLoadedSkyLightNeighbor(World world, BlockPosition pos, int radius) {
        return this.isAreaLoaded();
    }
    
    @Redirect(method = "a(IIII)V", at = @At(value = "INVOKE", target = "net/minecraft/server/World.c(Lnet/minecraft/server/EnumSkyBlock;Lnet/minecraft/server/BlockPosition;)Z"))
    private boolean onCheckLightForSkylightNeighbor(World world, EnumSkyBlock enumSkyBlock, BlockPosition pos) {
        return this.checkWorldLightFor(enumSkyBlock, pos);
    }
    
    /**
     * Rechecks chunk gaps async.
     * 
     * @param neighbors A thread-safe list of surrounding neighbor chunks
     */
    private void recheckGapsAsync(List<Chunk> neighbors) {
        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                if (this.updateSkylightColumns[i + j * 16]) {
                    this.updateSkylightColumns[i + j * 16] = false;
                    int k = this.getHeightValue(i, j);
                    int l = this.locX * 16 + i;
                    int i1 = this.locZ * 16 + j;
                    int j1 = Integer.MAX_VALUE;
                    
                    for (EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                        final Chunk chunk = this.getLightChunk((l + enumfacing.getAdjacentX()) >> 4, (i1 + enumfacing.getAdjacentZ()) >> 4, neighbors);
                        if (chunk == null || chunk.isUnloading()) {
                            continue;
                        }
                        j1 = Math.min(j1, chunk.w()); // PAIL: getLowestHeight
                    }
                    
                    this.checkSkylightNeighborHeight(l, i1, j1);
                    
                    for (EnumDirection enumfacing1 : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                        this.checkSkylightNeighborHeight(l + enumfacing1.getAdjacentX(), i1 + enumfacing1.getAdjacentZ(), k);
                    }
                }
            }
            
            // this.isGapLightingUpdated = false;
        }
    }
    
    @Redirect(method = "n()V", at = @At(value = "INVOKE", target = "net/minecraft/server/World.getType(Lnet/minecraft/server/BlockPosition;)Lnet/minecraft/server/IBlockData;"))
    private IBlockData onRelightChecksGetBlockData(World world, BlockPosition pos) {
        Chunk chunk = MCUtil.getLoadedChunkWithoutMarkingActive(world.getChunkProvider(), pos.getX() >> 4, pos.getZ() >> 4);
        
        final IMixinChunk spongeChunk = (IMixinChunk) chunk;
        if (chunk == null || chunk.isUnloading() || !spongeChunk.areNeighborsLoaded()) {
            return Blocks.AIR.getBlockData();
        }
        
        return chunk.getBlockData(pos);
    }
    
    @Redirect(method = "n()V", at = @At(value = "INVOKE", target = "net/minecraft/server/World.w(Lnet/minecraft/server/BlockPosition;)Z"))
    private boolean onRelightChecksCheckLight(World world, BlockPosition pos) {
        return this.checkWorldLight(pos);
    }
    
    // Avoids grabbing chunk async during light check
    @Redirect(method = "e(II)Z", at = @At(value = "INVOKE", target = "net/minecraft/server/World.w(Lnet/minecraft/server/BlockPosition;)Z"))
    private boolean onCheckLightWorld(World world, BlockPosition pos) {
        return this.checkWorldLight(pos);
    }
    
    @Inject(method = "o()V", at = @At("HEAD"), cancellable = true)
    private void checkLightHead(CallbackInfo ci) {
        if (this.world.getMinecraftServer().isStopped() || lightExecutorService.isShutdown()) {
            return;
        }
        
        if (this.isUnloading()) {
            return;
        }
        final List<Chunk> neighborChunks = this.getSurroundingChunks();
        if (neighborChunks.isEmpty()) {
            this.isLightPopulated = false;
            return;
        }
        
        if (Akari.isPrimaryThread()) {
            try {
                lightExecutorService.execute(() -> {
                    this.checkLightAsync(neighborChunks);
                });
            } catch (RejectedExecutionException ex) {
                // This could happen if ServerHangWatchdog kills the server
                // between the start of the method and the execute() call.
                if (!this.world.getMinecraftServer().isStopped() && !lightExecutorService.isShutdown()) {
                    throw ex;
                }
            }
        } else {
            this.checkLightAsync(neighborChunks);
        }
        ci.cancel();
    }
    
    /**
     * Checks light async.
     * 
     * @param neighbors A thread-safe list of surrounding neighbor chunks
     */
    private void checkLightAsync(List<Chunk> neighbors) {
        this.isTerrainPopulated = true;
        this.isLightPopulated = true;
        BlockPosition blockpos = new BlockPosition(this.locX << 4, 0, this.locZ << 4);
        
        if (this.world.worldProvider.m()) { // PAIL: hasSkyLight
            reCheckLight:
            
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (!this.checkLightAsync(i, j, neighbors)) {
                        this.isLightPopulated = false;
                        break reCheckLight;
                    }
                }
            }
            
            if (this.isLightPopulated) {
                for (EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                    int k = enumfacing.c() == EnumDirection.EnumAxisDirection.POSITIVE ? 16 : 1; // PAIL: getAxisDirection
                    final BlockPosition pos = blockpos.shift(enumfacing, k);
                    final Chunk chunk = this.getLightChunk(pos.getX() >> 4, pos.getZ() >> 4, neighbors);
                    if (chunk == null) {
                        continue;
                    }
                    chunk.checkLightSide(enumfacing.opposite());
                }
                
                this.setSkylightUpdated();
            }
        }
    }
    
    /**
     * Checks light async.
     * 
     * @param x The x position of chunk
     * @param z The z position of chunk
     * @param neighbors A thread-safe list of surrounding neighbor chunks
     * @return True if light update was successful, false if not
     */
    private boolean checkLightAsync(int x, int z, List<Chunk> neighbors) {
        int i = this.getTopFilledSegment();
        boolean flag = false;
        boolean flag1 = false;
        MutableBlockPosition blockpos$mutableblockpos = new MutableBlockPosition((this.locX << 4) + x, 0, (this.locZ << 4) + z);
        
        for (int j = i + 16 - 1; j > this.world.getSeaLevel() || j > 0 && !flag1; --j) {
            blockpos$mutableblockpos.setValues(blockpos$mutableblockpos.getX(), j, blockpos$mutableblockpos.getZ());
            int k = this.getBlockData(blockpos$mutableblockpos).c(); // PAIL: getLightOpacity
            
            if (k == 255 && blockpos$mutableblockpos.getY() < this.world.getSeaLevel()) {
                flag1 = true;
            }
            
            if (!flag && k > 0) {
                flag = true;
            } else if (flag && k == 0 && !this.checkWorldLight(blockpos$mutableblockpos, neighbors)) {
                return false;
            }
        }
        
        for (int l = blockpos$mutableblockpos.getY(); l > 0; --l) {
            blockpos$mutableblockpos.setValues(blockpos$mutableblockpos.getX(), l, blockpos$mutableblockpos.getZ());
            
            if (this.getBlockData(blockpos$mutableblockpos).d() > 0) { // getLightValue
                this.checkWorldLight(blockpos$mutableblockpos, neighbors);
            }
        }
        
        return true;
    }
    
    /**
     * Thread-safe method to retrieve a chunk during async light updates.
     * 
     * @param chunkX The x position of chunk.
     * @param chunkZ The z position of chunk.
     * @param neighbors A thread-safe list of surrounding neighbor chunks
     * @return The chunk if available, null if not
     */
    private Chunk getLightChunk(int chunkX, int chunkZ, List<Chunk> neighbors) {
        final Chunk currentChunk = (Chunk) (Object) this;
        if (currentChunk.a(chunkX, chunkZ)) { // PAIL: isAtLocation
            if (currentChunk.isUnloading()) {
                return null;
            }
            return currentChunk;
        }
        if (neighbors == null) {
            neighbors = this.getSurroundingChunks();
            if (neighbors.isEmpty()) {
                return null;
            }
        }
        for (Chunk neighbor : neighbors) {
            if (neighbor.a(chunkX, chunkZ)) { // PAIL: isAtLocation
                if (neighbor.isUnloading()) {
                    return null;
                }
                return neighbor;
            }
        }

        return null;
    }
    
    /**
     * Checks if surrounding chunks are loaded thread-safe.
     * 
     * @return True if surrounded chunks are loaded, false if not
     */
    private boolean isAreaLoaded() {
        if (!this.areNeighborsLoaded()) {
            return false;
        }
        
        // add diagonal chunks
        final Chunk southEastChunk = ((IMixinChunk) this.getNeighborChunk(0)).getNeighborChunk(2);
        if (southEastChunk == null) {
            return false;
        }
        
        final Chunk southWestChunk = ((IMixinChunk) this.getNeighborChunk(0)).getNeighborChunk(3);
        if (southWestChunk == null) {
            return false;
        }
        
        final Chunk northEastChunk = ((IMixinChunk) this.getNeighborChunk(1)).getNeighborChunk(2);
        if (northEastChunk == null) {
            return false;
        }
        
        final Chunk northWestChunk = ((IMixinChunk) this.getNeighborChunk(1)).getNeighborChunk(3);
        if (northWestChunk == null) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Gets surrounding chunks thread-safe.
     * 
     * @return The list of surrounding chunks, empty list if not loaded
     */
    private List<Chunk> getSurroundingChunks() {
        if (!this.areNeighborsLoaded()) {
            return Collections.emptyList();
        }
        
        // add diagonal chunks
        final Chunk southEastChunk = ((IMixinChunk) this.getNeighborChunk(0)).getNeighborChunk(2);
        if (southEastChunk == null) {
            return Collections.emptyList();
        }
        
        final Chunk southWestChunk = ((IMixinChunk) this.getNeighborChunk(0)).getNeighborChunk(3);
        if (southWestChunk == null) {
            return Collections.emptyList();
        }
        
        final Chunk northEastChunk = ((IMixinChunk) this.getNeighborChunk(1)).getNeighborChunk(2);
        if (northEastChunk == null) {
            return Collections.emptyList();
        }
        
        final Chunk northWestChunk = ((IMixinChunk) this.getNeighborChunk(1)).getNeighborChunk(3);
        if (northWestChunk == null) {
            return Collections.emptyList();
        }
        
        List<Chunk> chunkList = Lists.newArrayList();
        chunkList = this.getNeighbors();
        chunkList.add(southEastChunk);
        chunkList.add(southWestChunk);
        chunkList.add(northEastChunk);
        chunkList.add(northWestChunk);
        return chunkList;
    }
    
    @Inject(method = "c(III)V", at = @At("HEAD"), cancellable = true)
    private void onRelightBlock(int x, int y, int z, CallbackInfo ci) {
        lightExecutorService.execute(() -> {
            this.relightBlockAsync(x, y, z);
        });
        ci.cancel();
    }
    
    /**
     * Relight's a block async.
     * 
     * @param x The x position
     * @param y The y position
     * @param z The z position
     */
    private void relightBlockAsync(int x, int y, int z) {
        int i = this.heightMap[z << 4 | x] & 255;
        int j = i;
        
        if (y > i) {
            j = y;
        }
        
        while (j > 0 && this.getBlockData(x, j - 1, z).c() == 0) { // PAIL: getLightOpacity
            --j;
        }
        
        if (j != i) {
            this.markBlocksDirtyVerticalAsync(x + this.locX * 16, z + this.locZ * 16, j, i);
            this.heightMap[z << 4 | x] = j;
            int k = this.locX * 16 + x;
            int l = this.locZ * 16 + z;
            
            if (this.world.worldProvider.m()) { // PAIL: hasSkyLight
                if (j < i) {
                    for (int j1 = j; j1 < i; ++j1) {
                        ChunkSection extendedblockstorage2 = this.sections[j1 >> 4];
                        
                        if (extendedblockstorage2 != Chunk.EMPTY_CHUNK_SECTION) {
                            extendedblockstorage2.a(x, j1 & 15, z, 15); // PAIL: setSkyLight
                            // this.world.m(new BlockPosition((this.locX << 4) + x, j1, (this.locZ << 4) + z)); // PAIL: notifyLightSet - client side
                        }
                    }
                } else {
                    for (int i1 = i; i1 < j; ++i1) {
                        ChunkSection extendedblockstorage = this.sections[i1 >> 4];
                        
                        if (extendedblockstorage != Chunk.EMPTY_CHUNK_SECTION) {
                            extendedblockstorage.a(x, i1 & 15, z, 0); // PAIL: setSkyLight
                            // this.world.m(new BlockPosition((this.locX << 4) + x, i1, (this.locZ << 4) + z)); // PAIL: notifyLightSet - client side
                        }
                    }
                }
                
                int k1 = 15;
                
                while (j > 0 && k1 > 0) {
                    --j;
                    int i2 = this.getBlockData(x, j, z).c();
                    
                    if (i2 == 0) {
                        i2 = 1;
                    }
                    
                    k1 -= i2;
                    
                    if (k1 < 0) {
                        k1 = 0;
                    }
                    
                    ChunkSection extendedblockstorage1 = this.sections[j >> 4];
                    
                    if (extendedblockstorage1 != Chunk.EMPTY_CHUNK_SECTION) {
                        extendedblockstorage1.a(x, j & 15, z, k1); // PAIL: setSkyLight
                    }
                }
            }
            
            int l1 = this.heightMap[z << 4 | x];
            int j2 = i;
            int k2 = l1;
            
            if (l1 < i) {
                j2 = l1;
                k2 = i;
            }
            
            if (l1 < this.heightMapMinimum) {
                this.heightMapMinimum = l1;
            }
            
            if (this.world.worldProvider.m()) { // PAIL: hasSkyLight
                for (EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
                    this.updateSkylightNeighborHeight(k + enumfacing.getAdjacentX(), l + enumfacing.getAdjacentZ(), j2, k2); // PAIL: updateSkylightNeighborHeight
                }
                
                this.updateSkylightNeighborHeight(k, l, j2, k2);
            }
            
            this.markDirty();
        }
    }
    
    /**
     * Marks a vertical line of blocks as dirty async.
     * Instead of calling world directly, we pass chunk safely for async light method.
     * 
     * @param x1
     * @param z1
     * @param x2
     * @param z2
     */
    private void markBlocksDirtyVerticalAsync(int x1, int z1, int x2, int z2) {
        if (x2 > z2) {
            int i = z2;
            z2 = x2;
            x2 = i;
        }
        
        if (this.world.worldProvider.m()) { // PAIL: hasSkyLight
            for (int j = x2; j <= z2; ++j) {
                final BlockPosition pos = new BlockPosition(x1, j, z1);
                final Chunk chunk = this.getLightChunk(pos.getX() >> 4, pos.getZ() >> 4, null);
                if (chunk == null) {
                    continue;
                }
                ((IMixinWorldServer) this.world).updateLightAsync(EnumSkyBlock.SKY, new BlockPosition(x1, j, z1), chunk);
            }
        }
        
        this.world.b(x1, x2, z1, x1, z2, z1); // PAIL: markBlockRangeForRenderUpdate
    }
    
    /**
     * Checks world light thread-safe.
     * 
     * @param lightType The type of light to check
     * @param pos The block position
     * @return True if light update was successful, false if not
     */
    private boolean checkWorldLightFor(EnumSkyBlock lightType, BlockPosition pos) {
        final Chunk chunk = this.getLightChunk(pos.getX() >> 4, pos.getZ() >> 4, null);
        if (chunk == null) {
            return false;
        }
        
        return ((IMixinWorldServer) this.world).updateLightAsync(lightType, pos, chunk);
    }
    
    private boolean checkWorldLight(BlockPosition pos) {
        return this.checkWorldLight(pos, null);
    }
    
    /**
     * Checks world light async.
     * 
     * @param pos The block position
     * @param neighbors A thread-safe list of surrounding neighbor chunks
     * @return True if light update was successful, false if not
     */
    private boolean checkWorldLight(BlockPosition pos, List<Chunk> neighbors) {
        boolean flag = false;
        final Chunk chunk = this.getLightChunk(pos.getX() >> 4, pos.getZ() >> 4, neighbors);
        if (chunk == null) {
            return false;
        }
        
        if (this.world.worldProvider.m()) { // PAIL: hasSkyLight
            flag |= ((IMixinWorldServer) this.world).updateLightAsync(EnumSkyBlock.SKY, pos, chunk);
        }
        
        flag = flag | ((IMixinWorldServer) this.world).updateLightAsync(EnumSkyBlock.BLOCK, pos, chunk);
        return flag;
    }
    
    /**
     * Gets the list of block positions currently queued for lighting updates.
     * 
     * @param type The light type
     * @return The list of queued block positions, empty if none
     */
    @Override
    public CopyOnWriteArrayList<Short> getQueuedLightingUpdates(EnumSkyBlock type) {
        if (type == EnumSkyBlock.SKY) {
            return this.queuedSkyLightingUpdates;
        }
        return this.queuedBlockLightingUpdates;
    }
}
