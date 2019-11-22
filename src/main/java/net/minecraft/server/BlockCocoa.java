package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockCocoa extends BlockFacingHorizontal implements IBlockFragilePlantElement {

    public static final BlockStateInteger AGE = BlockProperties.Z;
    protected static final VoxelShape[] b = new VoxelShape[]{Block.a(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.a(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.a(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
    protected static final VoxelShape[] c = new VoxelShape[]{Block.a(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.a(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.a(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
    protected static final VoxelShape[] d = new VoxelShape[]{Block.a(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.a(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.a(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
    protected static final VoxelShape[] e = new VoxelShape[]{Block.a(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.a(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.a(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

    public BlockCocoa(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCocoa.FACING, EnumDirection.NORTH)).set(BlockCocoa.AGE, 0));
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.random.nextInt(Math.max(1, (int) (100.0F / world.spigotConfig.cocoaModifier) * 5)) == 0) { // Spigot
            int i = (Integer) iblockdata.get(BlockCocoa.AGE);

            if (i < 2) {
                CraftEventFactory.handleBlockGrowEvent(world, blockposition, (IBlockData) iblockdata.set(BlockCocoa.AGE, i + 1), 2); // CraftBukkkit
            }
        }

    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        Block block = iworldreader.getType(blockposition.shift((EnumDirection) iblockdata.get(BlockCocoa.FACING))).getBlock();

        return block.a(TagsBlock.JUNGLE_LOGS);
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        int i = (Integer) iblockdata.get(BlockCocoa.AGE);

        switch ((EnumDirection) iblockdata.get(BlockCocoa.FACING)) {
            case SOUTH:
                return BlockCocoa.e[i];
            case NORTH:
            default:
                return BlockCocoa.d[i];
            case WEST:
                return BlockCocoa.c[i];
            case EAST:
                return BlockCocoa.b[i];
        }
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlockData();
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        EnumDirection[] aenumdirection = blockactioncontext.e();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (enumdirection.k().c()) {
                iblockdata = (IBlockData) iblockdata.set(BlockCocoa.FACING, enumdirection);
                if (iblockdata.canPlace(world, blockposition)) {
                    return iblockdata;
                }
            }
        }

        return null;
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == iblockdata.get(BlockCocoa.FACING) && !iblockdata.canPlace(generatoraccess, blockposition) ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockCocoa.AGE) < 2;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        CraftEventFactory.handleBlockGrowEvent(world, blockposition, (IBlockData) iblockdata.set(BlockCocoa.AGE, (Integer) iblockdata.get(BlockCocoa.AGE) + 1), 2); // CraftBukkit
    }

    @Override
    public TextureType c() {
        return TextureType.CUTOUT;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCocoa.FACING, BlockCocoa.AGE);
    }
}
