package net.minecraft.server;

import java.util.Random;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.entity.EntityInteractEvent;
// CraftBukkit end

public class PathfinderGoalRemoveBlock extends PathfinderGoalGotoTarget {

    private final Block f;
    private final EntityInsentient entity;
    private int h;

    public PathfinderGoalRemoveBlock(Block block, EntityCreature entitycreature, double d0, int i) {
        super(entitycreature, d0, 24, i);
        this.f = block;
        this.entity = entitycreature;
    }

    public boolean a() {
        return !this.entity.world.getGameRules().getBoolean("mobGriefing") ? false : (this.entity.getRandom().nextInt(20) != 0 ? false : super.a());
    }

    protected int a(EntityCreature entitycreature) {
        return 0;
    }

    public boolean b() {
        return super.b();
    }

    public void d() {
        super.d();
        this.entity.fallDistance = 1.0F;
    }

    public void c() {
        super.c();
        this.h = 0;
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {}

    public void a(World world, BlockPosition blockposition) {}

    public void e() {
        super.e();
        World world = this.entity.world;
        BlockPosition blockposition = new BlockPosition(this.entity);
        BlockPosition blockposition1 = this.a(blockposition, (IBlockAccess) world);
        Random random = this.entity.getRandom();

        if (this.k() && blockposition1 != null) {
            if (this.h > 0) {
                this.entity.motY = 0.3D;
                if (!world.isClientSide) {
                    double d0 = 0.08D;

                    ((WorldServer) world).a(new ParticleParamItem(Particles.C, new ItemStack(Items.EGG)), (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.7D, (double) blockposition1.getZ() + 0.5D, 3, ((double) random.nextFloat() - 0.5D) * 0.08D, ((double) random.nextFloat() - 0.5D) * 0.08D, ((double) random.nextFloat() - 0.5D) * 0.08D, 0.15000000596046448D);
                }
            }

            if (this.h % 2 == 0) {
                this.entity.motY = -0.3D;
                if (this.h % 6 == 0) {
                    this.a((GeneratorAccess) world, this.d);
                }
            }

            if (this.h > 60) {
                // CraftBukkit start - Step on eggs
                EntityInteractEvent event = new EntityInteractEvent(this.entity.getBukkitEntity(), CraftBlock.at(world, blockposition1));
                world.getServer().getPluginManager().callEvent((EntityInteractEvent) event);

                if (event.isCancelled()) {
                    return;
                }
                // CraftBukkit end
                world.setAir(blockposition1);
                if (!world.isClientSide) {
                    for (int i = 0; i < 20; ++i) {
                        double d1 = random.nextGaussian() * 0.02D;
                        double d2 = random.nextGaussian() * 0.02D;
                        double d3 = random.nextGaussian() * 0.02D;

                        ((WorldServer) world).a(Particles.J, (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY(), (double) blockposition1.getZ() + 0.5D, 1, d1, d2, d3, 0.15000000596046448D);
                    }

                    this.a(world, this.d);
                }
            }

            ++this.h;
        }

    }

    @Nullable
    private BlockPosition a(BlockPosition blockposition, IBlockAccess iblockaccess) {
        if (iblockaccess.getType(blockposition).getBlock() == this.f) {
            return blockposition;
        } else {
            BlockPosition[] ablockposition = new BlockPosition[] { blockposition.down(), blockposition.west(), blockposition.east(), blockposition.north(), blockposition.south(), blockposition.down().down()};
            BlockPosition[] ablockposition1 = ablockposition;
            int i = ablockposition.length;

            for (int j = 0; j < i; ++j) {
                BlockPosition blockposition1 = ablockposition1[j];

                if (iblockaccess.getType(blockposition1).getBlock() == this.f) {
                    return blockposition1;
                }
            }

            return null;
        }
    }

    protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        Block block = iworldreader.getType(blockposition).getBlock();

        return block == this.f && iworldreader.getType(blockposition.up()).isAir() && iworldreader.getType(blockposition.up(2)).isAir();
    }
}
