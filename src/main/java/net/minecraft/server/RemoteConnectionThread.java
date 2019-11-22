package net.minecraft.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RemoteConnectionThread implements Runnable {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger i = new AtomicInteger(0);
    protected boolean a;
    protected final IMinecraftServer b; protected IMinecraftServer getServer() { return this.b; } // Paper - OBFHELPER
    protected final String c;
    protected Thread d;
    protected final int e = 5;
    protected final List<DatagramSocket> f = Lists.newArrayList();
    protected final List<ServerSocket> g = Lists.newArrayList();

    protected RemoteConnectionThread(IMinecraftServer iminecraftserver, String s) {
        this.b = iminecraftserver;
        this.c = s;
        if (this.b.isDebugging()) {
            this.c("Debugging is enabled, performance maybe reduced!");
        }

    }

    public synchronized void a() {
        this.d = new Thread(this, this.c + " #" + RemoteConnectionThread.i.incrementAndGet());
        this.d.setUncaughtExceptionHandler(new ThreadNamedUncaughtExceptionHandler(RemoteConnectionThread.LOGGER));
        this.d.start();
        this.a = true;
    }

    public synchronized void b() {
        this.a = false;
        if (null != this.d) {
            int i = 0;

            while (this.d.isAlive()) {
                try {
                    this.d.join(1000L);
                    ++i;
                    if (5 <= i) {
                        this.c("Waited " + i + " seconds attempting force stop!");
                        this.a(true);
                    } else if (this.d.isAlive()) {
                        this.c("Thread " + this + " (" + this.d.getState() + ") failed to exit after " + i + " second(s)");
                        this.c("Stack:");
                        StackTraceElement[] astacktraceelement = this.d.getStackTrace();
                        int j = astacktraceelement.length;

                        for (int k = 0; k < j; ++k) {
                            StackTraceElement stacktraceelement = astacktraceelement[k];

                            this.c(stacktraceelement.toString());
                        }

                        this.d.interrupt();
                    }
                } catch (InterruptedException interruptedexception) {
                    ;
                }
            }

            this.a(true);
            this.d = null;
        }
    }

    public boolean c() {
        return this.a;
    }

    protected void a(String s) {
        this.b.h(s);
    }

    protected void b(String s) {
        this.b.info(s);
    }

    protected void c(String s) {
        this.b.warning(s);
    }

    protected void d(String s) {
        this.b.g(s);
    }

    protected int getPlayerCount() { return this.d(); } // Paper - OBFHELPER
    protected int d() {
        return this.b.getPlayerCount();
    }

    protected void a(DatagramSocket datagramsocket) {
        this.a("registerSocket: " + datagramsocket);
        this.f.add(datagramsocket);
    }

    protected boolean a(DatagramSocket datagramsocket, boolean flag) {
        this.a("closeSocket: " + datagramsocket);
        if (null == datagramsocket) {
            return false;
        } else {
            boolean flag1 = false;

            if (!datagramsocket.isClosed()) {
                datagramsocket.close();
                flag1 = true;
            }

            if (flag) {
                this.f.remove(datagramsocket);
            }

            return flag1;
        }
    }

    protected boolean b(ServerSocket serversocket) {
        return this.a(serversocket, true);
    }

    protected boolean a(ServerSocket serversocket, boolean flag) {
        this.a("closeSocket: " + serversocket);
        if (null == serversocket) {
            return false;
        } else {
            boolean flag1 = false;

            try {
                if (!serversocket.isClosed()) {
                    serversocket.close();
                    flag1 = true;
                }
            } catch (IOException ioexception) {
                this.c("IO: " + ioexception.getMessage());
            }

            if (flag) {
                this.g.remove(serversocket);
            }

            return flag1;
        }
    }

    protected void e() {
        this.a(false);
    }

    protected void a(boolean flag) {
        int i = 0;
        Iterator iterator = this.f.iterator();

        while (iterator.hasNext()) {
            DatagramSocket datagramsocket = (DatagramSocket) iterator.next();

            if (this.a(datagramsocket, false)) {
                ++i;
            }
        }

        this.f.clear();
        iterator = this.g.iterator();

        while (iterator.hasNext()) {
            ServerSocket serversocket = (ServerSocket) iterator.next();

            if (this.a(serversocket, false)) {
                ++i;
            }
        }

        this.g.clear();
        if (flag && 0 < i) {
            this.c("Force closed " + i + " sockets");
        }

    }
}
