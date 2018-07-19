package org.bukkit.craftbukkit.command;

import com.google.common.base.Joiner;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.tree.CommandNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.server.CommandDispatcher;
import net.minecraft.server.CommandListenerWrapper;
import net.minecraft.server.DedicatedServer;
import net.minecraft.server.EntityMinecartCommandBlock;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldServer;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.ProxiedCommandSender;
import org.bukkit.command.RemoteConsoleCommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftMinecartCommand;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.minecart.CommandMinecart;

public final class VanillaCommandWrapper extends BukkitCommand {

    private final CommandDispatcher dispatcher;
    public final CommandNode<CommandListenerWrapper> vanillaCommand;

    public VanillaCommandWrapper(CommandDispatcher dispatcher, CommandNode<CommandListenerWrapper> vanillaCommand) {
        super(vanillaCommand.getName(), "A Mojang provided command.", vanillaCommand.getUsageText(), Collections.EMPTY_LIST);
        this.dispatcher = dispatcher;
        this.vanillaCommand = vanillaCommand;
        this.setPermission(getPermission(vanillaCommand));
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!testPermission(sender)) return true;

        CommandListenerWrapper icommandlistener = getListener(sender);
        dispatcher.a(icommandlistener, toDispatcher(args, getName()), toDispatcher(args, commandLabel));
        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        CommandListenerWrapper icommandlistener = getListener(sender);
        ParseResults<CommandListenerWrapper> parsed = dispatcher.a().parse(toDispatcher(args, getName()), icommandlistener);

        List<String> results = new ArrayList<>();
        dispatcher.a().getCompletionSuggestions(parsed).thenAccept((suggestions) -> {
            suggestions.getList().forEach((s) -> results.add(s.getText()));
        });

        return results;
    }

    public static class WorldRescueContext {

        private WorldServer[] prev;

        public WorldRescueContext start(WorldServer def) {
            // Some commands use the worldserver variable but we leave it full of null values,
            // so we must temporarily populate it with the world of the commandsender
            prev = MinecraftServer.getServer().worldServer;
            MinecraftServer server = MinecraftServer.getServer();
            server.worldServer = new WorldServer[server.worlds.size()];
            server.worldServer[0] = def;
            int bpos = 0;
            for (int pos = 1; pos < server.worldServer.length; pos++) {
                WorldServer world = server.worlds.get(bpos++);
                if (server.worldServer[0] == world) {
                    pos--;
                    continue;
                }
                server.worldServer[pos] = world;
            }

            return this;
        }

        public void end() {
            MinecraftServer.getServer().worldServer = prev;
        }
    }

    private CommandListenerWrapper getListener(CommandSender sender) {
        if (sender instanceof Player) {
            return ((CraftPlayer) sender).getHandle().getCommandListener();
        }
        if (sender instanceof BlockCommandSender) {
            return ((CraftBlockCommandSender) sender).getWrapper();
        }
        if (sender instanceof CommandMinecart) {
            return ((EntityMinecartCommandBlock) ((CraftMinecartCommand) sender).getHandle()).getCommandBlock().getWrapper();
        }
        if (sender instanceof RemoteConsoleCommandSender) {
            return ((DedicatedServer) MinecraftServer.getServer()).remoteControlCommandListener.f();
        }
        if (sender instanceof ConsoleCommandSender) {
            return ((CraftServer) sender.getServer()).getServer().getServerCommandListener();
        }
        if (sender instanceof ProxiedCommandSender) {
            return ((ProxiedNativeCommandSender) sender).getHandle();
        }

        throw new IllegalArgumentException("Cannot make " + sender + " a vanilla command listener");
    }

    public static String getPermission(CommandNode<CommandListenerWrapper> vanillaCommand) {
        return "minecraft.command." + ((vanillaCommand.getRedirect() == null) ? vanillaCommand.getName() : vanillaCommand.getRedirect().getName());
    }

    private String toDispatcher(String[] args, String name) {
        return "/" + name + ((args.length > 0) ? " " + Joiner.on(' ').join(args) : "");
    }
}
