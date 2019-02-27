package net.minecraft.server;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NBTTagList extends NBTList<NBTBase> {

    private static final Logger f = LogManager.getLogger();
    private List<NBTBase> list = Lists.newArrayList();
    private byte type = 0;

    public NBTTagList() {}

    public void write(DataOutput dataoutput) throws IOException {
        if (this.list.isEmpty()) {
            this.type = 0;
        } else {
            this.type = ((NBTBase) this.list.get(0)).getTypeId();
        }

        dataoutput.writeByte(this.type);
        dataoutput.writeInt(this.list.size());

        for (int i = 0; i < this.list.size(); ++i) {
            ((NBTBase) this.list.get(i)).write(dataoutput);
        }

    }

    public void load(DataInput datainput, int i, NBTReadLimiter nbtreadlimiter) throws IOException {
        nbtreadlimiter.a(296L);
        if (i > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.type = datainput.readByte();
            int j = datainput.readInt();

            if (this.type == 0 && j > 0) {
                throw new RuntimeException("Missing type on ListTag");
            } else {
                nbtreadlimiter.a(32L * (long) j);
                this.list = Lists.newArrayListWithCapacity(j);

                for (int k = 0; k < j; ++k) {
                    NBTBase nbtbase = NBTBase.createTag(this.type);

                    nbtbase.load(datainput, i + 1, nbtreadlimiter);
                    this.list.add(nbtbase);
                }

            }
        }
    }

    public byte getTypeId() {
        return 9;
    }

    public String toString() {
        StringBuilder stringbuilder = new StringBuilder("[");

        for (int i = 0; i < this.list.size(); ++i) {
            if (i != 0) {
                stringbuilder.append(',');
            }

            stringbuilder.append(this.list.get(i));
        }

        return stringbuilder.append(']').toString();
    }

    public boolean add(NBTBase nbtbase) {
        if (nbtbase.getTypeId() == 0) {
            NBTTagList.f.warn("Invalid TagEnd added to ListTag");
            return false;
        } else {
            if (this.type == 0) {
                this.type = nbtbase.getTypeId();
            } else if (this.type != nbtbase.getTypeId()) {
                NBTTagList.f.warn("Adding mismatching tag types to tag list");
                return false;
            }

            this.list.add(nbtbase);
            return true;
        }
    }

    public NBTBase set(int i, NBTBase nbtbase) {
        if (nbtbase.getTypeId() == 0) {
            NBTTagList.f.warn("Invalid TagEnd added to ListTag");
            return (NBTBase) this.list.get(i);
        } else if (i >= 0 && i < this.list.size()) {
            if (this.type == 0) {
                this.type = nbtbase.getTypeId();
            } else if (this.type != nbtbase.getTypeId()) {
                NBTTagList.f.warn("Adding mismatching tag types to tag list");
                return (NBTBase) this.list.get(i);
            }

            return (NBTBase) this.list.set(i, nbtbase);
        } else {
            NBTTagList.f.warn("index out of bounds to set tag in tag list");
            return null;
        }
    }

    public NBTBase remove(int i) {
        return (NBTBase) this.list.remove(i);
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    public NBTTagCompound getCompound(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 10) {
                return (NBTTagCompound) nbtbase;
            }
        }

        return new NBTTagCompound();
    }

    public NBTTagList f(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 9) {
                return (NBTTagList) nbtbase;
            }
        }

        return new NBTTagList();
    }

    public short g(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 2) {
                return ((NBTTagShort) nbtbase).asShort();
            }
        }

        return 0;
    }

    public int h(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 3) {
                return ((NBTTagInt) nbtbase).asInt();
            }
        }

        return 0;
    }

    public int[] i(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 11) {
                return ((NBTTagIntArray) nbtbase).d();
            }
        }

        return new int[0];
    }

    public double k(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 6) {
                return ((NBTTagDouble) nbtbase).asDouble();
            }
        }

        return 0.0D;
    }

    public float l(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            if (nbtbase.getTypeId() == 5) {
                return ((NBTTagFloat) nbtbase).asFloat();
            }
        }

        return 0.0F;
    }

    public String getString(int i) {
        if (i >= 0 && i < this.list.size()) {
            NBTBase nbtbase = (NBTBase) this.list.get(i);

            return nbtbase.getTypeId() == 8 ? nbtbase.asString() : nbtbase.toString();
        } else {
            return "";
        }
    }

    public NBTBase get(int i) {
        return (NBTBase) (i >= 0 && i < this.list.size() ? (NBTBase) this.list.get(i) : new NBTTagEnd());
    }

    public int size() {
        return this.list.size();
    }

    public NBTBase c(int i) {
        return (NBTBase) this.list.get(i);
    }

    public void a(int i, NBTBase nbtbase) {
        this.list.set(i, nbtbase);
    }

    public void b(int i) {
        this.list.remove(i);
    }

    public NBTTagList clone() {
        NBTTagList nbttaglist = new NBTTagList();

        nbttaglist.type = this.type;
        Iterator iterator = this.list.iterator();

        while (iterator.hasNext()) {
            NBTBase nbtbase = (NBTBase) iterator.next();
            NBTBase nbtbase1 = nbtbase.clone();

            nbttaglist.list.add(nbtbase1);
        }

        return nbttaglist;
    }

    public boolean equals(Object object) {
        return this == object ? true : object instanceof NBTTagList && Objects.equals(this.list, ((NBTTagList) object).list);
    }

    public int hashCode() {
        return this.list.hashCode();
    }

    public IChatBaseComponent a(String s, int i) {
        if (this.isEmpty()) {
            return new ChatComponentText("[]");
        } else {
            ChatComponentText chatcomponenttext = new ChatComponentText("[");

            if (!s.isEmpty()) {
                chatcomponenttext.a("\n");
            }

            for (int j = 0; j < this.list.size(); ++j) {
                ChatComponentText chatcomponenttext1 = new ChatComponentText(Strings.repeat(s, i + 1));

                chatcomponenttext1.addSibling(((NBTBase) this.list.get(j)).a(s, i + 1));
                if (j != this.list.size() - 1) {
                    chatcomponenttext1.a(String.valueOf(',')).a(s.isEmpty() ? " " : "\n");
                }

                chatcomponenttext.addSibling(chatcomponenttext1);
            }

            if (!s.isEmpty()) {
                chatcomponenttext.a("\n").a(Strings.repeat(s, i));
            }

            chatcomponenttext.a("]");
            return chatcomponenttext;
        }
    }

    public int d() {
        return this.type;
    }
}
