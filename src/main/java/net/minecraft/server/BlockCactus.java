package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class BlockCactus extends Block {

    public static final BlockStateInteger AGE = BlockProperties.X;
    protected static final VoxelShape b = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);
    protected static final VoxelShape c = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    protected BlockCactus(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCactus.AGE, 0));
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(world, blockposition)) {
            world.setAir(blockposition, true);
        } else {
            BlockPosition blockposition1 = blockposition.up();

            if (world.isEmpty(blockposition1)) {
                int i;

                for (i = 1; world.getType(blockposition.down(i)).getBlock() == this; ++i) {
                    ;
                }

                if (i < 3) {
                    int j = (Integer) iblockdata.get(BlockCactus.AGE);

                    if (j == 15) {
                        world.setTypeUpdate(blockposition1, this.getBlockData());
                        IBlockData iblockdata1 = (IBlockData) iblockdata.set(BlockCactus.AGE, 0);

                        world.setTypeAndData(blockposition, iblockdata1, 4);
                        iblockdata1.doPhysics(world, blockposition1, this, blockposition);
                    } else {
                        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCactus.AGE, j + 1), 4);
                    }

                }
            }
        }
    }

    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCactus.b;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCactus.c;
    }

    public boolean f(IBlockData iblockdata) {
        return true;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        EnumDirection enumdirection;
        Material material;

        do {
            if (!iterator.hasNext()) {
                Block block = iworldreader.getType(blockposition.down()).getBlock();

                return (block == Blocks.CACTUS || block == Blocks.SAND || block == Blocks.RED_SAND) && !iworldreader.getType(blockposition.up()).getMaterial().isLiquid();
            }

            enumdirection = (EnumDirection) iterator.next();
            IBlockData iblockdata1 = iworldreader.getType(blockposition.shift(enumdirection));

            material = iblockdata1.getMaterial();
        } while (!material.isBuildable() && !iworldreader.getFluid(blockposition.shift(enumdirection)).a(TagsFluid.LAVA));

        return false;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        entity.damageEntity(DamageSource.CACTUS, 1.0F);
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCactus.AGE);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
