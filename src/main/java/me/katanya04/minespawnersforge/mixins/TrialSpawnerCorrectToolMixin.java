package me.katanya04.minespawnersforge.mixins;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Blocks.class)
public class TrialSpawnerCorrectToolMixin {

    @ModifyArg(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/world/level/block/state/BlockBehaviour$Properties;)Lnet/minecraft/world/level/block/Block;", ordinal = 825), method = "<clinit>", index = 2)
    private static BlockBehaviour.Properties injected(BlockBehaviour.Properties properties) {
        return properties.requiresCorrectToolForDrops();
    }
}
