package net.minecraft.server;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.Random;
import java.util.function.Function;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// CraftBukkit start
import java.util.List;
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.DummyGeneratorAccess;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.world.StructureGrowEvent;
// CraftBukkit end

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

                // CraftBukkit start
                World world = isourceblock.getWorld();
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.add(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.add(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                if (entitytypes != null) {
                    entitytypes.a(isourceblock.getWorld(), itemstack, (EntityHuman) null, isourceblock.getBlockPosition().shift(enumdirection), enumdirection != EnumDirection.UP, false);
                }

                // itemstack.subtract(1); // Handled during event processing
                // CraftBukkit end
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
                // CraftBukkit start
                World world = isourceblock.getWorld();
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1, d2));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.add(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.add(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                itemstack1 = CraftItemStack.asNMSCopy(event.getItem());
                EntityFireworks entityfireworks = new EntityFireworks(isourceblock.getWorld(), event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), itemstack1);

                isourceblock.getWorld().addEntity(entityfireworks);
                // itemstack.subtract(1); // Handled during event processing
                // CraftBukkit end
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

                // CraftBukkit start
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d3, d4, d5));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.add(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.add(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                EntitySmallFireball fireball = new EntitySmallFireball(world, d0, d1, d2, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
                fireball.projectileSource = new org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource((TileEntityDispenser) isourceblock.getTileEntity());

                world.addEntity(fireball);
                // itemstack.subtract(1); // Handled during event processing
                // CraftBukkit end
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

                // CraftBukkit start
                int x = blockposition.getX();
                int y = blockposition.getY();
                int z = blockposition.getZ();
                IBlockData iblockdata = world.getType(blockposition);
                Material material = iblockdata.getMaterial();
                if (world.isEmpty(blockposition) || !material.isBuildable() || material.isReplaceable() || ((iblockdata.getBlock() instanceof IFluidContainer) && ((IFluidContainer) iblockdata.getBlock()).canPlace(world, blockposition, iblockdata, itembucket.fluidType))) {
                    org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                    CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                    BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(x, y, z));
                    if (!BlockDispenser.eventFired) {
                        world.getServer().getPluginManager().callEvent(event);
                    }

                    if (event.isCancelled()) {
                        return itemstack;
                    }

                    if (!event.getItem().equals(craftItem)) {
                        // Chain to handler for new item
                        ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                        IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                        if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                            idispensebehavior.dispense(isourceblock, eventStack);
                            return itemstack;
                        }
                    }

                    itembucket = (ItemBucket) CraftItemStack.asNMSCopy(event.getItem()).getItem();
                }
                // CraftBukkit end

                if (itembucket.a((EntityHuman) null, world, blockposition, (MovingObjectPosition) null)) {
                    itembucket.a(world, itemstack, blockposition);
                    // CraftBukkit start - Handle stacked buckets
                    Item item = Items.BUCKET;
                    itemstack.subtract(1);
                    if (itemstack.isEmpty()) {
                        itemstack.setItem(Items.BUCKET);
                        itemstack.setCount(1);
                    } else if (((TileEntityDispenser) isourceblock.getTileEntity()).addItem(new ItemStack(item)) < 0) {
                        this.a.dispense(isourceblock, new ItemStack(item));
                    }
                    // CraftBukkit end
                    return itemstack;
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
                    FluidType fluidtype = ((IFluidSource) block).removeFluid(DummyGeneratorAccess.INSTANCE, blockposition, iblockdata); // CraftBukkit

                    if (!(fluidtype instanceof FluidTypeFlowing)) {
                        return super.a(isourceblock, itemstack);
                    } else {
                        Item item = fluidtype.b();

                        // CraftBukkit start
                        org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                        CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                        BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                        if (!BlockDispenser.eventFired) {
                            world.getServer().getPluginManager().callEvent(event);
                        }

                        if (event.isCancelled()) {
                            return itemstack;
                        }

                        if (!event.getItem().equals(craftItem)) {
                            // Chain to handler for new item
                            ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                            IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                            if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                                idispensebehavior.dispense(isourceblock, eventStack);
                                return itemstack;
                            }
                        }

                        fluidtype = ((IFluidSource) block).removeFluid(world, blockposition, iblockdata); // From above
                        // CraftBukkit end

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
        BlockDispenser.a((IMaterial) Items.FLINT_AND_STEEL, (IDispenseBehavior) (new DispenserRegistry.DispenseBehaviorMaybe() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                this.a = true;
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));

                if (ItemFlintAndSteel.a((GeneratorAccess) world, blockposition)) {
                    // CraftBukkit start - Ignition by dispensing flint and steel
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition, isourceblock.getBlockPosition()).isCancelled()) {
                        world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                    }
                    // CraftBukkit end
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
        BlockDispenser.a((IMaterial) Items.BONE_MEAL, (IDispenseBehavior) (new DispenserRegistry.DispenseBehaviorMaybe() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                this.a = true;
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                // CraftBukkit start
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(0, 0, 0));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                world.captureTreeGeneration = true;
                // CraftBukkit end

                if (!ItemBoneMeal.a(itemstack, world, blockposition) && !ItemBoneMeal.a(itemstack, world, blockposition, (EnumDirection) null)) {
                    this.a = false;
                } else if (!world.isClientSide) {
                    world.triggerEffect(2005, blockposition, 0);
                }
                // CraftBukkit start
                world.captureTreeGeneration = false;
                if (world.capturedBlockStates.size() > 0) {
                    TreeType treeType = BlockSapling.treeType;
                    BlockSapling.treeType = null;
                    Location location = new Location(world.getWorld(), blockposition.getX(), blockposition.getY(), blockposition.getZ());
                    List<org.bukkit.block.BlockState> blocks = (List<org.bukkit.block.BlockState>) world.capturedBlockStates.clone();
                    world.capturedBlockStates.clear();
                    StructureGrowEvent structureEvent = null;
                    if (treeType != null) {
                        structureEvent = new StructureGrowEvent(location, treeType, false, null, blocks);
                        org.bukkit.Bukkit.getPluginManager().callEvent(structureEvent);
                    }
                    if (structureEvent == null || !structureEvent.isCancelled()) {
                        for (org.bukkit.block.BlockState blockstate : blocks) {
                            blockstate.update(true);
                        }
                    }
                }
                // CraftBukkit end

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Blocks.TNT, (IDispenseBehavior) (new DispenseBehaviorItem() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                // EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, (double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D, (EntityLiving) null);

                // CraftBukkit start
                ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
                org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

                BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector((double) blockposition.getX() + 0.5D, (double) blockposition.getY(), (double) blockposition.getZ() + 0.5D));
                if (!BlockDispenser.eventFired) {
                   world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    itemstack.add(1);
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    itemstack.add(1);
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }

                EntityTNTPrimed entitytntprimed = new EntityTNTPrimed(world, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), (EntityLiving) null);
                // CraftBukkit end

                world.addEntity(entitytntprimed);
                world.a((EntityHuman) null, entitytntprimed.locX, entitytntprimed.locY, entitytntprimed.locZ, SoundEffects.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                // itemstack.subtract(1); // CraftBukkit - handled above
                return itemstack;
            }
        }));
        DispenserRegistry.DispenseBehaviorMaybe dispenserregistry_dispensebehaviormaybe = new DispenserRegistry.DispenseBehaviorMaybe() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                this.a = !ItemArmor.a(isourceblock, itemstack).isEmpty();
                return itemstack;
            }
        };

        BlockDispenser.a((IMaterial) Items.CREEPER_HEAD, (IDispenseBehavior) dispenserregistry_dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.ZOMBIE_HEAD, (IDispenseBehavior) dispenserregistry_dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.DRAGON_HEAD, (IDispenseBehavior) dispenserregistry_dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.SKELETON_SKULL, (IDispenseBehavior) dispenserregistry_dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.PLAYER_HEAD, (IDispenseBehavior) dispenserregistry_dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.WITHER_SKELETON_SKULL, (IDispenseBehavior) (new DispenserRegistry.DispenseBehaviorMaybe() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

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
        BlockDispenser.a((IMaterial) Blocks.CARVED_PUMPKIN, (IDispenseBehavior) (new DispenserRegistry.DispenseBehaviorMaybe() {
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.e().get(BlockDispenser.FACING));
                BlockPumpkinCarved blockpumpkincarved = (BlockPumpkinCarved) Blocks.CARVED_PUMPKIN;

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    world.getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

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
                // CraftBukkit start - easier than fixing the decompile
                DataConverterFlattenData.a(1440, "{Name:\'minecraft:portal\',Properties:{axis:\'x\'}}", new String[]{"{Name:\'minecraft:portal\',Properties:{axis:\'x\'}}"});

                DataConverterMaterialId.ID_MAPPING.put(409, "minecraft:prismarine_shard");
                DataConverterMaterialId.ID_MAPPING.put(410, "minecraft:prismarine_crystals");
                DataConverterMaterialId.ID_MAPPING.put(411, "minecraft:rabbit");
                DataConverterMaterialId.ID_MAPPING.put(412, "minecraft:cooked_rabbit");
                DataConverterMaterialId.ID_MAPPING.put(413, "minecraft:rabbit_stew");
                DataConverterMaterialId.ID_MAPPING.put(414, "minecraft:rabbit_foot");
                DataConverterMaterialId.ID_MAPPING.put(415, "minecraft:rabbit_hide");
                DataConverterMaterialId.ID_MAPPING.put(416, "minecraft:armor_stand");

                DataConverterMaterialId.ID_MAPPING.put(423, "minecraft:mutton");
                DataConverterMaterialId.ID_MAPPING.put(424, "minecraft:cooked_mutton");
                DataConverterMaterialId.ID_MAPPING.put(425, "minecraft:banner");
                DataConverterMaterialId.ID_MAPPING.put(426, "minecraft:end_crystal");
                DataConverterMaterialId.ID_MAPPING.put(427, "minecraft:spruce_door");
                DataConverterMaterialId.ID_MAPPING.put(428, "minecraft:birch_door");
                DataConverterMaterialId.ID_MAPPING.put(429, "minecraft:jungle_door");
                DataConverterMaterialId.ID_MAPPING.put(430, "minecraft:acacia_door");
                DataConverterMaterialId.ID_MAPPING.put(431, "minecraft:dark_oak_door");
                DataConverterMaterialId.ID_MAPPING.put(432, "minecraft:chorus_fruit");
                DataConverterMaterialId.ID_MAPPING.put(433, "minecraft:chorus_fruit_popped");
                DataConverterMaterialId.ID_MAPPING.put(434, "minecraft:beetroot");
                DataConverterMaterialId.ID_MAPPING.put(435, "minecraft:beetroot_seeds");
                DataConverterMaterialId.ID_MAPPING.put(436, "minecraft:beetroot_soup");
                DataConverterMaterialId.ID_MAPPING.put(437, "minecraft:dragon_breath");
                DataConverterMaterialId.ID_MAPPING.put(438, "minecraft:splash_potion");
                DataConverterMaterialId.ID_MAPPING.put(439, "minecraft:spectral_arrow");
                DataConverterMaterialId.ID_MAPPING.put(440, "minecraft:tipped_arrow");
                DataConverterMaterialId.ID_MAPPING.put(441, "minecraft:lingering_potion");
                DataConverterMaterialId.ID_MAPPING.put(442, "minecraft:shield");
                DataConverterMaterialId.ID_MAPPING.put(443, "minecraft:elytra");
                DataConverterMaterialId.ID_MAPPING.put(444, "minecraft:spruce_boat");
                DataConverterMaterialId.ID_MAPPING.put(445, "minecraft:birch_boat");
                DataConverterMaterialId.ID_MAPPING.put(446, "minecraft:jungle_boat");
                DataConverterMaterialId.ID_MAPPING.put(447, "minecraft:acacia_boat");
                DataConverterMaterialId.ID_MAPPING.put(448, "minecraft:dark_oak_boat");
                DataConverterMaterialId.ID_MAPPING.put(449, "minecraft:totem_of_undying");
                DataConverterMaterialId.ID_MAPPING.put(450, "minecraft:shulker_shell");
                DataConverterMaterialId.ID_MAPPING.put(452, "minecraft:iron_nugget");
                DataConverterMaterialId.ID_MAPPING.put(453, "minecraft:knowledge_book");

                DataConverterSpawnEgg.ID_MAPPING[23] = "Arrow";
                // CraftBukkit end
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

    static class d extends DispenserRegistry.DispenseBehaviorMaybe {

        private d() {}

        protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
            this.a = false;
            Item item = itemstack.getItem();

            if (item instanceof ItemBlock) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.e().get(BlockDispenser.FACING);
                BlockPosition blockposition = isourceblock.getBlockPosition().shift(enumdirection);
                EnumDirection enumdirection1 = isourceblock.getWorld().isEmpty(blockposition.down()) ? enumdirection : EnumDirection.UP;

                // CraftBukkit start
                org.bukkit.block.Block bukkitBlock = isourceblock.getWorld().getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
                CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack);

                BlockDispenseEvent event = new BlockDispenseEvent(bukkitBlock, craftItem.clone(), new org.bukkit.util.Vector(blockposition.getX(), blockposition.getY(), blockposition.getZ()));
                if (!BlockDispenser.eventFired) {
                    isourceblock.getWorld().getServer().getPluginManager().callEvent(event);
                }

                if (event.isCancelled()) {
                    return itemstack;
                }

                if (!event.getItem().equals(craftItem)) {
                    // Chain to handler for new item
                    ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                    IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                    if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                        idispensebehavior.dispense(isourceblock, eventStack);
                        return itemstack;
                    }
                }
                // CraftBukkit end

                this.a = ((ItemBlock) item).a((BlockActionContext) (new DispenserRegistry.b(isourceblock.getWorld(), blockposition, enumdirection, itemstack, enumdirection1))) == EnumInteractionResult.SUCCESS;
                if (this.a) {
                    itemstack.subtract(1);
                }
            }

            return itemstack;
        }
    }

    public abstract static class DispenseBehaviorMaybe extends DispenseBehaviorItem {

        protected boolean a = true;

        public DispenseBehaviorMaybe() {}

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

            // EntityBoat entityboat = new EntityBoat(world, d0, d1 + d3, d2);
            // CraftBukkit start
            ItemStack itemstack1 = itemstack.cloneAndSubtract(1);
            org.bukkit.block.Block block = world.getWorld().getBlockAt(isourceblock.getBlockPosition().getX(), isourceblock.getBlockPosition().getY(), isourceblock.getBlockPosition().getZ());
            CraftItemStack craftItem = CraftItemStack.asCraftMirror(itemstack1);

            BlockDispenseEvent event = new BlockDispenseEvent(block, craftItem.clone(), new org.bukkit.util.Vector(d0, d1 + d3, d2));
            if (!BlockDispenser.eventFired) {
                world.getServer().getPluginManager().callEvent(event);
            }

            if (event.isCancelled()) {
                itemstack.add(1);
                return itemstack;
            }

            if (!event.getItem().equals(craftItem)) {
                itemstack.add(1);
                // Chain to handler for new item
                ItemStack eventStack = CraftItemStack.asNMSCopy(event.getItem());
                IDispenseBehavior idispensebehavior = (IDispenseBehavior) BlockDispenser.REGISTRY.get(eventStack.getItem());
                if (idispensebehavior != IDispenseBehavior.NONE && idispensebehavior != this) {
                    idispensebehavior.dispense(isourceblock, eventStack);
                    return itemstack;
                }
            }

            EntityBoat entityboat = new EntityBoat(world, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
            // CraftBukkit end

            entityboat.setType(this.b);
            entityboat.yaw = enumdirection.l();
            if (!world.addEntity(entityboat)) itemstack.add(1); // CraftBukkit
            // itemstack.subtract(1); // CraftBukkit - handled during event processing
            return itemstack;
        }

        protected void a(ISourceBlock isourceblock) {
            isourceblock.getWorld().triggerEffect(1000, isourceblock.getBlockPosition(), 0);
        }
    }
}
