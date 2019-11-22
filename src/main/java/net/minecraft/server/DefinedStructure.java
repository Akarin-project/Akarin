package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class DefinedStructure {

    private final List<List<DefinedStructure.BlockInfo>> a = Lists.newArrayList();
    private final List<DefinedStructure.EntityInfo> b = Lists.newArrayList();
    private BlockPosition c;
    private String d;

    public DefinedStructure() {
        this.c = BlockPosition.ZERO;
        this.d = "?";
    }

    public BlockPosition a() {
        return this.c;
    }

    public void a(String s) {
        this.d = s;
    }

    public String b() {
        return this.d;
    }

    public void a(World world, BlockPosition blockposition, BlockPosition blockposition1, boolean flag, @Nullable Block block) {
        if (blockposition1.getX() >= 1 && blockposition1.getY() >= 1 && blockposition1.getZ() >= 1) {
            BlockPosition blockposition2 = blockposition.a((BaseBlockPosition) blockposition1).b(-1, -1, -1);
            List<DefinedStructure.BlockInfo> list = Lists.newArrayList();
            List<DefinedStructure.BlockInfo> list1 = Lists.newArrayList();
            List<DefinedStructure.BlockInfo> list2 = Lists.newArrayList();
            BlockPosition blockposition3 = new BlockPosition(Math.min(blockposition.getX(), blockposition2.getX()), Math.min(blockposition.getY(), blockposition2.getY()), Math.min(blockposition.getZ(), blockposition2.getZ()));
            BlockPosition blockposition4 = new BlockPosition(Math.max(blockposition.getX(), blockposition2.getX()), Math.max(blockposition.getY(), blockposition2.getY()), Math.max(blockposition.getZ(), blockposition2.getZ()));

            this.c = blockposition1;
            Iterator iterator = BlockPosition.a(blockposition3, blockposition4).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition5 = (BlockPosition) iterator.next();
                BlockPosition blockposition6 = blockposition5.b(blockposition3);
                IBlockData iblockdata = world.getType(blockposition5);

                if (block == null || block != iblockdata.getBlock()) {
                    TileEntity tileentity = world.getTileEntity(blockposition5);

                    if (tileentity != null) {
                        NBTTagCompound nbttagcompound = tileentity.save(new NBTTagCompound());

                        nbttagcompound.remove("x");
                        nbttagcompound.remove("y");
                        nbttagcompound.remove("z");
                        list1.add(new DefinedStructure.BlockInfo(blockposition6, iblockdata, nbttagcompound));
                    } else if (!iblockdata.g(world, blockposition5) && !iblockdata.o(world, blockposition5)) {
                        list2.add(new DefinedStructure.BlockInfo(blockposition6, iblockdata, (NBTTagCompound) null));
                    } else {
                        list.add(new DefinedStructure.BlockInfo(blockposition6, iblockdata, (NBTTagCompound) null));
                    }
                }
            }

            List<DefinedStructure.BlockInfo> list3 = Lists.newArrayList();

            list3.addAll(list);
            list3.addAll(list1);
            list3.addAll(list2);
            this.a.clear();
            this.a.add(list3);
            if (flag) {
                this.a(world, blockposition3, blockposition4.b(1, 1, 1));
            } else {
                this.b.clear();
            }

        }
    }

    private void a(World world, BlockPosition blockposition, BlockPosition blockposition1) {
        List<Entity> list = world.a(Entity.class, new AxisAlignedBB(blockposition, blockposition1), (java.util.function.Predicate) (entity) -> { // CraftBukkit - decompile error
            return !(entity instanceof EntityHuman);
        });

        this.b.clear();

        Vec3D vec3d;
        NBTTagCompound nbttagcompound;
        BlockPosition blockposition2;

        for (Iterator iterator = list.iterator(); iterator.hasNext(); this.b.add(new DefinedStructure.EntityInfo(vec3d, blockposition2, nbttagcompound))) {
            Entity entity = (Entity) iterator.next();

            vec3d = new Vec3D(entity.locX - (double) blockposition.getX(), entity.locY - (double) blockposition.getY(), entity.locZ - (double) blockposition.getZ());
            nbttagcompound = new NBTTagCompound();
            entity.d(nbttagcompound);
            if (entity instanceof EntityPainting) {
                blockposition2 = ((EntityPainting) entity).getBlockPosition().b(blockposition);
            } else {
                blockposition2 = new BlockPosition(vec3d);
            }
        }

    }

    public List<DefinedStructure.BlockInfo> a(BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, Block block) {
        return this.a(blockposition, definedstructureinfo, block, true);
    }

    public List<DefinedStructure.BlockInfo> a(BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, Block block, boolean flag) {
        List<DefinedStructure.BlockInfo> list = Lists.newArrayList();
        StructureBoundingBox structureboundingbox = definedstructureinfo.h();
        Iterator iterator = definedstructureinfo.a(this.a, blockposition).iterator();

        while (iterator.hasNext()) {
            DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
            BlockPosition blockposition1 = flag ? a(definedstructureinfo, definedstructure_blockinfo.a).a((BaseBlockPosition) blockposition) : definedstructure_blockinfo.a;

            if (structureboundingbox == null || structureboundingbox.b((BaseBlockPosition) blockposition1)) {
                IBlockData iblockdata = definedstructure_blockinfo.b;

                if (iblockdata.getBlock() == block) {
                    list.add(new DefinedStructure.BlockInfo(blockposition1, iblockdata.a(definedstructureinfo.d()), definedstructure_blockinfo.c));
                }
            }
        }

        return list;
    }

    public BlockPosition a(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo1, BlockPosition blockposition1) {
        BlockPosition blockposition2 = a(definedstructureinfo, blockposition);
        BlockPosition blockposition3 = a(definedstructureinfo1, blockposition1);

        return blockposition2.b(blockposition3);
    }

    public static BlockPosition a(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        return a(blockposition, definedstructureinfo.c(), definedstructureinfo.d(), definedstructureinfo.e());
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        definedstructureinfo.k();
        this.b(generatoraccess, blockposition, definedstructureinfo);
    }

    public void b(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo) {
        this.a(generatoraccess, blockposition, definedstructureinfo, 2);
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, int i) {
        if (this.a.isEmpty()) {
            return false;
        } else {
            List<DefinedStructure.BlockInfo> list = definedstructureinfo.a(this.a, blockposition);

            if ((!list.isEmpty() || !definedstructureinfo.g() && !this.b.isEmpty()) && this.c.getX() >= 1 && this.c.getY() >= 1 && this.c.getZ() >= 1) {
                StructureBoundingBox structureboundingbox = definedstructureinfo.h();
                List<BlockPosition> list1 = Lists.newArrayListWithCapacity(definedstructureinfo.l() ? list.size() : 0);
                List<Pair<BlockPosition, NBTTagCompound>> list2 = Lists.newArrayListWithCapacity(list.size());
                int j = Integer.MAX_VALUE;
                int k = Integer.MAX_VALUE;
                int l = Integer.MAX_VALUE;
                int i1 = Integer.MIN_VALUE;
                int j1 = Integer.MIN_VALUE;
                int k1 = Integer.MIN_VALUE;
                List<DefinedStructure.BlockInfo> list3 = a(generatoraccess, blockposition, definedstructureinfo, list);
                Iterator iterator = list3.iterator();

                TileEntity tileentity;

                while (iterator.hasNext()) {
                    DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
                    BlockPosition blockposition1 = definedstructure_blockinfo.a;

                    if (structureboundingbox == null || structureboundingbox.b((BaseBlockPosition) blockposition1)) {
                        Fluid fluid = definedstructureinfo.l() ? generatoraccess.getFluid(blockposition1) : null;
                        IBlockData iblockdata = definedstructure_blockinfo.b.a(definedstructureinfo.c()).a(definedstructureinfo.d());

                        if (definedstructure_blockinfo.c != null) {
                            tileentity = generatoraccess.getTileEntity(blockposition1);
                            Clearable.a(tileentity);
                            generatoraccess.setTypeAndData(blockposition1, Blocks.BARRIER.getBlockData(), 20);
                        }

                        if (generatoraccess.setTypeAndData(blockposition1, iblockdata, i)) {
                            j = Math.min(j, blockposition1.getX());
                            k = Math.min(k, blockposition1.getY());
                            l = Math.min(l, blockposition1.getZ());
                            i1 = Math.max(i1, blockposition1.getX());
                            j1 = Math.max(j1, blockposition1.getY());
                            k1 = Math.max(k1, blockposition1.getZ());
                            list2.add(Pair.of(blockposition1, definedstructure_blockinfo.c));
                            if (definedstructure_blockinfo.c != null) {
                                tileentity = generatoraccess.getTileEntity(blockposition1);
                                if (tileentity != null) {
                                    definedstructure_blockinfo.c.setInt("x", blockposition1.getX());
                                    definedstructure_blockinfo.c.setInt("y", blockposition1.getY());
                                    definedstructure_blockinfo.c.setInt("z", blockposition1.getZ());
                                    tileentity.isLoadingStructure = true; // Paper
                                    tileentity.load(definedstructure_blockinfo.c);
                                    tileentity.a(definedstructureinfo.c());
                                    tileentity.a(definedstructureinfo.d());
                                    tileentity.isLoadingStructure = false; // Paper
                                }
                            }

                            if (fluid != null && iblockdata.getBlock() instanceof IFluidContainer) {
                                ((IFluidContainer) iblockdata.getBlock()).place(generatoraccess, blockposition1, iblockdata, fluid);
                                if (!fluid.isSource()) {
                                    list1.add(blockposition1);
                                }
                            }
                        }
                    }
                }

                boolean flag = true;
                EnumDirection[] aenumdirection = new EnumDirection[]{EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};

                Iterator iterator1;
                BlockPosition blockposition2;
                IBlockData iblockdata1;

                while (flag && !list1.isEmpty()) {
                    flag = false;
                    iterator1 = list1.iterator();

                    while (iterator1.hasNext()) {
                        BlockPosition blockposition3 = (BlockPosition) iterator1.next();

                        blockposition2 = blockposition3;
                        Fluid fluid1 = generatoraccess.getFluid(blockposition3);

                        for (int l1 = 0; l1 < aenumdirection.length && !fluid1.isSource(); ++l1) {
                            BlockPosition blockposition4 = blockposition2.shift(aenumdirection[l1]);
                            Fluid fluid2 = generatoraccess.getFluid(blockposition4);

                            if (fluid2.getHeight(generatoraccess, blockposition4) > fluid1.getHeight(generatoraccess, blockposition2) || fluid2.isSource() && !fluid1.isSource()) {
                                fluid1 = fluid2;
                                blockposition2 = blockposition4;
                            }
                        }

                        if (fluid1.isSource()) {
                            iblockdata1 = generatoraccess.getType(blockposition3);
                            Block block = iblockdata1.getBlock();

                            if (block instanceof IFluidContainer) {
                                ((IFluidContainer) block).place(generatoraccess, blockposition3, iblockdata1, fluid1);
                                flag = true;
                                iterator1.remove();
                            }
                        }
                    }
                }

                if (j <= i1) {
                    if (!definedstructureinfo.i()) {
                        VoxelShapeBitSet voxelshapebitset = new VoxelShapeBitSet(i1 - j + 1, j1 - k + 1, k1 - l + 1);
                        int i2 = j;
                        int j2 = k;
                        int k2 = l;
                        Iterator iterator2 = list2.iterator();

                        while (iterator2.hasNext()) {
                            Pair<BlockPosition, NBTTagCompound> pair = (Pair) iterator2.next();
                            BlockPosition blockposition5 = (BlockPosition) pair.getFirst();

                            voxelshapebitset.a(blockposition5.getX() - i2, blockposition5.getY() - j2, blockposition5.getZ() - k2, true, true);
                        }

                        a(generatoraccess, i, voxelshapebitset, i2, j2, k2);
                    }

                    iterator1 = list2.iterator();

                    while (iterator1.hasNext()) {
                        Pair<BlockPosition, NBTTagCompound> pair1 = (Pair) iterator1.next();

                        blockposition2 = (BlockPosition) pair1.getFirst();
                        if (!definedstructureinfo.i()) {
                            IBlockData iblockdata2 = generatoraccess.getType(blockposition2);

                            iblockdata1 = Block.b(iblockdata2, generatoraccess, blockposition2);
                            if (iblockdata2 != iblockdata1) {
                                generatoraccess.setTypeAndData(blockposition2, iblockdata1, i & -2 | 16);
                            }

                            generatoraccess.update(blockposition2, iblockdata1.getBlock());
                        }

                        if (pair1.getSecond() != null) {
                            tileentity = generatoraccess.getTileEntity(blockposition2);
                            if (tileentity != null) {
                                tileentity.update();
                            }
                        }
                    }
                }

                if (!definedstructureinfo.g()) {
                    this.a(generatoraccess, blockposition, definedstructureinfo.c(), definedstructureinfo.d(), definedstructureinfo.e(), structureboundingbox);
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public static void a(GeneratorAccess generatoraccess, int i, VoxelShapeDiscrete voxelshapediscrete, int j, int k, int l) {
        voxelshapediscrete.a((enumdirection, i1, j1, k1) -> {
            BlockPosition blockposition = new BlockPosition(j + i1, k + j1, l + k1);
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            IBlockData iblockdata = generatoraccess.getType(blockposition);
            IBlockData iblockdata1 = generatoraccess.getType(blockposition1);
            IBlockData iblockdata2 = iblockdata.updateState(enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);

            if (iblockdata != iblockdata2) {
                generatoraccess.setTypeAndData(blockposition, iblockdata2, i & -2 | 16);
            }

            IBlockData iblockdata3 = iblockdata1.updateState(enumdirection.opposite(), iblockdata2, generatoraccess, blockposition1, blockposition);

            if (iblockdata1 != iblockdata3) {
                generatoraccess.setTypeAndData(blockposition1, iblockdata3, i & -2 | 16);
            }

        });
    }

    public static List<DefinedStructure.BlockInfo> a(GeneratorAccess generatoraccess, BlockPosition blockposition, DefinedStructureInfo definedstructureinfo, List<DefinedStructure.BlockInfo> list) {
        List<DefinedStructure.BlockInfo> list1 = Lists.newArrayList();
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) iterator.next();
            BlockPosition blockposition1 = a(definedstructureinfo, definedstructure_blockinfo.a).a((BaseBlockPosition) blockposition);
            DefinedStructure.BlockInfo definedstructure_blockinfo1 = new DefinedStructure.BlockInfo(blockposition1, definedstructure_blockinfo.b, definedstructure_blockinfo.c);

            for (Iterator iterator1 = definedstructureinfo.j().iterator(); definedstructure_blockinfo1 != null && iterator1.hasNext(); definedstructure_blockinfo1 = ((DefinedStructureProcessor) iterator1.next()).a(generatoraccess, blockposition, definedstructure_blockinfo, definedstructure_blockinfo1, definedstructureinfo)) {
                ;
            }

            if (definedstructure_blockinfo1 != null) {
                list1.add(definedstructure_blockinfo1);
            }
        }

        return list1;
    }

    private void a(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition1, @Nullable StructureBoundingBox structureboundingbox) {
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            DefinedStructure.EntityInfo definedstructure_entityinfo = (DefinedStructure.EntityInfo) iterator.next();
            BlockPosition blockposition2 = a(definedstructure_entityinfo.b, enumblockmirror, enumblockrotation, blockposition1).a((BaseBlockPosition) blockposition);

            if (structureboundingbox == null || structureboundingbox.b((BaseBlockPosition) blockposition2)) {
                NBTTagCompound nbttagcompound = definedstructure_entityinfo.c;
                Vec3D vec3d = a(definedstructure_entityinfo.a, enumblockmirror, enumblockrotation, blockposition1);
                Vec3D vec3d1 = vec3d.add((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                NBTTagList nbttaglist = new NBTTagList();

                nbttaglist.add(new NBTTagDouble(vec3d1.x));
                nbttaglist.add(new NBTTagDouble(vec3d1.y));
                nbttaglist.add(new NBTTagDouble(vec3d1.z));
                nbttagcompound.set("Pos", nbttaglist);
                nbttagcompound.remove("UUIDMost");
                nbttagcompound.remove("UUIDLeast");
                a(generatoraccess, nbttagcompound).ifPresent((entity) -> {
                    float f = entity.a(enumblockmirror);

                    f += entity.yaw - entity.a(enumblockrotation);
                    entity.setPositionRotation(vec3d1.x, vec3d1.y, vec3d1.z, f, entity.pitch);
                    generatoraccess.addEntity(entity);
                });
            }
        }

    }

    private static Optional<Entity> a(GeneratorAccess generatoraccess, NBTTagCompound nbttagcompound) {
        // CraftBukkit start
        // try {
            return EntityTypes.a(nbttagcompound, generatoraccess.getMinecraftWorld());
        // } catch (Exception exception) {
            // return Optional.empty();
        // }
        // CraftBukkit end
    }

    public BlockPosition a(EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
            case CLOCKWISE_90:
                return new BlockPosition(this.c.getZ(), this.c.getY(), this.c.getX());
            default:
                return this.c;
        }
    }

    public static BlockPosition a(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition1) {
        int i = blockposition.getX();
        int j = blockposition.getY();
        int k = blockposition.getZ();
        boolean flag = true;

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                k = -k;
                break;
            case FRONT_BACK:
                i = -i;
                break;
            default:
                flag = false;
        }

        int l = blockposition1.getX();
        int i1 = blockposition1.getZ();

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                return new BlockPosition(l - i1 + k, j, l + i1 - i);
            case CLOCKWISE_90:
                return new BlockPosition(l + i1 - k, j, i1 - l + i);
            case CLOCKWISE_180:
                return new BlockPosition(l + l - i, j, i1 + i1 - k);
            default:
                return flag ? new BlockPosition(i, j, k) : blockposition;
        }
    }

    private static Vec3D a(Vec3D vec3d, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, BlockPosition blockposition) {
        double d0 = vec3d.x;
        double d1 = vec3d.y;
        double d2 = vec3d.z;
        boolean flag = true;

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                d2 = 1.0D - d2;
                break;
            case FRONT_BACK:
                d0 = 1.0D - d0;
                break;
            default:
                flag = false;
        }

        int i = blockposition.getX();
        int j = blockposition.getZ();

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                return new Vec3D((double) (i - j) + d2, d1, (double) (i + j + 1) - d0);
            case CLOCKWISE_90:
                return new Vec3D((double) (i + j + 1) - d2, d1, (double) (j - i) + d0);
            case CLOCKWISE_180:
                return new Vec3D((double) (i + i + 1) - d0, d1, (double) (j + j + 1) - d2);
            default:
                return flag ? new Vec3D(d0, d1, d2) : vec3d;
        }
    }

    public BlockPosition a(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation) {
        return a(blockposition, enumblockmirror, enumblockrotation, this.a().getX(), this.a().getZ());
    }

    public static BlockPosition a(BlockPosition blockposition, EnumBlockMirror enumblockmirror, EnumBlockRotation enumblockrotation, int i, int j) {
        --i;
        --j;
        int k = enumblockmirror == EnumBlockMirror.FRONT_BACK ? i : 0;
        int l = enumblockmirror == EnumBlockMirror.LEFT_RIGHT ? j : 0;
        BlockPosition blockposition1 = blockposition;

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                blockposition1 = blockposition.b(l, 0, i - k);
                break;
            case CLOCKWISE_90:
                blockposition1 = blockposition.b(j - l, 0, k);
                break;
            case CLOCKWISE_180:
                blockposition1 = blockposition.b(i - k, 0, j - l);
                break;
            case NONE:
                blockposition1 = blockposition.b(k, 0, l);
        }

        return blockposition1;
    }

    public StructureBoundingBox b(DefinedStructureInfo definedstructureinfo, BlockPosition blockposition) {
        EnumBlockRotation enumblockrotation = definedstructureinfo.d();
        BlockPosition blockposition1 = definedstructureinfo.e();
        BlockPosition blockposition2 = this.a(enumblockrotation);
        EnumBlockMirror enumblockmirror = definedstructureinfo.c();
        int i = blockposition1.getX();
        int j = blockposition1.getZ();
        int k = blockposition2.getX() - 1;
        int l = blockposition2.getY() - 1;
        int i1 = blockposition2.getZ() - 1;
        StructureBoundingBox structureboundingbox = new StructureBoundingBox(0, 0, 0, 0, 0, 0);

        switch (enumblockrotation) {
            case COUNTERCLOCKWISE_90:
                structureboundingbox = new StructureBoundingBox(i - j, 0, i + j - i1, i - j + k, l, i + j);
                break;
            case CLOCKWISE_90:
                structureboundingbox = new StructureBoundingBox(i + j - k, 0, j - i, i + j, l, j - i + i1);
                break;
            case CLOCKWISE_180:
                structureboundingbox = new StructureBoundingBox(i + i - k, 0, j + j - i1, i + i, l, j + j);
                break;
            case NONE:
                structureboundingbox = new StructureBoundingBox(0, 0, 0, k, l, i1);
        }

        switch (enumblockmirror) {
            case LEFT_RIGHT:
                this.a(enumblockrotation, i1, k, structureboundingbox, EnumDirection.NORTH, EnumDirection.SOUTH);
                break;
            case FRONT_BACK:
                this.a(enumblockrotation, k, i1, structureboundingbox, EnumDirection.WEST, EnumDirection.EAST);
            case NONE:
        }

        structureboundingbox.a(blockposition.getX(), blockposition.getY(), blockposition.getZ());
        return structureboundingbox;
    }

    private void a(EnumBlockRotation enumblockrotation, int i, int j, StructureBoundingBox structureboundingbox, EnumDirection enumdirection, EnumDirection enumdirection1) {
        BlockPosition blockposition = BlockPosition.ZERO;

        if (enumblockrotation != EnumBlockRotation.CLOCKWISE_90 && enumblockrotation != EnumBlockRotation.COUNTERCLOCKWISE_90) {
            if (enumblockrotation == EnumBlockRotation.CLOCKWISE_180) {
                blockposition = blockposition.shift(enumdirection1, i);
            } else {
                blockposition = blockposition.shift(enumdirection, i);
            }
        } else {
            blockposition = blockposition.shift(enumblockrotation.a(enumdirection), j);
        }

        structureboundingbox.a(blockposition.getX(), 0, blockposition.getZ());
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        if (this.a.isEmpty()) {
            nbttagcompound.set("blocks", new NBTTagList());
            nbttagcompound.set("palette", new NBTTagList());
        } else {
            List<DefinedStructure.a> list = Lists.newArrayList();
            DefinedStructure.a definedstructure_a = new DefinedStructure.a();

            list.add(definedstructure_a);

            for (int i = 1; i < this.a.size(); ++i) {
                list.add(new DefinedStructure.a());
            }

            NBTTagList nbttaglist = new NBTTagList();
            List<DefinedStructure.BlockInfo> list1 = (List) this.a.get(0);

            for (int j = 0; j < list1.size(); ++j) {
                DefinedStructure.BlockInfo definedstructure_blockinfo = (DefinedStructure.BlockInfo) list1.get(j);
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.set("pos", this.a(definedstructure_blockinfo.a.getX(), definedstructure_blockinfo.a.getY(), definedstructure_blockinfo.a.getZ()));
                int k = definedstructure_a.a(definedstructure_blockinfo.b);

                nbttagcompound1.setInt("state", k);
                if (definedstructure_blockinfo.c != null) {
                    nbttagcompound1.set("nbt", definedstructure_blockinfo.c);
                }

                nbttaglist.add(nbttagcompound1);

                for (int l = 1; l < this.a.size(); ++l) {
                    DefinedStructure.a definedstructure_a1 = (DefinedStructure.a) list.get(l);

                    definedstructure_a1.a(((DefinedStructure.BlockInfo) ((List) this.a.get(l)).get(j)).b, k);
                }
            }

            nbttagcompound.set("blocks", nbttaglist);
            NBTTagList nbttaglist1;
            Iterator iterator;

            if (list.size() == 1) {
                nbttaglist1 = new NBTTagList();
                iterator = definedstructure_a.iterator();

                while (iterator.hasNext()) {
                    IBlockData iblockdata = (IBlockData) iterator.next();

                    nbttaglist1.add(GameProfileSerializer.a(iblockdata));
                }

                nbttagcompound.set("palette", nbttaglist1);
            } else {
                nbttaglist1 = new NBTTagList();
                iterator = list.iterator();

                while (iterator.hasNext()) {
                    DefinedStructure.a definedstructure_a2 = (DefinedStructure.a) iterator.next();
                    NBTTagList nbttaglist2 = new NBTTagList();
                    Iterator iterator1 = definedstructure_a2.iterator();

                    while (iterator1.hasNext()) {
                        IBlockData iblockdata1 = (IBlockData) iterator1.next();

                        nbttaglist2.add(GameProfileSerializer.a(iblockdata1));
                    }

                    nbttaglist1.add(nbttaglist2);
                }

                nbttagcompound.set("palettes", nbttaglist1);
            }
        }

        NBTTagList nbttaglist3 = new NBTTagList();

        NBTTagCompound nbttagcompound2;

        for (Iterator iterator2 = this.b.iterator(); iterator2.hasNext(); nbttaglist3.add(nbttagcompound2)) {
            DefinedStructure.EntityInfo definedstructure_entityinfo = (DefinedStructure.EntityInfo) iterator2.next();

            nbttagcompound2 = new NBTTagCompound();
            nbttagcompound2.set("pos", this.a(definedstructure_entityinfo.a.x, definedstructure_entityinfo.a.y, definedstructure_entityinfo.a.z));
            nbttagcompound2.set("blockPos", this.a(definedstructure_entityinfo.b.getX(), definedstructure_entityinfo.b.getY(), definedstructure_entityinfo.b.getZ()));
            if (definedstructure_entityinfo.c != null) {
                nbttagcompound2.set("nbt", definedstructure_entityinfo.c);
            }
        }

        nbttagcompound.set("entities", nbttaglist3);
        nbttagcompound.set("size", this.a(this.c.getX(), this.c.getY(), this.c.getZ()));
        nbttagcompound.setInt("DataVersion", SharedConstants.a().getWorldVersion());
        return nbttagcompound;
    }

    public void b(NBTTagCompound nbttagcompound) {
        this.a.clear();
        this.b.clear();
        NBTTagList nbttaglist = nbttagcompound.getList("size", 3);

        this.c = new BlockPosition(nbttaglist.e(0), nbttaglist.e(1), nbttaglist.e(2));
        NBTTagList nbttaglist1 = nbttagcompound.getList("blocks", 10);
        NBTTagList nbttaglist2;
        int i;

        if (nbttagcompound.hasKeyOfType("palettes", 9)) {
            nbttaglist2 = nbttagcompound.getList("palettes", 9);

            for (i = 0; i < nbttaglist2.size(); ++i) {
                this.a(nbttaglist2.b(i), nbttaglist1);
            }
        } else {
            this.a(nbttagcompound.getList("palette", 10), nbttaglist1);
        }

        nbttaglist2 = nbttagcompound.getList("entities", 10);

        for (i = 0; i < nbttaglist2.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist2.getCompound(i);
            NBTTagList nbttaglist3 = nbttagcompound1.getList("pos", 6);
            Vec3D vec3d = new Vec3D(nbttaglist3.h(0), nbttaglist3.h(1), nbttaglist3.h(2));
            NBTTagList nbttaglist4 = nbttagcompound1.getList("blockPos", 3);
            BlockPosition blockposition = new BlockPosition(nbttaglist4.e(0), nbttaglist4.e(1), nbttaglist4.e(2));

            if (nbttagcompound1.hasKey("nbt")) {
                NBTTagCompound nbttagcompound2 = nbttagcompound1.getCompound("nbt");

                this.b.add(new DefinedStructure.EntityInfo(vec3d, blockposition, nbttagcompound2));
            }
        }

    }

    private void a(NBTTagList nbttaglist, NBTTagList nbttaglist1) {
        DefinedStructure.a definedstructure_a = new DefinedStructure.a();
        List<DefinedStructure.BlockInfo> list = Lists.newArrayList();

        int i;

        for (i = 0; i < nbttaglist.size(); ++i) {
            definedstructure_a.a(GameProfileSerializer.d(nbttaglist.getCompound(i)), i);
        }

        for (i = 0; i < nbttaglist1.size(); ++i) {
            NBTTagCompound nbttagcompound = nbttaglist1.getCompound(i);
            NBTTagList nbttaglist2 = nbttagcompound.getList("pos", 3);
            BlockPosition blockposition = new BlockPosition(nbttaglist2.e(0), nbttaglist2.e(1), nbttaglist2.e(2));
            IBlockData iblockdata = definedstructure_a.a(nbttagcompound.getInt("state"));
            NBTTagCompound nbttagcompound1;

            if (nbttagcompound.hasKey("nbt")) {
                nbttagcompound1 = nbttagcompound.getCompound("nbt");
            } else {
                nbttagcompound1 = null;
            }

            list.add(new DefinedStructure.BlockInfo(blockposition, iblockdata, nbttagcompound1));
        }

        list.sort(Comparator.comparingInt((definedstructure_blockinfo) -> {
            return definedstructure_blockinfo.a.getY();
        }));
        this.a.add(list);
    }

    private NBTTagList a(int... aint) {
        NBTTagList nbttaglist = new NBTTagList();
        int[] aint1 = aint;
        int i = aint.length;

        for (int j = 0; j < i; ++j) {
            int k = aint1[j];

            nbttaglist.add(new NBTTagInt(k));
        }

        return nbttaglist;
    }

    private NBTTagList a(double... adouble) {
        NBTTagList nbttaglist = new NBTTagList();
        double[] adouble1 = adouble;
        int i = adouble.length;

        for (int j = 0; j < i; ++j) {
            double d0 = adouble1[j];

            nbttaglist.add(new NBTTagDouble(d0));
        }

        return nbttaglist;
    }

    public static class EntityInfo {

        public final Vec3D a;
        public final BlockPosition b;
        public final NBTTagCompound c;

        public EntityInfo(Vec3D vec3d, BlockPosition blockposition, NBTTagCompound nbttagcompound) {
            this.a = vec3d;
            this.b = blockposition;
            this.c = nbttagcompound;
        }
    }

    public static class BlockInfo {

        public final BlockPosition a;
        public final IBlockData b;
        public final NBTTagCompound c;

        public BlockInfo(BlockPosition blockposition, IBlockData iblockdata, @Nullable NBTTagCompound nbttagcompound) {
            this.a = blockposition;
            this.b = iblockdata;
            this.c = nbttagcompound;
        }

        public String toString() {
            return String.format("<StructureBlockInfo | %s | %s | %s>", this.a, this.b, this.c);
        }
    }

    static class a implements Iterable<IBlockData> {

        public static final IBlockData a = Blocks.AIR.getBlockData();
        private final RegistryBlockID<IBlockData> b;
        private int c;

        private a() {
            this.b = new RegistryBlockID<>(16);
        }

        public int a(IBlockData iblockdata) {
            int i = this.b.getId(iblockdata);

            if (i == -1) {
                i = this.c++;
                this.b.a(iblockdata, i);
            }

            return i;
        }

        @Nullable
        public IBlockData a(int i) {
            IBlockData iblockdata = (IBlockData) this.b.fromId(i);

            return iblockdata == null ? a : iblockdata; // CraftBukkit - decompile error
        }

        public Iterator<IBlockData> iterator() {
            return this.b.iterator();
        }

        public void a(IBlockData iblockdata, int i) {
            this.b.a(iblockdata, i);
        }
    }
}
