package net.minecraft.server;

import java.util.ArrayList;
import java.util.List;

// CraftBukkit start
import org.bukkit.BlockChangeDelegate;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.craftbukkit.generator.CustomChunkGenerator;
import org.bukkit.craftbukkit.generator.InternalChunkGenerator;
import org.bukkit.craftbukkit.generator.NetherChunkGenerator;
import org.bukkit.craftbukkit.generator.NormalChunkGenerator;
import org.bukkit.craftbukkit.generator.SkyLandsChunkGenerator;
import org.bukkit.event.weather.LightningStrikeEvent;

public class WorldServer extends World implements BlockChangeDelegate {
    // CraftBukkit end

    public ChunkProviderServer chunkProviderServer;
    public boolean weirdIsOpCache = false;
    public boolean canSave;
    public final MinecraftServer server; // CraftBukkit - private -> public final
    private EntityList G = new EntityList();

    // CraftBukkit start - change signature
    public WorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, String s, int i, long j, org.bukkit.World.Environment env, ChunkGenerator gen) {
        super(idatamanager, s, j, WorldProvider.byDimension(env.getId()), gen, env);
        this.server = minecraftserver;

        this.dimension = i;
        this.pvpMode = minecraftserver.pvpMode;
        this.manager = new PlayerManager(minecraftserver, this.dimension, minecraftserver.propertyManager.getInt("view-distance", 10));
    }

    public final int dimension;
    public EntityTracker tracker;
    public PlayerManager manager;
    // CraftBukkit end

    public void entityJoinedWorld(Entity entity, boolean flag) {
        /* CraftBukkit start - We prevent spawning in general, so this butchering is not needed
        if (!this.server.spawnAnimals && (entity instanceof EntityAnimal || entity instanceof EntityWaterAnimal)) {
            entity.die();
        }
        // CraftBukkit end */

        if (entity.passenger == null || !(entity.passenger instanceof EntityHuman)) {
            super.entityJoinedWorld(entity, flag);
        }
    }

    public void vehicleEnteredWorld(Entity entity, boolean flag) {
        super.entityJoinedWorld(entity, flag);
    }

    protected IChunkProvider b() {
        IChunkLoader ichunkloader = this.w.a(this.worldProvider);

        // CraftBukkit start
        InternalChunkGenerator gen;

        if (this.generator != null) {
            gen = new CustomChunkGenerator(this, this.getSeed(), this.generator);
        } else if (this.worldProvider instanceof WorldProviderHell) {
            gen = new NetherChunkGenerator(this, this.getSeed());
        } else if (this.worldProvider instanceof WorldProviderSky) {
            gen = new SkyLandsChunkGenerator(this, this.getSeed());
        } else {
            gen = new NormalChunkGenerator(this, this.getSeed());
        }

        this.chunkProviderServer = new ChunkProviderServer(this, ichunkloader, gen);
        // CraftBukkit end

        return this.chunkProviderServer;
    }

    public List getTileEntities(int i, int j, int k, int l, int i1, int j1) {
        ArrayList arraylist = new ArrayList();

        for (int k1 = 0; k1 < this.c.size(); ++k1) {
            TileEntity tileentity = (TileEntity) this.c.get(k1);

            if (tileentity.x >= i && tileentity.y >= j && tileentity.z >= k && tileentity.x < l && tileentity.y < i1 && tileentity.z < j1) {
                arraylist.add(tileentity);
            }
        }

        return arraylist;
    }

    public boolean a(EntityHuman entityhuman, int i, int j, int k) {
        int l = (int) MathHelper.abs((float) (i - this.worldData.c()));
        int i1 = (int) MathHelper.abs((float) (k - this.worldData.e()));

        if (l > i1) {
            i1 = l;
        }

        // CraftBukkit - Configurable spawn protection
        return i1 > this.getServer().getSpawnRadius() || this.server.serverConfigurationManager.isOp(entityhuman.name);
    }

    protected void c(Entity entity) {
        super.c(entity);
        this.G.a(entity.id, entity);
    }

    protected void d(Entity entity) {
        super.d(entity);
        this.G.d(entity.id);
    }

    public Entity getEntity(int i) {
        return (Entity) this.G.a(i);
    }

    public boolean strikeLightning(Entity entity) {
        // CraftBukkit start
        LightningStrikeEvent lightning = new LightningStrikeEvent(this.getWorld(), (org.bukkit.entity.LightningStrike) entity.getBukkitEntity());
        this.getServer().getPluginManager().callEvent(lightning);

        if (lightning.isCancelled()) {
            return false;
        }

        if (super.strikeLightning(entity)) {
            this.server.serverConfigurationManager.sendPacketNearby(entity.locX, entity.locY, entity.locZ, 512.0D, this.dimension, new Packet71Weather(entity));
            // CraftBukkit end
            return true;
        } else {
            return false;
        }
    }

    public void a(Entity entity, byte b0) {
        Packet38EntityStatus packet38entitystatus = new Packet38EntityStatus(entity.id, b0);

        // CraftBukkit
        this.server.getTracker(this.dimension).sendPacketToEntity(entity, packet38entitystatus);
    }

    public Explosion createExplosion(Entity entity, double d0, double d1, double d2, float f, boolean flag) {
        // CraftBukkit start
        Explosion explosion = super.createExplosion(entity, d0, d1, d2, f, flag);

        if (explosion.wasCanceled) {
            return explosion;
        }

        /* Remove
        explosion.a = flag;
        explosion.a();
        explosion.a(false);
        */
        this.server.serverConfigurationManager.sendPacketNearby(d0, d1, d2, 64.0D, this.dimension, new Packet60Explosion(d0, d1, d2, f, explosion.blocks));
        // CraftBukkit end
        return explosion;
    }

    public void playNote(int i, int j, int k, int l, int i1) {
        super.playNote(i, j, k, l, i1);
        // CraftBukkit
        this.server.serverConfigurationManager.sendPacketNearby((double) i, (double) j, (double) k, 64.0D, this.dimension, new Packet54PlayNoteBlock(i, j, k, l, i1));
    }

    public void saveLevel() {
        this.w.e();
    }

    protected void i() {
        boolean flag = this.v();

        super.i();
        if (flag != this.v()) {
            // CraftBukkit start - only sending weather packets to those affected
            for (int i = 0; i < this.players.size(); ++i) {
                if (((EntityPlayer) this.players.get(i)).world == this) {
                    ((EntityPlayer) this.players.get(i)).netServerHandler.sendPacket(new Packet70Bed(flag ? 2 : 1));
                }
            }
            // CraftBukkit end
        }
    }
}
