package net.minecraft.server;

import java.util.function.Predicate;

public class PathfinderGoalEatTile extends PathfinderGoal {

    private static final Predicate<IBlockData> a = BlockStatePredicate.a(Blocks.GRASS);
    private final EntityInsentient b;
    private final World c;
    private int d;

    public PathfinderGoalEatTile(EntityInsentient entityinsentient) {
        this.b = entityinsentient;
        this.c = entityinsentient.world;
        this.a(7);
    }

    public boolean a() {
        if (this.b.getRandom().nextInt(this.b.isBaby() ? 50 : 1000) != 0) {
            return false;
        } else {
            BlockPosition blockposition = new BlockPosition(this.b.locX, this.b.locY, this.b.locZ);

            return PathfinderGoalEatTile.a.test(this.c.getType(blockposition)) ? true : this.c.getType(blockposition.down()).getBlock() == Blocks.GRASS_BLOCK;
        }
    }

    public void c() {
        this.d = 40;
        this.c.broadcastEntityEffect(this.b, (byte) 10);
        this.b.getNavigation().q();
    }

    public void d() {
        this.d = 0;
    }

    public boolean b() {
        return this.d > 0;
    }

    public int g() {
        return this.d;
    }

    public void e() {
        this.d = Math.max(0, this.d - 1);
        if (this.d == 4) {
            BlockPosition blockposition = new BlockPosition(this.b.locX, this.b.locY, this.b.locZ);

            if (PathfinderGoalEatTile.a.test(this.c.getType(blockposition))) {
                if (this.c.getGameRules().getBoolean("mobGriefing")) {
                    this.c.setAir(blockposition, false);
                }

                this.b.x();
            } else {
                BlockPosition blockposition1 = blockposition.down();

                if (this.c.getType(blockposition1).getBlock() == Blocks.GRASS_BLOCK) {
                    if (this.c.getGameRules().getBoolean("mobGriefing")) {
                        this.c.triggerEffect(2001, blockposition1, Block.getCombinedId(Blocks.GRASS_BLOCK.getBlockData()));
                        this.c.setTypeAndData(blockposition1, Blocks.DIRT.getBlockData(), 2);
                    }

                    this.b.x();
                }
            }

        }
    }
}
