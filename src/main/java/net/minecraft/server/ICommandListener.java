package net.minecraft.server;

public interface ICommandListener {

    ICommandListener DUMMY = new ICommandListener() {
        @Override
        public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

        @Override
        public boolean shouldSendSuccess() {
            return false;
        }

        @Override
        public boolean shouldSendFailure() {
            return false;
        }

        @Override
        public boolean shouldBroadcastCommands() {
            return false;
        }

        // CraftBukkit start
        @Override
        public org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        // CraftBukkit end
    };

    void sendMessage(IChatBaseComponent ichatbasecomponent);

    boolean shouldSendSuccess();

    boolean shouldSendFailure();

    boolean shouldBroadcastCommands();

    org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper); // CraftBukkit
}
