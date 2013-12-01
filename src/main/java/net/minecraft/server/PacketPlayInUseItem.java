package net.minecraft.server;

import java.io.IOException;

public class PacketPlayInUseItem implements Packet<PacketListenerPlayIn> {

    private BlockPosition a;
    private EnumDirection b;
    private EnumHand c;
    private float d;
    private float e;
    private float f;

    public PacketPlayInUseItem() {}

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.e();
        this.b = (EnumDirection) packetdataserializer.a(EnumDirection.class);
        this.c = (EnumHand) packetdataserializer.a(EnumHand.class);
        this.d = packetdataserializer.readFloat();
        this.e = packetdataserializer.readFloat();
        this.f = packetdataserializer.readFloat();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a(this.a);
        packetdataserializer.a((Enum) this.b);
        packetdataserializer.a((Enum) this.c);
        packetdataserializer.writeFloat(this.d);
        packetdataserializer.writeFloat(this.e);
        packetdataserializer.writeFloat(this.f);
    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    public BlockPosition b() {
        return this.a;
    }

    public EnumDirection c() {
        return this.b;
    }

    public EnumHand d() {
        return this.c;
    }

    public float e() {
        return this.d;
    }

    public float f() {
        return this.e;
    }

    public float g() {
        return this.f;
    }
}
