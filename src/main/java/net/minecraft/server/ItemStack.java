package net.minecraft.server;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonParseException;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockFertilizeEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.event.world.StructureGrowEvent;
// CraftBukkit end

public final class ItemStack {

    private static final Logger c = LogManager.getLogger();
    public static final ItemStack a = new ItemStack((Item) null);
    public static final DecimalFormat b = D();
    private int count;
    private int e;
    @Deprecated
    private Item item;
    NBTTagCompound tag; // Paper -> package private
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
    // Paper start
    private static final java.util.Comparator<? super NBTTagCompound> enchantSorter = java.util.Comparator.comparing(o -> o.getString("id"));
    private void processEnchantOrder(NBTTagCompound tag) {
        if (tag == null || !tag.hasKeyOfType("Enchantments", 9)) {
            return;
        }
        NBTTagList list = tag.getList("Enchantments", 10);
        if (list.size() < 2) {
            return;
        }
        try {
            //noinspection unchecked
            list.sort((Comparator<? super NBTBase>) enchantSorter); // Paper
        } catch (Exception ignored) {}
    }
    // Paper end

    public ItemStack(IMaterial imaterial) {
        this(imaterial, 1);
    }

    public ItemStack(IMaterial imaterial, int i) {
        this.item = imaterial == null ? null : imaterial.getItem();
        this.count = i;
        this.E();
    }

    // Called to run this stack through the data converter to handle older storage methods and serialized items
    public void convertStack() {
        if (false && MinecraftServer.getServer() != null) { // Skip for now, causes more issues than it solves
            // Don't convert some things - both the old and new data values are valid
            // Conversion would make getting then impossible
            if (this.item == Blocks.PUMPKIN.getItem() || this.item == Blocks.GRASS.getItem() || this.item == Blocks.SNOW.getItem()) {
                return;
            }

            NBTTagCompound savedStack = new NBTTagCompound();
            this.save(savedStack);
            savedStack = (NBTTagCompound) MinecraftServer.getServer().dataConverterManager.update(DataConverterTypes.ITEM_STACK, new Dynamic(DynamicOpsNBT.a, savedStack), -1, CraftMagicNumbers.INSTANCE.getDataVersion()).getValue();
            this.load(savedStack);
        }
    }

    private void E() {
        if (this.h && this == ItemStack.a) throw new AssertionError("TRAP"); // CraftBukkit
        this.h = false;
        this.h = this.isEmpty();
    }

    // CraftBukkit - break into own method
    private void load(NBTTagCompound nbttagcompound) {
        Item item = (Item) IRegistry.ITEM.get(new MinecraftKey(nbttagcompound.getString("id")));

        this.item = item == null ? Items.AIR : item;
        this.count = nbttagcompound.getByte("Count");
        if (nbttagcompound.hasKeyOfType("tag", 10)) {
            // CraftBukkit start - make defensive copy as this data may be coming from the save thread
            this.tag = (NBTTagCompound) nbttagcompound.getCompound("tag").clone();
            processEnchantOrder(this.tag); // Paper
            this.getItem().a(this.tag);
            // CraftBukkit end
        }

        if (this.getItem().usesDurability()) {
            this.setDamage(this.getDamage());
        }
    }

    private ItemStack(NBTTagCompound nbttagcompound) {
        this.load(nbttagcompound);
        // CraftBukkit end
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
        return this == ItemStack.a || this.item == null || this.item == Items.AIR || this.count <= 0; // Paper
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

    public EnumInteractionResult placeItem(ItemActionContext itemactioncontext, EnumHand enumhand) { // CraftBukkit - add hand
        EntityHuman entityhuman = itemactioncontext.getEntity();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(itemactioncontext.getWorld(), blockposition, false);

        if (entityhuman != null && !entityhuman.abilities.mayBuild && !this.b(itemactioncontext.getWorld().F(), shapedetectorblock)) {
            return EnumInteractionResult.PASS;
        } else {
            // CraftBukkit start - handle all block place event logic here
            NBTTagCompound oldData = this.getTagClone();
            int oldCount = this.getCount();
            World world = itemactioncontext.getWorld();

            if (!(this.getItem() instanceof ItemBucket)) { // if not bucket
                world.captureBlockStates = true;
                // special case bonemeal
                if (this.getItem() == Items.BONE_MEAL) {
                    world.captureTreeGeneration = true;
                }
            }
            Item item = this.getItem();
            EnumInteractionResult enuminteractionresult = item.a(itemactioncontext);
            NBTTagCompound newData = this.getTagClone();
            int newCount = this.getCount();
            this.setCount(oldCount);
            this.setTagClone(oldData);
            world.captureBlockStates = false;
            if (enuminteractionresult == EnumInteractionResult.SUCCESS && world.captureTreeGeneration && world.capturedBlockStates.size() > 0) {
                world.captureTreeGeneration = false;
                Location location = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
                TreeType treeType = BlockSapling.treeType;
                BlockSapling.treeType = null;
                List<BlockState> blocks = (List<BlockState>) world.capturedBlockStates.clone();
                world.capturedBlockStates.clear();
                StructureGrowEvent structureEvent = null;
                if (treeType != null) {
                    boolean isBonemeal = getItem() == Items.BONE_MEAL;
                    structureEvent = new StructureGrowEvent(location, treeType, isBonemeal, (Player) entityhuman.getBukkitEntity(), blocks);
                    org.bukkit.Bukkit.getPluginManager().callEvent(structureEvent);
                }

                BlockFertilizeEvent fertilizeEvent = new BlockFertilizeEvent(CraftBlock.at(world, blockposition), (Player) entityhuman.getBukkitEntity(), blocks);
                fertilizeEvent.setCancelled(structureEvent != null && structureEvent.isCancelled());
                org.bukkit.Bukkit.getPluginManager().callEvent(fertilizeEvent);

                if (!fertilizeEvent.isCancelled()) {
                    // Change the stack to its new contents if it hasn't been tampered with.
                    if (this.getCount() == oldCount && Objects.equals(this.tag, oldData)) {
                        this.setTag(newData);
                        this.setCount(newCount);
                    }
                    for (BlockState blockstate : blocks) {
                        blockstate.update(true);
                    }
                }

                return enuminteractionresult;
            }
            world.captureTreeGeneration = false;

            if (entityhuman != null && enuminteractionresult == EnumInteractionResult.SUCCESS) {
                org.bukkit.event.block.BlockPlaceEvent placeEvent = null;
                List<BlockState> blocks = (List<BlockState>) world.capturedBlockStates.clone();
                world.capturedBlockStates.clear();
                if (blocks.size() > 1) {
                    placeEvent = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockMultiPlaceEvent(world, entityhuman, enumhand, blocks, blockposition.getX(), blockposition.getY(), blockposition.getZ());
                } else if (blocks.size() == 1) {
                    placeEvent = org.bukkit.craftbukkit.event.CraftEventFactory.callBlockPlaceEvent(world, entityhuman, enumhand, blocks.get(0), blockposition.getX(), blockposition.getY(), blockposition.getZ());
                }

                if (placeEvent != null && (placeEvent.isCancelled() || !placeEvent.canBuild())) {
                    enuminteractionresult = EnumInteractionResult.FAIL; // cancel placement
                    // PAIL: Remove this when MC-99075 fixed
                    placeEvent.getPlayer().updateInventory();

                    // Paper start
                    for (Map.Entry<BlockPosition, TileEntity> e : world.capturedTileEntities.entrySet()) {
                        if (e.getValue() instanceof TileEntityLootable) {
                            ((TileEntityLootable) e.getValue()).setLootTable(null);
                        }
                    }
                    // Paper end

                    // revert back all captured blocks
                    for (BlockState blockstate : blocks) {
                        blockstate.update(true, false);
                    }

                    // Brute force all possible updates
                    BlockPosition placedPos = ((CraftBlock) placeEvent.getBlock()).getPosition();
                    for (EnumDirection dir : EnumDirection.values()) {
                        ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, placedPos.shift(dir)));
                    }
                } else {
                    // Change the stack to its new contents if it hasn't been tampered with.
                    if (this.getCount() == oldCount && Objects.equals(this.tag, oldData)) {
                        this.setTag(newData);
                        this.setCount(newCount);
                    }

                    for (Map.Entry<BlockPosition, TileEntity> e : world.capturedTileEntities.entrySet()) {
                        world.setTileEntity(e.getKey(), e.getValue());
                    }

                    for (BlockState blockstate : blocks) {
                        int updateFlag = ((CraftBlockState) blockstate).getFlag();
                        IBlockData oldBlock = ((CraftBlockState) blockstate).getHandle();
                        BlockPosition newblockposition = ((CraftBlockState) blockstate).getPosition();
                        IBlockData block = world.getType(newblockposition);

                        if (!(block.getBlock() instanceof BlockTileEntity)) { // Containers get placed automatically
                            block.getBlock().onPlace(block, world, newblockposition, oldBlock);
                        }

                        world.notifyAndUpdatePhysics(newblockposition, null, oldBlock, block, world.getType(newblockposition), updateFlag); // send null chunk as chunk.k() returns false by this point
                    }

                    // Special case juke boxes as they update their tile entity. Copied from ItemRecord.
                    // PAIL: checkme on updates.
                    if (this.item instanceof ItemRecord) {
                        ((BlockJukeBox) Blocks.JUKEBOX).a(world, blockposition, world.getType(blockposition), this);
                        world.a((EntityHuman) null, 1010, blockposition, Item.getId(this.item));
                        this.subtract(1);
                        entityhuman.a(StatisticList.PLAY_RECORD);
                    }

                    if (this.item == Items.WITHER_SKELETON_SKULL) { // Special case skulls to allow wither spawns to be cancelled
                        BlockPosition bp = blockposition;
                        if (!world.getType(blockposition).getMaterial().isReplaceable()) {
                            if (!world.getType(blockposition).getMaterial().isBuildable()) {
                                bp = null;
                            } else {
                                bp = bp.shift(itemactioncontext.getClickedFace());
                            }
                        }
                        if (bp != null) {
                            TileEntity te = world.getTileEntity(bp);
                            if (te instanceof TileEntitySkull) {
                                BlockWitherSkull.a(world, bp, (TileEntitySkull) te);
                            }
                        }
                    }

                    // SPIGOT-1288 - play sound stripped from ItemBlock
                    if (this.item instanceof ItemBlock) {
                        SoundEffectType soundeffecttype = ((ItemBlock) this.item).getBlock().getStepSound();
                        world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F);
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(item));
                }
            }
            world.capturedTileEntities.clear();
            world.capturedBlockStates.clear();
            // CraftBukkit end

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
            nbttagcompound.set("tag", this.tag.clone()); // CraftBukkit - make defensive copy, data is going to another thread
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
                // CraftBukkit start
                if (entityplayer != null) {
                    PlayerItemDamageEvent event = new PlayerItemDamageEvent(entityplayer.getBukkitEntity(), CraftItemStack.asCraftMirror(this), i);
                    event.getPlayer().getServer().getPluginManager().callEvent(event);

                    if (i != event.getDamage() || event.isCancelled()) {
                        event.getPlayer().updateInventory();
                    }
                    if (event.isCancelled()) {
                        return false;
                    }

                    i = event.getDamage();
                }
                // CraftBukkit end
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
                    // CraftBukkit start - Check for item breaking
                    if (this.count == 1 && entityliving instanceof EntityHuman) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerItemBreakEvent((EntityHuman) entityliving, this);
                    }
                    // CraftBukkit end

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

    public ItemStack cloneItemStack() { return cloneItemStack(false); } // Paper
    public ItemStack cloneItemStack(boolean origItem) { // Paper
        ItemStack itemstack = new ItemStack(origItem ? this.item : this.getItem(), this.count); // Paper

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

    public int getItemUseMaxDuration() { return k(); } // Paper - OBFHELPER
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

    // CraftBukkit start
    @Nullable
    private NBTTagCompound getTagClone() {
        return this.tag == null ? null : this.tag.clone();
    }

    private void setTagClone(@Nullable NBTTagCompound nbtttagcompound) {
        this.setTag(nbtttagcompound == null ? null : nbtttagcompound.clone());
    }
    // CraftBukkit end

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

    // Paper start - (this is just a good no conflict location)
    public org.bukkit.inventory.ItemStack asBukkitMirror() {
        return CraftItemStack.asCraftMirror(this);
    }
    public org.bukkit.inventory.ItemStack asBukkitCopy() {
        return CraftItemStack.asCraftMirror(this.cloneItemStack());
    }
    public static ItemStack fromBukkitCopy(org.bukkit.inventory.ItemStack itemstack) {
        return CraftItemStack.asNMSCopy(itemstack);
    }
    // Paper end
    public void setTag(@Nullable NBTTagCompound nbttagcompound) {
        this.tag = nbttagcompound;
        processEnchantOrder(this.tag); // Paper
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
        processEnchantOrder(nbttagcompound); // Paper
    }

    public boolean hasEnchantments() {
        return this.tag != null && this.tag.hasKeyOfType("Enchantments", 9) ? !this.tag.getList("Enchantments", 10).isEmpty() : false;
    }

    public void getOrCreateTagAndSet(String s, NBTBase nbtbase) { a(s, nbtbase);} // Paper - OBFHELPER
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
        // CraftBukkit start - remove RepairCost tag when 0 (SPIGOT-3945)
        if (i == 0) {
            if (this.hasTag()) {
                this.tag.remove("RepairCost");
            }
            return;
        }
        // CraftBukkit end
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

    // CraftBukkit start
    @Deprecated
    public void setItem(Item item) {
        this.item = item;
    }
    // CraftBukkit end

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
