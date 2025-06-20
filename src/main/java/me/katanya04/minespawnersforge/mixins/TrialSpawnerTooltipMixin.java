package me.katanya04.minespawnersforge.mixins;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * When adding tooltip, take mob info from "SpawnData" nbt tag if it's a spawner, or from "spawn_data" if it's a trial
 * spawner (See <a href="https://github.com/katanya04/mine-spawners/issues/6#issuecomment-2890670499">Issue #6</a> and
 * <a href="https://report.bugs.mojang.com/servicedesk/customer/portal/2/MC-298744">this issue at Mojira</a>)
 */
@Mixin(ItemStack.class)
public class TrialSpawnerTooltipMixin {
    @ModifyArg(method = "addDetailsToTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Spawner;appendHoverText(Lnet/minecraft/world/item/component/CustomData;Ljava/util/function/Consumer;Ljava/lang/String;)V"), index = 2)
    private String injected(String spawnDataKey) {
        ItemStack thisItem = (ItemStack) (Object) this;
        return thisItem.is(Items.SPAWNER) ? "SpawnData" : "spawn_data";
    }
}