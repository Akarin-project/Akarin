package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;

public class Pathfinder {

    private final Path a = new Path();
    private final Set<PathPoint> b = Sets.newHashSet();
    private final PathPoint[] c = new PathPoint[32];
    private PathfinderAbstract d;

    public Pathfinder(PathfinderAbstract pathfinderabstract) {
        this.d = pathfinderabstract;
    }

    @Nullable
    public PathEntity a(IBlockAccess iblockaccess, EntityInsentient entityinsentient, Entity entity, float f) {
        return this.a(iblockaccess, entityinsentient, entity.locX, entity.getBoundingBox().minY, entity.locZ, f);
    }

    @Nullable
    public PathEntity a(IBlockAccess iblockaccess, EntityInsentient entityinsentient, BlockPosition blockposition, float f) {
        return this.a(iblockaccess, entityinsentient, (double) ((float) blockposition.getX() + 0.5F), (double) ((float) blockposition.getY() + 0.5F), (double) ((float) blockposition.getZ() + 0.5F), f);
    }

    @Nullable
    private PathEntity a(IBlockAccess iblockaccess, EntityInsentient entityinsentient, double d0, double d1, double d2, float f) {
        this.a.a();
        this.d.a(iblockaccess, entityinsentient);
        PathPoint pathpoint = this.d.b();
        PathPoint pathpoint1 = this.d.a(d0, d1, d2);
        PathEntity pathentity = this.a(pathpoint, pathpoint1, f);

        this.d.a();
        return pathentity;
    }

    @Nullable
    private PathEntity a(PathPoint pathpoint, PathPoint pathpoint1, float f) {
        pathpoint.e = 0.0F;
        pathpoint.f = pathpoint.c(pathpoint1);
        pathpoint.g = pathpoint.f;
        this.a.a();
        this.b.clear();
        this.a.a(pathpoint);
        PathPoint pathpoint2 = pathpoint;
        int i = 0;

        while (!this.a.e()) {
            ++i;
            if (i >= 200) {
                break;
            }

            PathPoint pathpoint3 = this.a.c();

            if (pathpoint3.equals(pathpoint1)) {
                pathpoint2 = pathpoint1;
                break;
            }

            if (pathpoint3.c(pathpoint1) < pathpoint2.c(pathpoint1)) {
                pathpoint2 = pathpoint3;
            }

            pathpoint3.i = true;
            int j = this.d.a(this.c, pathpoint3, pathpoint1, f);

            for (int k = 0; k < j; ++k) {
                PathPoint pathpoint4 = this.c[k];
                float f1 = pathpoint3.c(pathpoint4);

                pathpoint4.j = pathpoint3.j + f1;
                pathpoint4.k = f1 + pathpoint4.l;
                float f2 = pathpoint3.e + pathpoint4.k;

                if (pathpoint4.j < f && (!pathpoint4.a() || f2 < pathpoint4.e)) {
                    pathpoint4.h = pathpoint3;
                    pathpoint4.e = f2;
                    pathpoint4.f = pathpoint4.c(pathpoint1) + pathpoint4.l;
                    if (pathpoint4.a()) {
                        this.a.a(pathpoint4, pathpoint4.e + pathpoint4.f);
                    } else {
                        pathpoint4.g = pathpoint4.e + pathpoint4.f;
                        this.a.a(pathpoint4);
                    }
                }
            }
        }

        if (pathpoint2 == pathpoint) {
            return null;
        } else {
            PathEntity pathentity = this.a(pathpoint, pathpoint2);

            return pathentity;
        }
    }

    private PathEntity a(PathPoint pathpoint, PathPoint pathpoint1) {
        int i = 1;

        PathPoint pathpoint2;

        for (pathpoint2 = pathpoint1; pathpoint2.h != null; pathpoint2 = pathpoint2.h) {
            ++i;
        }

        PathPoint[] apathpoint = new PathPoint[i];

        pathpoint2 = pathpoint1;
        --i;

        for (apathpoint[i] = pathpoint1; pathpoint2.h != null; apathpoint[i] = pathpoint2) {
            pathpoint2 = pathpoint2.h;
            --i;
        }

        return new PathEntity(apathpoint);
    }
}
