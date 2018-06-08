package io.akarin.server.core;

import io.akarin.api.Akari;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PacketPlayOutUpdateTime;

public class AkarinSlackScheduler extends Thread {
    public static AkarinSlackScheduler get() {
        return Singleton.instance;
    }
    
    public static void boot() {
        Singleton.instance.setName("Akarin Slack Scheduler Thread");
        Singleton.instance.setPriority(MIN_PRIORITY);
        Singleton.instance.setDaemon(true);
        Singleton.instance.start();
        Akari.logger.info("Slack scheduler service started");
    }
    
    private static class Singleton {
        private static final AkarinSlackScheduler instance = new AkarinSlackScheduler();
    }

    @Override
    public void run() {
        MinecraftServer server = MinecraftServer.getServer();
        
        // Send time updates to everyone, it will get the right time from the world the player is in.
        for (EntityPlayer player : server.getPlayerList().players) {
            player.playerConnection.sendPacket(new PacketPlayOutUpdateTime(player.world.getTime(), player.getPlayerTime(), player.world.getGameRules().getBoolean("doDaylightCycle"))); // Add support for per player time
        }
        
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Akari.logger.warn("Slack scheduler thread was interrupted unexpectly!");
            ex.printStackTrace();
        }
    }
    
}
