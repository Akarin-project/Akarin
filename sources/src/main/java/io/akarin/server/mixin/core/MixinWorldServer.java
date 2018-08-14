package io.akarin.server.mixin.core;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.spongepowered.asm.mixin.Mixin;
import io.akarin.api.internal.mixin.IMixinWorldServer;
import net.minecraft.server.WorldServer;

@Mixin(value = WorldServer.class, remap = false)
public abstract class MixinWorldServer implements IMixinWorldServer {
    private final Object tickLock = new Object();

    @Override
    public Object lock() {
        return tickLock;
    }
    
    private final Random sharedRandom = new io.akarin.api.internal.utils.random.LightRandom() {
        private static final long serialVersionUID = 1L;
        private boolean locked = false;
        @Override
        public synchronized void setSeed(long seed) {
            if (locked) {
                LogManager.getLogger().error("Ignoring setSeed on Entity.SHARED_RANDOM", new Throwable());
            } else {
                super.setSeed(seed);
                locked = true;
            }
        }
    };
    
    @Override
    public Random rand() {
        return sharedRandom;
    }
}
