package io.akarin.server.mixin.core;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.destroystokyo.paper.PaperConfig;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.FileIOThread;
import net.minecraft.server.IAsyncChunkSaver;

@Mixin(value = FileIOThread.class, remap = false)
public abstract class MixinFileIOThread {
    private final Executor executor = Executors.newFixedThreadPool(AkarinGlobalConfig.fileIOThreads, new ThreadFactoryBuilder().setNameFormat("Akarin File IO Thread - %1$d").setPriority(1).build());
    private final AtomicInteger queuedChunkCounter = new AtomicInteger(0);
    
    @Shadow(aliases = "e") private volatile boolean isAwaitFinish;
    
    @Overwrite // OBFHELPER: saveChunk
    public void a(IAsyncChunkSaver iasyncchunksaver) {
        queuedChunkCounter.incrementAndGet();
        executor.execute(() -> writeChunk(iasyncchunksaver));
    }
    
    /**
     * Process a chunk, re-add to the queue if unsuccessful
     */
    private void writeChunk(IAsyncChunkSaver iasyncchunksaver)  {
        if (!iasyncchunksaver.a()) { // PAIL: WriteNextIO() -> Returns if the write was unsuccessful
            queuedChunkCounter.decrementAndGet();
            
            if (PaperConfig.enableFileIOThreadSleep) {
                try {
                    Thread.sleep(isAwaitFinish ? 0L : 2L);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        } else {
            writeChunk(iasyncchunksaver);
        }
    }
    
    @Overwrite // OBFHELPER: waitForFinish
    public void b() throws InterruptedException {
        isAwaitFinish = true;
        while (queuedChunkCounter.get() != 0) Thread.sleep(9L);
        isAwaitFinish = false;
    }
}
