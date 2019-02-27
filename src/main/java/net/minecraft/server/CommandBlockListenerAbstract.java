package net.minecraft.server;

import com.mojang.brigadier.context.CommandContext;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;

public abstract class CommandBlockListenerAbstract implements ICommandListener {

    private static final SimpleDateFormat a = new SimpleDateFormat("HH:mm:ss");
    private long b = -1L;
    private boolean c = true;
    private int d;
    private boolean e = true;
    private IChatBaseComponent f;
    private String g = "";
    private IChatBaseComponent h = new ChatComponentText("@");
    // CraftBukkit start
    @Override
    public abstract CommandSender getBukkitSender(CommandListenerWrapper wrapper);
    // CraftBukkit end

    public CommandBlockListenerAbstract() {}

    public int i() {
        return this.d;
    }

    public void a(int i) {
        this.d = i;
    }

    public IChatBaseComponent j() {
        return (IChatBaseComponent) (this.f == null ? new ChatComponentText("") : this.f);
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setString("Command", this.g);
        nbttagcompound.setInt("SuccessCount", this.d);
        nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.h));
        nbttagcompound.setBoolean("TrackOutput", this.e);
        if (this.f != null && this.e) {
            nbttagcompound.setString("LastOutput", IChatBaseComponent.ChatSerializer.a(this.f));
        }

        nbttagcompound.setBoolean("UpdateLastExecution", this.c);
        if (this.c && this.b > 0L) {
            nbttagcompound.setLong("LastExecution", this.b);
        }

        return nbttagcompound;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.g = nbttagcompound.getString("Command");
        this.d = nbttagcompound.getInt("SuccessCount");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.h = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

        if (nbttagcompound.hasKeyOfType("TrackOutput", 1)) {
            this.e = nbttagcompound.getBoolean("TrackOutput");
        }

        if (nbttagcompound.hasKeyOfType("LastOutput", 8) && this.e) {
            try {
                this.f = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("LastOutput"));
            } catch (Throwable throwable) {
                this.f = new ChatComponentText(throwable.getMessage());
            }
        } else {
            this.f = null;
        }

        if (nbttagcompound.hasKey("UpdateLastExecution")) {
            this.c = nbttagcompound.getBoolean("UpdateLastExecution");
        }

        if (this.c && nbttagcompound.hasKey("LastExecution")) {
            this.b = nbttagcompound.getLong("LastExecution");
        } else {
            this.b = -1L;
        }

    }

    public void setCommand(String s) {
        this.g = s;
        this.d = 0;
    }

    public String getCommand() {
        return this.g;
    }

    public boolean a(World world) {
        if (!world.isClientSide && world.getTime() != this.b) {
            if ("Searge".equalsIgnoreCase(this.g)) {
                this.f = new ChatComponentText("#itzlipofutzli");
                this.d = 1;
                return true;
            } else {
                this.d = 0;
                MinecraftServer minecraftserver = this.d().getMinecraftServer();

                if (minecraftserver != null && minecraftserver.D() && minecraftserver.getEnableCommandBlock() && !UtilColor.b(this.g)) {
                    try {
                        this.f = null;
                        this.d = minecraftserver.getCommandDispatcher().dispatchServerCommand(this.getWrapper(), this.g); // CraftBukkit
                    } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.a(throwable, "Executing command block");
                        CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Command to be executed");

                        crashreportsystemdetails.a("Command", this::getCommand);
                        crashreportsystemdetails.a("Name", () -> {
                            return this.getName().getString();
                        });
                        throw new ReportedException(crashreport);
                    }
                }

                if (this.c) {
                    this.b = world.getTime();
                } else {
                    this.b = -1L;
                }

                return true;
            }
        } else {
            return false;
        }
    }

    public IChatBaseComponent getName() {
        return this.h;
    }

    public void setName(IChatBaseComponent ichatbasecomponent) {
        // CraftBukkit start
        if (ichatbasecomponent == null) {
            ichatbasecomponent = new ChatComponentText("@");
        }
        // CraftBukkit end
        this.h = ichatbasecomponent;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {
        if (this.e) {
            this.f = (new ChatComponentText("[" + CommandBlockListenerAbstract.a.format(new Date()) + "] ")).addSibling(ichatbasecomponent);
            this.e();
        }

    }

    public abstract WorldServer d();

    public abstract void e();

    public void c(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.f = ichatbasecomponent;
    }

    public void a(boolean flag) {
        this.e = flag;
    }

    public boolean a(EntityHuman entityhuman) {
        if (!entityhuman.isCreativeAndOp()) {
            return false;
        } else {
            if (entityhuman.getWorld().isClientSide) {
                entityhuman.a(this);
            }

            return true;
        }
    }

    public abstract CommandListenerWrapper getWrapper();

    public boolean a() {
        return this.d().getGameRules().getBoolean("sendCommandFeedback") && this.e;
    }

    public boolean b() {
        return this.e;
    }

    public boolean B_() {
        return this.d().getGameRules().getBoolean("commandBlockOutput");
    }
}
