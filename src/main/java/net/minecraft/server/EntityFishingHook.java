package net.minecraft.server;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.entity.Player;
import org.bukkit.entity.FishHook;
import org.bukkit.event.player.PlayerFishEvent;
// CraftBukkit end

public class EntityFishingHook extends Entity {

    private static final DataWatcherObject<Integer> c = DataWatcher.a(EntityFishingHook.class, DataWatcherRegistry.b);
    private boolean isInGround;
    private int e;
    public EntityHuman owner;
    private int g;
    private int ar;
    private int as;
    private int at;
    private float au;
    public Entity hooked;
    private EntityFishingHook.HookState av;
    private final int aw;
    private final int ax;

    private EntityFishingHook(World world, EntityHuman entityhuman, int i, int j) {
        super(EntityTypes.FISHING_BOBBER, world);
        this.av = EntityFishingHook.HookState.FLYING;
        this.af = true;
        this.owner = entityhuman;
        this.owner.hookedFish = this;
        this.aw = Math.max(0, i);
        this.ax = Math.max(0, j);
    }

    public EntityFishingHook(EntityHuman entityhuman, World world, int i, int j) {
        this(world, entityhuman, i, j);
        float f = this.owner.pitch;
        float f1 = this.owner.yaw;
        float f2 = MathHelper.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = MathHelper.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -MathHelper.cos(-f * 0.017453292F);
        float f5 = MathHelper.sin(-f * 0.017453292F);
        double d0 = this.owner.locX - (double) f3 * 0.3D;
        double d1 = this.owner.locY + (double) this.owner.getHeadHeight();
        double d2 = this.owner.locZ - (double) f2 * 0.3D;

        this.setPositionRotation(d0, d1, d2, f1, f);
        Vec3D vec3d = new Vec3D((double) (-f3), (double) MathHelper.a(-(f5 / f4), -5.0F, 5.0F), (double) (-f2));
        double d3 = vec3d.f();

        vec3d = vec3d.d(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
        this.setMot(vec3d);
        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);
        this.pitch = (float) (MathHelper.d(vec3d.y, (double) MathHelper.sqrt(b(vec3d))) * 57.2957763671875D);
        this.lastYaw = this.yaw;
        this.lastPitch = this.pitch;
    }

    @Override
    protected void initDatawatcher() {
        this.getDataWatcher().register(EntityFishingHook.c, 0);
    }

    @Override
    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityFishingHook.c.equals(datawatcherobject)) {
            int i = (Integer) this.getDataWatcher().get(EntityFishingHook.c);

            this.hooked = i > 0 ? this.world.getEntity(i - 1) : null;
        }

        super.a(datawatcherobject);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.owner == null) {
            this.die();
        } else if (this.world.isClientSide || !this.k()) {
            if (this.isInGround) {
                ++this.e;
                if (this.e >= 1200) {
                    this.die();
                    return;
                }
            }

            float f = 0.0F;
            BlockPosition blockposition = new BlockPosition(this);
            Fluid fluid = this.world.getFluid(blockposition);

            if (fluid.a(TagsFluid.WATER)) {
                f = fluid.getHeight(this.world, blockposition);
            }

            if (this.av == EntityFishingHook.HookState.FLYING) {
                if (this.hooked != null) {
                    this.setMot(Vec3D.a);
                    this.av = EntityFishingHook.HookState.HOOKED_IN_ENTITY;
                    return;
                }

                if (f > 0.0F) {
                    this.setMot(this.getMot().d(0.3D, 0.2D, 0.3D));
                    this.av = EntityFishingHook.HookState.BOBBING;
                    return;
                }

                if (!this.world.isClientSide) {
                    this.m();
                }

                if (!this.isInGround && !this.onGround && !this.positionChanged) {
                    ++this.g;
                } else {
                    this.g = 0;
                    this.setMot(Vec3D.a);
                }
            } else {
                if (this.av == EntityFishingHook.HookState.HOOKED_IN_ENTITY) {
                    if (this.hooked != null) {
                        if (this.hooked.dead) {
                            this.hooked = null;
                            this.av = EntityFishingHook.HookState.FLYING;
                        } else {
                            this.locX = this.hooked.locX;
                            this.locY = this.hooked.getBoundingBox().minY + (double) this.hooked.getHeight() * 0.8D;
                            this.locZ = this.hooked.locZ;
                            this.setPosition(this.locX, this.locY, this.locZ);
                        }
                    }

                    return;
                }

                if (this.av == EntityFishingHook.HookState.BOBBING) {
                    Vec3D vec3d = this.getMot();
                    double d0 = this.locY + vec3d.y - (double) blockposition.getY() - (double) f;

                    if (Math.abs(d0) < 0.01D) {
                        d0 += Math.signum(d0) * 0.1D;
                    }

                    this.setMot(vec3d.x * 0.9D, vec3d.y - d0 * (double) this.random.nextFloat() * 0.2D, vec3d.z * 0.9D);
                    if (!this.world.isClientSide && f > 0.0F) {
                        this.a(blockposition);
                    }
                }
            }

            if (!fluid.a(TagsFluid.WATER)) {
                this.setMot(this.getMot().add(0.0D, -0.03D, 0.0D));
            }

            this.move(EnumMoveType.SELF, this.getMot());
            this.l();
            double d1 = 0.92D;

            this.setMot(this.getMot().a(0.92D));
            this.setPosition(this.locX, this.locY, this.locZ);

            // Paper start - These shouldn't be going through portals
            if (this.inPortal()) {
                this.die();
            }
            // Paper end
        }
    }

    private boolean k() {
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

    private void l() {
        Vec3D vec3d = this.getMot();
        float f = MathHelper.sqrt(b(vec3d));

        this.yaw = (float) (MathHelper.d(vec3d.x, vec3d.z) * 57.2957763671875D);

        for (this.pitch = (float) (MathHelper.d(vec3d.y, (double) f) * 57.2957763671875D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
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

        this.pitch = MathHelper.g(0.2F, this.lastPitch, this.pitch);
        this.yaw = MathHelper.g(0.2F, this.lastYaw, this.yaw);
    }

    private void m() {
        MovingObjectPosition movingobjectposition = ProjectileHelper.a(this, this.getBoundingBox().a(this.getMot()).g(1.0D), (entity) -> {
            return !entity.isSpectator() && (entity.isInteractable() || entity instanceof EntityItem) && (entity != this.owner || this.g >= 5);
        }, RayTrace.BlockCollisionOption.COLLIDER, true);

        // Paper start - Call ProjectileCollideEvent
        if (movingobjectposition instanceof MovingObjectPositionEntity) {
            com.destroystokyo.paper.event.entity.ProjectileCollideEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileCollideEvent(this, (MovingObjectPositionEntity)movingobjectposition);
            if (event.isCancelled()) {
                movingobjectposition = null;
            }
        }
        // Paper end

        if (movingobjectposition != null && movingobjectposition.getType() != MovingObjectPosition.EnumMovingObjectType.MISS) { // Paper - add null check in case cancelled
            org.bukkit.craftbukkit.event.CraftEventFactory.callProjectileHitEvent(this, movingobjectposition); // Craftbukkit - Call event
            if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.ENTITY) {
                this.hooked = ((MovingObjectPositionEntity) movingobjectposition).getEntity();
                this.n();
            } else {
                this.isInGround = true;
            }
        }

    }

    private void n() {
        this.getDataWatcher().set(EntityFishingHook.c, this.hooked.getId() + 1);
    }

    private void a(BlockPosition blockposition) {
        WorldServer worldserver = (WorldServer) this.world;
        int i = 1;
        BlockPosition blockposition1 = blockposition.up();

        if (this.random.nextFloat() < 0.25F && this.world.isRainingAt(blockposition1)) {
            ++i;
        }

        if (this.random.nextFloat() < 0.5F && !this.world.f(blockposition1)) {
            --i;
        }

        if (this.ar > 0) {
            --this.ar;
            if (this.ar <= 0) {
                this.as = 0;
                this.at = 0;
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.FAILED_ATTEMPT);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);
                // CraftBukkit end
            } else {
                this.setMot(this.getMot().add(0.0D, -0.2D * (double) this.random.nextFloat() * (double) this.random.nextFloat(), 0.0D));
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
                    d1 = (double) ((float) MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
                    d2 = this.locZ + (double) (f2 * (float) this.at * 0.1F);
                    block = worldserver.getType(new BlockPosition(d0, d1 - 1.0D, d2)).getBlock();
                    if (block == Blocks.WATER) {
                        if (this.random.nextFloat() < 0.15F) {
                            worldserver.a(Particles.BUBBLE, d0, d1 - 0.10000000149011612D, d2, 1, (double) f1, 0.1D, (double) f2, 0.0D);
                        }

                        float f3 = f1 * 0.04F;
                        float f4 = f2 * 0.04F;

                        worldserver.a(Particles.FISHING, d0, d1, d2, 0, (double) f4, 0.01D, (double) (-f3), 1.0D);
                        worldserver.a(Particles.FISHING, d0, d1, d2, 0, (double) (-f4), 0.01D, (double) f3, 1.0D);
                    }
                } else {
                    // CraftBukkit start
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.BITE);
                    this.world.getServer().getPluginManager().callEvent(playerFishEvent);
                    if (playerFishEvent.isCancelled()) {
                        return;
                    }
                    // CraftBukkit end
                    Vec3D vec3d = this.getMot();

                    this.setMot(vec3d.x, (double) (-0.4F * MathHelper.a(this.random, 0.6F, 1.0F)), vec3d.z);
                    this.a(SoundEffects.ENTITY_FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
                    double d3 = this.getBoundingBox().minY + 0.5D;

                    worldserver.a(Particles.BUBBLE, this.locX, d3, this.locZ, (int) (1.0F + this.getWidth() * 20.0F), (double) this.getWidth(), 0.0D, (double) this.getWidth(), 0.20000000298023224D);
                    worldserver.a(Particles.FISHING, this.locX, d3, this.locZ, (int) (1.0F + this.getWidth() * 20.0F), (double) this.getWidth(), 0.0D, (double) this.getWidth(), 0.20000000298023224D);
                    this.ar = MathHelper.nextInt(this.random, 20, 40);
                }
            } else if (this.as > 0) {
                this.as -= i;
                f = 0.15F;
                if (this.as < 20) {
                    f = (float) ((double) f + (double) (20 - this.as) * 0.05D);
                } else if (this.as < 40) {
                    f = (float) ((double) f + (double) (40 - this.as) * 0.02D);
                } else if (this.as < 60) {
                    f = (float) ((double) f + (double) (60 - this.as) * 0.01D);
                }

                if (this.random.nextFloat() < f) {
                    f1 = MathHelper.a(this.random, 0.0F, 360.0F) * 0.017453292F;
                    f2 = MathHelper.a(this.random, 25.0F, 60.0F);
                    d0 = this.locX + (double) (MathHelper.sin(f1) * f2 * 0.1F);
                    d1 = (double) ((float) MathHelper.floor(this.getBoundingBox().minY) + 1.0F);
                    d2 = this.locZ + (double) (MathHelper.cos(f1) * f2 * 0.1F);
                    block = worldserver.getType(new BlockPosition(d0, d1 - 1.0D, d2)).getBlock();
                    if (block == Blocks.WATER) {
                        worldserver.a(Particles.SPLASH, d0, d1, d2, 2 + this.random.nextInt(2), 0.10000000149011612D, 0.0D, 0.10000000149011612D, 0.0D);
                    }
                }

                if (this.as <= 0) {
                    this.au = MathHelper.a(this.random, 0.0F, 360.0F);
                    this.at = MathHelper.nextInt(this.random, 20, 80);
                }
            } else {
                this.as = MathHelper.nextInt(this.random, world.paperConfig.fishingMinTicks, world.paperConfig.fishingMaxTicks); // Paper
                this.as -= this.ax * 20 * 5;
                this.as = Math.max(0, this.as); // Paper - Don't allow negative values
            }
        }

    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {}

    @Override
    public void a(NBTTagCompound nbttagcompound) {}

    public int b(ItemStack itemstack) {
        if (!this.world.isClientSide && this.owner != null) {
            int i = 0;

            if (this.hooked != null) {
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), this.hooked.getBukkitEntity(), (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_ENTITY);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                this.reel();
                CriterionTriggers.D.a((EntityPlayer) this.owner, itemstack, this, Collections.emptyList());
                this.world.broadcastEntityEffect(this, (byte) 31);
                i = this.hooked instanceof EntityItem ? 3 : 5;
            } else if (this.ar > 0) {
                LootTableInfo.Builder loottableinfo_builder = (new LootTableInfo.Builder((WorldServer) this.world)).set(LootContextParameters.POSITION, new BlockPosition(this)).set(LootContextParameters.TOOL, itemstack).a(this.random).a((float) this.aw + this.owner.eb());
                LootTable loottable = this.world.getMinecraftServer().getLootTableRegistry().getLootTable(LootTables.ab);
                List<ItemStack> list = loottable.populateLoot(loottableinfo_builder.build(LootContextParameterSets.FISHING));

                CriterionTriggers.D.a((EntityPlayer) this.owner, itemstack, this, list);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    ItemStack itemstack1 = (ItemStack) iterator.next();
                    EntityItem entityitem = new EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack1);
                    // CraftBukkit start
                    PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), entityitem.getBukkitEntity(), (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.CAUGHT_FISH);
                    playerFishEvent.setExpToDrop(this.random.nextInt(6) + 1);
                    this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                    if (playerFishEvent.isCancelled()) {
                        return 0;
                    }
                    // CraftBukkit end
                    double d0 = this.owner.locX - this.locX;
                    double d1 = this.owner.locY - this.locY;
                    double d2 = this.owner.locZ - this.locZ;
                    double d3 = 0.1D;

                    entityitem.setMot(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
                    this.world.addEntity(entityitem);
                    // CraftBukkit start - this.random.nextInt(6) + 1 -> playerFishEvent.getExpToDrop()
                    if (playerFishEvent.getExpToDrop() > 0) {
                        this.owner.world.addEntity(new EntityExperienceOrb(this.owner.world, this.owner.locX, this.owner.locY + 0.5D, this.owner.locZ + 0.5D, playerFishEvent.getExpToDrop(), org.bukkit.entity.ExperienceOrb.SpawnReason.FISHING, this.owner, this)); // Paper
                    }
                    // CraftBukkit end
                    if (itemstack1.getItem().a(TagsItem.FISHES)) {
                        this.owner.a(StatisticList.FISH_CAUGHT, 1);
                    }
                }

                i = 1;
            }

            if (this.isInGround) {
                // CraftBukkit start
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.IN_GROUND);
                this.world.getServer().getPluginManager().callEvent(playerFishEvent);

                if (playerFishEvent.isCancelled()) {
                    return 0;
                }
                // CraftBukkit end
                i = 2;
            }
            // CraftBukkit start
            if (i == 0) {
                PlayerFishEvent playerFishEvent = new PlayerFishEvent((Player) this.owner.getBukkitEntity(), null, (FishHook) this.getBukkitEntity(), PlayerFishEvent.State.REEL_IN);
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

    protected void reel() {
        if (this.owner != null) {
            Vec3D vec3d = (new Vec3D(this.owner.locX - this.locX, this.owner.locY - this.locY, this.owner.locZ - this.locZ)).a(0.1D);

            this.hooked.setMot(this.hooked.getMot().e(vec3d));
        }
    }

    @Override
    protected boolean playStepSound() {
        return false;
    }

    @Override
    public void die() {
        super.die();
        if (this.owner != null) {
            this.owner.hookedFish = null;
        }

    }

    @Nullable
    public EntityHuman i() {
        return this.owner;
    }

    @Override
    public boolean canPortal() {
        return false;
    }

    @Override
    public Packet<?> N() {
        EntityHuman entityhuman = this.i();

        return new PacketPlayOutSpawnEntity(this, entityhuman == null ? this.getId() : entityhuman.getId());
    }

    static enum HookState {

        FLYING, HOOKED_IN_ENTITY, BOBBING;

        private HookState() {}
    }
}
