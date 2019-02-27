package net.minecraft.server;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;

public class PathfinderGoalNearestAttackableTarget<T extends EntityLiving> extends PathfinderGoalTarget {

    protected final Class<T> a;
    private final int i;
    protected final PathfinderGoalNearestAttackableTarget.DistanceComparator b;
    protected final Predicate<? super T> c;
    protected T d;

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag) {
        this(entitycreature, oclass, flag, false);
    }

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, boolean flag, boolean flag1) {
        this(entitycreature, oclass, 10, flag, flag1, (Predicate) null);
    }

    public PathfinderGoalNearestAttackableTarget(EntityCreature entitycreature, Class<T> oclass, int i, boolean flag, boolean flag1, @Nullable Predicate<? super T> predicate) {
        super(entitycreature, flag, flag1);
        this.a = oclass;
        this.i = i;
        this.b = new PathfinderGoalNearestAttackableTarget.DistanceComparator(entitycreature);
        this.a(1);
        this.c = (entityliving) -> {
            return entityliving == null ? false : (predicate != null && !predicate.test(entityliving) ? false : (!IEntitySelector.f.test(entityliving) ? false : this.a(entityliving, false)));
        };
    }

    public boolean a() {
        if (this.i > 0 && this.e.getRandom().nextInt(this.i) != 0) {
            return false;
        } else if (this.a != EntityHuman.class && this.a != EntityPlayer.class) {
            List<T> list = this.e.world.a(this.a, this.a(this.i()), this.c);

            if (list.isEmpty()) {
                return false;
            } else {
                Collections.sort(list, this.b);
                this.d = (T) list.get(0); // CraftBukkit - fix decompile error
                return true;
            }
        } else {
            this.d = (T) this.e.world.a(this.e.locX, this.e.locY + (double) this.e.getHeadHeight(), this.e.locZ, this.i(), this.i(), new Function<EntityHuman, Double>() { // CraftBukkit - fix decompile error
                @Nullable
                public Double apply(@Nullable EntityHuman entityhuman) {
                    ItemStack itemstack = entityhuman.getEquipment(EnumItemSlot.HEAD);

                    return (!(PathfinderGoalNearestAttackableTarget.this.e instanceof EntitySkeleton) || itemstack.getItem() != Items.SKELETON_SKULL) && (!(PathfinderGoalNearestAttackableTarget.this.e instanceof EntityZombie) || itemstack.getItem() != Items.ZOMBIE_HEAD) && (!(PathfinderGoalNearestAttackableTarget.this.e instanceof EntityCreeper) || itemstack.getItem() != Items.CREEPER_HEAD) ? 1.0D : 0.5D;
                }
            }, (Predicate<EntityHuman>) this.c); // CraftBukkit - fix decompile error
            return this.d != null;
        }
    }

    protected AxisAlignedBB a(double d0) {
        return this.e.getBoundingBox().grow(d0, 4.0D, d0);
    }

    public void c() {
        this.e.setGoalTarget(this.d, d instanceof EntityPlayer ? org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_PLAYER : org.bukkit.event.entity.EntityTargetEvent.TargetReason.CLOSEST_ENTITY, true); // Craftbukkit - reason
        super.c();
    }

    public static class DistanceComparator implements Comparator<Entity> {

        private final Entity a;

        public DistanceComparator(Entity entity) {
            this.a = entity;
        }

        public int compare(Entity entity, Entity entity1) {
            double d0 = this.a.h(entity);
            double d1 = this.a.h(entity1);

            return d0 < d1 ? -1 : (d0 > d1 ? 1 : 0);
        }
    }
}
