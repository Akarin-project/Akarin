package net.minecraft.server;

import com.mojang.datafixers.DataFixTypes;
import com.mojang.datafixers.DataFixer;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldNBTStorage implements IDataManager, IPlayerFileData {

    private static final Logger b = LogManager.getLogger();
    private final File baseDir;
    private final File playerDir;
    private final long sessionId = SystemUtils.getMonotonicMillis();
    private final String f;
    private final DefinedStructureManager g;
    protected final DataFixer a;

    public WorldNBTStorage(File file, String s, @Nullable MinecraftServer minecraftserver, DataFixer datafixer) {
        this.a = datafixer;
        this.baseDir = new File(file, s);
        this.baseDir.mkdirs();
        this.playerDir = new File(this.baseDir, "playerdata");
        this.f = s;
        if (minecraftserver != null) {
            this.playerDir.mkdirs();
            this.g = new DefinedStructureManager(minecraftserver, this.baseDir, datafixer);
        } else {
            this.g = null;
        }

        this.j();
    }

    private void j() {
        try {
            File file = new File(this.baseDir, "session.lock");
            DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file));

            try {
                dataoutputstream.writeLong(this.sessionId);
            } finally {
                dataoutputstream.close();
            }

        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            throw new RuntimeException("Failed to check session lock, aborting");
        }
    }

    public File getDirectory() {
        return this.baseDir;
    }

    public void checkSession() throws ExceptionWorldConflict {
        try {
            File file = new File(this.baseDir, "session.lock");
            DataInputStream datainputstream = new DataInputStream(new FileInputStream(file));

            try {
                if (datainputstream.readLong() != this.sessionId) {
                    throw new ExceptionWorldConflict("The save is being accessed from another location, aborting");
                }
            } finally {
                datainputstream.close();
            }

        } catch (IOException ioexception) {
            throw new ExceptionWorldConflict("Failed to check session lock, aborting");
        }
    }

    public IChunkLoader createChunkLoader(WorldProvider worldprovider) {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    @Nullable
    public WorldData getWorldData() {
        File file = new File(this.baseDir, "level.dat");

        if (file.exists()) {
            WorldData worlddata = WorldLoader.a(file, this.a);

            if (worlddata != null) {
                return worlddata;
            }
        }

        file = new File(this.baseDir, "level.dat_old");
        return file.exists() ? WorldLoader.a(file, this.a) : null;
    }

    public void saveWorldData(WorldData worlddata, @Nullable NBTTagCompound nbttagcompound) {
        NBTTagCompound nbttagcompound1 = worlddata.a(nbttagcompound);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();

        nbttagcompound2.set("Data", nbttagcompound1);

        try {
            File file = new File(this.baseDir, "level.dat_new");
            File file1 = new File(this.baseDir, "level.dat_old");
            File file2 = new File(this.baseDir, "level.dat");

            NBTCompressedStreamTools.a(nbttagcompound2, (OutputStream) (new FileOutputStream(file)));
            if (file1.exists()) {
                file1.delete();
            }

            file2.renameTo(file1);
            if (file2.exists()) {
                file2.delete();
            }

            file.renameTo(file2);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void saveWorldData(WorldData worlddata) {
        this.saveWorldData(worlddata, (NBTTagCompound) null);
    }

    public void save(EntityHuman entityhuman) {
        try {
            NBTTagCompound nbttagcompound = entityhuman.save(new NBTTagCompound());
            File file = new File(this.playerDir, entityhuman.bu() + ".dat.tmp");
            File file1 = new File(this.playerDir, entityhuman.bu() + ".dat");

            NBTCompressedStreamTools.a(nbttagcompound, (OutputStream) (new FileOutputStream(file)));
            if (file1.exists()) {
                file1.delete();
            }

            file.renameTo(file1);
        } catch (Exception exception) {
            WorldNBTStorage.b.warn("Failed to save player data for {}", entityhuman.getDisplayName().getString());
        }

    }

    @Nullable
    public NBTTagCompound load(EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = null;

        try {
            File file = new File(this.playerDir, entityhuman.bu() + ".dat");

            if (file.exists() && file.isFile()) {
                nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file)));
            }
        } catch (Exception exception) {
            WorldNBTStorage.b.warn("Failed to load player data for {}", entityhuman.getDisplayName().getString());
        }

        if (nbttagcompound != null) {
            int i = nbttagcompound.hasKeyOfType("DataVersion", 3) ? nbttagcompound.getInt("DataVersion") : -1;

            entityhuman.f(GameProfileSerializer.a(this.a, DataFixTypes.PLAYER, nbttagcompound, i));
        }

        return nbttagcompound;
    }

    public IPlayerFileData getPlayerFileData() {
        return this;
    }

    public String[] getSeenPlayers() {
        String[] astring = this.playerDir.list();

        if (astring == null) {
            astring = new String[0];
        }

        for (int i = 0; i < astring.length; ++i) {
            if (astring[i].endsWith(".dat")) {
                astring[i] = astring[i].substring(0, astring[i].length() - 4);
            }
        }

        return astring;
    }

    public void a() {}

    public File getDataFile(DimensionManager dimensionmanager, String s) {
        File file = new File(dimensionmanager.a(this.baseDir), "data");

        file.mkdirs();
        return new File(file, s + ".dat");
    }

    public DefinedStructureManager h() {
        return this.g;
    }

    public DataFixer i() {
        return this.a;
    }
}
