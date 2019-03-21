package io.akarin.server.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;

public class AkarinAsyncExecutor extends Thread {
    private final static Logger LOGGER = LogManager.getLogger("Akarin");
    private final static int STD_TICK_TIME = 50;
    
    public static AkarinAsyncExecutor initalise() {
        return Singleton.instance;
    }
    
    private static class Singleton {
        private static final AkarinAsyncExecutor instance;
        
        static {
            instance = new AkarinAsyncExecutor();
            instance.setName("Akarin Slack Scheduler Thread");
            instance.setDaemon(true);
            instance.start();
            LOGGER.info("Async executor started");
        }
    }
    
    @Override
    public void run() {
        MinecraftServer server = MinecraftServer.getServer();
        
        while (server.isRunning()) {
            server.getWorlds().forEach(world -> world.entityList.forEach(entity -> {
                if (!entity.dead && entity.spirtingWaterParticle) {
                    entity.populateSprintingWaterParticles();
                    entity.spirtingWaterParticle = false;
                }
            }));
            
            try {
                Thread.sleep(STD_TICK_TIME);
            } catch (InterruptedException interrupted) {
                continue;
            }
        }
    }
    
}