package net.minecraft.server;

import com.destroystokyo.paper.antixray.ChunkPacketInfo; // Paper - Anti-Xray
import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

public class PacketPlayOutMapChunk implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private int c;
    private NBTTagCompound d;
    private byte[] e; private byte[] getData() { return this.e; } // Paper - OBFHELPER
    private List<NBTTagCompound> f;
    private boolean g;
    private volatile boolean ready; // Paper - Async-Anti-Xray - Ready flag for the network manager

    public PacketPlayOutMapChunk() {
        this.ready = true; // Paper - Async-Anti-Xray - Set the ready flag to true
    }

    // Paper start
    private final java.util.List<Packet> extraPackets = new java.util.ArrayList<>();
    private static final int SKIP_EXCESSIVE_SIGNS_LIMIT = Integer.getInteger("Paper.excessiveSignsLimit", 500);

    @Override
    public java.util.List<Packet> getExtraPackets() {
        return extraPackets;
    }
    // Paper end
    public PacketPlayOutMapChunk(Chunk chunk, int i) {
        // Paper start - add forceLoad param
        this(chunk, i, false);
    }
    public PacketPlayOutMapChunk(Chunk chunk, int i, boolean forceLoad) {
        // Paper end
        ChunkPacketInfo<IBlockData> chunkPacketInfo = chunk.world.chunkPacketBlockController.getChunkPacketInfo(this, chunk, i, forceLoad); // Paper - Anti-Xray - Add chunk packet info
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();

        this.a = chunkcoordintpair.x;
        this.b = chunkcoordintpair.z;
        this.g = i == 65535;
        this.d = new NBTTagCompound();
        Iterator iterator = chunk.f().iterator();

        Entry entry;

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            if (((HeightMap.Type) entry.getKey()).b()) {
                this.d.set(((HeightMap.Type) entry.getKey()).a(), new NBTTagLongArray(((HeightMap) entry.getValue()).a()));
            }
        }

        this.e = new byte[this.a(chunk, i)];
        // Paper start - Anti-Xray - Add chunk packet info
        if (chunkPacketInfo != null) {
            chunkPacketInfo.setData(this.getData());
        }
        // Paper end
        this.c = this.writeChunk(new PacketDataSerializer(this.i()), chunk, i, chunkPacketInfo); // Paper - Anti-Xray - Add chunk packet info
        this.f = Lists.newArrayList();
        iterator = chunk.getTileEntities().entrySet().iterator();
        int totalSigns = 0; // Paper

        while (iterator.hasNext()) {
            entry = (Entry) iterator.next();
            BlockPosition blockposition = (BlockPosition) entry.getKey();
            TileEntity tileentity = (TileEntity) entry.getValue();
            int j = blockposition.getY() >> 4;

            if (this.f() || (i & 1 << j) != 0) {
                // Paper start - send signs separately
                if (tileentity instanceof TileEntitySign) {
                    if (SKIP_EXCESSIVE_SIGNS_LIMIT < 0 || ++totalSigns < SKIP_EXCESSIVE_SIGNS_LIMIT) {
                        this.extraPackets.add(tileentity.getUpdatePacket());
                    }
                    continue;
                }
                // Paper end
                NBTTagCompound nbttagcompound = tileentity.b();
                if (tileentity instanceof TileEntitySkull) { TileEntitySkull.sanitizeTileEntityUUID(nbttagcompound); } // Paper

                this.f.add(nbttagcompound);
            }
        }
        chunk.world.chunkPacketBlockController.modifyBlocks(this, chunkPacketInfo, forceLoad, null); // Paper - Anti-Xray - Modify blocks
    }

    // Paper start - Async-Anti-Xray - Getter and Setter for the ready flag
    public boolean isReady() {
        return this.ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
    // Paper end

    @Override
    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readInt();
        this.b = packetdataserializer.readInt();
        this.g = packetdataserializer.readBoolean();
        this.c = packetdataserializer.i();
        this.d = packetdataserializer.l();
        int i = packetdataserializer.i();

        if (i > 2097152) { // Paper - if this changes, update PacketEncoder
            throw new RuntimeException("Chunk Packet trying to allocate too much memory on read.");
        } else {
            this.e = new byte[i];
            packetdataserializer.readBytes(this.e);
            int j = packetdataserializer.i();

            this.f = Lists.newArrayList();

            for (int k = 0; k < j; ++k) {
                this.f.add(packetdataserializer.l());
            }

        }
    }

    @Override
    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeInt(this.a);
        packetdataserializer.writeInt(this.b);
        packetdataserializer.writeBoolean(this.g);
        packetdataserializer.d(this.c);
        packetdataserializer.a(this.d);
        packetdataserializer.d(this.e.length);
        packetdataserializer.writeBytes(this.e);
        packetdataserializer.d(this.f.size());
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            NBTTagCompound nbttagcompound = (NBTTagCompound) iterator.next();

            packetdataserializer.a(nbttagcompound);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    private ByteBuf i() {
        ByteBuf bytebuf = Unpooled.wrappedBuffer(this.e);

        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public int writeChunk(PacketDataSerializer packetDataSerializer, Chunk chunk, int chunkSectionSelector) { return this.a(packetDataSerializer, chunk, chunkSectionSelector); } // Paper - OBFHELPER
    public int a(PacketDataSerializer packetdataserializer, Chunk chunk, int i) {
        // Paper start - Add parameter
        return this.writeChunk(packetdataserializer, chunk, i, null);
    }
    public int writeChunk(PacketDataSerializer packetdataserializer, Chunk chunk, int i, ChunkPacketInfo<IBlockData> chunkPacketInfo) {
        // Paper end
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;

        int l;

        for (l = achunksection.length; k < l; ++k) {
            ChunkSection chunksection = achunksection[k];

            if (chunksection != Chunk.a && (!this.f() || !chunksection.c()) && (i & 1 << k) != 0) {
                j |= 1 << k;
                packetdataserializer.writeShort(chunksection.nonEmptyBlockCount); // Paper - Anti-Xray - Add chunk packet info
                chunksection.getBlocks().writeDataPaletteBlock(packetdataserializer, chunkPacketInfo, k); // Paper - Anti-Xray - Add chunk packet info
            }
        }

        if (this.f()) {
            BiomeBase[] abiomebase = chunk.getBiomeIndex();

            for (l = 0; l < abiomebase.length; ++l) {
                packetdataserializer.writeInt(IRegistry.BIOME.a(abiomebase[l])); // Paper - decompile fix
            }
        }

        return j;
    }

    protected int a(Chunk chunk, int i) {
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;

        for (int l = achunksection.length; k < l; ++k) {
            ChunkSection chunksection = achunksection[k];

            if (chunksection != Chunk.a && (!this.f() || !chunksection.c()) && (i & 1 << k) != 0) {
                j += chunksection.j();
            }
        }

        if (this.f()) {
            j += chunk.getBiomeIndex().length * 4;
        }

        return j;
    }

    public boolean f() {
        return this.g;
    }
}
