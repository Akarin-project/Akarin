package net.minecraft.server;

import java.util.Map;
import java.util.function.IntConsumer;

import javax.annotation.concurrent.ThreadSafe;

import lombok.AllArgsConstructor;

@ThreadSafe
@AllArgsConstructor
public class AkarinAsyncLighting {
    private final World world;
    private final ChunkSection[] sections;
    private final Map<HeightMap.Type, HeightMap> heightMap;
    
    public void getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition, IntConsumer callback) {
        this.getBrightness(enumskyblock, blockposition, this.world.o().g(), callback);
    }
    
    public void getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition, boolean canSeeSky, IntConsumer callback) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = j >> 4;

        if (l >= 0 && l <= this.sections.length - 1) {
            ChunkSection chunksection = this.sections[l];

            if (chunksection == Chunk.a)
                callback.accept(this.canHasLight(blockposition) ? enumskyblock.c : 0);
            
            switch (enumskyblock) {
                case SKY:
                    callback.accept(canSeeSky ? chunksection.c(i, j & 15, k) : 0);
                case BLOCK:
                    callback.accept(chunksection.d(i, j & 15, k));
                default:
                    callback.accept(enumskyblock.c);
            }
        } else {
            boolean hasLight = (enumskyblock == EnumSkyBlock.SKY && canSeeSky) || enumskyblock == EnumSkyBlock.BLOCK;
            callback.accept(hasLight ? enumskyblock.c : 0);
        }
    }
    
    public boolean canHasLight(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;

        return j >= ((HeightMap) this.heightMap.get(HeightMap.Type.LIGHT_BLOCKING)).a(i, k);
    }
}