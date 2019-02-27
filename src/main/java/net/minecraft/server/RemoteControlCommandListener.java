package net.minecraft.server;

public class RemoteControlCommandListener implements ICommandListener {

    private final StringBuffer a = new StringBuffer();
    private final MinecraftServer b;

    public RemoteControlCommandListener(MinecraftServer minecraftserver) {
        this.b = minecraftserver;
    }

    public void clearMessages() {
        this.a.setLength(0);
    }

    public String getMessages() {
        return this.a.toString();
    }

    public CommandListenerWrapper f() {
        WorldServer worldserver = this.b.getWorldServer(DimensionManager.OVERWORLD);

        return new CommandListenerWrapper(this, new Vec3D(worldserver.getSpawn()), Vec2F.a, worldserver, 4, "Recon", new ChatComponentText("Rcon"), this.b, (Entity) null);
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        this.a.append(ichatbasecomponent.getString());
    }

    public boolean a() {
        return true;
    }

    public boolean b() {
        return true;
    }

    public boolean B_() {
        return this.b.k();
    }
}
