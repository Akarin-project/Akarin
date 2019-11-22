package net.minecraft.server;

import javax.annotation.Nullable;

public class ChunkSection {

    public static final DataPalette<IBlockData> GLOBAL_PALETTE = new DataPaletteGlobal<>(Block.REGISTRY_ID, Blocks.AIR.getBlockData());
    private final int yPos;
    short nonEmptyBlockCount; // Paper - private -> package-private
    private short tickingBlockCount;
    private short e;
    final DataPaletteBlock<IBlockData> blockIds; // Paper - package

    public ChunkSection(int i) {
        // Paper start - add parameters
        this(i, (IChunkAccess)null, (IWorldReader)null, true);
    }
    public ChunkSection(int i, IChunkAccess chunk, IWorldReader world, boolean initializeBlocks) {
        this(i, (short) 0, (short) 0, (short) 0, chunk, world, initializeBlocks);
        // Paper end
    }

    public ChunkSection(int i, short short0, short short1, short short2) {
        // Paper start - add parameters
        this(i, short0, short1, short2, (IChunkAccess)null, (IWorldReader)null, true);
    }
    public ChunkSection(int i, short short0, short short1, short short2, IChunkAccess chunk, IWorldReader world, boolean initializeBlocks) {
        // Paper end
        this.yPos = i;
        this.nonEmptyBlockCount = short0;
        this.tickingBlockCount = short1;
        this.e = short2;
        this.blockIds = new DataPaletteBlock<>(ChunkSection.GLOBAL_PALETTE, Block.REGISTRY_ID, GameProfileSerializer::d, GameProfileSerializer::a, Blocks.AIR.getBlockData(), world instanceof GeneratorAccess ? ((GeneratorAccess) world).getMinecraftWorld().chunkPacketBlockController.getPredefinedBlockData(world, chunk, this, initializeBlocks) : null, initializeBlocks); // Paper - Anti-Xray - Add predefined block data
    }

    public IBlockData getType(int i, int j, int k) {
        return (IBlockData) this.blockIds.a(i, j, k);
    }

    public Fluid b(int i, int j, int k) {
        return ((IBlockData) this.blockIds.a(i, j, k)).p();
    }

    public void a() {
        this.blockIds.a();
    }

    public void b() {
        this.blockIds.b();
    }

    public IBlockData setType(int i, int j, int k, IBlockData iblockdata) {
        return this.setType(i, j, k, iblockdata, true);
    }

    public IBlockData setType(int i, int j, int k, IBlockData iblockdata, boolean flag) {
        IBlockData iblockdata1;

        if (flag) {
            iblockdata1 = (IBlockData) this.blockIds.setBlock(i, j, k, iblockdata);
        } else {
            iblockdata1 = (IBlockData) this.blockIds.b(i, j, k, iblockdata);
        }

        Fluid fluid = iblockdata1.p();
        Fluid fluid1 = iblockdata.p();

        if (!iblockdata1.isAir()) {
            --this.nonEmptyBlockCount;
            if (iblockdata1.q()) {
                --this.tickingBlockCount;
            }
        }

        if (!fluid.isEmpty()) {
            --this.e;
        }

        if (!iblockdata.isAir()) {
            ++this.nonEmptyBlockCount;
            if (iblockdata.q()) {
                ++this.tickingBlockCount;
            }
        }

        if (!fluid1.isEmpty()) {
            ++this.e;
        }

        return iblockdata1;
    }

    public boolean c() {
        return this.nonEmptyBlockCount == 0;
    }

    public static boolean a(@Nullable ChunkSection chunksection) {
        return chunksection == Chunk.a || chunksection.c();
    }

    public boolean d() {
        return this.shouldTick() || this.f();
    }

    public boolean shouldTick() {
        return this.tickingBlockCount > 0;
    }

    public boolean f() {
        return this.e > 0;
    }

    public int getYPosition() {
        return this.yPos;
    }

    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;
        this.e = 0;
        this.blockIds.a((iblockdata, i) -> {
            Fluid fluid = iblockdata.p();

            if (!iblockdata.isAir()) {
                this.nonEmptyBlockCount = (short) (this.nonEmptyBlockCount + i);
                if (iblockdata.q()) {
                    this.tickingBlockCount = (short) (this.tickingBlockCount + i);
                }
            }

            if (!fluid.isEmpty()) {
                this.nonEmptyBlockCount = (short) (this.nonEmptyBlockCount + i);
                if (fluid.h()) {
                    this.e = (short) (this.e + i);
                }
            }

        });
    }

    public DataPaletteBlock<IBlockData> getBlocks() {
        return this.blockIds;
    }

    public void b(PacketDataSerializer packetdataserializer) {
        packetdataserializer.writeShort(this.nonEmptyBlockCount);
        this.blockIds.b(packetdataserializer);
    }

    public int j() {
        return 2 + this.blockIds.c();
    }

    public boolean a(IBlockData iblockdata) {
        return this.blockIds.a(iblockdata); // Paper - decompile fix
    }
}
