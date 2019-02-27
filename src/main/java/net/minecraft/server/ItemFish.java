package net.minecraft.server;

public class ItemFish extends ItemFood {

    private final boolean a;
    private final ItemFish.EnumFish b;

    public ItemFish(ItemFish.EnumFish itemfish_enumfish, boolean flag, Item.Info item_info) {
        super(0, 0.0F, false, item_info);
        this.b = itemfish_enumfish;
        this.a = flag;
    }

    public int getNutrition(ItemStack itemstack) {
        ItemFish.EnumFish itemfish_enumfish = ItemFish.EnumFish.a(itemstack);

        return this.a && itemfish_enumfish.e() ? itemfish_enumfish.c() : itemfish_enumfish.a();
    }

    public float getSaturationModifier(ItemStack itemstack) {
        return this.a && this.b.e() ? this.b.d() : this.b.b();
    }

    protected void a(ItemStack itemstack, World world, EntityHuman entityhuman) {
        ItemFish.EnumFish itemfish_enumfish = ItemFish.EnumFish.a(itemstack);

        if (itemfish_enumfish == ItemFish.EnumFish.PUFFERFISH) {
            entityhuman.addEffect(new MobEffect(MobEffects.POISON, 1200, 3));
            entityhuman.addEffect(new MobEffect(MobEffects.HUNGER, 300, 2));
            entityhuman.addEffect(new MobEffect(MobEffects.CONFUSION, 300, 1));
        }

        super.a(itemstack, world, entityhuman);
    }

    public static enum EnumFish {

        COD(2, 0.1F, 5, 0.6F), SALMON(2, 0.1F, 6, 0.8F), TROPICAL_FISH(1, 0.1F), PUFFERFISH(1, 0.1F);

        private final int e;
        private final float f;
        private final int g;
        private final float h;
        private final boolean i;

        private EnumFish(int i, float f, int j, float f1) {
            this.e = i;
            this.f = f;
            this.g = j;
            this.h = f1;
            this.i = j != 0;
        }

        private EnumFish(int i, float f) {
            this(i, f, 0, 0.0F);
        }

        public int a() {
            return this.e;
        }

        public float b() {
            return this.f;
        }

        public int c() {
            return this.g;
        }

        public float d() {
            return this.h;
        }

        public boolean e() {
            return this.i;
        }

        public static ItemFish.EnumFish a(ItemStack itemstack) {
            Item item = itemstack.getItem();

            return item instanceof ItemFish ? ((ItemFish) item).b : ItemFish.EnumFish.COD;
        }
    }
}
