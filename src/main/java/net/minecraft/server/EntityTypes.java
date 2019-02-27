package net.minecraft.server;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTypes<T extends Entity> {

    private static final Logger aR = LogManager.getLogger();
    public static final EntityTypes<EntityAreaEffectCloud> AREA_EFFECT_CLOUD = a("area_effect_cloud", EntityTypes.a.a(EntityAreaEffectCloud.class, EntityAreaEffectCloud::new));
    public static final EntityTypes<EntityArmorStand> ARMOR_STAND = a("armor_stand", EntityTypes.a.a(EntityArmorStand.class, EntityArmorStand::new));
    public static final EntityTypes<EntityTippedArrow> ARROW = a("arrow", EntityTypes.a.a(EntityTippedArrow.class, EntityTippedArrow::new));
    public static final EntityTypes<EntityBat> BAT = a("bat", EntityTypes.a.a(EntityBat.class, EntityBat::new));
    public static final EntityTypes<EntityBlaze> BLAZE = a("blaze", EntityTypes.a.a(EntityBlaze.class, EntityBlaze::new));
    public static final EntityTypes<EntityBoat> BOAT = a("boat", EntityTypes.a.a(EntityBoat.class, EntityBoat::new));
    public static final EntityTypes<EntityCaveSpider> CAVE_SPIDER = a("cave_spider", EntityTypes.a.a(EntityCaveSpider.class, EntityCaveSpider::new));
    public static final EntityTypes<EntityChicken> CHICKEN = a("chicken", EntityTypes.a.a(EntityChicken.class, EntityChicken::new));
    public static final EntityTypes<EntityCod> COD = a("cod", EntityTypes.a.a(EntityCod.class, EntityCod::new));
    public static final EntityTypes<EntityCow> COW = a("cow", EntityTypes.a.a(EntityCow.class, EntityCow::new));
    public static final EntityTypes<EntityCreeper> CREEPER = a("creeper", EntityTypes.a.a(EntityCreeper.class, EntityCreeper::new));
    public static final EntityTypes<EntityHorseDonkey> DONKEY = a("donkey", EntityTypes.a.a(EntityHorseDonkey.class, EntityHorseDonkey::new));
    public static final EntityTypes<EntityDolphin> DOLPHIN = a("dolphin", EntityTypes.a.a(EntityDolphin.class, EntityDolphin::new));
    public static final EntityTypes<EntityDragonFireball> DRAGON_FIREBALL = a("dragon_fireball", EntityTypes.a.a(EntityDragonFireball.class, EntityDragonFireball::new));
    public static final EntityTypes<EntityDrowned> DROWNED = a("drowned", EntityTypes.a.a(EntityDrowned.class, EntityDrowned::new));
    public static final EntityTypes<EntityGuardianElder> ELDER_GUARDIAN = a("elder_guardian", EntityTypes.a.a(EntityGuardianElder.class, EntityGuardianElder::new));
    public static final EntityTypes<EntityEnderCrystal> END_CRYSTAL = a("end_crystal", EntityTypes.a.a(EntityEnderCrystal.class, EntityEnderCrystal::new));
    public static final EntityTypes<EntityEnderDragon> ENDER_DRAGON = a("ender_dragon", EntityTypes.a.a(EntityEnderDragon.class, EntityEnderDragon::new));
    public static final EntityTypes<EntityEnderman> ENDERMAN = a("enderman", EntityTypes.a.a(EntityEnderman.class, EntityEnderman::new));
    public static final EntityTypes<EntityEndermite> ENDERMITE = a("endermite", EntityTypes.a.a(EntityEndermite.class, EntityEndermite::new));
    public static final EntityTypes<EntityEvokerFangs> EVOKER_FANGS = a("evoker_fangs", EntityTypes.a.a(EntityEvokerFangs.class, EntityEvokerFangs::new));
    public static final EntityTypes<EntityEvoker> EVOKER = a("evoker", EntityTypes.a.a(EntityEvoker.class, EntityEvoker::new));
    public static final EntityTypes<EntityExperienceOrb> EXPERIENCE_ORB = a("experience_orb", EntityTypes.a.a(EntityExperienceOrb.class, EntityExperienceOrb::new));
    public static final EntityTypes<EntityEnderSignal> EYE_OF_ENDER = a("eye_of_ender", EntityTypes.a.a(EntityEnderSignal.class, EntityEnderSignal::new));
    public static final EntityTypes<EntityFallingBlock> FALLING_BLOCK = a("falling_block", EntityTypes.a.a(EntityFallingBlock.class, EntityFallingBlock::new));
    public static final EntityTypes<EntityFireworks> FIREWORK_ROCKET = a("firework_rocket", EntityTypes.a.a(EntityFireworks.class, EntityFireworks::new));
    public static final EntityTypes<EntityGhast> GHAST = a("ghast", EntityTypes.a.a(EntityGhast.class, EntityGhast::new));
    public static final EntityTypes<EntityGiantZombie> GIANT = a("giant", EntityTypes.a.a(EntityGiantZombie.class, EntityGiantZombie::new));
    public static final EntityTypes<EntityGuardian> GUARDIAN = a("guardian", EntityTypes.a.a(EntityGuardian.class, EntityGuardian::new));
    public static final EntityTypes<EntityHorse> HORSE = a("horse", EntityTypes.a.a(EntityHorse.class, EntityHorse::new));
    public static final EntityTypes<EntityZombieHusk> HUSK = a("husk", EntityTypes.a.a(EntityZombieHusk.class, EntityZombieHusk::new));
    public static final EntityTypes<EntityIllagerIllusioner> ILLUSIONER = a("illusioner", EntityTypes.a.a(EntityIllagerIllusioner.class, EntityIllagerIllusioner::new));
    public static final EntityTypes<EntityItem> ITEM = a("item", EntityTypes.a.a(EntityItem.class, EntityItem::new));
    public static final EntityTypes<EntityItemFrame> ITEM_FRAME = a("item_frame", EntityTypes.a.a(EntityItemFrame.class, EntityItemFrame::new));
    public static final EntityTypes<EntityLargeFireball> FIREBALL = a("fireball", EntityTypes.a.a(EntityLargeFireball.class, EntityLargeFireball::new));
    public static final EntityTypes<EntityLeash> LEASH_KNOT = a("leash_knot", EntityTypes.a.a(EntityLeash.class, EntityLeash::new).b());
    public static final EntityTypes<EntityLlama> LLAMA = a("llama", EntityTypes.a.a(EntityLlama.class, EntityLlama::new));
    public static final EntityTypes<EntityLlamaSpit> LLAMA_SPIT = a("llama_spit", EntityTypes.a.a(EntityLlamaSpit.class, EntityLlamaSpit::new));
    public static final EntityTypes<EntityMagmaCube> MAGMA_CUBE = a("magma_cube", EntityTypes.a.a(EntityMagmaCube.class, EntityMagmaCube::new));
    public static final EntityTypes<EntityMinecartRideable> MINECART = a("minecart", EntityTypes.a.a(EntityMinecartRideable.class, EntityMinecartRideable::new));
    public static final EntityTypes<EntityMinecartChest> CHEST_MINECART = a("chest_minecart", EntityTypes.a.a(EntityMinecartChest.class, EntityMinecartChest::new));
    public static final EntityTypes<EntityMinecartCommandBlock> COMMAND_BLOCK_MINECART = a("command_block_minecart", EntityTypes.a.a(EntityMinecartCommandBlock.class, EntityMinecartCommandBlock::new));
    public static final EntityTypes<EntityMinecartFurnace> FURNACE_MINECART = a("furnace_minecart", EntityTypes.a.a(EntityMinecartFurnace.class, EntityMinecartFurnace::new));
    public static final EntityTypes<EntityMinecartHopper> HOPPER_MINECART = a("hopper_minecart", EntityTypes.a.a(EntityMinecartHopper.class, EntityMinecartHopper::new));
    public static final EntityTypes<EntityMinecartMobSpawner> SPAWNER_MINECART = a("spawner_minecart", EntityTypes.a.a(EntityMinecartMobSpawner.class, EntityMinecartMobSpawner::new));
    public static final EntityTypes<EntityMinecartTNT> TNT_MINECART = a("tnt_minecart", EntityTypes.a.a(EntityMinecartTNT.class, EntityMinecartTNT::new));
    public static final EntityTypes<EntityHorseMule> MULE = a("mule", EntityTypes.a.a(EntityHorseMule.class, EntityHorseMule::new));
    public static final EntityTypes<EntityMushroomCow> MOOSHROOM = a("mooshroom", EntityTypes.a.a(EntityMushroomCow.class, EntityMushroomCow::new));
    public static final EntityTypes<EntityOcelot> OCELOT = a("ocelot", EntityTypes.a.a(EntityOcelot.class, EntityOcelot::new));
    public static final EntityTypes<EntityPainting> PAINTING = a("painting", EntityTypes.a.a(EntityPainting.class, EntityPainting::new));
    public static final EntityTypes<EntityParrot> PARROT = a("parrot", EntityTypes.a.a(EntityParrot.class, EntityParrot::new));
    public static final EntityTypes<EntityPig> PIG = a("pig", EntityTypes.a.a(EntityPig.class, EntityPig::new));
    public static final EntityTypes<EntityPufferFish> PUFFERFISH = a("pufferfish", EntityTypes.a.a(EntityPufferFish.class, EntityPufferFish::new));
    public static final EntityTypes<EntityPigZombie> ZOMBIE_PIGMAN = a("zombie_pigman", EntityTypes.a.a(EntityPigZombie.class, EntityPigZombie::new));
    public static final EntityTypes<EntityPolarBear> POLAR_BEAR = a("polar_bear", EntityTypes.a.a(EntityPolarBear.class, EntityPolarBear::new));
    public static final EntityTypes<EntityTNTPrimed> TNT = a("tnt", EntityTypes.a.a(EntityTNTPrimed.class, EntityTNTPrimed::new));
    public static final EntityTypes<EntityRabbit> RABBIT = a("rabbit", EntityTypes.a.a(EntityRabbit.class, EntityRabbit::new));
    public static final EntityTypes<EntitySalmon> SALMON = a("salmon", EntityTypes.a.a(EntitySalmon.class, EntitySalmon::new));
    public static final EntityTypes<EntitySheep> SHEEP = a("sheep", EntityTypes.a.a(EntitySheep.class, EntitySheep::new));
    public static final EntityTypes<EntityShulker> SHULKER = a("shulker", EntityTypes.a.a(EntityShulker.class, EntityShulker::new));
    public static final EntityTypes<EntityShulkerBullet> SHULKER_BULLET = a("shulker_bullet", EntityTypes.a.a(EntityShulkerBullet.class, EntityShulkerBullet::new));
    public static final EntityTypes<EntitySilverfish> SILVERFISH = a("silverfish", EntityTypes.a.a(EntitySilverfish.class, EntitySilverfish::new));
    public static final EntityTypes<EntitySkeleton> SKELETON = a("skeleton", EntityTypes.a.a(EntitySkeleton.class, EntitySkeleton::new));
    public static final EntityTypes<EntityHorseSkeleton> SKELETON_HORSE = a("skeleton_horse", EntityTypes.a.a(EntityHorseSkeleton.class, EntityHorseSkeleton::new));
    public static final EntityTypes<EntitySlime> SLIME = a("slime", EntityTypes.a.a(EntitySlime.class, EntitySlime::new));
    public static final EntityTypes<EntitySmallFireball> SMALL_FIREBALL = a("small_fireball", EntityTypes.a.a(EntitySmallFireball.class, EntitySmallFireball::new));
    public static final EntityTypes<EntitySnowman> SNOW_GOLEM = a("snow_golem", EntityTypes.a.a(EntitySnowman.class, EntitySnowman::new));
    public static final EntityTypes<EntitySnowball> SNOWBALL = a("snowball", EntityTypes.a.a(EntitySnowball.class, EntitySnowball::new));
    public static final EntityTypes<EntitySpectralArrow> SPECTRAL_ARROW = a("spectral_arrow", EntityTypes.a.a(EntitySpectralArrow.class, EntitySpectralArrow::new));
    public static final EntityTypes<EntitySpider> SPIDER = a("spider", EntityTypes.a.a(EntitySpider.class, EntitySpider::new));
    public static final EntityTypes<EntitySquid> SQUID = a("squid", EntityTypes.a.a(EntitySquid.class, EntitySquid::new));
    public static final EntityTypes<EntitySkeletonStray> STRAY = a("stray", EntityTypes.a.a(EntitySkeletonStray.class, EntitySkeletonStray::new));
    public static final EntityTypes<EntityTropicalFish> TROPICAL_FISH = a("tropical_fish", EntityTypes.a.a(EntityTropicalFish.class, EntityTropicalFish::new));
    public static final EntityTypes<EntityTurtle> TURTLE = a("turtle", EntityTypes.a.a(EntityTurtle.class, EntityTurtle::new));
    public static final EntityTypes<EntityEgg> EGG = a("egg", EntityTypes.a.a(EntityEgg.class, EntityEgg::new));
    public static final EntityTypes<EntityEnderPearl> ENDER_PEARL = a("ender_pearl", EntityTypes.a.a(EntityEnderPearl.class, EntityEnderPearl::new));
    public static final EntityTypes<EntityThrownExpBottle> EXPERIENCE_BOTTLE = a("experience_bottle", EntityTypes.a.a(EntityThrownExpBottle.class, EntityThrownExpBottle::new));
    public static final EntityTypes<EntityPotion> POTION = a("potion", EntityTypes.a.a(EntityPotion.class, EntityPotion::new));
    public static final EntityTypes<EntityVex> VEX = a("vex", EntityTypes.a.a(EntityVex.class, EntityVex::new));
    public static final EntityTypes<EntityVillager> VILLAGER = a("villager", EntityTypes.a.a(EntityVillager.class, EntityVillager::new));
    public static final EntityTypes<EntityIronGolem> IRON_GOLEM = a("iron_golem", EntityTypes.a.a(EntityIronGolem.class, EntityIronGolem::new));
    public static final EntityTypes<EntityVindicator> VINDICATOR = a("vindicator", EntityTypes.a.a(EntityVindicator.class, EntityVindicator::new));
    public static final EntityTypes<EntityWitch> WITCH = a("witch", EntityTypes.a.a(EntityWitch.class, EntityWitch::new));
    public static final EntityTypes<EntityWither> WITHER = a("wither", EntityTypes.a.a(EntityWither.class, EntityWither::new));
    public static final EntityTypes<EntitySkeletonWither> WITHER_SKELETON = a("wither_skeleton", EntityTypes.a.a(EntitySkeletonWither.class, EntitySkeletonWither::new));
    public static final EntityTypes<EntityWitherSkull> WITHER_SKULL = a("wither_skull", EntityTypes.a.a(EntityWitherSkull.class, EntityWitherSkull::new));
    public static final EntityTypes<EntityWolf> WOLF = a("wolf", EntityTypes.a.a(EntityWolf.class, EntityWolf::new));
    public static final EntityTypes<EntityZombie> ZOMBIE = a("zombie", EntityTypes.a.a(EntityZombie.class, EntityZombie::new));
    public static final EntityTypes<EntityHorseZombie> ZOMBIE_HORSE = a("zombie_horse", EntityTypes.a.a(EntityHorseZombie.class, EntityHorseZombie::new));
    public static final EntityTypes<EntityZombieVillager> ZOMBIE_VILLAGER = a("zombie_villager", EntityTypes.a.a(EntityZombieVillager.class, EntityZombieVillager::new));
    public static final EntityTypes<EntityPhantom> PHANTOM = a("phantom", EntityTypes.a.a(EntityPhantom.class, EntityPhantom::new));
    public static final EntityTypes<EntityLightning> LIGHTNING_BOLT = a("lightning_bolt", EntityTypes.a.a(EntityLightning.class).b());
    public static final EntityTypes<EntityHuman> PLAYER = a("player", EntityTypes.a.a(EntityHuman.class).b().a());
    public static final EntityTypes<EntityFishingHook> FISHING_BOBBER = a("fishing_bobber", EntityTypes.a.a(EntityFishingHook.class).b().a());
    public static final EntityTypes<EntityThrownTrident> TRIDENT = a("trident", EntityTypes.a.a(EntityThrownTrident.class, EntityThrownTrident::new));
    private final Class<? extends T> aS;
    private final Function<? super World, ? extends T> aT;
    private final boolean aU;
    private final boolean aV;
    @Nullable
    private String aW;
    @Nullable
    private IChatBaseComponent aX;
    @Nullable
    private final Type<?> aY;

    public static <T extends Entity> EntityTypes<T> a(String s, EntityTypes.a<T> entitytypes_a) {
        EntityTypes<T> entitytypes = entitytypes_a.a(s);

        IRegistry.ENTITY_TYPE.a(new MinecraftKey(s), entitytypes); // CraftBukkit - decompile error
        return entitytypes;
    }

    @Nullable
    public static MinecraftKey getName(EntityTypes<?> entitytypes) {
        return IRegistry.ENTITY_TYPE.getKey(entitytypes);
    }

    @Nullable
    public static EntityTypes<?> a(String s) {
        return (EntityTypes) IRegistry.ENTITY_TYPE.get(MinecraftKey.a(s));
    }

    public EntityTypes(Class<? extends T> oclass, Function<? super World, ? extends T> function, boolean flag, boolean flag1, @Nullable Type<?> type) {
        this.aS = oclass;
        this.aT = function;
        this.aU = flag;
        this.aV = flag1;
        this.aY = type;
    }

    @Nullable
    public Entity a(World world, @Nullable ItemStack itemstack, @Nullable EntityHuman entityhuman, BlockPosition blockposition, boolean flag, boolean flag1) {
        return this.a(world, itemstack == null ? null : itemstack.getTag(), itemstack != null && itemstack.hasName() ? itemstack.getName() : null, entityhuman, blockposition, flag, flag1);
    }

    @Nullable
    public T a(World world, @Nullable NBTTagCompound nbttagcompound, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable EntityHuman entityhuman, BlockPosition blockposition, boolean flag, boolean flag1) {
        // CraftBukkit start
        return spawnCreature(world, nbttagcompound, ichatbasecomponent, entityhuman, blockposition, flag, flag1, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
    }

    @Nullable
    public T spawnCreature(World world, @Nullable NBTTagCompound nbttagcompound, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable EntityHuman entityhuman, BlockPosition blockposition, boolean flag, boolean flag1, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason spawnReason) {
        T t0 = this.b(world, nbttagcompound, ichatbasecomponent, entityhuman, blockposition, flag, flag1);

        return world.addEntity(t0, spawnReason) ? t0 : null; // Don't return an entity when CreatureSpawnEvent is canceled
        // CraftBukkit end
    }

    @Nullable
    public T b(World world, @Nullable NBTTagCompound nbttagcompound, @Nullable IChatBaseComponent ichatbasecomponent, @Nullable EntityHuman entityhuman, BlockPosition blockposition, boolean flag, boolean flag1) {
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

                entityinsentient.aS = entityinsentient.yaw;
                entityinsentient.aQ = entityinsentient.yaw;
                entityinsentient.prepare(world.getDamageScaler(new BlockPosition(entityinsentient)), (GroupDataEntity) null, nbttagcompound);
                entityinsentient.A();
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

        Stream<VoxelShape> stream = iworldreader.b((Entity) null, axisalignedbb1);

        return 1.0D + VoxelShapes.a(EnumDirection.EnumAxis.Y, axisalignedbb, stream, flag ? -2.0D : -1.0D);
    }

    public static void a(World world, @Nullable EntityHuman entityhuman, @Nullable Entity entity, @Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("EntityTag", 10)) {
            MinecraftServer minecraftserver = world.getMinecraftServer();

            if (minecraftserver != null && entity != null) {
                if (world.isClientSide || !entity.bM() || entityhuman != null && minecraftserver.getPlayerList().isOp(entityhuman.getProfile())) {
                    NBTTagCompound nbttagcompound1 = entity.save(new NBTTagCompound());
                    UUID uuid = entity.getUniqueID();

                    nbttagcompound1.a(nbttagcompound.getCompound("EntityTag"));
                    entity.a(uuid);
                    entity.f(nbttagcompound1);
                }
            }
        }
    }

    public boolean a() {
        return this.aU;
    }

    public boolean b() {
        return this.aV;
    }

    public Class<? extends T> c() {
        return this.aS;
    }

    public String d() {
        if (this.aW == null) {
            this.aW = SystemUtils.a("entity", IRegistry.ENTITY_TYPE.getKey(this));
        }

        return this.aW;
    }

    public IChatBaseComponent e() {
        if (this.aX == null) {
            this.aX = new ChatMessage(this.d(), new Object[0]);
        }

        return this.aX;
    }

    @Nullable
    public T a(World world) {
        return this.aT.apply(world); // CraftBukkit - decompile error
    }

    @Nullable
    public static Entity a(World world, MinecraftKey minecraftkey) {
        return a(world, (EntityTypes) IRegistry.ENTITY_TYPE.get(minecraftkey));
    }

    @Nullable
    public static Entity a(NBTTagCompound nbttagcompound, World world) {
        MinecraftKey minecraftkey = new MinecraftKey(nbttagcompound.getString("id"));
        Entity entity = a(world, minecraftkey);

        if (entity == null) {
            EntityTypes.aR.warn("Skipping Entity with id {}", minecraftkey);
        } else {
            entity.f(nbttagcompound);
        }

        return entity;
    }

    @Nullable
    private static Entity a(World world, @Nullable EntityTypes<?> entitytypes) {
        return entitytypes == null ? null : entitytypes.a(world);
    }

    public static class a<T extends Entity> {

        private final Class<? extends T> a;
        private final Function<? super World, ? extends T> b;
        private boolean c = true;
        private boolean d = true;

        private a(Class<? extends T> oclass, Function<? super World, ? extends T> function) {
            this.a = oclass;
            this.b = function;
        }

        public static <T extends Entity> EntityTypes.a<T> a(Class<? extends T> oclass, Function<? super World, ? extends T> function) {
            return new EntityTypes.a<>(oclass, function);
        }

        public static <T extends Entity> EntityTypes.a<T> a(Class<? extends T> oclass) {
            return new EntityTypes.a<>(oclass, (world) -> {
                return null;
            });
        }

        public EntityTypes.a<T> a() {
            this.d = false;
            return this;
        }

        public EntityTypes.a<T> b() {
            this.c = false;
            return this;
        }

        public EntityTypes<T> a(String s) {
            Type<?> type = null;

            if (this.c) {
                try {
                    type = DataConverterRegistry.a().getSchema(DataFixUtils.makeKey(1631)).getChoiceType(DataConverterTypes.n, s);
                } catch (IllegalStateException illegalstateexception) {
                    if (SharedConstants.b) {
                        throw illegalstateexception;
                    }

                    EntityTypes.aR.warn("No data fixer registered for entity {}", s);
                }
            }

            return new EntityTypes<>(this.a, this.b, this.c, this.d, type);
        }
    }
}
