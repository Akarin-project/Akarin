package net.minecraft.server;

import com.google.common.cache.LoadingCache;
import java.util.Random;
import javax.annotation.Nullable;

public class BlockPortal extends Block {

    public static final BlockStateEnum<EnumDirection.EnumAxis> AXIS = BlockProperties.z;
    protected static final VoxelShape b = Block.a(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
    protected static final VoxelShape c = Block.a(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);

    public BlockPortal(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPortal.AXIS, EnumDirection.EnumAxis.X));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        switch ((EnumDirection.EnumAxis) iblockdata.get(BlockPortal.AXIS)) {
        case Z:
            return BlockPortal.c;
        case X:
        default:
            return BlockPortal.b;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.worldProvider.isOverworld() && world.getGameRules().getBoolean("doMobSpawning") && random.nextInt(2000) < world.getDifficulty().a()) {
            int i = blockposition.getY();

            BlockPosition blockposition1;

            for (blockposition1 = blockposition; !world.getType(blockposition1).q() && blockposition1.getY() > 0; blockposition1 = blockposition1.down()) {
                ;
            }

            if (i > 0 && !world.getType(blockposition1.up()).isOccluding()) {
                Entity entity = EntityTypes.ZOMBIE_PIGMAN.a(world, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition1.up(), false, false);

                if (entity != null) {
                    entity.portalCooldown = entity.aQ();
                }
            }
        }

    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public boolean a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPortal.Shape blockportal_shape = this.b(generatoraccess, blockposition);

        if (blockportal_shape != null) {
            blockportal_shape.createPortal();
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public BlockPortal.Shape b(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        BlockPortal.Shape blockportal_shape = new BlockPortal.Shape(generatoraccess, blockposition, EnumDirection.EnumAxis.X);

        if (blockportal_shape.d() && blockportal_shape.e == 0) {
            return blockportal_shape;
        } else {
            BlockPortal.Shape blockportal_shape1 = new BlockPortal.Shape(generatoraccess, blockposition, EnumDirection.EnumAxis.Z);

            return blockportal_shape1.d() && blockportal_shape1.e == 0 ? blockportal_shape1 : null;
        }
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        EnumDirection.EnumAxis enumdirection_enumaxis = enumdirection.k();
        EnumDirection.EnumAxis enumdirection_enumaxis1 = (EnumDirection.EnumAxis) iblockdata.get(BlockPortal.AXIS);
        boolean flag = enumdirection_enumaxis1 != enumdirection_enumaxis && enumdirection_enumaxis.c();

        return !flag && iblockdata1.getBlock() != this && !(new BlockPortal.Shape(generatoraccess, blockposition, enumdirection_enumaxis1)).f() ? Blocks.AIR.getBlockData() : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public TextureType c() {
        return TextureType.TRANSLUCENT;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isPassenger() && !entity.isVehicle() && entity.bm()) {
            entity.e(blockposition);
        }

    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return ItemStack.a;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        switch (enumblockrotation) {
        case COUNTERCLOCKWISE_90:
        case CLOCKWISE_90:
            switch ((EnumDirection.EnumAxis) iblockdata.get(BlockPortal.AXIS)) {
            case Z:
                return (IBlockData) iblockdata.set(BlockPortal.AXIS, EnumDirection.EnumAxis.X);
            case X:
                return (IBlockData) iblockdata.set(BlockPortal.AXIS, EnumDirection.EnumAxis.Z);
            default:
                return iblockdata;
            }
        default:
            return iblockdata;
        }
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPortal.AXIS);
    }

    public ShapeDetector.ShapeDetectorCollection c(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        EnumDirection.EnumAxis enumdirection_enumaxis = EnumDirection.EnumAxis.Z;
        BlockPortal.Shape blockportal_shape = new BlockPortal.Shape(generatoraccess, blockposition, EnumDirection.EnumAxis.X);
        LoadingCache<BlockPosition, ShapeDetectorBlock> loadingcache = ShapeDetector.a(generatoraccess, true);

        if (!blockportal_shape.d()) {
            enumdirection_enumaxis = EnumDirection.EnumAxis.X;
            blockportal_shape = new BlockPortal.Shape(generatoraccess, blockposition, EnumDirection.EnumAxis.Z);
        }

        if (!blockportal_shape.d()) {
            return new ShapeDetector.ShapeDetectorCollection(blockposition, EnumDirection.NORTH, EnumDirection.UP, loadingcache, 1, 1, 1);
        } else {
            int[] aint = new int[EnumDirection.EnumAxisDirection.values().length];
            EnumDirection enumdirection = blockportal_shape.c.f();
            BlockPosition blockposition1 = blockportal_shape.position.up(blockportal_shape.a() - 1);
            EnumDirection.EnumAxisDirection[] aenumdirection_enumaxisdirection = EnumDirection.EnumAxisDirection.values();
            int i = aenumdirection_enumaxisdirection.length;

            int j;

            for (j = 0; j < i; ++j) {
                EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection = aenumdirection_enumaxisdirection[j];
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = new ShapeDetector.ShapeDetectorCollection(enumdirection.c() == enumdirection_enumaxisdirection ? blockposition1 : blockposition1.shift(blockportal_shape.c, blockportal_shape.b() - 1), EnumDirection.a(enumdirection_enumaxisdirection, enumdirection_enumaxis), EnumDirection.UP, loadingcache, blockportal_shape.b(), blockportal_shape.a(), 1);

                for (int k = 0; k < blockportal_shape.b(); ++k) {
                    for (int l = 0; l < blockportal_shape.a(); ++l) {
                        ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(k, l, 1);

                        if (!shapedetectorblock.a().isAir()) {
                            ++aint[enumdirection_enumaxisdirection.ordinal()];
                        }
                    }
                }
            }

            EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection1 = EnumDirection.EnumAxisDirection.POSITIVE;
            EnumDirection.EnumAxisDirection[] aenumdirection_enumaxisdirection1 = EnumDirection.EnumAxisDirection.values();

            j = aenumdirection_enumaxisdirection1.length;

            for (int i1 = 0; i1 < j; ++i1) {
                EnumDirection.EnumAxisDirection enumdirection_enumaxisdirection2 = aenumdirection_enumaxisdirection1[i1];

                if (aint[enumdirection_enumaxisdirection2.ordinal()] < aint[enumdirection_enumaxisdirection1.ordinal()]) {
                    enumdirection_enumaxisdirection1 = enumdirection_enumaxisdirection2;
                }
            }

            return new ShapeDetector.ShapeDetectorCollection(enumdirection.c() == enumdirection_enumaxisdirection1 ? blockposition1 : blockposition1.shift(blockportal_shape.c, blockportal_shape.b() - 1), EnumDirection.a(enumdirection_enumaxisdirection1, enumdirection_enumaxis), EnumDirection.UP, loadingcache, blockportal_shape.b(), blockportal_shape.a(), 1);
        }
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    public static class Shape {

        private final GeneratorAccess a;
        private final EnumDirection.EnumAxis b;
        private final EnumDirection c;
        private final EnumDirection d;
        private int e;
        private BlockPosition position;
        private int height;
        private int width;

        public Shape(GeneratorAccess generatoraccess, BlockPosition blockposition, EnumDirection.EnumAxis enumdirection_enumaxis) {
            this.a = generatoraccess;
            this.b = enumdirection_enumaxis;
            if (enumdirection_enumaxis == EnumDirection.EnumAxis.X) {
                this.d = EnumDirection.EAST;
                this.c = EnumDirection.WEST;
            } else {
                this.d = EnumDirection.NORTH;
                this.c = EnumDirection.SOUTH;
            }

            for (BlockPosition blockposition1 = blockposition; blockposition.getY() > blockposition1.getY() - 21 && blockposition.getY() > 0 && this.a(generatoraccess.getType(blockposition.down())); blockposition = blockposition.down()) {
                ;
            }

            int i = this.a(blockposition, this.d) - 1;

            if (i >= 0) {
                this.position = blockposition.shift(this.d, i);
                this.width = this.a(this.position, this.c);
                if (this.width < 2 || this.width > 21) {
                    this.position = null;
                    this.width = 0;
                }
            }

            if (this.position != null) {
                this.height = this.c();
            }

        }

        protected int a(BlockPosition blockposition, EnumDirection enumdirection) {
            int i;

            for (i = 0; i < 22; ++i) {
                BlockPosition blockposition1 = blockposition.shift(enumdirection, i);

                if (!this.a(this.a.getType(blockposition1)) || this.a.getType(blockposition1.down()).getBlock() != Blocks.OBSIDIAN) {
                    break;
                }
            }

            Block block = this.a.getType(blockposition.shift(enumdirection, i)).getBlock();

            return block == Blocks.OBSIDIAN ? i : 0;
        }

        public int a() {
            return this.height;
        }

        public int b() {
            return this.width;
        }

        protected int c() {
            int i;

            label56:
            for (this.height = 0; this.height < 21; ++this.height) {
                for (i = 0; i < this.width; ++i) {
                    BlockPosition blockposition = this.position.shift(this.c, i).up(this.height);
                    IBlockData iblockdata = this.a.getType(blockposition);

                    if (!this.a(iblockdata)) {
                        break label56;
                    }

                    Block block = iblockdata.getBlock();

                    if (block == Blocks.NETHER_PORTAL) {
                        ++this.e;
                    }

                    if (i == 0) {
                        block = this.a.getType(blockposition.shift(this.d)).getBlock();
                        if (block != Blocks.OBSIDIAN) {
                            break label56;
                        }
                    } else if (i == this.width - 1) {
                        block = this.a.getType(blockposition.shift(this.c)).getBlock();
                        if (block != Blocks.OBSIDIAN) {
                            break label56;
                        }
                    }
                }
            }

            for (i = 0; i < this.width; ++i) {
                if (this.a.getType(this.position.shift(this.c, i).up(this.height)).getBlock() != Blocks.OBSIDIAN) {
                    this.height = 0;
                    break;
                }
            }

            if (this.height <= 21 && this.height >= 3) {
                return this.height;
            } else {
                this.position = null;
                this.width = 0;
                this.height = 0;
                return 0;
            }
        }

        protected boolean a(IBlockData iblockdata) {
            Block block = iblockdata.getBlock();

            return iblockdata.isAir() || block == Blocks.FIRE || block == Blocks.NETHER_PORTAL;
        }

        public boolean d() {
            return this.position != null && this.width >= 2 && this.width <= 21 && this.height >= 3 && this.height <= 21;
        }

        public void createPortal() {
            for (int i = 0; i < this.width; ++i) {
                BlockPosition blockposition = this.position.shift(this.c, i);

                for (int j = 0; j < this.height; ++j) {
                    this.a.setTypeAndData(blockposition.up(j), (IBlockData) Blocks.NETHER_PORTAL.getBlockData().set(BlockPortal.AXIS, this.b), 18);
                }
            }

        }

        private boolean g() {
            return this.e >= this.width * this.height;
        }

        public boolean f() {
            return this.d() && this.g();
        }
    }
}
