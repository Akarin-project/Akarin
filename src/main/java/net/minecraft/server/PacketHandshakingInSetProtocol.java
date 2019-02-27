package net.minecraft.server;

import java.io.IOException;

public class PacketHandshakingInSetProtocol implements Packet<PacketHandshakingInListener> {

    private int a;
    public String hostname;
    public int port;
    private EnumProtocol d;

    public PacketHandshakingInSetProtocol() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.hostname = packetdataserializer.e(255);
        this.port = packetdataserializer.readUnsignedShort();
        this.d = EnumProtocol.a(packetdataserializer.g());
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a(this.hostname);
        packetdataserializer.writeShort(this.port);
        packetdataserializer.d(this.d.a());
    }

    public void a(PacketHandshakingInListener packethandshakinginlistener) {
        packethandshakinginlistener.a(this);
    }

    public EnumProtocol b() {
        return this.d;
    }

    public int c() {
        return this.a;
    }
}
