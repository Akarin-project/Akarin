package net.minecraft.server;

import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;

import org.bukkit.craftbukkit.event.CraftEventFactory; // CraftBukkit

public class EntityLightning extends EntityWeather {

    private int lifeTicks;
    public long a;
    private int c;
    private final boolean d;
    @Nullable
    private EntityPlayer e;
    public boolean isEffect; // CraftBukkit

    public EntityLightning(World world, double d0, double d1, double d2, boolean flag) {
        super(EntityTypes.LIGHTNING_BOLT, world);
        this.isEffect = flag; // CraftBukkit
        this.setPositionRotation(d0, d1, d2, 0.0F, 0.0F);
        this.lifeTicks = 2;
        this.a = this.random.nextLong();
        this.c = this.random.nextInt(3) + 1;
        this.d = flag;
        EnumDifficulty enumdifficulty = world.getDifficulty();

        if (enumdifficulty == EnumDifficulty.NORMAL || enumdifficulty == EnumDifficulty.HARD) {
            this.a(4);
        }

    }

    public SoundCategory bV() {
        return SoundCategory.WEATHER;
    }

    public void d(@Nullable EntityPlayer entityplayer) {
        this.e = entityplayer;
    }

    public void tick() {
        super.tick();
        if (this.lifeTicks == 2) {
            // CraftBukkit start - Use relative location for far away sounds
            // this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
            float pitch = 0.8F + this.random.nextFloat() * 0.2F;
            int viewDistance = ((WorldServer) this.world).getServer().getViewDistance() * 16;
            for (EntityPlayer player : (List<EntityPlayer>) (List) this.world.players) {
                double deltaX = this.locX - player.locX;
                double deltaZ = this.locZ - player.locZ;
                double distanceSquared = deltaX * deltaX + deltaZ * deltaZ;
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
            this.world.a((EntityHuman) null, this.locX, this.locY, this.locZ, SoundEffects.ENTITY_LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
        }

        --this.lifeTicks;
        if (this.lifeTicks < 0) {
            if (this.c == 0) {
                this.die();
            } else if (this.lifeTicks < -this.random.nextInt(10)) {
                --this.c;
                this.lifeTicks = 1;
                this.a = this.random.nextLong();
                this.a(0);
            }
        }

        if (this.lifeTicks >= 0 && !this.isEffect) { // CraftBukkit - add !this.isEffect
            if (this.world.isClientSide) {
                this.world.d(2);
            } else if (!this.d) {
                double d0 = 3.0D;
                List<Entity> list = this.world.getEntities(this, new AxisAlignedBB(this.locX - 3.0D, this.locY - 3.0D, this.locZ - 3.0D, this.locX + 3.0D, this.locY + 6.0D + 3.0D, this.locZ + 3.0D));

                for (int i = 0; i < list.size(); ++i) {
                    Entity entity = (Entity) list.get(i);

                    entity.onLightningStrike(this);
                }

                if (this.e != null) {
                    CriterionTriggers.E.a(this.e, (Collection) list);
                }
            }
        }

    }

    private void a(int i) {
        if (!this.d && !this.world.isClientSide && this.world.getGameRules().getBoolean("doFireTick")) {
            IBlockData iblockdata = Blocks.FIRE.getBlockData();
            BlockPosition blockposition = new BlockPosition(this);

            if (this.world.areChunksLoaded(blockposition, 10) && this.world.getType(blockposition).isAir() && iblockdata.canPlace(this.world, blockposition)) {
                // CraftBukkit start - add "!isEffect"
                if (!isEffect && !CraftEventFactory.callBlockIgniteEvent(world, blockposition, this).isCancelled()) {
                    this.world.setTypeUpdate(blockposition, iblockdata);
                }
                // CraftBukkit end
            }

            for (int j = 0; j < i; ++j) {
                BlockPosition blockposition1 = blockposition.a(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);

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

    protected void x_() {}

    protected void a(NBTTagCompound nbttagcompound) {}

    protected void b(NBTTagCompound nbttagcompound) {}
}
