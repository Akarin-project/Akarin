package net.minecraft.server;

import java.util.Iterator;
import java.util.List;

public class TileEntityChest extends TileEntityLootable implements ITickable {

    private NonNullList<ItemStack> items;
    protected float a;
    protected float e;
    protected int f;
    private int k;

    protected TileEntityChest(TileEntityTypes<?> tileentitytypes) {
        super(tileentitytypes);
        this.items = NonNullList.a(27, ItemStack.a);
    }

    public TileEntityChest() {
        this(TileEntityTypes.CHEST);
    }

    public int getSize() {
        return 27;
    }

    public boolean P_() {
        Iterator iterator = this.items.iterator();

        ItemStack itemstack;

        do {
            if (!iterator.hasNext()) {
                return true;
            }

            itemstack = (ItemStack) iterator.next();
        } while (itemstack.isEmpty());

        return false;
    }

    public IChatBaseComponent getDisplayName() {
        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        return (IChatBaseComponent) (ichatbasecomponent != null ? ichatbasecomponent : new ChatMessage("container.chest", new Object[0]));
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        if (!this.d(nbttagcompound)) {
            ContainerUtil.b(nbttagcompound, this.items);
        }

        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.i = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (!this.e(nbttagcompound)) {
            ContainerUtil.a(nbttagcompound, this.items);
        }

        IChatBaseComponent ichatbasecomponent = this.getCustomName();

        if (ichatbasecomponent != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
        }

        return nbttagcompound;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public void tick() {
        int i = this.position.getX();
        int j = this.position.getY();
        int k = this.position.getZ();

        ++this.k;
        float f;

        if (!this.world.isClientSide && this.f != 0 && (this.k + i + j + k) % 200 == 0) {
            this.f = 0;
            f = 5.0F;
            List<EntityHuman> list = this.world.a(EntityHuman.class, new AxisAlignedBB((double) ((float) i - 5.0F), (double) ((float) j - 5.0F), (double) ((float) k - 5.0F), (double) ((float) (i + 1) + 5.0F), (double) ((float) (j + 1) + 5.0F), (double) ((float) (k + 1) + 5.0F)));
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (entityhuman.activeContainer instanceof ContainerChest) {
                    IInventory iinventory = ((ContainerChest) entityhuman.activeContainer).d();

                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).a((IInventory) this)) {
                        ++this.f;
                    }
                }
            }
        }

        this.e = this.a;
        f = 0.1F;
        if (this.f > 0 && this.a == 0.0F) {
            this.a(SoundEffects.BLOCK_CHEST_OPEN);
        }

        if (this.f == 0 && this.a > 0.0F || this.f > 0 && this.a < 1.0F) {
            float f1 = this.a;

            if (this.f > 0) {
                this.a += 0.1F;
            } else {
                this.a -= 0.1F;
            }

            if (this.a > 1.0F) {
                this.a = 1.0F;
            }

            float f2 = 0.5F;

            if (this.a < 0.5F && f1 >= 0.5F) {
                this.a(SoundEffects.BLOCK_CHEST_CLOSE);
            }

            if (this.a < 0.0F) {
                this.a = 0.0F;
            }
        }

    }

    private void a(SoundEffect soundeffect) {
        BlockPropertyChestType blockpropertychesttype = (BlockPropertyChestType) this.getBlock().get(BlockChest.b);

        if (blockpropertychesttype != BlockPropertyChestType.LEFT) {
            double d0 = (double) this.position.getX() + 0.5D;
            double d1 = (double) this.position.getY() + 0.5D;
            double d2 = (double) this.position.getZ() + 0.5D;

            if (blockpropertychesttype == BlockPropertyChestType.RIGHT) {
                EnumDirection enumdirection = BlockChest.k(this.getBlock());

                d0 += (double) enumdirection.getAdjacentX() * 0.5D;
                d2 += (double) enumdirection.getAdjacentZ() * 0.5D;
            }

            this.world.a((EntityHuman) null, d0, d1, d2, soundeffect, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    public boolean c(int i, int j) {
        if (i == 1) {
            this.f = j;
            return true;
        } else {
            return super.c(i, j);
        }
    }

    public void startOpen(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            if (this.f < 0) {
                this.f = 0;
            }

            ++this.f;
            this.p();
        }

    }

    public void closeContainer(EntityHuman entityhuman) {
        if (!entityhuman.isSpectator()) {
            --this.f;
            this.p();
        }

    }

    protected void p() {
        Block block = this.getBlock().getBlock();

        if (block instanceof BlockChest) {
            this.world.playBlockAction(this.position, block, 1, this.f);
            this.world.applyPhysics(this.position, block);
        }

    }

    public String getContainerName() {
        return "minecraft:chest";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        this.d(entityhuman);
        return new ContainerChest(playerinventory, this, entityhuman);
    }

    protected NonNullList<ItemStack> q() {
        return this.items;
    }

    protected void a(NonNullList<ItemStack> nonnulllist) {
        this.items = nonnulllist;
    }

    public static int a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);

        if (iblockdata.getBlock().isTileEntity()) {
            TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                return ((TileEntityChest) tileentity).f;
            }
        }

        return 0;
    }

    public static void a(TileEntityChest tileentitychest, TileEntityChest tileentitychest1) {
        NonNullList<ItemStack> nonnulllist = tileentitychest.q();

        tileentitychest.a(tileentitychest1.q());
        tileentitychest1.a(nonnulllist);
    }
}
