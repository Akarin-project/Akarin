package net.minecraft.server;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Consumer; // Paper
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import com.destroystokyo.paper.exception.ServerInternalException;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public final class SpawnerCreature {

    private static final Logger LOGGER = LogManager.getLogger();

    // Paper start - add maxSpawns parameter and return spawned mobs
    public static void a(EnumCreatureType enumcreaturetype, World world, Chunk chunk, BlockPosition blockposition) {
        spawnMobs(enumcreaturetype, world, chunk, blockposition, Integer.MAX_VALUE, null);
    }
    public static int spawnMobs(EnumCreatureType enumcreaturetype, World world, Chunk chunk, BlockPosition blockposition, int maxSpawns, Consumer<Entity> trackEntity) {
        // Paper end
        ChunkGenerator<?> chunkgenerator = world.getChunkProvider().getChunkGenerator();
        int i = 0; // Paper - force diff on name change
        BlockPosition blockposition1 = getRandomPosition(world, chunk);
        int j = blockposition1.getX();
        int k = blockposition1.getY();
        int l = blockposition1.getZ();

        if (k >= 1) {
            IBlockData iblockdata = world.getTypeIfLoadedAndInBounds(blockposition1);  // Paper - don't load chunks for mob spawn

            if (iblockdata != null && !iblockdata.isOccluding(chunk, blockposition1)) { // Paper - don't load chunks for mob spawn
                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                int i1 = 0;

                while (i1 < 3) {
                    int j1 = j;
                    int k1 = l;
                    boolean flag = true;
                    BiomeBase.BiomeMeta biomebase_biomemeta = null;
                    GroupDataEntity groupdataentity = null;
                    int l1 = MathHelper.f(Math.random() * 4.0D);
                    int i2 = 0; // Paper - force diff on name change
                    int j2 = 0;

                    while (true) {
                        if (j2 < l1) {
                            label104:
                            {
                                j1 += world.random.nextInt(6) - world.random.nextInt(6);
                                k1 += world.random.nextInt(6) - world.random.nextInt(6);
                                blockposition_mutableblockposition.d(j1, k, k1);
                                float f = (float) j1 + 0.5F;
                                float f1 = (float) k1 + 0.5F;
                                EntityHuman entityhuman = world.a((double) f, (double) f1, -1.0D);

                                if (entityhuman != null) {
                                    double d0 = entityhuman.e((double) f, (double) k, (double) f1);

                                    if (d0 > 576.0D && !blockposition.a((IPosition) (new Vec3D((double) f, (double) k, (double) f1)), 24.0D) && world.isLoadedAndInBounds(blockposition_mutableblockposition)) { // Paper - don't load chunks for mob spawn
                                        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(blockposition_mutableblockposition);

                                        if (Objects.equals(chunkcoordintpair, chunk.getPos()) || world.getChunkProvider().a(chunkcoordintpair)) {
                                            if (biomebase_biomemeta == null) {
                                                biomebase_biomemeta = a(chunkgenerator, enumcreaturetype, world.random, (BlockPosition) blockposition_mutableblockposition);
                                                if (biomebase_biomemeta == null) {
                                                    break label104;
                                                }

                                                l1 = biomebase_biomemeta.c + world.random.nextInt(1 + biomebase_biomemeta.d - biomebase_biomemeta.c);
                                            }

                                            if (biomebase_biomemeta.b.e() != EnumCreatureType.MISC && (biomebase_biomemeta.b.d() || d0 <= 16384.0D)) {
                                                EntityTypes<?> entitytypes = biomebase_biomemeta.b;

                                                if (entitytypes.b() && a(chunkgenerator, enumcreaturetype, biomebase_biomemeta, (BlockPosition) blockposition_mutableblockposition)) {
                                                    EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.a(entitytypes);

                                                    if (a(entitypositiontypes_surface, (IWorldReader) world, (BlockPosition) blockposition_mutableblockposition, entitytypes) && EntityPositionTypes.a(entitytypes, world, EnumMobSpawn.NATURAL, blockposition_mutableblockposition, world.random) && world.c(entitytypes.a((double) f, (double) k, (double) f1))) {
                                                        EntityInsentient entityinsentient;

                                                    // Paper start
                                                    com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent event;
                                                    EntityTypes<?> cls = biomebase_biomemeta.b;
                                                    org.bukkit.entity.EntityType type = org.bukkit.entity.EntityType.fromName(EntityTypes.getName(cls).getKey());
                                                    if (type != null) {
                                                        event = new com.destroystokyo.paper.event.entity.PreCreatureSpawnEvent(
                                                            MCUtil.toLocation(world, blockposition_mutableblockposition),
                                                            type, SpawnReason.NATURAL
                                                        );
                                                        if (!event.callEvent()) {
                                                            if (event.shouldAbortSpawn()) {
                                                                return i; // Paper
                                                            }
                                                            ++i2;
                                                            continue;
                                                        }
                                                    }
                                                    // Paper end

                                                        try {
                                                            Entity entity = entitytypes.a(world);

                                                            if (!(entity instanceof EntityInsentient)) {
                                                                throw new IllegalStateException("Trying to spawn a non-mob: " + IRegistry.ENTITY_TYPE.getKey(entitytypes));
                                                            }

                                                            entityinsentient = (EntityInsentient) entity;
                                                        } catch (Exception exception) {
                                                            SpawnerCreature.LOGGER.warn("Failed to create mob", exception);
                                                            ServerInternalException.reportInternalException(exception); // Paper
                                                            return i; // Paper
                                                        }

                                                        entityinsentient.setPositionRotation((double) f, (double) k, (double) f1, world.random.nextFloat() * 360.0F, 0.0F);
                                                        if ((d0 <= 16384.0D || !entityinsentient.isTypeNotPersistent(d0)) && entityinsentient.a((GeneratorAccess) world, EnumMobSpawn.NATURAL) && entityinsentient.a((IWorldReader) world)) {
                                                            groupdataentity = entityinsentient.prepare(world, world.getDamageScaler(new BlockPosition(entityinsentient)), EnumMobSpawn.NATURAL, groupdataentity, (NBTTagCompound) null);
                                                            // CraftBukkit start
                                                            if (world.addEntity(entityinsentient, SpawnReason.NATURAL)) {
                                                                ++i; // Paper - force diff on name change
                                                                ++i2;
                                                                if (trackEntity != null) {
                                                                    trackEntity.accept(entityinsentient); // Paper
                                                                }
                                                            }
                                                            if (i >= maxSpawns) { return i; } // Paper
                                                            // CraftBukkit end
                                                            if (i >= entityinsentient.dC()) {
                                                                return i; // Paper
                                                            }

                                                            if (entityinsentient.c(i2)) {
                                                                break label104;
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                ++j2;
                                continue;
                            }
                        }

                        ++i1;
                        break;
                    }
                }

            }
        }
        return i; // Paper
    }

    @Nullable
    private static BiomeBase.BiomeMeta a(ChunkGenerator<?> chunkgenerator, EnumCreatureType enumcreaturetype, Random random, BlockPosition blockposition) {
        List<BiomeBase.BiomeMeta> list = chunkgenerator.getMobsFor(enumcreaturetype, blockposition);

        return list.isEmpty() ? null : (BiomeBase.BiomeMeta) WeightedRandom.a(random, list);
    }

    private static boolean a(ChunkGenerator<?> chunkgenerator, EnumCreatureType enumcreaturetype, BiomeBase.BiomeMeta biomebase_biomemeta, BlockPosition blockposition) {
        List<BiomeBase.BiomeMeta> list = chunkgenerator.getMobsFor(enumcreaturetype, blockposition);

        return list.isEmpty() ? false : list.contains(biomebase_biomemeta);
    }

    private static BlockPosition getRandomPosition(World world, Chunk chunk) {
        ChunkCoordIntPair chunkcoordintpair = chunk.getPos();
        int i = chunkcoordintpair.d() + world.random.nextInt(16);
        int j = chunkcoordintpair.e() + world.random.nextInt(16);
        int k = chunk.a(HeightMap.Type.WORLD_SURFACE, i, j) + 1;
        int l = world.random.nextInt(k + 1);

        return new BlockPosition(i, l, j);
    }

    public static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, IBlockData iblockdata, Fluid fluid) {
        return iblockdata.o(iblockaccess, blockposition) ? false : (iblockdata.isPowerSource() ? false : (!fluid.isEmpty() ? false : !iblockdata.a(TagsBlock.RAILS)));
    }

    public static boolean a(EntityPositionTypes.Surface entitypositiontypes_surface, IWorldReader iworldreader, BlockPosition blockposition, @Nullable EntityTypes<?> entitytypes) {
        if (entitypositiontypes_surface == EntityPositionTypes.Surface.NO_RESTRICTIONS) {
            return true;
        } else if (entitytypes != null && iworldreader.getWorldBorder().a(blockposition)) {
            IBlockData iblockdata = iworldreader.getType(blockposition);
            Fluid fluid = iworldreader.getFluid(blockposition);
            BlockPosition blockposition1 = blockposition.up();
            BlockPosition blockposition2 = blockposition.down();

            switch (entitypositiontypes_surface) {
                case IN_WATER:
                    return fluid.a(TagsFluid.WATER) && iworldreader.getFluid(blockposition2).a(TagsFluid.WATER) && !iworldreader.getType(blockposition1).isOccluding(iworldreader, blockposition1);
                case ON_GROUND:
                default:
                    IBlockData iblockdata1 = iworldreader.getType(blockposition2);

                    return !iblockdata1.a((IBlockAccess) iworldreader, blockposition2, entitytypes) ? false : a((IBlockAccess) iworldreader, blockposition, iblockdata, fluid) && a((IBlockAccess) iworldreader, blockposition1, iworldreader.getType(blockposition1), iworldreader.getFluid(blockposition1));
            }
        } else {
            return false;
        }
    }

    public static void a(GeneratorAccess generatoraccess, BiomeBase biomebase, int i, int j, Random random) {
        List<BiomeBase.BiomeMeta> list = biomebase.getMobs(EnumCreatureType.CREATURE);

        if (!list.isEmpty()) {
            int k = i << 4;
            int l = j << 4;

            while (random.nextFloat() < biomebase.d()) {
                BiomeBase.BiomeMeta biomebase_biomemeta = (BiomeBase.BiomeMeta) WeightedRandom.a(random, list);
                int i1 = biomebase_biomemeta.c + random.nextInt(1 + biomebase_biomemeta.d - biomebase_biomemeta.c);
                GroupDataEntity groupdataentity = null;
                int j1 = k + random.nextInt(16);
                int k1 = l + random.nextInt(16);
                int l1 = j1;
                int i2 = k1;

                for (int j2 = 0; j2 < i1; ++j2) {
                    boolean flag = false;

                    for (int k2 = 0; !flag && k2 < 4; ++k2) {
                        BlockPosition blockposition = a(generatoraccess, biomebase_biomemeta.b, j1, k1);

                        if (biomebase_biomemeta.b.b() && a(EntityPositionTypes.Surface.ON_GROUND, (IWorldReader) generatoraccess, blockposition, biomebase_biomemeta.b)) {
                            float f = biomebase_biomemeta.b.i();
                            double d0 = MathHelper.a((double) j1, (double) k + (double) f, (double) k + 16.0D - (double) f);
                            double d1 = MathHelper.a((double) k1, (double) l + (double) f, (double) l + 16.0D - (double) f);

                            if (!generatoraccess.c(biomebase_biomemeta.b.a(d0, (double) blockposition.getY(), d1)) || !EntityPositionTypes.a(biomebase_biomemeta.b, generatoraccess, EnumMobSpawn.CHUNK_GENERATION, new BlockPosition(d0, (double) blockposition.getY(), d1), generatoraccess.getRandom())) {
                                continue;
                            }

                            Entity entity;

                            try {
                                entity = biomebase_biomemeta.b.a(generatoraccess.getMinecraftWorld());
                            } catch (Exception exception) {
                                SpawnerCreature.LOGGER.warn("Failed to create mob", exception);
                                ServerInternalException.reportInternalException(exception); // Paper
                                continue;
                            }

                            entity.setPositionRotation(d0, (double) blockposition.getY(), d1, random.nextFloat() * 360.0F, 0.0F);
                            if (entity instanceof EntityInsentient) {
                                EntityInsentient entityinsentient = (EntityInsentient) entity;

                                if (entityinsentient.a(generatoraccess, EnumMobSpawn.CHUNK_GENERATION) && entityinsentient.a((IWorldReader) generatoraccess)) {
                                    groupdataentity = entityinsentient.prepare(generatoraccess, generatoraccess.getDamageScaler(new BlockPosition(entityinsentient)), EnumMobSpawn.CHUNK_GENERATION, groupdataentity, (NBTTagCompound) null);
                                    generatoraccess.addEntity(entityinsentient, SpawnReason.CHUNK_GEN); // CraftBukkit
                                    flag = true;
                                }
                            }
                        }

                        j1 += random.nextInt(5) - random.nextInt(5);

                        for (k1 += random.nextInt(5) - random.nextInt(5); j1 < k || j1 >= k + 16 || k1 < l || k1 >= l + 16; k1 = i2 + random.nextInt(5) - random.nextInt(5)) {
                            j1 = l1 + random.nextInt(5) - random.nextInt(5);
                        }
                    }
                }
            }

        }
    }

    private static BlockPosition a(IWorldReader iworldreader, @Nullable EntityTypes<?> entitytypes, int i, int j) {
        BlockPosition blockposition = new BlockPosition(i, iworldreader.a(EntityPositionTypes.b(entitytypes), i, j), j);
        BlockPosition blockposition1 = blockposition.down();

        return iworldreader.getType(blockposition1).a((IBlockAccess) iworldreader, blockposition1, PathMode.LAND) ? blockposition1 : blockposition;
    }
}
