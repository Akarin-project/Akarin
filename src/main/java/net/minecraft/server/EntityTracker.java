package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {

    private static final Logger a = LogManager.getLogger();
    private final WorldServer world;
    private final Set<EntityTrackerEntry> c = Sets.newHashSet();
    public final IntHashMap<EntityTrackerEntry> trackedEntities = new IntHashMap<>();
    private int trackingDistance;

    public EntityTracker(WorldServer worldserver) {
        this.world = worldserver;
        this.trackingDistance = worldserver.getMinecraftServer().getPlayerList().getFurthestViewableBlock();
    }

    public static long a(double d0) {
        return MathHelper.d(d0 * 4096.0D);
    }

    public void track(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.addEntity(entity, 512, 2);
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                if (entitytrackerentry.b() != entityplayer) {
                    entitytrackerentry.updatePlayer(entityplayer);
                }
            }
        } else if (entity instanceof EntityFishingHook) {
            this.addEntity(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.addEntity(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntitySnowball) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityLlamaSpit) {
            this.addEntity(entity, 64, 10, false);
        } else if (entity instanceof EntityEnderPearl) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderSignal) {
            this.addEntity(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityThrownExpBottle) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworks) {
            this.addEntity(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.addEntity(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecartAbstract) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            this.addEntity(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityShulkerBullet) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityBat) {
            this.addEntity(entity, 80, 3, false);
        } else if (entity instanceof EntityEnderDragon) {
            this.addEntity(entity, 160, 3, true);
        } else if (entity instanceof IAnimal) {
            this.addEntity(entity, 80, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.addEntity(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.addEntity(entity, 160, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityArmorStand) {
            this.addEntity(entity, 160, 3, true);
        } else if (entity instanceof EntityExperienceOrb) {
            this.addEntity(entity, 160, 20, true);
        } else if (entity instanceof EntityAreaEffectCloud) {
            this.addEntity(entity, 160, Integer.MAX_VALUE, true);
        } else if (entity instanceof EntityEnderCrystal) {
            this.addEntity(entity, 256, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityEvokerFangs) {
            this.addEntity(entity, 160, 2, false);
        }

    }

    public void addEntity(Entity entity, int i, int j) {
        this.addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, int j, boolean flag) {
        try {
            if (this.trackedEntities.b(entity.getId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, this.trackingDistance, j, flag);

            this.c.add(entitytrackerentry);
            this.trackedEntities.a(entity.getId(), entitytrackerentry);
            entitytrackerentry.scanPlayers(this.world.players);
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");

            crashreportsystemdetails.a("Tracking range", (Object) (i + " blocks"));
            crashreportsystemdetails.a("Update interval", () -> {
                String s = "Once per " + j + " ticks";

                if (j == Integer.MAX_VALUE) {
                    s = "Maximum (" + s + ")";
                }

                return s;
            });
            entity.appendEntityCrashDetails(crashreportsystemdetails);
            ((EntityTrackerEntry) this.trackedEntities.get(entity.getId())).b().appendEntityCrashDetails(crashreport.a("Entity That Is Already Tracked"));

            try {
                throw new ReportedException(crashreport);
            } catch (ReportedException reportedexception) {
                EntityTracker.a.error("\"Silently\" catching entity tracking error.", reportedexception);
            }
        }

    }

    public void untrackEntity(Entity entity) {
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                entitytrackerentry.a(entityplayer);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) this.trackedEntities.d(entity.getId());

        if (entitytrackerentry1 != null) {
            this.c.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }

    }

    public void updatePlayers() {
        List<EntityPlayer> list = Lists.newArrayList();
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.track(this.world.players);
            if (entitytrackerentry.b) {
                Entity entity = entitytrackerentry.b();

                if (entity instanceof EntityPlayer) {
                    list.add((EntityPlayer) entity);
                }
            }
        }

        for (int i = 0; i < list.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) list.get(i);
            Iterator iterator1 = this.c.iterator();

            while (iterator1.hasNext()) {
                EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) iterator1.next();

                if (entitytrackerentry1.b() != entityplayer) {
                    entitytrackerentry1.updatePlayer(entityplayer);
                }
            }
        }

    }

    public void a(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            if (entitytrackerentry.b() == entityplayer) {
                entitytrackerentry.scanPlayers(this.world.players);
            } else {
                entitytrackerentry.updatePlayer(entityplayer);
            }
        }

    }

    public void a(Entity entity, Packet<?> packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntities.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcast(packet);
        }

    }

    public void sendPacketToEntity(Entity entity, Packet<?> packet) {
        EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) this.trackedEntities.get(entity.getId());

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcastIncludingSelf(packet);
        }

    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.clear(entityplayer);
        }

    }

    public void a(EntityPlayer entityplayer, Chunk chunk) {
        List<Entity> list = Lists.newArrayList();
        List<Entity> list1 = Lists.newArrayList();
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();
            Entity entity = entitytrackerentry.b();

            if (entity != entityplayer && entity.chunkX == chunk.locX && entity.chunkZ == chunk.locZ) {
                entitytrackerentry.updatePlayer(entityplayer);
                if (entity instanceof EntityInsentient && ((EntityInsentient) entity).getLeashHolder() != null) {
                    list.add(entity);
                }

                if (!entity.bP().isEmpty()) {
                    list1.add(entity);
                }
            }
        }

        Entity entity1;

        if (!list.isEmpty()) {
            iterator = list.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(entity1, ((EntityInsentient) entity1).getLeashHolder()));
            }
        }

        if (!list1.isEmpty()) {
            iterator = list1.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.playerConnection.sendPacket(new PacketPlayOutMount(entity1));
            }
        }

    }

    public void a(int i) {
        this.trackingDistance = (i - 1) * 16;
        Iterator iterator = this.c.iterator();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.a(this.trackingDistance);
        }

    }
}
