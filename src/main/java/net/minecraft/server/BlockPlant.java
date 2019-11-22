package net.minecraft.server;

public class BlockPlant extends Block {

    protected BlockPlant(Block.Info block_info) {
        super(block_info);
    }

    protected boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = iblockdata.getBlock();

        return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        // CraftBukkit start
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            if (!(generatoraccess instanceof WorldServer && ((WorldServer) generatoraccess).hasPhysicsEvent) || !org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPhysicsEvent(generatoraccess, blockposition).isCancelled()) { // Paper
                return Blocks.AIR.getBlockData();
            }
        }
        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        // CraftBukkit end
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        return this.a_(iworldreader.getType(blockposition1), iworldreader, blockposition1);
    }

    @Override
    public TextureType c() {
        return TextureType.CUTOUT;
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }
}
