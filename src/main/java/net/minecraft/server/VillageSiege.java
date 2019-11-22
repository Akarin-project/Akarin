package net.minecraft.server;

import com.destroystokyo.paper.exception.ServerInternalException;

import java.util.Iterator;
import javax.annotation.Nullable;

public class VillageSiege {

    private boolean a;
    private VillageSiege.State b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;

    public VillageSiege() {
        this.b = VillageSiege.State.SIEGE_DONE;
    }

    public int a(WorldServer worldserver, boolean flag, boolean flag1) {
        if (!worldserver.J() && flag) {
            float f = worldserver.j(0.0F);

            if ((double) f == 0.5D) {
                this.b = worldserver.random.nextInt(10) == 0 ? VillageSiege.State.SIEGE_TONIGHT : VillageSiege.State.SIEGE_DONE;
            }

            if (this.b == VillageSiege.State.SIEGE_DONE) {
                return 0;
            } else {
                if (!this.a) {
                    if (!this.a(worldserver)) {
                        return 0;
                    }

                    this.a = true;
                }

                if (this.d > 0) {
                    --this.d;
                    return 0;
                } else {
                    this.d = 2;
                    if (this.c > 0) {
                        this.b(worldserver);
                        --this.c;
                    } else {
                        this.b = VillageSiege.State.SIEGE_DONE;
                    }

                    return 1;
                }
            }
        } else {
            this.b = VillageSiege.State.SIEGE_DONE;
            this.a = false;
            return 0;
        }
    }

    private boolean a(WorldServer worldserver) {
        Iterator iterator = worldserver.getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();

            if (!entityhuman.isSpectator()) {
                BlockPosition blockposition = entityhuman.getChunkCoordinates();

                if (worldserver.b_(blockposition) && worldserver.getBiome(blockposition).o() != BiomeBase.Geography.MUSHROOM) {
                    for (int i = 0; i < 10; ++i) {
                        float f = worldserver.random.nextFloat() * 6.2831855F;

                        this.e = blockposition.getX() + MathHelper.d(MathHelper.cos(f) * 32.0F);
                        this.f = blockposition.getY();
                        this.g = blockposition.getZ() + MathHelper.d(MathHelper.sin(f) * 32.0F);
                        if (this.a(worldserver, new BlockPosition(this.e, this.f, this.g)) != null) {
                            this.d = 0;
                            this.c = 20;
                            break;
                        }
                    }

                    return true;
                }
            }
        }

        return false;
    }

    private void b(WorldServer worldserver) {
        Vec3D vec3d = this.a(worldserver, new BlockPosition(this.e, this.f, this.g));

        if (vec3d != null) {
            EntityZombie entityzombie;

            try {
                entityzombie = new EntityZombie(worldserver);
                entityzombie.prepare(worldserver, worldserver.getDamageScaler(new BlockPosition(entityzombie)), EnumMobSpawn.EVENT, (GroupDataEntity) null, (NBTTagCompound) null);
            } catch (Exception exception) {
                exception.printStackTrace();
                ServerInternalException.reportInternalException(exception); // Paper
                return;
            }

            entityzombie.setPositionRotation(vec3d.x, vec3d.y, vec3d.z, worldserver.random.nextFloat() * 360.0F, 0.0F);
            worldserver.addEntity(entityzombie, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.VILLAGE_INVASION); // CraftBukkit
        }
    }

    @Nullable
    private Vec3D a(WorldServer worldserver, BlockPosition blockposition) {
        for (int i = 0; i < 10; ++i) {
            int j = blockposition.getX() + worldserver.random.nextInt(16) - 8;
            int k = blockposition.getZ() + worldserver.random.nextInt(16) - 8;
            int l = worldserver.a(HeightMap.Type.WORLD_SURFACE, j, k);
            BlockPosition blockposition1 = new BlockPosition(j, l, k);

            if (worldserver.b_(blockposition1) && EntityMonster.c(EntityTypes.ZOMBIE, worldserver, EnumMobSpawn.EVENT, blockposition1, worldserver.random)) {
                return new Vec3D((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D);
            }
        }

        return null;
    }

    static enum State {

        SIEGE_CAN_ACTIVATE, SIEGE_TONIGHT, SIEGE_DONE;

        private State() {}
    }
}
