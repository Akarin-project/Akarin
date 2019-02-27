package net.minecraft.server;

public class SecondaryWorldServer extends WorldServer {

    public SecondaryWorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, DimensionManager dimensionmanager, WorldServer worldserver, MethodProfiler methodprofiler) {
        super(minecraftserver, idatamanager, worldserver.h(), new SecondaryWorldData(worldserver.getWorldData()), dimensionmanager, methodprofiler);
        worldserver.getWorldBorder().a(new IWorldBorderListener() {
            public void a(WorldBorder worldborder, double d0) {
                SecondaryWorldServer.this.getWorldBorder().setSize(d0);
            }

            public void a(WorldBorder worldborder, double d0, double d1, long i) {
                SecondaryWorldServer.this.getWorldBorder().transitionSizeBetween(d0, d1, i);
            }

            public void a(WorldBorder worldborder, double d0, double d1) {
                SecondaryWorldServer.this.getWorldBorder().setCenter(d0, d1);
            }

            public void a(WorldBorder worldborder, int i) {
                SecondaryWorldServer.this.getWorldBorder().setWarningTime(i);
            }

            public void b(WorldBorder worldborder, int i) {
                SecondaryWorldServer.this.getWorldBorder().setWarningDistance(i);
            }

            public void b(WorldBorder worldborder, double d0) {
                SecondaryWorldServer.this.getWorldBorder().setDamageAmount(d0);
            }

            public void c(WorldBorder worldborder, double d0) {
                SecondaryWorldServer.this.getWorldBorder().setDamageBuffer(d0);
            }
        });
    }

    protected void a() {}

    public SecondaryWorldServer i_() {
        String s = PersistentVillage.a(this.worldProvider);
        PersistentVillage persistentvillage = (PersistentVillage) this.a(DimensionManager.OVERWORLD, PersistentVillage::new, s);

        if (persistentvillage == null) {
            this.villages = new PersistentVillage(this);
            this.a(DimensionManager.OVERWORLD, s, (PersistentBase) this.villages);
        } else {
            this.villages = persistentvillage;
            this.villages.a((World) this);
        }

        return this;
    }

    public void t_() {
        this.worldProvider.k();
    }
}
