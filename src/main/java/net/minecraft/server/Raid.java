package net.minecraft.server;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Raid {

    private static final ChatMessage a = new ChatMessage("event.minecraft.raid", new Object[0]);
    private static final ChatMessage b = new ChatMessage("event.minecraft.raid.victory", new Object[0]);
    private static final ChatMessage c = new ChatMessage("event.minecraft.raid.defeat", new Object[0]);
    private static final IChatBaseComponent d = Raid.a.g().a(" - ").addSibling(Raid.b);
    private static final IChatBaseComponent e = Raid.a.g().a(" - ").addSibling(Raid.c);
    private final Map<Integer, EntityRaider> f = Maps.newHashMap();
    private final Map<Integer, Set<EntityRaider>> g = Maps.newHashMap();
    public final Set<UUID> h = Sets.newHashSet(); // PAIL rename heroes, private -> public
    public long i; // PAIL rename activeTicks, private -> public
    private BlockPosition j;
    private final WorldServer k;
    private boolean l;
    private final int m;
    public float n; // PAIL rename originTotalHealth, private -> public
    public int o; // PAIL rename badOmenLevel, private -> public
    private boolean p;
    private int q;
    private final BossBattleServer r;
    private int s;
    private int t;
    private final Random u;
    public final int v; // PAIL rename totalWaves, private -> public
    private Raid.Status w;
    private int x;
    private Optional<BlockPosition> y;

    public Raid(int i, WorldServer worldserver, BlockPosition blockposition) {
        this.r = new BossBattleServer(Raid.a, BossBattle.BarColor.RED, BossBattle.BarStyle.NOTCHED_10);
        this.u = new Random();
        this.y = Optional.empty();
        this.m = i;
        this.k = worldserver;
        this.p = true;
        this.t = 300;
        this.r.setProgress(0.0F);
        this.j = blockposition;
        this.v = this.a(worldserver.getDifficulty());
        this.w = Raid.Status.ONGOING;
    }

    public Raid(WorldServer worldserver, NBTTagCompound nbttagcompound) {
        this.r = new BossBattleServer(Raid.a, BossBattle.BarColor.RED, BossBattle.BarStyle.NOTCHED_10);
        this.u = new Random();
        this.y = Optional.empty();
        this.k = worldserver;
        this.m = nbttagcompound.getInt("Id");
        this.l = nbttagcompound.getBoolean("Started");
        this.p = nbttagcompound.getBoolean("Active");
        this.i = nbttagcompound.getLong("TicksActive");
        this.o = nbttagcompound.getInt("BadOmenLevel");
        this.q = nbttagcompound.getInt("GroupsSpawned");
        this.t = nbttagcompound.getInt("PreRaidTicks");
        this.s = nbttagcompound.getInt("PostRaidTicks");
        this.n = nbttagcompound.getFloat("TotalHealth");
        this.j = new BlockPosition(nbttagcompound.getInt("CX"), nbttagcompound.getInt("CY"), nbttagcompound.getInt("CZ"));
        this.v = nbttagcompound.getInt("NumGroups");
        this.w = Raid.Status.b(nbttagcompound.getString("Status"));
        this.h.clear();
        if (nbttagcompound.hasKeyOfType("HeroesOfTheVillage", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("HeroesOfTheVillage", 10);

            for (int i = 0; i < nbttaglist.size(); ++i) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
                UUID uuid = nbttagcompound1.a("UUID");

                this.h.add(uuid);
            }
        }

    }

    public boolean a() {
        return this.e() || this.f();
    }

    public boolean b() {
        return this.c() && this.r() == 0 && this.t > 0;
    }

    public boolean c() {
        return this.q > 0;
    }

    public boolean d() {
        return this.w == Raid.Status.STOPPED;
    }

    public boolean e() {
        return this.w == Raid.Status.VICTORY;
    }

    public boolean f() {
        return this.w == Raid.Status.LOSS;
    }

    // CraftBukkit start
    public boolean isInProgress() {
        return this.w == Status.ONGOING;
    }
    // CraftBukkit end

    public World i() {
        return this.k;
    }

    public boolean j() {
        return this.l;
    }

    public int k() {
        return this.q;
    }

    private Predicate<EntityPlayer> x() {
        return (entityplayer) -> {
            BlockPosition blockposition = new BlockPosition(entityplayer);

            return entityplayer.isAlive() && this.k.c_(blockposition) == this;
        };
    }

    private void y() {
        Set<EntityPlayer> set = Sets.newHashSet(this.r.getPlayers());
        List<EntityPlayer> list = this.k.a(this.x());
        Iterator iterator = list.iterator();

        EntityPlayer entityplayer;

        while (iterator.hasNext()) {
            entityplayer = (EntityPlayer) iterator.next();
            if (!set.contains(entityplayer)) {
                this.r.addPlayer(entityplayer);
            }
        }

        iterator = set.iterator();

        while (iterator.hasNext()) {
            entityplayer = (EntityPlayer) iterator.next();
            if (!list.contains(entityplayer)) {
                this.r.removePlayer(entityplayer);
            }
        }

    }

    public int l() {
        return 5;
    }

    public int m() {
        return this.o;
    }

    public void a(EntityHuman entityhuman) {
        if (entityhuman.hasEffect(MobEffects.BAD_OMEN)) {
            this.o += entityhuman.getEffect(MobEffects.BAD_OMEN).getAmplifier() + 1;
            this.o = MathHelper.clamp(this.o, 0, this.l());
        }

        entityhuman.removeEffect(MobEffects.BAD_OMEN);
    }

    public void n() {
        this.p = false;
        this.r.b();
        this.w = Raid.Status.STOPPED;
    }

    public void o() {
        if (!this.d()) {
            if (this.w == Raid.Status.ONGOING) {
                boolean flag = this.p;

                this.p = this.k.isLoaded(this.j);
                if (this.k.getDifficulty() == EnumDifficulty.PEACEFUL) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(this, org.bukkit.event.raid.RaidStopEvent.Reason.PEACE); // CraftBukkit
                    this.n();
                    return;
                }

                if (flag != this.p) {
                    this.r.setVisible(this.p);
                }

                if (!this.p) {
                    return;
                }

                if (!this.k.b_(this.j)) {
                    this.z();
                }

                if (!this.k.b_(this.j)) {
                    if (this.q > 0) {
                        this.w = Raid.Status.LOSS;
                        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidFinishEvent(this, new java.util.ArrayList<>()); // CraftBukkit
                    } else {
                        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(this, org.bukkit.event.raid.RaidStopEvent.Reason.NOT_IN_VILLAGE); // CraftBukkit
                        this.n();
                    }
                }

                ++this.i;
                if (this.i >= 48000L) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(this, org.bukkit.event.raid.RaidStopEvent.Reason.TIMEOUT); // CraftBukkit
                    this.n();
                    return;
                }

                int i = this.r();
                boolean flag1;

                if (i == 0 && this.A()) {
                    if (this.t > 0) {
                        flag1 = this.y.isPresent();
                        boolean flag2 = !flag1 && this.t % 5 == 0;

                        if (flag1 && !this.k.getChunkProvider().a(new ChunkCoordIntPair((BlockPosition) this.y.get()))) {
                            flag2 = true;
                        }

                        if (flag2) {
                            byte b0 = 0;

                            if (this.t < 100) {
                                b0 = 1;
                            } else if (this.t < 40) {
                                b0 = 2;
                            }

                            this.y = this.d(b0);
                        }

                        if (this.t == 300 || this.t % 20 == 0) {
                            this.y();
                        }

                        --this.t;
                        this.r.setProgress(MathHelper.a((float) (300 - this.t) / 300.0F, 0.0F, 1.0F));
                    } else if (this.t == 0 && this.q > 0) {
                        this.t = 300;
                        this.r.a((IChatBaseComponent) Raid.a);
                        return;
                    }
                }

                if (this.i % 20L == 0L) {
                    this.y();
                    this.F();
                    if (i > 0) {
                        if (i <= 2) {
                            this.r.a(Raid.a.g().a(" - ").addSibling(new ChatMessage("event.minecraft.raid.raiders_remaining", new Object[]{i})));
                        } else {
                            this.r.a((IChatBaseComponent) Raid.a);
                        }
                    } else {
                        this.r.a((IChatBaseComponent) Raid.a);
                    }
                }

                flag1 = false;
                int j = 0;

                while (this.G()) {
                    BlockPosition blockposition = this.y.isPresent() ? (BlockPosition) this.y.get() : this.a(j, 20);

                    if (blockposition != null) {
                        this.l = true;
                        this.b(blockposition);
                        if (!flag1) {
                            this.a(blockposition);
                            flag1 = true;
                        }
                    } else {
                        ++j;
                    }

                    if (j > 3) {
                        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(this, org.bukkit.event.raid.RaidStopEvent.Reason.UNSPAWNABLE);  // CraftBukkit
                        this.n();
                        break;
                    }
                }

                if (this.j() && !this.A() && i == 0) {
                    if (this.s < 40) {
                        ++this.s;
                    } else {
                        this.w = Raid.Status.VICTORY;
                        Iterator iterator = this.h.iterator();

                        List<org.bukkit.entity.Player> winners = new java.util.ArrayList<>(); // CraftBukkit
                        while (iterator.hasNext()) {
                            UUID uuid = (UUID) iterator.next();
                            Entity entity = this.k.getEntity(uuid);

                            if (entity instanceof EntityLiving && !entity.isSpectator()) {
                                EntityLiving entityliving = (EntityLiving) entity;

                                entityliving.addEffect(new MobEffect(MobEffects.HERO_OF_THE_VILLAGE, 48000, this.o - 1, false, false, true));
                                if (entityliving instanceof EntityPlayer) {
                                    EntityPlayer entityplayer = (EntityPlayer) entityliving;

                                    entityplayer.a(StatisticList.RAID_WIN);
                                    CriterionTriggers.H.a(entityplayer);
                                    winners.add(entityplayer.getBukkitEntity()); // CraftBukkit
                                }
                            }
                        }
                        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidFinishEvent(this, winners); // CraftBukkit
                    }
                }

                this.H();
            } else if (this.a()) {
                ++this.x;
                if (this.x >= 600) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.callRaidStopEvent(this, org.bukkit.event.raid.RaidStopEvent.Reason.FINISHED); // CraftBukkit
                    this.n();
                    return;
                }

                if (this.x % 20 == 0) {
                    this.y();
                    this.r.setVisible(true);
                    if (this.e()) {
                        this.r.setProgress(0.0F);
                        this.r.a(Raid.d);
                    } else {
                        this.r.a(Raid.e);
                    }
                }
            }

        }
    }

    private void z() {
        Stream<SectionPosition> stream = SectionPosition.a(SectionPosition.a(this.j), 2);
        WorldServer worldserver = this.k;

        this.k.getClass();
        stream.filter(worldserver::a).map(SectionPosition::t).min(Comparator.comparingDouble((blockposition) -> {
            return blockposition.m(this.j);
        })).ifPresent(this::c);
    }

    private Optional<BlockPosition> d(int i) {
        for (int j = 0; j < 3; ++j) {
            BlockPosition blockposition = this.a(i, 1);

            if (blockposition != null) {
                return Optional.of(blockposition);
            }
        }

        return Optional.empty();
    }

    private boolean A() {
        return this.C() ? !this.D() : !this.B();
    }

    private boolean B() {
        return this.k() == this.v;
    }

    private boolean C() {
        return this.o > 1;
    }

    private boolean D() {
        return this.k() > this.v;
    }

    private boolean E() {
        return this.B() && this.r() == 0 && this.C();
    }

    private void F() {
        Iterator<Set<EntityRaider>> iterator = this.g.values().iterator();
        HashSet hashset = Sets.newHashSet();

        while (iterator.hasNext()) {
            Set<EntityRaider> set = (Set) iterator.next();
            Iterator iterator1 = set.iterator();

            while (iterator1.hasNext()) {
                EntityRaider entityraider = (EntityRaider) iterator1.next();
                BlockPosition blockposition = new BlockPosition(entityraider);

                if (!entityraider.dead && entityraider.dimension == this.k.getWorldProvider().getDimensionManager() && this.j.m(blockposition) < 12544.0D) {
                    if (entityraider.ticksLived > 600) {
                        if (this.k.getEntity(entityraider.getUniqueID()) == null) {
                            hashset.add(entityraider);
                        }

                        if (!this.k.b_(blockposition) && entityraider.cw() > 2400) {
                            entityraider.b(entityraider.en() + 1);
                        }

                        if (entityraider.en() >= 30) {
                            hashset.add(entityraider);
                        }
                    }
                } else {
                    hashset.add(entityraider);
                }
            }
        }

        Iterator iterator2 = hashset.iterator();

        while (iterator2.hasNext()) {
            EntityRaider entityraider1 = (EntityRaider) iterator2.next();

            this.a(entityraider1, true);
        }

    }

    private void a(BlockPosition blockposition) {
        float f = 13.0F;
        boolean flag = true;
        Iterator iterator = this.k.getPlayers().iterator();

        while (iterator.hasNext()) {
            EntityHuman entityhuman = (EntityHuman) iterator.next();
            Vec3D vec3d = new Vec3D(entityhuman.locX, entityhuman.locY, entityhuman.locZ);
            Vec3D vec3d1 = new Vec3D((double) blockposition.getX(), (double) blockposition.getY(), (double) blockposition.getZ());
            float f1 = MathHelper.sqrt((vec3d1.x - vec3d.x) * (vec3d1.x - vec3d.x) + (vec3d1.z - vec3d.z) * (vec3d1.z - vec3d.z));
            double d0 = vec3d.x + (double) (13.0F / f1) * (vec3d1.x - vec3d.x);
            double d1 = vec3d.z + (double) (13.0F / f1) * (vec3d1.z - vec3d.z);

            if (f1 <= 64.0F || this.k.b_(new BlockPosition(entityhuman))) {
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(SoundEffects.EVENT_RAID_HORN, SoundCategory.NEUTRAL, d0, entityhuman.locY, d1, 64.0F, 1.0F));
            }
        }

    }

    private void b(BlockPosition blockposition) {
        boolean flag = false;
        int i = this.q + 1;

        this.n = 0.0F;
        DifficultyDamageScaler difficultydamagescaler = this.k.getDamageScaler(blockposition);
        boolean flag1 = this.E();
        Raid.Wave[] araid_wave = Raid.Wave.f;
        int j = araid_wave.length;

        // CraftBukkit start
        EntityRaider leader = null;
        List<EntityRaider> raiders = new java.util.ArrayList<>();
        // CraftBukkit end
        for (int k = 0; k < j; ++k) {
            Raid.Wave raid_wave = araid_wave[k];
            int l = this.a(raid_wave, i, flag1) + this.a(raid_wave, this.u, i, difficultydamagescaler, flag1);
            int i1 = 0;

            for (int j1 = 0; j1 < l; ++j1) {
                EntityRaider entityraider = (EntityRaider) raid_wave.g.a((World) this.k);

                if (!flag && entityraider.dX()) {
                    entityraider.setPatrolLeader(true);
                    this.a(i, entityraider);
                    flag = true;
                    leader = entityraider; // CraftBukkit
                }

                this.a(i, entityraider, blockposition, false);
                raiders.add(entityraider); // CraftBukkit
                if (raid_wave.g == EntityTypes.RAVAGER) {
                    EntityRaider entityraider1 = null;

                    if (i == this.a(EnumDifficulty.NORMAL)) {
                        entityraider1 = (EntityRaider) EntityTypes.PILLAGER.a((World) this.k);
                    } else if (i >= this.a(EnumDifficulty.HARD)) {
                        if (i1 == 0) {
                            entityraider1 = (EntityRaider) EntityTypes.EVOKER.a((World) this.k);
                        } else {
                            entityraider1 = (EntityRaider) EntityTypes.VINDICATOR.a((World) this.k);
                        }
                    }

                    ++i1;
                    if (entityraider1 != null) {
                        this.a(i, entityraider1, blockposition, false);
                        entityraider1.setPositionRotation(blockposition, 0.0F, 0.0F);
                        entityraider1.startRiding(entityraider);
                        raiders.add(entityraider); // CraftBukkit
                    }
                }
            }
        }

        this.y = Optional.empty();
        ++this.q;
        this.p();
        this.H();
        org.bukkit.craftbukkit.event.CraftEventFactory.callRaidSpawnWaveEvent(this, leader, raiders); // CraftBukkit
    }

    public void a(int i, EntityRaider entityraider, @Nullable BlockPosition blockposition, boolean flag) {
        boolean flag1 = this.b(i, entityraider);

        if (flag1) {
            entityraider.a(this);
            entityraider.a(i);
            entityraider.t(true);
            entityraider.b(0);
            if (!flag && blockposition != null) {
                entityraider.setPosition((double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.0D, (double) blockposition.getZ() + 0.5D);
                entityraider.prepare(this.k, this.k.getDamageScaler(blockposition), EnumMobSpawn.EVENT, (GroupDataEntity) null, (NBTTagCompound) null);
                entityraider.a(i, false);
                entityraider.onGround = true;
                this.k.addEntity(entityraider, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.RAID); // CraftBukkit
            }
        }

    }

    public void p() {
        this.r.setProgress(MathHelper.a(this.q() / this.n, 0.0F, 1.0F));
    }

    public float q() {
        float f = 0.0F;
        Iterator iterator = this.g.values().iterator();

        while (iterator.hasNext()) {
            Set<EntityRaider> set = (Set) iterator.next();

            EntityRaider entityraider;

            for (Iterator iterator1 = set.iterator(); iterator1.hasNext(); f += entityraider.getHealth()) {
                entityraider = (EntityRaider) iterator1.next();
            }
        }

        return f;
    }

    private boolean G() {
        return this.t == 0 && (this.q < this.v || this.E()) && this.r() == 0;
    }

    public int r() {
        return this.g.values().stream().mapToInt(Set::size).sum();
    }

    public void a(@Nonnull EntityRaider entityraider, boolean flag) {
        Set<EntityRaider> set = (Set) this.g.get(entityraider.el());

        if (set != null) {
            boolean flag1 = set.remove(entityraider);

            if (flag1) {
                if (flag) {
                    this.n -= entityraider.getHealth();
                }

                entityraider.a((Raid) null);
                this.p();
                this.H();
            }
        }

    }

    private void H() {
        this.k.C().b();
    }

    public static ItemStack s() {
        ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
        NBTTagCompound nbttagcompound = itemstack.a("BlockEntityTag");
        NBTTagList nbttaglist = (new EnumBannerPatternType.a()).a(EnumBannerPatternType.RHOMBUS_MIDDLE, EnumColor.CYAN).a(EnumBannerPatternType.STRIPE_BOTTOM, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.STRIPE_CENTER, EnumColor.GRAY).a(EnumBannerPatternType.BORDER, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.STRIPE_MIDDLE, EnumColor.BLACK).a(EnumBannerPatternType.HALF_HORIZONTAL, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.CIRCLE_MIDDLE, EnumColor.LIGHT_GRAY).a(EnumBannerPatternType.BORDER, EnumColor.BLACK).a();

        nbttagcompound.set("Patterns", nbttaglist);
        itemstack.a((new ChatMessage("block.minecraft.ominous_banner", new Object[0])).a(EnumChatFormat.GOLD));
        return itemstack;
    }

    @Nullable
    public EntityRaider b(int i) {
        return (EntityRaider) this.f.get(i);
    }

    @Nullable
    private BlockPosition a(int i, int j) {
        int k = i == 0 ? 2 : 2 - i;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition();

        for (int l = 0; l < j; ++l) {
            float f = this.k.random.nextFloat() * 6.2831855F;
            int i1 = this.j.getX() + MathHelper.d(MathHelper.cos(f) * 32.0F * (float) k) + this.k.random.nextInt(5);
            int j1 = this.j.getZ() + MathHelper.d(MathHelper.sin(f) * 32.0F * (float) k) + this.k.random.nextInt(5);
            int k1 = this.k.a(HeightMap.Type.WORLD_SURFACE, i1, j1);

            blockposition_mutableblockposition.d(i1, k1, j1);
            if ((!this.k.b_(blockposition_mutableblockposition) || i >= 2) && this.k.isAreaLoaded(blockposition_mutableblockposition.getX() - 10, blockposition_mutableblockposition.getY() - 10, blockposition_mutableblockposition.getZ() - 10, blockposition_mutableblockposition.getX() + 10, blockposition_mutableblockposition.getY() + 10, blockposition_mutableblockposition.getZ() + 10) && this.k.getChunkProvider().a(new ChunkCoordIntPair(blockposition_mutableblockposition)) && (SpawnerCreature.a(EntityPositionTypes.Surface.ON_GROUND, (IWorldReader) this.k, (BlockPosition) blockposition_mutableblockposition, EntityTypes.RAVAGER) || this.k.getType(blockposition_mutableblockposition.down()).getBlock() == Blocks.SNOW && this.k.getType(blockposition_mutableblockposition).isAir())) {
                return blockposition_mutableblockposition;
            }
        }

        return null;
    }

    private boolean b(int i, EntityRaider entityraider) {
        return this.a(i, entityraider, true);
    }

    public boolean a(int i, EntityRaider entityraider, boolean flag) {
        this.g.computeIfAbsent(i, (integer) -> {
            return Sets.newHashSet();
        });
        Set<EntityRaider> set = (Set) this.g.get(i);
        EntityRaider entityraider1 = null;
        Iterator iterator = set.iterator();

        while (iterator.hasNext()) {
            EntityRaider entityraider2 = (EntityRaider) iterator.next();

            if (entityraider2.getUniqueID().equals(entityraider.getUniqueID())) {
                entityraider1 = entityraider2;
                break;
            }
        }

        if (entityraider1 != null) {
            set.remove(entityraider1);
            set.add(entityraider);
        }

        set.add(entityraider);
        if (flag) {
            this.n += entityraider.getHealth();
        }

        this.p();
        this.H();
        return true;
    }

    public void a(int i, EntityRaider entityraider) {
        this.f.put(i, entityraider);
        entityraider.setSlot(EnumItemSlot.HEAD, s());
        entityraider.a(EnumItemSlot.HEAD, 2.0F);
    }

    public void c(int i) {
        this.f.remove(i);
    }

    public BlockPosition t() {
        return this.j;
    }

    private void c(BlockPosition blockposition) {
        this.j = blockposition;
    }

    public int u() {
        return this.m;
    }

    private int a(Raid.Wave raid_wave, int i, boolean flag) {
        return flag ? raid_wave.h[this.v] : raid_wave.h[i];
    }

    private int a(Raid.Wave raid_wave, Random random, int i, DifficultyDamageScaler difficultydamagescaler, boolean flag) {
        EnumDifficulty enumdifficulty = difficultydamagescaler.a();
        boolean flag1 = enumdifficulty == EnumDifficulty.EASY;
        boolean flag2 = enumdifficulty == EnumDifficulty.NORMAL;
        int j;

        switch (raid_wave) {
            case WITCH:
                if (flag1 || i <= 2 || i == 4) {
                    return 0;
                }

                j = 1;
                break;
            case PILLAGER:
            case VINDICATOR:
                if (flag1) {
                    j = random.nextInt(2);
                } else if (flag2) {
                    j = 1;
                } else {
                    j = 2;
                }
                break;
            case RAVAGER:
                j = !flag1 && flag ? 1 : 0;
                break;
            default:
                return 0;
        }

        return j > 0 ? random.nextInt(j + 1) : 0;
    }

    public boolean v() {
        return this.p;
    }

    public NBTTagCompound a(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("Id", this.m);
        nbttagcompound.setBoolean("Started", this.l);
        nbttagcompound.setBoolean("Active", this.p);
        nbttagcompound.setLong("TicksActive", this.i);
        nbttagcompound.setInt("BadOmenLevel", this.o);
        nbttagcompound.setInt("GroupsSpawned", this.q);
        nbttagcompound.setInt("PreRaidTicks", this.t);
        nbttagcompound.setInt("PostRaidTicks", this.s);
        nbttagcompound.setFloat("TotalHealth", this.n);
        nbttagcompound.setInt("NumGroups", this.v);
        nbttagcompound.setString("Status", this.w.a());
        nbttagcompound.setInt("CX", this.j.getX());
        nbttagcompound.setInt("CY", this.j.getY());
        nbttagcompound.setInt("CZ", this.j.getZ());
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.h.iterator();

        while (iterator.hasNext()) {
            UUID uuid = (UUID) iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.a("UUID", uuid);
            nbttaglist.add(nbttagcompound1);
        }

        nbttagcompound.set("HeroesOfTheVillage", nbttaglist);
        return nbttagcompound;
    }

    public int a(EnumDifficulty enumdifficulty) {
        switch (enumdifficulty) {
            case EASY:
                return 3;
            case NORMAL:
                return 5;
            case HARD:
                return 7;
            default:
                return 0;
        }
    }

    public float w() {
        int i = this.m();

        return i == 2 ? 0.1F : (i == 3 ? 0.25F : (i == 4 ? 0.5F : (i == 5 ? 0.75F : 0.0F)));
    }

    public void a(Entity entity) {
        this.h.add(entity.getUniqueID());
    }

    // CraftBukkit start - a method to get all raiders
    public java.util.Collection<EntityRaider> getRaiders() {
        return this.g.values().stream().flatMap(Set::stream).collect(java.util.stream.Collectors.toSet());
    }
    // CraftBukkit end

    static enum Wave {

        VINDICATOR(EntityTypes.VINDICATOR, new int[]{0, 0, 2, 0, 1, 4, 2, 5}), EVOKER(EntityTypes.EVOKER, new int[]{0, 0, 0, 0, 0, 1, 1, 2}), PILLAGER(EntityTypes.PILLAGER, new int[]{0, 4, 3, 3, 4, 4, 4, 2}), WITCH(EntityTypes.WITCH, new int[]{0, 0, 0, 0, 3, 0, 0, 1}), RAVAGER(EntityTypes.RAVAGER, new int[]{0, 0, 0, 1, 0, 1, 0, 2});

        private static final Raid.Wave[] f = values();
        private final EntityTypes<? extends EntityRaider> g;
        private final int[] h;

        private Wave(EntityTypes entitytypes, int[] aint) {
            this.g = entitytypes;
            this.h = aint;
        }
    }

    static enum Status {

        ONGOING, VICTORY, LOSS, STOPPED;

        private static final Raid.Status[] e = values();

        private Status() {}

        private static Raid.Status b(String s) {
            Raid.Status[] araid_status = Raid.Status.e;
            int i = araid_status.length;

            for (int j = 0; j < i; ++j) {
                Raid.Status raid_status = araid_status[j];

                if (s.equalsIgnoreCase(raid_status.name())) {
                    return raid_status;
                }
            }

            return Raid.Status.ONGOING;
        }

        public String a() {
            return this.name().toLowerCase(Locale.ROOT);
        }
    }
}
