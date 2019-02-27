package net.minecraft.server;

import java.util.Random;

public class WorldGenWitchHut extends WorldGenScatteredPiece {

    private boolean e;

    public static void b() {
        WorldGenFactory.a(WorldGenWitchHut.class, "TeSH");
    }

    public WorldGenWitchHut() {}

    public WorldGenWitchHut(Random random, int i, int j) {
        super(random, i, 64, j, 7, 7, 9);
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        nbttagcompound.setBoolean("Witch", this.e);
    }

    protected void a(NBTTagCompound nbttagcompound, DefinedStructureManager definedstructuremanager) {
        super.a(nbttagcompound, definedstructuremanager);
        this.e = nbttagcompound.getBoolean("Witch");
    }

    public boolean a(GeneratorAccess generatoraccess, Random random, StructureBoundingBox structureboundingbox, ChunkCoordIntPair chunkcoordintpair) {
        if (!this.a(generatoraccess, structureboundingbox, 0)) {
            return false;
        } else {
            this.a(generatoraccess, structureboundingbox, 1, 1, 1, 5, 1, 7, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 1, 4, 2, 5, 4, 7, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 2, 1, 0, 4, 1, 0, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 2, 2, 2, 3, 3, 2, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 1, 2, 3, 1, 3, 6, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 5, 2, 3, 5, 3, 6, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 2, 2, 7, 4, 3, 7, Blocks.SPRUCE_PLANKS.getBlockData(), Blocks.SPRUCE_PLANKS.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 1, 0, 2, 1, 3, 2, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 5, 0, 2, 5, 3, 2, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 1, 0, 7, 1, 3, 7, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccess, structureboundingbox, 5, 0, 7, 5, 3, 7, Blocks.OAK_LOG.getBlockData(), Blocks.OAK_LOG.getBlockData(), false);
            this.a(generatoraccess, Blocks.OAK_FENCE.getBlockData(), 2, 3, 2, structureboundingbox);
            this.a(generatoraccess, Blocks.OAK_FENCE.getBlockData(), 3, 3, 7, structureboundingbox);
            this.a(generatoraccess, Blocks.AIR.getBlockData(), 1, 3, 4, structureboundingbox);
            this.a(generatoraccess, Blocks.AIR.getBlockData(), 5, 3, 4, structureboundingbox);
            this.a(generatoraccess, Blocks.AIR.getBlockData(), 5, 3, 5, structureboundingbox);
            this.a(generatoraccess, Blocks.POTTED_RED_MUSHROOM.getBlockData(), 1, 3, 5, structureboundingbox);
            this.a(generatoraccess, Blocks.CRAFTING_TABLE.getBlockData(), 3, 2, 6, structureboundingbox);
            this.a(generatoraccess, Blocks.CAULDRON.getBlockData(), 4, 2, 6, structureboundingbox);
            this.a(generatoraccess, Blocks.OAK_FENCE.getBlockData(), 1, 2, 1, structureboundingbox);
            this.a(generatoraccess, Blocks.OAK_FENCE.getBlockData(), 5, 2, 1, structureboundingbox);
            IBlockData iblockdata = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.NORTH);
            IBlockData iblockdata1 = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.EAST);
            IBlockData iblockdata2 = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.WEST);
            IBlockData iblockdata3 = (IBlockData) Blocks.SPRUCE_STAIRS.getBlockData().set(BlockStairs.FACING, EnumDirection.SOUTH);

            this.a(generatoraccess, structureboundingbox, 0, 4, 1, 6, 4, 1, iblockdata, iblockdata, false);
            this.a(generatoraccess, structureboundingbox, 0, 4, 2, 0, 4, 7, iblockdata1, iblockdata1, false);
            this.a(generatoraccess, structureboundingbox, 6, 4, 2, 6, 4, 7, iblockdata2, iblockdata2, false);
            this.a(generatoraccess, structureboundingbox, 0, 4, 8, 6, 4, 8, iblockdata3, iblockdata3, false);
            this.a(generatoraccess, (IBlockData) iblockdata.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT), 0, 4, 1, structureboundingbox);
            this.a(generatoraccess, (IBlockData) iblockdata.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT), 6, 4, 1, structureboundingbox);
            this.a(generatoraccess, (IBlockData) iblockdata3.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_LEFT), 0, 4, 8, structureboundingbox);
            this.a(generatoraccess, (IBlockData) iblockdata3.set(BlockStairs.SHAPE, BlockPropertyStairsShape.OUTER_RIGHT), 6, 4, 8, structureboundingbox);

            int i;
            int j;

            for (j = 2; j <= 7; j += 5) {
                for (i = 1; i <= 5; i += 4) {
                    this.b(generatoraccess, Blocks.OAK_LOG.getBlockData(), i, -1, j, structureboundingbox);
                }
            }

            if (!this.e) {
                j = this.a(2, 5);
                i = this.d(2);
                int k = this.b(2, 5);

                if (structureboundingbox.b((BaseBlockPosition) (new BlockPosition(j, i, k)))) {
                    this.e = true;
                    EntityWitch entitywitch = new EntityWitch(generatoraccess.getMinecraftWorld());

                    entitywitch.di();
                    entitywitch.setPositionRotation((double) j + 0.5D, (double) i, (double) k + 0.5D, 0.0F, 0.0F);
                    entitywitch.prepare(generatoraccess.getDamageScaler(new BlockPosition(j, i, k)), (GroupDataEntity) null, (NBTTagCompound) null);
                    generatoraccess.addEntity(entitywitch);
                }
            }

            return true;
        }
    }
}
