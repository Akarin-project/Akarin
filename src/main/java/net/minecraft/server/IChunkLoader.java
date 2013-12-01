package net.minecraft.server;

import java.io.IOException;
import java.util.function.Consumer;
import javax.annotation.Nullable;

public interface IChunkLoader {

    @Nullable
    Chunk a(GeneratorAccess generatoraccess, int i, int j, Consumer<Chunk> consumer) throws IOException;

    @Nullable
    ProtoChunk b(GeneratorAccess generatoraccess, int i, int j, Consumer<IChunkAccess> consumer) throws IOException;

    void saveChunk(World world, IChunkAccess ichunkaccess) throws IOException, ExceptionWorldConflict;

    void b();
}
