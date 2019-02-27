package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixer;
import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;

public class DataConverterRegistry {

    private static final BiFunction<Integer, Schema, Schema> a = Schema::new;
    private static final BiFunction<Integer, Schema, Schema> b = DataConverterSchemaNamed::new;
    private static final DataFixer c = b();

    private static DataFixer b() {
        DataFixerBuilder datafixerbuilder = new DataFixerBuilder(1631);

        a(datafixerbuilder);
        return datafixerbuilder.build(ForkJoinPool.commonPool());
    }

    public static DataFixer a() {
        return DataConverterRegistry.c;
    }

    private static void a(DataFixerBuilder datafixerbuilder) {
        Schema schema = datafixerbuilder.addSchema(99, DataConverterSchemaV99::new);
        Schema schema1 = datafixerbuilder.addSchema(100, DataConverterSchemaV100::new);

        datafixerbuilder.addFixer(new DataConverterEquipment(schema1, true));
        Schema schema2 = datafixerbuilder.addSchema(101, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterSignText(schema2, false));
        Schema schema3 = datafixerbuilder.addSchema(102, DataConverterSchemaV102::new);

        datafixerbuilder.addFixer(new DataConverterMaterialId(schema3, true));
        datafixerbuilder.addFixer(new DataConverterPotionId(schema3, false));
        Schema schema4 = datafixerbuilder.addSchema(105, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterSpawnEgg(schema4, true));
        Schema schema5 = datafixerbuilder.addSchema(106, DataConverterSchemaV106::new);

        datafixerbuilder.addFixer(new DataConverterMobSpawner(schema5, true));
        Schema schema6 = datafixerbuilder.addSchema(107, DataConverterSchemaV107::new);

        datafixerbuilder.addFixer(new DataConverterMinecart(schema6, true));
        Schema schema7 = datafixerbuilder.addSchema(108, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterUUID(schema7, true));
        Schema schema8 = datafixerbuilder.addSchema(109, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterHealth(schema8, true));
        Schema schema9 = datafixerbuilder.addSchema(110, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterSaddle(schema9, true));
        Schema schema10 = datafixerbuilder.addSchema(111, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterHanging(schema10, true));
        Schema schema11 = datafixerbuilder.addSchema(113, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterDropChances(schema11, true));
        Schema schema12 = datafixerbuilder.addSchema(135, DataConverterSchemaV135::new);

        datafixerbuilder.addFixer(new DataConverterRiding(schema12, true));
        Schema schema13 = datafixerbuilder.addSchema(143, DataConverterSchemaV143::new);

        datafixerbuilder.addFixer(new DataConverterEntityTippedArrow(schema13, true));
        Schema schema14 = datafixerbuilder.addSchema(147, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterArmorStand(schema14, true));
        Schema schema15 = datafixerbuilder.addSchema(165, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterBook(schema15, true));
        Schema schema16 = datafixerbuilder.addSchema(501, DataConverterSchemaV501::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema16, "Add 1.10 entities fix", DataConverterTypes.ENTITY));
        Schema schema17 = datafixerbuilder.addSchema(502, DataConverterRegistry.a);

        datafixerbuilder.addFixer(DataConverterItemName.a(schema17, "cooked_fished item renamer", (s) -> {
            return Objects.equals(DataConverterSchemaNamed.a(s), "minecraft:cooked_fished") ? "minecraft:cooked_fish" : s;
        }));
        datafixerbuilder.addFixer(new DataConverterZombie(schema17, false));
        Schema schema18 = datafixerbuilder.addSchema(505, DataConverterRegistry.a);

        datafixerbuilder.addFixer(new DataConverterVBO(schema18, false));
        Schema schema19 = datafixerbuilder.addSchema(700, DataConverterSchemaV700::new);

        datafixerbuilder.addFixer(new DataConverterGuardian(schema19, true));
        Schema schema20 = datafixerbuilder.addSchema(701, DataConverterSchemaV701::new);

        datafixerbuilder.addFixer(new DataConverterSkeleton(schema20, true));
        Schema schema21 = datafixerbuilder.addSchema(702, DataConverterSchemaV702::new);

        datafixerbuilder.addFixer(new DataConverterZombieType(schema21, true));
        Schema schema22 = datafixerbuilder.addSchema(703, DataConverterSchemaV703::new);

        datafixerbuilder.addFixer(new DataConverterHorse(schema22, true));
        Schema schema23 = datafixerbuilder.addSchema(704, DataConverterSchemaV704::new);

        datafixerbuilder.addFixer(new DataConverterTileEntity(schema23, true));
        Schema schema24 = datafixerbuilder.addSchema(705, DataConverterSchemaV705::new);

        datafixerbuilder.addFixer(new DataConverterEntity(schema24, true));
        Schema schema25 = datafixerbuilder.addSchema(804, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterBanner(schema25, true));
        Schema schema26 = datafixerbuilder.addSchema(806, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterPotionWater(schema26, false));
        Schema schema27 = datafixerbuilder.addSchema(808, DataConverterSchemaV808::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema27, "added shulker box", DataConverterTypes.j));
        Schema schema28 = datafixerbuilder.addSchema(808, 1, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterShulker(schema28, false));
        Schema schema29 = datafixerbuilder.addSchema(813, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterShulkerBoxItem(schema29, false));
        datafixerbuilder.addFixer(new DataConverterShulkerBoxBlock(schema29, false));
        Schema schema30 = datafixerbuilder.addSchema(816, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterLang(schema30, false));
        Schema schema31 = datafixerbuilder.addSchema(820, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterItemName.a(schema31, "totem item renamer", (s) -> {
            return Objects.equals(s, "minecraft:totem") ? "minecraft:totem_of_undying" : s;
        }));
        Schema schema32 = datafixerbuilder.addSchema(1022, DataConverterSchemaV1022::new);

        datafixerbuilder.addFixer(new DataConverterShoulderEntity(schema32, "added shoulder entities to players", DataConverterTypes.PLAYER));
        Schema schema33 = datafixerbuilder.addSchema(1125, DataConverterSchemaV1125::new);

        datafixerbuilder.addFixer(new DataConverterBedBlock(schema33, true));
        datafixerbuilder.addFixer(new DataConverterBedItem(schema33, false));
        Schema schema34 = datafixerbuilder.addSchema(1344, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterKeybind(schema34, false));
        Schema schema35 = datafixerbuilder.addSchema(1446, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterKeybind2(schema35, false));
        Schema schema36 = datafixerbuilder.addSchema(1450, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterFlattenState(schema36, false));
        Schema schema37 = datafixerbuilder.addSchema(1451, DataConverterSchemaV1451::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema37, "AddTrappedChestFix", DataConverterTypes.j));
        Schema schema38 = datafixerbuilder.addSchema(1451, 1, DataConverterSchemaV1451_1::new);

        datafixerbuilder.addFixer(new ChunkConverterPalette(schema38, true));
        Schema schema39 = datafixerbuilder.addSchema(1451, 2, DataConverterSchemaV1451_2::new);

        datafixerbuilder.addFixer(new DataConverterPiston(schema39, true));
        Schema schema40 = datafixerbuilder.addSchema(1451, 3, DataConverterSchemaV1451_3::new);

        datafixerbuilder.addFixer(new DataConverterEntityBlockState(schema40, true));
        datafixerbuilder.addFixer(new DataConverterMap(schema40, false));
        Schema schema41 = datafixerbuilder.addSchema(1451, 4, DataConverterSchemaV1451_4::new);

        datafixerbuilder.addFixer(new DataConverterBlockName(schema41, true));
        datafixerbuilder.addFixer(new DataConverterFlatten(schema41, false));
        Schema schema42 = datafixerbuilder.addSchema(1451, 5, DataConverterSchemaV1451_5::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema42, "RemoveNoteBlockFlowerPotFix", DataConverterTypes.j));
        datafixerbuilder.addFixer(new DataConverterFlattenSpawnEgg(schema42, false));
        datafixerbuilder.addFixer(new DataConverterWolf(schema42, false));
        datafixerbuilder.addFixer(new DataConverterBannerColour(schema42, false));
        datafixerbuilder.addFixer(new DataConverterWorldGenSettings(schema42, false));
        Schema schema43 = datafixerbuilder.addSchema(1451, 6, DataConverterSchemaV1451_6::new);

        datafixerbuilder.addFixer(new DataConverterStatistic(schema43, true));
        datafixerbuilder.addFixer(new DataConverterJukeBox(schema43, false));
        Schema schema44 = datafixerbuilder.addSchema(1451, 7, DataConverterSchemaV1451_7::new);

        datafixerbuilder.addFixer(new DataConverterVillage(schema44, true));
        Schema schema45 = datafixerbuilder.addSchema(1451, 7, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterVillagerTrade(schema45, false));
        Schema schema46 = datafixerbuilder.addSchema(1456, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterItemFrame(schema46, false));
        Schema schema47 = datafixerbuilder.addSchema(1458, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterCustomNameEntity(schema47, false));
        datafixerbuilder.addFixer(new DataConverterCustomNameItem(schema47, false));
        datafixerbuilder.addFixer(new DataConverterCustomNameTile(schema47, false));
        Schema schema48 = datafixerbuilder.addSchema(1460, DataConverterSchemaV1460::new);

        datafixerbuilder.addFixer(new DataConverterPainting(schema48, false));
        Schema schema49 = datafixerbuilder.addSchema(1466, DataConverterSchemaV1466::new);

        datafixerbuilder.addFixer(new DataConverterProtoChunk(schema49, true));
        Schema schema50 = datafixerbuilder.addSchema(1470, DataConverterSchemaV1470::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema50, "Add 1.13 entities fix", DataConverterTypes.ENTITY));
        Schema schema51 = datafixerbuilder.addSchema(1474, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterColorlessShulkerEntity(schema51, false));
        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema51, "Colorless shulker block fixer", (s) -> {
            return Objects.equals(DataConverterSchemaNamed.a(s), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : s;
        }));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema51, "Colorless shulker item fixer", (s) -> {
            return Objects.equals(DataConverterSchemaNamed.a(s), "minecraft:purple_shulker_box") ? "minecraft:shulker_box" : s;
        }));
        Schema schema52 = datafixerbuilder.addSchema(1475, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema52, "Flowing fixer", (s) -> {
            return (String) ImmutableMap.of("minecraft:flowing_water", "minecraft:water", "minecraft:flowing_lava", "minecraft:lava").getOrDefault(s, s);
        }));
        Schema schema53 = datafixerbuilder.addSchema(1480, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema53, "Rename coral blocks", (s) -> {
            return (String) DataConverterCoral.a.getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema53, "Rename coral items", (s) -> {
            return (String) DataConverterCoral.a.getOrDefault(s, s);
        }));
        Schema schema54 = datafixerbuilder.addSchema(1481, DataConverterSchemaV1481::new);

        datafixerbuilder.addFixer(new DataConverterAddChoices(schema54, "Add conduit", DataConverterTypes.j));
        Schema schema55 = datafixerbuilder.addSchema(1483, DataConverterSchemaV1483::new);

        datafixerbuilder.addFixer(new DataConverterEntityPufferfish(schema55, true));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema55, "Rename pufferfish egg item", (s) -> {
            return (String) DataConverterEntityPufferfish.a.getOrDefault(s, s);
        }));
        Schema schema56 = datafixerbuilder.addSchema(1484, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterItemName.a(schema56, "Rename seagrass items", (s) -> {
            return (String) ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema56, "Rename seagrass blocks", (s) -> {
            return (String) ImmutableMap.of("minecraft:sea_grass", "minecraft:seagrass", "minecraft:tall_sea_grass", "minecraft:tall_seagrass").getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(new DataConverterHeightmapRenaming(schema56, false));
        Schema schema57 = datafixerbuilder.addSchema(1486, DataConverterSchemaV1486::new);

        datafixerbuilder.addFixer(new DataConverterEntityCodSalmon(schema57, true));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema57, "Rename cod/salmon egg items", (s) -> {
            return (String) DataConverterEntityCodSalmon.b.getOrDefault(s, s);
        }));
        Schema schema58 = datafixerbuilder.addSchema(1487, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterItemName.a(schema58, "Rename prismarine_brick(s)_* blocks", (s) -> {
            return (String) ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs").getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema58, "Rename prismarine_brick(s)_* items", (s) -> {
            return (String) ImmutableMap.of("minecraft:prismarine_bricks_slab", "minecraft:prismarine_brick_slab", "minecraft:prismarine_bricks_stairs", "minecraft:prismarine_brick_stairs").getOrDefault(s, s);
        }));
        Schema schema59 = datafixerbuilder.addSchema(1488, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema59, "Rename kelp/kelptop", (s) -> {
            return (String) ImmutableMap.of("minecraft:kelp_top", "minecraft:kelp", "minecraft:kelp", "minecraft:kelp_plant").getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema59, "Rename kelptop", (s) -> {
            return Objects.equals(s, "minecraft:kelp_top") ? "minecraft:kelp" : s;
        }));
        datafixerbuilder.addFixer(new DataConverterNamedEntity(schema59, false, "Command block block entity custom name fix", DataConverterTypes.j, "minecraft:command_block") {
            protected Typed<?> a(Typed<?> typed) {
                return typed.update(DSL.remainderFinder(), DataConverterCustomNameEntity::a);
            }
        });
        datafixerbuilder.addFixer(new DataConverterNamedEntity(schema59, false, "Command block minecart custom name fix", DataConverterTypes.ENTITY, "minecraft:commandblock_minecart") {
            protected Typed<?> a(Typed<?> typed) {
                return typed.update(DSL.remainderFinder(), DataConverterCustomNameEntity::a);
            }
        });
        datafixerbuilder.addFixer(new DataConverterIglooMetadataRemoval(schema59, false));
        Schema schema60 = datafixerbuilder.addSchema(1490, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema60, "Rename melon_block", (s) -> {
            return Objects.equals(s, "minecraft:melon_block") ? "minecraft:melon" : s;
        }));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema60, "Rename melon_block/melon/speckled_melon", (s) -> {
            return (String) ImmutableMap.of("minecraft:melon_block", "minecraft:melon", "minecraft:melon", "minecraft:melon_slice", "minecraft:speckled_melon", "minecraft:glistering_melon_slice").getOrDefault(s, s);
        }));
        Schema schema61 = datafixerbuilder.addSchema(1492, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterChunkStructuresTemplateRename(schema61, false));
        Schema schema62 = datafixerbuilder.addSchema(1494, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterItemStackEnchantment(schema62, false));
        Schema schema63 = datafixerbuilder.addSchema(1496, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterLeaves(schema63, false));
        Schema schema64 = datafixerbuilder.addSchema(1500, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterBlockEntityKeepPacked(schema64, false));
        Schema schema65 = datafixerbuilder.addSchema(1501, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterAdvancement(schema65, false));
        Schema schema66 = datafixerbuilder.addSchema(1502, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterRecipes(schema66, false));
        Schema schema67 = datafixerbuilder.addSchema(1506, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterLevelDataGeneratorOptions(schema67, false));
        Schema schema68 = datafixerbuilder.addSchema(1508, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterBiome(schema68, false));
        Schema schema69 = datafixerbuilder.addSchema(1510, DataConverterSchemaV1510::new);

        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema69, "Block renamening fix", (s) -> {
            return (String) DataConverterEntityRename.b.getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(DataConverterItemName.a(schema69, "Item renamening fix", (s) -> {
            return (String) DataConverterEntityRename.c.getOrDefault(s, s);
        }));
        datafixerbuilder.addFixer(new DataConverterRecipeRename(schema69, false));
        datafixerbuilder.addFixer(new DataConverterEntityRename(schema69, true));
        datafixerbuilder.addFixer(new DataConverterSwimStats(schema69, false));
        Schema schema70 = datafixerbuilder.addSchema(1514, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterObjectiveDisplayName(schema70, false));
        datafixerbuilder.addFixer(new DataConverterTeamDisplayName(schema70, false));
        datafixerbuilder.addFixer(new DataConverterObjectiveRenderType(schema70, false));
        Schema schema71 = datafixerbuilder.addSchema(1515, DataConverterRegistry.b);

        datafixerbuilder.addFixer(DataConverterBlockRename.a(schema71, "Rename coral fan blocks", (s) -> {
            return (String) DataConverterCoralFan.a.getOrDefault(s, s);
        }));
        Schema schema72 = datafixerbuilder.addSchema(1624, DataConverterRegistry.b);

        datafixerbuilder.addFixer(new DataConverterTrappedChest(schema72, false));
    }
}
