package com.destroystokyo.paper.antixray;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.Chunk;
import net.minecraft.server.DataPalette;
import net.minecraft.server.IBlockData;
import net.minecraft.server.PacketPlayOutMapChunk;

/**
 * Akarin Changes Note
 * 1) byte[] -> ByteBuf (compatibility)
 */
public class PacketPlayOutMapChunkInfo {

    private final PacketPlayOutMapChunk packetPlayOutMapChunk;
    private final Chunk chunk;
    private final int chunkSectionSelector;
    private ByteBuf data; // Akarin - byte[] -> ByteBuf
    private final int[] bitsPerValue = new int[16];
    private final DataPalette[] dataPalettes = new DataPalette[16];
    private final int[] dataBitsIndexes = new int[16];
    private final IBlockData[][] predefinedBlockData = new IBlockData[16][];

    public PacketPlayOutMapChunkInfo(PacketPlayOutMapChunk packetPlayOutMapChunk, Chunk chunk, int chunkSectionSelector) {
        this.packetPlayOutMapChunk = packetPlayOutMapChunk;
        this.chunk = chunk;
        this.chunkSectionSelector = chunkSectionSelector;
    }

    public PacketPlayOutMapChunk getPacketPlayOutMapChunk() {
        return packetPlayOutMapChunk;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public int getChunkSectionSelector() {
        return chunkSectionSelector;
    }

    public byte[] getData() {
        return data.array(); // Akarin
    }

    public void setData(ByteBuf data) { // Akarin - byte[] -> ByteBuf
        this.data = data;
    }

    public int getBitsPerValue(int chunkSectionIndex) {
        return bitsPerValue[chunkSectionIndex];
    }

    public void setBitsPerValue(int chunkSectionIndex, int bitsPerValue) {
        this.bitsPerValue[chunkSectionIndex] = bitsPerValue;
    }

    public DataPalette getDataPalette(int chunkSectionIndex) {
        return dataPalettes[chunkSectionIndex];
    }

    public void setDataPalette(int chunkSectionIndex, DataPalette dataPalette) {
        dataPalettes[chunkSectionIndex] = dataPalette;
    }

    public int getDataBitsIndex(int chunkSectionIndex) {
        return dataBitsIndexes[chunkSectionIndex];
    }

    public void setDataBitsIndex(int chunkSectionIndex, int dataBitsIndex) {
        dataBitsIndexes[chunkSectionIndex] = dataBitsIndex;
    }

    public IBlockData[] getPredefinedBlockData(int chunkSectionIndex) {
        return predefinedBlockData[chunkSectionIndex];
    }

    public void setPredefinedBlockData(int chunkSectionIndex, IBlockData[] predefinedBlockData) {
        this.predefinedBlockData[chunkSectionIndex] = predefinedBlockData;
    }

    public boolean isWritten(int chunkSectionIndex) {
        return bitsPerValue[chunkSectionIndex] != 0;
    }
}
