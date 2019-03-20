package net.minecraft.server;

import java.io.IOException;

public class PacketPlayOutCombatEvent implements Packet<PacketListenerPlayOut> {

    public PacketPlayOutCombatEvent.EnumCombatEventType a;
    public int b;
    public int c;
    public int d;
    public IChatBaseComponent e;

    public PacketPlayOutCombatEvent() {}

    public PacketPlayOutCombatEvent(CombatTracker combattracker, PacketPlayOutCombatEvent.EnumCombatEventType packetplayoutcombatevent_enumcombateventtype) {
        this(combattracker, packetplayoutcombatevent_enumcombateventtype, new ChatComponentText(""));
    }

    public PacketPlayOutCombatEvent(CombatTracker combattracker, PacketPlayOutCombatEvent.EnumCombatEventType packetplayoutcombatevent_enumcombateventtype, IChatBaseComponent ichatbasecomponent) {
        this.a = packetplayoutcombatevent_enumcombateventtype;
        EntityLiving entityliving = combattracker.c();

        switch (packetplayoutcombatevent_enumcombateventtype) {
        case END_COMBAT:
            this.d = combattracker.f();
            this.c = entityliving == null ? -1 : entityliving.getId();
            break;
        case ENTITY_DIED:
            this.b = combattracker.h().getId();
            this.c = entityliving == null ? -1 : entityliving.getId();
            this.e = ichatbasecomponent;
        }

    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        this.a = (PacketPlayOutCombatEvent.EnumCombatEventType) packetdataserializer.a(PacketPlayOutCombatEvent.EnumCombatEventType.class);
        if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT) {
            this.d = packetdataserializer.g();
            this.c = packetdataserializer.readInt();
        } else if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED) {
            this.b = packetdataserializer.g();
            this.c = packetdataserializer.readInt();
            this.e = packetdataserializer.f();
        }

    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        packetdataserializer.a((Enum) this.a);
        if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.END_COMBAT) {
            packetdataserializer.d(this.d);
            packetdataserializer.writeInt(this.c);
        } else if (this.a == PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED) {
            packetdataserializer.d(this.b);
            packetdataserializer.writeInt(this.c);
            packetdataserializer.a(this.e);
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    public boolean a() {
        return this.a == PacketPlayOutCombatEvent.EnumCombatEventType.ENTITY_DIED;
    }

    public static enum EnumCombatEventType {

        ENTER_COMBAT, END_COMBAT, ENTITY_DIED;

        private EnumCombatEventType() {}
    }
}
