package net.minecraft.server;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import java.io.IOException;
// Paper start
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
// Paper end
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class IChunkLoader extends RegionFileCache {

    protected final DataFixer b;
    @Nullable
    private volatile PersistentStructureLegacy a; // Paper - async chunk loading

    private final Object persistentDataLock = new Object(); // Paper

    public IChunkLoader(File file, DataFixer datafixer) {
        super(file);
        this.b = datafixer;
    }

    // CraftBukkit start
    private boolean check(ChunkProviderServer cps, int x, int z) throws IOException {
        ChunkCoordIntPair pos = new ChunkCoordIntPair(x, z);
        if (cps != null) {
            //com.google.common.base.Preconditions.checkState(org.bukkit.Bukkit.isPrimaryThread(), "primary thread"); // Paper - this function is now MT-Safe
            if (cps.getChunkAtIfCachedImmediately(x, z) != null) { // Paper - isLoaded is a ticket level check, not a chunk loaded check!
                return true;
            }
        }

        if (this.chunkExists(pos)) {
            // Paper start - prioritize
            NBTTagCompound nbt = cps == null ? read(pos) :
                com.destroystokyo.paper.io.PaperFileIOThread.Holder.INSTANCE.loadChunkData((WorldServer)cps.getWorld(), x, z,
                    com.destroystokyo.paper.io.PrioritizedTaskQueue.HIGHER_PRIORITY, false, true).chunkData;
            // Paper end
            if (nbt != null) {
                NBTTagCompound level = nbt.getCompound("Level");
                if (level.getBoolean("TerrainPopulated")) {
                    return true;
                }

                ChunkStatus status = ChunkStatus.a(level.getString("Status"));
                if (status != null && status.b(ChunkStatus.FEATURES)) {
                    return true;
                }
            }
        }

        return false;
    }
    // CraftBukkit end

    public NBTTagCompound getChunkData(DimensionManager dimensionmanager, Supplier<WorldPersistentData> supplier, NBTTagCompound nbttagcompound, ChunkCoordIntPair pos, @Nullable GeneratorAccess generatoraccess) throws IOException {
        int i = a(nbttagcompound);
        boolean flag = true;

        // CraftBukkit start
        if (i < 1466) {
            NBTTagCompound level = nbttagcompound.getCompound("Level");
            if (level.getBoolean("TerrainPopulated") && !level.getBoolean("LightPopulated")) {
                ChunkProviderServer cps = (generatoraccess == null) ? null : ((WorldServer) generatoraccess).getChunkProvider();
                if (check(cps, pos.x - 1, pos.z) && check(cps, pos.x - 1, pos.z - 1) && check(cps, pos.x, pos.z - 1)) {
                    level.setBoolean("LightPopulated", true);
                }
            }
        }
        // CraftBukkit end

        if (i < 1493) {
            nbttagcompound = GameProfileSerializer.a(this.b, DataFixTypes.CHUNK, nbttagcompound, i, 1493);
            if (nbttagcompound.getCompound("Level").getBoolean("hasLegacyStructureData")) {
                synchronized (this.persistentDataLock) { // Paper - Async chunk loading
                if (this.a == null) {
                    this.a = PersistentStructureLegacy.a(dimensionmanager.getType(), (WorldPersistentData) supplier.get()); // CraftBukkit - getType
                }

                nbttagcompound = this.a.a(nbttagcompound);
                } // Paper - Async chunk loading
            }
        }

        nbttagcompound = GameProfileSerializer.a(this.b, DataFixTypes.CHUNK, nbttagcompound, Math.max(1493, i));
        if (i < SharedConstants.a().getWorldVersion()) {
            nbttagcompound.setInt("DataVersion", SharedConstants.a().getWorldVersion());
        }

        return nbttagcompound;
    }

    public static int a(NBTTagCompound nbttagcompound) {
        return nbttagcompound.hasKeyOfType("DataVersion", 99) ? nbttagcompound.getInt("DataVersion") : -1;
    }

    @Override
    public void write(ChunkCoordIntPair chunkcoordintpair, NBTTagCompound nbttagcompound) throws IOException {
        super.write(chunkcoordintpair, nbttagcompound);
        if (this.a != null) {
            synchronized (this.persistentDataLock) { // Paper - Async chunk loading
            this.a.a(chunkcoordintpair.pair());
            } // Paper - Async chunk loading
        }

    }
}
