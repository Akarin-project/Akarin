package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

public class EntityEvokerFangs extends Entity {

    private int a;
    private boolean b;
    private int c;
    private boolean d;
    private EntityLiving e;
    private UUID f;

    public EntityEvokerFangs(World world) {
        super(EntityTypes.EVOKER_FANGS, world);
        this.c = 22;
        this.setSize(0.5F, 0.8F);
    }

    public EntityEvokerFangs(World world, double d0, double d1, double d2, float f, int i, EntityLiving entityliving) {
        this(world);
        this.a = i;
        this.a(entityliving);
        this.yaw = f * 57.295776F;
        this.setPosition(d0, d1, d2);
    }

    protected void x_() {}

    public void a(@Nullable EntityLiving entityliving) {
        this.e = entityliving;
        this.f = entityliving == null ? null : entityliving.getUniqueID();
    }

    @Nullable
    public EntityLiving getOwner() {
        if (this.e == null && this.f != null && this.world instanceof WorldServer) {
            Entity entity = ((WorldServer) this.world).getEntity(this.f);

            if (entity instanceof EntityLiving) {
                this.e = (EntityLiving) entity;
            }
        }

        return this.e;
    }

    protected void a(NBTTagCompound nbttagcompound) {
        this.a = nbttagcompound.getInt("Warmup");
        if (nbttagcompound.b("OwnerUUID")) {
            this.f = nbttagcompound.a("OwnerUUID");
        }

    }

    protected void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Warmup", this.a);
        if (this.f != null) {
            nbttagcompound.a("OwnerUUID", this.f);
        }

    }

    public void tick() {
        super.tick();
        if (this.world.isClientSide) {
            if (this.d) {
                --this.c;
                if (this.c == 14) {
                    for (int i = 0; i < 12; ++i) {
                        double d0 = this.locX + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.width * 0.5D;
                        double d1 = this.locY + 0.05D + this.random.nextDouble();
                        double d2 = this.locZ + (this.random.nextDouble() * 2.0D - 1.0D) * (double) this.width * 0.5D;
                        double d3 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;
                        double d4 = 0.3D + this.random.nextDouble() * 0.3D;
                        double d5 = (this.random.nextDouble() * 2.0D - 1.0D) * 0.3D;

                        this.world.addParticle(Particles.h, d0, d1 + 1.0D, d2, d3, d4, d5);
                    }
                }
            }
        } else if (--this.a < 0) {
            if (this.a == -8) {
                List<EntityLiving> list = this.world.a(EntityLiving.class, this.getBoundingBox().grow(0.2D, 0.0D, 0.2D));
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    EntityLiving entityliving = (EntityLiving) iterator.next();

                    this.c(entityliving);
                }
            }

            if (!this.b) {
                this.world.broadcastEntityEffect(this, (byte) 4);
                this.b = true;
            }

            if (--this.c < 0) {
                this.die();
            }
        }

    }

    private void c(EntityLiving entityliving) {
        EntityLiving entityliving1 = this.getOwner();

        if (entityliving.isAlive() && !entityliving.bl() && entityliving != entityliving1) {
            if (entityliving1 == null) {
                entityliving.damageEntity(DamageSource.MAGIC, 6.0F);
            } else {
                if (entityliving1.r(entityliving)) {
                    return;
                }

                entityliving.damageEntity(DamageSource.c(this, entityliving1), 6.0F);
            }

        }
    }
}
