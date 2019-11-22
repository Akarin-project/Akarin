package net.minecraft.server;

import com.mojang.datafixers.DataFixUtils;
import java.util.Collections;
import java.util.Optional;
import java.util.Set; // Paper
import java.util.Map; // Paper
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTypes<T extends Entity> {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final EntityTypes<EntityAreaEffectCloud> AREA_EFFECT_CLOUD = a("area_effect_cloud", EntityTypes.a.a(EntityAreaEffectCloud::new, EnumCreatureType.MISC).c().a(6.0F, 0.5F));
    public static final EntityTypes<EntityArmorStand> ARMOR_STAND = a("armor_stand", EntityTypes.a.a(EntityArmorStand::new, EnumCreatureType.MISC).a(0.5F, 1.975F));
    public static final EntityTypes<EntityTippedArrow> ARROW = a("arrow", EntityTypes.a.a(EntityTippedArrow::new, EnumCreatureType.MISC).a(0.5F, 0.5F));
    public static final EntityTypes<EntityBat> BAT = a("bat", EntityTypes.a.a(EntityBat::new, EnumCreatureType.AMBIENT).a(0.5F, 0.9F));
    public static final EntityTypes<EntityBlaze> BLAZE = a("blaze", EntityTypes.a.a(EntityBlaze::new, EnumCreatureType.MONSTER).c().a(0.6F, 1.8F));
    public static final EntityTypes<EntityBoat> BOAT = a("boat", EntityTypes.a.a(EntityBoat::new, EnumCreatureType.MISC).a(1.375F, 0.5625F));
    public static final EntityTypes<EntityCat> CAT = a("cat", EntityTypes.a.a(EntityCat::new, EnumCreatureType.CREATURE).a(0.6F, 0.7F));
    public static final EntityTypes<EntityCaveSpider> CAVE_SPIDER = a("cave_spider", EntityTypes.a.a(EntityCaveSpider::new, EnumCreatureType.MONSTER).a(0.7F, 0.5F));
    public static final EntityTypes<EntityChicken> CHICKEN = a("chicken", EntityTypes.a.a(EntityChicken::new, EnumCreatureType.CREATURE).a(0.4F, 0.7F));
    public static final EntityTypes<EntityCod> COD = a("cod", EntityTypes.a.a(EntityCod::new, EnumCreatureType.WATER_CREATURE).a(0.5F, 0.3F));
    public static final EntityTypes<EntityCow> COW = a("cow", EntityTypes.a.a(EntityCow::new, EnumCreatureType.CREATURE).a(0.9F, 1.4F));
    public static final EntityTypes<EntityCreeper> CREEPER = a("creeper", EntityTypes.a.a(EntityCreeper::new, EnumCreatureType.MONSTER).a(0.6F, 1.7F));
    public static final EntityTypes<EntityHorseDonkey> DONKEY = a("donkey", EntityTypes.a.a(EntityHorseDonkey::new, EnumCreatureType.CREATURE).a(1.3964844F, 1.5F));
    public static final EntityTypes<EntityDolphin> DOLPHIN = a("dolphin", EntityTypes.a.a(EntityDolphin::new, EnumCreatureType.WATER_CREATURE).a(0.9F, 0.6F));
    public static final EntityTypes<EntityDragonFireball> DRAGON_FIREBALL = a("dragon_fireball", EntityTypes.a.a(EntityDragonFireball::new, EnumCreatureType.MISC).a(1.0F, 1.0F));
    public static final EntityTypes<EntityDrowned> DROWNED = a("drowned", EntityTypes.a.a(EntityDrowned::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityGuardianElder> ELDER_GUARDIAN = a("elder_guardian", EntityTypes.a.a(EntityGuardianElder::new, EnumCreatureType.MONSTER).a(1.9975F, 1.9975F));
    public static final EntityTypes<EntityEnderCrystal> END_CRYSTAL = a("end_crystal", EntityTypes.a.a(EntityEnderCrystal::new, EnumCreatureType.MISC).a(2.0F, 2.0F));
    public static final EntityTypes<EntityEnderDragon> ENDER_DRAGON = a("ender_dragon", EntityTypes.a.a(EntityEnderDragon::new, EnumCreatureType.MONSTER).c().a(16.0F, 8.0F));
    public static final EntityTypes<EntityEnderman> ENDERMAN = a("enderman", EntityTypes.a.a(EntityEnderman::new, EnumCreatureType.MONSTER).a(0.6F, 2.9F));
    public static final EntityTypes<EntityEndermite> ENDERMITE = a("endermite", EntityTypes.a.a(EntityEndermite::new, EnumCreatureType.MONSTER).a(0.4F, 0.3F));
    public static final EntityTypes<EntityEvokerFangs> EVOKER_FANGS = a("evoker_fangs", EntityTypes.a.a(EntityEvokerFangs::new, EnumCreatureType.MISC).a(0.5F, 0.8F));
    public static final EntityTypes<EntityEvoker> EVOKER = a("evoker", EntityTypes.a.a(EntityEvoker::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityExperienceOrb> EXPERIENCE_ORB = a("experience_orb", EntityTypes.a.a(EntityExperienceOrb::new, EnumCreatureType.MISC).a(0.5F, 0.5F));
    public static final EntityTypes<EntityEnderSignal> EYE_OF_ENDER = a("eye_of_ender", EntityTypes.a.a(EntityEnderSignal::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityFallingBlock> FALLING_BLOCK = a("falling_block", EntityTypes.a.a(EntityFallingBlock::new, EnumCreatureType.MISC).a(0.98F, 0.98F));
    public static final EntityTypes<EntityFireworks> FIREWORK_ROCKET = a("firework_rocket", EntityTypes.a.a(EntityFireworks::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityFox> FOX = a("fox", EntityTypes.a.a(EntityFox::new, EnumCreatureType.CREATURE).a(0.6F, 0.7F));
    public static final EntityTypes<EntityGhast> GHAST = a("ghast", EntityTypes.a.a(EntityGhast::new, EnumCreatureType.MONSTER).c().a(4.0F, 4.0F));
    public static final EntityTypes<EntityGiantZombie> GIANT = a("giant", EntityTypes.a.a(EntityGiantZombie::new, EnumCreatureType.MONSTER).a(3.6F, 12.0F));
    public static final EntityTypes<EntityGuardian> GUARDIAN = a("guardian", EntityTypes.a.a(EntityGuardian::new, EnumCreatureType.MONSTER).a(0.85F, 0.85F));
    public static final EntityTypes<EntityHorse> HORSE = a("horse", EntityTypes.a.a(EntityHorse::new, EnumCreatureType.CREATURE).a(1.3964844F, 1.6F));
    public static final EntityTypes<EntityZombieHusk> HUSK = a("husk", EntityTypes.a.a(EntityZombieHusk::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityIllagerIllusioner> ILLUSIONER = a("illusioner", EntityTypes.a.a(EntityIllagerIllusioner::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityItem> ITEM = a("item", EntityTypes.a.a(EntityItem::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityItemFrame> ITEM_FRAME = a("item_frame", EntityTypes.a.a(EntityItemFrame::new, EnumCreatureType.MISC).a(0.5F, 0.5F));
    public static final EntityTypes<EntityLargeFireball> FIREBALL = a("fireball", EntityTypes.a.a(EntityLargeFireball::new, EnumCreatureType.MISC).a(1.0F, 1.0F));
    public static final EntityTypes<EntityLeash> LEASH_KNOT = a("leash_knot", EntityTypes.a.a(EntityLeash::new, EnumCreatureType.MISC).b().a(0.5F, 0.5F));
    public static final EntityTypes<EntityLlama> LLAMA = a("llama", EntityTypes.a.a(EntityLlama::new, EnumCreatureType.CREATURE).a(0.9F, 1.87F));
    public static final EntityTypes<EntityLlamaSpit> LLAMA_SPIT = a("llama_spit", EntityTypes.a.a(EntityLlamaSpit::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityMagmaCube> MAGMA_CUBE = a("magma_cube", EntityTypes.a.a(EntityMagmaCube::new, EnumCreatureType.MONSTER).c().a(2.04F, 2.04F));
    public static final EntityTypes<EntityMinecartRideable> MINECART = a("minecart", EntityTypes.a.a(EntityMinecartRideable::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityMinecartChest> CHEST_MINECART = a("chest_minecart", EntityTypes.a.a(EntityMinecartChest::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityMinecartCommandBlock> COMMAND_BLOCK_MINECART = a("command_block_minecart", EntityTypes.a.a(EntityMinecartCommandBlock::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityMinecartFurnace> FURNACE_MINECART = a("furnace_minecart", EntityTypes.a.a(EntityMinecartFurnace::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityMinecartHopper> HOPPER_MINECART = a("hopper_minecart", EntityTypes.a.a(EntityMinecartHopper::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityMinecartMobSpawner> SPAWNER_MINECART = a("spawner_minecart", EntityTypes.a.a(EntityMinecartMobSpawner::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityMinecartTNT> TNT_MINECART = a("tnt_minecart", EntityTypes.a.a(EntityMinecartTNT::new, EnumCreatureType.MISC).a(0.98F, 0.7F));
    public static final EntityTypes<EntityHorseMule> MULE = a("mule", EntityTypes.a.a(EntityHorseMule::new, EnumCreatureType.CREATURE).a(1.3964844F, 1.6F));
    public static final EntityTypes<EntityMushroomCow> MOOSHROOM = a("mooshroom", EntityTypes.a.a(EntityMushroomCow::new, EnumCreatureType.CREATURE).a(0.9F, 1.4F));
    public static final EntityTypes<EntityOcelot> OCELOT = a("ocelot", EntityTypes.a.a(EntityOcelot::new, EnumCreatureType.CREATURE).a(0.6F, 0.7F));
    public static final EntityTypes<EntityPainting> PAINTING = a("painting", EntityTypes.a.a(EntityPainting::new, EnumCreatureType.MISC).a(0.5F, 0.5F));
    public static final EntityTypes<EntityPanda> PANDA = a("panda", EntityTypes.a.a(EntityPanda::new, EnumCreatureType.CREATURE).a(1.3F, 1.25F));
    public static final EntityTypes<EntityParrot> PARROT = a("parrot", EntityTypes.a.a(EntityParrot::new, EnumCreatureType.CREATURE).a(0.5F, 0.9F));
    public static final EntityTypes<EntityPig> PIG = a("pig", EntityTypes.a.a(EntityPig::new, EnumCreatureType.CREATURE).a(0.9F, 0.9F));
    public static final EntityTypes<EntityPufferFish> PUFFERFISH = a("pufferfish", EntityTypes.a.a(EntityPufferFish::new, EnumCreatureType.WATER_CREATURE).a(0.7F, 0.7F));
    public static final EntityTypes<EntityPigZombie> ZOMBIE_PIGMAN = a("zombie_pigman", EntityTypes.a.a(EntityPigZombie::new, EnumCreatureType.MONSTER).c().a(0.6F, 1.95F));
    public static final EntityTypes<EntityPolarBear> POLAR_BEAR = a("polar_bear", EntityTypes.a.a(EntityPolarBear::new, EnumCreatureType.CREATURE).a(1.4F, 1.4F));
    public static final EntityTypes<EntityTNTPrimed> TNT = a("tnt", EntityTypes.a.a(EntityTNTPrimed::new, EnumCreatureType.MISC).c().a(0.98F, 0.98F));
    public static final EntityTypes<EntityRabbit> RABBIT = a("rabbit", EntityTypes.a.a(EntityRabbit::new, EnumCreatureType.CREATURE).a(0.4F, 0.5F));
    public static final EntityTypes<EntitySalmon> SALMON = a("salmon", EntityTypes.a.a(EntitySalmon::new, EnumCreatureType.WATER_CREATURE).a(0.7F, 0.4F));
    public static final EntityTypes<EntitySheep> SHEEP = a("sheep", EntityTypes.a.a(EntitySheep::new, EnumCreatureType.CREATURE).a(0.9F, 1.3F));
    public static final EntityTypes<EntityShulker> SHULKER = a("shulker", EntityTypes.a.a(EntityShulker::new, EnumCreatureType.MONSTER).c().d().a(1.0F, 1.0F));
    public static final EntityTypes<EntityShulkerBullet> SHULKER_BULLET = a("shulker_bullet", EntityTypes.a.a(EntityShulkerBullet::new, EnumCreatureType.MISC).a(0.3125F, 0.3125F));
    public static final EntityTypes<EntitySilverfish> SILVERFISH = a("silverfish", EntityTypes.a.a(EntitySilverfish::new, EnumCreatureType.MONSTER).a(0.4F, 0.3F));
    public static final EntityTypes<EntitySkeleton> SKELETON = a("skeleton", EntityTypes.a.a(EntitySkeleton::new, EnumCreatureType.MONSTER).a(0.6F, 1.99F));
    public static final EntityTypes<EntityHorseSkeleton> SKELETON_HORSE = a("skeleton_horse", EntityTypes.a.a(EntityHorseSkeleton::new, EnumCreatureType.CREATURE).a(1.3964844F, 1.6F));
    public static final EntityTypes<EntitySlime> SLIME = a("slime", EntityTypes.a.a(EntitySlime::new, EnumCreatureType.MONSTER).a(2.04F, 2.04F));
    public static final EntityTypes<EntitySmallFireball> SMALL_FIREBALL = a("small_fireball", EntityTypes.a.a(EntitySmallFireball::new, EnumCreatureType.MISC).a(0.3125F, 0.3125F));
    public static final EntityTypes<EntitySnowman> SNOW_GOLEM = a("snow_golem", EntityTypes.a.a(EntitySnowman::new, EnumCreatureType.MISC).a(0.7F, 1.9F));
    public static final EntityTypes<EntitySnowball> SNOWBALL = a("snowball", EntityTypes.a.a(EntitySnowball::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntitySpectralArrow> SPECTRAL_ARROW = a("spectral_arrow", EntityTypes.a.a(EntitySpectralArrow::new, EnumCreatureType.MISC).a(0.5F, 0.5F));
    public static final EntityTypes<EntitySpider> SPIDER = a("spider", EntityTypes.a.a(EntitySpider::new, EnumCreatureType.MONSTER).a(1.4F, 0.9F));
    public static final EntityTypes<EntitySquid> SQUID = a("squid", EntityTypes.a.a(EntitySquid::new, EnumCreatureType.WATER_CREATURE).a(0.8F, 0.8F));
    public static final EntityTypes<EntitySkeletonStray> STRAY = a("stray", EntityTypes.a.a(EntitySkeletonStray::new, EnumCreatureType.MONSTER).a(0.6F, 1.99F));
    public static final EntityTypes<EntityLlamaTrader> TRADER_LLAMA = a("trader_llama", EntityTypes.a.a(EntityLlamaTrader::new, EnumCreatureType.CREATURE).a(0.9F, 1.87F));
    public static final EntityTypes<EntityTropicalFish> TROPICAL_FISH = a("tropical_fish", EntityTypes.a.a(EntityTropicalFish::new, EnumCreatureType.WATER_CREATURE).a(0.5F, 0.4F));
    public static final EntityTypes<EntityTurtle> TURTLE = a("turtle", EntityTypes.a.a(EntityTurtle::new, EnumCreatureType.CREATURE).a(1.2F, 0.4F));
    public static final EntityTypes<EntityEgg> EGG = a("egg", EntityTypes.a.a(EntityEgg::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityEnderPearl> ENDER_PEARL = a("ender_pearl", EntityTypes.a.a(EntityEnderPearl::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityThrownExpBottle> EXPERIENCE_BOTTLE = a("experience_bottle", EntityTypes.a.a(EntityThrownExpBottle::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityPotion> POTION = a("potion", EntityTypes.a.a(EntityPotion::new, EnumCreatureType.MISC).a(0.25F, 0.25F));
    public static final EntityTypes<EntityThrownTrident> TRIDENT = a("trident", EntityTypes.a.a(EntityThrownTrident::new, EnumCreatureType.MISC).a(0.5F, 0.5F));
    public static final EntityTypes<EntityVex> VEX = a("vex", EntityTypes.a.a(EntityVex::new, EnumCreatureType.MONSTER).c().a(0.4F, 0.8F));
    public static final EntityTypes<EntityVillager> VILLAGER = a("villager", EntityTypes.a.a(EntityVillager::new, EnumCreatureType.MISC).a(0.6F, 1.95F));
    public static final EntityTypes<EntityIronGolem> IRON_GOLEM = a("iron_golem", EntityTypes.a.a(EntityIronGolem::new, EnumCreatureType.MISC).a(1.4F, 2.7F));
    public static final EntityTypes<EntityVindicator> VINDICATOR = a("vindicator", EntityTypes.a.a(EntityVindicator::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityPillager> PILLAGER = a("pillager", EntityTypes.a.a(EntityPillager::new, EnumCreatureType.MONSTER).d().a(0.6F, 1.95F));
    public static final EntityTypes<EntityVillagerTrader> WANDERING_TRADER = a("wandering_trader", EntityTypes.a.a(EntityVillagerTrader::new, EnumCreatureType.CREATURE).a(0.6F, 1.95F));
    public static final EntityTypes<EntityWitch> WITCH = a("witch", EntityTypes.a.a(EntityWitch::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityWither> WITHER = a("wither", EntityTypes.a.a(EntityWither::new, EnumCreatureType.MONSTER).c().a(0.9F, 3.5F));
    public static final EntityTypes<EntitySkeletonWither> WITHER_SKELETON = a("wither_skeleton", EntityTypes.a.a(EntitySkeletonWither::new, EnumCreatureType.MONSTER).c().a(0.7F, 2.4F));
    public static final EntityTypes<EntityWitherSkull> WITHER_SKULL = a("wither_skull", EntityTypes.a.a(EntityWitherSkull::new, EnumCreatureType.MISC).a(0.3125F, 0.3125F));
    public static final EntityTypes<EntityWolf> WOLF = a("wolf", EntityTypes.a.a(EntityWolf::new, EnumCreatureType.CREATURE).a(0.6F, 0.85F));
    public static final EntityTypes<EntityZombie> ZOMBIE = a("zombie", EntityTypes.a.a(EntityZombie::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityHorseZombie> ZOMBIE_HORSE = a("zombie_horse", EntityTypes.a.a(EntityHorseZombie::new, EnumCreatureType.CREATURE).a(1.3964844F, 1.6F));
    public static final EntityTypes<EntityZombieVillager> ZOMBIE_VILLAGER = a("zombie_villager", EntityTypes.a.a(EntityZombieVillager::new, EnumCreatureType.MONSTER).a(0.6F, 1.95F));
    public static final EntityTypes<EntityPhantom> PHANTOM = a("phantom", EntityTypes.a.a(EntityPhantom::new, EnumCreatureType.MONSTER).a(0.9F, 0.5F));
    public static final EntityTypes<EntityRavager> RAVAGER = a("ravager", EntityTypes.a.a(EntityRavager::new, EnumCreatureType.MONSTER).a(1.95F, 2.2F));
    public static final EntityTypes<EntityLightning> LIGHTNING_BOLT = a("lightning_bolt", EntityTypes.a.a(EnumCreatureType.MISC).b().a(0.0F, 0.0F));
    public static final EntityTypes<EntityHuman> PLAYER = a("player", EntityTypes.a.a(EnumCreatureType.MISC).b().a().a(0.6F, 1.8F));
    public static final EntityTypes<EntityFishingHook> FISHING_BOBBER = a("fishing_bobber", EntityTypes.a.a(EnumCreatureType.MISC).b().a().a(0.25F, 0.25F));
    private final EntityTypes.b<T> aZ;
    private final EnumCreatureType ba;
    private final boolean bb;
    private final boolean bc;
    private final boolean bd;
    private final boolean be;
    @Nullable
    private String bf;
    @Nullable
    private IChatBaseComponent bg;
    @Nullable
    private MinecraftKey bh;
    private final EntitySize bi;

    private static <T extends Entity> EntityTypes<T> a(String s, EntityTypes.a entitytypes_a) { // CraftBukkit - decompile error
        return (EntityTypes) IRegistry.a((IRegistry) IRegistry.ENTITY_TYPE, s, (Object) entitytypes_a.a(s));
    }

    public static MinecraftKey getName(EntityTypes<?> entitytypes) {
        return IRegistry.ENTITY_TYPE.getKey(entitytypes);
    }

    public static Optional<EntityTypes<?>> a(String s) {
        return IRegistry.ENTITY_TYPE.getOptional(MinecraftKey.a(s));
    }

    public EntityTypes(EntityTypes.b<T> entitytypes_b, EnumCreatureType enumcreaturetype, boolean flag, boolean flag1, boolean flag2, boolean flag3, EntitySize entitysize) {
        this.aZ = entitytypes_b;
        this.ba = enumcreaturetype;
        this.be = flag3;
        this.bb = flag;
        this.bc = flag1;
        this.bd = flag2;
        this.bi = entitysize;
    }

    @Nullable
    public Entity spawnCreature(World world, @Nullable ItemStack itemstack, @Nullable EntityHuman entityhuman, BlockPosition blockposition, EnumMobSpawn enummobspawn, boolean flag, boolean flag1) {
        return this.spawnCreature(world, itemstack == null ? null : itemstack.getTag(), itemstack != null && itemstack.hasName() ? itemstack.getName() : null, entityhuman, blockposition, enummobspawn, flag, flag1);
    }

    @Nullable
    public T spawnCreature(World world, @Nullable NBTTagCompound nbttagcompound, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable EntityHuman entityhuman, BlockPosition blockposition, EnumMobSpawn enummobspawn, boolean flag, boolean flag1) {
        // CraftBukkit start
        return this.spawnCreature(world, nbttagcompound, ichatbasecomponent, entityhuman, blockposition, enummobspawn, flag, flag1, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
    }

    @Nullable
    public T spawnCreature(World world, @Nullable NBTTagCompound nbttagcompound, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable EntityHuman entityhuman, BlockPosition blockposition, EnumMobSpawn enummobspawn, boolean flag, boolean flag1, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason spawnReason) {
        T t0 = this.b(world, nbttagcompound, ichatbasecomponent, entityhuman, blockposition, enummobspawn, flag, flag1);

        return world.addEntity(t0, spawnReason) ? t0 : null; // Don't return an entity when CreatureSpawnEvent is canceled
        // CraftBukkit end
    }

    @Nullable
    public T b(World world, @Nullable NBTTagCompound nbttagcompound, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable EntityHuman entityhuman, BlockPosition blockposition, EnumMobSpawn enummobspawn, boolean flag, boolean flag1) {
        T t0 = this.a(world);

        if (t0 == null) {
            return null;
        } else {
            double d0;

            if (flag) {
                t0.setPosition((double) blockposition.getX() + 0.5D, (double) (blockposition.getY() + 1), (double) blockposition.getZ() + 0.5D);
                d0 = a(world, blockposition, flag1, t0.getBoundingBox());
            } else {
                d0 = 0.0D;
            }

            t0.setPositionRotation((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + d0, (double) blockposition.getZ() + 0.5D, MathHelper.g(world.random.nextFloat() * 360.0F), 0.0F);
            if (t0 instanceof EntityInsentient) {
                EntityInsentient entityinsentient = (EntityInsentient) t0;

                entityinsentient.aM = entityinsentient.yaw;
                entityinsentient.aK = entityinsentient.yaw;
                entityinsentient.prepare(world, world.getDamageScaler(new BlockPosition(entityinsentient)), enummobspawn, (GroupDataEntity) null, nbttagcompound);
                entityinsentient.B();
            }

            if (ichatbasecomponent != null && t0 instanceof EntityLiving) {
                t0.setCustomName(ichatbasecomponent);
            }

            a(world, entityhuman, t0, nbttagcompound);
            return t0;
        }
    }

    protected static double a(IWorldReader iworldreader, BlockPosition blockposition, boolean flag, AxisAlignedBB axisalignedbb) {
        AxisAlignedBB axisalignedbb1 = new AxisAlignedBB(blockposition);

        if (flag) {
            axisalignedbb1 = axisalignedbb1.b(0.0D, -1.0D, 0.0D);
        }

        Stream<VoxelShape> stream = iworldreader.c((Entity) null, axisalignedbb1, Collections.emptySet());

        return 1.0D + VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb, stream, flag ? -2.0D : -1.0D);
    }

    public static void a(World world, @Nullable EntityHuman entityhuman, @Nullable Entity entity, @Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("EntityTag", 10)) {
            MinecraftServer minecraftserver = world.getMinecraftServer();

            if (minecraftserver != null && entity != null) {
                if (world.isClientSide || !entity.bT() || entityhuman != null && minecraftserver.getPlayerList().isOp(entityhuman.getProfile())) {
                    NBTTagCompound nbttagcompound1 = entity.save(new NBTTagCompound());
                    UUID uuid = entity.getUniqueID();

                    nbttagcompound1.a(nbttagcompound.getCompound("EntityTag"));
                    entity.a(uuid);
                    entity.f(nbttagcompound1);
                }
            }
        }
    }

    public boolean isPersistable() { return a(); } // Paper - OBFHELPER
    public boolean a() {
        return this.bb;
    }

    public boolean b() {
        return this.bc;
    }

    public boolean c() {
        return this.bd;
    }

    public boolean d() {
        return this.be;
    }

    public EnumCreatureType getEnumCreatureType() { return this.e(); } // Paper - OBFHELPER
    public EnumCreatureType e() {
        return this.ba;
    }

    public String f() {
        if (this.bf == null) {
            this.bf = SystemUtils.a("entity", IRegistry.ENTITY_TYPE.getKey(this));
        }

        return this.bf;
    }

    public IChatBaseComponent g() {
        if (this.bg == null) {
            this.bg = new ChatMessage(this.f(), new Object[0]);
        }

        return this.bg;
    }

    public MinecraftKey h() {
        if (this.bh == null) {
            MinecraftKey minecraftkey = IRegistry.ENTITY_TYPE.getKey(this);

            this.bh = new MinecraftKey(minecraftkey.getNamespace(), "entities/" + minecraftkey.getKey());
        }

        return this.bh;
    }

    public float i() {
        return this.bi.width;
    }

    public float j() {
        return this.bi.height;
    }

    public T create(World world) { return this.a(world); } // Paper - OBFHELPER
    @Nullable public T a(World world) { // Paper - OBFHELPER
        return this.aZ.create(this, world);
    }

    public static Optional<Entity> a(NBTTagCompound nbttagcompound, World world) {
        return SystemUtils.a(a(nbttagcompound).map((entitytypes) -> {
            return entitytypes.a(world);
        }), (entity) -> {
            entity.f(nbttagcompound);
        }, () -> {
            EntityTypes.LOGGER.warn("Skipping Entity with id {}", nbttagcompound.getString("id"));
        });
    }

    public AxisAlignedBB a(double d0, double d1, double d2) {
        float f = this.i() / 2.0F;

        return new AxisAlignedBB(d0 - (double) f, d1, d2 - (double) f, d0 + (double) f, d1 + (double) this.j(), d2 + (double) f);
    }

    public EntitySize k() {
        return this.bi;
    }

    public static Optional<EntityTypes<?>> a(NBTTagCompound nbttagcompound) {
        return IRegistry.ENTITY_TYPE.getOptional(new MinecraftKey(nbttagcompound.getString("id")));
    }

    @Nullable
    public static Entity a(NBTTagCompound nbttagcompound, World world, Function<Entity, Entity> function) {
        return (Entity) b(nbttagcompound, world).map(function).map((entity) -> {
            if (nbttagcompound.hasKeyOfType("Passengers", 9)) {
                NBTTagList nbttaglist = nbttagcompound.getList("Passengers", 10);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    Entity entity1 = a(nbttaglist.getCompound(i), world, function);

                    if (entity1 != null) {
                        entity1.a(entity, true);
                    }
                }
            }

            return entity;
        }).orElse(null); // CraftBukkit - decompile error
    }

    private static Optional<Entity> b(NBTTagCompound nbttagcompound, World world) {
        try {
            return a(nbttagcompound, world);
        } catch (RuntimeException runtimeexception) {
            EntityTypes.LOGGER.warn("Exception loading entity: ", runtimeexception);
            return Optional.empty();
        }
    }

    public int getChunkRange() {
        return this == EntityTypes.PLAYER ? 32 : (this == EntityTypes.END_CRYSTAL ? 16 : (this != EntityTypes.ENDER_DRAGON && this != EntityTypes.TNT && this != EntityTypes.FALLING_BLOCK && this != EntityTypes.ITEM_FRAME && this != EntityTypes.LEASH_KNOT && this != EntityTypes.PAINTING && this != EntityTypes.ARMOR_STAND && this != EntityTypes.EXPERIENCE_ORB && this != EntityTypes.AREA_EFFECT_CLOUD && this != EntityTypes.EVOKER_FANGS ? (this != EntityTypes.FISHING_BOBBER && this != EntityTypes.ARROW && this != EntityTypes.SPECTRAL_ARROW && this != EntityTypes.TRIDENT && this != EntityTypes.SMALL_FIREBALL && this != EntityTypes.DRAGON_FIREBALL && this != EntityTypes.FIREBALL && this != EntityTypes.WITHER_SKULL && this != EntityTypes.SNOWBALL && this != EntityTypes.LLAMA_SPIT && this != EntityTypes.ENDER_PEARL && this != EntityTypes.EYE_OF_ENDER && this != EntityTypes.EGG && this != EntityTypes.POTION && this != EntityTypes.EXPERIENCE_BOTTLE && this != EntityTypes.FIREWORK_ROCKET && this != EntityTypes.ITEM ? 5 : 4) : 10));
    }

    public int getUpdateInterval() {
        // CraftBukkit - SPIGOT-3729: track area effect clouds
        return this != EntityTypes.PLAYER && this != EntityTypes.EVOKER_FANGS ? (this == EntityTypes.EYE_OF_ENDER ? 4 : (this == EntityTypes.FISHING_BOBBER ? 5 : (this != EntityTypes.SMALL_FIREBALL && this != EntityTypes.DRAGON_FIREBALL && this != EntityTypes.FIREBALL && this != EntityTypes.WITHER_SKULL && this != EntityTypes.SNOWBALL && this != EntityTypes.LLAMA_SPIT && this != EntityTypes.ENDER_PEARL && this != EntityTypes.EGG && this != EntityTypes.POTION && this != EntityTypes.EXPERIENCE_BOTTLE && this != EntityTypes.FIREWORK_ROCKET && this != EntityTypes.TNT ? (this != EntityTypes.ARROW && this != EntityTypes.SPECTRAL_ARROW && this != EntityTypes.TRIDENT && this != EntityTypes.ITEM && this != EntityTypes.FALLING_BLOCK && this != EntityTypes.EXPERIENCE_ORB ? (this != EntityTypes.ITEM_FRAME && this != EntityTypes.LEASH_KNOT && this != EntityTypes.PAINTING && this != EntityTypes.END_CRYSTAL ? 3 : Integer.MAX_VALUE) : 20) : 10))) : 2;
    }

    public boolean isDeltaTracking() {
        return this != EntityTypes.PLAYER && this != EntityTypes.LLAMA_SPIT && this != EntityTypes.WITHER && this != EntityTypes.BAT && this != EntityTypes.ITEM_FRAME && this != EntityTypes.LEASH_KNOT && this != EntityTypes.PAINTING && this != EntityTypes.END_CRYSTAL && this != EntityTypes.EVOKER_FANGS;
    }

    public boolean a(Tag<EntityTypes<?>> tag) {
        return tag.isTagged(this);
    }

    public interface b<T extends Entity> {

        T create(EntityTypes<T> entitytypes, World world);
    }

    public static class a<T extends Entity> {

        private final EntityTypes.b<T> a;
        private final EnumCreatureType b;
        private boolean c = true;
        private boolean d = true;
        private boolean e;
        private boolean f;
        private EntitySize g = EntitySize.b(0.6F, 1.8F);

        private a(EntityTypes.b<T> entitytypes_b, EnumCreatureType enumcreaturetype) {
            this.a = entitytypes_b;
            this.b = enumcreaturetype;
            this.f = enumcreaturetype == EnumCreatureType.CREATURE || enumcreaturetype == EnumCreatureType.MISC;
        }

        public static <T extends Entity> EntityTypes.a<T> a(EntityTypes.b entitytypes_b, EnumCreatureType enumcreaturetype) { // CraftBukkit - decompile error
            return new EntityTypes.a<>(entitytypes_b, enumcreaturetype);
        }

        public static <T extends Entity> EntityTypes.a<T> a(EnumCreatureType enumcreaturetype) {
            return new EntityTypes.a<>((entitytypes, world) -> {
                return null;
            }, enumcreaturetype);
        }

        public EntityTypes.a<T> a(float f, float f1) {
            this.g = EntitySize.b(f, f1);
            return this;
        }

        public EntityTypes.a<T> a() {
            this.d = false;
            return this;
        }

        public EntityTypes.a<T> b() {
            this.c = false;
            return this;
        }

        public EntityTypes.a<T> c() {
            this.e = true;
            return this;
        }

        public EntityTypes.a<T> d() {
            this.f = true;
            return this;
        }

        public EntityTypes<T> a(String s) {
            if (this.c) {
                try {
                    DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(SharedConstants.a().getWorldVersion())).getChoiceType(DataConverterTypes.o, s);
                } catch (IllegalStateException illegalstateexception) {
                    if (SharedConstants.b) {
                        throw illegalstateexception;
                    }

                    EntityTypes.LOGGER.warn("No data fixer registered for entity {}", s);
                }
            }

            return new EntityTypes<>(this.a, this.b, this.c, this.d, this.e, this.f, this.g);
        }
    }

    // Paper start
    public static Set<MinecraftKey> getEntityNameList() {
        return IRegistry.ENTITY_TYPE.keySet();
    }
    // Paper end
}
