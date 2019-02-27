package net.minecraft.server;

import javax.annotation.Nullable;

// CraftBukkit start
import org.bukkit.craftbukkit.event.CraftEventFactory;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.player.PlayerShearEntityEvent;
// CraftBukkit end

public class EntityMushroomCow extends EntityCow {

    public EntityMushroomCow(World world) {
        super(EntityTypes.MOOSHROOM, world);
        this.setSize(0.9F, 1.4F);
        this.bF = Blocks.MYCELIUM;
    }

    public boolean a(EntityHuman entityhuman, EnumHand enumhand) {
        ItemStack itemstack = entityhuman.b(enumhand);

        if (itemstack.getItem() == Items.BOWL && this.getAge() >= 0 && !entityhuman.abilities.canInstantlyBuild) {
            itemstack.subtract(1);
            if (itemstack.isEmpty()) {
                entityhuman.a(enumhand, new ItemStack(Items.MUSHROOM_STEW));
            } else if (!entityhuman.inventory.pickup(new ItemStack(Items.MUSHROOM_STEW))) {
                entityhuman.drop(new ItemStack(Items.MUSHROOM_STEW), false);
            }

            return true;
        } else if (itemstack.getItem() == Items.SHEARS && this.getAge() >= 0) {
            // CraftBukkit start
            PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), this.getBukkitEntity());
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            this.world.addParticle(Particles.u, this.locX, this.locY + (double) (this.length / 2.0F), this.locZ, 0.0D, 0.0D, 0.0D);
            if (!this.world.isClientSide) {
                // this.die(); // CraftBukkit - moved down
                EntityCow entitycow = new EntityCow(this.world);

                entitycow.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
                entitycow.setHealth(this.getHealth());
                entitycow.aQ = this.aQ;
                if (this.hasCustomName()) {
                    entitycow.setCustomName(this.getCustomName());
                }

                // CraftBukkit start
                if (CraftEventFactory.callEntityTransformEvent(this, entitycow, EntityTransformEvent.TransformReason.SHEARED).isCancelled()) {
                    return false;
                }
                this.world.addEntity(entitycow, org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason.SHEARED);

                this.die(); // CraftBukkit - from above
                // CraftBukkit end

                for (int i = 0; i < 5; ++i) {
                    this.world.addEntity(new EntityItem(this.world, this.locX, this.locY + (double) this.length, this.locZ, new ItemStack(Blocks.RED_MUSHROOM)));
                }

                itemstack.damage(1, entityhuman);
                this.a(SoundEffects.ENTITY_MOOSHROOM_SHEAR, 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

    public EntityMushroomCow createChild(EntityAgeable entityageable) {
        return new EntityMushroomCow(this.world);
    }

    @Nullable
    protected MinecraftKey getDefaultLootTable() {
        return LootTables.T;
    }
}
