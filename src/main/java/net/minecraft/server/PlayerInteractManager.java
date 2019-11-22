package net.minecraft.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

    private static final Logger LOGGER = LogManager.getLogger();
    public WorldServer world;
    public EntityPlayer player;
    private EnumGamemode gamemode;
    private boolean e;
    private int lastDigTick;
    private BlockPosition g;
    private int currentTick;
    private boolean i;
    private BlockPosition j;
    private int k;
    private int l;

    public PlayerInteractManager(WorldServer worldserver) {
        this.gamemode = EnumGamemode.NOT_SET;
        this.g = BlockPosition.ZERO;
        this.j = BlockPosition.ZERO;
        this.l = -1;
        this.world = worldserver;
    }

    public void setGameMode(EnumGamemode enumgamemode) {
        this.gamemode = enumgamemode;
        enumgamemode.a(this.player.abilities);
        this.player.updateAbilities();
        this.player.server.getPlayerList().sendAll(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.UPDATE_GAME_MODE, new EntityPlayer[]{this.player}), this.player); // CraftBukkit
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
        IBlockData iblockdata;

        if (this.i) {
            iblockdata = this.world.getType(this.j);
            if (iblockdata.isAir()) {
                this.i = false;
            } else {
                float f = this.a(iblockdata, this.j);

                if (f >= 1.0F) {
                    this.i = false;
                    this.breakBlock(this.j);
                }
            }
        } else if (this.e) {
            iblockdata = this.world.getType(this.g);
            if (iblockdata.isAir()) {
                this.world.a(this.player.getId(), this.g, -1);
                this.l = -1;
                this.e = false;
            } else {
                this.a(iblockdata, this.g);
            }
        }

    }

    private float a(IBlockData iblockdata, BlockPosition blockposition) {
        int i = this.currentTick - this.k;
        float f = iblockdata.getDamage(this.player, this.player.world, blockposition) * (float) (i + 1);
        int j = (int) (f * 10.0F);

        if (j != this.l) {
            this.world.a(this.player.getId(), blockposition, j);
            this.l = j;
        }

        return f;
    }

    public void a(BlockPosition blockposition, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype, EnumDirection enumdirection, int i) {
        double d0 = this.player.locX - ((double) blockposition.getX() + 0.5D);
        double d1 = this.player.locY - ((double) blockposition.getY() + 0.5D) + 1.5D;
        double d2 = this.player.locZ - ((double) blockposition.getZ() + 0.5D);
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        if (d3 > 36.0D) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false));
        } else if (blockposition.getY() >= i) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false));
        } else {
            IBlockData iblockdata;

            if (packetplayinblockdig_enumplayerdigtype == PacketPlayInBlockDig.EnumPlayerDigType.START_DESTROY_BLOCK) {
                if (!this.world.a((EntityHuman) this.player, blockposition)) {
                    // CraftBukkit start - fire PlayerInteractEvent
                    CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, blockposition, enumdirection, this.player.inventory.getItemInHand(), EnumHand.MAIN_HAND);
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false));
                    // Update any tile entity data for this block
                    TileEntity tileentity = world.getTileEntity(blockposition);
                    if (tileentity != null) {
                        this.player.playerConnection.sendPacket(tileentity.getUpdatePacket());
                    }
                    // CraftBukkit end
                    return;
                }

                // CraftBukkit start
                PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(this.player, Action.LEFT_CLICK_BLOCK, blockposition, enumdirection, this.player.inventory.getItemInHand(), EnumHand.MAIN_HAND);
                if (event.isCancelled()) {
                    // Let the client know the block still exists
                    // Paper start - brute force neighbor blocks for any attached blocks
                    for (EnumDirection dir : EnumDirection.values()) {
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(world, blockposition.shift(dir)));
                    }
                    // Paper end
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
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
                        this.a(blockposition, packetplayinblockdig_enumplayerdigtype);
                    } else {
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true));
                    }

                    return;
                }

                if (this.player.a((World) this.world, blockposition, this.gamemode)) {
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, false));
                    return;
                }

                // this.world.douseFire((EntityHuman) null, blockposition, enumdirection); // CraftBukkit - Moved down
                this.lastDigTick = this.currentTick;
                float f = 1.0F;

                iblockdata = this.world.getType(blockposition);
                // CraftBukkit start - Swings at air do *NOT* exist.
                if (event.useInteractedBlock() == Event.Result.DENY) {
                    // If we denied a door from opening, we need to send a correcting update to the client, as it already opened the door.
                    IBlockData data = this.world.getType(blockposition);
                    if (data.getBlock() instanceof BlockDoor) {
                        // For some reason *BOTH* the bottom/top part have to be marked updated.
                        boolean bottom = data.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, bottom ? blockposition.up() : blockposition.down()));
                    } else if (data.getBlock() instanceof BlockTrapdoor) {
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
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
                        this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                    }
                    return;
                }
                org.bukkit.event.block.BlockDamageEvent blockEvent = CraftEventFactory.callBlockDamageEvent(this.player, blockposition.getX(), blockposition.getY(), blockposition.getZ(), this.player.inventory.getItemInHand(), f >= 1.0f);

                if (blockEvent.isCancelled()) {
                    // Let the client know the block still exists
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));
                    return;
                }

                if (blockEvent.getInstaBreak()) {
                    f = 2.0f;
                }
                // CraftBukkit end

                if (!iblockdata.isAir() && f >= 1.0F) {
                    this.a(blockposition, packetplayinblockdig_enumplayerdigtype);
                } else {
                    this.e = true;
                    this.g = blockposition;
                    int j = (int) (f * 10.0F);

                    this.world.a(this.player.getId(), blockposition, j);
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition)); // Paper - fixes MC-156852
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true));
                    this.l = j;
                }
            } else if (packetplayinblockdig_enumplayerdigtype == PacketPlayInBlockDig.EnumPlayerDigType.STOP_DESTROY_BLOCK) {
                if (blockposition.equals(this.g)) {
                    int k = this.currentTick - this.lastDigTick;

                    iblockdata = this.world.getType(blockposition);
                    if (!iblockdata.isAir()) {
                        float f1 = iblockdata.getDamage(this.player, this.player.world, blockposition) * (float) (k + 1);

                        if (f1 >= 0.7F) {
                            this.e = false;
                            this.world.a(this.player.getId(), blockposition, -1);
                            this.a(blockposition, packetplayinblockdig_enumplayerdigtype);
                            return;
                        }

                        if (!this.i) {
                            this.e = false;
                            this.i = true;
                            this.j = blockposition;
                            this.k = this.lastDigTick;
                        }
                    }
                }

                this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true));
            } else if (packetplayinblockdig_enumplayerdigtype == PacketPlayInBlockDig.EnumPlayerDigType.ABORT_DESTROY_BLOCK) {
                this.e = false;
                this.world.a(this.player.getId(), this.g, -1);
                this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true));
            }

        }

        this.world.chunkPacketBlockController.onPlayerLeftClickBlock(this, blockposition, enumdirection); // Paper - Anti-Xray
    }

    public void a(BlockPosition blockposition, PacketPlayInBlockDig.EnumPlayerDigType packetplayinblockdig_enumplayerdigtype) {
        if (this.breakBlock(blockposition)) {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockBreak(blockposition, this.world.getType(blockposition), packetplayinblockdig_enumplayerdigtype, true));
        } else {
            this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition)); // CraftBukkit - SPIGOT-5196
        }

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
                this.player.playerConnection.sendPacket(packet);
            }

            event = new BlockBreakEvent(bblock, this.player.getBukkitEntity());

            // Sword + Creative mode pre-cancel
            event.setCancelled(isSwordNoBreak);

            // Calculate default block experience
            IBlockData nmsData = this.world.getType(blockposition);
            Block nmsBlock = nmsData.getBlock();

            ItemStack itemstack = this.player.getEquipment(EnumItemSlot.MAINHAND);

            if (nmsBlock != null && !event.isCancelled() && !this.isCreative() && this.player.hasBlock(nmsBlock.getBlockData())) {
                event.setExpToDrop(nmsBlock.getExpDrop(nmsData, this.world, blockposition, itemstack));
            }

            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                if (isSwordNoBreak) {
                    return false;
                }
                // Let the client know the block still exists
                this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));

                // Brute force all possible updates
                for (EnumDirection dir : EnumDirection.values()) {
                    this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(world, blockposition.shift(dir)));
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

        if (false && !this.player.getItemInMainHand().getItem().a(iblockdata, (World) this.world, blockposition, (EntityHuman) this.player)) { // CraftBukkit - false
            return false;
        } else {
            iblockdata = this.world.getType(blockposition); // CraftBukkit - update state from plugins
            if (iblockdata.isAir()) return false; // CraftBukkit - A plugin set block to air without cancelling
            TileEntity tileentity = this.world.getTileEntity(blockposition);
            Block block = iblockdata.getBlock();

            if ((block instanceof BlockCommand || block instanceof BlockStructure || block instanceof BlockJigsaw) && !this.player.isCreativeAndOp()) {
                this.world.notify(blockposition, iblockdata, iblockdata, 3);
                return false;
            } else if (this.player.a((World) this.world, blockposition, this.gamemode)) {
                return false;
            } else {
                // CraftBukkit start
                org.bukkit.block.BlockState state = bblock.getState();
                world.captureDrops = new ArrayList<>();
                // CraftBukkit end
                block.a((World) this.world, blockposition, iblockdata, (EntityHuman) this.player);
                boolean flag = this.world.a(blockposition, false);

                if (flag) {
                    block.postBreak(this.world, blockposition, iblockdata);
                }

                if (this.isCreative()) {
                    // return true; // CraftBukkit
                } else {
                    ItemStack itemstack = this.player.getItemInMainHand();
                    boolean flag1 = this.player.hasBlock(iblockdata);

                    ItemStack itemstack1 = flag && flag1 && event.isDropItems() && !itemstack.isEmpty() ? itemstack.cloneItemStack() : ItemStack.a; // Paper - MC-136865 - clone before use
                    itemstack.a(this.world, iblockdata, blockposition, this.player);
                    if (flag && flag1 && event.isDropItems()) { // CraftBukkit - Check if block should drop items
                        //ItemStack itemstack1 = itemstack.isEmpty() ? ItemStack.a : itemstack.cloneItemStack(); // Paper - MC-136865 - move up

                        block.a(this.world, this.player, blockposition, iblockdata, tileentity, itemstack1);
                    }

                    // return true; // CraftBukkit
                }
                // CraftBukkit start
                if (event.isDropItems()) {
                    org.bukkit.craftbukkit.event.CraftEventFactory.handleBlockDropItemEvent(bblock, state, this.player, world.captureDrops);
                }
                world.captureDrops = null;

                // Drop event experience
                if (flag && event != null) {
                    iblockdata.getBlock().dropExperience(this.world, blockposition, event.getExpToDrop(), this.player); // Paper
                }

                return true;
                // CraftBukkit end
            }
        }
    }

    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand) {
        if (this.gamemode == EnumGamemode.SPECTATOR) {
            return EnumInteractionResult.PASS;
        } else if (entityhuman.getCooldownTracker().hasCooldown(itemstack.getItem())) {
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
    public EnumInteractionResult a(EntityHuman entityhuman, World world, ItemStack itemstack, EnumHand enumhand, MovingObjectPositionBlock movingobjectpositionblock) {
        BlockPosition blockposition = movingobjectpositionblock.getBlockPosition();
        IBlockData iblockdata = world.getType(blockposition);
        EnumInteractionResult enuminteractionresult = EnumInteractionResult.PASS;
        boolean cancelledBlock = false;

        if (this.gamemode == EnumGamemode.SPECTATOR) {
            ITileInventory itileinventory = iblockdata.b(world, blockposition);
            cancelledBlock = !(itileinventory instanceof ITileInventory);
        }

        if (entityhuman.getCooldownTracker().hasCooldown(itemstack.getItem())) {
            cancelledBlock = true;
        }

        PlayerInteractEvent event = CraftEventFactory.callPlayerInteractEvent(entityhuman, Action.RIGHT_CLICK_BLOCK, blockposition, movingobjectpositionblock.getDirection(), itemstack, cancelledBlock, enumhand);
        firedInteract = true;
        interactResult = event.useItemInHand() == Event.Result.DENY;

        if (event.useInteractedBlock() == Event.Result.DENY) {
            // If we denied a door from opening, we need to send a correcting update to the client, as it already opened the door.
            if (iblockdata.getBlock() instanceof BlockDoor) {
                boolean bottom = iblockdata.get(BlockDoor.HALF) == BlockPropertyDoubleBlockHalf.LOWER;
                ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutBlockChange(world, bottom ? blockposition.up() : blockposition.down()));
            } else if (iblockdata.getBlock() instanceof BlockCake) {
                ((EntityPlayer) entityhuman).getBukkitEntity().sendHealthUpdate(); // SPIGOT-1341 - reset health for cake
            // Paper start  - extend Player Interact cancellation // TODO: consider merging this into the extracted method
            } else if (iblockdata.getBlock() instanceof BlockStructure) {
                    ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutCloseWindow());
            } else if (iblockdata.getBlock() instanceof BlockCommand) {
                    ((EntityPlayer) entityhuman).playerConnection.sendPacket(new PacketPlayOutCloseWindow());
            } else if (iblockdata.getBlock() instanceof BlockFlowerPot) {
                // Send a block change to air and then send back the correct block, just to make the client happy
                PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(this.world, blockposition);
                packet.block = Blocks.AIR.getBlockData();
                this.player.playerConnection.sendPacket(packet);

                this.player.playerConnection.sendPacket(new PacketPlayOutBlockChange(this.world, blockposition));

                TileEntity tileentity = this.world.getTileEntity(blockposition);
                if (tileentity != null) {
                    player.playerConnection.sendPacket(tileentity.getUpdatePacket());
                }
            }
            // Paper end - extend Player Interact cancellation
            ((EntityPlayer) entityhuman).getBukkitEntity().updateInventory(); // SPIGOT-2867
            enuminteractionresult = (event.useItemInHand() != Event.Result.ALLOW) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.PASS;
        } else if (this.gamemode == EnumGamemode.SPECTATOR) {
            ITileInventory itileinventory = iblockdata.b(world, blockposition);

            if (itileinventory != null) {
                entityhuman.openContainer(itileinventory);
                return EnumInteractionResult.SUCCESS;
            } else {
                return EnumInteractionResult.PASS;
            }
        } else {
            boolean flag = !entityhuman.getItemInMainHand().isEmpty() || !entityhuman.getItemInOffHand().isEmpty();
            boolean flag1 = entityhuman.isSneaking() && flag;

            if (!flag1) {
                enuminteractionresult = iblockdata.interact(world, entityhuman, enumhand, movingobjectpositionblock) ? EnumInteractionResult.SUCCESS : EnumInteractionResult.FAIL;
            }

            if (!itemstack.isEmpty() && enuminteractionresult != EnumInteractionResult.SUCCESS && !interactResult) { // add !interactResult SPIGOT-764
                ItemActionContext itemactioncontext = new ItemActionContext(entityhuman, enumhand, movingobjectpositionblock);

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
