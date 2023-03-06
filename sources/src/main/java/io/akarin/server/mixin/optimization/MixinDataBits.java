package io.akarin.server.mixin.optimization;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.server.DataBits;

@Mixin(value = DataBits.class, remap = false)
public abstract class MixinDataBits {
	@Shadow @Final private long[] a;
	@Shadow @Final private int b;
	@Shadow @Final private long c;
	
	@Overwrite
	public void a(int i, int j) {
        int k = i * this.b;
        int l = k >> 6;
        int i1 = (i + 1) * this.b - 1 >> 6;
        int j1 = k ^ l << 6;

        this.a[l] = this.a[l] & ~(this.c << j1) | ((long) j & this.c) << j1;
        if (l != i1) {
            int k1 = 64 - j1;
            int l1 = this.b - k1;

            this.a[i1] = this.a[i1] >>> l1 << l1 | ((long) j & this.c) >> k1;
        }

    }
	@Overwrite
    public int a(int i) {
        int j = i * this.b;
        int k = j >> 6;
        int l = (i + 1) * this.b - 1 >> 6;
        int i1 = j ^ k << 6;

        if (k == l) {
            return (int) (this.a[k] >>> i1 & this.c);
        } else {
            int j1 = 64 - i1;

            return (int) ((this.a[k] >>> i1 | this.a[l] << j1) & this.c);
        }
    }
}
