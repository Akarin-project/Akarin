package io.akarin.server.core;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import com.destroystokyo.paper.event.entity.PlayerNaturallySpawnCreaturesEvent;
import com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent;
import com.destroystokyo.paper.exception.ServerInternalException;
import com.google.common.collect.Maps;
import com.koloboke.collect.set.hash.HashObjSets;

import io.akarin.server.misc.ChunkCoordOrdinalInt3Tuple;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.ChunkCoordIntPair;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.EntityPositionTypes;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.EnumCreatureType;
import net.minecraft.server.GroupDataEntity;
import net.minecraft.server.MCUtil;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SpawnerCreature;
import net.minecraft.server.WorldServer;

/*
 * Reference on spawning mechanics by Colin Godsey <crgodsey@gmail.com>
 * https://github.com/yesdog/Paper/blob/0de3dd84b7e6688feb42af4fe6b4f323ce7e3013/Spigot-Server-Patches/0433-alternate-mob-spawning-mechanic.patch
 */
public class AkarinCreatureSpanwner {
    private static final Map<ChunkCoordIntPair, int[]> rangeChunks = Maps.newConcurrentMap();
    
    public static void increment(ChunkCoordIntPair chunk, EnumCreatureType type) {
        int[] values = rangeChunks.get(chunk);
        if (values == null) {
            values = new int[EnumCreatureType.values().length];;
            values[type.ordinal()]++;
            rangeChunks.put(chunk, values);
        } else {
            values[type.ordinal()]++;
        }
    }
    
    public static void decrement(ChunkCoordIntPair chunk, EnumCreatureType type) {
        int[] values = rangeChunks.get(chunk);
        if (values == null) {
            values = new int[EnumCreatureType.values().length];;
            int count = values[type.ordinal()];
            values[type.ordinal()] = count > 1 ? --count : 0;
            rangeChunks.put(chunk, values);
        } else {
            int count = values[type.ordinal()];
            values[type.ordinal()] = count > 1 ? --count : 0;
        }
    }
    
    private static int getSpawnRange(WorldServer world, EntityHuman player) {
        byte mobSpawnRange = world.spigotConfig.mobSpawnRange;
        
        mobSpawnRange = (mobSpawnRange > world.spigotConfig.viewDistance) ? (byte) world.spigotConfig.viewDistance : mobSpawnRange;
        mobSpawnRange = (mobSpawnRange > 8) ? 8 : mobSpawnRange;
        
        if (PlayerNaturallySpawnCreaturesEvent.getHandlerList().getRegisteredListeners().length != 0) {
            PlayerNaturallySpawnCreaturesEvent event = new PlayerNaturallySpawnCreaturesEvent((Player) player.getBukkitEntity(), mobSpawnRange);
            synchronized (PlayerNaturallySpawnCreaturesEvent.class) {
                Bukkit.getPluginManager().callEvent(event);
            }
            
            return event.isCancelled() ? 0 : event.getSpawnRadius();
        }
        
        return mobSpawnRange;
    }
    
    private static int getCreatureLimit(WorldServer world, EnumCreatureType type) {
        switch (type) {
            case MONSTER:
                return world.getWorld().getMonsterSpawnLimit();
            case CREATURE:
                return world.getWorld().getAnimalSpawnLimit();
            case WATER_CREATURE:
                return world.getWorld().getWaterAnimalSpawnLimit();
            case AMBIENT:
                return world.getWorld().getAmbientSpawnLimit();
        }
        return type.spawnLimit();
    }
    
    @Nullable
    private static EntityInsentient createMob(WorldServer world, EnumCreatureType type, BlockPosition pos, BiomeBase.BiomeMeta biomeMeta) {
        if (!world.isBiomeMetaValidAt(type, biomeMeta, pos)) return null;
        
        EntityTypes<? extends EntityInsentient> entityType = biomeMeta.entityType();
        org.bukkit.entity.EntityType bType = EntityTypes.clsToTypeMap.get(entityType.entityClass());
        if (bType != null) {
            PreCreatureSpawnEvent event = new PreCreatureSpawnEvent(
                    MCUtil.toLocation(world, pos),
                    bType, SpawnReason.NATURAL
                    );
            
            if (!event.callEvent() || event.shouldAbortSpawn())
                return null;
        }
        
        EntityInsentient entity = null;
        
        try {
            entity = entityType.create(world);
        } catch (Exception exception) {
            MinecraftServer.LOGGER.warn("Failed to create mob", exception);
            ServerInternalException.reportInternalException(exception);
        }
        
        return entity;
    }
    
    private static void spawnMob0(WorldServer world, Set<ChunkCoordIntPair> chunks, EnumCreatureType type, int amount) {
        if (chunks.isEmpty()) return;
        
        Random rand = ThreadLocalRandom.current();
        final int maxPackIterations = 10; // X attempts per pack, 1 pack per chunk
        Iterator<ChunkCoordIntPair> iterator = chunks.iterator();
        BlockPosition worldSpawn = world.getSpawn();
        
        int spawned = 0;
        
        while (spawned < amount && iterator.hasNext()) {
            ChunkCoordIntPair chunkCoord = iterator.next();
            int packSize = rand.nextInt(4) + 1;
            BlockPosition packCenter = SpawnerCreature.getRandomPosition(world, chunkCoord.x, chunkCoord.z);
            
            if (world.getType(packCenter).isOccluding()) continue;
            
            int x = packCenter.getX();
            int y = packCenter.getY();
            int z = packCenter.getZ();
            BlockPosition.MutableBlockPosition blockPointer = new BlockPosition.MutableBlockPosition();
            BiomeBase.BiomeMeta biomeMeta = null;
            GroupDataEntity group = null;
            EntityPositionTypes.Surface surfaceType = null;
            int iter = 0;
            int packSpawned = 0;
            
            while (packSpawned < packSize && iter < maxPackIterations) {
                iter++;
                
                // random walk
                x += rand.nextInt(12) - 6;
                y += rand.nextInt(2) - 1;
                z += rand.nextInt(12) - 6;
                blockPointer.setValues(x, y, z);
                
                if (worldSpawn.distanceSquared(x + 0.5, y, z + 0.5) < (24 * 24)) continue;
                
                if (biomeMeta == null) {
                    biomeMeta = world.getBiomeMetaAt(type, blockPointer);
                    
                    if (biomeMeta == null) break;
                    
                    int packRange = 1 + biomeMeta.getMaxPackSize() - biomeMeta.getMinPackSize();
                    packSize = biomeMeta.getMinPackSize() + rand.nextInt(packRange);
                    surfaceType = EntityPositionTypes.a(biomeMeta.entityType());
                }
                
                EntityInsentient entity = createMob(world, type, blockPointer, biomeMeta);
                
                if (entity == null) continue;
                
                entity.setPositionRotation(x + 0.5, y, z + 0.5, rand.nextFloat() * 360.0F, 0.0F);
                
                if (entity.canSpawnHere() && surfaceType != null
                        && SpawnerCreature.isValidSpawnSurface(surfaceType, world, blockPointer, biomeMeta.entityType())
                        && entity.isNotColliding(world) && !world.isPlayerNearby(x + 0.5, y, z + 0.5, 24)) {
                    group = entity.prepare(world.getDamageScaler(new BlockPosition(entity)), group, null);
                    
                    if (entity.isNotColliding(world) && world.addEntity(entity, SpawnReason.NATURAL))
                        packSpawned++;
                    
                    if (packSpawned >= entity.maxPackSize()) break;
                    if ((packSpawned + spawned) >= amount) break;
                } else {
                    entity.die();
                }
            }
            
            spawned += packSpawned;
        }
    }
    
    public static void spawnMobs(WorldServer world, boolean spawnMonsters, boolean spawnPassives, boolean spawnRare) {
        if(!spawnMonsters && !spawnPassives) return;
        
        AkarinAsyncExecutor.scheduleAsyncTask(() -> {
        Random rand = ThreadLocalRandom.current();
        int hashOrdinal = rand.nextInt();
        
        //Set<Chunk> rangeChunks = HashObjSets.newUpdatableSet();
        Map<EnumCreatureType, Set<ChunkCoordIntPair>> creatureChunks = new EnumMap<>(EnumCreatureType.class);
        int[] typeNumSpawn = new int[EnumCreatureType.values().length];
        
        for (EnumCreatureType type : EnumCreatureType.values()) {
            if (type.passive() && !spawnPassives) continue;
            if (!type.passive() && !spawnMonsters) continue;
            if (type.rare() && !spawnRare) continue;
            if (getCreatureLimit(world, type) <= 0) continue;
            
            creatureChunks.put(type, HashObjSets.newUpdatableSet());
        }
        
        if (creatureChunks.isEmpty()) return;
        
        for (EntityHuman player : world.players) {
            if (!player.affectsSpawning || player.isSpectator()) continue;
            
            int spawnRange = getSpawnRange(world, player);
            if (spawnRange <= 0) continue;
            
            int playerChunkX = MathHelper.floor(player.locX / 16.0);
            int playerChunkZ = MathHelper.floor(player.locZ / 16.0);
            
            rangeChunks.clear();
            
            for (int dX = -spawnRange; dX <= spawnRange; ++dX) {
                for (int dZ = -spawnRange; dZ <= spawnRange; ++dZ) {
                    ChunkCoordIntPair chunkCoord = new ChunkCoordOrdinalInt3Tuple(dX + playerChunkX, dZ + playerChunkZ, hashOrdinal);
                    
                    if (!world.getWorldBorder().isInBounds(chunkCoord)) continue;
                    
                    ChunkCoordIntPair pair = new ChunkCoordIntPair(chunkCoord.x, chunkCoord.z);
                    int[] cached = rangeChunks.get(pair);
                    if (cached == null)
                        rangeChunks.put(pair, new int[EnumCreatureType.values().length]);
                    else
                        for (int i = 0; i < cached.length; i++)
                            cached[i] = 0;
                }
            }
            
            for (EnumCreatureType type : creatureChunks.keySet()) {
                int limit = getCreatureLimit(world, type);
                int creatureTotal = 0;
                
                for (int[] chunk : rangeChunks.values())
                    creatureTotal += chunk[type.ordinal()];
                
                // if our local count is above the limit, dont qualify our chunks
                if (creatureTotal >= limit) continue;
                
                // expect number is rather meaningless, just a ceil
                int expect = limit - creatureTotal;
                typeNumSpawn[type.ordinal()] = Math.max(typeNumSpawn[type.ordinal()], expect);
            }
        }
        
        for (EnumCreatureType type : creatureChunks.keySet()) {
            Set<ChunkCoordIntPair> chunks = creatureChunks.get(type);
            
            if (!chunks.isEmpty())
                MinecraftServer.getServer().ensuresMainThread(() -> spawnMob0(world, chunks, type, typeNumSpawn[type.ordinal()]));
        }
        });
    }
}