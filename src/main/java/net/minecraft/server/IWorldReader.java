package net.minecraft.server;

import com.google.common.collect.Streams;
import java.util.Collections;
import java.util.Set;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;

public interface IWorldReader extends IIBlockAccess {

    default boolean isEmpty(BlockPosition blockposition) {
        return this.getType(blockposition).isAir();
    }

    default boolean u(BlockPosition blockposition) {
        if (blockposition.getY() >= this.getSeaLevel()) {
            return this.f(blockposition);
        } else {
            BlockPosition blockposition1 = new BlockPosition(blockposition.getX(), this.getSeaLevel(), blockposition.getZ());

            if (!this.f(blockposition1)) {
                return false;
            } else {
                for (blockposition1 = blockposition1.down(); blockposition1.getY() > blockposition.getY(); blockposition1 = blockposition1.down()) {
                    IBlockData iblockdata = this.getType(blockposition1);

                    if (iblockdata.b((IBlockAccess) this, blockposition1) > 0 && !iblockdata.getMaterial().isLiquid()) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    int getLightLevel(BlockPosition blockposition, int i);

    @Nullable IChunkAccess getChunkIfLoadedImmediately(int x, int z); // Paper - ifLoaded api (we need this since current impl blocks if the chunk is loading)
    @Nullable
    IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus, boolean flag);

    @Deprecated
    boolean isChunkLoaded(int i, int j);

    BlockPosition getHighestBlockYAt(HeightMap.Type heightmap_type, BlockPosition blockposition);

    int a(HeightMap.Type heightmap_type, int i, int j);

    default float v(BlockPosition blockposition) {
        return this.getWorldProvider().i()[this.getLightLevel(blockposition)];
    }

    int c();

    WorldBorder getWorldBorder();

    boolean a(@Nullable Entity entity, VoxelShape voxelshape);

    default int c(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getType(blockposition).c(this, blockposition, enumdirection);
    }

    boolean e();

    int getSeaLevel();

    default IChunkAccess w(BlockPosition blockposition) {
        return this.getChunkAt(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }

    default IChunkAccess getChunkAt(int i, int j) {
        return this.getChunkAt(i, j, ChunkStatus.FULL, true);
    }

    default IChunkAccess getChunkAt(int i, int j, ChunkStatus chunkstatus) {
        return this.getChunkAt(i, j, chunkstatus, true);
    }

    default ChunkStatus O() {
        return ChunkStatus.EMPTY;
    }

    default boolean a(IBlockData iblockdata, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        VoxelShape voxelshape = iblockdata.b((IBlockAccess) this, blockposition, voxelshapecollision);

        return voxelshape.isEmpty() || this.a((Entity) null, voxelshape.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ()));
    }

    default boolean i(Entity entity) {
        return this.a(entity, VoxelShapes.a(entity.getBoundingBox()));
    }

    default boolean c(AxisAlignedBB axisalignedbb) {
        return this.b((Entity) null, axisalignedbb, Collections.emptySet());
    }

    default boolean getCubes(Entity entity) {
        return this.b(entity, entity.getBoundingBox(), Collections.emptySet());
    }

    default boolean getCubes(Entity entity, AxisAlignedBB axisalignedbb) {
        return this.b(entity, axisalignedbb, Collections.emptySet());
    }

    default boolean b(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return this.c(entity, axisalignedbb, set).allMatch(VoxelShape::isEmpty);
    }

    default Stream<VoxelShape> a(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return Stream.empty();
    }

    default Stream<VoxelShape> c(@Nullable Entity entity, AxisAlignedBB axisalignedbb, Set<Entity> set) {
        return Streams.concat(new Stream[]{this.b(entity, axisalignedbb), this.a(entity, axisalignedbb, set)});
    }

    default Stream<VoxelShape> b(@Nullable final Entity entity, AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX - 1.0E-7D) - 1;
        int j = MathHelper.floor(axisalignedbb.maxX + 1.0E-7D) + 1;
        int k = MathHelper.floor(axisalignedbb.minY - 1.0E-7D) - 1;
        int l = MathHelper.floor(axisalignedbb.maxY + 1.0E-7D) + 1;
        int i1 = MathHelper.floor(axisalignedbb.minZ - 1.0E-7D) - 1;
        int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0E-7D) + 1;
        final VoxelShapeCollision voxelshapecollision = entity == null ? VoxelShapeCollision.a() : VoxelShapeCollision.a(entity);
        final CursorPosition cursorposition = new CursorPosition(i, k, i1, j, l, j1);
        final BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
        final VoxelShape voxelshape = VoxelShapes.a(axisalignedbb);

        return StreamSupport.stream(new AbstractSpliterator<VoxelShape>(Long.MAX_VALUE, 1280) {
            boolean a = entity == null;

            public boolean tryAdvance(Consumer<? super VoxelShape> consumer) {
                if (!this.a) {
                    this.a = true;
                    VoxelShape voxelshape1 = IWorldReader.this.getWorldBorder().a();
                    boolean flag = VoxelShapes.c(voxelshape1, VoxelShapes.a(entity.getBoundingBox().shrink(1.0E-7D)), OperatorBoolean.AND);
                    boolean flag1 = VoxelShapes.c(voxelshape1, VoxelShapes.a(entity.getBoundingBox().g(1.0E-7D)), OperatorBoolean.AND);

                    if (!flag && flag1) {
                        consumer.accept(voxelshape1);
                        return true;
                    }
                }

                while (cursorposition.a()) {
                    int k1 = cursorposition.b();
                    int l1 = cursorposition.c();
                    int i2 = cursorposition.d();
                    int j2 = cursorposition.e();

                    if (j2 != 3) {
                        int k2 = k1 >> 4;
                        int l2 = i2 >> 4;
                        IChunkAccess ichunkaccess = IWorldReader.this.getChunkAt(k2, l2, IWorldReader.this.O(), false);

                        if (ichunkaccess != null) {
                            blockposition_mutableblockposition.d(k1, l1, i2);
                            IBlockData iblockdata = ichunkaccess.getType(blockposition_mutableblockposition);

                            if ((j2 != 1 || iblockdata.f()) && (j2 != 2 || iblockdata.getBlock() == Blocks.MOVING_PISTON)) {
                                VoxelShape voxelshape2 = iblockdata.b((IBlockAccess) IWorldReader.this, blockposition_mutableblockposition, voxelshapecollision);
                                VoxelShape voxelshape3 = voxelshape2.a((double) k1, (double) l1, (double) i2);

                                if (VoxelShapes.c(voxelshape, voxelshape3, OperatorBoolean.AND)) {
                                    consumer.accept(voxelshape3);
                                    return true;
                                }
                            }
                        }
                    }
                }

                return false;
            }
        }, false);
    }

    default boolean x(BlockPosition blockposition) {
        return this.getFluid(blockposition).a(TagsFluid.WATER);
    }

    default boolean containsLiquid(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.f(axisalignedbb.maxX);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.f(axisalignedbb.maxY);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.f(axisalignedbb.maxZ);
        BlockPosition.PooledBlockPosition blockposition_pooledblockposition = BlockPosition.PooledBlockPosition.r();
        Throwable throwable = null;

        try {
            for (int k1 = i; k1 < j; ++k1) {
                for (int l1 = k; l1 < l; ++l1) {
                    for (int i2 = i1; i2 < j1; ++i2) {
                        IBlockData iblockdata = this.getType(blockposition_pooledblockposition.d(k1, l1, i2));

                        if (!iblockdata.p().isEmpty()) {
                            boolean flag = true;

                            return flag;
                        }
                    }
                }
            }

            return false;
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

    default int getLightLevel(BlockPosition blockposition) {
        return this.d(blockposition, this.c());
    }

    default int d(BlockPosition blockposition, int i) {
        return blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() < 30000000 ? this.getLightLevel(blockposition, i) : 15;
    }

    @Deprecated
    default boolean isLoaded(BlockPosition blockposition) {
        return this.isChunkLoaded(blockposition.getX() >> 4, blockposition.getZ() >> 4);
    }

    @Deprecated
    default boolean areChunksLoadedBetween(BlockPosition blockposition, BlockPosition blockposition1) {
        return this.isAreaLoaded(blockposition.getX(), blockposition.getY(), blockposition.getZ(), blockposition1.getX(), blockposition1.getY(), blockposition1.getZ());
    }

    @Deprecated
    default boolean isAreaLoaded(int i, int j, int k, int l, int i1, int j1) {
        if (i1 >= 0 && j < 256) {
            i >>= 4;
            k >>= 4;
            l >>= 4;
            j1 >>= 4;

            for (int k1 = i; k1 <= l; ++k1) {
                for (int l1 = k; l1 <= j1; ++l1) {
                    if (!this.isChunkLoaded(k1, l1)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    WorldProvider getWorldProvider();
}
