package io.akarin.server.mixin.optimization;

import java.util.Iterator;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.Block;
import net.minecraft.server.BlockChest;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.EnumDirection;
import net.minecraft.server.IBlockData;
import net.minecraft.server.Material;
import net.minecraft.server.World;

@Mixin(value = BlockChest.class, remap = false)
public abstract class MixinBlockChest extends Block {
	protected MixinBlockChest(Material material) {
		super(material);
	}
	@Shadow public abstract IBlockData e(World world, BlockPosition blockposition, IBlockData iblockdata);
	@Overwrite
	public void onPlace(World world, BlockPosition blockposition, IBlockData iblockdata) {
		//((BlockChest)(Object)this).e(world, blockposition, iblockdata);
		e(world, blockposition, iblockdata);
        Iterator<EnumDirection> iterator = EnumDirection.EnumDirectionLimit.HORIZONTAL.iterator();

        while (iterator.hasNext()) {
            EnumDirection enumdirection = iterator.next();
            BlockPosition blockposition1 = blockposition.shift(enumdirection);
            // NeonPaper start - Dont load chunks for chests
            final IBlockData iblockdata1 = world.isLoaded(blockposition1) ? world.getType(blockposition1) : null;
            if (iblockdata1 ==  null) {
                continue;
            }
            // NeonPaper end

            if (iblockdata1.getBlock() == this) {
            	//((BlockChest)(Object)this).e(world, blockposition1, iblockdata1);
            	e(world, blockposition1, iblockdata1);
            }
        }

    }
}
