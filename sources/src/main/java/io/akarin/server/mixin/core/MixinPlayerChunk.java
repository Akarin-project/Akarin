package io.akarin.server.mixin.core;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.PacketPlayOutMapChunk;
import net.minecraft.server.PlayerChunk;
import net.minecraft.server.PlayerChunkMap;

@Mixin(value = PlayerChunk.class, remap = false)
public abstract class MixinPlayerChunk {
	@Shadow @Final private static Logger a;
	@Shadow @Final private ChunkCoordIntPair location;
	@Shadow @Final private PlayerChunkMap playerChunkMap;
	@Shadow @Final public List<EntityPlayer> c;
	@Shadow public Chunk chunk;
	@Shadow private int dirtyCount;
	@Shadow private int h;
	@Shadow private long i;
	@Shadow private boolean done;
	
	@Shadow public abstract void sendChunk(EntityPlayer entityplayer);
	
	@Overwrite
	public void a(final EntityPlayer entityplayer) { // CraftBukkit - added final to argument
        if (this.c.contains(entityplayer)) {
        	a.debug("Failed to add player. {} already is in chunk {}, {}", entityplayer, Integer.valueOf(this.location.x), Integer.valueOf(this.location.z));
        	return;
        }
        if (AkarinGlobalConfig.noChunksPastWorldBorder && !playerChunkMap.getWorld().getWorldBorder().isChunkInBounds(location.x, location.z)) {
    		return;
    	}
    	if (this.c.isEmpty()) {
            this.i = this.playerChunkMap.getWorld().getTime();
        }

        this.c.add(entityplayer);
        
        if (this.done) {
            this.sendChunk(entityplayer);
        }
    }
	
	@Overwrite
    public boolean b() {
        if (this.done) {
            return true;
        }
        if (this.chunk == null) {
            return false;
        }
        if (!this.chunk.isReady()) {
            return false;
        }
        if (!this.chunk.world.chunkPacketBlockController.onChunkPacketCreate(this.chunk, '\uffff', false)) { // Paper - Anti-Xray - Load nearby chunks if necessary
            return false;
        }
        this.dirtyCount = 0;
        this.h = 0;
        this.done = true;
        if (c.isEmpty()) return true; // Akarin - Fixes MC-120780
        PacketPlayOutMapChunk packetplayoutmapchunk = new PacketPlayOutMapChunk(this.chunk, '\uffff');
        Iterator<EntityPlayer> iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = iterator.next();

            entityplayer.playerConnection.sendPacket(packetplayoutmapchunk);
            this.playerChunkMap.getWorld().getTracker().a(entityplayer, this.chunk);
        }

        return true;
    }
}
