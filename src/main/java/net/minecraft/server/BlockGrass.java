package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockFadeEvent;
// CraftBukkit end

public class BlockGrass extends Block {

    protected BlockGrass(int i) {
        super(i, Material.GRASS);
        this.textureId = 3;
        this.a(true);
    }

    public int a(int i, int j) {
        return i == 1 ? 0 : (i == 0 ? 2 : 3);
    }

    public void a(World world, int i, int j, int k, Random random) {
        if (!world.isStatic) {
            if (world.getLightLevel(i, j + 1, k) < 4 && Block.lightBlock[world.getTypeId(i, j + 1, k)] > 2) {
                // CraftBukkit start - reuse getLightLevel
                org.bukkit.World bworld = world.getWorld();
                org.bukkit.block.BlockState blockState = bworld.getBlockAt(i, j, k).getState();
                blockState.setTypeId(Block.DIRT.id);

                BlockFadeEvent event = new BlockFadeEvent(blockState.getBlock(), blockState);
                world.getServer().getPluginManager().callEvent(event);

                if (!event.isCancelled()) {
                    blockState.update(true);
                }
                // CraftBukkit end
            } else if (world.getLightLevel(i, j + 1, k) >= 9) {
                for (int l = 0; l < 4; ++l) {
                    int i1 = i + random.nextInt(3) - 1;
                    int j1 = j + random.nextInt(5) - 3;
                    int k1 = k + random.nextInt(3) - 1;
                    int l1 = world.getTypeId(i1, j1 + 1, k1);

                    if (world.getTypeId(i1, j1, k1) == Block.DIRT.id && world.getLightLevel(i1, j1 + 1, k1) >= 4 && Block.lightBlock[l1] <= 2) {
                        // CraftBukkit start
                        org.bukkit.World bworld = world.getWorld();
                        org.bukkit.block.BlockState blockState = bworld.getBlockAt(i1, j1, k1).getState();
                        blockState.setTypeId(this.id);

                        BlockSpreadEvent event = new BlockSpreadEvent(blockState.getBlock(), bworld.getBlockAt(i, j, k), blockState);
                        world.getServer().getPluginManager().callEvent(event);

                        if (!event.isCancelled()) {
                            blockState.update(true);
                        }
                        // CraftBukkit end
                    }
                }
            }
        }
    }

    public int getDropType(int i, Random random, int j) {
        return Block.DIRT.getDropType(0, random, j);
    }
}
