package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;

public class PacketPlayOutCommands implements Packet<PacketListenerPlayOut> {

    private RootCommandNode<ICompletionProvider> a;

    public PacketPlayOutCommands() {}

    public PacketPlayOutCommands(RootCommandNode<ICompletionProvider> rootcommandnode) {
        this.a = rootcommandnode;
    }

    public void a(PacketDataSerializer packetdataserializer) throws IOException {
        PacketPlayOutCommands.a[] apacketplayoutcommands_a = new PacketPlayOutCommands.a[packetdataserializer.g()];
        Deque<PacketPlayOutCommands.a> deque = new ArrayDeque(apacketplayoutcommands_a.length);

        for (int i = 0; i < apacketplayoutcommands_a.length; ++i) {
            apacketplayoutcommands_a[i] = this.c(packetdataserializer);
            deque.add(apacketplayoutcommands_a[i]);
        }

        boolean flag;

        do {
            if (deque.isEmpty()) {
                this.a = (RootCommandNode) apacketplayoutcommands_a[packetdataserializer.g()].e;
                return;
            }

            flag = false;
            Iterator iterator = deque.iterator();

            while (iterator.hasNext()) {
                PacketPlayOutCommands.a packetplayoutcommands_a = (PacketPlayOutCommands.a) iterator.next();

                if (packetplayoutcommands_a.a(apacketplayoutcommands_a)) {
                    iterator.remove();
                    flag = true;
                }
            }
        } while (flag);

        throw new IllegalStateException("Server sent an impossible command tree");
    }

    public void b(PacketDataSerializer packetdataserializer) throws IOException {
        Map<CommandNode<ICompletionProvider>, Integer> map = Maps.newHashMap();
        Deque<CommandNode<ICompletionProvider>> deque = new ArrayDeque();

        deque.add(this.a);

        while (!deque.isEmpty()) {
            CommandNode<ICompletionProvider> commandnode = (CommandNode) deque.pollFirst();

            if (!map.containsKey(commandnode)) {
                int i = map.size();

                map.put(commandnode, i);
                deque.addAll(commandnode.getChildren());
                if (commandnode.getRedirect() != null) {
                    deque.add(commandnode.getRedirect());
                }
            }
        }

        CommandNode<ICompletionProvider>[] acommandnode = (CommandNode[]) (new CommandNode[map.size()]);

        Entry entry;

        for (Iterator iterator = map.entrySet().iterator(); iterator.hasNext(); acommandnode[(Integer) entry.getValue()] = (CommandNode) entry.getKey()) {
            entry = (Entry) iterator.next();
        }

        packetdataserializer.d(acommandnode.length);
        CommandNode[] acommandnode1 = acommandnode;
        int j = acommandnode.length;

        for (int k = 0; k < j; ++k) {
            CommandNode<ICompletionProvider> commandnode1 = acommandnode1[k];

            this.a(packetdataserializer, commandnode1, map);
        }

        packetdataserializer.d((Integer) map.get(this.a));
    }

    private PacketPlayOutCommands.a c(PacketDataSerializer packetdataserializer) {
        byte b0 = packetdataserializer.readByte();
        int[] aint = packetdataserializer.b();
        int i = (b0 & 8) != 0 ? packetdataserializer.g() : 0;
        ArgumentBuilder<ICompletionProvider, ?> argumentbuilder = this.a(packetdataserializer, b0);

        return new PacketPlayOutCommands.a(argumentbuilder, b0, i, aint);
    }

    @Nullable
    private ArgumentBuilder<ICompletionProvider, ?> a(PacketDataSerializer packetdataserializer, byte b0) {
        int i = b0 & 3;

        if (i == 2) {
            String s = packetdataserializer.e(32767);
            ArgumentType<?> argumenttype = ArgumentRegistry.a(packetdataserializer);

            if (argumenttype == null) {
                return null;
            } else {
                RequiredArgumentBuilder<ICompletionProvider, ?> requiredargumentbuilder = RequiredArgumentBuilder.argument(s, argumenttype);

                if ((b0 & 16) != 0) {
                    requiredargumentbuilder.suggests(CompletionProviders.a(packetdataserializer.l()));
                }

                return requiredargumentbuilder;
            }
        } else {
            return i == 1 ? LiteralArgumentBuilder.literal(packetdataserializer.e(32767)) : null;
        }
    }

    private void a(PacketDataSerializer packetdataserializer, CommandNode<ICompletionProvider> commandnode, Map<CommandNode<ICompletionProvider>, Integer> map) {
        byte b0 = 0;

        if (commandnode.getRedirect() != null) {
            b0 = (byte) (b0 | 8);
        }

        if (commandnode.getCommand() != null) {
            b0 = (byte) (b0 | 4);
        }

        if (commandnode instanceof RootCommandNode) {
            b0 = (byte) (b0 | 0);
        } else if (commandnode instanceof ArgumentCommandNode) {
            b0 = (byte) (b0 | 2);
            if (((ArgumentCommandNode) commandnode).getCustomSuggestions() != null) {
                b0 = (byte) (b0 | 16);
            }
        } else {
            if (!(commandnode instanceof LiteralCommandNode)) {
                throw new UnsupportedOperationException("Unknown node type " + commandnode);
            }

            b0 = (byte) (b0 | 1);
        }

        packetdataserializer.writeByte(b0);
        packetdataserializer.d(commandnode.getChildren().size());
        Iterator iterator = commandnode.getChildren().iterator();

        while (iterator.hasNext()) {
            CommandNode<ICompletionProvider> commandnode1 = (CommandNode) iterator.next();

            packetdataserializer.d((Integer) map.get(commandnode1));
        }

        if (commandnode.getRedirect() != null) {
            packetdataserializer.d((Integer) map.get(commandnode.getRedirect()));
        }

        if (commandnode instanceof ArgumentCommandNode) {
            ArgumentCommandNode<ICompletionProvider, ?> argumentcommandnode = (ArgumentCommandNode) commandnode;

            packetdataserializer.a(argumentcommandnode.getName());
            ArgumentRegistry.a(packetdataserializer, argumentcommandnode.getType());
            if (argumentcommandnode.getCustomSuggestions() != null) {
                packetdataserializer.a(CompletionProviders.a(argumentcommandnode.getCustomSuggestions()));
            }
        } else if (commandnode instanceof LiteralCommandNode) {
            packetdataserializer.a(((LiteralCommandNode) commandnode).getLiteral());
        }

    }

    public void a(PacketListenerPlayOut packetlistenerplayout) {
        packetlistenerplayout.a(this);
    }

    static class a {

        @Nullable
        private final ArgumentBuilder<ICompletionProvider, ?> a;
        private final byte b;
        private final int c;
        private final int[] d;
        private CommandNode<ICompletionProvider> e;

        private a(@Nullable ArgumentBuilder<ICompletionProvider, ?> argumentbuilder, byte b0, int i, int[] aint) {
            this.a = argumentbuilder;
            this.b = b0;
            this.c = i;
            this.d = aint;
        }

        public boolean a(PacketPlayOutCommands.a[] apacketplayoutcommands_a) {
            if (this.e == null) {
                if (this.a == null) {
                    this.e = new RootCommandNode();
                } else {
                    if ((this.b & 8) != 0) {
                        if (apacketplayoutcommands_a[this.c].e == null) {
                            return false;
                        }

                        this.a.redirect(apacketplayoutcommands_a[this.c].e);
                    }

                    if ((this.b & 4) != 0) {
                        this.a.executes((commandcontext) -> {
                            return 0;
                        });
                    }

                    this.e = this.a.build();
                }
            }

            int[] aint = this.d;
            int i = aint.length;

            int j;
            int k;

            for (k = 0; k < i; ++k) {
                j = aint[k];
                if (apacketplayoutcommands_a[j].e == null) {
                    return false;
                }
            }

            aint = this.d;
            i = aint.length;

            for (k = 0; k < i; ++k) {
                j = aint[k];
                CommandNode<ICompletionProvider> commandnode = apacketplayoutcommands_a[j].e;

                if (!(commandnode instanceof RootCommandNode)) {
                    this.e.addChild(commandnode);
                }
            }

            return true;
        }
    }
}
