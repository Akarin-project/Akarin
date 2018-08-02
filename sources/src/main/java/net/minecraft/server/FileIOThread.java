package net.minecraft.server;

import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Akarin Changes Note
 * 1) Multi-threaded chunk saving (performance)
 */
public class FileIOThread implements Runnable {

    private static final Logger a = LogManager.getLogger();
    private static final FileIOThread b = new FileIOThread();
    private final List<IAsyncChunkSaver> c = /*Collections.synchronizedList(Lists.newArrayList())*/ null; // Akarin - I don't think any plugin rely on this
    private volatile long d;
    private volatile long e;
    private volatile boolean f;

    private FileIOThread() {
        // Thread thread = new Thread(this, "File IO Thread"); // Akarin

        // thread.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(FileIOThread.a)); // Akarin
        // thread.setPriority(1); // Akarin
        // thread.start(); // Akarin
    }

    public static FileIOThread a() {
        return FileIOThread.b;
    }

    public void run() {
        while (true) {
            this.c();
        }
    }

    private void c() {
        for (int i = 0; i < this.c.size(); ++i) {
            IAsyncChunkSaver iasyncchunksaver = (IAsyncChunkSaver) this.c.get(i);
            boolean flag = iasyncchunksaver.a();

            if (!flag) {
                this.c.remove(i--);
                ++this.e;
            }

            if (com.destroystokyo.paper.PaperConfig.enableFileIOThreadSleep) { // Paper
            try {
                Thread.sleep(this.f ? 0L : 1L); // Paper
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }} // Paper
        }

        if (this.c.isEmpty()) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException interruptedexception1) {
                interruptedexception1.printStackTrace();
            }
        }

    }

    public void a(IAsyncChunkSaver iasyncchunksaver) {
        if (!this.c.contains(iasyncchunksaver)) {
            ++this.d;
            this.c.add(iasyncchunksaver);
        }
    }

    public void b() throws InterruptedException {
        this.f = true;

        while (this.d != this.e) {
            Thread.sleep(10L);
        }

        this.f = false;
    }
}
