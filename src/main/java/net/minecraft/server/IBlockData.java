package net.minecraft.server;

import it.unimi.dsi.fastutil.objects.Object2ByteMap;
import it.unimi.dsi.fastutil.objects.Object2ByteOpenHashMap;
import java.util.Random;

public interface IBlockData extends IBlockDataHolder<IBlockData> {

    ThreadLocal<Object2ByteMap<IBlockData>> a = ThreadLocal.withInitial(() -> {
        Object2ByteOpenHashMap<IBlockData> object2byteopenhashmap = new Object2ByteOpenHashMap();

        object2byteopenhashmap.defaultReturnValue((byte) 127);
        return object2byteopenhashmap;
    });
    ThreadLocal<Object2ByteMap<IBlockData>> b = ThreadLocal.withInitial(() -> {
        Object2ByteOpenHashMap<IBlockData> object2byteopenhashmap = new Object2ByteOpenHashMap();

        object2byteopenhashmap.defaultReturnValue((byte) 127);
        return object2byteopenhashmap;
    });
    ThreadLocal<Object2ByteMap<IBlockData>> c = ThreadLocal.withInitial(() -> {
        Object2ByteOpenHashMap<IBlockData> object2byteopenhashmap = new Object2ByteOpenHashMap();

        object2byteopenhashmap.defaultReturnValue((byte) 127);
        return object2byteopenhashmap;
    });

    Block getBlock();

    default Material getMaterial() {
        return this.getBlock().n(this);
    }

    default boolean a(Entity entity) {
        return this.getBlock().a(this, entity);
    }

    default boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = this.getBlock();
        Object2ByteMap<IBlockData> object2bytemap = block.s() ? null : (Object2ByteMap) IBlockData.a.get();

        if (object2bytemap != null) {
            byte b0 = object2bytemap.getByte(this);

            if (b0 != object2bytemap.defaultReturnValue()) {
                return b0 != 0;
            }
        }

        boolean flag = block.a_(this, iblockaccess, blockposition);

        if (object2bytemap != null) {
            object2bytemap.put(this, (byte) (flag ? 1 : 0));
        }

        return flag;
    }

    default int b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = this.getBlock();
        Object2ByteMap<IBlockData> object2bytemap = block.s() ? null : (Object2ByteMap) IBlockData.b.get();

        if (object2bytemap != null) {
            byte b0 = object2bytemap.getByte(this);

            if (b0 != object2bytemap.defaultReturnValue()) {
                return b0;
            }
        }

        int i = block.j(this, iblockaccess, blockposition);

        if (object2bytemap != null) {
            object2bytemap.put(this, (byte) Math.min(i, iblockaccess.K()));
        }

        return i;
    }

    default int e() {
        return this.getBlock().m(this);
    }

    default boolean isAir() {
        return this.getBlock().e(this);
    }

    default boolean c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().k(this, iblockaccess, blockposition);
    }

    default MaterialMapColor d(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().c(this, iblockaccess, blockposition);
    }

    default IBlockData a(EnumBlockRotation enumblockrotation) {
        return this.getBlock().a(this, enumblockrotation);
    }

    default IBlockData a(EnumBlockMirror enumblockmirror) {
        return this.getBlock().a(this, enumblockmirror);
    }

    default boolean g() {
        return this.getBlock().a(this);
    }

    default EnumRenderType i() {
        return this.getBlock().c(this);
    }

    default boolean k() {
        return this.getBlock().o(this);
    }

    default boolean isOccluding() {
        return this.getBlock().isOccluding(this);
    }

    default boolean isPowerSource() {
        return this.getBlock().isPowerSource(this);
    }

    default int a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlock().a(this, iblockaccess, blockposition, enumdirection);
    }

    default boolean isComplexRedstone() {
        return this.getBlock().isComplexRedstone(this);
    }

    default int a(World world, BlockPosition blockposition) {
        return this.getBlock().a(this, world, blockposition);
    }

    default float e(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().d(this, iblockaccess, blockposition);
    }

    default float getDamage(EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().getDamage(this, entityhuman, iblockaccess, blockposition);
    }

    default int b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlock().b(this, iblockaccess, blockposition, enumdirection);
    }

    default EnumPistonReaction getPushReaction() {
        return this.getBlock().getPushReaction(this);
    }

    default boolean f(IBlockAccess iblockaccess, BlockPosition blockposition) {
        Block block = this.getBlock();
        Object2ByteMap<IBlockData> object2bytemap = block.s() ? null : (Object2ByteMap) IBlockData.c.get();

        if (object2bytemap != null) {
            byte b0 = object2bytemap.getByte(this);

            if (b0 != object2bytemap.defaultReturnValue()) {
                return b0 != 0;
            }
        }

        boolean flag = block.i(this, iblockaccess, blockposition);

        if (object2bytemap != null) {
            object2bytemap.put(this, (byte) (flag ? 1 : 0));
        }

        return flag;
    }

    default boolean p() {
        return this.getBlock().f(this);
    }

    default VoxelShape getShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().a(this, iblockaccess, blockposition);
    }

    default VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().f(this, iblockaccess, blockposition);
    }

    default VoxelShape i(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().g(this, iblockaccess, blockposition);
    }

    default VoxelShape j(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().h(this, iblockaccess, blockposition);
    }

    default boolean q() {
        return this.getBlock().r(this);
    }

    default Vec3D k(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().l(this, iblockaccess, blockposition);
    }

    default boolean a(World world, BlockPosition blockposition, int i, int j) {
        return this.getBlock().a(this, world, blockposition, i, j);
    }

    default void doPhysics(World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        this.getBlock().doPhysics(this, world, blockposition, block, blockposition1);
    }

    default void a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        this.getBlock().a(this, generatoraccess, blockposition, i);
    }

    default void b(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        this.getBlock().b(this, generatoraccess, blockposition, i);
    }

    default void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
        this.getBlock().onPlace(this, world, blockposition, iblockdata);
    }

    default void remove(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        this.getBlock().remove(this, world, blockposition, iblockdata, flag);
    }

    default void a(World world, BlockPosition blockposition, Random random) {
        this.getBlock().a(this, world, blockposition, random);
    }

    default void b(World world, BlockPosition blockposition, Random random) {
        this.getBlock().b(this, world, blockposition, random);
    }

    default void a(World world, BlockPosition blockposition, Entity entity) {
        this.getBlock().a(this, world, blockposition, entity);
    }

    default void a(World world, BlockPosition blockposition, int i) {
        this.dropNaturally(world, blockposition, 1.0F, i);
    }

    default void dropNaturally(World world, BlockPosition blockposition, float f, int i) {
        this.getBlock().dropNaturally(this, world, blockposition, f, i);
    }

    default boolean interact(World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        return this.getBlock().interact(this, world, blockposition, entityhuman, enumhand, enumdirection, f, f1, f2);
    }

    default void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.getBlock().attack(this, world, blockposition, entityhuman);
    }

    default boolean r() {
        return this.getBlock().q(this);
    }

    default EnumBlockFaceShape c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlock().a(iblockaccess, this, blockposition, enumdirection);
    }

    default IBlockData updateState(EnumDirection enumdirection, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return this.getBlock().updateState(this, enumdirection, iblockdata, generatoraccess, blockposition, blockposition1);
    }

    default boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return this.getBlock().a(this, iblockaccess, blockposition, pathmode);
    }

    default boolean a(BlockActionContext blockactioncontext) {
        return this.getBlock().a(this, blockactioncontext);
    }

    default boolean canPlace(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.getBlock().canPlace(this, iworldreader, blockposition);
    }

    default boolean l(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().e(this, iblockaccess, blockposition);
    }

    default boolean a(Tag<Block> tag) {
        return this.getBlock().a(tag);
    }

    default Fluid s() {
        return this.getBlock().h(this);
    }

    default boolean t() {
        return this.getBlock().isTicking(this);
    }
}
