package io.akarin.server.mixin.optimization;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import net.minecraft.server.BaseBlockPosition;
import net.minecraft.server.Block;
import net.minecraft.server.BlockDiodeAbstract;
import net.minecraft.server.BlockObserver;
import net.minecraft.server.BlockPiston;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.BlockRedstoneComparator;
import net.minecraft.server.BlockRedstoneTorch;
import net.minecraft.server.BlockRedstoneWire;
import net.minecraft.server.BlockRepeater;
import net.minecraft.server.Blocks;
import net.minecraft.server.EnumDirection;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IBlockData;
import net.minecraft.server.Material;
import net.minecraft.server.World;

@Mixin(value = BlockRedstoneWire.class, remap = false)
public abstract class PandaRedstoneWire extends Block {
    
    protected PandaRedstoneWire(Material material) {
        super(material);
    }
    
    /** Positions that need to be turned off **/
    private List<BlockPosition> turnOff = Lists.newArrayList();
    /** Positions that need to be checked to be turned on **/
    private List<BlockPosition> turnOn = Lists.newArrayList();
    /** Positions of wire that was updated already (Ordering determines update order and is therefore required!) **/
    private final Set<BlockPosition> updatedRedstoneWire = Sets.newLinkedHashSet();
    
    /** Ordered arrays of the facings; Needed for the update order.
     *  I went with a vertical-first order here, but vertical last would work to.
     *  However it should be avoided to update the vertical axis between the horizontal ones as this would cause unneeded directional behavior. **/
    private static final EnumDirection[] facingsHorizontal = {EnumDirection.WEST, EnumDirection.EAST, EnumDirection.NORTH, EnumDirection.SOUTH};
    private static final EnumDirection[] facingsVertical = {EnumDirection.DOWN, EnumDirection.UP};
    private static final EnumDirection[] facings = ArrayUtils.addAll(facingsVertical, facingsHorizontal);
    
    /** Offsets for all surrounding blocks that need to receive updates **/
    private static final BaseBlockPosition[] surroundingBlocksOffset;
    static {
        Set<BaseBlockPosition> set = Sets.newLinkedHashSet();
        for (EnumDirection facing : facings) {
            set.add(facing.getDirectionPosition());
        }
        for (EnumDirection facing1 : facings) {
            BaseBlockPosition v1 = facing1.getDirectionPosition();
            for (EnumDirection facing2 : facings) {
                BaseBlockPosition v2 = facing2.getDirectionPosition();
                set.add(new BaseBlockPosition(v1.getX() + v2.getX(), v1.getY() + v2.getY(), v1.getZ() + v2.getZ()));
            }
        }
        set.remove(BaseBlockPosition.ZERO);
        surroundingBlocksOffset = set.toArray(new BaseBlockPosition[set.size()]);
    }
    
    @Shadow(aliases = "g") private boolean canProvidePower;
    @Shadow public abstract int getPower(World world, BlockPosition pos, int strength);
    @Shadow(aliases = "b") public abstract boolean isPowerSourceAt(IBlockAccess worldIn, BlockPosition pos, EnumDirection side);
    
    @Inject(method = "e", at = @At("HEAD"), cancellable = true)
    private void onUpdateSurroundingRedstone(World worldIn, BlockPosition pos, IBlockData state, CallbackInfoReturnable<IBlockData> cir) {
        this.updateSurroundingRedstone(worldIn, pos);
        cir.setReturnValue(state);
    }
    
    @Inject(method = "a*", at = @At("HEAD"), cancellable = true)
    private void onCalculateCurrentChanges(World worldIn, BlockPosition pos1, BlockPosition pos2, IBlockData state, CallbackInfoReturnable<IBlockData> cir) {
        this.calculateCurrentChanges(worldIn, pos1);
        cir.setReturnValue(state);
    }
    
    /**
     * Recalculates all surrounding wires and causes all needed updates
     * 
     * @author panda
     * 
     * @param world World
     * @param pos Position that needs updating
     */
    private void updateSurroundingRedstone(World world, BlockPosition pos) {
        // Recalculate the connected wires
        this.calculateCurrentChanges(world, pos);

        // Set to collect all the updates, to only execute them once. Ordering required.
        Set<BlockPosition> blocksNeedingUpdate = Sets.newLinkedHashSet();
        
        // Add the needed updates
        for (BlockPosition posi : this.updatedRedstoneWire) {
            this.addBlocksNeedingUpdate(world, posi, blocksNeedingUpdate);
        }
        // Add all other updates to keep known behaviors
        // They are added in a backwards order because it preserves a commonly used behavior with the update order
        Iterator<BlockPosition> it = Lists.newLinkedList(this.updatedRedstoneWire).descendingIterator();
        while (it.hasNext()) {
            this.addAllSurroundingBlocks(it.next(), blocksNeedingUpdate);
        }
        // Remove updates on the wires as they just were updated
        blocksNeedingUpdate.removeAll(this.updatedRedstoneWire);
        /*
         * Avoid unnecessary updates on the just updated wires A huge scale test
         * showed about 40% more ticks per second It's probably less in normal
         * usage but likely still worth it
         */
        this.updatedRedstoneWire.clear();
        
        // Execute updates
        for (BlockPosition posi : blocksNeedingUpdate) {
            world.applyPhysics(posi, (BlockRedstoneWire) (Object) this, false);
        }
    }
    
    /**
     * Turns on or off all connected wires
     * 
     * @param worldIn World
     * @param position Position of the wire that received the update
     */
    private void calculateCurrentChanges(World worldIn, BlockPosition position) {
        // Turn off all connected wires first if needed
        if (worldIn.getType(position).getBlock() == (BlockRedstoneWire) (Object) this) {
            turnOff.add(position);
        } else {
            // In case this wire was removed, check the surrounding wires
            this.checkSurroundingWires(worldIn, position);
        }
        
        while (!turnOff.isEmpty()) {
            BlockPosition pos = turnOff.remove(0);
            if (pos == null) continue; // Akarin
            IBlockData state = worldIn.getType(pos);
            int oldPower = state.get(BlockRedstoneWire.POWER).intValue();
            this.canProvidePower = false;
            int blockPower = worldIn.z(pos); // OBFHELPER: isBlockIndirectlyGettingPowered
            this.canProvidePower = true;
            int wirePower = this.getSurroundingWirePower(worldIn, pos);
            
            // Lower the strength as it moved a block
            wirePower--;
            int newPower = Math.max(blockPower, wirePower);
            
            // Akarin start - BlockRedstoneEvent
            if (oldPower != newPower) {
                BlockRedstoneEvent event = new BlockRedstoneEvent(worldIn.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldPower, newPower);
                worldIn.getServer().getPluginManager().callEvent(event);
                newPower = event.getNewCurrent();
            }
            // Akarin end
            
            // Power lowered?
            if (newPower < oldPower) {
                // If it's still powered by a direct source (but weaker) mark for turn on
                if (blockPower > 0 && !this.turnOn.contains(pos)) {
                    this.turnOn.add(pos);
                }
                // Set all the way to off for now, because wires that were powered by this need to update first
                setWireState(worldIn, pos, state, 0);
            // Power rose?
            } else if (newPower > oldPower) {
                // Set new Power
                this.setWireState(worldIn, pos, state, newPower);
            }
            // Check if surrounding wires need to change based on the current/new state and add them to the lists
            this.checkSurroundingWires(worldIn, pos);
        }
        // Now all needed wires are turned off. Time to turn them on again if there is a power source.
        while (!this.turnOn.isEmpty()) {
            BlockPosition pos = this.turnOn.remove(0);
            if (pos == null) continue; // Akarin
            IBlockData state = worldIn.getType(pos);
            int oldPower = state.get(BlockRedstoneWire.POWER).intValue();
            this.canProvidePower = false;
            int blockPower = worldIn.z(pos); // OBFHELPER: isBlockIndirectlyGettingPowered
            this.canProvidePower = true;
            int wirePower = this.getSurroundingWirePower(worldIn, pos);
            // Lower the strength as it moved a block
            wirePower--;
            int newPower = Math.max(blockPower, wirePower);
            
            // Akarin start - BlockRedstoneEvent
            BlockRedstoneEvent event = new BlockRedstoneEvent(worldIn.getWorld().getBlockAt(pos.getX(), pos.getY(), pos.getZ()), oldPower, newPower);
            worldIn.getServer().getPluginManager().callEvent(event);
            newPower = event.getNewCurrent();
            // Akarin end
            
            if (newPower > oldPower) {
                setWireState(worldIn, pos, state, newPower);
            } else if (newPower < oldPower) {
                // Add warning
            }
            // Check if surrounding wires need to change based on the current/new state and add them to the lists
            this.checkSurroundingWires(worldIn, pos);
        }
        this.turnOff.clear();
        this.turnOn.clear();
    }

    /**
     * Checks if an wire needs to be marked for update depending on the power next to it
     * 
     * @author panda
     * 
     * @param worldIn World
     * @param pos Position of the wire that might need to change
     * @param otherPower Power of the wire next to it
     */
    private void addWireToList(World worldIn, BlockPosition pos, int otherPower) {
        IBlockData state = worldIn.getType(pos);
        if (state.getBlock() == (BlockRedstoneWire) (Object) this) {
            int power = state.get(BlockRedstoneWire.POWER).intValue();
            // Could get powered stronger by the neighbor?
            if (power < (otherPower - 1) && !this.turnOn.contains(pos)) {
                // Mark for turn on check.
                this.turnOn.add(pos);
            }
            // Should have powered the neighbor? Probably was powered by it and is in turn off phase.
            if (power > otherPower && !this.turnOff.contains(pos)) {
                // Mark for turn off check.
                this.turnOff.add(pos);
            }
        }
    }

    /**
     * Checks if the wires around need to get updated depending on this wires state.
     * Checks all wires below before the same layer before on top to keep
     * some more rotational symmetry around the y-axis. 
     * 
     * @author panda
     * 
     * @param worldIn World
     * @param pos Position of the wire
     */
    private void checkSurroundingWires(World worldIn, BlockPosition pos) {
        IBlockData state = worldIn.getType(pos);
        int ownPower = 0;
        if (state.getBlock() == (BlockRedstoneWire) (Object) this) {
            ownPower = state.get(BlockRedstoneWire.POWER).intValue();
        }
        // Check wires on the same layer first as they appear closer to the wire
        for (EnumDirection facing : facingsHorizontal) {
            BlockPosition offsetPos = pos.shift(facing);
            if (facing.getAxis().isHorizontal()) {
                this.addWireToList(worldIn, offsetPos, ownPower);
            }
        }
        for (EnumDirection facingVertical : facingsVertical) {
            BlockPosition offsetPos = pos.shift(facingVertical);
            boolean solidBlock = worldIn.getType(offsetPos).k(); // OBFHELPER: isBlockNormalCube
            for (EnumDirection facingHorizontal : facingsHorizontal) {
                // wire can travel upwards if the block on top doesn't cut the wire (is non-solid)
                // it can travel down if the block below is solid and the block "diagonal" doesn't cut off the wire (is non-solid) 
                if ((facingVertical == EnumDirection.UP && !solidBlock) || (facingVertical == EnumDirection.DOWN && solidBlock && !worldIn.getType(offsetPos.shift(facingHorizontal)).k())) { // OBFHELPER: isBlockNormalCube
                    this.addWireToList(worldIn, offsetPos.shift(facingHorizontal), ownPower);
                }
            }
        }
    }

    /**
     * Gets the maximum power of the surrounding wires
     * 
     * @author panda
     * 
     * @param worldIn World
     * @param pos Position of the asking wire
     * @return The maximum power of the wires that could power the wire at pos
     */
    private int getSurroundingWirePower(World worldIn, BlockPosition pos) {
        int wirePower = 0;
        for (EnumDirection enumfacing : EnumDirection.EnumDirectionLimit.HORIZONTAL) {
            BlockPosition offsetPos = pos.shift(enumfacing);
            // Wires on the same layer
            wirePower = this.getPower(worldIn, offsetPos, wirePower);
            
            // Block below the wire need to be solid (Upwards diode of slabs/stairs/glowstone) and no block should cut the wire
            if(worldIn.getType(offsetPos).l() && !worldIn.getType(pos.up()).l()) { // OBFHELPER: isNormalCube
                wirePower = this.getPower(worldIn, offsetPos.up(), wirePower);
                // Only get from power below if no block is cutting the wire
            } else if (!worldIn.getType(offsetPos).l()) { // OBFHELPER: isNormalCube
                wirePower = this.getPower(worldIn, offsetPos.down(), wirePower);
            }
        }
        return wirePower;
    }

    /**
     * Adds all blocks that need to receive an update from a redstone change in this position.
     * This means only blocks that actually could change.
     * 
     * @author panda
     * 
     * @param worldIn World
     * @param pos Position of the wire
     * @param set Set to add the update positions too
     */
    private void addBlocksNeedingUpdate(World worldIn, BlockPosition pos, Set<BlockPosition> set) {
        List<EnumDirection> connectedSides = this.getSidesToPower(worldIn, pos);
        // Add the blocks next to the wire first (closest first order)
        for (EnumDirection facing : facings) {
            BlockPosition offsetPos = pos.shift(facing);
            // canConnectTo() is not the nicest solution here as it returns true for e.g. the front of a repeater
            // canBlockBePowereFromSide catches these cases
            if (!connectedSides.contains(facing.opposite()) && facing != EnumDirection.DOWN
                    && (!facing.getAxis().isHorizontal() || canConnectToBlock(worldIn.getType(offsetPos), facing))) continue;
            if (this.canBlockBePoweredFromSide(worldIn.getType(offsetPos), facing, true))
                set.add(offsetPos);
        }
        // Later add blocks around the surrounding blocks that get powered
        for (EnumDirection facing : facings) {
            BlockPosition offsetPos = pos.shift(facing);
            if (!connectedSides.contains(facing.opposite()) && facing != EnumDirection.DOWN || !worldIn.getType(offsetPos).l()) continue; // OBFHELPER: isNormalCube
            for (EnumDirection facing1 : facings) {
                if (this.canBlockBePoweredFromSide(worldIn.getType(offsetPos.shift(facing1)), facing1, false))
                    set.add(offsetPos.shift(facing1));
            }
        }
    }

    /**
     * Checks if a block can get powered from a side.
     * This behavior would better be implemented per block type as follows:
     *  - return false as default. (blocks that are not affected by redstone don't need to be updated, it doesn't really hurt if they are either)
     *  - return true for all blocks that can get powered from all side and change based on it (doors, fence gates, trap doors, note blocks, lamps, dropper, hopper, TNT, rails, possibly more)
     *  - implement own logic for pistons, repeaters, comparators and redstone torches
     *  The current implementation was chosen to keep everything in one class.
     *  
     *  Why is this extra check needed?
     *  1. It makes sure that many old behaviors still work (QC + Pistons).
     *  2. It prevents updates from "jumping".
     *     Or rather it prevents this wire to update a block that would get powered by the next one of the same line.
     *     This is to prefer as it makes understanding the update order of the wire really easy. The signal "travels" from the power source.
     * 
     * @author panda
     * 
     * @param state      State of the block
     * @param side       Side from which it gets powered
     * @param isWire     True if it's powered by a wire directly, False if through a block
     * @return           True if the block can change based on the power level it gets on the given side, false otherwise
     */
    private boolean canBlockBePoweredFromSide(IBlockData state, EnumDirection side, boolean isWire) {
        if (state.getBlock() instanceof BlockPiston && state.get(BlockPiston.FACING) == side.opposite()) {
            return false;
        }
        if (state.getBlock() instanceof BlockDiodeAbstract && state.get(BlockDiodeAbstract.FACING) != side.opposite()) {
            if (isWire && state.getBlock() instanceof BlockRedstoneComparator
                    && state.get(BlockRedstoneComparator.FACING).k() != side.getAxis() && side.getAxis().isHorizontal()) {
                return true;
            }
            return false;
        }
        if (state.getBlock() instanceof BlockRedstoneTorch) {
            if (isWire || state.get(BlockRedstoneTorch.FACING) != side) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates a list of all horizontal sides that can get powered by a wire.
     * The list is ordered the same as the facingsHorizontal.
     * 
     * @param worldIn World
     * @param pos Position of the wire
     * @return List of all facings that can get powered by this wire
     */
    private List<EnumDirection> getSidesToPower(World worldIn, BlockPosition pos) {
        List<EnumDirection> retval = Lists.newArrayList();
        for (EnumDirection facing : facingsHorizontal) {
            if (isPowerSourceAt(worldIn, pos, facing))
                retval.add(facing);
        }
        if (retval.isEmpty()) return Lists.newArrayList(facingsHorizontal);
        boolean northsouth = retval.contains(EnumDirection.NORTH) || retval.contains(EnumDirection.SOUTH);
        boolean eastwest = retval.contains(EnumDirection.EAST) || retval.contains(EnumDirection.WEST);
        if (northsouth) {
            retval.remove(EnumDirection.EAST);
            retval.remove(EnumDirection.WEST);
        }
        if (eastwest) {
            retval.remove(EnumDirection.NORTH);
            retval.remove(EnumDirection.SOUTH);
        }
        return retval;
    }

    /**
     * Adds all surrounding positions to a set.
     * This is the neighbor blocks, as well as their neighbors 
     * 
     * @param pos
     * @param set
     */
    private void addAllSurroundingBlocks(BlockPosition pos, Set<BlockPosition> set) {
        for (BaseBlockPosition vect : surroundingBlocksOffset) {
            set.add(pos.a(vect)); // OBFHELPER: add
        }
    }

    /**
     * Sets the block state of a wire with a new power level and marks for updates
     * 
     * @author panda
     * 
     * @param worldIn World
     * @param pos Position at which the state needs to be set
     * @param state Old state
     * @param power Power it should get set to
     */
    private void setWireState(World worldIn, BlockPosition pos, IBlockData state, int power) {
        state = state.set(BlockRedstoneWire.POWER, Integer.valueOf(power));
        worldIn.setTypeAndData(pos, state, 2);
        updatedRedstoneWire.add(pos);
    }
    
    /**
     * @author panda
     * @reason Uses local surrounding block offset list for notifications.
     *
     * @param world The world
     * @param pos The position
     * @param state The block state
     */
    @Override
    @Overwrite
    public void onPlace(World world, BlockPosition pos, IBlockData state) {
        this.updateSurroundingRedstone(world, pos);
        for (BaseBlockPosition vec : surroundingBlocksOffset) {
            world.applyPhysics(pos.a(vec), this, false); // OBFHELPER: add
        }
    }
    
    /**
     * @author panda
     * @reason Uses local surrounding block offset list for notifications.
     *
     * @param world The world
     * @param pos The position
     */
    @Override
    @Overwrite
    public void remove(World world, BlockPosition pos, IBlockData state) {
        super.remove(world, pos, state);
        this.updateSurroundingRedstone(world, pos);
        for (BaseBlockPosition vec : surroundingBlocksOffset) {
            world.applyPhysics(pos.a(vec), this, false); // OBFHELPER: add
        }
    }

    /**
     * @author panda
     * @reason Changed to use getSidesToPower() to avoid duplicate implementation.
     *
     * @param blockState The block state
     * @param blockAccess The block access
     * @param pos The position
     * @param side The side
     */
    @Override
    @Overwrite
    public int b(IBlockData blockState, IBlockAccess blockAccess, BlockPosition pos, EnumDirection side) { // OBFHELPER: getWeakPower
        if (!this.canProvidePower) {
            return 0;
        } else {
            if (side == EnumDirection.UP || this.getSidesToPower((World) blockAccess, pos).contains(side)) {
                return blockState.get(BlockRedstoneWire.POWER).intValue();
            } else {
                return 0;
            }
        }
    }

    private static boolean canConnectToBlock(IBlockData blockState, @Nullable EnumDirection side) {
        Block block = blockState.getBlock();
        
        if (block == Blocks.REDSTONE_WIRE) {
            return true;
        } else if (Blocks.UNPOWERED_REPEATER.D(blockState)) { // OBFHELPER: isSameDiode
            EnumDirection enumdirection1 = blockState.get(BlockRepeater.FACING);
            
            return enumdirection1 == side || enumdirection1.opposite() == side;
        } else if (Blocks.dk == blockState.getBlock()) {
            return side == blockState.get(BlockObserver.FACING); // OBFHELPER: OBSERVER
        } else {
            return blockState.m() && side != null; // OBFHELPER: canProvidePower
        }
    }
    
}
