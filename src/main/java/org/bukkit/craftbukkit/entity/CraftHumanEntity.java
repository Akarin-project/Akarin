package org.bukkit.craftbukkit.entity;

import java.util.Set;

import net.minecraft.server.*;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventoryView;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

public class CraftHumanEntity extends CraftLivingEntity implements HumanEntity {
    private CraftInventoryPlayer inventory;
    private final CraftInventory enderChest;
    protected final PermissibleBase perm = new PermissibleBase(this);
    private boolean op;
    private GameMode mode;

    public CraftHumanEntity(final CraftServer server, final EntityHuman entity) {
        super(server, entity);
        mode = server.getDefaultGameMode();
        this.inventory = new CraftInventoryPlayer(entity.inventory);
        enderChest = new CraftInventory(entity.getEnderChest());
    }

    public String getName() {
        return getHandle().getName();
    }

    public PlayerInventory getInventory() {
        return inventory;
    }

    public EntityEquipment getEquipment() {
        return inventory;
    }

    public Inventory getEnderChest() {
        return enderChest;
    }

    public ItemStack getItemInHand() {
        return getInventory().getItemInHand();
    }

    public void setItemInHand(ItemStack item) {
        getInventory().setItemInHand(item);
    }

    public ItemStack getItemOnCursor() {
        return CraftItemStack.asCraftMirror(getHandle().inventory.getCarried());
    }

    public void setItemOnCursor(ItemStack item) {
        net.minecraft.server.ItemStack stack = CraftItemStack.asNMSCopy(item);
        getHandle().inventory.setCarried(stack);
        if (this instanceof CraftPlayer) {
            ((EntityPlayer) getHandle()).broadcastCarriedItem(); // Send set slot for cursor
        }
    }

    public boolean isSleeping() {
        return getHandle().sleeping;
    }

    public int getSleepTicks() {
        return getHandle().sleepTicks;
    }

    public boolean isOp() {
        return op;
    }

    public boolean isPermissionSet(String name) {
        return perm.isPermissionSet(name);
    }

    public boolean isPermissionSet(Permission perm) {
        return this.perm.isPermissionSet(perm);
    }

    public boolean hasPermission(String name) {
        return perm.hasPermission(name);
    }

    public boolean hasPermission(Permission perm) {
        return this.perm.hasPermission(perm);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value) {
        return perm.addAttachment(plugin, name, value);
    }

    public PermissionAttachment addAttachment(Plugin plugin) {
        return perm.addAttachment(plugin);
    }

    public PermissionAttachment addAttachment(Plugin plugin, String name, boolean value, int ticks) {
        return perm.addAttachment(plugin, name, value, ticks);
    }

    public PermissionAttachment addAttachment(Plugin plugin, int ticks) {
        return perm.addAttachment(plugin, ticks);
    }

    public void removeAttachment(PermissionAttachment attachment) {
        perm.removeAttachment(attachment);
    }

    public void recalculatePermissions() {
        perm.recalculatePermissions();
    }

    public void setOp(boolean value) {
        this.op = value;
        perm.recalculatePermissions();
    }

    public Set<PermissionAttachmentInfo> getEffectivePermissions() {
        return perm.getEffectivePermissions();
    }

    public GameMode getGameMode() {
        return mode;
    }

    public void setGameMode(GameMode mode) {
        if (mode == null) {
            throw new IllegalArgumentException("Mode cannot be null");
        }

        this.mode = mode;
    }

    @Override
    public EntityHuman getHandle() {
        return (EntityHuman) entity;
    }

    public void setHandle(final EntityHuman entity) {
        super.setHandle(entity);
        this.inventory = new CraftInventoryPlayer(entity.inventory);
    }

    @Override
    public String toString() {
        return "CraftHumanEntity{" + "id=" + getEntityId() + "name=" + getName() + '}';
    }

    public InventoryView getOpenInventory() {
        return getHandle().activeContainer.getBukkitView();
    }

    public InventoryView openInventory(Inventory inventory) {
        if(!(getHandle() instanceof EntityPlayer)) return null;
        EntityPlayer player = (EntityPlayer) getHandle();
        InventoryType type = inventory.getType();
        Container formerContainer = getHandle().activeContainer;
        // TODO: Should we check that it really IS a CraftInventory first?
        CraftInventory craftinv = (CraftInventory) inventory;
        switch(type) {
        case PLAYER:
        case CHEST:
        case ENDER_CHEST:
            getHandle().openContainer(craftinv.getInventory());
            break;
        case DISPENSER:
            if (craftinv.getInventory() instanceof TileEntityDispenser) {
                getHandle().openTileEntity((TileEntityDispenser) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, "minecraft:dispenser");
            }
            break;
        case FURNACE:
            if (craftinv.getInventory() instanceof TileEntityFurnace) {
                getHandle().openTileEntity((TileEntityFurnace) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, "minecraft:furnace");
            }
            break;
        case WORKBENCH:
            openCustomInventory(inventory, player, "minecraft:crafting_table");
            break;
        case BREWING:
            if (craftinv.getInventory() instanceof TileEntityBrewingStand) {
                getHandle().openTileEntity((TileEntityBrewingStand) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, "minecraft:brewing_stand");
            }
            break;
        case ENCHANTING:
            openCustomInventory(inventory, player, "minecraft:enchanting_table");
            break;
        case HOPPER:
            if (craftinv.getInventory() instanceof TileEntityHopper) {
                getHandle().openTileEntity((TileEntityHopper) craftinv.getInventory());
            } else if (craftinv.getInventory() instanceof EntityMinecartHopper) {
                getHandle().openTileEntity((EntityMinecartHopper) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, "minecraft:hopper");
            }
            break;
        case BEACON:
            if (craftinv.getInventory() instanceof TileEntityBeacon) {
                getHandle().openTileEntity((TileEntityBeacon) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, "minecraft:beacon");
            }
            break;
        case ANVIL:
            if (craftinv.getInventory() instanceof TileEntityContainerAnvil) {
                getHandle().openTileEntity((TileEntityContainerAnvil) craftinv.getInventory());
            } else {
                openCustomInventory(inventory, player, "minecraft:anvil");
            }
            break;
        case CREATIVE:
        case CRAFTING:
            throw new IllegalArgumentException("Can't open a " + type + " inventory!");
        }
        if (getHandle().activeContainer == formerContainer) {
            return null;
        }
        getHandle().activeContainer.checkReachable = false;
        return getHandle().activeContainer.getBukkitView();
    }

    private void openCustomInventory(Inventory inventory, EntityPlayer player, String windowType) {
        if (player.playerConnection == null) return;
        Container container = new CraftContainer(inventory, this, player.nextContainerCounter());

        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if(container == null) return;

        String title = container.getBukkitView().getTitle();
        int size = container.getBukkitView().getTopInventory().getSize();

        // Special cases
        if (windowType.equals("minecraft:crafting_table") 
                || windowType.equals("minecraft:anvil")
                || windowType.equals("minecraft:enchanting_table")
                ) {
            size = 0;
        }

        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, new ChatComponentText(title), size));
        getHandle().activeContainer = container;
        getHandle().activeContainer.addSlotListener(player);
    }

    public InventoryView openWorkbench(Location location, boolean force) {
        if (!force) {
            Block block = location.getBlock();
            if (block.getType() != Material.WORKBENCH) {
                return null;
            }
        }
        if (location == null) {
            location = getLocation();
        }
        getHandle().openTileEntity(new TileEntityContainerWorkbench(getHandle().world, new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())));
        if (force) {
            getHandle().activeContainer.checkReachable = false;
        }
        return getHandle().activeContainer.getBukkitView();
    }

    public InventoryView openEnchanting(Location location, boolean force) {
        if (!force) {
            Block block = location.getBlock();
            if (block.getType() != Material.ENCHANTMENT_TABLE) {
                return null;
            }
        }
        if (location == null) {
            location = getLocation();
        }

        // If there isn't an enchant table we can force create one, won't be very useful though.
        TileEntity container = getHandle().world.getTileEntity(new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ()));
        if (container == null && force) {
            container = new TileEntityEnchantTable();
        }
        getHandle().openTileEntity((ITileEntityContainer) container);

        if (force) {
            getHandle().activeContainer.checkReachable = false;
        }
        return getHandle().activeContainer.getBukkitView();
    }

    public void openInventory(InventoryView inventory) {
        if (!(getHandle() instanceof EntityPlayer)) return; // TODO: NPC support?
        if (((EntityPlayer) getHandle()).playerConnection == null) return;
        if (getHandle().activeContainer != getHandle().defaultContainer) {
            // fire INVENTORY_CLOSE if one already open
            ((EntityPlayer)getHandle()).playerConnection.a(new PacketPlayInCloseWindow(getHandle().activeContainer.windowId));
        }
        EntityPlayer player = (EntityPlayer) getHandle();
        Container container;
        if (inventory instanceof CraftInventoryView) {
            container = ((CraftInventoryView) inventory).getHandle();
        } else {
            container = new CraftContainer(inventory, player.nextContainerCounter());
        }

        // Trigger an INVENTORY_OPEN event
        container = CraftEventFactory.callInventoryOpenEvent(player, container);
        if (container == null) {
            return;
        }

        // Now open the window
        InventoryType type = inventory.getType();
        String windowType = CraftContainer.getNotchInventoryType(type);
        String title = inventory.getTitle();
        int size = inventory.getTopInventory().getSize();
        player.playerConnection.sendPacket(new PacketPlayOutOpenWindow(container.windowId, windowType, new ChatComponentText(title), size));
        player.activeContainer = container;
        player.activeContainer.addSlotListener(player);
    }

    public void closeInventory() {
        getHandle().closeInventory();
    }

    public boolean isBlocking() {
        return getHandle().isBlocking();
    }

    public boolean setWindowProperty(InventoryView.Property prop, int value) {
        return false;
    }

    public int getExpToLevel() {
        return getHandle().getExpToLevel();
    }
}
