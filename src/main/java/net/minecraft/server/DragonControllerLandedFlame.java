package net.minecraft.server;

public class DragonControllerLandedFlame extends AbstractDragonControllerLanded {

    private int b;
    private int c;
    private EntityAreaEffectCloud d;

    public DragonControllerLandedFlame(EntityEnderDragon entityenderdragon) {
        super(entityenderdragon);
    }

    @Override
    public void b() {
        ++this.b;
        if (this.b % 2 == 0 && this.b < 10) {
            Vec3D vec3d = this.a.u(1.0F).d();

            vec3d.b(-0.7853982F);
            double d0 = this.a.bA.locX;
            double d1 = this.a.bA.locY + (double) (this.a.bA.getHeight() / 2.0F);
            double d2 = this.a.bA.locZ;

            for (int i = 0; i < 8; ++i) {
                double d3 = d0 + this.a.getRandom().nextGaussian() / 2.0D;
                double d4 = d1 + this.a.getRandom().nextGaussian() / 2.0D;
                double d5 = d2 + this.a.getRandom().nextGaussian() / 2.0D;

                for (int j = 0; j < 6; ++j) {
                    this.a.world.addParticle(Particles.DRAGON_BREATH, d3, d4, d5, -vec3d.x * 0.07999999821186066D * (double) j, -vec3d.y * 0.6000000238418579D, -vec3d.z * 0.07999999821186066D * (double) j);
                }

                vec3d.b(0.19634955F);
            }
        }

    }

    @Override
    public void c() {
        ++this.b;
        if (this.b >= 200) {
            if (this.c >= 4) {
                this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.TAKEOFF);
            } else {
                this.a.getDragonControllerManager().setControllerPhase(DragonControllerPhase.SITTING_SCANNING);
            }
        } else if (this.b == 10) {
            Vec3D vec3d = (new Vec3D(this.a.bA.locX - this.a.locX, 0.0D, this.a.bA.locZ - this.a.locZ)).d();
            float f = 5.0F;
            double d0 = this.a.bA.locX + vec3d.x * 5.0D / 2.0D;
            double d1 = this.a.bA.locZ + vec3d.z * 5.0D / 2.0D;
            double d2 = this.a.bA.locY + (double) (this.a.bA.getHeight() / 2.0F);
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(d0, d2, d1);

            while (this.a.world.isEmpty(blockposition_mutableblockposition ) && d2 > 0) { // Paper
                --d2;
                blockposition_mutableblockposition.c(d0, d2, d1);
            }

            d2 = (double) (MathHelper.floor(d2) + 1);
            this.d = new EntityAreaEffectCloud(this.a.world, d0, d2, d1);
            this.d.setSource(this.a);
            this.d.setRadius(5.0F);
            this.d.setDuration(200);
            this.d.setParticle(Particles.DRAGON_BREATH);
            this.d.addEffect(new MobEffect(MobEffects.HARM));
            if (new com.destroystokyo.paper.event.entity.EnderDragonFlameEvent((org.bukkit.entity.EnderDragon) this.a.getBukkitEntity(), (org.bukkit.entity.AreaEffectCloud) this.d.getBukkitEntity()).callEvent()) { // Paper
            this.a.world.addEntity(this.d);
            } else {
                this.removeAreaEffect();
            }
        }

    }

    @Override
    public void d() {
        this.b = 0;
        ++this.c;
    }

    public void removeAreaEffect() { this.e(); } // Paper - OBFHELPER
    @Override
    public void e() {
        if (this.d != null) {
            this.d.die();
            this.d = null;
        }

    }

    @Override
    public DragonControllerPhase<DragonControllerLandedFlame> getControllerPhase() {
        return DragonControllerPhase.SITTING_FLAMING;
    }

    public void j() {
        this.c = 0;
    }
}
