package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.googlecode.concurentlocks.ReentrantReadWriteUpdateLock;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import javax.annotation.concurrent.ThreadSafe;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Akarin Changes Note
 * 1) Made collections and entry access thread-safe (safety issue)
 */
@ThreadSafe // Akarin
public class EntityTracker {

    private static final Logger a = LogManager.getLogger();
    private final WorldServer world;
    private final Set<EntityTrackerEntry> c = Sets.newHashSet();
    public final ReentrantReadWriteUpdateLock entriesLock = new ReentrantReadWriteUpdateLock(); // Akarin - add lock
    public final IntHashMap<EntityTrackerEntry> trackedEntities = new IntHashMap();
    private int e;

    public EntityTracker(WorldServer worldserver) {
        this.world = worldserver;
        this.e = PlayerChunkMap.getFurthestViewableBlock(worldserver.spigotConfig.viewDistance); // Spigot
    }

    public static long a(double d0) {
        return MathHelper.d(d0 * 4096.0D);
    }

    public void track(Entity entity) {
        if (entity instanceof EntityPlayer) {
            this.addEntity(entity, 512, 2);
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();
            // entriesLock.writeLock().lock(); // Akarin - locked in EntityPlayer

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                if (entitytrackerentry.b() != entityplayer) {
                    entitytrackerentry.updatePlayer(entityplayer);
                }
            }
            // entriesLock.writeLock().unlock(); // Akarin - locked in EntityPlayer
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
            this.addEntity(entity, 160, 10, true); // CraftBukkit
        } else if (entity instanceof EntityEnderCrystal) {
            this.addEntity(entity, 256, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityEvokerFangs) {
            this.addEntity(entity, 160, 2, false);
        }

    }

    public void addEntity(Entity entity, int i, int j) {
        this.addEntity(entity, i, j, false);
    }

    public void addEntity(Entity entity, int i, final int j, boolean flag) {
        org.spigotmc.AsyncCatcher.catchOp( "entity track"); // Spigot
        i = org.spigotmc.TrackingRange.getEntityTrackingRange(entity, i); // Spigot
        try {
            // entriesLock.writeLock().lock(); // Akarin - locked from track method
            if (this.trackedEntities.b(entity.getId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entitytrackerentry = new EntityTrackerEntry(entity, i, this.e, j, flag);

            this.c.add(entitytrackerentry);

            this.trackedEntities.a(entity.getId(), entitytrackerentry);
            entitytrackerentry.scanPlayers(this.world.players);
            // entriesLock.writeLock().unlock(); // Akarin - locked from track method
        } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.a(throwable, "Adding entity to track");
            CrashReportSystemDetails crashreportsystemdetails = crashreport.a("Entity To Track");

            crashreportsystemdetails.a("Tracking range", i + " blocks");
            final int finalI = i; // CraftBukkit - fix decompile error
            crashreportsystemdetails.a("Update interval", new CrashReportCallable() {
                public String a() throws Exception {
                    String s = "Once per " + finalI + " ticks"; // CraftBukkit

                    if (finalI == Integer.MAX_VALUE) { // CraftBukkit
                        s = "Maximum (" + s + ")";
                    }

                    return s;
                }

                @Override
                public Object call() throws Exception {
                    return this.a();
                }
            });
            entity.appendEntityCrashDetails(crashreportsystemdetails);
            this.trackedEntities.get(entity.getId()).b().appendEntityCrashDetails(crashreport.a("Entity That Is Already Tracked"));

            try {
                throw new ReportedException(crashreport);
            } catch (ReportedException reportedexception) {
                EntityTracker.a.error("\"Silently\" catching entity tracking error.", reportedexception);
            }
        }

    }

    public void untrackEntity(Entity entity) {
        org.spigotmc.AsyncCatcher.catchOp( "entity untrack"); // Spigot
        entriesLock.writeLock().lock(); // Akarin
        if (entity instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entity;
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

                entitytrackerentry.a(entityplayer);
            }
        }

        EntityTrackerEntry entitytrackerentry1 = this.trackedEntities.d(entity.getId());

        if (entitytrackerentry1 != null) {
            this.c.remove(entitytrackerentry1);
            entitytrackerentry1.a();
        }
        entriesLock.writeLock().unlock(); // Akarin
    }

    public void updatePlayers() {
        ArrayList arraylist = Lists.newArrayList();
        Iterator iterator = this.c.iterator();
        world.timings.tracker1.startTiming(); // Spigot
        entriesLock.writeLock().lock(); // Akarin
        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.track(this.world.players);
            if (entitytrackerentry.b) {
                Entity entity = entitytrackerentry.b();

                if (entity instanceof EntityPlayer) {
                    arraylist.add(entity);
                }
            }
        }
        world.timings.tracker1.stopTiming(); // Spigot

        world.timings.tracker2.startTiming(); // Spigot
        for (int i = 0; i < arraylist.size(); ++i) {
            EntityPlayer entityplayer = (EntityPlayer) arraylist.get(i);
            Iterator iterator1 = this.c.iterator();

            while (iterator1.hasNext()) {
                EntityTrackerEntry entitytrackerentry1 = (EntityTrackerEntry) iterator1.next();

                if (entitytrackerentry1.b() != entityplayer) {
                    entitytrackerentry1.updatePlayer(entityplayer);
                }
            }
        }
        entriesLock.writeLock().unlock(); // Akarin
        world.timings.tracker2.stopTiming(); // Spigot

    }

    public void a(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();
        entriesLock.writeLock().lock(); // Akarin

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            if (entitytrackerentry.b() == entityplayer) {
                entitytrackerentry.scanPlayers(this.world.players);
            } else {
                entitytrackerentry.updatePlayer(entityplayer);
            }
        }
        entriesLock.writeLock().unlock(); // Akarin
    }

    public void a(Entity entity, Packet<?> packet) {
        entriesLock.readLock().lock(); // Akarin
        EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
        entriesLock.readLock().unlock(); // Akarin

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcast(packet);
        }

    }

    public void sendPacketToEntity(Entity entity, Packet<?> packet) {
        entriesLock.readLock().lock(); // Akarin
        EntityTrackerEntry entitytrackerentry = this.trackedEntities.get(entity.getId());
        entriesLock.readLock().unlock(); // Akarin

        if (entitytrackerentry != null) {
            entitytrackerentry.broadcastIncludingSelf(packet);
        }

    }

    public void untrackPlayer(EntityPlayer entityplayer) {
        Iterator iterator = this.c.iterator();
        entriesLock.writeLock().lock();

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.clear(entityplayer);
        }
        entriesLock.writeLock().unlock();
    }

    public void a(EntityPlayer entityplayer, Chunk chunk) {
        ArrayList arraylist = Lists.newArrayList();
        ArrayList arraylist1 = Lists.newArrayList();
        Iterator iterator = this.c.iterator();
        entriesLock.writeLock().lock(); // Akarin

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();
            Entity entity = entitytrackerentry.b();

            if (entity != entityplayer && entity.ab == chunk.locX && entity.ad == chunk.locZ) {
                entitytrackerentry.updatePlayer(entityplayer);
                if (entity instanceof EntityInsentient && ((EntityInsentient) entity).getLeashHolder() != null) {
                    arraylist.add(entity);
                }

                if (!entity.bF().isEmpty()) {
                    arraylist1.add(entity);
                }
            }
        }
        entriesLock.writeLock().unlock(); // Akarin

        Entity entity1;

        if (!arraylist.isEmpty()) {
            iterator = arraylist.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(entity1, ((EntityInsentient) entity1).getLeashHolder()));
            }
        }

        if (!arraylist1.isEmpty()) {
            iterator = arraylist1.iterator();

            while (iterator.hasNext()) {
                entity1 = (Entity) iterator.next();
                entityplayer.playerConnection.sendPacket(new PacketPlayOutMount(entity1));
            }
        }

    }

    public void a(int i) {
        this.e = (i - 1) * 16;
        Iterator iterator = this.c.iterator();
        entriesLock.readLock().lock(); // Akarin

        while (iterator.hasNext()) {
            EntityTrackerEntry entitytrackerentry = (EntityTrackerEntry) iterator.next();

            entitytrackerentry.a(this.e);
        }
        entriesLock.readLock().unlock(); // Akarin
    }
}
