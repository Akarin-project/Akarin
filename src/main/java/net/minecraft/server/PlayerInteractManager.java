package net.minecraft.server;

public class PlayerInteractManager {

    public World world;
    public EntityPlayer player;
    private EnumGamemode gamemode;
    private boolean d;
    private int lastDigTick;
    private BlockPosition f;
    private int currentTick;
    private boolean h;
    private BlockPosition i;
    private int j;
    private int k;

    public PlayerInteractManager(World world) {
        this.gamemode = EnumGamemode.NOT_SET;
        this.f = BlockPosition.ZERO;
        this.i = BlockPosition.ZERO;
        this.k = -1;
        this.world = world;
    }

    public void setGameMode(EnumGamemode enumgamemode) {
        this.gamemode = enumgamemode;
        enumgamemode.a(this.player.abilities);
        this.player.updateAbilities();
        this.player.server.getPlayerList().sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, new EntityPlayer[] { this.player}));
        this.world.everyoneSleeping();
    }

    public EnumGamemode getGameMode() {
        return this.gamemode;
    }

    public boolean c() {
        return this.gamemode.f();
    }

    public boolean isCreative() {
        return this.gamemode.isCreative();
    }

    public void b(EnumGamemode enumgamemode) {
        if (this.gamemode == EnumGamemode.NOT_SET) {
            this.gamemode = enumgamemode;
        }

        this.setGameMode(this.gamemode);
    }

    public void a() {
        ++this.currentTick;
        float f;
        int i;

        if (this.h) {
            int j = this.currentTick - this.j;
            IBlockData iblockdata = this.world.getType(this.i);

            if (iblockdata.isAir()) {
                this.h = false;
            } else {
                f = iblockdata.getDamage(this.player, this.player.world, this.i) * (float) (j + 1);
                i = (int) (f * 10.0F);
                if (i != this.k) {
                    this.world.c(this.player.getId(), this.i, i);
                    this.k = i;
                }

                if (f >= 1.0F) {
                    this.h = false;
                    this.breakBlock(this.i);
                }
            }
        } else if (this.d) {
            IBlockData iblockdata1 = this.world.getType(this.f);

            if (iblockdata1.isAir()) {
                this.world.c(this.player.getId(), this.f, -1);
                this.k = -1;
                this.d = false;
            } else {
                int k = this.currentTick - this.lastDigTick;

                f = iblockdata1.getDamage(this.player, this.player.world, this.i) * (float) (k + 1);
                i = (int) (f * 10.0F);
                if (i != this.k) {
                    this.world.c(this.player.getId(), this.f, i);
                    this.k = i;
                }
            }
        }

    }

    public void a(BlockPosition blockposition, EnumDirection enumdirection) {
        if (this.isCreative()) {
            if (!this.world.douseFire((EntityHuman) null, blockposition, enumdirection)) {
                this.breakBlock(blockposition);
            }

        } else {
            if (this.gamemode.d()) {
                if (this.gamemode == EnumGamemode.SPECTATOR) {
                    return;
                }

                if (!this.player.dy()) {
                    ItemStack itemstack = this.player.getItemInMainHand();

                    if (itemstack.isEmpty()) {
                        return;
                    }

                    ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(this.world, blockposition, false);

                    if (!itemstack.a(this.world.F(), shapedetectorblock)) {
                        return;
                    }
                }
            }

            this.world.douseFire((EntityHuman) null, blockposition, enumdirection);
            this.lastDigTick = this.currentTick;
            float f = 1.0F;
            IBlockData iblockdata = this.world.getType(blockposition);

            if (!iblockdata.isAir()) {
                iblockdata.attack(this.world, blockposition, this.player);
                f = iblockdata.getDamage(this.player, this.player.world, blockposition);
            }

            if (!iblockdata.isAir() && f >= 1.0F) {
                this.breakBlock(blockposition);
            } else {
                this.d = true;
                this.f = blockposition;
                int i = (int) (f * 10.0F);

                this.world.c(this.player.getId(), blockposition, i);
                this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                this.k = i;
            }

        }
    }

    public void a(BlockPosition blockposition) {
        if (blockposition.equals(this.f)) {
            int i = this.currentTick - this.lastDigTick;
            IBlockData iblockdata = this.world.getType(blockposition);

            if (!iblockdata.isAir()) {
                float f = iblockdata.getDamage(this.player, this.player.world, blockposition) * (float) (i + 1);

                if (f >= 0.7F) {
                    this.d = false;
                    this.world.c(this.player.getId(), blockposition, -1);
                    this.breakBlock(blockposition);
                } else if (!this.h) {
                    this.d = false;
                    this.h = true;
                    this.i = blockposition;
                    this.j = this.lastDigTick;
                }
            }
        }

    }

    public void e() {
        this.d = false;
        this.world.c(this.player.getId(), this.f, -1);
    }

    private boolean c(BlockPosition blockposition) {
        IBlockData iblockdata = this.world.getType(blockposition);

        iblockdata.getBlock().a(this.world, blockposition, iblockdata, (EntityHuman) this.player);
        boolean flag = this.world.setAir(blockposition);

        if (flag) {
            iblockdata.getBlock().postBreak(this.world, blockposition, iblockdata);
        }

        return flag;
    }

    public boolean breakBlock(BlockPosition blockposition) {
        IBlockData iblockdata = this.world.getType(blockposition);

        if (!this.player.getItemInMainHand().getItem().a(iblockdata, this.world, blockposition, (EntityHuman) this.player)) {
            return false;
        } else {
            TileEntity tileentity = this.world.getTileEntity(blockposition);
            Block block = iblockdata.getBlock();

            if ((block instanceof BlockCommand || block instanceof BlockStructure) && !this.player.isCreativeAndOp()) {
                this.world.notify(blockposition, iblockdata, iblockdata, 3);
                return false;
            } else {
                if (this.gamemode.d()) {
                    if (this.gamemode == EnumGamemode.SPECTATOR) {
                        return false;
                    }

                    if (!this.player.dy()) {
                        ItemStack itemstack = this.player.getItemInMainHand();

                        if (itemstack.isEmpty()) {
                            return false;
                        }

                        ShapeDetectorBlock shapedetectorblock = new ShapeDetectorBlock(this.world, blockposition, false);

                        if (!itemstack.a(this.world.F(), shapedetectorblock)) {
                            return false;
                        }
                    }
                }

                boolean flag = this.c(blockposition);

                if (!this.isCreative()) {
                    ItemStack itemstack1 = this.player.getItemInMainHand();
                    boolean flag1 = this.player.hasBlock(iblockdata);

                    itemstack1.a(this.world, iblockdata, blockposition, this.player);
                    if (flag && flag1) {
                        ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack();

                        iblockdata.getBlock().a(this.world, this.player, blockposition, iblockdata, tileentity, itemstack2);
                    }
                }

                return flag;
            }
        }
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand) {
        if (this.gamemode == EnumGamemode.SPECTATOR) {
            return EnumInteractionResult.PASS;
        } else if (entityhuman.getCooldownTracker().a(itemstack.getItem())) {
            return EnumInteractionResult.PASS;
        } else {
            int i = itemstack.getCount();
            int j = itemstack.getDamage();
            InteractionResultWrapper<ItemStack> interactionresultwrapper = itemstack.a(world, entityhuman, enumhand);
            ItemStack itemstack1 = (ItemStack) interactionresultwrapper.b();

            if (itemstack1 == itemstack && itemstack1.getCount() == i && itemstack1.k() <= 0 && itemstack1.getDamage() == j) {
                return interactionresultwrapper.a();
            } else if (interactionresultwrapper.a() == EnumInteractionResult.FAIL && itemstack1.k() > 0 && !entityhuman.isHandRaised()) {
                return interactionresultwrapper.a();
            } else {
                entityhuman.a(enumhand, itemstack1);
                if (this.isCreative()) {
                    itemstack1.setCount(i);
                    if (itemstack1.e()) {
                        itemstack1.setDamage(j);
                    }
                }

                if (itemstack1.isEmpty()) {
                    entityhuman.a(enumhand, ItemStack.a);
                }

                if (!entityhuman.isHandRaised()) {
                    ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                }

                return interactionresultwrapper.a();
            }
        }
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);

        if (this.gamemode == EnumGamemode.SPECTATOR) {
            TileEntity tileentity = world.getTileEntity(blockposition);

            if (tileentity instanceof ITileInventory) {
                Block block = iblockdata.getBlock();
                ITileInventory itileinventory = (ITileInventory) tileentity;

                if (itileinventory instanceof TileEntityChest && block instanceof BlockChest) {
                    itileinventory = ((BlockChest) block).getInventory(iblockdata, world, blockposition, false);
                }

                if (itileinventory != null) {
                    entityhuman.openContainer(itileinventory);
                    return EnumInteractionResult.SUCCESS;
                }
            } else if (tileentity instanceof IInventory) {
                entityhuman.openContainer((IInventory) tileentity);
                return EnumInteractionResult.SUCCESS;
            }

            return EnumInteractionResult.PASS;
        } else {
            boolean flag = !entityhuman.getItemInMainHand().isEmpty() || !entityhuman.getItemInOffHand().isEmpty();
            boolean flag1 = entityhuman.isSneaking() && flag;

            if (!flag1 && iblockdata.interact(world, blockposition, entityhuman, enumhand, enumdirection, f, f1, f2)) {
                return EnumInteractionResult.SUCCESS;
            } else if (!itemstack.isEmpty() && !entityhuman.getCooldownTracker().a(itemstack.getItem())) {
                ItemActionContext itemactioncontext = new ItemActionContext(entityhuman, entityhuman.b(enumhand), blockposition, enumdirection, f, f1, f2);

                if (this.isCreative()) {
                    int i = itemstack.getCount();
                    EnumInteractionResult enuminteractionresult = itemstack.placeItem(itemactioncontext);

                    itemstack.setCount(i);
                    return enuminteractionresult;
                } else {
                    return itemstack.placeItem(itemactioncontext);
                }
            } else {
                return EnumInteractionResult.PASS;
            }
        }
    }

    public void a(WorldServer worldserver) {
        this.world = worldserver;
    }
}
