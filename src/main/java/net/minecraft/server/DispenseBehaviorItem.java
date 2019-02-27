package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.event.block.BlockDispenseEvent;
// CraftBukkit end

public class DispenseBehaviorItem implements IDispenseBehavior {

    public DispenseBehaviorItem() {}

    public final ItemStack dispense(ISourceBlock isourceblock, ItemStack itemstack) {
        ItemStack itemstack1 = this.a(isourceblock, itemstack);

        this.a(isourceblock);
        this.a(isourceblock, (EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
        return itemstack1;
    }

    protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
        IPosition iposition = BlockDispenser.a(isourceblock);
        ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

        // CraftBukkit start
        if (!a(isourceblock.getWorld(), itemstack1, 6, enumdirection, isourceblock)) {
            itemstack.add(1);
        }
        // CraftBukkit end
        return itemstack;
    }

    // CraftBukkit start - void -> boolean return, IPosition -> ISourceBlock last argument
    public static boolean a(World world, ItemStack itemstack, int i, EnumDirection enumdirection, ISourceBlock isourceblock) {
        if (itemstack.isEmpty()) return true;
        IPosition iposition = BlockDispenser.a(isourceblock);
        // CraftBukkit end
        double d0 = iposition.getX();
        double d1 = iposition.getY();
        double d2 = iposition.getZ();

        if (enumdirection.k() == EnumDirection.EnumAxis.Y) {
            d1 -= 0.125D;
        } else {
            d1 -= 0.15625D;
        }

        EntityItem entityitem = new EntityItem(world, d0, d1, d2, itemstack);
        double d3 = world.random.nextDouble() * 0.1D + 0.2D;

        entityitem.motX = (double) enumdirection.getAdjacentX() * d3;
        entityitem.motY = 0.20000000298023224D;
        entityitem.motZ = (double) enumdirection.getAdjacentZ() * d3;
        entityitem.motX += world.random.nextGaussian() * 0.007499999832361937D * (double) i;
        entityitem.motY += world.random.nextGaussian() * 0.007499999832361937D * (double) i;
        entityitem.motZ += world.random.nextGaussian() * 0.007499999832361937D * (double) i;

        // CraftBukkit start
        org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

        BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(entityitem.motX, entityitem.motY, entityitem.motZ));
        if (!BlockDispenser.eventFired) {
            world.getServer().getPluginManager().callEvent(event);
        }

        if (event.isCancelled()) {
            return false;
        }

        entityitem.setItemStack(CraftItemStack.asNMSCopy(event.getItem()));
        entityitem.motX = event.getVelocity().getX();
        entityitem.motY = event.getVelocity().getY();
        entityitem.motZ = event.getVelocity().getZ();

        if (!event.getItem().getType().equals(craftItem.getType())) {
            // Chain to handler for new item
            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
            IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
            if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior.getClass() != DispenseBehaviorItem.class) {
                idispensebehavior.dispense(isourceblock, eventStack);
            } else {
                world.addEntity(entityitem);
            }
            return false;
        }

        world.addEntity(entityitem);

        return true;
        // CraftBukkit end
    }

    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
    }

    protected void a(ISourceBlock isourceblock, EnumDirection enumdirection) {
        isourceblock.getWorld().triggerEffect(2000, isourceblock.getBlockPosition(), enumdirection.a());
    }
}
