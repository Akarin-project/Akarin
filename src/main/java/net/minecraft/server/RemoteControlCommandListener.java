package net.minecraft.server;

public class RemoteControlCommandListener implements ICommandListener {

    private final StringBuffer buffer = new StringBuffer();
    private final MinecraftServer server;

    public RemoteControlCommandListener(MinecraftServer minecraftserver) {
        this.server = minecraftserver;
    }

    public void clearMessages() {
        this.buffer.setLength(0);
    }

    public String getMessages() {
        return this.buffer.toString();
    }

    public CommandListenerWrapper getWrapper() {
        WorldServer worldserver = this.server.getWorldServer(DimensionManager.OVERWORLD);

        return new CommandListenerWrapper(this, new Vec3D(worldserver.getSpawn()), Vec2F.a, worldserver, 4, "Recon", new ChatComponentText("Rcon"), this.server, (Entity) null);
    }

    // CraftBukkit start - Send a String
    public void sendMessage(String message) {
        this.buffer.append(message);
    }

    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return server.remoteConsole;
    }
    // CraftBukkit end

    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.buffer.append(ichatbasecomponent.getString());
    }

    @Override
    public boolean shouldSendSuccess() {
        return true;
    }

    @Override
    public boolean shouldSendFailure() {
        return true;
    }

    @Override
    public boolean shouldBroadcastCommands() {
        return this.server.l();
    }
}
