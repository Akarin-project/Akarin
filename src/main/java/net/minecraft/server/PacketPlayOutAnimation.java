package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutAnimation implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;

    public PacketPlayOutAnimation() {}

    public PacketPlayOutAnimation(Entity entity, int i) {
        this.a = entity.getId();
        this.b = i;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.readUnsignedByte();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.writeByte(this.b);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
