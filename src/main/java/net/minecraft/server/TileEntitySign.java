package net.minecraft.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;

public class TileEntitySign extends TileEntity implements ICommandListener {

    public final IChatBaseComponent[] lines = new IChatBaseComponent[] { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    public int e = -1;
    public boolean isEditable = true;
    private EntityHuman g;
    private final String[] h = new String[4];

    public TileEntitySign() {
        super(TileEntityTypes.SIGN);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.load(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s);

            if (this.world instanceof WorldServer) {
                try {
                    this.lines[i] = ChatComponentUtils.filterForDisplay(this.a((EntityPlayer) null), ichatbasecomponent, (Entity) null);
                } catch (CommandSyntaxException commandsyntaxexception) {
                    this.lines[i] = ichatbasecomponent;
                }
            } else {
                this.lines[i] = ichatbasecomponent;
            }

            this.h[i] = null;
        }

    }

    public void a(int i, IChatBaseComponent ichatbasecomponent) {
        this.lines[i] = ichatbasecomponent;
        this.h[i] = null;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 9, this.aa_());
    }

    public NBTTagCompound aa_() {
        return this.save(new NBTTagCompound());
    }

    public boolean isFilteredNBT() {
        return true;
    }

    public boolean d() {
        return this.isEditable;
    }

    public void a(EntityHuman entityhuman) {
        this.g = entityhuman;
    }

    public EntityHuman e() {
        return this.g;
    }

    public boolean b(EntityHuman entityhuman) {
        IChatBaseComponent[] aichatbasecomponent = this.lines;
        int i = aichatbasecomponent.length;

        for (int j = 0; j < i; ++j) {
            IChatBaseComponent ichatbasecomponent = aichatbasecomponent[j];
            ChatModifier chatmodifier = ichatbasecomponent == null ? null : ichatbasecomponent.getChatModifier();

            if (chatmodifier != null && chatmodifier.h() != null) {
                ChatClickable chatclickable = chatmodifier.h();

                if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                    entityhuman.bK().getCommandDispatcher().a(this.a((EntityPlayer) entityhuman), chatclickable.b());
                }
            }
        }

        return true;
    }

    public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

    public CommandListenerWrapper a(@Nullable EntityPlayer entityplayer) {
        String s = entityplayer == null ? "Sign" : entityplayer.getDisplayName().getString();
        Object object = entityplayer == null ? new ChatComponentText("Sign") : entityplayer.getScoreboardDisplayName();

        return new CommandListenerWrapper(this, new Vec3D((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D), Vec2F.a, (WorldServer) this.world, 2, s, (IChatBaseComponent) object, this.world.getMinecraftServer(), entityplayer);
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public boolean B_() {
        return false;
    }
}
