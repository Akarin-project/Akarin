package net.minecraft.server;

import java.util.Iterator;
import java.util.Random;

public class MobSpawnerPhantom {

    private int a;

    public MobSpawnerPhantom() {}

    public int a(World world, boolean flag, boolean flag1) {
        if (!flag) {
            return 0;
        } else {
            Random random = world.random;

            --this.a;
            if (this.a > 0) {
                return 0;
            } else {
                this.a += (60 + random.nextInt(60)) * 20;
                if (world.c() < 5 && world.worldProvider.g()) {
                    return 0;
                } else {
                    int i = 0;
                    Iterator iterator = world.players.iterator();

                    while (iterator.hasNext()) {
                        EntityHuman entityhuman = (EntityHuman) iterator.next();

                        if (!entityhuman.isSpectator()) {
                            BlockPosition blockposition = new BlockPosition(entityhuman);

                            if (!world.worldProvider.g() || blockposition.getY() >= world.getSeaLevel() && world.e(blockposition)) {
                                DifficultyDamageScaler difficultydamagescaler = world.getDamageScaler(blockposition);

                                if (difficultydamagescaler.a(random.nextFloat() * 3.0F)) {
                                    ServerStatisticManager serverstatisticmanager = ((EntityPlayer) entityhuman).getStatisticManager();
                                    int j = MathHelper.clamp(serverstatisticmanager.getStatisticValue(StatisticList.CUSTOM.b(StatisticList.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                                    boolean flag2 = true;

                                    if (random.nextInt(j) >= 72000) {
                                        BlockPosition blockposition1 = blockposition.up(20 + random.nextInt(15)).east(-10 + random.nextInt(21)).south(-10 + random.nextInt(21));
                                        IBlockData iblockdata = world.getType(blockposition1);
                                        Fluid fluid = world.getFluid(blockposition1);

                                        if (SpawnerCreature.a(iblockdata, fluid)) {
                                            GroupDataEntity groupdataentity = null;
                                            int k = 1 + random.nextInt(difficultydamagescaler.a().a() + 1);

                                            for (int l = 0; l < k; ++l) {
                                                EntityPhantom entityphantom = new EntityPhantom(world);

                                                entityphantom.setPositionRotation(blockposition1, 0.0F, 0.0F);
                                                groupdataentity = entityphantom.prepare(difficultydamagescaler, groupdataentity, (NBTTagCompound) null);
                                                world.addEntity(entityphantom);
                                            }

                                            i += k;
                                        }
                                    }
                                }
                            }
                        }
                    }

                    return i;
                }
            }
        }
    }
}
