package net.minecraft.server;

// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.event.entity.EntityTeleportEvent;
// CraftBukkit end

public class PathfinderGoalFollowOwner extends PathfinderGoal {

    private final EntityTameableAnimal b;
    private EntityLiving c;
    protected final IWorldReader a;
    private final double d;
    private final NavigationAbstract e;
    private int f;
    private final float g;
    private final float h;
    private float i;

    public PathfinderGoalFollowOwner(EntityTameableAnimal entitytameableanimal, double d0, float f, float f1) {
        this.b = entitytameableanimal;
        this.a = entitytameableanimal.world;
        this.d = d0;
        this.e = entitytameableanimal.getNavigation();
        this.h = f;
        this.g = f1;
        this.a(3);
        if (!(entitytameableanimal.getNavigation() instanceof Navigation) && !(entitytameableanimal.getNavigation() instanceof NavigationFlying)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
    }

    public boolean a() {
        EntityLiving entityliving = this.b.getOwner();

        if (entityliving == null) {
            return false;
        } else if (entityliving instanceof EntityHuman && ((EntityHuman) entityliving).isSpectator()) {
            return false;
        } else if (this.b.isSitting()) {
            return false;
        } else if (this.b.h(entityliving) < (double) (this.h * this.h)) {
            return false;
        } else {
            this.c = entityliving;
            return true;
        }
    }

    public boolean b() {
        return !this.e.p() && this.b.h(this.c) > (double) (this.g * this.g) && !this.b.isSitting();
    }

    public void c() {
        this.f = 0;
        this.i = this.b.a(PathType.WATER);
        this.b.a(PathType.WATER, 0.0F);
    }

    public void d() {
        this.c = null;
        this.e.q();
        this.b.a(PathType.WATER, this.i);
    }

    public void e() {
        this.b.getControllerLook().a(this.c, 10.0F, (float) this.b.K());
        if (!this.b.isSitting()) {
            if (--this.f <= 0) {
                this.f = 10;
                if (!this.e.a((Entity) this.c, this.d)) {
                    if (!this.b.isLeashed() && !this.b.isPassenger()) {
                        if (this.b.h(this.c) >= 144.0D) {
                            int i = MathHelper.floor(this.c.locX) - 2;
                            int j = MathHelper.floor(this.c.locZ) - 2;
                            int k = MathHelper.floor(this.c.getBoundingBox().minY);

                            for (int l = 0; l <= 4; ++l) {
                                for (int i1 = 0; i1 <= 4; ++i1) {
                                    if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.a(i, j, k, l, i1)) {
                                        // CraftBukkit start
                                        CraftEntity entity = this.b.getBukkitEntity();
                                        Location to = new Location(entity.getWorld(), (double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), this.b.yaw, this.b.pitch);
                                        EntityTeleportEvent event = new EntityTeleportEvent(entity, entity.getLocation(), to);
                                        this.b.world.getServer().getPluginManager().callEvent(event);
                                        if (event.isCancelled()) {
                                            return;
                                        }
                                        to = event.getTo();

                                        this.b.setPositionRotation(to.getX(), to.getY(), to.getZ(), to.getYaw(), to.getPitch());
                                        // CraftBukkit end
                                        this.e.q();
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

    protected boolean a(int i, int j, int k, int l, int i1) {
        BlockPosition blockposition = new BlockPosition(i + l, k - 1, j + i1);
        IBlockData iblockdata = this.a.getType(blockposition);

        return iblockdata.c(this.a, blockposition, EnumDirection.DOWN) == EnumBlockFaceShape.SOLID && iblockdata.a((Entity) this.b) && this.a.isEmpty(blockposition.up()) && this.a.isEmpty(blockposition.up(2));
    }
}
