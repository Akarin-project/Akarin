package net.minecraft.server;

import com.destroystokyo.paper.block.TargetBlockInfo;
import com.destroystokyo.paper.profile.CraftPlayerProfile;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.authlib.GameProfile;

import co.aikar.timings.ThreadAssertion;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.Waitable;
import org.spigotmc.AsyncCatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

public final class MCUtil {
    private static final Executor asyncExecutor = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Paper Async Task Handler Thread - %1$d").build());

    private MCUtil() {}

    /**
     * Quickly generate a stack trace for current location
     *
     * @return Stacktrace
     */
    public static String stack() {
        return ExceptionUtils.getFullStackTrace(new Throwable());
    }

    /**
     * Quickly generate a stack trace for current location with message
     *
     * @param str
     * @return Stacktrace
     */
    public static String stack(String str) {
        return ExceptionUtils.getFullStackTrace(new Throwable(str));
    }

    public static boolean isMainThread() {
        return ThreadAssertion.isMainThread() && MinecraftServer.getServer().isMainThread(); // Akarin
    }

    private static class DelayedRunnable implements Runnable {

        private final int ticks;
        private final Runnable run;

        private DelayedRunnable(int ticks, Runnable run) {
            this.ticks = ticks;
            this.run = run;
        }

        @Override
        public void run() {
            if (ticks <= 0) {
                run.run();
            } else {
                scheduleTask(ticks-1, run);
            }
        }
    }

    public static void scheduleTask(int ticks, Runnable runnable) {
        // We use post to main instead of process queue as we don't want to process these mid tick if
        // Someone uses processQueueWhileWaiting
        MinecraftServer.getServer().postToMainThread(new DelayedRunnable(ticks, runnable));
    }

    public static void processQueue() {
        Runnable runnable;
        Queue<Runnable> processQueue = getProcessQueue();
        while ((runnable = processQueue.poll()) != null) {
            try {
                runnable.run();
            } catch (Exception e) {
                MinecraftServer.LOGGER.error("Error executing task", e);
            }
        }
    }
    public static <T> T processQueueWhileWaiting(CompletableFuture <T> future) {
        try {
            if (isMainThread()) {
                while (!future.isDone()) {
                    try {
                        return future.get(1, TimeUnit.MILLISECONDS);
                    } catch (TimeoutException ignored) {
                        processQueue();
                    }
                }
            }
            return future.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void ensureMain(Runnable run) {
        ensureMain(null, run);
    }
    /**
     * Ensures the target code is running on the main thread
     * @param reason
     * @param run
     * @return
     */
    public static void ensureMain(String reason, Runnable run) {
        if (/*AsyncCatcher.enabled &&*/ !ThreadAssertion.isMainThread() && Thread.currentThread() != MinecraftServer.getServer().primaryThread) { // Akarin
            if (reason != null) {
                new IllegalStateException("Asynchronous " + reason + "!").printStackTrace();
            }
            getProcessQueue().add(run);
            return;
        }
        run.run();
    }

    private static Queue<Runnable> getProcessQueue() {
        return MinecraftServer.getServer().processQueue;
    }

    public static <T> T ensureMain(Supplier<T> run) {
        return ensureMain(null, run);
    }
    /**
     * Ensures the target code is running on the main thread
     * @param reason
     * @param run
     * @param <T>
     * @return
     */
    public static <T> T ensureMain(String reason, Supplier<T> run) {
        if (/*AsyncCatcher.enabled &&*/ !ThreadAssertion.isMainThread() && Thread.currentThread() != MinecraftServer.getServer().primaryThread) { // Akarin
            if (reason != null) {
                new IllegalStateException("Asynchronous " + reason + "! Blocking thread until it returns ").printStackTrace();
            }
            Waitable<T> wait = new Waitable<T>() {
                @Override
                protected T evaluate() {
                    return run.get();
                }
            };
            getProcessQueue().add(wait);
            try {
                return wait.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            return null;
        }
        return run.get();
    }

    public static PlayerProfile toBukkit(GameProfile profile) {
        return CraftPlayerProfile.asBukkitMirror(profile);
    }

    /**
     * Calculates distance between 2 entities
     * @param e1
     * @param e2
     * @return
     */
    public static double distance(Entity e1, Entity e2) {
        return Math.sqrt(distanceSq(e1, e2));
    }


    /**
     * Calculates distance between 2 block positions
     * @param e1
     * @param e2
     * @return
     */
    public static double distance(BlockPosition e1, BlockPosition e2) {
        return Math.sqrt(distanceSq(e1, e2));
    }

    /**
     * Gets the distance between 2 positions
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(distanceSq(x1, y1, z1, x2, y2, z2));
    }

    /**
     * Get's the distance squared between 2 entities
     * @param e1
     * @param e2
     * @return
     */
    public static double distanceSq(Entity e1, Entity e2) {
        return distanceSq(e1.locX,e1.locY,e1.locZ, e2.locX,e2.locY,e2.locZ);
    }

    /**
     * Gets the distance sqaured between 2 block positions
     * @param pos1
     * @param pos2
     * @return
     */
    public static double distanceSq(BlockPosition pos1, BlockPosition pos2) {
        return distanceSq(pos1.getX(), pos1.getY(), pos1.getZ(), pos2.getX(), pos2.getY(), pos2.getZ());
    }

    /**
     * Gets the distance squared between 2 positions
     * @param x1
     * @param y1
     * @param z1
     * @param x2
     * @param y2
     * @param z2
     * @return
     */
    public static double distanceSq(double x1, double y1, double z1, double x2, double y2, double z2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2);
    }

    /**
     * Converts a NMS World/BlockPosition to Bukkit Location
     * @param world
     * @param x
     * @param y
     * @param z
     * @return
     */
    public static Location toLocation(World world, double x, double y, double z) {
        return new Location(world.getWorld(), x, y, z);
    }

    /**
     * Converts a NMS World/BlockPosition to Bukkit Location
     * @param world
     * @param pos
     * @return
     */
    public static Location toLocation(World world, BlockPosition pos) {
        return new Location(world.getWorld(), pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Converts an NMS entity's current location to a Bukkit Location
     * @param entity
     * @return
     */
    public static Location toLocation(Entity entity) {
        return new Location(entity.getWorld().getWorld(), entity.locX, entity.locY, entity.locZ);
    }

    public static org.bukkit.block.Block toBukkitBlock(World world, BlockPosition pos) {
        return world.getWorld().getBlockAt(pos); // Akarin
    }

    public static BlockPosition toBlockPosition(Location loc) {
        return new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
    }

    public static boolean isEdgeOfChunk(BlockPosition pos) {
        final int modX = pos.getX() & 15;
        final int modZ = pos.getZ() & 15;
        return (modX == 0 || modX == 15 || modZ == 0 || modZ == 15);
    }

    /**
     * Posts a task to be executed asynchronously
     * @param run
     */
    public static void scheduleAsyncTask(Runnable run) {
        asyncExecutor.execute(run);
    }

    @Nullable
    public static TileEntityHopper getHopper(World world, BlockPosition pos) {
        Chunk chunk = world.getChunkIfLoaded(pos.getX() >> 4, pos.getZ() >> 4);
        if (chunk != null && chunk.getBlockData(pos.getX(), pos.getY(), pos.getZ()).getBlock() == Blocks.HOPPER) {
            TileEntity tileEntity = chunk.getTileEntityImmediately(pos);
            if (tileEntity instanceof TileEntityHopper) {
                return (TileEntityHopper) tileEntity;
            }
        }
        return null;
    }

    @Nonnull
    public static World getNMSWorld(@Nonnull org.bukkit.World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static World getNMSWorld(@Nonnull org.bukkit.entity.Entity entity) {
        return getNMSWorld(entity.getWorld());
    }

    public static FluidCollisionOption getNMSFluidCollisionOption(TargetBlockInfo.FluidMode fluidMode) {
        if (fluidMode == TargetBlockInfo.FluidMode.NEVER) {
            return FluidCollisionOption.NEVER;
        }
        if (fluidMode == TargetBlockInfo.FluidMode.SOURCE_ONLY) {
            return FluidCollisionOption.SOURCE_ONLY;
        }
        if (fluidMode == TargetBlockInfo.FluidMode.ALWAYS) {
            return FluidCollisionOption.ALWAYS;
        }
        return null;
    }

    public static BlockFace toBukkitBlockFace(EnumDirection enumDirection) {
        switch (enumDirection) {
            case DOWN:
                return BlockFace.DOWN;
            case UP:
                return BlockFace.UP;
            case NORTH:
                return BlockFace.NORTH;
            case SOUTH:
                return BlockFace.SOUTH;
            case WEST:
                return BlockFace.WEST;
            case EAST:
                return BlockFace.EAST;
            default:
                return null;
        }
    }

    @Nullable
    public static IChatBaseComponent getBaseComponentFromNbt(String key, NBTTagCompound compound) {
        if (!compound.hasKey(key)) {
            return null;
        }
        String string = compound.getString(key);
        try {
            return IChatBaseComponent.ChatSerializer.jsonToComponent(string);
        } catch (com.google.gson.JsonParseException e) {
            org.bukkit.Bukkit.getLogger().warning("Unable to parse " + key + " from " + compound +": " + e.getMessage());
        }

        return null;
    }
}
