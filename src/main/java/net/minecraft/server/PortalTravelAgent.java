package net.minecraft.server;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public class PortalTravelAgent {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final BlockPortal b = (BlockPortal) Blocks.NETHER_PORTAL;
    private final WorldServer world;
    private final Random d;
    private final Map<BlockPosition2D, PortalTravelAgent.ChunkCoordinatesPortal> e = Maps.newHashMapWithExpectedSize(4096);
    private final Object2LongMap<BlockPosition2D> f = new Object2LongOpenHashMap();

    public PortalTravelAgent(WorldServer worldserver) {
        this.world = worldserver;
        this.d = new Random(worldserver.getSeed());
    }

    public boolean a(Entity entity, float f) {
        Vec3D vec3d = entity.getPortalOffset();
        EnumDirection enumdirection = entity.getPortalDirection();
        ShapeDetector.Shape shapedetector_shape = this.a(new BlockPosition(entity), entity.getMot(), enumdirection, vec3d.x, vec3d.y, entity instanceof EntityHuman);

        if (shapedetector_shape == null) {
            return false;
        } else {
            Vec3D vec3d1 = shapedetector_shape.position;
            Vec3D vec3d2 = shapedetector_shape.velocity;

            entity.setMot(vec3d2);
            entity.yaw = f + (float) shapedetector_shape.yaw;
            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).playerConnection.a(vec3d1.x, vec3d1.y, vec3d1.z, entity.yaw, entity.pitch);
                ((EntityPlayer) entity).playerConnection.syncPosition();
            } else {
                entity.setPositionRotation(vec3d1.x, vec3d1.y, vec3d1.z, entity.yaw, entity.pitch);
            }

            return true;
        }
    }

    @Nullable
    public ShapeDetector.Shape a(BlockPosition blockposition, Vec3D vec3d, EnumDirection enumdirection, double d0, double d1, boolean flag) {
        boolean flag1 = true;
        boolean flag2 = true;
        BlockPosition blockposition1 = null;
        BlockPosition2D blockposition2d = new BlockPosition2D(blockposition);

        if (!flag && this.f.containsKey(blockposition2d)) {
            return null;
        } else {
            PortalTravelAgent.ChunkCoordinatesPortal portaltravelagent_chunkcoordinatesportal = (PortalTravelAgent.ChunkCoordinatesPortal) this.e.get(blockposition2d);

            if (portaltravelagent_chunkcoordinatesportal != null) {
                blockposition1 = portaltravelagent_chunkcoordinatesportal.a;
                portaltravelagent_chunkcoordinatesportal.b = this.world.getTime();
                flag2 = false;
            } else {
                double d2 = Double.MAX_VALUE;

                int portalSearchRadius = world.paperConfig.portalSearchRadius; // Paper
                for (int i = -portalSearchRadius; i <= portalSearchRadius; ++i) { // Paper
                    BlockPosition blockposition2;

                    for (int j = -world.paperConfig.portalSearchRadius; j <= world.paperConfig.portalSearchRadius; ++j) { // Paper
                        for (BlockPosition blockposition3 = blockposition.b(i, this.world.getHeight() - 1 - blockposition.getY(), j); blockposition3.getY() >= 0; blockposition3 = blockposition2) {
                            blockposition2 = blockposition3.down();
                            if (this.world.getType(blockposition3).getBlock() == PortalTravelAgent.b) {
                                for (blockposition2 = blockposition3.down(); this.world.getType(blockposition2).getBlock() == PortalTravelAgent.b; blockposition2 = blockposition2.down()) {
                                    blockposition3 = blockposition2;
                                }

                                double d3 = blockposition3.m(blockposition);

                                if (d2 < 0.0D || d3 < d2) {
                                    d2 = d3;
                                    blockposition1 = blockposition3;
                                }
                            }
                        }
                    }
                }
            }

            if (blockposition1 == null) {
                long k = this.world.getTime() + 300L;

                this.f.put(blockposition2d, k);
                return null;
            } else {
                if (flag2) {
                    this.e.put(blockposition2d, new PortalTravelAgent.ChunkCoordinatesPortal(blockposition1, this.world.getTime()));
                    Logger logger = PortalTravelAgent.LOGGER;
                    Supplier[] asupplier = new Supplier[2];
                    WorldProvider worldprovider = this.world.getWorldProvider();

                    asupplier[0] = worldprovider::getDimensionManager;
                    asupplier[1] = () -> {
                        return blockposition2d;
                    };
                    logger.debug("Adding nether portal ticket for {}:{}", asupplier);
                    this.world.getChunkProvider().addTicket(TicketType.PORTAL, new ChunkCoordIntPair(blockposition1), 3, blockposition2d);
                }

                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = PortalTravelAgent.b.c((GeneratorAccess) this.world, blockposition1);

                return shapedetector_shapedetectorcollection.a(enumdirection, blockposition1, d1, vec3d, d0);
            }
        }
    }

    public boolean a(Entity entity) {
        boolean flag = true;
        double d0 = -1.0D;
        int i = MathHelper.floor(entity.locX);
        int j = MathHelper.floor(entity.locY);
        int k = MathHelper.floor(entity.locZ);
        int l = i;
        int i1 = j;
        int j1 = k;
        int k1 = 0;
        int l1 = this.d.nextInt(4);
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        double d1;
        int i2;
        double d2;
        int j2;
        int k2;
        int l2;
        int i3;
        int j3;
        int k3;
        int l3;
        int i4;
        int j4;
        int k4;
        double d3;
        double d4;

        for (i2 = i - 16; i2 <= i + 16; ++i2) {
            d1 = (double) i2 + 0.5D - entity.locX;

            for (j2 = k - 16; j2 <= k + 16; ++j2) {
                d2 = (double) j2 + 0.5D - entity.locZ;

                label257:
                for (k2 = this.world.getHeight() - 1; k2 >= 0; --k2) {
                    if (this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2, j2))) {
                        while (k2 > 0 && this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2 - 1, j2))) {
                            --k2;
                        }

                        for (i3 = l1; i3 < l1 + 4; ++i3) {
                            l2 = i3 % 2;
                            j3 = 1 - l2;
                            if (i3 % 4 >= 2) {
                                l2 = -l2;
                                j3 = -j3;
                            }

                            for (l3 = 0; l3 < 3; ++l3) {
                                for (i4 = 0; i4 < 4; ++i4) {
                                    for (k4 = -1; k4 < 4; ++k4) {
                                        k3 = i2 + (i4 - 1) * l2 + l3 * j3;
                                        j4 = k2 + k4;
                                        int l4 = j2 + (i4 - 1) * j3 - l3 * l2;

                                        blockposition_mutableblockposition.d(k3, j4, l4);
                                        if (k4 < 0 && !this.world.getType(blockposition_mutableblockposition).getMaterial().isBuildable() || k4 >= 0 && !this.world.isEmpty(blockposition_mutableblockposition)) {
                                            continue label257;
                                        }
                                    }
                                }
                            }

                            d3 = (double) k2 + 0.5D - entity.locY;
                            d4 = d1 * d1 + d3 * d3 + d2 * d2;
                            if (d0 < 0.0D || d4 < d0) {
                                d0 = d4;
                                l = i2;
                                i1 = k2;
                                j1 = j2;
                                k1 = i3 % 4;
                            }
                        }
                    }
                }
            }
        }

        if (d0 < 0.0D) {
            for (i2 = i - 16; i2 <= i + 16; ++i2) {
                d1 = (double) i2 + 0.5D - entity.locX;

                for (j2 = k - 16; j2 <= k + 16; ++j2) {
                    d2 = (double) j2 + 0.5D - entity.locZ;

                    label205:
                    for (k2 = this.world.getHeight() - 1; k2 >= 0; --k2) {
                        if (this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2, j2))) {
                            while (k2 > 0 && this.world.isEmpty(blockposition_mutableblockposition.d(i2, k2 - 1, j2))) {
                                --k2;
                            }

                            for (i3 = l1; i3 < l1 + 2; ++i3) {
                                l2 = i3 % 2;
                                j3 = 1 - l2;

                                for (l3 = 0; l3 < 4; ++l3) {
                                    for (i4 = -1; i4 < 4; ++i4) {
                                        k4 = i2 + (l3 - 1) * l2;
                                        k3 = k2 + i4;
                                        j4 = j2 + (l3 - 1) * j3;
                                        blockposition_mutableblockposition.d(k4, k3, j4);
                                        if (i4 < 0 && !this.world.getType(blockposition_mutableblockposition).getMaterial().isBuildable() || i4 >= 0 && !this.world.isEmpty(blockposition_mutableblockposition)) {
                                            continue label205;
                                        }
                                    }
                                }

                                d3 = (double) k2 + 0.5D - entity.locY;
                                d4 = d1 * d1 + d3 * d3 + d2 * d2;
                                if (d0 < 0.0D || d4 < d0) {
                                    d0 = d4;
                                    l = i2;
                                    i1 = k2;
                                    j1 = j2;
                                    k1 = i3 % 2;
                                }
                            }
                        }
                    }
                }
            }
        }

        int i5 = l;
        int j5 = i1;

        j2 = j1;
        int k5 = k1 % 2;
        int l5 = 1 - k5;

        if (k1 % 4 >= 2) {
            k5 = -k5;
            l5 = -l5;
        }

        org.bukkit.craftbukkit.util.BlockStateListPopulator blockList = new org.bukkit.craftbukkit.util.BlockStateListPopulator(this.world); // CraftBukkit - Use BlockStateListPopulator
        if (d0 < 0.0D) {
            i1 = MathHelper.clamp(i1, 70, this.world.getHeight() - 10);
            j5 = i1;

            for (k2 = -1; k2 <= 1; ++k2) {
                for (i3 = 1; i3 < 3; ++i3) {
                    for (l2 = -1; l2 < 3; ++l2) {
                        j3 = i5 + (i3 - 1) * k5 + k2 * l5;
                        l3 = j5 + l2;
                        i4 = j2 + (i3 - 1) * l5 - k2 * k5;
                        boolean flag1 = l2 < 0;

                        blockposition_mutableblockposition.d(j3, l3, i4);
                        blockList.setTypeAndData(blockposition_mutableblockposition, flag1 ? Blocks.OBSIDIAN.getBlockData() : Blocks.AIR.getBlockData(), 3); // CraftBukkit
                    }
                }
            }
        }

        for (k2 = -1; k2 < 3; ++k2) {
            for (i3 = -1; i3 < 4; ++i3) {
                if (k2 == -1 || k2 == 2 || i3 == -1 || i3 == 3) {
                    blockposition_mutableblockposition.d(i5 + k2 * k5, j5 + i3, j2 + k2 * l5);
                    blockList.setTypeAndData(blockposition_mutableblockposition, Blocks.OBSIDIAN.getBlockData(), 3); // CraftBukkit
                }
            }
        }

        IBlockData iblockdata = (IBlockData) PortalTravelAgent.b.getBlockData().set(BlockPortal.AXIS, k5 == 0 ? EnumDirection.EnumAxis.Z : EnumDirection.EnumAxis.X);

        for (i3 = 0; i3 < 2; ++i3) {
            for (l2 = 0; l2 < 3; ++l2) {
                blockposition_mutableblockposition.d(i5 + i3 * k5, j5 + l2, j2 + i3 * l5);
                blockList.setTypeAndData(blockposition_mutableblockposition, iblockdata, 18); // CraftBukkit
            }
        }

        // CraftBukkit start
        org.bukkit.World bworld = this.world.getWorld();
        org.bukkit.event.world.PortalCreateEvent event = new org.bukkit.event.world.PortalCreateEvent((java.util.List<org.bukkit.block.BlockState>) (java.util.List) blockList.getList(), bworld, entity.getBukkitEntity(), org.bukkit.event.world.PortalCreateEvent.CreateReason.NETHER_PAIR);

        this.world.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            blockList.updateList();
        }
        // CraftBukkit end
        return true;
    }

    public void a(long i) {
        if (i % 100L == 0L) {
            this.b(i);
            this.c(i);
        }

    }

    private void b(long i) {
        LongIterator longiterator = this.f.values().iterator();

        while (longiterator.hasNext()) {
            long j = longiterator.nextLong();

            if (j <= i) {
                longiterator.remove();
            }
        }

    }

    private void c(long i) {
        long j = i - 300L;
        Iterator iterator = this.e.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<BlockPosition2D, PortalTravelAgent.ChunkCoordinatesPortal> entry = (Entry) iterator.next();
            PortalTravelAgent.ChunkCoordinatesPortal portaltravelagent_chunkcoordinatesportal = (PortalTravelAgent.ChunkCoordinatesPortal) entry.getValue();

            if (portaltravelagent_chunkcoordinatesportal.b < j) {
                BlockPosition2D blockposition2d = (BlockPosition2D) entry.getKey();
                Logger logger = PortalTravelAgent.LOGGER;
                Supplier[] asupplier = new Supplier[2];
                WorldProvider worldprovider = this.world.getWorldProvider();

                asupplier[0] = worldprovider::getDimensionManager;
                asupplier[1] = () -> {
                    return blockposition2d;
                };
                logger.debug("Removing nether portal ticket for {}:{}", asupplier);
                this.world.getChunkProvider().removeTicket(TicketType.PORTAL, new ChunkCoordIntPair(portaltravelagent_chunkcoordinatesportal.a), 3, blockposition2d);
                iterator.remove();
            }
        }

    }

    static class ChunkCoordinatesPortal {

        public final BlockPosition a;
        public long b;

        public ChunkCoordinatesPortal(BlockPosition blockposition, long i) {
            this.a = blockposition;
            this.b = i;
        }
    }
}
