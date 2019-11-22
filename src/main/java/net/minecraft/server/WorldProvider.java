package net.minecraft.server;

import javax.annotation.Nullable;

public abstract class WorldProvider {

    public static final float[] a = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    protected final World b;
    private final DimensionManager f;
    protected boolean c;
    protected boolean d;
    protected final float[] e = new float[16];
    private final float[] g = new float[4];

    public WorldProvider(World world, DimensionManager dimensionmanager) {
        this.b = world;
        this.f = dimensionmanager;
        this.a();
    }

    protected void a() {
        float f = 0.0F;

        for (int i = 0; i <= 15; ++i) {
            float f1 = 1.0F - (float) i / 15.0F;

            this.e[i] = (1.0F - f1) / (f1 * 3.0F + 1.0F) * 1.0F + 0.0F;
        }

    }

    public int a(long i) {
        return (int) (i / 24000L % 8L + 8L) % 8;
    }

    @Nullable
    public BlockPosition d() {
        return null;
    }

    public boolean isNether() {
        return this.c;
    }

    public boolean g() {
        return this.f.hasSkyLight();
    }

    public boolean h() {
        return this.d;
    }

    public float[] i() {
        return this.e;
    }

    public WorldBorder getWorldBorder() {
        return new WorldBorder();
    }

    public void k() {}

    public void l() {}

    public abstract ChunkGenerator<?> getChunkGenerator();

    @Nullable
    public abstract BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag);

    @Nullable
    public abstract BlockPosition a(int i, int j, boolean flag);

    public abstract float a(long i, float f);

    public abstract boolean isOverworld();

    public abstract boolean canRespawn();

    public DimensionManager getDimensionManager() { return this.f; } // CraftBukkit
}
