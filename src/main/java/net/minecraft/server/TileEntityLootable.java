package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public abstract class TileEntityLootable extends TileEntityContainer implements ILootable {

    protected MinecraftKey g; public MinecraftKey getLootTableKey() { return g; } public void setLootTable(MinecraftKey key) { g = key; } // Paper - OBFHELPER
    protected long h; public long getSeed() { return h; } public void setSeed(long seed) { h = seed; } // Paper - OBFHELPER
    protected IChatBaseComponent i;
    public final com.destroystokyo.paper.loottable.PaperLootableInventoryData lootableData = new com.destroystokyo.paper.loottable.PaperLootableInventoryData(new com.destroystokyo.paper.loottable.PaperTileEntityLootableInventory(this)); // Paper

    protected TileEntityLootable(TileEntityTypes<?> tileentitytypes) {
        super(tileentitytypes);
    }

    public static void a(IBlockAccess iblockaccess, Random random, BlockPosition blockposition, MinecraftKey minecraftkey) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityLootable) {
            ((TileEntityLootable) tileentity).setLootTable(minecraftkey, random.nextLong());
        }

    }

    protected boolean d(NBTTagCompound nbttagcompound) {
        lootableData.loadNbt(nbttagcompound); // Paper
        if (nbttagcompound.hasKeyOfType("LootTable", 8)) {
            this.g = new MinecraftKey(nbttagcompound.getString("LootTable"));
            this.h = nbttagcompound.getLong("LootTableSeed");
            return false; // Paper - always load the items, table may still remain
        } else {
            return false;
        }
    }

    protected boolean e(NBTTagCompound nbttagcompound) {
        lootableData.saveNbt(nbttagcompound); // Paper
        if (this.g == null) {
            return false;
        } else {
            nbttagcompound.setString("LootTable", this.g.toString());
            if (this.h != 0L) {
                nbttagcompound.setLong("LootTableSeed", this.h);
            }

            return false; // Paper - always save the items, table may still remain
        }
    }

    public void d(@Nullable EntityHuman entityhuman) {
        if (lootableData.shouldReplenish(entityhuman) && this.world.getMinecraftServer() != null) { // Paper
            LootTable loottable = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(this.g);

            lootableData.processRefill(entityhuman); // Paper
            Random random;

            if (this.h == 0L) {
                random = new Random();
            } else {
                random = new Random(this.h);
            }

            LootTableInfo.Builder loottableinfo_builder = new LootTableInfo.Builder((WorldServer) this.world);

            loottableinfo_builder.position(this.position);
            if (entityhuman != null) {
                loottableinfo_builder.luck(entityhuman.dJ());
            }

            loottable.fillInventory(this, random, loottableinfo_builder.build());
        }

    }

    public MinecraftKey getLootTable() {
        return this.g;
    }

    public void setLootTable(MinecraftKey minecraftkey, long i) {
        this.g = minecraftkey;
        this.h = i;
    }

    public boolean hasCustomName() {
        return this.i != null;
    }

    public void setCustomName(@Nullable IChatBaseComponent ichatbasecomponent) {
        this.i = ichatbasecomponent;
    }

    @Nullable
    public IChatBaseComponent getCustomName() {
        return this.i;
    }

    public ItemStack getItem(int i) {
        this.d((EntityHuman) null);
        return (ItemStack) this.q().get(i);
    }

    public ItemStack splitStack(int i, int j) {
        this.d((EntityHuman) null);
        ItemStack itemstack = ContainerUtil.a(this.q(), i, j);

        if (!itemstack.isEmpty()) {
            this.update();
        }

        return itemstack;
    }

    public ItemStack splitWithoutUpdate(int i) {
        this.d((EntityHuman) null);
        return ContainerUtil.a(this.q(), i);
    }

    public void setItem(int i, @Nullable ItemStack itemstack) {
        this.d((EntityHuman) null);
        this.q().set(i, itemstack);
        if (itemstack.getCount() > this.getMaxStackSize()) {
            itemstack.setCount(this.getMaxStackSize());
        }

        this.update();
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.position) != this ? false : entityhuman.d((double) this.position.getX() + 0.5D, (double) this.position.getY() + 0.5D, (double) this.position.getZ() + 0.5D) <= 64.0D;
    }

    public void startOpen(EntityHuman entityhuman) {}

    public void closeContainer(EntityHuman entityhuman) {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int h() {
        return 0;
    }

    public void clear() {
        this.q().clear();
    }

    protected abstract NonNullList<ItemStack> q();

    protected abstract void a(NonNullList<ItemStack> nonnulllist);
}
