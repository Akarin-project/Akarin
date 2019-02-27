package net.minecraft.server;

public class BlockNote extends Block {

    public static final BlockStateEnum<BlockPropertyInstrument> INSTRUMENT = BlockProperties.as;
    public static final BlockStateBoolean POWERED = BlockProperties.t;
    public static final BlockStateInteger NOTE = BlockProperties.aj;

    public BlockNote(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockNote.INSTRUMENT, BlockPropertyInstrument.HARP)).set(BlockNote.NOTE, 0)).set(BlockNote.POWERED, false));
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockNote.INSTRUMENT, BlockPropertyInstrument.a(blockactioncontext.getWorld().getType(blockactioncontext.getClickPosition().down())));
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        return enumdirection == EnumDirection.DOWN ? (IBlockData) iblockdata.set(BlockNote.INSTRUMENT, BlockPropertyInstrument.a(iblockdata1)) : super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        boolean flag = world.isBlockIndirectlyPowered(blockposition);

        if (flag != (Boolean) iblockdata.get(BlockNote.POWERED)) {
            if (flag) {
                this.play(world, blockposition, iblockdata); // CraftBukkit
            }

            world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockNote.POWERED, flag), 3);
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

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
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

    public void attack(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        if (!world.isClientSide) {
            this.play(world, blockposition, iblockdata); // CraftBukkit
            entityhuman.a(StatisticList.PLAY_NOTEBLOCK);
        }
    }

    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, int i, int j) {
        int k = (Integer) iblockdata.get(BlockNote.NOTE);
        float f = (float) Math.pow(2.0D, (double) (k - 12) / 12.0D);

        world.a((EntityHuman) null, blockposition, ((BlockPropertyInstrument) iblockdata.get(BlockNote.INSTRUMENT)).a(), SoundCategory.RECORDS, 3.0F, f);
        world.addParticle(Particles.I, (double) blockposition.getX() + 0.5D, (double) blockposition.getY() + 1.2D, (double) blockposition.getZ() + 0.5D, (double) k / 24.0D, 0.0D, 0.0D);
        return true;
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockNote.INSTRUMENT, BlockNote.POWERED, BlockNote.NOTE);
    }
}
