package net.minecraft.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityLightning extends Entity {

    private int lifeTicks;
    public long b;
    private int d;
    private final boolean e;
    @Nullable
    private EntityPlayer f;
    public boolean isEffect; // CraftBukkit
    public boolean isSilent = false; // Spigot

    public EntityLightning(World world, double d0, double d1, double d2, boolean flag) {
        super(EntityTypes.LIGHTNING_BOLT, world);
        this.isEffect = flag; // CraftBukkit
        this.af = true;
        this.setPositionRotation(d0, d1, d2, 0.0F, 0.0F);
        this.lifeTicks = 2;
        this.b = this.random.nextLong();
        this.d = this.random.nextInt(3) + 1;
        this.e = flag;
        EnumDifficulty enumdifficulty = world.getDifficulty();

        if (enumdifficulty == EnumDifficulty.NORMAL || enumdifficulty == EnumDifficulty.HARD) {
            this.a(4);
        }

    }

    // Spigot start
    public EntityLightning(World world, double d0, double d1, double d2, boolean isEffect, boolean isSilent)
    {
        this( world, d0, d1, d2, isEffect );
        this.isSilent = isSilent;
    }
    // Spigot end

    @Override
    public SoundCategory getSoundCategory() {
        return SoundCategory.WEATHER;
    }

    public void d(@Nullable EntityPlayer entityplayer) {
        this.f = entityplayer;
    }

    @Override
    public void tick() {
        super.tick();
        if (!isSilent && this.lifeTicks == 2) { // Spigot
            // CraftBukkit start - Use relative location for far away sounds
            // this.world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
            float pitch = 0.8F + this.random.nextFloat() * 0.2F;
            int viewDistance = ((WorldServer) this.world).getServer().getViewDistance() * 16;
            for (EntityPlayer player : (List<EntityPlayer>) (List) this.world.getPlayers()) {
                double deltaX = this.locX - player.locX;
                double deltaZ = this.locZ - player.locZ;
                double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
                // Paper start - Limit lightning strike effect distance
                if (distanceSquared <= this.world.paperConfig.sqrMaxLightningImpactSoundDistance) {
                    player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.ENTITY_LIGHTNING_BOLT_IMPACT,
                            SoundCategory.WEATHER, this.locX, this.locY, this.locZ, 2.0f, 0.5F + this.random.nextFloat() * 0.2F));
                }

                if (world.paperConfig.sqrMaxThunderDistance != -1 && distanceSquared >= world.paperConfig.sqrMaxThunderDistance) {
                    continue;
                }

                // Paper end
                if (distanceSquared > viewDistance * viewDistance) {
                    double deltaLength = Math.sqrt(distanceSquared);
                    double relativeX = player.locX + (deltaX / deltaLength) * viewDistance;
                    double relativeZ = player.locZ + (deltaZ / deltaLength) * viewDistance;
                    player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, relativeX, this.locY, relativeZ, 10000.0F, pitch));
                } else {
                    player.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, this.locX, this.locY, this.locZ, 10000.0F, pitch));
                }
            }
            // CraftBukkit end
            //this.world.playSound((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F); // Paper - Limit lightning strike effect distance (the packet is now sent from inside the loop)
        }

        --this.lifeTicks;
        if (this.lifeTicks < 0) {
            if (this.d == 0) {
                this.die();
            } else if (this.lifeTicks < -this.random.nextInt(10)) {
                --this.d;
                this.lifeTicks = 1;
                this.b = this.random.nextLong();
                this.a(0);
            }
        }

        if (this.lifeTicks >= 0 && !this.isEffect) { // CraftBukkit - add !this.isEffect
            if (this.world.isClientSide) {
                this.world.c(2);
            } else if (!this.e) {
                double d0 = 3.0D;
                List<Entity> list = this.world.getEntities(this, new AxisAlignedBB(this.locX - 3.0D, this.locY - 3.0D, this.locZ - 3.0D, this.locX + 3.0D, this.locY + 6.0D + 3.0D, this.locZ + 3.0D), Entity::isAlive);
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    Entity entity = (Entity) iterator.next();

                    entity.onLightningStrike(this);
                }

                if (this.f != null) {
                    CriterionTriggers.E.a(this.f, (Collection) list);
                }
            }
        }

    }

    private void a(int i) {
        if (!this.e && !this.world.isClientSide && this.world.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            IBlockData iblockdata = Blocks.FIRE.getBlockData();
            BlockPosition blockposition = new BlockPosition(this);

            if (this.world.getType(blockposition).isAir() && iblockdata.canPlace(this.world, blockposition)) {
                // CraftBukkit start - add "!isEffect"
                if (!isEffect && !CraftEventFactory.callBlockIgniteEvent(world, blockposition, this).isCancelled()) {
                    this.world.setTypeUpdate(blockposition, iblockdata);
                }
                // CraftBukkit end
            }

            for (int j = 0; j < i; ++j) {
                BlockPosition blockposition1 = blockposition.b(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);

                if (this.world.getType(blockposition1).isAir() && iblockdata.canPlace(this.world, blockposition1)) {
                    // CraftBukkit start - add "!isEffect"
                    if (!isEffect && !CraftEventFactory.callBlockIgniteEvent(world, blockposition1, this).isCancelled()) {
                        this.world.setTypeUpdate(blockposition1, iblockdata);
                    }
                    // CraftBukkit end
                }
            }

        }
    }

    @Override
    protected void initDatawatcher() {}

    @Override
    protected void a(NBTTagCompound nbttagcompound) {}

    @Override
    protected void b(NBTTagCompound nbttagcompound) {}

    @Override
    public Packet<?> N() {
        return new PacketPlayOutSpawnEntityWeather(this);
    }
}
