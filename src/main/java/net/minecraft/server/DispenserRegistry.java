package net.minecraft.server;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DispenserRegistry {

    public static final PrintStream a = System.out;
    private static boolean b;
    private static final Logger c = LogManager.getLogger();

    public static boolean a() {
        return DispenserRegistry.b;
    }

    static void b() {
        BlockDispenser.a((IMaterial) Items.ARROW, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entitytippedarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        }));
        BlockDispenser.a((IMaterial) Items.TIPPED_ARROW, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entitytippedarrow.b(itemstack);
                entitytippedarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        }));
        BlockDispenser.a((IMaterial) Items.SPECTRAL_ARROW, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entityspectralarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entityspectralarrow;
            }
        }));
        BlockDispenser.a((IMaterial) Items.EGG, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return new EntityEgg(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }
        }));
        BlockDispenser.a((IMaterial) Items.SNOWBALL, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return new EntitySnowball(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }
        }));
        BlockDispenser.a((IMaterial) Items.EXPERIENCE_BOTTLE, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return new EntityThrownExpBottle(world, iposition.getX(), iposition.getY(), iposition.getZ());
            }

            protected float a() {
                return super.a() * 0.5F;
            }

            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        }));
        BlockDispenser.a((IMaterial) Items.SPLASH_POTION, new IDispenseBehavior() {
            public ItemStack dispense(ISourceBlock isourceblock, final ItemStack itemstack) {
                return (new DispenseBehaviorProjectile() {
                    protected IProjectile a(World world, IPosition iposition, ItemStack itemstack1) {
                        return new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ(), itemstack.cloneItemStack());
                    }

                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).dispense(isourceblock, itemstack);
            }
        });
        BlockDispenser.a((IMaterial) Items.LINGERING_POTION, new IDispenseBehavior() {
            public ItemStack dispense(ISourceBlock isourceblock, final ItemStack itemstack) {
                return (new DispenseBehaviorProjectile() {
                    protected IProjectile a(World world, IPosition iposition, ItemStack itemstack1) {
                        return new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ(), itemstack.cloneItemStack());
                    }

                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).dispense(isourceblock, itemstack);
            }
        });
        DispenseBehaviorItem dispensebehavioritem = new DispenseBehaviorItem() {
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                EntityTypes<?> entitytypes = ((ItemMonsterEgg) itemstack.getItem()).b(itemstack.getTag());

                if (entitytypes != null) {
                    entitytypes.a(isourceblock.getWorld(), itemstack, (EntityHuman) null, isourceblock.getBlockPosition().shift(enumdirection), enumdirection != EnumDirection.UP, false);
                }

                itemstack.subtract(1);
                return itemstack;
            }
        };
        Iterator iterator = ItemMonsterEgg.d().iterator();

        while (iterator.hasNext()) {
            ItemMonsterEgg itemmonsteregg = (ItemMonsterEgg) iterator.next();

            BlockDispenser.a((IMaterial) itemmonsteregg, (IDispenseBehavior) dispensebehavioritem);
        }

        BlockDispenser.a((IMaterial) Items.FIREWORK_ROCKET, (IDispenseBehavior) (new DispenseBehaviorItem() {
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                double d0 = isourceblock.getX() + (double) enumdirection.getAdjacentX();
                double d1 = (double) ((float) isourceblock.getBlockPosition().getY() + 0.2F);
                double d2 = isourceblock.getZ() + (double) enumdirection.getAdjacentZ();
                EntityFireworks entityfireworks = new EntityFireworks(isourceblock.getWorld(), d0, d1, d2, itemstack);

                isourceblock.getWorld().addEntity(entityfireworks);
                itemstack.subtract(1);
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1004, isourceblock.getBlockPosition(), 0);
            }
        }));
        BlockDispenser.a((IMaterial) Items.FIRE_CHARGE, (IDispenseBehavior) (new DispenseBehaviorItem() {
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                IPosition iposition = BlockDispenser.a(isourceblock);
                double d0 = iposition.getX() + (double) ((float) enumdirection.getAdjacentX() * 0.3F);
                double d1 = iposition.getY() + (double) ((float) enumdirection.getAdjacentY() * 0.3F);
                double d2 = iposition.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 0.3F);
                World world = isourceblock.getWorld();
                Random random = world.random;
                double d3 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentX();
                double d4 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentY();
                double d5 = random.nextGaussian() * 0.05D + (double) enumdirection.getAdjacentZ();

                world.addEntity(new EntitySmallFireball(world, d0, d1, d2, d3, d4, d5));
                itemstack.subtract(1);
                return itemstack;
            }

            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1018, isourceblock.getBlockPosition(), 0);
            }
        }));
        BlockDispenser.a((IMaterial) Items.OAK_BOAT, (IDispenseBehavior) (new DispenserRegistry.a(EntityBoat.EnumBoatType.OAK)));
        BlockDispenser.a((IMaterial) Items.SPRUCE_BOAT, (IDispenseBehavior) (new DispenserRegistry.a(EntityBoat.EnumBoatType.SPRUCE)));
        BlockDispenser.a((IMaterial) Items.BIRCH_BOAT, (IDispenseBehavior) (new DispenserRegistry.a(EntityBoat.EnumBoatType.BIRCH)));
        BlockDispenser.a((IMaterial) Items.JUNGLE_BOAT, (IDispenseBehavior) (new DispenserRegistry.a(EntityBoat.EnumBoatType.JUNGLE)));
        BlockDispenser.a((IMaterial) Items.DARK_OAK_BOAT, (IDispenseBehavior) (new DispenserRegistry.a(EntityBoat.EnumBoatType.DARK_OAK)));
        BlockDispenser.a((IMaterial) Items.ACACIA_BOAT, (IDispenseBehavior) (new DispenserRegistry.a(EntityBoat.EnumBoatType.ACACIA)));
        DispenseBehaviorItem dispensebehavioritem1 = new DispenseBehaviorItem() {
            private final DispenseBehaviorItem a = new DispenseBehaviorItem();

            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                ItemBucket itembucket = (ItemBucket) itemstack.getItem();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                World world = isourceblock.getWorld();

                if (itembucket.a((EntityHuman) null, world, blockposition, (MovingObjectPosition) null)) {
                    itembucket.a(world, itemstack, blockposition);
                    return new ItemStack(Items.BUCKET);
                } else {
                    return this.a.dispense(isourceblock, itemstack);
                }
            }
        };

        BlockDispenser.a((IMaterial) Items.LAVA_BUCKET, (IDispenseBehavior) dispensebehavioritem1);
        BlockDispenser.a((IMaterial) Items.WATER_BUCKET, (IDispenseBehavior) dispensebehavioritem1);
        BlockDispenser.a((IMaterial) Items.SALMON_BUCKET, (IDispenseBehavior) dispensebehavioritem1);
        BlockDispenser.a((IMaterial) Items.COD_BUCKET, (IDispenseBehavior) dispensebehavioritem1);
        BlockDispenser.a((IMaterial) Items.PUFFERFISH_BUCKET, (IDispenseBehavior) dispensebehavioritem1);
        BlockDispenser.a((IMaterial) Items.TROPICAL_FISH_BUCKET, (IDispenseBehavior) dispensebehavioritem1);
        BlockDispenser.a((IMaterial) Items.BUCKET, (IDispenseBehavior) (new DispenseBehaviorItem() {
            private final DispenseBehaviorItem a = new DispenseBehaviorItem();

            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                IBlockData iblockdata = world.getType(blockposition);
                Block block = iblockdata.getBlock();

                if (block instanceof IFluidSource) {
                    FluidType fluidtype = ((IFluidSource) block).removeFluid(world, blockposition, iblockdata);

                    if (!(fluidtype instanceof FluidTypeFlowing)) {
                        return super.a(isourceblock, itemstack);
                    } else {
                        Item item = fluidtype.b();

                        itemstack.subtract(1);
                        if (itemstack.isEmpty()) {
                            return new ItemStack(item);
                        } else {
                            if (((TileEntityDispenser) isourceblock.getTileEntity()).addItem(new ItemStack(item)) < 0) {
                                this.a.dispense(isourceblock, new ItemStack(item));
                            }

                            return itemstack;
                        }
                    }
                } else {
                    return super.a(isourceblock, itemstack);
                }
            }
        }));
        BlockDispenser.a((IMaterial) Items.FLINT_AND_STEEL, (IDispenseBehavior) (new DispenserRegistry.c() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();

                this.a = true;
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));

                if (ItemFlintAndSteel.a((GeneratorAccess) world, blockposition)) {
                    world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                } else {
                    Block block = world.getType(blockposition).getBlock();

                    if (block instanceof BlockTNT) {
                        ((BlockTNT) block).a(world, blockposition);
                        world.setAir(blockposition);
                    } else {
                        this.a = false;
                    }
                }

                if (this.a && itemstack.isDamaged(1, world.random, (EntityPlayer) null)) {
                    itemstack.setCount(0);
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Items.BONE_MEAL, (IDispenseBehavior) (new DispenserRegistry.c() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                this.a = true;
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));

                if (!ItemBoneMeal.a(itemstack, world, blockposition) && !ItemBoneMeal.a(itemstack, world, blockposition, (EnumDirection) null)) {
                    this.a = false;
                } else if (!world.isClientSide) {
                    world.triggerEffect(2005, blockposition, 0);
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Blocks.TNT, (IDispenseBehavior) (new DispenseBehaviorItem() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, (EntityLiving) null);

                world.addEntity(entitytntprimed);
                world.a((EntityHuman) null, entitytntprimed.locX, entitytntprimed.locY, entitytntprimed.locZ, SoundEffects.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                itemstack.subtract(1);
                return itemstack;
            }
        }));
        DispenserRegistry.c dispenserregistry_c = new DispenserRegistry.c() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                this.a = !ItemArmor.a(isourceblock, itemstack).isEmpty();
                return itemstack;
            }
        };

        BlockDispenser.a((IMaterial) Items.CREEPER_HEAD, (IDispenseBehavior) dispenserregistry_c);
        BlockDispenser.a((IMaterial) Items.ZOMBIE_HEAD, (IDispenseBehavior) dispenserregistry_c);
        BlockDispenser.a((IMaterial) Items.DRAGON_HEAD, (IDispenseBehavior) dispenserregistry_c);
        BlockDispenser.a((IMaterial) Items.SKELETON_SKULL, (IDispenseBehavior) dispenserregistry_c);
        BlockDispenser.a((IMaterial) Items.PLAYER_HEAD, (IDispenseBehavior) dispenserregistry_c);
        BlockDispenser.a((IMaterial) Items.WITHER_SKELETON_SKULL, (IDispenseBehavior) (new DispenserRegistry.c() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);

                this.a = true;
                if (world.isEmpty(blockposition) && BlockWitherSkull.b(world, blockposition, itemstack)) {
                    world.setTypeAndData(blockposition, (IBlockData) Blocks.WITHER_SKELETON_SKULL.getBlockData().set(BlockSkull.a, enumdirection.k() == EnumDirection.EnumAxis.Y ? 0 : enumdirection.opposite().get2DRotationValue() * 4), 3);
                    TileEntity tileentity = world.getTileEntity(blockposition);

                    if (tileentity instanceof TileEntitySkull) {
                        BlockWitherSkull.a(world, blockposition, (TileEntitySkull) tileentity);
                    }

                    itemstack.subtract(1);
                } else if (ItemArmor.a(isourceblock, itemstack).isEmpty()) {
                    this.a = false;
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Blocks.CARVED_PUMPKIN, (IDispenseBehavior) (new DispenserRegistry.c() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                BlockPumpkinCarved blockpumpkincarved = (BlockPumpkinCarved) Blocks.CARVED_PUMPKIN;

                this.a = true;
                if (world.isEmpty(blockposition) && blockpumpkincarved.a((IWorldReader) world, blockposition)) {
                    if (!world.isClientSide) {
                        world.setTypeAndData(blockposition, blockpumpkincarved.getBlockData(), 3);
                    }

                    itemstack.subtract(1);
                } else {
                    ItemStack itemstack1 = ItemArmor.a(isourceblock, itemstack);

                    if (itemstack1.isEmpty()) {
                        this.a = false;
                    }
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Blocks.SHULKER_BOX.getItem(), (IDispenseBehavior) (new DispenserRegistry.d()));
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            BlockDispenser.a((IMaterial) BlockShulkerBox.a(enumcolor).getItem(), (IDispenseBehavior) (new DispenserRegistry.d()));
        }

    }

    public static void c() {
        if (!DispenserRegistry.b) {
            DispenserRegistry.b = true;
            SoundEffect.b();
            FluidType.l();
            Block.t();
            BlockFire.d();
            MobEffectList.m();
            Enchantment.h();
            if (EntityTypes.getName(EntityTypes.PLAYER) == null) {
                throw new IllegalStateException("Failed loading EntityTypes");
            } else {
                Item.r();
                PotionRegistry.b();
                PotionBrewer.a();
                BiomeBase.t();
                PlayerSelector.a();
                Particle.c();
                b();
                ArgumentRegistry.a();
                BiomeLayout.a();
                TileEntityTypes.a();
                ChunkGeneratorType.a();
                DimensionManager.a();
                Paintings.a();
                StatisticList.a();
                IRegistry.e();
                if (SharedConstants.b) {
                    a("block", IRegistry.BLOCK, Block::m);
                    a("biome", IRegistry.BIOME, BiomeBase::k);
                    a("enchantment", IRegistry.ENCHANTMENT, Enchantment::g);
                    a("item", IRegistry.ITEM, Item::getName);
                    a("effect", IRegistry.MOB_EFFECT, MobEffectList::c);
                    a("entity", IRegistry.ENTITY_TYPE, EntityTypes::d);
                }

                d();
            }
        }
    }

    private static <T> void a(String s, IRegistry<T> iregistry, Function<T, String> function) {
        LocaleLanguage localelanguage = LocaleLanguage.a();

        iregistry.iterator().forEachRemaining((object) -> {
            String s1 = (String) function.apply(object);

            if (!localelanguage.b(s1)) {
                DispenserRegistry.c.warn("Missing translation for {}: {} (key: '{}')", s, iregistry.getKey(object), s1);
            }

        });
    }

    private static void d() {
        if (DispenserRegistry.c.isDebugEnabled()) {
            System.setErr(new DebugOutputStream("STDERR", System.err));
            System.setOut(new DebugOutputStream("STDOUT", DispenserRegistry.a));
        } else {
            System.setErr(new RedirectStream("STDERR", System.err));
            System.setOut(new RedirectStream("STDOUT", DispenserRegistry.a));
        }

    }

    static class b extends BlockActionContext {

        private final EnumDirection j;

        public b(World world, BlockPosition blockposition, EnumDirection enumdirection, ItemStack itemstack, EnumDirection enumdirection1) {
            super(world, (EntityHuman) null, itemstack, blockposition, enumdirection1, 0.5F, 0.0F, 0.5F);
            this.j = enumdirection;
        }

        public BlockPosition getClickPosition() {
            return this.i;
        }

        public boolean b() {
            return this.g.getType(this.i).a((BlockActionContext) this);
        }

        public boolean c() {
            return this.b();
        }

        public EnumDirection d() {
            return EnumDirection.DOWN;
        }

        public EnumDirection[] e() {
            switch (this.j) {
            case DOWN:
            default:
                return new EnumDirection[] { EnumDirection.DOWN, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST, EnumDirection.UP};
            case UP:
                return new EnumDirection[] { EnumDirection.DOWN, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.WEST};
            case NORTH:
                return new EnumDirection[] { EnumDirection.DOWN, EnumDirection.NORTH, EnumDirection.EAST, EnumDirection.WEST, EnumDirection.UP, EnumDirection.SOUTH};
            case SOUTH:
                return new EnumDirection[] { EnumDirection.DOWN, EnumDirection.SOUTH, EnumDirection.EAST, EnumDirection.WEST, EnumDirection.UP, EnumDirection.NORTH};
            case WEST:
                return new EnumDirection[] { EnumDirection.DOWN, EnumDirection.WEST, EnumDirection.SOUTH, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.EAST};
            case EAST:
                return new EnumDirection[] { EnumDirection.DOWN, EnumDirection.EAST, EnumDirection.SOUTH, EnumDirection.UP, EnumDirection.NORTH, EnumDirection.WEST};
            }
        }

        public EnumDirection f() {
            return this.j.k() == EnumDirection.EnumAxis.Y ? EnumDirection.NORTH : this.j;
        }

        public boolean isSneaking() {
            return false;
        }

        public float h() {
            return (float) (this.j.get2DRotationValue() * 90);
        }
    }

    static class d extends DispenserRegistry.c {

        private d() {}

        protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
            this.a = false;
            Item item = itemstack.getItem();

            if (item instanceof ItemBlock) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
                EnumDirection enumdirection1 = isourceblock.getWorld().isEmpty(blockposition.down()) ? enumdirection : EnumDirection.UP;

                this.a = ((ItemBlock) item).a((BlockActionContext) (new DispenserRegistry.b(isourceblock.getWorld(), blockposition, enumdirection, itemstack, enumdirection1))) == EnumInteractionResult.SUCCESS;
                if (this.a) {
                    itemstack.subtract(1);
                }
            }

            return itemstack;
        }
    }

    public abstract static class c extends DispenseBehaviorItem {

        protected boolean a = true;

        public c() {}

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(this.a ? 1000 : 1001, isourceblock.getBlockPosition(), 0);
        }
    }

    public static class a extends DispenseBehaviorItem {

        private final DispenseBehaviorItem a = new DispenseBehaviorItem();
        private final EntityBoat.EnumBoatType b;

        public a(EntityBoat.EnumBoatType entityboat_enumboattype) {
            this.b = entityboat_enumboattype;
        }

        public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
            EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
            World world = isourceblock.getWorld();
            double d0 = isourceblock.getX() + (double) ((float) enumdirection.getAdjacentX() * 1.125F);
            double d1 = isourceblock.getY() + (double) ((float) enumdirection.getAdjacentY() * 1.125F);
            double d2 = isourceblock.getZ() + (double) ((float) enumdirection.getAdjacentZ() * 1.125F);
            BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
            double d3;

            if (world.getFluid(blockposition).a(TagsFluid.WATER)) {
                d3 = 1.0D;
            } else {
                if (!world.getType(blockposition).isAir() || !world.getFluid(blockposition.down()).a(TagsFluid.WATER)) {
                    return this.a.dispense(isourceblock, itemstack);
                }

                d3 = 0.0D;
            }

            EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);

            entityboat.setType(this.b);
            entityboat.yaw = enumdirection.l();
            world.addEntity(entityboat);
            itemstack.subtract(1);
            return itemstack;
        }

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
        }
    }
}
