package net.minecraft.server;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nullable;

public class TileEntityConduit extends TileEntity implements ITickable {

    private static final Block[] e = new Block[] { Blocks.PRISMARINE, Blocks.PRISMARINE_BRICKS, Blocks.SEA_LANTERN, Blocks.DARK_PRISMARINE};
    public int a;
    private float f;
    private boolean g;
    private boolean h;
    private final List<BlockPosition> i;
    private EntityLiving target;
    private UUID k;
    private long l;

    public TileEntityConduit() {
        this(TileEntityTypes.CONDUIT);
    }

    public TileEntityConduit(TileEntityTypes<?> tileentitytypes) {
        super(tileentitytypes);
        this.i = Lists.newArrayList();
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        if (nbttagcompound.hasKey("target_uuid")) {
            this.k = GameProfileSerializer.b(nbttagcompound.getCompound("target_uuid"));
        } else {
            this.k = null;
        }

    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        if (this.target != null) {
            nbttagcompound.set("target_uuid", GameProfileSerializer.a(this.target.getUniqueID()));
        }

        return nbttagcompound;
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        return new PacketPlayOutTileEntityData(this.position, 5, this.aa_());
    }

    public NBTTagCompound aa_() {
        return this.save(new NBTTagCompound());
    }

    public void tick() {
        ++this.a;
        long i = this.world.getTime();

        if (i % 40L == 0L) {
            this.a(this.f());
            if (!this.world.isClientSide && this.c()) {
                this.h();
                this.i();
            }
        }

        if (i % 80L == 0L && this.c()) {
            this.a(SoundEffects.BLOCK_CONDUIT_AMBIENT);
        }

        if (i > this.l && this.c()) {
            this.l = i + 60L + (long) this.world.m().nextInt(40);
            this.a(SoundEffects.BLOCK_CONDUIT_AMBIENT_SHORT);
        }

        if (this.world.isClientSide) {
            this.j();
            this.m();
            if (this.c()) {
                ++this.f;
            }
        }

    }

    private boolean f() {
        this.i.clear();

        int i;
        int j;
        int k;

        for (i = -1; i <= 1; ++i) {
            for (j = -1; j <= 1; ++j) {
                for (k = -1; k <= 1; ++k) {
                    BlockPosition blockposition = this.position.a(i, j, k);

                    if (!this.world.B(blockposition)) {
                        return false;
                    }
                }
            }
        }

        for (i = -2; i <= 2; ++i) {
            for (j = -2; j <= 2; ++j) {
                for (k = -2; k <= 2; ++k) {
                    int l = Math.abs(i);
                    int i1 = Math.abs(j);
                    int j1 = Math.abs(k);

                    if ((l > 1 || i1 > 1 || j1 > 1) && (i == 0 && (i1 == 2 || j1 == 2) || j == 0 && (l == 2 || j1 == 2) || k == 0 && (l == 2 || i1 == 2))) {
                        BlockPosition blockposition1 = this.position.a(i, j, k);
                        IBlockData iblockdata = this.world.getType(blockposition1);
                        Block[] ablock = TileEntityConduit.e;
                        int k1 = ablock.length;

                        for (int l1 = 0; l1 < k1; ++l1) {
                            Block block = ablock[l1];

                            if (iblockdata.getBlock() == block) {
                                this.i.add(blockposition1);
                            }
                        }
                    }
                }
            }
        }

        this.b(this.i.size() >= 42);
        return this.i.size() >= 16;
    }

    private void h() {
        int i = this.i.size();
        int j = i / 7 * 16;
        int k = this.position.getX();
        int l = this.position.getY();
        int i1 = this.position.getZ();
        AxisAlignedBB axisalignedbb = (new AxisAlignedBB((double) k, (double) l, (double) i1, (double) (k + 1), (double) (l + 1), (double) (i1 + 1))).g((double) j).b(0.0D, (double) this.world.getHeight(), 0.0D);
        List<EntityHuman> list = this.world.a(EntityHuman.class, axisalignedbb);

        if (!list.isEmpty()) {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                EntityHuman entityhuman = (EntityHuman) iterator.next();

                if (this.position.m(new BlockPosition(entityhuman)) <= (double) j && entityhuman.ao()) {
                    entityhuman.addEffect(new MobEffect(MobEffects.CONDUIT_POWER, 260, 0, true, true));
                }
            }

        }
    }

    private void i() {
        EntityLiving entityliving = this.target;
        int i = this.i.size();

        if (i < 42) {
            this.target = null;
        } else if (this.target == null && this.k != null) {
            this.target = this.l();
            this.k = null;
        } else if (this.target == null) {
            List<EntityLiving> list = this.world.a(EntityLiving.class, this.k(), (entityliving1) -> {
                return entityliving1 instanceof IMonster && entityliving1.ao();
            });

            if (!list.isEmpty()) {
                this.target = (EntityLiving) list.get(this.world.random.nextInt(list.size()));
            }
        } else if (!this.target.isAlive() || this.position.m(new BlockPosition(this.target)) > 8.0D) {
            this.target = null;
        }

        if (this.target != null) {
            this.world.a((EntityHuman) null, this.target.locX, this.target.locY, this.target.locZ, SoundEffects.BLOCK_CONDUIT_ATTACK_TARGET, SoundCategory.BLOCKS, 1.0F, 1.0F);
            this.target.damageEntity(DamageSource.MAGIC, 4.0F);
        }

        if (entityliving != this.target) {
            IBlockData iblockdata = this.getBlock();

            this.world.notify(this.position, iblockdata, iblockdata, 2);
        }

    }

    private void j() {
        if (this.k == null) {
            this.target = null;
        } else if (this.target == null || !this.target.getUniqueID().equals(this.k)) {
            this.target = this.l();
            if (this.target == null) {
                this.k = null;
            }
        }

    }

    private AxisAlignedBB k() {
        int i = this.position.getX();
        int j = this.position.getY();
        int k = this.position.getZ();

        return (new AxisAlignedBB((double) i, (double) j, (double) k, (double) (i + 1), (double) (j + 1), (double) (k + 1))).g(8.0D);
    }

    @Nullable
    private EntityLiving l() {
        List<EntityLiving> list = this.world.a(EntityLiving.class, this.k(), (entityliving) -> {
            return entityliving.getUniqueID().equals(this.k);
        });

        return list.size() == 1 ? (EntityLiving) list.get(0) : null;
    }

    private void m() {
        Random random = this.world.random;
        float f = MathHelper.sin((float) (this.a + 35) * 0.1F) / 2.0F + 0.5F;

        f = (f * f + f) * 0.3F;
        Vec3D vec3d = new Vec3D((double) ((float) this.position.getX() + 0.5F), (double) ((float) this.position.getY() + 1.5F + f), (double) ((float) this.position.getZ() + 0.5F));
        Iterator iterator = this.i.iterator();

        float f1;
        float f2;

        while (iterator.hasNext()) {
            BlockPosition blockposition = (BlockPosition) iterator.next();

            if (random.nextInt(50) == 0) {
                f1 = -0.5F + random.nextFloat();
                f2 = -2.0F + random.nextFloat();
                float f3 = -0.5F + random.nextFloat();
                BlockPosition blockposition1 = blockposition.b(this.position);
                Vec3D vec3d1 = (new Vec3D((double) f1, (double) f2, (double) f3)).add((double) blockposition1.getX(), (double) blockposition1.getY(), (double) blockposition1.getZ());

                this.world.addParticle(Particles.W, vec3d.x, vec3d.y, vec3d.z, vec3d1.x, vec3d1.y, vec3d1.z);
            }
        }

        if (this.target != null) {
            Vec3D vec3d2 = new Vec3D(this.target.locX, this.target.locY + (double) this.target.getHeadHeight(), this.target.locZ);
            float f4 = (-0.5F + random.nextFloat()) * (3.0F + this.target.width);

            f1 = -1.0F + random.nextFloat() * this.target.length;
            f2 = (-0.5F + random.nextFloat()) * (3.0F + this.target.width);
            Vec3D vec3d3 = new Vec3D((double) f4, (double) f1, (double) f2);

            this.world.addParticle(Particles.W, vec3d2.x, vec3d2.y, vec3d2.z, vec3d3.x, vec3d3.y, vec3d3.z);
        }

    }

    public boolean c() {
        return this.g;
    }

    private void a(boolean flag) {
        if (flag != this.g) {
            this.a(flag ? SoundEffects.BLOCK_CONDUIT_ACTIVATE : SoundEffects.BLOCK_CONDUIT_DEACTIVATE);
        }

        this.g = flag;
    }

    private void b(boolean flag) {
        this.h = flag;
    }

    public void a(SoundEffect soundeffect) {
        this.world.a((EntityHuman) null, this.position, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
