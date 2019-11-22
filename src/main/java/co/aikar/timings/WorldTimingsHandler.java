package co.aikar.timings;

import net.minecraft.server.World;
import net.minecraft.server.WorldServer;

/**
 * Set of timers per world, to track world specific timings.
 */
// TODO: Re-implement missing timers
public class WorldTimingsHandler {
    public final Timing mobSpawn;
    public final Timing doChunkUnload;
    public final Timing doPortalForcer;
    public final Timing scheduledBlocks;
    public final Timing scheduledBlocksCleanup;
    public final Timing scheduledBlocksTicking;
    public final Timing chunkTicks;
    public final Timing lightChunk;
    public final Timing chunkTicksBlocks;
    public final Timing doVillages;
    public final Timing doChunkMap;
    public final Timing doChunkMapUpdate;
    public final Timing doChunkMapToUpdate;
    public final Timing doChunkMapSortMissing;
    public final Timing doChunkMapSortSendToPlayers;
    public final Timing doChunkMapPlayersNeedingChunks;
    public final Timing doChunkMapPendingSendToPlayers;
    public final Timing doChunkMapUnloadChunks;
    public final Timing doChunkGC;
    public final Timing doSounds;
    public final Timing entityRemoval;
    public final Timing entityTick;
    public final Timing tileEntityTick;
    public final Timing tileEntityPending;
    public final Timing tracker1;
    public final Timing tracker2;
    public final Timing doTick;
    public final Timing tickEntities;
    public final Timing chunks;
    public final Timing newEntities;
    public final Timing raids;
    public final Timing chunkProviderTick;
    public final Timing broadcastChunkUpdates;
    public final Timing countNaturalMobs;

    public final Timing syncChunkLoadTimer;
    public final Timing syncChunkLoadDataTimer;
    public final Timing syncChunkLoadStructuresTimer;
    public final Timing syncChunkLoadPostTimer;
    public final Timing syncChunkLoadPopulateTimer;
    public final Timing chunkAwait;
    public final Timing chunkLoadLevelTimer;
    public final Timing chunkGeneration;
    public final Timing chunkIOStage1;
    public final Timing chunkIOStage2;
    public final Timing worldSave;
    public final Timing worldSaveChunks;
    public final Timing worldSaveLevel;
    public final Timing chunkSaveData;


    public final Timing miscMobSpawning;
    public final Timing chunkRangeCheckBig;
    public final Timing chunkRangeCheckSmall;

    public final Timing poiUnload;
    public final Timing chunkUnload;
    public final Timing poiSaveDataSerialization;
    public final Timing chunkSave;
    public final Timing chunkSaveOverwriteCheck;
    public final Timing chunkSaveDataSerialization;
    public final Timing chunkSaveIOWait;
    public final Timing chunkUnloadPrepareSave;
    public final Timing chunkUnloadPOISerialization;
    public final Timing chunkUnloadDataSave;

    public final Timing playerMobDistanceMapUpdate;

    public WorldTimingsHandler(World server) {
        String name = server.worldData.getName() +" - ";

        mobSpawn = Timings.ofSafe(name + "mobSpawn");
        doChunkUnload = Timings.ofSafe(name + "doChunkUnload");
        scheduledBlocks = Timings.ofSafe(name + "Scheduled Blocks");
        scheduledBlocksCleanup = Timings.ofSafe(name + "Scheduled Blocks - Cleanup");
        scheduledBlocksTicking = Timings.ofSafe(name + "Scheduled Blocks - Ticking");
        chunkTicks = Timings.ofSafe(name + "Chunk Ticks");
        lightChunk = Timings.ofSafe(name + "Light Chunk");
        chunkTicksBlocks = Timings.ofSafe(name + "Chunk Ticks - Blocks");
        doVillages = Timings.ofSafe(name + "doVillages");
        doChunkMap = Timings.ofSafe(name + "doChunkMap");
        doChunkMapUpdate = Timings.ofSafe(name + "doChunkMap - Update");
        doChunkMapToUpdate = Timings.ofSafe(name + "doChunkMap - To Update");
        doChunkMapSortMissing = Timings.ofSafe(name + "doChunkMap - Sort Missing");
        doChunkMapSortSendToPlayers = Timings.ofSafe(name + "doChunkMap - Sort Send To Players");
        doChunkMapPlayersNeedingChunks = Timings.ofSafe(name + "doChunkMap - Players Needing Chunks");
        doChunkMapPendingSendToPlayers = Timings.ofSafe(name + "doChunkMap - Pending Send To Players");
        doChunkMapUnloadChunks = Timings.ofSafe(name + "doChunkMap - Unload Chunks");
        doSounds = Timings.ofSafe(name + "doSounds");
        doChunkGC = Timings.ofSafe(name + "doChunkGC");
        doPortalForcer = Timings.ofSafe(name + "doPortalForcer");
        entityTick = Timings.ofSafe(name + "entityTick");
        entityRemoval = Timings.ofSafe(name + "entityRemoval");
        tileEntityTick = Timings.ofSafe(name + "tileEntityTick");
        tileEntityPending = Timings.ofSafe(name + "tileEntityPending");

        syncChunkLoadTimer = Timings.ofSafe(name + "syncChunkLoad");
        syncChunkLoadDataTimer = Timings.ofSafe(name + "syncChunkLoad - Data");
        syncChunkLoadStructuresTimer = Timings.ofSafe(name + "chunkLoad - recreateStructures");
        syncChunkLoadPostTimer = Timings.ofSafe(name + "chunkLoad - Post");
        syncChunkLoadPopulateTimer = Timings.ofSafe(name + "chunkLoad - Populate");
        chunkAwait = Timings.ofSafe(name + "chunkAwait");
        chunkLoadLevelTimer = Timings.ofSafe(name + "chunkLoad - Load Level");
        chunkGeneration = Timings.ofSafe(name + "chunkGeneration");
        chunkIOStage1 = Timings.ofSafe(name + "ChunkIO Stage 1 - DiskIO");
        chunkIOStage2 = Timings.ofSafe(name + "ChunkIO Stage 2 - Post Load");
        worldSave = Timings.ofSafe(name + "World Save");
        worldSaveLevel = Timings.ofSafe(name + "World Save - Level");
        worldSaveChunks = Timings.ofSafe(name + "World Save - Chunks");
        chunkSaveData = Timings.ofSafe(name + "Chunk Save - Data");

        tracker1 = Timings.ofSafe(name + "tracker stage 1");
        tracker2 = Timings.ofSafe(name + "tracker stage 2");
        doTick = Timings.ofSafe(name + "doTick");
        tickEntities = Timings.ofSafe(name + "tickEntities");

        chunks = Timings.ofSafe(name + "Chunks");
        newEntities = Timings.ofSafe(name + "New entity registration");
        raids = Timings.ofSafe(name + "Raids");
        chunkProviderTick = Timings.ofSafe(name + "Chunk provider tick");
        broadcastChunkUpdates = Timings.ofSafe(name + "Broadcast chunk updates");
        countNaturalMobs = Timings.ofSafe(name + "Count natural mobs");


        miscMobSpawning = Timings.ofSafe(name + "Mob spawning - Misc");
        chunkRangeCheckBig = Timings.ofSafe(name + "Chunk Tick Range - Big");
        chunkRangeCheckSmall = Timings.ofSafe(name + "Chunk Tick Range - Small");

        poiUnload = Timings.ofSafe(name + "Chunk unload - POI");
        chunkUnload = Timings.ofSafe(name + "Chunk unload - Chunk");
        poiSaveDataSerialization = Timings.ofSafe(name + "Chunk save - POI Data serialization");
        chunkSave = Timings.ofSafe(name + "Chunk save - Chunk");
        chunkSaveOverwriteCheck = Timings.ofSafe(name + "Chunk save - Chunk Overwrite Check");
        chunkSaveDataSerialization = Timings.ofSafe(name + "Chunk save - Chunk Data serialization");
        chunkSaveIOWait = Timings.ofSafe(name + "Chunk save - Chunk IO Wait");
        chunkUnloadPrepareSave = Timings.ofSafe(name + "Chunk unload - Async Save Prepare");
        chunkUnloadPOISerialization = Timings.ofSafe(name + "Chunk unload - POI Data Serialization");
        chunkUnloadDataSave = Timings.ofSafe(name + "Chunk unload - Data Serialization");

        playerMobDistanceMapUpdate = Timings.ofSafe(name + "Per Player Mob Spawning - Distance Map Update");
    }

    public static Timing getTickList(WorldServer worldserver, String timingsType) {
        return Timings.ofSafe(worldserver.getWorldData().getName() + " - Scheduled " + timingsType);
    }
}
