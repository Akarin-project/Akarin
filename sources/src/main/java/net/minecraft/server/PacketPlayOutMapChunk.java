package net.minecraft.server;

import com.google.common.collect.Lists;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

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

    public PacketPlayOutMapChunk() {}

    public PacketPlayOutMapChunk(Chunk chunk, int i) {
        this.a = chunk.locX;
        this.b = chunk.locZ;
        this.f = i == '\uffff';
        boolean flag = chunk.getWorld().worldProvider.g();

        this.d = allocateBuffer(this.a(chunk, flag, i)); // Akarin
        this.c = this.a(new PacketDataSerializer(this.d), chunk, flag, i); // Akarin
        this.e = Lists.newArrayList();
        Iterator iterator = chunk.getTileEntities().entrySet().iterator();

        while (iterator.hasNext()) {
            Entry entry = (Entry) iterator.next();
            BlockPosition blockposition = (BlockPosition) entry.getKey();
            TileEntity tileentity = (TileEntity) entry.getValue();
            int j = blockposition.getY() >> 4;

            if (this.f() || (i & 1 << j) != 0) {
                NBTTagCompound nbttagcompound = tileentity.aa_();
                if (tileentity instanceof TileEntitySkull) { TileEntitySkull.sanitizeTileEntityUUID(nbttagcompound); } // Paper

                this.e.add(nbttagcompound);
            }
        }

    }

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
        packetdataserializer.writeBytes(this.d);
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

    private ByteBuf allocateBuffer(int expectedCapacity) { return h(expectedCapacity); } // Akarin - OBFHELPER
    private ByteBuf h(int expectedCapacity) { // Akarin - added argument
        ByteBuf bytebuf = Unpooled.buffer(expectedCapacity); // Akarin

        bytebuf.writerIndex(0);
        return bytebuf;
    }

    public int a(PacketDataSerializer packetdataserializer, Chunk chunk, boolean flag, int i) {
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;

        int l;

        for (l = achunksection.length; k < l; ++k) {
            ChunkSection chunksection = achunksection[k];

            if (chunksection != Chunk.a && (!this.f() || !chunksection.a()) && (i & 1 << k) != 0) {
                j |= 1 << k;
                chunksection.getBlocks().b(packetdataserializer);
                packetdataserializer.writeBytes(chunksection.getEmittedLightArray().asBytes());
                if (flag) {
                    packetdataserializer.writeBytes(chunksection.getSkyLightArray().asBytes());
                }
            }
        }

        if (this.f()) {
            BiomeBase[] abiomebase = chunk.getBiomeIndex();

            for (l = 0; l < abiomebase.length; ++l) {
                packetdataserializer.writeInt(BiomeBase.REGISTRY_ID.a((BiomeBase) abiomebase[l]));
            }
        }

        return j;
    }

    protected int a(Chunk chunk, boolean flag, int i) {
        int j = 0;
        ChunkSection[] achunksection = chunk.getSections();
        int k = 0;

        for (int l = achunksection.length; k < l; ++k) {
            ChunkSection chunksection = achunksection[k];

            if (chunksection != Chunk.a && (!this.f() || !chunksection.a()) && (i & 1 << k) != 0) {
                j += chunksection.getBlocks().a();
                j += chunksection.getEmittedLightArray().asBytes().length;
                if (flag) {
                    j += chunksection.getSkyLightArray().asBytes().length;
                }
            }
        }

        if (this.f()) {
            j += chunk.getBiomeIndex().length * 4;
        }

        return j;
    }

    public boolean f() {
        return this.f;
    }
}
