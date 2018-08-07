package net.minecraft.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

// Paper start
import com.destroystokyo.paper.antixray.PacketPlayOutMapChunkInfo; // Anti-Xray
// Paper end

/**
 * Akarin Changes Note
 * 1) WrappedByteBuf -> ByteBuf (compatibility)
 */
public class PacketPlayOutMapChunk implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private int c;
    private ByteBuf d; // Akarin - byte[] -> ByteBuf
    private List<NBTTagCompound> e;
    private boolean f;
    private volatile boolean ready = false; // Paper - Async-Anti-Xray - Ready flag for the network manager

    // Paper start - Async-Anti-Xray - Set the ready flag to true
    public PacketPlayOutMapChunk() {
        this.ready = true;
    }
    // Paper end

    public PacketPlayOutMapChunk(Chunk chunk, int i) {
        PacketPlayOutMapChunkInfo packetPlayOutMapChunkInfo = chunk.world.chunkPacketBlockController.getPacketPlayOutMapChunkInfo(this, chunk, i); // Paper - Anti-Xray - Add chunk packet info
        this.a = chunk.locX;
        this.b = chunk.locZ;
        this.f = i == '\uffff';
        boolean flag = chunk.getWorld().worldProvider.m();

        this.d = allocateBuffer(this.a(chunk, flag, i)); // Akarin

        // Paper start - Anti-Xray - Add chunk packet info
        if (packetPlayOutMapChunkInfo != null) {
            packetPlayOutMapChunkInfo.setData(this.d);
        }
        // Paper end

        this.c = this.writeChunk(new PacketDataSerializer(this.d), chunk, flag, i, packetPlayOutMapChunkInfo); // Paper - Anti-Xray - Add chunk packet info // Akarin
        this.e = Lists.newArrayList();
        Iterator iterator = chunk.getTileEntities().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            BlockPosition blockposition = (BlockPosition) entry.getKey();
            TileEntity tileentity = (TileEntity) entry.getValue();
            int j = blockposition.getY() >> 4;

            if (this.e() || (i & 1 << j) != 0) {
                NBTTagCompound nbttagcompound = tileentity.d();

                this.e.add(nbttagcompound);
            }
        }

        chunk.world.chunkPacketBlockController.modifyBlocks(this, packetPlayOutMapChunkInfo); // Paper - Anti-Xray - Modify blocks
    }

    // Paper start - Async-Anti-Xray - Getter and Setter for the ready flag
    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    // Paper end

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.readInt();
        this.f = packetdataserializer.readBoolean();
        this.c = packetdataserializer.g();
        int i = packetdataserializer.g();

        if (i > 2097152) {
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        } else {
            this.d = Unpooled.buffer(i); // Akarin
            packetdataserializer.readBytes(this.d);
            int j = packetdataserializer.g();

            this.e = Lists.newArrayList();

            for (int k = 0; k < j; ++k) {
                this.e.add(packetdataserializer.j());
            }

        }
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeInt(this.b);
        packetdataserializer.writeBoolean(this.f);
        packetdataserializer.d(this.c);
        packetdataserializer.d(this.d.array().length); // Akarin
        packetdataserializer.writeBytes(this.d.array()); // Akarin
        packetdataserializer.d(this.e.size());
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) iterator.next();

            packetdataserializer.a(nbttagcompound);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    private ByteBuf allocateBuffer(int expectedCapacity) { return g(expectedCapacity); } // Akarin - OBFHELPER
    private ByteBuf g(int expectedCapacity) { // Akarin - added argument
        ByteBuf bytebuf = Unpooled.buffer(expectedCapacity); // Akarin

        bytebuf.writerIndex(0);
        return bytebuf;
    }

    // Paper start - Anti-Xray - Support default method
    public int writeChunk(PacketDataSerializer packetDataSerializer, Chunk chunk, boolean writeSkyLightArray, int chunkSectionSelector) { return this.a(packetDataSerializer, chunk, writeSkyLightArray, chunkSectionSelector); } // OBFHELPER
    public int a(PacketDataSerializer packetdataserializer, Chunk chunk, boolean flag, int i) {
        return this.a(packetdataserializer, chunk, flag, i, null);
    }
    // Paper end

    public int writeChunk(PacketDataSerializer packetDataSerializer, Chunk chunk, boolean writeSkyLightArray, int chunkSectionSelector, PacketPlayOutMapChunkInfo packetPlayOutMapChunkInfo) { return this.a(packetDataSerializer, chunk, writeSkyLightArray, chunkSectionSelector, packetPlayOutMapChunkInfo); } // Paper - Anti-Xray - OBFHELPER
    public int a(PacketDataSerializer packetdataserializer, Chunk chunk, boolean flag, int i, PacketPlayOutMapChunkInfo packetPlayOutMapChunkInfo) { // Paper - Anti-Xray - Add chunk packet info
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;

        for (int l = achunksection.length; k < l; ++k) {
            ChunkSection chunksection = achunksection[k];

            if (chunksection != Chunk.a && (!this.e() || !chunksection.a()) && (i & 1 << k) != 0) {
                j |= 1 << k;
                chunksection.getBlocks().writeBlocks(packetdataserializer, packetPlayOutMapChunkInfo, k); // Paper - Anti-Xray - Add chunk packet info
                packetdataserializer.writeBytes(chunksection.getEmittedLightArray().asBytes());
                if (flag) {
                    packetdataserializer.writeBytes(chunksection.getSkyLightArray().asBytes());
                }
            }
        }

        if (this.e()) {
            packetdataserializer.writeBytes(chunk.getBiomeIndex());
        }

        return j;
    }

    protected int a(Chunk chunk, boolean flag, int i) {
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;

        for (int l = achunksection.length; k < l; ++k) {
            ChunkSection chunksection = achunksection[k];

            if (chunksection != Chunk.a && (!this.e() || !chunksection.a()) && (i & 1 << k) != 0) {
                j += chunksection.getBlocks().a();
                j += chunksection.getEmittedLightArray().asBytes().length;
                if (flag) {
                    j += chunksection.getSkyLightArray().asBytes().length;
                }
            }
        }

        if (this.e()) {
            j += chunk.getBiomeIndex().length;
        }

        return j;
    }

    public boolean e() {
        return this.f;
    }
}
