package net.minecraft.server;

public abstract class TileEntityContainer extends TileEntity implements ITileInventory {

    private ChestLock a;

    protected TileEntityContainer(TileEntityTypes<?> tileentitytypes) {
        super(tileentitytypes);
        this.a = ChestLock.a;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.a = ChestLock.b(nbttagcompound);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.a != null) {
            this.a.a(nbttagcompound);
        }

        return nbttagcompound;
    }

    public boolean isLocked() {
        return this.a != null && !this.a.a();
    }

    public ChestLock getLock() {
        return this.a;
    }

    public void setLock(ChestLock chestlock) {
        this.a = chestlock;
    }
}
