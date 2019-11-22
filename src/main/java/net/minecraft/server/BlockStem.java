package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockStem extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockProperties.ac;
    protected static final VoxelShape[] b = new VoxelShape[]{Block.a(7.0D, 0.0D, 7.0D, 9.0D, 2.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 4.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 6.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 8.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 10.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 12.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 14.0D, 9.0D), Block.a(7.0D, 0.0D, 7.0D, 9.0D, 16.0D, 9.0D)};
    private final BlockStemmed blockFruit;

    protected BlockStem(BlockStemmed blockstemmed, Block.Info block_info) {
        super(block_info);
        this.blockFruit = blockstemmed;
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockStem.AGE, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockStem.b[(Integer) iblockdata.get(BlockStem.AGE)];
    }

    @Override
    protected boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getBlock() == Blocks.FARMLAND;
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.tick(iblockdata, world, blockposition, random);
        if (world.getLightLevel(blockposition, 0) >= 9) {
            float f = BlockCrops.a((Block) this, (IBlockAccess) world, blockposition);

            if (random.nextInt((int) ((100.0F / (this == Blocks.PUMPKIN_STEM ? world.spigotConfig.pumpkinModifier : world.spigotConfig.melonModifier)) * (25.0F / f)) + 1) == 0) { // Spigot
                int i = (Integer) iblockdata.get(BlockStem.AGE);

                if (i < 7) {
                    iblockdata = (IBlockData) iblockdata.set(BlockStem.AGE, i + 1);
                    CraftEventFactory.handleBlockGrowEvent(world, blockposition, iblockdata, 2); // CraftBukkit
                } else {
                    EnumDirection enumdirection = EnumDirection.EnumDirectionLimit.HORIZONTAL.a(random);
                    BlockPosition blockposition1 = blockposition.shift(enumdirection);
                    Block block = world.getType(blockposition1.down()).getBlock();

                    if (world.getType(blockposition1).isAir() && (block == Blocks.FARMLAND || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.GRASS_BLOCK)) {
                        // CraftBukkit start
                        if (!CraftEventFactory.handleBlockGrowEvent(world, blockposition1, this.blockFruit.getBlockData())) {
                            return;
                        }
                        // CraftBukkit end
                        world.setTypeUpdate(blockposition, (IBlockData) this.blockFruit.e().getBlockData().set(BlockFacingHorizontal.FACING, enumdirection));
                    }
                }
            }

        }
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockStem.AGE) != 7;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = Math.min(7, (Integer) iblockdata.get(BlockStem.AGE) + MathHelper.nextInt(world.random, 2, 5));
        IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockStem.AGE, i);

        CraftEventFactory.handleBlockGrowEvent(world, blockposition, iblockdata1, 2); // CraftBukkit
        if (i == 7) {
            iblockdata1.a(world, blockposition, world.random);
        }

    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockStem.AGE);
    }

    public BlockStemmed e() {
        return this.blockFruit;
    }
}
