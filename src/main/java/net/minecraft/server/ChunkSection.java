package net.minecraft.server;

public class ChunkSection {

    public static final DataPalette<IBlockData> GLOBAL_PALETTE = new DataPaletteGlobal<>(Block.REGISTRY_ID, Blocks.AIR.getBlockData());
    private final int yPos;
    private int nonEmptyBlockCount;
    private int tickingBlockCount;
    private int e;
    private final DataPaletteBlock<IBlockData> blockIds;
    private NibbleArray emittedLight;
    private NibbleArray skyLight;

    public ChunkSection(int i, boolean flag) {
        this.yPos = i;
        this.blockIds = new DataPaletteBlock<>(ChunkSection.GLOBAL_PALETTE, Block.REGISTRY_ID, GameProfileSerializer::d, GameProfileSerializer::a, Blocks.AIR.getBlockData());
        this.emittedLight = new NibbleArray();
        if (flag) {
            this.skyLight = new NibbleArray();
        }

    }

    public IBlockData getType(int i, int j, int k) {
        return (IBlockData) this.blockIds.a(i, j, k);
    }

    public Fluid b(int i, int j, int k) {
        return ((IBlockData) this.blockIds.a(i, j, k)).s();
    }

    public void setType(int i, int j, int k, IBlockData iblockdata) {
        IBlockData iblockdata1 = this.getType(i, j, k);
        Fluid fluid = this.b(i, j, k);
        Fluid fluid1 = iblockdata.s();

        if (!iblockdata1.isAir()) {
            --this.nonEmptyBlockCount;
            if (iblockdata1.t()) {
                --this.tickingBlockCount;
            }
        }

        if (!fluid.e()) {
            --this.e;
        }

        if (!iblockdata.isAir()) {
            ++this.nonEmptyBlockCount;
            if (iblockdata.t()) {
                ++this.tickingBlockCount;
            }
        }

        if (!fluid1.e()) {
            --this.e;
        }

        this.blockIds.setBlock(i, j, k, iblockdata);
    }

    public boolean a() {
        return this.nonEmptyBlockCount == 0;
    }

    public boolean b() {
        return this.shouldTick() || this.d();
    }

    public boolean shouldTick() {
        return this.tickingBlockCount > 0;
    }

    public boolean d() {
        return this.e > 0;
    }

    public int getYPosition() {
        return this.yPos;
    }

    public void a(int i, int j, int k, int l) {
        this.skyLight.a(i, j, k, l);
    }

    public int c(int i, int j, int k) {
        return this.skyLight.a(i, j, k);
    }

    public void b(int i, int j, int k, int l) {
        this.emittedLight.a(i, j, k, l);
    }

    public int d(int i, int j, int k) {
        return this.emittedLight.a(i, j, k);
    }

    public void recalcBlockCounts() {
        this.nonEmptyBlockCount = 0;
        this.tickingBlockCount = 0;
        this.e = 0;

        for (int i = 0; i < 16; ++i) {
            for (int j = 0; j < 16; ++j) {
                for (int k = 0; k < 16; ++k) {
                    IBlockData iblockdata = this.getType(i, j, k);
                    Fluid fluid = this.b(i, j, k);

                    if (!iblockdata.isAir()) {
                        ++this.nonEmptyBlockCount;
                        if (iblockdata.t()) {
                            ++this.tickingBlockCount;
                        }
                    }

                    if (!fluid.e()) {
                        ++this.nonEmptyBlockCount;
                        if (fluid.h()) {
                            ++this.e;
                        }
                    }
                }
            }
        }

    }

    public DataPaletteBlock<IBlockData> getBlocks() {
        return this.blockIds;
    }

    public NibbleArray getEmittedLightArray() {
        return this.emittedLight;
    }

    public NibbleArray getSkyLightArray() {
        return this.skyLight;
    }

    public void a(NibbleArray nibblearray) {
        this.emittedLight = nibblearray;
    }

    public void b(NibbleArray nibblearray) {
        this.skyLight = nibblearray;
    }
}
