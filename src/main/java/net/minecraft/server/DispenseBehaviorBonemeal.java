package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

final class DispenseBehaviorBonemeal extends DispenseBehaviorItem {

    private boolean b = true;

    DispenseBehaviorBonemeal() {}

    protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
        if (itemstack.getData() == 15) {
            EnumFacing enumfacing = BlockDispenser.b(isourceblock.h());
            World world = isourceblock.k();
            int i = isourceblock.getBlockX() + enumfacing.getAdjacentX();
            int j = isourceblock.getBlockY() + enumfacing.getAdjacentY();
            int k = isourceblock.getBlockZ() + enumfacing.getAdjacentZ();

            // CraftBukkit start
            org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockX(), isourceblock.getBlockY(), isourceblock.getBlockZ());
            CraftItemStack craftItem = CraftItemStack.asNewCraftStack(itemstack.getItem());

            BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
            if (!BlockDispenser.eventFired) {
                world.getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                return itemstack;
            }

            if (!event.getItem().equals(craftItem)) {
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.a.get(eventStack.getItem());
                if (idispensebehavior != IDispenseBehavior.a && idispensebehavior != this) {
                    idispensebehavior.a(isourceblock, eventStack);
                    return itemstack;
                }
            }
            // CraftBukkit end

            if (ItemDye.a(itemstack, world, i, j, k)) {
                if (!world.isStatic) {
                    world.triggerEffect(2005, i, j, k, 0);
                }
            } else {
                this.b = false;
            }

            return itemstack;
        } else {
            return super.b(isourceblock, itemstack);
        }
    }

    protected void a(ISourceBlock isourceblock) {
        if (this.b) {
            isourceblock.k().triggerEffect(1000, isourceblock.getBlockX(), isourceblock.getBlockY(), isourceblock.getBlockZ(), 0);
        } else {
            isourceblock.k().triggerEffect(1001, isourceblock.getBlockX(), isourceblock.getBlockY(), isourceblock.getBlockZ(), 0);
        }
    }
}
