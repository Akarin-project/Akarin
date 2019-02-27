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

// CraftBukkit start
import java.util.UUID;
import org.bukkit.craftbukkit.entity.CraftPlayer;
// CraftBukkit end

public class WorldNBTStorage implements IDataManager, IPlayerFileData {

    private static final Logger b = LogManager.getLogger();
    private final File baseDir;
    private final File playerDir;
    private final long sessionId = SystemUtils.getMonotonicMillis();
    private final String f;
    private final DefinedStructureManager g;
    protected final DataFixer a;
    private UUID uuid = null; // CraftBukkit

    public WorldNBTStorage(File file, String s, @Nullable MinecraftServer minecraftserver, DataFixer datafixer) {
        this.a = datafixer;
        // Paper start
        if (com.destroystokyo.paper.PaperConfig.useVersionedWorld) {
            File origBaseDir = new File(file, s);
            final String currentVersion = MinecraftServer.getServer().getVersion();
            file = new File(file, currentVersion);
            File baseDir = new File(file, s);

            if (!baseDir.exists() && origBaseDir.exists() && !baseDir.mkdirs()) {
                LogManager.getLogger().error("Could not create world directory for " + file);
                System.exit(1);
            }

            try {
                boolean printedHeader = false;
                String[] dirs  = {"advancements", "data", "datapacks", "playerdata", "stats"};
                for (String dir : dirs) {
                    File origPlayerData = new File(origBaseDir, dir);
                    File targetPlayerData = new File(baseDir, dir);
                    if (origPlayerData.exists() && !targetPlayerData.exists()) {
                        if (!printedHeader) {
                            LogManager.getLogger().info("**** VERSIONED WORLD - Copying files");
                            printedHeader = true;
                        }
                        LogManager.getLogger().info("- Copying: " + dir);
                        org.apache.commons.io.FileUtils.copyDirectory(origPlayerData, targetPlayerData);
                    }
                }

                String[] files = {"level.dat", "level.dat_old", "session.lock", "uid.dat"};
                for (String fileName : files) {
                    File origPlayerData = new File(origBaseDir, fileName);
                    File targetPlayerData = new File(baseDir, fileName);
                    if (origPlayerData.exists() && !targetPlayerData.exists()) {
                        if (!printedHeader) {
                            LogManager.getLogger().info("- Copying files");
                            printedHeader = true;
                        }
                        LogManager.getLogger().info("- Copying: " + fileName);
                        org.apache.commons.io.FileUtils.copyFile(origPlayerData, targetPlayerData);

                    }
                }
                if (printedHeader) {
                    LogManager.getLogger().info("**** VERSIONED WORLD - Copying DONE");
                }
            } catch (IOException e) {
                LogManager.getLogger().error("Error copying versioned world data for " + origBaseDir + " to " + baseDir, e);
                com.destroystokyo.paper.util.SneakyThrow.sneaky(e);
            }

        }
        // Paper end
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
            throw new RuntimeException("Failed to check session lock for world located at " + this.baseDir + ", aborting. Stop the server and delete the session.lock in this world to prevent further issues."); // Spigot
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
                    throw new ExceptionWorldConflict("The save for world located at " + this.baseDir + " is being accessed from another location, aborting");  // Spigot
                }
            } finally {
                datainputstream.close();
            }

        } catch (IOException ioexception) {
            throw new ExceptionWorldConflict("Failed to check session lock for world located at " + this.baseDir + ", aborting. Stop the server and delete the session.lock in this world to prevent further issues."); // Spigot
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
        if(!com.destroystokyo.paper.PaperConfig.savePlayerData) return; // Paper - Make player data saving configurable
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
            WorldNBTStorage.b.error("Failed to save player data for {}", entityhuman.getName(), exception); // Paper
        }

    }

    @Nullable
    public NBTTagCompound load(EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = null;

        try {
            File file = new File(this.playerDir, entityhuman.bu() + ".dat");
            // Spigot Start
            boolean usingWrongFile = false;
            if ( org.bukkit.Bukkit.getOnlineMode() && !file.exists() ) // Paper - Check online mode first
            {
                file = new File( this.playerDir, UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + entityhuman.getName() ).getBytes( "UTF-8" ) ).toString() + ".dat");
                if ( file.exists() )
                {
                    usingWrongFile = true;
                    org.bukkit.Bukkit.getServer().getLogger().warning( "Using offline mode UUID file for player " + entityhuman.getName() + " as it is the only copy we can find." );
                }
            }
            // Spigot End

            if (file.exists() && file.isFile()) {
                nbttagcompound = NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file)));
            }
            // Spigot Start
            if ( usingWrongFile )
            {
                file.renameTo( new File( file.getPath() + ".offline-read" ) );
            }
            // Spigot End
        } catch (Exception exception) {
            WorldNBTStorage.b.warn("Failed to load player data for {}", entityhuman.getDisplayName().getString());
        }

        if (nbttagcompound != null) {
            // CraftBukkit start
            if (entityhuman instanceof EntityPlayer) {
                CraftPlayer player = (CraftPlayer) entityhuman.getBukkitEntity();
                // Only update first played if it is older than the one we have
                long modified = new File(this.playerDir, entityhuman.getUniqueID().toString() + ".dat").lastModified();
                if (modified < player.getFirstPlayed()) {
                    player.setFirstPlayed(modified);
                }
            }
            // CraftBukkit end
            int i = nbttagcompound.hasKeyOfType("DataVersion", 3) ? nbttagcompound.getInt("DataVersion") : -1;

            entityhuman.f(GameProfileSerializer.a(this.a, DataFixTypes.PLAYER, nbttagcompound, i));
        }

        return nbttagcompound;
    }

    // CraftBukkit start
    public NBTTagCompound getPlayerData(String s) {
        try {
            File file1 = new File(this.playerDir, s + ".dat");

            if (file1.exists()) {
                return NBTCompressedStreamTools.a((InputStream) (new FileInputStream(file1)));
            }
        } catch (Exception exception) {
            b.warn("Failed to load player data for " + s);
        }

        return null;
    }
    // CraftBukkit end

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

    // CraftBukkit start
    public UUID getUUID() {
        if (uuid != null) return uuid;
        File file1 = new File(this.baseDir, "uid.dat");
        if (file1.exists()) {
            DataInputStream dis = null;
            try {
                dis = new DataInputStream(new FileInputStream(file1));
                return uuid = new UUID(dis.readLong(), dis.readLong());
            } catch (IOException ex) {
                b.warn("Failed to read " + file1 + ", generating new random UUID", ex);
            } finally {
                if (dis != null) {
                    try {
                        dis.close();
                    } catch (IOException ex) {
                        // NOOP
                    }
                }
            }
        }
        uuid = UUID.randomUUID();
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new FileOutputStream(file1));
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
        } catch (IOException ex) {
            b.warn("Failed to write " + file1, ex);
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException ex) {
                    // NOOP
                }
            }
        }
        return uuid;
    }

    public File getPlayerDir() {
        return playerDir;
    }
    // CraftBukkit end
}
