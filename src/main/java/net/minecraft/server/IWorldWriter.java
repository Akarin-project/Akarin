package net.minecraft.server;

public interface IWorldWriter {

    boolean setTypeAndData(BlockPosition blockposition, IBlockData iblockdata, int i);

    boolean addEntity(Entity entity);

    boolean setAir(BlockPosition blockposition);

    void a(EnumSkyBlock enumskyblock, BlockPosition blockposition, int i);

    boolean setAir(BlockPosition blockposition, boolean flag);
}
