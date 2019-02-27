package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityZombieVillager extends EntityZombie {

    public static final DataWatcherObject<Boolean> CONVERTING = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.i);
    private static final DataWatcherObject<Integer> b = DataWatcher.a(EntityZombieVillager.class, DataWatcherRegistry.b);
    public int conversionTime;
    private UUID bD;

    public EntityZombieVillager(World world) {
        super(EntityTypes.ZOMBIE_VILLAGER, world);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityZombieVillager.CONVERTING, false);
        this.datawatcher.register(EntityZombieVillager.b, 0);
    }

    public void setProfession(int i) {
        this.datawatcher.set(EntityZombieVillager.b, i);
    }

    public int getProfession() {
        return Math.max((Integer) this.datawatcher.get(EntityZombieVillager.b) % 6, 0);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Profession", this.getProfession());
        nbttagcompound.setInt("ConversionTime", this.isConverting() ? this.conversionTime : -1);
        if (this.bD != null) {
            nbttagcompound.a("ConversionPlayer", this.bD);
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setProfession(nbttagcompound.getInt("Profession"));
        if (nbttagcompound.hasKeyOfType("ConversionTime", 99) && nbttagcompound.getInt("ConversionTime") > -1) {
            this.startConversion(nbttagcompound.b("ConversionPlayer") ? nbttagcompound.a("ConversionPlayer") : null, nbttagcompound.getInt("ConversionTime"));
        }

    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.setProfession(this.world.random.nextInt(6));
        return super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
    }

    public void tick() {
        if (!this.world.isClientSide && this.isConverting()) {
            int i = this.dK();

            this.conversionTime -= i;
            if (this.conversionTime <= 0) {
                this.dJ();
            }
        }

        super.tick();
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.GOLDEN_APPLE && this.hasEffect(MobEffects.WEAKNESS)) {
            if (!entityhuman.abilities.canInstantlyBuild) {
                itemstack.subtract(1);
            }

            if (!this.world.isClientSide) {
                this.startConversion(entityhuman.getUniqueID(), this.random.nextInt(2401) + 3600);
            }

            return true;
        } else {
            return false;
        }
    }

    protected boolean dC() {
        return false;
    }

    public boolean isTypeNotPersistent() {
        return !this.isConverting();
    }

    public boolean isConverting() {
        return (Boolean) this.getDataWatcher().get(EntityZombieVillager.CONVERTING);
    }

    public void startConversion(@Nullable UUID uuid, int i) {
        this.bD = uuid;
        this.conversionTime = i;
        this.getDataWatcher().set(EntityZombieVillager.CONVERTING, true);
        this.removeEffect(MobEffects.WEAKNESS);
        this.addEffect(new MobEffect(MobEffects.INCREASE_DAMAGE, i, Math.min(this.world.getDifficulty().a() - 1, 0)));
        this.world.broadcastEntityEffect(this, (byte) 16);
    }

    protected void dJ() {
        EntityVillager entityvillager = new EntityVillager(this.world);

        entityvillager.u(this);
        entityvillager.setProfession(this.getProfession());
        entityvillager.a(this.world.getDamageScaler(new BlockPosition(entityvillager)), (GroupDataEntity) null, (NBTTagCompound) null, false);
        entityvillager.dC();
        if (this.isBaby()) {
            entityvillager.setAgeRaw(-24000);
        }

        this.world.kill(this);
        entityvillager.setNoAI(this.isNoAI());
        if (this.hasCustomName()) {
            entityvillager.setCustomName(this.getCustomName());
            entityvillager.setCustomNameVisible(this.getCustomNameVisible());
        }

        this.world.addEntity(entityvillager);
        if (this.bD != null) {
            EntityHuman entityhuman = this.world.b(this.bD);

            if (entityhuman instanceof EntityPlayer) {
                CriterionTriggers.r.a((EntityPlayer) entityhuman, this, entityvillager);
            }
        }

        entityvillager.addEffect(new MobEffect(MobEffects.CONFUSION, 200, 0));
        this.world.a((EntityHuman) null, 1027, new BlockPosition((int) this.locX, (int) this.locY, (int) this.locZ), 0);
    }

    protected int dK() {
        int i = 1;

        if (this.random.nextFloat() < 0.01F) {
            int j = 0;
            BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

            for (int k = (int) this.locX - 4; k < (int) this.locX + 4 && j < 14; ++k) {
                for (int l = (int) this.locY - 4; l < (int) this.locY + 4 && j < 14; ++l) {
                    for (int i1 = (int) this.locZ - 4; i1 < (int) this.locZ + 4 && j < 14; ++i1) {
                        Block block = this.world.getType(blockposition_mutableblockposition.c(k, l, i1)).getBlock();

                        if (block == Blocks.IRON_BARS || block instanceof BlockBed) {
                            if (this.random.nextFloat() < 0.3F) {
                                ++i;
                            }

                            ++j;
                        }
                    }
                }
            }
        }

        return i;
    }

    protected float cE() {
        return this.isBaby() ? (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 2.0F : (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F;
    }

    public SoundEffect D() {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_AMBIENT;
    }

    public SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_HURT;
    }

    public SoundEffect cs() {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_DEATH;
    }

    public SoundEffect dA() {
        return SoundEffects.ENTITY_ZOMBIE_VILLAGER_STEP;
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.az;
    }

    protected ItemStack dB() {
        return ItemStack.a;
    }
}
