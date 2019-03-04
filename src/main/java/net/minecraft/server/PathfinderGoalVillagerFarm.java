package net.minecraft.server;

public class PathfinderGoalVillagerFarm extends PathfinderGoalGotoTarget {

    private final EntityVillager f;
    private boolean g;
    private boolean h;
    private int i;

    public PathfinderGoalVillagerFarm(EntityVillager entityvillager, double d0) {
        super(entityvillager, d0, 16);
        this.f = entityvillager;
    }

    public boolean a() {
        if (this.b <= 0) {
            if (!this.f.world.getGameRules().getBoolean("mobGriefing")) {
                return false;
            }

            this.i = -1;
            this.g = this.f.dH();
            this.h = this.f.dG();
        }

        return super.a();
    }

    public boolean b() {
        return this.i >= 0 && super.b();
    }

    public void e() {
        super.e();
        this.f.getControllerLook().a((double) this.d.getX() + 0.5D, (double) (this.d.getY() + 1), (double) this.d.getZ() + 0.5D, 10.0F, (float) this.f.K());
        if (this.k()) {
            World world = this.f.world;
            BlockPosition blockposition = this.d.up();
            IBlockData iblockdata = world.getType(blockposition);
            Block block = iblockdata.getBlock();

            if (this.i == 0 && block instanceof BlockCrops && ((BlockCrops) block).w(iblockdata)) {
                // CraftBukkit start
                if (!org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.f, blockposition, Blocks.AIR.getBlockData()).isCancelled()) {
                    world.setAir(blockposition, true);
                }
                // CraftBukkit end
            } else if (this.i == 1 && iblockdata.isAir()) {
                InventorySubcontainer inventorysubcontainer = this.f.dD();

                for (int i = 0; i < inventorysubcontainer.getSize(); ++i) {
                    ItemStack itemstack = inventorysubcontainer.getItem(i);
                    boolean flag = false;

                    if (!itemstack.isEmpty()) {
                        // CraftBukkit start
                        Block planted = null;
                        if (itemstack.getItem() == Items.WHEAT_SEEDS) {
                            planted = Blocks.WHEAT;
                            flag = true;
                        } else if (itemstack.getItem() == Items.POTATO) {
                            planted = Blocks.POTATOES;
                            flag = true;
                        } else if (itemstack.getItem() == Items.CARROT) {
                            planted = Blocks.CARROTS;
                            flag = true;
                        } else if (itemstack.getItem() == Items.BEETROOT_SEEDS) {
                            planted = Blocks.BEETROOTS;
                            flag = true;
                        }

                        if (planted != null && !org.bukkit.craftbukkit.event.CraftEventFactory.callEntityChangeBlockEvent(this.f, blockposition, planted.getBlockData()).isCancelled()) {
                            world.setTypeAndData(blockposition, planted.getBlockData(), 3);
                        } else {
                            flag = false;
                        }
                        // CraftBukkit end
                    }

                    if (flag) {
                        itemstack.subtract(1);
                        if (itemstack.isEmpty()) {
                            inventorysubcontainer.setItem(i, ItemStack.a);
                        }
                        break;
                    }
                }
            }

            this.i = -1;
            this.b = 10;
        }

    }

    protected boolean a(IWorldReader iworldreader, BlockPosition blockposition) {
        Block block = iworldreader.getType(blockposition).getBlock();

        if (block == Blocks.FARMLAND) {
            blockposition = blockposition.up();
            IBlockData iblockdata = iworldreader.getType(blockposition);

            block = iblockdata.getBlock();
            if (block instanceof BlockCrops && ((BlockCrops) block).w(iblockdata) && this.h && (this.i == 0 || this.i < 0)) {
                this.i = 0;
                return true;
            }

            if (iblockdata.isAir() && this.g && (this.i == 1 || this.i < 0)) {
                this.i = 1;
                return true;
            }
        }

        return false;
    }
}
