package net.minecraft.server;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import io.akarin.server.core.AkarinGlobalConfig;

import java.util.Map;
import java.util.Objects;
import javax.annotation.Nullable;

/**
 * Akarin Changes Note
 * 1) Restricted spawner modify (feature)
 */
public class ItemMonsterEgg extends Item {

    private static final Map<EntityTypes<?>, ItemMonsterEgg> a = Maps.newIdentityHashMap();
    private final int b;
    private final int c;
    private final EntityTypes<?> d;

    public ItemMonsterEgg(EntityTypes<?> entitytypes, int i, int j, Item.Info item_info) {
        super(item_info);
        this.d = entitytypes;
        this.b = i;
        this.c = j;
        ItemMonsterEgg.a.put(entitytypes, this);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();

        if (world.isClientSide) {
            return EnumInteractionResult.SUCCESS;
        } else {
            ItemStack itemstack = itemactioncontext.getItemStack();
            BlockPosition blockposition = itemactioncontext.getClickPosition();
            EnumDirection enumdirection = itemactioncontext.getClickedFace();
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (block == Blocks.SPAWNER && (AkarinGlobalConfig.allowSpawnerModify || itemactioncontext.getEntity().isCreativeAndOp())) { // Akarin
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity instanceof TileEntityMobSpawner) {
                    MobSpawnerAbstract mobspawnerabstract = ((TileEntityMobSpawner) tileentity).getSpawner();
                    EntityTypes entitytypes = this.b(itemstack.getTag());

                    if (entitytypes != null) {
                        mobspawnerabstract.setMobName(entitytypes);
                        tileentity.update();
                        world.notify(blockposition, iblockdata, iblockdata, 3);
                    }

                    itemstack.subtract(1);
                    return EnumInteractionResult.SUCCESS;
                }
            }

            BlockPosition blockposition1;

            if (iblockdata.h(world, blockposition).b()) {
                blockposition1 = blockposition;
            } else {
                blockposition1 = blockposition.shift(enumdirection);
            }

            EntityTypes entitytypes1 = this.b(itemstack.getTag());

            if (entitytypes1 == null || entitytypes1.a(world, itemstack, itemactioncontext.getEntity(), blockposition1, true, !Objects.equals(blockposition, blockposition1) && enumdirection == EnumDirection.UP) != null) {
                itemstack.subtract(1);
            }

            return EnumInteractionResult.SUCCESS;
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
                    EntityTypes entitytypes = this.b(itemstack.getTag());

                    if (entitytypes != null && entitytypes.a(world, itemstack, entityhuman, blockposition, false, false) != null) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack.subtract(1);
                        }

                        entityhuman.b(StatisticList.ITEM_USED.b(this));
                        return new InteractionResultWrapper(EnumInteractionResult.SUCCESS, itemstack);
                    } else {
                        return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                    }
                } else {
                    return new InteractionResultWrapper(EnumInteractionResult.FAIL, itemstack);
                }
            } else {
                return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
            }
        }
    }

    public boolean a(@Nullable NBTTagCompound nbttagcompound, EntityTypes<?> entitytypes) {
        return Objects.equals(this.b(nbttagcompound), entitytypes);
    }

    public static Iterable<ItemMonsterEgg> d() {
        return Iterables.unmodifiableIterable(ItemMonsterEgg.a.values());
    }

    @Nullable
    public EntityTypes<?> b(@Nullable NBTTagCompound nbttagcompound) {
        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("EntityTag", 10)) {
            NBTTagCompound nbttagcompound1 = nbttagcompound.getCompound("EntityTag");

            if (nbttagcompound1.hasKeyOfType("id", 8)) {
                return EntityTypes.a(nbttagcompound1.getString("id"));
            }
        }

        return this.d;
    }
}
