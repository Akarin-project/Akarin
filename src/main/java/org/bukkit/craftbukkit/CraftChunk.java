package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import java.lang.ref.WeakReference;
import java.util.Arrays;

import java.util.Random;
import net.minecraft.server.*;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Entity;
import org.bukkit.ChunkSnapshot;

public class CraftChunk implements Chunk {
    private WeakReference<net.minecraft.server.Chunk> weakChunk;
    private final WorldServer worldServer;
    private final int x;
    private final int z;
    private static final byte[] emptyData = new byte[2048];
    private static final DataPaletteBlock<IBlockData> emptyBlockIDs = new ChunkSection(0, false).getBlocks();
    private static final byte[] emptySkyLight = new byte[2048];

    public CraftChunk(net.minecraft.server.Chunk chunk) {
        this.weakChunk = new WeakReference<net.minecraft.server.Chunk>(chunk);

        worldServer = (WorldServer) getHandle().world;
        x = getHandle().locX;
        z = getHandle().locZ;
    }

    public World getWorld() {
        return worldServer.getWorld();
    }

    public CraftWorld getCraftWorld() {
        return (CraftWorld) getWorld();
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
        validateChunkCoordinates(x, y, z);

        return new CraftBlock(worldServer, new BlockPosition((this.x << 4) | x, y, (this.z << 4) | z));
    }

    public Entity[] getEntities() {
        int count = 0, index = 0;
        net.minecraft.server.Chunk chunk = getHandle();

        for (int i = 0; i < 16; i++) {
            count += chunk.entitySlices[i].size();
        }

        Entity[] entities = new Entity[count];

        for (int i = 0; i < 16; i++) {

            for (Object obj : chunk.entitySlices[i].toArray()) {
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
            if (!(obj instanceof BlockPosition)) {
                continue;
            }

            BlockPosition position = (BlockPosition) obj;
            entities[index++] = worldServer.getWorld().getBlockAt(position.getX(), position.getY(), position.getZ()).getState();
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

    @Override
    public boolean isSlimeChunk() {
        // 987234911L is deterimined in EntitySlime when seeing if a slime can spawn in a chunk
        return SeededRandom.a(getX(), getZ(), getWorld().getSeed(), 987234911L).nextInt(10) == 0;
    }

    public boolean unload(boolean save) {
        return getWorld().unloadChunk(getX(), getZ(), save);
    }

    public boolean unload(boolean save, boolean safe) {
        return getWorld().unloadChunk(getX(), getZ(), save, safe);
    }

    @Override
    public boolean isForceLoaded() {
        return getWorld().isChunkForceLoaded(getX(), getZ());
    }

    @Override
    public void setForceLoaded(boolean forced) {
        getWorld().setChunkForceLoaded(getX(), getZ(), forced);
    }

    public ChunkSnapshot getChunkSnapshot() {
        return getChunkSnapshot(true, false, false);
    }

    public ChunkSnapshot getChunkSnapshot(boolean includeMaxBlockY, boolean includeBiome, boolean includeBiomeTempRain) {
        net.minecraft.server.Chunk chunk = getHandle();

        ChunkSection[] cs = chunk.getSections();
        DataPaletteBlock[] sectionBlockIDs = new DataPaletteBlock[cs.length];
        byte[][] sectionSkyLights = new byte[cs.length][];
        byte[][] sectionEmitLights = new byte[cs.length][];
        boolean[] sectionEmpty = new boolean[cs.length];

        for (int i = 0; i < cs.length; i++) {
            if (cs[i] == null) { // Section is empty?
                sectionBlockIDs[i] = emptyBlockIDs;
                sectionSkyLights[i] = emptySkyLight;
                sectionEmitLights[i] = emptyData;
                sectionEmpty[i] = true;
            } else { // Not empty
                NBTTagCompound data = new NBTTagCompound();
                cs[i].getBlocks().b(data, "Spigot", "Magic");

                DataPaletteBlock blockids = new DataPaletteBlock<>(ChunkSection.GLOBAL_PALETTE, net.minecraft.server.Block.REGISTRY_ID, GameProfileSerializer::d, GameProfileSerializer::a, Blocks.AIR.getBlockData()); // TODO: snapshot whole ChunkSection
                blockids.a(data, "Spigot", "Magic");

                sectionBlockIDs[i] = blockids;

                if (cs[i].getSkyLightArray() == null) {
                    sectionSkyLights[i] = emptySkyLight;
                } else {
                    sectionSkyLights[i] = new byte[2048];
                    System.arraycopy(cs[i].getSkyLightArray().asBytes(), 0, sectionSkyLights[i], 0, 2048);
                }
                sectionEmitLights[i] = new byte[2048];
                System.arraycopy(cs[i].getEmittedLightArray().asBytes(), 0, sectionEmitLights[i], 0, 2048);
            }
        }

        HeightMap hmap = null;

        if (includeMaxBlockY) {
            hmap = new HeightMap(null, HeightMap.Type.LIGHT_BLOCKING);
            hmap.a(chunk.heightMap.get(HeightMap.Type.LIGHT_BLOCKING).b());
        }

        BiomeBase[] biome = null;
        double[] biomeTemp = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = worldServer.getChunkProvider().getChunkGenerator().getWorldChunkManager();

            if (includeBiome) {
                biome = new BiomeBase[256];
                for (int i = 0; i < 256; i++) {
                    biome[i] = chunk.getBiome(new BlockPosition(i & 0xF, 0, i >> 4));
                }
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                float[] dat = getTemperatures(wcm, getX() << 4, getZ() << 4);

                for (int i = 0; i < 256; i++) {
                    biomeTemp[i] = dat[i];
                }
            }
        }

        World world = getWorld();
        return new CraftChunkSnapshot(getX(), getZ(), world.getName(), world.getFullTime(), sectionBlockIDs, sectionSkyLights, sectionEmitLights, sectionEmpty, hmap, biome, biomeTemp);
    }

    public static ChunkSnapshot getEmptyChunkSnapshot(int x, int z, CraftWorld world, boolean includeBiome, boolean includeBiomeTempRain) {
        BiomeBase[] biome = null;
        double[] biomeTemp = null;

        if (includeBiome || includeBiomeTempRain) {
            WorldChunkManager wcm = world.getHandle().getChunkProvider().getChunkGenerator().getWorldChunkManager();

            if (includeBiome) {
                biome = new BiomeBase[256];
                for (int i = 0; i < 256; i++) {
                    biome[i] = world.getHandle().getBiome(new BlockPosition((x << 4) + (i & 0xF), 0, (z << 4) + (i >> 4)));
                }
            }

            if (includeBiomeTempRain) {
                biomeTemp = new double[256];
                float[] dat = getTemperatures(wcm, x << 4, z << 4);

                for (int i = 0; i < 256; i++) {
                    biomeTemp[i] = dat[i];
                }
            }
        }

        /* Fill with empty data */
        int hSection = world.getMaxHeight() >> 4;
        DataPaletteBlock[] blockIDs = new DataPaletteBlock[hSection];
        byte[][] skyLight = new byte[hSection][];
        byte[][] emitLight = new byte[hSection][];
        boolean[] empty = new boolean[hSection];

        for (int i = 0; i < hSection; i++) {
            blockIDs[i] = emptyBlockIDs;
            skyLight[i] = emptySkyLight;
            emitLight[i] = emptyData;
            empty[i] = true;
        }

        return new CraftChunkSnapshot(x, z, world.getName(), world.getFullTime(), blockIDs, skyLight, emitLight, empty, new HeightMap(null, HeightMap.Type.LIGHT_BLOCKING), biome, biomeTemp);
    }

    private static float[] getTemperatures(WorldChunkManager chunkmanager, int chunkX, int chunkZ) {
        BiomeBase[] biomes = chunkmanager.getBiomes(chunkX, chunkZ, 16, 16);
        float[] temps = new float[biomes.length];

        for (int i = 0; i < biomes.length; i++) {
            float temp = biomes[i].getTemperature(); // Vanilla of olde: ((int) biomes[i].temperature * 65536.0F) / 65536.0F

            if (temp > 1F) {
                temp = 1F;
            }

            temps[i] = temp;
        }

        return temps;
    }

    static void validateChunkCoordinates(int x, int y, int z) {
        Preconditions.checkArgument(0 <= x && x <= 15, "x out of range (expected 0-15, got %s)", x);
        Preconditions.checkArgument(0 <= y && y <= 255, "y out of range (expected 0-255, got %s)", y);
        Preconditions.checkArgument(0 <= z && z <= 15, "z out of range (expected 0-15, got %s)", z);
    }

    static {
        Arrays.fill(emptySkyLight, (byte) 0xFF);
    }
}
