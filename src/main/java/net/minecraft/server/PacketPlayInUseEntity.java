package net.minecraft.server;

import java.io.IOException;
import javax.annotation.Nullable;

public class PacketPlayInUseEntity implements Packet<PacketListenerPlayIn> {

    private int a;
    private PacketPlayInUseEntity.EnumEntityUseAction action;
    private Vec3D c;
    private EnumHand d;

    public PacketPlayInUseEntity() {}

    public PacketPlayInUseEntity(Entity entity) {
        this.a = entity.getId();
        this.action = PacketPlayInUseEntity.EnumEntityUseAction.ATTACK;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = packetdataserializer.g();
        this.action = (PacketPlayInUseEntity.EnumEntityUseAction) packetdataserializer.a(PacketPlayInUseEntity.EnumEntityUseAction.class);
        if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            this.c = new Vec3D((double) packetdataserializer.readFloat(), (double) packetdataserializer.readFloat(), (double) packetdataserializer.readFloat());
        }

        if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT || this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            this.d = (EnumHand) packetdataserializer.a(EnumHand.class);
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.d(this.a);
        packetdataserializer.a((Enum) this.action);
        if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            packetdataserializer.writeFloat((float) this.c.x);
            packetdataserializer.writeFloat((float) this.c.y);
            packetdataserializer.writeFloat((float) this.c.z);
        }

        if (this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT || this.action == PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) {
            packetdataserializer.a((Enum) this.d);
        }

    }

    public void a(PacketListenerPlayIn packetlistenerplayin) {
        packetlistenerplayin.a(this);
    }

    @Nullable
    public Entity a(World world) {
        return world.getEntity(this.a);
    }

    public PacketPlayInUseEntity.EnumEntityUseAction b() {
        return this.action;
    }

    public EnumHand c() {
        return this.d;
    }

    public Vec3D d() {
        return this.c;
    }

    public static enum EnumEntityUseAction {

        INTERACT, ATTACK, INTERACT_AT;

        private EnumEntityUseAction() {}
    }
}
