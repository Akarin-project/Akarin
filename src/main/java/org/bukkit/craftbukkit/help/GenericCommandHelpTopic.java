package org.bukkit.craftbukkit.help;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.apache.commons.lang.StringUtils;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.defaults.VanillaCommand;
import org.bukkit.help.HelpTopic;

public class GenericCommandHelpTopic extends HelpTopic {

    private Command command;

    public GenericCommandHelpTopic(Command command) {
        this.command = command;

        if (command.getLabel().startsWith("/")) {
            name = command.getLabel();
        } else {
            name = "/" + command.getLabel();
        }

        // The short text is the first line of the description
        int i = command.getDescription().indexOf("\n");
        if (i > 1) {
            shortText = command.getDescription().substring(0, i - 1);
        } else {
            shortText = command.getDescription();
        }

        // Build full text
        StringBuffer sb = new StringBuffer();

        sb.append(ChatColor.GOLD);
        sb.append("Description: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getDescription());

        sb.append("\n");

        sb.append(ChatColor.GOLD);
        sb.append("Usage: ");
        sb.append(ChatColor.WHITE);
        sb.append(command.getUsage().replace("<command>", name.substring(1)));

        if (command.getAliases().size() > 0) {
            sb.append("\n");
            sb.append(ChatColor.GOLD);
            sb.append("Aliases: ");
            sb.append(ChatColor.WHITE);
            sb.append(ChatColor.WHITE + StringUtils.join(command.getAliases(), ", "));
        }
        fullText = sb.toString();
    }

    public boolean canSee(CommandSender sender) {
        if (!command.isRegistered() && !(command instanceof VanillaCommand)) {
            // Unregistered commands should not show up in the help (ignore VanillaCommands)
            return false;
        }

        if (sender instanceof ConsoleCommandSender) {
            return true;
        }

        return command.testPermissionSilent(sender);
    }
}
