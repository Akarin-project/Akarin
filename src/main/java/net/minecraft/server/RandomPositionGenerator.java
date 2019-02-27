package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;

public class RandomPositionGenerator {

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j) {
        return c(entitycreature, i, j, (Vec3D) null);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j) {
        return a(entitycreature, i, j, (Vec3D) null, false, 0.0D);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX, entitycreature.locY, entitycreature.locZ);

        return c(entitycreature, i, j, vec3d1);
    }

    @Nullable
    public static Vec3D a(EntityCreature entitycreature, int i, int j, Vec3D vec3d, double d0) {
        Vec3D vec3d1 = vec3d.a(entitycreature.locX, entitycreature.locY, entitycreature.locZ);

        return a(entitycreature, i, j, vec3d1, true, d0);
    }

    @Nullable
    public static Vec3D b(EntityCreature entitycreature, int i, int j, Vec3D vec3d) {
        Vec3D vec3d1 = (new Vec3D(entitycreature.locX, entitycreature.locY, entitycreature.locZ)).d(vec3d);

        return c(entitycreature, i, j, vec3d1);
    }

    @Nullable
    private static Vec3D c(EntityCreature entitycreature, int i, int j, @Nullable Vec3D vec3d) {
        return a(entitycreature, i, j, vec3d, true, 1.5707963705062866D);
    }

    @Nullable
    private static Vec3D a(EntityCreature entitycreature, int i, int j, @Nullable Vec3D vec3d, boolean flag, double d0) {
        NavigationAbstract navigationabstract = entitycreature.getNavigation();
        Random random = entitycreature.getRandom();
        boolean flag1;

        if (entitycreature.dw()) {
            double d1 = entitycreature.dt().distanceSquared((double) MathHelper.floor(entitycreature.locX), (double) MathHelper.floor(entitycreature.locY), (double) MathHelper.floor(entitycreature.locZ)) + 4.0D;
            double d2 = (double) (entitycreature.du() + (float) i);

            flag1 = d1 < d2 * d2;
        } else {
            flag1 = false;
        }

        boolean flag2 = false;
        float f = -99999.0F;
        int k = 0;
        int l = 0;
        int i1 = 0;

        for (int j1 = 0; j1 < 10; ++j1) {
            BlockPosition blockposition = a(random, i, j, vec3d, d0);

            if (blockposition != null) {
                int k1 = blockposition.getX();
                int l1 = blockposition.getY();
                int i2 = blockposition.getZ();
                BlockPosition blockposition1;

                if (entitycreature.dw() && i > 1) {
                    blockposition1 = entitycreature.dt();
                    if (entitycreature.locX > (double) blockposition1.getX()) {
                        k1 -= random.nextInt(i / 2);
                    } else {
                        k1 += random.nextInt(i / 2);
                    }

                    if (entitycreature.locZ > (double) blockposition1.getZ()) {
                        i2 -= random.nextInt(i / 2);
                    } else {
                        i2 += random.nextInt(i / 2);
                    }
                }

                blockposition1 = new BlockPosition((double) k1 + entitycreature.locX, (double) l1 + entitycreature.locY, (double) i2 + entitycreature.locZ);
                if ((!flag1 || entitycreature.f(blockposition1)) && navigationabstract.a(blockposition1)) {
                    if (!flag) {
                        blockposition1 = a(blockposition1, entitycreature);
                        if (b(blockposition1, entitycreature)) {
                            continue;
                        }
                    }

                    float f1 = entitycreature.a(blockposition1);

                    if (f1 > f) {
                        f = f1;
                        k = k1;
                        l = l1;
                        i1 = i2;
                        flag2 = true;
                    }
                }
            }
        }

        if (flag2) {
            return new Vec3D((double) k + entitycreature.locX, (double) l + entitycreature.locY, (double) i1 + entitycreature.locZ);
        } else {
            return null;
        }
    }

    @Nullable
    private static BlockPosition a(Random random, int i, int j, @Nullable Vec3D vec3d, double d0) {
        if (vec3d != null && d0 < 3.141592653589793D) {
            double d1 = MathHelper.c(vec3d.z, vec3d.x) - 1.5707963705062866D;
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

            for (blockposition1 = blockposition.up(); blockposition1.getY() < entitycreature.world.getHeight() && entitycreature.world.getType(blockposition1).getMaterial().isBuildable(); blockposition1 = blockposition1.up()) {
                ;
            }

            return blockposition1;
        }
    }

    private static boolean b(BlockPosition blockposition, EntityCreature entitycreature) {
        return entitycreature.world.getFluid(blockposition).a(TagsFluid.WATER);
    }
}
