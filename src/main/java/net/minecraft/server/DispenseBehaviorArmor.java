package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

final class DispenseBehaviorArmor extends DispenseBehaviorItem {

    DispenseBehaviorArmor() {}

    protected ItemStack b(ISourceBlock isourceblock, ItemStack itemstack) {
        EnumFacing enumfacing = BlockDispenser.b(isourceblock.h());
        int i = isourceblock.getBlockX() + enumfacing.getAdjacentX();
        int j = isourceblock.getBlockY() + enumfacing.getAdjacentY();
        int k = isourceblock.getBlockZ() + enumfacing.getAdjacentZ();
        AxisAlignedBB axisalignedbb = AxisAlignedBB.a((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1));
        List list = isourceblock.k().a(EntityLiving.class, axisalignedbb, (IEntitySelector) (new EntitySelectorEquipable(itemstack)));

        if (list.size() > 0) {
            EntityLiving entityliving = (EntityLiving) list.get(0);
            int l = entityliving instanceof EntityHuman ? 1 : 0;
            int i1 = EntityInsentient.b(itemstack);

            // CraftBukkit start
            ItemStack itemstack1 = itemstack.a(1);
            World world = isourceblock.k();
            org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockX(), isourceblock.getBlockY(), isourceblock.getBlockZ());
            CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

            BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
            if (!BlockDispenser.eventFired) {
                world.getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                itemstack.count++;
                return itemstack;
            }

            if (!event.getItem().equals(craftItem)) {
                itemstack.count++;
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.a.get(eventStack.getItem());
                if (idispensebehavior != IDispenseBehavior.a && idispensebehavior != this) {
                    idispensebehavior.a(isourceblock, eventStack);
                    return itemstack;
                }
            }
            // CraftBukkit end

            itemstack1.count = 1;
            entityliving.setEquipment(i1 - l, itemstack1);
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).a(i1, 2.0F);
            }

            // --itemstack.count; // CraftBukkit - handled above
            return itemstack;
        } else {
            return super.b(isourceblock, itemstack);
        }
    }
}
