package net.minecraft.server;

import com.google.common.collect.UnmodifiableIterator;
import it.unimi.dsi.fastutil.objects.Object2ByteLinkedOpenHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Block implements IMaterial {

    protected static final Logger d = LogManager.getLogger();
    public static final RegistryBlockID<IBlockData> REGISTRY_ID = new RegistryBlockID<>();
    private static final EnumDirection[] a = new EnumDirection[] { EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH, EnumDirection.DOWN, EnumDirection.UP};
    protected final int f;
    public final float strength;
    protected final float durability;
    protected final boolean i;
    protected final SoundEffectType stepSound;
    protected final Material material;
    protected final MaterialMapColor l;
    private final float frictionFactor;
    protected final BlockStateList<Block, IBlockData> blockStateList;
    private IBlockData blockData;
    protected final boolean n;
    private final boolean o;
    @Nullable
    private String name;
    private static final ThreadLocal<Object2ByteLinkedOpenHashMap<Block.a>> q = ThreadLocal.withInitial(() -> {
        Object2ByteLinkedOpenHashMap<Block.a> object2bytelinkedopenhashmap = new Object2ByteLinkedOpenHashMap<Block.a>(200) {
            protected void rehash(int i) {}
        };

        object2bytelinkedopenhashmap.defaultReturnValue((byte) 127);
        return object2bytelinkedopenhashmap;
    });

    public static int getCombinedId(@Nullable IBlockData iblockdata) {
        if (iblockdata == null) {
            return 0;
        } else {
            int i = Block.REGISTRY_ID.getId(iblockdata);

            return i == -1 ? 0 : i;
        }
    }

    public static IBlockData getByCombinedId(int i) {
        IBlockData iblockdata = (IBlockData) Block.REGISTRY_ID.fromId(i);

        return iblockdata == null ? Blocks.AIR.getBlockData() : iblockdata;
    }

    public static Block asBlock(@Nullable Item item) {
        return item instanceof ItemBlock ? ((ItemBlock) item).getBlock() : Blocks.AIR;
    }

    public static IBlockData a(IBlockData iblockdata, IBlockData iblockdata1, World world, BlockPosition blockposition) {
        VoxelShape voxelshape = VoxelShapes.b(iblockdata.getCollisionShape(world, blockposition), iblockdata1.getCollisionShape(world, blockposition), OperatorBoolean.ONLY_SECOND).a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
        List<Entity> list = world.getEntities((Entity) null, voxelshape.getBoundingBox());
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();
            double d0 = VoxelShapes.a(EnumDirection.EnumAxis.Y, entity.getBoundingBox().d(0.0D, 1.0D, 0.0D), Stream.of(voxelshape), -1.0D);

            entity.enderTeleportTo(entity.locX, entity.locY + 1.0D + d0, entity.locZ);
        }

        return iblockdata1;
    }

    public static VoxelShape a(double d0, double d1, double d2, double d3, double d4, double d5) {
        return VoxelShapes.create(d0 / 16.0D, d1 / 16.0D, d2 / 16.0D, d3 / 16.0D, d4 / 16.0D, d5 / 16.0D);
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, Entity entity) {
        return true;
    }

    @Deprecated
    public boolean e(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public int m(IBlockData iblockdata) {
        return this.f;
    }

    @Deprecated
    public Material n(IBlockData iblockdata) {
        return this.material;
    }

    @Deprecated
    public MaterialMapColor c(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.l;
    }

    @Deprecated
    public void a(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        BlockPosition.b blockposition_b = BlockPosition.b.r();
        Throwable throwable = null;

        try {
            EnumDirection[] aenumdirection = Block.a;
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];

                blockposition_b.g(blockposition).c(enumdirection);
                IBlockData iblockdata1 = generatoraccess.getType(blockposition_b);
                IBlockData iblockdata2 = iblockdata1.updateState(enumdirection.opposite(), iblockdata, generatoraccess, blockposition_b, blockposition);

                a(iblockdata1, iblockdata2, generatoraccess, blockposition_b, i);
            }
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

    }

    public boolean a(Tag<Block> tag) {
        return tag.isTagged(this);
    }

    public static IBlockData b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        IBlockData iblockdata1 = iblockdata;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        EnumDirection[] aenumdirection = Block.a;
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            blockposition_mutableblockposition.g(blockposition).c(enumdirection);
            iblockdata1 = iblockdata1.updateState(enumdirection, generatoraccess.getType(blockposition_mutableblockposition), generatoraccess, blockposition, blockposition_mutableblockposition);
        }

        return iblockdata1;
    }

    public static void a(IBlockData iblockdata, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        if (iblockdata1 != iblockdata) {
            if (iblockdata1.isAir()) {
                if (!generatoraccess.e()) {
                    generatoraccess.setAir(blockposition, (i & 32) == 0);
                }
            } else {
                generatoraccess.setTypeAndData(blockposition, iblockdata1, i & -33);
            }
        }

    }

    @Deprecated
    public void b(IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {}

    @Deprecated
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return iblockdata;
    }

    @Deprecated
    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return iblockdata;
    }

    @Deprecated
    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata;
    }

    public Block(Block.Info block_info) {
        BlockStateList.a<Block, IBlockData> blockstatelist_a = new BlockStateList.a<>(this);

        this.a(blockstatelist_a);
        this.blockStateList = blockstatelist_a.a(BlockData::new);
        this.v((IBlockData) this.blockStateList.getBlockData());
        this.material = block_info.a;
        this.l = block_info.b;
        this.n = block_info.c;
        this.stepSound = block_info.d;
        this.f = block_info.e;
        this.durability = block_info.f;
        this.strength = block_info.g;
        this.i = block_info.h;
        this.frictionFactor = block_info.i;
        this.o = block_info.j;
    }

    protected static boolean a(Block block) {
        return block instanceof BlockShulkerBox || block instanceof BlockLeaves || block.a(TagsBlock.TRAPDOORS) || block instanceof BlockStainedGlass || block == Blocks.BEACON || block == Blocks.CAULDRON || block == Blocks.GLASS || block == Blocks.GLOWSTONE || block == Blocks.ICE || block == Blocks.SEA_LANTERN || block == Blocks.CONDUIT;
    }

    public static boolean b(Block block) {
        return a(block) || block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.PISTON_HEAD;
    }

    @Deprecated
    public boolean o(IBlockData iblockdata) {
        return iblockdata.getMaterial().isSolid() && iblockdata.g();
    }

    @Deprecated
    public boolean isOccluding(IBlockData iblockdata) {
        return iblockdata.getMaterial().f() && iblockdata.g() && !iblockdata.isPowerSource();
    }

    @Deprecated
    public boolean q(IBlockData iblockdata) {
        return this.material.isSolid() && iblockdata.g();
    }

    @Deprecated
    public boolean a(IBlockData iblockdata) {
        return true;
    }

    @Deprecated
    public boolean r(IBlockData iblockdata) {
        return iblockdata.getMaterial().f() && iblockdata.g();
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        switch (pathmode) {
        case LAND:
            return !a(this.f(iblockdata, iblockaccess, blockposition));
        case WATER:
            return iblockaccess.getFluid(blockposition).a(TagsFluid.WATER);
        case AIR:
            return !a(this.f(iblockdata, iblockaccess, blockposition));
        default:
            return false;
        }
    }

    @Deprecated
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return this.material.isReplaceable() && blockactioncontext.getItemStack().getItem() != this.getItem();
    }

    @Deprecated
    public float d(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.strength;
    }

    public boolean isTicking(IBlockData iblockdata) {
        return this.i;
    }

    public boolean isTileEntity() {
        return this instanceof ITileEntity;
    }

    @Deprecated
    public boolean e(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return false;
    }

    @Deprecated
    public boolean f(IBlockData iblockdata) {
        return this.n && iblockdata.getBlock().c() == TextureType.SOLID;
    }

    @Deprecated
    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.SOLID;
    }

    @Deprecated
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.b();
    }

    @Deprecated
    public VoxelShape f(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.n ? iblockdata.getShape(iblockaccess, blockposition) : VoxelShapes.a();
    }

    @Deprecated
    public VoxelShape g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.getShape(iblockaccess, blockposition);
    }

    @Deprecated
    public VoxelShape h(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return VoxelShapes.a();
    }

    public static boolean a(VoxelShape voxelshape, EnumDirection enumdirection) {
        VoxelShape voxelshape1 = voxelshape.a(enumdirection);

        return a(voxelshape1);
    }

    public static boolean a(VoxelShape voxelshape) {
        return !VoxelShapes.c(VoxelShapes.b(), voxelshape, OperatorBoolean.ONLY_FIRST);
    }

    @Deprecated
    public final boolean i(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = iblockdata.p();
        VoxelShape voxelshape = flag ? iblockdata.i(iblockaccess, blockposition) : VoxelShapes.a();

        return a(voxelshape);
    }

    public boolean a_(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !a(iblockdata.getShape(iblockaccess, blockposition)) && iblockdata.s().e();
    }

    @Deprecated
    public int j(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.f(iblockaccess, blockposition) ? iblockaccess.K() : (iblockdata.a(iblockaccess, blockposition) ? 0 : 1);
    }

    @Deprecated
    public final boolean k(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return !iblockdata.f(iblockaccess, blockposition) && iblockdata.b(iblockaccess, blockposition) == iblockaccess.K();
    }

    public boolean isCollidable(IBlockData iblockdata) {
        return this.j();
    }

    public boolean j() {
        return true;
    }

    @Deprecated
    public void b(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        this.a(iblockdata, world, blockposition, random);
    }

    @Deprecated
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {}

    public void postBreak(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata) {}

    @Deprecated
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {}

    public int a(IWorldReader iworldreader) {
        return 10;
    }

    @Deprecated
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {}

    @Deprecated
    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {}

    public int a(IBlockData iblockdata, Random random) {
        return 1;
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return this;
    }

    @Deprecated
    public float getDamage(IBlockData iblockdata, EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        float f = iblockdata.e(iblockaccess, blockposition);

        if (f == -1.0F) {
            return 0.0F;
        } else {
            int i = entityhuman.hasBlock(iblockdata) ? 30 : 100;

            return entityhuman.b(iblockdata) / f / (float) i;
        }
    }

    @Deprecated
    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (!world.isClientSide) {
            int j = this.getDropCount(iblockdata, i, world, blockposition, world.random);

            for (int k = 0; k < j; ++k) {
                if (f >= 1.0F || world.random.nextFloat() <= f) {
                    Item item = this.getDropType(iblockdata, world, blockposition, i).getItem();

                    if (item != Items.AIR) {
                        a(world, blockposition, new ItemStack(item));
                    }
                }
            }

        }
    }

    public static void a(World world, BlockPosition blockposition, ItemStack itemstack) {
        if (!world.isClientSide && !itemstack.isEmpty() && world.getGameRules().getBoolean("doTileDrops")) {
            float f = 0.5F;
            double d0 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            double d1 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            double d2 = (double) (world.random.nextFloat() * 0.5F) + 0.25D;
            EntityItem entityitem = new EntityItem(world, (double) blockposition.getX() + d0, (double) blockposition.getY() + d1, (double) blockposition.getZ() + d2, itemstack);

            entityitem.n();
            world.addEntity(entityitem);
        }
    }

    protected void dropExperience(World world, BlockPosition blockposition, int i) {
        if (!world.isClientSide && world.getGameRules().getBoolean("doTileDrops")) {
            while (i > 0) {
                int j = EntityExperienceOrb.getOrbValue(i);

                i -= j;
                world.addEntity(new EntityExperienceOrb(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, j));
            }
        }

    }

    public float getDurability() {
        return this.durability;
    }

    @Nullable
    public static MovingObjectPosition rayTrace(IBlockData iblockdata, World world, BlockPosition blockposition, Vec3D vec3d, Vec3D vec3d1) {
        MovingObjectPosition movingobjectposition = iblockdata.getShape(world, blockposition).rayTrace(vec3d, vec3d1, blockposition);

        if (movingobjectposition != null) {
            MovingObjectPosition movingobjectposition1 = iblockdata.j(world, blockposition).rayTrace(vec3d, vec3d1, blockposition);

            if (movingobjectposition1 != null && movingobjectposition1.pos.d(vec3d).c() < movingobjectposition.pos.d(vec3d).c()) {
                movingobjectposition.direction = movingobjectposition1.direction;
            }
        }

        return movingobjectposition;
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {}

    public TextureType c() {
        return TextureType.SOLID;
    }

    @Deprecated
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        return true;
    }

    @Deprecated
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        return false;
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {}

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.getBlockData();
    }

    @Deprecated
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {}

    @Deprecated
    public int a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    @Deprecated
    public boolean isPowerSource(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {}

    @Deprecated
    public int b(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return 0;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        entityhuman.b(StatisticList.BLOCK_MINED.b(this));
        entityhuman.applyExhaustion(0.005F);
        if (this.X_() && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0) {
            ItemStack itemstack1 = this.t(iblockdata);

            a(world, blockposition, itemstack1);
        } else {
            int i = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, itemstack);

            iblockdata.a(world, blockposition, i);
        }

    }

    protected boolean X_() {
        return this.getBlockData().g() && !this.isTileEntity();
    }

    protected ItemStack t(IBlockData iblockdata) {
        return new ItemStack(this);
    }

    public int getDropCount(IBlockData iblockdata, int i, World world, BlockPosition blockposition, Random random) {
        return this.a(iblockdata, random);
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {}

    public boolean a() {
        return !this.material.isBuildable() && !this.material.isLiquid();
    }

    public String m() {
        if (this.name == null) {
            this.name = SystemUtils.a("block", IRegistry.BLOCK.getKey(this));
        }

        return this.name;
    }

    @Deprecated
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        return false;
    }

    @Deprecated
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return this.material.getPushReaction();
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        entity.c(f, 1.0F);
    }

    public void a(IBlockAccess iblockaccess, Entity entity) {
        entity.motY = 0.0D;
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        return new ItemStack(this);
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        nonnulllist.add(new ItemStack(this));
    }

    @Deprecated
    public Fluid h(IBlockData iblockdata) {
        return FluidTypes.EMPTY.i();
    }

    public float n() {
        return this.frictionFactor;
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        world.a(entityhuman, 2001, blockposition, getCombinedId(iblockdata));
    }

    public void c(World world, BlockPosition blockposition) {}

    public boolean a(Explosion explosion) {
        return true;
    }

    @Deprecated
    public boolean isComplexRedstone(IBlockData iblockdata) {
        return false;
    }

    @Deprecated
    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return 0;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {}

    public BlockStateList<Block, IBlockData> getStates() {
        return this.blockStateList;
    }

    protected final void v(IBlockData iblockdata) {
        this.blockData = iblockdata;
    }

    public final IBlockData getBlockData() {
        return this.blockData;
    }

    public Block.EnumRandomOffset q() {
        return Block.EnumRandomOffset.NONE;
    }

    @Deprecated
    public Vec3D l(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block.EnumRandomOffset block_enumrandomoffset = this.q();

        if (block_enumrandomoffset == Block.EnumRandomOffset.NONE) {
            return Vec3D.a;
        } else {
            long i = MathHelper.c(blockposition.getX(), 0, blockposition.getZ());

            return new Vec3D(((double) ((float) (i & 15L) / 15.0F) - 0.5D) * 0.5D, block_enumrandomoffset == Block.EnumRandomOffset.XYZ ? ((double) ((float) (i >> 4 & 15L) / 15.0F) - 1.0D) * 0.2D : 0.0D, ((double) ((float) (i >> 8 & 15L) / 15.0F) - 0.5D) * 0.5D);
        }
    }

    public SoundEffectType getStepSound() {
        return this.stepSound;
    }

    public Item getItem() {
        return Item.getItemOf(this);
    }

    public boolean s() {
        return this.o;
    }

    public String toString() {
        return "Block{" + IRegistry.BLOCK.getKey(this) + "}";
    }

    public static boolean c(Block block) {
        return block == Blocks.STONE || block == Blocks.GRANITE || block == Blocks.DIORITE || block == Blocks.ANDESITE;
    }

    public static boolean d(Block block) {
        return block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL;
    }

    public static void t() {
        BlockAir blockair = new BlockAir(Block.Info.a(Material.AIR).a());

        a(IRegistry.BLOCK.b(), (Block) blockair);
        BlockStone blockstone = new BlockStone(Block.Info.a(Material.STONE, MaterialMapColor.m).a(1.5F, 6.0F));

        a("stone", (Block) blockstone);
        a("granite", new Block(Block.Info.a(Material.STONE, MaterialMapColor.l).a(1.5F, 6.0F)));
        a("polished_granite", new Block(Block.Info.a(Material.STONE, MaterialMapColor.l).a(1.5F, 6.0F)));
        a("diorite", new Block(Block.Info.a(Material.STONE, MaterialMapColor.p).a(1.5F, 6.0F)));
        a("polished_diorite", new Block(Block.Info.a(Material.STONE, MaterialMapColor.p).a(1.5F, 6.0F)));
        a("andesite", new Block(Block.Info.a(Material.STONE, MaterialMapColor.m).a(1.5F, 6.0F)));
        a("polished_andesite", new Block(Block.Info.a(Material.STONE, MaterialMapColor.m).a(1.5F, 6.0F)));
        a("grass_block", (Block) (new BlockGrass(Block.Info.a(Material.GRASS).c().b(0.6F).a(SoundEffectType.c))));
        a("dirt", new Block(Block.Info.a(Material.EARTH, MaterialMapColor.l).b(0.5F).a(SoundEffectType.b)));
        a("coarse_dirt", new Block(Block.Info.a(Material.EARTH, MaterialMapColor.l).b(0.5F).a(SoundEffectType.b)));
        a("podzol", (Block) (new BlockDirtSnow(Block.Info.a(Material.EARTH, MaterialMapColor.J).b(0.5F).a(SoundEffectType.b))));
        Block block = new Block(Block.Info.a(Material.STONE).a(2.0F, 6.0F));

        a("cobblestone", block);
        Block block1 = new Block(Block.Info.a(Material.WOOD, MaterialMapColor.o).a(2.0F, 3.0F).a(SoundEffectType.a));
        Block block2 = new Block(Block.Info.a(Material.WOOD, MaterialMapColor.J).a(2.0F, 3.0F).a(SoundEffectType.a));
        Block block3 = new Block(Block.Info.a(Material.WOOD, MaterialMapColor.d).a(2.0F, 3.0F).a(SoundEffectType.a));
        Block block4 = new Block(Block.Info.a(Material.WOOD, MaterialMapColor.l).a(2.0F, 3.0F).a(SoundEffectType.a));
        Block block5 = new Block(Block.Info.a(Material.WOOD, MaterialMapColor.q).a(2.0F, 3.0F).a(SoundEffectType.a));
        Block block6 = new Block(Block.Info.a(Material.WOOD, MaterialMapColor.B).a(2.0F, 3.0F).a(SoundEffectType.a));

        a("oak_planks", block1);
        a("spruce_planks", block2);
        a("birch_planks", block3);
        a("jungle_planks", block4);
        a("acacia_planks", block5);
        a("dark_oak_planks", block6);
        BlockSapling blocksapling = new BlockSapling(new WorldGenTreeProviderOak(), Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));
        BlockSapling blocksapling1 = new BlockSapling(new WorldGenTreeProviderSpruce(), Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));
        BlockSapling blocksapling2 = new BlockSapling(new WorldGenTreeProviderBirch(), Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));
        BlockSapling blocksapling3 = new BlockSapling(new WorldGenMegaTreeProviderJungle(), Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));
        BlockSapling blocksapling4 = new BlockSapling(new WorldGenTreeProviderAcacia(), Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));
        BlockSapling blocksapling5 = new BlockSapling(new WorldGenMegaTreeProviderDarkOak(), Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));

        a("oak_sapling", (Block) blocksapling);
        a("spruce_sapling", (Block) blocksapling1);
        a("birch_sapling", (Block) blocksapling2);
        a("jungle_sapling", (Block) blocksapling3);
        a("acacia_sapling", (Block) blocksapling4);
        a("dark_oak_sapling", (Block) blocksapling5);
        a("bedrock", (Block) (new BlockNoDrop(Block.Info.a(Material.STONE).a(-1.0F, 3600000.0F))));
        a("water", (Block) (new BlockFluids(FluidTypes.WATER, Block.Info.a(Material.WATER).a().b(100.0F))));
        a("lava", (Block) (new BlockFluids(FluidTypes.LAVA, Block.Info.a(Material.LAVA).a().c().b(100.0F).a(15))));
        a("sand", (Block) (new BlockSand(14406560, Block.Info.a(Material.SAND, MaterialMapColor.d).b(0.5F).a(SoundEffectType.h))));
        a("red_sand", (Block) (new BlockSand(11098145, Block.Info.a(Material.SAND, MaterialMapColor.q).b(0.5F).a(SoundEffectType.h))));
        a("gravel", (Block) (new BlockGravel(Block.Info.a(Material.SAND, MaterialMapColor.m).b(0.6F).a(SoundEffectType.b))));
        a("gold_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE).a(3.0F, 3.0F))));
        a("iron_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE).a(3.0F, 3.0F))));
        a("coal_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE).a(3.0F, 3.0F))));
        a("oak_log", (Block) (new BlockLogAbstract(MaterialMapColor.o, Block.Info.a(Material.WOOD, MaterialMapColor.J).b(2.0F).a(SoundEffectType.a))));
        a("spruce_log", (Block) (new BlockLogAbstract(MaterialMapColor.J, Block.Info.a(Material.WOOD, MaterialMapColor.B).b(2.0F).a(SoundEffectType.a))));
        a("birch_log", (Block) (new BlockLogAbstract(MaterialMapColor.d, Block.Info.a(Material.WOOD, MaterialMapColor.p).b(2.0F).a(SoundEffectType.a))));
        a("jungle_log", (Block) (new BlockLogAbstract(MaterialMapColor.l, Block.Info.a(Material.WOOD, MaterialMapColor.J).b(2.0F).a(SoundEffectType.a))));
        a("acacia_log", (Block) (new BlockLogAbstract(MaterialMapColor.q, Block.Info.a(Material.WOOD, MaterialMapColor.m).b(2.0F).a(SoundEffectType.a))));
        a("dark_oak_log", (Block) (new BlockLogAbstract(MaterialMapColor.B, Block.Info.a(Material.WOOD, MaterialMapColor.B).b(2.0F).a(SoundEffectType.a))));
        a("stripped_spruce_log", (Block) (new BlockLogAbstract(MaterialMapColor.J, Block.Info.a(Material.WOOD, MaterialMapColor.J).b(2.0F).a(SoundEffectType.a))));
        a("stripped_birch_log", (Block) (new BlockLogAbstract(MaterialMapColor.d, Block.Info.a(Material.WOOD, MaterialMapColor.d).b(2.0F).a(SoundEffectType.a))));
        a("stripped_jungle_log", (Block) (new BlockLogAbstract(MaterialMapColor.l, Block.Info.a(Material.WOOD, MaterialMapColor.l).b(2.0F).a(SoundEffectType.a))));
        a("stripped_acacia_log", (Block) (new BlockLogAbstract(MaterialMapColor.q, Block.Info.a(Material.WOOD, MaterialMapColor.q).b(2.0F).a(SoundEffectType.a))));
        a("stripped_dark_oak_log", (Block) (new BlockLogAbstract(MaterialMapColor.B, Block.Info.a(Material.WOOD, MaterialMapColor.B).b(2.0F).a(SoundEffectType.a))));
        a("stripped_oak_log", (Block) (new BlockLogAbstract(MaterialMapColor.o, Block.Info.a(Material.WOOD, MaterialMapColor.o).b(2.0F).a(SoundEffectType.a))));
        a("oak_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.o).b(2.0F).a(SoundEffectType.a))));
        a("spruce_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.J).b(2.0F).a(SoundEffectType.a))));
        a("birch_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.d).b(2.0F).a(SoundEffectType.a))));
        a("jungle_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.l).b(2.0F).a(SoundEffectType.a))));
        a("acacia_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.q).b(2.0F).a(SoundEffectType.a))));
        a("dark_oak_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.B).b(2.0F).a(SoundEffectType.a))));
        a("stripped_oak_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.o).b(2.0F).a(SoundEffectType.a))));
        a("stripped_spruce_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.J).b(2.0F).a(SoundEffectType.a))));
        a("stripped_birch_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.d).b(2.0F).a(SoundEffectType.a))));
        a("stripped_jungle_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.l).b(2.0F).a(SoundEffectType.a))));
        a("stripped_acacia_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.q).b(2.0F).a(SoundEffectType.a))));
        a("stripped_dark_oak_wood", (Block) (new BlockRotatable(Block.Info.a(Material.WOOD, MaterialMapColor.B).b(2.0F).a(SoundEffectType.a))));
        a("oak_leaves", (Block) (new BlockLeaves(Block.Info.a(Material.LEAVES).b(0.2F).c().a(SoundEffectType.c))));
        a("spruce_leaves", (Block) (new BlockLeaves(Block.Info.a(Material.LEAVES).b(0.2F).c().a(SoundEffectType.c))));
        a("birch_leaves", (Block) (new BlockLeaves(Block.Info.a(Material.LEAVES).b(0.2F).c().a(SoundEffectType.c))));
        a("jungle_leaves", (Block) (new BlockLeaves(Block.Info.a(Material.LEAVES).b(0.2F).c().a(SoundEffectType.c))));
        a("acacia_leaves", (Block) (new BlockLeaves(Block.Info.a(Material.LEAVES).b(0.2F).c().a(SoundEffectType.c))));
        a("dark_oak_leaves", (Block) (new BlockLeaves(Block.Info.a(Material.LEAVES).b(0.2F).c().a(SoundEffectType.c))));
        a("sponge", (Block) (new BlockSponge(Block.Info.a(Material.SPONGE).b(0.6F).a(SoundEffectType.c))));
        a("wet_sponge", (Block) (new BlockWetSponge(Block.Info.a(Material.SPONGE).b(0.6F).a(SoundEffectType.c))));
        a("glass", (Block) (new BlockGlass(Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("lapis_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE).a(3.0F, 3.0F))));
        a("lapis_block", new Block(Block.Info.a(Material.ORE, MaterialMapColor.H).a(3.0F, 3.0F)));
        a("dispenser", (Block) (new BlockDispenser(Block.Info.a(Material.STONE).b(3.5F))));
        Block block7 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.d).b(0.8F));

        a("sandstone", block7);
        a("chiseled_sandstone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.d).b(0.8F)));
        a("cut_sandstone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.d).b(0.8F)));
        a("note_block", (Block) (new BlockNote(Block.Info.a(Material.WOOD).a(SoundEffectType.a).b(0.8F))));
        a("white_bed", (Block) (new BlockBed(EnumColor.WHITE, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("orange_bed", (Block) (new BlockBed(EnumColor.ORANGE, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("magenta_bed", (Block) (new BlockBed(EnumColor.MAGENTA, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("light_blue_bed", (Block) (new BlockBed(EnumColor.LIGHT_BLUE, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("yellow_bed", (Block) (new BlockBed(EnumColor.YELLOW, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("lime_bed", (Block) (new BlockBed(EnumColor.LIME, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("pink_bed", (Block) (new BlockBed(EnumColor.PINK, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("gray_bed", (Block) (new BlockBed(EnumColor.GRAY, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("light_gray_bed", (Block) (new BlockBed(EnumColor.LIGHT_GRAY, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("cyan_bed", (Block) (new BlockBed(EnumColor.CYAN, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("purple_bed", (Block) (new BlockBed(EnumColor.PURPLE, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("blue_bed", (Block) (new BlockBed(EnumColor.BLUE, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("brown_bed", (Block) (new BlockBed(EnumColor.BROWN, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("green_bed", (Block) (new BlockBed(EnumColor.GREEN, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("red_bed", (Block) (new BlockBed(EnumColor.RED, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("black_bed", (Block) (new BlockBed(EnumColor.BLACK, Block.Info.a(Material.CLOTH).a(SoundEffectType.a).b(0.2F))));
        a("powered_rail", (Block) (new BlockPoweredRail(Block.Info.a(Material.ORIENTABLE).a().b(0.7F).a(SoundEffectType.e))));
        a("detector_rail", (Block) (new BlockMinecartDetector(Block.Info.a(Material.ORIENTABLE).a().b(0.7F).a(SoundEffectType.e))));
        a("sticky_piston", (Block) (new BlockPiston(true, Block.Info.a(Material.PISTON).b(0.5F))));
        a("cobweb", (Block) (new BlockWeb(Block.Info.a(Material.WEB).a().b(4.0F))));
        BlockLongGrass blocklonggrass = new BlockLongGrass(Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c));
        BlockLongGrass blocklonggrass1 = new BlockLongGrass(Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c));
        BlockDeadBush blockdeadbush = new BlockDeadBush(Block.Info.a(Material.REPLACEABLE_PLANT, MaterialMapColor.o).a().b().a(SoundEffectType.c));

        a("grass", (Block) blocklonggrass);
        a("fern", (Block) blocklonggrass1);
        a("dead_bush", (Block) blockdeadbush);
        BlockSeaGrass blockseagrass = new BlockSeaGrass(Block.Info.a(Material.REPLACEABLE_WATER_PLANT).a().b().a(SoundEffectType.m));

        a("seagrass", (Block) blockseagrass);
        a("tall_seagrass", (Block) (new BlockTallSeaGrass(blockseagrass, Block.Info.a(Material.REPLACEABLE_WATER_PLANT).a().b().a(SoundEffectType.m))));
        a("piston", (Block) (new BlockPiston(false, Block.Info.a(Material.PISTON).b(0.5F))));
        a("piston_head", (Block) (new BlockPistonExtension(Block.Info.a(Material.PISTON).b(0.5F))));
        a("white_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.j).b(0.8F).a(SoundEffectType.g)));
        a("orange_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.q).b(0.8F).a(SoundEffectType.g)));
        a("magenta_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.r).b(0.8F).a(SoundEffectType.g)));
        a("light_blue_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.s).b(0.8F).a(SoundEffectType.g)));
        a("yellow_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.t).b(0.8F).a(SoundEffectType.g)));
        a("lime_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.u).b(0.8F).a(SoundEffectType.g)));
        a("pink_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.v).b(0.8F).a(SoundEffectType.g)));
        a("gray_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.w).b(0.8F).a(SoundEffectType.g)));
        a("light_gray_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.x).b(0.8F).a(SoundEffectType.g)));
        a("cyan_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.y).b(0.8F).a(SoundEffectType.g)));
        a("purple_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.z).b(0.8F).a(SoundEffectType.g)));
        a("blue_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.A).b(0.8F).a(SoundEffectType.g)));
        a("brown_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.B).b(0.8F).a(SoundEffectType.g)));
        a("green_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.C).b(0.8F).a(SoundEffectType.g)));
        a("red_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.D).b(0.8F).a(SoundEffectType.g)));
        a("black_wool", new Block(Block.Info.a(Material.CLOTH, MaterialMapColor.E).b(0.8F).a(SoundEffectType.g)));
        a("moving_piston", (Block) (new BlockPistonMoving(Block.Info.a(Material.PISTON).b(-1.0F).d())));
        BlockFlowers blockflowers = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers1 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers2 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers3 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers4 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers5 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers6 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers7 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers8 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));
        BlockFlowers blockflowers9 = new BlockFlowers(Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.c));

        a("dandelion", (Block) blockflowers);
        a("poppy", (Block) blockflowers1);
        a("blue_orchid", (Block) blockflowers2);
        a("allium", (Block) blockflowers3);
        a("azure_bluet", (Block) blockflowers4);
        a("red_tulip", (Block) blockflowers5);
        a("orange_tulip", (Block) blockflowers6);
        a("white_tulip", (Block) blockflowers7);
        a("pink_tulip", (Block) blockflowers8);
        a("oxeye_daisy", (Block) blockflowers9);
        BlockMushroom blockmushroom = new BlockMushroom(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c).a(1));
        BlockMushroom blockmushroom1 = new BlockMushroom(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c));

        a("brown_mushroom", (Block) blockmushroom);
        a("red_mushroom", (Block) blockmushroom1);
        a("gold_block", new Block(Block.Info.a(Material.ORE, MaterialMapColor.F).a(3.0F, 6.0F).a(SoundEffectType.e)));
        a("iron_block", new Block(Block.Info.a(Material.ORE, MaterialMapColor.h).a(5.0F, 6.0F).a(SoundEffectType.e)));
        Block block8 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.D).a(2.0F, 6.0F));

        a("bricks", block8);
        a("tnt", (Block) (new BlockTNT(Block.Info.a(Material.TNT).b().a(SoundEffectType.c))));
        a("bookshelf", (Block) (new BlockBookshelf(Block.Info.a(Material.WOOD).b(1.5F).a(SoundEffectType.a))));
        a("mossy_cobblestone", new Block(Block.Info.a(Material.STONE).a(2.0F, 6.0F)));
        a("obsidian", new Block(Block.Info.a(Material.STONE, MaterialMapColor.E).a(50.0F, 1200.0F)));
        a("torch", (Block) (new BlockTorch(Block.Info.a(Material.ORIENTABLE).a().b().a(14).a(SoundEffectType.a))));
        a("wall_torch", (Block) (new BlockTorchWall(Block.Info.a(Material.ORIENTABLE).a().b().a(14).a(SoundEffectType.a))));
        a("fire", (Block) (new BlockFire(Block.Info.a(Material.FIRE, MaterialMapColor.f).a().c().b().a(15).a(SoundEffectType.g))));
        a("spawner", (Block) (new BlockMobSpawner(Block.Info.a(Material.STONE).b(5.0F).a(SoundEffectType.e))));
        a("oak_stairs", (Block) (new BlockStairs(block1.getBlockData(), Block.Info.a(block1))));
        a("chest", (Block) (new BlockChest(Block.Info.a(Material.WOOD).b(2.5F).a(SoundEffectType.a))));
        a("redstone_wire", (Block) (new BlockRedstoneWire(Block.Info.a(Material.ORIENTABLE).a().b())));
        a("diamond_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE).a(3.0F, 3.0F))));
        a("diamond_block", new Block(Block.Info.a(Material.ORE, MaterialMapColor.G).a(5.0F, 6.0F).a(SoundEffectType.e)));
        a("crafting_table", (Block) (new BlockWorkbench(Block.Info.a(Material.WOOD).b(2.5F).a(SoundEffectType.a))));
        a("wheat", (Block) (new BlockCrops(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c))));
        BlockSoil blocksoil = new BlockSoil(Block.Info.a(Material.EARTH).c().b(0.6F).a(SoundEffectType.b));

        a("farmland", (Block) blocksoil);
        a("furnace", (Block) (new BlockFurnace(Block.Info.a(Material.STONE).b(3.5F).a(13))));
        a("sign", (Block) (new BlockFloorSign(Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("oak_door", (Block) (new BlockDoor(Block.Info.a(Material.WOOD, block1.l).b(3.0F).a(SoundEffectType.a))));
        a("ladder", (Block) (new BlockLadder(Block.Info.a(Material.ORIENTABLE).b(0.4F).a(SoundEffectType.j))));
        a("rail", (Block) (new BlockMinecartTrack(Block.Info.a(Material.ORIENTABLE).a().b(0.7F).a(SoundEffectType.e))));
        a("cobblestone_stairs", (Block) (new BlockStairs(block.getBlockData(), Block.Info.a(block))));
        a("wall_sign", (Block) (new BlockWallSign(Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("lever", (Block) (new BlockLever(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("stone_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.MOBS, Block.Info.a(Material.STONE).a().b(0.5F))));
        a("iron_door", (Block) (new BlockDoor(Block.Info.a(Material.ORE, MaterialMapColor.h).b(5.0F).a(SoundEffectType.e))));
        a("oak_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, Block.Info.a(Material.WOOD, block1.l).a().b(0.5F).a(SoundEffectType.a))));
        a("spruce_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, Block.Info.a(Material.WOOD, block2.l).a().b(0.5F).a(SoundEffectType.a))));
        a("birch_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, Block.Info.a(Material.WOOD, block3.l).a().b(0.5F).a(SoundEffectType.a))));
        a("jungle_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, Block.Info.a(Material.WOOD, block4.l).a().b(0.5F).a(SoundEffectType.a))));
        a("acacia_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, Block.Info.a(Material.WOOD, block5.l).a().b(0.5F).a(SoundEffectType.a))));
        a("dark_oak_pressure_plate", (Block) (new BlockPressurePlateBinary(BlockPressurePlateBinary.EnumMobType.EVERYTHING, Block.Info.a(Material.WOOD, block6.l).a().b(0.5F).a(SoundEffectType.a))));
        a("redstone_ore", (Block) (new BlockRedstoneOre(Block.Info.a(Material.STONE).c().a(9).a(3.0F, 3.0F))));
        a("redstone_torch", (Block) (new BlockRedstoneTorch(Block.Info.a(Material.ORIENTABLE).a().b().a(7).a(SoundEffectType.a))));
        a("redstone_wall_torch", (Block) (new BlockRedstoneTorchWall(Block.Info.a(Material.ORIENTABLE).a().b().a(7).a(SoundEffectType.a))));
        a("stone_button", (Block) (new BlockStoneButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F))));
        a("snow", (Block) (new BlockSnow(Block.Info.a(Material.PACKED_ICE).c().b(0.1F).a(SoundEffectType.i))));
        a("ice", (Block) (new BlockIce(Block.Info.a(Material.ICE).a(0.98F).c().b(0.5F).a(SoundEffectType.f))));
        a("snow_block", (Block) (new BlockSnowBlock(Block.Info.a(Material.SNOW_BLOCK).c().b(0.2F).a(SoundEffectType.i))));
        BlockCactus blockcactus = new BlockCactus(Block.Info.a(Material.CACTUS).c().b(0.4F).a(SoundEffectType.g));

        a("cactus", (Block) blockcactus);
        a("clay", (Block) (new BlockClay(Block.Info.a(Material.CLAY).b(0.6F).a(SoundEffectType.b))));
        a("sugar_cane", (Block) (new BlockReed(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c))));
        a("jukebox", (Block) (new BlockJukeBox(Block.Info.a(Material.WOOD, MaterialMapColor.l).a(2.0F, 6.0F))));
        a("oak_fence", (Block) (new BlockFence(Block.Info.a(Material.WOOD, block1.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        BlockPumpkin blockpumpkin = new BlockPumpkin(Block.Info.a(Material.PUMPKIN, MaterialMapColor.q).b(1.0F).a(SoundEffectType.a));

        a("pumpkin", (Block) blockpumpkin);
        a("netherrack", new Block(Block.Info.a(Material.STONE, MaterialMapColor.K).b(0.4F)));
        a("soul_sand", (Block) (new BlockSlowSand(Block.Info.a(Material.SAND, MaterialMapColor.B).c().b(0.5F).a(SoundEffectType.h))));
        a("glowstone", (Block) (new BlockLightStone(Block.Info.a(Material.SHATTERABLE, MaterialMapColor.d).b(0.3F).a(SoundEffectType.f).a(15))));
        a("nether_portal", (Block) (new BlockPortal(Block.Info.a(Material.PORTAL).a().c().b(-1.0F).a(SoundEffectType.f).a(11))));
        a("carved_pumpkin", (Block) (new BlockPumpkinCarved(Block.Info.a(Material.PUMPKIN, MaterialMapColor.q).b(1.0F).a(SoundEffectType.a))));
        a("jack_o_lantern", (Block) (new BlockPumpkinCarved(Block.Info.a(Material.PUMPKIN, MaterialMapColor.q).b(1.0F).a(SoundEffectType.a).a(15))));
        a("cake", (Block) (new BlockCake(Block.Info.a(Material.CAKE).b(0.5F).a(SoundEffectType.g))));
        a("repeater", (Block) (new BlockRepeater(Block.Info.a(Material.ORIENTABLE).b().a(SoundEffectType.a))));
        a("white_stained_glass", (Block) (new BlockStainedGlass(EnumColor.WHITE, Block.Info.a(Material.SHATTERABLE, EnumColor.WHITE).b(0.3F).a(SoundEffectType.f))));
        a("orange_stained_glass", (Block) (new BlockStainedGlass(EnumColor.ORANGE, Block.Info.a(Material.SHATTERABLE, EnumColor.ORANGE).b(0.3F).a(SoundEffectType.f))));
        a("magenta_stained_glass", (Block) (new BlockStainedGlass(EnumColor.MAGENTA, Block.Info.a(Material.SHATTERABLE, EnumColor.MAGENTA).b(0.3F).a(SoundEffectType.f))));
        a("light_blue_stained_glass", (Block) (new BlockStainedGlass(EnumColor.LIGHT_BLUE, Block.Info.a(Material.SHATTERABLE, EnumColor.LIGHT_BLUE).b(0.3F).a(SoundEffectType.f))));
        a("yellow_stained_glass", (Block) (new BlockStainedGlass(EnumColor.YELLOW, Block.Info.a(Material.SHATTERABLE, EnumColor.YELLOW).b(0.3F).a(SoundEffectType.f))));
        a("lime_stained_glass", (Block) (new BlockStainedGlass(EnumColor.LIME, Block.Info.a(Material.SHATTERABLE, EnumColor.LIME).b(0.3F).a(SoundEffectType.f))));
        a("pink_stained_glass", (Block) (new BlockStainedGlass(EnumColor.PINK, Block.Info.a(Material.SHATTERABLE, EnumColor.PINK).b(0.3F).a(SoundEffectType.f))));
        a("gray_stained_glass", (Block) (new BlockStainedGlass(EnumColor.GRAY, Block.Info.a(Material.SHATTERABLE, EnumColor.GRAY).b(0.3F).a(SoundEffectType.f))));
        a("light_gray_stained_glass", (Block) (new BlockStainedGlass(EnumColor.LIGHT_GRAY, Block.Info.a(Material.SHATTERABLE, EnumColor.LIGHT_GRAY).b(0.3F).a(SoundEffectType.f))));
        a("cyan_stained_glass", (Block) (new BlockStainedGlass(EnumColor.CYAN, Block.Info.a(Material.SHATTERABLE, EnumColor.CYAN).b(0.3F).a(SoundEffectType.f))));
        a("purple_stained_glass", (Block) (new BlockStainedGlass(EnumColor.PURPLE, Block.Info.a(Material.SHATTERABLE, EnumColor.PURPLE).b(0.3F).a(SoundEffectType.f))));
        a("blue_stained_glass", (Block) (new BlockStainedGlass(EnumColor.BLUE, Block.Info.a(Material.SHATTERABLE, EnumColor.BLUE).b(0.3F).a(SoundEffectType.f))));
        a("brown_stained_glass", (Block) (new BlockStainedGlass(EnumColor.BROWN, Block.Info.a(Material.SHATTERABLE, EnumColor.BROWN).b(0.3F).a(SoundEffectType.f))));
        a("green_stained_glass", (Block) (new BlockStainedGlass(EnumColor.GREEN, Block.Info.a(Material.SHATTERABLE, EnumColor.GREEN).b(0.3F).a(SoundEffectType.f))));
        a("red_stained_glass", (Block) (new BlockStainedGlass(EnumColor.RED, Block.Info.a(Material.SHATTERABLE, EnumColor.RED).b(0.3F).a(SoundEffectType.f))));
        a("black_stained_glass", (Block) (new BlockStainedGlass(EnumColor.BLACK, Block.Info.a(Material.SHATTERABLE, EnumColor.BLACK).b(0.3F).a(SoundEffectType.f))));
        a("oak_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.WOOD, MaterialMapColor.o).b(3.0F).a(SoundEffectType.a))));
        a("spruce_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.WOOD, MaterialMapColor.J).b(3.0F).a(SoundEffectType.a))));
        a("birch_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.WOOD, MaterialMapColor.d).b(3.0F).a(SoundEffectType.a))));
        a("jungle_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.WOOD, MaterialMapColor.l).b(3.0F).a(SoundEffectType.a))));
        a("acacia_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.WOOD, MaterialMapColor.q).b(3.0F).a(SoundEffectType.a))));
        a("dark_oak_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.WOOD, MaterialMapColor.B).b(3.0F).a(SoundEffectType.a))));
        Block block9 = new Block(Block.Info.a(Material.STONE).a(1.5F, 6.0F));
        Block block10 = new Block(Block.Info.a(Material.STONE).a(1.5F, 6.0F));
        Block block11 = new Block(Block.Info.a(Material.STONE).a(1.5F, 6.0F));
        Block block12 = new Block(Block.Info.a(Material.STONE).a(1.5F, 6.0F));

        a("infested_stone", (Block) (new BlockMonsterEggs(blockstone, Block.Info.a(Material.CLAY).a(0.0F, 0.75F))));
        a("infested_cobblestone", (Block) (new BlockMonsterEggs(block, Block.Info.a(Material.CLAY).a(0.0F, 0.75F))));
        a("infested_stone_bricks", (Block) (new BlockMonsterEggs(block9, Block.Info.a(Material.CLAY).a(0.0F, 0.75F))));
        a("infested_mossy_stone_bricks", (Block) (new BlockMonsterEggs(block10, Block.Info.a(Material.CLAY).a(0.0F, 0.75F))));
        a("infested_cracked_stone_bricks", (Block) (new BlockMonsterEggs(block11, Block.Info.a(Material.CLAY).a(0.0F, 0.75F))));
        a("infested_chiseled_stone_bricks", (Block) (new BlockMonsterEggs(block12, Block.Info.a(Material.CLAY).a(0.0F, 0.75F))));
        a("stone_bricks", block9);
        a("mossy_stone_bricks", block10);
        a("cracked_stone_bricks", block11);
        a("chiseled_stone_bricks", block12);
        BlockHugeMushroom blockhugemushroom = new BlockHugeMushroom(blockmushroom, Block.Info.a(Material.WOOD, MaterialMapColor.l).b(0.2F).a(SoundEffectType.a));

        a("brown_mushroom_block", (Block) blockhugemushroom);
        BlockHugeMushroom blockhugemushroom1 = new BlockHugeMushroom(blockmushroom1, Block.Info.a(Material.WOOD, MaterialMapColor.D).b(0.2F).a(SoundEffectType.a));

        a("red_mushroom_block", (Block) blockhugemushroom1);
        a("mushroom_stem", (Block) (new BlockHugeMushroom((Block) null, Block.Info.a(Material.WOOD, MaterialMapColor.L).b(0.2F).a(SoundEffectType.a))));
        a("iron_bars", (Block) (new BlockIronBars(Block.Info.a(Material.ORE, MaterialMapColor.b).a(5.0F, 6.0F).a(SoundEffectType.e))));
        a("glass_pane", (Block) (new BlockGlassPane(Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        BlockMelon blockmelon = new BlockMelon(Block.Info.a(Material.PUMPKIN, MaterialMapColor.u).b(1.0F).a(SoundEffectType.a));

        a("melon", (Block) blockmelon);
        a("attached_pumpkin_stem", (Block) (new BlockStemAttached(blockpumpkin, Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.a))));
        a("attached_melon_stem", (Block) (new BlockStemAttached(blockmelon, Block.Info.a(Material.PLANT).a().b().a(SoundEffectType.a))));
        a("pumpkin_stem", (Block) (new BlockStem(blockpumpkin, Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.a))));
        a("melon_stem", (Block) (new BlockStem(blockmelon, Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.a))));
        a("vine", (Block) (new BlockVine(Block.Info.a(Material.REPLACEABLE_PLANT).a().c().b(0.2F).a(SoundEffectType.c))));
        a("oak_fence_gate", (Block) (new BlockFenceGate(Block.Info.a(Material.WOOD, block1.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("brick_stairs", (Block) (new BlockStairs(block8.getBlockData(), Block.Info.a(block8))));
        a("stone_brick_stairs", (Block) (new BlockStairs(block9.getBlockData(), Block.Info.a(block9))));
        a("mycelium", (Block) (new BlockMycel(Block.Info.a(Material.GRASS, MaterialMapColor.z).c().b(0.6F).a(SoundEffectType.c))));
        a("lily_pad", (Block) (new BlockWaterLily(Block.Info.a(Material.PLANT).b().a(SoundEffectType.c))));
        Block block13 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.K).a(2.0F, 6.0F));

        a("nether_bricks", block13);
        a("nether_brick_fence", (Block) (new BlockFence(Block.Info.a(Material.STONE, MaterialMapColor.K).a(2.0F, 6.0F))));
        a("nether_brick_stairs", (Block) (new BlockStairs(block13.getBlockData(), Block.Info.a(block13))));
        a("nether_wart", (Block) (new BlockNetherWart(Block.Info.a(Material.PLANT, MaterialMapColor.D).a().c())));
        a("enchanting_table", (Block) (new BlockEnchantmentTable(Block.Info.a(Material.STONE, MaterialMapColor.D).a(5.0F, 1200.0F))));
        a("brewing_stand", (Block) (new BlockBrewingStand(Block.Info.a(Material.ORE).b(0.5F).a(1))));
        a("cauldron", (Block) (new BlockCauldron(Block.Info.a(Material.ORE, MaterialMapColor.m).b(2.0F))));
        a("end_portal", (Block) (new BlockEnderPortal(Block.Info.a(Material.PORTAL, MaterialMapColor.E).a().a(15).a(-1.0F, 3600000.0F))));
        a("end_portal_frame", (Block) (new BlockEnderPortalFrame(Block.Info.a(Material.STONE, MaterialMapColor.C).a(SoundEffectType.f).a(1).a(-1.0F, 3600000.0F))));
        a("end_stone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.d).a(3.0F, 9.0F)));
        a("dragon_egg", (Block) (new BlockDragonEgg(Block.Info.a(Material.DRAGON_EGG, MaterialMapColor.E).a(3.0F, 9.0F).a(1))));
        a("redstone_lamp", (Block) (new BlockRedstoneLamp(Block.Info.a(Material.BUILDABLE_GLASS).a(15).b(0.3F).a(SoundEffectType.f))));
        a("cocoa", (Block) (new BlockCocoa(Block.Info.a(Material.PLANT).c().a(0.2F, 3.0F).a(SoundEffectType.a))));
        a("sandstone_stairs", (Block) (new BlockStairs(block7.getBlockData(), Block.Info.a(block7))));
        a("emerald_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE).a(3.0F, 3.0F))));
        a("ender_chest", (Block) (new BlockEnderChest(Block.Info.a(Material.STONE).a(22.5F, 600.0F).a(7))));
        BlockTripwireHook blocktripwirehook = new BlockTripwireHook(Block.Info.a(Material.ORIENTABLE).a());

        a("tripwire_hook", (Block) blocktripwirehook);
        a("tripwire", (Block) (new BlockTripwire(blocktripwirehook, Block.Info.a(Material.ORIENTABLE).a())));
        a("emerald_block", new Block(Block.Info.a(Material.ORE, MaterialMapColor.I).a(5.0F, 6.0F).a(SoundEffectType.e)));
        a("spruce_stairs", (Block) (new BlockStairs(block2.getBlockData(), Block.Info.a(block2))));
        a("birch_stairs", (Block) (new BlockStairs(block3.getBlockData(), Block.Info.a(block3))));
        a("jungle_stairs", (Block) (new BlockStairs(block4.getBlockData(), Block.Info.a(block4))));
        a("command_block", (Block) (new BlockCommand(Block.Info.a(Material.ORE, MaterialMapColor.B).a(-1.0F, 3600000.0F))));
        a("beacon", (Block) (new BlockBeacon(Block.Info.a(Material.SHATTERABLE, MaterialMapColor.G).b(3.0F).a(15))));
        a("cobblestone_wall", (Block) (new BlockCobbleWall(Block.Info.a(block))));
        a("mossy_cobblestone_wall", (Block) (new BlockCobbleWall(Block.Info.a(block))));
        a("flower_pot", (Block) (new BlockFlowerPot(blockair, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_oak_sapling", (Block) (new BlockFlowerPot(blocksapling, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_spruce_sapling", (Block) (new BlockFlowerPot(blocksapling1, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_birch_sapling", (Block) (new BlockFlowerPot(blocksapling2, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_jungle_sapling", (Block) (new BlockFlowerPot(blocksapling3, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_acacia_sapling", (Block) (new BlockFlowerPot(blocksapling4, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_dark_oak_sapling", (Block) (new BlockFlowerPot(blocksapling5, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_fern", (Block) (new BlockFlowerPot(blocklonggrass1, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_dandelion", (Block) (new BlockFlowerPot(blockflowers, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_poppy", (Block) (new BlockFlowerPot(blockflowers1, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_blue_orchid", (Block) (new BlockFlowerPot(blockflowers2, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_allium", (Block) (new BlockFlowerPot(blockflowers3, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_azure_bluet", (Block) (new BlockFlowerPot(blockflowers4, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_red_tulip", (Block) (new BlockFlowerPot(blockflowers5, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_orange_tulip", (Block) (new BlockFlowerPot(blockflowers6, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_white_tulip", (Block) (new BlockFlowerPot(blockflowers7, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_pink_tulip", (Block) (new BlockFlowerPot(blockflowers8, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_oxeye_daisy", (Block) (new BlockFlowerPot(blockflowers9, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_red_mushroom", (Block) (new BlockFlowerPot(blockmushroom1, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_brown_mushroom", (Block) (new BlockFlowerPot(blockmushroom, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_dead_bush", (Block) (new BlockFlowerPot(blockdeadbush, Block.Info.a(Material.ORIENTABLE).b())));
        a("potted_cactus", (Block) (new BlockFlowerPot(blockcactus, Block.Info.a(Material.ORIENTABLE).b())));
        a("carrots", (Block) (new BlockCarrots(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c))));
        a("potatoes", (Block) (new BlockPotatoes(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c))));
        a("oak_button", (Block) (new BlockWoodButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("spruce_button", (Block) (new BlockWoodButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("birch_button", (Block) (new BlockWoodButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("jungle_button", (Block) (new BlockWoodButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("acacia_button", (Block) (new BlockWoodButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("dark_oak_button", (Block) (new BlockWoodButton(Block.Info.a(Material.ORIENTABLE).a().b(0.5F).a(SoundEffectType.a))));
        a("skeleton_wall_skull", (Block) (new BlockSkullWall(BlockSkull.Type.SKELETON, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("skeleton_skull", (Block) (new BlockSkull(BlockSkull.Type.SKELETON, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("wither_skeleton_wall_skull", (Block) (new BlockWitherSkullWall(Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("wither_skeleton_skull", (Block) (new BlockWitherSkull(Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("zombie_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.ZOMBIE, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("zombie_head", (Block) (new BlockSkull(BlockSkull.Type.ZOMBIE, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("player_wall_head", (Block) (new BlockSkullPlayerWall(Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("player_head", (Block) (new BlockSkullPlayer(Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("creeper_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.CREEPER, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("creeper_head", (Block) (new BlockSkull(BlockSkull.Type.CREEPER, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("dragon_wall_head", (Block) (new BlockSkullWall(BlockSkull.Type.DRAGON, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("dragon_head", (Block) (new BlockSkull(BlockSkull.Type.DRAGON, Block.Info.a(Material.ORIENTABLE).b(1.0F))));
        a("anvil", (Block) (new BlockAnvil(Block.Info.a(Material.HEAVY, MaterialMapColor.h).a(5.0F, 1200.0F).a(SoundEffectType.k))));
        a("chipped_anvil", (Block) (new BlockAnvil(Block.Info.a(Material.HEAVY, MaterialMapColor.h).a(5.0F, 1200.0F).a(SoundEffectType.k))));
        a("damaged_anvil", (Block) (new BlockAnvil(Block.Info.a(Material.HEAVY, MaterialMapColor.h).a(5.0F, 1200.0F).a(SoundEffectType.k))));
        a("trapped_chest", (Block) (new BlockChestTrapped(Block.Info.a(Material.WOOD).b(2.5F).a(SoundEffectType.a))));
        a("light_weighted_pressure_plate", (Block) (new BlockPressurePlateWeighted(15, Block.Info.a(Material.ORE, MaterialMapColor.F).a().b(0.5F).a(SoundEffectType.a))));
        a("heavy_weighted_pressure_plate", (Block) (new BlockPressurePlateWeighted(150, Block.Info.a(Material.ORE).a().b(0.5F).a(SoundEffectType.a))));
        a("comparator", (Block) (new BlockRedstoneComparator(Block.Info.a(Material.ORIENTABLE).b().a(SoundEffectType.a))));
        a("daylight_detector", (Block) (new BlockDaylightDetector(Block.Info.a(Material.WOOD).b(0.2F).a(SoundEffectType.a))));
        a("redstone_block", (Block) (new BlockPowered(Block.Info.a(Material.ORE, MaterialMapColor.f).a(5.0F, 6.0F).a(SoundEffectType.e))));
        a("nether_quartz_ore", (Block) (new BlockOre(Block.Info.a(Material.STONE, MaterialMapColor.K).a(3.0F, 3.0F))));
        a("hopper", (Block) (new BlockHopper(Block.Info.a(Material.ORE, MaterialMapColor.m).a(3.0F, 4.8F).a(SoundEffectType.e))));
        Block block14 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.p).b(0.8F));

        a("quartz_block", block14);
        a("chiseled_quartz_block", new Block(Block.Info.a(Material.STONE, MaterialMapColor.p).b(0.8F)));
        a("quartz_pillar", (Block) (new BlockRotatable(Block.Info.a(Material.STONE, MaterialMapColor.p).b(0.8F))));
        a("quartz_stairs", (Block) (new BlockStairs(block14.getBlockData(), Block.Info.a(block14))));
        a("activator_rail", (Block) (new BlockPoweredRail(Block.Info.a(Material.ORIENTABLE).a().b(0.7F).a(SoundEffectType.e))));
        a("dropper", (Block) (new BlockDropper(Block.Info.a(Material.STONE).b(3.5F))));
        a("white_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.L).a(1.25F, 4.2F)));
        a("orange_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.M).a(1.25F, 4.2F)));
        a("magenta_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.N).a(1.25F, 4.2F)));
        a("light_blue_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.O).a(1.25F, 4.2F)));
        a("yellow_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.P).a(1.25F, 4.2F)));
        a("lime_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.Q).a(1.25F, 4.2F)));
        a("pink_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.R).a(1.25F, 4.2F)));
        a("gray_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.S).a(1.25F, 4.2F)));
        a("light_gray_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.T).a(1.25F, 4.2F)));
        a("cyan_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.U).a(1.25F, 4.2F)));
        a("purple_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.V).a(1.25F, 4.2F)));
        a("blue_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.W).a(1.25F, 4.2F)));
        a("brown_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.X).a(1.25F, 4.2F)));
        a("green_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.Y).a(1.25F, 4.2F)));
        a("red_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.Z).a(1.25F, 4.2F)));
        a("black_terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.aa).a(1.25F, 4.2F)));
        a("white_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.WHITE, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("orange_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.ORANGE, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("magenta_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.MAGENTA, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("light_blue_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIGHT_BLUE, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("yellow_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.YELLOW, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("lime_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIME, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("pink_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.PINK, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("gray_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.GRAY, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("light_gray_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.LIGHT_GRAY, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("cyan_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.CYAN, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("purple_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.PURPLE, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("blue_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BLUE, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("brown_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BROWN, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("green_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.GREEN, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("red_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.RED, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("black_stained_glass_pane", (Block) (new BlockStainedGlassPane(EnumColor.BLACK, Block.Info.a(Material.SHATTERABLE).b(0.3F).a(SoundEffectType.f))));
        a("acacia_stairs", (Block) (new BlockStairs(block5.getBlockData(), Block.Info.a(block5))));
        a("dark_oak_stairs", (Block) (new BlockStairs(block6.getBlockData(), Block.Info.a(block6))));
        a("slime_block", (Block) (new BlockSlime(Block.Info.a(Material.CLAY, MaterialMapColor.c).a(0.8F).a(SoundEffectType.l))));
        a("barrier", (Block) (new BlockBarrier(Block.Info.a(Material.BANNER).a(-1.0F, 3600000.8F))));
        a("iron_trapdoor", (Block) (new BlockTrapdoor(Block.Info.a(Material.ORE).b(5.0F).a(SoundEffectType.e))));
        Block block15 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.y).a(1.5F, 6.0F));

        a("prismarine", block15);
        Block block16 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.G).a(1.5F, 6.0F));

        a("prismarine_bricks", block16);
        Block block17 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.G).a(1.5F, 6.0F));

        a("dark_prismarine", block17);
        a("prismarine_stairs", (Block) (new BlockStairs(block15.getBlockData(), Block.Info.a(block15))));
        a("prismarine_brick_stairs", (Block) (new BlockStairs(block16.getBlockData(), Block.Info.a(block16))));
        a("dark_prismarine_stairs", (Block) (new BlockStairs(block17.getBlockData(), Block.Info.a(block17))));
        a("prismarine_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.y).a(1.5F, 6.0F))));
        a("prismarine_brick_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.G).a(1.5F, 6.0F))));
        a("dark_prismarine_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.G).a(1.5F, 6.0F))));
        a("sea_lantern", (Block) (new BlockSeaLantern(Block.Info.a(Material.SHATTERABLE, MaterialMapColor.p).b(0.3F).a(SoundEffectType.f).a(15))));
        a("hay_block", (Block) (new BlockHay(Block.Info.a(Material.GRASS, MaterialMapColor.t).b(0.5F).a(SoundEffectType.c))));
        a("white_carpet", (Block) (new BlockCarpet(EnumColor.WHITE, Block.Info.a(Material.WOOL, MaterialMapColor.j).b(0.1F).a(SoundEffectType.g))));
        a("orange_carpet", (Block) (new BlockCarpet(EnumColor.ORANGE, Block.Info.a(Material.WOOL, MaterialMapColor.q).b(0.1F).a(SoundEffectType.g))));
        a("magenta_carpet", (Block) (new BlockCarpet(EnumColor.MAGENTA, Block.Info.a(Material.WOOL, MaterialMapColor.r).b(0.1F).a(SoundEffectType.g))));
        a("light_blue_carpet", (Block) (new BlockCarpet(EnumColor.LIGHT_BLUE, Block.Info.a(Material.WOOL, MaterialMapColor.s).b(0.1F).a(SoundEffectType.g))));
        a("yellow_carpet", (Block) (new BlockCarpet(EnumColor.YELLOW, Block.Info.a(Material.WOOL, MaterialMapColor.t).b(0.1F).a(SoundEffectType.g))));
        a("lime_carpet", (Block) (new BlockCarpet(EnumColor.LIME, Block.Info.a(Material.WOOL, MaterialMapColor.u).b(0.1F).a(SoundEffectType.g))));
        a("pink_carpet", (Block) (new BlockCarpet(EnumColor.PINK, Block.Info.a(Material.WOOL, MaterialMapColor.v).b(0.1F).a(SoundEffectType.g))));
        a("gray_carpet", (Block) (new BlockCarpet(EnumColor.GRAY, Block.Info.a(Material.WOOL, MaterialMapColor.w).b(0.1F).a(SoundEffectType.g))));
        a("light_gray_carpet", (Block) (new BlockCarpet(EnumColor.LIGHT_GRAY, Block.Info.a(Material.WOOL, MaterialMapColor.x).b(0.1F).a(SoundEffectType.g))));
        a("cyan_carpet", (Block) (new BlockCarpet(EnumColor.CYAN, Block.Info.a(Material.WOOL, MaterialMapColor.y).b(0.1F).a(SoundEffectType.g))));
        a("purple_carpet", (Block) (new BlockCarpet(EnumColor.PURPLE, Block.Info.a(Material.WOOL, MaterialMapColor.z).b(0.1F).a(SoundEffectType.g))));
        a("blue_carpet", (Block) (new BlockCarpet(EnumColor.BLUE, Block.Info.a(Material.WOOL, MaterialMapColor.A).b(0.1F).a(SoundEffectType.g))));
        a("brown_carpet", (Block) (new BlockCarpet(EnumColor.BROWN, Block.Info.a(Material.WOOL, MaterialMapColor.B).b(0.1F).a(SoundEffectType.g))));
        a("green_carpet", (Block) (new BlockCarpet(EnumColor.GREEN, Block.Info.a(Material.WOOL, MaterialMapColor.C).b(0.1F).a(SoundEffectType.g))));
        a("red_carpet", (Block) (new BlockCarpet(EnumColor.RED, Block.Info.a(Material.WOOL, MaterialMapColor.D).b(0.1F).a(SoundEffectType.g))));
        a("black_carpet", (Block) (new BlockCarpet(EnumColor.BLACK, Block.Info.a(Material.WOOL, MaterialMapColor.E).b(0.1F).a(SoundEffectType.g))));
        a("terracotta", new Block(Block.Info.a(Material.STONE, MaterialMapColor.q).a(1.25F, 4.2F)));
        a("coal_block", new Block(Block.Info.a(Material.STONE, MaterialMapColor.E).a(5.0F, 6.0F)));
        a("packed_ice", (Block) (new BlockPackedIce(Block.Info.a(Material.SNOW_LAYER).a(0.98F).b(0.5F).a(SoundEffectType.f))));
        a("sunflower", (Block) (new BlockTallPlantFlower(Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c))));
        a("lilac", (Block) (new BlockTallPlantFlower(Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c))));
        a("rose_bush", (Block) (new BlockTallPlantFlower(Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c))));
        a("peony", (Block) (new BlockTallPlantFlower(Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c))));
        a("tall_grass", (Block) (new BlockTallPlantShearable(blocklonggrass, Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c))));
        a("large_fern", (Block) (new BlockTallPlantShearable(blocklonggrass1, Block.Info.a(Material.REPLACEABLE_PLANT).a().b().a(SoundEffectType.c))));
        a("white_banner", (Block) (new BlockBanner(EnumColor.WHITE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("orange_banner", (Block) (new BlockBanner(EnumColor.ORANGE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("magenta_banner", (Block) (new BlockBanner(EnumColor.MAGENTA, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("light_blue_banner", (Block) (new BlockBanner(EnumColor.LIGHT_BLUE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("yellow_banner", (Block) (new BlockBanner(EnumColor.YELLOW, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("lime_banner", (Block) (new BlockBanner(EnumColor.LIME, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("pink_banner", (Block) (new BlockBanner(EnumColor.PINK, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("gray_banner", (Block) (new BlockBanner(EnumColor.GRAY, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("light_gray_banner", (Block) (new BlockBanner(EnumColor.LIGHT_GRAY, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("cyan_banner", (Block) (new BlockBanner(EnumColor.CYAN, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("purple_banner", (Block) (new BlockBanner(EnumColor.PURPLE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("blue_banner", (Block) (new BlockBanner(EnumColor.BLUE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("brown_banner", (Block) (new BlockBanner(EnumColor.BROWN, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("green_banner", (Block) (new BlockBanner(EnumColor.GREEN, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("red_banner", (Block) (new BlockBanner(EnumColor.RED, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("black_banner", (Block) (new BlockBanner(EnumColor.BLACK, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("white_wall_banner", (Block) (new BlockBannerWall(EnumColor.WHITE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("orange_wall_banner", (Block) (new BlockBannerWall(EnumColor.ORANGE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("magenta_wall_banner", (Block) (new BlockBannerWall(EnumColor.MAGENTA, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("light_blue_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIGHT_BLUE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("yellow_wall_banner", (Block) (new BlockBannerWall(EnumColor.YELLOW, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("lime_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIME, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("pink_wall_banner", (Block) (new BlockBannerWall(EnumColor.PINK, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("gray_wall_banner", (Block) (new BlockBannerWall(EnumColor.GRAY, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("light_gray_wall_banner", (Block) (new BlockBannerWall(EnumColor.LIGHT_GRAY, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("cyan_wall_banner", (Block) (new BlockBannerWall(EnumColor.CYAN, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("purple_wall_banner", (Block) (new BlockBannerWall(EnumColor.PURPLE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("blue_wall_banner", (Block) (new BlockBannerWall(EnumColor.BLUE, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("brown_wall_banner", (Block) (new BlockBannerWall(EnumColor.BROWN, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("green_wall_banner", (Block) (new BlockBannerWall(EnumColor.GREEN, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("red_wall_banner", (Block) (new BlockBannerWall(EnumColor.RED, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        a("black_wall_banner", (Block) (new BlockBannerWall(EnumColor.BLACK, Block.Info.a(Material.WOOD).a().b(1.0F).a(SoundEffectType.a))));
        Block block18 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.q).b(0.8F));

        a("red_sandstone", block18);
        a("chiseled_red_sandstone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.q).b(0.8F)));
        a("cut_red_sandstone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.q).b(0.8F)));
        a("red_sandstone_stairs", (Block) (new BlockStairs(block18.getBlockData(), Block.Info.a(block18))));
        a("oak_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.WOOD, MaterialMapColor.o).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("spruce_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.WOOD, MaterialMapColor.J).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("birch_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.WOOD, MaterialMapColor.d).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("jungle_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.WOOD, MaterialMapColor.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("acacia_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.WOOD, MaterialMapColor.q).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("dark_oak_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.WOOD, MaterialMapColor.B).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("stone_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.m).a(2.0F, 6.0F))));
        a("sandstone_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.d).a(2.0F, 6.0F))));
        a("petrified_oak_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.o).a(2.0F, 6.0F))));
        a("cobblestone_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.m).a(2.0F, 6.0F))));
        a("brick_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.D).a(2.0F, 6.0F))));
        a("stone_brick_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.m).a(2.0F, 6.0F))));
        a("nether_brick_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.K).a(2.0F, 6.0F))));
        a("quartz_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.p).a(2.0F, 6.0F))));
        a("red_sandstone_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.q).a(2.0F, 6.0F))));
        a("purpur_slab", (Block) (new BlockStepAbstract(Block.Info.a(Material.STONE, MaterialMapColor.r).a(2.0F, 6.0F))));
        a("smooth_stone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.m).a(2.0F, 6.0F)));
        a("smooth_sandstone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.d).a(2.0F, 6.0F)));
        a("smooth_quartz", new Block(Block.Info.a(Material.STONE, MaterialMapColor.p).a(2.0F, 6.0F)));
        a("smooth_red_sandstone", new Block(Block.Info.a(Material.STONE, MaterialMapColor.q).a(2.0F, 6.0F)));
        a("spruce_fence_gate", (Block) (new BlockFenceGate(Block.Info.a(Material.WOOD, block2.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("birch_fence_gate", (Block) (new BlockFenceGate(Block.Info.a(Material.WOOD, block3.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("jungle_fence_gate", (Block) (new BlockFenceGate(Block.Info.a(Material.WOOD, block4.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("acacia_fence_gate", (Block) (new BlockFenceGate(Block.Info.a(Material.WOOD, block5.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("dark_oak_fence_gate", (Block) (new BlockFenceGate(Block.Info.a(Material.WOOD, block6.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("spruce_fence", (Block) (new BlockFence(Block.Info.a(Material.WOOD, block2.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("birch_fence", (Block) (new BlockFence(Block.Info.a(Material.WOOD, block3.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("jungle_fence", (Block) (new BlockFence(Block.Info.a(Material.WOOD, block4.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("acacia_fence", (Block) (new BlockFence(Block.Info.a(Material.WOOD, block5.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("dark_oak_fence", (Block) (new BlockFence(Block.Info.a(Material.WOOD, block6.l).a(2.0F, 3.0F).a(SoundEffectType.a))));
        a("spruce_door", (Block) (new BlockDoor(Block.Info.a(Material.WOOD, block2.l).b(3.0F).a(SoundEffectType.a))));
        a("birch_door", (Block) (new BlockDoor(Block.Info.a(Material.WOOD, block3.l).b(3.0F).a(SoundEffectType.a))));
        a("jungle_door", (Block) (new BlockDoor(Block.Info.a(Material.WOOD, block4.l).b(3.0F).a(SoundEffectType.a))));
        a("acacia_door", (Block) (new BlockDoor(Block.Info.a(Material.WOOD, block5.l).b(3.0F).a(SoundEffectType.a))));
        a("dark_oak_door", (Block) (new BlockDoor(Block.Info.a(Material.WOOD, block6.l).b(3.0F).a(SoundEffectType.a))));
        a("end_rod", (Block) (new BlockEndRod(Block.Info.a(Material.ORIENTABLE).b().a(14).a(SoundEffectType.a))));
        BlockChorusFruit blockchorusfruit = new BlockChorusFruit(Block.Info.a(Material.PLANT, MaterialMapColor.z).b(0.4F).a(SoundEffectType.a));

        a("chorus_plant", (Block) blockchorusfruit);
        a("chorus_flower", (Block) (new BlockChorusFlower(blockchorusfruit, Block.Info.a(Material.PLANT, MaterialMapColor.z).c().b(0.4F).a(SoundEffectType.a))));
        Block block19 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.r).a(1.5F, 6.0F));

        a("purpur_block", block19);
        a("purpur_pillar", (Block) (new BlockRotatable(Block.Info.a(Material.STONE, MaterialMapColor.r).a(1.5F, 6.0F))));
        a("purpur_stairs", (Block) (new BlockStairs(block19.getBlockData(), Block.Info.a(block19))));
        a("end_stone_bricks", new Block(Block.Info.a(Material.STONE, MaterialMapColor.d).b(0.8F)));
        a("beetroots", (Block) (new BlockBeetroot(Block.Info.a(Material.PLANT).a().c().b().a(SoundEffectType.c))));
        BlockGrassPath blockgrasspath = new BlockGrassPath(Block.Info.a(Material.EARTH).b(0.65F).a(SoundEffectType.c));

        a("grass_path", (Block) blockgrasspath);
        a("end_gateway", (Block) (new BlockEndGateway(Block.Info.a(Material.PORTAL, MaterialMapColor.E).a().a(15).a(-1.0F, 3600000.0F))));
        a("repeating_command_block", (Block) (new BlockCommand(Block.Info.a(Material.ORE, MaterialMapColor.z).a(-1.0F, 3600000.0F))));
        a("chain_command_block", (Block) (new BlockCommand(Block.Info.a(Material.ORE, MaterialMapColor.C).a(-1.0F, 3600000.0F))));
        a("frosted_ice", (Block) (new BlockIceFrost(Block.Info.a(Material.ICE).a(0.98F).c().b(0.5F).a(SoundEffectType.f))));
        a("magma_block", (Block) (new BlockMagma(Block.Info.a(Material.STONE, MaterialMapColor.K).a(3).c().b(0.5F))));
        a("nether_wart_block", new Block(Block.Info.a(Material.GRASS, MaterialMapColor.D).b(1.0F).a(SoundEffectType.a)));
        a("red_nether_bricks", new Block(Block.Info.a(Material.STONE, MaterialMapColor.K).a(2.0F, 6.0F)));
        a("bone_block", (Block) (new BlockRotatable(Block.Info.a(Material.STONE, MaterialMapColor.d).b(2.0F))));
        a("structure_void", (Block) (new BlockStructureVoid(Block.Info.a(Material.STRUCTURE_VOID).a())));
        a("observer", (Block) (new BlockObserver(Block.Info.a(Material.STONE).b(3.0F))));
        a("shulker_box", (Block) (new BlockShulkerBox((EnumColor) null, Block.Info.a(Material.STONE, MaterialMapColor.z).b(2.0F).d())));
        a("white_shulker_box", (Block) (new BlockShulkerBox(EnumColor.WHITE, Block.Info.a(Material.STONE, MaterialMapColor.j).b(2.0F).d())));
        a("orange_shulker_box", (Block) (new BlockShulkerBox(EnumColor.ORANGE, Block.Info.a(Material.STONE, MaterialMapColor.q).b(2.0F).d())));
        a("magenta_shulker_box", (Block) (new BlockShulkerBox(EnumColor.MAGENTA, Block.Info.a(Material.STONE, MaterialMapColor.r).b(2.0F).d())));
        a("light_blue_shulker_box", (Block) (new BlockShulkerBox(EnumColor.LIGHT_BLUE, Block.Info.a(Material.STONE, MaterialMapColor.s).b(2.0F).d())));
        a("yellow_shulker_box", (Block) (new BlockShulkerBox(EnumColor.YELLOW, Block.Info.a(Material.STONE, MaterialMapColor.t).b(2.0F).d())));
        a("lime_shulker_box", (Block) (new BlockShulkerBox(EnumColor.LIME, Block.Info.a(Material.STONE, MaterialMapColor.u).b(2.0F).d())));
        a("pink_shulker_box", (Block) (new BlockShulkerBox(EnumColor.PINK, Block.Info.a(Material.STONE, MaterialMapColor.v).b(2.0F).d())));
        a("gray_shulker_box", (Block) (new BlockShulkerBox(EnumColor.GRAY, Block.Info.a(Material.STONE, MaterialMapColor.w).b(2.0F).d())));
        a("light_gray_shulker_box", (Block) (new BlockShulkerBox(EnumColor.LIGHT_GRAY, Block.Info.a(Material.STONE, MaterialMapColor.x).b(2.0F).d())));
        a("cyan_shulker_box", (Block) (new BlockShulkerBox(EnumColor.CYAN, Block.Info.a(Material.STONE, MaterialMapColor.y).b(2.0F).d())));
        a("purple_shulker_box", (Block) (new BlockShulkerBox(EnumColor.PURPLE, Block.Info.a(Material.STONE, MaterialMapColor.V).b(2.0F).d())));
        a("blue_shulker_box", (Block) (new BlockShulkerBox(EnumColor.BLUE, Block.Info.a(Material.STONE, MaterialMapColor.A).b(2.0F).d())));
        a("brown_shulker_box", (Block) (new BlockShulkerBox(EnumColor.BROWN, Block.Info.a(Material.STONE, MaterialMapColor.B).b(2.0F).d())));
        a("green_shulker_box", (Block) (new BlockShulkerBox(EnumColor.GREEN, Block.Info.a(Material.STONE, MaterialMapColor.C).b(2.0F).d())));
        a("red_shulker_box", (Block) (new BlockShulkerBox(EnumColor.RED, Block.Info.a(Material.STONE, MaterialMapColor.D).b(2.0F).d())));
        a("black_shulker_box", (Block) (new BlockShulkerBox(EnumColor.BLACK, Block.Info.a(Material.STONE, MaterialMapColor.E).b(2.0F).d())));
        a("white_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.WHITE).b(1.4F))));
        a("orange_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.ORANGE).b(1.4F))));
        a("magenta_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.MAGENTA).b(1.4F))));
        a("light_blue_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.LIGHT_BLUE).b(1.4F))));
        a("yellow_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.YELLOW).b(1.4F))));
        a("lime_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.LIME).b(1.4F))));
        a("pink_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.PINK).b(1.4F))));
        a("gray_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.GRAY).b(1.4F))));
        a("light_gray_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.LIGHT_GRAY).b(1.4F))));
        a("cyan_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.CYAN).b(1.4F))));
        a("purple_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.PURPLE).b(1.4F))));
        a("blue_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.BLUE).b(1.4F))));
        a("brown_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.BROWN).b(1.4F))));
        a("green_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.GREEN).b(1.4F))));
        a("red_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.RED).b(1.4F))));
        a("black_glazed_terracotta", (Block) (new BlockGlazedTerracotta(Block.Info.a(Material.STONE, EnumColor.BLACK).b(1.4F))));
        Block block20 = new Block(Block.Info.a(Material.STONE, EnumColor.WHITE).b(1.8F));
        Block block21 = new Block(Block.Info.a(Material.STONE, EnumColor.ORANGE).b(1.8F));
        Block block22 = new Block(Block.Info.a(Material.STONE, EnumColor.MAGENTA).b(1.8F));
        Block block23 = new Block(Block.Info.a(Material.STONE, EnumColor.LIGHT_BLUE).b(1.8F));
        Block block24 = new Block(Block.Info.a(Material.STONE, EnumColor.YELLOW).b(1.8F));
        Block block25 = new Block(Block.Info.a(Material.STONE, EnumColor.LIME).b(1.8F));
        Block block26 = new Block(Block.Info.a(Material.STONE, EnumColor.PINK).b(1.8F));
        Block block27 = new Block(Block.Info.a(Material.STONE, EnumColor.GRAY).b(1.8F));
        Block block28 = new Block(Block.Info.a(Material.STONE, EnumColor.LIGHT_GRAY).b(1.8F));
        Block block29 = new Block(Block.Info.a(Material.STONE, EnumColor.CYAN).b(1.8F));
        Block block30 = new Block(Block.Info.a(Material.STONE, EnumColor.PURPLE).b(1.8F));
        Block block31 = new Block(Block.Info.a(Material.STONE, EnumColor.BLUE).b(1.8F));
        Block block32 = new Block(Block.Info.a(Material.STONE, EnumColor.BROWN).b(1.8F));
        Block block33 = new Block(Block.Info.a(Material.STONE, EnumColor.GREEN).b(1.8F));
        Block block34 = new Block(Block.Info.a(Material.STONE, EnumColor.RED).b(1.8F));
        Block block35 = new Block(Block.Info.a(Material.STONE, EnumColor.BLACK).b(1.8F));

        a("white_concrete", block20);
        a("orange_concrete", block21);
        a("magenta_concrete", block22);
        a("light_blue_concrete", block23);
        a("yellow_concrete", block24);
        a("lime_concrete", block25);
        a("pink_concrete", block26);
        a("gray_concrete", block27);
        a("light_gray_concrete", block28);
        a("cyan_concrete", block29);
        a("purple_concrete", block30);
        a("blue_concrete", block31);
        a("brown_concrete", block32);
        a("green_concrete", block33);
        a("red_concrete", block34);
        a("black_concrete", block35);
        a("white_concrete_powder", (Block) (new BlockConcretePowder(block20, Block.Info.a(Material.SAND, EnumColor.WHITE).b(0.5F).a(SoundEffectType.h))));
        a("orange_concrete_powder", (Block) (new BlockConcretePowder(block21, Block.Info.a(Material.SAND, EnumColor.ORANGE).b(0.5F).a(SoundEffectType.h))));
        a("magenta_concrete_powder", (Block) (new BlockConcretePowder(block22, Block.Info.a(Material.SAND, EnumColor.MAGENTA).b(0.5F).a(SoundEffectType.h))));
        a("light_blue_concrete_powder", (Block) (new BlockConcretePowder(block23, Block.Info.a(Material.SAND, EnumColor.LIGHT_BLUE).b(0.5F).a(SoundEffectType.h))));
        a("yellow_concrete_powder", (Block) (new BlockConcretePowder(block24, Block.Info.a(Material.SAND, EnumColor.YELLOW).b(0.5F).a(SoundEffectType.h))));
        a("lime_concrete_powder", (Block) (new BlockConcretePowder(block25, Block.Info.a(Material.SAND, EnumColor.LIME).b(0.5F).a(SoundEffectType.h))));
        a("pink_concrete_powder", (Block) (new BlockConcretePowder(block26, Block.Info.a(Material.SAND, EnumColor.PINK).b(0.5F).a(SoundEffectType.h))));
        a("gray_concrete_powder", (Block) (new BlockConcretePowder(block27, Block.Info.a(Material.SAND, EnumColor.GRAY).b(0.5F).a(SoundEffectType.h))));
        a("light_gray_concrete_powder", (Block) (new BlockConcretePowder(block28, Block.Info.a(Material.SAND, EnumColor.LIGHT_GRAY).b(0.5F).a(SoundEffectType.h))));
        a("cyan_concrete_powder", (Block) (new BlockConcretePowder(block29, Block.Info.a(Material.SAND, EnumColor.CYAN).b(0.5F).a(SoundEffectType.h))));
        a("purple_concrete_powder", (Block) (new BlockConcretePowder(block30, Block.Info.a(Material.SAND, EnumColor.PURPLE).b(0.5F).a(SoundEffectType.h))));
        a("blue_concrete_powder", (Block) (new BlockConcretePowder(block31, Block.Info.a(Material.SAND, EnumColor.BLUE).b(0.5F).a(SoundEffectType.h))));
        a("brown_concrete_powder", (Block) (new BlockConcretePowder(block32, Block.Info.a(Material.SAND, EnumColor.BROWN).b(0.5F).a(SoundEffectType.h))));
        a("green_concrete_powder", (Block) (new BlockConcretePowder(block33, Block.Info.a(Material.SAND, EnumColor.GREEN).b(0.5F).a(SoundEffectType.h))));
        a("red_concrete_powder", (Block) (new BlockConcretePowder(block34, Block.Info.a(Material.SAND, EnumColor.RED).b(0.5F).a(SoundEffectType.h))));
        a("black_concrete_powder", (Block) (new BlockConcretePowder(block35, Block.Info.a(Material.SAND, EnumColor.BLACK).b(0.5F).a(SoundEffectType.h))));
        BlockKelp blockkelp = new BlockKelp(Block.Info.a(Material.WATER_PLANT).a().c().b().a(SoundEffectType.m));

        a("kelp", (Block) blockkelp);
        a("kelp_plant", (Block) (new BlockKelpPlant(blockkelp, Block.Info.a(Material.WATER_PLANT).a().b().a(SoundEffectType.m))));
        a("dried_kelp_block", new Block(Block.Info.a(Material.GRASS, MaterialMapColor.B).a(0.5F, 2.5F).a(SoundEffectType.c)));
        a("turtle_egg", (Block) (new BlockTurtleEgg(Block.Info.a(Material.DRAGON_EGG, MaterialMapColor.x).b(0.5F).a(SoundEffectType.e).c())));
        Block block36 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.w).a(1.5F, 6.0F));
        Block block37 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.w).a(1.5F, 6.0F));
        Block block38 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.w).a(1.5F, 6.0F));
        Block block39 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.w).a(1.5F, 6.0F));
        Block block40 = new Block(Block.Info.a(Material.STONE, MaterialMapColor.w).a(1.5F, 6.0F));

        a("dead_tube_coral_block", block36);
        a("dead_brain_coral_block", block37);
        a("dead_bubble_coral_block", block38);
        a("dead_fire_coral_block", block39);
        a("dead_horn_coral_block", block40);
        a("tube_coral_block", (Block) (new BlockCoral(block36, Block.Info.a(Material.STONE, MaterialMapColor.A).a(1.5F, 6.0F).a(SoundEffectType.n))));
        a("brain_coral_block", (Block) (new BlockCoral(block37, Block.Info.a(Material.STONE, MaterialMapColor.v).a(1.5F, 6.0F).a(SoundEffectType.n))));
        a("bubble_coral_block", (Block) (new BlockCoral(block38, Block.Info.a(Material.STONE, MaterialMapColor.z).a(1.5F, 6.0F).a(SoundEffectType.n))));
        a("fire_coral_block", (Block) (new BlockCoral(block39, Block.Info.a(Material.STONE, MaterialMapColor.D).a(1.5F, 6.0F).a(SoundEffectType.n))));
        a("horn_coral_block", (Block) (new BlockCoral(block40, Block.Info.a(Material.STONE, MaterialMapColor.t).a(1.5F, 6.0F).a(SoundEffectType.n))));
        BlockCoralDead blockcoraldead = new BlockCoralDead(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralDead blockcoraldead1 = new BlockCoralDead(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralDead blockcoraldead2 = new BlockCoralDead(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralDead blockcoraldead3 = new BlockCoralDead(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralDead blockcoraldead4 = new BlockCoralDead(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());

        a("dead_tube_coral", (Block) blockcoraldead);
        a("dead_brain_coral", (Block) blockcoraldead1);
        a("dead_bubble_coral", (Block) blockcoraldead2);
        a("dead_fire_coral", (Block) blockcoraldead3);
        a("dead_horn_coral", (Block) blockcoraldead4);
        a("tube_coral", (Block) (new BlockCoralPlant(blockcoraldead, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.A).a().b().a(SoundEffectType.m))));
        a("brain_coral", (Block) (new BlockCoralPlant(blockcoraldead1, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.v).a().b().a(SoundEffectType.m))));
        a("bubble_coral", (Block) (new BlockCoralPlant(blockcoraldead2, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.z).a().b().a(SoundEffectType.m))));
        a("fire_coral", (Block) (new BlockCoralPlant(blockcoraldead3, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.D).a().b().a(SoundEffectType.m))));
        a("horn_coral", (Block) (new BlockCoralPlant(blockcoraldead4, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.t).a().b().a(SoundEffectType.m))));
        BlockCoralFanWallAbstract blockcoralfanwallabstract = new BlockCoralFanWallAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanWallAbstract blockcoralfanwallabstract1 = new BlockCoralFanWallAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanWallAbstract blockcoralfanwallabstract2 = new BlockCoralFanWallAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanWallAbstract blockcoralfanwallabstract3 = new BlockCoralFanWallAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanWallAbstract blockcoralfanwallabstract4 = new BlockCoralFanWallAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());

        a("dead_tube_coral_wall_fan", (Block) blockcoralfanwallabstract);
        a("dead_brain_coral_wall_fan", (Block) blockcoralfanwallabstract1);
        a("dead_bubble_coral_wall_fan", (Block) blockcoralfanwallabstract2);
        a("dead_fire_coral_wall_fan", (Block) blockcoralfanwallabstract3);
        a("dead_horn_coral_wall_fan", (Block) blockcoralfanwallabstract4);
        a("tube_coral_wall_fan", (Block) (new BlockCoralFanWall(blockcoralfanwallabstract, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.A).a().b().a(SoundEffectType.m))));
        a("brain_coral_wall_fan", (Block) (new BlockCoralFanWall(blockcoralfanwallabstract1, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.v).a().b().a(SoundEffectType.m))));
        a("bubble_coral_wall_fan", (Block) (new BlockCoralFanWall(blockcoralfanwallabstract2, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.z).a().b().a(SoundEffectType.m))));
        a("fire_coral_wall_fan", (Block) (new BlockCoralFanWall(blockcoralfanwallabstract3, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.D).a().b().a(SoundEffectType.m))));
        a("horn_coral_wall_fan", (Block) (new BlockCoralFanWall(blockcoralfanwallabstract4, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.t).a().b().a(SoundEffectType.m))));
        BlockCoralFanAbstract blockcoralfanabstract = new BlockCoralFanAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanAbstract blockcoralfanabstract1 = new BlockCoralFanAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanAbstract blockcoralfanabstract2 = new BlockCoralFanAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanAbstract blockcoralfanabstract3 = new BlockCoralFanAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());
        BlockCoralFanAbstract blockcoralfanabstract4 = new BlockCoralFanAbstract(Block.Info.a(Material.STONE, MaterialMapColor.w).a().b());

        a("dead_tube_coral_fan", (Block) blockcoralfanabstract);
        a("dead_brain_coral_fan", (Block) blockcoralfanabstract1);
        a("dead_bubble_coral_fan", (Block) blockcoralfanabstract2);
        a("dead_fire_coral_fan", (Block) blockcoralfanabstract3);
        a("dead_horn_coral_fan", (Block) blockcoralfanabstract4);
        a("tube_coral_fan", (Block) (new BlockCoralFan(blockcoralfanabstract, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.A).a().b().a(SoundEffectType.m))));
        a("brain_coral_fan", (Block) (new BlockCoralFan(blockcoralfanabstract1, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.v).a().b().a(SoundEffectType.m))));
        a("bubble_coral_fan", (Block) (new BlockCoralFan(blockcoralfanabstract2, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.z).a().b().a(SoundEffectType.m))));
        a("fire_coral_fan", (Block) (new BlockCoralFan(blockcoralfanabstract3, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.D).a().b().a(SoundEffectType.m))));
        a("horn_coral_fan", (Block) (new BlockCoralFan(blockcoralfanabstract4, Block.Info.a(Material.WATER_PLANT, MaterialMapColor.t).a().b().a(SoundEffectType.m))));
        a("sea_pickle", (Block) (new BlockSeaPickle(Block.Info.a(Material.WATER_PLANT, MaterialMapColor.C).a(3).a(SoundEffectType.l))));
        a("blue_ice", (Block) (new BlockBlueIce(Block.Info.a(Material.SNOW_LAYER).b(2.8F).a(0.989F).a(SoundEffectType.f))));
        a("conduit", (Block) (new BlockConduit(Block.Info.a(Material.SHATTERABLE, MaterialMapColor.G).b(3.0F).a(15))));
        a("void_air", (Block) (new BlockAir(Block.Info.a(Material.AIR).a())));
        a("cave_air", (Block) (new BlockAir(Block.Info.a(Material.AIR).a())));
        a("bubble_column", (Block) (new BlockBubbleColumn(Block.Info.a(Material.BUBBLE_COLUMN).a())));
        a("structure_block", (Block) (new BlockStructure(Block.Info.a(Material.ORE, MaterialMapColor.x).a(-1.0F, 3600000.0F))));
        Iterator iterator = IRegistry.BLOCK.iterator();

        while (iterator.hasNext()) {
            Block block41 = (Block) iterator.next();
            UnmodifiableIterator unmodifiableiterator = block41.getStates().a().iterator();

            while (unmodifiableiterator.hasNext()) {
                IBlockData iblockdata = (IBlockData) unmodifiableiterator.next();

                Block.REGISTRY_ID.b(iblockdata);
            }
        }

    }

    private static void a(MinecraftKey minecraftkey, Block block) {
        IRegistry.BLOCK.a(minecraftkey, (Object) block);
    }

    private static void a(String s, Block block) {
        a(new MinecraftKey(s), block);
    }

    public static enum EnumRandomOffset {

        NONE, XZ, XYZ;

        private EnumRandomOffset() {}
    }

    public static class Info {

        private Material a;
        private MaterialMapColor b;
        private boolean c = true;
        private SoundEffectType d;
        private int e;
        private float f;
        private float g;
        private boolean h;
        private float i;
        private boolean j;

        private Info(Material material, MaterialMapColor materialmapcolor) {
            this.d = SoundEffectType.d;
            this.i = 0.6F;
            this.a = material;
            this.b = materialmapcolor;
        }

        public static Block.Info a(Material material) {
            return a(material, material.i());
        }

        public static Block.Info a(Material material, EnumColor enumcolor) {
            return a(material, enumcolor.e());
        }

        public static Block.Info a(Material material, MaterialMapColor materialmapcolor) {
            return new Block.Info(material, materialmapcolor);
        }

        public static Block.Info a(Block block) {
            Block.Info block_info = new Block.Info(block.material, block.l);

            block_info.a = block.material;
            block_info.g = block.strength;
            block_info.f = block.durability;
            block_info.c = block.n;
            block_info.h = block.i;
            block_info.e = block.f;
            block_info.a = block.material;
            block_info.b = block.l;
            block_info.d = block.stepSound;
            block_info.i = block.n();
            block_info.j = block.o;
            return block_info;
        }

        public Block.Info a() {
            this.c = false;
            return this;
        }

        public Block.Info a(float f) {
            this.i = f;
            return this;
        }

        protected Block.Info a(SoundEffectType soundeffecttype) {
            this.d = soundeffecttype;
            return this;
        }

        protected Block.Info a(int i) {
            this.e = i;
            return this;
        }

        public Block.Info a(float f, float f1) {
            this.g = f;
            this.f = Math.max(0.0F, f1);
            return this;
        }

        protected Block.Info b() {
            return this.b(0.0F);
        }

        protected Block.Info b(float f) {
            this.a(f, f);
            return this;
        }

        protected Block.Info c() {
            this.h = true;
            return this;
        }

        protected Block.Info d() {
            this.j = true;
            return this;
        }
    }

    public static final class a {

        private final IBlockData a;
        private final IBlockData b;
        private final EnumDirection c;

        public a(IBlockData iblockdata, IBlockData iblockdata1, EnumDirection enumdirection) {
            this.a = iblockdata;
            this.b = iblockdata1;
            this.c = enumdirection;
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (!(object instanceof Block.a)) {
                return false;
            } else {
                Block.a block_a = (Block.a) object;

                return this.a == block_a.a && this.b == block_a.b && this.c == block_a.c;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[] { this.a, this.b, this.c});
        }
    }
}
