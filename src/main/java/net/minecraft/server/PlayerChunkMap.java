package net.minecraft.server;

import co.aikar.timings.Timing;
import com.google.common.collect.AbstractIterator;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;

// CraftBukkit start
import java.util.LinkedList;
// CraftBukkit end

public class PlayerChunkMap {

    private static final Predicate<EntityPlayer> a = (entityplayer) -> {
        return entityplayer != null && !entityplayer.isSpectator();
    };
    private static final Predicate<EntityPlayer> b = (entityplayer) -> {
        return entityplayer != null && (!entityplayer.isSpectator() || entityplayer.getWorldServer().getGameRules().getBoolean("spectatorsGenerateChunks"));
    }; static final Predicate<EntityPlayer> CAN_GEN_CHUNKS = b; // Paper - OBFHELPER
    private final WorldServer world;
    private final List<EntityPlayer> managedPlayers = Lists.newArrayList();
    private final Long2ObjectMap<PlayerChunk> e = new Long2ObjectOpenHashMap(4096); Long2ObjectMap<PlayerChunk> getChunks() { return e; } // Paper - OBFHELPER
    private final Set<PlayerChunk> f = Sets.newHashSet();
    private final List<PlayerChunk> g = Lists.newLinkedList();
    private final List<PlayerChunk> h = Lists.newLinkedList();
    private final List<PlayerChunk> i = Lists.newArrayList();
    private int j;public int getViewDistance() { return j; } // Paper OBFHELPER
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
        final Iterator<PlayerChunk> iterator = this.i.iterator();

        return new AbstractIterator<Chunk>() {
            protected Chunk computeNext() {
                while (true) {
                    if (iterator.hasNext()) {
                        PlayerChunk playerchunk = (PlayerChunk) iterator.next();
                        Chunk chunk = playerchunk.f();

                        if (chunk == null) {
                            continue;
                        }

                        if (!chunk.v()) {
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
        };
    }

    public void flush() {
        long i = this.world.getTime();
        PlayerChunk playerchunk;
        int j;

        if (i - this.k > 8000L) {
            try (Timing ignored = world.timings.doChunkMapUpdate.startTimingUnsafe()) { // Paper // Akarin
            this.k = i;

            for (j = 0; j < this.i.size(); ++j) {
                playerchunk = (PlayerChunk) this.i.get(j);
                playerchunk.d();
                playerchunk.c();
            }
            } // Paper timing
        }

        if (!this.f.isEmpty()) {
            try (Timing ignored = world.timings.doChunkMapToUpdate.startTimingUnsafe()) { // Paper // Akarin
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
            try (Timing ignored = world.timings.doChunkMapSortMissing.startTimingUnsafe()) { // Paper // Akarin
            Collections.sort(this.h, (playerchunk1, playerchunk2) -> {
                return ComparisonChain.start().compare(playerchunk1.g(), playerchunk2.g()).result();
            });
            } // Paper timing
        }

        if (this.m && i % 4L == 2L) {
            this.m = false;
            try (Timing ignored = world.timings.doChunkMapSortSendToPlayers.startTimingUnsafe()) { // Paper // Akarin
            Collections.sort(this.g, (playerchunk1, playerchunk2) -> {
                return ComparisonChain.start().compare(playerchunk1.g(), playerchunk2.g()).result();
            });
            } // Paper timing
        }

        if (!this.h.isEmpty()) {
            try (Timing ignored = world.timings.doChunkMapPlayersNeedingChunks.startTimingUnsafe()) { // Paper // Akarin
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
            try (Timing ignored = world.timings.doChunkMapPendingSendToPlayers.startTimingUnsafe()) { // Paper // Akarin
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

        if (this.managedPlayers.isEmpty()) {
            try (Timing ignored = world.timings.doChunkMapUnloadChunks.startTimingUnsafe()) { // Paper // Akarin
            WorldProvider worldprovider = this.world.worldProvider;

            if (!worldprovider.canRespawn() && !this.world.savingDisabled) { // Paper - respect saving disabled setting
                this.world.getChunkProvider().b();
            }
            } // Paper timing
        }

    }

    public boolean a(int i, int j) {
        long k = d(i, j);

        return this.e.get(k) != null;
    }

    @Nullable
    public PlayerChunk getChunk(int i, int j) {
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
            return (pi.players.size() > 0);
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
        for (ChunkCoordIntPair pair : chunkList) {
            this.c(pair.x, pair.z).a(entityplayer);
        }
        // CraftBukkit end

        this.managedPlayers.add(entityplayer);
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

        this.managedPlayers.remove(entityplayer);
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
            int i1 = entityplayer.getViewDistance(); // Paper - Player view distance API

            int j1 = i - k;
            int k1 = j - l;

            List<ChunkCoordIntPair> chunksToLoad = new LinkedList<ChunkCoordIntPair>(); // CraftBukkit

            if (j1 != 0 || k1 != 0) {
                for (int l1 = i - i1; l1 <= i + i1; ++l1) {
                    for (int i2 = j - i1; i2 <= j + i1; ++i2) {
                        if (!this.a(l1, i2, k, l, i1)) {
                            // this.c(l1, i2).a(entityplayer);
                            chunksToLoad.add(new ChunkCoordIntPair(l1, i2)); // CraftBukkit
                        }

                        if (!this.a(l1 - j1, i2 - k1, i, j, i1)) {
                            PlayerChunk playerchunk = this.getChunk(l1 - j1, i2 - k1);

                            if (playerchunk != null) {
                                playerchunk.b(entityplayer);
                            }
                        } else { // Paper start
                            PlayerChunk playerchunk = this.getChunk(l1 - j1, i2 - k1);
                            if (playerchunk != null) {
                                playerchunk.checkHighPriority(entityplayer); // Paper
                            }
                        }
                        // Paper end
                    }
                }

                entityplayer.d = entityplayer.locX;
                entityplayer.e = entityplayer.locZ;
                this.e();

                // CraftBukkit start - send nearest chunks first
                Collections.sort(chunksToLoad, new ChunkCoordComparator(entityplayer));
                for (ChunkCoordIntPair pair : chunksToLoad) {
                    // Paper start
                    PlayerChunk c = this.c(pair.x, pair.z);
                    c.checkHighPriority(entityplayer);
                    c.a(entityplayer);
                    // Paper end
                }
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
            List<EntityPlayer> list = Lists.newArrayList(this.managedPlayers);
            Iterator iterator = list.iterator();

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
                for (i1 = k - i; i1 <= k + i; ++i1) {
                    for (j1 = l - i; j1 <= l + i; ++j1) {
                        PlayerChunk playerchunk = this.c(i1, j1);

                        if (!playerchunk.d(entityplayer)) {
                            playerchunk.a(entityplayer);
                        }
                    }
                }
            } else {
                for (i1 = k - oldViewDistance; i1 <= k + oldViewDistance; ++i1) {
                    for (j1 = l - oldViewDistance; j1 <= l + oldViewDistance; ++j1) {
                        if (!this.a(i1, j1, k, l, i)) {
                            this.c(i1, j1).b(entityplayer);
                        }
                    }
                }
                if (markSort) {
                    this.e();
                }
            }
        }
    }

    void shutdown() {
        getChunks().values().forEach(pchunk -> {
                PaperAsyncChunkProvider.CancellableChunkRequest chunkRequest = pchunk.chunkRequest;
                if (chunkRequest != null) {
                    chunkRequest.cancel();
                }
        });
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

    public void a(PlayerChunk playerchunk) {
        org.spigotmc.AsyncCatcher.catchOp("Async Player Chunk Add"); // Paper
        this.f.add(playerchunk);
    }

    public void b(PlayerChunk playerchunk) {
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
                this.getWorld().getChunkProvider().unload(chunk);
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

            //Force update entity trackers
            this.getWorld().getTracker().updatePlayer(player);
        }
    }
    // Paper end
}
