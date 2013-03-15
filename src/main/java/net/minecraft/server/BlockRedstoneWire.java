package net.minecraft.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockRedstoneWire extends Block {

    private boolean a = true;
    private Set b = new HashSet();

    public BlockRedstoneWire(int i) {
        super(i, Material.ORIENTABLE);
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    public AxisAlignedBB b(World world, int i, int j, int k) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public int d() {
        return 5;
    }

    public boolean canPlace(World world, int i, int j, int k) {
        return world.w(i, j - 1, k) || world.getTypeId(i, j - 1, k) == Block.GLOWSTONE.id;
    }

    private void k(World world, int i, int j, int k) {
        this.a(world, i, j, k, i, j, k);
        ArrayList arraylist = new ArrayList(this.b);

        this.b.clear();

        for (int l = 0; l < arraylist.size(); ++l) {
            ChunkPosition chunkposition = (ChunkPosition) arraylist.get(l);

            world.applyPhysics(chunkposition.x, chunkposition.y, chunkposition.z, this.id);
        }
    }

    private void a(World world, int i, int j, int k, int l, int i1, int j1) {
        int k1 = world.getData(i, j, k);
        byte b0 = 0;
        int l1 = this.getPower(world, l, i1, j1, b0);

        this.a = false;
        int i2 = world.getHighestNeighborSignal(i, j, k);

        this.a = true;
        if (i2 > 0 && i2 > l1 - 1) {
            l1 = i2;
        }

        int j2 = 0;

        for (int k2 = 0; k2 < 4; ++k2) {
            int l2 = i;
            int i3 = k;

            if (k2 == 0) {
                l2 = i - 1;
            }

            if (k2 == 1) {
                ++l2;
            }

            if (k2 == 2) {
                i3 = k - 1;
            }

            if (k2 == 3) {
                ++i3;
            }

            if (l2 != l || i3 != j1) {
                j2 = this.getPower(world, l2, j, i3, j2);
            }

            if (world.u(l2, j, i3) && !world.u(i, j + 1, k)) {
                if ((l2 != l || i3 != j1) && j >= i1) {
                    j2 = this.getPower(world, l2, j + 1, i3, j2);
                }
            } else if (!world.u(l2, j, i3) && (l2 != l || i3 != j1) && j <= i1) {
                j2 = this.getPower(world, l2, j - 1, i3, j2);
            }
        }

        if (j2 > l1) {
            l1 = j2 - 1;
        } else if (l1 > 0) {
            --l1;
        } else {
            l1 = 0;
        }

        if (i2 > l1 - 1) {
            l1 = i2;
        }

        // CraftBukkit start
        if (k1 != l1) {
            BlockRedstoneEvent event = new BlockRedstoneEvent(world.getWorld().getBlockAt(i, j, k), k1, l1);
            world.getServer().getPluginManager().callEvent(event);

            l1 = event.getNewCurrent();
        }
        // CraftBukkit end

        if (k1 != l1) {
            world.setData(i, j, k, l1, 2);
            this.b.add(new ChunkPosition(i, j, k));
            this.b.add(new ChunkPosition(i - 1, j, k));
            this.b.add(new ChunkPosition(i + 1, j, k));
            this.b.add(new ChunkPosition(i, j - 1, k));
            this.b.add(new ChunkPosition(i, j + 1, k));
            this.b.add(new ChunkPosition(i, j, k - 1));
            this.b.add(new ChunkPosition(i, j, k + 1));
        }
    }

    private void m(World world, int i, int j, int k) {
        if (world.getTypeId(i, j, k) == this.id) {
            world.applyPhysics(i, j, k, this.id);
            world.applyPhysics(i - 1, j, k, this.id);
            world.applyPhysics(i + 1, j, k, this.id);
            world.applyPhysics(i, j, k - 1, this.id);
            world.applyPhysics(i, j, k + 1, this.id);
            world.applyPhysics(i, j - 1, k, this.id);
            world.applyPhysics(i, j + 1, k, this.id);
        }
    }

    public void onPlace(World world, int i, int j, int k) {
        super.onPlace(world, i, j, k);
        if (!world.isStatic) {
            this.k(world, i, j, k);
            world.applyPhysics(i, j + 1, k, this.id);
            world.applyPhysics(i, j - 1, k, this.id);
            this.m(world, i - 1, j, k);
            this.m(world, i + 1, j, k);
            this.m(world, i, j, k - 1);
            this.m(world, i, j, k + 1);
            if (world.u(i - 1, j, k)) {
                this.m(world, i - 1, j + 1, k);
            } else {
                this.m(world, i - 1, j - 1, k);
            }

            if (world.u(i + 1, j, k)) {
                this.m(world, i + 1, j + 1, k);
            } else {
                this.m(world, i + 1, j - 1, k);
            }

            if (world.u(i, j, k - 1)) {
                this.m(world, i, j + 1, k - 1);
            } else {
                this.m(world, i, j - 1, k - 1);
            }

            if (world.u(i, j, k + 1)) {
                this.m(world, i, j + 1, k + 1);
            } else {
                this.m(world, i, j - 1, k + 1);
            }
        }
    }

    public void remove(World world, int i, int j, int k, int l, int i1) {
        super.remove(world, i, j, k, l, i1);
        if (!world.isStatic) {
            world.applyPhysics(i, j + 1, k, this.id);
            world.applyPhysics(i, j - 1, k, this.id);
            world.applyPhysics(i + 1, j, k, this.id);
            world.applyPhysics(i - 1, j, k, this.id);
            world.applyPhysics(i, j, k + 1, this.id);
            world.applyPhysics(i, j, k - 1, this.id);
            this.k(world, i, j, k);
            this.m(world, i - 1, j, k);
            this.m(world, i + 1, j, k);
            this.m(world, i, j, k - 1);
            this.m(world, i, j, k + 1);
            if (world.u(i - 1, j, k)) {
                this.m(world, i - 1, j + 1, k);
            } else {
                this.m(world, i - 1, j - 1, k);
            }

            if (world.u(i + 1, j, k)) {
                this.m(world, i + 1, j + 1, k);
            } else {
                this.m(world, i + 1, j - 1, k);
            }

            if (world.u(i, j, k - 1)) {
                this.m(world, i, j + 1, k - 1);
            } else {
                this.m(world, i, j - 1, k - 1);
            }

            if (world.u(i, j, k + 1)) {
                this.m(world, i, j + 1, k + 1);
            } else {
                this.m(world, i, j - 1, k + 1);
            }
        }
    }

    // CraftBukkit - private -> public
    public int getPower(World world, int i, int j, int k, int l) {
        if (world.getTypeId(i, j, k) != this.id) {
            return l;
        } else {
            int i1 = world.getData(i, j, k);

            return i1 > l ? i1 : l;
        }
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        if (!world.isStatic) {
            boolean flag = this.canPlace(world, i, j, k);

            if (flag) {
                this.k(world, i, j, k);
            } else {
                this.c(world, i, j, k, 0, 0);
                world.setAir(i, j, k);
            }

            super.doPhysics(world, i, j, k, l);
        }
    }

    public int getDropType(int i, Random random, int j) {
        return Item.REDSTONE.id;
    }

    public int c(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        return !this.a ? 0 : this.b(iblockaccess, i, j, k, l);
    }

    public int b(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (!this.a) {
            return 0;
        } else {
            int i1 = iblockaccess.getData(i, j, k);

            if (i1 == 0) {
                return 0;
            } else if (l == 1) {
                return i1;
            } else {
                boolean flag = g(iblockaccess, i - 1, j, k, 1) || !iblockaccess.u(i - 1, j, k) && g(iblockaccess, i - 1, j - 1, k, -1);
                boolean flag1 = g(iblockaccess, i + 1, j, k, 3) || !iblockaccess.u(i + 1, j, k) && g(iblockaccess, i + 1, j - 1, k, -1);
                boolean flag2 = g(iblockaccess, i, j, k - 1, 2) || !iblockaccess.u(i, j, k - 1) && g(iblockaccess, i, j - 1, k - 1, -1);
                boolean flag3 = g(iblockaccess, i, j, k + 1, 0) || !iblockaccess.u(i, j, k + 1) && g(iblockaccess, i, j - 1, k + 1, -1);

                if (!iblockaccess.u(i, j + 1, k)) {
                    if (iblockaccess.u(i - 1, j, k) && g(iblockaccess, i - 1, j + 1, k, -1)) {
                        flag = true;
                    }

                    if (iblockaccess.u(i + 1, j, k) && g(iblockaccess, i + 1, j + 1, k, -1)) {
                        flag1 = true;
                    }

                    if (iblockaccess.u(i, j, k - 1) && g(iblockaccess, i, j + 1, k - 1, -1)) {
                        flag2 = true;
                    }

                    if (iblockaccess.u(i, j, k + 1) && g(iblockaccess, i, j + 1, k + 1, -1)) {
                        flag3 = true;
                    }
                }

                return !flag2 && !flag1 && !flag && !flag3 && l >= 2 && l <= 5 ? i1 : (l == 2 && flag2 && !flag && !flag1 ? i1 : (l == 3 && flag3 && !flag && !flag1 ? i1 : (l == 4 && flag && !flag2 && !flag3 ? i1 : (l == 5 && flag1 && !flag2 && !flag3 ? i1 : 0))));
            }
        }
    }

    public boolean isPowerSource() {
        return this.a;
    }

    public static boolean f(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        int i1 = iblockaccess.getTypeId(i, j, k);

        if (i1 == Block.REDSTONE_WIRE.id) {
            return true;
        } else if (i1 == 0) {
            return false;
        } else if (!Block.DIODE_OFF.g(i1)) {
            return Block.byId[i1].isPowerSource() && l != -1;
        } else {
            int j1 = iblockaccess.getData(i, j, k);

            return l == (j1 & 3) || l == Direction.f[j1 & 3];
        }
    }

    public static boolean g(IBlockAccess iblockaccess, int i, int j, int k, int l) {
        if (f(iblockaccess, i, j, k, l)) {
            return true;
        } else {
            int i1 = iblockaccess.getTypeId(i, j, k);

            if (i1 == Block.DIODE_ON.id) {
                int j1 = iblockaccess.getData(i, j, k);

                return l == (j1 & 3);
            } else {
                return false;
            }
        }
    }
}
