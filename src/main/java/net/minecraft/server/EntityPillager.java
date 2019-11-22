package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Map;
import javax.annotation.Nullable;

public class EntityPillager extends EntityIllagerAbstract implements ICrossbow, IRangedEntity {

    private static final DataWatcherObject<Boolean> b = DataWatcher.a(EntityPillager.class, DataWatcherRegistry.i);
    private final InventorySubcontainer inventory = new InventorySubcontainer(5);

    public EntityPillager(EntityTypes<? extends EntityPillager> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    protected void initPathfinder() {
        super.initPathfinder();
        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(2, new EntityRaider.a(this, 10.0F));
        this.goalSelector.a(3, new PathfinderGoalCrossbowAttack<>(this, 1.0D, 8.0F));
        this.goalSelector.a(8, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(9, new PathfinderGoalLookAtPlayer(this, EntityHuman.class, 15.0F, 1.0F));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 15.0F));
        this.targetSelector.a(1, (new PathfinderGoalHurtByTarget(this, new Class[]{EntityRaider.class})).a(new Class[0])); // CraftBukkit - decompile error
        this.targetSelector.a(2, new PathfinderGoalNearestAttackableTarget<>(this, EntityHuman.class, true));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityVillagerAbstract.class, false));
        this.targetSelector.a(3, new PathfinderGoalNearestAttackableTarget<>(this, EntityIronGolem.class, true));
    }

    @Override
    protected void initAttributes() {
        super.initAttributes();
        this.getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).setValue(0.3499999940395355D);
        this.getAttributeInstance(GenericAttributes.MAX_HEALTH).setValue(24.0D);
        this.getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).setValue(5.0D);
        this.getAttributeInstance(GenericAttributes.FOLLOW_RANGE).setValue(32.0D);
    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityPillager.b, false);
    }

    @Override
    public void a(boolean flag) {
        this.datawatcher.set(EntityPillager.b, flag);
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            ItemStack itemstack = this.inventory.getItem(i);

            if (!itemstack.isEmpty()) {
                nbttaglist.add(itemstack.save(new NBTTagCompound()));
            }
        }

        nbttagcompound.set("Inventory", nbttaglist);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Inventory", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            ItemStack itemstack = ItemStack.a(nbttaglist.getCompound(i));

            if (!itemstack.isEmpty()) {
                this.inventory.a(itemstack);
            }
        }

        this.setCanPickupLoot(true);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        Block block = iworldreader.getType(blockposition.down()).getBlock();

        return block != Blocks.GRASS_BLOCK && block != Blocks.SAND ? 0.5F - iworldreader.v(blockposition) : 10.0F;
    }

    @Override
    public int dC() {
        return 1;
    }

    @Nullable
    @Override
    public GroupDataEntity prepare(GeneratorAccess generatoraccess, DifficultyDamageScaler difficultydamagescaler, EnumMobSpawn enummobspawn, @Nullable GroupDataEntity groupdataentity, @Nullable NBTTagCompound nbttagcompound) {
        this.a(difficultydamagescaler);
        this.b(difficultydamagescaler);
        return super.prepare(generatoraccess, difficultydamagescaler, enummobspawn, groupdataentity, nbttagcompound);
    }

    @Override
    protected void a(DifficultyDamageScaler difficultydamagescaler) {
        ItemStack itemstack = new ItemStack(Items.CROSSBOW);

        if (this.random.nextInt(300) == 0) {
            Map<Enchantment, Integer> map = Maps.newHashMap();

            map.put(Enchantments.PIERCING, 1);
            EnchantmentManager.a((Map) map, itemstack);
        }

        this.setSlot(EnumItemSlot.MAINHAND, itemstack);
    }

    @Override
    public boolean r(Entity entity) {
        return super.r(entity) ? true : (entity instanceof EntityLiving && ((EntityLiving) entity).getMonsterType() == EnumMonsterType.ILLAGER ? this.getScoreboardTeam() == null && entity.getScoreboardTeam() == null : false);
    }

    @Override
    protected SoundEffect getSoundAmbient() {
        return SoundEffects.ENTITY_PILLAGER_AMBIENT;
    }

    @Override
    protected SoundEffect getSoundDeath() {
        return SoundEffects.ENTITY_PILLAGER_DEATH;
    }

    @Override
    protected SoundEffect getSoundHurt(DamageSource damagesource) {
        return SoundEffects.ENTITY_PILLAGER_HURT;
    }

    @Override
    public void a(EntityLiving entityliving, float f) {
        EnumHand enumhand = ProjectileHelper.a(this, Items.CROSSBOW);
        ItemStack itemstack = this.b(enumhand);

        if (this.a(Items.CROSSBOW)) {
            ItemCrossbow.a(this.world, this, enumhand, itemstack, 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        }

        this.ticksFarFromPlayer = 0;
    }

    @Override
    public void a(EntityLiving entityliving, ItemStack itemstack, IProjectile iprojectile, float f) {
        Entity entity = (Entity) iprojectile;
        double d0 = entityliving.locX - this.locX;
        double d1 = entityliving.locZ - this.locZ;
        double d2 = (double) MathHelper.sqrt(d0 * d0 + d1 * d1);
        double d3 = entityliving.getBoundingBox().minY + (double) (entityliving.getHeight() / 3.0F) - entity.locY + d2 * 0.20000000298023224D;
        Vector3fa vector3fa = this.a(new Vec3D(d0, d3, d1), f);

        iprojectile.shoot((double) vector3fa.a(), (double) vector3fa.b(), (double) vector3fa.c(), 1.6F, (float) (14 - this.world.getDifficulty().a() * 4));
        this.a(SoundEffects.ITEM_CROSSBOW_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
    }

    private Vector3fa a(Vec3D vec3d, float f) {
        Vec3D vec3d1 = vec3d.d();
        Vec3D vec3d2 = vec3d1.c(new Vec3D(0.0D, 1.0D, 0.0D));

        if (vec3d2.g() <= 1.0E-7D) {
            vec3d2 = vec3d1.c(this.i(1.0F));
        }

        Quaternion quaternion = new Quaternion(new Vector3fa(vec3d2), 90.0F, true);
        Vector3fa vector3fa = new Vector3fa(vec3d1);

        vector3fa.a(quaternion);
        Quaternion quaternion1 = new Quaternion(vector3fa, f, true);
        Vector3fa vector3fa1 = new Vector3fa(vec3d1);

        vector3fa1.a(quaternion1);
        return vector3fa1;
    }

    public InventorySubcontainer getInventory() {
        return this.inventory;
    }

    @Override
    protected void a(EntityItem entityitem) {
        ItemStack itemstack = entityitem.getItemStack();

        if (itemstack.getItem() instanceof ItemBanner) {
            super.a(entityitem);
        } else {
            Item item = itemstack.getItem();

            if (this.b(item)) {
                ItemStack itemstack1 = this.inventory.a(itemstack);

                if (itemstack1.isEmpty()) {
                    entityitem.die();
                } else {
                    itemstack.setCount(itemstack1.getCount());
                }
            }
        }

    }

    private boolean b(Item item) {
        return this.ek() && item == Items.WHITE_BANNER;
    }

    @Override
    public boolean a_(int i, ItemStack itemstack) {
        if (super.a_(i, itemstack)) {
            return true;
        } else {
            int j = i - 300;

            if (j >= 0 && j < this.inventory.getSize()) {
                this.inventory.setItem(j, itemstack);
                return true;
            } else {
                return false;
            }
        }
    }

    @Override
    public void a(int i, boolean flag) {
        Raid raid = this.ej();
        boolean flag1 = this.random.nextFloat() <= raid.w();

        if (flag1) {
            ItemStack itemstack = new ItemStack(Items.CROSSBOW);
            Map<Enchantment, Integer> map = Maps.newHashMap();

            if (i > raid.a(EnumDifficulty.NORMAL)) {
                map.put(Enchantments.QUICK_CHARGE, 2);
            } else if (i > raid.a(EnumDifficulty.EASY)) {
                map.put(Enchantments.QUICK_CHARGE, 1);
            }

            map.put(Enchantments.MULTISHOT, 1);
            EnchantmentManager.a((Map) map, itemstack);
            this.setSlot(EnumItemSlot.MAINHAND, itemstack);
        }

    }

    @Override
    public boolean I() {
        return super.I() && this.getInventory().isNotEmpty();
    }

    @Override
    public SoundEffect dV() {
        return SoundEffects.ENTITY_PILLAGER_CELEBRATE;
    }

    @Override
    public boolean isTypeNotPersistent(double d0) {
        return super.isTypeNotPersistent(d0) && this.getInventory() != null && this.getInventory().isNotEmpty(); // CraftBukkit - null in constructor
    }
}
