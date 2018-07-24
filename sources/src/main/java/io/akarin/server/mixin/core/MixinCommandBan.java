package io.akarin.server.mixin.core;

import java.util.Date;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import com.mojang.authlib.GameProfile;

import io.akarin.api.internal.Akari;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.CommandAbstract;
import net.minecraft.server.CommandBan;
import net.minecraft.server.CommandException;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ExceptionUsage;
import net.minecraft.server.GameProfileBanEntry;
import net.minecraft.server.ICommand;
import net.minecraft.server.ICommandListener;
import net.minecraft.server.MinecraftServer;

@Mixin(value = CommandBan.class, remap = false)
public abstract class MixinCommandBan {
    @Overwrite
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) throws CommandException {
        if (args.length >= 1 && args[0].length() > 1) {
            GameProfile profile = server.getUserCache().getProfile(args[0]);

            if (profile == null) {
                throw new CommandException("commands.ban.failed", new Object[] {args[0]});
            } else {
                // Akarin start - use string
                boolean hasReason = true; // Akarin
                String message = null;
                if (args.length >= 2) {
                    message = "";
                    for (int i = 2; i < args.length; i++) {
                        message = message + args[i];
                    }
                } else {
                    hasReason = false; // Akarin
                    message = Akari.EMPTY_STRING; // Akarin - modify message
                }
                // Akarin end
                
                GameProfileBanEntry entry = new GameProfileBanEntry(profile, (Date) null, sender.getName(), (Date) null, message);
                
                server.getPlayerList().getProfileBans().add(entry);
                EntityPlayer entityplayer = server.getPlayerList().getPlayer(args[0]);
                
                if (entityplayer != null) {
                    entityplayer.playerConnection.disconnect(hasReason ? message : AkarinGlobalConfig.messageBan);
                }
                
                CommandAbstract.a(sender, (ICommand) this, "commands.ban.success", args[0]); // OBFHELPER: notifyCommandListener
            }
        } else {
            throw new ExceptionUsage("commands.ban.usage");
        }
    }
}
