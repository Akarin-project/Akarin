package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;

// CraftBukkit start
import java.util.UUID;

import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.map.CraftMapView;
import org.bukkit.craftbukkit.util.CraftChatMessage;
// CraftBukkit end

public class WorldMap extends PersistentBase {

    public int centerX;
    public int centerZ;
    public DimensionManager map;
    public boolean track;
    public boolean unlimitedTracking;
    public byte scale;
    public byte[] colors = new byte[16384];
    public List<WorldMap.WorldMapHumanTracker> h = Lists.newArrayList();
    public final Map<EntityHuman, WorldMap.WorldMapHumanTracker> humans = Maps.newHashMap();
    private final Map<String, MapIconBanner> k = Maps.newHashMap();
    public Map<String, MapIcon> decorations = Maps.newLinkedHashMap();
    private final Map<String, WorldMapFrame> l = Maps.newHashMap();

    // CraftBukkit start
    public final CraftMapView mapView;
    private CraftServer server;
    private UUID uniqueId = null;
    // CraftBukkit end

    public WorldMap(String s) {
        super(s);
        // CraftBukkit start
        mapView = new CraftMapView(this);
        server = (CraftServer) org.bukkit.Bukkit.getServer();
        // CraftBukkit end
    }

    public void a(int i, int j, int k, boolean flag, boolean flag1, DimensionManager dimensionmanager) {
        this.scale = (byte) k;
        this.a((double) i, (double) j, this.scale);
        this.map = dimensionmanager;
        this.track = flag;
        this.unlimitedTracking = flag1;
        this.c();
    }

    public void a(double d0, double d1, int i) {
        int j = 128 * (1 << i);
        int k = MathHelper.floor((d0 + 64.0D) / (double) j);
        int l = MathHelper.floor((d1 + 64.0D) / (double) j);

        this.centerX = k * j + j / 2 - 64;
        this.centerZ = l * j + j / 2 - 64;
    }

    public void a(NBTTagCompound nbttagcompound) {
        // CraftBukkit start
        int dimension = nbttagcompound.getInt("dimension");

        if (dimension >= CraftWorld.CUSTOM_DIMENSION_OFFSET) {
            long least = nbttagcompound.getLong("UUIDLeast");
            long most = nbttagcompound.getLong("UUIDMost");

            if (least != 0L && most != 0L) {
                this.uniqueId = new UUID(most, least);

                CraftWorld world = (CraftWorld) server.getWorld(this.uniqueId);
                // Check if the stored world details are correct.
                if (world == null) {
                    /* All Maps which do not have their valid world loaded are set to a dimension which hopefully won't be reached.
                       This is to prevent them being corrupted with the wrong map data. */
                    this.map = new DimensionManager(127, null, null, null);
                } else {
                    this.map = world.getHandle().dimension;
                }
            }
        } else {
            this.map = DimensionManager.a(dimension);
        }

        // CraftBukkit end
        this.centerX = nbttagcompound.getInt("xCenter");
        this.centerZ = nbttagcompound.getInt("zCenter");
        this.scale = (byte) MathHelper.clamp(nbttagcompound.getByte("scale"), 0, 4);
        this.track = !nbttagcompound.hasKeyOfType("trackingPosition", 1) || nbttagcompound.getBoolean("trackingPosition");
        this.unlimitedTracking = nbttagcompound.getBoolean("unlimitedTracking");
        this.colors = nbttagcompound.getByteArray("colors");
        if (this.colors.length != 16384) {
            this.colors = new byte[16384];
        }

        NBTTagList nbttaglist = nbttagcompound.getList("banners", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            MapIconBanner mapiconbanner = MapIconBanner.a(nbttaglist.getCompound(i));

            this.k.put(mapiconbanner.f(), mapiconbanner);
            this.a(mapiconbanner.c(), (GeneratorAccess) null, mapiconbanner.f(), (double) mapiconbanner.a().getX(), (double) mapiconbanner.a().getZ(), 180.0D, mapiconbanner.d());
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("frames", 10);

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            WorldMapFrame worldmapframe = WorldMapFrame.a(nbttaglist1.getCompound(j));

            this.l.put(worldmapframe.e(), worldmapframe);
            this.a(MapIcon.Type.FRAME, (GeneratorAccess) null, "frame-" + worldmapframe.d(), (double) worldmapframe.b().getX(), (double) worldmapframe.b().getZ(), (double) worldmapframe.c(), (IChatBaseComponent) null);
        }

    }

    public NBTTagCompound b(NBTTagCompound nbttagcompound) {
        // CraftBukkit start
        if (this.map.getDimensionID() >= CraftWorld.CUSTOM_DIMENSION_OFFSET) {
            if (this.uniqueId == null) {
                for (org.bukkit.World world : server.getWorlds()) {
                    CraftWorld cWorld = (CraftWorld) world;
                    if (cWorld.getHandle().dimension == this.map) {
                        this.uniqueId = cWorld.getUID();
                        break;
                    }
                }
            }
            /* Perform a second check to see if a matching world was found, this is a necessary
               change incase Maps are forcefully unlinked from a World and lack a UID.*/
            if (this.uniqueId != null) {
                nbttagcompound.setLong("UUIDLeast", this.uniqueId.getLeastSignificantBits());
                nbttagcompound.setLong("UUIDMost", this.uniqueId.getMostSignificantBits());
            }
        }
        // CraftBukkit end
        nbttagcompound.setInt("dimension", this.map.getDimensionID());
        nbttagcompound.setInt("xCenter", this.centerX);
        nbttagcompound.setInt("zCenter", this.centerZ);
        nbttagcompound.setByte("scale", this.scale);
        nbttagcompound.setByteArray("colors", this.colors);
        nbttagcompound.setBoolean("trackingPosition", this.track);
        nbttagcompound.setBoolean("unlimitedTracking", this.unlimitedTracking);
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.k.values().iterator();

        while (iterator.hasNext()) {
            MapIconBanner mapiconbanner = (MapIconBanner) iterator.next();

            nbttaglist.add((NBTBase) mapiconbanner.e());
        }

        nbttagcompound.set("banners", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.l.values().iterator();

        while (iterator1.hasNext()) {
            WorldMapFrame worldmapframe = (WorldMapFrame) iterator1.next();

            nbttaglist1.add((NBTBase) worldmapframe.a());
        }

        nbttagcompound.set("frames", nbttaglist1);
        return nbttagcompound;
    }

    public void a(EntityHuman entityhuman, ItemStack itemstack) {
        if (!this.humans.containsKey(entityhuman)) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = new WorldMap.WorldMapHumanTracker(entityhuman);

            this.humans.put(entityhuman, worldmap_worldmaphumantracker);
            this.h.add(worldmap_worldmaphumantracker);
        }

        if (!entityhuman.inventory.h(itemstack)) {
            this.decorations.remove(entityhuman.getDisplayName().getString());
        }

        for (int i = 0; i < this.h.size(); ++i) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker1 = (WorldMap.WorldMapHumanTracker) this.h.get(i);
            String s = worldmap_worldmaphumantracker1.trackee.getDisplayName().getString();

            if (!worldmap_worldmaphumantracker1.trackee.dead && (worldmap_worldmaphumantracker1.trackee.inventory.h(itemstack) || itemstack.x())) {
                if (!itemstack.x() && worldmap_worldmaphumantracker1.trackee.dimension == this.map && this.track) {
                    this.a(MapIcon.Type.PLAYER, worldmap_worldmaphumantracker1.trackee.world, s, worldmap_worldmaphumantracker1.trackee.locX, worldmap_worldmaphumantracker1.trackee.locZ, (double) worldmap_worldmaphumantracker1.trackee.yaw, (IChatBaseComponent) null);
                }
            } else {
                this.humans.remove(worldmap_worldmaphumantracker1.trackee);
                this.h.remove(worldmap_worldmaphumantracker1);
                this.decorations.remove(s);
            }
        }

        if (itemstack.x() && this.track) {
            EntityItemFrame entityitemframe = itemstack.y();
            BlockPosition blockposition = entityitemframe.getBlockPosition();
            WorldMapFrame worldmapframe = (WorldMapFrame) this.l.get(WorldMapFrame.a(blockposition));

            if (worldmapframe != null && entityitemframe.getId() != worldmapframe.d() && this.l.containsKey(worldmapframe.e())) {
                this.decorations.remove("frame-" + worldmapframe.d());
            }

            WorldMapFrame worldmapframe1 = new WorldMapFrame(blockposition, entityitemframe.direction.get2DRotationValue() * 90, entityitemframe.getId());

            this.a(MapIcon.Type.FRAME, entityhuman.world, "frame-" + entityitemframe.getId(), (double) blockposition.getX(), (double) blockposition.getZ(), (double) (entityitemframe.direction.get2DRotationValue() * 90), (IChatBaseComponent) null);
            this.l.put(worldmapframe1.e(), worldmapframe1);
        }

        NBTTagCompound nbttagcompound = itemstack.getTag();

        if (nbttagcompound != null && nbttagcompound.hasKeyOfType("Decorations", 9)) {
            NBTTagList nbttaglist = nbttagcompound.getList("Decorations", 10);

            for (int j = 0; j < nbttaglist.size(); ++j) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(j);

                if (!this.decorations.containsKey(nbttagcompound1.getString("id"))) {
                    this.a(MapIcon.Type.a(nbttagcompound1.getByte("type")), entityhuman.world, nbttagcompound1.getString("id"), nbttagcompound1.getDouble("x"), nbttagcompound1.getDouble("z"), nbttagcompound1.getDouble("rot"), (IChatBaseComponent) null);
                }
            }
        }

    }

    public static void decorateMap(ItemStack itemstack, BlockPosition blockposition, String s, MapIcon.Type mapicon_type) {
        NBTTagList nbttaglist;

        if (itemstack.hasTag() && itemstack.getTag().hasKeyOfType("Decorations", 9)) {
            nbttaglist = itemstack.getTag().getList("Decorations", 10);
        } else {
            nbttaglist = new NBTTagList();
            itemstack.a("Decorations", (NBTBase) nbttaglist);
        }

        NBTTagCompound nbttagcompound = new NBTTagCompound();

        nbttagcompound.setByte("type", mapicon_type.a());
        nbttagcompound.setString("id", s);
        nbttagcompound.setDouble("x", (double) blockposition.getX());
        nbttagcompound.setDouble("z", (double) blockposition.getZ());
        nbttagcompound.setDouble("rot", 180.0D);
        nbttaglist.add((NBTBase) nbttagcompound);
        if (mapicon_type.c()) {
            NBTTagCompound nbttagcompound1 = itemstack.a("display");

            nbttagcompound1.setInt("MapColor", mapicon_type.d());
        }

    }

    private void a(MapIcon.Type mapicon_type, @Nullable GeneratorAccess generatoraccess, String s, double d0, double d1, double d2, @Nullable IChatBaseComponent ichatbasecomponent) {
        int i = 1 << this.scale;
        float f = (float) (d0 - (double) this.centerX) / (float) i;
        float f1 = (float) (d1 - (double) this.centerZ) / (float) i;
        byte b0 = (byte) ((int) ((double) (f * 2.0F) + 0.5D));
        byte b1 = (byte) ((int) ((double) (f1 * 2.0F) + 0.5D));
        boolean flag = true;
        byte b2;

        if (f >= -63.0F && f1 >= -63.0F && f <= 63.0F && f1 <= 63.0F) {
            d2 += d2 < 0.0D ? -8.0D : 8.0D;
            b2 = (byte) ((int) (d2 * 16.0D / 360.0D));
            if (this.map == DimensionManager.NETHER && generatoraccess != null) {
                int j = (int) (generatoraccess.getWorldData().getDayTime() / 10L);

                b2 = (byte) (j * j * 34187121 + j * 121 >> 15 & 15);
            }
        } else {
            if (mapicon_type != MapIcon.Type.PLAYER) {
                this.decorations.remove(s);
                return;
            }

            boolean flag1 = true;

            if (Math.abs(f) < 320.0F && Math.abs(f1) < 320.0F) {
                mapicon_type = MapIcon.Type.PLAYER_OFF_MAP;
            } else {
                if (!this.unlimitedTracking) {
                    this.decorations.remove(s);
                    return;
                }

                mapicon_type = MapIcon.Type.PLAYER_OFF_LIMITS;
            }

            b2 = 0;
            if (f <= -63.0F) {
                b0 = -128;
            }

            if (f1 <= -63.0F) {
                b1 = -128;
            }

            if (f >= 63.0F) {
                b0 = 127;
            }

            if (f1 >= 63.0F) {
                b1 = 127;
            }
        }

        this.decorations.put(s, new MapIcon(mapicon_type, b0, b1, b2, ichatbasecomponent));
    }

    @Nullable
    public Packet<?> a(ItemStack itemstack, IBlockAccess iblockaccess, EntityHuman entityhuman) {
        WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) this.humans.get(entityhuman);

        return worldmap_worldmaphumantracker == null ? null : worldmap_worldmaphumantracker.a(itemstack);
    }

    public void flagDirty(int i, int j) {
        this.c();
        Iterator iterator = this.h.iterator();

        while (iterator.hasNext()) {
            WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) iterator.next();

            worldmap_worldmaphumantracker.a(i, j);
        }

    }

    public WorldMap.WorldMapHumanTracker a(EntityHuman entityhuman) {
        WorldMap.WorldMapHumanTracker worldmap_worldmaphumantracker = (WorldMap.WorldMapHumanTracker) this.humans.get(entityhuman);

        if (worldmap_worldmaphumantracker == null) {
            worldmap_worldmaphumantracker = new WorldMap.WorldMapHumanTracker(entityhuman);
            this.humans.put(entityhuman, worldmap_worldmaphumantracker);
            this.h.add(worldmap_worldmaphumantracker);
        }

        return worldmap_worldmaphumantracker;
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition) {
        float f = (float) blockposition.getX() + 0.5F;
        float f1 = (float) blockposition.getZ() + 0.5F;
        int i = 1 << this.scale;
        float f2 = (f - (float) this.centerX) / (float) i;
        float f3 = (f1 - (float) this.centerZ) / (float) i;
        boolean flag = true;
        boolean flag1 = false;

        if (f2 >= -63.0F && f3 >= -63.0F && f2 <= 63.0F && f3 <= 63.0F) {
            MapIconBanner mapiconbanner = MapIconBanner.a(generatoraccess, blockposition);

            if (mapiconbanner == null) {
                return;
            }

            boolean flag2 = true;

            if (this.k.containsKey(mapiconbanner.f()) && ((MapIconBanner) this.k.get(mapiconbanner.f())).equals(mapiconbanner)) {
                this.k.remove(mapiconbanner.f());
                this.decorations.remove(mapiconbanner.f());
                flag2 = false;
                flag1 = true;
            }

            if (flag2) {
                this.k.put(mapiconbanner.f(), mapiconbanner);
                this.a(mapiconbanner.c(), generatoraccess, mapiconbanner.f(), (double) f, (double) f1, 180.0D, mapiconbanner.d());
                flag1 = true;
            }

            if (flag1) {
                this.c();
            }
        }

    }

    public void a(IBlockAccess iblockaccess, int i, int j) {
        Iterator iterator = this.k.values().iterator();

        while (iterator.hasNext()) {
            MapIconBanner mapiconbanner = (MapIconBanner) iterator.next();

            if (mapiconbanner.a().getX() == i && mapiconbanner.a().getZ() == j) {
                MapIconBanner mapiconbanner1 = MapIconBanner.a(iblockaccess, mapiconbanner.a());

                if (!mapiconbanner.equals(mapiconbanner1)) {
                    iterator.remove();
                    this.decorations.remove(mapiconbanner.f());
                }
            }
        }

    }

    public void a(BlockPosition blockposition, int i) {
        this.decorations.remove("frame-" + i);
        this.l.remove(WorldMapFrame.a(blockposition));
    }

    public class WorldMapHumanTracker {

        public final EntityHuman trackee;
        private boolean d = true;
        private int e;
        private int f;
        private int g = 127;
        private int h = 127;
        private int i;
        public int b;

        public WorldMapHumanTracker(EntityHuman entityhuman) {
            this.trackee = entityhuman;
        }

        @Nullable
        public Packet<?> a(ItemStack itemstack) {
            // CraftBukkit start
            org.bukkit.craftbukkit.map.RenderData render = WorldMap.this.mapView.render((org.bukkit.craftbukkit.entity.CraftPlayer) this.trackee.getBukkitEntity()); // CraftBukkit

            java.util.Collection<MapIcon> icons = new java.util.ArrayList<MapIcon>();

            for ( org.bukkit.map.MapCursor cursor : render.cursors) {

                if (cursor.isVisible()) {
                    icons.add(new MapIcon(MapIcon.Type.a(cursor.getRawType()), cursor.getX(), cursor.getY(), cursor.getDirection(), CraftChatMessage.fromStringOrNull(cursor.getCaption())));
                }
            }

            if (this.d) {
                this.d = false;
                return new PacketPlayOutMap(ItemWorldMap.e(itemstack), WorldMap.this.scale, WorldMap.this.track, icons, render.buffer, this.e, this.f, this.g + 1 - this.e, this.h + 1 - this.f);
            } else {
                return this.i++ % 5 == 0 ? new PacketPlayOutMap(ItemWorldMap.e(itemstack), WorldMap.this.scale, WorldMap.this.track, icons, render.buffer, 0, 0, 0, 0) : null;
            }
            // CraftBukkit end
        }

        public void a(int i, int j) {
            if (this.d) {
                this.e = Math.min(this.e, i);
                this.f = Math.min(this.f, j);
                this.g = Math.max(this.g, i);
                this.h = Math.max(this.h, j);
            } else {
                this.d = true;
                this.e = i;
                this.f = j;
                this.g = i;
                this.h = j;
            }

        }
    }
}
