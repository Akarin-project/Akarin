package io.akarin.server.mixin.optimization;

import java.io.File;
import java.io.FileOutputStream;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.destroystokyo.paper.exception.ServerInternalException;

import net.minecraft.server.IDataManager;
import net.minecraft.server.MCUtil;
import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.PersistentBase;
import net.minecraft.server.PersistentCollection;

@Mixin(value = PersistentCollection.class, remap = false)
public abstract class MixinPersistentCollection {
    @Shadow(aliases = "b") @Final private IDataManager dataManager;
    
    @Overwrite
    private void a(PersistentBase persistentbase) {
        if (this.dataManager == null) return;
        
        File file = this.dataManager.getDataFile(persistentbase.id);
        if (file == null) return;
        
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.set("data", persistentbase.b(new NBTTagCompound()));
        
        // Akarin start
        MCUtil.scheduleAsyncTask(() -> {
            try {
                FileOutputStream fileoutputstream = new FileOutputStream(file);
                
                NBTCompressedStreamTools.a(nbttagcompound, fileoutputstream);
                fileoutputstream.close();
            } catch (Exception exception) {
                exception.printStackTrace();
                ServerInternalException.reportInternalException(exception); // Paper
            }
        });
        // Akarin end
    }
}
