package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import net.minecraft.server.MathHelper;

@Mixin(value = MathHelper.class, remap = false)
public abstract class MixinMathHelper {
	private static final int[] SINE_TABLE_INT = new int[16384 + 1];
	private static final float SINE_TABLE_MIDPOINT;
	@Overwrite
	public static float sin(float f) {
        return lookup((int) (f * 10430.38) & 0xFFFF);
    }
	@Overwrite
    public static float cos(float f) {
        return lookup((int) (f * 10430.38 + 16384.0) & 0xFFFF);
    }
	private static float lookup(int index) {
        if (index == 32768) {
            return SINE_TABLE_MIDPOINT;
        }
        int neg = (index & 0x8000) << 16;
        int mask = (index << 17) >> 31;
        int pos = (0x8001 & mask) + (index ^ mask);
        pos &= 0x7fff;
        return Float.intBitsToFloat(SINE_TABLE_INT[pos] ^ neg);
    }
	static {
		int i;
		
		final float[] SINE_TABLE = new float[65536];
		for (i = 0; i < 65536; ++i) {
            SINE_TABLE[i] = (float) Math.sin((double) i * 3.141592653589793D * 2.0D / 65536.0D);
        }
        for (i = 0; i < SINE_TABLE_INT.length; i++) {
            SINE_TABLE_INT[i] = Float.floatToRawIntBits(SINE_TABLE[i]);
        }

        SINE_TABLE_MIDPOINT = SINE_TABLE[SINE_TABLE.length / 2];
        for (i = 0; i < SINE_TABLE.length; i++) {
            float expected = SINE_TABLE[i];
            float value = lookup(i);

            if (expected != value) {
                throw new IllegalArgumentException(String.format("LUT error at index %d (expected: %s, found: %s)", i, expected, value));
            }
        }
	}
}
