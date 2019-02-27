package net.minecraft.server;

import javax.annotation.Nullable;

public interface DataPalette<T> {

    int a(T t0);

    @Nullable
    T a(int i);

    void b(PacketDataSerializer packetdataserializer);

    int a();

    void a(NBTTagList nbttaglist);
}
