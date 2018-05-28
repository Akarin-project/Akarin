package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import io.netty.channel.Channel;
import net.minecraft.server.ITickable;
import net.minecraft.server.MCUtil;
import net.minecraft.server.NetworkManager;
import net.minecraft.server.PacketListener;

@Mixin(value = NetworkManager.class, remap = false)
public class MixinNetworkManager {
    @Shadow private boolean m() { return false; }
    @Shadow private PacketListener m;
    @Shadow public Channel channel;
    @Shadow private static boolean enableExplicitFlush;
    
    @Overwrite
    public void a() {
        MCUtil.scheduleAsyncTask(() -> m());
        if (this.m instanceof ITickable) {
            ((ITickable) this.m).e();
        }

        if (this.channel != null) {
            if (enableExplicitFlush) this.channel.eventLoop().execute(() -> this.channel.flush()); // Paper - we don't need to explicit flush here, but allow opt in incase issues are found to a better version
        }
    }
}
