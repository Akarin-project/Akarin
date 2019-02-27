package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileIOThread implements Runnable {

    private static final Logger a = LogManager.getLogger();
    private static final FileIOThread b = new FileIOThread();
    private final List<IAsyncChunkSaver> c = Collections.synchronizedList(Lists.newArrayList());
    private volatile long d;
    private volatile long e;
    private volatile boolean f;

    private FileIOThread() {
        Thread thread = new Thread(this, "File IO Thread");

        thread.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(FileIOThread.a));
        thread.setPriority(1);
        thread.start();
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
            boolean flag;

            synchronized (iasyncchunksaver) {
                flag = iasyncchunksaver.a();
            }

            if (!flag) {
                this.c.remove(i--);
                ++this.e;
            }

            try {
                Thread.sleep(this.f ? 0L : 10L);
            } catch (InterruptedException interruptedexception) {
                interruptedexception.printStackTrace();
            }
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
