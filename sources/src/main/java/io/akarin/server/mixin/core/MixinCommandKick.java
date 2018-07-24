package io.akarin.server.mixin.core;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import io.akarin.server.core.AkarinGlobalConfig;
import net.minecraft.server.CommandAbstract;
import net.minecraft.server.CommandException;
import net.minecraft.server.CommandKick;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.ExceptionPlayerNotFound;
import net.minecraft.server.ExceptionUsage;
import net.minecraft.server.ICommand;
import net.minecraft.server.ICommandListener;
import net.minecraft.server.MinecraftServer;

@Mixin(value = CommandKick.class, remap = false)
public abstract class MixinCommandKick {
    @Overwrite
    public void execute(MinecraftServer server, ICommandListener sender, String[] args) throws CommandException {
        if (args.length > 0 && args[0].length() > 1) {
            EntityPlayer target = server.getPlayerList().getPlayer(args[0]);

            if (target == null) {
                throw new ExceptionPlayerNotFound("commands.generic.player.notFound", args[0]);
            } else {
                if (args.length >= 2) {
                    // Akarin start - use string
                    String message = "";
                    for (int i = 2; i < args.length; i++) {
                        message = message + args[i];
                    }
                    target.playerConnection.disconnect(message);
                    CommandAbstract.a(sender, (ICommand) this, "commands.kick.success.reason", target.getName(), message); // OBFHELPER: notifyCommandListener
                    // Akarin end
                } else {
                    target.playerConnection.disconnect(AkarinGlobalConfig.messageKick); // Akarin
                    CommandAbstract.a(sender, (ICommand) this, "commands.kick.success", target.getName()); // OBFHELPER: notifyCommandListener
                }
            }
        } else {
            throw new ExceptionUsage("commands.kick.usage");
        }
    }
}
