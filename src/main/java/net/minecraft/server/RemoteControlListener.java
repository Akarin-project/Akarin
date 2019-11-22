package net.minecraft.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class RemoteControlListener extends RemoteConnectionThread {

    private final int h;
    private String i;
    private ServerSocket j;
    private final String k;
    private Map<SocketAddress, RemoteControlSession> l;

    public RemoteControlListener(IMinecraftServer iminecraftserver) {
        super(iminecraftserver, "RCON Listener");
        DedicatedServerProperties dedicatedserverproperties = iminecraftserver.getDedicatedServerProperties();

        this.h = dedicatedserverproperties.rconPort;
        this.k = dedicatedserverproperties.rconPassword;
        this.i = dedicatedserverproperties.rconIp; // Paper - Configurable rcon ip
        if (this.i.isEmpty()) {
            this.i = "0.0.0.0";
        }

        this.f();
        this.j = null;
    }

    private void f() {
        this.l = Maps.newHashMap();
    }

    private void g() {
        Iterator iterator = this.l.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<SocketAddress, RemoteControlSession> entry = (Entry) iterator.next();

            if (!((RemoteControlSession) entry.getValue()).c()) {
                iterator.remove();
            }
        }

    }

    public void run() {
        this.b("RCON running on " + this.i + ":" + this.h);

        try {
            while (this.a) {
                try {
                    Socket socket = this.j.accept();

                    socket.setSoTimeout(500);
                    RemoteControlSession remotecontrolsession = new RemoteControlSession(this.b, this.k, socket);

                    remotecontrolsession.a();
                    this.l.put(socket.getRemoteSocketAddress(), remotecontrolsession);
                    this.g();
                } catch (SocketTimeoutException sockettimeoutexception) {
                    this.g();
                } catch (IOException ioexception) {
                    if (this.a) {
                        this.b("IO: " + ioexception.getMessage());
                    }
                }
            }
        } finally {
            this.b(this.j);
        }

    }

    @Override
    public void a() {
        if (this.k.isEmpty()) {
            this.c("No rcon password set in server.properties, rcon disabled!");
        } else if (0 < this.h && 65535 >= this.h) {
            if (!this.a) {
                try {
                    this.j = new ServerSocket(this.h, 0, InetAddress.getByName(this.i));
                    this.j.setSoTimeout(500);
                    super.a();
                } catch (IOException ioexception) {
                    this.c("Unable to initialise rcon on " + this.i + ":" + this.h + " : " + ioexception.getMessage());
                }

            }
        } else {
            this.c("Invalid rcon port " + this.h + " found in server.properties, rcon disabled!");
        }
    }

    @Override
    public void b() {
        super.b();
        Iterator iterator = this.l.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<SocketAddress, RemoteControlSession> entry = (Entry) iterator.next();

            ((RemoteControlSession) entry.getValue()).b();
        }

        this.b(this.j);
        this.f();
    }
}
