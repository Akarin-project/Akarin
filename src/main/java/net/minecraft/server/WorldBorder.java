package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;

public class WorldBorder {

    private final List<IWorldBorderListener> a = Lists.newArrayList();
    private double b = 0.2D;
    private double c = 5.0D;
    private int d = 15;
    private int e = 5;
    private double f;
    private double g;
    private int h = 29999984;
    private WorldBorder.a i = new WorldBorder.c(6.0E7D);

    public WorldBorder() {}

    public boolean a(BlockPosition blockposition) {
        return (double) (blockposition.getX() + 1) > this.b() && (double) blockposition.getX() < this.d() && (double) (blockposition.getZ() + 1) > this.c() && (double) blockposition.getZ() < this.e();
    }

    public boolean isInBounds(ChunkCoordIntPair chunkcoordintpair) {
        return (double) chunkcoordintpair.f() > this.b() && (double) chunkcoordintpair.d() < this.d() && (double) chunkcoordintpair.g() > this.c() && (double) chunkcoordintpair.e() < this.e();
    }

    public boolean a(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.maxX > this.b() && axisalignedbb.minX < this.d() && axisalignedbb.maxZ > this.c() && axisalignedbb.minZ < this.e();
    }

    public double a(Entity entity) {
        return this.b(entity.locX, entity.locZ);
    }

    public double b(double d0, double d1) {
        double d2 = d1 - this.c();
        double d3 = this.e() - d1;
        double d4 = d0 - this.b();
        double d5 = this.d() - d0;
        double d6 = Math.min(d4, d5);

        d6 = Math.min(d6, d2);
        return Math.min(d6, d3);
    }

    public double b() {
        return this.i.a();
    }

    public double c() {
        return this.i.c();
    }

    public double d() {
        return this.i.b();
    }

    public double e() {
        return this.i.d();
    }

    public double getCenterX() {
        return this.f;
    }

    public double getCenterZ() {
        return this.g;
    }

    public void setCenter(double d0, double d1) {
        this.f = d0;
        this.g = d1;
        this.i.k();
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1);
        }

    }

    public double getSize() {
        return this.i.e();
    }

    public long i() {
        return this.i.g();
    }

    public double j() {
        return this.i.h();
    }

    public void setSize(double d0) {
        this.i = new WorldBorder.c(d0);
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0);
        }

    }

    public void transitionSizeBetween(double d0, double d1, long i) {
        this.i = (WorldBorder.a) (d0 != d1 ? new WorldBorder.b(d0, d1, i) : new WorldBorder.c(d1));
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1, i);
        }

    }

    protected List<IWorldBorderListener> k() {
        return Lists.newArrayList(this.a);
    }

    public void a(IWorldBorderListener iworldborderlistener) {
        this.a.add(iworldborderlistener);
    }

    public void a(int i) {
        this.h = i;
        this.i.j();
    }

    public int l() {
        return this.h;
    }

    public double getDamageBuffer() {
        return this.c;
    }

    public void setDamageBuffer(double d0) {
        this.c = d0;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.c(this, d0);
        }

    }

    public double getDamageAmount() {
        return this.b;
    }

    public void setDamageAmount(double d0) {
        this.b = d0;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, d0);
        }

    }

    public int getWarningTime() {
        return this.d;
    }

    public void setWarningTime(int i) {
        this.d = i;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, i);
        }

    }

    public int getWarningDistance() {
        return this.e;
    }

    public void setWarningDistance(int i) {
        this.e = i;
        Iterator iterator = this.k().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, i);
        }

    }

    public void r() {
        this.i = this.i.l();
    }

    class c implements WorldBorder.a {

        private final double b;
        private double c;
        private double d;
        private double e;
        private double f;

        public c(double d0) {
            this.b = d0;
            this.m();
        }

        public double a() {
            return this.c;
        }

        public double b() {
            return this.e;
        }

        public double c() {
            return this.d;
        }

        public double d() {
            return this.f;
        }

        public double e() {
            return this.b;
        }

        public long g() {
            return 0L;
        }

        public double h() {
            return this.b;
        }

        private void m() {
            this.c = Math.max(WorldBorder.this.getCenterX() - this.b / 2.0D, (double) (-WorldBorder.this.h));
            this.d = Math.max(WorldBorder.this.getCenterZ() - this.b / 2.0D, (double) (-WorldBorder.this.h));
            this.e = Math.min(WorldBorder.this.getCenterX() + this.b / 2.0D, (double) WorldBorder.this.h);
            this.f = Math.min(WorldBorder.this.getCenterZ() + this.b / 2.0D, (double) WorldBorder.this.h);
        }

        public void j() {
            this.m();
        }

        public void k() {
            this.m();
        }

        public WorldBorder.a l() {
            return this;
        }
    }

    class b implements WorldBorder.a {

        private final double b;
        private final double c;
        private final long d;
        private final long e;
        private final double f;

        private b(double d0, double d1, long i) {
            this.b = d0;
            this.c = d1;
            this.f = (double) i;
            this.e = SystemUtils.getMonotonicMillis();
            this.d = this.e + i;
        }

        public double a() {
            return Math.max(WorldBorder.this.getCenterX() - this.e() / 2.0D, (double) (-WorldBorder.this.h));
        }

        public double c() {
            return Math.max(WorldBorder.this.getCenterZ() - this.e() / 2.0D, (double) (-WorldBorder.this.h));
        }

        public double b() {
            return Math.min(WorldBorder.this.getCenterX() + this.e() / 2.0D, (double) WorldBorder.this.h);
        }

        public double d() {
            return Math.min(WorldBorder.this.getCenterZ() + this.e() / 2.0D, (double) WorldBorder.this.h);
        }

        public double e() {
            double d0 = (double) (SystemUtils.getMonotonicMillis() - this.e) / this.f;

            return d0 < 1.0D ? this.b + (this.c - this.b) * d0 : this.c;
        }

        public long g() {
            return this.d - SystemUtils.getMonotonicMillis();
        }

        public double h() {
            return this.c;
        }

        public void k() {}

        public void j() {}

        public WorldBorder.a l() {
            return (WorldBorder.a) (this.g() <= 0L ? WorldBorder.this.new c(this.c) : this);
        }
    }

    interface a {

        double a();

        double b();

        double c();

        double d();

        double e();

        long g();

        double h();

        void j();

        void k();

        WorldBorder.a l();
    }
}
