package org.bukkit.craftbukkit.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import net.minecraft.server.BlockPosition;
import net.minecraft.server.IBlockData;
import net.minecraft.server.World;

import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.block.CraftBlockState;

public class BlockStateListPopulator extends DummyGeneratorAccess {
    private final World world;
    private final LinkedHashMap<BlockPosition, CraftBlockState> list;

    public BlockStateListPopulator(World world) {
        this(world, new LinkedHashMap<>());
    }

    public BlockStateListPopulator(World world, LinkedHashMap<BlockPosition, CraftBlockState> list) {
        this.world = world;
        this.list = list;
    }

    @Override
    public boolean setTypeAndData(BlockPosition position, IBlockData data, int flag) {
        CraftBlockState state = CraftBlockState.getBlockState(world, position, flag);
        state.setData(data);
        list.put(position, state);
        return true;
    }

    public void updateList() {
        for (BlockState state : list.values()) {
            state.update(true);
        }
    }

    public List<CraftBlockState> getList() {
        return new ArrayList<>(list.values());
    }

    public World getWorld() {
        return world;
    }
}
