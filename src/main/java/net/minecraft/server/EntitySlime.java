package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftEntity;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.SlimeSplitEvent; 
// CraftBukkit end

public class EntitySlime extends EntityLiving implements IMonster {

    private static final float[] e = new float[] { 1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
    public float b;
    public float c;
    public float d;
    private int jumpDelay = 0;
    private Entity lastTarget; // CraftBukkit

    public EntitySlime(World world) {
        super(world);
        this.texture = "/mob/slime.png";
        int i = 1 << this.random.nextInt(3);

        this.height = 0.0F;
        this.jumpDelay = this.random.nextInt(20) + 10;
        this.setSize(i);
    }

    protected void a() {
        super.a();
        this.datawatcher.a(16, new Byte((byte) 1));
    }

    // CraftBukkit - protected -> public
    public void setSize(int i) {
        boolean updateMaxHealth = this.getMaxHealth() == this.maxHealth; // CraftBukkit
        this.datawatcher.watch(16, new Byte((byte) i));
        this.a(0.6F * (float) i, 0.6F * (float) i);
        this.setPosition(this.locX, this.locY, this.locZ);
        // CraftBukkit start
        if (updateMaxHealth) {
            this.maxHealth = this.getMaxHealth();
        }
        this.setHealth(this.maxHealth);
        // CraftBukkit end
        this.be = i;
    }

    public int getMaxHealth() {
        int i = this.getSize();

        return i * i;
    }

    public int getSize() {
        return this.datawatcher.getByte(16);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Size", this.getSize() - 1);
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSize(nbttagcompound.getInt("Size") + 1);
    }

    protected String h() {
        return "slime";
    }

    protected String n() {
        return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
    }

    public void l_() {
        if (!this.world.isStatic && this.world.difficulty == 0 && this.getSize() > 0) {
            this.dead = true;
        }

        this.c += (this.b - this.c) * 0.5F;
        this.d = this.c;
        boolean flag = this.onGround;

        super.l_();
        int i;

        if (this.onGround && !flag) {
            i = this.getSize();

            for (int j = 0; j < i * 8; ++j) {
                float f = this.random.nextFloat() * 3.1415927F * 2.0F;
                float f1 = this.random.nextFloat() * 0.5F + 0.5F;
                float f2 = MathHelper.sin(f) * (float) i * 0.5F * f1;
                float f3 = MathHelper.cos(f) * (float) i * 0.5F * f1;

                this.world.addParticle(this.h(), this.locX + (double) f2, this.boundingBox.b, this.locZ + (double) f3, 0.0D, 0.0D, 0.0D);
            }

            if (this.o()) {
                this.makeSound(this.n(), this.ba(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
            }

            this.b = -0.5F;
        } else if (!this.onGround && flag) {
            this.b = 1.0F;
        }

        this.k();
        if (this.world.isStatic) {
            i = this.getSize();
            this.a(0.6F * (float) i, 0.6F * (float) i);
        }
    }

    protected void bq() {
        this.bn();
        // CraftBukkit start
        Entity entityhuman = this.world.findNearbyVulnerablePlayer(this, 16.0D); // EntityHuman -> Entity
        EntityTargetEvent event = null;

        if (entityhuman != null && !entityhuman.equals(lastTarget)) {
            event = CraftEventFactory.callEntityTargetEvent(this, entityhuman, EntityTargetEvent.TargetReason.CLOSEST_PLAYER);
        } else if (lastTarget != null && entityhuman == null) {
            event = CraftEventFactory.callEntityTargetEvent(this, entityhuman, EntityTargetEvent.TargetReason.FORGOT_TARGET);
        }

        if (event != null && !event.isCancelled()) {
            entityhuman = event.getTarget() == null ? null : ((CraftEntity) event.getTarget()).getHandle();
        }

        this.lastTarget = entityhuman;
        // CraftBukkit end

        if (entityhuman != null) {
            this.a(entityhuman, 10.0F, 20.0F);
        }

        if (this.onGround && this.jumpDelay-- <= 0) {
            this.jumpDelay = this.j();
            if (entityhuman != null) {
                this.jumpDelay /= 3;
            }

            this.bG = true;
            if (this.q()) {
                this.makeSound(this.n(), this.ba(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) * 0.8F);
            }

            this.bD = 1.0F - this.random.nextFloat() * 2.0F;
            this.bE = (float) (1 * this.getSize());
        } else {
            this.bG = false;
            if (this.onGround) {
                this.bD = this.bE = 0.0F;
            }
        }
    }

    protected void k() {
        this.b *= 0.6F;
    }

    protected int j() {
        return this.random.nextInt(20) + 10;
    }

    protected EntitySlime i() {
        return new EntitySlime(this.world);
    }

    public void die() {
        int i = this.getSize();

        if (!this.world.isStatic && i > 1 && this.getHealth() <= 0) {
            int j = 2 + this.random.nextInt(3);

            // CraftBukkit start
            SlimeSplitEvent event = new SlimeSplitEvent((org.bukkit.entity.Slime) this.getBukkitEntity(), j);
            this.world.getServer().getPluginManager().callEvent(event);

            if (!event.isCancelled() && event.getCount() > 0) {
                j = event.getCount();
            } else {
                super.die();
                return;
            }
            // CraftBukkit end

            for (int k = 0; k < j; ++k) {
                float f = ((float) (k % 2) - 0.5F) * (float) i / 4.0F;
                float f1 = ((float) (k / 2) - 0.5F) * (float) i / 4.0F;
                EntitySlime entityslime = this.i();

                entityslime.setSize(i / 2);
                entityslime.setPositionRotation(this.locX + (double) f, this.locY + 0.5D, this.locZ + (double) f1, this.random.nextFloat() * 360.0F, 0.0F);
                this.world.addEntity(entityslime, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SLIME_SPLIT); // CraftBukkit - SpawnReason
            }
        }

        super.die();
    }

    public void b_(EntityHuman entityhuman) {
        if (this.l()) {
            int i = this.getSize();

            if (this.n(entityhuman) && this.e(entityhuman) < 0.6D * (double) i * 0.6D * (double) i && entityhuman.damageEntity(DamageSource.mobAttack(this), this.m())) {
                this.makeSound("mob.attack", 1.0F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }
        }
    }

    protected boolean l() {
        return this.getSize() > 1;
    }

    protected int m() {
        return this.getSize();
    }

    protected String bc() {
        return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
    }

    protected String bd() {
        return "mob.slime." + (this.getSize() > 1 ? "big" : "small");
    }

    protected int getLootId() {
        return this.getSize() == 1 ? Item.SLIME_BALL.id : 0;
    }

    public boolean canSpawn() {
        Chunk chunk = this.world.getChunkAtWorldCoords(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));

        if (this.world.getWorldData().getType() == WorldType.FLAT && this.random.nextInt(4) != 1) {
            return false;
        } else {
            if (this.getSize() == 1 || this.world.difficulty > 0) {
                BiomeBase biomebase = this.world.getBiome(MathHelper.floor(this.locX), MathHelper.floor(this.locZ));

                if (biomebase == BiomeBase.SWAMPLAND && this.locY > 50.0D && this.locY < 70.0D && this.random.nextFloat() < 0.5F && this.random.nextFloat() < e[this.world.w()] && this.world.getLightLevel(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ)) <= this.random.nextInt(8)) {
                    return super.canSpawn();
                }

                if (this.random.nextInt(10) == 0 && chunk.a(987234911L).nextInt(10) == 0 && this.locY < 40.0D) {
                    return super.canSpawn();
                }
            }

            return false;
        }
    }

    protected float ba() {
        return 0.4F * (float) this.getSize();
    }

    public int bs() {
        return 0;
    }

    protected boolean q() {
        return this.getSize() > 0;
    }

    protected boolean o() {
        return this.getSize() > 2;
    }
}
