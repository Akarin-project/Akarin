package net.minecraft.server;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockTrapdoor extends Block {

    protected BlockTrapdoor(int i, Material material) {
        super(i, material);
        float f = 0.5F;
        float f1 = 1.0F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f1, 0.5F + f);
        this.a(CreativeModeTab.d);
    }

    public boolean c() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public boolean b(IBlockAccess iblockaccess, int i, int j, int k) {
        return !f(iblockaccess.getData(i, j, k));
    }

    public int d() {
        return 0;
    }

    public AxisAlignedBB b(World world, int i, int j, int k) {
        this.updateShape(world, i, j, k);
        return super.b(world, i, j, k);
    }

    public void updateShape(IBlockAccess iblockaccess, int i, int j, int k) {
        this.d(iblockaccess.getData(i, j, k));
    }

    public void g() {
        float f = 0.1875F;

        this.a(0.0F, 0.5F - f / 2.0F, 0.0F, 1.0F, 0.5F + f / 2.0F, 1.0F);
    }

    public void d(int i) {
        float f = 0.1875F;

        if ((i & 8) != 0) {
            this.a(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.a(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if (f(i)) {
            if ((i & 3) == 0) {
                this.a(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }

            if ((i & 3) == 1) {
                this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }

            if ((i & 3) == 2) {
                this.a(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((i & 3) == 3) {
                this.a(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        }
    }

    public void attack(World world, int i, int j, int k, EntityHuman entityhuman) {}

    public boolean interact(World world, int i, int j, int k, EntityHuman entityhuman, int l, float f, float f1, float f2) {
        if (this.material == Material.ORE) {
            return true;
        } else {
            int i1 = world.getData(i, j, k);

            world.setData(i, j, k, i1 ^ 4, 2);
            world.a(entityhuman, 1003, i, j, k, 0);
            return true;
        }
    }

    public void setOpen(World world, int i, int j, int k, boolean flag) {
        int l = world.getData(i, j, k);
        boolean flag1 = (l & 4) > 0;

        if (flag1 != flag) {
            world.setData(i, j, k, l ^ 4, 2);
            world.a((EntityHuman) null, 1003, i, j, k, 0);
        }
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        if (!world.isStatic) {
            int i1 = world.getData(i, j, k);
            int j1 = i;
            int k1 = k;

            if ((i1 & 3) == 0) {
                k1 = k + 1;
            }

            if ((i1 & 3) == 1) {
                --k1;
            }

            if ((i1 & 3) == 2) {
                j1 = i + 1;
            }

            if ((i1 & 3) == 3) {
                --j1;
            }

            if (!g(world.getTypeId(j1, j, k1))) {
                world.setAir(i, j, k);
                this.c(world, i, j, k, i1, 0);
            }

            // CraftBukkit start
            if (l == 0 || l > 0 && Block.byId[l] != null && Block.byId[l].isPowerSource()) {
                org.bukkit.World bworld = world.getWorld();
                org.bukkit.block.Block block = bworld.getBlockAt(i, j, k);

                int power = block.getBlockPower();
                int oldPower = (world.getData(i, j, k) & 4) > 0 ? 15 : 0;

                if (oldPower == 0 ^ power == 0 || (Block.byId[l] != null && Block.byId[l].isPowerSource())) {
                    BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(block, oldPower, power);
                    world.getServer().getPluginManager().callEvent(eventRedstone);

                    this.setOpen(world, i, j, k, eventRedstone.getNewCurrent() > 0);
                }
                // CraftBukkit end
            }
        }
    }

    public MovingObjectPosition a(World world, int i, int j, int k, Vec3D vec3d, Vec3D vec3d1) {
        this.updateShape(world, i, j, k);
        return super.a(world, i, j, k, vec3d, vec3d1);
    }

    public int getPlacedData(World world, int i, int j, int k, int l, float f, float f1, float f2, int i1) {
        int j1 = 0;

        if (l == 2) {
            j1 = 0;
        }

        if (l == 3) {
            j1 = 1;
        }

        if (l == 4) {
            j1 = 2;
        }

        if (l == 5) {
            j1 = 3;
        }

        if (l != 1 && l != 0 && f1 > 0.5F) {
            j1 |= 8;
        }

        return j1;
    }

    public boolean canPlace(World world, int i, int j, int k, int l) {
        if (l == 0) {
            return false;
        } else if (l == 1) {
            return false;
        } else {
            if (l == 2) {
                ++k;
            }

            if (l == 3) {
                --k;
            }

            if (l == 4) {
                ++i;
            }

            if (l == 5) {
                --i;
            }

            return g(world.getTypeId(i, j, k));
        }
    }

    public static boolean f(int i) {
        return (i & 4) != 0;
    }

    private static boolean g(int i) {
        if (i <= 0) {
            return false;
        } else {
            Block block = Block.byId[i];

            return block != null && block.material.k() && block.b() || block == Block.GLOWSTONE || block instanceof BlockStepAbstract || block instanceof BlockStairs;
        }
    }
}
