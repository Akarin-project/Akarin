package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.event.entity.SheepRegrowWoolEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
import org.bukkit.inventory.InventoryView;
// CraftBukkit end

public class EntitySheep extends EntityAnimal {

    private static final DataWatcherObject<Byte> bC = DataWatcher.a(EntitySheep.class, DataWatcherRegistry.a);
    private final InventoryCrafting container = new InventoryCrafting(new Container() {
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
    private static final Map<EnumColor, IMaterial> bE = (Map) SystemUtils.a(Maps.newEnumMap(EnumColor.class), (enummap) -> { // CraftBukkit - decompile error
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
    private static final Map<EnumColor, float[]> bG = Maps.newEnumMap((Map) Arrays.stream(EnumColor.values()).collect(Collectors.toMap((enumcolor) -> {
        return enumcolor;
    }, EntitySheep::c)));
    private int bH;
    private PathfinderGoalEatTile bI;

    private static float[] c(EnumColor enumcolor) {
        if (enumcolor == EnumColor.WHITE) {
            return new float[] { 0.9019608F, 0.9019608F, 0.9019608F};
        } else {
            float[] afloat = enumcolor.d();
            float f = 0.75F;

            return new float[] { afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
        }
    }

    public EntitySheep(World world) {
        super(EntityTypes.SHEEP, world);
        this.setSize(0.9F, 1.3F);
    }

    protected void n() {
        this.bI = new PathfinderGoalEatTile(this);
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalPanic(this, 1.25D));
        this.goalSelector.a(2, new PathfinderGoalBreed(this, 1.0D));
        this.goalSelector.a(3, new PathfinderGoalTempt(this, 1.1D, RecipeItemStack.a(Items.WHEAT), false));
        this.goalSelector.a(4, new PathfinderGoalFollowParent(this, 1.1D));
        this.goalSelector.a(5, this.bI);
        this.goalSelector.a(6, new PathfinderGoalRandomStrollLand(this, 1.0D));
        this.goalSelector.a(7, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 6.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomLookaround(this));
    }

    protected void mobTick() {
        this.bH = this.bI.g();
        super.mobTick();
    }

    public void movementTick() {
        if (this.world.isClientSide) {
            this.bH = Math.max(0, this.bH - 1);
        }

        super.movementTick();
    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue(8.0D);
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.23000000417232513D);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntitySheep.bC, (byte) 0);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        if (this.isSheared()) {
            return LootTables.W;
        } else {
            switch (this.getColor()) {
            case WHITE:
            default:
                return LootTables.X;
            case ORANGE:
                return LootTables.Y;
            case MAGENTA:
                return LootTables.Z;
            case LIGHT_BLUE:
                return LootTables.aa;
            case YELLOW:
                return LootTables.ab;
            case LIME:
                return LootTables.ac;
            case PINK:
                return LootTables.ad;
            case GRAY:
                return LootTables.ae;
            case LIGHT_GRAY:
                return LootTables.af;
            case CYAN:
                return LootTables.ag;
            case PURPLE:
                return LootTables.ah;
            case BLUE:
                return LootTables.ai;
            case BROWN:
                return LootTables.aj;
            case GREEN:
                return LootTables.ak;
            case RED:
                return LootTables.al;
            case BLACK:
                return LootTables.am;
            }
        }
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) {
            if (!this.world.isClientSide) {
                // CraftBukkit start
                PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), this.getBukkitEntity());
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }
                // CraftBukkit end

                this.setSheared(true);
                int i = 1 + this.random.nextInt(3);

                for (int j = 0; j < i; ++j) {
                    this.forceDrops = true; // CraftBukkit
                    EntityItem entityitem = this.a((IMaterial) EntitySheep.bE.get(this.getColor()), 1);
                    this.forceDrops = false; // CraftBukkit

                    if (entityitem != null) {
                        entityitem.motY += (double) (this.random.nextFloat() * 0.05F);
                        entityitem.motX += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                        entityitem.motZ += (double) ((this.random.nextFloat() - this.random.nextFloat()) * 0.1F);
                    }
                }
            }

            itemstack.damage(1, entityhuman);
            this.a(SoundEffects.ENTITY_SHEEP_SHEAR, 1.0F, 1.0F);
        }

        return super.a(entityhuman, enumhand);
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setBoolean("Sheared", this.isSheared());
        nbttagcompound.setByte("Color", (byte) this.getColor().getColorIndex());
    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setSheared(nbttagcompound.getBoolean("Sheared"));
        this.setColor(EnumColor.fromColorIndex(nbttagcompound.getByte("Color")));
    }

    protected SoundEffect D() {
        return SoundEffects.ENTITY_SHEEP_AMBIENT;
    }

    protected SoundEffect d(DamageSource damagesource) {
        return SoundEffects.ENTITY_SHEEP_HURT;
    }

    protected SoundEffect cs() {
        return SoundEffects.ENTITY_SHEEP_DEATH;
    }

    protected void a(BlockPosition blockposition, IBlockData iblockdata) {
        this.a(SoundEffects.ENTITY_SHEEP_STEP, 0.15F, 1.0F);
    }

    public EnumColor getColor() {
        return EnumColor.fromColorIndex((Byte) this.datawatcher.get(EntitySheep.bC) & 15);
    }

    public void setColor(EnumColor enumcolor) {
        byte b0 = (Byte) this.datawatcher.get(EntitySheep.bC);

        this.datawatcher.set(EntitySheep.bC, (byte) (b0 & 240 | enumcolor.getColorIndex() & 15));
    }

    public boolean isSheared() {
        return ((Byte) this.datawatcher.get(EntitySheep.bC) & 16) != 0;
    }

    public void setSheared(boolean flag) {
        byte b0 = (Byte) this.datawatcher.get(EntitySheep.bC);

        if (flag) {
            this.datawatcher.set(EntitySheep.bC, (byte) (b0 | 16));
        } else {
            this.datawatcher.set(EntitySheep.bC, (byte) (b0 & -17));
        }

    }

    public static EnumColor a(Random random) {
        int i = random.nextInt(100);

        return i < 5 ? EnumColor.BLACK : (i < 10 ? EnumColor.GRAY : (i < 15 ? EnumColor.LIGHT_GRAY : (i < 18 ? EnumColor.BROWN : (random.nextInt(500) == 0 ? EnumColor.PINK : EnumColor.WHITE))));
    }

    public EntitySheep createChild(EntityAgeable entityageable) {
        EntitySheep entitysheep = (EntitySheep) entityageable;
        EntitySheep entitysheep1 = new EntitySheep(this.world);

        entitysheep1.setColor(this.a((EntityAnimal) this, (EntityAnimal) entitysheep));
        return entitysheep1;
    }

    public void x() {
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
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        groupdataentity = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        this.setColor(a(this.world.random));
        return groupdataentity;
    }

    private EnumColor a(EntityAnimal entityanimal, EntityAnimal entityanimal1) {
        EnumColor enumcolor = ((EntitySheep) entityanimal).getColor();
        EnumColor enumcolor1 = ((EntitySheep) entityanimal1).getColor();

        this.container.setItem(0, new ItemStack(ItemDye.a(enumcolor)));
        this.container.setItem(1, new ItemStack(ItemDye.a(enumcolor1)));
        this.container.resultInventory = new InventoryCraftResult(); // CraftBukkit - add result slot for event
        ItemStack itemstack = entityanimal.world.getCraftingManager().craft(this.container, ((EntitySheep) entityanimal).world);
        Item item = itemstack.getItem();
        EnumColor enumcolor2;

        if (item instanceof ItemDye) {
            enumcolor2 = ((ItemDye) item).d();
        } else {
            enumcolor2 = this.world.random.nextBoolean() ? enumcolor : enumcolor1;
        }

        return enumcolor2;
    }

    public float getHeadHeight() {
        return 0.95F * this.length;
    }
}
