package io.akarin.server.mixin.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
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
import io.akarin.api.internal.Akari.TimingSignal;
import io.akarin.api.internal.mixin.IMixinLockProvider;
import io.akarin.api.internal.mixin.IMixinTimingHandler;
import io.akarin.server.core.AkarinGlobalConfig;
import io.akarin.server.core.AkarinSlackScheduler;
import net.minecraft.server.CrashReport;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.ITickable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MojangStatisticsGenerator;
import net.minecraft.server.ReportedException;
import net.minecraft.server.ServerConnection;
import net.minecraft.server.SystemUtils;
import net.minecraft.server.WorldServer;

@Mixin(value = MinecraftServer.class, remap = false)
public abstract class MixinMinecraftServer {
    @Shadow @Final public Thread primaryThread;
    private int cachedWorldSize;
    
    @Overwrite
    public String getServerModName() {
        return "Akarin";
    }
    
    @Inject(method = "run()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/SystemUtils.b()J",
            shift = At.Shift.BEFORE
    ))
    private void prerun(CallbackInfo info) {
        primaryThread.setPriority(AkarinGlobalConfig.primaryThreadPriority < Thread.NORM_PRIORITY ? Thread.NORM_PRIORITY :
            (AkarinGlobalConfig.primaryThreadPriority > Thread.MAX_PRIORITY ? 10 : AkarinGlobalConfig.primaryThreadPriority));
        Akari.resizeTickExecutors((cachedWorldSize = worlds.size()));
        
        /*
        for (int i = 0; i < worlds.size(); ++i) {
            WorldServer world = worlds.get(i);
            TileEntityHopper.skipHopperEvents = world.paperConfig.disableHopperMoveEvents || InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length == 0;
        }
        */
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
    
    /*
     * Parallel world ticking
     */
    @Shadow public CraftServer server;
    @Shadow @Mutable protected Queue<FutureTask<?>> g;
    @Shadow public Queue<Runnable> processQueue;
    @Shadow private int ticks;
    @Shadow public List<WorldServer> worlds;
    @Shadow(aliases = "l") @Final private List<ITickable> tickables;
    
    @Shadow public abstract CustomFunctionData getFunctionData();
    @Shadow public abstract ServerConnection getServerConnection();
    
    private boolean tickEntities(WorldServer world) {
        try {
            world.tickEntities();
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
            world.doTick();
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
    public void w() throws InterruptedException, ExecutionException {
        Runnable runnable;
        MinecraftTimings.bukkitSchedulerTimer.startTiming();
        this.server.getScheduler().mainThreadHeartbeat(this.ticks);
        MinecraftTimings.bukkitSchedulerTimer.stopTiming();
        
        MinecraftTimings.minecraftSchedulerTimer.startTiming();
        FutureTask<?> task;
        int count = g.size();
        while (count-- > 0 && (task = g.poll()) != null) {
            SystemUtils.a(task, MinecraftServer.LOGGER);
        }
        MinecraftTimings.minecraftSchedulerTimer.stopTiming();
        
        MinecraftTimings.commandFunctionsTimer.startTiming();
        getFunctionData().Y_();
        MinecraftTimings.commandFunctionsTimer.stopTiming();
        
        MinecraftTimings.processQueueTimer.startTiming();
        while ((runnable = processQueue.poll()) != null) runnable.run();
        MinecraftTimings.processQueueTimer.stopTiming();
        
        MinecraftTimings.chunkIOTickTimer.startTiming();
        ChunkIOExecutor.tick();
        MinecraftTimings.chunkIOTickTimer.stopTiming();
        
        Akari.worldTiming.startTiming();
        if (cachedWorldSize != worlds.size()) Akari.resizeTickExecutors((cachedWorldSize = worlds.size()));
        
        // Never tick one world concurrently!
        for (int i = 0; i < cachedWorldSize; i++) {
            // Impl Note:
            // Entities ticking: index 1 -> ... -> 0 (parallel)
            //   World  ticking: index 0 -> ... (parallel)
            int interlace = i + 1;
            WorldServer entityWorld = worlds.get(interlace < cachedWorldSize ? interlace : 0);
            Akari.STAGE_TICK.submit(() -> {
                synchronized (((IMixinLockProvider) entityWorld).lock()) {
                    tickEntities(entityWorld);
                    entityWorld.getTracker().updatePlayers();
                    entityWorld.explosionDensityCache.clear(); // Paper - Optimize explosions
                }
            }, new TimingSignal(entityWorld, true));
            
            WorldServer world = worlds.get(i);
            world.timings.doTick.startTiming();
            synchronized (((IMixinLockProvider) world).lock()) {
                tickWorld(world);
            }
            world.timings.doTick.stopTiming();
        }
        
        for (int i = cachedWorldSize; i --> 0 ;) {
            long startTiming = System.nanoTime();
            TimingSignal signal = Akari.STAGE_TICK.take().get();
            IMixinTimingHandler timing = (IMixinTimingHandler) (signal.isEntities ? signal.tickedWorld.timings.tickEntities : signal.tickedWorld.timings.doTick);
            timing.stopTiming(startTiming);
        }
        
        Akari.callbackTiming.startTiming();
        while ((runnable = Akari.callbackQueue.poll()) != null) runnable.run();
        Akari.callbackTiming.stopTiming();
        
        MinecraftTimings.connectionTimer.startTiming();
        getServerConnection().c();
        MinecraftTimings.connectionTimer.stopTiming();
        
        Akari.callbackTiming.startTiming();
        while ((runnable = Akari.callbackQueue.poll()) != null) runnable.run();
        Akari.callbackTiming.stopTiming();
        
        MinecraftTimings.tickablesTimer.startTiming();
        for (int i = 0; i < this.tickables.size(); ++i) {
            tickables.get(i).Y_();
        }
        MinecraftTimings.tickablesTimer.stopTiming();
    }
}
