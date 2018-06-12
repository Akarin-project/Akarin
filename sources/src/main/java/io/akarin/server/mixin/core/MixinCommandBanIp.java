package io.akarin.server.mixin.core;

import java.util.Date;
import java.util.List;
import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.CommandAbstract;
import net.minecraft.server.CommandBanIp;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ICommand;
import net.minecraft.server.ICommandListener;
import net.minecraft.server.IpBanEntry;
import net.minecraft.server.MinecraftServer;

@Mixin(value = CommandBanIp.class, remap = false)
public class MixinCommandBanIp {
    @Overwrite // PAIL: banIp
    protected void a(MinecraftServer server, ICommandListener sender, String args, @Nullable String banReason) {
        if (banReason == null) banReason = AkarinGlobalConfig.messageBanIp; // Akarin - modify message
        IpBanEntry ipbanentry = new IpBanEntry(args, (Date) null, sender.getName(), (Date) null, banReason);

        server.getPlayerList().getIPBans().add(ipbanentry);
        List<EntityPlayer> withIpPlayers = server.getPlayerList().b(args); // PAIL: getPlayersMatchingAddress
        String[] banPlayerNames = new String[withIpPlayers.size()];
        
        for (int i = 0; i < banPlayerNames.length; i++) {
            EntityPlayer each = withIpPlayers.get(i);
            banPlayerNames[i] = each.getName();
            each.playerConnection.disconnect(banReason);
        }
        
        if (withIpPlayers.isEmpty()) {
            CommandAbstract.a(sender, (ICommand) this, "commands.banip.success", args); // PAIL: notifyCommandListener
        } else {
            CommandAbstract.a(sender, (ICommand) this, "commands.banip.success.players", args, CommandAbstract.a(banPlayerNames)); // PAIL: notifyCommandListener - joinNiceString
        }
    }
}
