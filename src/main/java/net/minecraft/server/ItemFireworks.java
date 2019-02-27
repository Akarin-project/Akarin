package net.minecraft.server;

import java.util.Arrays;
import java.util.Comparator;

public class ItemFireworks extends Item {

    public ItemFireworks(Item.Info item_info) {
        super(item_info);
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();

        if (!world.isClientSide) {
            BlockPosition blockposition = itemactioncontext.getClickPosition();
            ItemStack itemstack = itemactioncontext.getItemStack();
            EntityFireworks entityfireworks = new EntityFireworks(world, (double) ((float) blockposition.getX() + itemactioncontext.m()), (double) ((float) blockposition.getY() + itemactioncontext.n()), (double) ((float) blockposition.getZ() + itemactioncontext.o()), itemstack);

            world.addEntity(entityfireworks);
            itemstack.subtract(1);
        }

        return EnumInteractionResult.SUCCESS;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        if (entityhuman.dc()) {
            ItemStack itemstack = entityhuman.b(enumhand);

            if (!world.isClientSide) {
                EntityFireworks entityfireworks = new EntityFireworks(world, itemstack, entityhuman);

                world.addEntity(entityfireworks);
                if (!entityhuman.abilities.canInstantlyBuild) {
                    itemstack.subtract(1);
                }
            }

            return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, entityhuman.b(enumhand));
        } else {
            return new InteractionResultWrapper<>(EnumInteractionResult.PASS, entityhuman.b(enumhand));
        }
    }

    public static enum EffectType {

        SMALL_BALL(0, "small_ball"), LARGE_BALL(1, "large_ball"), STAR(2, "star"), CREEPER(3, "creeper"), BURST(4, "burst");

        private static final ItemFireworks.EffectType[] f = (ItemFireworks.EffectType[]) Arrays.stream(values()).sorted(Comparator.comparingInt((itemfireworks_effecttype) -> {
            return itemfireworks_effecttype.g;
        })).toArray((i) -> {
            return new ItemFireworks.EffectType[i];
        });
        private final int g;
        private final String h;

        private EffectType(int i, String s) {
            this.g = i;
            this.h = s;
        }

        public int a() {
            return this.g;
        }
    }
}
