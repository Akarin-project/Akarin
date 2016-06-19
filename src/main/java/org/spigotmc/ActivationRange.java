package org.spigotmc;

import java.util.List;
import java.util.Set;

import co.aikar.timings.MinecraftTimings;
import net.minecraft.server.AxisAlignedBB;
import net.minecraft.server.Chunk;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityAmbient;
import net.minecraft.server.EntityAnimal;
import net.minecraft.server.EntityArrow;
import net.minecraft.server.EntityComplexPart;
import net.minecraft.server.EntityCreature;
import net.minecraft.server.EntityCreeper;
import net.minecraft.server.EntityEnderCrystal;
import net.minecraft.server.EntityEnderDragon;
import net.minecraft.server.EntityFallingBlock;
import net.minecraft.server.EntityFireball;
import net.minecraft.server.EntityFireworks;
import net.minecraft.server.EntityFish;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityInsentient;
import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityLlama;
import net.minecraft.server.EntityMonster;
import net.minecraft.server.EntityProjectile;
import net.minecraft.server.EntitySheep;
import net.minecraft.server.EntitySlime;
import net.minecraft.server.EntityTNTPrimed;
import net.minecraft.server.EntityThrownTrident;
import net.minecraft.server.EntityVillager;
import net.minecraft.server.EntityWaterAnimal;
import net.minecraft.server.EntityWeather;
import net.minecraft.server.EntityWither;
import net.minecraft.server.MCUtil;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.NavigationGuardian;
import net.minecraft.server.World;

public class ActivationRange
{

    static AxisAlignedBB maxBB = new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB miscBB = new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB animalBB = new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );
    static AxisAlignedBB waterBB = new AxisAlignedBB( 0, 0, 0, 0, 0, 0 ); // Paper
    static AxisAlignedBB monsterBB = new AxisAlignedBB( 0, 0, 0, 0, 0, 0 );

    /**
     * Initializes an entities type on construction to specify what group this
     * entity is in for activation ranges.
     *
     * @param entity
     * @return group id
     */
    public static byte initializeEntityActivationType(Entity entity)
    {
        if (entity instanceof EntityWaterAnimal) { return 4; } // Paper
        if ( entity instanceof EntityMonster || entity instanceof EntitySlime )
        {
            return 1; // Monster
        } else if ( entity instanceof EntityCreature || entity instanceof EntityAmbient )
        {
            return 2; // Animal
        } else
        {
            return 3; // Misc
        }
    }

    /**
     * These entities are excluded from Activation range checks.
     *
     * @param entity Entity to initialize
     * @param config Spigot config to determine ranges
     * @return boolean If it should always tick.
     */
    public static boolean initializeEntityActivationState(Entity entity, SpigotWorldConfig config)
    {
        if ( ( entity.activationType == 3 && config.miscActivationRange == 0 )
                || ( entity.activationType == 2 && config.animalActivationRange == 0 )
                || ( entity.activationType == 1 && config.monsterActivationRange == 0 )
                || ( entity.activationType == 4 && config.waterActivationRange == 0 ) // Paper
                || entity instanceof EntityHuman
                || entity instanceof EntityProjectile
                || entity instanceof EntityEnderDragon
                || entity instanceof EntityComplexPart
                || entity instanceof EntityWither
                || entity instanceof EntityFireball
                || entity instanceof EntityWeather
                || entity instanceof EntityTNTPrimed
                || entity instanceof EntityFallingBlock // Paper - Always tick falling blocks
                || entity instanceof EntityEnderCrystal
                || entity instanceof EntityFireworks
                || entity instanceof EntityThrownTrident )
        {
            return true;
        }

        return false;
    }

    /**
     * Find what entities are in range of the players in the world and set
     * active if in range.
     *
     * @param world
     */
    public static void activateEntities(World world)
    {
        MinecraftTimings.entityActivationCheckTimer.startTiming();
        final int miscActivationRange = world.spigotConfig.miscActivationRange;
        final int animalActivationRange = world.spigotConfig.animalActivationRange;
        final int monsterActivationRange = world.spigotConfig.monsterActivationRange;
        final int waterActivationRange = world.spigotConfig.waterActivationRange; // Paper

        int maxRange = Math.max( monsterActivationRange, animalActivationRange );
        maxRange = Math.max( maxRange, miscActivationRange );
        //maxRange = Math.min( ( world.spigotConfig.viewDistance << 4 ) - 8, maxRange ); Paper - Use player view distance API below instead

        Chunk chunk; // Paper
        for ( EntityHuman player : world.players )
        {
            int playerMaxRange = maxRange = Math.min( ( player.getViewDistance() << 4 ) - 8, maxRange ); // Paper - Use player view distance API
            player.activatedTick = MinecraftServer.currentTick;
            maxBB = player.getBoundingBox().grow( playerMaxRange, 256, playerMaxRange ); // Paper - Use player view distance API
            miscBB = player.getBoundingBox().grow( miscActivationRange, 256, miscActivationRange );
            animalBB = player.getBoundingBox().grow( animalActivationRange, 256, animalActivationRange );
            waterBB = player.getBoundingBox().grow( waterActivationRange, 256, waterActivationRange ); // Paper
            monsterBB = player.getBoundingBox().grow( monsterActivationRange, 256, monsterActivationRange );

            int i = MathHelper.floor( maxBB.minX / 16.0D );
            int j = MathHelper.floor( maxBB.maxX / 16.0D );
            int k = MathHelper.floor( maxBB.minZ / 16.0D );
            int l = MathHelper.floor( maxBB.maxZ / 16.0D );

            for ( int i1 = i; i1 <= j; ++i1 )
            {
                for ( int j1 = k; j1 <= l; ++j1 )
                {
                    if ( (chunk = world.getChunkIfLoaded(i1, j1 )) != null ) // Paper
                    {
                        activateChunkEntities( chunk ); // Paper
                    }
                }
            }
        }
        MinecraftTimings.entityActivationCheckTimer.stopTiming();
    }

    /**
     * Checks for the activation state of all entities in this chunk.
     *
     * @param chunk
     */
    private static void activateChunkEntities(Chunk chunk)
    {
        for ( List<Entity> slice : chunk.entitySlices )
        {
            for ( Entity entity : slice )
            {
                if ( MinecraftServer.currentTick > entity.activatedTick )
                {
                    if ( entity.defaultActivationState )
                    {
                        entity.activatedTick = MinecraftServer.currentTick;
                        continue;
                    }
                    switch ( entity.activationType )
                    {
                        case 1:
                            if ( monsterBB.c( entity.getBoundingBox() ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                        case 2:
                            if ( animalBB.c( entity.getBoundingBox() ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                            // Paper start
                        case 4:
                            if ( waterBB.c( entity.getBoundingBox() ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                            break;
                            // Paper end
                        case 3:
                        default:
                            if ( miscBB.c( entity.getBoundingBox() ) )
                            {
                                entity.activatedTick = MinecraftServer.currentTick;
                            }
                    }
                }
            }
        }
    }

    /**
     * If an entity is not in range, do some more checks to see if we should
     * give it a shot.
     *
     * @param entity
     * @return
     */
    public static boolean checkEntityImmunities(Entity entity)
    {
        // Paper start - optimize Water cases
        if (entity instanceof EntityFish) {
            return false;
        }
        if (entity.inWater && (!(entity instanceof EntityInsentient) || !(((EntityInsentient) entity).getNavigation() instanceof NavigationGuardian))) {
            return true;
        }
        if (entity.fireTicks > 0) {
            return true;
        }
        // Paper end
        if ( !( entity instanceof EntityArrow ) )
        {
            if ( !entity.onGround || !entity.passengers.isEmpty() || entity.isPassenger() )
            {
                return true;
            }
        } else if ( !( (EntityArrow) entity ).inGround )
        {
            return true;
        }
        // special cases.
        if ( entity instanceof EntityLiving )
        {
            EntityLiving living = (EntityLiving) entity;
            if ( living.lastDamageByPlayerTime > 0 || living.hurtTicks > 0 || living.effects.size() > 0 ) // Paper
            {
                return true;
            }
            if ( entity instanceof EntityCreature )
            {
                // Paper start
                EntityCreature creature = (EntityCreature) entity;
                if (creature.getGoalTarget() != null || creature.getMovingTarget() != null) {
                    return true;
                }
                // Paper end
            }
            if ( entity instanceof EntityVillager && ( (EntityVillager) entity ).isInLove() )
            {
                return true;
            }
            // Paper start
            if ( entity instanceof EntityLlama && ( (EntityLlama ) entity ).inCaravan() )
            {
                return true;
            }
            // Paper end
            if ( entity instanceof EntityAnimal )
            {
                EntityAnimal animal = (EntityAnimal) entity;
                if ( animal.isBaby() || animal.isInLove() )
                {
                    return true;
                }
                if ( entity instanceof EntitySheep && ( (EntitySheep) entity ).isSheared() )
                {
                    return true;
                }
            }
            if (entity instanceof EntityCreeper && ((EntityCreeper) entity).isIgnited()) { // isExplosive
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the entity is active for this tick.
     *
     * @param entity
     * @return
     */
    public static boolean checkIfActive(Entity entity)
    {
        // Never safe to skip fireworks or entities not yet added to chunk
        if ( !entity.inChunk || entity instanceof EntityFireworks ) {
            return true;
        }

        boolean isActive = entity.activatedTick >= MinecraftServer.currentTick || entity.defaultActivationState;

        // Should this entity tick?
        if ( !isActive )
        {
            if ( ( MinecraftServer.currentTick - entity.activatedTick - 1 ) % 20 == 0 )
            {
                // Check immunities every 20 ticks.
                if ( checkEntityImmunities( entity ) )
                {
                    // Triggered some sort of immunity, give 20 full ticks before we check again.
                    entity.activatedTick = MinecraftServer.currentTick + 20;
                }
                isActive = true;
                // Paper start
            } else if (entity instanceof EntityInsentient && ((EntityInsentient) entity).targetSelector.hasTasks()) {
                isActive = true;
            }
            // Paper end
            // Add a little performance juice to active entities. Skip 1/4 if not immune.
        } else if ( !entity.defaultActivationState && entity.ticksLived % 4 == 0 && !(entity instanceof EntityInsentient && ((EntityInsentient) entity).targetSelector.hasTasks()) && !checkEntityImmunities( entity ) ) // Paper - add targetSelector.hasTasks
        {
            isActive = false;
        }
        //int x = MathHelper.floor( entity.locX ); // Paper
        //int z = MathHelper.floor( entity.locZ ); // Paper
        // Make sure not on edge of unloaded chunk
        Chunk chunk = entity.getChunkAtLocation(); // Paper
        if ( isActive && !( chunk != null && chunk.areNeighborsLoaded( 1 ) ) )
        {
            isActive = false;
        }
        // Paper start - Skip ticking in chunks scheduled for unload
        else if (entity.world.paperConfig.skipEntityTickingInChunksScheduledForUnload && (chunk == null || chunk.scheduledForUnload != null)) {
            isActive = false;
        }
        // Paper end
        return isActive;
    }
}
