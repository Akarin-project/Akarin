package net.minecraft.server;

import java.util.Random;

public class BlockMagma extends Block {

    public BlockMagma(Block.Info block_info) {
        super(block_info);
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        if (!entity.isFireProof() && entity instanceof EntityLiving && !EnchantmentManager.i((EntityLiving) entity)) {
            org.bukkit.craftbukkit.event.CraftEventFactory.blockDamage = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()); // CraftBukkit
            entity.damageEntity(DamageSource.HOT_FLOOR, 1.0F);
            org.bukkit.craftbukkit.event.CraftEventFactory.blockDamage = null; // CraftBukkit
        }

        super.stepOn(world, blockposition, entity);
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        BlockBubbleColumn.a(world, blockposition.up(), true);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        if (enumdirection == EnumDirection.UP && iblockdata1.getBlock() == Blocks.WATER) {
            generatoraccess.getBlockTickList().a(blockposition, this, this.a((IWorldReader) generatoraccess));
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public void b(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        BlockPosition blockposition1 = blockposition.up();

        if (world.getFluid(blockposition).a(TagsFluid.WATER)) {
            world.a((EntityHuman) null, blockposition, SoundEffects.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);
            if (world instanceof WorldServer) {
                ((WorldServer) world).a(Particles.F, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.25D, (double) blockposition1.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
            }
        }

    }

    public int a(IWorldReader iworldreader) {
        return 20;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
    }

    public boolean a(IBlockData iblockdata, Entity entity) {
        return entity.isFireProof();
    }

    public boolean e(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return true;
    }
}
