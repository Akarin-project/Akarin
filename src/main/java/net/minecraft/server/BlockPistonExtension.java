package net.minecraft.server;

import java.util.ArrayList;
import java.util.Random;

public class BlockPistonExtension extends Block {

    private int a = -1;

    public BlockPistonExtension(int i, int j) {
        super(i, j, Material.PISTON);
        this.a(h);
        this.c(0.5F);
    }

    public void remove(World world, int i, int j, int k) {
        super.remove(world, i, j, k);
        int l = world.getData(i, j, k);
        if (l > 5 || l < 0) return; // CraftBukkit - fixed a piston AIOOBE issue.
        int i1 = PistonBlockTextures.a[b(l)];

        i += PistonBlockTextures.b[i1];
        j += PistonBlockTextures.c[i1];
        k += PistonBlockTextures.d[i1];
        int j1 = world.getTypeId(i, j, k);

        if (j1 == Block.PISTON.id || j1 == Block.PISTON_STICKY.id) {
            l = world.getData(i, j, k);
            if (BlockPiston.d(l)) {
                Block.byId[j1].g(world, i, j, k, l);
                world.setTypeId(i, j, k, 0);
            }
        }
    }

    public int a(int i, int j) {
        int k = b(j);

        return i == k ? (this.a >= 0 ? this.a : ((j & 8) != 0 ? this.textureId - 1 : this.textureId)) : (i == PistonBlockTextures.a[k] ? 107 : 108);
    }

    public boolean a() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public boolean canPlace(World world, int i, int j, int k) {
        return false;
    }

    public boolean canPlace(World world, int i, int j, int k, int l) {
        return false;
    }

    public int a(Random random) {
        return 0;
    }

    public void a(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, ArrayList arraylist) {
        int l = world.getData(i, j, k);

        switch (b(l)) {
        case 0:
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            this.a(0.375F, 0.25F, 0.375F, 0.625F, 1.0F, 0.625F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            break;

        case 1:
            this.a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            this.a(0.375F, 0.0F, 0.375F, 0.625F, 0.75F, 0.625F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            break;

        case 2:
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            this.a(0.25F, 0.375F, 0.25F, 0.75F, 0.625F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            break;

        case 3:
            this.a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            this.a(0.25F, 0.375F, 0.0F, 0.75F, 0.625F, 0.75F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            break;

        case 4:
            this.a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            this.a(0.375F, 0.25F, 0.25F, 0.625F, 0.75F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            break;

        case 5:
            this.a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
            this.a(0.0F, 0.375F, 0.25F, 0.75F, 0.625F, 0.75F);
            super.a(world, i, j, k, axisalignedbb, arraylist);
        }

        this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    public void a(IBlockAccess iblockaccess, int i, int j, int k) {
        int l = iblockaccess.getData(i, j, k);

        switch (b(l)) {
        case 0:
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.25F, 1.0F);
            break;

        case 1:
            this.a(0.0F, 0.75F, 0.0F, 1.0F, 1.0F, 1.0F);
            break;

        case 2:
            this.a(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.25F);
            break;

        case 3:
            this.a(0.0F, 0.0F, 0.75F, 1.0F, 1.0F, 1.0F);
            break;

        case 4:
            this.a(0.0F, 0.0F, 0.0F, 0.25F, 1.0F, 1.0F);
            break;

        case 5:
            this.a(0.75F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        int i1 = b(world.getData(i, j, k));
        if (i1 > 5 || i1 < 0) return; // CraftBukkit - fixed a piston AIOOBE issue.
        int j1 = world.getTypeId(i - PistonBlockTextures.b[i1], j - PistonBlockTextures.c[i1], k - PistonBlockTextures.d[i1]);

        if (j1 != Block.PISTON.id && j1 != Block.PISTON_STICKY.id) {
            world.setTypeId(i, j, k, 0);
        } else {
            Block.byId[j1].doPhysics(world, i - PistonBlockTextures.b[i1], j - PistonBlockTextures.c[i1], k - PistonBlockTextures.d[i1], l);
        }
    }

    public static int b(int i) {
        return i & 7;
    }
}
