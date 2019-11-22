package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BlockReed extends Block {

    public static final BlockStateInteger AGE = BlockProperties.ad;
    protected static final VoxelShape b = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

    protected BlockReed(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockReed.AGE, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockReed.b;
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(world, blockposition)) {
            world.b(blockposition, true);
        } else if (world.isEmpty(blockposition.up())) {
            if (world.paperConfig.fixZeroTickInstantGrowFarms && !randomTick) return; // Paper - fix MC-113809
            int i;

            for (i = 1; world.getType(blockposition.down(i)).getBlock() == this; ++i) {
                ;
            }

                if (i < world.paperConfig.reedMaxHeight) { // Paper - Configurable growth height
                int j = (Integer) iblockdata.get(BlockReed.AGE);

                if (j >= (byte) range(3, ((100.0F / world.spigotConfig.caneModifier) * 15) + 0.5F, 15)) { // Spigot
                    org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockGrowEvent(world, blockposition.up(), this.getBlockData()); // CraftBukkit
                    world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockReed.AGE, 0), 4);
                } else {
                    world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockReed.AGE, j + 1), 4);
                }
            }
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        Block block = iworldreader.getType(blockposition.down()).getBlock();

        if (block == this) {
            return true;
        } else {
            if (block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.SAND || block == Blocks.RED_SAND) {
                BlockPosition blockposition1 = blockposition.down();
                Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator.hasNext()) {
                    EnumDirection enumdirection = (EnumDirection) iterator.next();
                    IBlockData iblockdata1 = iworldreader.getType(blockposition1.shift(enumdirection));
                    Fluid fluid = iworldreader.getFluid(blockposition1.shift(enumdirection));

                    if (fluid.a(TagsFluid.WATER) || iblockdata1.getBlock() == Blocks.FROSTED_ICE) {
                        return true;
                    }
                }
            }

            return false;
        }
    }

    @Override
    public TextureType c() {
        return TextureType.CUTOUT;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockReed.AGE);
    }
}
