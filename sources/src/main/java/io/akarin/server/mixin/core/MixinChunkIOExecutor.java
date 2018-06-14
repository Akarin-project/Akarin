package io.akarin.server.mixin.core;

import org.bukkit.craftbukkit.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.util.AsynchronousExecutor;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.Chunk;

@Mixin(value = ChunkIOExecutor.class, remap = false)
public abstract class MixinChunkIOExecutor {
    @Shadow @Final static int BASE_THREADS;
    @Shadow @Mutable @Final static int PLAYERS_PER_THREAD;
    @Shadow @Final private static AsynchronousExecutor<?, Chunk, Runnable, RuntimeException> instance;
    
    @Overwrite
    public static void adjustPoolSize(int players) {
        int size = Math.max(BASE_THREADS, (int) Math.ceil(players / (PLAYERS_PER_THREAD = AkarinGlobalConfig.playersPerIOThread)));
        instance.setActiveThreads(size);
    }
}
