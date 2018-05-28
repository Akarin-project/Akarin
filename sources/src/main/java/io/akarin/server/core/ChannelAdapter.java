package io.akarin.server.core;

import java.util.List;

import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import net.minecraft.server.EnumProtocolDirection;
import net.minecraft.server.HandshakeListener;
import net.minecraft.server.LegacyPingHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PacketDecoder;
import net.minecraft.server.PacketEncoder;
import net.minecraft.server.PacketPrepender;
import net.minecraft.server.PacketSplitter;

public class ChannelAdapter extends ChannelInitializer<Channel> {
    private final List<NetworkManager> managers;
    
    public ChannelAdapter(List<NetworkManager> list) {
        managers = list;
    }
    
    public static ChannelAdapter create(List<NetworkManager> managers) {
        return new ChannelAdapter(managers);
    }
    
    @Override
    protected void initChannel(Channel channel) {
        try {
            channel.config().setOption(ChannelOption.TCP_NODELAY, true);
        } catch (ChannelException ex) {
            ;
        }
        channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30))
                          .addLast("legacy_query", new LegacyPingHandler(MinecraftServer.getServer().getServerConnection()))
                          .addLast("splitter", new PacketSplitter()).addLast("decoder", new PacketDecoder(EnumProtocolDirection.SERVERBOUND))
                          .addLast("prepender", new PacketPrepender()).addLast("encoder", new PacketEncoder(EnumProtocolDirection.CLIENTBOUND));
        
        NetworkManager manager = new NetworkManager(EnumProtocolDirection.SERVERBOUND);
        managers.add(manager);
        
        channel.pipeline().addLast("packet_handler", manager);
        manager.setPacketListener(new HandshakeListener(MinecraftServer.getServer(), manager));
    }
}
