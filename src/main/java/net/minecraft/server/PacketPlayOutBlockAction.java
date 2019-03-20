package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutBlockAction implements Packet<PacketListenerPlayOut> {

    private BlockPosition a;
    private int b;
    private int c;
    private Block d;

    public PacketPlayOutBlockAction() {}

    public PacketPlayOutBlockAction(BlockPosition blockposition, Block block, int i, int j) {
        this.a = blockposition;
        this.d = block;
        this.b = i;
        this.c = j;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = packetdataserializer.readUnsignedByte();
        this.c = packetdataserializer.readUnsignedByte();
        this.d = (Block) IRegistry.BLOCK.fromId(packetdataserializer.g());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.writeByte(this.b);
        packetdataserializer.writeByte(this.c);
        packetdataserializer.d(IRegistry.BLOCK.a(this.d)); // Akarin - fixes decompile error
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
    // Akarin start
    @Override
    public boolean canDispatchImmediately() {
        return true;
    }
    // Akarin end
}
