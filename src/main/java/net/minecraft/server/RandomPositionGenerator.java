package net.minecraft.server;

import java.util.Random;
import java.util.function.ToDoubleFunction;
import javax.annotation.Nullable;

public class RandomPositionGenerator {

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j) {
        return d(entitycreature, i, j, (Vec3D) null);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j) {
        entitycreature.getClass();
        return a(entitycreature, i, j, entitycreature::f);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, ToDoubleFunction<BlockPosition> todoublefunction) {
        return a(entitycreature, i, j, (Vec3D) null, false, 0.0D, todoublefunction);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX, entitycreature.locY, entitycreature.locZ);

        return d(entitycreature, i, j, vec3d1);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX, entitycreature.locY, entitycreature.locZ);

        entitycreature.getClass();
        return a(entitycreature, i, j, vec3d1, true, d0, entitycreature::f);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = (new Vec3D(entitycreature.locX, entitycreature.locY, entitycreature.locZ)).d(vec3d);

        entitycreature.getClass();
        return a(entitycreature, i, j, vec3d1, false, 1.5707963705062866D, entitycreature::f);
    }

    @Nullable
    public static Vec3D c(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = (new Vec3D(entitycreature.locX, entitycreature.locY, entitycreature.locZ)).d(vec3d);

        return d(entitycreature, i, j, vec3d1);
    }

    @Nullable
    private static Vec3D d(EntityCreature entitycreature, int i, int j, @Nullable Vec3D vec3d) {
        entitycreature.getClass();
        return a(entitycreature, i, j, vec3d, true, 1.5707963705062866D, entitycreature::f);
    }

    @Nullable
    private static Vec3D a(EntityCreature entitycreature, int i, int j, @Nullable Vec3D vec3d, boolean flag, double d0, ToDoubleFunction<BlockPosition> todoublefunction) {
        NavigationAbstract navigationabstract = entitycreature.getNavigation();
        Random random = entitycreature.getRandom();
        boolean flag1;

        if (entitycreature.dL()) {
            flag1 = entitycreature.dI().a((IPosition) entitycreature.getPositionVector(), (double) (entitycreature.dJ() + (float) i) + 1.0D);
        } else {
            flag1 = false;
        }

        boolean flag2 = false;
        double d1 = Double.NEGATIVE_INFINITY;
        BlockPosition blockposition = new BlockPosition(entitycreature);

        for (int k = 0; k < 10; ++k) {
            BlockPosition blockposition1 = a(random, i, j, vec3d, d0);

            if (blockposition1 != null) {
                int l = blockposition1.getX();
                int i1 = blockposition1.getY();
                int j1 = blockposition1.getZ();
                BlockPosition blockposition2;

                if (entitycreature.dL() && i > 1) {
                    blockposition2 = entitycreature.dI();
                    if (entitycreature.locX > (double) blockposition2.getX()) {
                        l -= random.nextInt(i / 2);
                    } else {
                        l += random.nextInt(i / 2);
                    }

                    if (entitycreature.locZ > (double) blockposition2.getZ()) {
                        j1 -= random.nextInt(i / 2);
                    } else {
                        j1 += random.nextInt(i / 2);
                    }
                }

                blockposition2 = new BlockPosition((double) l + entitycreature.locX, (double) i1 + entitycreature.locY, (double) j1 + entitycreature.locZ);
                if (!entitycreature.world.isLoaded(blockposition2)) continue; // Paper
                if ((!flag1 || entitycreature.a(blockposition2)) && navigationabstract.a(blockposition2)) {
                    if (!flag) {
                        blockposition2 = a(blockposition2, entitycreature);
                        if (b(blockposition2, entitycreature)) {
                            continue;
                        }
                    }

                    double d2 = todoublefunction.applyAsDouble(blockposition2);

                    if (d2 > d1) {
                        d1 = d2;
                        blockposition = blockposition2;
                        flag2 = true;
                    }
                }
            }
        }

        if (flag2) {
            return new Vec3D(blockposition);
        } else {
            return null;
        }
    }

    @Nullable
    private static BlockPosition a(Random random, int i, int j, @Nullable Vec3D vec3d, double d0) {
        if (vec3d != null && d0 < 3.141592653589793D) {
            double d1 = MathHelper.d(vec3d.z, vec3d.x) - 1.5707963705062866D;
            double d2 = d1 + (double) (2.0F * random.nextFloat() - 1.0F) * d0;
            double d3 = Math.sqrt(random.nextDouble()) * (double) MathHelper.a * (double) i;
            double d4 = -d3 * Math.sin(d2);
            double d5 = d3 * Math.cos(d2);

            if (Math.abs(d4) <= (double) i && Math.abs(d5) <= (double) i) {
                int k = random.nextInt(2 * j + 1) - j;

                return new BlockPosition(d4, (double) k, d5);
            } else {
                return null;
            }
        } else {
            int l = random.nextInt(2 * i + 1) - i;
            int i1 = random.nextInt(2 * j + 1) - j;
            int j1 = random.nextInt(2 * i + 1) - i;

            return new BlockPosition(l, i1, j1);
        }
    }

    private static BlockPosition a(BlockPosition blockposition, EntityCreature entitycreature) {
        if (!entitycreature.world.getType(blockposition).getMaterial().isBuildable()) {
            return blockposition;
        } else {
            BlockPosition blockposition1;

            for (blockposition1 = blockposition.up(); blockposition1.getY() < entitycreature.world.getBuildHeight() && entitycreature.world.getType(blockposition1).getMaterial().isBuildable(); blockposition1 = blockposition1.up()) {
                ;
            }

            return blockposition1;
        }
    }

    private static boolean b(BlockPosition blockposition, EntityCreature entitycreature) {
        Fluid fluid = entitycreature.world.getFluidIfLoaded(blockposition); // Paper
        return fluid != null && fluid.a(TagsFluid.WATER); // Paper
    }
}
