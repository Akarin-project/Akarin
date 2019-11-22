package net.minecraft.server;

import java.util.Iterator;
import java.util.List;
import java.util.Random;
// CraftBukkit start
import org.bukkit.Location;
import org.bukkit.TreeType;
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.craftbukkit.util.DummyGeneratorAccess;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.world.StructureGrowEvent;
// CraftBukkit end

public interface IDispenseBehavior {

    IDispenseBehavior NONE = (isourceblock, itemstack) -> {
        return itemstack;
    };

    ItemStack dispense(ISourceBlock isourceblock, ItemStack itemstack);

    static void c() {
        BlockDispenser.a((IMaterial) Items.ARROW, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            @Override
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entitytippedarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        }));
        BlockDispenser.a((IMaterial) Items.TIPPED_ARROW, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            @Override
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntityTippedArrow entitytippedarrow = new EntityTippedArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entitytippedarrow.b(itemstack);
                entitytippedarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entitytippedarrow;
            }
        }));
        BlockDispenser.a((IMaterial) Items.SPECTRAL_ARROW, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            @Override
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                EntitySpectralArrow entityspectralarrow = new EntitySpectralArrow(world, iposition.getX(), iposition.getY(), iposition.getZ());

                entityspectralarrow.fromPlayer = EntityArrow.PickupStatus.ALLOWED;
                return entityspectralarrow;
            }
        }));
        BlockDispenser.a((IMaterial) Items.EGG, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            @Override
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return (IProjectile) SystemUtils.a((new EntityEgg(world, iposition.getX(), iposition.getY(), iposition.getZ())), (entityegg) -> { // CraftBukkit - decompile error
                    entityegg.setItem(itemstack);
                });
            }
        }));
        BlockDispenser.a((IMaterial) Items.SNOWBALL, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            @Override
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return (IProjectile) SystemUtils.a((new EntitySnowball(world, iposition.getX(), iposition.getY(), iposition.getZ())), (entitysnowball) -> { // CraftBukkit - decompile error
                    entitysnowball.setItem(itemstack);
                });
            }
        }));
        BlockDispenser.a((IMaterial) Items.EXPERIENCE_BOTTLE, (IDispenseBehavior) (new DispenseBehaviorProjectile() {
            @Override
            protected IProjectile a(World world, IPosition iposition, ItemStack itemstack) {
                return (IProjectile) SystemUtils.a((new EntityThrownExpBottle(world, iposition.getX(), iposition.getY(), iposition.getZ())), (entitythrownexpbottle) -> { // CraftBukkit - decompile error
                    entitythrownexpbottle.setItem(itemstack);
                });
            }

            @Override
            protected float a() {
                return super.a() * 0.5F;
            }

            @Override
            protected float getPower() {
                return super.getPower() * 1.25F;
            }
        }));
        BlockDispenser.a((IMaterial) Items.SPLASH_POTION, new IDispenseBehavior() {
            @Override
            public ItemStack dispense(ISourceBlock isourceblock, ItemStack itemstack) {
                return (new DispenseBehaviorProjectile() {
                    @Override
                    protected IProjectile a(World world, IPosition iposition, ItemStack itemstack1) {
                        return (IProjectile) SystemUtils.a((new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ())), (entitypotion) -> { // CraftBukkit - decompile error
                            entitypotion.setItem(itemstack1);
                        });
                    }

                    @Override
                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).dispense(isourceblock, itemstack);
            }
        });
        BlockDispenser.a((IMaterial) Items.LINGERING_POTION, new IDispenseBehavior() {
            @Override
            public ItemStack dispense(ISourceBlock isourceblock, ItemStack itemstack) {
                return (new DispenseBehaviorProjectile() {
                    @Override
                    protected IProjectile a(World world, IPosition iposition, ItemStack itemstack1) {
                        return (IProjectile) SystemUtils.a((new EntityPotion(world, iposition.getX(), iposition.getY(), iposition.getZ())), (entitypotion) -> { // CraftBukkit - decompile error
                            entitypotion.setItem(itemstack1);
                        });
                    }

                    @Override
                    protected float a() {
                        return super.a() * 0.5F;
                    }

                    @Override
                    protected float getPower() {
                        return super.getPower() * 1.25F;
                    }
                }).dispense(isourceblock, itemstack);
            }
        });
        DispenseBehaviorItem dispensebehavioritem = new DispenseBehaviorItem() {
            @Override
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
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

                try { // Paper
                entitytypes.spawnCreature(isourceblock.getWorld(), itemstack, (EntityHuman) null, isourceblock.getBlockPosition().shift(enumdirection), EnumMobSpawn.DISPENSER, enumdirection != EnumDirection.UP, false);
                    // Paper start
                } catch (Exception ex){
                    MinecraftServer.LOGGER.warn("An exception occurred dispensing entity at {}[{}]", world.getWorld().getName(), isourceblock.getBlockPosition(), ex);
                }
                // Paper end

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
            @Override
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
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
                isourceblock.getWorld().addEntity(new EntityFireworks(isourceblock.getWorld(), event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ(), itemstack1));
                // itemstack.subtract(1); // Handled during event processing
                // CraftBukkit end
                return itemstack;
            }

            @Override
            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1004, isourceblock.getBlockPosition(), 0);
            }
        }));
        BlockDispenser.a((IMaterial) Items.FIRE_CHARGE, (IDispenseBehavior) (new DispenseBehaviorItem() {
            @Override
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
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

                EntitySmallFireball entitysmallfireball = new EntitySmallFireball(world, d0, d1, d2, event.getVelocity().getX(), event.getVelocity().getY(), event.getVelocity().getZ());
                entitysmallfireball.b(itemstack);
                entitysmallfireball.projectileSource = new org.bukkit.craftbukkit.projectiles.CraftBlockProjectileSource((TileEntityDispenser) isourceblock.getTileEntity());

                world.addEntity(entitysmallfireball);
                // itemstack.subtract(1); // Handled during event processing
                // CraftBukkit end
                return itemstack;
            }

            @Override
            protected void a(ISourceBlock isourceblock) {
                isourceblock.getWorld().triggerEffect(1018, isourceblock.getBlockPosition(), 0);
            }
        }));
        BlockDispenser.a((IMaterial) Items.OAK_BOAT, (IDispenseBehavior) (new DispenseBehaviorBoat(EntityBoat.EnumBoatType.OAK)));
        BlockDispenser.a((IMaterial) Items.SPRUCE_BOAT, (IDispenseBehavior) (new DispenseBehaviorBoat(EntityBoat.EnumBoatType.SPRUCE)));
        BlockDispenser.a((IMaterial) Items.BIRCH_BOAT, (IDispenseBehavior) (new DispenseBehaviorBoat(EntityBoat.EnumBoatType.BIRCH)));
        BlockDispenser.a((IMaterial) Items.JUNGLE_BOAT, (IDispenseBehavior) (new DispenseBehaviorBoat(EntityBoat.EnumBoatType.JUNGLE)));
        BlockDispenser.a((IMaterial) Items.DARK_OAK_BOAT, (IDispenseBehavior) (new DispenseBehaviorBoat(EntityBoat.EnumBoatType.DARK_OAK)));
        BlockDispenser.a((IMaterial) Items.ACACIA_BOAT, (IDispenseBehavior) (new DispenseBehaviorBoat(EntityBoat.EnumBoatType.ACACIA)));
        DispenseBehaviorItem dispensebehavioritem1 = new DispenseBehaviorItem() {
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            @Override
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                ItemBucket itembucket = (ItemBucket) itemstack.getItem();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
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

                if (itembucket.a((EntityHuman) null, world, blockposition, (MovingObjectPositionBlock) null)) {
                    itembucket.a(world, itemstack, blockposition);
                    // CraftBukkit start - Handle stacked buckets
                    Item item = Items.BUCKET;
                    itemstack.subtract(1);
                    if (itemstack.isEmpty()) {
                        itemstack.setItem(Items.BUCKET);
                        itemstack.setCount(1);
                    } else if (((TileEntityDispenser) isourceblock.getTileEntity()).addItem(new ItemStack(item)) < 0) {
                        this.b.dispense(isourceblock, new ItemStack(item));
                    }
                    // CraftBukkit end
                    return itemstack;
                } else {
                    return this.b.dispense(isourceblock, itemstack);
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
            private final DispenseBehaviorItem b = new DispenseBehaviorItem();

            @Override
            public ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
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
                                this.b.dispense(isourceblock, new ItemStack(item));
                            }

                            return itemstack;
                        }
                    }
                } else {
                    return super.a(isourceblock, itemstack);
                }
            }
        }));
        BlockDispenser.a((IMaterial) Items.FLINT_AND_STEEL, (IDispenseBehavior) (new DispenseBehaviorMaybe() {
            @Override
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

                this.dispensed = true;
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
                IBlockData iblockdata = world.getType(blockposition);

                if (ItemFlintAndSteel.a(iblockdata, (GeneratorAccess) world, blockposition)) {
                    // CraftBukkit start - Ignition by dispensing flint and steel
                    if (!org.bukkit.craftbukkit.event.CraftEventFactory.callBlockIgniteEvent(world, blockposition, isourceblock.getBlockPosition()).isCancelled()) {
                        world.setTypeUpdate(blockposition, Blocks.FIRE.getBlockData());
                    }
                    // CraftBukkit end
                } else if (ItemFlintAndSteel.a(iblockdata)) {
                    world.setTypeUpdate(blockposition, (IBlockData) iblockdata.set(BlockProperties.r, true));
                } else if (iblockdata.getBlock() instanceof BlockTNT) {
                    BlockTNT.a(world, blockposition);
                    world.a(blockposition, false);
                } else {
                    this.dispensed = false;
                }

                if (this.dispensed && itemstack.isDamaged(1, world.random, (EntityPlayer) null)) {
                    itemstack.setCount(0);
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Items.BONE_MEAL, (IDispenseBehavior) (new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                this.dispensed = true;
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
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
                    this.dispensed = false;
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
            @Override
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
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
                world.playSound((EntityHuman) null, entitytntprimed.locX, entitytntprimed.locY, entitytntprimed.locZ, SoundEffects.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0F, 1.0F);
                // itemstack.subtract(1); // CraftBukkit - handled above
                return itemstack;
            }
        }));
        DispenseBehaviorMaybe dispensebehaviormaybe = new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                this.dispensed = !ItemArmor.a(isourceblock, itemstack).isEmpty();
                return itemstack;
            }
        };

        BlockDispenser.a((IMaterial) Items.CREEPER_HEAD, (IDispenseBehavior) dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.ZOMBIE_HEAD, (IDispenseBehavior) dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.DRAGON_HEAD, (IDispenseBehavior) dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.SKELETON_SKULL, (IDispenseBehavior) dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.PLAYER_HEAD, (IDispenseBehavior) dispensebehaviormaybe);
        BlockDispenser.a((IMaterial) Items.WITHER_SKELETON_SKULL, (IDispenseBehavior) (new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                EnumDirection enumdirection = (EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING);
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

                this.dispensed = true;
                if (world.isEmpty(blockposition) && BlockWitherSkull.b(world, blockposition, itemstack)) {
                    world.setTypeAndData(blockposition, (IBlockData) Blocks.WITHER_SKELETON_SKULL.getBlockData().set(BlockSkull.a, enumdirection.k() == EnumDirection.EnumAxis.Y ? 0 : enumdirection.opposite().get2DRotationValue() * 4), 3);
                    TileEntity tileentity = world.getTileEntity(blockposition);

                    if (tileentity instanceof TileEntitySkull) {
                        BlockWitherSkull.a(world, blockposition, (TileEntitySkull) tileentity);
                    }

                    itemstack.subtract(1);
                } else if (ItemArmor.a(isourceblock, itemstack).isEmpty()) {
                    this.dispensed = false;
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Blocks.CARVED_PUMPKIN, (IDispenseBehavior) (new DispenseBehaviorMaybe() {
            @Override
            protected ItemStack a(ISourceBlock isourceblock, ItemStack itemstack) {
                World world = isourceblock.getWorld();
                BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
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

                this.dispensed = true;
                if (world.isEmpty(blockposition) && blockpumpkincarved.a((IWorldReader) world, blockposition)) {
                    if (!world.isClientSide) {
                        world.setTypeAndData(blockposition, blockpumpkincarved.getBlockData(), 3);
                    }

                    itemstack.subtract(1);
                } else {
                    ItemStack itemstack1 = ItemArmor.a(isourceblock, itemstack);

                    if (itemstack1.isEmpty()) {
                        this.dispensed = false;
                    }
                }

                return itemstack;
            }
        }));
        BlockDispenser.a((IMaterial) Blocks.SHULKER_BOX.getItem(), (IDispenseBehavior) (new DispenseBehaviorShulkerBox()));
        EnumColor[] aenumcolor = EnumColor.values();
        int i = aenumcolor.length;

        for (int j = 0; j < i; ++j) {
            EnumColor enumcolor = aenumcolor[j];

            BlockDispenser.a((IMaterial) BlockShulkerBox.a(enumcolor).getItem(), (IDispenseBehavior) (new DispenseBehaviorShulkerBox()));
        }

        BlockDispenser.a((IMaterial) Items.SHEARS.getItem(), (IDispenseBehavior) (new DispenseBehaviorMaybe() {
            @Override
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

                if (!world.e()) {
                    this.dispensed = false;
                    BlockPosition blockposition = isourceblock.getBlockPosition().shift((EnumDirection) isourceblock.getBlockData().get(BlockDispenser.FACING));
                    List<EntitySheep> list = world.a(EntitySheep.class, new AxisAlignedBB(blockposition));
                    Iterator iterator1 = list.iterator();

                    while (iterator1.hasNext()) {
                        EntitySheep entitysheep = (EntitySheep) iterator1.next();

                        if (entitysheep.isAlive() && !entitysheep.isSheared() && !entitysheep.isBaby()) {
                            // CraftBukkit start
                            if (CraftEventFactory.callBlockShearEntityEvent(entitysheep, bukkitBlock, craftItem).isCancelled()) {
                                continue;
                            }
                            // CraftBukkit end
                            entitysheep.shear();
                            if (itemstack.isDamaged(1, world.random, (EntityPlayer) null)) {
                                itemstack.setCount(0);
                            }

                            this.dispensed = true;
                            break;
                        }
                    }
                }

                return itemstack;
            }
        }));
    }
}
