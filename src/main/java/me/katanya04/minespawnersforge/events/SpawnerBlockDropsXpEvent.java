package me.katanya04.minespawnersforge.events;

import me.katanya04.minespawnersforge.Mine_spawners_forge;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.SpawnerBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Event listening on block break event to see if it's a spawner, and remove the drop xp if it was
 * mined with a pickaxe
 */
@Mod.EventBusSubscriber(modid = Mine_spawners_forge.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SpawnerBlockDropsXpEvent {
    @SubscribeEvent
    public static void onMineSpawner(BlockEvent.BreakEvent event) {
        BlockState block = event.getState();
        if (block == null || !(block.getBlock() instanceof SpawnerBlock))
            return;
        Player player = event.getPlayer();
        if (player == null)
            return;
        ItemStack tool = player.getMainHandItem();
        if (tool.isCorrectToolForDrops(block) && tool.getAllEnchantments().containsKey(Enchantments.SILK_TOUCH))
            event.setExpToDrop(0);
    }
}