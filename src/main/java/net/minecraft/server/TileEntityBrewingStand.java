package net.minecraft.server;

import java.util.Arrays;
import java.util.Iterator;
import javax.annotation.Nullable;

public class TileEntityBrewingStand extends TileEntityContainer implements IWorldInventory, ITickable {

    private static final int[] a = new int[] { 3};
    private static final int[] e = new int[] { 0, 1, 2, 3};
    private static final int[] f = new int[] { 0, 1, 2, 4};
    private NonNullList<ItemStack> items;
    private int brewTime;
    private boolean[] i;
    private Item j;
    private IChatBaseComponent k;
    private int fuelLevel;

    public TileEntityBrewingStand() {
        super(TileEntityTypes.BREWING_STAND);
        this.items = NonNullList.a(5, ItemStack.a);
    }

    public IChatBaseComponent getDisplayName() {
        return (IChatBaseComponent) (this.k != null ? this.k : new ChatMessage("container.brewing", new Object[0]));
    }

    public boolean hasCustomName() {
        return this.k != null;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return this.k;
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.k = ichatbasecomponent;
    }

    public int getSize() {
        return this.items.size();
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

    public void tick() {
        ItemStack itemstack = (ItemStack) this.items.get(4);

        if (this.fuelLevel <= 0 && itemstack.getItem() == Items.BLAZE_POWDER) {
            this.fuelLevel = 20;
            itemstack.subtract(1);
            this.update();
        }

        boolean flag = this.q();
        boolean flag1 = this.brewTime > 0;
        ItemStack itemstack1 = (ItemStack) this.items.get(3);

        if (flag1) {
            --this.brewTime;
            boolean flag2 = this.brewTime == 0;

            if (flag2 && flag) {
                this.r();
                this.update();
            } else if (!flag) {
                this.brewTime = 0;
                this.update();
            } else if (this.j != itemstack1.getItem()) {
                this.brewTime = 0;
                this.update();
            }
        } else if (flag && this.fuelLevel > 0) {
            --this.fuelLevel;
            this.brewTime = 400;
            this.j = itemstack1.getItem();
            this.update();
        }

        if (!this.world.isClientSide) {
            boolean[] aboolean = this.p();

            if (!Arrays.equals(aboolean, this.i)) {
                this.i = aboolean;
                IBlockData iblockdata = this.world.getType(this.getPosition());

                if (!(iblockdata.getBlock() instanceof BlockBrewingStand)) {
                    return;
                }

                for (int i = 0; i < BlockBrewingStand.HAS_BOTTLE.length; ++i) {
                    iblockdata = (IBlockData) iblockdata.set(BlockBrewingStand.HAS_BOTTLE[i], aboolean[i]);
                }

                this.world.setTypeAndData(this.position, iblockdata, 2);
            }
        }

    }

    public boolean[] p() {
        boolean[] aboolean = new boolean[3];

        for (int i = 0; i < 3; ++i) {
            if (!((ItemStack) this.items.get(i)).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    private boolean q() {
        ItemStack itemstack = (ItemStack) this.items.get(3);

        if (itemstack.isEmpty()) {
            return false;
        } else if (!PotionBrewer.a(itemstack)) {
            return false;
        } else {
            for (int i = 0; i < 3; ++i) {
                ItemStack itemstack1 = (ItemStack) this.items.get(i);

                if (!itemstack1.isEmpty() && PotionBrewer.a(itemstack1, itemstack)) {
                    return true;
                }
            }

            return false;
        }
    }

    private void r() {
        ItemStack itemstack = (ItemStack) this.items.get(3);

        for (int i = 0; i < 3; ++i) {
            this.items.set(i, PotionBrewer.d(itemstack, (ItemStack) this.items.get(i)));
        }

        itemstack.subtract(1);
        BlockPosition blockposition = this.getPosition();

        if (itemstack.getItem().p()) {
            ItemStack itemstack1 = new ItemStack(itemstack.getItem().o());

            if (itemstack.isEmpty()) {
                itemstack = itemstack1;
            } else {
                InventoryUtils.dropItem(this.world, (double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), itemstack1);
            }
        }

        this.items.set(3, itemstack);
        this.world.triggerEffect(1035, blockposition, 0);
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.items = NonNullList.a(this.getSize(), ItemStack.a);
        ContainerUtil.b(nbttagcompound, this.items);
        this.brewTime = nbttagcompound.getShort("BrewTime");
        if (nbttagcompound.hasKeyOfType("CustomName", 8)) {
            this.k = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("CustomName"));
        }

        this.fuelLevel = nbttagcompound.getByte("Fuel");
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        nbttagcompound.setShort("BrewTime", (short) this.brewTime);
        ContainerUtil.a(nbttagcompound, this.items);
        if (this.k != null) {
            nbttagcompound.setString("CustomName", IChatBaseComponent.ChatSerializer.a(this.k));
        }

        nbttagcompound.setByte("Fuel", (byte) this.fuelLevel);
        return nbttagcompound;
    }

    public ItemStack getItem(int i) {
        return i >= 0 && i < this.items.size() ? (ItemStack) this.items.get(i) : ItemStack.a;
    }

    public ItemStack splitStack(int i, int j) {
        return ContainerUtil.a(this.items, i, j);
    }

    public ItemStack splitWithoutUpdate(int i) {
        return ContainerUtil.a(this.items, i);
    }

    public void setItem(int i, ItemStack itemstack) {
        if (i >= 0 && i < this.items.size()) {
            this.items.set(i, itemstack);
        }

    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        if (i == 3) {
            return PotionBrewer.a(itemstack);
        } else {
            Item item = itemstack.getItem();

            return i == 4 ? item == Items.BLAZE_POWDER : (item == Items.POTION || item == Items.SPLASH_POTION || item == Items.LINGERING_POTION || item == Items.GLASS_BOTTLE) && this.getItem(i).isEmpty();
        }
    }

    public int[] getSlotsForFace(EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? TileEntityBrewingStand.a : (enumdirection == EnumDirection.DOWN ? TileEntityBrewingStand.e : TileEntityBrewingStand.f);
    }

    public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
        return this.b(i, itemstack);
    }

    public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
        return i == 3 ? itemstack.getItem() == Items.GLASS_BOTTLE : true;
    }

    public String getContainerName() {
        return "minecraft:brewing_stand";
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerBrewingStand(playerinventory, this);
    }

    public int getProperty(int i) {
        switch (i) {
        case 0:
            return this.brewTime;
        case 1:
            return this.fuelLevel;
        default:
            return 0;
        }
    }

    public void setProperty(int i, int j) {
        switch (i) {
        case 0:
            this.brewTime = j;
            break;
        case 1:
            this.fuelLevel = j;
        }

    }

    public int h() {
        return 2;
    }

    public void clear() {
        this.items.clear();
    }
}
