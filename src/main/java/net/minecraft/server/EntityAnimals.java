package net.minecraft.server;

public abstract class EntityAnimals extends EntityCreature implements IAnimals {

    public EntityAnimals(World world) {
        super(world);
    }

    protected float a(int i, int j, int k) {
        if (l.a(i, j - 1, k) == Block.u.bi) {
            return 10F;
        } else {
            return l.l(i, j, k) - 0.5F;
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
    }

    public boolean b() {
        int i = MathHelper.b(p);
        int j = MathHelper.b(z.b);
        int k = MathHelper.b(r);

        return l.a(i, j - 1, k) == Block.u.bi && l.j(i, j, k) > 8 && super.b();
    }

    public int c() {
        return 120;
    }
}
