package net.minecraft.server;

import java.util.EnumSet;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.inventory.CraftMerchantRecipe;
import org.bukkit.entity.AbstractVillager;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
// CraftBukkit end

public class EntityVillagerTrader extends EntityVillagerAbstract {

    @Nullable
    private BlockPosition bA;
    private int bB;

    public EntityVillagerTrader(EntityTypes<? extends EntityVillagerTrader> entitytypes, World world) {
        super(entitytypes, world);
        this.attachedToPlayer = true;
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(0, new PathfinderGoalUseItem<>(this, PotionUtil.a(new ItemStack(Items.POTION), Potions.INVISIBILITY), SoundEffects.ENTITY_WANDERING_TRADER_DISAPPEARED, (entityvillagertrader) -> {
            return !this.world.J() && !entityvillagertrader.isInvisible();
        }));
        this.goalSelector.a(0, new PathfinderGoalUseItem<>(this, new ItemStack(Items.MILK_BUCKET), SoundEffects.ENTITY_WANDERING_TRADER_REAPPEARED, (entityvillagertrader) -> {
            return this.world.J() && entityvillagertrader.isInvisible();
        }));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityZombie.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityEvoker.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVindicator.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityVex.class, 8.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityPillager.class, 15.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalAvoidTarget<>(this, EntityIllagerIllusioner.class, 12.0F, 0.5D, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 0.5D));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new EntityVillagerTrader.a(this, 2.0D, 0.35D));
        this.goalSelector.a(4, new PathfinderGoalMoveTowardsRestriction(this, 1.0D));
        this.goalSelector.a(8, new PathfinderGoalRandomStrollLand(this, 0.35D));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        return null;
    }

    @Override
    public boolean ea() {
        return false;
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = itemstack.getItem() == Items.NAME_TAG;

        if (flag) {
            itemstack.a(entityhuman, (EntityLiving) this, enumhand);
            return true;
        } else if (itemstack.getItem() != Items.VILLAGER_SPAWN_EGG && this.isAlive() && !this.dY() && !this.isBaby()) {
            if (enumhand == EnumHand.MAIN_HAND) {
                entityhuman.a(StatisticList.TALKED_TO_VILLAGER);
            }

            if (this.getOffers().isEmpty()) {
                return super.a(entityhuman, enumhand);
            } else {
                if (!this.world.isClientSide) {
                    this.setTradingPlayer(entityhuman);
                    this.openTrade(entityhuman, this.getScoreboardDisplayName(), 1);
                }

                return true;
            }
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    @Override
    protected void eh() {
        VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption = (VillagerTrades.IMerchantRecipeOption[]) VillagerTrades.b.get(1);
        VillagerTrades.IMerchantRecipeOption[] avillagertrades_imerchantrecipeoption1 = (VillagerTrades.IMerchantRecipeOption[]) VillagerTrades.b.get(2);

        if (avillagertrades_imerchantrecipeoption != null && avillagertrades_imerchantrecipeoption1 != null) {
            MerchantRecipeList merchantrecipelist = this.getOffers();

            this.a(merchantrecipelist, avillagertrades_imerchantrecipeoption, 5);
            int i = this.random.nextInt(avillagertrades_imerchantrecipeoption1.length);
            VillagerTrades.IMerchantRecipeOption villagertrades_imerchantrecipeoption = avillagertrades_imerchantrecipeoption1[i];
            MerchantRecipe merchantrecipe = villagertrades_imerchantrecipeoption.a(this, this.random);

            if (merchantrecipe != null) {
                // CraftBukkit start
                VillagerAcquireTradeEvent event = new VillagerAcquireTradeEvent((AbstractVillager) getBukkitEntity(), merchantrecipe.asBukkit());
                // Suppress during worldgen
                if (this.valid) {
                    Bukkit.getPluginManager().callEvent(event);
                }
                if (!event.isCancelled()) {
                    merchantrecipelist.add(CraftMerchantRecipe.fromBukkit(event.getRecipe()).toMinecraft());
                }
                // CraftBukkit end
            }

        }
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("DespawnDelay", this.bB);
        if (this.bA != null) {
            nbttagcompound.set("WanderTarget", GameProfileSerializer.a(this.bA));
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        if (nbttagcompound.hasKeyOfType("DespawnDelay", 99)) {
            this.bB = nbttagcompound.getInt("DespawnDelay");
        }

        if (nbttagcompound.hasKey("WanderTarget")) {
            this.bA = GameProfileSerializer.c(nbttagcompound.getCompound("WanderTarget"));
        }

        this.setAgeRaw(Math.max(0, this.getAge()));
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return false;
    }

    @Override
    protected void b(MerchantRecipe merchantrecipe) {
        if (merchantrecipe.isRewardExp()) {
            int i = 3 + this.random.nextInt(4);

            this.world.addEntity(new EntityExperienceOrb(this.world, this.locX, this.locY + 0.5D, this.locZ, i, org.bukkit.entity.ExperienceOrb.SpawnReason.VILLAGER_TRADE, this.getTrader(), this)); // Paper
        }

    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return this.dY() ? SoundEffects.ENTITY_WANDERING_TRADER_TRADE : SoundEffects.ENTITY_WANDERING_TRADER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_WANDERING_TRADER_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_WANDERING_TRADER_DEATH;
    }

    @Override
    protected SoundEffect c(ItemStack itemstack) {
        Item item = itemstack.getItem();

        return item == Items.MILK_BUCKET ? SoundEffects.ENTITY_WANDERING_TRADER_DRINK_MILK : SoundEffects.ENTITY_WANDERING_TRADER_DRINK_POTION;
    }

    @Override
    protected SoundEffect r(boolean flag) {
        return flag ? SoundEffects.ENTITY_WANDERING_TRADER_YES : SoundEffects.ENTITY_WANDERING_TRADER_NO;
    }

    @Override
    public SoundEffect eb() {
        return SoundEffects.ENTITY_WANDERING_TRADER_YES;
    }

    public void t(int i) {
        this.bB = i;
    }

    public int ef() {
        return this.bB;
    }

    @Override
    public void movementTick() {
        super.movementTick();
        if (!this.world.isClientSide) {
            this.ej();
        }

    }

    private void ej() {
        if (this.bB > 0 && !this.dY() && --this.bB == 0) {
            this.die();
        }

    }

    public void g(@Nullable BlockPosition blockposition) {
        this.bA = blockposition;
    }

    @Nullable
    private BlockPosition ek() {
        return this.bA;
    }

    class a extends PathfinderGoal {

        final EntityVillagerTrader a;
        final double b;
        final double c;

        a(EntityVillagerTrader entityvillagertrader, double d0, double d1) {
            this.a = entityvillagertrader;
            this.b = d0;
            this.c = d1;
            this.a(EnumSet.of(PathfinderGoal.Type.MOVE));
        }

        @Override
        public void d() {
            this.a.g((BlockPosition) null);
            EntityVillagerTrader.this.navigation.o();
        }

        @Override
        public boolean a() {
            BlockPosition blockposition = this.a.ek();

            return blockposition != null && this.a(blockposition, this.b);
        }

        @Override
        public void e() {
            BlockPosition blockposition = this.a.ek();

            if (blockposition != null && EntityVillagerTrader.this.navigation.n()) {
                if (this.a(blockposition, 10.0D)) {
                    Vec3D vec3d = (new Vec3D((double) blockposition.getX() - this.a.locX, (double) blockposition.getY() - this.a.locY, (double) blockposition.getZ() - this.a.locZ)).d();
                    Vec3D vec3d1 = vec3d.a(10.0D).add(this.a.locX, this.a.locY, this.a.locZ);

                    EntityVillagerTrader.this.navigation.a(vec3d1.x, vec3d1.y, vec3d1.z, this.c);
                } else {
                    EntityVillagerTrader.this.navigation.a((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ(), this.c);
                }
            }

        }

        private boolean a(BlockPosition blockposition, double d0) {
            return !blockposition.a((IPosition) this.a.getPositionVector(), d0);
        }
    }
}
