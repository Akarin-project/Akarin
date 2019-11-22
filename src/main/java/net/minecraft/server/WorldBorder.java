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
    public WorldServer world; // CraftBukkit

    public WorldBorder() {}

    public final boolean isInBounds(BlockPosition blockposition) { return this.a(blockposition); } // Paper - OBFHELPER
    public boolean a(BlockPosition blockposition) {
        return (double) (blockposition.getX() + 1) > this.c() && (double) blockposition.getX() < this.e() && (double) (blockposition.getZ() + 1) > this.d() && (double) blockposition.getZ() < this.f();
    }

    // Paper start
    private final BlockPosition.MutableBlockPosition mutPos = new BlockPosition.MutableBlockPosition();
    public boolean isBlockInBounds(int chunkX, int chunkZ) {
        this.mutPos.setValues(chunkX, 64, chunkZ);
        return this.isInBounds(this.mutPos);
    }
    public boolean isChunkInBounds(int chunkX, int chunkZ) {
        this.mutPos.setValues(((chunkX << 4) + 15), 64, (chunkZ << 4) + 15);
        return this.isInBounds(this.mutPos);
    }
    // Paper end

    public boolean isInBounds(ChunkCoordIntPair chunkcoordintpair) {
        return (double) chunkcoordintpair.f() > this.c() && (double) chunkcoordintpair.d() < this.e() && (double) chunkcoordintpair.g() > this.d() && (double) chunkcoordintpair.e() < this.f();
    }

    public boolean a(AxisAlignedBB axisalignedbb) {
        return axisalignedbb.maxX > this.c() && axisalignedbb.minX < this.e() && axisalignedbb.maxZ > this.d() && axisalignedbb.minZ < this.f();
    }

    public double a(Entity entity) {
        return this.b(entity.locX, entity.locZ);
    }

    public VoxelShape a() {
        return this.i.m();
    }

    public double b(double d0, double d1) {
        double d2 = d1 - this.d();
        double d3 = this.f() - d1;
        double d4 = d0 - this.c();
        double d5 = this.e() - d0;
        double d6 = Math.min(d4, d5);

        d6 = Math.min(d6, d2);
        return Math.min(d6, d3);
    }

    public double c() {
        return this.i.a();
    }

    public double d() {
        return this.i.c();
    }

    public double e() {
        return this.i.b();
    }

    public double f() {
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
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1);
        }

    }

    public double getSize() {
        return this.i.e();
    }

    public long j() {
        return this.i.g();
    }

    public double k() {
        return this.i.h();
    }

    public void setSize(double d0) {
        this.i = new WorldBorder.c(d0);
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0);
        }

    }

    public void transitionSizeBetween(double d0, double d1, long i) {
        this.i = (WorldBorder.a) (d0 == d1 ? new WorldBorder.c(d1) : new WorldBorder.b(d0, d1, i));
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.a(this, d0, d1, i);
        }

    }

    protected List<IWorldBorderListener> l() {
        return Lists.newArrayList(this.a);
    }

    public void a(IWorldBorderListener iworldborderlistener) {
        if (a.contains(iworldborderlistener)) return; // CraftBukkit
        this.a.add(iworldborderlistener);
    }

    public void a(int i) {
        this.h = i;
        this.i.j();
    }

    public int m() {
        return this.h;
    }

    public double getDamageBuffer() {
        return this.c;
    }

    public void setDamageBuffer(double d0) {
        this.c = d0;
        Iterator iterator = this.l().iterator();

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
        Iterator iterator = this.l().iterator();

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
        Iterator iterator = this.l().iterator();

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
        Iterator iterator = this.l().iterator();

        while (iterator.hasNext()) {
            IWorldBorderListener iworldborderlistener = (IWorldBorderListener) iterator.next();

            iworldborderlistener.b(this, i);
        }

    }

    public void s() {
        this.i = this.i.l();
    }

    public void a(WorldData worlddata) {
        worlddata.a(this.getSize());
        worlddata.d(this.getCenterX());
        worlddata.c(this.getCenterZ());
        worlddata.e(this.getDamageBuffer());
        worlddata.f(this.getDamageAmount());
        worlddata.h(this.getWarningDistance());
        worlddata.i(this.getWarningTime());
        worlddata.b(this.k());
        worlddata.c(this.j());
    }

    public void b(WorldData worlddata) {
        this.setCenter(worlddata.B(), worlddata.C());
        this.setDamageAmount(worlddata.H());
        this.setDamageBuffer(worlddata.G());
        this.setWarningDistance(worlddata.I());
        this.setWarningTime(worlddata.J());
        if (worlddata.E() > 0L) {
            this.transitionSizeBetween(worlddata.D(), worlddata.F(), worlddata.E());
        } else {
            this.setSize(worlddata.D());
        }

    }

    class c implements WorldBorder.a {

        private final double b;
        private double c;
        private double d;
        private double e;
        private double f;
        private VoxelShape g;

        public c(double d0) {
            this.b = d0;
            this.n();
        }

        @Override
        public double a() {
            return this.c;
        }

        @Override
        public double b() {
            return this.e;
        }

        @Override
        public double c() {
            return this.d;
        }

        @Override
        public double d() {
            return this.f;
        }

        @Override
        public double e() {
            return this.b;
        }

        @Override
        public long g() {
            return 0L;
        }

        @Override
        public double h() {
            return this.b;
        }

        private void n() {
            this.c = Math.max(WorldBorder.this.getCenterX() - this.b / 2.0D, (double) (-WorldBorder.this.h));
            this.d = Math.max(WorldBorder.this.getCenterZ() - this.b / 2.0D, (double) (-WorldBorder.this.h));
            this.e = Math.min(WorldBorder.this.getCenterX() + this.b / 2.0D, (double) WorldBorder.this.h);
            this.f = Math.min(WorldBorder.this.getCenterZ() + this.b / 2.0D, (double) WorldBorder.this.h);
            this.g = VoxelShapes.a(VoxelShapes.a, VoxelShapes.create(Math.floor(this.a()), Double.NEGATIVE_INFINITY, Math.floor(this.c()), Math.ceil(this.b()), Double.POSITIVE_INFINITY, Math.ceil(this.d())), OperatorBoolean.ONLY_FIRST);
        }

        @Override
        public void j() {
            this.n();
        }

        @Override
        public void k() {
            this.n();
        }

        @Override
        public WorldBorder.a l() {
            return this;
        }

        @Override
        public VoxelShape m() {
            return this.g;
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

        @Override
        public double a() {
            return Math.max(WorldBorder.this.getCenterX() - this.e() / 2.0D, (double) (-WorldBorder.this.h));
        }

        @Override
        public double c() {
            return Math.max(WorldBorder.this.getCenterZ() - this.e() / 2.0D, (double) (-WorldBorder.this.h));
        }

        @Override
        public double b() {
            return Math.min(WorldBorder.this.getCenterX() + this.e() / 2.0D, (double) WorldBorder.this.h);
        }

        @Override
        public double d() {
            return Math.min(WorldBorder.this.getCenterZ() + this.e() / 2.0D, (double) WorldBorder.this.h);
        }

        @Override
        public double e() {
            double d0 = (double) (SystemUtils.getMonotonicMillis() - this.e) / this.f;

            return d0 < 1.0D ? MathHelper.d(d0, this.b, this.c) : this.c;
        }

        @Override
        public long g() {
            return this.d - SystemUtils.getMonotonicMillis();
        }

        @Override
        public double h() {
            return this.c;
        }

        @Override
        public void k() {}

        @Override
        public void j() {}

        @Override
        public WorldBorder.a l() {
            return (WorldBorder.a) (this.g() <= 0L ? WorldBorder.this.new c(this.c) : this);
        }

        @Override
        public VoxelShape m() {
            return VoxelShapes.a(VoxelShapes.a, VoxelShapes.create(Math.floor(this.a()), Double.NEGATIVE_INFINITY, Math.floor(this.c()), Math.ceil(this.b()), Double.POSITIVE_INFINITY, Math.ceil(this.d())), OperatorBoolean.ONLY_FIRST);
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

        VoxelShape m();
    }
}
