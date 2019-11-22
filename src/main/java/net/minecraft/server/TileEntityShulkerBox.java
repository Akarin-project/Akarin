package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class TileEntityShulkerBox extends TileEntityLootable implements IWorldInventory, ITickable {

    private static final int[] a = IntStream.range(0, 27).toArray();
    private NonNullList<ItemStack> contents;
    private int c;
    private TileEntityShulkerBox.AnimationPhase i;
    private float j;
    private float k;
    private EnumColor l;
    private boolean m;

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public List<ItemStack> getContents() {
        return this.contents;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    @Override
    public int getMaxStackSize() {
        return maxStack;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end

    public TileEntityShulkerBox(@Nullable EnumColor enumcolor) {
        super(TileEntityTypes.SHULKER_BOX);
        this.contents = NonNullList.a(27, ItemStack.a);
        this.i = TileEntityShulkerBox.AnimationPhase.CLOSED;
        this.l = enumcolor;
    }

    public TileEntityShulkerBox() {
        this((EnumColor) null);
        this.m = true;
    }

    @Override
    public void tick() {
        this.h();
        if (this.i == TileEntityShulkerBox.AnimationPhase.OPENING || this.i == TileEntityShulkerBox.AnimationPhase.CLOSING) {
            this.u();
        }

    }

    protected void h() {
        this.k = this.j;
        switch (this.i) {
            case CLOSED:
                this.j = 0.0F;
                break;
            case OPENING:
                this.j += 0.1F;
                if (this.j >= 1.0F) {
                    this.u();
                    this.i = TileEntityShulkerBox.AnimationPhase.OPENED;
                    this.j = 1.0F;
                    this.v();
                }
                break;
            case CLOSING:
                this.j -= 0.1F;
                if (this.j <= 0.0F) {
                    this.i = TileEntityShulkerBox.AnimationPhase.CLOSED;
                    this.j = 0.0F;
                    this.v();
                }
                break;
            case OPENED:
                this.j = 1.0F;
        }

    }

    public TileEntityShulkerBox.AnimationPhase s() {
        return this.i;
    }

    public AxisAlignedBB a(IBlockData iblockdata) {
        return this.b((EnumDirection) iblockdata.get(BlockShulkerBox.a));
    }

    public AxisAlignedBB b(EnumDirection enumdirection) {
        float f = this.a(1.0F);

        return VoxelShapes.b().getBoundingBox().b((double) (0.5F * f * (float) enumdirection.getAdjacentX()), (double) (0.5F * f * (float) enumdirection.getAdjacentY()), (double) (0.5F * f * (float) enumdirection.getAdjacentZ()));
    }

    private AxisAlignedBB c(EnumDirection enumdirection) {
        EnumDirection enumdirection1 = enumdirection.opposite();

        return this.b(enumdirection).a((double) enumdirection1.getAdjacentX(), (double) enumdirection1.getAdjacentY(), (double) enumdirection1.getAdjacentZ());
    }

    private void u() {
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

                        entity.move(EnumMoveType.SHULKER_BOX, new Vec3D(d0 * (double) enumdirection.getAdjacentX(), d1 * (double) enumdirection.getAdjacentY(), d2 * (double) enumdirection.getAdjacentZ()));
                    }
                }

            }
        }
    }

    @Override
    public int getSize() {
        return this.contents.size();
    }

    @Override
    public boolean setProperty(int i, int j) {
        if (i == 1) {
            this.c = j;
            if (j == 0) {
                this.i = TileEntityShulkerBox.AnimationPhase.CLOSING;
                this.v();
            }

            if (j == 1) {
                this.i = TileEntityShulkerBox.AnimationPhase.OPENING;
                this.v();
            }

            return true;
        } else {
            return super.setProperty(i, j);
        }
    }

    private void v() {
        this.getBlock().a(this.getWorld(), this.getPosition(), 3);
    }

    @Override
    public void startOpen(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            if (this.c < 0) {
                this.c = 0;
            }

            ++this.c;
            this.world.playBlockAction(this.position, this.getBlock().getBlock(), 1, this.c);
            if (this.c == 1) {
                this.world.playSound((EntityHuman) null, this.position, SoundEffects.BLOCK_SHULKER_BOX_OPEN, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    @Override
    public void closeContainer(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            --this.c;
            this.world.playBlockAction(this.position, this.getBlock().getBlock(), 1, this.c);
            if (this.c <= 0) {
                this.world.playSound((EntityHuman) null, this.position, SoundEffects.BLOCK_SHULKER_BOX_CLOSE, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
            }
        }

    }

    @Override
    protected IChatBaseComponent getContainerName() {
        return new ChatMessage("container.shulkerBox", new Object[0]);
    }

    @Override
    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.f(nbttagcompound);
    }

    @Override
    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        return this.g(nbttagcompound);
    }

    public void f(NBTTagCompound nbttagcompound) {
        this.contents = NonNullList.a(this.getSize(), ItemStack.a);
        if (!this.d(nbttagcompound) && nbttagcompound.hasKeyOfType("Items", 9)) {
            ContainerUtil.b(nbttagcompound, this.contents);
        }

    }

    public NBTTagCompound g(NBTTagCompound nbttagcompound) {
        if (!this.e(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.contents, false);
        }

        return nbttagcompound;
    }

    @Override
    protected NonNullList<ItemStack> f() {
        return this.contents;
    }

    @Override
    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.contents = nonnulllist;
    }

    @Override
    public boolean isNotEmpty() {
        Iterator iterator = this.contents.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    @Override
    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return TileEntityShulkerBox.a;
    }

    @Override
    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return !(Block.asBlock(itemstack.getItem()) instanceof BlockShulkerBox);
    }

    @Override
    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return true;
    }

    public float a(float f) {
        return MathHelper.g(f, this.k, this.j);
    }

    @Override
    protected Container createContainer(int i, PlayerInventory playerinventory) {
        return new ContainerShulkerBox(i, playerinventory, this);
    }

    public static enum AnimationPhase {

        CLOSED, OPENING, OPENED, CLOSING;

        private AnimationPhase() {}
    }
}
