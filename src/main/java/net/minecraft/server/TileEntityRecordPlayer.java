package net.minecraft.server;

public class TileEntityRecordPlayer extends TileEntity {

    private ItemStack record;

    public TileEntityRecordPlayer() {}

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("RecordItem")) {
            this.setRecord(ItemStack.createStack(nbttagcompound.getCompound("RecordItem")));
        } else if (nbttagcompound.getInt("Record") > 0) {
            this.setRecord(new ItemStack(nbttagcompound.getInt("Record"), 1, 0));
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        if (this.getRecord() != null) {
            nbttagcompound.setCompound("RecordItem", this.getRecord().save(new NBTTagCompound()));
            nbttagcompound.setInt("Record", this.getRecord().id);
        }
    }

    public ItemStack getRecord() {
        return this.record;
    }

    public void setRecord(ItemStack itemstack) {
        // CraftBukkit start - There can only be one
        if (itemstack != null) {
            itemstack.count = 1;
        }
        // CraftBukkit end

        this.record = itemstack;
        this.update();
    }
}
