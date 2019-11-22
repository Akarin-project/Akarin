package net.minecraft.server;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public abstract class NavigationAbstract {

    protected final EntityInsentient a; public Entity getEntity() { return a; } // Paper - OBFHELPER
    protected final World b;
    @Nullable
    protected PathEntity c;
    protected double d;
    private final AttributeInstance p;
    protected int e;
    protected int f;
    protected Vec3D g;
    protected Vec3D h;
    protected long i;
    protected long j;
    protected double k;
    protected float l;
    protected boolean m;
    protected long n;
    protected PathfinderAbstract o;
    private BlockPosition q;
    private int r;
    private Pathfinder s; public Pathfinder getPathfinder() { return this.s; } // Paper - OBFHELPER

    public NavigationAbstract(EntityInsentient entityinsentient, World world) {
        this.g = Vec3D.a;
        this.h = Vec3D.a;
        this.l = 0.5F;
        this.a = entityinsentient;
        this.b = world;
        this.p = entityinsentient.getAttributeInstance(GenericAttributes.FOLLOW_RANGE);
        this.s = this.a(MathHelper.floor(this.p.getValue() * 16.0D));
    }

    public BlockPosition h() {
        return this.q;
    }

    protected abstract Pathfinder a(int i);

    public void a(double d0) {
        this.d = d0;
    }

    public float i() {
        return (float) this.p.getValue();
    }

    public boolean j() {
        return this.m;
    }

    public void k() {
        if (this.b.getTime() - this.n > 20L) {
            if (this.q != null) {
                this.c = null;
                this.c = this.a(this.q, this.r);
                this.n = this.b.getTime();
                this.m = false;
            }
        } else {
            this.m = true;
        }

    }

    @Nullable
    public final PathEntity calculateDestination(double d0, double d1, double d2) { return a(d0, d1, d2, 0); } public final PathEntity a(double d0, double d1, double d2, int i) { // Paper - OBFHELPER
        return this.a(new BlockPosition(d0, d1, d2), i);
    }

    @Nullable
    public PathEntity a(Stream<BlockPosition> stream, int i) {
        return this.a((Set) stream.collect(Collectors.toSet()), 8, false, i);
    }

    @Nullable
    public PathEntity a(BlockPosition blockposition, int i) {
        // Paper start - add target parameter
        return this.a(blockposition, null, i);
    }
    @Nullable public PathEntity a(BlockPosition blockposition, Entity target, int i) {
        return this.a(ImmutableSet.of(blockposition), target, 8, false, i);
        // Paper end
    }

    @Nullable
    public final PathEntity calculateDestination(Entity entity) { return a(entity, 0); }  public PathEntity a(Entity entity, int i) {
        return this.a(ImmutableSet.of(new BlockPosition(entity)), entity, 16, true, i); // Paper
    }

    @Nullable
    // Paper start - Add target
    protected PathEntity a(Set<BlockPosition> set, int i, boolean flag, int j) {
        return this.a(set, null, i, flag, j);
    }
    @Nullable protected PathEntity a(Set<BlockPosition> set, Entity target, int i, boolean flag, int j) {
        // Paper end
        if (set.isEmpty()) {
            return null;
        } else if (this.a.locY < 0.0D) {
            return null;
        } else if (!this.a()) {
            return null;
        } else if (this.c != null && !this.c.b() && set.contains(this.q)) {
            return this.c;
        } else {
            // Paper start - Pathfind event
            boolean copiedSet = false;
            for (BlockPosition possibleTarget : set) {
                if (!getEntity().getWorld().getWorldBorder().isInBounds(possibleTarget) || !new com.destroystokyo.paper.event.entity.EntityPathfindEvent(getEntity().getBukkitEntity(), // Paper - don't path out of world border
                    MCUtil.toLocation(getEntity().world, possibleTarget), target == null ? null : target.getBukkitEntity()).callEvent()) {
                    if (!copiedSet) {
                        copiedSet = true;
                        set = new java.util.HashSet<>(set);
                    }
                    // note: since we copy the set this remove call is safe, since we're iterating over the old copy
                    set.remove(possibleTarget);
                    if (set.isEmpty()) {
                        return null;
                    }
                }
            }
            // Paper end
            this.b.getMethodProfiler().enter("pathfind");
            float f = this.i();
            BlockPosition blockposition = flag ? (new BlockPosition(this.a)).up() : new BlockPosition(this.a);
            int k = (int) (f + (float) i);
            ChunkCache chunkcache = new ChunkCache(this.b, blockposition.b(-k, -k, -k), blockposition.b(k, k, k));
            PathEntity pathentity = this.s.a(chunkcache, this.a, set, f, j);

            this.b.getMethodProfiler().exit();
            if (pathentity != null && pathentity.k() != null) {
                this.q = pathentity.k();
                this.r = j;
            }

            return pathentity;
        }
    }

    public boolean a(double d0, double d1, double d2, double d3) {
        return this.a(this.a(d0, d1, d2, 1), d3);
    }

    public boolean a(Entity entity, double d0) {
        PathEntity pathentity = this.a(entity, 1);

        return pathentity != null && this.a(pathentity, d0);
    }

    public boolean setDestination(@Nullable PathEntity pathentity, double speed) { return a(pathentity, speed); } // Paper - OBFHELPER
    public boolean a(@Nullable PathEntity pathentity, double d0) {
        if (pathentity == null) {
            this.c = null;
            return false;
        } else {
            if (!pathentity.a(this.c)) {
                this.c = pathentity;
            }

            this.D_();
            if (this.c.e() <= 0) {
                return false;
            } else {
                this.d = d0;
                Vec3D vec3d = this.b();

                this.f = this.e;
                this.g = vec3d;
                return true;
            }
        }
    }

    @Nullable public PathEntity getPathEntity() { return l(); } @Nullable // Paper - OBFHELPER
    public PathEntity l() {
        return this.c;
    }

    public void c() {
        ++this.e;
        if (this.m) {
            this.k();
        }

        if (!this.n()) {
            Vec3D vec3d;

            if (this.a()) {
                this.m();
            } else if (this.c != null && this.c.f() < this.c.e()) {
                vec3d = this.b();
                Vec3D vec3d1 = this.c.a(this.a, this.c.f());

                if (vec3d.y > vec3d1.y && !this.a.onGround && MathHelper.floor(vec3d.x) == MathHelper.floor(vec3d1.x) && MathHelper.floor(vec3d.z) == MathHelper.floor(vec3d1.z)) {
                    this.c.c(this.c.f() + 1);
                }
            }

            PacketDebug.a(this.b, this.a, this.c, this.l);
            if (!this.n()) {
                vec3d = this.c.a((Entity) this.a);
                BlockPosition blockposition = new BlockPosition(vec3d);

                this.a.getControllerMove().a(vec3d.x, this.b.getType(blockposition.down()).isAir() ? vec3d.y : PathfinderNormal.a((IBlockAccess) this.b, blockposition), vec3d.z, this.d);
            }
        }
    }

    protected void m() {
        Vec3D vec3d = this.b();

        this.l = this.a.getWidth() > 0.75F ? this.a.getWidth() / 2.0F : 0.75F - this.a.getWidth() / 2.0F;
        Vec3D vec3d1 = this.c.g();

        if (Math.abs(this.a.locX - (vec3d1.x + 0.5D)) < (double) this.l && Math.abs(this.a.locZ - (vec3d1.z + 0.5D)) < (double) this.l && Math.abs(this.a.locY - vec3d1.y) < 1.0D) {
            this.c.c(this.c.f() + 1);
        }

        this.a(vec3d);
    }

    protected void a(Vec3D vec3d) {
        if (this.e - this.f > 100) {
            if (vec3d.distanceSquared(this.g) < 2.25D) {
                this.o();
            }

            this.f = this.e;
            this.g = vec3d;
        }

        if (this.c != null && !this.c.b()) {
            Vec3D vec3d1 = this.c.g();

            if (vec3d1.equals(this.h)) {
                this.i += SystemUtils.getMonotonicMillis() - this.j;
            } else {
                this.h = vec3d1;
                double d0 = vec3d.f(this.h);

                this.k = this.a.db() > 0.0F ? d0 / (double) this.a.db() * 1000.0D : 0.0D;
            }

            if (this.k > 0.0D && (double) this.i > this.k * 3.0D) {
                this.h = Vec3D.a;
                this.i = 0L;
                this.k = 0.0D;
                this.o();
            }

            this.j = SystemUtils.getMonotonicMillis();
        }

    }

    public boolean n() {
        return this.c == null || this.c.b();
    }

    public void stopPathfinding() { o(); } // Paper - OBFHELPER
    public void o() {
        this.c = null;
    }

    protected abstract Vec3D b();

    protected abstract boolean a();

    protected boolean p() {
        return this.a.av() || this.a.aD();
    }

    protected void D_() {
        if (this.c != null) {
            for (int i = 0; i < this.c.e(); ++i) {
                PathPoint pathpoint = this.c.a(i);
                PathPoint pathpoint1 = i + 1 < this.c.e() ? this.c.a(i + 1) : null;
                IBlockData iblockdata = this.b.getType(new BlockPosition(pathpoint.a, pathpoint.b, pathpoint.c));
                Block block = iblockdata.getBlock();

                if (block == Blocks.CAULDRON) {
                    this.c.a(i, pathpoint.a(pathpoint.a, pathpoint.b + 1, pathpoint.c));
                    if (pathpoint1 != null && pathpoint.b >= pathpoint1.b) {
                        this.c.a(i + 1, pathpoint1.a(pathpoint1.a, pathpoint.b + 1, pathpoint1.c));
                    }
                }
            }

        }
    }

    protected abstract boolean a(Vec3D vec3d, Vec3D vec3d1, int i, int j, int k);

    public boolean a(BlockPosition blockposition) {
        BlockPosition blockposition1 = blockposition.down();

        return this.b.getType(blockposition1).g(this.b, blockposition1);
    }

    public PathfinderAbstract q() {
        return this.o;
    }

    public void d(boolean flag) {
        this.o.c(flag);
    }

    public boolean r() {
        return this.o.e();
    }

    public void b(BlockPosition blockposition) {
        if (this.c != null && !this.c.b() && this.c.e() != 0) {
            PathPoint pathpoint = this.c.c();
            Vec3D vec3d = new Vec3D(((double) pathpoint.a + this.a.locX) / 2.0D, ((double) pathpoint.b + this.a.locY) / 2.0D, ((double) pathpoint.c + this.a.locZ) / 2.0D);

            if (blockposition.a((IPosition) vec3d, (double) (this.c.e() - this.c.f()))) {
                this.k();
            }

        }
    }
}
