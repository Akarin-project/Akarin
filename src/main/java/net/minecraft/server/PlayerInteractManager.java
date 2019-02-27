package net.minecraft.server;

// CraftBukkit start
import java.util.ArrayList;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
// CraftBukkit end

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
        this.player.server.getPlayerList().sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, new EntityPlayer[] { this.player}), this.player); // CraftBukkit
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
        this.currentTick = MinecraftServer.currentTick; // CraftBukkit;
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
        // CraftBukkit start
        PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, blockposition, enumdirection, this.player.inventory.getItemInHand(), EnumHand.MAIN_HAND);
        if (event.isCancelled()) {
            // Let the client know the block still exists
            ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
            // Update any tile entity data for this block
            TileEntity tileentity = this.world.getTileEntity(blockposition);
            if (tileentity != null) {
                this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
            }
            return;
        }
        // CraftBukkit end
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

            // this.world.douseFire((EntityHuman) null, blockposition, enumdirection); // CraftBukkit - Moved down
            this.lastDigTick = this.currentTick;
            float f = 1.0F;
            IBlockData iblockdata = this.world.getType(blockposition);

            // CraftBukkit start - Swings at air do *NOT* exist.
            if (event.useInteractedBlock() == Event.Result.DENY) {
                // If we denied a door from opening, we need to send a correcting update to the client, as it already opened the door.
                IBlockData data = this.world.getType(blockposition);
                if (data.getBlock() instanceof BlockDoor) {
                    // For some reason *BOTH* the bottom/top part have to be marked updated.
                    boolean bottom = data.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
                    ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                    ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, bottom ? blockposition.up() : blockposition.down()));
                } else if (data.getBlock() instanceof BlockTrapdoor) {
                    ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                }
            } else if (!iblockdata.isAir()) {
                iblockdata.attack(this.world, blockposition, this.player);
                f = iblockdata.getDamage(this.player, this.player.world, blockposition);
                // Allow fire punching to be blocked
                this.world.douseFire((EntityHuman) null, blockposition, enumdirection);
            }

            if (event.useItemInHand() == Event.Result.DENY) {
                // If we 'insta destroyed' then the client needs to be informed.
                if (f > 1.0f) {
                    ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                }
                return;
            }
            org.bukkit.event.block.BlockDamageEvent blockEvent = CraftEventFactory.callBlockDamageEvent(this.player, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.player.inventory.getItemInHand(), f >= 1.0f);

            if (blockEvent.isCancelled()) {
                // Let the client know the block still exists
                ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                return;
            }

            if (blockEvent.getInstaBreak()) {
                f = 2.0f;
            }
            // CraftBukkit end

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
            this.currentTick = MinecraftServer.currentTick; // CraftBukkit
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
        // CraftBukkit start - Force block reset to client
        } else {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
            // CraftBukkit end
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
        // CraftBukkit start - fire BlockBreakEvent
        org.bukkit.block.Block bblock = CraftBlock.at(world, blockposition);
        BlockBreakEvent event = null;

        if (this.player instanceof EntityPlayer) {
            // Sword + Creative mode pre-cancel
            boolean isSwordNoBreak = !this.player.getItemInMainHand().getItem().a(iblockdata, this.world, blockposition, (EntityHuman) this.player);

            // Tell client the block is gone immediately then process events
            // Don't tell the client if its a creative sword break because its not broken!
            if (world.getTileEntity(blockposition) == null && !isSwordNoBreak) {
                PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(this.world, blockposition);
                packet.block = Blocks.AIR.getBlockData();
                ((EntityPlayer) this.player).playerConnection.sendPacket(packet);
            }

            event = new BlockBreakEvent(bblock, this.player.getBukkitEntity());

            // Sword + Creative mode pre-cancel
            event.setCancelled(isSwordNoBreak);

            // Calculate default block experience
            IBlockData nmsData = this.world.getType(blockposition);
            Block nmsBlock = nmsData.getBlock();

            ItemStack itemstack = this.player.getEquipment(EnumItemSlot.MAINHAND);

            if (nmsBlock != null && !event.isCancelled() && !this.isCreative() && this.player.hasBlock(nmsBlock.getBlockData())) {
                // Copied from block.a(World world, EntityHuman entityhuman, BlockPosition blockposition, IBlockData iblockdata, @Nullable TileEntity tileentity, ItemStack itemstack)
                // PAIL: checkme each update
                if (!(nmsBlock.X_() && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0)) {
                    int bonusLevel = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, itemstack);

                    event.setExpToDrop(nmsBlock.getExpDrop(nmsData, this.world, blockposition, bonusLevel));
                }
            }

            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                if (isSwordNoBreak) {
                    return false;
                }
                // Let the client know the block still exists
                ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));

                // Brute force all possible updates
                for (EnumDirection dir : EnumDirection.values()) {
                    ((EntityPlayer) this.player).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, blockposition.shift(dir)));
                }

                // Update any tile entity data for this block
                TileEntity tileentity = this.world.getTileEntity(blockposition);
                if (tileentity != null) {
                    this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
                }
                return false;
            }
        }
        // CraftBukkit end

        if (false && !this.player.getItemInMainHand().getItem().a(iblockdata, this.world, blockposition, (EntityHuman) this.player)) { // CraftBukkit - false
            return false;
        } else {
            iblockdata = this.world.getType(blockposition); // CraftBukkit - update state from plugins
            if (iblockdata.isAir()) return false; // CraftBukkit - A plugin set block to air without cancelling
            TileEntity tileentity = this.world.getTileEntity(blockposition);
            Block block = iblockdata.getBlock();

            // CraftBukkit start - Special case skulls, their item data comes from a tile entity (Also check if block should drop items)
            // And shulker boxes too for duplication on BlockPlaceEvent cancel reasons (Also check if block should drop items)
            if (((iblockdata.getBlock() instanceof BlockSkullAbstract && !this.isCreative()) || iblockdata.getBlock() instanceof BlockShulkerBox) && event.isDropItems()) {
                org.bukkit.block.BlockState state = bblock.getState();
                world.captureDrops = new ArrayList<>();

                iblockdata.getBlock().dropNaturally(iblockdata, world, blockposition, 1.0F, 0);
                boolean flag = this.c(blockposition);

                if (event.isDropItems()) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockDropItemEvent(bblock, state, this.player, world.captureDrops);
                }

                world.captureDrops = null;
                return flag;
            }
            // CraftBukkit end

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

                // CraftBukkit start
                org.bukkit.block.BlockState state = bblock.getState();
                world.captureDrops = new ArrayList<>();
                // CraftBukkit end
                boolean flag = this.c(blockposition);

                if (!this.isCreative()) {
                    ItemStack itemstack1 = this.player.getItemInMainHand();
                    boolean flag1 = this.player.hasBlock(iblockdata);

                    itemstack1.a(this.world, iblockdata, blockposition, this.player);
                    // CraftBukkit start - Check if block should drop items
                    if (flag && flag1 && event.isDropItems()) {
                        ItemStack itemstack2 = itemstack1.isEmpty() ? ItemStack.a : itemstack1.cloneItemStack();

                        iblockdata.getBlock().a(this.world, this.player, blockposition, iblockdata, tileentity, itemstack2);
                    }
                    // CraftBukkit end
                }

                if (event.isDropItems()) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockDropItemEvent(bblock, state, this.player, world.captureDrops);
                }
                world.captureDrops = null;
                // CraftBukkit end

                // CraftBukkit start - Drop event experience
                if (flag && event != null) {
                    iblockdata.getBlock().dropExperience(this.world, blockposition, event.getExpToDrop());
                }
                // CraftBukkit end

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

    // CraftBukkit start - whole method
    public boolean interactResult = false;
    public boolean firedInteract = false;
    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand, BlockPosition blockposition, EnumDirection enumdirection, float f, float f1, float f2) {
        IBlockData iblockdata = world.getType(blockposition);
        EnumInteractionResult enuminteractionresult = EnumInteractionResult.PASS;
        if (iblockdata.isAir()) return enuminteractionresult;
        boolean cancelledBlock = false;

        if (this.gamemode == EnumGamemode.SPECTATOR) {
            TileEntity tileentity = world.getTileEntity(blockposition);
            cancelledBlock = !(tileentity instanceof ITileInventory || tileentity instanceof IInventory);
        }

        if (entityhuman.getCooldownTracker().a(itemstack.getItem())) {
            cancelledBlock = true;
        }

        PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(entityhuman, Action.RIGHT_CLICK_BLOCK, blockposition, enumdirection, itemstack, cancelledBlock, enumhand);
        firedInteract = true;
        interactResult = event.useItemInHand() == Event.Result.DENY;

        if (event.useInteractedBlock() == Event.Result.DENY) {
            // If we denied a door from opening, we need to send a correcting update to the client, as it already opened the door.
            if (iblockdata.getBlock() instanceof BlockDoor) {
                boolean bottom = iblockdata.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, bottom ? blockposition.up() : blockposition.down()));
            } else if (iblockdata.getBlock() instanceof BlockCake) {
                ((EntityPlayer) entityhuman).getBukkitEntity().sendHealthUpdate(); // SPIGOT-1341 - reset health for cake
            }
            ((EntityPlayer) entityhuman).getBukkitEntity().updateInventory(); // SPIGOT-2867
            enuminteractionresult = (event.useItemInHand() != Event.Result.ALLOW) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        } else if (this.gamemode == EnumGamemode.SPECTATOR) {
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

            if (!flag1) {
                enuminteractionresult = iblockdata.interact(world, blockposition, entityhuman, enumhand, enumdirection, f, f1, f2) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.FAIL;
            }

            if (!itemstack.isEmpty() && enuminteractionresult != EnumInteractionResult.SUCCESS && !interactResult) { // add !interactResult SPIGOT-764
                ItemActionContext itemactioncontext = new ItemActionContext(entityhuman, entityhuman.b(enumhand), blockposition, enumdirection, f, f1, f2);

                if (this.isCreative()) {
                    int i = itemstack.getCount();
                    enuminteractionresult = itemstack.placeItem(itemactioncontext, enumhand);

                    itemstack.setCount(i);
                    return enuminteractionresult;
                } else {
                    return itemstack.placeItem(itemactioncontext, enumhand);
                }
            }
        }
        return enuminteractionresult;
        // CraftBukkit end
    }

    public void a(WorldServer worldserver) {
        this.world = worldserver;
    }
}
