package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldPersistentData {

    private static final Logger LOGGER = LogManager.getLogger();
    public final Map<String, PersistentBase> data = Maps.newHashMap();
    private final DataFixer c;
    private final File d;

    public WorldPersistentData(File file, DataFixer datafixer) {
        this.c = datafixer;
        this.d = file;
    }

    private File a(String s) {
        return new File(this.d, s + ".dat");
    }

    public <T extends PersistentBase> T a(Supplier<T> supplier, String s) {
        T t0 = this.b(supplier, s);

        if (t0 != null) {
            return t0;
        } else {
            T t1 = supplier.get(); // Paper - decompile fix

            this.a(t1);
            return t1;
        }
    }

    @Nullable
    public <T extends PersistentBase> T b(Supplier<T> supplier, String s) {
        T persistentbase = (T) this.data.get(s); // Paper - decompile fix

        if (persistentbase == null && !this.data.containsKey(s)) {
            persistentbase = this.c(supplier, s);
            this.data.put(s, persistentbase);
        }

        return persistentbase;
    }

    @Nullable
    private <T extends PersistentBase> T c(Supplier<T> supplier, String s) {
        try {
            File file = this.a(s);

            if (file.exists()) {
                T t0 = supplier.get(); // Paper - decompile fix
                NBTTagCompound nbttagcompound = this.a(s, SharedConstants.a().getWorldVersion());

                t0.a(nbttagcompound.getCompound("data"));
                return t0;
            }
        } catch (Exception exception) {
            WorldPersistentData.LOGGER.error("Error loading saved data: {}", s, exception);
        }

        return null;
    }

    public void a(PersistentBase persistentbase) {
        this.data.put(persistentbase.getId(), persistentbase);
    }

    public NBTTagCompound a(String s, int i) throws IOException {
        File file = this.a(s);
        PushbackInputStream pushbackinputstream = new PushbackInputStream(new FileInputStream(file), 2);
        Throwable throwable = null;

        NBTTagCompound nbttagcompound;

        try {
            NBTTagCompound nbttagcompound1;

            if (this.a(pushbackinputstream)) {
                nbttagcompound1 = NBTCompressedStreamTools.a((InputStream) pushbackinputstream);
            } else {
                DataInputStream datainputstream = new DataInputStream(pushbackinputstream);
                Throwable throwable1 = null;

                try {
                    nbttagcompound1 = NBTCompressedStreamTools.a(datainputstream);
                } catch (Throwable throwable2) {
                    throwable1 = throwable2;
                    throw throwable2;
                } finally {
                    if (datainputstream != null) {
                        if (throwable1 != null) {
                            try {
                                datainputstream.close();
                            } catch (Throwable throwable3) {
                                throwable1.addSuppressed(throwable3);
                            }
                        } else {
                            datainputstream.close();
                        }
                    }

                }
            }

            int j = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : 1343;

            nbttagcompound = GameProfileSerializer.a(this.c, DataFixTypes.SAVED_DATA, nbttagcompound1, j, i);
        } catch (Throwable throwable4) {
            throwable = throwable4;
            com.destroystokyo.paper.exception.ServerInternalException.reportInternalException(throwable); // Paper
            throw throwable4;
        } finally {
            if (pushbackinputstream != null) {
                if (throwable != null) {
                    try {
                        pushbackinputstream.close();
                    } catch (Throwable throwable5) {
                        throwable.addSuppressed(throwable5);
                    }
                } else {
                    pushbackinputstream.close();
                }
            }

        }

        return nbttagcompound;
    }

    private boolean a(PushbackInputStream pushbackinputstream) throws IOException {
        byte[] abyte = new byte[2];
        boolean flag = false;
        int i = pushbackinputstream.read(abyte, 0, 2);

        if (i == 2) {
            int j = (abyte[1] & 255) << 8 | abyte[0] & 255;

            if (j == 35615) {
                flag = true;
            }
        }

        if (i != 0) {
            pushbackinputstream.unread(abyte, 0, i);
        }

        return flag;
    }

    public void a() {
        Iterator iterator = this.data.values().iterator();

        while (iterator.hasNext()) {
            PersistentBase persistentbase = (PersistentBase) iterator.next();

            if (persistentbase != null) {
                persistentbase.a(this.a(persistentbase.getId()));
            }
        }

    }
}
