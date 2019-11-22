package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class ItemBoat extends Item {

    private static final Predicate<Entity> a = IEntitySelector.f.and(Entity::isInteractable);
    private final EntityBoat.EnumBoatType b;

    public ItemBoat(EntityBoat.EnumBoatType entityboat_enumboattype, Item.Info item_info) {
        super(item_info);
        this.b = entityboat_enumboattype;
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPosition movingobjectposition = a(world, entityhuman, RayTrace.FluidCollisionOption.ANY);

        if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.MISS) {
            return new InteractionResultWrapper<>(EnumInteractionResult.PASS, itemstack);
        } else {
            Vec3D vec3d = entityhuman.f(1.0F);
            double d0 = 5.0D;
            List<Entity> list = world.getEntities(entityhuman, entityhuman.getBoundingBox().a(vec3d.a(5.0D)).g(1.0D), ItemBoat.a);

            if (!list.isEmpty()) {
                Vec3D vec3d1 = entityhuman.j(1.0F);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();
                    AxisAlignedBB axisalignedbb = entity.getBoundingBox().g((double) entity.aS());

                    if (axisalignedbb.c(vec3d1)) {
                        return new InteractionResultWrapper<>(EnumInteractionResult.PASS, itemstack);
                    }
                }
            }

            if (movingobjectposition.getType() == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
                // CraftBukkit start - Boat placement
                MovingObjectPositionBlock movingobjectpositionblock = (MovingObjectPositionBlock) movingobjectposition;
                org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent(entityhuman, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, movingobjectpositionblock.getBlockPosition(), movingobjectpositionblock.getDirection(), itemstack, enumhand);

                if (event.isCancelled()) {
                    return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack);
                }
                // CraftBukkit end
                EntityBoat entityboat = new EntityBoat(world, movingobjectposition.getPos().x, movingobjectposition.getPos().y, movingobjectposition.getPos().z);

                entityboat.setType(this.b);
                entityboat.yaw = entityhuman.yaw;
                if (!world.getCubes(entityboat, entityboat.getBoundingBox().g(-0.1D))) {
                    return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
                } else {
                    if (!world.isClientSide) {
                        if (!world.addEntity(entityboat)) return new InteractionResultWrapper(EnumInteractionResult.PASS, itemstack); // CraftBukkit
                    }

                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(this));
                    return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
                }
            } else {
                return new InteractionResultWrapper<>(EnumInteractionResult.PASS, itemstack);
            }
        }
    }
}
