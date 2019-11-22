package net.minecraft.server;

import com.google.common.collect.Multimap;

public class ItemTrident extends Item {

    public ItemTrident(Item.Info item_info) {
        super(item_info);
        // CraftBukkit start - obfuscator went a little crazy
        /*
        this.a(new MinecraftKey("throwing"), (itemstack, world, entityliving) -> {
            return entityliving != null && entityliving.isHandRaised() && entityliving.dl() == itemstack ? 1.0F : 0.0F;
        });
         */
        // CraftBukkit end
    }

    @Override
    public boolean a(IBlockData iblockdata, World world, BlockPosition blockposition, EntityHuman entityhuman) {
        return !entityhuman.isCreative();
    }

    @Override
    public EnumAnimation e_(ItemStack itemstack) {
        return EnumAnimation.SPEAR;
    }

    @Override
    public int f_(ItemStack itemstack) {
        return 72000;
    }

    @Override
    public void a(ItemStack itemstack, World world, EntityLiving entityliving, int i) {
        if (entityliving instanceof EntityHuman) {
            EntityHuman entityhuman = (EntityHuman) entityliving;
            int j = this.f_(itemstack) - i;

            if (j >= 10) {
                int k = EnchantmentManager.g(itemstack);

                if (k <= 0 || entityhuman.isInWaterOrRain()) {
                    if (!world.isClientSide) {
                        // CraftBukkit - moved down
                        /*
                        itemstack.damage(1, entityhuman, (entityhuman1) -> {
                            entityhuman1.d(entityliving.getRaisedHand());
                        });
                        */
                        if (k == 0) {
                            EntityThrownTrident entitythrowntrident = new EntityThrownTrident(world, entityhuman, itemstack);

                            entitythrowntrident.a(entityhuman, entityhuman.pitch, entityhuman.yaw, 0.0F, 2.5F + (float) k * 0.5F, 1.0F);
                            if (entityhuman.abilities.canInstantlyBuild) {
                                entitythrowntrident.fromPlayer = EntityArrow.PickupStatus.CREATIVE_ONLY;
                            }

                            // CraftBukkit start
                            if (!world.addEntity(entitythrowntrident)) {
                                if (entityhuman instanceof EntityPlayer) {
                                    ((EntityPlayer) entityhuman).getBukkitEntity().updateInventory();
                                }
                                return;
                            }

                            itemstack.damage(1, entityhuman, (entityhuman1) -> {
                                entityhuman1.d(entityliving.getRaisedHand());
                            });
                            entitythrowntrident.trident = itemstack.cloneItemStack(); // SPIGOT-4511 update since damage call moved
                            // CraftBukkit end

                            world.playSound((EntityHuman) null, (Entity) entitythrowntrident, SoundEffects.ITEM_TRIDENT_THROW, SoundCategory.PLAYERS, 1.0F, 1.0F);
                            if (!entityhuman.abilities.canInstantlyBuild) {
                                entityhuman.inventory.f(itemstack);
                            }
                        }
                    }

                    entityhuman.b(StatisticList.ITEM_USED.b(this));
                    if (k > 0) {
                        // CraftBukkit start
                        org.bukkit.event.player.PlayerRiptideEvent event = new org.bukkit.event.player.PlayerRiptideEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), org.bukkit.craftbukkit.inventory.CraftItemStack.asCraftMirror(itemstack));
                        event.getPlayer().getServer().getPluginManager().callEvent(event);
                        // CraftBukkit end
                        float f = entityhuman.yaw;
                        float f1 = entityhuman.pitch;
                        float f2 = -MathHelper.sin(f * 0.017453292F) * MathHelper.cos(f1 * 0.017453292F);
                        float f3 = -MathHelper.sin(f1 * 0.017453292F);
                        float f4 = MathHelper.cos(f * 0.017453292F) * MathHelper.cos(f1 * 0.017453292F);
                        float f5 = MathHelper.c(f2 * f2 + f3 * f3 + f4 * f4);
                        float f6 = 3.0F * ((1.0F + (float) k) / 4.0F);

                        f2 *= f6 / f5;
                        f3 *= f6 / f5;
                        f4 *= f6 / f5;
                        entityhuman.f((double) f2, (double) f3, (double) f4);
                        entityhuman.q(20);
                        if (entityhuman.onGround) {
                            float f7 = 1.1999999F;

                            entityhuman.move(EnumMoveType.SELF, new Vec3D(0.0D, 1.1999999284744263D, 0.0D));
                        }

                        SoundEffect soundeffect;

                        if (k >= 3) {
                            soundeffect = SoundEffects.ITEM_TRIDENT_RIPTIDE_3;
                        } else if (k == 2) {
                            soundeffect = SoundEffects.ITEM_TRIDENT_RIPTIDE_2;
                        } else {
                            soundeffect = SoundEffects.ITEM_TRIDENT_RIPTIDE_1;
                        }

                        world.playSound((EntityHuman) null, (Entity) entityhuman, soundeffect, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    }

                }
            }
        }
    }

    @Override
    public InteractionResultWrapper<ItemStack> a(World world, EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getDamage() >= itemstack.h()) {
            return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
        } else if (EnchantmentManager.g(itemstack) > 0 && !entityhuman.isInWaterOrRain()) {
            return new InteractionResultWrapper<>(EnumInteractionResult.FAIL, itemstack);
        } else {
            entityhuman.c(enumhand);
            return new InteractionResultWrapper<>(EnumInteractionResult.SUCCESS, itemstack);
        }
    }

    @Override
    public boolean a(ItemStack itemstack, EntityLiving entityliving, EntityLiving entityliving1) {
        itemstack.damage(1, entityliving1, (entityliving2) -> {
            entityliving2.c(EnumItemSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean a(ItemStack itemstack, World world, IBlockData iblockdata, BlockPosition blockposition, EntityLiving entityliving) {
        if ((double) iblockdata.f(world, blockposition) != 0.0D) {
            itemstack.damage(2, entityliving, (entityliving1) -> {
                entityliving1.c(EnumItemSlot.MAINHAND);
            });
        }

        return true;
    }

    @Override
    public Multimap<String, AttributeModifier> a(EnumItemSlot enumitemslot) {
        Multimap<String, AttributeModifier> multimap = super.a(enumitemslot);

        if (enumitemslot == EnumItemSlot.MAINHAND) {
            multimap.put(GenericAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ItemTrident.g, "Tool modifier", 8.0D, AttributeModifier.Operation.ADDITION));
            multimap.put(GenericAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ItemTrident.h, "Tool modifier", -2.9000000953674316D, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public int c() {
        return 1;
    }
}
