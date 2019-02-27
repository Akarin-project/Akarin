package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.DataFixTypes;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldPersistentData {

    private static final Logger a = LogManager.getLogger();
    private final DimensionManager b;
    public Map<String, PersistentBase> data = Maps.newHashMap();
    private final Object2IntMap<String> d = new Object2IntOpenHashMap();
    @Nullable
    private final IDataManager e;

    public WorldPersistentData(DimensionManager dimensionmanager, @Nullable IDataManager idatamanager) {
        this.b = dimensionmanager;
        this.e = idatamanager;
        this.d.defaultReturnValue(-1);
    }

    @Nullable
    public <T extends PersistentBase> T a(Function<String, T> function, String s) {
        PersistentBase persistentbase = (PersistentBase) this.data.get(s);

        if (persistentbase == null && this.e != null) {
            try {
                File file = this.e.getDataFile(this.b, s);

                if (file != null && file.exists()) {
                    persistentbase = (PersistentBase) function.apply(s);
                    persistentbase.a(a(this.e, this.b, s, 1631).getCompound("data"));
                    this.data.put(s, persistentbase);
                }
            } catch (Exception exception) {
                WorldPersistentData.a.error("Error loading saved data: {}", s, exception);
            }
        }

        return persistentbase;
    }

    public void a(String s, PersistentBase persistentbase) {
        this.data.put(s, persistentbase);
    }

    public void a() {
        try {
            this.d.clear();
            if (this.e == null) {
                return;
            }

            File file = this.e.getDataFile(this.b, "idcounts");

            if (file != null && file.exists()) {
                DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));
                NBTTagCompound nbttagcompound = NBTCompressedStreamTools.a(datainputstream);

                datainputstream.close();
                Iterator iterator = nbttagcompound.getKeys().iterator();

                while (iterator.hasNext()) {
                    String s = (String) iterator.next();

                    if (nbttagcompound.hasKeyOfType(s, 99)) {
                        this.d.put(s, nbttagcompound.getInt(s));
                    }
                }
            }
        } catch (Exception exception) {
            WorldPersistentData.a.error("Could not load aux values", exception);
        }

    }

    public int a(String s) {
        int i = this.d.getInt(s) + 1;

        this.d.put(s, i);
        if (this.e == null) {
            return i;
        } else {
            try {
                File file = this.e.getDataFile(this.b, "idcounts");

                if (file != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();
                    ObjectIterator objectiterator = this.d.object2IntEntrySet().iterator();

                    while (objectiterator.hasNext()) {
                        Entry<String> entry = (Entry) objectiterator.next();

                        nbttagcompound.setInt((String) entry.getKey(), entry.getIntValue());
                    }

                    DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));

                    NBTCompressedStreamTools.a(nbttagcompound, (DataOutput) dataoutputstream);
                    dataoutputstream.close();
                }
            } catch (Exception exception) {
                WorldPersistentData.a.error("Could not get free aux value {}", s, exception);
            }

            return i;
        }
    }

    public static NBTTagCompound a(IDataManager idatamanager, DimensionManager dimensionmanager, String s, int i) throws IOException {
        File file = idatamanager.getDataFile(dimensionmanager, s);
        FileInputStream fileinputstream = new FileInputStream(file);
        Throwable throwable = null;

        NBTTagCompound nbttagcompound;

        try {
            NBTTagCompound nbttagcompound1 = NBTCompressedStreamTools.a((InputStream) fileinputstream);
            int j = nbttagcompound1.hasKeyOfType("DataVersion", 99) ? nbttagcompound1.getInt("DataVersion") : 1343;

            nbttagcompound = GameProfileSerializer.a(idatamanager.i(), DataFixTypes.SAVED_DATA, nbttagcompound1, j, i);
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (fileinputstream != null) {
                if (throwable != null) {
                    try {
                        fileinputstream.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    fileinputstream.close();
                }
            }

        }

        return nbttagcompound;
    }

    public void b() {
        if (this.e != null) {
            Iterator iterator = this.data.values().iterator();

            while (iterator.hasNext()) {
                PersistentBase persistentbase = (PersistentBase) iterator.next();

                if (persistentbase.d()) {
                    this.a(persistentbase);
                    persistentbase.a(false);
                }
            }

        }
    }

    private void a(PersistentBase persistentbase) {
        if (this.e != null) {
            try {
                File file = this.e.getDataFile(this.b, persistentbase.getId());

                if (file != null) {
                    NBTTagCompound nbttagcompound = new NBTTagCompound();

                    nbttagcompound.set("data", persistentbase.b(new NBTTagCompound()));
                    nbttagcompound.setInt("DataVersion", 1631);
                    FileOutputStream fileoutputstream = new FileOutputStream(file);

                    NBTCompressedStreamTools.a(nbttagcompound, (OutputStream) fileoutputstream);
                    fileoutputstream.close();
                }
            } catch (Exception exception) {
                WorldPersistentData.a.error("Could not save data {}", persistentbase, exception);
            }

        }
    }
}
