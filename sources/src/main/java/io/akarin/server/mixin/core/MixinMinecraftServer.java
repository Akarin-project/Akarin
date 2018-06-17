package io.akarin.server.mixin.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import co.aikar.timings.MinecraftTimings;
import io.akarin.api.internal.Akari;
import io.akarin.api.internal.mixin.IMixinLockProvider;
import io.akarin.server.core.AkarinGlobalConfig;
import io.akarin.server.core.AkarinSlackScheduler;
import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
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
        for (int i = 0; i < worlds.size(); ++i) {
            WorldServer world = worlds.get(i);
            TileEntityHopper.skipHopperEvents = world.paperConfig.disableHopperMoveEvents || InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length == 0;
        }
        AkarinSlackScheduler.boot();
    }
    
    /*
     * Forcely disable snooper
     */
    @Overwrite
    public void a(MojangStatisticsGenerator generator) {}
    
    @Overwrite
    public void b(MojangStatisticsGenerator generator) {}
    
    /*
     * Parallel world ticking
     */
    @Shadow public CraftServer server;
    @Shadow @Mutable protected Queue<FutureTask<?>> j;
    @Shadow public Queue<Runnable> processQueue;
    @Shadow private int ticks;
    @Shadow public List<WorldServer> worlds;
    @Shadow private PlayerList v;
    @Shadow @Final private List<ITickable> o;
    
    @Shadow public abstract PlayerList getPlayerList();
    @Shadow public abstract ServerConnection an();
    @Shadow public abstract CustomFunctionData aL();
    
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
    public void D() throws InterruptedException {
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
        
        Akari.worldTiming.startTiming();
        if (AkarinGlobalConfig.legacyWorldTimings) {
            for (int i = 0; i < worlds.size(); ++i) {
                worlds.get(i).timings.tickEntities.startTiming();
                worlds.get(i).timings.doTick.startTiming();
            }
        }
        Akari.silentTiming = true; // Disable timings
        Akari.mayEnableAsyncCathcer = false;
        Akari.STAGE_TICK.submit(() -> {
            // Never tick one world concurrently!
            for (int i = 1; i <= worlds.size(); ++i) {
                WorldServer world = worlds.get(i < worlds.size() ? i : 0);
                synchronized (((IMixinLockProvider) world).lock()) {
                    tickEntities(world);
                }
            }
        }, null);
        
        for (int i = 0; i < worlds.size(); ++i) {
            WorldServer world = worlds.get(i);
            synchronized (((IMixinLockProvider) world).lock()) {
                tickWorld(world);
            }
        }
        
        Akari.STAGE_TICK.take();
        Akari.mayEnableAsyncCathcer = true;
        Akari.silentTiming = false; // Enable timings
        Akari.worldTiming.stopTiming();
        if (AkarinGlobalConfig.legacyWorldTimings) {
            for (int i = 0; i < worlds.size(); ++i) {
                worlds.get(i).timings.tickEntities.stopTiming();
                worlds.get(i).timings.doTick.startTiming();
            }
        }
        
        Akari.callbackTiming.startTiming();
        while ((runnable = Akari.callbackQueue.poll()) != null) runnable.run();
        Akari.callbackTiming.stopTiming();
        
        for (int i = 0; i < worlds.size(); ++i) {
            WorldServer world = worlds.get(i);
            tickUnsafeSync(world);
            
            world.getTracker().updatePlayers();
            world.explosionDensityCache.clear(); // Paper - Optimize explosions
        }
        
        MinecraftTimings.connectionTimer.startTiming();
        this.an().c();
        MinecraftTimings.connectionTimer.stopTiming();
        
        MinecraftTimings.playerListTimer.startTiming();
        this.v.tick();
        MinecraftTimings.playerListTimer.stopTiming();
        
        MinecraftTimings.commandFunctionsTimer.startTiming();
        this.aL().e();
        MinecraftTimings.commandFunctionsTimer.stopTiming();
        
        MinecraftTimings.tickablesTimer.startTiming();
        for (int i = 0; i < this.o.size(); ++i) {
            this.o.get(i).e();
        }
        MinecraftTimings.tickablesTimer.stopTiming();
    }
    
    public void tickUnsafeSync(WorldServer world) {
        world.timings.doChunkMap.startTiming();
        world.manager.flush();
        world.timings.doChunkMap.stopTiming();
    }
    
}
