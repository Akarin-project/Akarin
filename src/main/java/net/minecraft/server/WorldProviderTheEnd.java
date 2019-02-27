package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class WorldProviderTheEnd extends WorldProvider {

    public static final BlockPosition g = new BlockPosition(100, 50, 0);
    private EnderDragonBattle h;

    public WorldProviderTheEnd() {}

    public void m() {
        NBTTagCompound nbttagcompound = this.b.getWorldData().a(DimensionManager.THE_END);

        this.h = this.b instanceof WorldServer ? new EnderDragonBattle((WorldServer) this.b, nbttagcompound.getCompound("DragonFight")) : null;
        this.e = false;
    }

    public ChunkGenerator<?> getChunkGenerator() {
        GeneratorSettingsEnd generatorsettingsend = (GeneratorSettingsEnd) ChunkGeneratorType.c.b();

        generatorsettingsend.a(Blocks.END_STONE.getBlockData());
        generatorsettingsend.b(Blocks.AIR.getBlockData());
        generatorsettingsend.a(this.d());
        return ChunkGeneratorType.c.create(this.b, BiomeLayout.d.a(((BiomeLayoutTheEndConfiguration) BiomeLayout.d.b()).a(this.b.getSeed())), generatorsettingsend);
    }

    public float a(long i, float f) {
        return 0.0F;
    }

    public boolean canRespawn() {
        return false;
    }

    public boolean isOverworld() {
        return false;
    }

    @Nullable
    public BlockPosition a(ChunkCoordIntPair chunkcoordintpair, boolean flag) {
        Random random = new Random(this.b.getSeed());
        BlockPosition blockposition = new BlockPosition(chunkcoordintpair.d() + random.nextInt(15), 0, chunkcoordintpair.g() + random.nextInt(15));

        return this.b.i(blockposition).getMaterial().isSolid() ? blockposition : null;
    }

    public BlockPosition d() {
        return WorldProviderTheEnd.g;
    }

    @Nullable
    public BlockPosition a(int i, int j, boolean flag) {
        return this.a(new ChunkCoordIntPair(i >> 4, j >> 4), flag);
    }

    public DimensionManager getDimensionManager() {
        return DimensionManager.THE_END;
    }

    public void k() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.h != null) {
            nbttagcompound.set("DragonFight", this.h.a());
        }

        this.b.getWorldData().a(DimensionManager.THE_END, nbttagcompound);
    }

    public void l() {
        if (this.h != null) {
            this.h.b();
        }

    }

    @Nullable
    public EnderDragonBattle r() {
        return this.h;
    }
}
