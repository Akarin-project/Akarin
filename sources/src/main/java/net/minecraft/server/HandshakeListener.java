package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
// CraftBukkit start
import java.net.InetAddress;
import java.util.Iterator;
import java.util.Map;
// CraftBukkit end

public class HandshakeListener implements PacketHandshakingInListener {

    private static final com.google.gson.Gson gson = new com.google.gson.Gson(); // Spigot
    // CraftBukkit start - add fields
    private static final Object2LongOpenHashMap<InetAddress> throttleTracker = new Object2LongOpenHashMap<>();
    private static int throttleCounter = 0;
    // CraftBukkit end

    private final MinecraftServer a;
    private final NetworkManager b;
    private NetworkManager getNetworkManager() { return b; } // Paper - OBFHELPER

    public HandshakeListener(MinecraftServer minecraftserver, NetworkManager networkmanager) {
        this.a = minecraftserver;
        this.b = networkmanager;
    }

    public void a(PacketHandshakingInSetProtocol packethandshakinginsetprotocol) {
        switch (packethandshakinginsetprotocol.a()) {
        case LOGIN:
            this.b.setProtocol(EnumProtocol.LOGIN);
            ChatMessage chatmessage;

            // CraftBukkit start - Connection throttle
            try {
                long currentTime = System.currentTimeMillis();
                long connectionThrottle = MinecraftServer.getServer().server.getConnectionThrottle();
                InetAddress address = ((java.net.InetSocketAddress) this.b.getSocketAddress()).getAddress();

                synchronized (throttleTracker) {
                    if (throttleTracker.containsKey(address) && !"127.0.0.1".equals(address.getHostAddress()) && currentTime - throttleTracker.getLong(address) < connectionThrottle) {
                        throttleTracker.put(address, currentTime);
                        chatmessage = new ChatMessage("Connection throttled! Please wait before reconnecting.");
                        this.b.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                        this.b.close(chatmessage);
                        return;
                    }

                    throttleTracker.put(address, currentTime);
                    throttleCounter++;
                    if (throttleCounter > 200) {
                        throttleCounter = 0;

                        // Cleanup stale entries
                        throttleTracker.object2LongEntrySet().removeIf(entry -> entry.getLongValue() > connectionThrottle); // Dionysus
                    }
                }
            } catch (Throwable t) {
                org.apache.logging.log4j.LogManager.getLogger().debug("Failed to check connection throttle", t);
            }
            // CraftBukkit end

            if (packethandshakinginsetprotocol.b() > 340) {
                chatmessage = new ChatMessage( java.text.MessageFormat.format( org.spigotmc.SpigotConfig.outdatedServerMessage.replaceAll("'", "''"), "1.12.2" ) ); // Spigot
                this.b.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                this.b.close(chatmessage);
            } else if (packethandshakinginsetprotocol.b() < 340) {
                chatmessage = new ChatMessage( java.text.MessageFormat.format( org.spigotmc.SpigotConfig.outdatedClientMessage.replaceAll("'", "''"), "1.12.2" ) ); // Spigot
                this.b.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                this.b.close(chatmessage);
            } else {
                this.b.setPacketListener(new LoginListener(this.a, this.b));
                // Paper start - handshake event
                boolean proxyLogicEnabled = org.spigotmc.SpigotConfig.bungee;
                boolean handledByEvent = false;
                // Try and handle the handshake through the event
                if (com.destroystokyo.paper.event.player.PlayerHandshakeEvent.getHandlerList().getRegisteredListeners().length != 0) { // Hello? Can you hear me?
                    com.destroystokyo.paper.event.player.PlayerHandshakeEvent event = new com.destroystokyo.paper.event.player.PlayerHandshakeEvent(packethandshakinginsetprotocol.hostname, !proxyLogicEnabled);
                    if (event.callEvent()) {
                        // If we've failed somehow, let the client know so and go no further.
                        if (event.isFailed()) {
                            chatmessage = new ChatMessage(event.getFailMessage());
                            this.b.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                            this.b.close(chatmessage);
                            return;
                        }

                        packethandshakinginsetprotocol.hostname = event.getServerHostname();
                        this.b.l = new java.net.InetSocketAddress(event.getSocketAddressHostname(), ((java.net.InetSocketAddress) this.b.getSocketAddress()).getPort());
                        this.b.spoofedUUID = event.getUniqueId();
                        this.b.spoofedProfile = gson.fromJson(event.getPropertiesJson(), com.mojang.authlib.properties.Property[].class);
                        handledByEvent = true; // Hooray, we did it!
                    }
                }
                // Don't try and handle default logic if it's been handled by the event.
                if (!handledByEvent && proxyLogicEnabled) {
                // Paper end
                // Spigot Start
                //if (org.spigotmc.SpigotConfig.bungee) { // Paper - comment out, we check above!
                    String[] split = packethandshakinginsetprotocol.hostname.split("\00");
                    if ( split.length == 3 || split.length == 4 ) {
                        packethandshakinginsetprotocol.hostname = split[0];
                        b.l = new java.net.InetSocketAddress(split[1], ((java.net.InetSocketAddress) b.getSocketAddress()).getPort());
                        b.spoofedUUID = com.mojang.util.UUIDTypeAdapter.fromString( split[2] );
                    } else
                    {
                        chatmessage = new ChatMessage("If you wish to use IP forwarding, please enable it in your BungeeCord config as well!");
                        this.b.sendPacket(new PacketLoginOutDisconnect(chatmessage));
                        this.b.close(chatmessage);
                        return;
                    }
                    if ( split.length == 4 )
                    {
                        b.spoofedProfile = gson.fromJson(split[3], com.mojang.authlib.properties.Property[].class);
                    }
                }
                // Spigot End
                ((LoginListener) this.b.i()).hostname = packethandshakinginsetprotocol.hostname + ":" + packethandshakinginsetprotocol.port; // CraftBukkit - set hostname
            }
            break;

        case STATUS:
            this.b.setProtocol(EnumProtocol.STATUS);
            this.b.setPacketListener(new PacketStatusListener(this.a, this.b));
            break;

        default:
            throw new UnsupportedOperationException("Invalid intention " + packethandshakinginsetprotocol.a());
        }

        // Paper start - NetworkClient implementation
        this.getNetworkManager().protocolVersion = packethandshakinginsetprotocol.getProtocolVersion();
        this.getNetworkManager().virtualHost = com.destroystokyo.paper.network.PaperNetworkClient.prepareVirtualHost(packethandshakinginsetprotocol.hostname, packethandshakinginsetprotocol.port);
        // Paper end
    }

    public void a(IChatBaseComponent ichatbasecomponent) {}
}
