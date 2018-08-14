package io.akarin.server.mixin.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import org.apache.commons.lang.WordUtils;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import co.aikar.timings.MinecraftTimings;
import io.akarin.api.internal.Akari;
import io.akarin.api.internal.Akari.AssignableFactory;
import io.akarin.api.internal.mixin.IMixinWorldServer;
import io.akarin.server.core.AkarinGlobalConfig;
import io.akarin.server.core.AkarinSlackScheduler;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.CrashReport;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.ITickable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MojangStatisticsGenerator;
import net.minecraft.server.PlayerList;
import net.minecraft.server.ReportedException;
import net.minecraft.server.ServerConnection;
import net.minecraft.server.SystemUtils;
import net.minecraft.server.TileEntityHopper;
import net.minecraft.server.WorldServer;

@Mixin(value = MinecraftServer.class, remap = false)
public abstract class MixinMinecraftServer {
    @Shadow @Final public Thread primaryThread;
    private boolean tickedPrimaryEntities;
	private int cachedWorldSize;
    
    @Overwrite
    public String getServerModName() {
        return "Akarin";
    }
    
    @Inject(method = "run()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/MinecraftServer.aw()J",
            shift = At.Shift.BEFORE
    ))
    private void prerun(CallbackInfo info) {
        primaryThread.setPriority(AkarinGlobalConfig.primaryThreadPriority < Thread.NORM_PRIORITY ? Thread.NORM_PRIORITY :
            (AkarinGlobalConfig.primaryThreadPriority > Thread.MAX_PRIORITY ? 10 : AkarinGlobalConfig.primaryThreadPriority));
        Akari.resizeTickExecutors((cachedWorldSize = worlds.size()));
        
        for (int i = 0; i < worlds.size(); ++i) {
            WorldServer world = worlds.get(i);
            TileEntityHopper.skipHopperEvents = world.paperConfig.disableHopperMoveEvents || InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length == 0;
        }
        AkarinSlackScheduler.get().boot();
    }
    
    @Overwrite
    public boolean isMainThread() {
        return Akari.isPrimaryThread();
    }
    
    /*
     * Forcely disable snooper
     */
    @Overwrite
    public void a(MojangStatisticsGenerator generator) {}
    
    @Overwrite
    public void b(MojangStatisticsGenerator generator) {}
    
    /*
     * Parallel spawn chunks generation
     */
    @Shadow public abstract boolean isRunning();
    @Shadow(aliases = "a_") protected abstract void output(String s, int i);
    @Shadow(aliases = "t") protected abstract void enablePluginsPostWorld();
    
    private void prepareChunks(WorldServer world, int index) {
        MinecraftServer.LOGGER.info("Preparing start region for level " + index + " (Seed: " + world.getSeed() + ")");
        BlockPosition spawnPos = world.getSpawn();
        long lastRecord = System.currentTimeMillis();
        
        int preparedChunks = 0;
        short radius = world.paperConfig.keepLoadedRange;
        for (int skipX = -radius; skipX <= radius && isRunning(); skipX += 16) {
            for (int skipZ = -radius; skipZ <= radius && isRunning(); skipZ += 16) {
                long now = System.currentTimeMillis();
                
                if (now - lastRecord > 1000L) {
                    output("Preparing spawn area (level " + index + ") ", preparedChunks * 100 / 625);
                    lastRecord = now;
                }
                
                preparedChunks++;
                world.getChunkProviderServer().getChunkAt(spawnPos.getX() + skipX >> 4, spawnPos.getZ() + skipZ >> 4);
            }
        }
    }
    
    @Overwrite
    protected void l() throws InterruptedException {
        ExecutorCompletionService<?> executor = new ExecutorCompletionService<>(Executors.newFixedThreadPool(worlds.size(), new AssignableFactory("Akarin Parallel Terrain Generation Thread - $")));
        
        for (int index = 0; index < worlds.size(); index++) {
            WorldServer world = this.worlds.get(index);
            if (!world.getWorld().getKeepSpawnInMemory()) continue;
            
            int fIndex = index;
            executor.submit(() -> prepareChunks(world, fIndex), null);
        }
        
        for (WorldServer world : this.worlds) {
            if (world.getWorld().getKeepSpawnInMemory()) executor.take();
        }
        if (WorldLoadEvent.getHandlerList().getRegisteredListeners().length != 0) {
            for (WorldServer world : this.worlds) {
                this.server.getPluginManager().callEvent(new WorldLoadEvent(world.getWorld()));
            }
        }
        
        enablePluginsPostWorld();
    }
    
    /*
     * Parallel world ticking
     */
    @Shadow public CraftServer server;
    @Shadow @Mutable protected Queue<FutureTask<?>> j;
    @Shadow public Queue<Runnable> processQueue;
    @Shadow private int ticks;
    @Shadow public List<WorldServer> worlds;
    @Shadow(aliases = "v") private PlayerList playerList;
    @Shadow(aliases = "o") @Final private List<ITickable> tickables;
    
    @Shadow public abstract PlayerList getPlayerList();
    @Shadow(aliases = "an") public abstract ServerConnection serverConnection();
    @Shadow(aliases = "aL") public abstract CustomFunctionData functionManager();
    
    private boolean tickEntities(WorldServer world) {
        try {
            world.timings.tickEntities.startTiming();
            world.tickEntities();
            world.timings.tickEntities.stopTiming();
            world.getTracker().updatePlayers();
            world.explosionDensityCache.clear(); // Paper - Optimize explosions
        } catch (Throwable throwable) {
            CrashReport crashreport;
            try {
                crashreport = CrashReport.a(throwable, "Exception ticking world entities");
            } catch (Throwable t){
                throw new RuntimeException("Error generating crash report", t);
            }
            world.a(crashreport);
            throw new ReportedException(crashreport);
        }
        return true;
    }
    
    private void tickWorld(WorldServer world) {
        try {
            world.timings.doTick.startTiming();
            world.doTick();
            world.timings.doTick.stopTiming();
        } catch (Throwable throwable) {
            CrashReport crashreport;
            try {
                crashreport = CrashReport.a(throwable, "Exception ticking world");
            } catch (Throwable t){
                throw new RuntimeException("Error generating crash report", t);
            }
            world.a(crashreport);
            throw new ReportedException(crashreport);
        }
    }
    
    @Overwrite
    public void D() throws InterruptedException, ExecutionException, CancellationException {
        Runnable runnable;
        Akari.callbackTiming.startTiming();
        while ((runnable = Akari.callbackQueue.poll()) != null) runnable.run();
        Akari.callbackTiming.stopTiming();
        
        MinecraftTimings.bukkitSchedulerTimer.startTiming();
        this.server.getScheduler().mainThreadHeartbeat(this.ticks);
        MinecraftTimings.bukkitSchedulerTimer.stopTiming();
        
        MinecraftTimings.minecraftSchedulerTimer.startTiming();
        FutureTask<?> task;
        int count = j.size();
        while (count-- > 0 && (task = j.poll()) != null) {
            SystemUtils.a(task, MinecraftServer.LOGGER);
        }
        MinecraftTimings.minecraftSchedulerTimer.stopTiming();
        
        MinecraftTimings.processQueueTimer.startTiming();
        while ((runnable = processQueue.poll()) != null) runnable.run();
        MinecraftTimings.processQueueTimer.stopTiming();
        
        MinecraftTimings.chunkIOTickTimer.startTiming();
        ChunkIOExecutor.tick();
        MinecraftTimings.chunkIOTickTimer.stopTiming();
        
        if (cachedWorldSize != worlds.size()) Akari.resizeTickExecutors((cachedWorldSize = worlds.size()));
        switch (AkarinGlobalConfig.parallelMode) {
            case 1:
            case 2:
            default:
                // Never tick one world concurrently!
                for (int i = 0; i < cachedWorldSize; i++) {
                    // Impl Note:
                    // Entities ticking: index 1 -> ... -> 0 (parallel)
                    //   World  ticking: index 0 -> ... (parallel)
                    int interlace = i + 1;
                    WorldServer entityWorld = worlds.get(interlace < cachedWorldSize ? interlace : 0);
                    Akari.STAGE_TICK.submit(() -> {
                        synchronized (((IMixinWorldServer) entityWorld).lock()) {
                            tickEntities(entityWorld);
                        }
                    }, null/*new TimingSignal(entityWorld, true)*/);
                    
                    if (AkarinGlobalConfig.parallelMode != 1) {
                        int fi = i;
                        Akari.STAGE_TICK.submit(() -> {
                            WorldServer world = worlds.get(fi);
                            synchronized (((IMixinWorldServer) world).lock()) {
                                tickWorld(world);
                            }
                        }, null);
                    }
                }
                
                if (AkarinGlobalConfig.parallelMode == 1)
                Akari.STAGE_TICK.submit(() -> {
                    for (int i = 0; i < cachedWorldSize; i++) {
                        WorldServer world = worlds.get(i);
                        synchronized (((IMixinWorldServer) world).lock()) {
                            tickWorld(world);
                        }
                    }
                }, null);
                
                for (int i = (AkarinGlobalConfig.parallelMode == 1 ? cachedWorldSize + 1 : cachedWorldSize * 2); i --> 0 ;) {
                    Akari.STAGE_TICK.take();
                }
                
                /* for (int i = (AkarinGlobalConfig.parallelMode == 1 ? cachedWorldSize : cachedWorldSize * 2); i --> 0 ;) {
                    long startTiming = System.nanoTime();
                    TimingSignal signal = Akari.STAGE_TICK.take().get();
                    IMixinTimingHandler timing = (IMixinTimingHandler) (signal.isEntities ? signal.tickedWorld.timings.tickEntities : signal.tickedWorld.timings.doTick);
                    timing.stopTiming(startTiming); // The overlap will be ignored
                } */
                
                break;
            case 0:
                Akari.STAGE_TICK.submit(() -> {
                    for (int i = 1; i <= cachedWorldSize; ++i) {
                        WorldServer world = worlds.get(i < cachedWorldSize ? i : 0);
                        synchronized (((IMixinWorldServer) world).lock()) {
                            tickEntities(world);
                        }
                    }
                }, null);
                
                Akari.STAGE_TICK.submit(() -> {
                    for (int i = 0; i < cachedWorldSize; ++i) {
                        WorldServer world = worlds.get(i);
                        synchronized (((IMixinWorldServer) world).lock()) {
                            tickWorld(world);
                        }
                    }
                }, null);
                
                Akari.STAGE_TICK.take();
                Akari.STAGE_TICK.take();
                break;
            case -1:
                for (int i = 0; i < cachedWorldSize; ++i) {
                    WorldServer world = worlds.get(i);
                    tickWorld(world);
                    tickEntities(world);
                }
                break;
        }
        
        Akari.callbackTiming.startTiming();
        while ((runnable = Akari.callbackQueue.poll()) != null) runnable.run();
        Akari.callbackTiming.stopTiming();
        
        MinecraftTimings.connectionTimer.startTiming();
        serverConnection().c();
        MinecraftTimings.connectionTimer.stopTiming();
        
        Akari.callbackTiming.startTiming();
        while ((runnable = Akari.callbackQueue.poll()) != null) runnable.run();
        Akari.callbackTiming.stopTiming();
        
        MinecraftTimings.commandFunctionsTimer.startTiming();
        functionManager().e();
        MinecraftTimings.commandFunctionsTimer.stopTiming();
        
        MinecraftTimings.tickablesTimer.startTiming();
        for (int i = 0; i < this.tickables.size(); ++i) {
            tickables.get(i).e();
        }
        MinecraftTimings.tickablesTimer.stopTiming();
    }
}
