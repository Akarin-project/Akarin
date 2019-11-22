package net.minecraft.server;

import java.util.Optional;
import javax.annotation.Nullable;

public class BlockBed extends BlockFacingHorizontal implements ITileEntity {

    public static final BlockStateEnum<BlockPropertyBedPart> PART = BlockProperties.aw;
    public static final BlockStateBoolean OCCUPIED = BlockProperties.t;
    protected static final VoxelShape c = Block.a(0.0D, 3.0D, 0.0D, 16.0D, 9.0D, 16.0D);
    protected static final VoxelShape d = Block.a(0.0D, 0.0D, 0.0D, 3.0D, 3.0D, 3.0D);
    protected static final VoxelShape e = Block.a(0.0D, 0.0D, 13.0D, 3.0D, 3.0D, 16.0D);
    protected static final VoxelShape f = Block.a(13.0D, 0.0D, 0.0D, 16.0D, 3.0D, 3.0D);
    protected static final VoxelShape g = Block.a(13.0D, 0.0D, 13.0D, 16.0D, 3.0D, 16.0D);
    protected static final VoxelShape h = VoxelShapes.a(BlockBed.c, BlockBed.d, BlockBed.f);
    protected static final VoxelShape i = VoxelShapes.a(BlockBed.c, BlockBed.e, BlockBed.g);
    protected static final VoxelShape j = VoxelShapes.a(BlockBed.c, BlockBed.d, BlockBed.e);
    protected static final VoxelShape k = VoxelShapes.a(BlockBed.c, BlockBed.f, BlockBed.g);
    private final EnumColor color;

    public BlockBed(EnumColor enumcolor, Block.Info block_info) {
        super(block_info);
        this.color = enumcolor;
        this.o((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockBed.PART, BlockPropertyBedPart.FOOT)).set(BlockBed.OCCUPIED, false));
    }

    @Override
    public MaterialMapColor e(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.FOOT ? this.color.e() : MaterialMapColor.e;
    }

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return true;
        } else {
            if (iblockdata.get(BlockBed.PART) != BlockPropertyBedPart.HEAD) {
                blockposition = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));
                iblockdata = world.getType(blockposition);
                if (iblockdata.getBlock() != this) {
                    return true;
                }
            }

            // CraftBukkit - moved world and biome check into EntityHuman
            if (true || world.worldProvider.canRespawn() && world.getBiome(blockposition) != Biomes.NETHER) {
                if ((Boolean) iblockdata.get(BlockBed.OCCUPIED)) {
                    entityhuman.a((IChatBaseComponent) (new ChatMessage("block.minecraft.bed.occupied", new Object[0])), true);
                    return true;
                } else {
                    // CraftBukkit start
                    IBlockData finaliblockdata = iblockdata;
                    BlockPosition finalblockposition = blockposition;
                    // CraftBukkit end
                    entityhuman.sleep(blockposition).ifLeft((entityhuman_enumbedresult) -> {
                        // CraftBukkit start - handling bed explosion from below here
                        if (entityhuman_enumbedresult == EntityHuman.EnumBedResult.NOT_POSSIBLE_HERE) {
                            this.explodeBed(finaliblockdata, world, finalblockposition);
                        } else
                        // CraftBukkit end
                        if (entityhuman_enumbedresult != null) {
                            entityhuman.a(entityhuman_enumbedresult.a(), true);
                        }

                    });
                    return true;
                }
                // CraftBukkit start - moved bed explosion into separate method
            } else {
                return true;
            }
        }
    }

    private boolean explodeBed(IBlockData iblockdata, World world, BlockPosition blockposition) {
                world.a(blockposition, false);
                BlockPosition blockposition1 = blockposition.shift(((EnumDirection) iblockdata.get(BlockBed.FACING)).opposite());

                if (world.getType(blockposition1).getBlock() == this) {
                    world.a(blockposition1, false);
                }

                world.createExplosion((Entity) null, DamageSource.a(), (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D, 5.0F, true, Explosion.Effect.DESTROY);
                return true;
                // CraftBukkit end
    }

    @Override
    public void fallOn(World world, BlockPosition blockposition, Entity entity, float f) {
        super.fallOn(world, blockposition, entity, f * 0.5F);
    }

    @Override
    public void a(IBlockAccess iblockaccess, Entity entity) {
        if (entity.isSneaking()) {
            super.a(iblockaccess, entity);
        } else {
            Vec3D vec3d = entity.getMot();

            if (vec3d.y < 0.0D) {
                double d0 = entity instanceof EntityLiving ? 1.0D : 0.8D;

                entity.setMot(vec3d.x, -vec3d.y * 0.6600000262260437D * d0, vec3d.z);
            }
        }

    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == a((BlockPropertyBedPart) iblockdata.get(BlockBed.PART), (EnumDirection) iblockdata.get(BlockBed.FACING)) ? (iblockdata1.getBlock() == this && iblockdata1.get(BlockBed.PART) != iblockdata.get(BlockBed.PART) ? (IBlockData) iblockdata.set(BlockBed.OCCUPIED, iblockdata1.get(BlockBed.OCCUPIED)) : Blocks.AIR.getBlockData()) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    private static EnumDirection a(BlockPropertyBedPart blockpropertybedpart, EnumDirection enumdirection) {
        return blockpropertybedpart == BlockPropertyBedPart.FOOT ? enumdirection : enumdirection.opposite();
    }

    @Override
    public void a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack) {
        super.a(world, entityhuman, blockposition, Blocks.AIR.getBlockData(), tileentity, itemstack);
    }

    @Override
    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, EntityHuman entityhuman) {
        BlockPropertyBedPart blockpropertybedpart = (BlockPropertyBedPart) iblockdata.get(BlockBed.PART);
        BlockPosition blockposition1 = blockposition.shift(a(blockpropertybedpart, (EnumDirection) iblockdata.get(BlockBed.FACING)));
        IBlockData iblockdata1 = world.getType(blockposition1);

        if (iblockdata1.getBlock() == this && iblockdata1.get(BlockBed.PART) != blockpropertybedpart) {
            world.setTypeAndData(blockposition1, Blocks.AIR.getBlockData(), 35);
            world.a(entityhuman, 2001, blockposition1, Block.getCombinedId(iblockdata1));
            if (!world.isClientSide && !entityhuman.isCreative()) {
                ItemStack itemstack = entityhuman.getItemInMainHand();

                dropItems(iblockdata, world, blockposition, (TileEntity) null, entityhuman, itemstack);
                dropItems(iblockdata1, world, blockposition1, (TileEntity) null, entityhuman, itemstack);
            }

            entityhuman.b(StatisticList.BLOCK_MINED.b(this));
        }

        super.a(world, blockposition, iblockdata, entityhuman);
    }

    @Nullable
    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        EnumDirection enumdirection = blockactioncontext.f();
        BlockPosition blockposition = blockactioncontext.getClickPosition();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);

        return blockactioncontext.getWorld().getType(blockposition1).a(blockactioncontext) ? (IBlockData) this.getBlockData().set(BlockBed.FACING, enumdirection) : null;
    }

    @Override
    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        EnumDirection enumdirection = (EnumDirection) iblockdata.get(BlockBed.FACING);
        EnumDirection enumdirection1 = iblockdata.get(BlockBed.PART) == BlockPropertyBedPart.HEAD ? enumdirection : enumdirection.opposite();

        switch (enumdirection1) {
            case NORTH:
                return BlockBed.h;
            case SOUTH:
                return BlockBed.i;
            case WEST:
                return BlockBed.j;
            default:
                return BlockBed.k;
        }
    }

    public static Optional<Vec3D> a(EntityTypes<?> entitytypes, IWorldReader iworldreader, BlockPosition blockposition, int i) {
        EnumDirection enumdirection = (EnumDirection) iworldreader.getType(blockposition).get(BlockBed.FACING);
    // Paper start - configurable bed search radius
        return findSafePosition(entitytypes, (World) iworldreader, enumdirection, blockposition);
    }
        /*
        int j = blockposition.getX();
        int k = blockposition.getY();
        int l = blockposition.getZ();

        for (int i1 = 0; i1 <= 1; ++i1) {
            int j1 = j - enumdirection.getAdjacentX() * i1 - 1;
            int k1 = l - enumdirection.getAdjacentZ() * i1 - 1;
            int l1 = j1 + 2;
            int i2 = k1 + 2;

            for (int j2 = j1; j2 <= l1; ++j2) {
                for (int k2 = k1; k2 <= i2; ++k2) {
                    BlockPosition blockposition1 = new BlockPosition(j2, k, k2);
                    Optional<Vec3D> optional = a(entitytypes, iworldreader, blockposition1);

                    if (optional.isPresent()) {
                        if (i <= 0) {
                            return optional;
                        }

                        --i;
                    }
                }
            }
        }

        return Optional.empty();
    }
    */

    private static Optional<Vec3D> findSafePosition(EntityTypes<?> entitytypes, World world, EnumDirection updirection, BlockPosition blockposition){
        int radius = world.paperConfig.bedSearchRadius;
        double angle = Math.PI / 2;
        int tmpX = (int)(updirection.getAdjacentX() * Math.cos(angle) - updirection.getAdjacentZ() * Math.sin(angle));
        int tmpZ = (int)(updirection.getAdjacentX() * Math.sin(angle) + updirection.getAdjacentZ() * Math.cos(angle));

        EnumDirection rightDirection = EnumDirection.a(tmpX, 0, tmpZ);
        EnumDirection downDirection = updirection.opposite();
        EnumDirection leftDirection = rightDirection.opposite();

        EnumDirection[] corePositionOutDirection = new EnumDirection[6];
        corePositionOutDirection[0] = updirection;
        corePositionOutDirection[1] = leftDirection;
        corePositionOutDirection[2] = leftDirection;
        corePositionOutDirection[3] = downDirection;
        corePositionOutDirection[4] = rightDirection;
        corePositionOutDirection[5] = rightDirection;

        BlockPosition[] corePosition = new BlockPosition[6];
        corePosition[0] = blockposition.add(updirection.getAdjacentX(), 0, updirection.getAdjacentZ());
        corePosition[1] = blockposition.add(leftDirection.getAdjacentX(), 0, leftDirection.getAdjacentZ());
        corePosition[2] = corePosition[1].add(downDirection.getAdjacentX(), 0, downDirection.getAdjacentZ());
        corePosition[3] = blockposition.add(2 * downDirection.getAdjacentX(), 0, 2 * downDirection.getAdjacentZ());
        corePosition[5] = blockposition.add(rightDirection.getAdjacentX(), 0, rightDirection.getAdjacentZ());
        corePosition[4] = corePosition[5].add(downDirection.getAdjacentX(), 0, downDirection.getAdjacentZ());

        BlockPosition[] tmpPosition = new BlockPosition[8];
        EnumDirection[] tmpPositionDirection = new EnumDirection[8];
        tmpPositionDirection[0] = rightDirection;
        tmpPositionDirection[1] = leftDirection;
        tmpPositionDirection[2] = updirection;
        tmpPositionDirection[3] = downDirection;
        tmpPositionDirection[4] = leftDirection;
        tmpPositionDirection[5] = rightDirection;
        tmpPositionDirection[6] = downDirection;
        tmpPositionDirection[7] = updirection;

        BlockPosition pos;
        Optional<Vec3D> vector;
        for (int r = 1; r <= radius; r++) {
            int h = 0;
            while (h <= 1) {
                int numIterated = 0;
                for (int index = (int)(Math.random() * corePosition.length); numIterated < corePosition.length; index = (index+1) % corePosition.length) {
                    numIterated++;

                    pos = corePosition[index].add(0, h, 0);
                    vector = isSafeRespawn(entitytypes, world, pos);
                    if (vector.isPresent()) {
                        return vector;
                    }
                }
                tmpPosition[0] = corePosition[0].add(0, h, 0);
                tmpPosition[1] = corePosition[0].add(0, h, 0);
                tmpPosition[2] = corePosition[1].add(0, h, 0);
                tmpPosition[3] = corePosition[2].add(0, h, 0);
                tmpPosition[4] = corePosition[3].add(0, h, 0);
                tmpPosition[5] = corePosition[3].add(0, h, 0);
                tmpPosition[6] = corePosition[4].add(0, h, 0);
                tmpPosition[7] = corePosition[5].add(0, h, 0);
                for (int rr = 1; rr <= r; rr++){
                    numIterated = 0;
                    for (int index = (int)(Math.random() * tmpPosition.length); numIterated < tmpPosition.length; index = (index+1) % tmpPosition.length) {
                        numIterated++;
                        tmpPosition[index] = tmpPosition[index].add(tmpPositionDirection[index].getAdjacentX(), 0, tmpPositionDirection[index].getAdjacentZ());
                        pos = tmpPosition[index];

                        vector = isSafeRespawn(entitytypes, world, pos);
                        if (vector.isPresent()) {
                            return vector;
                        }
                    }
                }
                switch (h) {
                    case -1:
                        h = 1;
                        break;
                    case 0:
                        h = -1;
                        break;
                    case 1:
                        h = Integer.MAX_VALUE;
                        break;
                }
            }
            for (int index = 0; index < corePosition.length; index++) {
                EnumDirection tmp = corePositionOutDirection[index];
                corePosition[index] = corePosition[index].add(tmp.getAdjacentX(), 0, tmp.getAdjacentZ());
            }
        }
        return Optional.empty();
    }
    // Paper end

    protected static Optional<Vec3D> isSafeRespawn(EntityTypes<?> entityTypes, IWorldReader iworldreader, BlockPosition blockPosition) { return a(entityTypes, iworldreader, blockPosition); } // Paper -- obfhelper
    protected static Optional<Vec3D> a(EntityTypes<?> entitytypes, IWorldReader iworldreader, BlockPosition blockposition) {
        VoxelShape voxelshape = iworldreader.getType(blockposition).getCollisionShape(iworldreader, blockposition);

        if (voxelshape.c(EnumDirection.EnumAxis.Y) > 0.4375D) {
            return Optional.empty();
        } else {
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);

            while (blockposition_mutableblockposition.getY() >= 0 && blockposition.getY() - blockposition_mutableblockposition.getY() <= 2 && iworldreader.getType(blockposition_mutableblockposition).getCollisionShape(iworldreader, blockposition_mutableblockposition).isEmpty()) {
                blockposition_mutableblockposition.c(EnumDirection.DOWN);
            }

            VoxelShape voxelshape1 = iworldreader.getType(blockposition_mutableblockposition).getCollisionShape(iworldreader, blockposition_mutableblockposition);

            if (voxelshape1.isEmpty()) {
                return Optional.empty();
            } else {
                double d0 = (double) blockposition_mutableblockposition.getY() + voxelshape1.c(EnumDirection.EnumAxis.Y) + 2.0E-7D;

                if ((double) blockposition.getY() - d0 > 2.0D) {
                    return Optional.empty();
                } else {
                    float f = entitytypes.i() / 2.0F;
                    Vec3D vec3d = new Vec3D((double) blockposition_mutableblockposition.getX() + 0.5D, d0, (double) blockposition_mutableblockposition.getZ() + 0.5D);

                    return iworldreader.c(new AxisAlignedBB(vec3d.x - (double) f, vec3d.y, vec3d.z - (double) f, vec3d.x + (double) f, vec3d.y + (double) entitytypes.j(), vec3d.z + (double) f)) ? Optional.of(vec3d) : Optional.empty();
                }
            }
        }
    }

    @Override
    public EnumPistonReaction getPushReaction(IBlockData iblockdata) {
        return EnumPistonReaction.DESTROY;
    }

    @Override
    public TextureType c() {
        return TextureType.CUTOUT;
    }

    @Override
    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockBed.FACING, BlockBed.PART, BlockBed.OCCUPIED);
    }

    @Override
    public TileEntity createTile(IBlockAccess iblockaccess) {
        return new TileEntityBed(this.color);
    }

    @Override
    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, @Nullable EntityLiving entityliving, ItemStack itemstack) {
        super.postPlace(world, blockposition, iblockdata, entityliving, itemstack);
        if (!world.isClientSide) {
            BlockPosition blockposition1 = blockposition.shift((EnumDirection) iblockdata.get(BlockBed.FACING));

            world.setTypeAndData(blockposition1, (IBlockData) iblockdata.set(BlockBed.PART, BlockPropertyBedPart.HEAD), 3);
            world.update(blockposition, Blocks.AIR);
            iblockdata.a(world, blockposition, 3);
        }

    }

    @Override
    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
