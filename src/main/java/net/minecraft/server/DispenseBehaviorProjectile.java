package net.minecraft.server;

public abstract class DispenseBehaviorProjectile extends DispenseBehaviorItem {

    public DispenseBehaviorProjectile() {}

    public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        World world = isourceblock.getWorld();
        IPosition iposition = BlockDispenser.a(isourceblock);
        EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
        IProjectile iprojectile = this.a(world, iposition, itemstack);

        iprojectile.shoot((double) enumdirection.getAdjacentX(), (double) ((float) enumdirection.getAdjacentY() + 0.1F), (double) enumdirection.getAdjacentZ(), this.getPower(), this.a());
        world.addEntity((Entity) iprojectile);
        itemstack.subtract(1);
        return itemstack;
    }

    protected void a(ISourceBlock isourceblock) {
        isourceblock.getWorld().triggerEffect(1002, isourceblock.getBlockPosition(), 0);
    }

    protected abstract IProjectile a(World world, IPosition iposition, ItemStack itemstack);

    protected float a() {
        return 6.0F;
    }

    protected float getPower() {
        return 1.1F;
    }
}
