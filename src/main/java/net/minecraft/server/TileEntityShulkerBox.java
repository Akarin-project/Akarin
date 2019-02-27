package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

public class TileEntityShulkerBox extends TileEntityLootable implements IWorldInventory, ITickable {

    private static final int[] a = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> e;
    private boolean f;
    private int j;
    private TileEntityShulkerBox.AnimationPhase k;
    private float l;
    private float m;
    private EnumColor n;
    private boolean o;
    private boolean p;

    public TileEntityShulkerBox(@Nullable EnumColor enumcolor) {
        super(TileEntityTypes.SHULKER_BOX);
        this.e = NonNullList.a(27, ItemStack.a);
        this.k = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.n = enumcolor;
    }

    public TileEntityShulkerBox() {
        this((EnumColor) null);
        this.o = true;
    }

    public void tick() {
        this.p();
        if (this.k == TileEntityShulkerBox.AnimationPhase.OPENING || this.k == TileEntityShulkerBox.AnimationPhase.CLOSING) {
            this.H();
        }

    }

    protected void p() {
        this.m = this.l;
        switch (this.k) {
        case CLOSED:
            this.l = 0.0F;
            break;
        case OPENING:
            this.l += 0.1F;
            if (this.l >= 1.0F) {
                this.H();
                this.k = TileEntityShulkerBox.AnimationPhase.OPENED;
                this.l = 1.0F;
            }
            break;
        case CLOSING:
            this.l -= 0.1F;
            if (this.l <= 0.0F) {
                this.k = TileEntityShulkerBox.AnimationPhase.CLOSED;
                this.l = 0.0F;
            }
            break;
        case OPENED:
            this.l = 1.0F;
        }

    }

    public TileEntityShulkerBox.AnimationPhase r() {
        return this.k;
    }

    public AxisAlignedBB a(IBlockData iblockdata) {
        return this.b((EnumDirection) iblockdata.get(BlockShulkerBox.a));
    }

    public AxisAlignedBB b(EnumDirection enumdirection) {
        return VoxelShapes.b().getBoundingBox().b((double) (0.5F * this.a(1.0F) * (float) enumdirection.getAdjacentX()), (double) (0.5F * this.a(1.0F) * (float) enumdirection.getAdjacentY()), (double) (0.5F * this.a(1.0F) * (float) enumdirection.getAdjacentZ()));
    }

    private AxisAlignedBB c(EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.opposite();

        return this.b(enumdirection).a((double) enumdirection1.getAdjacentX(), (double) enumdirection1.getAdjacentY(), (double) enumdirection1.getAdjacentZ());
    }

    private void H() {
        IBlockData iblockdata = this.world.getType(this.getPosition());

        if (iblockdata.getBlock() instanceof BlockShulkerBox) {
            EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockShulkerBox.a);
            AxisAlignedBB axisalignedbb = this.c(enumdirection).a(this.position);
            List<Entity> list = this.world.getEntities((Entity) null, axisalignedbb);

            if (!list.isEmpty()) {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    if (entity.getPushReaction() != EnumPistonReaction.IGNORE) {
                        double d0 = 0.0D;
                        double d1 = 0.0D;
                        double d2 = 0.0D;
                        AxisAlignedBB axisalignedbb1 = entity.getBoundingBox();

                        switch (enumdirection.k()) {
                        case X:
                            if (enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE) {
                                d0 = axisalignedbb.maxX - axisalignedbb1.minX;
                            } else {
                                d0 = axisalignedbb1.maxX - axisalignedbb.minX;
                            }

                            d0 += 0.01D;
                            break;
                        case Y:
                            if (enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE) {
                                d1 = axisalignedbb.maxY - axisalignedbb1.minY;
                            } else {
                                d1 = axisalignedbb1.maxY - axisalignedbb.minY;
                            }

                            d1 += 0.01D;
                            break;
                        case Z:
                            if (enumdirection.c() == EnumDirection.EnumAxisDirection.POSITIVE) {
                                d2 = axisalignedbb.maxZ - axisalignedbb1.minZ;
                            } else {
                                d2 = axisalignedbb1.maxZ - axisalignedbb.minZ;
                            }

                            d2 += 0.01D;
                        }

                        entity.move(EnumMoveType.SHULKER_BOX, d0 * (double) enumdirection.getAdjacentX(), d1 * (double) enumdirection.getAdjacentY(), d2 * (double) enumdirection.getAdjacentZ());
                    }
                }

            }
        }
    }

    public int getSize() {
        return this.e.size();
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.j = j;
            if (j == 0) {
                this.k = TileEntityShulkerBox.AnimationPhase.CLOSING;
            }

            if (j == 1) {
                this.k = TileEntityShulkerBox.AnimationPhase.OPENING;
            }

            return true;
        } else {
            return super.c(i, j);
        }
    }

    public void startOpen(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            if (this.j < 0) {
                this.j = 0;
            }

            ++this.j;
            this.world.playBlockAction(this.position, this.getBlock().getBlock(), 1, this.j);
            if (this.j == 1) {
                this.world.a((EntityHuman) null, this.position, SoundEffects.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public void closeContainer(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            --this.j;
            this.world.playBlockAction(this.position, this.getBlock().getBlock(), 1, this.j);
            if (this.j <= 0) {
                this.world.a((EntityHuman) null, this.position, SoundEffects.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerShulkerBox(playerinventory, this, entityhuman);
    }

    public String getContainerName() {
        return "minecraft:shulker_box";
    }

    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return (IChatBaseComponent) (ichatbasecomponent != null ? ichatbasecomponent : new ChatMessage("container.shulkerBox", new Object[0]));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.f(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        return this.g(nbttagcompound);
    }

    public void f(NBTTagCompound nbttagcompound) {
        this.e = NonNullList.a(this.getSize(), ItemStack.a);
        if (!this.d(nbttagcompound) && nbttagcompound.hasKeyOfType("Items", 9)) {
            ContainerUtil.b(nbttagcompound, this.e);
        }

        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.i = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

    }

    public NBTTagCompound g(NBTTagCompound nbttagcompound) {
        if (!this.e(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.e, false);
        }

        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        if (ichatbasecomponent != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
        }

        if (!nbttagcompound.hasKey("Lock") && this.isLocked()) {
            this.getLock().a(nbttagcompound);
        }

        return nbttagcompound;
    }

    protected NonNullList<ItemStack> q() {
        return this.e;
    }

    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.e = nonnulllist;
    }

    public boolean P_() {
        Iterator iterator = this.e.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return TileEntityShulkerBox.a;
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return !(Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return true;
    }

    public void clear() {
        this.f = true;
        super.clear();
    }

    public boolean s() {
        return this.f;
    }

    public float a(float f) {
        return this.m + (this.l - this.m) * f;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 10, this.aa_());
    }

    public boolean E() {
        return this.p;
    }

    public void a(boolean flag) {
        this.p = flag;
    }

    public boolean G() {
        return !this.E() || !this.P_() || this.hasCustomName() || this.g != null;
    }

    public static enum AnimationPhase {

        CLOSED, OPENING, OPENED, CLOSING;

        private AnimationPhase() {}
    }
}
