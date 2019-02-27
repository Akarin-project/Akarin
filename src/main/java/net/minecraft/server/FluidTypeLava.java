package net.minecraft.server;

import java.util.Random;

public abstract class FluidTypeLava extends FluidTypeFlowing {

    public FluidTypeLava() {}

    public FluidType e() {
        return FluidTypes.FLOWING_LAVA;
    }

    public FluidType f() {
        return FluidTypes.LAVA;
    }

    public Item b() {
        return Items.LAVA_BUCKET;
    }

    public void b(World world, BlockPosition blockposition, Fluid fluid, Random random) {
        if (world.getGameRules().getBoolean("doFireTick")) {
            int i = random.nextInt(3);

            if (i > 0) {
                BlockPosition blockposition1 = blockposition;

                for (int j = 0; j < i; ++j) {
                    blockposition1 = blockposition1.a(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
                    if (!world.p(blockposition1)) {
                        return;
                    }

                    IBlockData iblockdata = world.getType(blockposition1);

                    if (iblockdata.isAir()) {
                        if (this.a((IWorldReader) world, blockposition1)) {
                            world.setTypeUpdate(blockposition1, Blocks.FIRE.getBlockData());
                            return;
                        }
                    } else if (iblockdata.getMaterial().isSolid()) {
                        return;
                    }
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    BlockPosition blockposition2 = blockposition.a(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);

                    if (!world.p(blockposition2)) {
                        return;
                    }

                    if (world.isEmpty(blockposition2.up()) && this.b(world, blockposition2)) {
                        world.setTypeUpdate(blockposition2.up(), Blocks.FIRE.getBlockData());
                    }
                }
            }

        }
    }

    private boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.b(iworldreader, blockposition.shift(enumdirection))) {
                return true;
            }
        }

        return false;
    }

    private boolean b(IWorldReader iworldreader, BlockPosition blockposition) {
        return blockposition.getY() >= 0 && blockposition.getY() < 256 && !iworldreader.isLoaded(blockposition) ? false : iworldreader.getType(blockposition).getMaterial().isBurnable();
    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {
        this.a(generatoraccess, blockposition);
    }

    public int b(IWorldReader iworldreader) {
        return iworldreader.o().isNether() ? 4 : 2;
    }

    public IBlockData b(Fluid fluid) {
        return (IBlockData) Blocks.LAVA.getBlockData().set(BlockFluids.LEVEL, e(fluid));
    }

    public boolean a(FluidType fluidtype) {
        return fluidtype == FluidTypes.LAVA || fluidtype == FluidTypes.FLOWING_LAVA;
    }

    public int c(IWorldReader iworldreader) {
        return iworldreader.o().isNether() ? 1 : 2;
    }

    public boolean a(Fluid fluid, FluidType fluidtype, EnumDirection enumdirection) {
        return fluid.getHeight() >= 0.44444445F && fluidtype.a(TagsFluid.WATER);
    }

    public int a(IWorldReader iworldreader) {
        return iworldreader.o().h() ? 10 : 30;
    }

    public int a(World world, Fluid fluid, Fluid fluid1) {
        int i = this.a((IWorldReader) world);

        if (!fluid.e() && !fluid1.e() && !(Boolean) fluid.get(FluidTypeLava.FALLING) && !(Boolean) fluid1.get(FluidTypeLava.FALLING) && fluid1.getHeight() > fluid.getHeight() && world.m().nextInt(4) != 0) {
            i *= 4;
        }

        return i;
    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        double d0 = (double) blockposition.getX();
        double d1 = (double) blockposition.getY();
        double d2 = (double) blockposition.getZ();

        generatoraccess.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_LAVA_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (generatoraccess.m().nextFloat() - generatoraccess.m().nextFloat()) * 0.8F);

        for (int i = 0; i < 8; ++i) {
            generatoraccess.addParticle(Particles.F, d0 + Math.random(), d1 + 1.2D, d2 + Math.random(), 0.0D, 0.0D, 0.0D);
        }

    }

    protected boolean g() {
        return false;
    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, Fluid fluid) {
        if (enumdirection == EnumDirection.DOWN) {
            Fluid fluid1 = generatoraccess.getFluid(blockposition);

            if (this.a(TagsFluid.LAVA) && fluid1.a(TagsFluid.WATER)) {
                if (iblockdata.getBlock() instanceof BlockFluids) {
                    generatoraccess.setTypeAndData(blockposition, Blocks.STONE.getBlockData(), 3);
                }

                this.a(generatoraccess, blockposition);
                return;
            }
        }

        super.a(generatoraccess, blockposition, iblockdata, enumdirection, fluid);
    }

    protected boolean k() {
        return true;
    }

    protected float d() {
        return 100.0F;
    }

    public static class a extends FluidTypeLava {

        public a() {}

        protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
            super.a(blockstatelist_a);
            blockstatelist_a.a(FluidTypeLava.a.LEVEL);
        }

        public int d(Fluid fluid) {
            return (Integer) fluid.get(FluidTypeLava.a.LEVEL);
        }

        public boolean c(Fluid fluid) {
            return false;
        }
    }

    public static class b extends FluidTypeLava {

        public b() {}

        public int d(Fluid fluid) {
            return 8;
        }

        public boolean c(Fluid fluid) {
            return true;
        }
    }
}
