package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ItemRecord extends Item {

    private static final Map<SoundEffect, ItemRecord> a = Maps.newHashMap();
    private static final List<ItemRecord> b = Lists.newArrayList();
    private final int c;
    private final SoundEffect d;

    protected ItemRecord(int i, SoundEffect soundeffect, Item.Info item_info) {
        super(item_info);
        this.c = i;
        this.d = soundeffect;
        ItemRecord.a.put(this.d, this);
        ItemRecord.b.add(this);
    }

    public static ItemRecord a(Random random) {
        return (ItemRecord) ItemRecord.b.get(random.nextInt(ItemRecord.b.size()));
    }

    public EnumInteractionResult a(ItemActionContext itemactioncontext) {
        World world = itemactioncontext.getWorld();
        BlockPosition blockposition = itemactioncontext.getClickPosition();
        IBlockData iblockdata = world.getType(blockposition);

        if (iblockdata.getBlock() == Blocks.JUKEBOX && !(Boolean) iblockdata.get(BlockJukeBox.HAS_RECORD)) {
            ItemStack itemstack = itemactioncontext.getItemStack();

            if (!world.isClientSide) {
                ((BlockJukeBox) Blocks.JUKEBOX).a((GeneratorAccess) world, blockposition, iblockdata, itemstack);
                world.a((EntityHuman) null, 1010, blockposition, Item.getId(this));
                itemstack.subtract(1);
                EntityHuman entityhuman = itemactioncontext.getEntity();

                if (entityhuman != null) {
                    entityhuman.a(StatisticList.PLAY_RECORD);
                }
            }

            return EnumInteractionResult.SUCCESS;
        } else {
            return EnumInteractionResult.PASS;
        }
    }

    public int d() {
        return this.c;
    }
}
