package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
// CraftBukkit end

public class EntityArrow extends Entity {

    private int d = -1;
    private int e = -1;
    private int f = -1;
    private int g = 0;
    private int h = 0;
    private boolean inGround = false;
    public boolean fromPlayer = false;
    public int shake = 0;
    public EntityLiving shooter;
    private int j;
    private int k = 0;

    public EntityArrow(World world) {
        super(world);
        this.b(0.5F, 0.5F);
    }

    public EntityArrow(World world, double d0, double d1, double d2) {
        super(world);
        this.b(0.5F, 0.5F);
        this.setPosition(d0, d1, d2);
        this.height = 0.0F;
    }

    public EntityArrow(World world, EntityLiving entityliving) {
        super(world);
        this.shooter = entityliving;
        this.fromPlayer = entityliving instanceof EntityHuman;
        this.b(0.5F, 0.5F);
        this.setPositionRotation(entityliving.locX, entityliving.locY + (double) entityliving.t(), entityliving.locZ, entityliving.yaw, entityliving.pitch);
        this.locX -= (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.locY -= 0.10000000149011612D;
        this.locZ -= (double) (MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * 0.16F);
        this.setPosition(this.locX, this.locY, this.locZ);
        this.height = 0.0F;
        this.motX = (double) (-MathHelper.sin(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.motZ = (double) (MathHelper.cos(this.yaw / 180.0F * 3.1415927F) * MathHelper.cos(this.pitch / 180.0F * 3.1415927F));
        this.motY = (double) (-MathHelper.sin(this.pitch / 180.0F * 3.1415927F));
        this.a(this.motX, this.motY, this.motZ, 1.5F, 1.0F);
    }

    protected void b() {}

    public void a(double d0, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.a(d0 * d0 + d1 * d1 + d2 * d2);

        d0 /= (double) f2;
        d1 /= (double) f2;
        d2 /= (double) f2;
        d0 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d1 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d2 += this.random.nextGaussian() * 0.007499999832361937D * (double) f1;
        d0 *= (double) f;
        d1 *= (double) f;
        d2 *= (double) f;
        this.motX = d0;
        this.motY = d1;
        this.motZ = d2;
        float f3 = MathHelper.a(d0 * d0 + d2 * d2);

        this.lastYaw = this.yaw = (float) (Math.atan2(d0, d2) * 180.0D / 3.1415927410125732D);
        this.lastPitch = this.pitch = (float) (Math.atan2(d1, (double) f3) * 180.0D / 3.1415927410125732D);
        this.j = 0;
    }

    public void m_() {
        super.m_();
        if (this.lastPitch == 0.0F && this.lastYaw == 0.0F) {
            float f = MathHelper.a(this.motX * this.motX + this.motZ * this.motZ);

            this.lastYaw = this.yaw = (float) (Math.atan2(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);
            this.lastPitch = this.pitch = (float) (Math.atan2(this.motY, (double) f) * 180.0D / 3.1415927410125732D);
        }

        int i = this.world.getTypeId(this.d, this.e, this.f);

        if (i > 0) {
            Block.byId[i].a(this.world, this.d, this.e, this.f);
            AxisAlignedBB axisalignedbb = Block.byId[i].e(this.world, this.d, this.e, this.f);

            if (axisalignedbb != null && axisalignedbb.a(Vec3D.create(this.locX, this.locY, this.locZ))) {
                this.inGround = true;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            i = this.world.getTypeId(this.d, this.e, this.f);
            int j = this.world.getData(this.d, this.e, this.f);

            if (i == this.g && j == this.h) {
                ++this.j;
                if (this.j == 1200) {
                    this.die();
                }
            } else {
                this.inGround = false;
                this.motX *= (double) (this.random.nextFloat() * 0.2F);
                this.motY *= (double) (this.random.nextFloat() * 0.2F);
                this.motZ *= (double) (this.random.nextFloat() * 0.2F);
                this.j = 0;
                this.k = 0;
            }
        } else {
            ++this.k;
            Vec3D vec3d = Vec3D.create(this.locX, this.locY, this.locZ);
            Vec3D vec3d1 = Vec3D.create(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            MovingObjectPosition movingobjectposition = this.world.rayTrace(vec3d, vec3d1, false, true);

            vec3d = Vec3D.create(this.locX, this.locY, this.locZ);
            vec3d1 = Vec3D.create(this.locX + this.motX, this.locY + this.motY, this.locZ + this.motZ);
            if (movingobjectposition != null) {
                vec3d1 = Vec3D.create(movingobjectposition.f.a, movingobjectposition.f.b, movingobjectposition.f.c);
            }

            Entity entity = null;
            List list = this.world.b((Entity) this, this.boundingBox.a(this.motX, this.motY, this.motZ).b(1.0D, 1.0D, 1.0D));
            double d0 = 0.0D;

            float f1;

            for (int k = 0; k < list.size(); ++k) {
                Entity entity1 = (Entity) list.get(k);

                if (entity1.l_() && (entity1 != this.shooter || this.k >= 5)) {
                    f1 = 0.3F;
                    AxisAlignedBB axisalignedbb1 = entity1.boundingBox.b((double) f1, (double) f1, (double) f1);
                    MovingObjectPosition movingobjectposition1 = axisalignedbb1.a(vec3d, vec3d1);

                    if (movingobjectposition1 != null) {
                        double d1 = vec3d.a(movingobjectposition1.f);

                        if (d1 < d0 || d0 == 0.0D) {
                            entity = entity1;
                            d0 = d1;
                        }
                    }
                }
            }

            if (entity != null) {
                movingobjectposition = new MovingObjectPosition(entity);
            }

            float f2;

            if (movingobjectposition != null) {
                // CraftBukkit start
                ProjectileHitEvent phe = new ProjectileHitEvent((Projectile) this.getBukkitEntity());
                this.world.getServer().getPluginManager().callEvent(phe);
                // CraftBukkit end
                if (movingobjectposition.entity != null) {
                    // CraftBukkit start
                    boolean stick;
                    if (entity instanceof EntityLiving) {
                        org.bukkit.Server server = this.world.getServer();

                        // TODO decide if we should create DamageCause.ARROW, DamageCause.PROJECTILE
                        // or leave as DamageCause.ENTITY_ATTACK
                        org.bukkit.entity.Entity damagee = movingobjectposition.entity.getBukkitEntity();
                        Projectile projectile = (Projectile) this.getBukkitEntity();
                        // TODO deal with arrows being fired from a non-entity

                        EntityDamageByEntityEvent event = new EntityDamageByEntityEvent(projectile, damagee, EntityDamageEvent.DamageCause.PROJECTILE, 4);
                        server.getPluginManager().callEvent(event);
                        this.shooter = (projectile.getShooter() == null) ? null : ((CraftLivingEntity) projectile.getShooter()).getHandle();

                        if (event.isCancelled()) {
                            stick = !projectile.doesBounce();
                        } else {
                            // this function returns if the arrow should stick in or not, i.e. !bounce
                            stick = movingobjectposition.entity.damageEntity(this, event.getDamage());
                        }
                    } else {
                        stick = movingobjectposition.entity.damageEntity(this.shooter, 4);
                    }
                    if (stick) {
                        // CraftBukkit end
                        this.world.makeSound(this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                        this.die();
                    } else {
                        this.motX *= -0.10000000149011612D;
                        this.motY *= -0.10000000149011612D;
                        this.motZ *= -0.10000000149011612D;
                        this.yaw += 180.0F;
                        this.lastYaw += 180.0F;
                        this.k = 0;
                    }
                } else {
                    this.d = movingobjectposition.b;
                    this.e = movingobjectposition.c;
                    this.f = movingobjectposition.d;
                    this.g = this.world.getTypeId(this.d, this.e, this.f);
                    this.h = this.world.getData(this.d, this.e, this.f);
                    this.motX = (double) ((float) (movingobjectposition.f.a - this.locX));
                    this.motY = (double) ((float) (movingobjectposition.f.b - this.locY));
                    this.motZ = (double) ((float) (movingobjectposition.f.c - this.locZ));
                    f2 = MathHelper.a(this.motX * this.motX + this.motY * this.motY + this.motZ * this.motZ);
                    this.locX -= this.motX / (double) f2 * 0.05000000074505806D;
                    this.locY -= this.motY / (double) f2 * 0.05000000074505806D;
                    this.locZ -= this.motZ / (double) f2 * 0.05000000074505806D;
                    this.world.makeSound(this, "random.drr", 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
                    this.inGround = true;
                    this.shake = 7;
                }
            }

            this.locX += this.motX;
            this.locY += this.motY;
            this.locZ += this.motZ;
            f2 = MathHelper.a(this.motX * this.motX + this.motZ * this.motZ);
            this.yaw = (float) (Math.atan2(this.motX, this.motZ) * 180.0D / 3.1415927410125732D);

            for (this.pitch = (float) (Math.atan2(this.motY, (double) f2) * 180.0D / 3.1415927410125732D); this.pitch - this.lastPitch < -180.0F; this.lastPitch -= 360.0F) {
                ;
            }

            while (this.pitch - this.lastPitch >= 180.0F) {
                this.lastPitch += 360.0F;
            }

            while (this.yaw - this.lastYaw < -180.0F) {
                this.lastYaw -= 360.0F;
            }

            while (this.yaw - this.lastYaw >= 180.0F) {
                this.lastYaw += 360.0F;
            }

            this.pitch = this.lastPitch + (this.pitch - this.lastPitch) * 0.2F;
            this.yaw = this.lastYaw + (this.yaw - this.lastYaw) * 0.2F;
            float f3 = 0.99F;

            f1 = 0.03F;
            if (this.ad()) {
                for (int l = 0; l < 4; ++l) {
                    float f4 = 0.25F;

                    this.world.a("bubble", this.locX - this.motX * (double) f4, this.locY - this.motY * (double) f4, this.locZ - this.motZ * (double) f4, this.motX, this.motY, this.motZ);
                }

                f3 = 0.8F;
            }

            this.motX *= (double) f3;
            this.motY *= (double) f3;
            this.motZ *= (double) f3;
            this.motY -= (double) f1;
            this.setPosition(this.locX, this.locY, this.locZ);
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.a("xTile", (short) this.d);
        nbttagcompound.a("yTile", (short) this.e);
        nbttagcompound.a("zTile", (short) this.f);
        nbttagcompound.a("inTile", (byte) this.g);
        nbttagcompound.a("inData", (byte) this.h);
        nbttagcompound.a("shake", (byte) this.shake);
        nbttagcompound.a("inGround", (byte) (this.inGround ? 1 : 0));
        nbttagcompound.a("player", this.fromPlayer);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.d = nbttagcompound.d("xTile");
        this.e = nbttagcompound.d("yTile");
        this.f = nbttagcompound.d("zTile");
        this.g = nbttagcompound.c("inTile") & 255;
        this.h = nbttagcompound.c("inData") & 255;
        this.shake = nbttagcompound.c("shake") & 255;
        this.inGround = nbttagcompound.c("inGround") == 1;
        this.fromPlayer = nbttagcompound.m("player");
    }

    public void b(EntityHuman entityhuman) {
        if (!this.world.isStatic) {
            // CraftBukkit start
            ItemStack itemstack = new ItemStack(Item.ARROW, 1);
            if (this.inGround && this.fromPlayer && this.shake <= 0 && entityhuman.inventory.canHold(itemstack) > 0) {
                net.minecraft.server.EntityItem item = new net.minecraft.server.EntityItem(this.world, this.locX, this.locY, this.locZ, itemstack);

                PlayerPickupItemEvent event = new PlayerPickupItemEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), new org.bukkit.craftbukkit.entity.CraftItem(this.world.getServer(), item), 0);
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return;
                }
            }
            // CraftBukkit end

            if (this.inGround && this.fromPlayer && this.shake <= 0 && entityhuman.inventory.pickup(new ItemStack(Item.ARROW, 1))) {
                this.world.makeSound(this, "random.pop", 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
                entityhuman.receive(this, 1);
                this.die();
            }
        }
    }
}
