package net.minecraft.server;

import java.util.EnumSet;
// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;
// CraftBukkit end

public class PathfinderGoalFollowOwner extends PathfinderGoal {

    protected final EntityTameableAnimal a;
    private EntityLiving c;
    protected final IWorldReader b;
    private final double d;
    private final NavigationAbstract e;
    private int f;
    private final float g;
    private final float h;
    private float i;

    public PathfinderGoalFollowOwner(EntityTameableAnimal entitytameableanimal, double d0, float f, float f1) {
        this.a = entitytameableanimal;
        this.b = entitytameableanimal.world;
        this.d = d0;
        this.e = entitytameableanimal.getNavigation();
        this.h = f;
        this.g = f1;
        this.a(EnumSet.of(PathfinderGoal.Type.MOVE, PathfinderGoal.Type.LOOK));
        if (!(entitytameableanimal.getNavigation() instanceof Navigation) && !(entitytameableanimal.getNavigation() instanceof NavigationFlying)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    @Override
    public boolean a() {
        EntityLiving entityliving = this.a.getOwner();

        if (entityliving == null) {
            return false;
        } else if (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).isSpectator()) {
            return false;
        } else if (this.a.isSitting()) {
            return false;
        } else if (this.a.h((Entity) entityliving) < (double) (this.h * this.h)) {
            return false;
        } else {
            this.c = entityliving;
            return true;
        }
    }

    @Override
    public boolean b() {
        return !this.e.n() && this.a.h((Entity) this.c) > (double) (this.g * this.g) && !this.a.isSitting();
    }

    @Override
    public void c() {
        this.f = 0;
        this.i = this.a.a(PathType.WATER);
        this.a.a(PathType.WATER, 0.0F);
    }

    @Override
    public void d() {
        this.c = null;
        this.e.o();
        this.a.a(PathType.WATER, this.i);
    }

    @Override
    public void e() {
        this.a.getControllerLook().a(this.c, 10.0F, (float) this.a.M());
        if (!this.a.isSitting()) {
            if (--this.f <= 0) {
                this.f = 10;
                if (!this.e.a((Entity) this.c, this.d)) {
                    if (!this.a.isLeashed() && !this.a.isPassenger()) {
                        if (this.a.h((Entity) this.c) >= 144.0D) {
                            int i = MathHelper.floor(this.c.locX) - 2;
                            int j = MathHelper.floor(this.c.locZ) - 2;
                            int k = MathHelper.floor(this.c.getBoundingBox().minY);

                            for (int l = 0; l <= 4; ++l) {
                                for (int i1 = 0; i1 <= 4; ++i1) {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.a(new BlockPosition(i + l, k - 1, j + i1))) {
                                        // CraftBukkit start
                                        CraftEntity entity = this.a.getBukkitEntity();
                                        Location to = new Location(entity.getWorld(), (double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), this.a.yaw, this.a.pitch);
                                        EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
                                        this.a.world.getServer().getPluginManager().callEvent(event);
                                        if (event.isCancelled()) {
                                            return;
                                        }
                                        to = event.getTo();

                                        this.a.setPositionRotation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
                                        // CraftBukkit end
                                        this.e.o();
                                        return;
                                    }
                                }
                            }

                        }
                    }
                }
            }
        }
    }

    protected boolean a(BlockPosition blockposition) {
        IBlockData iblockdata = this.b.getType(blockposition);

        return iblockdata.a((IBlockAccess) this.b, blockposition, this.a.getEntityType()) && this.b.isEmpty(blockposition.up()) && this.b.isEmpty(blockposition.up(2));
    }
}
