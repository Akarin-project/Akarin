package net.minecraft.server;

import java.util.List;
import java.util.Random;

public class ItemArmorStand extends Item {

    public ItemArmorStand(Item.Info item_info) {
        super(item_info);
    }

    @Override
    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        EnumDirection enumdirection = itemactioncontext.getClickedFace();

        if (enumdirection == EnumDirection.DOWN) {
            return EnumInteractionResult.FAIL;
        } else {
            World world = itemactioncontext.getWorld();
            BlockActionContext blockactioncontext = new BlockActionContext(itemactioncontext);
            BlockPosition blockposition = blockactioncontext.getClickPosition();
            BlockPosition blockposition1 = blockposition.up();

            if (blockactioncontext.b() && world.getType(blockposition1).a(blockactioncontext)) {
                double d0 = (double) blockposition.getX();
                double d1 = (double) blockposition.getY();
                double d2 = (double) blockposition.getZ();
                List<Entity> list = world.getEntities((Entity) null, new AxisAlignedBB(d0, d1, d2, d0 + 1.0D, d1 + 2.0D, d2 + 1.0D));

                if (!list.isEmpty()) {
                    return EnumInteractionResult.FAIL;
                } else {
                    ItemStack itemstack = itemactioncontext.getItemStack();

                    if (!world.isClientSide) {
                        world.a(blockposition, false);
                        world.a(blockposition1, false);
                        EntityArmorStand entityarmorstand = new EntityArmorStand(world, d0 + 0.5D, d1, d2 + 0.5D);
                        float f = (float) MathHelper.d((MathHelper.g(itemactioncontext.h() - 180.0F) + 22.5F) / 45.0F) * 45.0F;

                        entityarmorstand.setPositionRotation(d0 + 0.5D, d1, d2 + 0.5D, f, 0.0F);
                        this.a(entityarmorstand, world.random);
                        EntityTypes.a(world, itemactioncontext.getEntity(), (Entity) entityarmorstand, itemstack.getTag());
                        // CraftBukkit start
                        if (org.bukkit.craftbukkit.event.CraftEventFactory.callEntityPlaceEvent(itemactioncontext, entityarmorstand).isCancelled()) {
                            return EnumInteractionResult.FAIL;
                        }
                        // CraftBukkit end
                        world.addEntity(entityarmorstand);
                        world.playSound((EntityHuman) null, entityarmorstand.locX, entityarmorstand.locY, entityarmorstand.locZ, SoundEffects.ENTITY_ARMOR_STAND_PLACE, SoundCategory.BLOCKS, 0.75F, 0.8F);
                    }

                    itemstack.subtract(1);
                    return EnumInteractionResult.SUCCESS;
                }
            } else {
                return EnumInteractionResult.FAIL;
            }
        }
    }

    private void a(EntityArmorStand entityarmorstand, Random random) {
        Vector3f vector3f = entityarmorstand.r();
        float f = random.nextFloat() * 5.0F;
        float f1 = random.nextFloat() * 20.0F - 10.0F;
        Vector3f vector3f1 = new Vector3f(vector3f.getX() + f, vector3f.getY() + f1, vector3f.getZ());

        entityarmorstand.setHeadPose(vector3f1);
        vector3f = entityarmorstand.s();
        f = random.nextFloat() * 10.0F - 5.0F;
        vector3f1 = new Vector3f(vector3f.getX(), vector3f.getY() + f, vector3f.getZ());
        entityarmorstand.setBodyPose(vector3f1);
    }
}
