package net.minecraft.server;

public class EntityExperienceOrb extends Entity {

    public int a;
    public int b;
    public int c;
    private int d = 5;
    public int value;
    private EntityHuman targetPlayer;
    private int targetTime;

    public EntityExperienceOrb(World world, double d0, double d1, double d2, int i) {
        super(EntityTypes.EXPERIENCE_ORB, world);
        this.setSize(0.5F, 0.5F);
        this.setPosition(d0, d1, d2);
        this.yaw = (float) (Math.random() * 360.0D);
        this.motX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.motY = (double) ((float) (Math.random() * 0.2D) * 2.0F);
        this.motZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.value = i;
    }

    public EntityExperienceOrb(World world) {
        super(EntityTypes.EXPERIENCE_ORB, world);
        this.setSize(0.25F, 0.25F);
    }

    protected boolean playStepSound() {
        return false;
    }

    protected void x_() {}

    public void tick() {
        super.tick();
        if (this.c > 0) {
            --this.c;
        }

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        if (this.a(TagsFluid.WATER)) {
            this.k();
        } else if (!this.isNoGravity()) {
            this.motY -= 0.029999999329447746D;
        }

        if (this.world.getFluid(new BlockPosition(this)).a(TagsFluid.LAVA)) {
            this.motY = 0.20000000298023224D;
            this.motX = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.motZ = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.a(SoundEffects.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

        this.i(this.locX, (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.locZ);
        double d0 = 8.0D;

        if (this.targetTime < this.a - 20 + this.getId() % 100) {
            if (this.targetPlayer == null || this.targetPlayer.h(this) > 64.0D) {
                this.targetPlayer = this.world.findNearbyPlayer(this, 8.0D);
            }

            this.targetTime = this.a;
        }

        if (this.targetPlayer != null && this.targetPlayer.isSpectator()) {
            this.targetPlayer = null;
        }

        if (this.targetPlayer != null) {
            double d1 = (this.targetPlayer.locX - this.locX) / 8.0D;
            double d2 = (this.targetPlayer.locY + (double) this.targetPlayer.getHeadHeight() / 2.0D - this.locY) / 8.0D;
            double d3 = (this.targetPlayer.locZ - this.locZ) / 8.0D;
            double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
            double d5 = 1.0D - d4;

            if (d5 > 0.0D) {
                d5 *= d5;
                this.motX += d1 / d4 * d5 * 0.1D;
                this.motY += d2 / d4 * d5 * 0.1D;
                this.motZ += d3 / d4 * d5 * 0.1D;
            }
        }

        this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
        float f = 0.98F;

        if (this.onGround) {
            f = this.world.getType(new BlockPosition(MathHelper.floor(this.locX), MathHelper.floor(this.getBoundingBox().minY) - 1, MathHelper.floor(this.locZ))).getBlock().n() * 0.98F;
        }

        this.motX *= (double) f;
        this.motY *= 0.9800000190734863D;
        this.motZ *= (double) f;
        if (this.onGround) {
            this.motY *= -0.8999999761581421D;
        }

        ++this.a;
        ++this.b;
        if (this.b >= 6000) {
            this.die();
        }

    }

    private void k() {
        this.motY += 5.000000237487257E-4D;
        this.motY = Math.min(this.motY, 0.05999999865889549D);
        this.motX *= 0.9900000095367432D;
        this.motZ *= 0.9900000095367432D;
    }

    protected void au() {}

    protected void burn(int i) {
        this.damageEntity(DamageSource.FIRE, (float) i);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            this.aA();
            this.d = (int) ((float) this.d - f);
            if (this.d <= 0) {
                this.die();
            }

            return false;
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) this.d);
        nbttagcompound.setShort("Age", (short) this.b);
        nbttagcompound.setShort("Value", (short) this.value);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.d = nbttagcompound.getShort("Health");
        this.b = nbttagcompound.getShort("Age");
        this.value = nbttagcompound.getShort("Value");
    }

    public void d(EntityHuman entityhuman) {
        if (!this.world.isClientSide) {
            if (this.c == 0 && entityhuman.bJ == 0) {
                entityhuman.bJ = 2;
                entityhuman.receive(this, 1);
                ItemStack itemstack = EnchantmentManager.b(Enchantments.MENDING, (EntityLiving) entityhuman);

                if (!itemstack.isEmpty() && itemstack.f()) {
                    int i = Math.min(this.c(this.value), itemstack.getDamage());

                    this.value -= this.b(i);
                    itemstack.setDamage(itemstack.getDamage() - i);
                }

                if (this.value > 0) {
                    entityhuman.giveExp(this.value);
                }

                this.die();
            }

        }
    }

    private int b(int i) {
        return i / 2;
    }

    private int c(int i) {
        return i * 2;
    }

    public int f() {
        return this.value;
    }

    public static int getOrbValue(int i) {
        return i >= 2477 ? 2477 : (i >= 1237 ? 1237 : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1)))))))));
    }

    public boolean bk() {
        return false;
    }
}
