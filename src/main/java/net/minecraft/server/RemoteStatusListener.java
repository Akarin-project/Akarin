package net.minecraft.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

public class RemoteStatusListener extends RemoteConnectionThread {

    private long h;
    private int i;
    private final int j; private int getServerPort() { return j; } // Paper - OBFHELPER
    private final int k; private int getMaxPlayers() { return k; } // Paper - OBFHELPER
    private final String l; private String getMotd() { return l; } // Paper - OBFHELPER
    private final String m; private String getWorldName() { return m; } // Paper - OBFHELPER
    private DatagramSocket n;
    private final byte[] o = new byte[1460];
    private DatagramPacket p;
    private final Map<SocketAddress, String> q;
    private String r; private String getServerHost() { return r; } // Paper - OBFHELPER
    private String s;
    private final Map<SocketAddress, RemoteStatusListener.RemoteStatusChallenge> t;
    private final long u;
    private final RemoteStatusReply v; private RemoteStatusReply getCachedFullResponse() { return v; } // Paper - OBFHELPER
    private long w;

    public RemoteStatusListener(IMinecraftServer iminecraftserver) {
        super(iminecraftserver, "Query Listener");
        this.i = iminecraftserver.a("query.port", 0);
        this.s = iminecraftserver.e();
        this.j = iminecraftserver.e_();
        this.l = iminecraftserver.m();
        this.k = iminecraftserver.getMaxPlayers();
        this.m = iminecraftserver.getWorld();
        this.w = 0L;
        this.r = "0.0.0.0";
        if (!this.s.isEmpty() && !this.r.equals(this.s)) {
            this.r = this.s;
        } else {
            this.s = "0.0.0.0";

            try {
                InetAddress inetaddress = InetAddress.getLocalHost();

                this.r = inetaddress.getHostAddress();
            } catch (UnknownHostException unknownhostexception) {
                this.c("Unable to determine local host IP, please set server-ip in '" + iminecraftserver.d_() + "' : " + unknownhostexception.getMessage());
            }
        }

        if (0 == this.i) {
            this.i = this.j;
            this.b("Setting default query port to " + this.i);
            iminecraftserver.a("query.port", (Object) this.i);
            iminecraftserver.a("debug", (Object) false);
            iminecraftserver.c_();
        }

        this.q = Maps.newHashMap();
        this.v = new RemoteStatusReply(1460);
        this.t = Maps.newHashMap();
        this.u = (new Date()).getTime();
    }

    private void a(byte[] abyte, DatagramPacket datagrampacket) throws IOException {
        this.n.send(new DatagramPacket(abyte, abyte.length, datagrampacket.getSocketAddress()));
    }

    private boolean a(DatagramPacket datagrampacket) throws IOException {
        byte[] abyte = datagrampacket.getData();
        int i = datagrampacket.getLength();
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        this.a("Packet len " + i + " [" + socketaddress + "]");
        if (3 <= i && -2 == abyte[0] && -3 == abyte[1]) {
            this.a("Packet '" + StatusChallengeUtils.a(abyte[2]) + "' [" + socketaddress + "]");
            switch (abyte[2]) {
            case 0:
                if (!this.c(datagrampacket)) {
                    this.a("Invalid challenge [" + socketaddress + "]");
                    return false;
                } else if (15 == i) {
                    this.a(this.b(datagrampacket), datagrampacket);
                    this.a("Rules [" + socketaddress + "]");
                } else {
                    RemoteStatusReply remotestatusreply = new RemoteStatusReply(1460);

                    remotestatusreply.a((int) 0);
                    remotestatusreply.a(this.a(datagrampacket.getSocketAddress()));
                    /* Paper start - GS4 Query event
                    remotestatusreply.a(this.l);
                    remotestatusreply.a("SMP");
                    remotestatusreply.a(this.m);
                    remotestatusreply.a(Integer.toString(this.d()));
                    remotestatusreply.a(Integer.toString(this.k));
                    remotestatusreply.a((short) this.j);
                    remotestatusreply.a(this.r);
                    */
                    com.destroystokyo.paper.event.server.GS4QueryEvent.QueryType queryType =
                        com.destroystokyo.paper.event.server.GS4QueryEvent.QueryType.BASIC;
                    com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse queryResponse = com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse.builder()
                        .motd(this.getMotd())
                        .map(this.getWorldName())
                        .currentPlayers(this.getPlayerCount())
                        .maxPlayers(this.getMaxPlayers())
                        .port(this.getServerPort())
                        .hostname(this.getServerHost())
                        .gameVersion(this.getServer().getVersion())
                        .serverVersion(org.bukkit.Bukkit.getServer().getName() + " on " + org.bukkit.Bukkit.getServer().getBukkitVersion())
                        .build();
                    com.destroystokyo.paper.event.server.GS4QueryEvent queryEvent =
                        new com.destroystokyo.paper.event.server.GS4QueryEvent(queryType, datagrampacket.getAddress(), queryResponse);
                    queryEvent.callEvent();
                    queryResponse = queryEvent.getResponse();
                    remotestatusreply.writeString(queryResponse.getMotd());
                    remotestatusreply.writeString("SMP");
                    remotestatusreply.writeString(queryResponse.getMap());
                    remotestatusreply.writeString(Integer.toString(queryResponse.getCurrentPlayers()));
                    remotestatusreply.writeString(Integer.toString(queryResponse.getMaxPlayers()));
                    remotestatusreply.writeShort((short) queryResponse.getPort());
                    remotestatusreply.writeString(queryResponse.getHostname());
                    // Paper end
                    this.a(remotestatusreply.a(), datagrampacket);
                    this.a("Status [" + socketaddress + "]");
                }
            default:
                return true;
            case 9:
                this.d(datagrampacket);
                this.a("Challenge [" + socketaddress + "]");
                return true;
            }
        } else {
            this.a("Invalid packet [" + socketaddress + "]");
            return false;
        }
    }

    private byte[] b(DatagramPacket datagrampacket) throws IOException {
        long i = SystemUtils.getMonotonicMillis();

        if (i < this.w + 5000L) {
            byte[] abyte = this.v.a();
            byte[] abyte1 = this.a(datagrampacket.getSocketAddress());

            abyte[1] = abyte1[0];
            abyte[2] = abyte1[1];
            abyte[3] = abyte1[2];
            abyte[4] = abyte1[3];
            return abyte;
        } else {
            this.w = i;
            this.v.b();
            this.v.a((int) 0);
            this.v.a(this.a(datagrampacket.getSocketAddress()));
            this.v.a("splitnum");
            this.v.a((int) 128);
            this.v.a((int) 0);
            /* Paper start - GS4 Query event
            this.v.a("hostname");
            this.v.a(this.l);
            this.v.a("gametype");
            this.v.a("SMP");
            this.v.a("game_id");
            this.v.a("MINECRAFT");
            this.v.a("version");
            this.v.a(this.b.getVersion());
            this.v.a("plugins");
            this.v.a(this.b.getPlugins());
            this.v.a("map");
            this.v.a(this.m);
            this.v.a("numplayers");
            this.v.a("" + this.d());
            this.v.a("maxplayers");
            this.v.a("" + this.k);
            this.v.a("hostport");
            this.v.a("" + this.j);
            this.v.a("hostip");
            this.v.a(this.r);
            this.v.a((int) 0);
            this.v.a((int) 1);
            this.v.a("player_");
            this.v.a((int) 0);
            String[] astring = this.b.getPlayers();
            String[] astring1 = astring;
            int j = astring.length;

            for (int k = 0; k < j; ++k) {
                String s = astring1[k];

                this.v.a(s);
            }

            this.v.a((int) 0);
            */
            // Pack plugins
            java.util.List<com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse.PluginInformation> plugins = java.util.Collections.emptyList();
            org.bukkit.plugin.Plugin[] bukkitPlugins;
            if(((DedicatedServer) this.getServer()).server.getQueryPlugins() && (bukkitPlugins = org.bukkit.Bukkit.getPluginManager().getPlugins()).length > 0) {
                plugins = java.util.stream.Stream.of(bukkitPlugins)
                    .map(plugin -> com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse.PluginInformation.of(plugin.getName(), plugin.getDescription().getVersion()))
                    .collect(java.util.stream.Collectors.toList());
            }

            com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse queryResponse = com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse.builder()
                .motd(this.getMotd())
                .map(this.getWorldName())
                .currentPlayers(this.getPlayerCount())
                .maxPlayers(this.getMaxPlayers())
                .port(this.getServerPort())
                .hostname(this.getServerHost())
                .plugins(plugins)
                .players(this.getServer().getPlayers())
                .gameVersion(this.getServer().getVersion())
                .serverVersion(org.bukkit.Bukkit.getServer().getName() + " on " + org.bukkit.Bukkit.getServer().getBukkitVersion())
                .build();
            com.destroystokyo.paper.event.server.GS4QueryEvent.QueryType queryType =
                com.destroystokyo.paper.event.server.GS4QueryEvent.QueryType.FULL;
            com.destroystokyo.paper.event.server.GS4QueryEvent queryEvent =
                new com.destroystokyo.paper.event.server.GS4QueryEvent(queryType, datagrampacket.getAddress(), queryResponse);
            queryEvent.callEvent();
            queryResponse = queryEvent.getResponse();
            this.getCachedFullResponse().writeString("hostname");
            this.getCachedFullResponse().writeString(queryResponse.getMotd());
            this.getCachedFullResponse().writeString("gametype");
            this.getCachedFullResponse().writeString("SMP");
            this.getCachedFullResponse().writeString("game_id");
            this.getCachedFullResponse().writeString("MINECRAFT");
            this.getCachedFullResponse().writeString("version");
            this.getCachedFullResponse().writeString(queryResponse.getGameVersion());
            this.getCachedFullResponse().writeString("plugins");
            java.lang.StringBuilder pluginsString = new java.lang.StringBuilder();
            pluginsString.append(queryResponse.getServerVersion());
            if(!queryResponse.getPlugins().isEmpty()) {
                pluginsString.append(": ");
                Iterator<com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse.PluginInformation> iter = queryResponse.getPlugins().iterator();
                while(iter.hasNext()) {
                    com.destroystokyo.paper.event.server.GS4QueryEvent.QueryResponse.PluginInformation info = iter.next();
                    pluginsString.append(info.getName());
                    if (info.getVersion() != null) {
                        pluginsString.append(' ').append(info.getVersion().replaceAll(";", ","));
                    }
                    if (iter.hasNext()) {
                        pluginsString.append(';').append(' ');
                    }
                }
            }
            this.getCachedFullResponse().writeString(pluginsString.toString());
            this.getCachedFullResponse().writeString("map");
            this.getCachedFullResponse().writeString(queryResponse.getMap());
            this.getCachedFullResponse().writeString("numplayers");
            this.getCachedFullResponse().writeString(Integer.toString(queryResponse.getCurrentPlayers()));
            this.getCachedFullResponse().writeString("maxplayers");
            this.getCachedFullResponse().writeString(Integer.toString(queryResponse.getMaxPlayers()));
            this.getCachedFullResponse().writeString("hostport");
            this.getCachedFullResponse().writeString(Integer.toString(queryResponse.getPort()));
            this.getCachedFullResponse().writeString("hostip");
            this.getCachedFullResponse().writeString(queryResponse.getHostname());
            // The "meaningless data" start, copied from above
            this.getCachedFullResponse().writeInt(0);
            this.getCachedFullResponse().writeInt(1);
            this.getCachedFullResponse().writeString("player_");
            this.getCachedFullResponse().writeInt(0);
            // "Meaningless data" end
            queryResponse.getPlayers().forEach(this.getCachedFullResponse()::writeStringUnchecked);
            this.getCachedFullResponse().writeInt(0);
            // Paper end
            return this.v.a();
        }
    }

    private byte[] a(SocketAddress socketaddress) {
        return ((RemoteStatusListener.RemoteStatusChallenge) this.t.get(socketaddress)).c();
    }

    private Boolean c(DatagramPacket datagrampacket) {
        SocketAddress socketaddress = datagrampacket.getSocketAddress();

        if (!this.t.containsKey(socketaddress)) {
            return false;
        } else {
            byte[] abyte = datagrampacket.getData();

            return ((RemoteStatusListener.RemoteStatusChallenge) this.t.get(socketaddress)).a() != StatusChallengeUtils.c(abyte, 7, datagrampacket.getLength()) ? false : true;
        }
    }

    private void d(DatagramPacket datagrampacket) throws IOException {
        RemoteStatusListener.RemoteStatusChallenge remotestatuslistener_remotestatuschallenge = new RemoteStatusListener.RemoteStatusChallenge(datagrampacket);

        this.t.put(datagrampacket.getSocketAddress(), remotestatuslistener_remotestatuschallenge);
        this.a(remotestatuslistener_remotestatuschallenge.b(), datagrampacket);
    }

    private void f() {
        if (this.a) {
            long i = SystemUtils.getMonotonicMillis();

            if (i >= this.h + 30000L) {
                this.h = i;
                Iterator iterator = this.t.entrySet().iterator();

                while (iterator.hasNext()) {
                    Entry<SocketAddress, RemoteStatusListener.RemoteStatusChallenge> entry = (Entry) iterator.next();

                    if (((RemoteStatusListener.RemoteStatusChallenge) entry.getValue()).a(i)) {
                        iterator.remove();
                    }
                }

            }
        }
    }

    public void run() {
        this.b("Query running on " + this.s + ":" + this.i);
        this.h = SystemUtils.getMonotonicMillis();
        this.p = new DatagramPacket(this.o, this.o.length);

        try {
            while (this.a) {
                try {
                    this.n.receive(this.p);
                    this.f();
                    this.a(this.p);
                } catch (SocketTimeoutException sockettimeoutexception) {
                    this.f();
                } catch (PortUnreachableException portunreachableexception) {
                    ;
                } catch (IOException ioexception) {
                    this.a((Exception) ioexception);
                }
            }
        } finally {
            this.e();
        }

    }

    public void a() {
        if (!this.a) {
            if (0 < this.i && 65535 >= this.i) {
                if (this.g()) {
                    super.a();
                }

            } else {
                this.c("Invalid query port " + this.i + " found in '" + this.b.d_() + "' (queries disabled)");
            }
        }
    }

    private void a(Exception exception) {
        if (this.a) {
            this.c("Unexpected exception, buggy JRE? (" + exception + ")");
            if (!this.g()) {
                this.d("Failed to recover from buggy JRE, shutting down!");
                this.a = false;
            }

        }
    }

    private boolean g() {
        try {
            this.n = new DatagramSocket(this.i, InetAddress.getByName(this.s));
            this.a(this.n);
            this.n.setSoTimeout(500);
            return true;
        } catch (SocketException socketexception) {
            this.c("Unable to initialise query system on " + this.s + ":" + this.i + " (Socket): " + socketexception.getMessage());
        } catch (UnknownHostException unknownhostexception) {
            this.c("Unable to initialise query system on " + this.s + ":" + this.i + " (Unknown Host): " + unknownhostexception.getMessage());
        } catch (Exception exception) {
            this.c("Unable to initialise query system on " + this.s + ":" + this.i + " (E): " + exception.getMessage());
        }

        return false;
    }

    class RemoteStatusChallenge {

        private final long time = (new Date()).getTime();
        private final int token;
        private final byte[] identity;
        private final byte[] e;
        private final String f;

        public RemoteStatusChallenge(DatagramPacket datagrampacket) {
            byte[] abyte = datagrampacket.getData();

            this.identity = new byte[4];
            this.identity[0] = abyte[3];
            this.identity[1] = abyte[4];
            this.identity[2] = abyte[5];
            this.identity[3] = abyte[6];
            this.f = new String(this.identity, StandardCharsets.UTF_8);
            this.token = (new Random()).nextInt(16777216);
            this.e = String.format("\t%s%d\u0000", this.f, this.token).getBytes(StandardCharsets.UTF_8);
        }

        public Boolean a(long i) {
            return this.time < i;
        }

        public int a() {
            return this.token;
        }

        public byte[] b() {
            return this.e;
        }

        public byte[] c() {
            return this.identity;
        }
    }
}
