package net.minecraft.server;

import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;

public class TileEntityBanner extends TileEntity implements INamableTileEntity {

    private IChatBaseComponent a;
    public EnumColor color;
    public NBTTagList patterns;
    private boolean g;
    private List<EnumBannerPatternType> h;
    private List<EnumColor> i;
    private String j;

    public TileEntityBanner() {
        super(TileEntityTypes.BANNER);
        this.color = EnumColor.WHITE;
    }

    public TileEntityBanner(EnumColor enumcolor) {
        this();
        this.color = enumcolor;
    }

    @Override
    public IChatBaseComponent getDisplayName() {
        return (IChatBaseComponent) (this.a != null ? this.a : new ChatMessage("block.minecraft.banner", new Object[0]));
    }

    @Nullable
    @Override
    public IChatBaseComponent getCustomName() {
        return this.a;
    }

    public void a(IChatBaseComponent ichatbasecomponent) {
        this.a = ichatbasecomponent;
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.patterns != null) {
            nbttagcompound.set("Patterns", this.patterns);
        }

        if (this.a != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.a));
        }

        return nbttagcompound;
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.a = MCUtil.getBaseComponentFromNbt("CustomName", nbttagcompound); // Paper - Catch ParseException
        }

        if (this.hasWorld()) {
            this.color = ((BlockBannerAbstract) this.getBlock().getBlock()).getColor();
        } else {
            this.color = null;
        }

        this.patterns = nbttagcompound.getList("Patterns", 10);
        // CraftBukkit start
        while (this.patterns.size() > 20) {
            this.patterns.remove(20);
        }
        // CraftBukkit end
        this.h = null;
        this.i = null;
        this.j = null;
        this.g = true;
    }

    @Nullable
    @Override
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 6, this.b());
    }

    @Override
    public NBTTagCompound b() {
        return this.save(new NBTTagCompound());
    }

    public static int a(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        return nbttagcompound != null && nbttagcompound.hasKey("Patterns") ? nbttagcompound.getList("Patterns", 10).size() : 0;
    }

    public static void b(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Patterns", 10);

            if (!nbttaglist.isEmpty()) {
                nbttaglist.remove(nbttaglist.size() - 1);
                if (nbttaglist.isEmpty()) {
                    itemstack.removeTag("BlockEntityTag");
                }

            }
        }
    }

    public EnumColor a(Supplier<IBlockData> supplier) {
        if (this.color == null) {
            this.color = ((BlockBannerAbstract) ((IBlockData) supplier.get()).getBlock()).getColor();
        }

        return this.color;
    }
}
