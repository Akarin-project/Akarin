/*
 * Copyright (c) 2018 Daniel Ennis (Aikar) MIT License
 */

package com.destroystokyo.paper;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MaterialSetTag implements Tag<Material> {

    private final NamespacedKey key;
    private final Set<Material> materials;

    /**
     * @deprecated Use NamespacedKey version of constructor
     */
    @Deprecated
    public MaterialSetTag(@NotNull Predicate<Material> filter) {
        this(null, Stream.of(Material.values()).filter(filter).collect(Collectors.toList()));
    }

    /**
     * @deprecated Use NamespacedKey version of constructor
     */
    @Deprecated
    public MaterialSetTag(@NotNull Collection<Material> materials) {
        this(null, materials);
    }

    /**
     * @deprecated Use NamespacedKey version of constructor
     */
    @Deprecated
    public MaterialSetTag(@NotNull Material... materials) {
        this(null, materials);
    }

    public MaterialSetTag(@Nullable NamespacedKey key, @NotNull Predicate<Material> filter) {
        this(key, Stream.of(Material.values()).filter(filter).collect(Collectors.toList()));
    }

    public MaterialSetTag(@Nullable NamespacedKey key, @NotNull Material... materials) {
        this(key, Lists.newArrayList(materials));
    }

    public MaterialSetTag(@Nullable NamespacedKey key, @NotNull Collection<Material> materials) {
        this.key = key != null ? key : NamespacedKey.randomKey();
        this.materials = Sets.newEnumSet(materials, Material.class);
    }

    @NotNull
    @Override
    public NamespacedKey getKey() {
        return key;
    }

    @NotNull
    public MaterialSetTag add(@NotNull Tag<Material>... tags) {
        for (Tag<Material> tag : tags) {
            add(tag.getValues());
        }
        return this;
    }

    @NotNull
    public MaterialSetTag add(@NotNull MaterialSetTag... tags) {
        for (Tag<Material> tag : tags) {
            add(tag.getValues());
        }
        return this;
    }

    @NotNull
    public MaterialSetTag add(@NotNull Material... material) {
        this.materials.addAll(Lists.newArrayList(material));
        return this;
    }

    @NotNull
    public MaterialSetTag add(@NotNull Collection<Material> materials) {
        this.materials.addAll(materials);
        return this;
    }

    @NotNull
    public MaterialSetTag contains(@NotNull String with) {
        return add(mat -> mat.name().contains(with));
    }

    @NotNull
    public MaterialSetTag endsWith(@NotNull String with) {
        return add(mat -> mat.name().endsWith(with));
    }


    @NotNull
    public MaterialSetTag startsWith(@NotNull String with) {
        return add(mat -> mat.name().startsWith(with));
    }
    @NotNull
    public MaterialSetTag add(@NotNull Predicate<Material> filter) {
        add(Stream.of(Material.values()).filter(((Predicate<Material>) Material::isLegacy).negate()).filter(filter).collect(Collectors.toList()));
        return this;
    }

    @NotNull
    public MaterialSetTag not(@NotNull MaterialSetTag tags) {
        not(tags.getValues());
        return this;
    }

    @NotNull
    public MaterialSetTag not(@NotNull Material... material) {
        this.materials.removeAll(Lists.newArrayList(material));
        return this;
    }

    @NotNull
    public MaterialSetTag not(@NotNull Collection<Material> materials) {
        this.materials.removeAll(materials);
        return this;
    }

    @NotNull
    public MaterialSetTag not(@NotNull Predicate<Material> filter) {
        not(Stream.of(Material.values()).filter(((Predicate<Material>) Material::isLegacy).negate()).filter(filter).collect(Collectors.toList()));
        return this;
    }

    @NotNull
    public MaterialSetTag notEndsWith(@NotNull String with) {
        return not(mat -> mat.name().endsWith(with));
    }


    @NotNull
    public MaterialSetTag notStartsWith(@NotNull String with) {
        return not(mat -> mat.name().startsWith(with));
    }

    @NotNull
    public Set<Material> getValues() {
        return this.materials;
    }

    public boolean isTagged(@NotNull BlockData block) {
        return isTagged(block.getMaterial());
    }

    public boolean isTagged(@NotNull BlockState block) {
        return isTagged(block.getType());
    }

    public boolean isTagged(@NotNull Block block) {
        return isTagged(block.getType());
    }

    public boolean isTagged(@NotNull ItemStack item) {
        return isTagged(item.getType());
    }

    public boolean isTagged(@NotNull Material material) {
        return this.materials.contains(material);
    }

    @NotNull
    public MaterialSetTag ensureSize(@NotNull String label, int size) {
        long actual = this.materials.stream().filter(((Predicate<Material>) Material::isLegacy).negate()).count();
        if (size != actual) {
            throw new IllegalStateException(label + " - Expected " + size + " materials, got " + actual);
        }
        return this;
    }
}
