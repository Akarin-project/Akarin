package net.minecraft.server;

import com.destroystokyo.paper.exception.ServerInternalException;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Lists; // CraftBukkit
import org.bukkit.Server; // CraftBukkit
import org.bukkit.craftbukkit.util.CraftMagicNumbers; // Paper

/**
 * Akarin Changes Note
 * 1) Add volatile to fields (async lighting)
 * 2) Expose private methods (async lighting)
 */
public class Chunk {

    private static final Logger e = LogManager.getLogger();
    public static final ChunkSection a = null; public static final ChunkSection EMPTY_CHUNK_SECTION = Chunk.a; // Paper - OBFHELPER
    private final ChunkSection[] sections;
    private final byte[] g;
    private final int[] h;
    private final boolean[] i;
    private boolean j; public boolean isLoaded() { return j; } // Paper - OBFHELPER
    public final World world;
    public final int[] heightMap;
    public Long scheduledForUnload; // Paper - delay chunk unloads
    public final int locX;
    public final int locZ;
    private boolean m;
    public final Map<BlockPosition, TileEntity> tileEntities;
    public final List<Entity>[] entitySlices; // Spigot
    // Paper start
    public final co.aikar.util.Counter<String> entityCounts = new co.aikar.util.Counter<>();
    public final co.aikar.util.Counter<String> tileEntityCounts = new co.aikar.util.Counter<>();
    private class TileEntityHashMap extends java.util.HashMap<BlockPosition, TileEntity> {
        @Override
        public TileEntity put(BlockPosition key, TileEntity value) {
            TileEntity replaced = super.put(key, value);
            if (replaced != null) {
                replaced.setCurrentChunk(null);
                tileEntityCounts.decrement(replaced.tileEntityKeyString);
            }
            if (value != null) {
                value.setCurrentChunk(Chunk.this);
                tileEntityCounts.increment(value.tileEntityKeyString);
            }
            return replaced;
        }

        @Override
        public TileEntity remove(Object key) {
            TileEntity removed = super.remove(key);
            if (removed != null) {
                removed.setCurrentChunk(null);
                tileEntityCounts.decrement(removed.tileEntityKeyString);
            }
            return removed;
        }
    }
    public final PaperLightingQueue.LightingQueue lightingQueue = new PaperLightingQueue.LightingQueue(this); // Akarin - public
    // Paper end
    private volatile boolean done; // Akarin - volatile
    private volatile boolean lit; // Akarin - volatile
    private volatile boolean r; private boolean isTicked() { return r; }; // Paper - OBFHELPER // Akarin - volatile
    private boolean s;
    private boolean t;
    private long lastSaved;
    private int v;
    private long w;
    private int x;
    private final ConcurrentLinkedQueue<BlockPosition> y;
    public boolean d; public void setShouldUnload(boolean unload) { this.d = unload; } public boolean isUnloading() { return d; } // Paper - OBFHELPER
    protected gnu.trove.map.hash.TObjectIntHashMap<Class> entityCount = new gnu.trove.map.hash.TObjectIntHashMap<Class>(); // Spigot

    // Paper start
    // Track the number of minecarts and items
    // Keep this synced with entitySlices.add() and entitySlices.remove()
    private final int[] itemCounts = new int[16];
    private final int[] inventoryEntityCounts = new int[16];
    // Paper end

    // CraftBukkit start - Neighbor loaded cache for chunk lighting and entity ticking
    private int neighbors = 0x1 << 12;
    public long chunkKey;

    public boolean areNeighborsLoaded(final int radius) {
        switch (radius) {
            case 2:
                return this.neighbors == Integer.MAX_VALUE >> 6;
            case 1:
                final int mask =
                        //       x        z   offset          x        z   offset          x         z   offset
                        (0x1 << (1 * 5 +  1 + 12)) | (0x1 << (0 * 5 +  1 + 12)) | (0x1 << (-1 * 5 +  1 + 12)) |
                        (0x1 << (1 * 5 +  0 + 12)) | (0x1 << (0 * 5 +  0 + 12)) | (0x1 << (-1 * 5 +  0 + 12)) |
                        (0x1 << (1 * 5 + -1 + 12)) | (0x1 << (0 * 5 + -1 + 12)) | (0x1 << (-1 * 5 + -1 + 12));
                return (this.neighbors & mask) == mask;
            default:
                throw new UnsupportedOperationException(String.valueOf(radius));
        }
    }

    public void setNeighborLoaded(final int x, final int z) {
        this.neighbors |= 0x1 << (x * 5 + 12 + z);
    }

    public void setNeighborUnloaded(final int x, final int z) {
        this.neighbors &= ~(0x1 << (x * 5 + 12 + z));
    }
    // CraftBukkit end

    public Chunk(World world, int i, int j) {
        this.sections = new ChunkSection[16];
        this.g = new byte[256];
        this.h = new int[256];
        this.i = new boolean[256];
        this.tileEntities = new TileEntityHashMap(); // Paper
        this.x = 4096;
        this.y = Queues.newConcurrentLinkedQueue();
        this.entitySlices = (List[]) (new List[16]); // Spigot
        this.world = world;
        this.locX = i;
        this.locZ = j;
        this.heightMap = new int[256];

        for (int k = 0; k < this.entitySlices.length; ++k) {
            this.entitySlices[k] = new org.bukkit.craftbukkit.util.UnsafeList(); // Spigot
        }

        Arrays.fill(this.h, -999);
        Arrays.fill(this.g, (byte) -1);
        // CraftBukkit start
        this.bukkitChunk = new org.bukkit.craftbukkit.CraftChunk(this);
        this.chunkKey = ChunkCoordIntPair.a(this.locX, this.locZ);
    }

    public org.bukkit.Chunk bukkitChunk;
    public boolean mustSave;
    // CraftBukkit end

    public Chunk(World world, ChunkSnapshot chunksnapshot, int i, int j) {
        this(world, i, j);
        boolean flag = true;
        boolean flag1 = world.worldProvider.m();

        for (int k = 0; k < 16; ++k) {
            for (int l = 0; l < 16; ++l) {
                for (int i1 = 0; i1 < 256; ++i1) {
                    IBlockData iblockdata = chunksnapshot.a(k, i1, l);

                    if (iblockdata.getMaterial() != Material.AIR) {
                        int j1 = i1 >> 4;

                        if (this.sections[j1] == Chunk.a) {
                            this.sections[j1] = new ChunkSection(j1 << 4, flag1, world.chunkPacketBlockController.getPredefinedBlockData(this, j1)); // Paper - Anti-Xray - Add predefined block data
                        }

                        this.sections[j1].setType(k, i1 & 15, l, iblockdata);
                    }
                }
            }
        }

    }

    public boolean a(int i, int j) {
        return i == this.locX && j == this.locZ;
    }

    public int e(BlockPosition blockposition) {
        return this.b(blockposition.getX() & 15, blockposition.getZ() & 15);
    }

    public int b(int i, int j) {
        return this.heightMap[j << 4 | i];
    }

    @Nullable
    private ChunkSection y() {
        for (int i = this.sections.length - 1; i >= 0; --i) {
            if (this.sections[i] != Chunk.a) {
                return this.sections[i];
            }
        }

        return null;
    }

    public int g() {
        ChunkSection chunksection = this.y();

        return chunksection == null ? 0 : chunksection.getYPosition();
    }

    public ChunkSection[] getSections() {
        return this.sections;
    }

    public void initLighting() {
        int i = this.g();

        this.v = Integer.MAX_VALUE;

        for (int j = 0; j < 16; ++j) {
            int k = 0;

            while (k < 16) {
                this.h[j + (k << 4)] = -999;
                int l = i + 16;

                while (true) {
                    if (l > 0) {
                        if (this.d(j, l - 1, k) == 0) {
                            --l;
                            continue;
                        }

                        this.heightMap[k << 4 | j] = l;
                        if (l < this.v) {
                            this.v = l;
                        }
                    }

                    if (this.world.worldProvider.m()) {
                        l = 15;
                        int i1 = i + 16 - 1;

                        do {
                            int j1 = this.d(j, i1, k);

                            if (j1 == 0 && l != 15) {
                                j1 = 1;
                            }

                            l -= j1;
                            if (l > 0) {
                                ChunkSection chunksection = this.sections[i1 >> 4];

                                if (chunksection != Chunk.a) {
                                    chunksection.a(j, i1 & 15, k, l);
                                    this.world.m(new BlockPosition((this.locX << 4) + j, i1, (this.locZ << 4) + k));
                                }
                            }

                            --i1;
                        } while (i1 > 0 && l > 0);
                    }

                    ++k;
                    break;
                }
            }
        }

        this.s = true;
    }

    private void d(int i, int j) {
        this.i[i + j * 16] = true;
        this.m = true;
    }

    private void h(boolean flag) {
        this.world.methodProfiler.a("recheckGaps");
        if (this.world.areChunksLoaded(new BlockPosition(this.locX * 16 + 8, 0, this.locZ * 16 + 8), 16)) {
            this.runOrQueueLightUpdate(() -> recheckGaps(flag)); // Paper - Queue light update
        }
    }

    private void recheckGaps(boolean flag) {
        if (true) {
            // Paper end
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.i[i + j * 16]) {
                        this.i[i + j * 16] = false;
                        int k = this.b(i, j);
                        int l = this.locX * 16 + i;
                        int i1 = this.locZ * 16 + j;
                        int j1 = Integer.MAX_VALUE;

                        Iterator iterator;
                        EnumDirection enumdirection;

                        for (iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator(); iterator.hasNext(); j1 = Math.min(j1, this.world.d(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ()))) {
                            enumdirection = (EnumDirection) iterator.next();
                        }

                        this.b(l, i1, j1);
                        iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                        while (iterator.hasNext()) {
                            enumdirection = (EnumDirection) iterator.next();
                            this.b(l + enumdirection.getAdjacentX(), i1 + enumdirection.getAdjacentZ(), k);
                        }

                        if (flag) {
                            this.world.methodProfiler.b();
                            return;
                        }
                    }
                }
            }

            this.m = false;
        }

        this.world.methodProfiler.b();
    }

    private void b(int i, int j, int k) {
        int l = this.world.getHighestBlockYAt(new BlockPosition(i, 0, j)).getY();

        if (l > k) {
            this.a(i, j, k, l + 1);
        } else if (l < k) {
            this.a(i, j, l, k + 1);
        }

    }

    private void a(int i, int j, int k, int l) {
        if (l > k && this.world.areChunksLoaded(new BlockPosition(i, 0, j), 16)) {
            for (int i1 = k; i1 < l; ++i1) {
                this.world.c(EnumSkyBlock.SKY, new BlockPosition(i, i1, j));
            }

            this.s = true;
        }

    }

    private void c(int i, int j, int k) {
        int l = this.heightMap[k << 4 | i] & 255;
        int i1 = l;

        if (j > l) {
            i1 = j;
        }

        while (i1 > 0 && this.d(i, i1 - 1, k) == 0) {
            --i1;
        }

        if (i1 != l) {
            this.world.a(i + this.locX * 16, k + this.locZ * 16, i1, l);
            this.heightMap[k << 4 | i] = i1;
            int j1 = this.locX * 16 + i;
            int k1 = this.locZ * 16 + k;
            int l1;
            int i2;

            if (this.world.worldProvider.m()) {
                ChunkSection chunksection;

                if (i1 < l) {
                    for (l1 = i1; l1 < l; ++l1) {
                        chunksection = this.sections[l1 >> 4];
                        if (chunksection != Chunk.a) {
                            chunksection.a(i, l1 & 15, k, 15);
                            this.world.m(new BlockPosition((this.locX << 4) + i, l1, (this.locZ << 4) + k));
                        }
                    }
                } else {
                    for (l1 = l; l1 < i1; ++l1) {
                        chunksection = this.sections[l1 >> 4];
                        if (chunksection != Chunk.a) {
                            chunksection.a(i, l1 & 15, k, 0);
                            this.world.m(new BlockPosition((this.locX << 4) + i, l1, (this.locZ << 4) + k));
                        }
                    }
                }

                l1 = 15;

                while (i1 > 0 && l1 > 0) {
                    --i1;
                    i2 = this.d(i, i1, k);
                    if (i2 == 0) {
                        i2 = 1;
                    }

                    l1 -= i2;
                    if (l1 < 0) {
                        l1 = 0;
                    }

                    ChunkSection chunksection1 = this.sections[i1 >> 4];

                    if (chunksection1 != Chunk.a) {
                        chunksection1.a(i, i1 & 15, k, l1);
                    }
                }
            }

            l1 = this.heightMap[k << 4 | i];
            i2 = l;
            int j2 = l1;

            if (l1 < l) {
                i2 = l1;
                j2 = l;
            }

            if (l1 < this.v) {
                this.v = l1;
            }

            if (this.world.worldProvider.m()) {
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();

                    this.a(j1 + enumdirection.getAdjacentX(), k1 + enumdirection.getAdjacentZ(), i2, j2);
                }

                this.a(j1, k1, i2, j2);
            }

            this.s = true;
        }
    }

    public int b(BlockPosition blockposition) {
        return this.getBlockData(blockposition).c();
    }

    private int d(int i, int j, int k) {
        return this.a(i, j, k).c();
    }

    // Paper start - Optimize getBlockData to reduce instructions
    public final IBlockData getBlockData(final BlockPosition pos) {
        return getBlockData(pos.getX(), pos.getY(), pos.getZ());
    }

    public final IBlockData getBlockData(final int x, final int y, final int z) {
        // Method body / logic copied from below
        final int i = y >> 4;
        if (y >= 0 && i < this.sections.length && this.sections[i] != null) {
            // Inlined ChunkSection.getType() and DataPaletteBlock.a(int,int,int)
            return this.sections[i].blockIds.a((y & 15) << 8 | (z & 15) << 4 | x & 15);
        }
        return Blocks.AIR.getBlockData();
    }

    public IBlockData a(final int i, final int j, final int k) {
        return getBlockData(i, j, k);
    }

    public IBlockData unused(final int i, final int j, final int k) {
    // Paper end
        if (this.world.N() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            IBlockData iblockdata = null;

            if (j == 60) {
                iblockdata = Blocks.BARRIER.getBlockData();
            }

            if (j == 70) {
                iblockdata = ChunkProviderDebug.c(i, k);
            }

            return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
        } else {
            try {
                if (j >= 0 && j >> 4 < this.sections.length) {
                    ChunkSection chunksection = this.sections[j >> 4];

                    if (chunksection != Chunk.a) {
                        return chunksection.getType(i & 15, j & 15, k & 15);
                    }
                }

                return Blocks.AIR.getBlockData();
            } catch (Throwable throwable) {
                CrashReport crashreport = CrashReport.a(throwable, "Getting block state");
                CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Block being got");

                crashreportsystemdetails.a("Location", new CrashReportCallable() {
                    public String a() throws Exception {
                        return CrashReportSystemDetails.a(i, j, k);
                    }

                    public Object call() throws Exception {
                        return this.a();
                    }
                });
                throw new ReportedException(crashreport);
            }
        }
    }

    @Nullable
    public IBlockData a(BlockPosition blockposition, IBlockData iblockdata) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        int l = k << 4 | i;

        if (j >= this.h[l] - 1) {
            this.h[l] = -999;
        }

        int i1 = this.heightMap[l];
        IBlockData iblockdata1 = this.getBlockData(blockposition);

        if (iblockdata1 == iblockdata) {
            return null;
        } else {
            Block block = iblockdata.getBlock();
            Block block1 = iblockdata1.getBlock();
            ChunkSection chunksection = this.sections[j >> 4];
            boolean flag = false;

            if (chunksection == Chunk.a) {
                if (block == Blocks.AIR) {
                    return null;
                }

                chunksection = new ChunkSection(j >> 4 << 4, this.world.worldProvider.m(), this.world.chunkPacketBlockController.getPredefinedBlockData(this, j >> 4)); // Paper - Anti-Xray - Add predefined block data
                this.sections[j >> 4] = chunksection;
                flag = j >= i1;
            }

            chunksection.setType(i, j & 15, k, iblockdata);
            if (block1 != block) {
                if (!this.world.isClientSide) {
                    block1.remove(this.world, blockposition, iblockdata1);
                } else if (block1 instanceof ITileEntity) {
                    this.world.s(blockposition);
                }
            }

            if (chunksection.getType(i, j & 15, k).getBlock() != block) {
                return null;
            } else {
                if (flag) {
                    this.initLighting();
                } else {
                    this.runOrQueueLightUpdate(() -> { // Paper - Queue light update
                    int j1 = iblockdata.c();
                    int k1 = iblockdata1.c();

                    if (j1 > 0) {
                        if (j >= i1) {
                            this.c(i, j + 1, k);
                        }
                    } else if (j == i1 - 1) {
                        this.c(i, j, k);
                    }

                    if (j1 != k1 && (j1 < k1 || this.getBrightness(EnumSkyBlock.SKY, blockposition) > 0 || this.getBrightness(EnumSkyBlock.BLOCK, blockposition) > 0)) {
                        this.d(i, k);
                    }
                    }); // Paper
                }

                TileEntity tileentity;

                if (block1 instanceof ITileEntity) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity != null) {
                        tileentity.invalidateBlockCache();
                    }
                }

                // CraftBukkit - Don't place while processing the BlockPlaceEvent, unless it's a BlockContainer. Prevents blocks such as TNT from activating when cancelled.
                if (!this.world.isClientSide && block1 != block  && (!this.world.captureBlockStates || block instanceof BlockTileEntity)) {
                    block.onPlace(this.world, blockposition, iblockdata);
                }

                if (block instanceof ITileEntity) {
                    tileentity = this.a(blockposition, Chunk.EnumTileEntityState.CHECK);
                    if (tileentity == null) {
                        tileentity = ((ITileEntity) block).a(this.world, block.toLegacyData(iblockdata));
                        this.world.setTileEntity(blockposition, tileentity);
                    }

                    if (tileentity != null) {
                        tileentity.invalidateBlockCache();
                    }
                }

                this.s = true;
                return iblockdata1;
            }
        }
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;
        ChunkSection chunksection = this.sections[j >> 4];

        return chunksection == Chunk.a ? (this.c(blockposition) ? enumskyblock.c : 0) : (enumskyblock == EnumSkyBlock.SKY ? (!this.world.worldProvider.m() ? 0 : chunksection.b(i, j & 15, k)) : (enumskyblock == EnumSkyBlock.BLOCK ? chunksection.c(i, j & 15, k) : enumskyblock.c));
    }

    public void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        ChunkSection chunksection = this.sections[k >> 4];

        if (chunksection == Chunk.a) {
            chunksection = new ChunkSection(k >> 4 << 4, this.world.worldProvider.m(), this.world.chunkPacketBlockController.getPredefinedBlockData(this, k >> 4)); // Paper - Anti-Xray - Add predefined block data
            this.sections[k >> 4] = chunksection;
            this.initLighting();
        }

        this.s = true;
        if (enumskyblock == EnumSkyBlock.SKY) {
            if (this.world.worldProvider.m()) {
                chunksection.a(j, k & 15, l, i);
            }
        } else if (enumskyblock == EnumSkyBlock.BLOCK) {
            chunksection.b(j, k & 15, l, i);
        }

    }

    public final int getLightSubtracted(BlockPosition blockposition, int i) { return this.a(blockposition, i); } // Paper - OBFHELPER
    public int a(BlockPosition blockposition, int i) {
        int j = blockposition.getX() & 15;
        int k = blockposition.getY();
        int l = blockposition.getZ() & 15;
        ChunkSection chunksection = this.sections[k >> 4];

        if (chunksection == Chunk.a) {
            return this.world.worldProvider.m() && i < EnumSkyBlock.SKY.c ? EnumSkyBlock.SKY.c - i : 0;
        } else {
            int i1 = !this.world.worldProvider.m() ? 0 : chunksection.b(j, k & 15, l);

            i1 -= i;
            int j1 = chunksection.c(j, k & 15, l);

            if (j1 > i1) {
                i1 = j1;
            }

            return i1;
        }
    }

    public void a(Entity entity) {
        this.t = true;
        int i = MathHelper.floor(entity.locX / 16.0D);
        int j = MathHelper.floor(entity.locZ / 16.0D);

        if (i != this.locX || j != this.locZ) {
            Chunk.e.warn("Wrong location! ({}, {}) should be ({}, {}), {}", Integer.valueOf(i), Integer.valueOf(j), Integer.valueOf(this.locX), Integer.valueOf(this.locZ), entity);
            entity.die();
        }

        int k = MathHelper.floor(entity.locY / 16.0D);

        if (k < 0) {
            k = 0;
        }

        if (k >= this.entitySlices.length) {
            k = this.entitySlices.length - 1;
        }

        entity.aa = true;
        entity.ab = this.locX;
        entity.ac = k;
        entity.ad = this.locZ;
        this.entitySlices[k].add(entity);
        // Paper start
        entity.setCurrentChunk(this);
        entityCounts.increment(entity.entityKeyString);
        if (entity instanceof EntityItem) {
            itemCounts[k]++;
        } else if (entity instanceof IInventory) {
            inventoryEntityCounts[k]++;
        }
        // Paper end
        // Spigot start - increment creature type count
        // Keep this synced up with World.a(Class)
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;
            if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
                return;
            }
        }
        for ( EnumCreatureType creatureType : EnumCreatureType.values() )
        {
            if ( creatureType.a().isAssignableFrom( entity.getClass() ) )
            {
                this.entityCount.adjustOrPutValue( creatureType.a(), 1, 1 );
            }
        }
        // Spigot end
    }

    public void removeEntity(Entity entity) { b(entity); } // Paper - OBFHELPER
    public void b(Entity entity) {
        this.a(entity, entity.ac);
    }

    public void a(Entity entity, int i) {
        if (i < 0) {
            i = 0;
        }

        if (i >= this.entitySlices.length) {
            i = this.entitySlices.length - 1;
        }

        // Paper start
        if (!this.entitySlices[i].remove(entity)) { return; }
        entity.setCurrentChunk(null);
        entityCounts.decrement(entity.entityKeyString);
        if (entity instanceof EntityItem) {
            itemCounts[i]--;
        } else if (entity instanceof IInventory) {
            inventoryEntityCounts[i]--;
        }
        // Paper end
        // Spigot start - decrement creature type count
        // Keep this synced up with World.a(Class)
        if (entity instanceof EntityInsentient) {
            EntityInsentient entityinsentient = (EntityInsentient) entity;
            if (entityinsentient.isTypeNotPersistent() && entityinsentient.isPersistent()) {
                return;
            }
        }
        for ( EnumCreatureType creatureType : EnumCreatureType.values() )
        {
            if ( creatureType.a().isAssignableFrom( entity.getClass() ) )
            {
                this.entityCount.adjustValue( creatureType.a(), -1 );
            }
        }
        // Spigot end
    }

    public boolean c(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getY();
        int k = blockposition.getZ() & 15;

        return j >= this.heightMap[k << 4 | i];
    }

    @Nullable
    private TileEntity g(BlockPosition blockposition) {
        IBlockData iblockdata = this.getBlockData(blockposition);
        Block block = iblockdata.getBlock();

        return !block.isTileEntity() ? null : ((ITileEntity) block).a(this.world, iblockdata.getBlock().toLegacyData(iblockdata));
    }

    @Nullable public final TileEntity getTileEntityImmediately(BlockPosition pos) { return this.a(pos, EnumTileEntityState.IMMEDIATE); } // Paper - OBFHELPER
    @Nullable
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        // CraftBukkit start
        TileEntity tileentity = null;
        if (world.captureBlockStates) {
            tileentity = world.capturedTileEntities.get(blockposition);
        }
        if (tileentity == null) {
            tileentity = (TileEntity) this.tileEntities.get(blockposition);
        }
        // CraftBukkit end

        if (tileentity == null) {
            if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.IMMEDIATE) {
                tileentity = this.g(blockposition);
                this.world.setTileEntity(blockposition, tileentity);
            } else if (chunk_enumtileentitystate == Chunk.EnumTileEntityState.QUEUED) {
                this.y.add(blockposition);
            }
        } else if (tileentity.y()) {
            this.tileEntities.remove(blockposition);
            return null;
        }

        return tileentity;
    }

    public void a(TileEntity tileentity) {
        this.a(tileentity.getPosition(), tileentity);
        if (this.j) {
            this.world.a(tileentity);
        }

    }

    public void a(BlockPosition blockposition, TileEntity tileentity) {
        tileentity.a(this.world);
        tileentity.setPosition(blockposition);
        if (this.getBlockData(blockposition).getBlock() instanceof ITileEntity) {
            if (this.tileEntities.containsKey(blockposition)) {
                ((TileEntity) this.tileEntities.get(blockposition)).z();
            }

            tileentity.A();
            this.tileEntities.put(blockposition, tileentity);
            // CraftBukkit start
            // Paper start - Remove invalid mob spawner tile entities
        } else if (tileentity instanceof TileEntityMobSpawner && org.bukkit.craftbukkit.util.CraftMagicNumbers.getMaterial(getBlockData(blockposition).getBlock()) != org.bukkit.Material.MOB_SPAWNER) {
            this.tileEntities.remove(blockposition);
            // Paper end
        } else {
            // Paper start
            ServerInternalException e = new ServerInternalException(
                    "Attempted to place a tile entity (" + tileentity + ") at " + tileentity.position.getX() + ","
                            + tileentity.position.getY() + "," + tileentity.position.getZ()
                            + " (" + CraftMagicNumbers.getMaterial(getBlockData(blockposition).getBlock()) + ") where there was no entity tile!\n" +
                            "Chunk coordinates: " + (this.locX * 16) + "," + (this.locZ * 16));
            e.printStackTrace();
            ServerInternalException.reportInternalException(e);

            if (this.world.paperConfig.removeCorruptTEs) {
                this.removeTileEntity(tileentity.getPosition());
                this.markDirty();
                org.bukkit.Bukkit.getLogger().info("Removing corrupt tile entity");
            }
            // Paper end
            // CraftBukkit end
        }
    }

    public void removeTileEntity(BlockPosition blockposition) { this.d(blockposition); } // Paper - OBFHELPER
    public void d(BlockPosition blockposition) {
        if (this.j) {
            TileEntity tileentity = (TileEntity) this.tileEntities.remove(blockposition);

            if (tileentity != null) {
                tileentity.z();
            }
        }

    }

    public void addEntities() {
        this.j = true;
        this.world.b(this.tileEntities.values());
        List[] aentityslice = this.entitySlices; // Spigot
        int i = aentityslice.length;

        for (int j = 0; j < i; ++j) {
            List entityslice = aentityslice[j]; // Spigot

            this.world.a((Collection) entityslice);
        }

    }

    public void removeEntities() {
        this.j = false;
        Iterator iterator = this.tileEntities.values().iterator();

        while (iterator.hasNext()) {
            TileEntity tileentity = (TileEntity) iterator.next();
            // Spigot Start
            if ( tileentity instanceof IInventory )
            {
                for ( org.bukkit.entity.HumanEntity h : Lists.<org.bukkit.entity.HumanEntity>newArrayList((List<org.bukkit.entity.HumanEntity>) ( (IInventory) tileentity ).getViewers() ) )
                {
                    if ( h instanceof org.bukkit.craftbukkit.entity.CraftHumanEntity )
                    {
                       ( (org.bukkit.craftbukkit.entity.CraftHumanEntity) h).getHandle().closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.UNLOADED); // Paper
                    }
                }
            }
            // Spigot End

            this.world.b(tileentity);
        }

        List[] aentityslice = this.entitySlices; // Spigot
        int i = aentityslice.length;

        for (int j = 0; j < i; ++j) {
            // CraftBukkit start
            List<Entity> newList = Lists.newArrayList(aentityslice[j]);
            java.util.Iterator<Entity> iter = newList.iterator();
            while (iter.hasNext()) {
                Entity entity = iter.next();
                // Spigot Start
                if ( entity instanceof IInventory )
                {
                    for ( org.bukkit.entity.HumanEntity h : Lists.<org.bukkit.entity.HumanEntity>newArrayList( (List<org.bukkit.entity.HumanEntity>) ( (IInventory) entity ).getViewers() ) )
                    {
                        if ( h instanceof org.bukkit.craftbukkit.entity.CraftHumanEntity )
                        {
                           ( (org.bukkit.craftbukkit.entity.CraftHumanEntity) h).getHandle().closeInventory(org.bukkit.event.inventory.InventoryCloseEvent.Reason.UNLOADED); // Paper
                        }
                    }
                }
                // Spigot End

                // Do not pass along players, as doing so can get them stuck outside of time.
                // (which for example disables inventory icon updates and prevents block breaking)
                if (entity instanceof EntityPlayer) {
                    iter.remove();
                }
            }

            this.world.c(newList);
            // CraftBukkit end
        }

    }

    public void markDirty() {
        this.s = true;
    }

    public void a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, List<Entity> list, Predicate<? super Entity> predicate) {
        int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        for (int k = i; k <= j; ++k) {
            if (!this.entitySlices[k].isEmpty()) {
                Iterator iterator = this.entitySlices[k].iterator();

                // Paper start - Don't search for inventories if we have none, and that is all we want
                /*
                * We check if they want inventories by seeing if it is the static `IEntitySelector.c`
                *
                * Make sure the inventory selector stays in sync.
                * It should be the one that checks `var1 instanceof IInventory && var1.isAlive()`
                */
                if (predicate == IEntitySelector.c && inventoryEntityCounts[k] <= 0) continue;
                // Paper end
                while (iterator.hasNext()) {
                    Entity entity1 = (Entity) iterator.next();

                    if (entity1.getBoundingBox().c(axisalignedbb) && entity1 != entity) {
                        if (predicate == null || predicate.apply(entity1)) {
                            list.add(entity1);
                        }

                        Entity[] aentity = entity1.bb();

                        if (aentity != null) {
                            Entity[] aentity1 = aentity;
                            int l = aentity.length;

                            for (int i1 = 0; i1 < l; ++i1) {
                                Entity entity2 = aentity1[i1];

                                if (entity2 != entity && entity2.getBoundingBox().c(axisalignedbb) && (predicate == null || predicate.apply(entity2))) {
                                    list.add(entity2);
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    public <T extends Entity> void a(Class<? extends T> oclass, AxisAlignedBB axisalignedbb, List<T> list, Predicate<? super T> predicate) {
        int i = MathHelper.floor((axisalignedbb.b - 2.0D) / 16.0D);
        int j = MathHelper.floor((axisalignedbb.e + 2.0D) / 16.0D);

        i = MathHelper.clamp(i, 0, this.entitySlices.length - 1);
        j = MathHelper.clamp(j, 0, this.entitySlices.length - 1);

        // Paper start
        int[] counts;
        if (EntityItem.class.isAssignableFrom(oclass)) {
            counts = itemCounts;
        } else if (IInventory.class.isAssignableFrom(oclass)) {
            counts = inventoryEntityCounts;
        } else {
            counts = null;
        }
        // Paper end
        for (int k = i; k <= j; ++k) {
            if (counts != null && counts[k] <= 0) continue; // Paper - Don't check a chunk if it doesn't have the type we are looking for
            Iterator iterator = this.entitySlices[k].iterator(); // Spigot

            while (iterator.hasNext()) {
                Entity entity = (Entity) iterator.next();

                if (oclass.isInstance(entity) && entity.getBoundingBox().c(axisalignedbb) && (predicate == null || predicate.apply((T) entity))) { // CraftBukkit - fix decompile error // Spigot - instance check
                    list.add((T) entity); // Fix decompile error
                }
            }
        }

    }

    public boolean a(boolean flag) {
        if (flag) {
            if (this.t && this.world.getTime() != this.lastSaved || this.s) {
                return true;
            }
        }
        // This !flag section should say if s(isModified) or t(hasEntities), then check auto save
        return ((this.s || this.t) && this.world.getTime() >= this.lastSaved + world.paperConfig.autoSavePeriod); // Paper - Make world configurable and incremental
    }

    public Random a(long i) {
        return new Random(this.world.getSeed() + (long) (this.locX * this.locX * 4987142) + (long) (this.locX * 5947611) + (long) (this.locZ * this.locZ) * 4392871L + (long) (this.locZ * 389711) ^ i);
    }

    public boolean isEmpty() {
        return false;
    }

    // CraftBukkit start
    public void loadNearby(IChunkProvider ichunkprovider, ChunkGenerator chunkgenerator, boolean newChunk) {
        world.timings.syncChunkLoadPostTimer.startTiming(); // Paper
        Server server = world.getServer();
        if (server != null) {
            /*
             * If it's a new world, the first few chunks are generated inside
             * the World constructor. We can't reliably alter that, so we have
             * no way of creating a CraftWorld/CraftServer at that point.
             */
            server.getPluginManager().callEvent(new org.bukkit.event.world.ChunkLoadEvent(bukkitChunk, newChunk));
        }

        // Update neighbor counts
        for (int x = -2; x < 3; x++) {
            for (int z = -2; z < 3; z++) {
                if (x == 0 && z == 0) {
                    continue;
                }

                Chunk neighbor = getWorld().getChunkIfLoaded(locX + x, locZ + z);
                if (neighbor != null) {
                    neighbor.setNeighborLoaded(-x, -z);
                    setNeighborLoaded(x, z);
                }
            }
        }
        // CraftBukkit end
        world.timings.syncChunkLoadPostTimer.stopTiming(); // Paper
        world.timings.syncChunkLoadPopulateNeighbors.startTiming(); // Paper
        Chunk chunk = MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX, this.locZ - 1); // Paper
        Chunk chunk1 = MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX + 1, this.locZ); // Paper
        Chunk chunk2 = MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX, this.locZ + 1); // Paper
        Chunk chunk3 = MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX - 1, this.locZ); // Paper

        if (chunk1 != null && chunk2 != null && MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX + 1, this.locZ + 1) != null) { // Paper
            this.a(chunkgenerator);
        }

        if (chunk3 != null && chunk2 != null && MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX - 1, this.locZ + 1) != null) { // Paper
            chunk3.a(chunkgenerator);
        }

        if (chunk != null && chunk1 != null && MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX + 1, this.locZ - 1) != null) { // Paper
            chunk.a(chunkgenerator);
        }

        if (chunk != null && chunk3 != null) {
            Chunk chunk4 = MCUtil.getLoadedChunkWithoutMarkingActive(ichunkprovider,this.locX - 1, this.locZ - 1); // Paper

            if (chunk4 != null) {
                chunk4.a(chunkgenerator);
            }
        }
        world.timings.syncChunkLoadPopulateNeighbors.stopTiming(); // Paper

    }

    protected void a(ChunkGenerator chunkgenerator) {
        if (this.isDone()) {
            if (chunkgenerator.a(this, this.locX, this.locZ)) {
                this.markDirty();
            }
        } else {
            this.o();
            chunkgenerator.recreateStructures(this.locX, this.locZ);

            // CraftBukkit start
            BlockSand.instaFall = true;
            Random random = new Random();
            random.setSeed(world.getSeed());
            long xRand = random.nextLong() / 2L * 2L + 1L;
            long zRand = random.nextLong() / 2L * 2L + 1L;
            random.setSeed((long) locX * xRand + (long) locZ * zRand ^ world.getSeed());

            org.bukkit.World world = this.world.getWorld();
            if (world != null) {
                this.world.populating = true;
                try {
                    for (org.bukkit.generator.BlockPopulator populator : world.getPopulators()) {
                        populator.populate(world, random, bukkitChunk);
                    }
                } finally {
                    this.world.populating = false;
                }
            }
            BlockSand.instaFall = false;
            this.world.getServer().getPluginManager().callEvent(new org.bukkit.event.world.ChunkPopulateEvent(bukkitChunk));
            // CraftBukkit end
            this.markDirty();
        }

    }

    public BlockPosition f(BlockPosition blockposition) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getZ() & 15;
        int k = i | j << 4;
        BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), this.h[k], blockposition.getZ());

        if (blockposition1.getY() == -999) {
            int l = this.g() + 15;

            blockposition1 = new BlockPosition(blockposition.getX(), l, blockposition.getZ());
            int i1 = -1;

            while (blockposition1.getY() > 0 && i1 == -1) {
                IBlockData iblockdata = this.getBlockData(blockposition1);
                Material material = iblockdata.getMaterial();

                if (!material.isSolid() && !material.isLiquid()) {
                    blockposition1 = blockposition1.down();
                } else {
                    i1 = blockposition1.getY() + 1;
                }
            }

            this.h[k] = i1;
        }

        return new BlockPosition(blockposition.getX(), this.h[k], blockposition.getZ());
    }

    public void b(boolean flag) {
        if (this.m && this.world.worldProvider.m() && !flag) {
            this.h(this.world.isClientSide);
        }

        this.r = true;
        if (!this.lit && this.done && this.world.spigotConfig.randomLightUpdates) { // Spigot - also use random light updates setting to determine if we should relight
            this.o();
        }

        while (!this.y.isEmpty()) {
            BlockPosition blockposition = (BlockPosition) this.y.poll();

            if (this.a(blockposition, Chunk.EnumTileEntityState.CHECK) == null && this.getBlockData(blockposition).getBlock().isTileEntity()) {
                TileEntity tileentity = this.g(blockposition);

                this.world.setTileEntity(blockposition, tileentity);
                this.world.b(blockposition, blockposition);
            }
        }

    }

    public boolean isReady() {
        // Spigot Start
        /*
         * As of 1.7, Mojang added a check to make sure that only chunks which have been lit are sent to the client.
         * Unfortunately this interferes with our modified chunk ticking algorithm, which will only tick chunks distant from the player on a very infrequent basis.
         * We cannot unfortunately do this lighting stage during chunk gen as it appears to put a lot more noticeable load on the server, than when it is done at play time.
         * For now at least we will simply send all chunks, in accordance with pre 1.7 behaviour.
         */
        // Paper Start
        // if randomLightUpdates are disabled, we should always return true, otherwise chunks may never send
        // to the client due to not being lit, otherwise retain standard behavior and only send properly lit chunks.
        return !this.world.spigotConfig.randomLightUpdates || (this.isTicked() && this.done && this.lit);
        // Paper End
        // Spigot End
    }

    public boolean j() {
        return this.r;
    }

    public ChunkCoordIntPair k() {
        return new ChunkCoordIntPair(this.locX, this.locZ);
    }

    public boolean c(int i, int j) {
        if (i < 0) {
            i = 0;
        }

        if (j >= 256) {
            j = 255;
        }

        for (int k = i; k <= j; k += 16) {
            ChunkSection chunksection = this.sections[k >> 4];

            if (chunksection != Chunk.a && !chunksection.a()) {
                return false;
            }
        }

        return true;
    }

    public void a(ChunkSection[] achunksection) {
        if (this.sections.length != achunksection.length) {
            Chunk.e.warn("Could not set level chunk sections, array length is {} instead of {}", Integer.valueOf(achunksection.length), Integer.valueOf(this.sections.length));
        } else {
            System.arraycopy(achunksection, 0, this.sections, 0, this.sections.length);
        }
    }

    public BiomeBase getBiome(BlockPosition blockposition, WorldChunkManager worldchunkmanager) {
        int i = blockposition.getX() & 15;
        int j = blockposition.getZ() & 15;
        int k = this.g[j << 4 | i] & 255;
        BiomeBase biomebase;

        if (k == 255) {
            biomebase = worldchunkmanager.getBiome(blockposition, Biomes.c);
            k = BiomeBase.a(biomebase);
            this.g[j << 4 | i] = (byte) (k & 255);
        }

        biomebase = BiomeBase.getBiome(k);
        return biomebase == null ? Biomes.c : biomebase;
    }

    public byte[] getBiomeIndex() {
        return this.g;
    }

    public void a(byte[] abyte) {
        if (this.g.length != abyte.length) {
            Chunk.e.warn("Could not set level chunk biomes, array length is {} instead of {}", Integer.valueOf(abyte.length), Integer.valueOf(this.g.length));
        } else {
            System.arraycopy(abyte, 0, this.g, 0, this.g.length);
        }
    }

    public void m() {
        this.x = 0;
    }

    public void n() {
        if (this.x < 4096) {
            BlockPosition blockposition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

            for (int i = 0; i < 8; ++i) {
                if (this.x >= 4096) {
                    return;
                }

                int j = this.x % 16;
                int k = this.x / 16 % 16;
                int l = this.x / 256;

                ++this.x;

                for (int i1 = 0; i1 < 16; ++i1) {
                    BlockPosition blockposition1 = blockposition.a(k, (j << 4) + i1, l);
                    boolean flag = i1 == 0 || i1 == 15 || k == 0 || k == 15 || l == 0 || l == 15;

                    if (this.sections[j] == Chunk.a && flag || this.sections[j] != Chunk.a && this.sections[j].getType(k, i1, l).getMaterial() == Material.AIR) {
                        EnumDirection[] aenumdirection = EnumDirection.values();
                        int j1 = aenumdirection.length;

                        for (int k1 = 0; k1 < j1; ++k1) {
                            EnumDirection enumdirection = aenumdirection[k1];
                            BlockPosition blockposition2 = blockposition1.shift(enumdirection);

                            if (this.world.getType(blockposition2).d() > 0) {
                                this.world.w(blockposition2);
                            }
                        }

                        this.world.w(blockposition1);
                    }
                }
            }

        }
    }

    public void o() {
        world.timings.lightChunk.startTiming(); // Paper
        this.done = true;
        this.lit = true;
        BlockPosition blockposition = new BlockPosition(this.locX << 4, 0, this.locZ << 4);

        if (this.world.worldProvider.m()) {
            if (this.world.areChunksLoadedBetween(blockposition.a(-1, 0, -1), blockposition.a(16, this.world.getSeaLevel(), 16))) {
                label42:
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (!this.e(i, j)) {
                            this.lit = false;
                            break label42;
                        }
                    }
                }

                if (this.lit) {
                    Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                    while (iterator.hasNext()) {
                        EnumDirection enumdirection = (EnumDirection) iterator.next();
                        int k = enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE ? 16 : 1;

                        this.world.getChunkAtWorldCoords(blockposition.shift(enumdirection, k)).a(enumdirection.opposite());
                    }

                    this.z();
                }
            } else {
                this.lit = false;
            }
        }

        world.timings.lightChunk.stopTiming(); // Paper
    }

    private void z() {
        for (int i = 0; i < this.i.length; ++i) {
            this.i[i] = true;
        }

        this.h(false);
    }

    public void a(EnumDirection enumdirection) { // Akarin - private -> public
        if (this.done) {
            int i;

            if (enumdirection == EnumDirection.EAST) {
                for (i = 0; i < 16; ++i) {
                    this.e(15, i);
                }
            } else if (enumdirection == EnumDirection.WEST) {
                for (i = 0; i < 16; ++i) {
                    this.e(0, i);
                }
            } else if (enumdirection == EnumDirection.SOUTH) {
                for (i = 0; i < 16; ++i) {
                    this.e(i, 15);
                }
            } else if (enumdirection == EnumDirection.NORTH) {
                for (i = 0; i < 16; ++i) {
                    this.e(i, 0);
                }
            }

        }
    }

    private boolean e(int i, int j) {
        int k = this.g();
        boolean flag = false;
        boolean flag1 = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition((this.locX << 4) + i, 0, (this.locZ << 4) + j);

        int l;

        for (l = k + 16 - 1; l > this.world.getSeaLevel() || l > 0 && !flag1; --l) {
            blockposition_mutableblockposition.c(blockposition_mutableblockposition.getX(), l, blockposition_mutableblockposition.getZ());
            int i1 = this.b((BlockPosition) blockposition_mutableblockposition);

            if (i1 == 255 && blockposition_mutableblockposition.getY() < this.world.getSeaLevel()) {
                flag1 = true;
            }

            if (!flag && i1 > 0) {
                flag = true;
            } else if (flag && i1 == 0 && !this.world.w(blockposition_mutableblockposition)) {
                return false;
            }
        }

        for (l = blockposition_mutableblockposition.getY(); l > 0; --l) {
            blockposition_mutableblockposition.c(blockposition_mutableblockposition.getX(), l, blockposition_mutableblockposition.getZ());
            if (this.getBlockData(blockposition_mutableblockposition).d() > 0) {
                this.world.w(blockposition_mutableblockposition);
            }
        }

        return true;
    }

    public boolean p() {
        return this.j;
    }

    public World getWorld() {
        return this.world;
    }

    public int[] r() {
        return this.heightMap;
    }

    public void a(int[] aint) {
        if (this.heightMap.length != aint.length) {
            Chunk.e.warn("Could not set level chunk heightmap, array length is {} instead of {}", Integer.valueOf(aint.length), Integer.valueOf(this.heightMap.length));
        } else {
            System.arraycopy(aint, 0, this.heightMap, 0, this.heightMap.length);
        }
    }

    public Map<BlockPosition, TileEntity> getTileEntities() {
        return this.tileEntities;
    }

    public List<Entity>[] getEntitySlices() {
        return this.entitySlices;
    }

    public boolean isDone() {
        return this.done;
    }

    public void d(boolean flag) {
        this.done = flag;
    }

    public boolean v() {
        return this.lit;
    }

    public void e(boolean flag) {
        this.lit = flag;
    }

    public void f(boolean flag) {
        this.s = flag;
    }

    public void g(boolean flag) {
        this.t = flag;
    }

    public void setLastSaved(long i) {
        this.lastSaved = i;
    }

    public int w() {
        return this.v;
    }

    public long x() {
        return world.paperConfig.useInhabitedTime ? this.w : 0; // Paper
    }

    public void c(long i) {
        this.w = i;
    }

    // Paper start
    public void runOrQueueLightUpdate(Runnable runnable) {
        if (this.world.paperConfig.queueLightUpdates) {
            lightingQueue.add(runnable);
        } else {
            runnable.run();
        }
    }
    // Paper end

    public static enum EnumTileEntityState {

        IMMEDIATE, QUEUED, CHECK;

        private EnumTileEntityState() {}
    }
}
