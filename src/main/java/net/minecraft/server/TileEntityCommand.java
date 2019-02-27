package net.minecraft.server;

import javax.annotation.Nullable;

public class TileEntityCommand extends TileEntity {

    private boolean a;
    private boolean e;
    private boolean f;
    private boolean g;
    private final CommandBlockListenerAbstract h = new CommandBlockListenerAbstract() {
        public void setCommand(String s) {
            super.setCommand(s);
            TileEntityCommand.this.update();
        }

        public WorldServer d() {
            return (WorldServer) TileEntityCommand.this.world;
        }

        public void e() {
            IBlockData iblockdata = TileEntityCommand.this.world.getType(TileEntityCommand.this.position);

            this.d().notify(TileEntityCommand.this.position, iblockdata, iblockdata, 3);
        }

        public CommandListenerWrapper getWrapper() {
            return new CommandListenerWrapper(this, new Vec3D((double) TileEntityCommand.this.position.getX() + 0.5D, (double) TileEntityCommand.this.position.getY() + 0.5D, (double) TileEntityCommand.this.position.getZ() + 0.5D), Vec2F.a, this.d(), 2, this.getName().getString(), this.getName(), this.d().getMinecraftServer(), (Entity) null);
        }
    };

    public TileEntityCommand() {
        super(TileEntityTypes.COMMAND_BLOCK);
    }

    public NBTTagCompound save(NBTTagCompound nbttagcompound) {
        super.save(nbttagcompound);
        this.h.a(nbttagcompound);
        nbttagcompound.setBoolean("powered", this.d());
        nbttagcompound.setBoolean("conditionMet", this.f());
        nbttagcompound.setBoolean("auto", this.e());
        return nbttagcompound;
    }

    public void load(NBTTagCompound nbttagcompound) {
        super.load(nbttagcompound);
        this.h.b(nbttagcompound);
        this.a = nbttagcompound.getBoolean("powered");
        this.f = nbttagcompound.getBoolean("conditionMet");
        this.b(nbttagcompound.getBoolean("auto"));
    }

    @Nullable
    public PacketPlayOutTileEntityData getUpdatePacket() {
        if (this.i()) {
            this.c(false);
            NBTTagCompound nbttagcompound = this.save(new NBTTagCompound());

            return new PacketPlayOutTileEntityData(this.position, 2, nbttagcompound);
        } else {
            return null;
        }
    }

    public boolean isFilteredNBT() {
        return true;
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.h;
    }

    public void a(boolean flag) {
        this.a = flag;
    }

    public boolean d() {
        return this.a;
    }

    public boolean e() {
        return this.e;
    }

    public void b(boolean flag) {
        boolean flag1 = this.e;

        this.e = flag;
        if (!flag1 && flag && !this.a && this.world != null && this.j() != TileEntityCommand.Type.SEQUENCE) {
            Block block = this.getBlock().getBlock();

            if (block instanceof BlockCommand) {
                this.h();
                this.world.getBlockTickList().a(this.position, block, block.a((IWorldReader) this.world));
            }
        }

    }

    public boolean f() {
        return this.f;
    }

    public boolean h() {
        this.f = true;
        if (this.k()) {
            BlockPosition blockposition = this.position.shift(((EnumDirection) this.world.getType(this.position).get(BlockCommand.a)).opposite());

            if (this.world.getType(blockposition).getBlock() instanceof BlockCommand) {
                TileEntity tileentity = this.world.getTileEntity(blockposition);

                this.f = tileentity instanceof TileEntityCommand && ((TileEntityCommand) tileentity).getCommandBlock().i() > 0;
            } else {
                this.f = false;
            }
        }

        return this.f;
    }

    public boolean i() {
        return this.g;
    }

    public void c(boolean flag) {
        this.g = flag;
    }

    public TileEntityCommand.Type j() {
        Block block = this.getBlock().getBlock();

        return block == Blocks.COMMAND_BLOCK ? TileEntityCommand.Type.REDSTONE : (block == Blocks.REPEATING_COMMAND_BLOCK ? TileEntityCommand.Type.AUTO : (block == Blocks.CHAIN_COMMAND_BLOCK ? TileEntityCommand.Type.SEQUENCE : TileEntityCommand.Type.REDSTONE));
    }

    public boolean k() {
        IBlockData iblockdata = this.world.getType(this.getPosition());

        return iblockdata.getBlock() instanceof BlockCommand ? (Boolean) iblockdata.get(BlockCommand.b) : false;
    }

    public void z() {
        this.invalidateBlockCache();
        super.z();
    }

    public static enum Type {

        SEQUENCE, AUTO, REDSTONE;

        private Type() {}
    }
}
