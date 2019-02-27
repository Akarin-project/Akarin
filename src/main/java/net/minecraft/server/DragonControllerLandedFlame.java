package net.minecraft.server;

public class DragonControllerLandedFlame extends AbstractDragonControllerLanded {

    private int b;
    private int c;
    private EntityAreaEffectCloud d;

    public DragonControllerLandedFlame(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    public void b() {
        ++this.b;
        if (this.b % 2 == 0 && this.b < 10) {
            Vec3D vec3d = this.a.a(1.0F).a();

            vec3d.b(-0.7853982F);
            double d0 = this.a.bD.locX;
            double d1 = this.a.bD.locY + (double) (this.a.bD.length / 2.0F);
            double d2 = this.a.bD.locZ;

            for (int i = 0; i < 8; ++i) {
                double d3 = d0 + this.a.getRandom().nextGaussian() / 2.0D;
                double d4 = d1 + this.a.getRandom().nextGaussian() / 2.0D;
                double d5 = d2 + this.a.getRandom().nextGaussian() / 2.0D;

                for (int j = 0; j < 6; ++j) {
                    this.a.world.addParticle(Particles.j, d3, d4, d5, -vec3d.x * 0.07999999821186066D * (double) j, -vec3d.y * 0.6000000238418579D, -vec3d.z * 0.07999999821186066D * (double) j);
                }

                vec3d.b(0.19634955F);
            }
        }

    }

    public void c() {
        ++this.b;
        if (this.b >= 200) {
            if (this.c >= 4) {
                this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.TAKEOFF);
            } else {
                this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.SITTING_SCANNING);
            }
        } else if (this.b == 10) {
            Vec3D vec3d = (new Vec3D(this.a.bD.locX - this.a.locX, 0.0D, this.a.bD.locZ - this.a.locZ)).a();
            float f = 5.0F;
            double d0 = this.a.bD.locX + vec3d.x * 5.0D / 2.0D;
            double d1 = this.a.bD.locZ + vec3d.z * 5.0D / 2.0D;
            double d2 = this.a.bD.locY + (double) (this.a.bD.length / 2.0F);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(MathHelper.floor(d0), MathHelper.floor(d2), MathHelper.floor(d1));

            while (this.a.world.isEmpty(blockposition_mutableblockposition)) {
                --d2;
                blockposition_mutableblockposition.c(MathHelper.floor(d0), MathHelper.floor(d2), MathHelper.floor(d1));
            }

            d2 = (double) (MathHelper.floor(d2) + 1);
            this.d = new EntityAreaEffectCloud(this.a.world, d0, d2, d1);
            this.d.setSource(this.a);
            this.d.setRadius(5.0F);
            this.d.setDuration(200);
            this.d.setParticle(Particles.j);
            this.d.a(new MobEffect(MobEffects.HARM));
            this.a.world.addEntity(this.d);
        }

    }

    public void d() {
        this.b = 0;
        ++this.c;
    }

    public void e() {
        if (this.d != null) {
            this.d.die();
            this.d = null;
        }

    }

    public DragonControllerPhase<DragonControllerLandedFlame> getControllerPhase() {
        return DragonControllerPhase.SITTING_FLAMING;
    }

    public void j() {
        this.c = 0;
    }
}
