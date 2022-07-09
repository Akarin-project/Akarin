package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.Location;
import org.bukkit.event.block.BlockExplodeEvent;
// CraftBukkit end

public class Explosion {

    private final boolean a;
    private final boolean b;
    private final Random c = new Random();
    private final World world;
    private final double posX;
    private final double posY;
    private final double posZ;
    public final Entity source;
    private final float size;
    private final List<BlockPosition> blocks = Lists.newArrayList();
    private final Map<EntityHuman, Vec3D> k = Maps.newHashMap();
    public boolean wasCanceled = false; // CraftBukkit - add field
	
    // Dionysus start
    private final BlockPosition.MutableBlockPosition cachedPos = new BlockPosition.MutableBlockPosition();
    // The chunk coordinate of the most recently stepped through block.
    private int prevChunkX = Integer.MIN_VALUE;
    private int prevChunkZ = Integer.MIN_VALUE;

    // The chunk belonging to prevChunkPos.
    private Chunk prevChunk;

    private static final com.google.common.base.Predicate<Entity> hitPredicate = entity -> IEntitySelector.d.apply(entity) && !entity.dead; // Dionysus - Paper - don't hit dead entities
    // Dionysus end

    public Explosion(World world, Entity entity, double d0, double d1, double d2, float f, boolean flag, boolean flag1) {
        this.world = world;
        this.source = entity;
        this.size = (float) Math.max(f, 0.0); // CraftBukkit - clamp bad values
        this.posX = d0;
        this.posY = d1;
        this.posZ = d2;
        this.a = flag;
        this.b = flag1;
    }

    public void a() {
        // CraftBukkit start
        if (this.size < 0.1F) {
            return;
        }
        // CraftBukkit end
        // Dionysus start - optimize memory usage from explosions
        // CaffeineMC optimized raytracing loop
        // @author JellySquid
        // @author nopjmp
        // https://github.com/CaffeineMC/lithium-fabric
        // Using integer encoding for the block positions provides a massive speedup and prevents us from needing to
        // allocate a block position for every step we make along each ray, eliminating essentially all the memory
        // allocations of this function. The overhead of packing block positions into integer format is negligible
        // compared to a memory allocation and associated overhead of hashing real objects in a set.
        final LongOpenHashSet touched = new LongOpenHashSet(0);
        final Random random = this.world.random;
        for (int rayX = 0; rayX < 16; ++rayX) {
            boolean xPlane = rayX == 0 || rayX == 15;
            double vecX = (((float) rayX / 15.0F) * 2.0F) - 1.0F;

            for (int rayY = 0; rayY < 16; ++rayY) {
                boolean yPlane = rayY == 0 || rayY == 15;
                double vecY = (((float) rayY / 15.0F) * 2.0F) - 1.0F;

                for (int rayZ = 0; rayZ < 16; ++rayZ) {
                    boolean zPlane = rayZ == 0 || rayZ == 15;

                    // We only fire rays from the surface of our origin volume
                    if (xPlane || yPlane || zPlane) {
                        double vecZ = (((float) rayZ / 15.0F) * 2.0F) - 1.0F;

                        this.performRayCast(random, vecX, vecY, vecZ, touched);
                    }
                }
            }
        }

        // We can now iterate back over the set of positions we modified and re-build BlockPos objects from them
        // This will only allocate as many objects as there are in the set, where otherwise we would allocate them
        // each step of a every ray.
        blocks.ensureCapacity(touched.size());
        for (Long longPos : touched) {
            blocks.add(BlockPosition.fromLong(longPos));
        }
        float f3 = this.size * 2.0F;

        int i = MathHelper.floor(this.posX - (double) f3 - 1.0D);
        int j = MathHelper.floor(this.posX + (double) f3 + 1.0D);
        int l = MathHelper.floor(this.posY - (double) f3 - 1.0D);
        int i1 = MathHelper.floor(this.posY + (double) f3 + 1.0D);
        int j1 = MathHelper.floor(this.posZ - (double) f3 - 1.0D);
        int k1 = MathHelper.floor(this.posZ + (double) f3 + 1.0D);
        // Paper start - Fix lag from explosions processing dead entities
        List<Entity> list = this.world.getEntities(this.source, new AxisAlignedBB(i, l, j1, j, i1, k1), hitPredicate);
        // Paper end
        Vec3D vec3d = new Vec3D(this.posX, this.posY, this.posZ);

        for (Entity entity : list) {

            if (!entity.bB()) {
                double d7 = entity.e(this.posX, this.posY, this.posZ) / (double) f3;

                if (d7 <= 1.0D) {
                    double d8 = entity.locX - this.posX;
                    double d9 = entity.locY + (double) entity.getHeadHeight() - this.posY;
                    double d10 = entity.locZ - this.posZ;
                    double d11 = MathHelper.sqrt(d8 * d8 + d9 * d9 + d10 * d10);

                    if (d11 != 0.0D) {
                        d8 /= d11;
                        d9 /= d11;
                        d10 /= d11;
                        double d12 = this.getBlockDensity(vec3d, entity.getBoundingBox()); // Paper - Optimize explosions
                        double d13 = (1.0D - d7) * d12;

                        // CraftBukkit start
                        // entity.damageEntity(DamageSource.explosion(this), (float) ((int) ((d13 * d13 + d13) / 2.0D * 7.0D * (double) f3 + 1.0D)));
                        CraftEventFactory.entityDamage = source;
                        entity.forceExplosionKnockback = false;
                        boolean wasDamaged = entity.damageEntity(DamageSource.explosion(this), (float) ((int) ((d13 * d13 + d13) / 2.0D * 7.0D * (double) f3 + 1.0D)));
                        CraftEventFactory.entityDamage = null;
                        if (!wasDamaged && !(entity instanceof EntityTNTPrimed || entity instanceof EntityFallingBlock) && !entity.forceExplosionKnockback) {
                            continue;
                        }
                        // CraftBukkit end
                        double d14 = d13;

                        if (entity instanceof EntityLiving) {
                            d14 = entity instanceof EntityHuman && world.paperConfig.disableExplosionKnockback ? 0 : EnchantmentProtection.a((EntityLiving) entity, d13); // Paper - Disable explosion knockback
                        }

                        entity.motX += d8 * d14;
                        entity.motY += d9 * d14;
                        entity.motZ += d10 * d14;
                        if (entity instanceof EntityHuman) {
                            EntityHuman entityhuman = (EntityHuman) entity;

                            if (!entityhuman.isSpectator() && (!entityhuman.z() && !world.paperConfig.disableExplosionKnockback || !entityhuman.abilities.isFlying)) { // Paper - Disable explosion knockback
                                this.k.put(entityhuman, new Vec3D(d8 * d13, d9 * d13, d10 * d13));
                            }
                        }
                    }
                }
            }
        }

    }
	
    private void performRayCast(Random random, double vecX, double vecY, double vecZ, LongOpenHashSet touched) {
        double dist = Math.sqrt((vecX * vecX) + (vecY * vecY) + (vecZ * vecZ));

        double normX = (vecX / dist) * 0.3D;
        double normY = (vecY / dist) * 0.3D;
        double normZ = (vecZ / dist) * 0.3D;

        float strength = this.size * (0.7F + (random.nextFloat() * 0.6F));

        double stepX = this.posX;
        double stepY = this.posY;
        double stepZ = this.posZ;

        int prevX = Integer.MIN_VALUE;
        int prevY = Integer.MIN_VALUE;
        int prevZ = Integer.MIN_VALUE;

        float prevResistance = 0.0F;

        // Step through the ray until it is finally stopped
        while (strength > 0.0F) {
            int blockX = MathHelper.floor(stepX);
            int blockY = MathHelper.floor(stepY);
            int blockZ = MathHelper.floor(stepZ);

            float resistance;

            // Check whether or not we have actually moved into a new block this step. Due to how rays are stepped through,
            // over-sampling of the same block positions will occur. Changing this behaviour would introduce differences in
            // aliasing and sampling, which is unacceptable for our purposes. As a band-aid, we can simply re-use the
            // previous result and get a decent boost.
            if (prevX != blockX || prevY != blockY || prevZ != blockZ) {
                resistance = this.traverseBlock(strength, blockX, blockY, blockZ, touched);

                prevX = blockX;
                prevY = blockY;
                prevZ = blockZ;

                prevResistance = resistance;
            } else {
                resistance = prevResistance;
            }

            strength -= resistance;
            // Apply a constant fall-off
            strength -= 0.22500001F;

            stepX += normX;
            stepY += normY;
            stepZ += normZ;
        }
    }

    /**
     * Called for every step made by a ray being cast by an explosion.
     *
     * @param strength The strength of the ray during this step
     * @param blockX   The x-coordinate of the block the ray is inside of
     * @param blockY   The y-coordinate of the block the ray is inside of
     * @param blockZ   The z-coordinate of the block the ray is inside of
     * @return The resistance of the current block space to the ray
     */
    private float traverseBlock(float strength, int blockX, int blockY, int blockZ, LongOpenHashSet touched) {
        cachedPos.setValues(blockX, blockY, blockZ);
        IBlockData iblockdata = this.world.getType(cachedPos);

        // Early-exit if the y-coordinate is out of bounds.
        if (cachedPos.isInvalidYLocation()) {
            if (iblockdata.getMaterial() != Material.AIR) {
                float blastResistance = this.source != null ? this.source.a(this, this.world, cachedPos, iblockdata) : iblockdata.getBlock().a((Entity) null);
                return (blastResistance + 0.3F) * 0.3F;
            }
            return 0.0F;
        }


        int chunkX = blockX >> 4;
        int chunkZ = blockZ >> 4;

        // Avoid calling into the chunk manager as much as possible through managing chunks locally
        if (this.prevChunkX != chunkX || this.prevChunkZ != chunkZ) {
            this.prevChunk = this.world.getChunkAt(chunkX, chunkZ);

            this.prevChunkX = chunkX;
            this.prevChunkZ = chunkZ;
        }

        final Chunk chunk = this.prevChunk;

        float totalResistance = 0.0F;
        Optional<Float> blastResistance = Optional.empty();

        // If the chunk is missing or out of bounds, assume that it is air
        if (chunk != null) {
            // We operate directly on chunk sections to avoid interacting with BlockPos and to squeeze out as much
            // performance as possible here
            ChunkSection section = chunk.getSections()[blockY >> 4];

            // If the section doesn't exist or it's empty, assume that the block is air
            if (section != null && !section.isEmpty()) {
                // Retrieve the block state from the chunk section directly to avoid associated overhead
                IBlockData blockState = section.getType(blockX & 15, blockY & 15, blockZ & 15);

                // If the block state is air, it cannot have fluid or any kind of resistance, so just leave
                if (blockState.getBlock() != Blocks.AIR) {
                    // Get the explosion resistance like vanilla
                    blastResistance = Optional.of(this.source != null ? this.source.a(this, this.world, cachedPos, iblockdata) : iblockdata.getBlock().a((Entity) null));
                }
            }
        }
        
        // Calculate how much this block will resist an explosion's ray
        if (blastResistance.isPresent()) {
            totalResistance = (blastResistance.get() + 0.3F) * 0.3F;
        }

        // Check if this ray is still strong enough to break blocks, and if so, add this position to the set
        // of positions to destroy
        float reducedStrength = strength - totalResistance;
        if (reducedStrength > 0.0F && (this.explodeAirBlocks() || iblockdata.getMaterial() != Material.AIR)) {
            if ((this.source == null || this.source.a(this, this.world, cachedPos, iblockdata, reducedStrength)) && cachedPos.getY() < 256 && cachedPos.getY() >= 0) {
                touched.add(cachedPos.asLong());
            }
        }

        return totalResistance;
    }
    // Dionysus end


    public void a(boolean flag) {
        this.world.a((EntityHuman) null, this.posX, this.posY, this.posZ, SoundEffects.bV, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.world.random.nextFloat() - this.world.random.nextFloat()) * 0.2F) * 0.7F);
        if (this.size >= 2.0F && this.b) {
            this.world.addParticle(EnumParticle.EXPLOSION_HUGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);
        } else {
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.posX, this.posY, this.posZ, 1.0D, 0.0D, 0.0D);
        }

        Iterator iterator;
        BlockPosition blockposition;

        if (this.b) {
            // CraftBukkit start
            org.bukkit.World bworld = this.world.getWorld();
            org.bukkit.entity.Entity explode = this.source == null ? null : this.source.getBukkitEntity();
            Location location = new Location(bworld, this.posX, this.posY, this.posZ);

            List<org.bukkit.block.Block> blockList = Lists.newArrayList();
            for (int i1 = this.blocks.size() - 1; i1 >= 0; i1--) {
                BlockPosition cpos = (BlockPosition) this.blocks.get(i1);
                org.bukkit.block.Block bblock = bworld.getBlockAt(cpos.getX(), cpos.getY(), cpos.getZ());
                if (bblock.getType() != org.bukkit.Material.AIR) {
                    blockList.add(bblock);
                }
            }

            boolean cancelled;
            List<org.bukkit.block.Block> bukkitBlocks;
            float yield;

            if (explode != null) {
                EntityExplodeEvent event = new EntityExplodeEvent(explode, location, blockList, 1.0F / this.size);
                this.world.getServer().getPluginManager().callEvent(event);
                cancelled = event.isCancelled();
                bukkitBlocks = event.blockList();
                yield = event.getYield();
            } else {
                BlockExplodeEvent event = new BlockExplodeEvent(location.getBlock(), blockList, 1.0F / this.size);
                this.world.getServer().getPluginManager().callEvent(event);
                cancelled = event.isCancelled();
                bukkitBlocks = event.blockList();
                yield = event.getYield();
            }

            this.blocks.clear();

            this.blocks.ensureCapacity(bukkitBlocks.size());
            for (org.bukkit.block.Block bblock : bukkitBlocks) {
                BlockPosition coords = new BlockPosition(bblock.getX(), bblock.getY(), bblock.getZ());
                blocks.add(coords);
            }

            if (cancelled) {
                this.wasCanceled = true;
                return;
            }
            // CraftBukkit end
            iterator = this.blocks.iterator();

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                IBlockData iblockdata = this.world.getType(blockposition);
                Block block = iblockdata.getBlock();
                this.world.chunkPacketBlockController.updateNearbyBlocks(this.world, blockposition); // Paper - Anti-Xray

                if (flag) {
                    double d0 = (double) ((float) blockposition.getX() + this.world.random.nextFloat());
                    double d1 = (double) ((float) blockposition.getY() + this.world.random.nextFloat());
                    double d2 = (double) ((float) blockposition.getZ() + this.world.random.nextFloat());
                    double d3 = d0 - this.posX;
                    double d4 = d1 - this.posY;
                    double d5 = d2 - this.posZ;
                    double d6 = (double) MathHelper.sqrt(d3 * d3 + d4 * d4 + d5 * d5);

                    d3 /= d6;
                    d4 /= d6;
                    d5 /= d6;
                    double d7 = 0.5D / (d6 / (double) this.size + 0.1D);

                    d7 *= (double) (this.world.random.nextFloat() * this.world.random.nextFloat() + 0.3F);
                    d3 *= d7;
                    d4 *= d7;
                    d5 *= d7;
                    this.world.addParticle(EnumParticle.EXPLOSION_NORMAL, (d0 + this.posX) / 2.0D, (d1 + this.posY) / 2.0D, (d2 + this.posZ) / 2.0D, d3, d4, d5);
                    this.world.addParticle(EnumParticle.SMOKE_NORMAL, d0, d1, d2, d3, d4, d5);
                }

                if (iblockdata.getMaterial() != Material.AIR) {
                    if (block.a(this)) {
                        // CraftBukkit - add yield
                        block.dropNaturally(this.world, blockposition, this.world.getType(blockposition), yield, 0);
                    }

                    this.world.setTypeAndData(blockposition, Blocks.AIR.getBlockData(), 3);
                    block.wasExploded(this.world, blockposition, this);
                }
            }
        }

        if (this.a) {
            iterator = this.blocks.iterator();

            while (iterator.hasNext()) {
                blockposition = (BlockPosition) iterator.next();
                if (this.world.getType(blockposition).getMaterial() == Material.AIR && this.world.getType(blockposition.down()).b() && this.c.nextInt(3) == 0) {
                    // CraftBukkit start - Ignition by explosion
                    if (!CraftEventFactory.callBlockIgniteEvent(this.world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this).isCancelled()) {
                        this.world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                    }
                    // CraftBukkit end
                }
            }
        }

    }

    public Map<EntityHuman, Vec3D> b() {
        return this.k;
    }

    @Nullable
    public EntityLiving getSource() {
        // CraftBukkit start - obtain Fireball shooter for explosion tracking
        return this.source == null ? null : (this.source instanceof EntityTNTPrimed ? ((EntityTNTPrimed) this.source).getSource() : (this.source instanceof EntityLiving ? (EntityLiving) this.source : (this.source instanceof EntityFireball ? ((EntityFireball) this.source).shooter : null)));
        // CraftBukkit end
    }

    public void clearBlocks() {
        this.blocks.clear();
    }

    public List<BlockPosition> getBlocks() {
        return this.blocks;
    }

    // Paper start - Optimize explosions
    private float getBlockDensity(Vec3D vec3d, AxisAlignedBB aabb) {
        if (!this.world.paperConfig.optimizeExplosions) {
            return this.world.a(vec3d, aabb);
        }
        CacheKey key = new CacheKey(this, aabb);
        return this.world.explosionDensityCache.computeIfAbsent(key, k1 -> this.world.a(vec3d, aabb));
    }

    static class CacheKey {
        private final World world;
        private final double posX, posY, posZ;
        private final double minX, minY, minZ;
        private final double maxX, maxY, maxZ;

        public CacheKey(Explosion explosion, AxisAlignedBB aabb) {
            this.world = explosion.world;
            this.posX = explosion.posX;
            this.posY = explosion.posY;
            this.posZ = explosion.posZ;
            this.minX = aabb.a;
            this.minY = aabb.b;
            this.minZ = aabb.c;
            this.maxX = aabb.d;
            this.maxY = aabb.e;
            this.maxZ = aabb.f;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            CacheKey cacheKey = (CacheKey) o;

            if (Double.compare(cacheKey.posX, posX) != 0) return false;
            if (Double.compare(cacheKey.posY, posY) != 0) return false;
            if (Double.compare(cacheKey.posZ, posZ) != 0) return false;
            if (Double.compare(cacheKey.minX, minX) != 0) return false;
            if (Double.compare(cacheKey.minY, minY) != 0) return false;
            if (Double.compare(cacheKey.minZ, minZ) != 0) return false;
            if (Double.compare(cacheKey.maxX, maxX) != 0) return false;
            if (Double.compare(cacheKey.maxY, maxY) != 0) return false;
            if (Double.compare(cacheKey.maxZ, maxZ) != 0) return false;
            return world.equals(cacheKey.world);
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            result = world.hashCode();
            temp = Double.doubleToLongBits(posX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(posY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(posZ);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(minX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(minY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(minZ);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(maxX);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(maxY);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(maxZ);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            return result;
        }
    }
    // Paper end
}
