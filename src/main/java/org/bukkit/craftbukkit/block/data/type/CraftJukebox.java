package org.bukkit.craftbukkit.block.data.type;

import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.block.data.type.Jukebox;

public abstract class CraftJukebox extends CraftBlockData implements Jukebox {

    private static final net.minecraft.server.BlockStateBoolean HAS_RECORD = getBoolean("has_record");

    @Override
    public boolean hasRecord() {
        return get(HAS_RECORD);
    }
}
