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
package io.akarin.server.mixin.cps;

import java.util.List;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.Lists;

import io.akarin.api.internal.mixin.IMixinChunk;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Chunk;
import net.minecraft.server.EnumDirection;
import net.minecraft.server.MCUtil;
import net.minecraft.server.World;

@Mixin(value = Chunk.class, remap = false)
public abstract class MixinChunk implements IMixinChunk {
    private Chunk[] neighborChunks = new Chunk[4];
    private static final EnumDirection[] CARDINAL_DIRECTIONS = new EnumDirection[] {EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.EAST, EnumDirection.WEST};
    
    @Shadow @Final public World world;
    @Shadow @Final public int locX;
    @Shadow @Final public int locZ;
    
    @Override
    public Chunk getNeighborChunk(int index) {
        return this.neighborChunks[index];
    }
    
    @Override
    public void setNeighborChunk(int index, @Nullable Chunk chunk) {
        this.neighborChunks[index] = chunk;
    }
    
    @Override
    public List<Chunk> getNeighbors() {
        List<Chunk> neighborList = Lists.newArrayList();
        for (Chunk neighbor : this.neighborChunks) {
            if (neighbor != null) {
                neighborList.add(neighbor);
            }
        }
        return neighborList;
    }
    
    @Override
    public boolean areNeighborsLoaded() {
        for (int i = 0; i < 4; i++) {
            if (this.neighborChunks[i] == null) {
                return false;
            }
        }
        return true;
    }
    
    private static int directionToIndex(EnumDirection direction) {
        switch (direction) {
            case NORTH:
                return 0;
            case SOUTH:
                return 1;
            case EAST:
                return 2;
            case WEST:
                return 3;
            default:
                throw new IllegalArgumentException("Unexpected direction");
        }
    }
    
    @Inject(method = "addEntities", at = @At("RETURN"))
    public void onLoadReturn(CallbackInfo ci) {
        BlockPosition origin = new BlockPosition(locX, 0, locZ);
        for (EnumDirection direction : CARDINAL_DIRECTIONS) {
            BlockPosition shift = origin.shift(direction);
            Chunk neighbor = MCUtil.getLoadedChunkWithoutMarkingActive(world.getChunkProvider(), shift.getX(), shift.getZ());
            if (neighbor != null) {
                int neighborIndex = directionToIndex(direction);
                int oppositeNeighborIndex = directionToIndex(direction.opposite());
                this.setNeighborChunk(neighborIndex, neighbor);
                ((IMixinChunk) neighbor).setNeighborChunk(oppositeNeighborIndex, (Chunk) (Object) this);
            }
        }
    }
    
    @Inject(method = "removeEntities", at = @At("RETURN"))
    public void onUnload(CallbackInfo ci) {
        BlockPosition origin = new BlockPosition(locX, 0, locZ);
        for (EnumDirection direction : CARDINAL_DIRECTIONS) {
            BlockPosition shift = origin.shift(direction);
            Chunk neighbor = MCUtil.getLoadedChunkWithoutMarkingActive(world.getChunkProvider(), shift.getX(), shift.getZ());
            if (neighbor != null) {
                int neighborIndex = directionToIndex(direction);
                int oppositeNeighborIndex = directionToIndex(direction.opposite());
                this.setNeighborChunk(neighborIndex, null);
                ((IMixinChunk) neighbor).setNeighborChunk(oppositeNeighborIndex, null);
            }
        }
    }
}
