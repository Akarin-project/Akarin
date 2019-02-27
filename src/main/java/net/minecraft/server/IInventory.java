package net.minecraft.server;

public interface IInventory extends INamableTileEntity {

    int getSize();

    boolean P_();

    ItemStack getItem(int i);

    ItemStack splitStack(int i, int j);

    ItemStack splitWithoutUpdate(int i);

    void setItem(int i, ItemStack itemstack);

    int getMaxStackSize();

    void update();

    boolean a(EntityHuman entityhuman);

    void startOpen(EntityHuman entityhuman);

    void closeContainer(EntityHuman entityhuman);

    boolean b(int i, ItemStack itemstack);

    int getProperty(int i);

    void setProperty(int i, int j);

    int h();

    void clear();

    default int n() {
        return 0;
    }

    default int U_() {
        return 0;
    }
}
