package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutVehicleMove implements Packet<PacketListenerPlayOut> {

    private double a;
    private double b;
    private double c;
    private float d;
    private float e;

    public PacketPlayOutVehicleMove() {}

    public PacketPlayOutVehicleMove(Entity entity) {
        this.a = entity.locX;
        this.b = entity.locY;
        this.c = entity.locZ;
        this.d = entity.yaw;
        this.e = entity.pitch;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.readDouble();
        this.b = packetdataserializer.readDouble();
        this.c = packetdataserializer.readDouble();
        this.d = packetdataserializer.readFloat();
        this.e = packetdataserializer.readFloat();
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.writeDouble(this.a);
        packetdataserializer.writeDouble(this.b);
        packetdataserializer.writeDouble(this.c);
        packetdataserializer.writeFloat(this.d);
        packetdataserializer.writeFloat(this.e);
    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }
}
