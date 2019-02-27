package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockFluids extends Block implements IFluidSource {

    public static final BlockStateInteger LEVEL = BlockProperties.ah;
    protected final FluidTypeFlowing b;
    private final List<Fluid> c;
    private final Map<IBlockData, VoxelShape> o = Maps.newIdentityHashMap();

    protected BlockFluids(FluidTypeFlowing fluidtypeflowing, Block.Info block_info) {
        super(block_info);
        this.b = fluidtypeflowing;
        this.c = Lists.newArrayList();
        this.c.add(fluidtypeflowing.a(false));

        for (int i = 1; i < 8; ++i) {
            this.c.add(fluidtypeflowing.a(8 - i, false));
        }

        this.c.add(fluidtypeflowing.a(8, true));
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockFluids.LEVEL, 0));
    }

    public void b(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        world.getFluid(blockposition).b(world, blockposition, random);
    }

    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return !this.b.a(TagsFluid.LAVA);
    }

    public Fluid h(IBlockData iblockdata) {
        int i = (Integer) iblockdata.get(BlockFluids.LEVEL);

        return (Fluid) this.c.get(Math.min(i, 8));
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean isCollidable(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Fluid fluid = iblockaccess.getFluid(blockposition.up());

        return fluid.c().a((FluidType) this.b) ? VoxelShapes.b() : (VoxelShape) this.o.computeIfAbsent(iblockdata, (iblockdata1) -> {
            Fluid fluid1 = iblockdata1.s();

            return VoxelShapes.create(0.0D, 0.0D, 0.0D, 1.0D, (double) fluid1.getHeight(), 1.0D);
        });
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.INVISIBLE;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public int a(IWorldReader iworldreader) {
        return this.b.a(iworldreader);
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (this.a(world, blockposition, iblockdata)) {
            world.getFluidTickList().a(blockposition, iblockdata.s().c(), this.a((IWorldReader) world));
        }

    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (iblockdata.s().d() || iblockdata1.s().d()) {
            generatoraccess.getFluidTickList().a(blockposition, iblockdata.s().c(), this.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (this.a(world, blockposition, iblockdata)) {
            world.getFluidTickList().a(blockposition, iblockdata.s().c(), this.a((IWorldReader) world));
        }

    }

    public boolean a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        if (this.b.a(TagsFluid.LAVA)) {
            boolean flag = false;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];

                if (enumdirection != EnumDirection.DOWN && world.getFluid(blockposition.shift(enumdirection)).a(TagsFluid.WATER)) {
                    flag = true;
                    break;
                }
            }

            if (flag) {
                Fluid fluid = world.getFluid(blockposition);

                if (fluid.d()) {
                    // CraftBukkit start
                    if (org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(world, blockposition, Blocks.OBSIDIAN.getBlockData())) {
                        this.fizz(world, blockposition);
                    }
                    // CraftBukkit end
                    return false;
                }

                if (fluid.getHeight() >= 0.44444445F) {
                    // CraftBukkit start
                    if (org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(world, blockposition, Blocks.COBBLESTONE.getBlockData())) {
                        this.fizz(world, blockposition);
                    }
                    // CraftBukkit end
                    return false;
                }
            }
        }

        return true;
    }

    protected void fizz(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        generatoraccess.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (generatoraccess.m().nextFloat() - generatoraccess.m().nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            generatoraccess.addParticle(Particles.F, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
        }

    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFluids.LEVEL);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public FluidType removeFluid(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        if ((Integer) iblockdata.get(BlockFluids.LEVEL) == 0) {
            generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            return this.b;
        } else {
            return FluidTypes.EMPTY;
        }
    }
}
