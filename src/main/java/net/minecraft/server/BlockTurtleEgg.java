package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;
import org.bukkit.craftbukkit.block.CraftBlock;

// CraftBukkit start
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
// CraftBukkit end

public class BlockTurtleEgg extends Block {

    private static final VoxelShape c = Block.a(3.0D, 0.0D, 3.0D, 12.0D, 7.0D, 12.0D);
    private static final VoxelShape o = Block.a(1.0D, 0.0D, 1.0D, 15.0D, 7.0D, 15.0D);
    public static final BlockStateInteger a = BlockProperties.ad;
    public static final BlockStateInteger b = BlockProperties.ac;

    public BlockTurtleEgg(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockTurtleEgg.a, 0)).set(BlockTurtleEgg.b, 1));
    }

    public void stepOn(World world, BlockPosition blockposition, Entity entity) {
        this.a(world, blockposition, entity, 100);
        super.stepOn(world, blockposition, entity);
    }

    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        if (!(entity instanceof EntityZombie)) {
            this.a(world, blockposition, entity, 3);
        }

        super.fallOn(world, blockposition, entity, f);
    }

    private void a(World world, BlockPosition blockposition, Entity entity, int i) {
        if (!this.a(world, entity)) {
            super.stepOn(world, blockposition, entity);
        } else {
            if (!world.isClientSide && world.random.nextInt(i) == 0) {
                // CraftBukkit start - Step on eggs
                org.bukkit.event.Cancellable cancellable;
                if (entity instanceof EntityHuman) {
                    cancellable = CraftEventFactory.callPlayerInteractEvent((EntityHuman) entity, org.bukkit.event.block.Action.PHYSICAL, blockposition, null, null, null);
                } else {
                    cancellable = new EntityInteractEvent(entity.getBukkitEntity(), CraftBlock.at(world, blockposition));
                    world.getServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
                }

                if (cancellable.isCancelled()) {
                    return;
                }
                // CraftBukkit end
                this.a(world, blockposition, world.getType(blockposition));
            }

        }
    }

    private void a(World world, BlockPosition blockposition, IBlockData iblockdata) {
        world.a((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        int i = (Integer) iblockdata.get(BlockTurtleEgg.b);

        if (i <= 1) {
            world.setAir(blockposition, false);
        } else {
            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTurtleEgg.b, i - 1), 2);
            world.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
        }

    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (this.a(world) && this.a((IBlockAccess) world, blockposition)) {
            int i = (Integer) iblockdata.get(BlockTurtleEgg.a);

            if (i < 2) {
                // CraftBukkit start - Call BlockGrowEvent
                if (!CraftEventFactory.handleBlockGrowEvent(world, blockposition, iblockdata.set(BlockTurtleEgg.a, i + 1), 2)) {
                    return;
                }
                // CraftBukkit end
                world.a((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_EGG_CRACK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                // world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockTurtleEgg.a, i + 1), 2); // CraftBukkit - handled above
            } else {
                // CraftBukkit start - Call BlockFadeEvent
                if (CraftEventFactory.callBlockFadeEvent(world, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                    return;
                }
                // CraftBukkit end
                world.a((EntityHuman) null, blockposition, SoundEffects.ENTITY_TURTLE_EGG_HATCH, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.setAir(blockposition);
                if (!world.isClientSide) {
                    for (int j = 0; j < (Integer) iblockdata.get(BlockTurtleEgg.b); ++j) {
                        world.triggerEffect(2001, blockposition, Block.getCombinedId(iblockdata));
                        EntityTurtle entityturtle = new EntityTurtle(world);

                        entityturtle.setAgeRaw(-24000);
                        entityturtle.g(blockposition);
                        entityturtle.setPositionRotation((double) blockposition.getX() + 0.3D + (double) j * 0.2D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.3D, 0.0F, 0.0F);
                        world.addEntity(entityturtle, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.EGG); // CraftBukkit
                    }
                }
            }
        }

    }

    private boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockaccess.getType(blockposition.down()).getBlock() == Blocks.SAND;
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (this.a((IBlockAccess) world, blockposition) && !world.isClientSide) {
            world.triggerEffect(2005, blockposition, 0);
        }

    }

    private boolean a(World world) {
        float f = world.k(1.0F);

        return (double) f < 0.69D && (double) f > 0.65D ? true : world.random.nextInt(500) == 0;
    }

    protected boolean X_() {
        return true;
    }

    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, iblockdata, tileentity, itemstack);
        this.a(world, blockposition, iblockdata);
    }

    public IMaterial getDropType(IBlockData iblockdata, World world, BlockPosition blockposition, int i) {
        return Items.AIR;
    }

    public boolean a(IBlockData iblockdata, BlockActionContext blockactioncontext) {
        return blockactioncontext.getItemStack().getItem() == this.getItem() && (Integer) iblockdata.get(BlockTurtleEgg.b) < 4 ? true : super.a(iblockdata, blockactioncontext);
    }

    @Nullable
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition());

        return iblockdata.getBlock() == this ? (IBlockData) iblockdata.set(BlockTurtleEgg.b, Math.min(4, (Integer) iblockdata.get(BlockTurtleEgg.b) + 1)) : super.getPlacedState(blockactioncontext);
    }

    public TextureType c() {
        return TextureType.CUTOUT;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockTurtleEgg.b) > 1 ? BlockTurtleEgg.o : BlockTurtleEgg.c;
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return EnumBlockFaceShape.UNDEFINED;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockTurtleEgg.a, BlockTurtleEgg.b);
    }

    private boolean a(World world, Entity entity) {
        return entity instanceof EntityTurtle ? false : (entity instanceof EntityLiving && !(entity instanceof EntityHuman) ? world.getGameRules().getBoolean("mobGriefing") : true);
    }
}
