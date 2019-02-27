package net.minecraft.server;

import java.util.Random;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bukkit.event.block.BlockRedstoneEvent; // CraftBukkit

public class BlockCommand extends BlockTileEntity {

    private static final Logger c = LogManager.getLogger();
    public static final BlockStateDirection a = BlockDirectional.FACING;
    public static final BlockStateBoolean b = BlockProperties.b;

    public BlockCommand(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCommand.a, EnumDirection.NORTH)).set(BlockCommand.b, false));
    }

    public TileEntity a(IBlockAccess iblockaccess) {
        TileEntityCommand tileentitycommand = new TileEntityCommand();

        tileentitycommand.b(this == Blocks.CHAIN_COMMAND_BLOCK);
        return tileentitycommand;
    }

    public void doPhysics(IBlockData iblockdata, World world, BlockPosition blockposition, Block block, BlockPosition blockposition1) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
                boolean flag = world.isBlockIndirectlyPowered(blockposition);
                boolean flag1 = tileentitycommand.d();
                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ());
                int old = flag1 ? 15 : 0;
                int current = flag ? 15 : 0;

                BlockRedstoneEvent eventRedstone = new BlockRedstoneEvent(bukkitBlock, old, current);
                world.getServer().getPluginManager().callEvent(eventRedstone);
                flag = eventRedstone.getNewCurrent() > 0;
                // CraftBukkit end

                tileentitycommand.a(flag);
                if (!flag1 && !tileentitycommand.e() && tileentitycommand.j() != TileEntityCommand.Type.SEQUENCE) {
                    if (flag) {
                        tileentitycommand.h();
                        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
                    }

                }
            }
        }
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Random random) {
        if (!world.isClientSide) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof TileEntityCommand) {
                TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
                CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();
                boolean flag = !UtilColor.b(commandblocklistenerabstract.getCommand());
                TileEntityCommand.Type tileentitycommand_type = tileentitycommand.j();
                boolean flag1 = tileentitycommand.f();

                if (tileentitycommand_type == TileEntityCommand.Type.AUTO) {
                    tileentitycommand.h();
                    if (flag1) {
                        this.a(iblockdata, world, blockposition, commandblocklistenerabstract, flag);
                    } else if (tileentitycommand.k()) {
                        commandblocklistenerabstract.a(0);
                    }

                    if (tileentitycommand.d() || tileentitycommand.e()) {
                        world.getBlockTickList().a(blockposition, this, this.a((IWorldReader) world));
                    }
                } else if (tileentitycommand_type == TileEntityCommand.Type.REDSTONE) {
                    if (flag1) {
                        this.a(iblockdata, world, blockposition, commandblocklistenerabstract, flag);
                    } else if (tileentitycommand.k()) {
                        commandblocklistenerabstract.a(0);
                    }
                }

                world.updateAdjacentComparators(blockposition, this);
            }

        }
    }

    private void a(IBlockData iblockdata, World world, BlockPosition blockposition, CommandBlockListenerAbstract commandblocklistenerabstract, boolean flag) {
        if (flag) {
            commandblocklistenerabstract.a(world);
        } else {
            commandblocklistenerabstract.a(0);
        }

        a(world, blockposition, (EnumDirection) iblockdata.get(BlockCommand.a));
    }

    public int a(IWorldReader iworldreader) {
        return 1;
    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCommand && entityhuman.isCreativeAndOp()) {
            entityhuman.a((TileEntityCommand) tileentity);
            return true;
        } else {
            return false;
        }
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        return tileentity instanceof TileEntityCommand ? ((TileEntityCommand) tileentity).getCommandBlock().i() : 0;
    }

    public void postPlace(World world, BlockPosition blockposition, IBlockData iblockdata, EntityLiving entityliving, ItemStack itemstack) {
        TileEntity tileentity = world.getTileEntity(blockposition);

        if (tileentity instanceof TileEntityCommand) {
            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;
            CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();

            if (itemstack.hasName()) {
                commandblocklistenerabstract.setName(itemstack.getName());
            }

            if (!world.isClientSide) {
                if (itemstack.b("BlockEntityTag") == null) {
                    commandblocklistenerabstract.a(world.getGameRules().getBoolean("sendCommandFeedback"));
                    tileentitycommand.b(this == Blocks.CHAIN_COMMAND_BLOCK);
                }

                if (tileentitycommand.j() == TileEntityCommand.Type.SEQUENCE) {
                    boolean flag = world.isBlockIndirectlyPowered(blockposition);

                    tileentitycommand.a(flag);
                }
            }

        }
    }

    public int a(IBlockData iblockdata, Random random) {
        return 0;
    }

    public EnumRenderType c(IBlockData iblockdata) {
        return EnumRenderType.MODEL;
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockRotation enumblockrotation) {
        return (IBlockData) iblockdata.set(BlockCommand.a, enumblockrotation.a((EnumDirection) iblockdata.get(BlockCommand.a)));
    }

    public IBlockData a(IBlockData iblockdata, EnumBlockMirror enumblockmirror) {
        return iblockdata.a(enumblockmirror.a((EnumDirection) iblockdata.get(BlockCommand.a)));
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCommand.a, BlockCommand.b);
    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockCommand.a, blockactioncontext.d().opposite());
    }

    private static void a(World world, BlockPosition blockposition, EnumDirection enumdirection) {
        BlockPosition.MutableBlockPosition blockposition_mutableblockposition = new BlockPosition.MutableBlockPosition(blockposition);
        GameRules gamerules = world.getGameRules();

        IBlockData iblockdata;
        int i;

        for (i = gamerules.c("maxCommandChainLength"); i-- > 0; enumdirection = (EnumDirection) iblockdata.get(BlockCommand.a)) {
            blockposition_mutableblockposition.c(enumdirection);
            iblockdata = world.getType(blockposition_mutableblockposition);
            Block block = iblockdata.getBlock();

            if (block != Blocks.CHAIN_COMMAND_BLOCK) {
                break;
            }

            TileEntity tileentity = world.getTileEntity(blockposition_mutableblockposition);

            if (!(tileentity instanceof TileEntityCommand)) {
                break;
            }

            TileEntityCommand tileentitycommand = (TileEntityCommand) tileentity;

            if (tileentitycommand.j() != TileEntityCommand.Type.SEQUENCE) {
                break;
            }

            if (tileentitycommand.d() || tileentitycommand.e()) {
                CommandBlockListenerAbstract commandblocklistenerabstract = tileentitycommand.getCommandBlock();

                if (tileentitycommand.h()) {
                    if (!commandblocklistenerabstract.a(world)) {
                        break;
                    }

                    world.updateAdjacentComparators(blockposition_mutableblockposition, block);
                } else if (tileentitycommand.k()) {
                    commandblocklistenerabstract.a(0);
                }
            }
        }

        if (i <= 0) {
            int j = Math.max(gamerules.c("maxCommandChainLength"), 0);

            BlockCommand.c.warn("Command Block chain tried to execute more than {} steps!", j);
        }

    }
}
