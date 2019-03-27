package io.akarin.server.core;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.server.MinecraftServer;

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
            
            try {
                long sleepFixed = STD_TICK_TIME - (System.currentTimeMillis() - currentLoop);
                Thread.sleep(sleepFixed);
            } catch (InterruptedException interrupted) {
                continue;
            }
        }
    }
}