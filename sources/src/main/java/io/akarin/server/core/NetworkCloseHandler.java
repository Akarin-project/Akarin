package io.akarin.server.core;

import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.server.ChatComponentText;
import net.minecraft.server.NetworkManager;

public class NetworkCloseHandler implements GenericFutureListener<Future<? super Void>> {
    private final NetworkManager manager;
    private final ChatComponentText message;
    
    public NetworkCloseHandler(NetworkManager instance, ChatComponentText text) {
        manager = instance;
        message = text;
    }
    
    @Override
    public void operationComplete(Future<? super Void> future) throws Exception {
        manager.close(message);
    }
}
