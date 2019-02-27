package net.minecraft.server;

import com.google.common.collect.Multimap;
import java.util.List;
import java.util.UUID;

public class ItemArmor extends Item {

    private static final UUID[] k = new UUID[] { UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
    public static final IDispenseBehavior a = new DispenseBehaviorItem() {
        protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
            ItemStack itemstack1 = ItemArmor.a(isourceblock, itemstack);

            return itemstack1.isEmpty() ? super.a(isourceblock, itemstack) : itemstack1;
        }
    };
    protected final EnumItemSlot b;
    protected final int c;
    protected final float d;
    protected final ArmorMaterial e;

    public static ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
        BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
        List<EntityLiving> list = isourceblock.getWorld().a(EntityLiving.class, new AxisAlignedBB(blockposition), IEntitySelector.f.and(new IEntitySelector.EntitySelectorEquipable(itemstack)));

        if (list.isEmpty()) {
            return ItemStack.a;
        } else {
            EntityLiving entityliving = (EntityLiving) list.get(0);
            EnumItemSlot enumitemslot = EntityInsentient.e(itemstack);
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);

            entityliving.setSlot(enumitemslot, itemstack1);
            if (entityliving instanceof EntityInsentient) {
                ((EntityInsentient) entityliving).a(enumitemslot, 2.0F);
                ((EntityInsentient) entityliving).di();
            }

            return itemstack;
        }
    }

    public ItemArmor(ArmorMaterial armormaterial, EnumItemSlot enumitemslot, Item.Info item_info) {
        super(item_info.b(armormaterial.a(enumitemslot)));
        this.e = armormaterial;
        this.b = enumitemslot;
        this.c = armormaterial.b(enumitemslot);
        this.d = armormaterial.e();
        BlockDispenser.a((IMaterial) this, ItemArmor.a);
    }

    public EnumItemSlot b() {
        return this.b;
    }

    public int c() {
        return this.e.a();
    }

    public ArmorMaterial d() {
        return this.e;
    }

    public boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return this.e.c().test(itemstack1) || super.a(itemstack, itemstack1);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        EnumItemSlot enumitemslot = EntityInsentient.e(itemstack);
        ItemStack itemstack1 = entityhuman.getEquipment(enumitemslot);

        if (itemstack1.isEmpty()) {
            entityhuman.setSlot(enumitemslot, itemstack.cloneItemStack());
            itemstack.setCount(0);
            return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
        } else {
            return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
        }
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Multimap<String, AttributeModifier> multimap = super.a(enumitemslot);

        if (enumitemslot == this.b) {
            multimap.put(GenericAttributes.h.getName(), new AttributeModifier(ItemArmor.k[enumitemslot.b()], "Armor modifier", (double) this.c, 0));
            multimap.put(GenericAttributes.i.getName(), new AttributeModifier(ItemArmor.k[enumitemslot.b()], "Armor toughness", (double) this.d, 0));
        }

        return multimap;
    }

    public int e() {
        return this.c;
    }
}
