package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutBlockChange implements Packet<PacketListenerPlayOut> {

    private BlockPosition a;
    public IBlockData block;

    public PacketPlayOutBlockChange() {}

    public PacketPlayOutBlockChange(IBlockAccess iblockaccess, BlockPosition blockposition) {
        this.a = blockposition;
        this.block = iblockaccess.getType(blockposition);
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.block = (IBlockData) Block.REGISTRY_ID.fromId(packetdataserializer.g());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.d(Block.getCombinedId(this.block));
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
