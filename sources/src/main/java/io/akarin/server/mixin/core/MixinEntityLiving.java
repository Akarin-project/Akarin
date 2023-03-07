package io.akarin.server.mixin.core;

import javax.annotation.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.EntityLiving;
import net.minecraft.server.EntityPlayer;
import net.minecraft.server.MathHelper;
import net.minecraft.server.MobEffect;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.MobEffects;

@Mixin(value = EntityLiving.class, remap = false)
public abstract class MixinEntityLiving {
	@Shadow public abstract boolean hasEffect(MobEffectList mobeffectlist);
	@Shadow @Nullable public abstract MobEffect getEffect(MobEffectList mobeffectlist);
	@Shadow protected abstract float ct();
	protected long lastJumpTime = 0L; // Dionysus - Backport ArrowDMG fix
	
	@Overwrite
	protected void cu() {
        // Dionysus start - Backport ArrowDMG fix
        long time = System.nanoTime();
        boolean canCrit = true;
        if ((Object)this instanceof EntityPlayer) {
            canCrit = false;
            if (time - lastJumpTime > (long)(0.250e9)) {
                lastJumpTime = time;
                canCrit = true;
            }
        }
        // Dionysus end - Backport ArrowDMG fix
        ((EntityLiving)(Object)this).motY = (double) ct();
        if (hasEffect(MobEffects.JUMP)) {
        	((EntityLiving)(Object)this).motY += (double) ((float) (getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
        }

        if (canCrit&&((EntityLiving)(Object)this).isSprinting()) {
            float f = ((EntityLiving)(Object)this).yaw * 0.017453292F;

            ((EntityLiving)(Object)this).motX -= (double) (MathHelper.sin(f) * 0.2F);
            ((EntityLiving)(Object)this).motZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        ((EntityLiving)(Object)this).impulse = true;
    }
}
