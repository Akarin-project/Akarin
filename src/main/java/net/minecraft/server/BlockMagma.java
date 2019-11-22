package net.minecraft.server;

import java.util.Random;

public class BlockMagma extends Block {

    public BlockMagma(Block.Info block_info) {
        super(block_info);
    }

    @Override
    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof() && entity instanceof EntityLiving && !EnchantmentManager.i((EntityLiving) entity)) {
            org.bukkit.craftbukkit.event.CraftEventFactory.blockDamage = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()); // CraftBukkit
            entity.damageEntity(DamageSource.HOT_FLOOR, 1.0F);
            org.bukkit.craftbukkit.event.CraftEventFactory.blockDamage = null; // CraftBukkit
        }

        super.stepOn(world, blockposition, entity);
    }

    @Override
    public void tick(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        BlockBubbleColumn.a(world, blockposition.up(), true);
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.UP && iblockdata1.getBlock() == Blocks.WATER) {
            generatoraccess.getBlockTickList().a(blockposition, this, this.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void c(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.up();

        if (world.getFluid(blockposition).a(TagsFluid.WATER)) {
            world.playSound((EntityHuman) null, blockposition, SoundEffects.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            if (world instanceof WorldServer) {
                ((WorldServer) world).a(Particles.LARGE_SMOKE, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.25D, (double) blockposition1.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
            }
        }

    }

    @Override
    public int a(IWorldReader iworldreader) {
        return 20;
    }

    @Override
    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return entitytypes.c();
    }

    @Override
    public boolean g(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }
}
