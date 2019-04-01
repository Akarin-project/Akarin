package net.minecraft.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import javax.annotation.Nullable;

public class CommandBan {

    private static final SimpleCommandExceptionType a = new SimpleCommandExceptionType(new ChatMessage("commands.ban.failed", new Object[0]));

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        com_mojang_brigadier_commanddispatcher.register((LiteralArgumentBuilder) ((LiteralArgumentBuilder) CommandDispatcher.a("ban").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.getServer().getPlayerList().getProfileBans().isEnabled() && commandlistenerwrapper.hasPermission(3);
        })).then(((RequiredArgumentBuilder) CommandDispatcher.a("targets", (ArgumentType) ArgumentProfile.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.a(commandcontext, "targets"), (IChatBaseComponent) null);
        })).then(CommandDispatcher.a("reason", (ArgumentType) ArgumentChat.a()).executes((commandcontext) -> {
            return a((CommandListenerWrapper) commandcontext.getSource(), ArgumentProfile.a(commandcontext, "targets"), ArgumentChat.a(commandcontext, "reason"));
        }))));
    }

    private static int a(CommandListenerWrapper commandlistenerwrapper, Collection<GameProfile> collection, @Nullable IChatBaseComponent ichatbasecomponent) throws CommandSyntaxException {
        GameProfileBanList gameprofilebanlist = commandlistenerwrapper.getServer().getPlayerList().getProfileBans();
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            GameProfile gameprofile = (GameProfile) iterator.next();

            if (!gameprofilebanlist.isBanned(gameprofile)) {
                GameProfileBanEntry gameprofilebanentry = new GameProfileBanEntry(gameprofile, (Date) null, commandlistenerwrapper.getName(), (Date) null, ichatbasecomponent == null ? null : ichatbasecomponent.getString());

                gameprofilebanlist.add(gameprofilebanentry);
                ++i;
                commandlistenerwrapper.sendMessage(new ChatMessage("commands.ban.success", new Object[] { ChatComponentUtils.a(gameprofile), gameprofilebanentry.getReason()}), true);
                EntityPlayer entityplayer = commandlistenerwrapper.getServer().getPlayerList().getPlayer(gameprofile.getName()); // Akarin

                if (entityplayer != null) {
                    entityplayer.playerConnection.disconnect(new ChatMessage("multiplayer.disconnect.banned", new Object[0]));
                }
            }
        }

        if (i == 0) {
            throw CommandBan.a.create();
        } else {
            return i;
        }
    }
}
