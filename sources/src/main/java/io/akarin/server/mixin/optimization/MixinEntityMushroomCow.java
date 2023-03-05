package io.akarin.server.mixin.optimization;

import org.bukkit.event.player.PlayerShearEntityEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.Blocks;
import net.minecraft.server.EntityCow;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.EntityItem;
import net.minecraft.server.EntityMushroomCow;
import net.minecraft.server.EnumHand;
import net.minecraft.server.EnumParticle;
import net.minecraft.server.ItemStack;
import net.minecraft.server.Items;
import net.minecraft.server.SoundEffects;
import net.minecraft.server.World;

@Mixin(value = EntityMushroomCow.class, remap = false)
public abstract class MixinEntityMushroomCow extends EntityCow {

	public MixinEntityMushroomCow(World world) {
		super(world);
	}
	
	@Overwrite
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
			if (this.dead) return false; // Reaper - Fix cow dupe
            // CraftBukkit start
            PlayerShearEntityEvent event = new PlayerShearEntityEvent((org.bukkit.entity.Player) entityhuman.getBukkitEntity(), this.getBukkitEntity());
            this.world.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end
            this.die();
            this.world.addParticle(EnumParticle.EXPLOSION_LARGE, this.locX, this.locY + (double) (this.length / 2.0F), this.locZ, 0.0D, 0.0D, 0.0D, new int[0]);
            if (!this.world.isClientSide) {
                EntityCow entitycow = new EntityCow(this.world);

                entitycow.setPositionRotation(this.locX, this.locY, this.locZ, this.yaw, this.pitch);
                entitycow.setHealth(this.getHealth());
                entitycow.aN = this.aN;
                if (this.hasCustomName()) {
                    entitycow.setCustomName(this.getCustomName());
                }

                this.world.addEntity(entitycow);

                for (int i = 0; i < 5; ++i) {
                    this.world.addEntity(new EntityItem(this.world, this.locX, this.locY + (double) this.length, this.locZ, new ItemStack(Blocks.RED_MUSHROOM)));
                }

                itemstack.damage(1, entityhuman);
                this.a(SoundEffects.ei, 1.0F, 1.0F);
            }

            return true;
        } else {
            return super.a(entityhuman, enumhand);
        }
    }

}
