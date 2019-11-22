package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;

public class Pathfinder {

    private final Path a = new Path();
    private final Set<PathPoint> b = Sets.newHashSet();
    private final PathPoint[] c = new PathPoint[32];
    private final int d;
    private PathfinderAbstract e; public PathfinderAbstract getPathfinder() { return this.e; }  // Paper - OBFHELPER

    public Pathfinder(PathfinderAbstract pathfinderabstract, int i) {
        this.e = pathfinderabstract;
        this.d = i;
    }

    @Nullable
    public PathEntity a(IWorldReader iworldreader, EntityInsentient entityinsentient, Set<BlockPosition> set, float f, int i) {
        this.a.a();
        this.e.a(iworldreader, entityinsentient);
        PathPoint pathpoint = this.e.b();
        Map<PathDestination, BlockPosition> map = (Map) set.stream().collect(Collectors.toMap((blockposition) -> {
            return this.e.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
        }, Function.identity()));
        PathEntity pathentity = this.a(pathpoint, map, f, i);

        this.e.a();
        return pathentity;
    }

    @Nullable
    private PathEntity a(PathPoint pathpoint, Map<PathDestination, BlockPosition> map, float f, int i) {
        Set<PathDestination> set = map.keySet();

        pathpoint.e = 0.0F;
        pathpoint.f = this.a(pathpoint, set);
        pathpoint.g = pathpoint.f;
        this.a.a();
        this.b.clear();
        this.a.a(pathpoint);
        int j = 0;

        while (!this.a.e()) {
            ++j;
            if (j >= this.d) {
                break;
            }

            PathPoint pathpoint1 = this.a.c();

            pathpoint1.i = true;
            set.stream().filter((pathdestination) -> {
                return pathpoint1.c((PathPoint) pathdestination) <= (float) i;
            }).forEach(PathDestination::e);
            if (set.stream().anyMatch(PathDestination::f)) {
                break;
            }

            if (pathpoint1.a(pathpoint) < f) {
                int k = this.e.a(this.c, pathpoint1);

                for (int l = 0; l < k; ++l) {
                    PathPoint pathpoint2 = this.c[l];
                    float f1 = pathpoint1.a(pathpoint2);

                    pathpoint2.j = pathpoint1.j + f1;
                    float f2 = pathpoint1.e + f1 + pathpoint2.k;

                    if (pathpoint2.j < f && (!pathpoint2.c() || f2 < pathpoint2.e)) {
                        pathpoint2.h = pathpoint1;
                        pathpoint2.e = f2;
                        pathpoint2.f = this.a(pathpoint2, set) * 1.5F;
                        if (pathpoint2.c()) {
                            this.a.a(pathpoint2, pathpoint2.e + pathpoint2.f);
                        } else {
                            pathpoint2.g = pathpoint2.e + pathpoint2.f;
                            this.a.a(pathpoint2);
                        }
                    }
                }
            }
        }

        Stream stream;

        if (set.stream().anyMatch(PathDestination::f)) {
            stream = set.stream().filter(PathDestination::f).map((pathdestination) -> {
                return this.a(pathdestination.d(), (BlockPosition) map.get(pathdestination), true);
            }).sorted(Comparator.comparingInt(PathEntity::e));
        } else {
            stream = set.stream().map((pathdestination) -> {
                return this.a(pathdestination.d(), (BlockPosition) map.get(pathdestination), false);
            }).sorted(Comparator.comparingDouble(PathEntity::l).thenComparingInt(PathEntity::e));
        }

        Optional<PathEntity> optional = stream.findFirst();

        if (!optional.isPresent()) {
            return null;
        } else {
            PathEntity pathentity = (PathEntity) optional.get();

            return pathentity;
        }
    }

    private float a(PathPoint pathpoint, Set<PathDestination> set) {
        float f = Float.MAX_VALUE;

        float f1;

        for (Iterator iterator = set.iterator(); iterator.hasNext(); f = Math.min(f1, f)) {
            PathDestination pathdestination = (PathDestination) iterator.next();

            f1 = pathpoint.a(pathdestination);
            pathdestination.a(f1, pathpoint);
        }

        return f;
    }

    private PathEntity a(PathPoint pathpoint, BlockPosition blockposition, boolean flag) {
        List<PathPoint> list = Lists.newArrayList();
        PathPoint pathpoint1 = pathpoint;

        list.add(0, pathpoint);

        while (pathpoint1.h != null) {
            pathpoint1 = pathpoint1.h;
            list.add(0, pathpoint1);
        }

        return new PathEntity(list, blockposition, flag);
    }
}
