package net.minecraft.server;

import javax.annotation.Nullable;

public class EntityThrownTrident extends EntityArrow {

    private static final DataWatcherObject<Byte> h = DataWatcher.a(EntityThrownTrident.class, DataWatcherRegistry.a);
    public ItemStack trident;
    private boolean ax;
    public int g;

    public EntityThrownTrident(World world) {
        super(EntityTypes.TRIDENT, world);
        this.trident = new ItemStack(Items.TRIDENT);
    }

    public EntityThrownTrident(World world, EntityLiving entityliving, ItemStack itemstack) {
        super(EntityTypes.TRIDENT, entityliving, world);
        this.trident = new ItemStack(Items.TRIDENT);
        this.trident = itemstack.cloneItemStack();
        this.datawatcher.set(EntityThrownTrident.h, (byte) EnchantmentManager.f(itemstack));
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityThrownTrident.h, (byte) 0);
    }

    public void tick() {
        if (this.c > 4) {
            this.ax = true;
        }

        Entity entity = this.getShooter();

        if ((this.ax || this.q()) && entity != null) {
            byte b0 = (Byte) this.datawatcher.get(EntityThrownTrident.h);

            if (b0 > 0 && !this.r()) {
                if (!this.world.isClientSide && this.fromPlayer == EntityArrow.PickupStatus.ALLOWED) {
                    this.a(this.getItemStack(), 0.1F);
                }

                this.die();
            } else if (b0 > 0) {
                this.o(true);
                Vec3D vec3d = new Vec3D(entity.locX - this.locX, entity.locY + (double) entity.getHeadHeight() - this.locY, entity.locZ - this.locZ);

                this.locY += vec3d.y * 0.015D * (double) b0;
                if (this.world.isClientSide) {
                    this.O = this.locY;
                }

                vec3d = vec3d.a();
                double d0 = 0.05D * (double) b0;

                this.motX += vec3d.x * d0 - this.motX * 0.05D;
                this.motY += vec3d.y * d0 - this.motY * 0.05D;
                this.motZ += vec3d.z * d0 - this.motZ * 0.05D;
                if (this.g == 0) {
                    this.a(SoundEffects.ITEM_TRIDENT_RETURN, 10.0F, 1.0F);
                }

                ++this.g;
            }
        }

        super.tick();
    }

    private boolean r() {
        Entity entity = this.getShooter();

        return entity != null && entity.isAlive() ? !(entity instanceof EntityPlayer) || !((EntityPlayer) entity).isSpectator() : false;
    }

    protected ItemStack getItemStack() {
        return this.trident.cloneItemStack();
    }

    @Nullable
    protected Entity a(Vec3D vec3d, Vec3D vec3d1) {
        return this.ax ? null : super.a(vec3d, vec3d1);
    }

    protected void b(MovingObjectPosition movingobjectposition) {
        Entity entity = movingobjectposition.entity;
        float f = 8.0F;

        if (entity instanceof EntityLiving) {
            EntityLiving entityliving = (EntityLiving) entity;

            f += EnchantmentManager.a(this.trident, entityliving.getMonsterType());
        }

        Entity entity1 = this.getShooter();
        DamageSource damagesource = DamageSource.a(this, (Entity) (entity1 == null ? this : entity1));

        this.ax = true;
        SoundEffect soundeffect = SoundEffects.ITEM_TRIDENT_HIT;

        if (entity.damageEntity(damagesource, f) && entity instanceof EntityLiving) {
            EntityLiving entityliving1 = (EntityLiving) entity;

            if (entity1 instanceof EntityLiving) {
                EnchantmentManager.a(entityliving1, entity1);
                EnchantmentManager.b((EntityLiving) entity1, (Entity) entityliving1);
            }

            this.a(entityliving1);
        }

        this.motX *= -0.009999999776482582D;
        this.motY *= -0.10000000149011612D;
        this.motZ *= -0.009999999776482582D;
        float f1 = 1.0F;

        if (this.world.Y() && EnchantmentManager.h(this.trident)) {
            BlockPosition blockposition = entity.getChunkCoordinates();

            if (this.world.e(blockposition)) {
                EntityLightning entitylightning = new EntityLightning(this.world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, false);

                entitylightning.d(entity1 instanceof EntityPlayer ? (EntityPlayer) entity1 : null);
                ((WorldServer) this.world).strikeLightning(entitylightning, org.bukkit.event.weather.LightningStrikeEvent.Cause.TRIDENT); // CraftBukkit
                soundeffect = SoundEffects.ITEM_TRIDENT_THUNDER;
                f1 = 5.0F;
            }
        }

        this.a(soundeffect, f1, 1.0F);
    }

    protected SoundEffect i() {
        return SoundEffects.ITEM_TRIDENT_HIT_GROUND;
    }

    public void d(EntityHuman entityhuman) {
        Entity entity = this.getShooter();

        if (entity == null || entity.getUniqueID() == entityhuman.getUniqueID()) {
            super.d(entityhuman);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("Trident", 10)) {
            this.trident = ItemStack.a(nbttagcompound.getCompound("Trident"));
        }

        this.ax = nbttagcompound.getBoolean("DealtDamage");
        this.datawatcher.set(EntityThrownTrident.h, (byte) EnchantmentManager.f(this.trident));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.set("Trident", this.trident.save(new NBTTagCompound()));
        nbttagcompound.setBoolean("DealtDamage", this.ax);
    }

    protected void f() {
        byte b0 = (Byte) this.datawatcher.get(EntityThrownTrident.h);

        if (this.fromPlayer != EntityArrow.PickupStatus.ALLOWED || b0 <= 0) {
            super.f();
        }

    }

    protected float p() {
        return 0.99F;
    }
}
