package net.minecraft.server;

import java.util.Random;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class BlockStem extends BlockFlower {

    private Block blockFruit;

    protected BlockStem(int i, Block block) {
        super(i, 111);
        this.blockFruit = block;
        this.b(true);
        float f = 0.125F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
        this.a((CreativeModeTab) null);
    }

    protected boolean d_(int i) {
        return i == Block.SOIL.id;
    }

    public void b(World world, int i, int j, int k, Random random) {
        super.b(world, i, j, k, random);
        if (world.getLightLevel(i, j + 1, k) >= 9) {
            float f = this.n(world, i, j, k);

            if (random.nextInt((int) (25.0F / f) + 1) == 0) {
                int l = world.getData(i, j, k);

                if (l < 7) {
                    ++l;
                    CraftEventFactory.handleBlockGrowEvent(world, i, j, k, this.id, l); // CraftBukkit
                } else {
                    if (world.getTypeId(i - 1, j, k) == this.blockFruit.id) {
                        return;
                    }

                    if (world.getTypeId(i + 1, j, k) == this.blockFruit.id) {
                        return;
                    }

                    if (world.getTypeId(i, j, k - 1) == this.blockFruit.id) {
                        return;
                    }

                    if (world.getTypeId(i, j, k + 1) == this.blockFruit.id) {
                        return;
                    }

                    int i1 = random.nextInt(4);
                    int j1 = i;
                    int k1 = k;

                    if (i1 == 0) {
                        j1 = i - 1;
                    }

                    if (i1 == 1) {
                        ++j1;
                    }

                    if (i1 == 2) {
                        k1 = k - 1;
                    }

                    if (i1 == 3) {
                        ++k1;
                    }

                    int l1 = world.getTypeId(j1, j - 1, k1);

                    if (world.getTypeId(j1, j, k1) == 0 && (l1 == Block.SOIL.id || l1 == Block.DIRT.id || l1 == Block.GRASS.id)) {
                        CraftEventFactory.handleBlockGrowEvent(world, j1, j, k1, this.blockFruit.id, 0); // CraftBukkit
                    }
                }
            }
        }
    }

    public void l(World world, int i, int j, int k) {
        world.setData(i, j, k, 7);
    }

    private float n(World world, int i, int j, int k) {
        float f = 1.0F;
        int l = world.getTypeId(i, j, k - 1);
        int i1 = world.getTypeId(i, j, k + 1);
        int j1 = world.getTypeId(i - 1, j, k);
        int k1 = world.getTypeId(i + 1, j, k);
        int l1 = world.getTypeId(i - 1, j, k - 1);
        int i2 = world.getTypeId(i + 1, j, k - 1);
        int j2 = world.getTypeId(i + 1, j, k + 1);
        int k2 = world.getTypeId(i - 1, j, k + 1);
        boolean flag = j1 == this.id || k1 == this.id;
        boolean flag1 = l == this.id || i1 == this.id;
        boolean flag2 = l1 == this.id || i2 == this.id || j2 == this.id || k2 == this.id;

        for (int l2 = i - 1; l2 <= i + 1; ++l2) {
            for (int i3 = k - 1; i3 <= k + 1; ++i3) {
                int j3 = world.getTypeId(l2, j - 1, i3);
                float f1 = 0.0F;

                if (j3 == Block.SOIL.id) {
                    f1 = 1.0F;
                    if (world.getData(l2, j - 1, i3) > 0) {
                        f1 = 3.0F;
                    }
                }

                if (l2 != i || i3 != k) {
                    f1 /= 4.0F;
                }

                f += f1;
            }
        }

        if (flag2 || flag && flag1) {
            f /= 2.0F;
        }

        return f;
    }

    public int a(int i, int j) {
        return this.textureId;
    }

    public void f() {
        float f = 0.125F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.25F, 0.5F + f);
    }

    public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
        this.maxY = (double) ((float) (iblockaccess.getData(i, j, k) * 2 + 2) / 16.0F);
        float f = 0.125F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, (float) this.maxY, 0.5F + f);
    }

    public int b() {
        return 19;
    }

    public void dropNaturally(World world, int i, int j, int k, int l, float f, int i1) {
        super.dropNaturally(world, i, j, k, l, f, i1);
        if (!world.isStatic) {
            Item item = null;

            if (this.blockFruit == Block.PUMPKIN) {
                item = Item.PUMPKIN_SEEDS;
            }

            if (this.blockFruit == Block.MELON) {
                item = Item.MELON_SEEDS;
            }

            for (int j1 = 0; j1 < 3; ++j1) {
                if (world.random.nextInt(15) <= l) {
                    float f1 = 0.7F;
                    float f2 = world.random.nextFloat() * f1 + (1.0F - f1) * 0.5F;
                    float f3 = world.random.nextFloat() * f1 + (1.0F - f1) * 0.5F;
                    float f4 = world.random.nextFloat() * f1 + (1.0F - f1) * 0.5F;
                    EntityItem entityitem = new EntityItem(world, (double) ((float) i + f2), (double) ((float) j + f3), (double) ((float) k + f4), new ItemStack(item));

                    entityitem.pickupDelay = 10;
                    world.addEntity(entityitem);
                }
            }
        }
    }

    public int getDropType(int i, Random random, int j) {
        return -1;
    }

    public int a(Random random) {
        return 1;
    }
}
