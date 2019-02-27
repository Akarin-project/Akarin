package net.minecraft.server;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class ChunkCache implements IIBlockAccess {

    protected int a;
    protected int b;
    protected Chunk[][] c;
    protected boolean d;
    protected World e;

    public ChunkCache(World world, BlockPosition blockposition, BlockPosition blockposition1, int i) {
        this.e = world;
        this.a = blockposition.getX() - i >> 4;
        this.b = blockposition.getZ() - i >> 4;
        int j = blockposition1.getX() + i >> 4;
        int k = blockposition1.getZ() + i >> 4;

        this.c = new Chunk[j - this.a + 1][k - this.b + 1];
        this.d = true;

        int l;
        int i1;

        for (l = this.a; l <= j; ++l) {
            for (i1 = this.b; i1 <= k; ++i1) {
                this.c[l - this.a][i1 - this.b] = world.getChunkAt(l, i1);
            }
        }

        for (l = blockposition.getX() >> 4; l <= blockposition1.getX() >> 4; ++l) {
            for (i1 = blockposition.getZ() >> 4; i1 <= blockposition1.getZ() >> 4; ++i1) {
                Chunk chunk = this.c[l - this.a][i1 - this.b];

                if (chunk != null && !chunk.b(blockposition.getY(), blockposition1.getY())) {
                    this.d = false;
                }
            }
        }

    }

    @Nullable
    public TileEntity getTileEntity(BlockPosition blockposition) {
        return this.a(blockposition, Chunk.EnumTileEntityState.IMMEDIATE);
    }

    @Nullable
    public TileEntity a(BlockPosition blockposition, Chunk.EnumTileEntityState chunk_enumtileentitystate) {
        int i = (blockposition.getX() >> 4) - this.a;
        int j = (blockposition.getZ() >> 4) - this.b;

        return this.c[i][j].a(blockposition, chunk_enumtileentitystate);
    }

    public float A(BlockPosition blockposition) {
        return this.e.worldProvider.i()[this.getLightLevel(blockposition)];
    }

    public int d(BlockPosition blockposition, int i) {
        if (this.getType(blockposition).c(this, blockposition)) {
            int j = 0;
            EnumDirection[] aenumdirection = EnumDirection.values();
            int k = aenumdirection.length;

            for (int l = 0; l < k; ++l) {
                EnumDirection enumdirection = aenumdirection[l];
                int i1 = this.getLightLevel(blockposition.shift(enumdirection), i);

                if (i1 > j) {
                    j = i1;
                }

                if (j >= 15) {
                    return j;
                }
            }

            return j;
        } else {
            return this.getLightLevel(blockposition, i);
        }
    }

    public WorldProvider o() {
        return this.e.o();
    }

    public int getLightLevel(BlockPosition blockposition, int i) {
        if (blockposition.getX() >= -30000000 && blockposition.getZ() >= -30000000 && blockposition.getX() < 30000000 && blockposition.getZ() <= 30000000) {
            if (blockposition.getY() < 0) {
                return 0;
            } else {
                int j;

                if (blockposition.getY() >= 256) {
                    j = 15 - i;
                    if (j < 0) {
                        j = 0;
                    }

                    return j;
                } else {
                    j = (blockposition.getX() >> 4) - this.a;
                    int k = (blockposition.getZ() >> 4) - this.b;

                    return this.c[j][k].a(blockposition, i);
                }
            }
        } else {
            return 15;
        }
    }

    public boolean isChunkLoaded(int i, int j, boolean flag) {
        return this.a(i, j);
    }

    public boolean e(BlockPosition blockposition) {
        return false;
    }

    public boolean a(int i, int j) {
        int k = i - this.a;
        int l = j - this.b;

        return k >= 0 && k < this.c.length && l >= 0 && l < this.c[k].length;
    }

    public int a(HeightMap.Type heightmap_type, int i, int j) {
        throw new RuntimeException("NOT IMPLEMENTED!");
    }

    public WorldBorder getWorldBorder() {
        return this.e.getWorldBorder();
    }

    public boolean a(@Nullable Entity entity, VoxelShape voxelshape) {
        throw new RuntimeException("This method should never be called here. No entity logic inside Region");
    }

    @Nullable
    public EntityHuman a(double d0, double d1, double d2, double d3, Predicate<Entity> predicate) {
        throw new RuntimeException("This method should never be called here. No entity logic inside Region");
    }

    public IBlockData getType(BlockPosition blockposition) {
        if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
            int i = (blockposition.getX() >> 4) - this.a;
            int j = (blockposition.getZ() >> 4) - this.b;

            if (i >= 0 && i < this.c.length && j >= 0 && j < this.c[i].length) {
                Chunk chunk = this.c[i][j];

                if (chunk != null) {
                    return chunk.getType(blockposition);
                }
            }
        }

        return Blocks.AIR.getBlockData();
    }

    public Fluid getFluid(BlockPosition blockposition) {
        if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
            int i = (blockposition.getX() >> 4) - this.a;
            int j = (blockposition.getZ() >> 4) - this.b;

            if (i >= 0 && i < this.c.length && j >= 0 && j < this.c[i].length) {
                Chunk chunk = this.c[i][j];

                if (chunk != null) {
                    return chunk.getFluid(blockposition);
                }
            }
        }

        return FluidTypes.EMPTY.i();
    }

    public int c() {
        return 0;
    }

    public BiomeBase getBiome(BlockPosition blockposition) {
        int i = (blockposition.getX() >> 4) - this.a;
        int j = (blockposition.getZ() >> 4) - this.b;

        return this.c[i][j].getBiome(blockposition);
    }

    public boolean isEmpty(BlockPosition blockposition) {
        return this.getType(blockposition).isAir();
    }

    public int getBrightness(EnumSkyBlock enumskyblock, BlockPosition blockposition) {
        if (blockposition.getY() >= 0 && blockposition.getY() < 256) {
            int i = (blockposition.getX() >> 4) - this.a;
            int j = (blockposition.getZ() >> 4) - this.b;

            return this.c[i][j].getBrightness(enumskyblock, blockposition);
        } else {
            return enumskyblock.c;
        }
    }

    public int a(BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getType(blockposition).b((IBlockAccess) this, blockposition, enumdirection);
    }

    public boolean e() {
        throw new RuntimeException("Not yet implemented");
    }

    public int getSeaLevel() {
        throw new RuntimeException("Not yet implemented");
    }
}
