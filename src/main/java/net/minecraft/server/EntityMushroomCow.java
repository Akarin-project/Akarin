package net.minecraft.server;

import java.util.Random;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;
// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
// CraftBukkit end

public class EntityMushroomCow extends EntityCow {

    private static final DataWatcherObject<String> bz = DataWatcher.a(EntityMushroomCow.class, DataWatcherRegistry.d);
    private MobEffectList bA;
    private int bB;
    private UUID bC;

    public EntityMushroomCow(EntityTypes<? extends EntityMushroomCow> entitytypes, World world) {
        super(entitytypes, world);
    }

    @Override
    public float a(BlockPosition blockposition, IWorldReader iworldreader) {
        return iworldreader.getType(blockposition.down()).getBlock() == Blocks.MYCELIUM ? 10.0F : iworldreader.v(blockposition) - 0.5F;
    }

    public static boolean c(EntityTypes<EntityMushroomCow> entitytypes, GeneratorAccess generatoraccess, EnumMobSpawn enummobspawn, BlockPosition blockposition, Random random) {
        return generatoraccess.getType(blockposition.down()).getBlock() == Blocks.MYCELIUM && generatoraccess.getLightLevel(blockposition, 0) > 8;
    }

    @Override
    public void onLightningStrike(EntityLightning entitylightning) {
        UUID uuid = entitylightning.getUniqueID();

        if (!uuid.equals(this.bC)) {
            this.setVariant(this.getVariant() == EntityMushroomCow.Type.RED ? EntityMushroomCow.Type.BROWN : EntityMushroomCow.Type.RED);
            this.bC = uuid;
            this.a(SoundEffects.ENTITY_MOOSHROOM_CONVERT, 2.0F, 1.0F);
        }

    }

    @Override
    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.register(EntityMushroomCow.bz, EntityMushroomCow.Type.RED.c);
    }

    @Override
    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.BOWL && this.getAge() >= 0 && !entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
            boolean flag = false;
            ItemStack itemstack1;

            if (this.bA != null) {
                flag = true;
                itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
                ItemSuspiciousStew.a(itemstack1, this.bA, this.bB);
                this.bA = null;
                this.bB = 0;
            } else {
                itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
            }

            if (itemstack.isEmpty()) {
                entityhuman.a(enumhand, itemstack1);
            } else if (!entityhuman.inventory.pickup(itemstack1)) {
                entityhuman.drop(itemstack1, false);
            }

            SoundEffect soundeffect;

            if (flag) {
                soundeffect = SoundEffects.ENTITY_MOOSHROOM_SUSPICIOUS_MILK;
            } else {
                soundeffect = SoundEffects.ENTITY_MOOSHROOM_MILK;
            }

            this.a(soundeffect, 1.0F, 1.0F);
            return true;
        } else {
            int i;

            if (itemstack.getItem() == Items.SHEARS && this.getAge() >= 0) {
                // CraftBukkit start
                PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), this.getBukkitEntity());
                this.world.getServer().getPluginManager().callEvent(event);

                if (event.isCancelled()) {
                    return false;
                }
                // CraftBukkit end
                this.world.addParticle(Particles.EXPLOSION, this.locX, this.locY + (double) (this.getHeight() / 2.0F), this.locZ, 0.0D, 0.0D, 0.0D);
                if (!this.world.isClientSide) {
                    // this.die(); // CraftBukkit - moved down
                    EntityCow entitycow = (EntityCow) EntityTypes.COW.a(this.world);

                    entitycow.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
                    entitycow.setHealth(this.getHealth());
                    entitycow.aK = this.aK;
                    if (this.hasCustomName()) {
                        entitycow.setCustomName(this.getCustomName());
                    }

                    // CraftBukkit start
                    if (CraftEventFactory.callEntityTransformEvent(this, entitycow, EntityTransformEvent.TransformReason.SHEARED).isCancelled()) {
                        return false;
                    }
                    if (!new com.destroystokyo.paper.event.entity.EntityTransformedEvent(this.getBukkitEntity(), entitycow.getBukkitEntity(), com.destroystokyo.paper.event.entity.EntityTransformedEvent.TransformedReason.SHEARED).callEvent()) return false; // Paper
                    this.world.addEntity(entitycow, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SHEARED);

                    this.die(); // CraftBukkit - from above
                    // CraftBukkit end

                    for (i = 0; i < 5; ++i) {
                        this.world.addEntity(new EntityItem(this.world, this.locX, this.locY + (double) this.getHeight(), this.locZ, new ItemStack(this.getVariant().d.getBlock())));
                    }

                    itemstack.damage(1, entityhuman, (entityhuman1) -> {
                        entityhuman1.d(enumhand);
                    });
                    this.a(SoundEffects.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
                }

                return true;
            } else {
                if (this.getVariant() == EntityMushroomCow.Type.BROWN && itemstack.getItem().a(TagsItem.SMALL_FLOWERS)) {
                    if (this.bA != null) {
                        for (int j = 0; j < 2; ++j) {
                            this.world.addParticle(Particles.SMOKE, this.locX + (double) (this.random.nextFloat() / 2.0F), this.locY + (double) (this.getHeight() / 2.0F), this.locZ + (double) (this.random.nextFloat() / 2.0F), 0.0D, (double) (this.random.nextFloat() / 5.0F), 0.0D);
                        }
                    } else {
                        Pair<MobEffectList, Integer> pair = this.j(itemstack);

                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        for (i = 0; i < 4; ++i) {
                            this.world.addParticle(Particles.EFFECT, this.locX + (double) (this.random.nextFloat() / 2.0F), this.locY + (double) (this.getHeight() / 2.0F), this.locZ + (double) (this.random.nextFloat() / 2.0F), 0.0D, (double) (this.random.nextFloat() / 5.0F), 0.0D);
                        }

                        this.bA = (MobEffectList) pair.getLeft();
                        this.bB = (Integer) pair.getRight();
                        this.a(SoundEffects.ENTITY_MOOSHROOM_EAT, 2.0F, 1.0F);
                    }
                }

                return super.a(entityhuman, enumhand);
            }
        }
    }

    @Override
    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        nbttagcompound.setString("Type", this.getVariant().c);
        if (this.bA != null) {
            nbttagcompound.setByte("EffectId", (byte) MobEffectList.getId(this.bA));
            nbttagcompound.setInt("EffectDuration", this.bB);
        }

    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.setVariant(EntityMushroomCow.Type.b(nbttagcompound.getString("Type")));
        if (nbttagcompound.hasKeyOfType("EffectId", 1)) {
            this.bA = MobEffectList.fromId(nbttagcompound.getByte("EffectId"));
        }

        if (nbttagcompound.hasKeyOfType("EffectDuration", 3)) {
            this.bB = nbttagcompound.getInt("EffectDuration");
        }

    }

    private Pair<MobEffectList, Integer> j(ItemStack itemstack) {
        BlockFlowers blockflowers = (BlockFlowers) ((ItemBlock) itemstack.getItem()).getBlock();

        return Pair.of(blockflowers.d(), blockflowers.e());
    }

    public void setVariant(EntityMushroomCow.Type entitymushroomcow_type) {
        this.datawatcher.set(EntityMushroomCow.bz, entitymushroomcow_type.c);
    }

    public EntityMushroomCow.Type getVariant() {
        return EntityMushroomCow.Type.b((String) this.datawatcher.get(EntityMushroomCow.bz));
    }

    @Override
    public EntityMushroomCow createChild(EntityAgeable entityageable) {
        EntityMushroomCow entitymushroomcow = (EntityMushroomCow) EntityTypes.MOOSHROOM.a(this.world);

        entitymushroomcow.setVariant(this.a((EntityMushroomCow) entityageable));
        return entitymushroomcow;
    }

    private EntityMushroomCow.Type a(EntityMushroomCow entitymushroomcow) {
        EntityMushroomCow.Type entitymushroomcow_type = this.getVariant();
        EntityMushroomCow.Type entitymushroomcow_type1 = entitymushroomcow.getVariant();
        EntityMushroomCow.Type entitymushroomcow_type2;

        if (entitymushroomcow_type == entitymushroomcow_type1 && this.random.nextInt(1024) == 0) {
            entitymushroomcow_type2 = entitymushroomcow_type == EntityMushroomCow.Type.BROWN ? EntityMushroomCow.Type.RED : EntityMushroomCow.Type.BROWN;
        } else {
            entitymushroomcow_type2 = this.random.nextBoolean() ? entitymushroomcow_type : entitymushroomcow_type1;
        }

        return entitymushroomcow_type2;
    }

    public static enum Type {

        RED("red", Blocks.RED_MUSHROOM.getBlockData()), BROWN("brown", Blocks.BROWN_MUSHROOM.getBlockData());

        private final String c;
        private final IBlockData d;

        private Type(String s, IBlockData iblockdata) {
            this.c = s;
            this.d = iblockdata;
        }

        private static EntityMushroomCow.Type b(String s) {
            EntityMushroomCow.Type[] aentitymushroomcow_type = values();
            int i = aentitymushroomcow_type.length;

            for (int j = 0; j < i; ++j) {
                EntityMushroomCow.Type entitymushroomcow_type = aentitymushroomcow_type[j];

                if (entitymushroomcow_type.c.equals(s)) {
                    return entitymushroomcow_type;
                }
            }

            return EntityMushroomCow.Type.RED;
        }
    }
}
