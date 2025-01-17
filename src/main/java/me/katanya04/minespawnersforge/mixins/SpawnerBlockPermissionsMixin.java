package me.katanya04.minespawnersforge.mixins;

import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin that allows non-creative players to place a spawner without it's NBT resetting
 */
@Mixin(SpawnerBlockEntity.class)
public class SpawnerBlockPermissionsMixin {
    @Inject(at = @At(value = "HEAD"), method = "onlyOpCanSetNbt", cancellable = true)
    private void injected(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
