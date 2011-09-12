package org.bukkit.craftbukkit;

import com.google.common.collect.MapMaker;
import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.server.ChunkPosition;

import net.minecraft.server.WorldServer;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.ChunkSnapshot;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.WorldChunkManager;

public class CraftChunk implements Chunk {
    private WeakReference<net.minecraft.server.Chunk> weakChunk;
    private final ConcurrentMap<Integer, Block> cache = new MapMaker().softValues().makeMap();
    private WorldServer worldServer;
    private int x;
    private int z;

    public CraftChunk(net.minecraft.server.Chunk chunk) {
        this.weakChunk = new WeakReference<net.minecraft.server.Chunk>(chunk);
        worldServer = (WorldServer) getHandle().world;
        x = getHandle().x;
        z = getHandle().z;
    }

    public World getWorld() {
        return worldServer.getWorld();
    }

    public net.minecraft.server.Chunk getHandle() {
        net.minecraft.server.Chunk c = weakChunk.get();
        if (c == null) {
            c = worldServer.getChunkAt(x, z);
            weakChunk = new WeakReference<net.minecraft.server.Chunk>(c);
        }
        return c;
    }

    void breakLink() {
        weakChunk.clear();
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "CraftChunk{" + "x=" + getX() + "z=" + getZ() + '}';
    }

    public Block getBlock(int x, int y, int z) {
        int pos = (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
        Block block = this.cache.get(pos);
        if (block == null) {
            Block newBlock = new CraftBlock(this, (getX() << 4) | (x & 0xF), y & 0x7F, (getZ() << 4) | (z & 0xF));
            Block oldBlock = this.cache.put(pos, newBlock);
            if (oldBlock == null) {
                block = newBlock;
            } else {
                block = oldBlock;
            }
        }
        return block;
    }

    public Entity[] getEntities() {
        int count = 0, index = 0;
        net.minecraft.server.Chunk chunk = getHandle();
        for (int i = 0; i < 8; i++) {
            count += chunk.entitySlices[i].size();
        }

        Entity[] entities = new Entity[count];
        for (int i = 0; i < 8; i++) {
            for (Object obj: chunk.entitySlices[i].toArray()) {
                if (!(obj instanceof net.minecraft.server.Entity)) {
                    continue;
                }
                entities[index++] = ((net.minecraft.server.Entity) obj).getBukkitEntity();
            }
        }
        return entities;
    }

    public BlockState[] getTileEntities() {
        int index = 0;
        net.minecraft.server.Chunk chunk = getHandle();
        BlockState[] entities = new BlockState[chunk.tileEntities.size()];
        for (Object obj : chunk.tileEntities.keySet().toArray()) {
            if (!(obj instanceof ChunkPosition)) {
                continue;
            }
            ChunkPosition position = (ChunkPosition) obj;
            entities[index++] = worldServer.getWorld().getBlockAt(position.x + (chunk.x << 4), position.y, position.z + (chunk.z << 4)).getState();
        }
        return entities;
    }

    public boolean isLoaded() {
        return getWorld().isChunkLoaded(this);
    }

    public boolean load() {
        return getWorld().loadChunk(getX(), getZ(), true);
    }

    public boolean load(boolean generate) {
        return getWorld().loadChunk(getX(), getZ(), generate);
    }

    public boolean unload() {
        return getWorld().unloadChunk(getX(), getZ());
    }

    public boolean unload(boolean save) {
        return getWorld().unloadChunk(getX(), getZ(), save);
    }

    public boolean unload(boolean save, boolean safe) {
        return getWorld().unloadChunk(getX(), getZ(), save, safe);
    }

    public ChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    public ChunkSnapshot getChunkSnapshot(boolean includeMaxblocky, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.server.Chunk chunk = getHandle();
        byte[] buf = new byte[32768 + 16384 + 16384 + 16384]; // Get big enough buffer for whole chunk
        chunk.getData(buf, 0, 0, 0, 16, 128, 16, 0); // Get whole chunk
        byte[] hmap = null;

        if (includeMaxblocky) {
            hmap = new byte[256]; // Get copy of height map
            System.arraycopy(chunk.heightMap, 0, hmap, 0, 256);
        }

        BiomeBase[] biome = null;
        double[] biomeTemp = null;
        double[] biomeRain = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = chunk.world.getWorldChunkManager();
            BiomeBase[] biomeBase = wcm.getBiomeData(getX() << 4, getZ() << 4, 16, 16);

            if (includeBiome) {
                biome = new BiomeBase[256];
                System.arraycopy(biomeBase, 0, biome, 0, biome.length);
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                biomeRain = new double[256];
                System.arraycopy(wcm.temperature, 0, biomeTemp, 0, biomeTemp.length);
                System.arraycopy(wcm.rain, 0, biomeRain, 0, biomeRain.length);
            }
        }
        World world = getWorld();
        return new CraftChunkSnapshot(getX(), getZ(), world.getName(), world.getFullTime(), buf, hmap, biome, biomeTemp, biomeRain);
    }

    /**
     * Empty chunk snapshot - nothing but air blocks, but can include valid biome data
     */
    private static class EmptyChunkSnapshot extends CraftChunkSnapshot {
        EmptyChunkSnapshot(int x, int z, String worldName, long time, BiomeBase[] biome, double[] biomeTemp, double[] biomeRain) {
            super(x, z, worldName, time, null, null, biome, biomeTemp, biomeRain);
        }

        public final int getBlockTypeId(int x, int y, int z) {
            return 0;
        }

        public final int getBlockData(int x, int y, int z) {
            return 0;
        }

        public final int getBlockSkyLight(int x, int y, int z) {
            return 15;
        }

        public final int getBlockEmittedLight(int x, int y, int z) {
            return 0;
        }

        public final int getHighestBlockYAt(int x, int z) {
            return 0;
        }
    }

    public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
        BiomeBase[] biome = null;
        double[] biomeTemp = null;
        double[] biomeRain = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = world.getHandle().getWorldChunkManager();
            BiomeBase[] biomeBase = wcm.getBiomeData(x << 4, z << 4, 16, 16);

            if (includeBiome) {
                biome = new BiomeBase[256];
                System.arraycopy(biomeBase, 0, biome, 0, biome.length);
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                biomeRain = new double[256];
                System.arraycopy(wcm.temperature, 0, biomeTemp, 0, biomeTemp.length);
                System.arraycopy(wcm.rain, 0, biomeRain, 0, biomeRain.length);
            }
        }
        return new EmptyChunkSnapshot(x, z, world.getName(), world.getFullTime(), biome, biomeTemp, biomeRain);
    }
}
