package net.minecraft.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;

public class TileEntitySign extends TileEntity implements ICommandListener {

    public final IChatBaseComponent[] lines = new IChatBaseComponent[] { new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    public int e = -1;
    public boolean isEditable = true;
    private EntityHuman g;
    private final String[] h = new String[4];

    // Paper start - Strip invalid unicode from signs on load
    private static final boolean keepInvalidUnicode = Boolean.getBoolean("Paper.keepInvalidUnicode"); // Allow people to keep their bad unicode if they really want it
    private boolean privateUnicodeRemoved = false;
    public java.util.UUID signEditor;
    private static final boolean CONVERT_LEGACY_SIGNS = Boolean.getBoolean("convertLegacySigns");
    // Paper end

    public TileEntitySign() {
        super(TileEntityTypes.SIGN);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);

        for (int i = 0; i < 4; ++i) {
            String s = IChatBaseComponent.ChatSerializer.a(this.lines[i]);

            nbttagcompound.setString("Text" + (i + 1), s);
        }

        // CraftBukkit start
        if (CONVERT_LEGACY_SIGNS) { // Paper
            nbttagcompound.setBoolean("Bukkit.isConverted", true);
        }
        // CraftBukkit end

        // Paper start - Only remove private area unicode once
        if (this.privateUnicodeRemoved) {
            nbttagcompound.setBoolean("Paper.RemovedPrivateUnicode", true);
        }
        // Paper end

        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.load(nbttagcompound);

        // Paper start - Keep track, only do it once per sign
        this.privateUnicodeRemoved = nbttagcompound.getBoolean("Paper.RemovedPrivateUnicode");
        boolean ranUnicodeRemoval = false;
        // Paper end

        // CraftBukkit start - Add an option to convert signs correctly
        // This is done with a flag instead of all the time because
        // we have no way to tell whether a sign is from 1.7.10 or 1.8

        boolean oldSign = Boolean.getBoolean("convertLegacySigns") && !nbttagcompound.getBoolean("Bukkit.isConverted");

        for (int i = 0; i < 4; ++i) {
            String s = nbttagcompound.getString("Text" + (i + 1));
            if (s != null && s.length() > 2048) {
                s = "\"\"";
            }

            // Paper start - Strip private use area unicode from signs
            if (s != null && !keepInvalidUnicode && !this.privateUnicodeRemoved) {
                StringBuilder builder = new StringBuilder();
                for (char character : s.toCharArray()) {
                    if (Character.UnicodeBlock.of(character) != Character.UnicodeBlock.PRIVATE_USE_AREA) {
                        builder.append(character);
                    }
                }
                s = builder.toString();
                ranUnicodeRemoval = true;
            }
            // Paper end

            try {
                //IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s); // Paper - move down - the old format might throw a json error

                if (oldSign && !isLoadingStructure) { // Paper - saved structures will be in the new format, but will not have isConverted
                    lines[i] = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(s)[0];
                    continue;
                }
                // CraftBukkit end
                IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s); // Paper - after old sign

                if (this.world instanceof WorldServer) {
                    try {
                        this.lines[i] = ChatComponentUtils.filterForDisplay(this.a((EntityPlayer) null), ichatbasecomponent, (Entity) null);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        this.lines[i] = ichatbasecomponent;
                    }
                } else {
                    this.lines[i] = ichatbasecomponent;
                }
            } catch (com.google.gson.JsonParseException jsonparseexception) {
                this.lines[i] = new ChatComponentText(s);
            }

            this.h[i] = null;
        }

        if (ranUnicodeRemoval) this.privateUnicodeRemoved = true; // Paper - Flag to write NBT
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
        // Paper start
        //this.g = entityhuman;
        signEditor = entityhuman != null ? entityhuman.getUniqueID() : null;
        // Paper end
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

    // CraftBukkit start
    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return wrapper.getEntity() != null ? wrapper.getEntity().getBukkitSender(wrapper) : new org.bukkit.craftbukkit.command.CraftBlockCommandSender(wrapper, this);
    }
    // CraftBukkit end

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
