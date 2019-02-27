package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public abstract class StructureStart {

    protected final List<StructurePiece> a = Lists.newArrayList();
    protected StructureBoundingBox b;
    protected int c;
    protected int d;
    private BiomeBase e;
    private int f;

    public StructureStart() {}

    public StructureStart(int i, int j, BiomeBase biomebase, SeededRandom seededrandom, long k) {
        this.c = i;
        this.d = j;
        this.e = biomebase;
        seededrandom.c(k, this.c, this.d);
    }

    public StructureBoundingBox c() {
        return this.b;
    }

    public List<StructurePiece> d() {
        return this.a;
    }

    public void a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        List list = this.a;

        synchronized (this.a) {
            Iterator iterator = this.a.iterator();

            while (iterator.hasNext()) {
                StructurePiece structurepiece = (StructurePiece) iterator.next();

                if (structurepiece.d().a(structureboundingbox) && !structurepiece.a(generatoraccess, random, structureboundingbox, chunkcoordintpair)) {
                    iterator.remove();
                }
            }

            this.a((IBlockAccess) generatoraccess);
        }
    }

    protected void a(IBlockAccess iblockaccess) {
        this.b = StructureBoundingBox.a();
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            this.b.b(structurepiece.d());
        }

    }

    public NBTTagCompound a(int i, int j) {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.b()) {
            nbttagcompound.setString("id", WorldGenFactory.a(this));
            nbttagcompound.setString("biome", IRegistry.BIOME.getKey(this.e).toString());
            nbttagcompound.setInt("ChunkX", i);
            nbttagcompound.setInt("ChunkZ", j);
            nbttagcompound.setInt("references", this.f);
            nbttagcompound.set("BB", this.b.g());
            NBTTagList nbttaglist = new NBTTagList();
            List list = this.a;

            synchronized (this.a) {
                Iterator iterator = this.a.iterator();

                while (iterator.hasNext()) {
                    StructurePiece structurepiece = (StructurePiece) iterator.next();

                    nbttaglist.add((NBTBase) structurepiece.c());
                }
            }

            nbttagcompound.set("Children", nbttaglist);
            this.a(nbttagcompound);
            return nbttagcompound;
        } else {
            nbttagcompound.setString("id", "INVALID");
            return nbttagcompound;
        }
    }

    public void a(NBTTagCompound nbttagcompound) {}

    public void a(GeneratorAccess generatoraccess, NBTTagCompound nbttagcompound) {
        this.c = nbttagcompound.getInt("ChunkX");
        this.d = nbttagcompound.getInt("ChunkZ");
        this.f = nbttagcompound.getInt("references");
        this.e = nbttagcompound.hasKey("biome") ? (BiomeBase) IRegistry.BIOME.get(new MinecraftKey(nbttagcompound.getString("biome"))) : generatoraccess.getChunkProvider().getChunkGenerator().getWorldChunkManager().getBiome(new BlockPosition((this.c << 4) + 9, 0, (this.d << 4) + 9), Biomes.PLAINS);
        if (nbttagcompound.hasKey("BB")) {
            this.b = new StructureBoundingBox(nbttagcompound.getIntArray("BB"));
        }

        NBTTagList nbttaglist = nbttagcompound.getList("Children", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            this.a.add(WorldGenFactory.b(nbttaglist.getCompound(i), generatoraccess));
        }

        this.b(nbttagcompound);
    }

    public void b(NBTTagCompound nbttagcompound) {}

    protected void a(IWorldReader iworldreader, Random random, int i) {
        int j = iworldreader.getSeaLevel() - i;
        int k = this.b.d() + 1;

        if (k < j) {
            k += random.nextInt(j - k);
        }

        int l = k - this.b.e;

        this.b.a(0, l, 0);
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            structurepiece.a(0, l, 0);
        }

    }

    protected void a(IBlockAccess iblockaccess, Random random, int i, int j) {
        int k = j - i + 1 - this.b.d();
        int l;

        if (k > 1) {
            l = i + random.nextInt(k);
        } else {
            l = i;
        }

        int i1 = l - this.b.b;

        this.b.a(0, i1, 0);
        Iterator iterator = this.a.iterator();

        while (iterator.hasNext()) {
            StructurePiece structurepiece = (StructurePiece) iterator.next();

            structurepiece.a(0, i1, 0);
        }

    }

    public boolean b() {
        return true;
    }

    public void b(ChunkCoordIntPair chunkcoordintpair) {}

    public int e() {
        return this.c;
    }

    public int f() {
        return this.d;
    }

    public BlockPosition a() {
        return new BlockPosition(this.c << 4, 0, this.d << 4);
    }

    public boolean g() {
        return this.f < this.i();
    }

    public void h() {
        ++this.f;
    }

    protected int i() {
        return 1;
    }
}
