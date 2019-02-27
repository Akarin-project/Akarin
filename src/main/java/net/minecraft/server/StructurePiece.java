package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;

public abstract class StructurePiece {

    protected static final IBlockData m = Blocks.CAVE_AIR.getBlockData();
    protected StructureBoundingBox n;
    @Nullable
    private EnumDirection a;
    private EnumBlockMirror b;
    private EnumBlockRotation c;
    protected int o;
    private static final Set<Block> d = ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

    public StructurePiece() {}

    protected StructurePiece(int i) {
        this.o = i;
    }

    public final NBTTagCompound c() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("id", WorldGenFactory.a(this));
        nbttagcompound.set("BB", this.n.g());
        EnumDirection enumdirection = this.f();

        nbttagcompound.setInt("O", enumdirection == null ? -1 : enumdirection.get2DRotationValue());
        nbttagcompound.setInt("GD", this.o);
        this.a(nbttagcompound);
        return nbttagcompound;
    }

    protected abstract void a(NBTTagCompound nbttagcompound);

    public void a(GeneratorAccess generatoraccess, NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKey("BB")) {
            this.n = new StructureBoundingBox(nbttagcompound.getIntArray("BB"));
        }

        int i = nbttagcompound.getInt("O");

        this.a(i == -1 ? null : EnumDirection.fromType2(i));
        this.o = nbttagcompound.getInt("GD");
        this.a(nbttagcompound, generatoraccess.getDataManager().h());
    }

    protected abstract void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager);

    public void a(StructurePiece structurepiece, List<StructurePiece> list, Random random) {}

    public abstract boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair);

    public StructureBoundingBox d() {
        return this.n;
    }

    public int e() {
        return this.o;
    }

    public static StructurePiece a(List<StructurePiece> list, StructureBoundingBox structureboundingbox) {
        Iterator iterator = list.iterator();

        StructurePiece structurepiece;

        do {
            if (!iterator.hasNext()) {
                return null;
            }

            structurepiece = (StructurePiece) iterator.next();
        } while (structurepiece.d() == null || !structurepiece.d().a(structureboundingbox));

        return structurepiece;
    }

    protected boolean a(IBlockAccess iblockaccess, StructureBoundingBox structureboundingbox) {
        int i = Math.max(this.n.a - 1, structureboundingbox.a);
        int j = Math.max(this.n.b - 1, structureboundingbox.b);
        int k = Math.max(this.n.c - 1, structureboundingbox.c);
        int l = Math.min(this.n.d + 1, structureboundingbox.d);
        int i1 = Math.min(this.n.e + 1, structureboundingbox.e);
        int j1 = Math.min(this.n.f + 1, structureboundingbox.f);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        int k1;
        int l1;

        for (k1 = i; k1 <= l; ++k1) {
            for (l1 = k; l1 <= j1; ++l1) {
                if (iblockaccess.getType(blockposition_mutableblockposition.c(k1, j, l1)).getMaterial().isLiquid()) {
                    return true;
                }

                if (iblockaccess.getType(blockposition_mutableblockposition.c(k1, i1, l1)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }

        for (k1 = i; k1 <= l; ++k1) {
            for (l1 = j; l1 <= i1; ++l1) {
                if (iblockaccess.getType(blockposition_mutableblockposition.c(k1, l1, k)).getMaterial().isLiquid()) {
                    return true;
                }

                if (iblockaccess.getType(blockposition_mutableblockposition.c(k1, l1, j1)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }

        for (k1 = k; k1 <= j1; ++k1) {
            for (l1 = j; l1 <= i1; ++l1) {
                if (iblockaccess.getType(blockposition_mutableblockposition.c(i, l1, k1)).getMaterial().isLiquid()) {
                    return true;
                }

                if (iblockaccess.getType(blockposition_mutableblockposition.c(l, l1, k1)).getMaterial().isLiquid()) {
                    return true;
                }
            }
        }

        return false;
    }

    protected int a(int i, int j) {
        EnumDirection enumdirection = this.f();

        if (enumdirection == null) {
            return i;
        } else {
            switch (enumdirection) {
            case NORTH:
            case SOUTH:
                return this.n.a + i;
            case WEST:
                return this.n.d - j;
            case EAST:
                return this.n.a + j;
            default:
                return i;
            }
        }
    }

    protected int d(int i) {
        return this.f() == null ? i : i + this.n.b;
    }

    protected int b(int i, int j) {
        EnumDirection enumdirection = this.f();

        if (enumdirection == null) {
            return j;
        } else {
            switch (enumdirection) {
            case NORTH:
                return this.n.f - j;
            case SOUTH:
                return this.n.c + j;
            case WEST:
            case EAST:
                return this.n.c + i;
            default:
                return j;
            }
        }
    }

    protected void a(GeneratorAccess generatoraccess, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));

        if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
            if (this.b != EnumBlockMirror.NONE) {
                iblockdata = iblockdata.a(this.b);
            }

            if (this.c != EnumBlockRotation.NONE) {
                iblockdata = iblockdata.a(this.c);
            }

            generatoraccess.setTypeAndData(blockposition, iblockdata, 2);
            Fluid fluid = generatoraccess.getFluid(blockposition);

            if (!fluid.e()) {
                generatoraccess.getFluidTickList().a(blockposition, fluid.c(), 0);
            }

            if (StructurePiece.d.contains(iblockdata.getBlock())) {
                generatoraccess.y(blockposition).e(blockposition);
            }

        }
    }

    protected IBlockData a(IBlockAccess iblockaccess, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        int l = this.a(i, k);
        int i1 = this.d(j);
        int j1 = this.b(i, k);
        BlockPosition blockposition = new BlockPosition(l, i1, j1);

        return !structureboundingbox.b((BaseBlockPosition) blockposition) ? Blocks.AIR.getBlockData() : iblockaccess.getType(blockposition);
    }

    protected boolean a(IWorldReader iworldreader, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        int l = this.a(i, k);
        int i1 = this.d(j + 1);
        int j1 = this.b(i, k);
        BlockPosition blockposition = new BlockPosition(l, i1, j1);

        return !structureboundingbox.b((BaseBlockPosition) blockposition) ? false : i1 < iworldreader.a(HeightMap.Type.OCEAN_FLOOR_WG, l, j1);
    }

    protected void b(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    this.a(generatoraccess, Blocks.AIR.getBlockData(), l1, k1, i2, structureboundingbox);
                }
            }
        }

    }

    protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || !this.a((IBlockAccess) generatoraccess, l1, k1, i2, structureboundingbox).isAir()) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.a(generatoraccess, iblockdata1, l1, k1, i2, structureboundingbox);
                        } else {
                            this.a(generatoraccess, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, boolean flag, Random random, StructurePiece.StructurePieceBlockSelector structurepiece_structurepieceblockselector) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (!flag || !this.a((IBlockAccess) generatoraccess, l1, k1, i2, structureboundingbox).isAir()) {
                        structurepiece_structurepieceblockselector.a(random, l1, k1, i2, k1 == j || k1 == i1 || l1 == i || l1 == l || i2 == k || i2 == j1);
                        this.a(generatoraccess, structurepiece_structurepieceblockselector.a(), l1, k1, i2, structureboundingbox);
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, IBlockData iblockdata1, boolean flag, boolean flag1) {
        for (int k1 = j; k1 <= i1; ++k1) {
            for (int l1 = i; l1 <= l; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    if (random.nextFloat() <= f && (!flag || !this.a((IBlockAccess) generatoraccess, l1, k1, i2, structureboundingbox).isAir()) && (!flag1 || this.a((IWorldReader) generatoraccess, l1, k1, i2, structureboundingbox))) {
                        if (k1 != j && k1 != i1 && l1 != i && l1 != l && i2 != k && i2 != j1) {
                            this.a(generatoraccess, iblockdata1, l1, k1, i2, structureboundingbox);
                        } else {
                            this.a(generatoraccess, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Random random, float f, int i, int j, int k, IBlockData iblockdata) {
        if (random.nextFloat() < f) {
            this.a(generatoraccess, iblockdata, i, j, k, structureboundingbox);
        }

    }

    protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, int i, int j, int k, int l, int i1, int j1, IBlockData iblockdata, boolean flag) {
        float f = (float) (l - i + 1);
        float f1 = (float) (i1 - j + 1);
        float f2 = (float) (j1 - k + 1);
        float f3 = (float) i + f / 2.0F;
        float f4 = (float) k + f2 / 2.0F;

        for (int k1 = j; k1 <= i1; ++k1) {
            float f5 = (float) (k1 - j) / f1;

            for (int l1 = i; l1 <= l; ++l1) {
                float f6 = ((float) l1 - f3) / (f * 0.5F);

                for (int i2 = k; i2 <= j1; ++i2) {
                    float f7 = ((float) i2 - f4) / (f2 * 0.5F);

                    if (!flag || !this.a((IBlockAccess) generatoraccess, l1, k1, i2, structureboundingbox).isAir()) {
                        float f8 = f6 * f6 + f5 * f5 + f7 * f7;

                        if (f8 <= 1.05F) {
                            this.a(generatoraccess, iblockdata, l1, k1, i2, structureboundingbox);
                        }
                    }
                }
            }
        }

    }

    protected void a(GeneratorAccess generatoraccess, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));

        if (structureboundingbox.b((BaseBlockPosition) blockposition)) {
            while (!generatoraccess.isEmpty(blockposition) && blockposition.getY() < 255) {
                generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
                blockposition = blockposition.up();
            }

        }
    }

    protected void b(GeneratorAccess generatoraccess, IBlockData iblockdata, int i, int j, int k, StructureBoundingBox structureboundingbox) {
        int l = this.a(i, k);
        int i1 = this.d(j);
        int j1 = this.b(i, k);

        if (structureboundingbox.b((BaseBlockPosition) (new BlockPosition(l, i1, j1)))) {
            while ((generatoraccess.isEmpty(new BlockPosition(l, i1, j1)) || generatoraccess.getType(new BlockPosition(l, i1, j1)).getMaterial().isLiquid()) && i1 > 1) {
                generatoraccess.setTypeAndData(new BlockPosition(l, i1, j1), iblockdata, 2);
                --i1;
            }

        }
    }

    protected boolean a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, MinecraftKey minecraftkey) {
        BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));

        return this.a(generatoraccess, structureboundingbox, random, blockposition, minecraftkey, (IBlockData) null);
    }

    public static IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        EnumDirection enumdirection = null;
        Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection1 = (EnumDirection) iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection1);
            IBlockData iblockdata1 = iblockaccess.getType(blockposition1);

            if (iblockdata1.getBlock() == Blocks.CHEST) {
                return iblockdata;
            }

            if (iblockdata1.f(iblockaccess, blockposition1)) {
                if (enumdirection != null) {
                    enumdirection = null;
                    break;
                }

                enumdirection = enumdirection1;
            }
        }

        if (enumdirection != null) {
            return (IBlockData) iblockdata.set(BlockFacingHorizontal.FACING, enumdirection.opposite());
        } else {
            EnumDirection enumdirection2 = (EnumDirection) iblockdata.get(BlockFacingHorizontal.FACING);
            BlockPosition blockposition2 = blockposition.shift(enumdirection2);

            if (iblockaccess.getType(blockposition2).f(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.opposite();
                blockposition2 = blockposition.shift(enumdirection2);
            }

            if (iblockaccess.getType(blockposition2).f(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.e();
                blockposition2 = blockposition.shift(enumdirection2);
            }

            if (iblockaccess.getType(blockposition2).f(iblockaccess, blockposition2)) {
                enumdirection2 = enumdirection2.opposite();
                blockposition.shift(enumdirection2);
            }

            return (IBlockData) iblockdata.set(BlockFacingHorizontal.FACING, enumdirection2);
        }
    }

    protected boolean a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Random random, BlockPosition blockposition, MinecraftKey minecraftkey, @Nullable IBlockData iblockdata) {
        if (structureboundingbox.b((BaseBlockPosition) blockposition) && generatoraccess.getType(blockposition).getBlock() != Blocks.CHEST) {
            if (iblockdata == null) {
                iblockdata = a((IBlockAccess) generatoraccess, blockposition, Blocks.CHEST.getBlockData());
            }

            generatoraccess.setTypeAndData(blockposition, iblockdata, 2);
            TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityChest) {
                ((TileEntityChest) tileentity).setLootTable(minecraftkey, random.nextLong());
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, EnumDirection enumdirection, MinecraftKey minecraftkey) {
        BlockPosition blockposition = new BlockPosition(this.a(i, k), this.d(j), this.b(i, k));

        if (structureboundingbox.b((BaseBlockPosition) blockposition) && generatoraccess.getType(blockposition).getBlock() != Blocks.DISPENSER) {
            this.a(generatoraccess, (IBlockData) Blocks.DISPENSER.getBlockData().set(BlockDispenser.FACING, enumdirection), i, j, k, structureboundingbox);
            TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityDispenser) {
                ((TileEntityDispenser) tileentity).setLootTable(minecraftkey, random.nextLong());
            }

            return true;
        } else {
            return false;
        }
    }

    protected void a(GeneratorAccess generatoraccess, StructureBoundingBox structureboundingbox, Random random, int i, int j, int k, EnumDirection enumdirection, BlockDoor blockdoor) {
        this.a(generatoraccess, (IBlockData) blockdoor.getBlockData().set(BlockDoor.FACING, enumdirection), i, j, k, structureboundingbox);
        this.a(generatoraccess, (IBlockData) ((IBlockData) blockdoor.getBlockData().set(BlockDoor.FACING, enumdirection)).set(BlockDoor.HALF, BlockPropertyDoubleBlockHalf.UPPER), i, j + 1, k, structureboundingbox);
    }

    public void a(int i, int j, int k) {
        this.n.a(i, j, k);
    }

    @Nullable
    public EnumDirection f() {
        return this.a;
    }

    public void a(@Nullable EnumDirection enumdirection) {
        this.a = enumdirection;
        if (enumdirection == null) {
            this.c = EnumBlockRotation.NONE;
            this.b = EnumBlockMirror.NONE;
        } else {
            switch (enumdirection) {
            case SOUTH:
                this.b = EnumBlockMirror.LEFT_RIGHT;
                this.c = EnumBlockRotation.NONE;
                break;
            case WEST:
                this.b = EnumBlockMirror.LEFT_RIGHT;
                this.c = EnumBlockRotation.CLOCKWISE_90;
                break;
            case EAST:
                this.b = EnumBlockMirror.NONE;
                this.c = EnumBlockRotation.CLOCKWISE_90;
                break;
            default:
                this.b = EnumBlockMirror.NONE;
                this.c = EnumBlockRotation.NONE;
            }
        }

    }

    public abstract static class StructurePieceBlockSelector {

        protected IBlockData a;

        protected StructurePieceBlockSelector() {
            this.a = Blocks.AIR.getBlockData();
        }

        public abstract void a(Random random, int i, int j, int k, boolean flag);

        public IBlockData a() {
            return this.a;
        }
    }
}
