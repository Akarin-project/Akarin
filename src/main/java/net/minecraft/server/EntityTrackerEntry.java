package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerVelocityEvent;
// CraftBukkit end

public class EntityTrackerEntry {

    private static final Logger c = LogManager.getLogger();
    private final Entity tracker;
    private final int e;
    private int f;
    private final int g;
    private long xLoc;
    private long yLoc;
    private long zLoc;
    private int yRot;
    private int xRot;
    private int headYaw;
    private double n;
    private double o;
    private double p;
    public int a;
    private double q;
    private double r;
    private double s;
    private boolean isMoving;
    private final boolean u;
    private int v;
    private List<Entity> w = Collections.emptyList();
    private boolean x;
    private boolean y;
    public boolean b;
    public final Set<EntityPlayer> trackedPlayers = Sets.newHashSet();

    public EntityTrackerEntry(Entity entity, int i, int j, int k, boolean flag) {
        this.tracker = entity;
        this.e = i;
        this.f = j;
        this.g = k;
        this.u = flag;
        this.xLoc = EntityTracker.a(entity.locX);
        this.yLoc = EntityTracker.a(entity.locY);
        this.zLoc = EntityTracker.a(entity.locZ);
        this.yRot = MathHelper.d(entity.yaw * 256.0F / 360.0F);
        this.xRot = MathHelper.d(entity.pitch * 256.0F / 360.0F);
        this.headYaw = MathHelper.d(entity.getHeadRotation() * 256.0F / 360.0F);
        this.y = entity.onGround;
    }

    public boolean equals(Object object) {
        return object instanceof EntityTrackerEntry ? ((EntityTrackerEntry) object).tracker.getId() == this.tracker.getId() : false;
    }

    public int hashCode() {
        return this.tracker.getId();
    }

    public void track(List<EntityHuman> list) {
        this.b = false;
        if (!this.isMoving || this.tracker.d(this.q, this.r, this.s) > 16.0D) {
            this.q = this.tracker.locX;
            this.r = this.tracker.locY;
            this.s = this.tracker.locZ;
            this.isMoving = true;
            this.b = true;
            this.scanPlayers(list);
        }

        List<Entity> list1 = this.tracker.bP();

        if (!list1.equals(this.w)) {
            this.w = list1;
            this.broadcastIncludingSelf(new PacketPlayOutMount(this.tracker)); // CraftBukkit
        }

        // PAIL : rename
        if (this.tracker instanceof EntityItemFrame /*&& this.a % 10 == 0*/) { // CraftBukkit - Moved below, should always enter this block
            EntityItemFrame entityitemframe = (EntityItemFrame) this.tracker;
            ItemStack itemstack = entityitemframe.getItem();

            if (this.a % 10 == 0 && itemstack.getItem() instanceof ItemWorldMap) { // CraftBukkit - Moved this.a % 10 logic here so item frames do not enter the other blocks
                WorldMap worldmap = ItemWorldMap.getSavedMap(itemstack, this.tracker.world);
                Iterator iterator = this.trackedPlayers.iterator(); // CraftBukkit

                while (iterator.hasNext()) {
                    EntityHuman entityhuman = (EntityHuman) iterator.next();
                    EntityPlayer entityplayer = (EntityPlayer) entityhuman;

                    worldmap.a((EntityHuman) entityplayer, itemstack);
                    Packet<?> packet = ((ItemWorldMap) itemstack.getItem()).a(itemstack, this.tracker.world, (EntityHuman) entityplayer);

                    if (packet != null) {
                        entityplayer.playerConnection.sendPacket(packet);
                    }
                }
            }

            this.d();
        }

        if (this.a % this.g == 0 || this.tracker.impulse || this.tracker.getDataWatcher().a()) {
            int i;

            if (this.tracker.isPassenger()) {
                i = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                int j = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                boolean flag = Math.abs(i - this.yRot) >= 1 || Math.abs(j - this.xRot) >= 1;

                if (flag) {
                    this.broadcast(new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte) i, (byte) j, this.tracker.onGround));
                    this.yRot = i;
                    this.xRot = j;
                }

                this.xLoc = EntityTracker.a(this.tracker.locX);
                this.yLoc = EntityTracker.a(this.tracker.locY);
                this.zLoc = EntityTracker.a(this.tracker.locZ);
                this.d();
                this.x = true;
            } else {
                ++this.v;
                long k = EntityTracker.a(this.tracker.locX);
                long l = EntityTracker.a(this.tracker.locY);
                long i1 = EntityTracker.a(this.tracker.locZ);
                int j1 = MathHelper.d(this.tracker.yaw * 256.0F / 360.0F);
                int k1 = MathHelper.d(this.tracker.pitch * 256.0F / 360.0F);
                long l1 = k - this.xLoc;
                long i2 = l - this.yLoc;
                long j2 = i1 - this.zLoc;
                Packet<?> packet1 = null;
                boolean flag1 = l1 * l1 + i2 * i2 + j2 * j2 >= 128L || this.a % 60 == 0;
                boolean flag2 = Math.abs(j1 - this.yRot) >= 1 || Math.abs(k1 - this.xRot) >= 1;

                // CraftBukkit start - Code moved from below
                if (flag1) {
                    this.xLoc = k;
                    this.yLoc = l;
                    this.zLoc = i1;
                }

                if (flag2) {
                    this.yRot = j1;
                    this.xRot = k1;
                }
                // CraftBukkit end

                if (this.a > 0 || this.tracker instanceof EntityArrow) {
                    if (l1 >= -32768L && l1 < 32768L && i2 >= -32768L && i2 < 32768L && j2 >= -32768L && j2 < 32768L && this.v <= 400 && !this.x && this.y == this.tracker.onGround) {
                        if ((!flag1 || !flag2) && !(this.tracker instanceof EntityArrow)) {
                            if (flag1) {
                                packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMove(this.tracker.getId(), l1, i2, j2, this.tracker.onGround);
                            } else if (flag2) {
                                packet1 = new PacketPlayOutEntity.PacketPlayOutEntityLook(this.tracker.getId(), (byte) j1, (byte) k1, this.tracker.onGround);
                            }
                        } else {
                            packet1 = new PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(this.tracker.getId(), l1, i2, j2, (byte) j1, (byte) k1, this.tracker.onGround);
                        }
                    } else {
                        this.y = this.tracker.onGround;
                        this.v = 0;
                        // CraftBukkit start - Refresh list of who can see a player before sending teleport packet
                        if (this.tracker instanceof EntityPlayer) {
                            this.scanPlayers(new java.util.ArrayList(this.trackedPlayers));
                        }
                        // CraftBukkit end
                        this.c();
                        packet1 = new PacketPlayOutEntityTeleport(this.tracker);
                    }
                }

                boolean flag3 = this.u || this.tracker.impulse;

                if (this.tracker instanceof EntityLiving && ((EntityLiving) this.tracker).dc()) {
                    flag3 = true;
                }

                if (flag3 && this.a > 0) {
                    double d0 = this.tracker.motX - this.n;
                    double d1 = this.tracker.motY - this.o;
                    double d2 = this.tracker.motZ - this.p;
                    double d3 = 0.02D;
                    double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                    if (d4 > 4.0E-4D || d4 > 0.0D && this.tracker.motX == 0.0D && this.tracker.motY == 0.0D && this.tracker.motZ == 0.0D) {
                        this.n = this.tracker.motX;
                        this.o = this.tracker.motY;
                        this.p = this.tracker.motZ;
                        this.broadcast(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.n, this.o, this.p));
                    }
                }

                if (packet1 != null) {
                    this.broadcast((Packet) packet1);
                }

                this.d();
                /* CraftBukkit start - Code moved up
                if (flag1) {
                    this.xLoc = k;
                    this.yLoc = l;
                    this.zLoc = i1;
                }

                if (flag2) {
                    this.yRot = j1;
                    this.xRot = k1;
                }
                // CraftBukkit end */

                this.x = false;
            }

            i = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
            if (Math.abs(i - this.headYaw) >= 1) {
                this.broadcast(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) i));
                this.headYaw = i;
            }

            this.tracker.impulse = false;
        }

        ++this.a;
        if (this.tracker.velocityChanged) {
            // CraftBukkit start - Create PlayerVelocity event
            boolean cancelled = false;

            if (this.tracker instanceof EntityPlayer) {
                Player player = (Player) this.tracker.getBukkitEntity();
                org.bukkit.util.Vector velocity = player.getVelocity();

                PlayerVelocityEvent event = new PlayerVelocityEvent(player, velocity.clone());
                this.tracker.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    cancelled = true;
                } else if (!velocity.equals(event.getVelocity())) {
                    player.setVelocity(event.getVelocity());
                }
            }

            if (!cancelled) {
                this.broadcastIncludingSelf(new PacketPlayOutEntityVelocity(this.tracker));
            }
            // CraftBukkit end
            this.tracker.velocityChanged = false;
        }

    }

    private void d() {
        DataWatcher datawatcher = this.tracker.getDataWatcher();

        if (datawatcher.a()) {
            this.broadcastIncludingSelf(new PacketPlayOutEntityMetadata(this.tracker.getId(), datawatcher, false));
        }

        if (this.tracker instanceof EntityLiving) {
            AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
            Set<AttributeInstance> set = attributemapserver.getAttributes();

            if (!set.isEmpty()) {
                // CraftBukkit start - Send scaled max health
                if (this.tracker instanceof EntityPlayer) {
                    ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(set, false);
                }
                // CraftBukkit end
                this.broadcastIncludingSelf(new PacketPlayOutUpdateAttributes(this.tracker.getId(), set));
            }

            set.clear();
        }

    }

    public void broadcast(Packet<?> packet) {
        Iterator iterator = this.trackedPlayers.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            entityplayer.playerConnection.sendPacket(packet);
        }

    }

    public void broadcastIncludingSelf(Packet<?> packet) {
        this.broadcast(packet);
        if (this.tracker instanceof EntityPlayer) {
            ((EntityPlayer) this.tracker).playerConnection.sendPacket(packet);
        }

    }

    public void a() {
        Iterator iterator = this.trackedPlayers.iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            this.tracker.c(entityplayer);
            entityplayer.c(this.tracker);
        }

    }

    public void a(EntityPlayer entityplayer) {
        if (this.trackedPlayers.contains(entityplayer)) {
            this.tracker.c(entityplayer);
            entityplayer.c(this.tracker);
            this.trackedPlayers.remove(entityplayer);
        }

    }

    public void updatePlayer(EntityPlayer entityplayer) {
        if (entityplayer != this.tracker) {
            if (this.c(entityplayer)) {
                if (!this.trackedPlayers.contains(entityplayer) && (this.e(entityplayer) || this.tracker.attachedToPlayer)) {
                    // CraftBukkit start - respect vanish API
                    if (this.tracker instanceof EntityPlayer) {
                        Player player = ((EntityPlayer) this.tracker).getBukkitEntity();
                        if (!entityplayer.getBukkitEntity().canSee(player)) {
                            return;
                        }
                    }

                    entityplayer.removeQueue.remove(Integer.valueOf(this.tracker.getId()));
                    // CraftBukkit end
                    this.trackedPlayers.add(entityplayer);
                    Packet<?> packet = this.e();

                    entityplayer.playerConnection.sendPacket(packet);
                    if (!this.tracker.getDataWatcher().d()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityMetadata(this.tracker.getId(), this.tracker.getDataWatcher(), true));
                    }

                    boolean flag = this.u;

                    if (this.tracker instanceof EntityLiving) {
                        AttributeMapServer attributemapserver = (AttributeMapServer) ((EntityLiving) this.tracker).getAttributeMap();
                        Collection<AttributeInstance> collection = attributemapserver.c();

                        // CraftBukkit start - If sending own attributes send scaled health instead of current maximum health
                        if (this.tracker.getId() == entityplayer.getId()) {
                            ((EntityPlayer) this.tracker).getBukkitEntity().injectScaledMaxHealth(collection, false);
                        }
                        // CraftBukkit end

                        if (!collection.isEmpty()) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutUpdateAttributes(this.tracker.getId(), collection));
                        }

                        if (((EntityLiving) this.tracker).dc()) {
                            flag = true;
                        }
                    }

                    this.n = this.tracker.motX;
                    this.o = this.tracker.motY;
                    this.p = this.tracker.motZ;
                    if (flag && !(packet instanceof PacketPlayOutSpawnEntityLiving)) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityVelocity(this.tracker.getId(), this.tracker.motX, this.tracker.motY, this.tracker.motZ));
                    }

                    if (this.tracker instanceof EntityLiving) {
                        EnumItemSlot[] aenumitemslot = EnumItemSlot.values();
                        int i = aenumitemslot.length;

                        for (int j = 0; j < i; ++j) {
                            EnumItemSlot enumitemslot = aenumitemslot[j];
                            ItemStack itemstack = ((EntityLiving) this.tracker).getEquipment(enumitemslot);

                            if (!itemstack.isEmpty()) {
                                entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEquipment(this.tracker.getId(), enumitemslot, itemstack));
                            }
                        }
                    }

                    if (this.tracker instanceof EntityHuman) {
                        EntityHuman entityhuman = (EntityHuman) this.tracker;

                        if (entityhuman.isSleeping()) {
                            entityplayer.playerConnection.sendPacket(new PacketPlayOutBed(entityhuman, new BlockPosition(this.tracker)));
                        }
                    }

                    // CraftBukkit start - Fix for nonsensical head yaw
                    this.headYaw = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
                    this.broadcast(new PacketPlayOutEntityHeadRotation(this.tracker, (byte) headYaw));
                    // CraftBukkit end

                    if (this.tracker instanceof EntityLiving) {
                        EntityLiving entityliving = (EntityLiving) this.tracker;
                        Iterator iterator = entityliving.getEffects().iterator();

                        while (iterator.hasNext()) {
                            MobEffect mobeffect = (MobEffect) iterator.next();

                            entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityEffect(this.tracker.getId(), mobeffect));
                        }
                    }

                    if (!this.tracker.bP().isEmpty()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutMount(this.tracker));
                    }

                    if (this.tracker.isPassenger()) {
                        entityplayer.playerConnection.sendPacket(new PacketPlayOutMount(this.tracker.getVehicle()));
                    }

                    this.tracker.b(entityplayer);
                    entityplayer.d(this.tracker);
                }
            } else if (this.trackedPlayers.contains(entityplayer)) {
                this.trackedPlayers.remove(entityplayer);
                this.tracker.c(entityplayer);
                entityplayer.c(this.tracker);
            }

        }
    }

    public boolean c(EntityPlayer entityplayer) {
        double d0 = entityplayer.locX - (double) this.xLoc / 4096.0D;
        double d1 = entityplayer.locZ - (double) this.zLoc / 4096.0D;
        int i = Math.min(this.e, this.f);

        return d0 >= (double) (-i) && d0 <= (double) i && d1 >= (double) (-i) && d1 <= (double) i && this.tracker.a(entityplayer);
    }

    private boolean e(EntityPlayer entityplayer) {
        return entityplayer.getWorldServer().getPlayerChunkMap().a(entityplayer, this.tracker.chunkX, this.tracker.chunkZ);
    }

    public void scanPlayers(List<EntityHuman> list) {
        for (int i = 0; i < list.size(); ++i) {
            this.updatePlayer((EntityPlayer) list.get(i));
        }

    }

    private Packet<?> e() {
        if (this.tracker.dead) {
            // CraftBukkit start - Remove useless error spam, just return
            // EntityTrackerEntry.d.warn("Fetching addPacket for removed entity");
            return null;
            // CraftBukkit end
        }

        if (this.tracker instanceof EntityPlayer) {
            return new PacketPlayOutNamedEntitySpawn((EntityHuman) this.tracker);
        } else if (this.tracker instanceof IAnimal) {
            this.headYaw = MathHelper.d(this.tracker.getHeadRotation() * 256.0F / 360.0F);
            return new PacketPlayOutSpawnEntityLiving((EntityLiving) this.tracker);
        } else if (this.tracker instanceof EntityPainting) {
            return new PacketPlayOutSpawnEntityPainting((EntityPainting) this.tracker);
        } else if (this.tracker instanceof EntityItem) {
            return new PacketPlayOutSpawnEntity(this.tracker, 2, 1);
        } else if (this.tracker instanceof EntityMinecartAbstract) {
            EntityMinecartAbstract entityminecartabstract = (EntityMinecartAbstract) this.tracker;

            return new PacketPlayOutSpawnEntity(this.tracker, 10, entityminecartabstract.v().a());
        } else if (this.tracker instanceof EntityBoat) {
            return new PacketPlayOutSpawnEntity(this.tracker, 1);
        } else if (this.tracker instanceof EntityExperienceOrb) {
            return new PacketPlayOutSpawnEntityExperienceOrb((EntityExperienceOrb) this.tracker);
        } else if (this.tracker instanceof EntityFishingHook) {
            EntityHuman entityhuman = ((EntityFishingHook) this.tracker).i();

            return new PacketPlayOutSpawnEntity(this.tracker, 90, entityhuman == null ? this.tracker.getId() : entityhuman.getId());
        } else {
            Entity entity;

            if (this.tracker instanceof EntitySpectralArrow) {
                entity = ((EntitySpectralArrow) this.tracker).getShooter();
                return new PacketPlayOutSpawnEntity(this.tracker, 91, 1 + (entity == null ? this.tracker.getId() : entity.getId()));
            } else if (this.tracker instanceof EntityTippedArrow) {
                entity = ((EntityArrow) this.tracker).getShooter();
                return new PacketPlayOutSpawnEntity(this.tracker, 60, 1 + (entity == null ? this.tracker.getId() : entity.getId()));
            } else if (this.tracker instanceof EntitySnowball) {
                return new PacketPlayOutSpawnEntity(this.tracker, 61);
            } else if (this.tracker instanceof EntityThrownTrident) {
                entity = ((EntityArrow) this.tracker).getShooter();
                return new PacketPlayOutSpawnEntity(this.tracker, 94, 1 + (entity == null ? this.tracker.getId() : entity.getId()));
            } else if (this.tracker instanceof EntityLlamaSpit) {
                return new PacketPlayOutSpawnEntity(this.tracker, 68);
            } else if (this.tracker instanceof EntityPotion) {
                return new PacketPlayOutSpawnEntity(this.tracker, 73);
            } else if (this.tracker instanceof EntityThrownExpBottle) {
                return new PacketPlayOutSpawnEntity(this.tracker, 75);
            } else if (this.tracker instanceof EntityEnderPearl) {
                return new PacketPlayOutSpawnEntity(this.tracker, 65);
            } else if (this.tracker instanceof EntityEnderSignal) {
                return new PacketPlayOutSpawnEntity(this.tracker, 72);
            } else if (this.tracker instanceof EntityFireworks) {
                return new PacketPlayOutSpawnEntity(this.tracker, 76);
            } else if (this.tracker instanceof EntityFireball) {
                EntityFireball entityfireball = (EntityFireball) this.tracker;
                byte b0 = 63;

                if (this.tracker instanceof EntitySmallFireball) {
                    b0 = 64;
                } else if (this.tracker instanceof EntityDragonFireball) {
                    b0 = 93;
                } else if (this.tracker instanceof EntityWitherSkull) {
                    b0 = 66;
                }

                PacketPlayOutSpawnEntity packetplayoutspawnentity;

                if (entityfireball.shooter == null) {
                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, 0);
                } else {
                    packetplayoutspawnentity = new PacketPlayOutSpawnEntity(this.tracker, b0, ((EntityFireball) this.tracker).shooter.getId());
                }

                packetplayoutspawnentity.a((int) (entityfireball.dirX * 8000.0D));
                packetplayoutspawnentity.b((int) (entityfireball.dirY * 8000.0D));
                packetplayoutspawnentity.c((int) (entityfireball.dirZ * 8000.0D));
                return packetplayoutspawnentity;
            } else if (this.tracker instanceof EntityShulkerBullet) {
                PacketPlayOutSpawnEntity packetplayoutspawnentity1 = new PacketPlayOutSpawnEntity(this.tracker, 67, 0);

                packetplayoutspawnentity1.a((int) (this.tracker.motX * 8000.0D));
                packetplayoutspawnentity1.b((int) (this.tracker.motY * 8000.0D));
                packetplayoutspawnentity1.c((int) (this.tracker.motZ * 8000.0D));
                return packetplayoutspawnentity1;
            } else if (this.tracker instanceof EntityEgg) {
                return new PacketPlayOutSpawnEntity(this.tracker, 62);
            } else if (this.tracker instanceof EntityEvokerFangs) {
                return new PacketPlayOutSpawnEntity(this.tracker, 79);
            } else if (this.tracker instanceof EntityTNTPrimed) {
                return new PacketPlayOutSpawnEntity(this.tracker, 50);
            } else if (this.tracker instanceof EntityEnderCrystal) {
                return new PacketPlayOutSpawnEntity(this.tracker, 51);
            } else if (this.tracker instanceof EntityFallingBlock) {
                EntityFallingBlock entityfallingblock = (EntityFallingBlock) this.tracker;

                return new PacketPlayOutSpawnEntity(this.tracker, 70, Block.getCombinedId(entityfallingblock.getBlock()));
            } else if (this.tracker instanceof EntityArmorStand) {
                return new PacketPlayOutSpawnEntity(this.tracker, 78);
            } else if (this.tracker instanceof EntityItemFrame) {
                EntityItemFrame entityitemframe = (EntityItemFrame) this.tracker;

                return new PacketPlayOutSpawnEntity(this.tracker, 71, entityitemframe.direction.a(), entityitemframe.getBlockPosition());
            } else if (this.tracker instanceof EntityLeash) {
                EntityLeash entityleash = (EntityLeash) this.tracker;

                return new PacketPlayOutSpawnEntity(this.tracker, 77, 0, entityleash.getBlockPosition());
            } else if (this.tracker instanceof EntityAreaEffectCloud) {
                return new PacketPlayOutSpawnEntity(this.tracker, 3);
            } else {
                throw new IllegalArgumentException("Don't know how to add " + this.tracker.getClass() + "!");
            }
        }
    }

    public void clear(EntityPlayer entityplayer) {
        if (this.trackedPlayers.contains(entityplayer)) {
            this.trackedPlayers.remove(entityplayer);
            this.tracker.c(entityplayer);
            entityplayer.c(this.tracker);
        }

    }

    public Entity b() {
        return this.tracker;
    }

    public void a(int i) {
        this.f = i;
    }

    public void c() {
        this.isMoving = false;
    }
}
