package net.minecraft.server;

import java.util.concurrent.Executor;

public class SecondaryWorldServer extends WorldServer {

    // CraftBukkit start - Add WorldData, Environment and ChunkGenerator arguments
    public SecondaryWorldServer(WorldServer worldserver, MinecraftServer minecraftserver, Executor executor, WorldNBTStorage worldnbtstorage, DimensionManager dimensionmanager, GameProfilerFiller gameprofilerfiller, WorldLoadListener worldloadlistener, WorldData worldData, org.bukkit.World.Environment env, org.bukkit.generator.ChunkGenerator gen) {
        super(minecraftserver, executor, worldnbtstorage, worldData, dimensionmanager, gameprofilerfiller, worldloadlistener, env, gen);
        // worldserver.getWorldBorder().a((IWorldBorderListener) (new IWorldBorderListener.a(this.getWorldBorder())));
        // CraftBukkit end
    }

    // @Override // CraftBukkit
    // protected void a() {} // CraftBukkit
}
