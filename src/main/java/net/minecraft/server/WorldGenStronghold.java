package net.minecraft.server;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;

public class WorldGenStronghold extends StructureGenerator<WorldGenFeatureEmptyConfiguration> {

    /* // Paper start - no shared state
    private boolean a;
    private ChunkCoordIntPair[] aS;
    private final List<StructureStart> aT = Lists.newArrayList();
    private long aU;
     */

    public WorldGenStronghold(Function<Dynamic<?>, ? extends WorldGenFeatureEmptyConfiguration> function) {
        super(function);
    }

    @Override
    public boolean a(ChunkGenerator<?> chunkgenerator, Random random, int i, int j) {
        // Paper start
        /*
        if (this.aU != chunkgenerator.getSeed()) {
            this.d();
        }
        */
        final World world = chunkgenerator.getWorld();

        synchronized (world.stuctureLock) {
        if ( world.strongholdCoords == null) {
            this.a(chunkgenerator);
          // this.a = true;
        }}
        // Paper end

        ChunkCoordIntPair[] achunkcoordintpair = world.strongholdCoords; // Paper
        int k = achunkcoordintpair.length;

        for (int l = 0; l < k; ++l) {
            ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[l];

            if (i == chunkcoordintpair.x && j == chunkcoordintpair.z) {
                return true;
            }
        }

        return false;
    }

    private void d() {
        /* // Paper
        this.a = false;
        this.aS = null;
        this.aT.clear();
         */ // Paper
    }

    @Override
    public StructureGenerator.a a() {
        return WorldGenStronghold.a::new;
    }

    @Override
    public String b() {
        return "Stronghold";
    }

    @Override
    public int c() {
        return 8;
    }


    @Nullable
    @Override
    public synchronized BlockPosition getNearestGeneratedFeature(World world, ChunkGenerator<? extends GeneratorSettingsDefault> chunkgenerator, BlockPosition blockposition, int i, boolean flag) { // CraftBukkit - synchronized
        if (!chunkgenerator.getWorldChunkManager().a(this)) {
            return null;
        } else {
            // Paper start - no shared state
            /*
            if (this.aU != world.getSeed()) {
                this.d();
            }
             */

            synchronized (world.stuctureLock) {
                if ( world.strongholdCoords == null) {
                    this.a(chunkgenerator);
                    //this.a = true;
                }
            }
            // Paper end

            BlockPosition blockposition1 = null;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
            double d0 = Double.MAX_VALUE;
            ChunkCoordIntPair[] achunkcoordintpair = world.strongholdCoords; // Paper
            int j = achunkcoordintpair.length;

            for (int k = 0; k < j; ++k) {
                ChunkCoordIntPair chunkcoordintpair = achunkcoordintpair[k];

                blockposition_mutableblockposition.d((chunkcoordintpair.x << 4) + 8, 32, (chunkcoordintpair.z << 4) + 8);
                double d1 = blockposition_mutableblockposition.m(blockposition);

                if (blockposition1 == null) {
                    blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                    d0 = d1;
                } else if (d1 < d0) {
                    blockposition1 = new BlockPosition(blockposition_mutableblockposition);
                    d0 = d1;
                }
            }

            return blockposition1;
        }
    }

    private void a(ChunkGenerator<?> chunkgenerator) {
        //this.aU = chunkgenerator.getSeed(); // Paper
        List<BiomeBase> list = Lists.newArrayList();
        Iterator iterator = IRegistry.BIOME.iterator();

        while (iterator.hasNext()) {
            BiomeBase biomebase = (BiomeBase) iterator.next();

            if (biomebase != null && chunkgenerator.canSpawnStructure(biomebase, WorldGenerator.STRONGHOLD)) {
                list.add(biomebase);
            }
        }

        int i = chunkgenerator.getSettings().e();
        int j = chunkgenerator.getSettings().f();
        int k = chunkgenerator.getSettings().g();

        ChunkCoordIntPair[] strongholdCoords = chunkgenerator.getWorld().strongholdCoords = new ChunkCoordIntPair[j]; // Paper
        int l = 0;
        Iterator iterator1 = chunkgenerator.getWorld().strongholdStuctures.iterator(); // Paper

        while (iterator1.hasNext()) {
            StructureStart structurestart = (StructureStart) iterator1.next();

            if (l < strongholdCoords.length) { // Paper
                strongholdCoords[l++] = new ChunkCoordIntPair(structurestart.f(), structurestart.g()); // Paper
            }
        }

        Random random = new Random();

        random.setSeed(chunkgenerator.getSeed());
        double d0 = random.nextDouble() * 3.141592653589793D * 2.0D;
        int i1 = l;

        if (l < strongholdCoords.length) { // Paper
            int j1 = 0;
            int k1 = 0;

            for (int l1 = 0; l1 < strongholdCoords.length; ++l1) { // Paper
                double d1 = (double) (4 * i + i * k1 * 6) + (random.nextDouble() - 0.5D) * (double) i * 2.5D;
                int i2 = (int) Math.round(Math.cos(d0) * d1);
                int j2 = (int) Math.round(Math.sin(d0) * d1);
                BlockPosition blockposition = chunkgenerator.getWorldChunkManager().a((i2 << 4) + 8, (j2 << 4) + 8, 112, list, random);

                if (blockposition != null) {
                    i2 = blockposition.getX() >> 4;
                    j2 = blockposition.getZ() >> 4;
                }

                if (l1 >= i1) {
                    strongholdCoords[l1] = new ChunkCoordIntPair(i2, j2); // Paper
                }

                d0 += 6.283185307179586D / (double) k;
                ++j1;
                if (j1 == k) {
                    ++k1;
                    j1 = 0;
                    k += 2 * k / (k1 + 1);
                    k = Math.min(k, strongholdCoords.length - l1);
                    d0 += random.nextDouble() * 3.141592653589793D * 2.0D;
                }
            }
        }

    }

    public static class a extends StructureStart {

        public a(StructureGenerator<?> structuregenerator, int i, int j, BiomeBase biomebase, StructureBoundingBox structureboundingbox, int k, long l) {
            super(structuregenerator, i, j, biomebase, structureboundingbox, k, l);
        }

        @Override
        public void a(ChunkGenerator<?> chunkgenerator, DefinedStructureManager definedstructuremanager, int i, int j, BiomeBase biomebase) {
            int k = 0;
            long l = chunkgenerator.getSeed();

            WorldGenStrongholdPieces.WorldGenStrongholdStart worldgenstrongholdpieces_worldgenstrongholdstart;

            do {
                this.b.clear();
                this.c = StructureBoundingBox.a();
                this.d.c(l + (long) (k++), i, j);
                WorldGenStrongholdPieces.a();
                worldgenstrongholdpieces_worldgenstrongholdstart = new WorldGenStrongholdPieces.WorldGenStrongholdStart(this.d, (i << 4) + 2, (j << 4) + 2);
                this.b.add(worldgenstrongholdpieces_worldgenstrongholdstart);
                worldgenstrongholdpieces_worldgenstrongholdstart.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.b, (Random) this.d);
                List list = worldgenstrongholdpieces_worldgenstrongholdstart.c;

                while (!list.isEmpty()) {
                    int i1 = this.d.nextInt(list.size());
                    StructurePiece structurepiece = (StructurePiece) list.remove(i1);

                    structurepiece.a((StructurePiece) worldgenstrongholdpieces_worldgenstrongholdstart, this.b, (Random) this.d);
                }

                this.b();
                this.a(chunkgenerator.getSeaLevel(), this.d, 10);
            } while (this.b.isEmpty() || worldgenstrongholdpieces_worldgenstrongholdstart.b == null);

            chunkgenerator.getWorld().strongholdStuctures.add(this); // Paper - this worries me, this is never cleared, even in vanilla (world seed never changes "world", and that appears to be the only relevant world)
        }
    }
}
