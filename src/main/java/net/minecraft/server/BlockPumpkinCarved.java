package net.minecraft.server;

import java.util.Iterator;
import java.util.function.Predicate;

// CraftBukkit start
import org.bukkit.craftbukkit.util.BlockStateListPopulator;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
// CraftBukkit end

public class BlockPumpkinCarved extends BlockFacingHorizontal {

    public static final BlockStateDirection a = BlockFacingHorizontal.FACING;
    private ShapeDetector b;
    private ShapeDetector c;
    private ShapeDetector o;
    private ShapeDetector p;
    private static final Predicate<IBlockData> q = (iblockdata) -> {
        return iblockdata != null && (iblockdata.getBlock() == Blocks.CARVED_PUMPKIN || iblockdata.getBlock() == Blocks.JACK_O_LANTERN);
    };

    protected BlockPumpkinCarved(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockPumpkinCarved.a, EnumDirection.NORTH));
    }

    public void onPlace(IBlockData iblockdata, World world, BlockPosition blockposition, IBlockData iblockdata1) {
        if (iblockdata1.getBlock() != iblockdata.getBlock()) {
            this.a(world, blockposition);
        }
    }

    public boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        return this.d().a(iworldreader, blockposition) != null || this.f().a(iworldreader, blockposition) != null;
    }

    private void a(World world, BlockPosition blockposition) {
        ShapeDetector.ShapeDetectorCollection shapedetector_shapedetectorcollection = this.e().a(world, blockposition);
        int i;
        Iterator iterator;
        EntityPlayer entityplayer;
        int j;
        ShapeDetectorBlock shapedetectorblock;
        int k;

        BlockStateListPopulator blockList = new BlockStateListPopulator(world); // CraftBukkit - Use BlockStateListPopulator
        if (shapedetector_shapedetectorcollection != null) {
            for (i = 0; i < this.e().b(); ++i) {
                ShapeDetectorBlock shapedetectorblock1 = shapedetector_shapedetectorcollection.a(0, i, 0);

                blockList.setTypeAndData(shapedetectorblock1.getPosition(), Blocks.AIR.getBlockData(), 2); // CraftBukkit
            }

            EntitySnowman entitysnowman = new EntitySnowman(world);
            BlockPosition blockposition1 = shapedetector_shapedetectorcollection.a(0, 2, 0).getPosition();

            entitysnowman.setPositionRotation((double) blockposition1.getX() + 0.5D, (double) blockposition1.getY() + 0.05D, (double) blockposition1.getZ() + 0.5D, 0.0F, 0.0F);
            // CraftBukkit start
            if (!world.addEntity(entitysnowman, SpawnReason.BUILD_SNOWMAN)) {
                return;
            }
            blockList.updateList();
            // CraftBukkit end
            iterator = world.a(EntityPlayer.class, entitysnowman.getBoundingBox().g(5.0D)).iterator();

            while (iterator.hasNext()) {
                entityplayer = (EntityPlayer) iterator.next();
                CriterionTriggers.n.a(entityplayer, (Entity) entitysnowman);
            }

            j = Block.getCombinedId(Blocks.SNOW_BLOCK.getBlockData());
            world.triggerEffect(2001, blockposition1, j);
            world.triggerEffect(2001, blockposition1.up(), j);

            for (k = 0; k < this.e().b(); ++k) {
                shapedetectorblock = shapedetector_shapedetectorcollection.a(0, k, 0);
                world.update(shapedetectorblock.getPosition(), Blocks.AIR);
            }
        } else {
            shapedetector_shapedetectorcollection = this.g().a(world, blockposition);
            if (shapedetector_shapedetectorcollection != null) {
                for (i = 0; i < this.g().c(); ++i) {
                    for (int l = 0; l < this.g().b(); ++l) {
                        blockList.setTypeAndData(shapedetector_shapedetectorcollection.a(i, l, 0).getPosition(), Blocks.AIR.getBlockData(), 2); // CraftBukkit
                    }
                }

                BlockPosition blockposition2 = shapedetector_shapedetectorcollection.a(1, 2, 0).getPosition();
                EntityIronGolem entityirongolem = new EntityIronGolem(world);

                entityirongolem.setPlayerCreated(true);
                entityirongolem.setPositionRotation((double) blockposition2.getX() + 0.5D, (double) blockposition2.getY() + 0.05D, (double) blockposition2.getZ() + 0.5D, 0.0F, 0.0F);
                // CraftBukkit start
                if (!world.addEntity(entityirongolem, SpawnReason.BUILD_IRONGOLEM)) {
                    return;
                }
                blockList.updateList();
                // CraftBukkit end
                iterator = world.a(EntityPlayer.class, entityirongolem.getBoundingBox().g(5.0D)).iterator();

                while (iterator.hasNext()) {
                    entityplayer = (EntityPlayer) iterator.next();
                    CriterionTriggers.n.a(entityplayer, (Entity) entityirongolem);
                }

                for (j = 0; j < 120; ++j) {
                    world.addParticle(Particles.E, (double) blockposition2.getX() + world.random.nextDouble(), (double) blockposition2.getY() + world.random.nextDouble() * 3.9D, (double) blockposition2.getZ() + world.random.nextDouble(), 0.0D, 0.0D, 0.0D);
                }

                for (j = 0; j < this.g().c(); ++j) {
                    for (k = 0; k < this.g().b(); ++k) {
                        shapedetectorblock = shapedetector_shapedetectorcollection.a(j, k, 0);
                        world.update(shapedetectorblock.getPosition(), Blocks.AIR);
                    }
                }
            }
        }

    }

    public IBlockData getPlacedState(BlockActionContext blockactioncontext) {
        return (IBlockData) this.getBlockData().set(BlockPumpkinCarved.a, blockactioncontext.f().opposite());
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockPumpkinCarved.a);
    }

    protected ShapeDetector d() {
        if (this.b == null) {
            this.b = ShapeDetectorBuilder.a().a(" ", "#", "#").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW_BLOCK))).b();
        }

        return this.b;
    }

    protected ShapeDetector e() {
        if (this.c == null) {
            this.c = ShapeDetectorBuilder.a().a("^", "#", "#").a('^', ShapeDetectorBlock.a(BlockPumpkinCarved.q)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.SNOW_BLOCK))).b();
        }

        return this.c;
    }

    protected ShapeDetector f() {
        if (this.o == null) {
            this.o = ShapeDetectorBuilder.a().a("~ ~", "###", "~#~").a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.o;
    }

    protected ShapeDetector g() {
        if (this.p == null) {
            this.p = ShapeDetectorBuilder.a().a("~^~", "###", "~#~").a('^', ShapeDetectorBlock.a(BlockPumpkinCarved.q)).a('#', ShapeDetectorBlock.a(BlockStatePredicate.a(Blocks.IRON_BLOCK))).a('~', ShapeDetectorBlock.a(MaterialPredicate.a(Material.AIR))).b();
        }

        return this.p;
    }
}
