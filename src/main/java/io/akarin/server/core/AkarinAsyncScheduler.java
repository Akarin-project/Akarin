package io.akarin.server.core;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PacketPlayOutUpdateTime;
import net.minecraft.server.WorldServer;

public class AkarinAsyncScheduler extends Thread {
    private final static Logger LOGGER = LogManager.getLogger("Akarin");
    private final static int STD_TICK_TIME = 50;
    
    public static AkarinAsyncScheduler initalise() {
        return Singleton.instance;
    }
    
    private static class Singleton {
        private static final AkarinAsyncScheduler instance;
        
        static {
            instance = new AkarinAsyncScheduler();
            instance.setName("Akarin Async Scheduler Thread");
            instance.setDaemon(true);
            instance.start();
            LOGGER.info("Async executor started");
        }
    }
    
    @Override
    public void run() {
        MinecraftServer server = MinecraftServer.getServer();
        
        while (server.isRunning()) {
            long currentLoop = System.currentTimeMillis();
            
            List<NetworkManager> networkManagers = server.getServerConnection().getNetworkManagers();
            if (!networkManagers.isEmpty()) {
                synchronized (networkManagers) {
                    for (NetworkManager player : networkManagers)
                        player.sendPacketQueue();
                }
            }
            
            // Send time updates to everyone, it will get the right time from the world the player is in.
            // Paper start - optimize time updates
            for (final WorldServer world : server.getWorlds()) {
                final boolean doDaylight = world.getGameRules().getBoolean("doDaylightCycle");
                final long dayTime = world.getDayTime();
                long worldTime = world.getTime();
                final PacketPlayOutUpdateTime worldPacket = new PacketPlayOutUpdateTime(worldTime, dayTime, doDaylight);
                for (EntityHuman entityhuman : world.players) {
                    if (!(entityhuman instanceof EntityPlayer) || (server.currentTick() + entityhuman.getId()) % 20 != 0) {
                        continue;
                    }
                    EntityPlayer entityplayer = (EntityPlayer) entityhuman;
                    long playerTime = entityplayer.getPlayerTime();
                    PacketPlayOutUpdateTime packet = (playerTime == dayTime) ? worldPacket :
                        new PacketPlayOutUpdateTime(worldTime, playerTime, doDaylight);
                    entityplayer.playerConnection.sendPacket(packet); // Add support for per player time
                }
            }
            // Paper end
            
            try {
                long sleepFixed = STD_TICK_TIME - (System.currentTimeMillis() - currentLoop);
                Thread.sleep(sleepFixed);
            } catch (InterruptedException interrupted) {
                continue;
            }
        }
    }
}