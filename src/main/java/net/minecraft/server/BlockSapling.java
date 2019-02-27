package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import java.util.List;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.event.world.StructureGrowEvent;
// CraftBukkit end

public class BlockSapling extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger STAGE = BlockProperties.am;
    protected static final VoxelShape b = Block.a(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);
    private final WorldGenTreeProvider c;
    public static TreeType treeType; // CraftBukkit

    protected BlockSapling(WorldGenTreeProvider worldgentreeprovider, Block.Info block_info) {
        super(block_info);
        this.c = worldgentreeprovider;
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSapling.STAGE, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockSapling.b;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.a(iblockdata, world, blockposition, random);
        if (world.getLightLevel(blockposition.up()) >= 9 && random.nextInt(7) == 0) {
            // CraftBukkit start
            world.captureTreeGeneration = true;
            // CraftBukkit end
            this.grow(world, blockposition, iblockdata, random);
            // CraftBukkit start
            world.captureTreeGeneration = false;
            if (world.capturedBlockStates.size() > 0) {
                TreeType treeType = BlockSapling.treeType;
                BlockSapling.treeType = null;
                Location location = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
                List<BlockState> blocks = (List<BlockState>) world.capturedBlockStates.clone();
                world.capturedBlockStates.clear();
                StructureGrowEvent event = null;
                if (treeType != null) {
                    event = new StructureGrowEvent(location, treeType, false, null, blocks);
                    org.bukkit.Bukkit.getPluginManager().callEvent(event);
                }
                if (event == null || !event.isCancelled()) {
                    for (BlockState blockstate : blocks) {
                        blockstate.update(true);
                    }
                }
            }
            // CraftBukkit end
        }

    }

    public void grow(GeneratorAccess generatoraccess, BlockPosition blockposition, IBlockData iblockdata, Random random) {
        if ((Integer) iblockdata.get(BlockSapling.STAGE) == 0) {
            generatoraccess.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockSapling.STAGE), 4);
        } else {
            this.c.a(generatoraccess, blockposition, iblockdata, random);
        }

    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return true;
    }

    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return (double) world.random.nextFloat() < 0.45D;
    }

    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        this.grow(world, blockposition, iblockdata, random);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSapling.STAGE);
    }
}
