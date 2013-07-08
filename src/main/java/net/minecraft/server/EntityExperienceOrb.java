package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
// CraftBukkit end

public class EntityExperienceOrb extends Entity {

    public int a;
    public int b;
    public int c;
    private int d = 5;
    public int value; // CraftBukkit - private -> public
    private EntityHuman targetPlayer;
    private int targetTime;

    public EntityExperienceOrb(World world, double d0, double d1, double d2, int i) {
        super(world);
        this.a(0.5F, 0.5F);
        this.height = this.length / 2.0F;
        this.setPosition(d0, d1, d2);
        this.yaw = (float) (Math.random() * 360.0D);
        this.motX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.motY = (double) ((float) (Math.random() * 0.2D) * 2.0F);
        this.motZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.value = i;
    }

    protected boolean e_() {
        return false;
    }

    public EntityExperienceOrb(World world) {
        super(world);
        this.a(0.25F, 0.25F);
        this.height = this.length / 2.0F;
    }

    protected void a() {}

    public void l_() {
        super.l_();
        if (this.c > 0) {
            --this.c;
        }

        this.lastX = this.locX;
        this.lastY = this.locY;
        this.lastZ = this.locZ;
        this.motY -= 0.029999999329447746D;
        if (this.world.getMaterial(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) == Material.LAVA) {
            this.motY = 0.20000000298023224D;
            this.motX = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.motZ = (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F);
            this.makeSound("random.fizz", 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
        }

        this.i(this.locX, (this.boundingBox.b + this.boundingBox.e) / 2.0D, this.locZ);
        double d0 = 8.0D;

        if (this.targetTime < this.a - 20 + this.id % 100) {
            if (this.targetPlayer == null || this.targetPlayer.e(this) > d0 * d0) {
                this.targetPlayer = this.world.findNearbyPlayer(this, d0);
            }

            this.targetTime = this.a;
        }

        if (this.targetPlayer != null) {
            // CraftBukkit start
            EntityTargetEvent event = CraftEventFactory.callEntityTargetEvent(this, targetPlayer, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
            Entity target = event.getTarget() == null ? null : ((org.bukkit.craftbukkit.entity.CraftEntity) event.getTarget()).getHandle();

            if (!event.isCancelled() && target != null) {
                double d1 = (target.locX - this.locX) / d0;
                double d2 = (target.locY + (double) target.getHeadHeight() - this.locY) / d0;
                double d3 = (target.locZ - this.locZ) / d0;
                double d4 = Math.sqrt(d1 * d1 + d2 * d2 + d3 * d3);
                double d5 = 1.0D - d4;
                if (d5 > 0.0D) {
                    d5 *= d5;
                    this.motX += d1 / d4 * d5 * 0.1D;
                    this.motY += d2 / d4 * d5 * 0.1D;
                    this.motZ += d3 / d4 * d5 * 0.1D;
                }
                // CraftBukkit end
            }
        }

        this.move(this.motX, this.motY, this.motZ);
        float f = 0.98F;

        if (this.onGround) {
            f = 0.58800006F;
            int i = this.world.getTypeId(MathHelper.floor(this.locX), MathHelper.floor(this.boundingBox.b) - 1, MathHelper.floor(this.locZ));

            if (i > 0) {
                f = Block.byId[i].frictionFactor * 0.98F;
            }
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

    public boolean H() {
        return this.world.a(this.boundingBox, Material.WATER, (Entity) this);
    }

    protected void burn(int i) {
        this.damageEntity(DamageSource.FIRE, (float) i);
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable()) {
            return false;
        } else {
            this.J();
            this.d = (int) ((float) this.d - f);
            if (this.d <= 0) {
                this.die();
            }

            return false;
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setShort("Health", (short) ((byte) this.d));
        nbttagcompound.setShort("Age", (short) this.b);
        nbttagcompound.setShort("Value", (short) this.value);
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.d = nbttagcompound.getShort("Health") & 255;
        this.b = nbttagcompound.getShort("Age");
        this.value = nbttagcompound.getShort("Value");
    }

    public void b_(EntityHuman entityhuman) {
        if (!this.world.isStatic) {
            if (this.c == 0 && entityhuman.bv == 0) {
                entityhuman.bv = 2;
                this.makeSound("random.orb", 0.1F, 0.5F * ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.8F));
                entityhuman.receive(this, 1);
                entityhuman.giveExp(CraftEventFactory.callPlayerExpChangeEvent(entityhuman, this.value).getAmount()); // CraftBukkit - this.value to event.getAmount()
                this.die();
            }
        }
    }

    public int c() {
        return this.value;
    }

    public static int getOrbValue(int i) {
        // CraftBukkit start
        if (i > 162670129) return i - 100000;
        if (i > 81335063) return 81335063;
        if (i > 40667527) return 40667527;
        if (i > 20333759) return 20333759;
        if (i > 10166857) return 10166857;
        if (i > 5083423) return 5083423;
        if (i > 2541701) return 2541701;
        if (i > 1270849) return 1270849;
        if (i > 635413) return 635413;
        if (i > 317701) return 317701;
        if (i > 158849) return 158849;
        if (i > 79423) return 79423;
        if (i > 39709) return 39709;
        if (i > 19853) return 19853;
        if (i > 9923) return 9923;
        if (i > 4957) return 4957;
        // CraftBukkit end

        return i >= 2477 ? 2477 : (i >= 1237 ? 1237 : (i >= 617 ? 617 : (i >= 307 ? 307 : (i >= 149 ? 149 : (i >= 73 ? 73 : (i >= 37 ? 37 : (i >= 17 ? 17 : (i >= 7 ? 7 : (i >= 3 ? 3 : 1)))))))));
    }

    public boolean ap() {
        return false;
    }
}
