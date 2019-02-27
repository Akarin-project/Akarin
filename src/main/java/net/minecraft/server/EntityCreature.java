package net.minecraft.server;

// CraftBukkit start
import org.bukkit.event.entity.EntityUnleashEvent;
// CraftBukkit end

public abstract class EntityCreature extends EntityInsentient {

    private BlockPosition a;
    private float b;

    protected EntityCreature(EntityTypes<?> entitytypes, World world) {
        super(entitytypes, world);
        this.a = BlockPosition.ZERO;
        this.b = -1.0F;
    }

    public float a(BlockPosition blockposition) {
        return this.a(blockposition, (IWorldReader) this.world);
    }

    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return 0.0F;
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        return super.a(generatoraccess, flag) && this.a(new BlockPosition(this.locX, this.getBoundingBox().minY, this.locZ), (IWorldReader) generatoraccess) >= 0.0F;
    }

    public boolean dr() {
        return !this.navigation.p();
    }

    public boolean ds() {
        return this.f(new BlockPosition(this));
    }

    public boolean f(BlockPosition blockposition) {
        return this.b == -1.0F ? true : this.a.n(blockposition) < (double) (this.b * this.b);
    }

    public void a(BlockPosition blockposition, int i) {
        this.a = blockposition;
        this.b = (float) i;
    }

    public BlockPosition dt() {
        return this.a;
    }

    public float du() {
        return this.b;
    }

    public void dv() {
        this.b = -1.0F;
    }

    public boolean dw() {
        return this.b != -1.0F;
    }

    protected void dl() {
        super.dl();
        if (this.isLeashed() && this.getLeashHolder() != null && this.getLeashHolder().world == this.world) {
            Entity entity = this.getLeashHolder();

            this.a(new BlockPosition((int) entity.locX, (int) entity.locY, (int) entity.locZ), 5);
            float f = this.g(entity);

            if (this instanceof EntityTameableAnimal && ((EntityTameableAnimal) this).isSitting()) {
                if (f > 10.0F) {
                    this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
                    this.unleash(true, true);
                }

                return;
            }

            this.u(f);
            if (f > 10.0F) {
                this.world.getServer().getPluginManager().callEvent(new EntityUnleashEvent(this.getBukkitEntity(), EntityUnleashEvent.UnleashReason.DISTANCE)); // CraftBukkit
                this.unleash(true, true);
                this.goalSelector.c(1);
            } else if (f > 6.0F) {
                double d0 = (entity.locX - this.locX) / (double) f;
                double d1 = (entity.locY - this.locY) / (double) f;
                double d2 = (entity.locZ - this.locZ) / (double) f;

                this.motX += d0 * Math.abs(d0) * 0.4D;
                this.motY += d1 * Math.abs(d1) * 0.4D;
                this.motZ += d2 * Math.abs(d2) * 0.4D;
            } else {
                this.goalSelector.d(1);
                float f1 = 2.0F;
                Vec3D vec3d = (new Vec3D(entity.locX - this.locX, entity.locY - this.locY, entity.locZ - this.locZ)).a().a((double) Math.max(f - 2.0F, 0.0F));

                this.getNavigation().a(this.locX + vec3d.x, this.locY + vec3d.y, this.locZ + vec3d.z, this.dx());
            }
        }

    }

    protected double dx() {
        return 1.0D;
    }

    protected void u(float f) {}
}
