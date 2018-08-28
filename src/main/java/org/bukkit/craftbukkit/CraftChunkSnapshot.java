package org.bukkit.craftbukkit;

import com.google.common.base.Preconditions;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;

import net.minecraft.server.BiomeBase;
import net.minecraft.server.DataPaletteBlock;
import net.minecraft.server.HeightMap;
import net.minecraft.server.IBlockData;

/**
 * Represents a static, thread-safe snapshot of chunk of blocks
 * Purpose is to allow clean, efficient copy of a chunk data to be made, and then handed off for processing in another thread (e.g. map rendering)
 */
public class CraftChunkSnapshot implements ChunkSnapshot {
    private final int x, z;
    private final String worldname;
    private final DataPaletteBlock<IBlockData>[] blockids;
    private final byte[][] skylight;
    private final byte[][] emitlight;
    private final boolean[] empty;
    private final HeightMap hmap; // Height map
    private final long captureFulltime;
    private final BiomeBase[] biome;
    private final double[] biomeTemp;

    CraftChunkSnapshot(int x, int z, String wname, long wtime, DataPaletteBlock<IBlockData>[] sectionBlockIDs, byte[][] sectionSkyLights, byte[][] sectionEmitLights, boolean[] sectionEmpty, HeightMap hmap, BiomeBase[] biome, double[] biomeTemp) {
        this.x = x;
        this.z = z;
        this.worldname = wname;
        this.captureFulltime = wtime;
        this.blockids = sectionBlockIDs;
        this.skylight = sectionSkyLights;
        this.emitlight = sectionEmitLights;
        this.empty = sectionEmpty;
        this.hmap = hmap;
        this.biome = biome;
        this.biomeTemp = biomeTemp;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldname;
    }

    @Override
    public Material getBlockType(int x, int y, int z) {
        CraftChunk.validateChunkCoordinates(x, y, z);

        return CraftMagicNumbers.getMaterial(blockids[y >> 4].a(x, y & 0xF, z).getBlock());
    }

    @Override
    public final BlockData getBlockData(int x, int y, int z) {
        CraftChunk.validateChunkCoordinates(x, y, z);

        return CraftBlockData.fromData(blockids[y >> 4].a(x, y & 0xF, z));
    }

    @Override
    public final int getData(int x, int y, int z) {
        CraftChunk.validateChunkCoordinates(x, y, z);

        return CraftMagicNumbers.toLegacyData(blockids[y >> 4].a(x, y & 0xF, z));
    }

    public final int getBlockSkyLight(int x, int y, int z) {
        CraftChunk.validateChunkCoordinates(x, y, z);

        int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (skylight[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getBlockEmittedLight(int x, int y, int z) {
        CraftChunk.validateChunkCoordinates(x, y, z);

        int off = ((y & 0xF) << 7) | (z << 3) | (x >> 1);
        return (emitlight[y >> 4][off] >> ((x & 1) << 2)) & 0xF;
    }

    public final int getHighestBlockYAt(int x, int z) {
        CraftChunk.validateChunkCoordinates(x, 0, z);

        return hmap.a(x, z);
    }

    public final Biome getBiome(int x, int z) {
        CraftChunk.validateChunkCoordinates(x, 0, z);

        return CraftBlock.biomeBaseToBiome(biome[z << 4 | x]);
    }

    public final double getRawBiomeTemperature(int x, int z) {
        CraftChunk.validateChunkCoordinates(x, 0, z);

        return biomeTemp[z << 4 | x];
    }

    public final long getCaptureFullTime() {
        return captureFulltime;
    }

    public final boolean isSectionEmpty(int sy) {
        return empty[sy];
    }
}
