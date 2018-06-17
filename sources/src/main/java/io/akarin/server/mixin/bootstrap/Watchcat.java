package io.akarin.server.mixin.bootstrap;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.CraftServer;
import org.spigotmc.RestartCommand;
import org.spigotmc.WatchdogThread;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.server.MinecraftServer;

@Mixin(value = WatchdogThread.class, remap = false)
public abstract class Watchcat extends Thread {
    @Shadow private static WatchdogThread instance;
    @Shadow private @Final long timeoutTime;
    @Shadow private @Final boolean restart;
    @Shadow private volatile long lastTick;
    @Shadow private volatile boolean stopping;
    
    @Shadow private static void dumpThread(ThreadInfo thread, Logger log) {}
    
    @Inject(method = "<init>(JZ)V", at = @At("RETURN"))
    private void hook(CallbackInfo info) {
        setName("Akarin Watchcat Thread");
    }
    
    @Override
    @Overwrite
    public void run() {
        while (!stopping) {
            //
            if (lastTick != 0 && System.currentTimeMillis() > lastTick + timeoutTime && !Boolean.getBoolean("disable.watchdog")) { // Paper - Add property to disable
                Logger log = Bukkit.getServer().getLogger();
                log.log(Level.SEVERE, "Server has stopped responding!");
                log.log(Level.SEVERE, "Please report this to https://github.com/Akarin-project/Akarin/issues");
                log.log(Level.SEVERE, "Be sure to include ALL relevant console errors and Minecraft crash reports");
                log.log(Level.SEVERE, "Akarin version: " + Bukkit.getServer().getVersion());
                //
                if (net.minecraft.server.World.haveWeSilencedAPhysicsCrash) {
                    log.log(Level.SEVERE, "------------------------------");
                    log.log(Level.SEVERE, "During the run of the server, a physics stackoverflow was supressed");
                    log.log(Level.SEVERE, "near " + net.minecraft.server.World.blockLocation);
                }
                // Paper start - Warn in watchdog if an excessive velocity was ever set
                if (CraftServer.excessiveVelEx != null) {
                    log.log(Level.SEVERE, "------------------------------");
                    log.log(Level.SEVERE, "During the run of the server, a plugin set an excessive velocity on an entity");
                    log.log(Level.SEVERE, "This may be the cause of the issue, or it may be entirely unrelated");
                    log.log(Level.SEVERE, CraftServer.excessiveVelEx.getMessage());
                    for (StackTraceElement stack : CraftServer.excessiveVelEx.getStackTrace()) {
                        log.log(Level.SEVERE, "\t\t" + stack);
                    }
                }
                // Paper end
                log.log(Level.SEVERE, "------------------------------");
                log.log(Level.SEVERE, "Server thread dump (Look for plugins here before reporting to Akarin!):");
                dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(MinecraftServer.getServer().primaryThread.getId(), Integer.MAX_VALUE), log);
                log.log(Level.SEVERE, "------------------------------");
                //
                log.log(Level.SEVERE, "Entire Thread Dump:");
                ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                for (ThreadInfo thread : threads) {
                    dumpThread(thread, log);
                }
                log.log(Level.SEVERE, "------------------------------");
                
                if (restart) RestartCommand.restart(); // GC Inlined
                break;
            }
            
            try {
                sleep(9000); // Akarin
            } catch (InterruptedException ex) {
                interrupt();
            }
        }
    }
}
