package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;

import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason; // CraftBukkit

public class BlockMonsterEggs extends Block {

    private final Block a;
    private static final Map<Block, Block> b = Maps.newIdentityHashMap();

    public BlockMonsterEggs(Block block, Block.Info block_info) {
        super(block_info);
        this.a = block;
        BlockMonsterEggs.b.put(block, this);
    }

    public Block d() {
        return this.a;
    }

    public static boolean j(IBlockData iblockdata) {
        return BlockMonsterEggs.b.containsKey(iblockdata.getBlock());
    }

    @Override
    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, ItemStack itemstack) {
        super.dropNaturally(iblockdata, world, blockposition, itemstack);
        if (!world.isClientSide && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) == 0) {
            EntitySilverfish entitysilverfish = (EntitySilverfish) EntityTypes.SILVERFISH.a(world);

            entitysilverfish.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, 0.0F, 0.0F);
            world.addEntity(entitysilverfish, SpawnReason.SILVERFISH_BLOCK); // CraftBukkit - add SpawnReason
            entitysilverfish.doSpawnEffect();
        }

    }

    public static IBlockData e(Block block) {
        return ((Block) BlockMonsterEggs.b.get(block)).getBlockData();
    }
}
