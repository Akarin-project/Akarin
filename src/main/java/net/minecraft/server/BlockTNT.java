package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockTNT extends Block {

    public static final BlockStateBoolean a = BlockProperties.x;

    public BlockTNT(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) this.getBlockData().set(BlockTNT.a, false));
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            if (world.isBlockIndirectlyPowered(blockposition)) {
                this.a(world, blockposition);
                world.setAir(blockposition);
            }

        }
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (world.isBlockIndirectlyPowered(blockposition)) {
            this.a(world, blockposition);
            world.setAir(blockposition);
        }

    }

    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (!(Boolean) iblockdata.get(BlockTNT.a)) {
            super.dropNaturally(iblockdata, world, blockposition, f, i);
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (!world.e() && !entityhuman.u() && (Boolean) iblockdata.get(BlockTNT.a)) {
            this.a(world, blockposition);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    public void wasExploded(World world, BlockPosition blockposition, Explosion explosion) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) blockposition.getX() + 0.5F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.5F), explosion.getSource());

            entitytntprimed.setFuseTicks((short) (world.random.nextInt(entitytntprimed.getFuseTicks() / 4) + entitytntprimed.getFuseTicks() / 8));
            world.addEntity(entitytntprimed);
        }
    }

    public void a(World world, BlockPosition blockposition) {
        this.a(world, blockposition, (EntityLiving) null);
    }

    private void a(World world, BlockPosition blockposition, @Nullable EntityLiving entityliving) {
        if (!world.isClientSide) {
            EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) ((float) blockposition.getX() + 0.5F), (double) blockposition.getY(), (double) ((float) blockposition.getZ() + 0.5F), entityliving);

            world.addEntity(entitytntprimed);
            world.a((EntityHuman) null, entitytntprimed.locX, entitytntprimed.locY, entitytntprimed.locZ, SoundEffects.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
        }
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);
        Item item = itemstack.getItem();

        if (item != Items.FLINT_AND_STEEL && item != Items.FIRE_CHARGE) {
            return super.interact(iblockdata, world, blockposition, entityhuman, enumhand, enumdirection, f, f1, f2);
        } else {
            this.a(world, blockposition, (EntityLiving) entityhuman);
            world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 11);
            if (item == Items.FLINT_AND_STEEL) {
                itemstack.damage(1, entityhuman);
            } else {
                itemstack.subtract(1);
            }

            return true;
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        if (!world.isClientSide && entity instanceof EntityArrow) {
            EntityArrow entityarrow = (EntityArrow) entity;
            Entity entity1 = entityarrow.getShooter();

            if (entityarrow.isBurning()) {
                this.a(world, blockposition, entity1 instanceof EntityLiving ? (EntityLiving) entity1 : null);
                world.setAir(blockposition);
            }
        }

    }

    public boolean a(Explosion explosion) {
        return false;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTNT.a);
    }
}
