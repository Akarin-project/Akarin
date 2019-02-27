package net.minecraft.server;

import javax.annotation.Nullable;

public class ItemBucket extends Item {

    public final FluidType fluidType;

    public ItemBucket(FluidType fluidtype, Item.Info item_info) {
        super(item_info);
        this.fluidType = fluidtype;
    }

    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);
        MovingObjectPosition movingobjectposition = this.a(world, entityhuman, this.fluidType == FluidTypes.EMPTY);

        if (movingobjectposition == null) {
            return new InteractionResultWrapper<>(EnumInteractionResult.PASS, itemstack);
        } else if (movingobjectposition.type == MovingObjectPosition.EnumMovingObjectType.BLOCK) {
            BlockPosition blockposition = movingobjectposition.getBlockPosition();

            if (world.a(entityhuman, blockposition) && entityhuman.a(blockposition, movingobjectposition.direction, itemstack)) {
                IBlockData iblockdata;

                if (this.fluidType == FluidTypes.EMPTY) {
                    iblockdata = world.getType(blockposition);
                    if (iblockdata.getBlock() instanceof IFluidSource) {
                        FluidType fluidtype = ((IFluidSource) iblockdata.getBlock()).removeFluid(world, blockposition, iblockdata);

                        if (fluidtype != FluidTypes.EMPTY) {
                            entityhuman.b(StatisticList.ITEM_USED.b(this));
                            entityhuman.a(fluidtype.a(TagsFluid.LAVA) ? SoundEffects.ITEM_BUCKET_FILL_LAVA : SoundEffects.ITEM_BUCKET_FILL, 1.0F, 1.0F);
                            ItemStack itemstack1 = this.a(itemstack, entityhuman, fluidtype.b());

                            if (!world.isClientSide) {
                                CriterionTriggers.j.a((EntityPlayer) entityhuman, new ItemStack(fluidtype.b()));
                            }

                            return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack1);
                        }
                    }

                    return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
                } else {
                    iblockdata = world.getType(blockposition);
                    BlockPosition blockposition1 = this.a(iblockdata, blockposition, movingobjectposition);

                    if (this.a(entityhuman, world, blockposition1, movingobjectposition)) {
                        this.a(world, itemstack, blockposition1);
                        if (entityhuman instanceof EntityPlayer) {
                            CriterionTriggers.y.a((EntityPlayer) entityhuman, blockposition1, itemstack);
                        }

                        entityhuman.b(StatisticList.ITEM_USED.b(this));
                        return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, this.a(itemstack, entityhuman));
                    } else {
                        return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
                    }
                }
            } else {
                return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
            }
        } else {
            return new InteractionResultWrapper<>(EnumInteractionResult.PASS, itemstack);
        }
    }

    private BlockPosition a(IBlockData iblockdata, BlockPosition blockposition, MovingObjectPosition movingobjectposition) {
        return iblockdata.getBlock() instanceof IFluidContainer ? blockposition : movingobjectposition.getBlockPosition().shift(movingobjectposition.direction);
    }

    protected ItemStack a(ItemStack itemstack, EntityHuman entityhuman) {
        return !entityhuman.abilities.canInstantlyBuild ? new ItemStack(Items.BUCKET) : itemstack;
    }

    public void a(World world, ItemStack itemstack, BlockPosition blockposition) {}

    private ItemStack a(ItemStack itemstack, EntityHuman entityhuman, Item item) {
        if (entityhuman.abilities.canInstantlyBuild) {
            return itemstack;
        } else {
            itemstack.subtract(1);
            if (itemstack.isEmpty()) {
                return new ItemStack(item);
            } else {
                if (!entityhuman.inventory.pickup(new ItemStack(item))) {
                    entityhuman.drop(new ItemStack(item), false);
                }

                return itemstack;
            }
        }
    }

    public boolean a(@Nullable EntityHuman entityhuman, World world, BlockPosition blockposition, @Nullable MovingObjectPosition movingobjectposition) {
        if (!(this.fluidType instanceof FluidTypeFlowing)) {
            return false;
        } else {
            IBlockData iblockdata = world.getType(blockposition);
            Material material = iblockdata.getMaterial();
            boolean flag = !material.isBuildable();
            boolean flag1 = material.isReplaceable();

            if (!world.isEmpty(blockposition) && !flag && !flag1 && (!(iblockdata.getBlock() instanceof IFluidContainer) || !((IFluidContainer) iblockdata.getBlock()).canPlace(world, blockposition, iblockdata, this.fluidType))) {
                return movingobjectposition == null ? false : this.a(entityhuman, world, movingobjectposition.getBlockPosition().shift(movingobjectposition.direction), (MovingObjectPosition) null);
            } else {
                if (world.worldProvider.isNether() && this.fluidType.a(TagsFluid.WATER)) {
                    int i = blockposition.getX();
                    int j = blockposition.getY();
                    int k = blockposition.getZ();

                    world.a(entityhuman, blockposition, SoundEffects.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.random.nextFloat() - world.random.nextFloat()) * 0.8F);

                    for (int l = 0; l < 8; ++l) {
                        world.addParticle(Particles.F, (double) i + Math.random(), (double) j + Math.random(), (double) k + Math.random(), 0.0D, 0.0D, 0.0D);
                    }
                } else if (iblockdata.getBlock() instanceof IFluidContainer) {
                    if (((IFluidContainer) iblockdata.getBlock()).place(world, blockposition, iblockdata, ((FluidTypeFlowing) this.fluidType).a(false))) {
                        this.a(entityhuman, (GeneratorAccess) world, blockposition);
                    }
                } else {
                    if (!world.isClientSide && (flag || flag1) && !material.isLiquid()) {
                        world.setAir(blockposition, true);
                    }

                    this.a(entityhuman, (GeneratorAccess) world, blockposition);
                    world.setTypeAndData(blockposition, this.fluidType.i().i(), 11);
                }

                return true;
            }
        }
    }

    protected void a(@Nullable EntityHuman entityhuman, GeneratorAccess generatoraccess, BlockPosition blockposition) {
        SoundEffect soundeffect = this.fluidType.a(TagsFluid.LAVA) ? SoundEffects.ITEM_BUCKET_EMPTY_LAVA : SoundEffects.ITEM_BUCKET_EMPTY;

        generatoraccess.a(entityhuman, blockposition, soundeffect, SoundCategory.BLOCKS, 1.0F, 1.0F);
    }
}
