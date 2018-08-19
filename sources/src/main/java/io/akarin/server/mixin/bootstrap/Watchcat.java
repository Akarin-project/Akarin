package io.akarin.server.mixin.bootstrap;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
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
    @Shadow private @Final long earlyWarningEvery; // Paper - Timeout time for just printing a dump but not restarting
    @Shadow private @Final long earlyWarningDelay; // Paper
    @Shadow public static volatile boolean hasStarted; // Paper
    @Shadow private long lastEarlyWarning; // Paper - Keep track of short dump times to avoid spamming console with short dumps
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
            // Paper start
            long currentTime = System.currentTimeMillis();
            if ( lastTick != 0 && currentTime > lastTick + earlyWarningEvery && !Boolean.getBoolean("disable.watchdog") )
            {
                boolean isLongTimeout = currentTime > lastTick + timeoutTime;
                // Don't spam early warning dumps
                if (!isLongTimeout && (earlyWarningEvery <= 0 || !hasStarted || currentTime < lastEarlyWarning + earlyWarningEvery || currentTime < lastTick + earlyWarningDelay))
                    continue;
                lastEarlyWarning = currentTime;
                // Paper end
                Logger log = Bukkit.getServer().getLogger();
                // Paper start - Different message when it's a short timeout
                if (isLongTimeout) {
                    log.log(Level.SEVERE, "The server has stopped responding!");
                    log.log(Level.SEVERE, "Please report this to https://github.com/Akarin-project/Akarin/issues"); // Akarin
                    log.log(Level.SEVERE, "Be sure to include ALL relevant console errors and Minecraft crash reports");
                    log.log(Level.SEVERE, "Akarin version: " + Bukkit.getServer().getVersion()); // Akarin
                    //
                    if (net.minecraft.server.World.haveWeSilencedAPhysicsCrash) {
                        log.log(Level.SEVERE, "------------------------------");
                        log.log(Level.SEVERE, "During the run of the server, a physics stackoverflow was supressed");
                        log.log(Level.SEVERE, "near " + net.minecraft.server.World.blockLocation);
                    }
                    // Paper start - Warn in watchdog if an excessive velocity was ever set
                    if (org.bukkit.craftbukkit.CraftServer.excessiveVelEx != null) {
                        log.log(Level.SEVERE, "------------------------------");
                        log.log(Level.SEVERE, "During the run of the server, a plugin set an excessive velocity on an entity");
                        log.log(Level.SEVERE, "This may be the cause of the issue, or it may be entirely unrelated");
                        log.log(Level.SEVERE, org.bukkit.craftbukkit.CraftServer.excessiveVelEx.getMessage());
                        for (StackTraceElement stack : org.bukkit.craftbukkit.CraftServer.excessiveVelEx.getStackTrace()) {
                            log.log(Level.SEVERE, "\t\t" + stack);
                        }
                    }
                    // Paper end
                } else {
                    // log.log(Level.SEVERE, "--- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH ---"); // Akarin
                    log.log(Level.SEVERE, "The server has not responded for " + (currentTime - lastTick) / 1000 + " seconds! Creating thread dump");
                }
                // Paper end - Different message for short timeout
                log.log(Level.SEVERE, "------------------------------");
                log.log(Level.SEVERE, "Server thread dump (Look for plugins here before reporting to Akarin!):");
                dumpThread(ManagementFactory.getThreadMXBean().getThreadInfo(MinecraftServer.getServer().primaryThread.getId(), Integer.MAX_VALUE), log);
                log.log(Level.SEVERE, "------------------------------");
                //
                // Paper start - Only print full dump on long timeouts
                if (isLongTimeout) {
                    log.log(Level.SEVERE, "Entire Thread Dump:");
                    ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);
                    for (ThreadInfo thread : threads) {
                        dumpThread(thread, log);
                    }
                } else {
                    // log.log(Level.SEVERE, "--- DO NOT REPORT THIS TO PAPER - THIS IS NOT A BUG OR A CRASH ---"); // Akarin
                }
                    log.log(Level.SEVERE, "------------------------------");
                    
                    if ( isLongTimeout )
                    {
                    if (restart) {
                        RestartCommand.restart();
                    }
                    break;
                } // Paper end
            }
            
            try {
                sleep(1000); // Paper - Reduce check time to every second instead of every ten seconds, more consistent and allows for short timeout
            } catch (InterruptedException ex) {
                interrupt();
            }
        }
    }
}
