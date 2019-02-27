package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nullable;

public class Village {

    private World a;
    private final List<VillageDoor> b = Lists.newArrayList();
    private BlockPosition c;
    private BlockPosition d;
    private int e;
    private int f;
    private int g;
    private int h;
    private int i;
    private final Map<String, Integer> j;
    private final List<Village.Aggressor> k;
    private int l;

    public Village() {
        this.c = BlockPosition.ZERO;
        this.d = BlockPosition.ZERO;
        this.j = Maps.newHashMap();
        this.k = Lists.newArrayList();
    }

    public Village(World world) {
        this.c = BlockPosition.ZERO;
        this.d = BlockPosition.ZERO;
        this.j = Maps.newHashMap();
        this.k = Lists.newArrayList();
        this.a = world;
    }

    public void a(World world) {
        this.a = world;
    }

    public void a(int i) {
        this.g = i;
        this.m();
        this.l();
        if (i % 20 == 0) {
            this.k();
        }

        if (i % 30 == 0) {
            this.j();
        }

        int j = this.h / 10;

        if (this.l < j && this.b.size() > 20 && this.a.random.nextInt(7000) == 0) {
            Entity entity = this.f(this.d);

            if (entity != null) {
                ++this.l;
            }
        }

    }

    @Nullable
    private Entity f(BlockPosition blockposition) {
        for (int i = 0; i < 10; ++i) {
            BlockPosition blockposition1 = blockposition.a(this.a.random.nextInt(16) - 8, this.a.random.nextInt(6) - 3, this.a.random.nextInt(16) - 8);

            if (this.a(blockposition1)) {
                EntityIronGolem entityirongolem = (EntityIronGolem) EntityTypes.IRON_GOLEM.b(this.a, (NBTTagCompound) null, (IChatBaseComponent) null, (EntityHuman) null, blockposition1, false, false);

                if (entityirongolem != null) {
                    if (entityirongolem.a((GeneratorAccess) this.a, false) && entityirongolem.a((IWorldReader) this.a)) {
                        this.a.addEntity(entityirongolem);
                        return entityirongolem;
                    }

                    entityirongolem.die();
                }
            }
        }

        return null;
    }

    private void j() {
        List<EntityIronGolem> list = this.a.a(EntityIronGolem.class, new AxisAlignedBB((double) (this.d.getX() - this.e), (double) (this.d.getY() - 4), (double) (this.d.getZ() - this.e), (double) (this.d.getX() + this.e), (double) (this.d.getY() + 4), (double) (this.d.getZ() + this.e)));

        this.l = list.size();
    }

    private void k() {
        List<EntityVillager> list = this.a.a(EntityVillager.class, new AxisAlignedBB((double) (this.d.getX() - this.e), (double) (this.d.getY() - 4), (double) (this.d.getZ() - this.e), (double) (this.d.getX() + this.e), (double) (this.d.getY() + 4), (double) (this.d.getZ() + this.e)));

        this.h = list.size();
        if (this.h == 0) {
            this.j.clear();
        }

    }

    public BlockPosition a() {
        return this.d;
    }

    public int b() {
        return this.e;
    }

    public int c() {
        return this.b.size();
    }

    public int d() {
        return this.g - this.f;
    }

    public int e() {
        return this.h;
    }

    public boolean a(BlockPosition blockposition) {
        return this.d.n(blockposition) < (double) (this.e * this.e);
    }

    public List<VillageDoor> f() {
        return this.b;
    }

    public VillageDoor b(BlockPosition blockposition) {
        VillageDoor villagedoor = null;
        int i = Integer.MAX_VALUE;
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            VillageDoor villagedoor1 = (VillageDoor) iterator.next();
            int j = villagedoor1.a(blockposition);

            if (j < i) {
                villagedoor = villagedoor1;
                i = j;
            }
        }

        return villagedoor;
    }

    public VillageDoor c(BlockPosition blockposition) {
        VillageDoor villagedoor = null;
        int i = Integer.MAX_VALUE;
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            VillageDoor villagedoor1 = (VillageDoor) iterator.next();
            int j = villagedoor1.a(blockposition);

            if (j > 256) {
                j *= 1000;
            } else {
                j = villagedoor1.c();
            }

            if (j < i) {
                BlockPosition blockposition1 = villagedoor1.d();
                EnumDirection enumdirection = villagedoor1.j();

                if (this.a.getType(blockposition1.shift(enumdirection, 1)).a((IBlockAccess) this.a, blockposition1.shift(enumdirection, 1), PathMode.LAND) && this.a.getType(blockposition1.shift(enumdirection, -1)).a((IBlockAccess) this.a, blockposition1.shift(enumdirection, -1), PathMode.LAND) && this.a.getType(blockposition1.up().shift(enumdirection, 1)).a((IBlockAccess) this.a, blockposition1.up().shift(enumdirection, 1), PathMode.LAND) && this.a.getType(blockposition1.up().shift(enumdirection, -1)).a((IBlockAccess) this.a, blockposition1.up().shift(enumdirection, -1), PathMode.LAND)) {
                    villagedoor = villagedoor1;
                    i = j;
                }
            }
        }

        return villagedoor;
    }

    @Nullable
    public VillageDoor e(BlockPosition blockposition) {
        if (this.d.n(blockposition) > (double) (this.e * this.e)) {
            return null;
        } else {
            Iterator iterator = this.b.iterator();

            VillageDoor villagedoor;

            do {
                if (!iterator.hasNext()) {
                    return null;
                }

                villagedoor = (VillageDoor) iterator.next();
            } while (villagedoor.d().getX() != blockposition.getX() || villagedoor.d().getZ() != blockposition.getZ() || Math.abs(villagedoor.d().getY() - blockposition.getY()) > 1);

            return villagedoor;
        }
    }

    public void a(VillageDoor villagedoor) {
        this.b.add(villagedoor);
        this.c = this.c.a((BaseBlockPosition) villagedoor.d());
        this.n();
        this.f = villagedoor.h();
    }

    public boolean g() {
        return this.b.isEmpty();
    }

    public void a(EntityLiving entityliving) {
        Iterator iterator = this.k.iterator();

        Village.Aggressor village_aggressor;

        do {
            if (!iterator.hasNext()) {
                this.k.add(new Village.Aggressor(entityliving, this.g));
                return;
            }

            village_aggressor = (Village.Aggressor) iterator.next();
        } while (village_aggressor.a != entityliving);

        village_aggressor.b = this.g;
    }

    @Nullable
    public EntityLiving b(EntityLiving entityliving) {
        double d0 = Double.MAX_VALUE;
        Village.Aggressor village_aggressor = null;

        for (int i = 0; i < this.k.size(); ++i) {
            Village.Aggressor village_aggressor1 = (Village.Aggressor) this.k.get(i);
            double d1 = village_aggressor1.a.h(entityliving);

            if (d1 <= d0) {
                village_aggressor = village_aggressor1;
                d0 = d1;
            }
        }

        return village_aggressor == null ? null : village_aggressor.a;
    }

    public EntityHuman c(EntityLiving entityliving) {
        double d0 = Double.MAX_VALUE;
        EntityHuman entityhuman = null;
        Iterator iterator = this.j.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            if (this.d(s)) {
                EntityHuman entityhuman1 = this.a.a(s);

                if (entityhuman1 != null) {
                    double d1 = entityhuman1.h(entityliving);

                    if (d1 <= d0) {
                        entityhuman = entityhuman1;
                        d0 = d1;
                    }
                }
            }
        }

        return entityhuman;
    }

    private void l() {
        Iterator iterator = this.k.iterator();

        while (iterator.hasNext()) {
            Village.Aggressor village_aggressor = (Village.Aggressor) iterator.next();

            if (!village_aggressor.a.isAlive() || Math.abs(this.g - village_aggressor.b) > 300) {
                iterator.remove();
            }
        }

    }

    private void m() {
        boolean flag = false;
        boolean flag1 = this.a.random.nextInt(50) == 0;
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            VillageDoor villagedoor = (VillageDoor) iterator.next();

            if (flag1) {
                villagedoor.a();
            }

            if (!this.g(villagedoor.d()) || Math.abs(this.g - villagedoor.h()) > 1200) {
                this.c = this.c.b(villagedoor.d());
                flag = true;
                villagedoor.a(true);
                iterator.remove();
            }
        }

        if (flag) {
            this.n();
        }

    }

    private boolean g(BlockPosition blockposition) {
        IBlockData iblockdata = this.a.getType(blockposition);
        Block block = iblockdata.getBlock();

        return block instanceof BlockDoor ? iblockdata.getMaterial() == Material.WOOD : false;
    }

    private void n() {
        int i = this.b.size();

        if (i == 0) {
            this.d = BlockPosition.ZERO;
            this.e = 0;
        } else {
            this.d = new BlockPosition(this.c.getX() / i, this.c.getY() / i, this.c.getZ() / i);
            int j = 0;

            VillageDoor villagedoor;

            for (Iterator iterator = this.b.iterator(); iterator.hasNext(); j = Math.max(villagedoor.a(this.d), j)) {
                villagedoor = (VillageDoor) iterator.next();
            }

            this.e = Math.max(32, (int) Math.sqrt((double) j) + 1);
        }
    }

    public int a(String s) {
        Integer integer = (Integer) this.j.get(s);

        return integer == null ? 0 : integer;
    }

    public int a(String s, int i) {
        int j = this.a(s);
        int k = MathHelper.clamp(j + i, -30, 10);

        this.j.put(s, k);
        return k;
    }

    public boolean d(String s) {
        return this.a(s) <= -15;
    }

    public void a(NBTTagCompound nbttagcompound) {
        this.h = nbttagcompound.getInt("PopSize");
        this.e = nbttagcompound.getInt("Radius");
        this.l = nbttagcompound.getInt("Golems");
        this.f = nbttagcompound.getInt("Stable");
        this.g = nbttagcompound.getInt("Tick");
        this.i = nbttagcompound.getInt("MTick");
        this.d = new BlockPosition(nbttagcompound.getInt("CX"), nbttagcompound.getInt("CY"), nbttagcompound.getInt("CZ"));
        this.c = new BlockPosition(nbttagcompound.getInt("ACX"), nbttagcompound.getInt("ACY"), nbttagcompound.getInt("ACZ"));
        NBTTagList nbttaglist = nbttagcompound.getList("Doors", 10);

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompound(i);
            VillageDoor villagedoor = new VillageDoor(new BlockPosition(nbttagcompound1.getInt("X"), nbttagcompound1.getInt("Y"), nbttagcompound1.getInt("Z")), nbttagcompound1.getInt("IDX"), nbttagcompound1.getInt("IDZ"), nbttagcompound1.getInt("TS"));

            this.b.add(villagedoor);
        }

        NBTTagList nbttaglist1 = nbttagcompound.getList("Players", 10);

        for (int j = 0; j < nbttaglist1.size(); ++j) {
            NBTTagCompound nbttagcompound2 = nbttaglist1.getCompound(j);

            if (nbttagcompound2.hasKey("UUID") && this.a != null && this.a.getMinecraftServer() != null) {
                UserCache usercache = this.a.getMinecraftServer().getUserCache();
                GameProfile gameprofile = usercache.a(UUID.fromString(nbttagcompound2.getString("UUID")));

                if (gameprofile != null) {
                    this.j.put(gameprofile.getName(), nbttagcompound2.getInt("S"));
                }
            } else {
                this.j.put(nbttagcompound2.getString("Name"), nbttagcompound2.getInt("S"));
            }
        }

    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("PopSize", this.h);
        nbttagcompound.setInt("Radius", this.e);
        nbttagcompound.setInt("Golems", this.l);
        nbttagcompound.setInt("Stable", this.f);
        nbttagcompound.setInt("Tick", this.g);
        nbttagcompound.setInt("MTick", this.i);
        nbttagcompound.setInt("CX", this.d.getX());
        nbttagcompound.setInt("CY", this.d.getY());
        nbttagcompound.setInt("CZ", this.d.getZ());
        nbttagcompound.setInt("ACX", this.c.getX());
        nbttagcompound.setInt("ACY", this.c.getY());
        nbttagcompound.setInt("ACZ", this.c.getZ());
        NBTTagList nbttaglist = new NBTTagList();
        Iterator iterator = this.b.iterator();

        while (iterator.hasNext()) {
            VillageDoor villagedoor = (VillageDoor) iterator.next();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();

            nbttagcompound1.setInt("X", villagedoor.d().getX());
            nbttagcompound1.setInt("Y", villagedoor.d().getY());
            nbttagcompound1.setInt("Z", villagedoor.d().getZ());
            nbttagcompound1.setInt("IDX", villagedoor.f());
            nbttagcompound1.setInt("IDZ", villagedoor.g());
            nbttagcompound1.setInt("TS", villagedoor.h());
            nbttaglist.add((NBTBase) nbttagcompound1);
        }

        nbttagcompound.set("Doors", nbttaglist);
        NBTTagList nbttaglist1 = new NBTTagList();
        Iterator iterator1 = this.j.keySet().iterator();

        while (iterator1.hasNext()) {
            String s = (String) iterator1.next();
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            UserCache usercache = this.a.getMinecraftServer().getUserCache();

            try {
                GameProfile gameprofile = usercache.getProfile(s);

                if (gameprofile != null) {
                    nbttagcompound2.setString("UUID", gameprofile.getId().toString());
                    nbttagcompound2.setInt("S", (Integer) this.j.get(s));
                    nbttaglist1.add((NBTBase) nbttagcompound2);
                }
            } catch (RuntimeException runtimeexception) {
                ;
            }
        }

        nbttagcompound.set("Players", nbttaglist1);
    }

    public void h() {
        this.i = this.g;
    }

    public boolean i() {
        return this.i == 0 || this.g - this.i >= 3600;
    }

    public void b(int i) {
        Iterator iterator = this.j.keySet().iterator();

        while (iterator.hasNext()) {
            String s = (String) iterator.next();

            this.a(s, i);
        }

    }

    class Aggressor {

        public EntityLiving a;
        public int b;

        Aggressor(EntityLiving entityliving, int i) {
            this.a = entityliving;
            this.b = i;
        }
    }
}
