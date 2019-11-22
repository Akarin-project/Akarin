package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;

public class PathfinderNormal extends PathfinderAbstract {

    protected float j;

    public PathfinderNormal() {}

    @Override
    public void a(IWorldReader iworldreader, EntityInsentient entityinsentient) {
        super.a(iworldreader, entityinsentient);
        this.j = entityinsentient.a(PathType.WATER);
    }

    @Override
    public void a() {
        this.b.a(PathType.WATER, this.j);
        super.a();
    }

    @Override
    public PathPoint b() {
        int i;
        BlockPosition blockposition;

        if (this.e() && this.b.isInWater()) {
            i = MathHelper.floor(this.b.getBoundingBox().minY);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(this.b.locX, (double) i, this.b.locZ);

            for (IBlockData iblockdata = this.a.getType(blockposition_mutableblockposition); iblockdata.getBlock() == Blocks.WATER || iblockdata.p() == FluidTypes.WATER.a(false); iblockdata = this.a.getType(blockposition_mutableblockposition)) {
                ++i;
                blockposition_mutableblockposition.c(this.b.locX, (double) i, this.b.locZ);
            }

            --i;
        } else if (this.b.onGround) {
            i = MathHelper.floor(this.b.getBoundingBox().minY + 0.5D);
        } else {
            for (blockposition = new BlockPosition(this.b); (this.a.getType(blockposition).isAir() || this.a.getType(blockposition).a((IBlockAccess) this.a, blockposition, PathMode.LAND)) && blockposition.getY() > 0; blockposition = blockposition.down()) {
                ;
            }

            i = blockposition.up().getY();
        }

        blockposition = new BlockPosition(this.b);
        PathType pathtype = this.a(this.b, blockposition.getX(), i, blockposition.getZ());

        if (this.b.a(pathtype) < 0.0F) {
            Set<BlockPosition> set = Sets.newHashSet();

            set.add(new BlockPosition(this.b.getBoundingBox().minX, (double) i, this.b.getBoundingBox().minZ));
            set.add(new BlockPosition(this.b.getBoundingBox().minX, (double) i, this.b.getBoundingBox().maxZ));
            set.add(new BlockPosition(this.b.getBoundingBox().maxX, (double) i, this.b.getBoundingBox().minZ));
            set.add(new BlockPosition(this.b.getBoundingBox().maxX, (double) i, this.b.getBoundingBox().maxZ));
            Iterator iterator = set.iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition1 = (BlockPosition) iterator.next();
                PathType pathtype1 = this.a(this.b, blockposition1);

                if (this.b.a(pathtype1) >= 0.0F) {
                    return this.a(blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
                }
            }
        }

        return this.a(blockposition.getX(), i, blockposition.getZ());
    }

    @Override
    public PathDestination a(double d0, double d1, double d2) {
        return new PathDestination(this.a(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2)));
    }

    @Override
    public int a(PathPoint[] apathpoint, PathPoint pathpoint) {
        int i = 0;
        int j = 0;
        PathType pathtype = this.a(this.b, pathpoint.a, pathpoint.b + 1, pathpoint.c);

        if (this.b.a(pathtype) >= 0.0F) {
            j = MathHelper.d(Math.max(1.0F, this.b.K));
        }

        double d0 = a((IBlockAccess) this.a, new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c));
        PathPoint pathpoint1 = this.a(pathpoint.a, pathpoint.b, pathpoint.c + 1, j, d0, EnumDirection.SOUTH);

        if (pathpoint1 != null && !pathpoint1.i && pathpoint1.k >= 0.0F) {
            apathpoint[i++] = pathpoint1;
        }

        PathPoint pathpoint2 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c, j, d0, EnumDirection.WEST);

        if (pathpoint2 != null && !pathpoint2.i && pathpoint2.k >= 0.0F) {
            apathpoint[i++] = pathpoint2;
        }

        PathPoint pathpoint3 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c, j, d0, EnumDirection.EAST);

        if (pathpoint3 != null && !pathpoint3.i && pathpoint3.k >= 0.0F) {
            apathpoint[i++] = pathpoint3;
        }

        PathPoint pathpoint4 = this.a(pathpoint.a, pathpoint.b, pathpoint.c - 1, j, d0, EnumDirection.NORTH);

        if (pathpoint4 != null && !pathpoint4.i && pathpoint4.k >= 0.0F) {
            apathpoint[i++] = pathpoint4;
        }

        PathPoint pathpoint5 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c - 1, j, d0, EnumDirection.NORTH);

        if (this.a(pathpoint, pathpoint2, pathpoint4, pathpoint5)) {
            apathpoint[i++] = pathpoint5;
        }

        PathPoint pathpoint6 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c - 1, j, d0, EnumDirection.NORTH);

        if (this.a(pathpoint, pathpoint3, pathpoint4, pathpoint6)) {
            apathpoint[i++] = pathpoint6;
        }

        PathPoint pathpoint7 = this.a(pathpoint.a - 1, pathpoint.b, pathpoint.c + 1, j, d0, EnumDirection.SOUTH);

        if (this.a(pathpoint, pathpoint2, pathpoint1, pathpoint7)) {
            apathpoint[i++] = pathpoint7;
        }

        PathPoint pathpoint8 = this.a(pathpoint.a + 1, pathpoint.b, pathpoint.c + 1, j, d0, EnumDirection.SOUTH);

        if (this.a(pathpoint, pathpoint3, pathpoint1, pathpoint8)) {
            apathpoint[i++] = pathpoint8;
        }

        return i;
    }

    private boolean a(PathPoint pathpoint, @Nullable PathPoint pathpoint1, @Nullable PathPoint pathpoint2, @Nullable PathPoint pathpoint3) {
        return pathpoint3 != null && pathpoint2 != null && pathpoint1 != null ? (pathpoint3.i ? false : (pathpoint2.b <= pathpoint.b && pathpoint1.b <= pathpoint.b ? pathpoint3.k >= 0.0F && (pathpoint2.b < pathpoint.b || pathpoint2.k >= 0.0F) && (pathpoint1.b < pathpoint.b || pathpoint1.k >= 0.0F) : false)) : false;
    }

    public static double a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();
        VoxelShape voxelshape = iblockaccess.getType(blockposition1).getCollisionShape(iblockaccess, blockposition1);

        return (double) blockposition1.getY() + (voxelshape.isEmpty() ? 0.0D : voxelshape.c(EnumDirection.EnumAxis.Y));
    }

    @Nullable
    private PathPoint a(int i, int j, int k, int l, double d0, EnumDirection enumdirection) {
        PathPoint pathpoint = null;
        BlockPosition blockposition = new BlockPosition(i, j, k);
        double d1 = a((IBlockAccess) this.a, blockposition);

        if (d1 - d0 > 1.125D) {
            return null;
        } else {
            PathType pathtype = this.a(this.b, i, j, k);
            float f = this.b.a(pathtype);
            double d2 = (double) this.b.getWidth() / 2.0D;

            if (f >= 0.0F) {
                pathpoint = this.a(i, j, k);
                pathpoint.l = pathtype;
                pathpoint.k = Math.max(pathpoint.k, f);
            }

            if (pathtype == PathType.WALKABLE) {
                return pathpoint;
            } else {
                if ((pathpoint == null || pathpoint.k < 0.0F) && l > 0 && pathtype != PathType.FENCE && pathtype != PathType.TRAPDOOR) {
                    pathpoint = this.a(i, j + 1, k, l - 1, d0, enumdirection);
                    if (pathpoint != null && (pathpoint.l == PathType.OPEN || pathpoint.l == PathType.WALKABLE) && this.b.getWidth() < 1.0F) {
                        double d3 = (double) (i - enumdirection.getAdjacentX()) + 0.5D;
                        double d4 = (double) (k - enumdirection.getAdjacentZ()) + 0.5D;
                        AxisAlignedBB axisalignedbb = new AxisAlignedBB(d3 - d2, a((IBlockAccess) this.a, new BlockPosition(d3, (double) (j + 1), d4)) + 0.001D, d4 - d2, d3 + d2, (double) this.b.getHeight() + a((IBlockAccess) this.a, new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c)) - 0.002D, d4 + d2);

                        if (!this.a.getCubes(this.b, axisalignedbb)) {
                            pathpoint = null;
                        }
                    }
                }

                if (pathtype == PathType.WATER && !this.e()) {
                    if (this.a(this.b, i, j - 1, k) != PathType.WATER) {
                        return pathpoint;
                    }

                    while (j > 0) {
                        --j;
                        pathtype = this.a(this.b, i, j, k);
                        if (pathtype != PathType.WATER) {
                            return pathpoint;
                        }

                        pathpoint = this.a(i, j, k);
                        pathpoint.l = pathtype;
                        pathpoint.k = Math.max(pathpoint.k, this.b.a(pathtype));
                    }
                }

                if (pathtype == PathType.OPEN) {
                    AxisAlignedBB axisalignedbb1 = new AxisAlignedBB((double) i - d2 + 0.5D, (double) j + 0.001D, (double) k - d2 + 0.5D, (double) i + d2 + 0.5D, (double) ((float) j + this.b.getHeight()), (double) k + d2 + 0.5D);

                    if (!this.a.getCubes(this.b, axisalignedbb1)) {
                        return null;
                    }

                    if (this.b.getWidth() >= 1.0F) {
                        PathType pathtype1 = this.a(this.b, i, j - 1, k);

                        if (pathtype1 == PathType.BLOCKED) {
                            pathpoint = this.a(i, j, k);
                            pathpoint.l = PathType.WALKABLE;
                            pathpoint.k = Math.max(pathpoint.k, f);
                            return pathpoint;
                        }
                    }

                    int i1 = 0;
                    int j1 = j;

                    while (pathtype == PathType.OPEN) {
                        --j;
                        PathPoint pathpoint1;

                        if (j < 0) {
                            pathpoint1 = this.a(i, j1, k);
                            pathpoint1.l = PathType.BLOCKED;
                            pathpoint1.k = -1.0F;
                            return pathpoint1;
                        }

                        pathpoint1 = this.a(i, j, k);
                        if (i1++ >= this.b.bv()) {
                            pathpoint1.l = PathType.BLOCKED;
                            pathpoint1.k = -1.0F;
                            return pathpoint1;
                        }

                        pathtype = this.a(this.b, i, j, k);
                        f = this.b.a(pathtype);
                        if (pathtype != PathType.OPEN && f >= 0.0F) {
                            pathpoint = pathpoint1;
                            pathpoint1.l = pathtype;
                            pathpoint1.k = Math.max(pathpoint1.k, f);
                            break;
                        }

                        if (f < 0.0F) {
                            pathpoint1.l = PathType.BLOCKED;
                            pathpoint1.k = -1.0F;
                            return pathpoint1;
                        }
                    }
                }

                return pathpoint;
            }
        }
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, EntityInsentient entityinsentient, int l, int i1, int j1, boolean flag, boolean flag1) {
        EnumSet<PathType> enumset = EnumSet.noneOf(PathType.class);
        PathType pathtype = PathType.BLOCKED;
        double d0 = (double) entityinsentient.getWidth() / 2.0D;
        BlockPosition blockposition = new BlockPosition(entityinsentient);

        pathtype = this.a(iblockaccess, i, j, k, l, i1, j1, flag, flag1, enumset, pathtype, blockposition);
        if (enumset.contains(PathType.FENCE)) {
            return PathType.FENCE;
        } else {
            PathType pathtype1 = PathType.BLOCKED;
            Iterator iterator = enumset.iterator();

            while (iterator.hasNext()) {
                PathType pathtype2 = (PathType) iterator.next();

                if (entityinsentient.a(pathtype2) < 0.0F) {
                    return pathtype2;
                }

                if (entityinsentient.a(pathtype2) >= entityinsentient.a(pathtype1)) {
                    pathtype1 = pathtype2;
                }
            }

            if (pathtype == PathType.OPEN && entityinsentient.a(pathtype1) == 0.0F) {
                return PathType.OPEN;
            } else {
                return pathtype1;
            }
        }
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, int l, int i1, int j1, boolean flag, boolean flag1, EnumSet<PathType> enumset, PathType pathtype, BlockPosition blockposition) {
        for (int k1 = 0; k1 < l; ++k1) {
            for (int l1 = 0; l1 < i1; ++l1) {
                for (int i2 = 0; i2 < j1; ++i2) {
                    int j2 = k1 + i;
                    int k2 = l1 + j;
                    int l2 = i2 + k;
                    PathType pathtype1 = this.a(iblockaccess, j2, k2, l2);

                    pathtype1 = this.a(iblockaccess, flag, flag1, blockposition, pathtype1);
                    if (k1 == 0 && l1 == 0 && i2 == 0) {
                        pathtype = pathtype1;
                    }

                    enumset.add(pathtype1);
                }
            }
        }

        return pathtype;
    }

    protected PathType a(IBlockAccess iblockaccess, boolean flag, boolean flag1, BlockPosition blockposition, PathType pathtype) {
        if (pathtype == PathType.DOOR_WOOD_CLOSED && flag && flag1) {
            pathtype = PathType.WALKABLE;
        }

        if (pathtype == PathType.DOOR_OPEN && !flag1) {
            pathtype = PathType.BLOCKED;
        }

        if (pathtype == PathType.RAIL && !(iblockaccess.getType(blockposition).getBlock() instanceof BlockMinecartTrackAbstract) && !(iblockaccess.getType(blockposition.down()).getBlock() instanceof BlockMinecartTrackAbstract)) {
            pathtype = PathType.FENCE;
        }

        if (pathtype == PathType.LEAVES) {
            pathtype = PathType.BLOCKED;
        }

        return pathtype;
    }

    private PathType a(EntityInsentient entityinsentient, BlockPosition blockposition) {
        return this.a(entityinsentient, blockposition.getX(), blockposition.getY(), blockposition.getZ());
    }

    private PathType a(EntityInsentient entityinsentient, int i, int j, int k) {
        return this.a(this.a, i, j, k, entityinsentient, this.d, this.e, this.f, this.d(), this.c());
    }

    @Override
    public PathType a(IBlockAccess iblockaccess, int i, int j, int k) {
        PathType pathtype = this.b(iblockaccess, i, j, k);

        if (pathtype == PathType.OPEN && j >= 1) {
            Block block = iblockaccess.getBlockIfLoaded(new BlockPosition(i, j - 1, k)); // Paper
            if (block == null) return PathType.BLOCKED; // Paper
            PathType pathtype1 = this.b(iblockaccess, i, j - 1, k);

            pathtype = pathtype1 != PathType.WALKABLE && pathtype1 != PathType.OPEN && pathtype1 != PathType.WATER && pathtype1 != PathType.LAVA ? PathType.WALKABLE : PathType.OPEN;
            if (pathtype1 == PathType.DAMAGE_FIRE || block == Blocks.MAGMA_BLOCK || block == Blocks.CAMPFIRE) {
                pathtype = PathType.DAMAGE_FIRE;
            }

            if (pathtype1 == PathType.DAMAGE_CACTUS) {
                pathtype = PathType.DAMAGE_CACTUS;
            }

            if (pathtype1 == PathType.DAMAGE_OTHER) {
                pathtype = PathType.DAMAGE_OTHER;
            }
        }

        pathtype = this.a(iblockaccess, i, j, k, pathtype);
        return pathtype;
    }

    public PathType a(IBlockAccess iblockaccess, int i, int j, int k, PathType pathtype) {
        if (pathtype == PathType.WALKABLE) {
            BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
            Throwable throwable = null;

            try {
                for (int l = -1; l <= 1; ++l) {
                    for (int i1 = -1; i1 <= 1; ++i1) {
                        if (l != 0 || i1 != 0) {
                            Block block = iblockaccess.getBlockIfLoaded(blockposition_pooledblockposition.d(l + i, j, i1 + k)); // Paper

                            if (block == null) pathtype = PathType.BLOCKED; // Paper
                            else if (block == Blocks.CACTUS) { // Paper
                                pathtype = PathType.DANGER_CACTUS;
                            } else if (block == Blocks.FIRE) {
                                pathtype = PathType.DANGER_FIRE;
                            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                                pathtype = PathType.DANGER_OTHER;
                            }
                        }
                    }
                }
            } catch (Throwable throwable1) {
                throwable = throwable1;
                throw throwable1;
            } finally {
                if (blockposition_pooledblockposition != null) {
                    if (throwable != null) {
                        try {
                            blockposition_pooledblockposition.close();
                        } catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    } else {
                        blockposition_pooledblockposition.close();
                    }
                }

            }
        }

        return pathtype;
    }

    protected PathType b(IBlockAccess iblockaccess, int i, int j, int k) {
        BlockPosition blockposition = new BlockPosition(i, j, k);
        IBlockData iblockdata = iblockaccess.getTypeIfLoaded(blockposition); // Paper
        if (iblockdata == null) return PathType.BLOCKED; // Paper
        Block block = iblockdata.getBlock();
        Material material = iblockdata.getMaterial();

        if (iblockdata.isAir()) {
            return PathType.OPEN;
        } else if (!block.a(TagsBlock.TRAPDOORS) && block != Blocks.LILY_PAD) {
            if (block == Blocks.FIRE) {
                return PathType.DAMAGE_FIRE;
            } else if (block == Blocks.CACTUS) {
                return PathType.DAMAGE_CACTUS;
            } else if (block == Blocks.SWEET_BERRY_BUSH) {
                return PathType.DAMAGE_OTHER;
            } else if (block instanceof BlockDoor && material == Material.WOOD && !(Boolean) iblockdata.get(BlockDoor.OPEN)) {
                return PathType.DOOR_WOOD_CLOSED;
            } else if (block instanceof BlockDoor && material == Material.ORE && !(Boolean) iblockdata.get(BlockDoor.OPEN)) {
                return PathType.DOOR_IRON_CLOSED;
            } else if (block instanceof BlockDoor && (Boolean) iblockdata.get(BlockDoor.OPEN)) {
                return PathType.DOOR_OPEN;
            } else if (block instanceof BlockMinecartTrackAbstract) {
                return PathType.RAIL;
            } else if (block instanceof BlockLeaves) {
                return PathType.LEAVES;
            } else if (!block.a(TagsBlock.FENCES) && !block.a(TagsBlock.WALLS) && (!(block instanceof BlockFenceGate) || (Boolean) iblockdata.get(BlockFenceGate.OPEN))) {
                Fluid fluid = iblockaccess.getFluid(blockposition);

                return fluid.a(TagsFluid.WATER) ? PathType.WATER : (fluid.a(TagsFluid.LAVA) ? PathType.LAVA : (iblockdata.a(iblockaccess, blockposition, PathMode.LAND) ? PathType.OPEN : PathType.BLOCKED));
            } else {
                return PathType.FENCE;
            }
        } else {
            return PathType.TRAPDOOR;
        }
    }
}
