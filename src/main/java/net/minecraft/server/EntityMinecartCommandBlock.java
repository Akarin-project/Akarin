package net.minecraft.server;

public class EntityMinecartCommandBlock extends EntityMinecartAbstract {

    public static final DataWatcherObject<String> COMMAND = DataWatcher.a(EntityMinecartCommandBlock.class, DataWatcherRegistry.d);
    private static final DataWatcherObject<IChatBaseComponent> b = DataWatcher.a(EntityMinecartCommandBlock.class, DataWatcherRegistry.e);
    private final CommandBlockListenerAbstract c = new EntityMinecartCommandBlock.a();
    private int d;

    public EntityMinecartCommandBlock(World world) {
        super(EntityTypes.COMMAND_BLOCK_MINECART, world);
    }

    public EntityMinecartCommandBlock(World world, double d0, double d1, double d2) {
        super(EntityTypes.COMMAND_BLOCK_MINECART, world, d0, d1, d2);
    }

    protected void x_() {
        super.x_();
        this.getDataWatcher().register(EntityMinecartCommandBlock.COMMAND, "");
        this.getDataWatcher().register(EntityMinecartCommandBlock.b, new ChatComponentText(""));
    }

    protected void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        this.c.b(nbttagcompound);
        this.getDataWatcher().set(EntityMinecartCommandBlock.COMMAND, this.getCommandBlock().getCommand());
        this.getDataWatcher().set(EntityMinecartCommandBlock.b, this.getCommandBlock().j());
    }

    protected void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        this.c.a(nbttagcompound);
    }

    public EntityMinecartAbstract.EnumMinecartType v() {
        return EntityMinecartAbstract.EnumMinecartType.COMMAND_BLOCK;
    }

    public IBlockData z() {
        return Blocks.COMMAND_BLOCK.getBlockData();
    }

    public CommandBlockListenerAbstract getCommandBlock() {
        return this.c;
    }

    public void a(int i, int j, int k, boolean flag) {
        if (flag && this.ticksLived - this.d >= 4) {
            this.getCommandBlock().a(this.world);
            this.d = this.ticksLived;
        }

    }

    public boolean b(EntityHuman entityhuman, EnumHand enumhand) {
        this.c.a(entityhuman);
        return true;
    }

    public void a(DataWatcherObject<?> datawatcherobject) {
        super.a(datawatcherobject);
        if (EntityMinecartCommandBlock.b.equals(datawatcherobject)) {
            try {
                this.c.c((IChatBaseComponent) this.getDataWatcher().get(EntityMinecartCommandBlock.b));
            } catch (Throwable throwable) {
                ;
            }
        } else if (EntityMinecartCommandBlock.COMMAND.equals(datawatcherobject)) {
            this.c.setCommand((String) this.getDataWatcher().get(EntityMinecartCommandBlock.COMMAND));
        }

    }

    public boolean bM() {
        return true;
    }

    public class a extends CommandBlockListenerAbstract {

        public a() {}

        public WorldServer d() {
            return (WorldServer) EntityMinecartCommandBlock.this.world;
        }

        public void e() {
            EntityMinecartCommandBlock.this.getDataWatcher().set(EntityMinecartCommandBlock.COMMAND, this.getCommand());
            EntityMinecartCommandBlock.this.getDataWatcher().set(EntityMinecartCommandBlock.b, this.j());
        }

        public CommandListenerWrapper getWrapper() {
            return new CommandListenerWrapper(this, new Vec3D(EntityMinecartCommandBlock.this.locX, EntityMinecartCommandBlock.this.locY, EntityMinecartCommandBlock.this.locZ), EntityMinecartCommandBlock.this.aO(), this.d(), 2, this.getName().getString(), EntityMinecartCommandBlock.this.getScoreboardDisplayName(), this.d().getMinecraftServer(), EntityMinecartCommandBlock.this);
        }

        // CraftBukkit start
        @Override
        public org.bukkit.command.CommandSender getBukkitSender(CommandListenerWrapper wrapper) {
            return (org.bukkit.craftbukkit.entity.CraftMinecartCommand) EntityMinecartCommandBlock.this.getBukkitEntity();
        }
        // CraftBukkit end
    }
}
