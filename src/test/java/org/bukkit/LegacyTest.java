package org.bukkit;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.craftbukkit.util.CraftLegacy;
import org.bukkit.material.MaterialData;
import org.bukkit.support.AbstractTestingBase;
import org.junit.Assert;
import org.junit.Test;

public class LegacyTest extends AbstractTestingBase {

    private final Set<Material> INVALIDATED_MATERIALS = new HashSet<>(Arrays.asList(Material.ACACIA_BUTTON, Material.ACACIA_PRESSURE_PLATE, Material.ACACIA_TRAPDOOR, Material.AIR, Material.ATTACHED_MELON_STEM, Material.ATTACHED_PUMPKIN_STEM,
            Material.BIRCH_BUTTON, Material.BIRCH_PRESSURE_PLATE, Material.BIRCH_TRAPDOOR, Material.BLACK_WALL_BANNER, Material.BLUE_WALL_BANNER, Material.BROWN_WALL_BANNER, Material.BUBBLE_COLUMN, Material.CAVE_AIR, Material.CREEPER_WALL_HEAD,
            Material.CYAN_WALL_BANNER, Material.DARK_OAK_BUTTON, Material.DARK_OAK_PRESSURE_PLATE, Material.DARK_OAK_TRAPDOOR, Material.DARK_PRISMARINE_SLAB, Material.DARK_PRISMARINE_STAIRS, Material.DEBUG_STICK, Material.DONKEY_SPAWN_EGG,
            Material.DRAGON_WALL_HEAD, Material.DRIED_KELP, Material.DRIED_KELP_BLOCK, Material.ELDER_GUARDIAN_SPAWN_EGG, Material.EVOKER_SPAWN_EGG, Material.GRAY_WALL_BANNER, Material.GREEN_WALL_BANNER, Material.HUSK_SPAWN_EGG,
            Material.JUNGLE_BUTTON, Material.JUNGLE_PRESSURE_PLATE, Material.JUNGLE_TRAPDOOR, Material.KELP, Material.KELP_PLANT, Material.LIGHT_BLUE_WALL_BANNER, Material.LIGHT_GRAY_WALL_BANNER, Material.LIME_WALL_BANNER, Material.LLAMA_SPAWN_EGG,
            Material.MAGENTA_WALL_BANNER, Material.MULE_SPAWN_EGG, Material.ORANGE_WALL_BANNER, Material.PARROT_SPAWN_EGG, Material.PHANTOM_SPAWN_EGG, Material.PINK_WALL_BANNER, Material.PLAYER_WALL_HEAD, Material.POLAR_BEAR_SPAWN_EGG,
            Material.POTTED_ACACIA_SAPLING, Material.POTTED_ALLIUM, Material.POTTED_AZURE_BLUET, Material.POTTED_BIRCH_SAPLING, Material.POTTED_BLUE_ORCHID, Material.POTTED_BROWN_MUSHROOM, Material.POTTED_DANDELION, Material.POTTED_DARK_OAK_SAPLING,
            Material.POTTED_DEAD_BUSH, Material.POTTED_FERN, Material.POTTED_JUNGLE_SAPLING, Material.POTTED_OAK_SAPLING, Material.POTTED_ORANGE_TULIP, Material.POTTED_OXEYE_DAISY, Material.POTTED_PINK_TULIP, Material.POTTED_POPPY,
            Material.POTTED_RED_MUSHROOM, Material.POTTED_RED_TULIP, Material.POTTED_SPRUCE_SAPLING, Material.POTTED_WHITE_TULIP, Material.PRISMARINE_BRICK_SLAB, Material.PRISMARINE_BRICK_STAIRS, Material.PRISMARINE_SLAB, Material.PRISMARINE_STAIRS,
            Material.PUMPKIN, Material.PURPLE_WALL_BANNER, Material.RED_WALL_BANNER, Material.SEAGRASS, Material.SKELETON_HORSE_SPAWN_EGG, Material.SKELETON_WALL_SKULL, Material.SPRUCE_BUTTON, Material.SPRUCE_PRESSURE_PLATE, Material.SPRUCE_TRAPDOOR,
            Material.STRAY_SPAWN_EGG, Material.STRIPPED_ACACIA_LOG, Material.STRIPPED_BIRCH_LOG, Material.STRIPPED_DARK_OAK_LOG, Material.STRIPPED_JUNGLE_LOG, Material.STRIPPED_OAK_LOG, Material.STRIPPED_SPRUCE_LOG, Material.TALL_SEAGRASS,
            Material.TRIDENT, Material.TURTLE_EGG, Material.TURTLE_HELMET, Material.SCUTE, Material.TURTLE_SPAWN_EGG, Material.VEX_SPAWN_EGG, Material.VINDICATOR_SPAWN_EGG, Material.VOID_AIR, Material.WHITE_BED,
            Material.WITHER_SKELETON_SPAWN_EGG, Material.WITHER_SKELETON_WALL_SKULL, Material.YELLOW_WALL_BANNER, Material.ZOMBIE_HORSE_SPAWN_EGG, Material.ZOMBIE_VILLAGER_SPAWN_EGG, Material.ZOMBIE_WALL_HEAD,
            Material.COD_BUCKET, Material.COD_SPAWN_EGG, Material.PUFFERFISH_BUCKET, Material.PUFFERFISH_SPAWN_EGG, Material.SALMON_BUCKET, Material.SALMON_SPAWN_EGG,
            Material.TROPICAL_FISH_BUCKET, Material.DROWNED_SPAWN_EGG, Material.SHULKER_BOX, Material.TROPICAL_FISH_SPAWN_EGG,
            Material.BLUE_ICE, Material.BRAIN_CORAL, Material.BRAIN_CORAL_BLOCK, Material.BRAIN_CORAL_FAN, Material.BUBBLE_CORAL, Material.BUBBLE_CORAL_BLOCK, Material.BUBBLE_CORAL_FAN, Material.CONDUIT, Material.DEAD_BRAIN_CORAL_BLOCK,
            Material.DEAD_BUBBLE_CORAL_BLOCK, Material.DEAD_FIRE_CORAL_BLOCK, Material.DEAD_HORN_CORAL_BLOCK, Material.DEAD_TUBE_CORAL_BLOCK, Material.DOLPHIN_SPAWN_EGG, Material.FIRE_CORAL, Material.FIRE_CORAL_BLOCK, Material.FIRE_CORAL_FAN,
            Material.HEART_OF_THE_SEA, Material.HORN_CORAL, Material.HORN_CORAL_BLOCK, Material.HORN_CORAL_FAN, Material.NAUTILUS_SHELL, Material.PHANTOM_MEMBRANE, Material.SEA_PICKLE, Material.TUBE_CORAL, Material.TUBE_CORAL_BLOCK,
            Material.TUBE_CORAL_FAN, Material.STRIPPED_ACACIA_WOOD, Material.STRIPPED_BIRCH_WOOD, Material.STRIPPED_DARK_OAK_WOOD, Material.STRIPPED_JUNGLE_WOOD, Material.STRIPPED_OAK_WOOD, Material.STRIPPED_SPRUCE_WOOD,
            Material.ACACIA_WOOD, Material.BIRCH_WOOD, Material.DARK_OAK_WOOD, Material.JUNGLE_WOOD, Material.OAK_WOOD, Material.SPRUCE_WOOD,
            Material.TUBE_CORAL_WALL_FAN, Material.BRAIN_CORAL_WALL_FAN, Material.BUBBLE_CORAL_WALL_FAN, Material.FIRE_CORAL_WALL_FAN, Material.HORN_CORAL_WALL_FAN, Material.DEAD_TUBE_CORAL_WALL_FAN, Material.DEAD_BRAIN_CORAL_WALL_FAN,
            Material.DEAD_BUBBLE_CORAL_WALL_FAN, Material.DEAD_FIRE_CORAL_WALL_FAN, Material.DEAD_HORN_CORAL_WALL_FAN, Material.DEAD_TUBE_CORAL_FAN, Material.DEAD_BRAIN_CORAL_FAN, Material.DEAD_BUBBLE_CORAL_FAN, Material.DEAD_FIRE_CORAL_FAN,
            Material.DEAD_HORN_CORAL_FAN,
            //
            Material.LEGACY_AIR, Material.LEGACY_DEAD_BUSH, Material.LEGACY_BURNING_FURNACE, Material.LEGACY_WALL_SIGN, Material.LEGACY_REDSTONE_TORCH_OFF, Material.LEGACY_SKULL, Material.LEGACY_REDSTONE_COMPARATOR_ON, Material.LEGACY_WALL_BANNER, Material.LEGACY_MONSTER_EGG));

    private final Set<Material> INVERSION_FAILS = new HashSet<>(Arrays.asList(Material.LEGACY_DOUBLE_STEP, Material.LEGACY_GLOWING_REDSTONE_ORE, Material.LEGACY_DIODE_BLOCK_ON, Material.LEGACY_REDSTONE_LAMP_ON, Material.LEGACY_WOOD_DOUBLE_STEP,
            Material.LEGACY_DAYLIGHT_DETECTOR_INVERTED, Material.LEGACY_DOUBLE_STONE_SLAB2, Material.LEGACY_PURPUR_DOUBLE_SLAB, Material.LEGACY_WHEAT, Material.LEGACY_SIGN, Material.LEGACY_WOOD_DOOR, Material.LEGACY_IRON_DOOR, Material.LEGACY_SUGAR_CANE,
            Material.LEGACY_CAKE, Material.LEGACY_BED, Material.LEGACY_DIODE, Material.LEGACY_NETHER_STALK, Material.LEGACY_BREWING_STAND_ITEM, Material.LEGACY_CAULDRON_ITEM, Material.LEGACY_REDSTONE_COMPARATOR, Material.LEGACY_SPRUCE_DOOR_ITEM,
            Material.LEGACY_BIRCH_DOOR_ITEM, Material.LEGACY_JUNGLE_DOOR_ITEM, Material.LEGACY_ACACIA_DOOR_ITEM, Material.LEGACY_DARK_OAK_DOOR_ITEM, Material.LEGACY_STATIONARY_LAVA, Material.LEGACY_STATIONARY_WATER));

    @Test
    public void toLegacyMaterial() {
        for (Material material : Material.values()) {
            if (!INVALIDATED_MATERIALS.contains(material) && !material.isLegacy()) {
                MaterialData converted = CraftLegacy.toLegacyData(material);

                Assert.assertNotEquals("Could not toLegacy " + material, Material.LEGACY_AIR, converted.getItemType());

                if (!INVALIDATED_MATERIALS.contains(converted.getItemType())) {
                    Assert.assertNotEquals("Could not fromLegacy(toLegacy) " + converted + "(" + material + ")", Material.AIR, CraftLegacy.fromLegacy(converted));
                }
                if (!INVERSION_FAILS.contains(material)) {
                    Assert.assertEquals("Could not fromLegacy(toLegacy) " + converted + "(" + material + ")", material, CraftLegacy.fromLegacy(converted));
                }
            }
        }

        Assert.assertEquals("Could not toLegacy Air", Material.LEGACY_AIR, CraftLegacy.toLegacy(Material.AIR));
    }

    @Test
    public void fromLegacyMaterial() {
        for (Material material : Material.values()) {
            if (!INVALIDATED_MATERIALS.contains(material) && material.isLegacy()) {
                Material converted = CraftLegacy.fromLegacy(material);
                Assert.assertNotEquals("Could not fromLegacy " + material, Material.AIR, converted);

                Assert.assertNotEquals("Could not toLegacy(fromLegacy) " + converted + "(" + material + ")", Material.AIR, CraftLegacy.toLegacy(converted));
                if (!INVERSION_FAILS.contains(material)) {
                    Assert.assertEquals("Could not toLegacy(fromLegacy) " + converted + "(" + material + ")", material, CraftLegacy.toLegacy(converted));
                }
            }
        }

        Assert.assertEquals("Could not fromLegacy Air", Material.AIR, CraftLegacy.fromLegacy(Material.LEGACY_AIR));
    }

    @Test
    public void testRestricted() {
        for (Material material : CraftLegacy.values()) {
            Assert.assertTrue("Must iterate only legacy materials", material.isLegacy());
        }

        for (Material material : CraftLegacy.modern_values()) {
            Assert.assertFalse("Must iterate only modern materials", material.isLegacy());
        }
    }
}
