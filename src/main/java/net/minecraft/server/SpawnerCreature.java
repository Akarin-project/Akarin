package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.craftbukkit.util.LongHash;
import org.bukkit.craftbukkit.util.LongHashSet;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public final class SpawnerCreature {

    private static final Logger a = LogManager.getLogger();
    private static final int b = (int) Math.pow(17.0D, 2.0D);
    private final LongHashSet c = new LongHashSet(); // CraftBukkit

    public SpawnerCreature() {}

    public int a(WorldServer worldserver, boolean flag, boolean flag1, boolean flag2) {
        if (!flag && !flag1) {
            return 0;
        } else {
            this.c.clear();
            int i = 0;
            Iterator iterator = worldserver.players.iterator();

            int j;
            int k;

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (!entityhuman.isSpectator()) {
                    int l = MathHelper.floor(entityhuman.locX / 16.0D);

                    j = MathHelper.floor(entityhuman.locZ / 16.0D);
                    boolean flag3 = true;

                    for (int i1 = -8; i1 <= 8; ++i1) {
                        for (k = -8; k <= 8; ++k) {
                            boolean flag4 = i1 == -8 || i1 == 8 || k == -8 || k == 8;
                            ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(i1 + l, k + j);

                            // CraftBukkit start - use LongHash and LongHashSet
                            long chunkCoords = LongHash.toLong(chunkcoordintpair.x, chunkcoordintpair.z);
                            if (!this.c.contains(chunkCoords)) {
                                ++i;
                                if (!flag4 && worldserver.getWorldBorder().isInBounds(chunkcoordintpair)) {
                                    PlayerChunk playerchunk = worldserver.getPlayerChunkMap().getChunk(chunkcoordintpair.x, chunkcoordintpair.z);

                                    if (playerchunk != null && playerchunk.e()) {
                                        this.c.add(chunkCoords);
                                        // CraftBukkit end
                                    }
                                }
                            }
                        }
                    }
                }
            }

            int j1 = 0;
            BlockPosition blockposition = worldserver.getSpawn();
            EnumCreatureType[] aenumcreaturetype = EnumCreatureType.values();

            j = aenumcreaturetype.length;

            for (int k1 = 0; k1 < j; ++k1) {
                EnumCreatureType enumcreaturetype = aenumcreaturetype[k1];

               // CraftBukkit start - Use per-world spawn limits
                int limit = enumcreaturetype.b();
                switch (enumcreaturetype) {
                    case MONSTER:
                        limit = worldserver.getWorld().getMonsterSpawnLimit();
                        break;
                    case CREATURE:
                        limit = worldserver.getWorld().getAnimalSpawnLimit();
                        break;
                    case WATER_CREATURE:
                        limit = worldserver.getWorld().getWaterAnimalSpawnLimit();
                        break;
                    case AMBIENT:
                        limit = worldserver.getWorld().getAmbientSpawnLimit();
                        break;
                }

                if (limit == 0) {
                    continue;
                }
                // CraftBukkit end

                if ((!enumcreaturetype.c() || flag1) && (enumcreaturetype.c() || flag) && (!enumcreaturetype.d() || flag2)) {
                    k = limit * i / SpawnerCreature.b; // CraftBukkit - use per-world limits
                    int l1 = worldserver.a(enumcreaturetype.a(), k);

                    if (l1 <= k) {
                        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();
                        Iterator iterator1 = this.c.iterator();

                        label137:
                        while (iterator1.hasNext()) {
                            // CraftBukkit start = use LongHash and LongObjectHashMap
                            long key = ((Long) iterator1.next()).longValue();
                            BlockPosition blockposition1 = getRandomPosition(worldserver, LongHash.msw(key), LongHash.lsw(key));
                            // CraftBukkit
                            int i2 = blockposition1.getX();
                            int j2 = blockposition1.getY();
                            int k2 = blockposition1.getZ();
                            IBlockData iblockdata = worldserver.getType(blockposition1);

                            if (!iblockdata.isOccluding()) {
                                int l2 = 0;
                                int i3 = 0;

                                while (i3 < 3) {
                                    int j3 = i2;
                                    int k3 = j2;
                                    int l3 = k2;
                                    boolean flag5 = true;
                                    BiomeBase.BiomeMeta biomebase_biomemeta = null;
                                    GroupDataEntity groupdataentity = null;
                                    int i4 = MathHelper.f(Math.random() * 4.0D);
                                    int j4 = 0;
                                    int k4 = 0;

                                    while (true) {
                                        if (k4 < i4) {
                                            label130:
                                            {
                                                j3 += worldserver.random.nextInt(6) - worldserver.random.nextInt(6);
                                                k3 += worldserver.random.nextInt(1) - worldserver.random.nextInt(1);
                                                l3 += worldserver.random.nextInt(6) - worldserver.random.nextInt(6);
                                                blockposition_mutableblockposition.c(j3, k3, l3);
                                                float f = (float) j3 + 0.5F;
                                                float f1 = (float) l3 + 0.5F;
                                                EntityHuman entityhuman1 = worldserver.a((double) f, (double) f1, -1.0D);

                                                if (entityhuman1 != null) {
                                                    double d0 = entityhuman1.d((double) f, (double) k3, (double) f1);

                                                    if (d0 > 576.0D && blockposition.distanceSquared((double) f, (double) k3, (double) f1) >= 576.0D) {
                                                        if (biomebase_biomemeta == null) {
                                                            biomebase_biomemeta = worldserver.a(enumcreaturetype, (BlockPosition) blockposition_mutableblockposition);
                                                            if (biomebase_biomemeta == null) {
                                                                break label130;
                                                            }

                                                            i4 = biomebase_biomemeta.c + worldserver.random.nextInt(1 + biomebase_biomemeta.d - biomebase_biomemeta.c);
                                                        }

                                                        if (worldserver.a(enumcreaturetype, biomebase_biomemeta, (BlockPosition) blockposition_mutableblockposition)) {
                                                            EntityPositionTypes.Surface entitypositiontypes_surface = EntityPositionTypes.a(biomebase_biomemeta.b);

                                                            if (entitypositiontypes_surface != null && a(entitypositiontypes_surface, worldserver, blockposition_mutableblockposition, biomebase_biomemeta.b)) {
                                                                EntityInsentient entityinsentient;

                                                                try {
                                                                    entityinsentient = (EntityInsentient) biomebase_biomemeta.b.a((World) worldserver);
                                                                } catch (Exception exception) {
                                                                    SpawnerCreature.a.warn("Failed to create mob", exception);
                                                                    return j1;
                                                                }

                                                                entityinsentient.setPositionRotation((double) f, (double) k3, (double) f1, worldserver.random.nextFloat() * 360.0F, 0.0F);
                                                                if ((d0 <= 16384.0D || !entityinsentient.isTypeNotPersistent()) && entityinsentient.a((GeneratorAccess) worldserver, false) && entityinsentient.a((IWorldReader) worldserver)) {
                                                                    groupdataentity = entityinsentient.prepare(worldserver.getDamageScaler(new BlockPosition(entityinsentient)), groupdataentity, (NBTTagCompound) null);
                                                                    if (entityinsentient.a((IWorldReader) worldserver)) {
                                                                        // CraftBukkit start
                                                                        if (worldserver.addEntity(entityinsentient, SpawnReason.NATURAL)) {
                                                                            ++l2;
                                                                            ++j4;
                                                                        }
                                                                        // CraftBukkit end
                                                                    } else {
                                                                        entityinsentient.die();
                                                                    }

                                                                    if (l2 >= entityinsentient.dg()) {
                                                                        continue label137;
                                                                    }

                                                                    if (entityinsentient.c(j4)) {
                                                                        break label130;
                                                                    }
                                                                }

                                                                j1 += l2;
                                                            }
                                                        }
                                                    }
                                                }

                                                ++k4;
                                                continue;
                                            }
                                        }

                                        ++i3;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return j1;
        }
    }

    private static BlockPosition getRandomPosition(World world, int i, int j) {
        Chunk chunk = world.getChunkAt(i, j);
        int k = i * 16 + world.random.nextInt(16);
        int l = j * 16 + world.random.nextInt(16);
        int i1 = chunk.a(HeightMap.Type.LIGHT_BLOCKING, k, l) + 1;
        int j1 = world.random.nextInt(i1 + 1);

        return new BlockPosition(k, j1, l);
    }

    public static boolean a(IBlockData iblockdata, Fluid fluid) {
        return iblockdata.k() ? false : (iblockdata.isPowerSource() ? false : (!fluid.e() ? false : !iblockdata.a(TagsBlock.RAILS)));
    }

    public static boolean a(EntityPositionTypes.Surface entitypositiontypes_surface, IWorldReader iworldreader, BlockPosition blockposition, @Nullable EntityTypes<? extends EntityInsentient> entitytypes) {
        if (entitytypes != null && iworldreader.getWorldBorder().a(blockposition)) {
            IBlockData iblockdata = iworldreader.getType(blockposition);
            Fluid fluid = iworldreader.getFluid(blockposition);

            switch (entitypositiontypes_surface) {
            case IN_WATER:
                return fluid.a(TagsFluid.WATER) && iworldreader.getFluid(blockposition.down()).a(TagsFluid.WATER) && !iworldreader.getType(blockposition.up()).isOccluding();
            case ON_GROUND:
            default:
                IBlockData iblockdata1 = iworldreader.getType(blockposition.down());

                if (!iblockdata1.q() && (entitytypes == null || !EntityPositionTypes.a(entitytypes, iblockdata1))) {
                    return false;
                } else {
                    Block block = iblockdata1.getBlock();
                    boolean flag = block != Blocks.BEDROCK && block != Blocks.BARRIER;

                    return flag && a(iblockdata, fluid) && a(iworldreader.getType(blockposition.up()), iworldreader.getFluid(blockposition.up()));
                }
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

            while (random.nextFloat() < biomebase.e()) {
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

                        if (a(EntityPositionTypes.Surface.ON_GROUND, generatoraccess, blockposition, biomebase_biomemeta.b)) {
                            EntityInsentient entityinsentient;

                            try {
                                entityinsentient = (EntityInsentient) biomebase_biomemeta.b.a(generatoraccess.getMinecraftWorld());
                            } catch (Exception exception) {
                                SpawnerCreature.a.warn("Failed to create mob", exception);
                                continue;
                            }

                            double d0 = MathHelper.a((double) j1, (double) k + (double) entityinsentient.width, (double) k + 16.0D - (double) entityinsentient.width);
                            double d1 = MathHelper.a((double) k1, (double) l + (double) entityinsentient.width, (double) l + 16.0D - (double) entityinsentient.width);

                            entityinsentient.setPositionRotation(d0, (double) blockposition.getY(), d1, random.nextFloat() * 360.0F, 0.0F);
                            if (entityinsentient.a(generatoraccess, false) && entityinsentient.a((IWorldReader) generatoraccess)) {
                                groupdataentity = entityinsentient.prepare(generatoraccess.getDamageScaler(new BlockPosition(entityinsentient)), groupdataentity, (NBTTagCompound) null);
                                generatoraccess.addEntity(entityinsentient, SpawnReason.CHUNK_GEN); // CraftBukkit
                                flag = true;
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

    private static BlockPosition a(GeneratorAccess generatoraccess, @Nullable EntityTypes<? extends EntityInsentient> entitytypes, int i, int j) {
        BlockPosition blockposition = new BlockPosition(i, generatoraccess.a(EntityPositionTypes.b(entitytypes), i, j), j);
        BlockPosition blockposition1 = blockposition.down();

        return generatoraccess.getType(blockposition1).a((IBlockAccess) generatoraccess, blockposition1, PathMode.LAND) ? blockposition1 : blockposition;
    }
}
