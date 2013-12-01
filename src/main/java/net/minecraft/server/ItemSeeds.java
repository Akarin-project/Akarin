package net.minecraft.server;

public class ItemSeeds extends Item {

    private Block block;
    private Block b;

    public ItemSeeds(Block block, Block block1) {
        this.block = block;
        this.b = block1;
        this.a(CreativeModeTab.l);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        final int clickedX = i, clickedY = j, clickedZ = k; // CraftBukkit
        if (l != 1) {
            return false;
        } else if (entityhuman.a(i, j, k, l, itemstack) && entityhuman.a(i, j + 1, k, l, itemstack)) {
            if (world.getType(i, j, k) == this.b && world.isEmpty(i, j + 1, k)) {
                // CraftBukkit start - Seeds
                // world.setTypeUpdate(i, j + 1, k, this.block);
                if (!ItemBlock.processBlockPlace(world, entityhuman, null, i, j + 1, k, this.block, 0, clickedX, clickedY, clickedZ)) {
                    return false;
                }
                // CraftBukkit end
                --itemstack.count;
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
