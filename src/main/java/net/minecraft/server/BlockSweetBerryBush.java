package net.minecraft.server;

import java.util.Random;
// CraftBukkit start
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.event.CraftEventFactory;
// CraftBukkit end

public class BlockSweetBerryBush extends BlockPlant implements IBlockFragilePlantElement {

    public static final BlockStateInteger a = BlockProperties.aa;
    private static final VoxelShape b = Block.a(3.0D, 0.0D, 3.0D, 13.0D, 8.0D, 13.0D);
    private static final VoxelShape c = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);

    public BlockSweetBerryBush(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockSweetBerryBush.a, 0));
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return (Integer) iblockdata.get(BlockSweetBerryBush.a) == 0 ? BlockSweetBerryBush.b : ((Integer) iblockdata.get(BlockSweetBerryBush.a) < 3 ? BlockSweetBerryBush.c : super.a(iblockdata, iblockaccess, blockposition, voxelshapecollision));
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        super.tick(iblockdata, world, blockposition, random);
        int i = (Integer) iblockdata.get(BlockSweetBerryBush.a);

        if (i < 3 && world.random.nextInt(Math.max(1, (int) (100.0F / world.spigotConfig.sweetBerryModifier) * 5)) == 0 && world.getLightLevel(blockposition.up(), 0) >= 9) { // Spigot
            CraftEventFactory.handleBlockGrowEvent(world, blockposition, (IBlockData) iblockdata.set(BlockSweetBerryBush.a, i + 1), 2); // CraftBukkit
        }

    }

    @Override
    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (entity instanceof EntityLiving && entity.getEntityType() != EntityTypes.FOX) {
            entity.a(iblockdata, new Vec3D(0.800000011920929D, 0.75D, 0.800000011920929D));
            if (!world.isClientSide && (Integer) iblockdata.get(BlockSweetBerryBush.a) > 0 && (entity.H != entity.locX || entity.J != entity.locZ)) {
                double d0 = Math.abs(entity.locX - entity.H);
                double d1 = Math.abs(entity.locZ - entity.J);

                if (d0 >= 0.003000000026077032D || d1 >= 0.003000000026077032D) {
                    CraftEventFactory.blockDamage = CraftBlock.at(world, blockposition); // CraftBukkit
                    entity.damageEntity(DamageSource.SWEET_BERRY_BUSH, 1.0F);
                    CraftEventFactory.blockDamage = null; // CraftBukkit
                }
            }

        }
    }

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        int i = (Integer) iblockdata.get(BlockSweetBerryBush.a);
        boolean flag = i == 3;

        if (!flag && entityhuman.b(enumhand).getItem() == Items.BONE_MEAL) {
            return false;
        } else if (i > 1) {
            int j = 1 + world.random.nextInt(2);

            a(world, blockposition, new ItemStack(Items.SWEET_BERRIES, j + (flag ? 1 : 0)));
            world.playSound((EntityHuman) null, blockposition, SoundEffects.ITEM_SWEET_BERRIES_PICK_FROM_BUSH, SoundCategory.BLOCKS, 1.0F, 0.8F + world.random.nextFloat() * 0.4F);
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSweetBerryBush.a, 1), 2);
            return true;
        } else {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, movingobjectpositionblock);
        }
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockSweetBerryBush.a);
    }

    @Override
    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        return (Integer) iblockdata.get(BlockSweetBerryBush.a) < 3;
    }

    @Override
    public boolean a(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        return true;
    }

    @Override
    public void b(World world, Random random, BlockPosition blockposition, IBlockData iblockdata) {
        int i = Math.min(3, (Integer) iblockdata.get(BlockSweetBerryBush.a) + 1);

        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockSweetBerryBush.a, i), 2);
    }
}
