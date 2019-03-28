package io.akarin.server.core;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PacketPlayOutPlayerInfo;
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
            instance.setPriority(MIN_PRIORITY);
            instance.start();
            LOGGER.info("Async executor started");
        }
    }
    
    private int playerListTick;
    
    @Override
    public void run() {
        MinecraftServer server = MinecraftServer.getServer();
        
        while (server.isRunning()) {
            long currentLoop = System.currentTimeMillis();
            
            // Send pending chunk packets
            List<NetworkManager> networkManagers = server.getServerConnection().getNetworkManagers();
            if (!networkManagers.isEmpty()) {
                synchronized (networkManagers) {
                    for (NetworkManager player : networkManagers)
                        player.sendPacketQueue();
                }
            }
            
            // Send time updates to everyone, it will get the right time from the world the player is in.
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
            
            // Send player latency update packets
            if (++playerListTick > 600) {
                List<EntityPlayer> players = server.getPlayerList().players;
                for (EntityPlayer target : players) {
                    target.playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_LATENCY, Iterables.filter(players, new Predicate<EntityPlayer>() {
                        @Override
                        public boolean apply(EntityPlayer input) {
                            return target.getBukkitEntity().canSee(input.getBukkitEntity());
                        }
                    })));
                }
                playerListTick = 0;
            }
            
            // Save players data
            int playerSaveInterval = com.destroystokyo.paper.PaperConfig.playerAutoSaveRate;
            if (playerSaveInterval < 0) {
                playerSaveInterval = server.autosavePeriod;
            }
            if (playerSaveInterval > 0) {
                server.getPlayerList().savePlayers(playerSaveInterval);
            }
            
            try {
                long sleepFixed = STD_TICK_TIME - (System.currentTimeMillis() - currentLoop);
                if (sleepFixed > 0) Thread.sleep(sleepFixed);
            } catch (InterruptedException interrupted) {
                continue;
            }
        }
    }
}