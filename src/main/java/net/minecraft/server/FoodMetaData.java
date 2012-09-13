package net.minecraft.server;

import org.bukkit.event.entity.EntityDamageEvent; // CraftBukkit

public class FoodMetaData {

    // CraftBukkit start - all made public
    public int foodLevel = 20;
    public float saturationLevel = 5.0F;
    public float exhaustionLevel;
    public int foodTickTimer = 0;
    // CraftBukkit end
    private int e = 20;

    public FoodMetaData() {}

    public void eat(int i, float f) {
        this.foodLevel = Math.min(i + this.foodLevel, 20);
        this.saturationLevel = Math.min(this.saturationLevel + (float) i * f * 2.0F, (float) this.foodLevel);
    }

    public void a(ItemFood itemfood) {
        this.eat(itemfood.getNutrition(), itemfood.getSaturationModifier());
    }

    public void a(EntityHuman entityhuman) {
        int i = entityhuman.world.difficulty;

        this.e = this.foodLevel;
        if (this.exhaustionLevel > 4.0F) {
            this.exhaustionLevel -= 4.0F;
            if (this.saturationLevel > 0.0F) {
                this.saturationLevel = Math.max(this.saturationLevel - 1.0F, 0.0F);
            } else if (i > 0) {
                // CraftBukkit start
                org.bukkit.event.entity.FoodLevelChangeEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callFoodLevelChangeEvent(entityhuman, Math.max(this.foodLevel - 1, 0));

                if (!event.isCancelled()) {
                    this.foodLevel = event.getFoodLevel();
                }

                ((EntityPlayer) entityhuman).netServerHandler.sendPacket(new Packet8UpdateHealth(entityhuman.getHealth(), this.foodLevel, this.saturationLevel));
                // CraftBukkit end
            }
        }

        if (this.foodLevel >= 18 && entityhuman.bM()) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                // CraftBukkit - added RegainReason
                entityhuman.heal(1, org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason.SATIATED);
                this.foodTickTimer = 0;
            }
        } else if (this.foodLevel <= 0) {
            ++this.foodTickTimer;
            if (this.foodTickTimer >= 80) {
                if (entityhuman.getHealth() > 10 || i >= 3 || entityhuman.getHealth() > 1 && i >= 2) {
                    // CraftBukkit start
                    EntityDamageEvent event = new EntityDamageEvent(entityhuman.getBukkitEntity(), EntityDamageEvent.DamageCause.STARVATION, 1);
                    entityhuman.world.getServer().getPluginManager().callEvent(event);

                    if (!event.isCancelled()) {
                        event.getEntity().setLastDamageCause(event);
                        entityhuman.damageEntity(DamageSource.STARVE, event.getDamage());
                    }
                    // CraftBukkit end
                }

                this.foodTickTimer = 0;
            }
        } else {
            this.foodTickTimer = 0;
        }
    }

    public void a(NBTTagCompound nbttagcompound) {
        if (nbttagcompound.hasKey("foodLevel")) {
            this.foodLevel = nbttagcompound.getInt("foodLevel");
            this.foodTickTimer = nbttagcompound.getInt("foodTickTimer");
            this.saturationLevel = nbttagcompound.getFloat("foodSaturationLevel");
            this.exhaustionLevel = nbttagcompound.getFloat("foodExhaustionLevel");
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInt("foodLevel", this.foodLevel);
        nbttagcompound.setInt("foodTickTimer", this.foodTickTimer);
        nbttagcompound.setFloat("foodSaturationLevel", this.saturationLevel);
        nbttagcompound.setFloat("foodExhaustionLevel", this.exhaustionLevel);
    }

    public int a() {
        return this.foodLevel;
    }

    public boolean c() {
        return this.foodLevel < 20;
    }

    public void a(float f) {
        this.exhaustionLevel = Math.min(this.exhaustionLevel + f, 40.0F);
    }

    public float e() {
        return this.saturationLevel;
    }
}
