package net.minecraft.server;

// CraftBukkit start
import org.bukkit.craftbukkit.block.CraftBlockState;
import org.bukkit.event.block.BlockFormEvent;
// CraftBukkit end

public class BlockConcretePowder extends BlockFalling {

    private final IBlockData a;

    public BlockConcretePowder(Block block, Block.Info block_info) {
        super(block_info);
        this.a = block.getBlockData();
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, IBlockData iblockdata1) {
        if (x(iblockdata1)) {
            org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockFormEvent(world, blockposition, this.a, 3); // CraftBukkit
        }

    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        World world = blockactioncontext.getWorld();
        BlockPosition blockposition = blockactioncontext.getClickPosition();

        // CraftBukkit start
        if (!x(world.getType(blockposition)) && !a((IBlockAccess) world, blockposition)) {
            return super.getPlacedState(blockactioncontext);
        }

        // TODO: An event factory call for methods like this
        CraftBlockState blockState = CraftBlockState.getBlockState(world, blockposition);
        blockState.setData(this.a);

        BlockFormEvent event = new BlockFormEvent(blockState.getBlock(), blockState);
        world.getMinecraftServer().server.getPluginManager().callEvent(event);

        if (!event.isCancelled()) {
            return blockState.getHandle();
        }

        return super.getPlacedState(blockactioncontext);
        // CraftBukkit end
    }

    private static boolean a(IBlockAccess iblockaccess, BlockPosition blockposition) {
        boolean flag = false;
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);
        EnumDirection[] aenumdirection = EnumDirection.values();
        int i = aenumdirection.length;

        for (int j = 0; j < i; ++j) {
            EnumDirection enumdirection = aenumdirection[j];
            IBlockData iblockdata = iblockaccess.getType(blockposition_mutableblockposition);

            if (enumdirection != EnumDirection.DOWN || x(iblockdata)) {
                blockposition_mutableblockposition.g(blockposition).c(enumdirection);
                iblockdata = iblockaccess.getType(blockposition_mutableblockposition);
                if (x(iblockdata) && !Block.a(iblockdata.getCollisionShape(iblockaccess, blockposition), enumdirection.opposite())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    private static boolean x(IBlockData iblockdata) {
        return iblockdata.s().a(TagsFluid.WATER);
    }

    public IBlockData updateState(IBlockData iblockdata, EnumDirection enumdirection, IBlockData iblockdata1, GeneratorAccess generatoraccess, BlockPosition blockposition, BlockPosition blockposition1) {
        // CraftBukkit start
        if (a((IBlockAccess) generatoraccess, blockposition)) {
            CraftBlockState blockState = CraftBlockState.getBlockState(generatoraccess, blockposition);
            blockState.setData(this.a);

            BlockFormEvent event = new BlockFormEvent(blockState.getBlock(), blockState);
            generatoraccess.getMinecraftWorld().getMinecraftServer().server.getPluginManager().callEvent(event);

            if (!event.isCancelled()) {
                return blockState.getHandle();
            }
        }

        return super.updateState(iblockdata, enumdirection, iblockdata1, generatoraccess, blockposition, blockposition1);
        // CraftBukkit end
    }
}
