package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
// CraftBukkit end

public class EntityCow extends EntityAnimal {

    public EntityCow(EntityTypes<? extends EntityCow> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 2.0D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.25D, RecipeItemStack.a(Items.WHEAT), false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.25D));
        this.goalSelector.a(5, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(6, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(7, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(10.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.20000000298023224D);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_COW_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_COW_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_COW_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_COW_STEP, 0.15F, 1.0F);
    }

    @Override
    protected float getSoundVolume() {
        return 0.4F;
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.BUCKET && !entityhuman.abilities.canInstantlyBuild && !this.isBaby()) {
            // CraftBukkit start - Got milk?
            org.bukkit.event.player.PlayerBucketFillEvent event = CraftEventFactory.callPlayerBucketFillEvent(entityhuman.world, entityhuman, this.getChunkCoordinates(), this.getChunkCoordinates(), null, itemstack, Items.MILK_BUCKET, enumhand); // Paper - add enumHand

            if (event.isCancelled()) {
                return false;
            }

            ItemStack result = CraftItemStack.asNMSCopy(event.getItemStack());
            entityhuman.a(SoundEffects.ENTITY_COW_MILK, 1.0F, 1.0F);
            itemstack.subtract(1);
            if (itemstack.isEmpty()) {
                entityhuman.a(enumhand, result);
            } else if (!entityhuman.inventory.pickup(result)) {
                entityhuman.drop(result, false);
            }
            // CraftBukkit end

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    @Override
    public EntityCow createChild(EntityAgeable entityageable) {
        return (EntityCow) EntityTypes.COW.a(this.world);
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return this.isBaby() ? entitysize.height * 0.95F : 1.3F;
    }
}
