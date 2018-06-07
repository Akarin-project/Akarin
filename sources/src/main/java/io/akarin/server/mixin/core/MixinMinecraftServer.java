package io.akarin.server.mixin.core;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.FutureTask;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import co.aikar.timings.MinecraftTimings;
import io.akarin.api.Akari;
import net.minecraft.server.CrashReport;
import net.minecraft.server.CustomFunctionData;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ITickable;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.MojangStatisticsGenerator;
import net.minecraft.server.PacketPlayOutUpdateTime;
import net.minecraft.server.PlayerList;
import net.minecraft.server.ReportedException;
import net.minecraft.server.ServerConnection;
import net.minecraft.server.SystemUtils;
import net.minecraft.server.TileEntityHopper;
import net.minecraft.server.WorldServer;

@Mixin(value = MinecraftServer.class, remap = false)
public class MixinMinecraftServer {
    @Overwrite
    public String getServerModName() {
        return "Akarin";
    }
    
    /*
     * Forcely disable snooper
     */
    @Overwrite
    public void a(MojangStatisticsGenerator generator) {}
    
    @Overwrite
    public void b(MojangStatisticsGenerator generator) {}
    
    @Shadow public CraftServer server;
    @Shadow @Mutable protected Queue<FutureTask<?>> j;
    @Shadow public Queue<Runnable> processQueue;
    @Shadow private int ticks;
    @Shadow public List<WorldServer> worlds;
    @Shadow private PlayerList v;
    @Shadow @Final private List<ITickable> o;
    
    @Shadow public PlayerList getPlayerList() { return null; }
    @Shadow public ServerConnection an() { return null; }
    @Shadow public CustomFunctionData aL() { return null; }
    
    private void tickEntities(WorldServer world) {
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
    }
    
    @Overwrite
    public void D() throws InterruptedException {
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
        
        Runnable runnable;
        MinecraftTimings.processQueueTimer.startTiming();
        while ((runnable = processQueue.poll()) != null) runnable.run();
        MinecraftTimings.processQueueTimer.stopTiming();
        
        MinecraftTimings.chunkIOTickTimer.startTiming();
        ChunkIOExecutor.tick();
        MinecraftTimings.chunkIOTickTimer.stopTiming();
        
        MinecraftTimings.timeUpdateTimer.startTiming();
        // Send time updates to everyone, it will get the right time from the world the player is in.
        if (this.ticks % 20 == 0) {
            for (int i = 0; i < this.getPlayerList().players.size(); ++i) {
                EntityPlayer entityplayer = this.getPlayerList().players.get(i);
                entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateTime(entityplayer.world.getTime(), entityplayer.getPlayerTime(), entityplayer.world.getGameRules().getBoolean("doDaylightCycle"))); // Add support for per player time
            }
        }
        MinecraftTimings.timeUpdateTimer.stopTiming();
        
        for (int i = 0; i < worlds.size(); ++i) {
            WorldServer mainWorld = worlds.get(i);
            WorldServer entityWorld = worlds.get(i + 1 < worlds.size() ? i + 1 : 0);
            TileEntityHopper.skipHopperEvents = entityWorld.paperConfig.disableHopperMoveEvents || InventoryMoveItemEvent.getHandlerList().getRegisteredListeners().length == 0;
            
            Akari.silentTiming = true;
            Akari.STAGE_TICK.submit(() -> tickEntities(entityWorld), null);
            
            try {
                mainWorld.timings.doTick.startTiming();
                mainWorld.doTick();
                mainWorld.timings.doTick.stopTiming();
            } catch (Throwable throwable) {
                CrashReport crashreport;
                try {
                    crashreport = CrashReport.a(throwable, "Exception ticking world");
                } catch (Throwable t){
                    throw new RuntimeException("Error generating crash report", t);
                }
                mainWorld.a(crashreport);
                throw new ReportedException(crashreport);
            }
            
            entityWorld.timings.tickEntities.startTiming();
            Akari.STAGE_TICK.take();
            entityWorld.timings.tickEntities.stopTiming();
            
            entityWorld.getTracker().updatePlayers();
            Akari.silentTiming = false;
            mainWorld.explosionDensityCache.clear(); // Paper - Optimize explosions
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
    
}
