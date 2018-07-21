package net.minecraft.server;

import com.destroystokyo.paper.antixray.ChunkPacketInfo; // Paper - Anti-Xray
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;
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
    // Paper start - use read write locks only during generation, disable once back on main thread
    private static final NoopLock NOOP_LOCK = new NoopLock();
    private java.util.concurrent.locks.Lock readLock = NOOP_LOCK;
    private java.util.concurrent.locks.Lock writeLock = NOOP_LOCK;

    private static class NoopLock extends ReentrantReadWriteLock.WriteLock {
        private NoopLock() {
            super(new ReentrantReadWriteLock());
        }

        @Override
        public final void lock() {
        }

        @Override
        public final void unlock() {

        }
    }

    synchronized void enableLocks() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        readLock = lock.readLock();
        writeLock = lock.writeLock();
    }
    synchronized void disableLocks() {
        readLock = NOOP_LOCK;
        writeLock = NOOP_LOCK;
    }

    private void b() {
        writeLock.lock();
    }
    private void c() {
        writeLock.unlock();
    }
    // Paper end

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
                // TODO: MathHelper.d(int i) can be used here instead (see DataPaletteBlock#a(NBTTagCompound nbttagcompound, String s, String s1)) but I don't understand the implementation
                // Count the bits of the maximum array index to initialize a data palette with enough space from the beginning
                // The length of the array is used because air is also added to the data palette from the beginning
                // Start with at least 4
                int maxIndex = predefinedObjects.length >> 4;
                int bitCount = 4;

                while (maxIndex != 0) {
                    maxIndex >>= 1;
                    bitCount++;
                }

                // Initialize with at least 15 free indixes
                this.initialize((1 << bitCount) - predefinedObjects.length < 16 ? bitCount + 1 : bitCount);
                this.addPredefinedObjects();
            }
        }
        // Paper end
    }

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

    // Paper start - Anti-Xray - Add predefined objects
    private void addPredefinedObjects() {
        if (this.predefinedObjects != null && this.getDataPalette() != this.getDataPaletteGlobal()) {
            for (int i = 0; i < this.predefinedObjects.length; i++) {
                this.getDataPalette().getOrCreateIdFor(this.predefinedObjects[i]);
            }
        }
    }
    // Paper end

    public int onResize(int i, T t0) {
        this.b();
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
        this.c();
        return j;
    }

    public void setBlock(int i, int j, int k, T t0) {
        this.b();
        this.setBlockIndex(b(i, j, k), t0);
        this.c();
    }

    protected void setBlockIndex(int i, T t0) {
        int j = this.h.a(t0);

        this.a.a(i, j);
    }

    public T a(int i, int j, int k) {
        return this.a(b(i, j, k));
    }

    protected T a(int i) {
        try { // Paper start - read lock
            readLock.lock();
            T object = this.h.a(this.a.a(i)); // Paper - decompile fix
            return (T)(object == null ? this.g : object);
        } finally {
            readLock.unlock();
        } // Paper end
    }

    // Paper start - Anti-Xray - Support default methods
    public void writeDataPaletteBlock(PacketDataSerializer packetDataSerializer) { this.b(packetDataSerializer); }
    public void b(PacketDataSerializer packetdataserializer) {
        this.b(packetdataserializer, null, 0);
    }
    // Paper end

    public void writeDataPaletteBlock(PacketDataSerializer packetDataSerializer, ChunkPacketInfo<T> chunkPacketInfo, int chunkSectionIndex) { this.b(packetDataSerializer, chunkPacketInfo, chunkSectionIndex); } // Paper - OBFHELPER // Paper - Anti-Xray - Add chunk packet info
    public void b(PacketDataSerializer packetdataserializer, ChunkPacketInfo<T> chunkPacketInfo, int chunkSectionIndex) { // Paper - Anti-Xray - Add chunk packet info
        this.b();
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
        this.c();
    }

    public void a(NBTTagCompound nbttagcompound, String s, String s1) {
        this.b();
        NBTTagList nbttaglist = nbttagcompound.getList(s, 10);
        // Paper - Anti-Xray - TODO: Should this.predefinedObjects.length just be added here (faster) or should the contents be compared to calculate the size (less RAM)?
        int i = Math.max(4, MathHelper.d(nbttaglist.size() + (this.predefinedObjects == null ? 0 : this.predefinedObjects.length))); // Paper - Anti-Xray - Calculate the size with predefined objects

        if (true || i != this.i) { // Paper - Anti-Xray - Not initialized yet
            this.b(i);
        }

        this.h.a(nbttaglist);
        this.addPredefinedObjects(); // Paper - Anti-Xray - Add predefined objects
        long[] along = nbttagcompound.o(s1);
        int j = along.length * 64 / 4096;

        if (this.h == this.b) {
            DataPalette<T> datapalette = new DataPaletteHash<>(this.d, i, this.c, this.e, this.f);

            datapalette.a(nbttaglist);
            DataBits databits = new DataBits(i, 4096, along);

            for (int k = 0; k < 4096; ++k) {
                this.a.a(k, this.b.a(datapalette.a(databits.a(k))));
            }
        } else if (j == this.i) {
            System.arraycopy(along, 0, this.a.a(), 0, along.length);
        } else {
            DataBits databits1 = new DataBits(j, 4096, along);

            for (int l = 0; l < 4096; ++l) {
                this.a.a(l, databits1.a(l));
            }
        }

        this.c();
    }

    public void b(NBTTagCompound nbttagcompound, String s, String s1) {
        this.b();
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
            databits.a(k, aint[k]);
        }

        nbttagcompound.a(s1, databits.a());
        this.c();
    }

    public int a() {
        return 1 + this.h.a() + PacketDataSerializer.a(this.a.b()) + this.a.a().length * 8;
    }
}
