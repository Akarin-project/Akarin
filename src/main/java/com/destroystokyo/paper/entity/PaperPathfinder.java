package com.destroystokyo.paper.entity;

import net.minecraft.server.EntityInsentient;
import net.minecraft.server.PathEntity;
import net.minecraft.server.PathPoint;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class PaperPathfinder implements com.destroystokyo.paper.entity.Pathfinder {

    private final EntityInsentient entity;

    public PaperPathfinder(EntityInsentient entity) {
        this.entity = entity;
    }

    @Override
    public Mob getEntity() {
        return entity.getBukkitMob();
    }

    @Override
    public void stopPathfinding() {
        entity.getNavigation().stopPathfinding();
    }

    @Override
    public boolean hasPath() {
        return entity.getNavigation().getPathEntity() != null;
    }

    @Nullable
    @Override
    public PathResult getCurrentPath() {
        PathEntity path = entity.getNavigation().getPathEntity();
        return path != null ? new PaperPathResult(path) : null;
    }

    @Nullable
    @Override
    public PathResult findPath(Location loc) {
        Validate.notNull(loc, "Location can not be null");
        PathEntity path = entity.getNavigation().calculateDestination(loc.getX(), loc.getY(), loc.getZ());
        return path != null ? new PaperPathResult(path) : null;
    }

    @Nullable
    @Override
    public PathResult findPath(LivingEntity target) {
        Validate.notNull(target, "Target can not be null");
        PathEntity path = entity.getNavigation().calculateDestination(((CraftLivingEntity) target).getHandle());
        return path != null ? new PaperPathResult(path) : null;
    }

    @Override
    public boolean moveTo(@Nonnull PathResult path, double speed) {
        Validate.notNull(path, "PathResult can not be null");
        PathEntity pathEntity = ((PaperPathResult) path).path;
        return entity.getNavigation().setDestination(pathEntity, speed);
    }

    public class PaperPathResult implements com.destroystokyo.paper.entity.PaperPathfinder.PathResult {

        private final PathEntity path;
        PaperPathResult(PathEntity path) {
            this.path = path;
        }

        @Nullable
        @Override
        public Location getFinalPoint() {
            PathPoint point = path.getFinalPoint();
            return point != null ? toLoc(point) : null;
        }

        @Override
        public List<Location> getPoints() {
            int pathCount = path.getPathCount();
            List<Location> points = new ArrayList<>();
            PathPoint[] pathPoints = path.getPoints();
            for (int i = 0; i < pathCount; i++) {
                points.add(toLoc(pathPoints[i]));
            }
            return points;
        }

        @Override
        public int getNextPointIndex() {
            return path.getNextIndex();
        }

        @Nullable
        @Override
        public Location getNextPoint() {
            if (!path.hasNext()) {
                return null;
            }
            return toLoc(path.getPoints()[path.getNextIndex()]);
        }
    }

    private Location toLoc(PathPoint point) {
        return new Location(entity.world.getWorld(), point.getX(), point.getY(), point.getZ());
    }
}
