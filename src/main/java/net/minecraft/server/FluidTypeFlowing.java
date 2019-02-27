package net.minecraft.server;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanMap;
import it.unimi.dsi.fastutil.shorts.Short2BooleanOpenHashMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import it.unimi.dsi.fastutil.shorts.Short2ObjectOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class FluidTypeFlowing extends FluidType {

    public static final BlockStateBoolean FALLING = BlockProperties.h;
    public static final BlockStateInteger LEVEL = BlockProperties.ag;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.a>> e = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.a>(200) {
            protected void rehash(int i) {}
        };

        object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
        return object2bytelinkedopenhashmap;
    });

    public FluidTypeFlowing() {}

    protected void a(BlockStateList.a<FluidType, Fluid> blockstatelist_a) {
        blockstatelist_a.a(FluidTypeFlowing.FALLING);
    }

    public Vec3D a(IWorldReader iworldreader, BlockPosition blockposition, Fluid fluid) {
        double d0 = 0.0D;
        double d1 = 0.0D;
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        Vec3D vec3d;

        try {
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();

                blockposition_b.g(blockposition).c(enumdirection);
                Fluid fluid1 = iworldreader.getFluid(blockposition_b);

                if (this.g(fluid1)) {
                    float f = fluid1.getHeight();
                    float f1 = 0.0F;

                    if (f == 0.0F) {
                        if (!iworldreader.getType(blockposition_b).getMaterial().isSolid()) {
                            Fluid fluid2 = iworldreader.getFluid(blockposition_b.down());

                            if (this.g(fluid2)) {
                                f = fluid2.getHeight();
                                if (f > 0.0F) {
                                    f1 = fluid.getHeight() - (f - 0.8888889F);
                                }
                            }
                        }
                    } else if (f > 0.0F) {
                        f1 = fluid.getHeight() - f;
                    }

                    if (f1 != 0.0F) {
                        d0 += (double) ((float) enumdirection.getAdjacentX() * f1);
                        d1 += (double) ((float) enumdirection.getAdjacentZ() * f1);
                    }
                }
            }

            Vec3D vec3d1 = new Vec3D(d0, 0.0D, d1);

            if ((Boolean) fluid.get(FluidTypeFlowing.FALLING)) {
                Iterator iterator1 = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

                while (iterator1.hasNext()) {
                    EnumDirection enumdirection1 = (EnumDirection) iterator1.next();

                    blockposition_b.g(blockposition).c(enumdirection1);
                    if (this.a((IBlockAccess) iworldreader, (BlockPosition) blockposition_b, enumdirection1) || this.a((IBlockAccess) iworldreader, blockposition_b.up(), enumdirection1)) {
                        vec3d1 = vec3d1.a().add(0.0D, -6.0D, 0.0D);
                        break;
                    }
                }
            }

            vec3d = vec3d1.a();
        } catch (Throwable throwable1) {
            throwable = throwable1;
            throw throwable1;
        } finally {
            if (blockposition_b != null) {
                if (throwable != null) {
                    try {
                        blockposition_b.close();
                    } catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                } else {
                    blockposition_b.close();
                }
            }

        }

        return vec3d;
    }

    private boolean g(Fluid fluid) {
        return fluid.e() || fluid.c().a((FluidType) this);
    }

    protected boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        IBlockData iblockdata = iblockaccess.getType(blockposition);
        Block block = iblockdata.getBlock();
        Fluid fluid = iblockaccess.getFluid(blockposition);

        if (fluid.c().a((FluidType) this)) {
            return false;
        } else if (enumdirection == EnumDirection.UP) {
            return true;
        } else if (iblockdata.getMaterial() == Material.ICE) {
            return false;
        } else {
            boolean flag = Block.b(block) || block instanceof BlockStairs;

            return !flag && iblockdata.c(iblockaccess, blockposition, enumdirection) == EnumBlockFaceShape.SOLID;
        }
    }

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid) {
        if (!fluid.e()) {
            IBlockData iblockdata = generatoraccess.getType(blockposition);
            BlockPosition blockposition1 = blockposition.down();
            IBlockData iblockdata1 = generatoraccess.getType(blockposition1);
            Fluid fluid1 = this.a((IWorldReader) generatoraccess, blockposition1, iblockdata1);

            if (this.a(generatoraccess, blockposition, iblockdata, EnumDirection.DOWN, blockposition1, iblockdata1, generatoraccess.getFluid(blockposition1), fluid1.c())) {
                this.a(generatoraccess, blockposition1, iblockdata1, EnumDirection.DOWN, fluid1);
                if (this.a((IWorldReader) generatoraccess, blockposition) >= 3) {
                    this.a(generatoraccess, blockposition, fluid, iblockdata);
                }
            } else if (fluid.d() || !this.a((IBlockAccess) generatoraccess, fluid1.c(), blockposition, iblockdata, blockposition1, iblockdata1)) {
                this.a(generatoraccess, blockposition, fluid, iblockdata);
            }

        }
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, Fluid fluid, IBlockData iblockdata) {
        int i = fluid.g() - this.c((IWorldReader) generatoraccess);

        if ((Boolean) fluid.get(FluidTypeFlowing.FALLING)) {
            i = 7;
        }

        if (i > 0) {
            Map<EnumDirection, Fluid> map = this.b(generatoraccess, blockposition, iblockdata);
            Iterator iterator = map.entrySet().iterator();

            while (iterator.hasNext()) {
                Entry<EnumDirection, Fluid> entry = (Entry) iterator.next();
                EnumDirection enumdirection = (EnumDirection) entry.getKey();
                Fluid fluid1 = (Fluid) entry.getValue();
                BlockPosition blockposition1 = blockposition.shift(enumdirection);
                IBlockData iblockdata1 = generatoraccess.getType(blockposition1);

                if (this.a(generatoraccess, blockposition, iblockdata, enumdirection, blockposition1, iblockdata1, generatoraccess.getFluid(blockposition1), fluid1.c())) {
                    this.a(generatoraccess, blockposition1, iblockdata1, enumdirection, fluid1);
                }
            }

        }
    }

    protected Fluid a(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        int i = 0;
        int j = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData iblockdata1 = iworldreader.getType(blockposition1);
            Fluid fluid = iblockdata1.s();

            if (fluid.c().a((FluidType) this) && this.a(enumdirection, (IBlockAccess) iworldreader, blockposition, iblockdata, blockposition1, iblockdata1)) {
                if (fluid.d()) {
                    ++j;
                }

                i = Math.max(i, fluid.g());
            }
        }

        if (this.g() && j >= 2) {
            IBlockData iblockdata2 = iworldreader.getType(blockposition.down());
            Fluid fluid1 = iblockdata2.s();

            if (iblockdata2.getMaterial().isBuildable() || this.h(fluid1)) {
                return this.a(false);
            }
        }

        BlockPosition blockposition2 = blockposition.up();
        IBlockData iblockdata3 = iworldreader.getType(blockposition2);
        Fluid fluid2 = iblockdata3.s();

        if (!fluid2.e() && fluid2.c().a((FluidType) this) && this.a(EnumDirection.UP, (IBlockAccess) iworldreader, blockposition, iblockdata, blockposition2, iblockdata3)) {
            return this.a(8, true);
        } else {
            int k = i - this.c(iworldreader);

            return k <= 0 ? FluidTypes.EMPTY.i() : this.a(k, false);
        }
    }

    private boolean a(EnumDirection enumdirection, IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1) {
        Object2ByteLinkedOpenHashMap object2bytelinkedopenhashmap;

        if (!iblockdata.getBlock().s() && !iblockdata1.getBlock().s()) {
            object2bytelinkedopenhashmap = (Object2ByteLinkedOpenHashMap) FluidTypeFlowing.e.get();
        } else {
            object2bytelinkedopenhashmap = null;
        }

        Block.a block_a;

        if (object2bytelinkedopenhashmap != null) {
            block_a = new Block.a(iblockdata, iblockdata1, enumdirection);
            byte b0 = object2bytelinkedopenhashmap.getAndMoveToFirst(block_a);

            if (b0 != 127) {
                return b0 != 0;
            }
        } else {
            block_a = null;
        }

        VoxelShape voxelshape = iblockdata.getCollisionShape(iblockaccess, blockposition);
        VoxelShape voxelshape1 = iblockdata1.getCollisionShape(iblockaccess, blockposition1);
        boolean flag = !VoxelShapes.b(voxelshape, voxelshape1, enumdirection);

        if (object2bytelinkedopenhashmap != null) {
            if (object2bytelinkedopenhashmap.size() == 200) {
                object2bytelinkedopenhashmap.removeLastByte();
            }

            object2bytelinkedopenhashmap.putAndMoveToFirst(block_a, (byte) (flag ? 1 : 0));
        }

        return flag;
    }

    public abstract FluidType e();

    public Fluid a(int i, boolean flag) {
        return (Fluid) ((Fluid) this.e().i().set(FluidTypeFlowing.LEVEL, i)).set(FluidTypeFlowing.FALLING, flag);
    }

    public abstract FluidType f();

    public Fluid a(boolean flag) {
        return (Fluid) this.f().i().set(FluidTypeFlowing.FALLING, flag);
    }

    protected abstract boolean g();

    protected void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, Fluid fluid) {
        if (iblockdata.getBlock() instanceof IFluidContainer) {
            ((IFluidContainer) iblockdata.getBlock()).place(generatoraccess, blockposition, iblockdata, fluid);
        } else {
            if (!iblockdata.isAir()) {
                this.a(generatoraccess, blockposition, iblockdata);
            }

            generatoraccess.setTypeAndData(blockposition, fluid.i(), 3);
        }

    }

    protected abstract void a(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata);

    private static short a(BlockPosition blockposition, BlockPosition blockposition1) {
        int i = blockposition1.getX() - blockposition.getX();
        int j = blockposition1.getZ() - blockposition.getZ();

        return (short) ((i + 128 & 255) << 8 | j + 128 & 255);
    }

    protected int a(IWorldReader iworldreader, BlockPosition blockposition, int i, EnumDirection enumdirection, IBlockData iblockdata, BlockPosition blockposition1, Short2ObjectMap<Pair<IBlockData, Fluid>> short2objectmap, Short2BooleanMap short2booleanmap) {
        int j = 1000;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();

            if (enumdirection1 != enumdirection) {
                BlockPosition blockposition2 = blockposition.shift(enumdirection1);
                short short0 = a(blockposition1, blockposition2);
                Pair<IBlockData, Fluid> pair = (Pair) short2objectmap.computeIfAbsent(short0, (k) -> {
                    IBlockData iblockdata1 = iworldreader.getType(blockposition2);

                    return Pair.of(iblockdata1, iblockdata1.s());
                });
                IBlockData iblockdata1 = (IBlockData) pair.getFirst();
                Fluid fluid = (Fluid) pair.getSecond();

                if (this.a(iworldreader, this.e(), blockposition, iblockdata, enumdirection1, blockposition2, iblockdata1, fluid)) {
                    boolean flag = short2booleanmap.computeIfAbsent(short0, (k) -> {
                        BlockPosition blockposition3 = blockposition2.down();
                        IBlockData iblockdata2 = iworldreader.getType(blockposition3);

                        return this.a((IBlockAccess) iworldreader, this.e(), blockposition2, iblockdata1, blockposition3, iblockdata2);
                    });

                    if (flag) {
                        return i;
                    }

                    if (i < this.b(iworldreader)) {
                        int k = this.a(iworldreader, blockposition2, i + 1, enumdirection1.opposite(), iblockdata1, blockposition1, short2objectmap, short2booleanmap);

                        if (k < j) {
                            j = k;
                        }
                    }
                }
            }
        }

        return j;
    }

    private boolean a(IBlockAccess iblockaccess, FluidType fluidtype, BlockPosition blockposition, IBlockData iblockdata, BlockPosition blockposition1, IBlockData iblockdata1) {
        return !this.a(EnumDirection.DOWN, iblockaccess, blockposition, iblockdata, blockposition1, iblockdata1) ? false : (iblockdata1.s().c().a((FluidType) this) ? true : this.a(iblockaccess, blockposition1, iblockdata1, fluidtype));
    }

    private boolean a(IBlockAccess iblockaccess, FluidType fluidtype, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, BlockPosition blockposition1, IBlockData iblockdata1, Fluid fluid) {
        return !this.h(fluid) && this.a(enumdirection, iblockaccess, blockposition, iblockdata, blockposition1, iblockdata1) && this.a(iblockaccess, blockposition1, iblockdata1, fluidtype);
    }

    private boolean h(Fluid fluid) {
        return fluid.c().a((FluidType) this) && fluid.d();
    }

    protected abstract int b(IWorldReader iworldreader);

    private int a(IWorldReader iworldreader, BlockPosition blockposition) {
        int i = 0;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            Fluid fluid = iworldreader.getFluid(blockposition1);

            if (this.h(fluid)) {
                ++i;
            }
        }

        return i;
    }

    protected Map<EnumDirection, Fluid> b(IWorldReader iworldreader, BlockPosition blockposition, IBlockData iblockdata) {
        int i = 1000;
        Map<EnumDirection, Fluid> map = Maps.newEnumMap(EnumDirection.class);
        Short2ObjectMap<Pair<IBlockData, Fluid>> short2objectmap = new Short2ObjectOpenHashMap();
        Short2BooleanOpenHashMap short2booleanopenhashmap = new Short2BooleanOpenHashMap();
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            short short0 = a(blockposition, blockposition1);
            Pair<IBlockData, Fluid> pair = (Pair) short2objectmap.computeIfAbsent(short0, (j) -> {
                IBlockData iblockdata1 = iworldreader.getType(blockposition1);

                return Pair.of(iblockdata1, iblockdata1.s());
            });
            IBlockData iblockdata1 = (IBlockData) pair.getFirst();
            Fluid fluid = (Fluid) pair.getSecond();
            Fluid fluid1 = this.a(iworldreader, blockposition1, iblockdata1);

            if (this.a(iworldreader, fluid1.c(), blockposition, iblockdata, enumdirection, blockposition1, iblockdata1, fluid)) {
                BlockPosition blockposition2 = blockposition1.down();
                boolean flag = short2booleanopenhashmap.computeIfAbsent(short0, (j) -> {
                    IBlockData iblockdata2 = iworldreader.getType(blockposition2);

                    return this.a((IBlockAccess) iworldreader, this.e(), blockposition1, iblockdata1, blockposition2, iblockdata2);
                });
                int j;

                if (flag) {
                    j = 0;
                } else {
                    j = this.a(iworldreader, blockposition1, 1, enumdirection.opposite(), iblockdata1, blockposition, short2objectmap, short2booleanopenhashmap);
                }

                if (j < i) {
                    map.clear();
                }

                if (j <= i) {
                    map.put(enumdirection, fluid1);
                    i = j;
                }
            }
        }

        return map;
    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, FluidType fluidtype) {
        Block block = iblockdata.getBlock();

        if (block instanceof IFluidContainer) {
            return ((IFluidContainer) block).canPlace(iblockaccess, blockposition, iblockdata, fluidtype);
        } else if (!(block instanceof BlockDoor) && block != Blocks.SIGN && block != Blocks.LADDER && block != Blocks.SUGAR_CANE && block != Blocks.BUBBLE_COLUMN) {
            Material material = iblockdata.getMaterial();

            return material != Material.PORTAL && material != Material.STRUCTURE_VOID && material != Material.WATER_PLANT && material != Material.REPLACEABLE_WATER_PLANT ? !material.isSolid() : false;
        } else {
            return false;
        }
    }

    protected boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, EnumDirection enumdirection, BlockPosition blockposition1, IBlockData iblockdata1, Fluid fluid, FluidType fluidtype) {
        return fluid.a(fluidtype, enumdirection) && this.a(enumdirection, iblockaccess, blockposition, iblockdata, blockposition1, iblockdata1) && this.a(iblockaccess, blockposition1, iblockdata1, fluidtype);
    }

    protected abstract int c(IWorldReader iworldreader);

    protected int a(World world, Fluid fluid, Fluid fluid1) {
        return this.a((IWorldReader) world);
    }

    public void a(World world, BlockPosition blockposition, Fluid fluid) {
        if (!fluid.d()) {
            Fluid fluid1 = this.a((IWorldReader) world, blockposition, world.getType(blockposition));
            int i = this.a(world, fluid, fluid1);

            if (fluid1.e()) {
                fluid = fluid1;
                world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
            } else if (!fluid1.equals(fluid)) {
                fluid = fluid1;
                IBlockData iblockdata = fluid1.i();

                world.setTypeAndData(blockposition, iblockdata, 2);
                world.getFluidTickList().a(blockposition, fluid1.c(), i);
                world.applyPhysics(blockposition, iblockdata.getBlock());
            }
        }

        this.a((GeneratorAccess) world, blockposition, fluid);
    }

    protected static int e(Fluid fluid) {
        return fluid.d() ? 0 : 8 - Math.min(fluid.g(), 8) + ((Boolean) fluid.get(FluidTypeFlowing.FALLING) ? 8 : 0);
    }

    public float a(Fluid fluid) {
        return (float) fluid.g() / 9.0F;
    }
}
