package net.minecraft.server;

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

        a(isourceblock.getWorld(), itemstack1, 6, enumdirection, iposition);
        return itemstack;
    }

    public static void a(World world, ItemStack itemstack, int i, EnumDirection enumdirection, IPosition iposition) {
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
        world.addEntity(entityitem);
    }

    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
    }

    protected void a(ISourceBlock isourceblock, EnumDirection enumdirection) {
        isourceblock.getWorld().triggerEffect(2000, isourceblock.getBlockPosition(), enumdirection.a());
    }
}
