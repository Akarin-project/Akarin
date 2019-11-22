package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.InventoryView;
// CraftBukkit end

public class EntitySheep extends EntityAnimal {

    private static final DataWatcherObject<Byte> bz = DataWatcher.a(EntitySheep.class, DataWatcherRegistry.a);
    private static final Map<EnumColor, IMaterial> bA = (Map) SystemUtils.a(Maps.newEnumMap(EnumColor.class), (enummap) -> { // CraftBukkit - decompile error
        enummap.put(EnumColor.WHITE, Blocks.WHITE_WOOL);
        enummap.put(EnumColor.ORANGE, Blocks.ORANGE_WOOL);
        enummap.put(EnumColor.MAGENTA, Blocks.MAGENTA_WOOL);
        enummap.put(EnumColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
        enummap.put(EnumColor.YELLOW, Blocks.YELLOW_WOOL);
        enummap.put(EnumColor.LIME, Blocks.LIME_WOOL);
        enummap.put(EnumColor.PINK, Blocks.PINK_WOOL);
        enummap.put(EnumColor.GRAY, Blocks.GRAY_WOOL);
        enummap.put(EnumColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
        enummap.put(EnumColor.CYAN, Blocks.CYAN_WOOL);
        enummap.put(EnumColor.PURPLE, Blocks.PURPLE_WOOL);
        enummap.put(EnumColor.BLUE, Blocks.BLUE_WOOL);
        enummap.put(EnumColor.BROWN, Blocks.BROWN_WOOL);
        enummap.put(EnumColor.GREEN, Blocks.GREEN_WOOL);
        enummap.put(EnumColor.RED, Blocks.RED_WOOL);
        enummap.put(EnumColor.BLACK, Blocks.BLACK_WOOL);
    });
    private static final Map<EnumColor, float[]> bB = Maps.newEnumMap((Map) Arrays.stream(EnumColor.values()).collect(Collectors.toMap((enumcolor) -> {
        return enumcolor;
    }, EntitySheep::c)));
    private int bC;
    private PathfinderGoalEatTile bD;

    private static float[] c(EnumColor enumcolor) {
        if (enumcolor == EnumColor.WHITE) {
            return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
        } else {
            float[] afloat = enumcolor.d();
            float f = 0.75F;

            return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
        }
    }

    public EntitySheep(EntityTypes<? extends EntitySheep> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        this.bD = new PathfinderGoalEatTile(this);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.1D, RecipeItemStack.a(Items.WHEAT), false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, this.bD);
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    @Override
    protected void mobTick() {
        this.bC = this.bD.g();
        super.mobTick();
    }

    @Override
    public void movementTick() {
        if (this.world.isClientSide) {
            this.bC = Math.max(0, this.bC - 1);
        }

        super.movementTick();
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(8.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntitySheep.bz, (byte) 0);
    }

    @Override
    public MinecraftKey getDefaultLootTable() {
        if (this.isSheared()) {
            return this.getEntityType().h();
        } else {
            switch (this.getColor()) {
                case WHITE:
                default:
                    return LootTables.L;
                case ORANGE:
                    return LootTables.M;
                case MAGENTA:
                    return LootTables.N;
                case LIGHT_BLUE:
                    return LootTables.O;
                case YELLOW:
                    return LootTables.P;
                case LIME:
                    return LootTables.Q;
                case PINK:
                    return LootTables.R;
                case GRAY:
                    return LootTables.S;
                case LIGHT_GRAY:
                    return LootTables.T;
                case CYAN:
                    return LootTables.U;
                case PURPLE:
                    return LootTables.V;
                case BLUE:
                    return LootTables.W;
                case BROWN:
                    return LootTables.X;
                case GREEN:
                    return LootTables.Y;
                case RED:
                    return LootTables.Z;
                case BLACK:
                    return LootTables.aa;
            }
        }
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
            // CraftBukkit start
            PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), this.getBukkitEntity());
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end

            this.shear();
            if (!this.world.isClientSide) {
                itemstack.damage(1, entityhuman, (entityhuman1) -> {
                    entityhuman1.d(enumhand);
                });
            }
        }

        return super.a(entityhuman, enumhand);
    }

    public void shear() {
        if (!this.world.isClientSide) {
            this.setSheared(true);
            int i = 1 + this.random.nextInt(3);

            for (int j = 0; j < i; ++j) {
                this.forceDrops = true; // CraftBukkit
                EntityItem entityitem = this.a((IMaterial) EntitySheep.bA.get(this.getColor()), 1);
                this.forceDrops = false; // CraftBukkit

                if (entityitem != null) {
                    entityitem.setMot(entityitem.getMot().add((double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double) (this.random.nextFloat() * 0.05F), (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
                }
            }
        }

        this.a(SoundEffects.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Sheared", this.isSheared());
        nbttagcompound.setByte("Color", (byte) this.getColor().getColorIndex());
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSheared(nbttagcompound.getBoolean("Sheared"));
        this.setColor(EnumColor.fromColorIndex(nbttagcompound.getByte("Color")));
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_SHEEP_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_SHEEP_HURT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_SHEEP_DEATH;
    }

    @Override
    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    public EnumColor getColor() {
        return EnumColor.fromColorIndex((Byte) this.datawatcher.get(EntitySheep.bz) & 15);
    }

    public void setColor(EnumColor enumcolor) {
        byte b0 = (Byte) this.datawatcher.get(EntitySheep.bz);

        this.datawatcher.set(EntitySheep.bz, (byte) (b0 & 240 | enumcolor.getColorIndex() & 15));
    }

    public boolean isSheared() {
        return ((Byte) this.datawatcher.get(EntitySheep.bz) & 16) != 0;
    }

    public void setSheared(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntitySheep.bz);

        if (flag) {
            this.datawatcher.set(EntitySheep.bz, (byte) (b0 | 16));
        } else {
            this.datawatcher.set(EntitySheep.bz, (byte) (b0 & -17));
        }

    }

    public static EnumColor a(Random random) {
        int i = random.nextInt(100);

        return i < 5 ? EnumColor.BLACK : (i < 10 ? EnumColor.GRAY : (i < 15 ? EnumColor.LIGHT_GRAY : (i < 18 ? EnumColor.BROWN : (random.nextInt(500) == 0 ? EnumColor.PINK : EnumColor.WHITE))));
    }

    @Override
    public EntitySheep createChild(EntityAgeable entityageable) {
        EntitySheep entitysheep = (EntitySheep) entityageable;
        EntitySheep entitysheep1 = (EntitySheep) EntityTypes.SHEEP.a(this.world);

        entitysheep1.setColor(this.a((EntityAnimal) this, (EntityAnimal) entitysheep));
        return entitysheep1;
    }

    @Override
    public void blockEaten() {
        // CraftBukkit start
        SheepRegrowWoolEvent event = new SheepRegrowWoolEvent((org.bukkit.entity.Sheep) this.getBukkitEntity());
        this.world.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;
        // CraftBukkit end
        this.setSheared(false);
        if (this.isBaby()) {
            this.setAge(60);
        }

    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
        this.setColor(a(generatoraccess.getRandom()));
        return groupdataentity;
    }

    private EnumColor a(EntityAnimal entityanimal, EntityAnimal entityanimal1) {
        EnumColor enumcolor = ((EntitySheep) entityanimal).getColor();
        EnumColor enumcolor1 = ((EntitySheep) entityanimal1).getColor();
        InventoryCrafting inventorycrafting = a(enumcolor, enumcolor1);
        Optional<Item> optional = this.world.getCraftingManager().craft(Recipes.CRAFTING, inventorycrafting, this.world).map((recipecrafting) -> { // Eclipse fail
            return recipecrafting.a(inventorycrafting);
        }).map(ItemStack::getItem);

        ItemDye.class.getClass();
        optional = optional.filter(ItemDye.class::isInstance);
        ItemDye.class.getClass();
        return (EnumColor) optional.map(ItemDye.class::cast).map(ItemDye::d).orElseGet(() -> {
            return this.world.random.nextBoolean() ? enumcolor : enumcolor1;
        });
    }

    private static InventoryCrafting a(EnumColor enumcolor, EnumColor enumcolor1) {
        InventoryCrafting inventorycrafting = new InventoryCrafting(new Container((Containers) null, -1) {
            @Override
            public boolean canUse(EntityHuman entityhuman) {
                return false;
            }

            // CraftBukkit start
            @Override
            public InventoryView getBukkitView() {
                return null; // TODO: O.O
            }
            // CraftBukkit end
        }, 2, 1);

        inventorycrafting.setItem(0, new ItemStack(ItemDye.a(enumcolor)));
        inventorycrafting.setItem(1, new ItemStack(ItemDye.a(enumcolor1)));
        inventorycrafting.resultInventory = new InventoryCraftResult(); // CraftBukkit - add result slot for event
        return inventorycrafting;
    }

    @Override
    protected float b(EntityPose entitypose, EntitySize entitysize) {
        return 0.95F * entitysize.height;
    }
}
