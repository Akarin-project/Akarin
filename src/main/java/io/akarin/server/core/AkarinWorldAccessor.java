package io.akarin.server.core;

import lombok.RequiredArgsConstructor;
import net.minecraft.server.BlockPosition;
import net.minecraft.server.Entity;
import net.minecraft.server.EntityHuman;
import net.minecraft.server.IBlockAccess;
import net.minecraft.server.IBlockData;
import net.minecraft.server.IWorldAccess;
import net.minecraft.server.NavigationListener;
import net.minecraft.server.ParticleParam;
import net.minecraft.server.SoundCategory;
import net.minecraft.server.SoundEffect;
import net.minecraft.server.WorldManager;

@RequiredArgsConstructor
public class AkarinWorldAccessor implements IWorldAccess {
    private final WorldManager worldManager;
    private final NavigationListener navigationListener;
    
    @Override
    public void a(Entity arg0) {
        worldManager.a(arg0);
        navigationListener.a(arg0);
    }
    
    @Override
    public void a(int arg0, BlockPosition arg1, int arg2) {
        worldManager.a(arg0, arg1, arg2);
    }
    
    @Override
    public void a(EntityHuman arg0, int arg1, BlockPosition arg2, int arg3) {
        worldManager.a(arg0, arg1, arg2, arg3);
    }
    
    @Override
    public void a(IBlockAccess arg0, BlockPosition arg1, IBlockData arg2, IBlockData arg3, int arg4) {
        worldManager.a(arg0, arg1, arg2, arg3, arg4);
        navigationListener.a(arg0, arg1, arg2, arg3, arg4);
    }
    
    @Override
    public void a(EntityHuman arg0, SoundEffect arg1, SoundCategory arg2, double arg3, double arg4, double arg5, float arg6, float arg7) {
        worldManager.a(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7);
    }
    
    @Override
    public void b(Entity arg0) {
        worldManager.b(arg0);
        navigationListener.b(arg0);
    }
    
    @Override
    public void b(int arg0, BlockPosition arg1, int arg2) {
        worldManager.b(arg0, arg1, arg2);
    }
    
    // unused
    @Override
    @Deprecated
    public void a(BlockPosition arg0) {
        ;
    }
    
    @Override
    @Deprecated
    public void a(SoundEffect arg0, BlockPosition arg1) {
        ;
    }
    
    @Override
    @Deprecated
    public void a(int arg0, int arg1, int arg2, int arg3, int arg4, int arg5) {
        ;
    }
    
    @Override
    @Deprecated
    public void a(ParticleParam arg0, boolean arg1, double arg2, double arg3, double arg4, double arg5, double arg6, double arg7) {
        ;
    }
    
    @Override
    @Deprecated
    public void a(ParticleParam arg0, boolean arg1, boolean arg2, double arg3, double arg4, double arg5, double arg6, double arg7, double arg8) {
        ;
    }
}