package net.minecraft.server;

import com.mojang.datafixers.DataFixer;
import java.io.File;
import javax.annotation.Nullable;

public interface IDataManager {

    @Nullable
    WorldData getWorldData();

    void checkSession() throws ExceptionWorldConflict;

    IChunkLoader createChunkLoader(WorldProvider worldprovider);

    void saveWorldData(WorldData worlddata, NBTTagCompound nbttagcompound);

    void saveWorldData(WorldData worlddata);

    IPlayerFileData getPlayerFileData();

    void a();

    File getDirectory();

    @Nullable
    File getDataFile(DimensionManager dimensionmanager, String s);

    DefinedStructureManager h();

    DataFixer i();
}
