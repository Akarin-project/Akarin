package net.minecraft.server;

public interface ICommandListener {

    void sendMessage(IChatBaseComponent ichatbasecomponent);

    boolean a();

    boolean b();

    boolean B_();

    org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper); // CraftBukkit
}
