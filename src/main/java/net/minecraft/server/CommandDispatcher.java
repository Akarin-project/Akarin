package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CommandDispatcher {

    private static final Logger a = LogManager.getLogger();
    private final com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> b = new com.mojang.brigadier.CommandDispatcher();

    public CommandDispatcher(boolean flag) {
        CommandAdvancement.a(this.b);
        CommandExecute.a(this.b);
        CommandBossBar.a(this.b);
        CommandClear.a(this.b);
        CommandClone.a(this.b);
        CommandData.a(this.b);
        CommandDatapack.a(this.b);
        CommandDebug.a(this.b);
        CommandGamemodeDefault.a(this.b);
        CommandDifficulty.a(this.b);
        CommandEffect.a(this.b);
        CommandMe.a(this.b);
        CommandEnchant.a(this.b);
        CommandXp.a(this.b);
        CommandFill.a(this.b);
        CommandFunction.a(this.b);
        CommandGamemode.a(this.b);
        CommandGamerule.a(this.b);
        CommandGive.a(this.b);
        CommandHelp.a(this.b);
        CommandKick.a(this.b);
        CommandKill.a(this.b);
        CommandList.a(this.b);
        CommandLocate.a(this.b);
        CommandTell.a(this.b);
        CommandParticle.a(this.b);
        CommandPlaySound.a(this.b);
        CommandPublish.a(this.b);
        CommandReload.a(this.b);
        CommandRecipe.a(this.b);
        CommandReplaceItem.a(this.b);
        CommandSay.a(this.b);
        CommandScoreboard.a(this.b);
        CommandSeed.a(this.b);
        CommandSetBlock.a(this.b);
        CommandSpawnpoint.a(this.b);
        CommandSetWorldSpawn.a(this.b);
        CommandSpreadPlayers.a(this.b);
        CommandStopSound.a(this.b);
        CommandSummon.a(this.b);
        CommandTag.a(this.b);
        CommandTeam.a(this.b);
        CommandTeleport.a(this.b);
        CommandTellRaw.a(this.b);
        CommandForceload.a(this.b);
        CommandTime.a(this.b);
        CommandTitle.a(this.b);
        CommandTrigger.a(this.b);
        CommandWeather.a(this.b);
        CommandWorldBorder.a(this.b);
        if (flag) {
            CommandBanIp.a(this.b);
            CommandBanList.a(this.b);
            CommandBan.a(this.b);
            CommandDeop.a(this.b);
            CommandOp.a(this.b);
            CommandPardon.a(this.b);
            CommandPardonIP.a(this.b);
            CommandSaveAll.a(this.b);
            CommandSaveOff.a(this.b);
            CommandSaveOn.a(this.b);
            CommandIdleTimeout.a(this.b);
            CommandStop.a(this.b);
            CommandWhitelist.a(this.b);
        }

        this.b.findAmbiguities((commandnode, commandnode1, commandnode2, collection) -> {
            CommandDispatcher.a.warn("Ambiguity between arguments {} and {} with inputs: {}", this.b.getPath(commandnode1), this.b.getPath(commandnode2), collection);
        });
        this.b.setConsumer((commandcontext, flag1, i) -> {
            ((CommandListenerWrapper) commandcontext.getSource()).a(commandcontext, flag1, i);
        });
    }

    public void a(File file) {
        try {
            Files.write((new GsonBuilder()).setPrettyPrinting().create().toJson(ArgumentRegistry.a(this.b, (CommandNode) this.b.getRoot())), file, StandardCharsets.UTF_8);
        } catch (IOException ioexception) {
            CommandDispatcher.a.error("Couldn't write out command tree!", ioexception);
        }

    }

    public int a(CommandListenerWrapper commandlistenerwrapper, String s) {
        StringReader stringreader = new StringReader(s);

        if (stringreader.canRead() && stringreader.peek() == '/') {
            stringreader.skip();
        }

        commandlistenerwrapper.getServer().methodProfiler.enter(s);

        byte b0;

        try {
            byte b1;
            ChatComponentText chatcomponenttext;

            try {
                int i = this.b.execute(stringreader, commandlistenerwrapper);

                return i;
            } catch (CommandException commandexception) {
                commandlistenerwrapper.sendFailureMessage(commandexception.a());
                b1 = 0;
                return b1;
            } catch (CommandSyntaxException commandsyntaxexception) {
                commandlistenerwrapper.sendFailureMessage(ChatComponentUtils.a(commandsyntaxexception.getRawMessage()));
                if (commandsyntaxexception.getInput() != null && commandsyntaxexception.getCursor() >= 0) {
                    int j = Math.min(commandsyntaxexception.getInput().length(), commandsyntaxexception.getCursor());
                    IChatBaseComponent ichatbasecomponent = (new ChatComponentText("")).a(EnumChatFormat.GRAY).a((chatmodifier) -> {
                        chatmodifier.setChatClickable(new ChatClickable(ChatClickable.EnumClickAction.SUGGEST_COMMAND, s));
                    });

                    if (j > 10) {
                        ichatbasecomponent.a("...");
                    }

                    ichatbasecomponent.a(commandsyntaxexception.getInput().substring(Math.max(0, j - 10), j));
                    if (j < commandsyntaxexception.getInput().length()) {
                        IChatBaseComponent ichatbasecomponent1 = (new ChatComponentText(commandsyntaxexception.getInput().substring(j))).a(new EnumChatFormat[]{EnumChatFormat.RED, EnumChatFormat.UNDERLINE});

                        ichatbasecomponent.addSibling(ichatbasecomponent1);
                    }

                    ichatbasecomponent.addSibling((new ChatMessage("command.context.here", new Object[0])).a(new EnumChatFormat[]{EnumChatFormat.RED, EnumChatFormat.ITALIC}));
                    commandlistenerwrapper.sendFailureMessage(ichatbasecomponent);
                }

                b1 = 0;
                return b1;
            } catch (Exception exception) {
                chatcomponenttext = new ChatComponentText;
            }

            chatcomponenttext.<init>(exception.getMessage() == null ? exception.getClass().getName() : exception.getMessage());
            ChatComponentText chatcomponenttext1 = chatcomponenttext;

            if (CommandDispatcher.a.isDebugEnabled()) {
                StackTraceElement[] astacktraceelement = exception.getStackTrace();

                for(int k = 0; k < Math.min(astacktraceelement.length, 3); ++k) {
                    chatcomponenttext1.a("\n\n").a(astacktraceelement[k].getMethodName()).a("\n ").a(astacktraceelement[k].getFileName()).a(":").a(String.valueOf(astacktraceelement[k].getLineNumber()));
                }
            }

            commandlistenerwrapper.sendFailureMessage((new ChatMessage("command.failed", new Object[0])).a((chatmodifier) -> {
                chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_TEXT, chatcomponenttext1));
            }));
            b0 = 0;
        } finally {
            commandlistenerwrapper.getServer().methodProfiler.exit();
        }

        return b0;
    }

    public void a(EntityPlayer entityplayer) {
        Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map = Maps.newHashMap();
        RootCommandNode<ICompletionProvider> rootcommandnode = new RootCommandNode();

        map.put(this.b.getRoot(), rootcommandnode);
        this.a(this.b.getRoot(), rootcommandnode, entityplayer.getCommandListener(), (Map) map);
        entityplayer.playerConnection.sendPacket(new PacketPlayOutCommands(rootcommandnode));
    }

    private void a(CommandNode<CommandListenerWrapper> commandnode, CommandNode<ICompletionProvider> commandnode1, CommandListenerWrapper commandlistenerwrapper, Map<CommandNode<CommandListenerWrapper>, CommandNode<ICompletionProvider>> map) {
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<CommandListenerWrapper> commandnode2 = (CommandNode) iterator.next();

            if (commandnode2.canUse(commandlistenerwrapper)) {
                ArgumentBuilder<ICompletionProvider, ?> argumentbuilder = commandnode2.createBuilder();

                argumentbuilder.requires((icompletionprovider) -> {
                    return true;
                });
                if (argumentbuilder.getCommand() != null) {
                    argumentbuilder.executes((commandcontext) -> {
                        return 0;
                    });
                }

                if (argumentbuilder instanceof RequiredArgumentBuilder) {
                    RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = (RequiredArgumentBuilder) argumentbuilder;

                    if (requiredargumentbuilder.getSuggestionsProvider() != null) {
                        requiredargumentbuilder.suggests(CompletionProviders.b(requiredargumentbuilder.getSuggestionsProvider()));
                    }
                }

                if (argumentbuilder.getRedirect() != null) {
                    argumentbuilder.redirect((CommandNode) map.get(argumentbuilder.getRedirect()));
                }

                CommandNode<ICompletionProvider> commandnode3 = argumentbuilder.build();

                map.put(commandnode2, commandnode3);
                commandnode1.addChild(commandnode3);
                if (!commandnode2.getChildren().isEmpty()) {
                    this.a(commandnode2, commandnode3, commandlistenerwrapper, map);
                }
            }
        }

    }

    public static LiteralArgumentBuilder<CommandListenerWrapper> a(String s) {
        return LiteralArgumentBuilder.literal(s);
    }

    public static <T> RequiredArgumentBuilder<CommandListenerWrapper, T> a(String s, ArgumentType<T> argumenttype) {
        return RequiredArgumentBuilder.argument(s, argumenttype);
    }

    public static Predicate<String> a(CommandDispatcher.a commanddispatcher_a) {
        return (s) -> {
            try {
                commanddispatcher_a.parse(new StringReader(s));
                return true;
            } catch (CommandSyntaxException commandsyntaxexception) {
                return false;
            }
        };
    }

    public com.mojang.brigadier.CommandDispatcher<CommandListenerWrapper> a() {
        return this.b;
    }

    @FunctionalInterface
    public interface a {

        void parse(StringReader stringreader) throws CommandSyntaxException;
    }
}
