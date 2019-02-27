package net.minecraft.server;

import java.util.UUID;
import javax.annotation.Nullable;

public class EntityHorse extends EntityHorseAbstract {

    private static final UUID bM = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F295");
    private static final DataWatcherObject<Integer> bN = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final DataWatcherObject<Integer> bO = DataWatcher.a(EntityHorse.class, DataWatcherRegistry.b);
    private static final String[] bP = new String[] { "textures/entity/horse/horse_white.png", "textures/entity/horse/horse_creamy.png", "textures/entity/horse/horse_chestnut.png", "textures/entity/horse/horse_brown.png", "textures/entity/horse/horse_black.png", "textures/entity/horse/horse_gray.png", "textures/entity/horse/horse_darkbrown.png"};
    private static final String[] bQ = new String[] { "hwh", "hcr", "hch", "hbr", "hbl", "hgr", "hdb"};
    private static final String[] bR = new String[] { null, "textures/entity/horse/horse_markings_white.png", "textures/entity/horse/horse_markings_whitefield.png", "textures/entity/horse/horse_markings_whitedots.png", "textures/entity/horse/horse_markings_blackdots.png"};
    private static final String[] bS = new String[] { "", "wo_", "wmo", "wdo", "bdo"};
    private String bT;
    private final String[] bU = new String[3];

    public EntityHorse(World world) {
        super(EntityTypes.HORSE, world);
    }

    protected void x_() {
        super.x_();
        this.datawatcher.register(EntityHorse.bN, 0);
        this.datawatcher.register(EntityHorse.bO, EnumHorseArmor.NONE.a());
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setInt("Variant", this.getVariant());
        if (!this.inventoryChest.getItem(1).isEmpty()) {
            nbttagcompound.set("ArmorItem", this.inventoryChest.getItem(1).save(new NBTTagCompound()));
        }

    }

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(nbttagcompound.getInt("Variant"));
        if (nbttagcompound.hasKeyOfType("ArmorItem", 10)) {
            ItemStack itemstack = ItemStack.a(nbttagcompound.getCompound("ArmorItem"));

            if (!itemstack.isEmpty() && EnumHorseArmor.b(itemstack.getItem())) {
                this.inventoryChest.setItem(1, itemstack);
            }
        }

        this.dS();
    }

    public void setVariant(int i) {
        this.datawatcher.set(EntityHorse.bN, i);
        this.eg();
    }

    public int getVariant() {
        return (Integer) this.datawatcher.get(EntityHorse.bN);
    }

    private void eg() {
        this.bT = null;
    }

    protected void dS() {
        super.dS();
        this.h(this.inventoryChest.getItem(1));
    }

    public void h(ItemStack itemstack) {
        EnumHorseArmor enumhorsearmor = EnumHorseArmor.a(itemstack);

        this.datawatcher.set(EntityHorse.bO, enumhorsearmor.a());
        this.eg();
        if (!this.world.isClientSide) {
            this.getAttributeInstance(GenericAttributes.h).b(EntityHorse.bM);
            int i = enumhorsearmor.c();

            if (i != 0) {
                this.getAttributeInstance(GenericAttributes.h).b((new AttributeModifier(EntityHorse.bM, "Horse armor bonus", (double) i, 0)).a(false));
            }
        }

    }

    public EnumHorseArmor dH() {
        return EnumHorseArmor.a((Integer) this.datawatcher.get(EntityHorse.bO));
    }

    public void a(IInventory iinventory) {
        EnumHorseArmor enumhorsearmor = this.dH();

        super.a(iinventory);
        EnumHorseArmor enumhorsearmor1 = this.dH();

        if (this.ticksLived > 20 && enumhorsearmor != enumhorsearmor1 && enumhorsearmor1 != EnumHorseArmor.NONE) {
            this.a(SoundEffects.ENTITY_HORSE_ARMOR, 0.5F, 1.0F);
        }

    }

    protected void a(SoundEffectType soundeffecttype) {
        super.a(soundeffecttype);
        if (this.random.nextInt(10) == 0) {
            this.a(SoundEffects.ENTITY_HORSE_BREATHE, soundeffecttype.a() * 0.6F, soundeffecttype.b());
        }

    }

    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.maxHealth).setValue((double) this.ec());
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(this.ee());
        this.getAttributeInstance(EntityHorse.attributeJumpStrength).setValue(this.ed());
    }

    public void tick() {
        super.tick();
        if (this.world.isClientSide && this.datawatcher.a()) {
            this.datawatcher.e();
            this.eg();
        }

    }

    protected SoundEffect D() {
        super.D();
        return SoundEffects.ENTITY_HORSE_AMBIENT;
    }

    protected SoundEffect cs() {
        super.cs();
        return SoundEffects.ENTITY_HORSE_DEATH;
    }

    protected SoundEffect d(DamageSource damagesource) {
        super.d(damagesource);
        return SoundEffects.ENTITY_HORSE_HURT;
    }

    protected SoundEffect dB() {
        super.dB();
        return SoundEffects.ENTITY_HORSE_ANGRY;
    }

    protected MinecraftKey getDefaultLootTable() {
        return LootTables.N;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        boolean flag = !itemstack.isEmpty();

        if (flag && itemstack.getItem() instanceof ItemMonsterEgg) {
            return super.a(entityhuman, enumhand);
        } else {
            if (!this.isBaby()) {
                if (this.isTamed() && entityhuman.isSneaking()) {
                    this.c(entityhuman);
                    return true;
                }

                if (this.isVehicle()) {
                    return super.a(entityhuman, enumhand);
                }
            }

            if (flag) {
                if (this.b(entityhuman, itemstack)) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return true;
                }

                if (itemstack.a(entityhuman, (EntityLiving) this, enumhand)) {
                    return true;
                }

                if (!this.isTamed()) {
                    this.dZ();
                    return true;
                }

                boolean flag1 = EnumHorseArmor.a(itemstack) != EnumHorseArmor.NONE;
                boolean flag2 = !this.isBaby() && !this.dV() && itemstack.getItem() == Items.SADDLE;

                if (flag1 || flag2) {
                    this.c(entityhuman);
                    return true;
                }
            }

            if (this.isBaby()) {
                return super.a(entityhuman, enumhand);
            } else {
                this.g(entityhuman);
                return true;
            }
        }
    }

    public boolean mate(EntityAnimal entityanimal) {
        return entityanimal == this ? false : (!(entityanimal instanceof EntityHorseDonkey) && !(entityanimal instanceof EntityHorse) ? false : this.eb() && ((EntityHorseAbstract) entityanimal).eb());
    }

    public EntityAgeable createChild(EntityAgeable entityageable) {
        Object object;

        if (entityageable instanceof EntityHorseDonkey) {
            object = new EntityHorseMule(this.world);
        } else {
            EntityHorse entityhorse = (EntityHorse) entityageable;

            object = new EntityHorse(this.world);
            int i = this.random.nextInt(9);
            int j;

            if (i < 4) {
                j = this.getVariant() & 255;
            } else if (i < 8) {
                j = entityhorse.getVariant() & 255;
            } else {
                j = this.random.nextInt(7);
            }

            int k = this.random.nextInt(5);

            if (k < 2) {
                j |= this.getVariant() & '\uff00';
            } else if (k < 4) {
                j |= entityhorse.getVariant() & '\uff00';
            } else {
                j |= this.random.nextInt(5) << 8 & '\uff00';
            }

            ((EntityHorse) object).setVariant(j);
        }

        this.a(entityageable, (EntityHorseAbstract) object);
        return (EntityAgeable) object;
    }

    public boolean ef() {
        return true;
    }

    public boolean g(ItemStack itemstack) {
        return EnumHorseArmor.b(itemstack.getItem());
    }

    @Nullable
    public GroupDataEntity prepare(DifficultyDamageScaler difficultydamagescaler, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        Object object = super.prepare(difficultydamagescaler, groupdataentity, nbttagcompound);
        int i;

        if (object instanceof EntityHorse.a) {
            i = ((EntityHorse.a) object).a;
        } else {
            i = this.random.nextInt(7);
            object = new EntityHorse.a(i);
        }

        this.setVariant(i | this.random.nextInt(5) << 8);
        return (GroupDataEntity) object;
    }

    public static class a implements GroupDataEntity {

        public int a;

        public a(int i) {
            this.a = i;
        }
    }
}
