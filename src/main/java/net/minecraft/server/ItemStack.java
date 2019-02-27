package net.minecraft.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ItemStack {

    private static final Logger c = LogManager.getLogger();
    public static final ItemStack a = new ItemStack((Item) null);
    public static final DecimalFormat b = D();
    private int count;
    private int e;
    @Deprecated
    private Item item;
    private NBTTagCompound tag;
    private boolean h;
    private EntityItemFrame i;
    private ShapeDetectorBlock j;
    private boolean k;
    private ShapeDetectorBlock l;
    private boolean m;

    private static DecimalFormat D() {
        DecimalFormat decimalformat = new DecimalFormat("#.##");

        decimalformat.setDecimalFormatSymbols(DecimalFormatSymbols.getInstance(Locale.ROOT));
        return decimalformat;
    }

    public ItemStack(IMaterial imaterial) {
        this(imaterial, 1);
    }

    public ItemStack(IMaterial imaterial, int i) {
        this.item = imaterial == null ? null : imaterial.getItem();
        this.count = i;
        this.E();
    }

    private void E() {
        this.h = false;
        this.h = this.isEmpty();
    }

    private ItemStack(NBTTagCompound nbttagcompound) {
        Item item = (Item) IRegistry.ITEM.get(new MinecraftKey(nbttagcompound.getString("id")));

        this.item = item == null ? Items.AIR : item;
        this.count = nbttagcompound.getByte("Count");
        if (nbttagcompound.hasKeyOfType("tag", 10)) {
            this.tag = nbttagcompound.getCompound("tag");
            this.getItem().a(nbttagcompound);
        }

        if (this.getItem().usesDurability()) {
            this.setDamage(this.getDamage());
        }

        this.E();
    }

    public static ItemStack a(NBTTagCompound nbttagcompound) {
        try {
            return new ItemStack(nbttagcompound);
        } catch (RuntimeException runtimeexception) {
            ItemStack.c.debug("Tried to load invalid item: {}", nbttagcompound, runtimeexception);
            return ItemStack.a;
        }
    }

    public boolean isEmpty() {
        return this == ItemStack.a ? true : (this.getItem() != null && this.getItem() != Items.AIR ? this.count <= 0 : true);
    }

    public ItemStack cloneAndSubtract(int i) {
        int j = Math.min(i, this.count);
        ItemStack itemstack = this.cloneItemStack();

        itemstack.setCount(j);
        this.subtract(j);
        return itemstack;
    }

    public Item getItem() {
        return this.h ? Items.AIR : this.item;
    }

    public EnumInteractionResult placeItem(ItemActionContext itemactioncontext) {
        EntityHuman entityhuman = itemactioncontext.getEntity();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(itemactioncontext.getWorld(), blockposition, false);

        if (entityhuman != null && !entityhuman.abilities.mayBuild && !this.b(itemactioncontext.getWorld().F(), shapedetectorblock)) {
            return EnumInteractionResult.PASS;
        } else {
            Item item = this.getItem();
            EnumInteractionResult enuminteractionresult = item.a(itemactioncontext);

            if (entityhuman != null && enuminteractionresult == EnumInteractionResult.SUCCESS) {
                entityhuman.b(StatisticList.ITEM_USED.b(item));
            }

            return enuminteractionresult;
        }
    }

    public float a(IBlockData iblockdata) {
        return this.getItem().getDestroySpeed(this, iblockdata);
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        return this.getItem().a(world, entityhuman, enumhand);
    }

    public ItemStack a(World world, EntityLiving entityliving) {
        return this.getItem().a(this, world, entityliving);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        MinecraftKey minecraftkey = IRegistry.ITEM.getKey(this.getItem());

        nbttagcompound.setString("id", minecraftkey == null ? "minecraft:air" : minecraftkey.toString());
        nbttagcompound.setByte("Count", (byte) this.count);
        if (this.tag != null) {
            nbttagcompound.set("tag", this.tag);
        }

        return nbttagcompound;
    }

    public int getMaxStackSize() {
        return this.getItem().getMaxStackSize();
    }

    public boolean isStackable() {
        return this.getMaxStackSize() > 1 && (!this.e() || !this.f());
    }

    public boolean e() {
        if (!this.h && this.getItem().getMaxDurability() > 0) {
            NBTTagCompound nbttagcompound = this.getTag();

            return nbttagcompound == null || !nbttagcompound.getBoolean("Unbreakable");
        } else {
            return false;
        }
    }

    public boolean f() {
        return this.e() && this.getDamage() > 0;
    }

    public int getDamage() {
        return this.tag == null ? 0 : this.tag.getInt("Damage");
    }

    public void setDamage(int i) {
        this.getOrCreateTag().setInt("Damage", Math.max(0, i));
    }

    public int h() {
        return this.getItem().getMaxDurability();
    }

    public boolean isDamaged(int i, Random random, @Nullable EntityPlayer entityplayer) {
        if (!this.e()) {
            return false;
        } else {
            int j;

            if (i > 0) {
                j = EnchantmentManager.getEnchantmentLevel(Enchantments.DURABILITY, this);
                int k = 0;

                for (int l = 0; j > 0 && l < i; ++l) {
                    if (EnchantmentDurability.a(this, j, random)) {
                        ++k;
                    }
                }

                i -= k;
                if (i <= 0) {
                    return false;
                }
            }

            if (entityplayer != null && i != 0) {
                CriterionTriggers.t.a(entityplayer, this, this.getDamage() + i);
            }

            j = this.getDamage() + i;
            this.setDamage(j);
            return j >= this.h();
        }
    }

    public void damage(int i, EntityLiving entityliving) {
        if (!(entityliving instanceof EntityHuman) || !((EntityHuman) entityliving).abilities.canInstantlyBuild) {
            if (this.e()) {
                if (this.isDamaged(i, entityliving.getRandom(), entityliving instanceof EntityPlayer ? (EntityPlayer) entityliving : null)) {
                    entityliving.c(this);
                    Item item = this.getItem();

                    this.subtract(1);
                    if (entityliving instanceof EntityHuman) {
                        ((EntityHuman) entityliving).b(StatisticList.ITEM_BROKEN.b(item));
                    }

                    this.setDamage(0);
                }

            }
        }
    }

    public void a(EntityLiving entityliving, EntityHuman entityhuman) {
        Item item = this.getItem();

        if (item.a(this, entityliving, (EntityLiving) entityhuman)) {
            entityhuman.b(StatisticList.ITEM_USED.b(item));
        }

    }

    public void a(World world, IBlockData iblockdata, BlockPosition blockposition, EntityHuman entityhuman) {
        Item item = this.getItem();

        if (item.a(this, world, iblockdata, blockposition, entityhuman)) {
            entityhuman.b(StatisticList.ITEM_USED.b(item));
        }

    }

    public boolean b(IBlockData iblockdata) {
        return this.getItem().canDestroySpecialBlock(iblockdata);
    }

    public boolean a(EntityHuman entityhuman, EntityLiving entityliving, EnumHand enumhand) {
        return this.getItem().a(this, entityhuman, entityliving, enumhand);
    }

    public ItemStack cloneItemStack() {
        ItemStack itemstack = new ItemStack(this.getItem(), this.count);

        itemstack.d(this.B());
        if (this.tag != null) {
            itemstack.tag = this.tag.clone();
        }

        return itemstack;
    }

    public static boolean equals(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.isEmpty() && itemstack1.isEmpty() ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? (itemstack.tag == null && itemstack1.tag != null ? false : itemstack.tag == null || itemstack.tag.equals(itemstack1.tag)) : false);
    }

    public static boolean matches(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.isEmpty() && itemstack1.isEmpty() ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.c(itemstack1) : false);
    }

    private boolean c(ItemStack itemstack) {
        return this.count != itemstack.count ? false : (this.getItem() != itemstack.getItem() ? false : (this.tag == null && itemstack.tag != null ? false : this.tag == null || this.tag.equals(itemstack.tag)));
    }

    public static boolean c(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == itemstack1 ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.doMaterialsMatch(itemstack1) : false);
    }

    public static boolean d(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack == itemstack1 ? true : (!itemstack.isEmpty() && !itemstack1.isEmpty() ? itemstack.b(itemstack1) : false);
    }

    public boolean doMaterialsMatch(ItemStack itemstack) {
        return !itemstack.isEmpty() && this.getItem() == itemstack.getItem();
    }

    public boolean b(ItemStack itemstack) {
        return !this.e() ? this.doMaterialsMatch(itemstack) : !itemstack.isEmpty() && this.getItem() == itemstack.getItem();
    }

    public String j() {
        return this.getItem().h(this);
    }

    public String toString() {
        return this.count + "x" + this.getItem().getName();
    }

    public void a(World world, Entity entity, int i, boolean flag) {
        if (this.e > 0) {
            --this.e;
        }

        if (this.getItem() != null) {
            this.getItem().a(this, world, entity, i, flag);
        }

    }

    public void a(World world, EntityHuman entityhuman, int i) {
        entityhuman.a(StatisticList.ITEM_CRAFTED.b(this.getItem()), i);
        this.getItem().b(this, world, entityhuman);
    }

    public int k() {
        return this.getItem().c(this);
    }

    public EnumAnimation l() {
        return this.getItem().d(this);
    }

    public void a(World world, EntityLiving entityliving, int i) {
        this.getItem().a(this, world, entityliving, i);
    }

    public boolean hasTag() {
        return !this.h && this.tag != null && !this.tag.isEmpty();
    }

    @Nullable
    public NBTTagCompound getTag() {
        return this.tag;
    }

    public NBTTagCompound getOrCreateTag() {
        if (this.tag == null) {
            this.setTag(new NBTTagCompound());
        }

        return this.tag;
    }

    public NBTTagCompound a(String s) {
        if (this.tag != null && this.tag.hasKeyOfType(s, 10)) {
            return this.tag.getCompound(s);
        } else {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            this.a(s, (NBTBase) nbttagcompound);
            return nbttagcompound;
        }
    }

    @Nullable
    public NBTTagCompound b(String s) {
        return this.tag != null && this.tag.hasKeyOfType(s, 10) ? this.tag.getCompound(s) : null;
    }

    public void c(String s) {
        if (this.tag != null && this.tag.hasKey(s)) {
            this.tag.remove(s);
            if (this.tag.isEmpty()) {
                this.tag = null;
            }
        }

    }

    public NBTTagList getEnchantments() {
        return this.tag != null ? this.tag.getList("Enchantments", 10) : new NBTTagList();
    }

    public void setTag(@Nullable NBTTagCompound nbttagcompound) {
        this.tag = nbttagcompound;
    }

    public IChatBaseComponent getName() {
        NBTTagCompound nbttagcompound = this.b("display");

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Name", 8)) {
            try {
                IChatBaseComponent ichatbasecomponent = IChatBaseComponent.ChatSerializer.a(nbttagcompound.getString("Name"));

                if (ichatbasecomponent != null) {
                    return ichatbasecomponent;
                }

                nbttagcompound.remove("Name");
            } catch (JsonParseException jsonparseexception) {
                nbttagcompound.remove("Name");
            }
        }

        return this.getItem().i(this);
    }

    public ItemStack a(@Nullable IChatBaseComponent ichatbasecomponent) {
        NBTTagCompound nbttagcompound = this.a("display");

        if (ichatbasecomponent != null) {
            nbttagcompound.setString("Name", IChatBaseComponent.ChatSerializer.a(ichatbasecomponent));
        } else {
            nbttagcompound.remove("Name");
        }

        return this;
    }

    public void r() {
        NBTTagCompound nbttagcompound = this.b("display");

        if (nbttagcompound != null) {
            nbttagcompound.remove("Name");
            if (nbttagcompound.isEmpty()) {
                this.c("display");
            }
        }

        if (this.tag != null && this.tag.isEmpty()) {
            this.tag = null;
        }

    }

    public boolean hasName() {
        NBTTagCompound nbttagcompound = this.b("display");

        return nbttagcompound != null && nbttagcompound.hasKeyOfType("Name", 8);
    }

    public EnumItemRarity u() {
        return this.getItem().j(this);
    }

    public boolean canEnchant() {
        return !this.getItem().a(this) ? false : !this.hasEnchantments();
    }

    public void addEnchantment(Enchantment enchantment, int i) {
        this.getOrCreateTag();
        if (!this.tag.hasKeyOfType("Enchantments", 9)) {
            this.tag.set("Enchantments", new NBTTagList());
        }

        NBTTagList nbttaglist = this.tag.getList("Enchantments", 10);
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setString("id", String.valueOf(IRegistry.ENCHANTMENT.getKey(enchantment)));
        nbttagcompound.setShort("lvl", (short) ((byte) i));
        nbttaglist.add((NBTBase) nbttagcompound);
    }

    public boolean hasEnchantments() {
        return this.tag != null && this.tag.hasKeyOfType("Enchantments", 9) ? !this.tag.getList("Enchantments", 10).isEmpty() : false;
    }

    public void a(String s, NBTBase nbtbase) {
        this.getOrCreateTag().set(s, nbtbase);
    }

    public boolean x() {
        return this.i != null;
    }

    public void a(@Nullable EntityItemFrame entityitemframe) {
        this.i = entityitemframe;
    }

    @Nullable
    public EntityItemFrame y() {
        return this.h ? null : this.i;
    }

    public int getRepairCost() {
        return this.hasTag() && this.tag.hasKeyOfType("RepairCost", 3) ? this.tag.getInt("RepairCost") : 0;
    }

    public void setRepairCost(int i) {
        this.getOrCreateTag().setInt("RepairCost", i);
    }

    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Object object;

        if (this.hasTag() && this.tag.hasKeyOfType("AttributeModifiers", 9)) {
            object = HashMultimap.create();
            NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound = nbttaglist.getCompound(i);
                AttributeModifier attributemodifier = GenericAttributes.a(nbttagcompound);

                if (attributemodifier != null && (!nbttagcompound.hasKeyOfType("Slot", 8) || nbttagcompound.getString("Slot").equals(enumitemslot.getSlotName())) && attributemodifier.a().getLeastSignificantBits() != 0L && attributemodifier.a().getMostSignificantBits() != 0L) {
                    ((Multimap) object).put(nbttagcompound.getString("AttributeName"), attributemodifier);
                }
            }
        } else {
            object = this.getItem().a(enumitemslot);
        }

        return (Multimap) object;
    }

    public void a(String s, AttributeModifier attributemodifier, @Nullable EnumItemSlot enumitemslot) {
        this.getOrCreateTag();
        if (!this.tag.hasKeyOfType("AttributeModifiers", 9)) {
            this.tag.set("AttributeModifiers", new NBTTagList());
        }

        NBTTagList nbttaglist = this.tag.getList("AttributeModifiers", 10);
        NBTTagCompound nbttagcompound = GenericAttributes.a(attributemodifier);

        nbttagcompound.setString("AttributeName", s);
        if (enumitemslot != null) {
            nbttagcompound.setString("Slot", enumitemslot.getSlotName());
        }

        nbttaglist.add((NBTBase) nbttagcompound);
    }

    public IChatBaseComponent A() {
        IChatBaseComponent ichatbasecomponent = (new ChatComponentText("")).addSibling(this.getName());

        if (this.hasName()) {
            ichatbasecomponent.a(EnumChatFormat.ITALIC);
        }

        IChatBaseComponent ichatbasecomponent1 = ChatComponentUtils.a(ichatbasecomponent);

        if (!this.h) {
            NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

            ichatbasecomponent1.a(this.u().e).a((chatmodifier) -> {
                chatmodifier.setChatHoverable(new ChatHoverable(ChatHoverable.EnumHoverAction.SHOW_ITEM, new ChatComponentText(nbttagcompound.toString())));
            });
        }

        return ichatbasecomponent1;
    }

    private static boolean a(ShapeDetectorBlock shapedetectorblock, @Nullable ShapeDetectorBlock shapedetectorblock1) {
        return shapedetectorblock1 != null && shapedetectorblock.a() == shapedetectorblock1.a() ? (shapedetectorblock.b() == null && shapedetectorblock1.b() == null ? true : (shapedetectorblock.b() != null && shapedetectorblock1.b() != null ? Objects.equals(shapedetectorblock.b().save(new NBTTagCompound()), shapedetectorblock1.b().save(new NBTTagCompound())) : false)) : false;
    }

    public boolean a(TagRegistry tagregistry, ShapeDetectorBlock shapedetectorblock) {
        if (a(shapedetectorblock, this.j)) {
            return this.k;
        } else {
            this.j = shapedetectorblock;
            if (this.hasTag() && this.tag.hasKeyOfType("CanDestroy", 9)) {
                NBTTagList nbttaglist = this.tag.getList("CanDestroy", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);

                    try {
                        Predicate<ShapeDetectorBlock> predicate = ArgumentBlockPredicate.a().parse(new StringReader(s)).create(tagregistry);

                        if (predicate.test(shapedetectorblock)) {
                            this.k = true;
                            return true;
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ;
                    }
                }
            }

            this.k = false;
            return false;
        }
    }

    public boolean b(TagRegistry tagregistry, ShapeDetectorBlock shapedetectorblock) {
        if (a(shapedetectorblock, this.l)) {
            return this.m;
        } else {
            this.l = shapedetectorblock;
            if (this.hasTag() && this.tag.hasKeyOfType("CanPlaceOn", 9)) {
                NBTTagList nbttaglist = this.tag.getList("CanPlaceOn", 8);

                for (int i = 0; i < nbttaglist.size(); ++i) {
                    String s = nbttaglist.getString(i);

                    try {
                        Predicate<ShapeDetectorBlock> predicate = ArgumentBlockPredicate.a().parse(new StringReader(s)).create(tagregistry);

                        if (predicate.test(shapedetectorblock)) {
                            this.m = true;
                            return true;
                        }
                    } catch (CommandSyntaxException commandsyntaxexception) {
                        ;
                    }
                }
            }

            this.m = false;
            return false;
        }
    }

    public int B() {
        return this.e;
    }

    public void d(int i) {
        this.e = i;
    }

    public int getCount() {
        return this.h ? 0 : this.count;
    }

    public void setCount(int i) {
        this.count = i;
        this.E();
    }

    public void add(int i) {
        this.setCount(this.count + i);
    }

    public void subtract(int i) {
        this.add(-i);
    }
}
