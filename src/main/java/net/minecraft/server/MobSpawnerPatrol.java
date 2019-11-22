package net.minecraft.server;

import java.util.Random;

public class MobSpawnerPatrol {

    private int a;

    public MobSpawnerPatrol() {}

    public int a(WorldServer worldserver, boolean flag, boolean flag1) {
        if (!flag) {
            return 0;
        } else {
            Random random = worldserver.random;

            --this.a;
            if (this.a > 0) {
                return 0;
            } else {
                this.a += 12000 + random.nextInt(1200);
                long i = worldserver.getDayTime() / 24000L;

                if (i >= 5L && worldserver.J()) {
                    if (random.nextInt(5) != 0) {
                        return 0;
                    } else {
                        int j = worldserver.getPlayers().size();

                        if (j < 1) {
                            return 0;
                        } else {
                            EntityHuman entityhuman = (EntityHuman) worldserver.getPlayers().get(random.nextInt(j));

                            if (entityhuman.isSpectator()) {
                                return 0;
                            } else if (worldserver.b_(entityhuman.getChunkCoordinates())) {
                                return 0;
                            } else {
                                int k = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                                int l = (24 + random.nextInt(24)) * (random.nextBoolean() ? -1 : 1);
                                BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

                                blockposition_mutableblockposition.c(entityhuman.locX, entityhuman.locY, entityhuman.locZ).e(k, 0, l);
                                if (!worldserver.isAreaLoaded(blockposition_mutableblockposition.getX() - 10, blockposition_mutableblockposition.getY() - 10, blockposition_mutableblockposition.getZ() - 10, blockposition_mutableblockposition.getX() + 10, blockposition_mutableblockposition.getY() + 10, blockposition_mutableblockposition.getZ() + 10)) {
                                    return 0;
                                } else {
                                    BiomeBase biomebase = worldserver.getBiome(blockposition_mutableblockposition);
                                    BiomeBase.Geography biomebase_geography = biomebase.o();

                                    if (biomebase_geography == BiomeBase.Geography.MUSHROOM) {
                                        return 0;
                                    } else {
                                        int i1 = 0;
                                        int j1 = (int) Math.ceil((double) worldserver.getDamageScaler(blockposition_mutableblockposition).b()) + 1;

                                        for (int k1 = 0; k1 < j1; ++k1) {
                                            ++i1;
                                            blockposition_mutableblockposition.p(worldserver.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, blockposition_mutableblockposition).getY());
                                            if (k1 == 0) {
                                                if (!this.a(worldserver, blockposition_mutableblockposition, random, true)) {
                                                    break;
                                                }
                                            } else {
                                                this.a(worldserver, blockposition_mutableblockposition, random, false);
                                            }

                                            blockposition_mutableblockposition.o(blockposition_mutableblockposition.getX() + random.nextInt(5) - random.nextInt(5));
                                            blockposition_mutableblockposition.q(blockposition_mutableblockposition.getZ() + random.nextInt(5) - random.nextInt(5));
                                        }

                                        return i1;
                                    }
                                }
                            }
                        }
                    }
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean a(World world, BlockPosition blockposition, Random random, boolean flag) {
        if (!EntityMonsterPatrolling.b(EntityTypes.PILLAGER, world, EnumMobSpawn.PATROL, blockposition, random)) {
            return false;
        } else {
            EntityMonsterPatrolling entitymonsterpatrolling = (EntityMonsterPatrolling) EntityTypes.PILLAGER.a(world);

            if (entitymonsterpatrolling != null) {
                if (flag) {
                    entitymonsterpatrolling.setPatrolLeader(true);
                    entitymonsterpatrolling.ed();
                }

                entitymonsterpatrolling.setPosition((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
                entitymonsterpatrolling.prepare(world, world.getDamageScaler(blockposition), EnumMobSpawn.PATROL, (GroupDataEntity) null, (NBTTagCompound) null);
                world.addEntity(entitymonsterpatrolling, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.PATROL); // CraftBukkit
                return true;
            } else {
                return false;
            }
        }
    }
}
