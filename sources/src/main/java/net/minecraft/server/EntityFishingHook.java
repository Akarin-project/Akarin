package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.entity.Fish;
import org.bukkit.event.player.PlayerFishEvent;
// CraftBukkit end

public class EntityFishingHook extends Entity {

    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.b);
    private boolean isInGround;
    private int d;
    public EntityHuman owner;
    private int f;
    private int g;
    private int h;
    private int at;
    private float au;
    public Entity hooked;
    private EntityFishingHook.HookState av;
    private int aw;
    private int ax;

    public EntityFishingHook(World world, EntityHuman entityhuman) {
        super(world);
        this.av = EntityFishingHook.HookState.FLYING;
        this.a(entityhuman);
        this.n();
    }

    private void a(EntityHuman entityhuman) {
        this.setSize(0.25F, 0.25F);
        this.ah = true;
        this.owner = entityhuman;
        this.owner.hookedFish = this;
    }

    public void a(int i) {
        this.ax = i;
    }

    public void c(int i) {
        this.aw = i;
    }

    private void n() {
        float f = this.owner.lastPitch + (this.owner.pitch - this.owner.lastPitch);
        float f1 = this.owner.lastYaw + (this.owner.yaw - this.owner.lastYaw);
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        double d0 = this.owner.lastX + (this.owner.locX - this.owner.lastX) - (double) f3 * 0.3D;
        double d1 = this.owner.lastY + (this.owner.locY - this.owner.lastY) + (double) this.owner.getHeadHeight();
        double d2 = this.owner.lastZ + (this.owner.locZ - this.owner.lastZ) - (double) f2 * 0.3D;

        this.setPositionRotation(d0, d1, d2, f1, f);
        this.motX = (double) (-f3);
        this.motY = (double) MathHelper.a(-(f5 / f4), -5.0F, 5.0F);
        this.motZ = (double) (-f2);
        float f6 = MathHelper.sqrt(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);

        this.motX *= 0.6D / (double) f6 + 0.5D + this.random.nextGaussian() * 0.0045D;
        this.motY *= 0.6D / (double) f6 + 0.5D + this.random.nextGaussian() * 0.0045D;
        this.motZ *= 0.6D / (double) f6 + 0.5D + this.random.nextGaussian() * 0.0045D;
        float f7 = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.c(this.motY, (double) f7) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    protected void i() {
        this.getDataWatcher().register(EntityFishingHook.b, Integer.valueOf(0));
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityFishingHook.b.equals(datawatcherobject)) {
            int i = ((Integer) this.getDataWatcher().get(EntityFishingHook.b)).intValue();

            this.hooked = i > 0 ? this.world.getEntity(i - 1) : null;
        }

        super.a(datawatcherobject);
    }

    public void B_() {
        super.B_();
        if (this.owner == null) {
            this.die();
        } else if (this.world.isClientSide || !this.p()) {
            if (this.isInGround) {
                ++this.d;
                if (this.d >= 1200) {
                    this.die();
                    return;
                }
            }

            float f = 0.0F;
            BlockPosition blockposition = new BlockPosition(this);
            IBlockData iblockdata = this.world.getType(blockposition);

            if (iblockdata.getMaterial() == Material.WATER) {
                f = BlockFluids.g(iblockdata, this.world, blockposition);
            }

            double d0;

            if (this.av == EntityFishingHook.HookState.FLYING) {
                if (this.hooked != null) {
                    this.motX = 0.0D;
                    this.motY = 0.0D;
                    this.motZ = 0.0D;
                    this.av = EntityFishingHook.HookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (f > 0.0F) {
                    this.motX *= 0.3D;
                    this.motY *= 0.2D;
                    this.motZ *= 0.3D;
                    this.av = EntityFishingHook.HookState.BOBBING;
                    return;
                }

                if (!this.world.isClientSide) {
                    this.r();
                }

                if (!this.isInGround && !this.onGround && !this.positionChanged) {
                    ++this.f;
                } else {
                    this.f = 0;
                    this.motX = 0.0D;
                    this.motY = 0.0D;
                    this.motZ = 0.0D;
                }
            } else {
                if (this.av == EntityFishingHook.HookState.HOOKED_IN_ENTITY) {
                    if (this.hooked != null) {
                        if (this.hooked.dead) {
                            this.hooked = null;
                            this.av = EntityFishingHook.HookState.FLYING;
                        } else {
                            this.locX = this.hooked.locX;
                            double d1 = (double) this.hooked.length;

                            this.locY = this.hooked.getBoundingBox().b + d1 * 0.8D;
                            this.locZ = this.hooked.locZ;
                            this.setPosition(this.locX, this.locY, this.locZ);
							if (this.ak) this.die(); // NeonPaper - Prevent going through portals
                        }
                    }

                    return;
                }

                if (this.av == EntityFishingHook.HookState.BOBBING) {
                    this.motX *= 0.9D;
                    this.motZ *= 0.9D;
                    d0 = this.locY + this.motY - (double) blockposition.getY() - (double) f;
                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.motY -= d0 * (double) this.random.nextFloat() * 0.2D;
                    if (!this.world.isClientSide && f > 0.0F) {
                        this.a(blockposition);
                    }
                }
            }

            if (iblockdata.getMaterial() != Material.WATER) {
                this.motY -= 0.03D;
            }

            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
            this.q();
            d0 = 0.92D;
            this.motX *= 0.92D;
            this.motY *= 0.92D;
            this.motZ *= 0.92D;
            this.setPosition(this.locX, this.locY, this.locZ);

            // Paper start - These shouldn't be going through portals
            if (this.inPortal()) {
                this.die();
            }
            // Paper end
        }
    }

    private boolean p() {
        ItemStack itemstack = this.owner.getItemInMainHand();
        ItemStack itemstack1 = this.owner.getItemInOffHand();
        boolean flag = itemstack.getItem() == Items.FISHING_ROD;
        boolean flag1 = itemstack1.getItem() == Items.FISHING_ROD;

        if (!this.owner.dead && this.owner.isAlive() && (flag || flag1) && this.h(this.owner) <= 1024.0D) {
            return false;
        } else {
            this.die();
            return true;
        }
    }

    private void q() {
        float f = MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ);

        this.yaw = (float) (MathHelper.c(this.motX, this.motZ) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.c(this.motY, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
            ;
        }

        while (this.pitch - this.lastPitch >= 180.0F) {
            this.lastPitch += 360.0F;
        }

        while (this.yaw - this.lastYaw < -180.0F) {
            this.lastYaw -= 360.0F;
        }

        while (this.yaw - this.lastYaw >= 180.0F) {
            this.lastYaw += 360.0F;
        }

        this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
        this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
    }

    private void r() {
        Vec3D vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        Vec3D vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
        MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true, false);

        vec3d = new Vec3D(this.locX, this.locY, this.locZ);
        vec3d1 = new Vec3D(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);

        // Paper start - Call ProjectileCollideEvent
        if (movingobjectposition != null && movingobjectposition.entity != null) {
            com.destroystokyo.paper.event.entity.ProjectileCollideEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileCollideEvent(this, movingobjectposition);
            if (event.isCancelled()) {
                movingobjectposition = null;
            }
        }
        // Paper end

        if (movingobjectposition != null) {
            vec3d1 = new Vec3D(movingobjectposition.pos.x, movingobjectposition.pos.y, movingobjectposition.pos.z);
        }

        Entity entity = null;
        List list = this.world.getEntities(this, this.getBoundingBox().b(this.motX, this.motY, this.motZ).g(1.0D));
        double d0 = 0.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity1 = (Entity) iterator.next();

            if (this.a(entity1) && (entity1 != this.owner || this.f >= 5)) {
                AxisAlignedBB axisalignedbb = entity1.getBoundingBox().g(0.30000001192092896D);
                MovingObjectPosition movingobjectposition1 = axisalignedbb.b(vec3d, vec3d1);

                if (movingobjectposition1 != null) {
                    double d1 = vec3d.distanceSquared(movingobjectposition1.pos);

                    if (d1 < d0 || d0 == 0.0D) {
                        entity = entity1;
                        d0 = d1;
                    }
                }
            }
        }

        if (entity != null) {
            movingobjectposition = new MovingObjectPosition(entity);
        }

        if (movingobjectposition != null && movingobjectposition.type != MovingObjectPosition.EnumMovingObjectType.MISS) {
            org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this, movingobjectposition); // Craftbukkit - Call event
            if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                this.hooked = movingobjectposition.entity;
                this.s();
            } else {
                this.isInGround = true;
            }
        }

    }

    private void s() {
        this.getDataWatcher().set(EntityFishingHook.b, Integer.valueOf(this.hooked.getId() + 1));
    }

    private void a(BlockPosition blockposition) {
        WorldServer worldserver = (WorldServer) this.world;
        int i = 1;
        BlockPosition blockposition1 = blockposition.up();

        if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(blockposition1)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.world.h(blockposition1)) {
            --i;
        }

        if (this.g > 0) {
            --this.g;
            if (this.g <= 0) {
                this.h = 0;
                this.at = 0;
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (Fish) this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);
                // CraftBukkit end
            } else {
                this.motY -= 0.2D * (double) this.random.nextFloat() * (double) this.random.nextFloat();
            }
        } else {
            float f;
            float f1;
            float f2;
            double d0;
            double d1;
            double d2;
            Block block;

            if (this.at > 0) {
                this.at -= i;
                if (this.at > 0) {
                    this.au = (float) ((double) this.au + this.random.nextGaussian() * 4.0D);
                    f = this.au * 0.017453292F;
                    f1 = MathHelper.sin(f);
                    f2 = MathHelper.cos(f);
                    d0 = this.locX + (double) (f1 * (float) this.at * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.getBoundingBox().b) + 1.0F);
                    d2 = this.locZ + (double) (f2 * (float) this.at * 0.1F);
                    block = worldserver.getType(new BlockPosition(d0, d1 - 1.0D, d2)).getBlock();
                    if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                        if (this.random.nextFloat() < 0.15F) {
                            worldserver.a(EnumParticle.WATER_BUBBLE, d0, d1 - 0.10000000149011612D, d2, 1, (double) f1, 0.1D, (double) f2, 0.0D, new int[0]);
                        }

                        float f3 = f1 * 0.04F;
                        float f4 = f2 * 0.04F;

                        worldserver.a(EnumParticle.WATER_WAKE, d0, d1, d2, 0, (double) f4, 0.01D, (double) (-f3), 1.0D, new int[0]);
                        worldserver.a(EnumParticle.WATER_WAKE, d0, d1, d2, 0, (double) (-f4), 0.01D, (double) f3, 1.0D, new int[0]);
                    }
                } else {
                    // CraftBukkit start
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (Fish) this.getBukkitEntity(), PlayerFishEvent.State.BITE);
                    this.world.getServer().getPluginManager().callEvent(playerFishEvent);
                    if (playerFishEvent.isCancelled()) {
                        return;
                    }
                    // CraftBukkit end
                    this.motY = (double) (-0.4F * MathHelper.a(this.random, 0.6F, 1.0F));
                    this.a(SoundEffects.K, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.getBoundingBox().b + 0.5D;

                    worldserver.a(EnumParticle.WATER_BUBBLE, this.locX, d3, this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
                    worldserver.a(EnumParticle.WATER_WAKE, this.locX, d3, this.locZ, (int) (1.0F + this.width * 20.0F), (double) this.width, 0.0D, (double) this.width, 0.20000000298023224D, new int[0]);
                    this.g = MathHelper.nextInt(this.random, 20, 40);
                }
            } else if (this.h > 0) {
                this.h -= i;
                f = 0.15F;
                if (this.h < 20) {
                    f = (float) ((double) f + (double) (20 - this.h) * 0.05D);
                } else if (this.h < 40) {
                    f = (float) ((double) f + (double) (40 - this.h) * 0.02D);
                } else if (this.h < 60) {
                    f = (float) ((double) f + (double) (60 - this.h) * 0.01D);
                }

                if (this.random.nextFloat() < f) {
                    f1 = MathHelper.a(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f2 = MathHelper.a(this.random, 25.0F, 60.0F);
                    d0 = this.locX + (double) (MathHelper.sin(f1) * f2 * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.getBoundingBox().b) + 1.0F);
                    d2 = this.locZ + (double) (MathHelper.cos(f1) * f2 * 0.1F);
                    block = worldserver.getType(new BlockPosition((int) d0, (int) d1 - 1, (int) d2)).getBlock();
                    if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                        worldserver.a(EnumParticle.WATER_SPLASH, d0, d1, d2, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D, new int[0]);
                    }
                }

                if (this.h <= 0) {
                    this.au = MathHelper.a(this.random, 0.0F, 360.0F);
                    this.at = MathHelper.nextInt(this.random, 20, 80);
                }
            } else {
                this.h = MathHelper.nextInt(this.random, world.paperConfig.fishingMinTicks, world.paperConfig.fishingMaxTicks); // Paper
                this.h -= this.ax * 20 * 5;
            }
        }

    }

    protected boolean a(Entity entity) {
        return entity.isInteractable() || entity instanceof EntityItem;
    }

    public void b(NBTTagCompound nbttagcompound) {}

    public void a(NBTTagCompound nbttagcompound) {}

    public int j() {
        if (!this.world.isClientSide && this.owner != null) {
            int i = 0;

            if (this.hooked != null) {
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), this.hooked.getBukkitEntity(), (Fish) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                this.k();
                this.world.broadcastEntityEffect(this, (byte) 31);
                i = this.hooked instanceof EntityItem ? 3 : 5;
            } else if (this.g > 0) {
                LootTableInfo.a loottableinfo_a = new LootTableInfo.a((WorldServer) this.world);

                loottableinfo_a.a((float) this.aw + this.owner.du());
                Iterator iterator = this.world.getLootTableRegistry().a(LootTables.aA).a(this.random, loottableinfo_a.a()).iterator();

                while (iterator.hasNext()) {
                    ItemStack itemstack = (ItemStack) iterator.next();
                    EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack);
                    // CraftBukkit start
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), entityitem.getBukkitEntity(), (Fish) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
                    playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
                    this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                    if (playerFishEvent.isCancelled()) {
                        return 0;
                    }
                    // CraftBukkit end
                    double d0 = this.owner.locX - this.locX;
                    double d1 = this.owner.locY - this.locY;
                    double d2 = this.owner.locZ - this.locZ;
                    double d3 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                    double d4 = 0.1D;

                    entityitem.motX = d0 * 0.1D;
                    entityitem.motY = d1 * 0.1D + (double) MathHelper.sqrt(d3) * 0.08D;
                    entityitem.motZ = d2 * 0.1D;
                    this.world.addEntity(entityitem);
                    // CraftBukkit start - this.random.nextInt(6) + 1 -> playerFishEvent.getExpToDrop()
                    if (playerFishEvent.getExpToDrop() > 0) {
                        this.owner.world.addEntity(new EntityExperienceOrb(this.owner.world, this.owner.locX, this.owner.locY + 0.5D, this.owner.locZ + 0.5D, playerFishEvent.getExpToDrop(), org.bukkit.entity.ExperienceOrb.SpawnReason.FISHING, this.owner, this)); // Paper
                    }
                    // CraftBukkit end
                    Item item = itemstack.getItem();

                    if (item == Items.FISH || item == Items.COOKED_FISH) {
                        this.owner.a(StatisticList.E, 1);
                    }
                }

                i = 1;
            }

            if (this.isInGround) {
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (Fish) this.getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                i = 2;
            }
            // CraftBukkit start
            if (i == 0) {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (Fish) this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);
                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
            }
            // CraftBukkit end

            this.die();
            return i;
        } else {
            return 0;
        }
    }

    protected void k() {
        if (this.owner != null) {
            double d0 = this.owner.locX - this.locX;
            double d1 = this.owner.locY - this.locY;
            double d2 = this.owner.locZ - this.locZ;
            double d3 = 0.1D;

            this.hooked.motX += d0 * 0.1D;
            this.hooked.motY += d1 * 0.1D;
            this.hooked.motZ += d2 * 0.1D;
        }
    }

    protected boolean playStepSound() {
        return false;
    }

    public void die() {
        super.die();
        if (this.owner != null) {
            this.owner.hookedFish = null;
        }

    }

    public EntityHuman l() {
        return this.owner;
    }

    static enum HookState {

        FLYING, HOOKED_IN_ENTITY, BOBBING;

        private HookState() {}
    }
}
