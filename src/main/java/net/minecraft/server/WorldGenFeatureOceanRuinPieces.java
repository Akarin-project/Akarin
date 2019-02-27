package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class WorldGenFeatureOceanRuinPieces {

    private static final MinecraftKey[] a = new MinecraftKey[] { new MinecraftKey("underwater_ruin/warm_1"), new MinecraftKey("underwater_ruin/warm_2"), new MinecraftKey("underwater_ruin/warm_3"), new MinecraftKey("underwater_ruin/warm_4"), new MinecraftKey("underwater_ruin/warm_5"), new MinecraftKey("underwater_ruin/warm_6"), new MinecraftKey("underwater_ruin/warm_7"), new MinecraftKey("underwater_ruin/warm_8")};
    private static final MinecraftKey[] b = new MinecraftKey[] { new MinecraftKey("underwater_ruin/brick_1"), new MinecraftKey("underwater_ruin/brick_2"), new MinecraftKey("underwater_ruin/brick_3"), new MinecraftKey("underwater_ruin/brick_4"), new MinecraftKey("underwater_ruin/brick_5"), new MinecraftKey("underwater_ruin/brick_6"), new MinecraftKey("underwater_ruin/brick_7"), new MinecraftKey("underwater_ruin/brick_8")};
    private static final MinecraftKey[] c = new MinecraftKey[] { new MinecraftKey("underwater_ruin/cracked_1"), new MinecraftKey("underwater_ruin/cracked_2"), new MinecraftKey("underwater_ruin/cracked_3"), new MinecraftKey("underwater_ruin/cracked_4"), new MinecraftKey("underwater_ruin/cracked_5"), new MinecraftKey("underwater_ruin/cracked_6"), new MinecraftKey("underwater_ruin/cracked_7"), new MinecraftKey("underwater_ruin/cracked_8")};
    private static final MinecraftKey[] d = new MinecraftKey[] { new MinecraftKey("underwater_ruin/mossy_1"), new MinecraftKey("underwater_ruin/mossy_2"), new MinecraftKey("underwater_ruin/mossy_3"), new MinecraftKey("underwater_ruin/mossy_4"), new MinecraftKey("underwater_ruin/mossy_5"), new MinecraftKey("underwater_ruin/mossy_6"), new MinecraftKey("underwater_ruin/mossy_7"), new MinecraftKey("underwater_ruin/mossy_8")};
    private static final MinecraftKey[] e = new MinecraftKey[] { new MinecraftKey("underwater_ruin/big_brick_1"), new MinecraftKey("underwater_ruin/big_brick_2"), new MinecraftKey("underwater_ruin/big_brick_3"), new MinecraftKey("underwater_ruin/big_brick_8")};
    private static final MinecraftKey[] f = new MinecraftKey[] { new MinecraftKey("underwater_ruin/big_mossy_1"), new MinecraftKey("underwater_ruin/big_mossy_2"), new MinecraftKey("underwater_ruin/big_mossy_3"), new MinecraftKey("underwater_ruin/big_mossy_8")};
    private static final MinecraftKey[] g = new MinecraftKey[] { new MinecraftKey("underwater_ruin/big_cracked_1"), new MinecraftKey("underwater_ruin/big_cracked_2"), new MinecraftKey("underwater_ruin/big_cracked_3"), new MinecraftKey("underwater_ruin/big_cracked_8")};
    private static final MinecraftKey[] h = new MinecraftKey[] { new MinecraftKey("underwater_ruin/big_warm_4"), new MinecraftKey("underwater_ruin/big_warm_5"), new MinecraftKey("underwater_ruin/big_warm_6"), new MinecraftKey("underwater_ruin/big_warm_7")};

    public static void a() {
        WorldGenFactory.a(WorldGenFeatureOceanRuinPieces.a.class, "ORP");
    }

    private static MinecraftKey a(Random random) {
        return WorldGenFeatureOceanRuinPieces.a[random.nextInt(WorldGenFeatureOceanRuinPieces.a.length)];
    }

    private static MinecraftKey b(Random random) {
        return WorldGenFeatureOceanRuinPieces.h[random.nextInt(WorldGenFeatureOceanRuinPieces.h.length)];
    }

    public static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<StructurePiece> list, Random random, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration) {
        boolean flag = random.nextFloat() <= worldgenfeatureoceanruinconfiguration.b;
        float f = flag ? 0.9F : 0.8F;

        a(definedstructuremanager, blockposition, enumblockrotation, list, random, worldgenfeatureoceanruinconfiguration, flag, f);
        if (flag && random.nextFloat() <= worldgenfeatureoceanruinconfiguration.c) {
            a(definedstructuremanager, random, enumblockrotation, blockposition, worldgenfeatureoceanruinconfiguration, list);
        }

    }

    private static void a(DefinedStructureManager definedstructuremanager, Random random, EnumBlockRotation enumblockrotation, BlockPosition blockposition, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration, List<StructurePiece> list) {
        int i = blockposition.getX();
        int j = blockposition.getZ();
        BlockPosition blockposition1 = DefinedStructure.a(new BlockPosition(15, 0, 15), EnumBlockMirror.NONE, enumblockrotation, new BlockPosition(0, 0, 0)).a(i, 0, j);
        StructureBoundingBox structureboundingbox = StructureBoundingBox.a(i, 0, j, blockposition1.getX(), 0, blockposition1.getZ());
        BlockPosition blockposition2 = new BlockPosition(Math.min(i, blockposition1.getX()), 0, Math.min(j, blockposition1.getZ()));
        List<BlockPosition> list1 = a(random, blockposition2.getX(), blockposition2.getZ());
        int k = MathHelper.nextInt(random, 4, 8);

        for (int l = 0; l < k; ++l) {
            if (!list1.isEmpty()) {
                int i1 = random.nextInt(list1.size());
                BlockPosition blockposition3 = (BlockPosition) list1.remove(i1);
                int j1 = blockposition3.getX();
                int k1 = blockposition3.getZ();
                EnumBlockRotation enumblockrotation1 = EnumBlockRotation.values()[random.nextInt(EnumBlockRotation.values().length)];
                BlockPosition blockposition4 = DefinedStructure.a(new BlockPosition(5, 0, 6), EnumBlockMirror.NONE, enumblockrotation1, new BlockPosition(0, 0, 0)).a(j1, 0, k1);
                StructureBoundingBox structureboundingbox1 = StructureBoundingBox.a(j1, 0, k1, blockposition4.getX(), 0, blockposition4.getZ());

                if (!structureboundingbox1.a(structureboundingbox)) {
                    a(definedstructuremanager, blockposition3, enumblockrotation1, list, random, worldgenfeatureoceanruinconfiguration, false, 0.8F);
                }
            }
        }

    }

    private static List<BlockPosition> a(Random random, int i, int j) {
        List<BlockPosition> list = Lists.newArrayList();

        list.add(new BlockPosition(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPosition(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPosition(i - 16 + MathHelper.nextInt(random, 1, 8), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
        list.add(new BlockPosition(i + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPosition(i + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 6)));
        list.add(new BlockPosition(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + 16 + MathHelper.nextInt(random, 3, 8)));
        list.add(new BlockPosition(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j + MathHelper.nextInt(random, 1, 7)));
        list.add(new BlockPosition(i + 16 + MathHelper.nextInt(random, 1, 7), 90, j - 16 + MathHelper.nextInt(random, 4, 8)));
        return list;
    }

    private static void a(DefinedStructureManager definedstructuremanager, BlockPosition blockposition, EnumBlockRotation enumblockrotation, List<StructurePiece> list, Random random, WorldGenFeatureOceanRuinConfiguration worldgenfeatureoceanruinconfiguration, boolean flag, float f) {
        if (worldgenfeatureoceanruinconfiguration.a == WorldGenFeatureOceanRuin.Temperature.WARM) {
            MinecraftKey minecraftkey = flag ? b(random) : a(random);

            list.add(new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, minecraftkey, blockposition, enumblockrotation, f, worldgenfeatureoceanruinconfiguration.a, flag));
        } else if (worldgenfeatureoceanruinconfiguration.a == WorldGenFeatureOceanRuin.Temperature.COLD) {
            MinecraftKey[] aminecraftkey = flag ? WorldGenFeatureOceanRuinPieces.e : WorldGenFeatureOceanRuinPieces.b;
            MinecraftKey[] aminecraftkey1 = flag ? WorldGenFeatureOceanRuinPieces.g : WorldGenFeatureOceanRuinPieces.c;
            MinecraftKey[] aminecraftkey2 = flag ? WorldGenFeatureOceanRuinPieces.f : WorldGenFeatureOceanRuinPieces.d;
            int i = random.nextInt(aminecraftkey.length);

            list.add(new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, aminecraftkey[i], blockposition, enumblockrotation, f, worldgenfeatureoceanruinconfiguration.a, flag));
            list.add(new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, aminecraftkey1[i], blockposition, enumblockrotation, 0.7F, worldgenfeatureoceanruinconfiguration.a, flag));
            list.add(new WorldGenFeatureOceanRuinPieces.a(definedstructuremanager, aminecraftkey2[i], blockposition, enumblockrotation, 0.5F, worldgenfeatureoceanruinconfiguration.a, flag));
        }

    }

    public static class a extends DefinedStructurePiece {

        private WorldGenFeatureOceanRuin.Temperature d;
        private float e;
        private MinecraftKey f;
        private EnumBlockRotation g;
        private boolean h;

        public a() {}

        public a(DefinedStructureManager definedstructuremanager, MinecraftKey minecraftkey, BlockPosition blockposition, EnumBlockRotation enumblockrotation, float f, WorldGenFeatureOceanRuin.Temperature worldgenfeatureoceanruin_temperature, boolean flag) {
            super(0);
            this.f = minecraftkey;
            this.c = blockposition;
            this.g = enumblockrotation;
            this.e = f;
            this.d = worldgenfeatureoceanruin_temperature;
            this.h = flag;
            this.a(definedstructuremanager);
        }

        private void a(DefinedStructureManager definedstructuremanager) {
            DefinedStructure definedstructure = definedstructuremanager.a(this.f);
            DefinedStructureInfo definedstructureinfo = (new DefinedStructureInfo()).a(this.g).a(EnumBlockMirror.NONE).a(Blocks.AIR);

            this.a(definedstructure, this.c, definedstructureinfo);
        }

        protected void a(NBTTagCompound nbttagcompound) {
            super.a(nbttagcompound);
            nbttagcompound.setString("Template", this.f.toString());
            nbttagcompound.setString("Rot", this.g.name());
            nbttagcompound.setFloat("Integrity", this.e);
            nbttagcompound.setString("BiomeType", this.d.toString());
            nbttagcompound.setBoolean("IsLarge", this.h);
        }

        protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
            super.a(nbttagcompound, definedstructuremanager);
            this.f = new MinecraftKey(nbttagcompound.getString("Template"));
            this.g = EnumBlockRotation.valueOf(nbttagcompound.getString("Rot"));
            this.e = nbttagcompound.getFloat("Integrity");
            this.d = WorldGenFeatureOceanRuin.Temperature.valueOf(nbttagcompound.getString("BiomeType"));
            this.h = nbttagcompound.getBoolean("IsLarge");
            this.a(definedstructuremanager);
        }

        protected void a(String s, BlockPosition blockposition, GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox) {
            if ("chest".equals(s)) {
                generatoraccess.setTypeAndData(blockposition, (IBlockData) Blocks.CHEST.getBlockData().set(BlockChest.c, generatoraccess.getFluid(blockposition).a(TagsFluid.WATER)), 2);
                TileEntity tileentity = generatoraccess.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityChest) {
                    ((TileEntityChest) tileentity).setLootTable(this.h ? LootTables.q : LootTables.p, random.nextLong());
                }
            } else if ("drowned".equals(s)) {
                EntityDrowned entitydrowned = new EntityDrowned(generatoraccess.getMinecraftWorld());

                entitydrowned.di();
                entitydrowned.setPositionRotation(blockposition, 0.0F, 0.0F);
                entitydrowned.prepare(generatoraccess.getDamageScaler(blockposition), (GroupDataEntity) null, (NBTTagCompound) null);
                generatoraccess.addEntity(entitydrowned);
                if (blockposition.getY() > generatoraccess.getSeaLevel()) {
                    generatoraccess.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 2);
                } else {
                    generatoraccess.setTypeAndData(blockposition, Blocks.WATER.getBlockData(), 2);
                }
            }

        }

        public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
            this.b.a(this.e);
            int i = generatoraccess.a(HeightMap.Type.OCEAN_FLOOR_WG, this.c.getX(), this.c.getZ());

            this.c = new BlockPosition(this.c.getX(), i, this.c.getZ());
            BlockPosition blockposition = DefinedStructure.a(new BlockPosition(this.a.a().getX() - 1, 0, this.a.a().getZ() - 1), EnumBlockMirror.NONE, this.g, new BlockPosition(0, 0, 0)).a((BaseBlockPosition) this.c);

            this.c = new BlockPosition(this.c.getX(), this.a(this.c, (IBlockAccess) generatoraccess, blockposition), this.c.getZ());
            return super.a(generatoraccess, random, structureboundingbox, chunkcoordintpair);
        }

        private int a(BlockPosition blockposition, IBlockAccess iblockaccess, BlockPosition blockposition1) {
            int i = blockposition.getY();
            int j = 512;
            int k = i - 1;
            int l = 0;
            Iterator iterator = BlockPosition.a(blockposition, blockposition1).iterator();

            while (iterator.hasNext()) {
                BlockPosition blockposition2 = (BlockPosition) iterator.next();
                int i1 = blockposition2.getX();
                int j1 = blockposition2.getZ();
                int k1 = blockposition.getY() - 1;
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(i1, k1, j1);
                IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

                for (Fluid fluid = iblockaccess.getFluid(blockposition_mutableblockposition); (iblockdata.isAir() || fluid.a(TagsFluid.WATER) || iblockdata.getBlock().a(TagsBlock.ICE)) && k1 > 1; fluid = iblockaccess.getFluid(blockposition_mutableblockposition)) {
                    --k1;
                    blockposition_mutableblockposition.c(i1, k1, j1);
                    iblockdata = iblockaccess.getType(blockposition_mutableblockposition);
                }

                j = Math.min(j, k1);
                if (k1 < k - 2) {
                    ++l;
                }
            }

            int l1 = Math.abs(blockposition.getX() - blockposition1.getX());

            if (k - j > 2 && l > l1 - 2) {
                i = j + 1;
            }

            return i;
        }
    }
}
