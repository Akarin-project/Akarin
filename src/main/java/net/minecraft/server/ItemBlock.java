package net.minecraft.server;

import java.util.Map;
import javax.annotation.Nullable;
// CraftBukkit start
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.block.data.CraftBlockData;
import org.bukkit.event.block.BlockCanBuildEvent;
// CraftBukkit end

public class ItemBlock extends Item {

    @Deprecated
    private final Block a;

    public ItemBlock(Block block, Item.Info item_info) {
        super(item_info);
        this.a = block;
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        return this.a(new BlockActionContext(itemactioncontext));
    }

    public EnumInteractionResult a(BlockActionContext blockactioncontext) {
        if (!blockactioncontext.b()) {
            return EnumInteractionResult.FAIL;
        } else {
            IBlockData iblockdata = this.b(blockactioncontext);

            if (iblockdata == null) {
                return EnumInteractionResult.FAIL;
            } else if (!this.a(blockactioncontext, iblockdata)) {
                return EnumInteractionResult.FAIL;
            } else {
                BlockPosition blockposition = blockactioncontext.getClickPosition();
                World world = blockactioncontext.getWorld();
                EntityHuman entityhuman = blockactioncontext.getEntity();
                ItemStack itemstack = blockactioncontext.getItemStack();
                IBlockData iblockdata1 = world.getType(blockposition);
                Block block = iblockdata1.getBlock();

                if (block == iblockdata.getBlock()) {
                    this.a(blockposition, world, entityhuman, itemstack, iblockdata1);
                    block.postPlace(world, blockposition, iblockdata1, entityhuman, itemstack);
                    if (entityhuman instanceof EntityPlayer) {
                        CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition, itemstack);
                    }
                }

                SoundEffectType soundeffecttype = block.getStepSound();

                // world.a(entityhuman, blockposition, soundeffecttype.e(), SoundCategory.BLOCKS, (soundeffecttype.a() + 1.0F) / 2.0F, soundeffecttype.b() * 0.8F); // CraftBukkit - SPIGOT-1288
                itemstack.subtract(1);
                return EnumInteractionResult.SUCCESS;
            }
        }
    }

    protected boolean a(BlockPosition blockposition, World world, @Nullable EntityHuman entityhuman, ItemStack itemstack, IBlockData iblockdata) {
        return a(world, entityhuman, blockposition, itemstack);
    }

    @Nullable
    protected IBlockData b(BlockActionContext blockactioncontext) {
        IBlockData iblockdata = this.getBlock().getPlacedState(blockactioncontext);

        return iblockdata != null && this.b(blockactioncontext, iblockdata) ? iblockdata : null;
    }

    protected boolean b(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        // CraftBukkit start - store default return
        boolean defaultReturn = iblockdata.canPlace(blockactioncontext.getWorld(), blockactioncontext.getClickPosition()) && blockactioncontext.getWorld().a(iblockdata, blockactioncontext.getClickPosition());
        org.bukkit.entity.Player player = (blockactioncontext.getEntity() instanceof EntityPlayer) ? (org.bukkit.entity.Player) blockactioncontext.getEntity().getBukkitEntity() : null;

        BlockCanBuildEvent event = new BlockCanBuildEvent(CraftBlock.at(blockactioncontext.getWorld(), blockactioncontext.getClickPosition()), player, CraftBlockData.fromData(iblockdata), defaultReturn);
        blockactioncontext.getWorld().getServer().getPluginManager().callEvent(event);

        return event.isBuildable();
        // CraftBukkit end
    }

    protected boolean a(BlockActionContext blockactioncontext, IBlockData iblockdata) {
        return blockactioncontext.getWorld().setTypeAndData(blockactioncontext.getClickPosition(), iblockdata, 11);
    }

    public static boolean a(World world, @Nullable EntityHuman entityhuman, BlockPosition blockposition, ItemStack itemstack) {
        MinecraftServer minecraftserver = world.getMinecraftServer();

        if (minecraftserver == null) {
            return false;
        } else {
            NBTTagCompound nbttagcompound = itemstack.b("BlockEntityTag");

            if (nbttagcompound != null) {
                TileEntity tileentity = world.getTileEntity(blockposition);

                if (tileentity != null) {
                    if (!world.isClientSide && tileentity.isFilteredNBT() && (entityhuman == null || !entityhuman.isCreativeAndOp())) {
                        return false;
                    }

                    NBTTagCompound nbttagcompound1 = tileentity.save(new NBTTagCompound());
                    NBTTagCompound nbttagcompound2 = nbttagcompound1.clone();

                    nbttagcompound1.a(nbttagcompound);
                    nbttagcompound1.setInt("x", blockposition.getX());
                    nbttagcompound1.setInt("y", blockposition.getY());
                    nbttagcompound1.setInt("z", blockposition.getZ());
                    if (!nbttagcompound1.equals(nbttagcompound2)) {
                        tileentity.load(nbttagcompound1);
                        tileentity.update();
                        return true;
                    }
                }
            }

            return false;
        }
    }

    public String getName() {
        return this.getBlock().m();
    }

    public void a(CreativeModeTab creativemodetab, NonNullList<ItemStack> nonnulllist) {
        if (this.a(creativemodetab)) {
            this.getBlock().a(creativemodetab, nonnulllist);
        }

    }

    public Block getBlock() {
        return this.a;
    }

    public void a(Map<Block, Item> map, Item item) {
        map.put(this.getBlock(), item);
    }
}
