package net.minecraft.server;

public class BlockCauldron extends Block {

    public static final BlockStateInteger LEVEL = BlockProperties.af;
    protected static final VoxelShape b = Block.a(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    protected static final VoxelShape c = VoxelShapes.a(VoxelShapes.b(), BlockCauldron.b, OperatorBoolean.ONLY_FIRST);

    public BlockCauldron(Block.Info block_info) {
        super(block_info);
        this.v((IBlockData) ((IBlockData) this.blockStateList.getBlockData()).set(BlockCauldron.LEVEL, 0));
    }

    public VoxelShape a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCauldron.c;
    }

    public boolean f(IBlockData iblockdata) {
        return false;
    }

    public VoxelShape h(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition) {
        return BlockCauldron.b;
    }

    public boolean a(IBlockData iblockdata) {
        return false;
    }

    public void a(IBlockData iblockdata, World world, BlockPosition blockposition, Entity entity) {
        int i = (Integer) iblockdata.get(BlockCauldron.LEVEL);
        float f = (float) blockposition.getY() + (6.0F + (float) (3 * i)) / 16.0F;

        if (!world.isClientSide && entity.isBurning() && i > 0 && entity.getBoundingBox().minY <= (double) f) {
            entity.extinguish();
            this.a(world, blockposition, iblockdata, i - 1);
        }

    }

    public boolean interact(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman, EnumHand enumhand, EnumDirection enumdirection, float f, float f1, float f2) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.isEmpty()) {
            return true;
        } else {
            int i = (Integer) iblockdata.get(BlockCauldron.LEVEL);
            Item item = itemstack.getItem();

            if (item == Items.WATER_BUCKET) {
                if (i < 3 && !world.isClientSide) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        entityhuman.a(enumhand, new ItemStack(Items.BUCKET));
                    }

                    entityhuman.a(StatisticList.FILL_CAULDRON);
                    this.a(world, blockposition, iblockdata, 3);
                    world.a((EntityHuman) null, blockposition, SoundEffects.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return true;
            } else if (item == Items.BUCKET) {
                if (i == 3 && !world.isClientSide) {
                    if (!entityhuman.abilities.canInstantlyBuild) {
                        itemstack.subtract(1);
                        if (itemstack.isEmpty()) {
                            entityhuman.a(enumhand, new ItemStack(Items.WATER_BUCKET));
                        } else if (!entityhuman.inventory.pickup(new ItemStack(Items.WATER_BUCKET))) {
                            entityhuman.drop(new ItemStack(Items.WATER_BUCKET), false);
                        }
                    }

                    entityhuman.a(StatisticList.USE_CAULDRON);
                    this.a(world, blockposition, iblockdata, 0);
                    world.a((EntityHuman) null, blockposition, SoundEffects.ITEM_BUCKET_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                }

                return true;
            } else {
                ItemStack itemstack1;

                if (item == Items.GLASS_BOTTLE) {
                    if (i > 0 && !world.isClientSide) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack1 = PotionUtil.a(new ItemStack(Items.POTION), Potions.b);
                            entityhuman.a(StatisticList.USE_CAULDRON);
                            itemstack.subtract(1);
                            if (itemstack.isEmpty()) {
                                entityhuman.a(enumhand, itemstack1);
                            } else if (!entityhuman.inventory.pickup(itemstack1)) {
                                entityhuman.drop(itemstack1, false);
                            } else if (entityhuman instanceof EntityPlayer) {
                                ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                            }
                        }

                        world.a((EntityHuman) null, blockposition, SoundEffects.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.a(world, blockposition, iblockdata, i - 1);
                    }

                    return true;
                } else if (item == Items.POTION && PotionUtil.d(itemstack) == Potions.b) {
                    if (i < 3 && !world.isClientSide) {
                        if (!entityhuman.abilities.canInstantlyBuild) {
                            itemstack1 = new ItemStack(Items.GLASS_BOTTLE);
                            entityhuman.a(StatisticList.USE_CAULDRON);
                            entityhuman.a(enumhand, itemstack1);
                            if (entityhuman instanceof EntityPlayer) {
                                ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                            }
                        }

                        world.a((EntityHuman) null, blockposition, SoundEffects.ITEM_BOTTLE_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                        this.a(world, blockposition, iblockdata, i + 1);
                    }

                    return true;
                } else {
                    if (i > 0 && item instanceof ItemArmorColorable) {
                        ItemArmorColorable itemarmorcolorable = (ItemArmorColorable) item;

                        if (itemarmorcolorable.e(itemstack) && !world.isClientSide) {
                            itemarmorcolorable.g(itemstack);
                            this.a(world, blockposition, iblockdata, i - 1);
                            entityhuman.a(StatisticList.CLEAN_ARMOR);
                            return true;
                        }
                    }

                    if (i > 0 && item instanceof ItemBanner) {
                        if (TileEntityBanner.a(itemstack) > 0 && !world.isClientSide) {
                            itemstack1 = itemstack.cloneItemStack();
                            itemstack1.setCount(1);
                            TileEntityBanner.b(itemstack1);
                            entityhuman.a(StatisticList.CLEAN_BANNER);
                            if (!entityhuman.abilities.canInstantlyBuild) {
                                itemstack.subtract(1);
                                this.a(world, blockposition, iblockdata, i - 1);
                            }

                            if (itemstack.isEmpty()) {
                                entityhuman.a(enumhand, itemstack1);
                            } else if (!entityhuman.inventory.pickup(itemstack1)) {
                                entityhuman.drop(itemstack1, false);
                            } else if (entityhuman instanceof EntityPlayer) {
                                ((EntityPlayer) entityhuman).updateInventory(entityhuman.defaultContainer);
                            }
                        }

                        return true;
                    } else if (i > 0 && item instanceof ItemBlock) {
                        Block block = ((ItemBlock) item).getBlock();

                        if (block instanceof BlockShulkerBox && !world.e()) {
                            ItemStack itemstack2 = new ItemStack(Blocks.SHULKER_BOX, 1);

                            if (itemstack.hasTag()) {
                                itemstack2.setTag(itemstack.getTag().clone());
                            }

                            entityhuman.a(enumhand, itemstack2);
                            this.a(world, blockposition, iblockdata, i - 1);
                            entityhuman.a(StatisticList.CLEAN_SHULKER_BOX);
                        }

                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
    }

    public void a(World world, BlockPosition blockposition, IBlockData iblockdata, int i) {
        world.setTypeAndData(blockposition, (IBlockData) iblockdata.set(BlockCauldron.LEVEL, MathHelper.clamp(i, 0, 3)), 2);
        world.updateAdjacentComparators(blockposition, this);
    }

    public void c(World world, BlockPosition blockposition) {
        if (world.random.nextInt(20) == 1) {
            float f = world.getBiome(blockposition).getAdjustedTemperature(blockposition);

            if (f >= 0.15F) {
                IBlockData iblockdata = world.getType(blockposition);

                if ((Integer) iblockdata.get(BlockCauldron.LEVEL) < 3) {
                    world.setTypeAndData(blockposition, (IBlockData) iblockdata.a((IBlockState) BlockCauldron.LEVEL), 2);
                }

            }
        }
    }

    public boolean isComplexRedstone(IBlockData iblockdata) {
        return true;
    }

    public int a(IBlockData iblockdata, World world, BlockPosition blockposition) {
        return (Integer) iblockdata.get(BlockCauldron.LEVEL);
    }

    protected void a(BlockStateList.a<Block, IBlockData> blockstatelist_a) {
        blockstatelist_a.a(BlockCauldron.LEVEL);
    }

    public EnumBlockFaceShape a(IBlockAccess iblockaccess, IBlockData iblockdata, BlockPosition blockposition, EnumDirection enumdirection) {
        return enumdirection == EnumDirection.UP ? EnumBlockFaceShape.BOWL : (enumdirection == EnumDirection.DOWN ? EnumBlockFaceShape.UNDEFINED : EnumBlockFaceShape.SOLID);
    }

    public boolean a(IBlockData iblockdata, IBlockAccess iblockaccess, BlockPosition blockposition, PathMode pathmode) {
        return false;
    }
}
