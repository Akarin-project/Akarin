package net.minecraft.server;

import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonBattle {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Predicate<Entity> b = IEntitySelector.a.and(IEntitySelector.a(0.0D, 128.0D, 0.0D, 192.0D));
    public final BossBattleServer bossBattle;
    private final WorldServer d;
    private final List<Integer> e;
    private final ShapeDetector f;
    private int g;
    private int h;
    private int i;
    private int j;
    private boolean k; private void setDragonKilled(boolean dragonKilled) { this.k = dragonKilled; } // Paper - OBFHELPER
    private boolean l;
    private UUID m;
    private boolean n;  private void setScanForLegacyFight(boolean scanForLegacyFight) { this.n = scanForLegacyFight; } private boolean scanForLegacyFight() { return this.n; } // Paper - OBFHELPER
    private BlockPosition o;
    private EnumDragonRespawn p;
    private int q;
    private List<EntityEnderCrystal> r;

    public EnderDragonBattle(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        this.bossBattle = (BossBattleServer) (new BossBattleServer(new ChatMessage("entity.minecraft.ender_dragon", new Object[0]), BossBattle.BarColor.PINK, BossBattle.BarStyle.PROGRESS)).setPlayMusic(true).c(true);
        this.e = Lists.newArrayList();
        this.n = true;
        // Paper start
        setScanForLegacyFight(worldserver.paperConfig.scanForLegacyEnderDragon);
        if (!scanForLegacyFight()) setDragonKilled(true);
        // Paper end
        this.d = worldserver;
        if (nbttagcompound.hasKeyOfType("DragonKilled", 99)) {
            if (nbttagcompound.b("DragonUUID")) {
                this.m = nbttagcompound.a("DragonUUID");
            }

            this.k = nbttagcompound.getBoolean("DragonKilled");
            this.l = nbttagcompound.getBoolean("PreviouslyKilled");
            if (nbttagcompound.getBoolean("IsRespawning")) {
                this.p = EnumDragonRespawn.START;
            }

            if (nbttagcompound.hasKeyOfType("ExitPortalLocation", 10)) {
                this.o = GameProfileSerializer.c(nbttagcompound.getCompound("ExitPortalLocation"));
            }
        } else {
            this.k = true;
            this.l = true;
        }

        if (nbttagcompound.hasKeyOfType("Gateways", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Gateways", 3);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                this.e.add(nbttaglist.e(i));
            }
        } else {
            this.e.addAll(ContiguousSet.create(Range.closedOpen(0, 20), DiscreteDomain.integers()));
            Collections.shuffle(this.e, new Random(worldserver.getSeed()));
        }

        this.f = ShapeDetectorBuilder.a().a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").a("       ", "       ", "       ", "   #   ", "       ", "       ", "       ").a("  ###  ", " #   # ", "#     #", "#  #  #", "#     #", " #   # ", "  ###  ").a("       ", "  ###  ", " ##### ", " ##### ", " ##### ", "  ###  ", "       ").a('#', ShapeDetectorBlock.a(BlockPredicate.a(Blocks.BEDROCK))).b();
    }

    public NBTTagCompound a() {
        NBTTagCompound nbttagcompound = new NBTTagCompound();

        if (this.m != null) {
            nbttagcompound.a("DragonUUID", this.m);
        }

        nbttagcompound.setBoolean("DragonKilled", this.k);
        nbttagcompound.setBoolean("PreviouslyKilled", this.l);
        if (this.o != null) {
            nbttagcompound.set("ExitPortalLocation", GameProfileSerializer.a(this.o));
        }

        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.e.iterator();

        while (iterator.hasNext()) {
            int i = (Integer) iterator.next();

            nbttaglist.add(new NBTTagInt(i));
        }

        nbttagcompound.set("Gateways", nbttaglist);
        return nbttagcompound;
    }

    public void b() {
        this.bossBattle.setVisible(!this.k);
        if (++this.j >= 20) {
            this.l();
            this.j = 0;
        }

        if (!this.bossBattle.getPlayers().isEmpty()) {
            this.d.getChunkProvider().addTicket(TicketType.DRAGON, new ChunkCoordIntPair(0, 0), 9, Unit.INSTANCE);
            boolean flag = this.k();

            if (this.n && flag) {
                this.g();
                this.n = false;
            }

            if (this.p != null) {
                if (this.r == null && flag) {
                    this.p = null;
                    this.e();
                }

                this.p.a(this.d, this, this.r, this.q++, this.o);
            }

            if (!this.k) {
                if ((this.m == null || ++this.g >= 1200) && flag) {
                    this.h();
                    this.g = 0;
                }

                if (++this.i >= 100 && flag) {
                    this.m();
                    this.i = 0;
                }
            }
        } else {
            this.d.getChunkProvider().removeTicket(TicketType.DRAGON, new ChunkCoordIntPair(0, 0), 9, Unit.INSTANCE);
        }

    }

    private void g() {
        EnderDragonBattle.LOGGER.info("Scanning for legacy world dragon fight...");
        boolean flag = this.i();

        if (flag) {
            EnderDragonBattle.LOGGER.info("Found that the dragon has been killed in this world already.");
            this.l = true;
        } else {
            EnderDragonBattle.LOGGER.info("Found that the dragon has not yet been killed in this world.");
            this.l = false;
            this.a(false);
        }

        List<EntityEnderDragon> list = this.d.j();

        if (list.isEmpty()) {
            this.k = true;
        } else {
            EntityEnderDragon entityenderdragon = (EntityEnderDragon) list.get(0);

            this.m = entityenderdragon.getUniqueID();
            EnderDragonBattle.LOGGER.info("Found that there's a dragon still alive ({})", entityenderdragon);
            this.k = false;
            if (!flag) {
                EnderDragonBattle.LOGGER.info("But we didn't have a portal, let's remove it.");
                entityenderdragon.die();
                this.m = null;
            }
        }

        if (!this.l && this.k) {
            this.k = false;
        }

    }

    private void h() {
        List<EntityEnderDragon> list = this.d.j();

        if (list.isEmpty()) {
            EnderDragonBattle.LOGGER.debug("Haven't seen the dragon, respawning it");
            this.o();
        } else {
            EnderDragonBattle.LOGGER.debug("Haven't seen our dragon, but found another one to use.");
            this.m = ((EntityEnderDragon) list.get(0)).getUniqueID();
        }

    }

    protected void a(EnumDragonRespawn enumdragonrespawn) {
        if (this.p == null) {
            throw new IllegalStateException("Dragon respawn isn't in progress, can't skip ahead in the animation.");
        } else {
            this.q = 0;
            if (enumdragonrespawn == EnumDragonRespawn.END) {
                this.p = null;
                this.k = false;
                EntityEnderDragon entityenderdragon = this.o();
                Iterator iterator = this.bossBattle.getPlayers().iterator();

                while (iterator.hasNext()) {
                    EntityPlayer entityplayer = (EntityPlayer) iterator.next();

                    CriterionTriggers.n.a(entityplayer, (Entity) entityenderdragon);
                }
            } else {
                this.p = enumdragonrespawn;
            }

        }
    }

    private boolean i() {
        for (int i = -8; i <= 8; ++i) {
            int j = -8;

            label27:
            while (j <= 8) {
                Chunk chunk = this.d.getChunkAt(i, j);
                Iterator iterator = chunk.getTileEntities().values().iterator();

                TileEntity tileentity;

                do {
                    if (!iterator.hasNext()) {
                        ++j;
                        continue label27;
                    }

                    tileentity = (TileEntity) iterator.next();
                } while (!(tileentity instanceof TileEntityEnderPortal));

                return true;
            }
        }

        return false;
    }

    @Nullable
    private ShapeDetector.ShapeDetectorCollection j() {
        int i;
        int j;

        for (i = -8; i <= 8; ++i) {
            for (j = -8; j <= 8; ++j) {
                Chunk chunk = this.d.getChunkAt(i, j);
                Iterator iterator = chunk.getTileEntities().values().iterator();

                while (iterator.hasNext()) {
                    TileEntity tileentity = (TileEntity) iterator.next();

                    if (tileentity instanceof TileEntityEnderPortal) {
                        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.f.a(this.d, tileentity.getPosition());

                        if (shapedetector_shapedetectorcollection != null) {
                            BlockPosition blockposition = shapedetector_shapedetectorcollection.a(3, 3, 3).getPosition();

                            if (this.o == null && blockposition.getX() == 0 && blockposition.getZ() == 0) {
                                this.o = blockposition;
                            }

                            return shapedetector_shapedetectorcollection;
                        }
                    }
                }
            }
        }

        i = this.d.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, WorldGenEndTrophy.a).getY();

        for (j = i; j >= 0; --j) {
            ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection1 = this.f.a(this.d, new BlockPosition(WorldGenEndTrophy.a.getX(), j, WorldGenEndTrophy.a.getZ()));

            if (shapedetector_shapedetectorcollection1 != null) {
                if (this.o == null) {
                    this.o = shapedetector_shapedetectorcollection1.a(3, 3, 3).getPosition();
                }

                return shapedetector_shapedetectorcollection1;
            }
        }

        return null;
    }

    private boolean k() {
        for (int i = -8; i <= 8; ++i) {
            for (int j = 8; j <= 8; ++j) {
                IChunkAccess ichunkaccess = this.d.getChunkAt(i, j, ChunkStatus.FULL, false);

                if (!(ichunkaccess instanceof Chunk)) {
                    return false;
                }

                PlayerChunk.State playerchunk_state = ((Chunk) ichunkaccess).getState();

                if (!playerchunk_state.isAtLeast(PlayerChunk.State.TICKING)) {
                    return false;
                }
            }
        }

        return true;
    }

    private void l() {
        Set<EntityPlayer> set = Sets.newHashSet();
        Iterator iterator = this.d.a(EnderDragonBattle.b).iterator();

        while (iterator.hasNext()) {
            EntityPlayer entityplayer = (EntityPlayer) iterator.next();

            this.bossBattle.addPlayer(entityplayer);
            set.add(entityplayer);
        }

        Set<EntityPlayer> set1 = Sets.newHashSet(this.bossBattle.getPlayers());

        set1.removeAll(set);
        Iterator iterator1 = set1.iterator();

        while (iterator1.hasNext()) {
            EntityPlayer entityplayer1 = (EntityPlayer) iterator1.next();

            this.bossBattle.removePlayer(entityplayer1);
        }

    }

    private void m() {
        this.i = 0;
        this.h = 0;

        WorldGenEnder.Spike worldgenender_spike;

        for (Iterator iterator = WorldGenEnder.a((GeneratorAccess) this.d).iterator(); iterator.hasNext(); this.h += this.d.a(EntityEnderCrystal.class, worldgenender_spike.f()).size()) {
            worldgenender_spike = (WorldGenEnder.Spike) iterator.next();
        }

        EnderDragonBattle.LOGGER.debug("Found {} end crystals still alive", this.h);
    }

    public void a(EntityEnderDragon entityenderdragon) {
        if (entityenderdragon.getUniqueID().equals(this.m)) {
            this.bossBattle.setProgress(0.0F);
            this.bossBattle.setVisible(false);
            this.a(true);
            this.n();
            if (!this.l) {
                this.d.setTypeUpdate(this.d.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING, WorldGenEndTrophy.a), Blocks.DRAGON_EGG.getBlockData());
            }

            this.l = true;
            this.k = true;
        }

    }

    private void n() {
        if (!this.e.isEmpty()) {
            int i = (Integer) this.e.remove(this.e.size() - 1);
            int j = MathHelper.floor(96.0D * Math.cos(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double) i)));
            int k = MathHelper.floor(96.0D * Math.sin(2.0D * (-3.141592653589793D + 0.15707963267948966D * (double) i)));

            this.a(new BlockPosition(j, 75, k));
        }
    }

    private void a(BlockPosition blockposition) {
        this.d.triggerEffect(3000, blockposition, 0);
        WorldGenerator.END_GATEWAY.generate(this.d, this.d.getChunkProvider().getChunkGenerator(), new Random(), blockposition, WorldGenEndGatewayConfiguration.a());
    }

    private void a(boolean flag) {
        WorldGenEndTrophy worldgenendtrophy = new WorldGenEndTrophy(flag);

        if (this.o == null) {
            for (this.o = this.d.getHighestBlockYAt(HeightMap.Type.MOTION_BLOCKING_NO_LEAVES, WorldGenEndTrophy.a).down(); this.d.getType(this.o).getBlock() == Blocks.BEDROCK && this.o.getY() > this.d.getSeaLevel(); this.o = this.o.down()) {
                ;
            }
        }

        worldgenendtrophy.a(this.d, this.d.getChunkProvider().getChunkGenerator(), new Random(), this.o, WorldGenFeatureConfiguration.e);
    }

    private EntityEnderDragon o() {
        this.d.getChunkAtWorldCoords(new BlockPosition(0, 128, 0));
        EntityEnderDragon entityenderdragon = (EntityEnderDragon) EntityTypes.ENDER_DRAGON.a((World) this.d);

        entityenderdragon.getDragonControllerManager().setControllerPhase(DragonControllerPhase.HOLDING_PATTERN);
        entityenderdragon.setPositionRotation(0.0D, 128.0D, 0.0D, this.d.random.nextFloat() * 360.0F, 0.0F);
        this.d.addEntity(entityenderdragon);
        this.m = entityenderdragon.getUniqueID();
        return entityenderdragon;
    }

    public void b(EntityEnderDragon entityenderdragon) {
        if (entityenderdragon.getUniqueID().equals(this.m)) {
            this.bossBattle.setProgress(entityenderdragon.getHealth() / entityenderdragon.getMaxHealth());
            this.g = 0;
            if (entityenderdragon.hasCustomName()) {
                this.bossBattle.a(entityenderdragon.getScoreboardDisplayName());
            }
        }

    }

    public int c() {
        return this.h;
    }

    public void a(EntityEnderCrystal entityendercrystal, DamageSource damagesource) {
        if (this.p != null && this.r.contains(entityendercrystal)) {
            EnderDragonBattle.LOGGER.debug("Aborting respawn sequence");
            this.p = null;
            this.q = 0;
            this.f();
            this.a(true);
        } else {
            this.m();
            Entity entity = this.d.getEntity(this.m);

            if (entity instanceof EntityEnderDragon) {
                ((EntityEnderDragon) entity).a(entityendercrystal, new BlockPosition(entityendercrystal), damagesource);
            }
        }

    }

    public boolean d() {
        return this.l;
    }

    public void e() {
        if (this.k && this.p == null) {
            BlockPosition blockposition = this.o;

            if (blockposition == null) {
                EnderDragonBattle.LOGGER.debug("Tried to respawn, but need to find the portal first.");
                ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.j();

                if (shapedetector_shapedetectorcollection == null) {
                    EnderDragonBattle.LOGGER.debug("Couldn't find a portal, so we made one.");
                    this.a(true);
                } else {
                    EnderDragonBattle.LOGGER.debug("Found the exit portal & temporarily using it.");
                }

                blockposition = this.o;
            }

            List<EntityEnderCrystal> list = Lists.newArrayList();
            BlockPosition blockposition1 = blockposition.up(1);
            Iterator iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

            while (iterator.hasNext()) {
                EnumDirection enumdirection = (EnumDirection) iterator.next();
                List<EntityEnderCrystal> list1 = this.d.a(EntityEnderCrystal.class, new AxisAlignedBB(blockposition1.shift(enumdirection, 2)));

                if (list1.isEmpty()) {
                    return;
                }

                list.addAll(list1);
            }

            EnderDragonBattle.LOGGER.debug("Found all crystals, respawning dragon.");
            this.a((List) list);
        }

    }

    private void a(List<EntityEnderCrystal> list) {
        if (this.k && this.p == null) {
            for (ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.j(); shapedetector_shapedetectorcollection != null; shapedetector_shapedetectorcollection = this.j()) {
                for (int i = 0; i < this.f.c(); ++i) {
                    for (int j = 0; j < this.f.b(); ++j) {
                        for (int k = 0; k < this.f.a(); ++k) {
                            ShapeDetectorBlock shapedetectorblock = shapedetector_shapedetectorcollection.a(i, j, k);

                            if (shapedetectorblock.a().getBlock() == Blocks.BEDROCK || shapedetectorblock.a().getBlock() == Blocks.END_PORTAL) {
                                this.d.setTypeUpdate(shapedetectorblock.getPosition(), Blocks.END_STONE.getBlockData());
                            }
                        }
                    }
                }
            }

            this.p = EnumDragonRespawn.START;
            this.q = 0;
            this.a(false);
            this.r = list;
        }

    }

    public void f() {
        Iterator iterator = WorldGenEnder.a((GeneratorAccess) this.d).iterator();

        while (iterator.hasNext()) {
            WorldGenEnder.Spike worldgenender_spike = (WorldGenEnder.Spike) iterator.next();
            List<EntityEnderCrystal> list = this.d.a(EntityEnderCrystal.class, worldgenender_spike.f());
            Iterator iterator1 = list.iterator();

            while (iterator1.hasNext()) {
                EntityEnderCrystal entityendercrystal = (EntityEnderCrystal) iterator1.next();

                entityendercrystal.setInvulnerable(false);
                entityendercrystal.setBeamTarget((BlockPosition) null);
            }
        }

    }
}
