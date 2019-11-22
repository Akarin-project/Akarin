package net.minecraft.server;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

public class NBTTagByteArray extends NBTList<NBTTagByte> {

    private byte[] data;

    NBTTagByteArray() {}

    public NBTTagByteArray(byte[] abyte) {
        this.data = abyte;
    }

    public NBTTagByteArray(List<Byte> list) {
        this(a(list));
    }

    private static byte[] a(List<Byte> list) {
        byte[] abyte = new byte[list.size()];

        for (int i = 0; i < list.size(); ++i) {
            Byte obyte = (Byte) list.get(i);

            abyte[i] = obyte == null ? 0 : obyte;
        }

        return abyte;
    }

    @Override
    public void write(DataOutput dataoutput) throws IOException {
        dataoutput.writeInt(this.data.length);
        dataoutput.write(this.data);
    }

    @Override
    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(192L);
        int j = datainput.readInt();
        com.google.common.base.Preconditions.checkArgument( j < 1 << 24);

        nbtreadlimiter.a((long) (8 * j));
        this.data = new byte[j];
        datainput.readFully(this.data);
    }

    @Override
    public byte getTypeId() {
        return 7;
    }

    @Override
    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[B;");

        for (int i = 0; i < this.data.length; ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.data[i]).append('B');
        }

        return stringbuilder.append(']').toString();
    }

    @Override
    public NBTBase clone() {
        byte[] abyte = new byte[this.data.length];

        System.arraycopy(this.data, 0, abyte, 0, this.data.length);
        return new NBTTagByteArray(abyte);
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagByteArray && Arrays.equals(this.data, ((NBTTagByteArray) object).data);
    }

    public int hashCode() {
        return Arrays.hashCode(this.data);
    }

    @Override
    public IChatBaseComponent a(String s, int i) {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("B")).a(NBTTagByteArray.e);
        IChatBaseComponent ichatbasecomponent1 = (new ChatComponentText("[")).addSibling(ichatbasecomponent).a(";");

        for (int j = 0; j < this.data.length; ++j) {
            IChatBaseComponent ichatbasecomponent2 = (new ChatComponentText(String.valueOf(this.data[j]))).a(NBTTagByteArray.d);

            ichatbasecomponent1.a(" ").addSibling(ichatbasecomponent2).addSibling(ichatbasecomponent);
            if (j != this.data.length - 1) {
                ichatbasecomponent1.a(",");
            }
        }

        ichatbasecomponent1.a("]");
        return ichatbasecomponent1;
    }

    public byte[] getBytes() {
        return this.data;
    }

    public int size() {
        return this.data.length;
    }

    public NBTTagByte get(int i) {
        return new NBTTagByte(this.data[i]);
    }

    public NBTTagByte set(int i, NBTTagByte nbttagbyte) {
        byte b0 = this.data[i];

        this.data[i] = nbttagbyte.asByte();
        return new NBTTagByte(b0);
    }

    public void add(int i, NBTTagByte nbttagbyte) {
        this.data = ArrayUtils.add(this.data, i, nbttagbyte.asByte());
    }

    @Override
    public boolean a(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data[i] = ((NBTNumber) nbtbase).asByte();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean b(int i, NBTBase nbtbase) {
        if (nbtbase instanceof NBTNumber) {
            this.data = ArrayUtils.add(this.data, i, ((NBTNumber) nbtbase).asByte());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public NBTTagByte remove(int i) {
        byte b0 = this.data[i];

        this.data = ArrayUtils.remove(this.data, i);
        return new NBTTagByte(b0);
    }

    public void clear() {
        this.data = new byte[0];
    }
}
