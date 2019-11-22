package net.minecraft.server;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import javax.annotation.Nullable;

public class TileEntitySign extends TileEntity implements ICommandListener { // CraftBukkit - implements

    public final IChatBaseComponent[] lines = new IChatBaseComponent[]{new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText(""), new ChatComponentText("")};
    private int c = -1;
    private int g = -1;
    private int h = -1;
    public boolean isEditable = true;
    private EntityHuman j;
    private final String[] k = new String[4];
    private EnumColor color;

    // Paper start - Strip invalid unicode from signs on load
    private static final boolean keepInvalidUnicode = Boolean.getBoolean("Paper.keepInvalidUnicode"); // Allow people to keep their bad unicode if they really want it
    private boolean privateUnicodeRemoved = false;
    public java.util.UUID signEditor;
    private static final boolean CONVERT_LEGACY_SIGNS = Boolean.getBoolean("convertLegacySigns");
    // Paper end

    public TileEntitySign() {
        super(TileEntityTypes.SIGN);
        this.color = EnumColor.BLACK;
    }

    @Override
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

        nbttagcompound.setString("Color", this.color.b());
        // Paper start - Only remove private area unicode once // TODO - this doesn't need to run for every sign, check data ver
        if (this.privateUnicodeRemoved) {
            nbttagcompound.setBoolean("Paper.RemovedPrivateUnicode", true);
        }
        // Paper end

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        this.isEditable = false;
        super.load(nbttagcompound);
        this.color = EnumColor.a(nbttagcompound.getString("Color"), EnumColor.BLACK);

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
                //IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s.isEmpty() ? "\"\"" : s); // Paper - move down - the old format might throw a json error

                if (oldSign && !isLoadingStructure) { // Paper - saved structures will be in the new format, but will not have isConverted
                    lines[i] = org.bukkit.craftbukkit.util.CraftChatMessage.fromString(s)[0];
                    continue;
                }
                // CraftBukkit end
                IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(s.isEmpty() ? "\"\"" : s); // Paper - after old sign

                if (this.world instanceof WorldServer) {
                    try {
                        this.lines[i] = ChatComponentUtils.filterForDisplay(this.a((EntityPlayer) null), ichatbasecomponent, (Entity) null, 0);
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        this.lines[i] = ichatbasecomponent;
                    }
                } else {
                    this.lines[i] = ichatbasecomponent;
                }
            } catch (com.google.gson.JsonParseException jsonparseexception) {
                this.lines[i] = new ChatComponentText(s);
            }

            this.k[i] = null;
        }

        if (ranUnicodeRemoval) this.privateUnicodeRemoved = true; // Paper - Flag to write NBT
    }

    public void a(int i, IChatBaseComponent ichatbasecomponent) {
        this.lines[i] = ichatbasecomponent;
        this.k[i] = null;
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 9, this.b());
    }

    @Override
    public NBTTagCompound b() {
        return this.save(new NBTTagCompound());
    }

    @Override
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

    public EntityHuman f() {
        return this.j;
    }

    public boolean b(EntityHuman entityhuman) {
        IChatBaseComponent[] aichatbasecomponent = this.lines;
        int i = aichatbasecomponent.length;

        for (int j = 0; j < i; ++j) {
            IChatBaseComponent ichatbasecomponent = aichatbasecomponent[j];
            ChatModifier chatmodifier = ichatbasecomponent == null ? null : ichatbasecomponent.getChatModifier();

            if (chatmodifier != null && chatmodifier.getClickEvent() != null) {
                ChatClickable chatclickable = chatmodifier.getClickEvent();

                if (chatclickable.a() == ChatClickable.EnumClickAction.RUN_COMMAND) {
                    entityhuman.getMinecraftServer().getCommandDispatcher().a(this.a((EntityPlayer) entityhuman), chatclickable.b());
                }
            }
        }

        return true;
    }

    // CraftBukkit start
    @Override
    public void sendMessage(IChatBaseComponent ichatbasecomponent) {}

    @Override
    public org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
        return wrapper.getEntity() != null ? wrapper.getEntity().getBukkitSender(wrapper) : new org.bukkit.craftbukkit.command.CraftBlockCommandSender(wrapper, this);
    }

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
    // CraftBukkit end

    public CommandListenerWrapper a(@Nullable EntityPlayer entityplayer) {
        String s = entityplayer == null ? "Sign" : entityplayer.getDisplayName().getString();
        Object object = entityplayer == null ? new ChatComponentText("Sign") : entityplayer.getScoreboardDisplayName();

        // CraftBukkit - this
        return new CommandListenerWrapper(this, new Vec3D((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D), Vec2F.a, (WorldServer) this.world, 2, s, (IChatBaseComponent) object, this.world.getMinecraftServer(), entityplayer);
    }

    public EnumColor getColor() {
        return this.color;
    }

    public boolean setColor(EnumColor enumcolor) {
        if (enumcolor != this.getColor()) {
            this.color = enumcolor;
            this.update();
            if (this.world != null) this.world.notify(this.getPosition(), this.getBlock(), this.getBlock(), 3); // CraftBukkit - skip notify if world is null (SPIGOT-5122)
            return true;
        } else {
            return false;
        }
    }
}
