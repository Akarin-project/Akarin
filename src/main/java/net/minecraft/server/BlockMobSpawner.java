package net.minecraft.server;

public class BlockMobSpawner extends BlockTileEntity {

    protected BlockMobSpawner(Block.Info block_info) {
        super(block_info);
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityMobSpawner();
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        super.dropNaturally(iblockdata, world, blockposition, f, i);
        /* CraftBukkit start - Delegate to getExpDrop
        int j = 15 + world.random.nextInt(15) + world.random.nextInt(15);

        this.dropExperience(world, blockposition, j);
        */
    }

    @Override
    public int getExpDrop(IBlockData iblockdata, World world, BlockPosition blockposition, int enchantmentLevel) {
        int j = 15 + world.random.nextInt(15) + world.random.nextInt(15);

        return j;
        // CraftBukkit end
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }
}
