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

import io.akarin.server.core.AkarinGlobalConfig;
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
     * Registry order: SoundEffect -> Block
     */
    private static final ExecutorService STAGE_BLOCK = Executors.newSingleThreadExecutor(STAGE_FACTORY);
    /**
     * Registry order: Item -> PotionBrewer & orderless: BlockFire, BiomeBase (After STAGE_BLOCK)
     */
    private static final ExecutorService STAGE_BLOCK_BASE  = Executors.newWorkStealingPool(3);
    
    /**
     * Registry order: MobEffectList -> PotionRegistry & orderless: Enchantment, EntityTypes
     */
    private static final ExecutorService STAGE_STANDALONE = Executors.newWorkStealingPool(3);
    
    private static final int TERMINATION_IN_SEC = AkarinGlobalConfig.registryTerminationSeconds;
    
    // We should keep the original order in codes thought orderless in runtime
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/SoundEffect.b()V"
    ))
    private static void soundEffect() {
        STAGE_BLOCK.execute(() -> {
            SoundEffect.b();
            Block.w();
            
            STAGE_BLOCK_BASE.execute(() -> BlockFire.e()); // This single task only cost ~4ms, however, firing a task only takes ~1ms
            STAGE_BLOCK_BASE.execute(() -> {
                Item.t();
                PotionBrewer.a();
            });
            STAGE_BLOCK_BASE.execute(() -> BiomeBase.q());
        });
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/Block.w()V"
    ))
    private static void block() {} // STAGE_BLOCK
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/BlockFire.e()V"
    ))
    private static void blockFire() {} // STAGE_BLOCK_BASE
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/MobEffectList.k()V"
    ))
    private static void mobEffectList() {} // STAGE_STANDALONE
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/Enchantment.g()V"
    ))
    private static void enchantment() {
        STAGE_STANDALONE.execute(() -> Enchantment.g());
        STAGE_STANDALONE.execute(() -> EntityTypes.c());
        STAGE_STANDALONE.execute(() -> {
            MobEffectList.k();
            PotionRegistry.b();
        });
    }
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/Item.t()V"
    ))
    private static void item() {} // STAGE_BLOCK_BASE
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PotionRegistry.b()V"
    ))
    private static void potionRegistry() {} // STAGE_STANDALONE
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/PotionBrewer.a()V"
    ))
    private static void potionBrewer() {} // STAGE_BLOCK_BASE
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/EntityTypes.c()V"
    ))
    private static void entityTypes() {} // STAGE_STANDALONE
    
    @Redirect(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/BiomeBase.q()V"
    ))
    private static void biomeBase() {} // STAGE_BLOCK_BASE
    
    @Inject(method = "c()V", at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/DispenserRegistry.b()V",
            shift = At.Shift.BEFORE
    ))
    private static void await(CallbackInfo info) throws InterruptedException {
        // Shutdown BLOCK and STANDALONE stage
        STAGE_STANDALONE.shutdown();
        STAGE_BLOCK.shutdown();
        STAGE_BLOCK.awaitTermination(TERMINATION_IN_SEC, TimeUnit.SECONDS);
        
        STAGE_BLOCK_BASE.shutdown(); // This must after STAGE_BLOCK terminated
        STAGE_BLOCK_BASE.awaitTermination(TERMINATION_IN_SEC, TimeUnit.SECONDS);

        STAGE_STANDALONE.awaitTermination(TERMINATION_IN_SEC, TimeUnit.SECONDS); // Behind the shutdown of BLOCK_BASE should be faster
    }
}
