package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;

import io.akarin.server.core.AkarinGlobalConfig;

/**
 * Akarin Changes Note
 * 1) Restricted spawner modify (feature)
 */
public class ItemMonsterEgg extends Item {

    public ItemMonsterEgg() {
        this.b(CreativeModeTab.f);
    }

    public String b(ItemStack itemstack) {
        String s = ("" + LocaleI18n.get(this.getName() + ".name")).trim();
        String s1 = EntityTypes.a(h(itemstack));

        if (s1 != null) {
            s = s + " " + LocaleI18n.get("entity." + s1 + ".name");
        }

        return s;
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, BlockPosition blockposition, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else if (!entityhuman.a(blockposition.shift(enumdirection), enumdirection, itemstack)) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (block == Blocks.MOB_SPAWNER && (AkarinGlobalConfig.allowSpawnerModify || entityhuman.isCreativeAndOp())) { // Akarin
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityMobSpawner) {
                    MobSpawnerAbstract mobspawnerabstract = ((TileEntityMobSpawner) tileentity).getSpawner();

                    mobspawnerabstract.setMobName(h(itemstack));
                    tileentity.update();
                    world.notify(blockposition, iblockdata, iblockdata, 3);
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    return EnumInteractionResult.SUCCESS;
                }
            }

            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            double d0 = this.a(world, blockposition1);
            Entity entity = a(world, h(itemstack), (double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + d0, (double) blockposition1.getZ() + 0.5D);

            if (entity != null) {
                if (entity instanceof EntityLiving && itemstack.hasName()) {
                    entity.setCustomName(itemstack.getName());
                }

                a(world, entityhuman, itemstack, entity);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }
            }

            return EnumInteractionResult.SUCCESS;
        }
    }

    protected double a(World world, BlockPosition blockposition) {
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockposition)).b(0.0D, -1.0D, 0.0D);
        List list = world.getCubes((Entity) null, axisalignedbb);

        if (list.isEmpty()) {
            return 0.0D;
        } else {
            double d0 = axisalignedbb.b;

            AxisAlignedBB axisalignedbb1;

            for (Iterator iterator = list.iterator(); iterator.hasNext(); d0 = Math.max(axisalignedbb1.e, d0)) {
                axisalignedbb1 = (AxisAlignedBB) iterator.next();
            }

            return d0 - (double) blockposition.getY();
        }
    }

    public static void a(World world, @Nullable EntityHuman entityhuman, ItemStack itemstack, @Nullable Entity entity) {
        MinecraftServer minecraftserver = world.getMinecraftServer();

        if (minecraftserver != null && entity != null) {
            NBTTagCompound nbttagcompound = itemstack.getTag();

            if (nbttagcompound != null && nbttagcompound.hasKeyOfType("EntityTag", 10)) {
                if (!world.isClientSide && entity.bC() && (entityhuman == null || !minecraftserver.getPlayerList().isOp(entityhuman.getProfile()))) {
                    return;
                }

                NBTTagCompound nbttagcompound1 = entity.save(new NBTTagCompound());
                UUID uuid = entity.getUniqueID();

                nbttagcompound1.a(nbttagcompound.getCompound("EntityTag"));
                entity.a(uuid);
                entity.f(nbttagcompound1);
            }

        }
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (world.isClientSide) {
            return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
        } else {
            MovingObjectPosition movingobjectposition = this.a(world, entityhuman, true);

            if (movingobjectposition != null && movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                BlockPosition blockposition = movingobjectposition.a();

                if (!(world.getType(blockposition).getBlock() instanceof BlockFluids)) {
                    return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                } else if (world.a(entityhuman, blockposition) && entityhuman.a(blockposition, movingobjectposition.direction, itemstack)) {
                    Entity entity = a(world, h(itemstack), (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 0.5D, (double) blockposition.getZ() + 0.5D);

                    if (entity == null) {
                        return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                    } else {
                        if (entity instanceof EntityLiving && itemstack.hasName()) {
                            entity.setCustomName(itemstack.getName());
                        }

                        a(world, entityhuman, itemstack, entity);
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        entityhuman.b(StatisticList.b((Item) this));
                        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
                    }
                } else {
                    return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
                }
            } else {
                return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
            }
        }
    }

    @Nullable
    public static Entity a(World world, @Nullable MinecraftKey minecraftkey, double d0, double d1, double d2) {
        return spawnCreature(world, minecraftkey, d0, d1, d2, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SPAWNER_EGG);
    }

    @Nullable
    public static Entity spawnCreature(World world, @Nullable MinecraftKey minecraftkey, double d0, double d1, double d2, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason spawnReason) {
        if (minecraftkey != null && EntityTypes.eggInfo.containsKey(minecraftkey)) {
            Entity entity = null;

            for (int i = 0; i < 1; ++i) {
                entity = EntityTypes.a(minecraftkey, world);
                if (entity instanceof EntityInsentient) {
                    EntityInsentient entityinsentient = (EntityInsentient) entity;

                    entity.setPositionRotation(d0, d1, d2, MathHelper.g(world.random.nextFloat() * 360.0F), 0.0F);
                    entityinsentient.aP = entityinsentient.yaw;
                    entityinsentient.aN = entityinsentient.yaw;
                    entityinsentient.prepare(world.D(new BlockPosition(entityinsentient)), (GroupDataEntity) null);
                    // CraftBukkit start - don't return an entity when CreatureSpawnEvent is canceled
                    if (!world.addEntity(entity, spawnReason)) {
                        entity = null;
                    } else {
                        entityinsentient.D();
                    }
                    // CraftBukkit end
                }
            }

            return entity;
        } else {
            return null;
        }
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            Iterator iterator = EntityTypes.eggInfo.values().iterator();

            while (iterator.hasNext()) {
                EntityTypes.MonsterEggInfo entitytypes_monsteregginfo = (EntityTypes.MonsterEggInfo) iterator.next();
                ItemStack itemstack = new ItemStack(this, 1);

                a(itemstack, entitytypes_monsteregginfo.a);
                nonnulllist.add(itemstack);
            }
        }

    }

    public static void a(ItemStack itemstack, MinecraftKey minecraftkey) {
        NBTTagCompound nbttagcompound = itemstack.hasTag() ? itemstack.getTag() : new NBTTagCompound();
        NBTTagCompound nbttagcompound1 = new NBTTagCompound();

        nbttagcompound1.setString("id", minecraftkey.toString());
        nbttagcompound.set("EntityTag", nbttagcompound1);
        itemstack.setTag(nbttagcompound);
    }

    @Nullable
    public static MinecraftKey h(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound == null) {
            return null;
        } else if (!nbttagcompound.hasKeyOfType("EntityTag", 10)) {
            return null;
        } else {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("EntityTag");

            if (!nbttagcompound1.hasKeyOfType("id", 8)) {
                return null;
            } else {
                String s = nbttagcompound1.getString("id");
                MinecraftKey minecraftkey = new MinecraftKey(s);

                if (!s.contains(":")) {
                    nbttagcompound1.setString("id", minecraftkey.toString());
                }

                return minecraftkey;
            }
        }
    }
}
