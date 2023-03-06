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
	@Shadow public abstract boolean isSprinting();
	@Shadow public double motX;
	@Shadow public double motY;
	@Shadow public double motZ;
	@Shadow public float yaw;
	@Shadow public float pitch;
	@Shadow public boolean impulse;
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
        motY = (double) ct();
        if (hasEffect(MobEffects.JUMP)) {
            motY += (double) ((float) (getEffect(MobEffects.JUMP).getAmplifier() + 1) * 0.1F);
        }

        if (canCrit&&isSprinting()) {
            float f = yaw * 0.017453292F;

            motX -= (double) (MathHelper.sin(f) * 0.2F);
            motZ += (double) (MathHelper.cos(f) * 0.2F);
        }

        impulse = true;
    }
}
