package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutEntityTeleport implements Packet<PacketListenerPlayOut> {

    private int a;
    private double b;
    private double c;
    private double d;
    private byte e;
    private byte f;
    private boolean g;

    public PacketPlayOutEntityTeleport() {}

    public PacketPlayOutEntityTeleport(Entity entity) {
        this.a = entity.getId();
        this.b = entity.locX;
        this.c = entity.locY;
        this.d = entity.locZ;
        this.e = (byte) ((int) (entity.yaw * 256.0F / 360.0F));
        this.f = (byte) ((int) (entity.pitch * 256.0F / 360.0F));
        this.g = entity.onGround;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.b = packetdataserializer.readDouble();
        this.c = packetdataserializer.readDouble();
        this.d = packetdataserializer.readDouble();
        this.e = packetdataserializer.readByte();
        this.f = packetdataserializer.readByte();
        this.g = packetdataserializer.readBoolean();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.writeDouble(this.b);
        packetdataserializer.writeDouble(this.c);
        packetdataserializer.writeDouble(this.d);
        packetdataserializer.writeByte(this.e);
        packetdataserializer.writeByte(this.f);
        packetdataserializer.writeBoolean(this.g);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
