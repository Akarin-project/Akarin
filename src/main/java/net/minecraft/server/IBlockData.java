package net.minecraft.server;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import org.bukkit.craftbukkit.block.data.CraftBlockData;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;

public class IBlockData extends BlockDataAbstract<Block, IBlockData> implements IBlockDataHolder<IBlockData> {

    @Nullable
    private IBlockData.a c;
    private final int d;
    private final boolean e;

    public IBlockData(Block block, ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap) {
        super(block, immutablemap);
        this.d = block.a(this);
        this.e = block.n(this);
    }

    public void c() {
        if (!this.getBlock().p()) {
            this.c = new IBlockData.a(this);
        }

    }

    public Block getBlock() {
        return (Block) this.a;
    }

    // Paper start - impl cached craft block data, lazy load to fix issue with loading at the wrong time
    private CraftBlockData cachedCraftBlockData;

    public CraftBlockData createCraftBlockData() {
        if(cachedCraftBlockData == null) cachedCraftBlockData = CraftBlockData.createData(this);
        return (CraftBlockData) cachedCraftBlockData.clone();
    }
    // Paper end

    public Material getMaterial() {
        return this.getBlock().l(this);
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, EntityTypes<?> entitytypes) {
        return this.getBlock().a(this, iblockaccess, blockposition, entitytypes);
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.c != null ? this.c.d : this.getBlock().b(this, iblockaccess, blockposition);
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.c != null ? this.c.e : this.getBlock().k(this, iblockaccess, blockposition);
    }

    public VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.c != null && this.c.f != null ? this.c.f[enumdirection.ordinal()] : VoxelShapes.a(this.j(iblockaccess, blockposition), enumdirection);
    }

    public boolean f() {
        return this.c == null || this.c.h;
    }

    public boolean g() {
        return this.e;
    }

    public int h() {
        return this.d;
    }

    public boolean isAir() {
        return this.getBlock().e(this);
    }

    public MaterialMapColor c(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().e(this, iblockaccess, blockposition);
    }

    public IBlockData a(EnumBlockRotation enumblockrotation) {
        return this.getBlock().a(this, enumblockrotation);
    }

    public IBlockData a(EnumBlockMirror enumblockmirror) {
        return this.getBlock().a(this, enumblockmirror);
    }

    public EnumRenderType k() {
        return this.getBlock().c(this);
    }

    public boolean isOccluding(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().isOccluding(this, iblockaccess, blockposition);
    }

    public boolean isPowerSource() {
        return this.getBlock().isPowerSource(this);
    }

    public int b(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlock().a(this, iblockaccess, blockposition, enumdirection);
    }

    public boolean isComplexRedstone() {
        return this.getBlock().isComplexRedstone(this);
    }

    public int a(World world, BlockPosition blockposition) {
        return this.getBlock().a(this, world, blockposition);
    }

    public float f(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().f(this, iblockaccess, blockposition);
    }

    public float getDamage(EntityHuman entityhuman, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().getDamage(this, entityhuman, iblockaccess, blockposition);
    }

    public int c(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.getBlock().b(this, iblockaccess, blockposition, enumdirection);
    }

    public EnumPistonReaction getPushReaction() {
        return this.getBlock().getPushReaction(this);
    }

    public boolean g(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.c != null ? this.c.c : this.getBlock().j(this, iblockaccess, blockposition);
    }

    public boolean o() {
        return this.c != null ? this.c.b : this.getBlock().f(this);
    }

    public VoxelShape getShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.a(iblockaccess, blockposition, VoxelShapeCollision.a());
    }

    public VoxelShape a(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getBlock().a(this, iblockaccess, blockposition, voxelshapecollision);
    }

    public VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.c != null ? this.c.g : this.b(iblockaccess, blockposition, VoxelShapeCollision.a());
    }

    public final VoxelShape getCollisionShape(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) { return this.b(iblockaccess, blockposition, voxelshapecollision); } // Paper - OBFHELPER
    public VoxelShape b(IBlockAccess iblockaccess, BlockPosition blockposition, VoxelShapeCollision voxelshapecollision) {
        return this.getBlock().b(this, iblockaccess, blockposition, voxelshapecollision);
    }

    public VoxelShape j(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().h(this, iblockaccess, blockposition);
    }

    public VoxelShape k(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().i(this, iblockaccess, blockposition);
    }

    public final boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, Entity entity) {
        return Block.a(this.b(iblockaccess, blockposition, VoxelShapeCollision.a(entity)), EnumDirection.UP);
    }

    public Vec3D l(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().l(this, iblockaccess, blockposition);
    }

    public boolean a(World world, BlockPosition blockposition, int i, int j) {
        return this.getBlock().a(this, world, blockposition, i, j);
    }

    public void doPhysics(World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        this.getBlock().doPhysics(this, world, blockposition, block, blockposition1, flag);
    }

    public void a(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        this.getBlock().a(this, generatoraccess, blockposition, i);
    }

    public void b(GeneratorAccess generatoraccess, BlockPosition blockposition, int i) {
        this.getBlock().b(this, generatoraccess, blockposition, i);
    }

    public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        this.getBlock().onPlace(this, world, blockposition, iblockdata, flag);
    }

    public void remove(World world, BlockPosition blockposition, IBlockData iblockdata, boolean flag) {
        this.getBlock().remove(this, world, blockposition, iblockdata, flag);
    }

    public void a(World world, BlockPosition blockposition, Random random) {
        this.getBlock().tick(this, world, blockposition, random);
    }

    public void b(World world, BlockPosition blockposition, Random random) {
        this.getBlock().c(this, world, blockposition, random);
    }

    public void a(World world, BlockPosition blockposition, Entity entity) {
        this.getBlock().a(this, world, blockposition, entity);
    }

    public void dropNaturally(World world, BlockPosition blockposition, ItemStack itemstack) {
        this.getBlock().dropNaturally(this, world, blockposition, itemstack);
    }

    public List<ItemStack> a(LootTableInfo.Builder loottableinfo_builder) {
        return this.getBlock().a(this, loottableinfo_builder);
    }

    public boolean interact(World world, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        return this.getBlock().interact(this, world, movingobjectpositionblock.getBlockPosition(), entityhuman, enumhand, movingobjectpositionblock);
    }

    public void attack(World world, BlockPosition blockposition, EntityHuman entityhuman) {
        this.getBlock().attack(this, world, blockposition, entityhuman);
    }

    public boolean m(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().c(this, iblockaccess, blockposition);
    }

    public IBlockData updateState(EnumDirection enumdirection, IBlockData iblockdata, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return this.getBlock().updateState(this, enumdirection, iblockdata, generatoraccess, blockposition, blockposition1);
    }

    public boolean a(IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return this.getBlock().a(this, iblockaccess, blockposition, pathmode);
    }

    public boolean a(BlockActionContext blockactioncontext) {
        return this.getBlock().a(this, blockactioncontext);
    }

    public boolean canPlace(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.getBlock().canPlace(this, iworldreader, blockposition);
    }

    public boolean n(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.getBlock().g(this, iblockaccess, blockposition);
    }

    @Nullable
    public ITileInventory b(World world, BlockPosition blockposition) {
        return this.getBlock().getInventory(this, world, blockposition);
    }

    public boolean a(Tag<Block> tag) {
        return this.getBlock().a(tag);
    }

    public Fluid p() {
        return this.getBlock().g(this);
    }

    public boolean q() {
        return this.getBlock().isTicking(this);
    }

    public final SoundEffectType getStepSound() { return this.r(); } // Paper - OBFHELPER
    public SoundEffectType r() {
        return this.getBlock().getStepSound(this);
    }

    public void a(World world, IBlockData iblockdata, MovingObjectPositionBlock movingobjectpositionblock, Entity entity) {
        this.getBlock().a(world, iblockdata, movingobjectpositionblock, entity);
    }

    public boolean d(IBlockAccess iblockaccess, BlockPosition blockposition, EnumDirection enumdirection) {
        return this.c != null ? this.c.i[enumdirection.ordinal()] : Block.d(this, iblockaccess, blockposition, enumdirection);
    }

    public boolean o(IBlockAccess iblockaccess, BlockPosition blockposition) {
        return this.c != null ? this.c.j : Block.a(this.getCollisionShape(iblockaccess, blockposition));
    }

    public static <T> Dynamic<T> a(DynamicOps<T> dynamicops, IBlockData iblockdata) {
        ImmutableMap<IBlockState<?>, Comparable<?>> immutablemap = iblockdata.getStateMap();
        T object; // Paper - decompile fix

        if (immutablemap.isEmpty()) {
            object = dynamicops.createMap(ImmutableMap.of(dynamicops.createString("Name"), dynamicops.createString(IRegistry.BLOCK.getKey(iblockdata.getBlock()).toString())));
        } else {
            object = dynamicops.createMap(ImmutableMap.of(dynamicops.createString("Name"), dynamicops.createString(IRegistry.BLOCK.getKey(iblockdata.getBlock()).toString()), dynamicops.createString("Properties"), dynamicops.createMap(immutablemap.entrySet().stream().map((entry) -> { // Paper - decompile fix
                return Pair.of(dynamicops.createString(((IBlockState) entry.getKey()).a()), dynamicops.createString(IBlockDataHolder.b((IBlockState) entry.getKey(), (Comparable) entry.getValue())));
            }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
        }

        return new Dynamic(dynamicops, object);
    }

    public static <T> IBlockData a(Dynamic<T> dynamic) {
        RegistryBlocks registryblocks = IRegistry.BLOCK;
        Optional optional = dynamic.getElement("Name");
        DynamicOps dynamicops = dynamic.getOps();

        dynamicops.getClass();
        Block block = (Block) registryblocks.get(new MinecraftKey((String) optional.flatMap(dynamicops::getStringValue).orElse("minecraft:air")));
        Map<String, String> map = dynamic.get("Properties").asMap((dynamic1) -> {
            return dynamic1.asString("");
        }, (dynamic1) -> {
            return dynamic1.asString("");
        });
        IBlockData iblockdata = block.getBlockData();
        BlockStateList<Block, IBlockData> blockstatelist = block.getStates();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, String> entry = (Entry) iterator.next();
            String s = (String) entry.getKey();
            IBlockState<?> iblockstate = blockstatelist.a(s);

            if (iblockstate != null) {
                iblockdata = (IBlockData) IBlockDataHolder.a(iblockdata, iblockstate, s, dynamic.toString(), (String) entry.getValue());
            }
        }

        return iblockdata;
    }

    static final class a {

        private static final EnumDirection[] a = EnumDirection.values();
        private final boolean b;
        private final boolean c;
        private final boolean d;
        private final int e;
        private final VoxelShape[] f;
        private final VoxelShape g;
        private final boolean h;
        private final boolean[] i;
        private final boolean j;

        private a(IBlockData iblockdata) {
            Block block = iblockdata.getBlock();

            this.b = block.f(iblockdata);
            this.c = block.j(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
            this.d = block.b(iblockdata, (IBlockAccess) BlockAccessAir.INSTANCE, BlockPosition.ZERO);
            this.e = block.k(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
            int i;

            if (!iblockdata.o()) {
                this.f = null;
            } else {
                this.f = new VoxelShape[a.length]; // Paper - decompile fix
                VoxelShape voxelshape = block.h(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO);
                EnumDirection[] aenumdirection = a; // Paper - decompile fix

                i = aenumdirection.length;

                for (int j = 0; j < i; ++j) {
                    EnumDirection enumdirection = aenumdirection[j];

                    this.f[enumdirection.ordinal()] = VoxelShapes.a(voxelshape, enumdirection);
                }
            }

            this.g = block.b(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO, VoxelShapeCollision.a());
            this.h = Arrays.stream(EnumDirection.EnumAxis.values()).anyMatch((enumdirection_enumaxis) -> {
                return this.g.b(enumdirection_enumaxis) < 0.0D || this.g.c(enumdirection_enumaxis) > 1.0D;
            });
            this.i = new boolean[6];
            EnumDirection[] aenumdirection1 = a; // Paper - decompile fix
            int k = aenumdirection1.length;

            for (i = 0; i < k; ++i) {
                EnumDirection enumdirection1 = aenumdirection1[i];

                this.i[enumdirection1.ordinal()] = Block.d(iblockdata, BlockAccessAir.INSTANCE, BlockPosition.ZERO, enumdirection1);
            }

            this.j = Block.a(iblockdata.getCollisionShape(BlockAccessAir.INSTANCE, BlockPosition.ZERO));
        }
    }
}
