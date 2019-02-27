package net.minecraft.server;

import javax.annotation.Nullable;

public class BlockShulkerBox extends BlockTileEntity {

    public static final BlockStateEnum<EnumDirection> a = BlockDirectional.FACING;
    @Nullable
    public final EnumColor color;

    public BlockShulkerBox(@Nullable EnumColor enumcolor, Block.Info block_info) {
        super(block_info);
        this.color = enumcolor;
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockShulkerBox.a, EnumDirection.UP));
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        return new TileEntityShulkerBox(this.color);
    }

    public boolean q(IBlockData iblockdata) {
        return true;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        if (world.isClientSide) {
            return true;
        } else if (entityhuman.isSpectator()) {
            return true;
        } else {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockShulkerBox.a);
                boolean flag;

                if (((TileEntityShulkerBox) tileentity).r() == TileEntityShulkerBox.AnimationPhase.CLOSED) {
                    AxisAlignedBB axisalignedbb = VoxelShapes.b().getBoundingBox().b((double) (0.5F * (float) enumdirection1.getAdjacentX()), (double) (0.5F * (float) enumdirection1.getAdjacentY()), (double) (0.5F * (float) enumdirection1.getAdjacentZ())).a((double) enumdirection1.getAdjacentX(), (double) enumdirection1.getAdjacentY(), (double) enumdirection1.getAdjacentZ());

                    flag = world.getCubes((Entity) null, axisalignedbb.a(blockposition.shift(enumdirection1)));
                } else {
                    flag = true;
                }

                if (flag) {
                    entityhuman.a(StatisticList.OPEN_SHULKER_BOX);
                    entityhuman.openContainer((IInventory) tileentity);
                }

                return true;
            } else {
                return false;
            }
        }
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockShulkerBox.a, blockactioncontext.getClickedFace());
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockShulkerBox.a);
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        if (world.getTileEntity(blockposition) instanceof TileEntityShulkerBox) {
            TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) world.getTileEntity(blockposition);

            tileentityshulkerbox.a(entityhuman.abilities.canInstantlyBuild);
            tileentityshulkerbox.d(entityhuman);
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    // CraftBukkit start - override to prevent duplication when dropping
    @Override
    public void dropNaturally(IBlockData iblockdata, World world, BlockPosition blockposition, float f, int i) {
        if (true) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

                if (!tileentityshulkerbox.s() && tileentityshulkerbox.G()) {
                    ItemStack itemstack = new ItemStack(this);

                    itemstack.getOrCreateTag().set("BlockEntityTag", ((TileEntityShulkerBox) tileentity).g(new NBTTagCompound()));
                    if (tileentityshulkerbox.hasCustomName()) {
                        itemstack.a(tileentityshulkerbox.getCustomName());
                        tileentityshulkerbox.setCustomName((IChatBaseComponent) null);
                    }

                    a(world, blockposition, itemstack);
                }
            }
            world.updateAdjacentComparators(blockposition, iblockdata.getBlock());
        }
    }
    // CraftBukkit end

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        if (itemstack.hasName()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityShulkerBox) {
                ((TileEntityShulkerBox) tileentity).setCustomName(itemstack.getName());
            }
        }

    }

    public void remove(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1, boolean flag) {
        if (iblockdata.getBlock() != iblockdata1.getBlock()) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (false && tileentity instanceof TileEntityShulkerBox) { // CraftBukkit - moved up
                TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) tileentity;

                if (!tileentityshulkerbox.s() && tileentityshulkerbox.G()) {
                    ItemStack itemstack = new ItemStack(this);

                    itemstack.getOrCreateTag().set("BlockEntityTag", ((TileEntityShulkerBox) tileentity).g(new NBTTagCompound()));
                    if (tileentityshulkerbox.hasCustomName()) {
                        itemstack.a(tileentityshulkerbox.getCustomName());
                        tileentityshulkerbox.setCustomName((IChatBaseComponent) null);
                    }

                    a(world, blockposition, itemstack);
                }

            }
            world.updateAdjacentComparators(blockposition, iblockdata.getBlock()); // CraftBukkit - moved down

            super.remove(iblockdata, world, blockposition, iblockdata1, flag);
        }
    }

    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        TileEntity tileentity = iblockaccess.getTileEntity(blockposition);

        return tileentity instanceof TileEntityShulkerBox ? VoxelShapes.a(((TileEntityShulkerBox) tileentity).a(iblockdata)) : VoxelShapes.b();
    }

    public boolean f(IBlockData iblockdata) {
        return false;
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return Container.b((IInventory) world.getTileEntity(blockposition));
    }

    public ItemStack a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata) {
        ItemStack itemstack = super.a(iblockaccess, blockposition, iblockdata);
        TileEntityShulkerBox tileentityshulkerbox = (TileEntityShulkerBox) iblockaccess.getTileEntity(blockposition);
        NBTTagCompound nbttagcompound = tileentityshulkerbox.g(new NBTTagCompound());

        if (!nbttagcompound.isEmpty()) {
            itemstack.a("BlockEntityTag", (NBTBase) nbttagcompound);
        }

        return itemstack;
    }

    public static Block a(EnumColor enumcolor) {
        if (enumcolor == null) {
            return Blocks.SHULKER_BOX;
        } else {
            switch (enumcolor) {
            case WHITE:
                return Blocks.WHITE_SHULKER_BOX;
            case ORANGE:
                return Blocks.ORANGE_SHULKER_BOX;
            case MAGENTA:
                return Blocks.MAGENTA_SHULKER_BOX;
            case LIGHT_BLUE:
                return Blocks.LIGHT_BLUE_SHULKER_BOX;
            case YELLOW:
                return Blocks.YELLOW_SHULKER_BOX;
            case LIME:
                return Blocks.LIME_SHULKER_BOX;
            case PINK:
                return Blocks.PINK_SHULKER_BOX;
            case GRAY:
                return Blocks.GRAY_SHULKER_BOX;
            case LIGHT_GRAY:
                return Blocks.LIGHT_GRAY_SHULKER_BOX;
            case CYAN:
                return Blocks.CYAN_SHULKER_BOX;
            case PURPLE:
            default:
                return Blocks.PURPLE_SHULKER_BOX;
            case BLUE:
                return Blocks.BLUE_SHULKER_BOX;
            case BROWN:
                return Blocks.BROWN_SHULKER_BOX;
            case GREEN:
                return Blocks.GREEN_SHULKER_BOX;
            case RED:
                return Blocks.RED_SHULKER_BOX;
            case BLACK:
                return Blocks.BLACK_SHULKER_BOX;
            }
        }
    }

    public static ItemStack b(EnumColor enumcolor) {
        return new ItemStack(a(enumcolor));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockShulkerBox.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockShulkerBox.a)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockShulkerBox.a)));
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        EnumDirection enumdirection1 = (EnumDirection) iblockdata.get(BlockShulkerBox.a);
        TileEntityShulkerBox.AnimationPhase tileentityshulkerbox_animationphase = ((TileEntityShulkerBox) iblockaccess.getTileEntity(blockposition)).r();

        return tileentityshulkerbox_animationphase != TileEntityShulkerBox.AnimationPhase.CLOSED && (tileentityshulkerbox_animationphase != TileEntityShulkerBox.AnimationPhase.OPENED || enumdirection1 != enumdirection.opposite() && enumdirection1 != enumdirection) ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID;
    }
}
