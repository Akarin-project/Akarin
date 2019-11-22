package net.minecraft.server;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap.Entry;
import com.destroystokyo.paper.antixray.ChunkPacketInfo; // Paper - Anti-Xray
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataPaletteBlock<T> implements DataPaletteExpandable<T> {

    private final DataPalette<T> b; private final DataPalette<T> getDataPaletteGlobal() { return this.b; } // Paper - OBFHELPER
    private final DataPaletteExpandable<T> c = (i, object) -> {
        return 0;
    };
    private final RegistryBlockID<T> d;
    private final Function<NBTTagCompound, T> e;
    private final Function<T, NBTTagCompound> f;
    private final T g;
    private final T[] predefinedObjects; // Paper - Anti-Xray - Add predefined objects
    protected DataBits a; protected DataBits getDataBits() { return this.a; } // Paper - OBFHELPER
    private DataPalette<T> h; private DataPalette<T> getDataPalette() { return this.h; } // Paper - OBFHELPER
    private int i; private int getBitsPerObject() { return this.i; } // Paper - OBFHELPER
    private final com.destroystokyo.paper.util.ReentrantLockWithGetOwner j = new com.destroystokyo.paper.util.ReentrantLockWithGetOwner(); private com.destroystokyo.paper.util.ReentrantLockWithGetOwner getLock() { return this.j; } // Paper - change type to ReentrantLockWithGetOwner // Paper - OBFHELPER

    public void a() {
        // Paper start - log other thread
        Thread owningThread;
        if (this.j.isLocked() && (owningThread = this.getLock().getOwner()) != null && owningThread != Thread.currentThread()) {
            // Paper end
            String s = (String) Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((thread) -> {
                return thread.getName() + ": \n\tat " + (String) Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
            }).collect(Collectors.joining("\n"));
            CrashReport crashreport = new CrashReport("Writing into PalettedContainer from multiple threads (other thread: name: " + owningThread.getName() + ", class: " + owningThread.getClass().toString() + ")", new IllegalStateException()); // Paper - log other thread
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Thread dumps");

            crashreportsystemdetails.a("Thread dumps", (Object) s);
            throw new ReportedException(crashreport);
        } else {
            this.j.lock();
        }
    }

    public void b() {
        this.j.unlock();
    }

    public DataPaletteBlock(DataPalette<T> datapalette, RegistryBlockID<T> registryblockid, Function<NBTTagCompound, T> function, Function<T, NBTTagCompound> function1, T t0) {
        // Paper start - Anti-Xray - Support default constructor
        this(datapalette, registryblockid, function, function1, t0, null, true);
    }

    public DataPaletteBlock(DataPalette<T> datapalette, RegistryBlockID<T> registryblockid, Function<NBTTagCompound, T> function, Function<T, NBTTagCompound> function1, T t0, T[] predefinedObjects, boolean initialize) {
        // Paper end - Anti-Xray - Add predefined objects
        this.b = datapalette;
        this.d = registryblockid;
        this.e = function;
        this.f = function1;
        this.g = t0;
        // Paper start - Anti-Xray - Add predefined objects
        this.predefinedObjects = predefinedObjects;

        if (initialize) {
            if (predefinedObjects == null) {
                // Default
                this.initialize(4);
            } else {
                // MathHelper.d() is trailingBits(roundCeilPow2(n)), alternatively; (int)ceil(log2(n)); however it's trash, use numberOfLeadingZeros instead
                // Count the bits of the maximum array index to initialize a data palette with enough space from the beginning
                // The length of the array is used because air is also added to the data palette from the beginning
                // Start with at least 4
                int maxIndex = predefinedObjects.length >> 4;
                int bitCount = (32 - Integer.numberOfLeadingZeros(Math.max(16, maxIndex) - 1));

                // Initialize with at least 15 free indixes
                this.initialize((1 << bitCount) - predefinedObjects.length < 16 ? bitCount + 1 : bitCount);
                this.addPredefinedObjects();
            }
        }
        // Paper end
    }

    // Paper start - Anti-Xray - Add predefined objects
    private void addPredefinedObjects() {
        if (this.predefinedObjects != null && this.getDataPalette() != this.getDataPaletteGlobal()) {
            for (int i = 0; i < this.predefinedObjects.length; i++) {
                this.getDataPalette().getOrCreateIdFor(this.predefinedObjects[i]);
            }
        }
    }
    // Paper end

    private static int b(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

    private void initialize(int bitsPerObject) { this.b(bitsPerObject); } // Paper - OBFHELPER
    private void b(int i) {
        if (i != this.i) {
            this.i = i;
            if (this.i <= 4) {
                this.i = 4;
                this.h = new DataPaletteLinear<>(this.d, this.i, this, this.e);
            } else if (this.i < 9) {
                this.h = new DataPaletteHash<>(this.d, this.i, this, this.e, this.f);
            } else {
                this.h = this.b;
                this.i = MathHelper.d(this.d.a());
            }

            this.h.a(this.g);
            this.a = new DataBits(this.i, 4096);
        }
    }

    @Override
    public int onResize(int i, T t0) {
        this.a();
        DataBits databits = this.a;
        DataPalette<T> datapalette = this.h;

        this.b(i);

        int j;

        this.addPredefinedObjects(); // Paper - Anti-Xray - Add predefined objects
        for (j = 0; j < databits.b(); ++j) {
            T t1 = datapalette.a(databits.a(j));

            if (t1 != null) {
                this.setBlockIndex(j, t1);
            }
        }

        j = this.h.a(t0);
        this.b();
        return j;
    }

    public T setBlock(int i, int j, int k, T t0) {
        this.a();
        T t1 = this.a(b(i, j, k), t0);

        this.b();
        return t1;
    }

    public T b(int i, int j, int k, T t0) {
        return this.a(b(i, j, k), t0);
    }

    protected T a(int i, T t0) {
        int j = this.h.a(t0);
        int k = this.a.a(i, j);
        T t1 = this.h.a(k);

        return t1 == null ? this.g : t1;
    }

    protected void setBlockIndex(int i, T t0) {
        int j = this.h.a(t0);

        this.a.b(i, j);
    }

    public T a(int i, int j, int k) {
        return this.a(b(i, j, k));
    }

    protected T a(int i) {
        T t0 = this.h.a(this.a.a(i));

        return t0 == null ? this.g : t0;
    }

    public void writeDataPaletteBlock(PacketDataSerializer packetDataSerializer) { this.b(packetDataSerializer); } // Paper - OBFHELPER
    public void b(PacketDataSerializer packetdataserializer) {
        // Paper start - add parameters
        this.writeDataPaletteBlock(packetdataserializer, null, 0);
    }
    public void writeDataPaletteBlock(PacketDataSerializer packetdataserializer, ChunkPacketInfo<T> chunkPacketInfo, int chunkSectionIndex) {
        // Paper end
        this.a();
        packetdataserializer.writeByte(this.i);
        this.h.b(packetdataserializer);

        // Paper start - Anti-Xray - Add chunk packet info
        if (chunkPacketInfo != null) {
            chunkPacketInfo.setBitsPerObject(chunkSectionIndex, this.getBitsPerObject());
            chunkPacketInfo.setDataPalette(chunkSectionIndex, this.getDataPalette());
            chunkPacketInfo.setDataBitsIndex(chunkSectionIndex, packetdataserializer.writerIndex() + PacketDataSerializer.countBytes(this.getDataBits().getDataBits().length));
            chunkPacketInfo.setPredefinedObjects(chunkSectionIndex, this.predefinedObjects);
        }
        // Paper end

        packetdataserializer.a(this.a.a());
        this.b();
    }

    public void a(NBTTagList nbttaglist, long[] along) {
        this.a();
        // Paper - Anti-Xray - TODO: Should this.predefinedObjects.length just be added here (faster) or should the contents be compared to calculate the size (less RAM)?
        int i = Math.max(4, MathHelper.d(nbttaglist.size() + (this.predefinedObjects == null ? 0 : this.predefinedObjects.length))); // Paper - Anti-Xray - Calculate the size with predefined objects

        if (true || i != this.i) { // Paper - Anti-Xray - Not initialized yet
            this.b(i);
        }

        this.h.a(nbttaglist);
        this.addPredefinedObjects(); // Paper - Anti-Xray - Add predefined objects
        int j = along.length * 64 / 4096;

        if (this.h == this.b) {
            DataPalette<T> datapalette = new DataPaletteHash<>(this.d, i, this.c, this.e, this.f);

            datapalette.a(nbttaglist);
            DataBits databits = new DataBits(i, 4096, along);

            for (int k = 0; k < 4096; ++k) {
                this.a.b(k, this.b.a(datapalette.a(databits.a(k))));
            }
        } else if (j == this.i) {
            System.arraycopy(along, 0, this.a.a(), 0, along.length);
        } else {
            DataBits databits1 = new DataBits(j, 4096, along);

            for (int l = 0; l < 4096; ++l) {
                this.a.b(l, databits1.a(l));
            }
        }

        this.b();
    }

    public void a(NBTTagCompound nbttagcompound, String s, String s1) {
        this.a();
        DataPaletteHash<T> datapalettehash = new DataPaletteHash<>(this.d, this.i, this.c, this.e, this.f);

        datapalettehash.a(this.g);
        int[] aint = new int[4096];

        for (int i = 0; i < 4096; ++i) {
            aint[i] = datapalettehash.a(this.a(i));
        }

        NBTTagList nbttaglist = new NBTTagList();

        datapalettehash.b(nbttaglist);
        nbttagcompound.set(s, nbttaglist);
        int j = Math.max(4, MathHelper.d(nbttaglist.size()));
        DataBits databits = new DataBits(j, 4096);

        for (int k = 0; k < aint.length; ++k) {
            databits.b(k, aint[k]);
        }

        nbttagcompound.a(s1, databits.a());
        this.b();
    }

    public int c() {
        return 1 + this.h.a() + PacketDataSerializer.a(this.a.b()) + this.a.a().length * 8;
    }

    public boolean a(T t0) {
        return this.h.b(t0);
    }

    public void a(DataPaletteBlock.a<T> datapaletteblock_a) {
        Int2IntOpenHashMap int2intopenhashmap = new Int2IntOpenHashMap();

        this.a.a((i) -> {
            int2intopenhashmap.put(i, int2intopenhashmap.get(i) + 1);
        });
        int2intopenhashmap.int2IntEntrySet().forEach((entry) -> {
            datapaletteblock_a.accept(this.h.a(entry.getIntKey()), entry.getIntValue());
        });
    }

    @FunctionalInterface
    public interface a<T> {

        void accept(T t0, int i);
    }
}
