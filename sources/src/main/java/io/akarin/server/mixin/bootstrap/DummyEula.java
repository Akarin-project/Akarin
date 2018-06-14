package io.akarin.server.mixin.bootstrap;

import java.io.File;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import net.minecraft.server.EULA;

@Mixin(value = EULA.class, remap = false)
public abstract class DummyEula {
    /**
     * Read then check the EULA file <i>formerly</i>
     * @param file
     * @return true
     */
    @Overwrite
    public boolean a(File file) {
        return true;
    }
    
    /**
     * Check whether the EULA has been accepted <i>formerly</i>
     * @return true
     */
    @Overwrite
    public boolean a() {
        return true;
    }
    
    /**
     * Generate an EULA file <i>formerly</i>
     */
    @Overwrite
    public void b() {}
}
