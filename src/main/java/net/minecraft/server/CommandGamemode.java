package net.minecraft.server;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class CommandGamemode {

    public static void a(com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> com_mojang_brigadier_commanddispatcher) {
        LiteralArgumentBuilder<CommandListenerWrapper> literalargumentbuilder = (LiteralArgumentBuilder) CommandDispatcher.a("gamemode").requires((commandlistenerwrapper) -> {
            return commandlistenerwrapper.hasPermission(2);
        });
        EnumGamemode[] aenumgamemode = EnumGamemode.values();
        int i = aenumgamemode.length;

        for (int j = 0; j < i; ++j) {
            EnumGamemode enumgamemode = aenumgamemode[j];

            if (enumgamemode != EnumGamemode.NOT_SET) {
                literalargumentbuilder.then(((LiteralArgumentBuilder) CommandDispatcher.a(enumgamemode.b()).executes((commandcontext) -> {
                    return a(commandcontext, (Collection) Collections.singleton(((CommandListenerWrapper) commandcontext.getSource()).h()), enumgamemode);
                })).then(CommandDispatcher.a("target", (ArgumentType) ArgumentEntity.d()).executes((commandcontext) -> {
                    return a(commandcontext, ArgumentEntity.f(commandcontext, "target"), enumgamemode);
                })));
            }
        }

        com_mojang_brigadier_commanddispatcher.register(literalargumentbuilder);
    }

    private static void a(CommandListenerWrapper commandlistenerwrapper, EntityPlayer entityplayer, EnumGamemode enumgamemode) {
        ChatMessage chatmessage = new ChatMessage("gameMode." + enumgamemode.b(), new Object[0]);

        if (commandlistenerwrapper.getEntity() == entityplayer) {
            commandlistenerwrapper.sendMessage(new ChatMessage("commands.gamemode.success.self", new Object[] { chatmessage}), true);
        } else {
            if (commandlistenerwrapper.getWorld().getGameRules().getBoolean("sendCommandFeedback")) {
                entityplayer.sendMessage(new ChatMessage("gameMode.changed", new Object[] { chatmessage}));
            }

            commandlistenerwrapper.sendMessage(new ChatMessage("commands.gamemode.success.other", new Object[] { entityplayer.getScoreboardDisplayName(), chatmessage}), true);
        }

    }

    private static int a(CommandContext<CommandListenerWrapper> commandcontext, Collection<EntityPlayer> collection, EnumGamemode enumgamemode) {
        int i = 0;
        Iterator iterator = collection.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            if (entityplayer.playerInteractManager.getGameMode() != enumgamemode) {
                entityplayer.a(enumgamemode);
                a((CommandListenerWrapper) commandcontext.getSource(), entityplayer, enumgamemode);
                ++i;
            }
        }

        return i;
    }
}
