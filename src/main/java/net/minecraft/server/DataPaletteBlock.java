package net.minecraft.server;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataPaletteBlock<T> implements DataPaletteExpandable<T> {

    private final DataPalette<T> b;
    private final DataPaletteExpandable<T> c = (i, object) -> {
        return 0;
    };
    private final RegistryBlockID<T> d;
    private final Function<NBTTagCompound, T> e;
    private final Function<T, NBTTagCompound> f;
    private final T g;
    protected DataBits a;
    private DataPalette<T> h;
    private int i;
    private final ReentrantLock j = new ReentrantLock();

    private void b() {
        if (this.j.isLocked() && !this.j.isHeldByCurrentThread()) {
            String s = (String) Thread.getAllStackTraces().keySet().stream().filter(Objects::nonNull).map((thread) -> {
                return thread.getName() + ": \n\tat " + (String) Arrays.stream(thread.getStackTrace()).map(Object::toString).collect(Collectors.joining("\n\tat "));
            }).collect(Collectors.joining("\n"));
            CrashReport crashreport = new CrashReport("Writing into PalettedContainer from multiple threads", new IllegalStateException());
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Thread dumps");

            crashreportsystemdetails.a("Thread dumps", (Object) s);
            throw new ReportedException(crashreport);
        } else {
            this.j.lock();
        }
    }

    private void c() {
        this.j.unlock();
    }

    public DataPaletteBlock(DataPalette<T> datapalette, RegistryBlockID<T> registryblockid, Function<NBTTagCompound, T> function, Function<T, NBTTagCompound> function1, T t0) {
        this.b = datapalette;
        this.d = registryblockid;
        this.e = function;
        this.f = function1;
        this.g = t0;
        this.b(4);
    }

    private static int b(int i, int j, int k) {
        return j << 8 | k << 4 | i;
    }

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

    public int onResize(int i, T t0) {
        this.b();
        DataBits databits = this.a;
        DataPalette<T> datapalette = this.h;

        this.b(i);

        int j;

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
        T t0 = this.h.a(this.a.a(i));

        return t0 == null ? this.g : t0;
    }

    public void b(PacketDataSerializer packetdataserializer) {
        this.b();
        packetdataserializer.writeByte(this.i);
        this.h.b(packetdataserializer);
        packetdataserializer.a(this.a.a());
        this.c();
    }

    public void a(NBTTagCompound nbttagcompound, String s, String s1) {
        this.b();
        NBTTagList nbttaglist = nbttagcompound.getList(s, 10);
        int i = Math.max(4, MathHelper.d(nbttaglist.size()));

        if (i != this.i) {
            this.b(i);
        }

        this.h.a(nbttaglist);
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
