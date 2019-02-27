package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityEnderDragon extends EntityInsentient implements IComplex, IMonster {

    private static final Logger bQ = LogManager.getLogger();
    public static final DataWatcherObject<Integer> PHASE = DataWatcher.a(EntityEnderDragon.class, DataWatcherRegistry.b);
    public double[][] b = new double[64][3];
    public int c = -1;
    public EntityComplexPart[] children;
    public EntityComplexPart bD = new EntityComplexPart(this, "head", 6.0F, 6.0F);
    public EntityComplexPart bE = new EntityComplexPart(this, "neck", 6.0F, 6.0F);
    public EntityComplexPart bF = new EntityComplexPart(this, "body", 8.0F, 8.0F);
    public EntityComplexPart bG = new EntityComplexPart(this, "tail", 4.0F, 4.0F);
    public EntityComplexPart bH = new EntityComplexPart(this, "tail", 4.0F, 4.0F);
    public EntityComplexPart bI = new EntityComplexPart(this, "tail", 4.0F, 4.0F);
    public EntityComplexPart bJ = new EntityComplexPart(this, "wing", 4.0F, 4.0F);
    public EntityComplexPart bK = new EntityComplexPart(this, "wing", 4.0F, 4.0F);
    public float bL;
    public float bM;
    public boolean bN;
    public int bO;
    public EntityEnderCrystal currentEnderCrystal;
    private final EnderDragonBattle bR;
    private final DragonControllerManager bS;
    private int bT = 100;
    private int bU;
    private final PathPoint[] bV = new PathPoint[24];
    private final int[] bW = new int[24];
    private final Path bX = new Path();

    public EntityEnderDragon(World world) {
        super(EntityTypes.ENDER_DRAGON, world);
        this.children = new EntityComplexPart[] { this.bD, this.bE, this.bF, this.bG, this.bH, this.bI, this.bJ, this.bK};
        this.setHealth(this.getMaxHealth());
        this.setSize(16.0F, 8.0F);
        this.noclip = true;
        this.fireProof = true;
        this.ak = true;
        if (!world.isClientSide && world.worldProvider instanceof WorldProviderTheEnd) {
            this.bR = ((WorldProviderTheEnd) world.worldProvider).r();
        } else {
            this.bR = null;
        }

        this.bS = new DragonControllerManager(this);
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(200.0D);
    }

    protected void x_() {
        super.x_();
        this.getDataWatcher().register(EntityEnderDragon.PHASE, DragonControllerPhase.HOVER.b());
    }

    public double[] a(int i, float f) {
        if (this.getHealth() <= 0.0F) {
            f = 0.0F;
        }

        f = 1.0F - f;
        int j = this.c - i & 63;
        int k = this.c - i - 1 & 63;
        double[] adouble = new double[3];
        double d0 = this.b[j][0];
        double d1 = MathHelper.g(this.b[k][0] - d0);

        adouble[0] = d0 + d1 * (double) f;
        d0 = this.b[j][1];
        d1 = this.b[k][1] - d0;
        adouble[1] = d0 + d1 * (double) f;
        adouble[2] = this.b[j][2] + (this.b[k][2] - this.b[j][2]) * (double) f;
        return adouble;
    }

    public void movementTick() {
        float f;
        float f1;

        if (this.world.isClientSide) {
            this.setHealth(this.getHealth());
            if (!this.isSilent()) {
                f = MathHelper.cos(this.bM * 6.2831855F);
                f1 = MathHelper.cos(this.bL * 6.2831855F);
                if (f1 <= -0.3F && f >= -0.3F) {
                    this.world.a(this.locX, this.locY, this.locZ, SoundEffects.ENTITY_ENDER_DRAGON_FLAP, this.bV(), 5.0F, 0.8F + this.random.nextFloat() * 0.3F, false);
                }

                if (!this.bS.a().a() && --this.bT < 0) {
                    this.world.a(this.locX, this.locY, this.locZ, SoundEffects.ENTITY_ENDER_DRAGON_GROWL, this.bV(), 2.5F, 0.8F + this.random.nextFloat() * 0.3F, false);
                    this.bT = 200 + this.random.nextInt(200);
                }
            }
        }

        this.bL = this.bM;
        float f2;

        if (this.getHealth() <= 0.0F) {
            f = (this.random.nextFloat() - 0.5F) * 8.0F;
            f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            f2 = (this.random.nextFloat() - 0.5F) * 8.0F;
            this.world.addParticle(Particles.u, this.locX + (double) f, this.locY + 2.0D + (double) f1, this.locZ + (double) f2, 0.0D, 0.0D, 0.0D);
        } else {
            this.dt();
            f = 0.2F / (MathHelper.sqrt(this.motX * this.motX + this.motZ * this.motZ) * 10.0F + 1.0F);
            f *= (float) Math.pow(2.0D, this.motY);
            if (this.bS.a().a()) {
                this.bM += 0.1F;
            } else if (this.bN) {
                this.bM += f * 0.5F;
            } else {
                this.bM += f;
            }

            this.yaw = MathHelper.g(this.yaw);
            if (this.isNoAI()) {
                this.bM = 0.5F;
            } else {
                if (this.c < 0) {
                    for (int i = 0; i < this.b.length; ++i) {
                        this.b[i][0] = (double) this.yaw;
                        this.b[i][1] = this.locY;
                    }
                }

                if (++this.c == this.b.length) {
                    this.c = 0;
                }

                this.b[this.c][0] = (double) this.yaw;
                this.b[this.c][1] = this.locY;
                double d0;
                double d1;
                double d2;
                float f3;
                float f4;

                if (this.world.isClientSide) {
                    if (this.bl > 0) {
                        double d3 = this.locX + (this.bm - this.locX) / (double) this.bl;

                        d0 = this.locY + (this.bn - this.locY) / (double) this.bl;
                        d1 = this.locZ + (this.bo - this.locZ) / (double) this.bl;
                        d2 = MathHelper.g(this.bp - (double) this.yaw);
                        this.yaw = (float) ((double) this.yaw + d2 / (double) this.bl);
                        this.pitch = (float) ((double) this.pitch + (this.bq - (double) this.pitch) / (double) this.bl);
                        --this.bl;
                        this.setPosition(d3, d0, d1);
                        this.setYawPitch(this.yaw, this.pitch);
                    }

                    this.bS.a().b();
                } else {
                    IDragonController idragoncontroller = this.bS.a();

                    idragoncontroller.c();
                    if (this.bS.a() != idragoncontroller) {
                        idragoncontroller = this.bS.a();
                        idragoncontroller.c();
                    }

                    Vec3D vec3d = idragoncontroller.g();

                    if (vec3d != null) {
                        d0 = vec3d.x - this.locX;
                        d1 = vec3d.y - this.locY;
                        d2 = vec3d.z - this.locZ;
                        double d4 = d0 * d0 + d1 * d1 + d2 * d2;

                        f3 = idragoncontroller.f();
                        d1 = MathHelper.a(d1 / (double) MathHelper.sqrt(d0 * d0 + d2 * d2), (double) (-f3), (double) f3);
                        this.motY += d1 * 0.10000000149011612D;
                        this.yaw = MathHelper.g(this.yaw);
                        double d5 = MathHelper.a(MathHelper.g(180.0D - MathHelper.c(d0, d2) * 57.2957763671875D - (double) this.yaw), -50.0D, 50.0D);
                        Vec3D vec3d1 = (new Vec3D(vec3d.x - this.locX, vec3d.y - this.locY, vec3d.z - this.locZ)).a();
                        Vec3D vec3d2 = (new Vec3D((double) MathHelper.sin(this.yaw * 0.017453292F), this.motY, (double) (-MathHelper.cos(this.yaw * 0.017453292F)))).a();

                        f4 = Math.max(((float) vec3d2.b(vec3d1) + 0.5F) / 1.5F, 0.0F);
                        this.bk *= 0.8F;
                        this.bk = (float) ((double) this.bk + d5 * (double) idragoncontroller.h());
                        this.yaw += this.bk * 0.1F;
                        float f5 = (float) (2.0D / (d4 + 1.0D));
                        float f6 = 0.06F;

                        this.a(0.0F, 0.0F, -1.0F, 0.06F * (f4 * f5 + (1.0F - f5)));
                        if (this.bN) {
                            this.move(EnumMoveType.SELF, this.motX * 0.800000011920929D, this.motY * 0.800000011920929D, this.motZ * 0.800000011920929D);
                        } else {
                            this.move(EnumMoveType.SELF, this.motX, this.motY, this.motZ);
                        }

                        Vec3D vec3d3 = (new Vec3D(this.motX, this.motY, this.motZ)).a();
                        float f7 = ((float) vec3d3.b(vec3d2) + 1.0F) / 2.0F;

                        f7 = 0.8F + 0.15F * f7;
                        this.motX *= (double) f7;
                        this.motZ *= (double) f7;
                        this.motY *= 0.9100000262260437D;
                    }
                }

                this.aQ = this.yaw;
                this.bD.width = 1.0F;
                this.bD.length = 1.0F;
                this.bE.width = 3.0F;
                this.bE.length = 3.0F;
                this.bG.width = 2.0F;
                this.bG.length = 2.0F;
                this.bH.width = 2.0F;
                this.bH.length = 2.0F;
                this.bI.width = 2.0F;
                this.bI.length = 2.0F;
                this.bF.length = 3.0F;
                this.bF.width = 5.0F;
                this.bJ.length = 2.0F;
                this.bJ.width = 4.0F;
                this.bK.length = 3.0F;
                this.bK.width = 4.0F;
                Vec3D[] avec3d = new Vec3D[this.children.length];

                for (int j = 0; j < this.children.length; ++j) {
                    avec3d[j] = new Vec3D(this.children[j].locX, this.children[j].locY, this.children[j].locZ);
                }

                f2 = (float) (this.a(5, 1.0F)[1] - this.a(10, 1.0F)[1]) * 10.0F * 0.017453292F;
                float f8 = MathHelper.cos(f2);
                float f9 = MathHelper.sin(f2);
                float f10 = this.yaw * 0.017453292F;
                float f11 = MathHelper.sin(f10);
                float f12 = MathHelper.cos(f10);

                this.bF.tick();
                this.bF.setPositionRotation(this.locX + (double) (f11 * 0.5F), this.locY, this.locZ - (double) (f12 * 0.5F), 0.0F, 0.0F);
                this.bJ.tick();
                this.bJ.setPositionRotation(this.locX + (double) (f12 * 4.5F), this.locY + 2.0D, this.locZ + (double) (f11 * 4.5F), 0.0F, 0.0F);
                this.bK.tick();
                this.bK.setPositionRotation(this.locX - (double) (f12 * 4.5F), this.locY + 2.0D, this.locZ - (double) (f11 * 4.5F), 0.0F, 0.0F);
                if (!this.world.isClientSide && this.hurtTicks == 0) {
                    this.a(this.world.getEntities(this, this.bJ.getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                    this.a(this.world.getEntities(this, this.bK.getBoundingBox().grow(4.0D, 2.0D, 4.0D).d(0.0D, -2.0D, 0.0D)));
                    this.b(this.world.getEntities(this, this.bD.getBoundingBox().g(1.0D)));
                    this.b(this.world.getEntities(this, this.bE.getBoundingBox().g(1.0D)));
                }

                double[] adouble = this.a(5, 1.0F);
                float f13 = MathHelper.sin(this.yaw * 0.017453292F - this.bk * 0.01F);
                float f14 = MathHelper.cos(this.yaw * 0.017453292F - this.bk * 0.01F);

                this.bD.tick();
                this.bE.tick();
                f3 = this.u(1.0F);
                this.bD.setPositionRotation(this.locX + (double) (f13 * 6.5F * f8), this.locY + (double) f3 + (double) (f9 * 6.5F), this.locZ - (double) (f14 * 6.5F * f8), 0.0F, 0.0F);
                this.bE.setPositionRotation(this.locX + (double) (f13 * 5.5F * f8), this.locY + (double) f3 + (double) (f9 * 5.5F), this.locZ - (double) (f14 * 5.5F * f8), 0.0F, 0.0F);

                int k;

                for (k = 0; k < 3; ++k) {
                    EntityComplexPart entitycomplexpart = null;

                    if (k == 0) {
                        entitycomplexpart = this.bG;
                    }

                    if (k == 1) {
                        entitycomplexpart = this.bH;
                    }

                    if (k == 2) {
                        entitycomplexpart = this.bI;
                    }

                    double[] adouble1 = this.a(12 + k * 2, 1.0F);
                    float f15 = this.yaw * 0.017453292F + this.c(adouble1[0] - adouble[0]) * 0.017453292F;
                    float f16 = MathHelper.sin(f15);
                    float f17 = MathHelper.cos(f15);
                    float f18 = 1.5F;

                    f4 = (float) (k + 1) * 2.0F;
                    entitycomplexpart.tick();
                    entitycomplexpart.setPositionRotation(this.locX - (double) ((f11 * 1.5F + f16 * f4) * f8), this.locY + (adouble1[1] - adouble[1]) - (double) ((f4 + 1.5F) * f9) + 1.5D, this.locZ + (double) ((f12 * 1.5F + f17 * f4) * f8), 0.0F, 0.0F);
                }

                if (!this.world.isClientSide) {
                    this.bN = this.b(this.bD.getBoundingBox()) | this.b(this.bE.getBoundingBox()) | this.b(this.bF.getBoundingBox());
                    if (this.bR != null) {
                        this.bR.b(this);
                    }
                }

                for (k = 0; k < this.children.length; ++k) {
                    this.children[k].lastX = avec3d[k].x;
                    this.children[k].lastY = avec3d[k].y;
                    this.children[k].lastZ = avec3d[k].z;
                }

            }
        }
    }

    private float u(float f) {
        double d0;

        if (this.bS.a().a()) {
            d0 = -1.0D;
        } else {
            double[] adouble = this.a(5, 1.0F);
            double[] adouble1 = this.a(0, 1.0F);

            d0 = adouble[1] - adouble1[1];
        }

        return (float) d0;
    }

    private void dt() {
        if (this.currentEnderCrystal != null) {
            if (this.currentEnderCrystal.dead) {
                this.currentEnderCrystal = null;
            } else if (this.ticksLived % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
                this.setHealth(this.getHealth() + 1.0F);
            }
        }

        if (this.random.nextInt(10) == 0) {
            List<EntityEnderCrystal> list = this.world.a(EntityEnderCrystal.class, this.getBoundingBox().g(32.0D));
            EntityEnderCrystal entityendercrystal = null;
            double d0 = Double.MAX_VALUE;
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityEnderCrystal entityendercrystal1 = (EntityEnderCrystal) iterator.next();
                double d1 = entityendercrystal1.h(this);

                if (d1 < d0) {
                    d0 = d1;
                    entityendercrystal = entityendercrystal1;
                }
            }

            this.currentEnderCrystal = entityendercrystal;
        }

    }

    private void a(List<Entity> list) {
        double d0 = (this.bF.getBoundingBox().minX + this.bF.getBoundingBox().maxX) / 2.0D;
        double d1 = (this.bF.getBoundingBox().minZ + this.bF.getBoundingBox().maxZ) / 2.0D;
        Iterator iterator = list.iterator();

        while (iterator.hasNext()) {
            Entity entity = (Entity) iterator.next();

            if (entity instanceof EntityLiving) {
                double d2 = entity.locX - d0;
                double d3 = entity.locZ - d1;
                double d4 = d2 * d2 + d3 * d3;

                entity.f(d2 / d4 * 4.0D, 0.20000000298023224D, d3 / d4 * 4.0D);
                if (!this.bS.a().a() && ((EntityLiving) entity).cg() < entity.ticksLived - 2) {
                    entity.damageEntity(DamageSource.mobAttack(this), 5.0F);
                    this.a((EntityLiving) this, entity);
                }
            }
        }

    }

    private void b(List<Entity> list) {
        for (int i = 0; i < list.size(); ++i) {
            Entity entity = (Entity) list.get(i);

            if (entity instanceof EntityLiving) {
                entity.damageEntity(DamageSource.mobAttack(this), 10.0F);
                this.a((EntityLiving) this, entity);
            }
        }

    }

    private float c(double d0) {
        return (float) MathHelper.g(d0);
    }

    private boolean b(AxisAlignedBB axisalignedbb) {
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.floor(axisalignedbb.minY);
        int k = MathHelper.floor(axisalignedbb.minZ);
        int l = MathHelper.floor(axisalignedbb.maxX);
        int i1 = MathHelper.floor(axisalignedbb.maxY);
        int j1 = MathHelper.floor(axisalignedbb.maxZ);
        boolean flag = false;
        boolean flag1 = false;

        for (int k1 = i; k1 <= l; ++k1) {
            for (int l1 = j; l1 <= i1; ++l1) {
                for (int i2 = k; i2 <= j1; ++i2) {
                    BlockPosition blockposition = new BlockPosition(k1, l1, i2);
                    IBlockData iblockdata = this.world.getType(blockposition);
                    Block block = iblockdata.getBlock();

                    if (!iblockdata.isAir() && iblockdata.getMaterial() != Material.FIRE) {
                        if (!this.world.getGameRules().getBoolean("mobGriefing")) {
                            flag = true;
                        } else if (block != Blocks.BARRIER && block != Blocks.OBSIDIAN && block != Blocks.END_STONE && block != Blocks.BEDROCK && block != Blocks.END_PORTAL && block != Blocks.END_PORTAL_FRAME) {
                            if (block != Blocks.COMMAND_BLOCK && block != Blocks.REPEATING_COMMAND_BLOCK && block != Blocks.CHAIN_COMMAND_BLOCK && block != Blocks.IRON_BARS && block != Blocks.END_GATEWAY) {
                                flag1 = this.world.setAir(blockposition) || flag1;
                            } else {
                                flag = true;
                            }
                        } else {
                            flag = true;
                        }
                    }
                }
            }
        }

        if (flag1) {
            double d0 = axisalignedbb.minX + (axisalignedbb.maxX - axisalignedbb.minX) * (double) this.random.nextFloat();
            double d1 = axisalignedbb.minY + (axisalignedbb.maxY - axisalignedbb.minY) * (double) this.random.nextFloat();
            double d2 = axisalignedbb.minZ + (axisalignedbb.maxZ - axisalignedbb.minZ) * (double) this.random.nextFloat();

            this.world.addParticle(Particles.u, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }

        return flag;
    }

    public boolean a(EntityComplexPart entitycomplexpart, DamageSource damagesource, float f) {
        f = this.bS.a().a(entitycomplexpart, damagesource, f);
        if (entitycomplexpart != this.bD) {
            f = f / 4.0F + Math.min(f, 1.0F);
        }

        if (f < 0.01F) {
            return false;
        } else {
            if (damagesource.getEntity() instanceof EntityHuman || damagesource.isExplosion()) {
                float f1 = this.getHealth();

                this.dealDamage(damagesource, f);
                if (this.getHealth() <= 0.0F && !this.bS.a().a()) {
                    this.setHealth(1.0F);
                    this.bS.setControllerPhase(DragonControllerPhase.DYING);
                }

                if (this.bS.a().a()) {
                    this.bU = (int) ((float) this.bU + (f1 - this.getHealth()));
                    if ((float) this.bU > 0.25F * this.getMaxHealth()) {
                        this.bU = 0;
                        this.bS.setControllerPhase(DragonControllerPhase.TAKEOFF);
                    }
                }
            }

            return true;
        }
    }

    public boolean damageEntity(DamageSource damagesource, float f) {
        if (damagesource instanceof EntityDamageSource && ((EntityDamageSource) damagesource).y()) {
            this.a(this.bF, damagesource, f);
        }

        return false;
    }

    protected boolean dealDamage(DamageSource damagesource, float f) {
        return super.damageEntity(damagesource, f);
    }

    public void killEntity() {
        this.die();
        if (this.bR != null) {
            this.bR.b(this);
            this.bR.a(this);
        }

    }

    protected void cb() {
        if (this.bR != null) {
            this.bR.b(this);
        }

        ++this.bO;
        if (this.bO >= 180 && this.bO <= 200) {
            float f = (this.random.nextFloat() - 0.5F) * 8.0F;
            float f1 = (this.random.nextFloat() - 0.5F) * 4.0F;
            float f2 = (this.random.nextFloat() - 0.5F) * 8.0F;

            this.world.addParticle(Particles.t, this.locX + (double) f, this.locY + 2.0D + (double) f1, this.locZ + (double) f2, 0.0D, 0.0D, 0.0D);
        }

        boolean flag = this.world.getGameRules().getBoolean("doMobLoot");
        short short0 = 500;

        if (this.bR != null && !this.bR.d()) {
            short0 = 12000;
        }

        if (!this.world.isClientSide) {
            if (this.bO > 150 && this.bO % 5 == 0 && flag) {
                this.a(MathHelper.d((float) short0 * 0.08F));
            }

            if (this.bO == 1) {
                this.world.a(1028, new BlockPosition(this), 0);
            }
        }

        this.move(EnumMoveType.SELF, 0.0D, 0.10000000149011612D, 0.0D);
        this.yaw += 20.0F;
        this.aQ = this.yaw;
        if (this.bO == 200 && !this.world.isClientSide) {
            if (flag) {
                this.a(MathHelper.d((float) short0 * 0.2F));
            }

            if (this.bR != null) {
                this.bR.a(this);
            }

            this.die();
        }

    }

    private void a(int i) {
        while (i > 0) {
            int j = EntityExperienceOrb.getOrbValue(i);

            i -= j;
            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY, this.locZ, j));
        }

    }

    public int l() {
        if (this.bV[0] == null) {
            for (int i = 0; i < 24; ++i) {
                int j = 5;
                int k;
                int l;

                if (i < 12) {
                    k = (int) (60.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.2617994F * (float) i)));
                    l = (int) (60.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.2617994F * (float) i)));
                } else {
                    int i1;

                    if (i < 20) {
                        i1 = i - 12;
                        k = (int) (40.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.3926991F * (float) i1)));
                        l = (int) (40.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.3926991F * (float) i1)));
                        j += 10;
                    } else {
                        i1 = i - 20;
                        k = (int) (20.0F * MathHelper.cos(2.0F * (-3.1415927F + 0.7853982F * (float) i1)));
                        l = (int) (20.0F * MathHelper.sin(2.0F * (-3.1415927F + 0.7853982F * (float) i1)));
                    }
                }

                int j1 = Math.max(this.world.getSeaLevel() + 10, this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPosition(k, 0, l)).getY() + j);

                this.bV[i] = new PathPoint(k, j1, l);
            }

            this.bW[0] = 6146;
            this.bW[1] = 8197;
            this.bW[2] = 8202;
            this.bW[3] = 16404;
            this.bW[4] = 32808;
            this.bW[5] = 32848;
            this.bW[6] = 65696;
            this.bW[7] = 131392;
            this.bW[8] = 131712;
            this.bW[9] = 263424;
            this.bW[10] = 526848;
            this.bW[11] = 525313;
            this.bW[12] = 1581057;
            this.bW[13] = 3166214;
            this.bW[14] = 2138120;
            this.bW[15] = 6373424;
            this.bW[16] = 4358208;
            this.bW[17] = 12910976;
            this.bW[18] = 9044480;
            this.bW[19] = 9706496;
            this.bW[20] = 15216640;
            this.bW[21] = 13688832;
            this.bW[22] = 11763712;
            this.bW[23] = 8257536;
        }

        return this.k(this.locX, this.locY, this.locZ);
    }

    public int k(double d0, double d1, double d2) {
        float f = 10000.0F;
        int i = 0;
        PathPoint pathpoint = new PathPoint(MathHelper.floor(d0), MathHelper.floor(d1), MathHelper.floor(d2));
        byte b0 = 0;

        if (this.bR == null || this.bR.c() == 0) {
            b0 = 12;
        }

        for (int j = b0; j < 24; ++j) {
            if (this.bV[j] != null) {
                float f1 = this.bV[j].b(pathpoint);

                if (f1 < f) {
                    f = f1;
                    i = j;
                }
            }
        }

        return i;
    }

    @Nullable
    public PathEntity a(int i, int j, @Nullable PathPoint pathpoint) {
        PathPoint pathpoint1;

        for (int k = 0; k < 24; ++k) {
            pathpoint1 = this.bV[k];
            pathpoint1.i = false;
            pathpoint1.g = 0.0F;
            pathpoint1.e = 0.0F;
            pathpoint1.f = 0.0F;
            pathpoint1.h = null;
            pathpoint1.d = -1;
        }

        PathPoint pathpoint2 = this.bV[i];

        pathpoint1 = this.bV[j];
        pathpoint2.e = 0.0F;
        pathpoint2.f = pathpoint2.a(pathpoint1);
        pathpoint2.g = pathpoint2.f;
        this.bX.a();
        this.bX.a(pathpoint2);
        PathPoint pathpoint3 = pathpoint2;
        byte b0 = 0;

        if (this.bR == null || this.bR.c() == 0) {
            b0 = 12;
        }

        label70:
        while (!this.bX.e()) {
            PathPoint pathpoint4 = this.bX.c();

            if (pathpoint4.equals(pathpoint1)) {
                if (pathpoint != null) {
                    pathpoint.h = pathpoint1;
                    pathpoint1 = pathpoint;
                }

                return this.a(pathpoint2, pathpoint1);
            }

            if (pathpoint4.a(pathpoint1) < pathpoint3.a(pathpoint1)) {
                pathpoint3 = pathpoint4;
            }

            pathpoint4.i = true;
            int l = 0;
            int i1 = 0;

            while (true) {
                if (i1 < 24) {
                    if (this.bV[i1] != pathpoint4) {
                        ++i1;
                        continue;
                    }

                    l = i1;
                }

                i1 = b0;

                while (true) {
                    if (i1 >= 24) {
                        continue label70;
                    }

                    if ((this.bW[l] & 1 << i1) > 0) {
                        PathPoint pathpoint5 = this.bV[i1];

                        if (!pathpoint5.i) {
                            float f = pathpoint4.e + pathpoint4.a(pathpoint5);

                            if (!pathpoint5.a() || f < pathpoint5.e) {
                                pathpoint5.h = pathpoint4;
                                pathpoint5.e = f;
                                pathpoint5.f = pathpoint5.a(pathpoint1);
                                if (pathpoint5.a()) {
                                    this.bX.a(pathpoint5, pathpoint5.e + pathpoint5.f);
                                } else {
                                    pathpoint5.g = pathpoint5.e + pathpoint5.f;
                                    this.bX.a(pathpoint5);
                                }
                            }
                        }
                    }

                    ++i1;
                }
            }
        }

        if (pathpoint3 == pathpoint2) {
            return null;
        } else {
            EntityEnderDragon.bQ.debug("Failed to find path from {} to {}", i, j);
            if (pathpoint != null) {
                pathpoint.h = pathpoint3;
                pathpoint3 = pathpoint;
            }

            return this.a(pathpoint2, pathpoint3);
        }
    }

    private PathEntity a(PathPoint pathpoint, PathPoint pathpoint1) {
        int i = 1;

        PathPoint pathpoint2;

        for (pathpoint2 = pathpoint1; pathpoint2.h != null; pathpoint2 = pathpoint2.h) {
            ++i;
        }

        PathPoint[] apathpoint = new PathPoint[i];

        pathpoint2 = pathpoint1;
        --i;

        for (apathpoint[i] = pathpoint1; pathpoint2.h != null; apathpoint[i] = pathpoint2) {
            pathpoint2 = pathpoint2.h;
            --i;
        }

        return new PathEntity(apathpoint);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("DragonPhase", this.bS.a().getControllerPhase().b());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKey("DragonPhase")) {
            this.bS.setControllerPhase(DragonControllerPhase.getById(nbttagcompound.getInt("DragonPhase")));
        }

    }

    protected void I() {}

    public Entity[] bi() {
        return this.children;
    }

    public boolean isInteractable() {
        return false;
    }

    public World J_() {
        return this.world;
    }

    public SoundCategory bV() {
        return SoundCategory.HOSTILE;
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_ENDER_DRAGON_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_ENDER_DRAGON_HURT;
    }

    protected float cD() {
        return 5.0F;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.aH;
    }

    public Vec3D a(float f) {
        IDragonController idragoncontroller = this.bS.a();
        DragonControllerPhase<? extends IDragonController> dragoncontrollerphase = idragoncontroller.getControllerPhase();
        float f1;
        Vec3D vec3d;

        if (dragoncontrollerphase != DragonControllerPhase.LANDING && dragoncontrollerphase != DragonControllerPhase.TAKEOFF) {
            if (idragoncontroller.a()) {
                float f2 = this.pitch;

                f1 = 1.5F;
                this.pitch = -45.0F;
                vec3d = this.f(f);
                this.pitch = f2;
            } else {
                vec3d = this.f(f);
            }
        } else {
            BlockPosition blockposition = this.world.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a);

            f1 = Math.max(MathHelper.sqrt(this.d(blockposition)) / 4.0F, 1.0F);
            float f3 = 6.0F / f1;
            float f4 = this.pitch;
            float f5 = 1.5F;

            this.pitch = -f3 * 1.5F * 5.0F;
            vec3d = this.f(f);
            this.pitch = f4;
        }

        return vec3d;
    }

    public void a(EntityEnderCrystal entityendercrystal, BlockPosition blockposition, DamageSource damagesource) {
        EntityHuman entityhuman;

        if (damagesource.getEntity() instanceof EntityHuman) {
            entityhuman = (EntityHuman) damagesource.getEntity();
        } else {
            entityhuman = this.world.a(blockposition, 64.0D, 64.0D);
        }

        if (entityendercrystal == this.currentEnderCrystal) {
            this.a(this.bD, DamageSource.b(entityhuman), 10.0F);
        }

        this.bS.a().a(entityendercrystal, blockposition, damagesource, entityhuman);
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        if (EntityEnderDragon.PHASE.equals(datawatcherobject) && this.world.isClientSide) {
            this.bS.setControllerPhase(DragonControllerPhase.getById((Integer) this.getDataWatcher().get(EntityEnderDragon.PHASE)));
        }

        super.a(datawatcherobject);
    }

    public DragonControllerManager getDragonControllerManager() {
        return this.bS;
    }

    @Nullable
    public EnderDragonBattle getEnderDragonBattle() {
        return this.bR;
    }

    public boolean addEffect(MobEffect mobeffect) {
        return false;
    }

    protected boolean n(Entity entity) {
        return false;
    }

    public boolean bm() {
        return false;
    }
}
