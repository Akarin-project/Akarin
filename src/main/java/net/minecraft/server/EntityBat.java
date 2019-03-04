package net.minecraft.server;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import javax.annotation.Nullable;
import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityBat extends EntityAmbient {

    private static final DataWatcherObject<Byte> a = DataWatcher.a(EntityBat.class, DataWatcherRegistry.a);
    private BlockPosition b;

    public EntityBat(World world) {
        super(EntityTypes.BAT, world);
        this.setSize(0.5F, 0.9F);
        this.setAsleep(true);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityBat.a, (byte) 0);
    }

    protected float cD() {
        return 0.1F;
    }

    protected float cE() {
        return super.cE() * 0.95F;
    }

    @Nullable
    public SoundEffect D() {
        return this.isAsleep() && this.random.nextInt(4) != 0 ? null : SoundEffects.ENTITY_BAT_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_BAT_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_BAT_DEATH;
    }

    public boolean isCollidable() {
        return false;
    }

    protected void C(Entity entity) {}

    protected void cN() {}

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(6.0D);
    }

    public boolean isAsleep() {
        return ((Byte) this.datawatcher.get(EntityBat.a) & 1) != 0;
    }

    public void setAsleep(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntityBat.a);

        if (flag) {
            this.datawatcher.set(EntityBat.a, (byte) (b0 | 1));
        } else {
            this.datawatcher.set(EntityBat.a, (byte) (b0 & -2));
        }

    }

    public void tick() {
        super.tick();
        if (this.isAsleep()) {
            this.motX = 0.0D;
            this.motY = 0.0D;
            this.motZ = 0.0D;
            this.locY = (double) MathHelper.floor(this.locY) + 1.0D - (double) this.length;
        } else {
            this.motY *= 0.6000000238418579D;
        }

    }

    protected void mobTick() {
        super.mobTick();
        BlockPosition blockposition = new BlockPosition(this);
        BlockPosition blockposition1 = blockposition.up();

        if (this.isAsleep()) {
            if (this.world.getType(blockposition1).isOccluding()) {
                if (this.random.nextInt(200) == 0) {
                    this.aS = (float) this.random.nextInt(360);
                }

                if (this.world.b(this, 4.0D) != null) {
                    // CraftBukkit Start - Call BatToggleSleepEvent
                    if (CraftEventFactory.handleBatToggleSleepEvent(this, true)) {
                        this.setAsleep(false);
                        this.world.a((EntityHuman) null, 1025, blockposition, 0);
                    }
                    // CraftBukkit End
                }
            } else {
                // CraftBukkit Start - Call BatToggleSleepEvent
                if (CraftEventFactory.handleBatToggleSleepEvent(this, true)) {
                    this.setAsleep(false);
                    this.world.a((EntityHuman) null, 1025, blockposition, 0);
                }
                // CraftBukkit End - Call BatToggleSleepEvent
            }
        } else {
            if (this.b != null && (!this.world.isEmpty(this.b) || this.b.getY() < 1)) {
                this.b = null;
            }

            if (this.b == null || this.random.nextInt(30) == 0 || this.b.distanceSquared((double) ((int) this.locX), (double) ((int) this.locY), (double) ((int) this.locZ)) < 4.0D) {
                this.b = new BlockPosition((int) this.locX + this.random.nextInt(7) - this.random.nextInt(7), (int) this.locY + this.random.nextInt(6) - 2, (int) this.locZ + this.random.nextInt(7) - this.random.nextInt(7));
            }

            double d0 = (double) this.b.getX() + 0.5D - this.locX;
            double d1 = (double) this.b.getY() + 0.1D - this.locY;
            double d2 = (double) this.b.getZ() + 0.5D - this.locZ;

            this.motX += (Math.signum(d0) * 0.5D - this.motX) * 0.10000000149011612D;
            this.motY += (Math.signum(d1) * 0.699999988079071D - this.motY) * 0.10000000149011612D;
            this.motZ += (Math.signum(d2) * 0.5D - this.motZ) * 0.10000000149011612D;
            float f = (float) (MathHelper.c(this.motZ, this.motX) * 57.2957763671875D) - 90.0F;
            float f1 = MathHelper.g(f - this.yaw);

            this.bj = 0.5F;
            this.yaw += f1;
            if (this.random.nextInt(100) == 0 && this.world.getType(blockposition1).isOccluding()) {
                // CraftBukkit Start - Call BatToggleSleepEvent
                if (CraftEventFactory.handleBatToggleSleepEvent(this, false)) {
                    this.setAsleep(true);
                }
                // CraftBukkit End
            }
        }

    }

    protected boolean playStepSound() {
        return false;
    }

    public void c(float f, float f1) {}

    protected void a(double d0, boolean flag, IBlockData iblockdata, BlockPosition blockposition) {}

    public boolean isIgnoreBlockTrigger() {
        return true;
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (this.isInvulnerable(damagesource)) {
            return false;
        } else {
            if (!this.world.isClientSide && this.isAsleep()) {
                // CraftBukkit Start - Call BatToggleSleepEvent
                if (CraftEventFactory.handleBatToggleSleepEvent(this, true)) {
                    this.setAsleep(false);
                }
                // CraftBukkit End - Call BatToggleSleepEvent
            }

            return super.damageEntity(damagesource, f);
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.datawatcher.set(EntityBat.a, nbttagcompound.getByte("BatFlags"));
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setByte("BatFlags", (Byte) this.datawatcher.get(EntityBat.a));
    }

    public boolean a(GeneratorAccess generatoraccess, boolean flag) {
        BlockPosition blockposition = new BlockPosition(this.locX, this.getBoundingBox().minY, this.locZ);

        if (blockposition.getY() >= generatoraccess.getSeaLevel()) {
            return false;
        } else {
            int i = generatoraccess.getLightLevel(blockposition);
            byte b0 = 4;

            if (this.dr()) {
                b0 = 7;
            } else if (this.random.nextBoolean()) {
                return false;
            }

            return i > this.random.nextInt(b0) ? false : super.a(generatoraccess, flag);
        }
    }

    private boolean dr() {
        LocalDate localdate = LocalDate.now();
        int i = localdate.get(ChronoField.DAY_OF_MONTH);
        int j = localdate.get(ChronoField.MONTH_OF_YEAR);

        return j == 10 && i >= 20 || j == 11 && i <= 3;
    }

    public float getHeadHeight() {
        return this.length / 2.0F;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.an;
    }
}
