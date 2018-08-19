package net.minecraft.server;

import co.aikar.timings.Timing;
import com.google.common.base.Predicate;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

// CraftBukkit start
import java.util.LinkedList;
// CraftBukkit end

/**
 * Akarin Changes Note
 * 1) Make whole class thread-safe (safety issue)
 */
@ThreadSafe // Akarin - idk why we need do so!!
public class PlayerChunkMap {

    private static final Predicate<EntityPlayer> a = new Predicate() {
        public boolean a(@Nullable EntityPlayer entityplayer) {
            return entityplayer != null && !entityplayer.isSpectator();
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EntityPlayer) object);
        }
    };
    private static final Predicate<EntityPlayer> b = new Predicate() {
        public boolean a(@Nullable EntityPlayer entityplayer) {
            return entityplayer != null && (!entityplayer.isSpectator() || entityplayer.x().getGameRules().getBoolean("spectatorsGenerateChunks"));
        }

        public boolean apply(@Nullable Object object) {
            return this.a((EntityPlayer) object);
        }
    };
    private final WorldServer world;
    private final List<EntityPlayer> managedPlayers = Lists.newArrayList();
    private final ReentrantReadWriteLock managedPlayersLock = new ReentrantReadWriteLock(); // Akarin - add lock
    private final Long2ObjectMap<PlayerChunk> e = new Long2ObjectOpenHashMap(4096);
    private final Set<PlayerChunk> f = Sets.newHashSet();
    private final List<PlayerChunk> g = Lists.newLinkedList();
    private final List<PlayerChunk> h = Lists.newLinkedList();
    private final List<PlayerChunk> i = Lists.newCopyOnWriteArrayList(); // Akarin - bad plugin will access this
    private int j; public int getViewDistance() { return j; } // Paper OBFHELPER
    private long k;
    private boolean l = true;
    private boolean m = true;
    private boolean wasNotEmpty; // CraftBukkit - add field

    public PlayerChunkMap(WorldServer worldserver) {
        this.world = worldserver;
        this.a(worldserver.spigotConfig.viewDistance); // Spigot
    }

    public WorldServer getWorld() {
        return this.world;
    }

    public Iterator<Chunk> b() {
        final Iterator iterator = this.i.iterator();

        return new AbstractIterator<Chunk>() {
            protected Chunk a() {
                while (true) {
                    if (iterator.hasNext()) {
                        PlayerChunk playerchunk = (PlayerChunk) iterator.next();
                        Chunk chunk = playerchunk.f();

                        if (chunk == null) {
                            continue;
                        }

                        if (!chunk.v() && chunk.isDone()) {
                            return chunk;
                        }

                        if (!chunk.j()) {
                            return chunk;
                        }

                        if (!playerchunk.a(128.0D, PlayerChunkMap.a)) {
                            continue;
                        }

                        return chunk;
                    }

                    return (Chunk) this.endOfData();
                }
            }

            protected Chunk computeNext() {
                return this.a();
            }
        };
    }

    public synchronized void flush() { // Akarin - synchronized
        long i = this.world.getTime();
        int j;
        PlayerChunk playerchunk;

        if (i - this.k > 8000L) {
            try (Timing ignored = world.timings.doChunkMapUpdate.startTiming()) { // Paper
            this.k = i;

            for (j = 0; j < this.i.size(); ++j) {
                playerchunk = (PlayerChunk) this.i.get(j);
                playerchunk.d();
                playerchunk.c();
            }
            } // Paper timing
        }

        if (!this.f.isEmpty()) {
            try (Timing ignored = world.timings.doChunkMapToUpdate.startTiming()) { // Paper
            Iterator iterator = this.f.iterator();

            while (iterator.hasNext()) {
                playerchunk = (PlayerChunk) iterator.next();
                playerchunk.d();
            }

            this.f.clear();
            } // Paper timing
        }

        if (this.l && i % 4L == 0L) {
            this.l = false;
            try (Timing ignored = world.timings.doChunkMapSortMissing.startTiming()) { // Paper
            Collections.sort(this.h, new Comparator() {
                public int a(PlayerChunk playerchunk, PlayerChunk playerchunk1) {
                    return ComparisonChain.start().compare(playerchunk.g(), playerchunk1.g()).result();
                }

                public int compare(Object object, Object object1) {
                    return this.a((PlayerChunk) object, (PlayerChunk) object1);
                }
            });
            } // Paper timing
        }

        if (this.m && i % 4L == 2L) {
            this.m = false;
            try (Timing ignored = world.timings.doChunkMapSortSendToPlayers.startTiming()) { // Paper
            Collections.sort(this.g, new Comparator() {
                public int a(PlayerChunk playerchunk, PlayerChunk playerchunk1) {
                    return ComparisonChain.start().compare(playerchunk.g(), playerchunk1.g()).result();
                }

                public int compare(Object object, Object object1) {
                    return this.a((PlayerChunk) object, (PlayerChunk) object1);
                }
            });
            } // Paper timing
        }

        if (!this.h.isEmpty()) {
            try (Timing ignored = world.timings.doChunkMapPlayersNeedingChunks.startTiming()) { // Paper
            // Spigot start
            org.spigotmc.SlackActivityAccountant activityAccountant = this.world.getMinecraftServer().slackActivityAccountant;
            activityAccountant.startActivity(0.5);
            int chunkGensAllowed = world.paperConfig.maxChunkGensPerTick; // Paper
            // Spigot end

            Iterator iterator1 = this.h.iterator();

            while (iterator1.hasNext()) {
                PlayerChunk playerchunk1 = (PlayerChunk) iterator1.next();

                if (playerchunk1.f() == null) {
                    boolean flag = playerchunk1.a(PlayerChunkMap.b);
                    // Paper start
                    if (flag && !playerchunk1.chunkExists && chunkGensAllowed-- <= 0) {
                        continue;
                    }
                    // Paper end

                    if (playerchunk1.a(flag)) {
                        iterator1.remove();
                        if (playerchunk1.b()) {
                            this.g.remove(playerchunk1);
                        }

                        if (activityAccountant.activityTimeIsExhausted()) { // Spigot
                            break;
                        }
                    }
                // CraftBukkit start - SPIGOT-2891: remove once chunk has been provided
                } else {
                    iterator1.remove();
                }
                // CraftBukkit end
            }

            activityAccountant.endActivity(); // Spigot
            } // Paper timing
        }

        if (!this.g.isEmpty()) {
            j = world.paperConfig.maxChunkSendsPerTick; // Paper
            try (Timing ignored = world.timings.doChunkMapPendingSendToPlayers.startTiming()) { // Paper
            Iterator iterator2 = this.g.iterator();

            while (iterator2.hasNext()) {
                PlayerChunk playerchunk2 = (PlayerChunk) iterator2.next();

                if (playerchunk2.b()) {
                    iterator2.remove();
                    --j;
                    if (j < 0) {
                        break;
                    }
                }
            }
            } // Paper timing
        }

        managedPlayersLock.readLock().lock(); // Akarin
        if (this.managedPlayers.isEmpty()) {
            try (Timing ignored = world.timings.doChunkMapUnloadChunks.startTiming()) { // Paper
            WorldProvider worldprovider = this.world.worldProvider;

            if (!worldprovider.e() && !this.world.savingDisabled) { // Paper - respect saving disabled setting
                this.world.getChunkProviderServer().b();
            }
            } // Paper timing
        }
        managedPlayersLock.readLock().unlock(); // Akarin

    }

    public synchronized boolean a(int i, int j) { // Akarin - synchronized
        long k = d(i, j);

        return this.e.get(k) != null;
    }

    @Nullable
    public synchronized PlayerChunk getChunk(int i, int j) { // Akarin - synchronized
        return (PlayerChunk) this.e.get(d(i, j));
    }

    private PlayerChunk c(int i, int j) {
        long k = d(i, j);
        PlayerChunk playerchunk = (PlayerChunk) this.e.get(k);

        if (playerchunk == null) {
            playerchunk = new PlayerChunk(this, i, j);
            this.e.put(k, playerchunk);
            this.i.add(playerchunk);
            if (playerchunk.f() == null) {
                this.h.add(playerchunk);
            }

            if (!playerchunk.b()) {
                this.g.add(playerchunk);
            }
        }

        return playerchunk;
    }

    // CraftBukkit start - add method
    public final boolean isChunkInUse(int x, int z) {
        PlayerChunk pi = getChunk(x, z);
        if (pi != null) {
            return (pi.c.size() > 0);
        }
        return false;
    }
    // CraftBukkit end

    public void flagDirty(BlockPosition blockposition) {
        int i = blockposition.getX() >> 4;
        int j = blockposition.getZ() >> 4;
        PlayerChunk playerchunk = this.getChunk(i, j);

        if (playerchunk != null) {
            playerchunk.a(blockposition.getX() & 15, blockposition.getY(), blockposition.getZ() & 15);
        }

    }

    public void addPlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;

        entityplayer.d = entityplayer.locX;
        entityplayer.e = entityplayer.locZ;


        // CraftBukkit start - Load nearby chunks first
        List<ChunkCoordIntPair> chunkList = new LinkedList<ChunkCoordIntPair>();

        // Paper start - Player view distance API
        int viewDistance = entityplayer.getViewDistance();
        for (int k = i - viewDistance; k <= i + viewDistance; ++k) {
            for (int l = j - viewDistance; l <= j + viewDistance; ++l) {
                // Paper end
                chunkList.add(new ChunkCoordIntPair(k, l));
            }
        }

        Collections.sort(chunkList, new ChunkCoordComparator(entityplayer));
        synchronized (this) { // Akarin - synchronized
        for (ChunkCoordIntPair pair : chunkList) {
            this.c(pair.x, pair.z).a(entityplayer);
        }
        } // Akarin
        // CraftBukkit end

        managedPlayersLock.writeLock().lock(); // Akarin
        this.managedPlayers.add(entityplayer);
        managedPlayersLock.writeLock().unlock(); // Akarin
        this.e();
    }

    public void removePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.d >> 4;
        int j = (int) entityplayer.e >> 4;

        // Paper start - Player view distance API
        int viewDistance = entityplayer.getViewDistance();
        for (int k = i - viewDistance; k <= i + viewDistance; ++k) {
            for (int l = j - viewDistance; l <= j + viewDistance; ++l) {
                // Paper end
                PlayerChunk playerchunk = this.getChunk(k, l);

                if (playerchunk != null) {
                    playerchunk.b(entityplayer);
                }
            }
        }

        managedPlayersLock.writeLock().lock(); // Akarin
        this.managedPlayers.remove(entityplayer);
        managedPlayersLock.writeLock().unlock(); // Akarin
        this.e();
    }

    private boolean a(int i, int j, int k, int l, int i1) {
        int j1 = i - k;
        int k1 = j - l;

        return j1 >= -i1 && j1 <= i1 ? k1 >= -i1 && k1 <= i1 : false;
    }

    public void movePlayer(EntityPlayer entityplayer) {
        int i = (int) entityplayer.locX >> 4;
        int j = (int) entityplayer.locZ >> 4;
        double d0 = entityplayer.d - entityplayer.locX;
        double d1 = entityplayer.e - entityplayer.locZ;
        double d2 = d0 * d0 + d1 * d1;

        if (d2 >= 64.0D) {
            int k = (int) entityplayer.d >> 4;
            int l = (int) entityplayer.e >> 4;
            final int viewDistance = entityplayer.getViewDistance(); // Paper - Player view distance API
            int i1 = Math.max(getViewDistance(), viewDistance); // Paper - Player view distance API

            int j1 = i - k;
            int k1 = j - l;

            List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>(); // CraftBukkit

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.a(l1, i2, k, l, viewDistance)) { // Paper - Player view distance API
                            // this.c(l1, i2).a(entityplayer);
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2)); // CraftBukkit
                        }

                        if (!this.a(l1 - j1, i2 - k1, i, j, i1)) {
                            PlayerChunk playerchunk = this.getChunk(l1 - j1, i2 - k1);

                            if (playerchunk != null) {
                                playerchunk.b(entityplayer);
                            }
                        }
                    }
                }

                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;
                this.e();

                // CraftBukkit start - send nearest chunks first
                Collections.sort(chunksToLoad, new ChunkCoordComparator(entityplayer));
                synchronized (this) { // Akarin - synchronized
                for (ChunkCoordIntPair pair : chunksToLoad) {
                    this.c(pair.x, pair.z).a(entityplayer);
                }
                } // Akarin
                // CraftBukkit end
            }
        }
    }

    public boolean a(EntityPlayer entityplayer, int i, int j) {
        PlayerChunk playerchunk = this.getChunk(i, j);

        return playerchunk != null && playerchunk.d(entityplayer) && playerchunk.e();
    }

    public final void setViewDistanceForAll(int viewDistance) { this.a(viewDistance); } // Paper - OBFHELPER
    // Paper start - Separate into two methods
    public void a(int i) {
        i = MathHelper.clamp(i, 3, 32);
        if (i != this.j) {
            int j = i - this.j;
            managedPlayersLock.readLock().lock(); // Akarin
            ArrayList arraylist = Lists.newArrayList(this.managedPlayers);
            managedPlayersLock.readLock().unlock(); // Akarin
            Iterator iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                EntityPlayer entityplayer = (EntityPlayer) iterator.next();
                this.setViewDistance(entityplayer, i, false); // Paper - Split, don't mark sort pending, we'll handle it after
            }

            this.j = i;
            this.e();
        }
    }

    public void setViewDistance(EntityPlayer entityplayer, int i) {
        this.setViewDistance(entityplayer, i, true); // Mark sort pending by default so we don't have to remember to do so all the time
    }
    
    // Copied from above with minor changes
    public void setViewDistance(EntityPlayer entityplayer, int i, boolean markSort) {
        i = MathHelper.clamp(i, 3, 32);
        int oldViewDistance = entityplayer.getViewDistance();
        if (i != oldViewDistance) {
            int j = i - oldViewDistance;
            
            int k = (int) entityplayer.locX >> 4;
            int l = (int) entityplayer.locZ >> 4;
            int i1;
            int j1;

            if (j > 0) {
                synchronized (this) { // Akarin - synchronized
                for (i1 = k - i; i1 <= k + i; ++i1) {
                    for (j1 = l - i; j1 <= l + i; ++j1) {
                        PlayerChunk playerchunk = this.c(i1, j1);

                        if (!playerchunk.d(entityplayer)) {
                            playerchunk.a(entityplayer);
                        }
                    }
                }
                } // Akarin
            } else {
                synchronized (this) { // Akarin - synchronized
                for (i1 = k - oldViewDistance; i1 <= k + oldViewDistance; ++i1) {
                    for (j1 = l - oldViewDistance; j1 <= l + oldViewDistance; ++j1) {
                        if (!this.a(i1, j1, k, l, i)) {
                            this.c(i1, j1).b(entityplayer);
                        }
                    }
                }
                } // Akarin
                if (markSort) {
                    this.e();
                }
            }
        }
    }
    // Paper end

    private void e() {
        this.l = true;
        this.m = true;
    }

    public static int getFurthestViewableBlock(int i) {
        return i * 16 - 16;
    }

    private static long d(int i, int j) {
        return (long) i + 2147483647L | (long) j + 2147483647L << 32;
    }

    public synchronized void a(PlayerChunk playerchunk) { // Akarin - synchronized
        // org.spigotmc.AsyncCatcher.catchOp("Async Player Chunk Add"); // Paper // Akarin
        this.f.add(playerchunk);
    }

    public synchronized void b(PlayerChunk playerchunk) { // Akarin - synchronized
        org.spigotmc.AsyncCatcher.catchOp("Async Player Chunk Remove"); // Paper
        ChunkCoordIntPair chunkcoordintpair = playerchunk.a();
        long i = d(chunkcoordintpair.x, chunkcoordintpair.z);

        playerchunk.c();
        this.e.remove(i);
        this.i.remove(playerchunk);
        this.f.remove(playerchunk);
        this.g.remove(playerchunk);
        this.h.remove(playerchunk);
        Chunk chunk = playerchunk.f();

        if (chunk != null) {
            // Paper start - delay chunk unloads
            if (world.paperConfig.delayChunkUnloadsBy <= 0) {
                this.getWorld().getChunkProviderServer().unload(chunk);
            } else {
                chunk.scheduledForUnload = System.currentTimeMillis();
            }
            // Paper end
        }

    }

    // CraftBukkit start - Sorter to load nearby chunks first
    private static class ChunkCoordComparator implements java.util.Comparator<ChunkCoordIntPair> {
        private int x;
        private int z;

        public ChunkCoordComparator (EntityPlayer entityplayer) {
            x = (int) entityplayer.locX >> 4;
            z = (int) entityplayer.locZ >> 4;
        }

        public int compare(ChunkCoordIntPair a, ChunkCoordIntPair b) {
            if (a.equals(b)) {
                return 0;
            }

            // Subtract current position to set center point
            int ax = a.x - this.x;
            int az = a.z - this.z;
            int bx = b.x - this.x;
            int bz = b.z - this.z;

            int result = ((ax - bx) * (ax + bx)) + ((az - bz) * (az + bz));
            if (result != 0) {
                return result;
            }

            if (ax < 0) {
                if (bx < 0) {
                    return bz - az;
                } else {
                    return -1;
                }
            } else {
                if (bx < 0) {
                    return 1;
                } else {
                    return az - bz;
                }
            }
        }
    }
    // CraftBukkit end

    // Paper start - Player view distance API
    public void updateViewDistance(EntityPlayer player, int distanceIn) {
        final int oldViewDistance = player.getViewDistance();

        // This represents the view distance that we will set on the player
        // It can exist as a negative value
        int playerViewDistance = MathHelper.clamp(distanceIn, 3, 32);

        // This value is the one we actually use to update the chunk map
        // We don't ever want this to be a negative
        int toSet = playerViewDistance;

        if (distanceIn < 0) {
            playerViewDistance = -1;
            toSet = world.getPlayerChunkMap().getViewDistance();
        }

        if (toSet != oldViewDistance) {
            // Order matters
            this.setViewDistance(player, toSet);
            player.setViewDistance(playerViewDistance);
        }
    }
    // Paper end
}
