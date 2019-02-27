package net.minecraft.server;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Set;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PathfinderGoalSelector {

    private static final Logger a = LogManager.getLogger();
    private final Set<PathfinderGoalSelector.PathfinderGoalSelectorItem> b = Sets.newLinkedHashSet();
    private final Set<PathfinderGoalSelector.PathfinderGoalSelectorItem> c = Sets.newLinkedHashSet();
    private final MethodProfiler d;
    private int e;
    private int f = 3;
    private int g;

    public PathfinderGoalSelector(MethodProfiler methodprofiler) {
        this.d = methodprofiler;
    }

    public void a(int i, PathfinderGoal pathfindergoal) {
        this.b.add(new PathfinderGoalSelector.PathfinderGoalSelectorItem(i, pathfindergoal));
    }

    public void a(PathfinderGoal pathfindergoal) {
        Iterator iterator = this.b.iterator();

        PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem;
        PathfinderGoal pathfindergoal1;

        do {
            if (!iterator.hasNext()) {
                return;
            }

            pathfindergoalselector_pathfindergoalselectoritem = (PathfinderGoalSelector.PathfinderGoalSelectorItem) iterator.next();
            pathfindergoal1 = pathfindergoalselector_pathfindergoalselectoritem.a;
        } while (pathfindergoal1 != pathfindergoal);

        if (pathfindergoalselector_pathfindergoalselectoritem.c) {
            pathfindergoalselector_pathfindergoalselectoritem.c = false;
            pathfindergoalselector_pathfindergoalselectoritem.a.d();
            this.c.remove(pathfindergoalselector_pathfindergoalselectoritem);
        }

        iterator.remove();
    }

    public void doTick() {
        this.d.enter("goalSetup");
        Iterator iterator;
        PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem;

        if (this.e++ % this.f == 0) {
            iterator = this.b.iterator();

            while (iterator.hasNext()) {
                pathfindergoalselector_pathfindergoalselectoritem = (PathfinderGoalSelector.PathfinderGoalSelectorItem) iterator.next();
                if (pathfindergoalselector_pathfindergoalselectoritem.c) {
                    if (!this.b(pathfindergoalselector_pathfindergoalselectoritem) || !this.a(pathfindergoalselector_pathfindergoalselectoritem)) {
                        pathfindergoalselector_pathfindergoalselectoritem.c = false;
                        pathfindergoalselector_pathfindergoalselectoritem.a.d();
                        this.c.remove(pathfindergoalselector_pathfindergoalselectoritem);
                    }
                } else if (this.b(pathfindergoalselector_pathfindergoalselectoritem) && pathfindergoalselector_pathfindergoalselectoritem.a.a()) {
                    pathfindergoalselector_pathfindergoalselectoritem.c = true;
                    pathfindergoalselector_pathfindergoalselectoritem.a.c();
                    this.c.add(pathfindergoalselector_pathfindergoalselectoritem);
                }
            }
        } else {
            iterator = this.c.iterator();

            while (iterator.hasNext()) {
                pathfindergoalselector_pathfindergoalselectoritem = (PathfinderGoalSelector.PathfinderGoalSelectorItem) iterator.next();
                if (!this.a(pathfindergoalselector_pathfindergoalselectoritem)) {
                    pathfindergoalselector_pathfindergoalselectoritem.c = false;
                    pathfindergoalselector_pathfindergoalselectoritem.a.d();
                    iterator.remove();
                }
            }
        }

        this.d.exit();
        if (!this.c.isEmpty()) {
            this.d.enter("goalTick");
            iterator = this.c.iterator();

            while (iterator.hasNext()) {
                pathfindergoalselector_pathfindergoalselectoritem = (PathfinderGoalSelector.PathfinderGoalSelectorItem) iterator.next();
                pathfindergoalselector_pathfindergoalselectoritem.a.e();
            }

            this.d.exit();
        }

    }

    private boolean a(PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem) {
        return pathfindergoalselector_pathfindergoalselectoritem.a.b();
    }

    private boolean b(PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem) {
        if (this.c.isEmpty()) {
            return true;
        } else if (this.b(pathfindergoalselector_pathfindergoalselectoritem.a.h())) {
            return false;
        } else {
            Iterator iterator = this.c.iterator();

            while (iterator.hasNext()) {
                PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem1 = (PathfinderGoalSelector.PathfinderGoalSelectorItem) iterator.next();

                if (pathfindergoalselector_pathfindergoalselectoritem1 != pathfindergoalselector_pathfindergoalselectoritem) {
                    if (pathfindergoalselector_pathfindergoalselectoritem.b >= pathfindergoalselector_pathfindergoalselectoritem1.b) {
                        if (!this.a(pathfindergoalselector_pathfindergoalselectoritem, pathfindergoalselector_pathfindergoalselectoritem1)) {
                            return false;
                        }
                    } else if (!pathfindergoalselector_pathfindergoalselectoritem1.a.f()) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

    private boolean a(PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem, PathfinderGoalSelector.PathfinderGoalSelectorItem pathfindergoalselector_pathfindergoalselectoritem1) {
        return (pathfindergoalselector_pathfindergoalselectoritem.a.h() & pathfindergoalselector_pathfindergoalselectoritem1.a.h()) == 0;
    }

    public boolean b(int i) {
        return (this.g & i) > 0;
    }

    public void c(int i) {
        this.g |= i;
    }

    public void d(int i) {
        this.g &= ~i;
    }

    public void a(int i, boolean flag) {
        if (flag) {
            this.d(i);
        } else {
            this.c(i);
        }

    }

    class PathfinderGoalSelectorItem {

        public final PathfinderGoal a;
        public final int b;
        public boolean c;

        public PathfinderGoalSelectorItem(int i, PathfinderGoal pathfindergoal) {
            this.b = i;
            this.a = pathfindergoal;
        }

        public boolean equals(@Nullable Object object) {
            return this == object ? true : (object != null && this.getClass() == object.getClass() ? this.a.equals(((PathfinderGoalSelector.PathfinderGoalSelectorItem) object).a) : false);
        }

        public int hashCode() {
            return this.a.hashCode();
        }
    }
}
