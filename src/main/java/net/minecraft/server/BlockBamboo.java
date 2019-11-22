package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class BlockBamboo extends Block implements IBlockFragilePlantElement {

    protected static final VoxelShape a = Block.a(5.0D, 0.0D, 5.0D, 11.0D, 16.0D, 11.0D);
    protected static final VoxelShape b = Block.a(3.0D, 0.0D, 3.0D, 13.0D, 16.0D, 13.0D);
    protected static final VoxelShape c = Block.a(6.5D, 0.0D, 6.5D, 9.5D, 16.0D, 9.5D);
    public static final BlockStateInteger d = BlockProperties.Y;
    public static final BlockStateEnum<BlockPropertyBambooSize> e = BlockProperties.aF;
    public static final BlockStateInteger f = BlockProperties.at;

    public BlockBamboo(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBamboo.d, 0)).set(BlockBamboo.e, BlockPropertyBambooSize.NONE)).set(BlockBamboo.f, 0));
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBamboo.d, BlockBamboo.e, BlockBamboo.f);
    }

    @Override
    public Block.EnumRandomOffset R_() {
        return Block.EnumRandomOffset.XZ;
    }

    @Override
    public boolean b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        VoxelShape voxelshape = iblockdata.get(BlockBamboo.e) == BlockPropertyBambooSize.LARGE ? BlockBamboo.b : BlockBamboo.a;
        Vec3D vec3d = iblockdata.l(iblockaccess, blockposition);

        return voxelshape.a(vec3d.x, vec3d.y, vec3d.z);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        Vec3D vec3d = iblockdata.l(iblockaccess, blockposition);

        return BlockBamboo.c.a(vec3d.x, vec3d.y, vec3d.z);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        Fluid fluid = blockactioncontext.getWorld().getFluid(blockactioncontext.getClickPosition());

        if (!fluid.isEmpty()) {
            return null;
        } else {
            IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().down());

            if (iblockdata.a(TagsBlock.BAMBOO_PLANTABLE_ON)) {
                Block block = iblockdata.getBlock();

                if (block == Blocks.BAMBOO_SAPLING) {
                    return (IBlockData) this.getBlockData().set(BlockBamboo.d, 0);
                } else if (block == Blocks.BAMBOO) {
                    int i = (Integer) iblockdata.get(BlockBamboo.d) > 0 ? 1 : 0;

                    return (IBlockData) this.getBlockData().set(BlockBamboo.d, i);
                } else {
                    return Blocks.BAMBOO_SAPLING.getBlockData();
                }
            } else {
                return null;
            }
        }
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!iblockdata.canPlace(world, blockposition)) {
            world.b(blockposition, true);
        } else if ((Integer) iblockdata.get(BlockBamboo.f) == 0) {
            if (world.paperConfig.fixZeroTickInstantGrowFarms && !randomTick) return; // Paper - fix MC-113809
            if (world.random.nextInt(Math.max(1, (int) (100.0F / world.spigotConfig.bambooModifier) * 3)) == 0 && world.isEmpty(blockposition.up()) && world.getLightLevel(blockposition.up(), 0) >= 9) { // Spigot
                int i = this.b((IBlockAccess) world, blockposition) + 1;

                if (i < 16) {
                    this.a(iblockdata, world, blockposition, random, i);
                }
            }

        }
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return iworldreader.getType(blockposition.down()).a(TagsBlock.BAMBOO_PLANTABLE_ON);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            generatoraccess.getBlockTickList().a(blockposition, this, 1);
        }

        if (enumdirection == EnumDirection.UP && iblockdata1.getBlock() == Blocks.BAMBOO && (Integer) iblockdata1.get(BlockBamboo.d) > (Integer) iblockdata.get(BlockBamboo.d)) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockBamboo.d), 2);
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        int i = this.a(iblockaccess, blockposition);
        int j = this.b(iblockaccess, blockposition);

        return i + j + 1 < 16 && (Integer) iblockaccess.getType(blockposition.up(i)).get(BlockBamboo.f) != 1;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = this.a((IBlockAccess) world, blockposition);
        int j = this.b((IBlockAccess) world, blockposition);
        int k = i + j + 1;
        int l = 1 + random.nextInt(2);

        for (int i1 = 0; i1 < l; ++i1) {
            BlockPosition blockposition1 = blockposition.up(i);
            IBlockData iblockdata1 = world.getType(blockposition1);

            if (k >= 16 || (Integer) iblockdata1.get(BlockBamboo.f) == 1 || !world.isEmpty(blockposition1.up())) {
                return;
            }

            this.a(iblockdata1, world, blockposition1, random, k);
            ++i;
            ++k;
        }

    }

    @Override
    public float getDamage(IBlockData iblockdata, EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return entityhuman.getItemInMainHand().getItem() instanceof ItemSword ? 1.0F : super.getDamage(iblockdata, entityhuman, iblockaccess, blockposition);
    }

    @Override
    public TextureType c() {
        return TextureType.CUTOUT;
    }

    protected void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random, int i) {
        IBlockData iblockdata1 = world.getType(blockposition.down());
        BlockPosition blockposition1 = blockposition.down(2);
        IBlockData iblockdata2 = world.getType(blockposition1);
        BlockPropertyBambooSize blockpropertybamboosize = BlockPropertyBambooSize.NONE;
        boolean shouldUpdateOthers = false; // CraftBukkit

        if (i >= 1) {
            if (iblockdata1.getBlock() == Blocks.BAMBOO && iblockdata1.get(BlockBamboo.e) != BlockPropertyBambooSize.NONE) {
                if (iblockdata1.getBlock() == Blocks.BAMBOO && iblockdata1.get(BlockBamboo.e) != BlockPropertyBambooSize.NONE) {
                    blockpropertybamboosize = BlockPropertyBambooSize.LARGE;
                    if (iblockdata2.getBlock() == Blocks.BAMBOO) {
                        // CraftBukkit start - moved down
                        // world.setTypeAndData(blockposition.down(), (IBlockData) iblockdata1.set(BlockBamboo.e, BlockPropertyBambooSize.SMALL), 3);
                        // world.setTypeAndData(blockposition1, (IBlockData) iblockdata2.set(BlockBamboo.e, BlockPropertyBambooSize.NONE), 3);
                        shouldUpdateOthers = true;
                        // CraftBukkit end
                    }
                }
            } else {
                blockpropertybamboosize = BlockPropertyBambooSize.SMALL;
            }
        }

        int j = (Integer) iblockdata.get(BlockBamboo.d) != 1 && iblockdata2.getBlock() != Blocks.BAMBOO ? 0 : 1;
        int k = (i < 11 || random.nextFloat() >= 0.25F) && i != 15 ? 0 : 1;

        // CraftBukkit start
        if (org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockSpreadEvent(world, blockposition, blockposition.up(), (IBlockData) ((IBlockData) ((IBlockData) this.getBlockData().set(BlockBamboo.d, j)).set(BlockBamboo.e, blockpropertybamboosize)).set(BlockBamboo.f, k), 3)) {
            if (shouldUpdateOthers) {
                world.setTypeAndData(blockposition.down(), (IBlockData) iblockdata1.set(BlockBamboo.e, BlockPropertyBambooSize.SMALL), 3);
                world.setTypeAndData(blockposition1, (IBlockData) iblockdata2.set(BlockBamboo.e, BlockPropertyBambooSize.NONE), 3);
            }
        }
        // CraftBukkit end
    }

    protected int a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i;

        for (i = 0; i < 16 && iblockaccess.getType(blockposition.up(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {
            ;
        }

        return i;
    }

    protected int b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        int i;

        for (i = 0; i < 16 && iblockaccess.getType(blockposition.down(i + 1)).getBlock() == Blocks.BAMBOO; ++i) {
            ;
        }

        return i;
    }
}
