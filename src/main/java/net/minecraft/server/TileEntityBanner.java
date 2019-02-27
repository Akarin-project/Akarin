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

    public void a(ItemStack itemstack, EnumColor enumcolor) {
        this.patterns = null;
        NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Patterns", 9)) {
            this.patterns = nbttagcompound.getList("Patterns", 10).clone();
        }

        this.color = enumcolor;
        this.h = null;
        this.i = null;
        this.j = "";
        this.g = true;
        this.a = itemstack.hasName() ? itemstack.getName() : null;
    }

    public IChatBaseComponent getDisplayName() {
        return (IChatBaseComponent) (this.a != null ? this.a : new ChatMessage("block.minecraft.banner", new Object[0]));
    }

    public boolean hasCustomName() {
        return this.a != null;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return this.a;
    }

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

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.a = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

        if (this.hasWorld()) {
            this.color = ((BlockBannerAbstract) this.getBlock().getBlock()).b();
        } else {
            this.color = null;
        }

        this.patterns = nbttagcompound.getList("Patterns", 10);
        this.h = null;
        this.i = null;
        this.j = null;
        this.g = true;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 6, this.aa_());
    }

    public NBTTagCompound aa_() {
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
                    itemstack.c("BlockEntityTag");
                }

            }
        }
    }

    public ItemStack a(IBlockData iblockdata) {
        ItemStack itemstack = new ItemStack(BlockBanner.a(this.a(() -> {
            return iblockdata;
        })));

        if (this.patterns != null && !this.patterns.isEmpty()) {
            itemstack.a("BlockEntityTag").set("Patterns", this.patterns.clone());
        }

        if (this.a != null) {
            itemstack.a(this.a);
        }

        return itemstack;
    }

    public EnumColor a(Supplier<IBlockData> supplier) {
        if (this.color == null) {
            this.color = ((BlockBannerAbstract) ((IBlockData) supplier.get()).getBlock()).b();
        }

        return this.color;
    }
}
