package net.minecraft.server;

public class ItemMinecart extends Item {

    private static final IDispenseBehavior a = new DispenseBehaviorItem() {
        private final DispenseBehaviorItem a = new DispenseBehaviorItem();

        public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
            EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
            World world = isourceblock.getWorld();
            double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX() * 1.125D;
            double d1 = Math.floor(isourceblock.getY()) + (double) enumdirection.getAdjacentY();
            double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ() * 1.125D;
            BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
            IBlockData iblockdata = world.getType(blockposition);
            BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata.get(((BlockMinecartTrackAbstract) iblockdata.getBlock()).e()) : BlockPropertyTrackPosition.NORTH_SOUTH;
            double d3;

            if (iblockdata.a(TagsBlock.RAILS)) {
                if (blockpropertytrackposition.c()) {
                    d3 = 0.6D;
                } else {
                    d3 = 0.1D;
                }
            } else {
                if (!iblockdata.isAir() || !world.getType(blockposition.down()).a(TagsBlock.RAILS)) {
                    return this.a.dispense(isourceblock, itemstack);
                }

                IBlockData iblockdata1 = world.getType(blockposition.down());
                BlockPropertyTrackPosition blockpropertytrackposition1 = iblockdata1.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata1.get(((BlockMinecartTrackAbstract) iblockdata1.getBlock()).e()) : BlockPropertyTrackPosition.NORTH_SOUTH;

                if (enumdirection != EnumDirection.DOWN && blockpropertytrackposition1.c()) {
                    d3 = -0.4D;
                } else {
                    d3 = -0.9D;
                }
            }

            EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.a(world, d0, d1 + d3, d2, ((ItemMinecart) itemstack.getItem()).b);

            if (itemstack.hasName()) {
                entityminecartabstract.setCustomName(itemstack.getName());
            }

            world.addEntity(entityminecartabstract);
            itemstack.subtract(1);
            return itemstack;
        }

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
        }
    };
    private final EntityMinecartAbstract.EnumMinecartType b;

    public ItemMinecart(EntityMinecartAbstract.EnumMinecartType entityminecartabstract_enumminecarttype, Item.Info item_info) {
        super(item_info);
        this.b = entityminecartabstract_enumminecarttype;
        BlockDispenser.a((IMaterial) this, ItemMinecart.a);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (!iblockdata.a(TagsBlock.RAILS)) {
            return EnumInteractionResult.FAIL;
        } else {
            ItemStack itemstack = itemactioncontext.getItemStack();

            if (!world.isClientSide) {
                BlockPropertyTrackPosition blockpropertytrackposition = iblockdata.getBlock() instanceof BlockMinecartTrackAbstract ? (BlockPropertyTrackPosition) iblockdata.get(((BlockMinecartTrackAbstract) iblockdata.getBlock()).e()) : BlockPropertyTrackPosition.NORTH_SOUTH;
                double d0 = 0.0D;

                if (blockpropertytrackposition.c()) {
                    d0 = 0.5D;
                }

                EntityMinecartAbstract entityminecartabstract = EntityMinecartAbstract.a(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.0625D + d0, (double) blockposition.getZ() + 0.5D, this.b);

                if (itemstack.hasName()) {
                    entityminecartabstract.setCustomName(itemstack.getName());
                }

                world.addEntity(entityminecartabstract);
            }

            itemstack.subtract(1);
            return EnumInteractionResult.SUCCESS;
        }
    }
}
