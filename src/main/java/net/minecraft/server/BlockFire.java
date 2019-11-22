package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import com.destroystokyo.paper.event.block.TNTPrimeEvent; // Paper - TNTPrimeEvent
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockSpreadEvent;
// CraftBukkit end

public class BlockFire extends Block {

    public static final BlockStateInteger AGE = BlockProperties.ad;
    public static final BlockStateBoolean NORTH = BlockSprawling.a;
    public static final BlockStateBoolean EAST = BlockSprawling.b;
    public static final BlockStateBoolean SOUTH = BlockSprawling.c;
    public static final BlockStateBoolean WEST = BlockSprawling.d;
    public static final BlockStateBoolean UPPER = BlockSprawling.e;
    private static final Map<EnumDirection, BlockStateBoolean> g = (Map) BlockSprawling.g.entrySet().stream().filter((entry) -> {
        return entry.getKey() != EnumDirection.DOWN;
    }).collect(SystemUtils.a());
    private final Object2IntMap<Block> flameChances = new Object2IntOpenHashMap();
    private final Object2IntMap<Block> i = new Object2IntOpenHashMap();

    protected BlockFire(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockFire.AGE, 0)).set(BlockFire.NORTH, false)).set(BlockFire.EAST, false)).set(BlockFire.SOUTH, false)).set(BlockFire.WEST, false)).set(BlockFire.UPPER, false));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return VoxelShapes.a();
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        // CraftBukkit start
        if (!iblockdata.canPlace(generatoraccess, blockposition)) {
            CraftBlockState blockState = CraftBlockState.getBlockState(generatoraccess, blockposition);
            blockState.setData(Blocks.AIR.getBlockData());

            BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
            generatoraccess.getMinecraftWorld().getMinecraftServer().server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                return blockState.getHandle();
            }
        }
        return this.a((IBlockAccess) generatoraccess, blockposition).set(BlockFire.AGE, iblockdata.get(BlockFire.AGE));
        // CraftBukkit end
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return this.a((IBlockAccess) blockactioncontext.getWorld(), blockactioncontext.getClickPosition());
    }

    public IBlockData a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        IBlockData iblockdata = iblockaccess.getType(blockposition1);

        if (!this.j(iblockdata) && !iblockdata.d(iblockaccess, blockposition1, EnumDirection.UP)) {
            IBlockData iblockdata1 = this.getBlockData();
            EnumDirection[] aenumdirection = EnumDirection.values();
            int i = aenumdirection.length;

            for (int j = 0; j < i; ++j) {
                EnumDirection enumdirection = aenumdirection[j];
                BlockStateBoolean blockstateboolean = (BlockStateBoolean) BlockFire.g.get(enumdirection);

                if (blockstateboolean != null) {
                    iblockdata1 = (IBlockData) iblockdata1.set(blockstateboolean, this.j(iblockaccess.getType(blockposition.shift(enumdirection))));
                }
            }

            return iblockdata1;
        } else {
            return this.getBlockData();
        }
    }

    @Override
    public boolean canPlace(IBlockData iblockdata, IWorldReader iworldreader, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        return iworldreader.getType(blockposition1).d(iworldreader, blockposition1, EnumDirection.UP) || this.canBurn(iworldreader, blockposition);
    }

    @Override
    public int a(IWorldReader iworldreader) {
        return 30;
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            if (!iblockdata.canPlace(world, blockposition)) {
                fireExtinguished(world, blockposition); // CraftBukkit - invalid place location
            }

            Block block = world.getType(blockposition.down()).getBlock();
            boolean flag = world.worldProvider instanceof WorldProviderTheEnd && block == Blocks.BEDROCK || block == Blocks.NETHERRACK || block == Blocks.MAGMA_BLOCK;
            int i = (Integer) iblockdata.get(BlockFire.AGE);

            if (!flag && world.isRaining() && this.a(world, blockposition) && random.nextFloat() < 0.2F + (float) i * 0.03F) {
                fireExtinguished(world, blockposition); // CraftBukkit - extinguished by rain
            } else {
                int j = Math.min(15, i + random.nextInt(3) / 2);

                if (i != j) {
                    iblockdata = (IBlockData) iblockdata.set(BlockFire.AGE, j);
                    world.setTypeAndData(blockposition, iblockdata, 4);
                }

                if (!flag) {
                    world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world) + random.nextInt(10));
                    if (!this.canBurn(world, blockposition)) {
                        BlockPosition blockposition1 = blockposition.down();

                        if (!world.getType(blockposition1).d(world, blockposition1, EnumDirection.UP) || i > 3) {
                            fireExtinguished(world, blockposition); // CraftBukkit
                        }

                        return;
                    }

                    if (i == 15 && random.nextInt(4) == 0 && !this.j(world.getType(blockposition.down()))) {
                        fireExtinguished(world, blockposition); // CraftBukkit
                        return;
                    }
                }

                boolean flag1 = world.s(blockposition);
                int k = flag1 ? -50 : 0;

                // CraftBukkit start - add source blockposition to burn calls
                this.a(world, blockposition.east(), 300 + k, random, i, blockposition);
                this.a(world, blockposition.west(), 300 + k, random, i, blockposition);
                this.a(world, blockposition.down(), 250 + k, random, i, blockposition);
                this.a(world, blockposition.up(), 250 + k, random, i, blockposition);
                this.a(world, blockposition.north(), 300 + k, random, i, blockposition);
                this.a(world, blockposition.south(), 300 + k, random, i, blockposition);
                // CraftBukkit end
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                for (int l = -1; l <= 1; ++l) {
                    for (int i1 = -1; i1 <= 1; ++i1) {
                        for (int j1 = -1; j1 <= 4; ++j1) {
                            if (l != 0 || j1 != 0 || i1 != 0) {
                                int k1 = 100;

                                if (j1 > 1) {
                                    k1 += (j1 - 1) * 100;
                                }

                                blockposition_mutableblockposition.g(blockposition).e(l, j1, i1);
                                if (!world.isLoaded(blockposition_mutableblockposition)) continue; // Paper
                                int l1 = this.a((IWorldReader) world, (BlockPosition) blockposition_mutableblockposition);

                                if (l1 > 0) {
                                    int i2 = (l1 + 40 + world.getDifficulty().a() * 7) / (i + 30);

                                    if (flag1) {
                                        i2 /= 2;
                                    }

                                    if (i2 > 0 && random.nextInt(k1) <= i2 && (!world.isRaining() || !this.a(world, (BlockPosition) blockposition_mutableblockposition))) {
                                        int j2 = Math.min(15, i + random.nextInt(5) / 4);

                                        // CraftBukkit start - Call to stop spread of fire
                                        if (world.getType(blockposition_mutableblockposition).getBlock() != Blocks.FIRE) {
                                            if (CraftEventFactory.callBlockIgniteEvent(world, blockposition_mutableblockposition, blockposition).isCancelled()) {
                                                continue;
                                            }

                                            CraftEventFactory.handleBlockSpreadEvent(world, blockposition, blockposition_mutableblockposition, (IBlockData) this.a((IBlockAccess) world, (BlockPosition) blockposition_mutableblockposition).set(BlockFire.AGE, j2), 3); // CraftBukkit
                                        }
                                        // CraftBukkit end
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    }

    protected boolean a(World world, BlockPosition blockposition) {
        return world.isRainingAt(blockposition) || world.isRainingAt(blockposition.west()) || world.isRainingAt(blockposition.east()) || world.isRainingAt(blockposition.north()) || world.isRainingAt(blockposition.south());
    }

    private int q(IBlockData iblockdata) {
        return iblockdata.b((IBlockState) BlockProperties.C) && (Boolean) iblockdata.get(BlockProperties.C) ? 0 : this.i.getInt(iblockdata.getBlock());
    }

    private int r(IBlockData iblockdata) {
        return iblockdata.b((IBlockState) BlockProperties.C) && (Boolean) iblockdata.get(BlockProperties.C) ? 0 : this.flameChances.getInt(iblockdata.getBlock());
    }

    private void a(World world, BlockPosition blockposition, int i, Random random, int j, BlockPosition sourceposition) { // CraftBukkit add sourceposition
        // Paper start
        final IBlockData iblockdata = world.getTypeIfLoaded(blockposition);
        if (iblockdata == null) {
            return;
        }
        int k = this.q(iblockdata);
        // Paper end

        if (random.nextInt(i) < k) {
            //IBlockData iblockdata = world.getType(blockposition); // Paper

            // CraftBukkit start
            org.bukkit.block.Block theBlock = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
            org.bukkit.block.Block sourceBlock = world.getWorld().getBlockAt(sourceposition.getX(), sourceposition.getY(), sourceposition.getZ());

            BlockBurnEvent event = new BlockBurnEvent(theBlock, sourceBlock);
            world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return;
            }
            // CraftBukkit end

            if (random.nextInt(j + 10) < 5 && !world.isRainingAt(blockposition)) {
                int l = Math.min(j + random.nextInt(5) / 4, 15);

                world.setTypeAndData(blockposition, (IBlockData) this.a((IBlockAccess) world, blockposition).set(BlockFire.AGE, l), 3);
            } else {
                if(iblockdata.getBlock() != Blocks.TNT) world.a(blockposition, false); // Paper - TNTPrimeEvent - We might be cancelling it below, move the setAir down
            }

            Block block = iblockdata.getBlock();

            if (block instanceof BlockTNT) {
                BlockTNT blocktnt = (BlockTNT) block;

                // Paper start - TNTPrimeEvent
                org.bukkit.block.Block tntBlock = MCUtil.toBukkitBlock(world, blockposition);
                if (!new TNTPrimeEvent(tntBlock, TNTPrimeEvent.PrimeReason.FIRE, null).callEvent()) {
                    return;
                }
                // Paper end
                BlockTNT.a(world, blockposition);
            }
        }

    }

    private boolean canBurn(IBlockAccess iblockaccess, BlockPosition blockposition) {
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];

            if (this.j(iblockaccess.getType(blockposition.shift(enumdirection)))) {
                return true;
            }
        }

        return false;
    }

    private int a(IWorldReader iworldreader, BlockPosition blockposition) {
        if (!iworldreader.isEmpty(blockposition)) {
            return 0;
        } else {
            int i = 0;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int j = aenumdirection.length;

            for (int k = 0; k < j; ++k) {
                EnumDirection enumdirection = aenumdirection[k];
                // Paper start
                IBlockData iblockdata = iworldreader.getTypeIfLoaded(blockposition.shift(enumdirection));
                if (iblockdata == null) {
                    continue;
                }
                // Paper end
                i = Math.max(this.r(iblockdata), i);
            }

            return i;
        }
    }

    public boolean j(IBlockData iblockdata) {
        return this.r(iblockdata) > 0;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            // CraftBukkit - getType()
            if (world.worldProvider.getDimensionManager().getType() != DimensionManager.OVERWORLD && world.worldProvider.getDimensionManager().getType() != DimensionManager.NETHER || !((BlockPortal) Blocks.NETHER_PORTAL).a((GeneratorAccess) world, blockposition)) {
                if (!iblockdata.canPlace(world, blockposition)) {
                    fireExtinguished(world, blockposition); // CraftBukkit - fuel block broke
                } else {
                    world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world) + world.random.nextInt(10));
                }
            }
        }
    }

    @Override
    public TextureType c() {
        return TextureType.CUTOUT;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockFire.AGE, BlockFire.NORTH, BlockFire.EAST, BlockFire.SOUTH, BlockFire.WEST, BlockFire.UPPER);
    }

    public void a(Block block, int i, int j) {
        this.flameChances.put(block, i);
        this.i.put(block, j);
    }

    public static void d() {
        BlockFire blockfire = (BlockFire) Blocks.FIRE;

        blockfire.a(Blocks.OAK_PLANKS, 5, 20);
        blockfire.a(Blocks.SPRUCE_PLANKS, 5, 20);
        blockfire.a(Blocks.BIRCH_PLANKS, 5, 20);
        blockfire.a(Blocks.JUNGLE_PLANKS, 5, 20);
        blockfire.a(Blocks.ACACIA_PLANKS, 5, 20);
        blockfire.a(Blocks.DARK_OAK_PLANKS, 5, 20);
        blockfire.a(Blocks.OAK_SLAB, 5, 20);
        blockfire.a(Blocks.SPRUCE_SLAB, 5, 20);
        blockfire.a(Blocks.BIRCH_SLAB, 5, 20);
        blockfire.a(Blocks.JUNGLE_SLAB, 5, 20);
        blockfire.a(Blocks.ACACIA_SLAB, 5, 20);
        blockfire.a(Blocks.DARK_OAK_SLAB, 5, 20);
        blockfire.a(Blocks.OAK_FENCE_GATE, 5, 20);
        blockfire.a(Blocks.SPRUCE_FENCE_GATE, 5, 20);
        blockfire.a(Blocks.BIRCH_FENCE_GATE, 5, 20);
        blockfire.a(Blocks.JUNGLE_FENCE_GATE, 5, 20);
        blockfire.a(Blocks.DARK_OAK_FENCE_GATE, 5, 20);
        blockfire.a(Blocks.ACACIA_FENCE_GATE, 5, 20);
        blockfire.a(Blocks.OAK_FENCE, 5, 20);
        blockfire.a(Blocks.SPRUCE_FENCE, 5, 20);
        blockfire.a(Blocks.BIRCH_FENCE, 5, 20);
        blockfire.a(Blocks.JUNGLE_FENCE, 5, 20);
        blockfire.a(Blocks.DARK_OAK_FENCE, 5, 20);
        blockfire.a(Blocks.ACACIA_FENCE, 5, 20);
        blockfire.a(Blocks.OAK_STAIRS, 5, 20);
        blockfire.a(Blocks.BIRCH_STAIRS, 5, 20);
        blockfire.a(Blocks.SPRUCE_STAIRS, 5, 20);
        blockfire.a(Blocks.JUNGLE_STAIRS, 5, 20);
        blockfire.a(Blocks.ACACIA_STAIRS, 5, 20);
        blockfire.a(Blocks.DARK_OAK_STAIRS, 5, 20);
        blockfire.a(Blocks.OAK_LOG, 5, 5);
        blockfire.a(Blocks.SPRUCE_LOG, 5, 5);
        blockfire.a(Blocks.BIRCH_LOG, 5, 5);
        blockfire.a(Blocks.JUNGLE_LOG, 5, 5);
        blockfire.a(Blocks.ACACIA_LOG, 5, 5);
        blockfire.a(Blocks.DARK_OAK_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_OAK_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_SPRUCE_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_BIRCH_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_JUNGLE_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_ACACIA_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_DARK_OAK_LOG, 5, 5);
        blockfire.a(Blocks.STRIPPED_OAK_WOOD, 5, 5);
        blockfire.a(Blocks.STRIPPED_SPRUCE_WOOD, 5, 5);
        blockfire.a(Blocks.STRIPPED_BIRCH_WOOD, 5, 5);
        blockfire.a(Blocks.STRIPPED_JUNGLE_WOOD, 5, 5);
        blockfire.a(Blocks.STRIPPED_ACACIA_WOOD, 5, 5);
        blockfire.a(Blocks.STRIPPED_DARK_OAK_WOOD, 5, 5);
        blockfire.a(Blocks.OAK_WOOD, 5, 5);
        blockfire.a(Blocks.SPRUCE_WOOD, 5, 5);
        blockfire.a(Blocks.BIRCH_WOOD, 5, 5);
        blockfire.a(Blocks.JUNGLE_WOOD, 5, 5);
        blockfire.a(Blocks.ACACIA_WOOD, 5, 5);
        blockfire.a(Blocks.DARK_OAK_WOOD, 5, 5);
        blockfire.a(Blocks.OAK_LEAVES, 30, 60);
        blockfire.a(Blocks.SPRUCE_LEAVES, 30, 60);
        blockfire.a(Blocks.BIRCH_LEAVES, 30, 60);
        blockfire.a(Blocks.JUNGLE_LEAVES, 30, 60);
        blockfire.a(Blocks.ACACIA_LEAVES, 30, 60);
        blockfire.a(Blocks.DARK_OAK_LEAVES, 30, 60);
        blockfire.a(Blocks.BOOKSHELF, 30, 20);
        blockfire.a(Blocks.TNT, 15, 100);
        blockfire.a(Blocks.GRASS, 60, 100);
        blockfire.a(Blocks.FERN, 60, 100);
        blockfire.a(Blocks.DEAD_BUSH, 60, 100);
        blockfire.a(Blocks.SUNFLOWER, 60, 100);
        blockfire.a(Blocks.LILAC, 60, 100);
        blockfire.a(Blocks.ROSE_BUSH, 60, 100);
        blockfire.a(Blocks.PEONY, 60, 100);
        blockfire.a(Blocks.TALL_GRASS, 60, 100);
        blockfire.a(Blocks.LARGE_FERN, 60, 100);
        blockfire.a(Blocks.DANDELION, 60, 100);
        blockfire.a(Blocks.POPPY, 60, 100);
        blockfire.a(Blocks.BLUE_ORCHID, 60, 100);
        blockfire.a(Blocks.ALLIUM, 60, 100);
        blockfire.a(Blocks.AZURE_BLUET, 60, 100);
        blockfire.a(Blocks.RED_TULIP, 60, 100);
        blockfire.a(Blocks.ORANGE_TULIP, 60, 100);
        blockfire.a(Blocks.WHITE_TULIP, 60, 100);
        blockfire.a(Blocks.PINK_TULIP, 60, 100);
        blockfire.a(Blocks.OXEYE_DAISY, 60, 100);
        blockfire.a(Blocks.CORNFLOWER, 60, 100);
        blockfire.a(Blocks.LILY_OF_THE_VALLEY, 60, 100);
        blockfire.a(Blocks.WITHER_ROSE, 60, 100);
        blockfire.a(Blocks.WHITE_WOOL, 30, 60);
        blockfire.a(Blocks.ORANGE_WOOL, 30, 60);
        blockfire.a(Blocks.MAGENTA_WOOL, 30, 60);
        blockfire.a(Blocks.LIGHT_BLUE_WOOL, 30, 60);
        blockfire.a(Blocks.YELLOW_WOOL, 30, 60);
        blockfire.a(Blocks.LIME_WOOL, 30, 60);
        blockfire.a(Blocks.PINK_WOOL, 30, 60);
        blockfire.a(Blocks.GRAY_WOOL, 30, 60);
        blockfire.a(Blocks.LIGHT_GRAY_WOOL, 30, 60);
        blockfire.a(Blocks.CYAN_WOOL, 30, 60);
        blockfire.a(Blocks.PURPLE_WOOL, 30, 60);
        blockfire.a(Blocks.BLUE_WOOL, 30, 60);
        blockfire.a(Blocks.BROWN_WOOL, 30, 60);
        blockfire.a(Blocks.GREEN_WOOL, 30, 60);
        blockfire.a(Blocks.RED_WOOL, 30, 60);
        blockfire.a(Blocks.BLACK_WOOL, 30, 60);
        blockfire.a(Blocks.VINE, 15, 100);
        blockfire.a(Blocks.COAL_BLOCK, 5, 5);
        blockfire.a(Blocks.HAY_BLOCK, 60, 20);
        blockfire.a(Blocks.WHITE_CARPET, 60, 20);
        blockfire.a(Blocks.ORANGE_CARPET, 60, 20);
        blockfire.a(Blocks.MAGENTA_CARPET, 60, 20);
        blockfire.a(Blocks.LIGHT_BLUE_CARPET, 60, 20);
        blockfire.a(Blocks.YELLOW_CARPET, 60, 20);
        blockfire.a(Blocks.LIME_CARPET, 60, 20);
        blockfire.a(Blocks.PINK_CARPET, 60, 20);
        blockfire.a(Blocks.GRAY_CARPET, 60, 20);
        blockfire.a(Blocks.LIGHT_GRAY_CARPET, 60, 20);
        blockfire.a(Blocks.CYAN_CARPET, 60, 20);
        blockfire.a(Blocks.PURPLE_CARPET, 60, 20);
        blockfire.a(Blocks.BLUE_CARPET, 60, 20);
        blockfire.a(Blocks.BROWN_CARPET, 60, 20);
        blockfire.a(Blocks.GREEN_CARPET, 60, 20);
        blockfire.a(Blocks.RED_CARPET, 60, 20);
        blockfire.a(Blocks.BLACK_CARPET, 60, 20);
        blockfire.a(Blocks.DRIED_KELP_BLOCK, 30, 60);
        blockfire.a(Blocks.BAMBOO, 60, 60);
        blockfire.a(Blocks.SCAFFOLDING, 60, 60);
        blockfire.a(Blocks.LECTERN, 30, 20);
        blockfire.a(Blocks.COMPOSTER, 5, 20);
        blockfire.a(Blocks.SWEET_BERRY_BUSH, 60, 100);
    }

    // CraftBukkit start
    private void fireExtinguished(GeneratorAccess world, BlockPosition position) {
        if (!CraftEventFactory.callBlockFadeEvent(world, position, Blocks.AIR.getBlockData()).isCancelled()) {
            world.a(position, false);
        }
    }
    // CraftBukkit end
}
