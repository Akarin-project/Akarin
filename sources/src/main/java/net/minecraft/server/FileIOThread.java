package net.minecraft.server;

import java.util.List;

/**
 * Akarin Changes Note
 * 1) Multi-threaded chunk saving (performance)
 */
public class FileIOThread implements Runnable {

    private static final FileIOThread a = new FileIOThread();
    private final List<IAsyncChunkSaver> b = /*Collections.synchronizedList(Lists.newArrayList())*/ null; // Akarin - I don't think any plugin rely on this
    private volatile long c;
    private volatile long d;
    private volatile boolean e;

    private FileIOThread() {
        // Thread thread = new Thread(this, "File IO Thread"); // Akarin

        // thread.setPriority(1); // Akarin
        // thread.start(); // Akarin
    }

    public static FileIOThread a() {
        return FileIOThread.a;
    }

    public void run() {
        while (true) {
            this.c();
        }
    }

    private void c() {
        for (int i = 0; i < this.b.size(); ++i) {
            IAsyncChunkSaver iasyncchunksaver = (IAsyncChunkSaver) this.b.get(i);
            boolean flag = iasyncchunksaver.a();

            if (!flag) {
                this.b.remove(i--);
                ++this.d;
            }

            // Paper start - Add toggle
            if (com.destroystokyo.paper.PaperConfig.enableFileIOThreadSleep) {
                try {
                    Thread.sleep(this.e ? 0L : 2L);
                } catch (InterruptedException interruptedexception) {
                    interruptedexception.printStackTrace();
                }
            }
            // Paper end
        }

        if (this.b.isEmpty()) {
            try {
                Thread.sleep(25L);
            } catch (InterruptedException interruptedexception1) {
                interruptedexception1.printStackTrace();
            }
        }

    }

    public void a(IAsyncChunkSaver iasyncchunksaver) {
        if (!this.b.contains(iasyncchunksaver)) {
            ++this.c;
            this.b.add(iasyncchunksaver);
        }
    }

    public void b() throws InterruptedException {
        this.e = true;

        while (this.c != this.d) {
            Thread.sleep(10L);
        }

        this.e = false;
    }
}
