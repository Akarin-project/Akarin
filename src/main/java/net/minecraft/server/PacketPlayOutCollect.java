package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutCollect implements Packet<PacketListenerPlayOut> {

    private int a;
    private int b;
    private int c;

    public PacketPlayOutCollect() {}

    public PacketPlayOutCollect(int i, int j, int k) {
        this.a = i;
        this.b = j;
        this.c = k;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.g();
        this.c = packetdataserializer.g();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.d(this.b);
        packetdataserializer.d(this.c);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
