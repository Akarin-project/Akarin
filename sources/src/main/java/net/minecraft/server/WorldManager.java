package net.minecraft.server;

import java.util.Iterator;
import javax.annotation.Nullable;

public class WorldManager implements IWorldAccess {

    private final MinecraftServer a;
    private final WorldServer world;

    public WorldManager(MinecraftServer minecraftserver, WorldServer worldserver) {
        this.a = minecraftserver;
        this.world = worldserver;
    }

    public void a(int i, boolean flag, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {}

    public void a(int i, boolean flag, boolean flag1, double d0, double d1, double d2, double d3, double d4, double d5, int... aint) {}

    public void a(Entity entity) {
        this.world.getTracker().track(entity);
        if (entity instanceof EntityPlayer) {
            this.world.worldProvider.a((EntityPlayer) entity);
        }

    }

    public void b(Entity entity) {
        this.world.getTracker().untrackEntity(entity);
        this.world.getScoreboard().a(entity);
        if (entity instanceof EntityPlayer) {
            this.world.worldProvider.b((EntityPlayer) entity);
        }

    }

    public void a(@Nullable EntityHuman entityhuman, SoundEffect soundeffect, SoundCategory soundcategory, double d0, double d1, double d2, float f, float f1) {
        // CraftBukkit - this.world.dimension, // Paper - this.world.dimension -> this.world
        this.a.getPlayerList().sendPacketNearby(entityhuman, d0, d1, d2, f > 1.0F ? (double) (16.0F * f) : 16.0D, this.world, new PacketPlayOutNamedSoundEffect(soundeffect, soundcategory, d0, d1, d2, f, f1));
    }

    public void a(int i, int j, int k, int l, int i1, int j1) {}

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1, int i) {
        this.world.getPlayerChunkMap().flagDirty(blockposition);
    }

    public void a(BlockPosition blockposition) {}

    public void a(SoundEffect soundeffect, BlockPosition blockposition) {}

    public void a(EntityHuman entityhuman, int i, BlockPosition blockposition, int j) {
        // CraftBukkit - this.world.dimension
        this.a.getPlayerList().sendPacketNearby(entityhuman, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), 64.0D, this.world, new PacketPlayOutWorldEvent(i, blockposition, j, false));
    }

    public void a(int i, BlockPosition blockposition, int j) {
        this.a.getPlayerList().sendAll(new PacketPlayOutWorldEvent(i, blockposition, j, true));
    }

    public void b(int i, BlockPosition blockposition, int j) {
        // Iterator iterator = this.a.getPlayerList().v().iterator(); // Paper

        // CraftBukkit start
        EntityHuman entityhuman = null;
        Entity entity = world.getEntity(i);
        if (entity instanceof EntityHuman) entityhuman = (EntityHuman) entity;
        // CraftBukkit end

        // Paper start
        java.util.List<? extends EntityHuman> list = entity != null ? entity.world.players : this.a.getPlayerList().v();
        Iterator<? extends EntityHuman> iterator = list.iterator();
		PacketPlayOutBlockBreakAnimation packet = null; // NeonPaper - cache packet
        while (iterator.hasNext()) {
            EntityHuman human = iterator.next();
            if (!(human instanceof EntityPlayer)) continue;
            EntityPlayer entityplayer = (EntityPlayer) human;
            // Paper end

            if (entityplayer != null && entityplayer.world == this.world && entityplayer.getId() != i) {
                double d0 = (double) blockposition.getX() - entityplayer.locX;
                double d1 = (double) blockposition.getY() - entityplayer.locY;
                double d2 = (double) blockposition.getZ() - entityplayer.locZ;

                // CraftBukkit start
                if (entityhuman != null && entityhuman instanceof EntityPlayer && !entityplayer.getBukkitEntity().canSee(((EntityPlayer) entityhuman).getBukkitEntity())) {
                    continue;
                }
                // CraftBukkit end

                if (d0 * d0 + d1 * d1 + d2 * d2 < 1024.0D) {
                // NeonPaper start
                if (packet == null) packet = new PacketPlayOutBlockBreakAnimation(i, blockposition, j);
                    entityplayer.playerConnection.sendPacket(packet);
                // NeonPaper end
                }
            }
        }

    }
}
