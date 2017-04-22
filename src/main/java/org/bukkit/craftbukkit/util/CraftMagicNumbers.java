package org.bukkit.craftbukkit.util;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.minecraft.server.Block;
import net.minecraft.server.Blocks;
import net.minecraft.server.Item;
import net.minecraft.server.MinecraftKey;
import net.minecraft.server.MojangsonParseException;
import net.minecraft.server.MojangsonParser;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.StatisticList;

import org.bukkit.Achievement;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.UnsafeValues;
import org.bukkit.craftbukkit.CraftStatistic;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.StringUtil;

@SuppressWarnings("deprecation")
public final class CraftMagicNumbers implements UnsafeValues {
    public static final UnsafeValues INSTANCE = new CraftMagicNumbers();

    private CraftMagicNumbers() {}

    public static Block getBlock(org.bukkit.block.Block block) {
        return getBlock(block.getType());
    }

    @Deprecated
    // A bad method for bad magic.
    public static Block getBlock(int id) {
        return getBlock(Material.getMaterial(id));
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(Block block) {
        return Block.getId(block);
    }

    public static Material getMaterial(Block block) {
        return Material.getMaterial(Block.getId(block));
    }

    public static Item getItem(Material material) {
        // TODO: Don't use ID
        Item item = Item.getById(material.getId());
        return item;
    }

    @Deprecated
    // A bad method for bad magic.
    public static Item getItem(int id) {
        return Item.getById(id);
    }

    @Deprecated
    // A bad method for bad magic.
    public static int getId(Item item) {
        return Item.getId(item);
    }

    public static Material getMaterial(Item item) {
        // TODO: Don't use ID
        Material material = Material.getMaterial(Item.getId(item));

        if (material == null) {
            return Material.AIR;
        }

        return material;
    }

    public static Block getBlock(Material material) {
        if (material == null) {
            return null;
        }
        // TODO: Don't use ID
        Block block = Block.getById(material.getId());

        if (block == null) {
            return Blocks.AIR;
        }

        return block;
    }

    @Override
    public Material getMaterialFromInternalName(String name) {
        return getMaterial((Item) Item.REGISTRY.get(new MinecraftKey(name)));
    }

    @Override
    public List<String> tabCompleteInternalMaterialName(String token, List<String> completions) {
        ArrayList<String> results = Lists.newArrayList();
        for (MinecraftKey key : (Set<MinecraftKey>)Item.REGISTRY.keySet()) {
            results.add(key.toString());
        }
        return StringUtil.copyPartialMatches(token, results, completions);
    }

    @Override
    public ItemStack modifyItemStack(ItemStack stack, String arguments) {
        net.minecraft.server.ItemStack nmsStack = CraftItemStack.asNMSCopy(stack);

        try {
            nmsStack.setTag((NBTTagCompound) MojangsonParser.parse(arguments));
        } catch (MojangsonParseException ex) {
            Logger.getLogger(CraftMagicNumbers.class.getName()).log(Level.SEVERE, null, ex);
        }

        stack.setItemMeta(CraftItemStack.getItemMeta(nmsStack));

        return stack;
    }

    @Override
    public Statistic getStatisticFromInternalName(String name) {
        return CraftStatistic.getBukkitStatisticByName(name);
    }

    @Override
    public Achievement getAchievementFromInternalName(String name) {
        return CraftStatistic.getBukkitAchievementByName(name);
    }

    @Override
    public List<String> tabCompleteInternalStatisticOrAchievementName(String token, List<String> completions) {
        List<String> matches = new ArrayList<String>();
        Iterator iterator = StatisticList.stats.iterator();
        while (iterator.hasNext()) {
            String statistic = ((net.minecraft.server.Statistic) iterator.next()).name;
            if (statistic.startsWith(token)) {
                matches.add(statistic);
            }
        }
        return matches;
    }

    /**
     * This helper class represents the different NBT Tags.
     * <p>
     * These should match NBTBase#getTypeId
     */
    public static class NBT {

        public static final int TAG_END = 0;
        public static final int TAG_BYTE = 1;
        public static final int TAG_SHORT = 2;
        public static final int TAG_INT = 3;
        public static final int TAG_LONG = 4;
        public static final int TAG_FLOAT = 5;
        public static final int TAG_DOUBLE = 6;
        public static final int TAG_BYTE_ARRAY = 7;
        public static final int TAG_STRING = 8;
        public static final int TAG_LIST = 9;
        public static final int TAG_COMPOUND = 10;
        public static final int TAG_INT_ARRAY = 11;
        public static final int TAG_ANY_NUMBER = 99;
    }
}
