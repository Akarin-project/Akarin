package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PlayerChunk {

    private static final Logger a = LogManager.getLogger();
    private final PlayerChunkMap playerChunkMap;
    public final List<EntityPlayer> players = Lists.newArrayList();
    private final ChunkCoordIntPair location;
    private final short[] dirtyBlocks = new short[64];
    @Nullable
    public Chunk chunk;
    private int dirtyCount;
    private int h;
    private long i;
    private boolean done;

    public PlayerChunk(PlayerChunkMap playerchunkmap, int i, int j) {
        this.playerChunkMap = playerchunkmap;
        this.location = new ChunkCoordIntPair(i, j);
        ChunkProviderServer chunkproviderserver = playerchunkmap.getWorld().getChunkProvider();

        chunkproviderserver.a(i, j);
        this.chunk = chunkproviderserver.getChunkAt(i, j, true, false);
    }

    public ChunkCoordIntPair a() {
        return this.location;
    }

    public void a(EntityPlayer entityplayer) {
        if (this.players.contains(entityplayer)) {
            PlayerChunk.a.debug("Failed to add player. {} already is in chunk {}, {}", entityplayer, this.location.x, this.location.z);
        } else {
            if (this.players.isEmpty()) {
                this.i = this.playerChunkMap.getWorld().getTime();
            }

            this.players.add(entityplayer);
            if (this.done) {
                this.sendChunk(entityplayer);
            }

        }
    }

    public void b(EntityPlayer entityplayer) {
        if (this.players.contains(entityplayer)) {
            if (this.done) {
                entityplayer.playerConnection.sendPacket(new PacketPlayOutUnloadChunk(this.location.x, this.location.z));
            }

            this.players.remove(entityplayer);
            if (this.players.isEmpty()) {
                this.playerChunkMap.b(this);
            }

        }
    }

    public boolean a(boolean flag) {
        if (this.chunk != null) {
            return true;
        } else {
            this.chunk = this.playerChunkMap.getWorld().getChunkProvider().getChunkAt(this.location.x, this.location.z, true, flag);
            return this.chunk != null;
        }
    }

    public boolean b() {
        if (this.done) {
            return true;
        } else if (this.chunk == null) {
            return false;
        } else if (!this.chunk.isReady()) {
            return false;
        } else {
            this.dirtyCount = 0;
            this.h = 0;
            this.done = true;
            if (!this.players.isEmpty()) {
                Packet<?> packet = new PacketPlayOutMapChunk(this.chunk, 65535);
                Iterator iterator = this.players.iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    entityplayer.playerConnection.sendPacket(packet);
                    this.playerChunkMap.getWorld().getTracker().a(entityplayer, this.chunk);
                }
            }

            return true;
        }
    }

    public void sendChunk(EntityPlayer entityplayer) {
        if (this.done) {
            entityplayer.playerConnection.sendPacket(new PacketPlayOutMapChunk(this.chunk, 65535));
            this.playerChunkMap.getWorld().getTracker().a(entityplayer, this.chunk);
        }
    }

    public void c() {
        long i = this.playerChunkMap.getWorld().getTime();

        if (this.chunk != null) {
            this.chunk.b(this.chunk.m() + i - this.i);
        }

        this.i = i;
    }

    public void a(int i, int j, int k) {
        if (this.done) {
            if (this.dirtyCount == 0) {
                this.playerChunkMap.a(this);
            }

            this.h |= 1 << (j >> 4);
            if (this.dirtyCount < 64) {
                short short0 = (short) (i << 12 | k << 8 | j);

                for (int l = 0; l < this.dirtyCount; ++l) {
                    if (this.dirtyBlocks[l] == short0) {
                        return;
                    }
                }

                this.dirtyBlocks[this.dirtyCount++] = short0;
            }

        }
    }

    public void a(Packet<?> packet) {
        if (this.done) {
            for (int i = 0; i < this.players.size(); ++i) {
                ((EntityPlayer) this.players.get(i)).playerConnection.sendPacket(packet);
            }

        }
    }

    public void d() {
        if (this.done && this.chunk != null) {
            if (this.dirtyCount != 0) {
                int i;
                int j;
                int k;

                if (this.dirtyCount == 1) {
                    i = (this.dirtyBlocks[0] >> 12 & 15) + this.location.x * 16;
                    j = this.dirtyBlocks[0] & 255;
                    k = (this.dirtyBlocks[0] >> 8 & 15) + this.location.z * 16;
                    BlockPosition blockposition = new BlockPosition(i, j, k);

                    this.a((Packet) (new PacketPlayOutBlockChange(this.playerChunkMap.getWorld(), blockposition)));
                    if (this.playerChunkMap.getWorld().getType(blockposition).getBlock().isTileEntity()) {
                        this.a(this.playerChunkMap.getWorld().getTileEntity(blockposition));
                    }
                } else if (this.dirtyCount == 64) {
                    this.a((Packet) (new PacketPlayOutMapChunk(this.chunk, this.h)));
                } else {
                    this.a((Packet) (new PacketPlayOutMultiBlockChange(this.dirtyCount, this.dirtyBlocks, this.chunk)));

                    for (i = 0; i < this.dirtyCount; ++i) {
                        j = (this.dirtyBlocks[i] >> 12 & 15) + this.location.x * 16;
                        k = this.dirtyBlocks[i] & 255;
                        int l = (this.dirtyBlocks[i] >> 8 & 15) + this.location.z * 16;
                        BlockPosition blockposition1 = new BlockPosition(j, k, l);

                        if (this.playerChunkMap.getWorld().getType(blockposition1).getBlock().isTileEntity()) {
                            this.a(this.playerChunkMap.getWorld().getTileEntity(blockposition1));
                        }
                    }
                }

                this.dirtyCount = 0;
                this.h = 0;
            }
        }
    }

    private void a(@Nullable TileEntity tileentity) {
        if (tileentity != null) {
            PacketPlayOutTileEntityData packetplayouttileentitydata = tileentity.getUpdatePacket();

            if (packetplayouttileentitydata != null) {
                this.a((Packet) packetplayouttileentitydata);
            }
        }

    }

    public boolean d(EntityPlayer entityplayer) {
        return this.players.contains(entityplayer);
    }

    public boolean a(Predicate<EntityPlayer> predicate) {
        return this.players.stream().anyMatch(predicate);
    }

    public boolean a(double d0, Predicate<EntityPlayer> predicate) {
        int i = 0;

        for (int j = this.players.size(); i < j; ++i) {
            EntityPlayer entityplayer = (EntityPlayer) this.players.get(i);

            if (predicate.test(entityplayer) && this.location.a(entityplayer) < d0 * d0) {
                return true;
            }
        }

        return false;
    }

    public boolean e() {
        return this.done;
    }

    @Nullable
    public Chunk f() {
        return this.chunk;
    }

    public double g() {
        double d0 = Double.MAX_VALUE;
        Iterator iterator = this.players.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();
            double d1 = this.location.a(entityplayer);

            if (d1 < d0) {
                d0 = d1;
            }
        }

        return d0;
    }
}
