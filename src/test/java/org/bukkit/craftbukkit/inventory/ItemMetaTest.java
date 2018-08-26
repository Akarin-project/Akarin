package org.bukkit.craftbukkit.inventory;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.server.Block;
import net.minecraft.server.IRegistry;
import net.minecraft.server.ITileEntity;
import net.minecraft.server.Item;
import net.minecraft.server.ItemBlock;
import net.minecraft.server.ItemBlockWallable;
import net.minecraft.server.NBTTagInt;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.FireworkEffect.Type;
import org.bukkit.NamespacedKey;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.craftbukkit.inventory.ItemStackTest.StackProvider;
import org.bukkit.craftbukkit.inventory.ItemStackTest.StackWrapper;
import org.bukkit.craftbukkit.inventory.ItemStackTest.BukkitWrapper;
import org.bukkit.craftbukkit.inventory.ItemStackTest.CraftWrapper;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TropicalFish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkEffectMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.KnowledgeBookMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SpawnEggMeta;
import org.bukkit.inventory.meta.TropicalFishBucketMeta;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Test;

public class ItemMetaTest extends AbstractTestingBase {

    static final int MAX_FIREWORK_POWER = 127; // Please update ItemStackFireworkTest if/when this gets changed.

    @Test(expected=IllegalArgumentException.class)
    public void testPowerLimitExact() {
        newFireworkMeta().setPower(MAX_FIREWORK_POWER + 1);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPowerLimitMax() {
        newFireworkMeta().setPower(Integer.MAX_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPowerLimitMin() {
        newFireworkMeta().setPower(Integer.MIN_VALUE);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testPowerLimitNegative() {
        newFireworkMeta().setPower(-1);
    }

    @Test
    public void testPowers() {
        for (int i = 0; i <= MAX_FIREWORK_POWER; i++) {
            FireworkMeta firework = newFireworkMeta();
            firework.setPower(i);
            assertThat(String.valueOf(i), firework.getPower(), is(i));
        }
    }

    @Test
    public void testConflictingEnchantment() {
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_PICKAXE);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.DURABILITY), is(false));

        itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.DURABILITY), is(false));
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.LOOT_BONUS_BLOCKS), is(true));
        assertThat(itemMeta.hasConflictingEnchant(null), is(false));
    }

    @Test
    public void testConflictingStoredEnchantment() {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.DURABILITY), is(false));

        itemMeta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.DURABILITY), is(false));
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS), is(true));
        assertThat(itemMeta.hasConflictingStoredEnchant(null), is(false));
    }

    @Test
    public void testConflictingEnchantments() {
        ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta(Material.DIAMOND_PICKAXE);
        itemMeta.addEnchant(Enchantment.DURABILITY, 6, true);
        itemMeta.addEnchant(Enchantment.DIG_SPEED, 6, true);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.LOOT_BONUS_BLOCKS), is(false));

        itemMeta.addEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingEnchant(Enchantment.LOOT_BONUS_BLOCKS), is(true));
        assertThat(itemMeta.hasConflictingEnchant(null), is(false));
    }

    @Test
    public void testConflictingStoredEnchantments() {
        EnchantmentStorageMeta itemMeta = (EnchantmentStorageMeta) Bukkit.getItemFactory().getItemMeta(Material.ENCHANTED_BOOK);
        itemMeta.addStoredEnchant(Enchantment.DURABILITY, 6, true);
        itemMeta.addStoredEnchant(Enchantment.DIG_SPEED, 6, true);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS), is(false));

        itemMeta.addStoredEnchant(Enchantment.SILK_TOUCH, 1, false);
        assertThat(itemMeta.hasConflictingStoredEnchant(Enchantment.LOOT_BONUS_BLOCKS), is(true));
        assertThat(itemMeta.hasConflictingStoredEnchant(null), is(false));
    }

    private static FireworkMeta newFireworkMeta() {
        return ((FireworkMeta) Bukkit.getItemFactory().getItemMeta(Material.FIREWORK_ROCKET));
    }

    @Test
    public void testCrazyEquality() {
        CraftItemStack craft = CraftItemStack.asCraftCopy(new ItemStack(Material.STONE));
        craft.setItemMeta(craft.getItemMeta());
        ItemStack bukkit = new ItemStack(craft);
        assertThat(craft, is(bukkit));
        assertThat(bukkit, is((ItemStack) craft));
    }

    @Test
    public void testTaggedButNotMeta() {
        CraftItemStack craft = CraftItemStack.asCraftCopy(new ItemStack(Material.SHEARS));
        craft.handle.setDamage(0);

        assertThat("Should have NBT tag", CraftItemStack.hasItemMeta(craft.handle), is(true));
        assertThat("NBT Tag should contain Damage", craft.handle.getTag().get("Damage"), instanceOf(NBTTagInt.class));
        assertThat("But we should not have meta", craft.hasItemMeta(), is(false));

        ItemStack pureBukkit = new ItemStack(Material.SHEARS);
        assertThat("Bukkit and craft stacks should be similar", craft.isSimilar(pureBukkit), is(true));
        assertThat("Bukkit and craft stacks should be equal", craft.equals(pureBukkit), is(true));
    }

    @Test
    public void testBlockStateMeta() {
        List<Block> queue = new ArrayList<>();

        for (Item item : (Iterable<Item>) IRegistry.ITEM) { // Eclipse fail
            if (item instanceof ItemBlock) {
                queue.add(((ItemBlock) item).getBlock());
            }
            if (item instanceof ItemBlockWallable) {
                queue.add(((ItemBlockWallable) item).wallBlock);
            }
        }

        for (Block block : queue) {
            if (block != null) {
                if (block instanceof ITileEntity) {
                    ItemStack stack = CraftItemStack.asNewCraftStack(Item.getItemOf(block));

                    // Command blocks aren't unit testable atm
                    if (stack.getType() == Material.COMMAND_BLOCK || stack.getType() == Material.CHAIN_COMMAND_BLOCK || stack.getType() == Material.REPEATING_COMMAND_BLOCK) {
                        return;
                    }

                    ItemMeta meta = stack.getItemMeta();
                    assertTrue(stack + " has meta of type " + meta + " expected BlockStateMeta", meta instanceof BlockStateMeta);

                    BlockStateMeta blockState = (BlockStateMeta) meta;
                    assertNotNull(stack + " has null block state", blockState.getBlockState());

                    blockState.setBlockState(blockState.getBlockState());
                }
            }
        }
    }

    @Test
    public void testEachExtraData() {
        final List<StackProvider> providers = Arrays.asList(
            new StackProvider(Material.WRITABLE_BOOK) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final BookMeta meta = (BookMeta) cleanStack.getItemMeta();
                    meta.setAuthor("Some author");
                    meta.setPages("Page 1", "Page 2");
                    meta.setTitle("A title");
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.WRITTEN_BOOK) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final BookMeta meta = (BookMeta) cleanStack.getItemMeta();
                    meta.setAuthor("Some author");
                    meta.setPages("Page 1", "Page 2");
                    meta.setTitle("A title");
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            /* Skulls rely on a running server instance
            new StackProvider(Material.SKULL_ITEM) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final SkullMeta meta = (SkullMeta) cleanStack.getItemMeta();
                    meta.setOwner("Notch");
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            */
            new StackProvider(Material.FILLED_MAP) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final MapMeta meta = (MapMeta) cleanStack.getItemMeta();
                    meta.setScaling(true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.LEATHER_BOOTS) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final LeatherArmorMeta meta = (LeatherArmorMeta) cleanStack.getItemMeta();
                    meta.setColor(Color.FUCHSIA);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.POTION) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final PotionMeta meta = (PotionMeta) cleanStack.getItemMeta();
                    meta.setBasePotionData(new PotionData(PotionType.UNCRAFTABLE, false, false));
                    meta.addCustomEffect(PotionEffectType.CONFUSION.createEffect(1, 1), false);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.FIREWORK_ROCKET) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final FireworkMeta meta = (FireworkMeta) cleanStack.getItemMeta();
                    meta.addEffect(FireworkEffect.builder().withColor(Color.GREEN).withFade(Color.OLIVE).with(Type.BALL_LARGE).build());
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.ENCHANTED_BOOK) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final EnchantmentStorageMeta meta = (EnchantmentStorageMeta) cleanStack.getItemMeta();
                    meta.addStoredEnchant(Enchantment.ARROW_FIRE, 1, true);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.FIREWORK_STAR) {
                @Override ItemStack operate(final ItemStack cleanStack) {
                    final FireworkEffectMeta meta = (FireworkEffectMeta) cleanStack.getItemMeta();
                    meta.setEffect(FireworkEffect.builder().withColor(Color.MAROON, Color.BLACK).with(Type.CREEPER).withFlicker().build());
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.WHITE_BANNER) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final BannerMeta meta = (BannerMeta) cleanStack.getItemMeta();
                    meta.setBaseColor(DyeColor.CYAN);
                    meta.addPattern(new Pattern(DyeColor.WHITE, PatternType.BRICKS));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            /* No distinguishing features, add back with virtual entity API
            new StackProvider(Material.ZOMBIE_SPAWN_EGG) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final SpawnEggMeta meta = (SpawnEggMeta) cleanStack.getItemMeta();
                    meta.setSpawnedType(EntityType.ZOMBIE);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            */
            new StackProvider(Material.KNOWLEDGE_BOOK) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final KnowledgeBookMeta meta = (KnowledgeBookMeta) cleanStack.getItemMeta();
                    meta.addRecipe(new NamespacedKey("minecraft", "test"), new NamespacedKey("plugin", "test"));
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            },
            new StackProvider(Material.TROPICAL_FISH_BUCKET) {
                @Override ItemStack operate(ItemStack cleanStack) {
                    final TropicalFishBucketMeta meta = (TropicalFishBucketMeta) cleanStack.getItemMeta();
                    meta.setBodyColor(DyeColor.ORANGE);
                    meta.setPatternColor(DyeColor.BLACK);
                    meta.setPattern(TropicalFish.Pattern.DASHER);
                    cleanStack.setItemMeta(meta);
                    return cleanStack;
                }
            }
        );

        assertThat("Forgotten test?", providers, hasSize(ItemStackTest.COMPOUND_MATERIALS.length - 4/* Normal item meta, skulls, eggs and tile entities */));

        for (final StackProvider provider : providers) {
            downCastTest(new BukkitWrapper(provider));
            downCastTest(new CraftWrapper(provider));
        }
    }

    private void downCastTest(final StackWrapper provider) {
        final String name = provider.toString();
        final ItemStack blank = new ItemStack(Material.STONE);
        final ItemStack craftBlank = CraftItemStack.asCraftCopy(blank);

        downCastTest(name, provider.stack(), blank);
        blank.setItemMeta(blank.getItemMeta());
        downCastTest(name, provider.stack(), blank);

        downCastTest(name, provider.stack(), craftBlank);
        craftBlank.setItemMeta(craftBlank.getItemMeta());
        downCastTest(name, provider.stack(), craftBlank);
    }

    private void downCastTest(final String name, final ItemStack stack, final ItemStack blank) {
        assertThat(name, stack, is(not(blank)));
        assertThat(name, stack.getItemMeta(), is(not(blank.getItemMeta())));

        stack.setType(Material.STONE);

        assertThat(name, stack, is(blank));
    }
}
