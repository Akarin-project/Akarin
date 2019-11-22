package net.minecraft.server;

public class BlockNote extends Block {

    public static final BlockStateEnum<BlockPropertyInstrument> INSTRUMENT = BlockProperties.aA;
    public static final BlockStateBoolean POWERED = BlockProperties.w;
    public static final BlockStateInteger NOTE = BlockProperties.aq;

    public BlockNote(Block.Info block_info) {
        super(block_info);
        this.o((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockNote.INSTRUMENT, BlockPropertyInstrument.HARP)).set(BlockNote.NOTE, 0)).set(BlockNote.POWERED, false));
    }

    @Override
    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockNote.INSTRUMENT, BlockPropertyInstrument.a(blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().down())));
    }

    @Override
    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN ? (IBlockData) iblockdata.set(BlockNote.INSTRUMENT, BlockPropertyInstrument.a(iblockdata1)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    @Override
    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1, boolean flag) {
        boolean flag1 = world.isBlockIndirectlyPowered(blockposition);

        if (flag1 != (Boolean) iblockdata.get(BlockNote.POWERED)) {
            if (flag1) {
                this.play(world, blockposition, iblockdata); // CraftBukkit
            }

            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockNote.POWERED, flag1), 3);
        }

    }

    private void play(World world, BlockPosition blockposition, IBlockData data) { // CraftBukkit
        if (world.getType(blockposition.up()).isAir()) {
            // CraftBukkit start
            org.bukkit.event.block.NotePlayEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callNotePlayEvent(world, blockposition, data.get(BlockNote.INSTRUMENT), data.get(BlockNote.NOTE));
            if (!event.isCancelled()) {
                world.playBlockAction(blockposition, this, 0, 0);
            }
            // CraftBukkit end
        }

    }

    @Override
    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        if (world.isClientSide) {
            return true;
        } else {
            iblockdata = (IBlockData) iblockdata.a((IBlockState) BlockNote.NOTE);
            world.setTypeAndData(blockposition, iblockdata, 3);
            this.play(world, blockposition, iblockdata); // CraftBukkit
            entityhuman.a(StatisticList.TUNE_NOTEBLOCK);
            return true;
        }
    }

    @Override
    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            this.play(world, blockposition, iblockdata); // CraftBukkit
            entityhuman.a(StatisticList.PLAY_NOTEBLOCK);
        }
    }

    @Override
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        int k = (Integer) iblockdata.get(BlockNote.NOTE);
        float f = (float) Math.pow(2.0D, (double) (k - 12) / 12.0D);

        world.playSound((EntityHuman) null, blockposition, ((BlockPropertyInstrument) iblockdata.get(BlockNote.INSTRUMENT)).a(), SoundCategory.RECORDS, 3.0F, f);
        world.addParticle(Particles.NOTE, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.2D, (double) blockposition.getZ() + 0.5D, (double) k / 24.0D, 0.0D, 0.0D);
        return true;
    }

    @Override
    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockNote.INSTRUMENT, BlockNote.POWERED, BlockNote.NOTE);
    }
}
