package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemHanging extends Item {

    private final Class<? extends EntityHanging> a;

    public ItemHanging(Class<? extends EntityHanging> oclass, Item.Info item_info) {
        super(item_info);
        this.a = oclass;
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        EnumDirection enumdirection = itemactioncontext.getClickedFace();
        BlockPosition blockposition1 = blockposition.shift(enumdirection);
        EntityHuman entityhuman = itemactioncontext.getEntity();

        if (entityhuman != null && !this.a(entityhuman, enumdirection, itemactioncontext.getItemStack(), blockposition1)) {
            return EnumInteractionResult.FAIL;
        } else {
            World world = itemactioncontext.getWorld();
            EntityHanging entityhanging = this.a(world, blockposition1, enumdirection);

            if (entityhanging != null && entityhanging.survives()) {
                if (!world.isClientSide) {
                    entityhanging.m();
                    world.addEntity(entityhanging);
                }

                itemactioncontext.getItemStack().subtract(1);
            }

            return EnumInteractionResult.SUCCESS;
        }
    }

    protected boolean a(EntityHuman entityhuman, EnumDirection enumdirection, ItemStack itemstack, BlockPosition blockposition) {
        return !enumdirection.k().b() && entityhuman.a(blockposition, enumdirection, itemstack);
    }

    @Nullable
    private EntityHanging a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        return (EntityHanging) (this.a == EntityPainting.class ? new EntityPainting(world, blockposition, enumdirection) : (this.a == EntityItemFrame.class ? new EntityItemFrame(world, blockposition, enumdirection) : null));
    }
}
