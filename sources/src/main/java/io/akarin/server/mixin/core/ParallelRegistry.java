package io.akarin.server.mixin.core;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.akarin.api.LogWrapper;
import net.minecraft.server.BiomeBase;
import net.minecraft.server.Block;
import net.minecraft.server.BlockFire;
import net.minecraft.server.DispenserRegistry;
import net.minecraft.server.Enchantment;
import net.minecraft.server.EntityTypes;
import net.minecraft.server.Item;
import net.minecraft.server.MobEffectList;
import net.minecraft.server.PotionBrewer;
import net.minecraft.server.PotionRegistry;
import net.minecraft.server.SoundEffect;

@Mixin(value = DispenserRegistry.class, remap = false)
public class ParallelRegistry {
    private static final ThreadFactory STAGE_FACTORY = new ThreadFactoryBuilder().setNameFormat("Parallel Registry Thread - %1$d").build();
    
    /**
     * Registry order: SoundEffect -> Block -> BlockFire -> Item -> PotionBrewer -> BiomeBase
     */
    private static final ExecutorService STAGE_A = Executors.newSingleThreadExecutor(STAGE_FACTORY); // TODO go deeper!
    /**
     * Registry order: MobEffectList -> PotionRegistry
     */
    private static final ExecutorService STAGE_B = Executors.newSingleThreadExecutor(STAGE_FACTORY);
    /**
     * Registry order: Enchantment -> EntityTypes
     */
    private static final ExecutorService STAGE_C = Executors.newSingleThreadExecutor(STAGE_FACTORY);
    
    private static final int TERMINATION_IN_SEC = 30;
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/SoundEffect.b()V"
    ))
    private static void soundEffect() {
        STAGE_A.execute(() -> SoundEffect.b());
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/Block.w()V"
    ))
    private static void block() {
        STAGE_A.execute(() -> Block.w());
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/BlockFire.e()V"
    ))
    private static void blockFire() {
        STAGE_A.execute(() -> BlockFire.e());
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/MobEffectList.k()V"
    ))
    private static void mobEffectList() {
        STAGE_B.execute(() -> MobEffectList.k());
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/Enchantment.g()V"
    ))
    private static void enchantment() {
        STAGE_C.execute(() -> Enchantment.g());
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/Item.t()V"
    ))
    private static void item() {
        STAGE_A.execute(() -> Item.t());
    }
    
    @Redirect(method = "c", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PotionRegistry.b()V"
    ))
    private static void potionRegistry() {
        STAGE_B.execute(() -> PotionRegistry.b());
    }
    
    @Redirect(method = "c", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PotionBrewer.a()V"
    ))
    private static void potionBrewer() {
        STAGE_A.execute(() -> PotionBrewer.a());
    }
    
    @Redirect(method = "c", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/EntityTypes.c()V"
    ))
    private static void entityTypes() {
        STAGE_C.execute(() -> EntityTypes.c());
    }
    
    @Redirect(method = "c", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/BiomeBase.q()V"
    ))
    private static void biomeBase() {
        STAGE_A.execute(() -> BiomeBase.q());
    }
    
    @Inject(method = "c", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/DispenserRegistry.b()V",
            shift = At.Shift.BEFORE
    ))
    private static void await(CallbackInfo info) throws InterruptedException {
        STAGE_A.shutdown();
        STAGE_B.shutdown();
        STAGE_C.shutdown();
        STAGE_A.awaitTermination(TERMINATION_IN_SEC, TimeUnit.SECONDS);
        STAGE_B.awaitTermination(TERMINATION_IN_SEC, TimeUnit.SECONDS);
        STAGE_C.awaitTermination(TERMINATION_IN_SEC, TimeUnit.SECONDS);
    }
}
