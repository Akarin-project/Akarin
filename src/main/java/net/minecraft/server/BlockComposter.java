package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2FloatMap;
import it.unimi.dsi.fastutil.objects.Object2FloatOpenHashMap;
import java.util.Random;
import javax.annotation.Nullable;
import org.bukkit.craftbukkit.inventory.CraftBlockInventoryHolder; // CraftBukkit

public class BlockComposter extends Block implements IInventoryHolder {

    public static final BlockStateInteger a = BlockProperties.am;
    public static final Object2FloatMap<IMaterial> b = new Object2FloatOpenHashMap();
    public static final VoxelShape c = VoxelShapes.b();
    private static final VoxelShape[] d = (VoxelShape[]) SystemUtils.a((new VoxelShape[9]), (avoxelshape) -> { // CraftBukkit - decompile error
        for (int i = 0; i < 8; ++i) {
            avoxelshape[i] = VoxelShapes.a(BlockComposter.c, Block.a(2.0D, (double) Math.max(2, 1 + i * 2), 2.0D, 14.0D, 16.0D, 14.0D), OperatorBoolean.ONLY_FIRST);
        }

        avoxelshape[8] = avoxelshape[7];
    });

    public static void d() {
        BlockComposter.b.defaultReturnValue(-1.0F);
        float f = 0.3F;
        float f1 = 0.5F;
        float f2 = 0.65F;
        float f3 = 0.85F;
        float f4 = 1.0F;

        a(0.3F, Items.ah);
        a(0.3F, Items.ae);
        a(0.3F, Items.af);
        a(0.3F, Items.aj);
        a(0.3F, Items.ai);
        a(0.3F, Items.ag);
        a(0.3F, Items.t);
        a(0.3F, Items.u);
        a(0.3F, Items.v);
        a(0.3F, Items.w);
        a(0.3F, Items.x);
        a(0.3F, Items.y);
        a(0.3F, Items.BEETROOT_SEEDS);
        a(0.3F, Items.DRIED_KELP);
        a(0.3F, Items.ay);
        a(0.3F, Items.kO);
        a(0.3F, Items.MELON_SEEDS);
        a(0.3F, Items.PUMPKIN_SEEDS);
        a(0.3F, Items.aB);
        a(0.3F, Items.SWEET_BERRIES);
        a(0.3F, Items.WHEAT_SEEDS);
        a(0.5F, Items.kP);
        a(0.5F, Items.fp);
        a(0.5F, Items.cw);
        a(0.5F, Items.kN);
        a(0.5F, Items.dh);
        a(0.5F, Items.MELON_SLICE);
        a(0.65F, Items.aC);
        a(0.65F, Items.dr);
        a(0.65F, Items.cF);
        a(0.65F, Items.cG);
        a(0.65F, Items.dg);
        a(0.65F, Items.APPLE);
        a(0.65F, Items.BEETROOT);
        a(0.65F, Items.CARROT);
        a(0.65F, Items.COCOA_BEANS);
        a(0.65F, Items.POTATO);
        a(0.65F, Items.WHEAT);
        a(0.65F, Items.bh);
        a(0.65F, Items.bi);
        a(0.65F, Items.dd);
        a(0.65F, Items.aU);
        a(0.65F, Items.aV);
        a(0.65F, Items.aW);
        a(0.65F, Items.aX);
        a(0.65F, Items.aY);
        a(0.65F, Items.aZ);
        a(0.65F, Items.ba);
        a(0.65F, Items.bb);
        a(0.65F, Items.bc);
        a(0.65F, Items.bd);
        a(0.65F, Items.be);
        a(0.65F, Items.bf);
        a(0.65F, Items.bg);
        a(0.65F, Items.az);
        a(0.65F, Items.fl);
        a(0.65F, Items.fm);
        a(0.65F, Items.fn);
        a(0.65F, Items.fo);
        a(0.65F, Items.fq);
        a(0.85F, Items.eN);
        a(0.85F, Items.db);
        a(0.85F, Items.dc);
        a(0.85F, Items.BREAD);
        a(0.85F, Items.BAKED_POTATO);
        a(0.85F, Items.COOKIE);
        a(1.0F, Items.lD);
        a(1.0F, Items.PUMPKIN_PIE);
    }

    private static void a(float f, IMaterial imaterial) {
        BlockComposter.b.put(imaterial.getItem(), f);
    }

    public BlockComposter(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockComposter.a, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockComposter.d[(Integer) iblockdata.get(BlockComposter.a)];
    }

    @Override
    public VoxelShape i(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockComposter.c;
    }

    @Override
    public VoxelShape b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return BlockComposter.d[0];
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if ((Integer) iblockdata.get(BlockComposter.a) == 7) {
            world.getBlockTickList().a(blockposition, iblockdata.getBlock(), 20);
        }

    }

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        int i = (Integer) iblockdata.get(BlockComposter.a);
        ItemStack itemstack = entityhuman.b(enumhand);

        if (i < 8 && BlockComposter.b.containsKey(itemstack.getItem())) {
            if (i < 7 && !world.isClientSide) {
                boolean flag = b(iblockdata, (GeneratorAccess) world, blockposition, itemstack);

                world.triggerEffect(1500, blockposition, flag ? 1 : 0);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }
            }

            return true;
        } else if (i == 8) {
            if (!world.isClientSide) {
                float f = 0.7F;
                double d0 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                double d1 = (double) (world.random.nextFloat() * 0.7F) + 0.06000000238418579D + 0.6D;
                double d2 = (double) (world.random.nextFloat() * 0.7F) + 0.15000000596046448D;
                EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, new ItemStack(Items.BONE_MEAL));

                entityitem.defaultPickupDelay();
                world.addEntity(entityitem);
            }

            d(iblockdata, (GeneratorAccess) world, blockposition);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BLOCK_COMPOSTER_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
            return true;
        } else {
            return false;
        }
    }

    private static void d(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockComposter.a, 0), 3);
    }

    private static boolean b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, ItemStack itemstack) {
        int i = (Integer) iblockdata.get(BlockComposter.a);
        float f = BlockComposter.b.getFloat(itemstack.getItem());

        if ((i != 0 || f <= 0.0F) && generatoraccess.getRandom().nextDouble() >= (double) f) {
            return false;
        } else {
            int j = i + 1;

            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockComposter.a, j), 3);
            if (j == 7) {
                generatoraccess.getBlockTickList().a(blockposition, iblockdata.getBlock(), 20);
            }

            return true;
        }
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if ((Integer) iblockdata.get(BlockComposter.a) == 7) {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockComposter.a), 3);
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BLOCK_COMPOSTER_READY, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }

        super.tick(iblockdata, world, blockposition, random);
    }

    @Override
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    @Override
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockComposter.a);
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockComposter.a);
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }

    @Override
    public IWorldInventory a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        int i = (Integer) iblockdata.get(BlockComposter.a);

        // CraftBukkit - empty generatoraccess, blockposition
        return (IWorldInventory) (i == 8 ? new BlockComposter.ContainerOutput(iblockdata, generatoraccess, blockposition, new ItemStack(Items.BONE_MEAL)) : (i < 7 ? new BlockComposter.ContainerInput(iblockdata, generatoraccess, blockposition) : new BlockComposter.ContainerEmpty(generatoraccess, blockposition)));
    }

    static class ContainerInput extends InventorySubcontainer implements IWorldInventory {

        private final IBlockData a;
        private final GeneratorAccess b;
        private final BlockPosition c;
        private boolean d;

        public ContainerInput(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
            super(1);
            this.bukkitOwner = new CraftBlockInventoryHolder(generatoraccess, blockposition, this); // CraftBukkit
            this.a = iblockdata;
            this.b = generatoraccess;
            this.c = blockposition;
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(EnumDirection enumdirection) {
            return enumdirection == EnumDirection.UP ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
            return !this.d && enumdirection == EnumDirection.UP && BlockComposter.b.containsKey(itemstack.getItem());
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
            return false;
        }

        @Override
        public void update() {
            ItemStack itemstack = this.getItem(0);

            if (!itemstack.isEmpty()) {
                this.d = true;
                BlockComposter.b(this.a, this.b, this.c, itemstack);
                this.splitWithoutUpdate(0);
            }

        }
    }

    static class ContainerOutput extends InventorySubcontainer implements IWorldInventory {

        private final IBlockData a;
        private final GeneratorAccess b;
        private final BlockPosition c;
        private boolean d;

        public ContainerOutput(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, ItemStack itemstack) {
            super(itemstack);
            this.a = iblockdata;
            this.b = generatoraccess;
            this.c = blockposition;
            this.bukkitOwner = new CraftBlockInventoryHolder(generatoraccess, blockposition, this); // CraftBukkit
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public int[] getSlotsForFace(EnumDirection enumdirection) {
            return enumdirection == EnumDirection.DOWN ? new int[]{0} : new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
            return !this.d && enumdirection == EnumDirection.DOWN && itemstack.getItem() == Items.BONE_MEAL;
        }

        @Override
        public void update() {
            BlockComposter.d(this.a, this.b, this.c);
            this.d = true;
        }
    }

    static class ContainerEmpty extends InventorySubcontainer implements IWorldInventory {

        public ContainerEmpty(GeneratorAccess generatoraccess, BlockPosition blockposition) { // CraftBukkit
            super(0);
            this.bukkitOwner = new CraftBlockInventoryHolder(generatoraccess, blockposition, this); // CraftBukkit
        }

        @Override
        public int[] getSlotsForFace(EnumDirection enumdirection) {
            return new int[0];
        }

        @Override
        public boolean canPlaceItemThroughFace(int i, ItemStack itemstack, @Nullable EnumDirection enumdirection) {
            return false;
        }

        @Override
        public boolean canTakeItemThroughFace(int i, ItemStack itemstack, EnumDirection enumdirection) {
            return false;
        }
    }
}
