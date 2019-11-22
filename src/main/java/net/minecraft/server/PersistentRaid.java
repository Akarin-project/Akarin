package net.minecraft.server;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class PersistentRaid extends PersistentBase {

    public final Map<Integer, Raid> a = Maps.newHashMap(); // PAIL rename raids, private -> public
    private final WorldServer b;
    private int c;
    private int d;

    public PersistentRaid(WorldServer worldserver) {
        super(a(worldserver.worldProvider));
        this.b = worldserver;
        this.c = 1;
        this.b();
    }

    public Raid a(int i) {
        return (Raid) this.a.get(i);
    }

    public void a() {
        ++this.d;
        Iterator iterator = this.a.values().iterator();

        while (iterator.hasNext()) {
            Raid raid = (Raid) iterator.next();

            if (this.b.getGameRules().getBoolean(GameRules.x)) {
                raid.n();
            }

            if (raid.d()) {
                iterator.remove();
                this.b();
            } else {
                raid.o();
            }
        }

        if (this.d % 200 == 0) {
            this.b();
        }

        PacketDebug.a(this.b, this.a.values());
    }

    public static boolean a(EntityRaider entityraider, Raid raid) {
        return entityraider != null && raid != null && raid.i() != null ? entityraider.isAlive() && entityraider.ei() && entityraider.cw() <= 2400 && entityraider.world.getWorldProvider().getDimensionManager() == raid.i().getWorldProvider().getDimensionManager() : false;
    }

    @Nullable
    public Raid a(EntityPlayer entityplayer) {
        if (entityplayer.isSpectator()) {
            return null;
        } else if (this.b.getGameRules().getBoolean(GameRules.x)) {
            return null;
        } else {
            DimensionManager dimensionmanager = entityplayer.world.getWorldProvider().getDimensionManager();

            if (dimensionmanager == DimensionManager.NETHER) {
                return null;
            } else {
                BlockPosition blockposition = new BlockPosition(entityplayer);
                List<VillagePlaceRecord> list = (List) this.b.B().b(VillagePlaceType.a, blockposition, 64, VillagePlace.Occupancy.IS_OCCUPIED).collect(Collectors.toList());
                int i = 0;
                Vec3D vec3d = new Vec3D(0.0D, 0.0D, 0.0D);

                for (Iterator iterator = list.iterator(); iterator.hasNext(); ++i) {
                    VillagePlaceRecord villageplacerecord = (VillagePlaceRecord) iterator.next();
                    BlockPosition blockposition1 = villageplacerecord.f();

                    vec3d = vec3d.add((double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());
                }

                BlockPosition blockposition2;

                if (i > 0) {
                    vec3d = vec3d.a(1.0D / (double) i);
                    blockposition2 = new BlockPosition(vec3d);
                } else {
                    blockposition2 = blockposition;
                }

                Raid raid = this.a(entityplayer.getWorldServer(), blockposition2);
                boolean flag = false;

                if (!raid.j()) {
                    /* CraftBukkit - moved down
                    if (!this.a.containsKey(raid.u())) {
                        this.a.put(raid.u(), raid);
                    }
                    */

                    flag = true;
                    // CraftBukkit start - fixed a bug with raid: players could add up Bad Omen level even when the raid had finished
                } else if (raid.isInProgress() && raid.m() < raid.l()) {
                    flag = true;
                    // CraftBukkit end
                } else {
                    entityplayer.removeEffect(MobEffects.BAD_OMEN);
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) 43));
                }

                if (flag) {
                    // CraftBukkit start
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.callRaidTriggerEvent(raid, entityplayer)) {
                        entityplayer.removeEffect(MobEffects.BAD_OMEN);
                        return null;
                    }

                    if (!this.a.containsKey(raid.u())) {
                        this.a.put(raid.u(), raid);
                    }
                    // CraftBukkit end
                    raid.a((EntityHuman) entityplayer);
                    entityplayer.playerConnection.sendPacket(new PacketPlayOutEntityStatus(entityplayer, (byte) 43));
                    if (!raid.c()) {
                        entityplayer.a(StatisticList.RAID_TRIGGER);
                        CriterionTriggers.I.a(entityplayer);
                    }
                }

                this.b();
                return raid;
            }
        }
    }

    private Raid a(WorldServer worldserver, BlockPosition blockposition) {
        Raid raid = worldserver.c_(blockposition);

        return raid != null ? raid : new Raid(this.e(), worldserver, blockposition);
    }

    @Override
    public void a(NBTTagCompound nbttagcompound) {
        this.c = nbttagcompound.getInt("NextAvailableID");
        this.d = nbttagcompound.getInt("Tick");
        NBTTagList nbttaglist = nbttagcompound.getList("Raids", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            Raid raid = new Raid(this.b, nbttagcompound1);

            this.a.put(raid.u(), raid);
        }

    }

    @Override
    public NBTTagCompound b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("NextAvailableID", this.c);
        nbttagcompound.setInt("Tick", this.d);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.a.values().iterator();

        while (iterator.hasNext()) {
            Raid raid = (Raid) iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            raid.a(nbttagcompound1);
            nbttaglist.add(nbttagcompound1);
        }

        nbttagcompound.set("Raids", nbttaglist);
        return nbttagcompound;
    }

    public static String a(WorldProvider worldprovider) {
        return "raids" + worldprovider.getDimensionManager().getSuffix();
    }

    private int e() {
        return ++this.c;
    }

    @Nullable
    public Raid a(BlockPosition blockposition, int i) {
        Raid raid = null;
        double d0 = (double) i;
        Iterator iterator = this.a.values().iterator();

        while (iterator.hasNext()) {
            Raid raid1 = (Raid) iterator.next();
            double d1 = raid1.t().m(blockposition);

            if (raid1.v() && d1 < d0) {
                raid = raid1;
                d0 = d1;
            }
        }

        return raid;
    }
}
