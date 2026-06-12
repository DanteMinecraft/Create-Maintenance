package net.dantemc.create_maintenance;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber
public class IgnorePlacingRulesItem extends BlockItem {

    public IgnorePlacingRulesItem(Block block, Properties properties) {
        super(block, properties);
    }

    @SubscribeEvent
    public static void alwaysPlace(PlayerInteractEvent.RightClickBlock event) {

        ItemStack stack = event.getItemStack();

        if (!(stack.getItem() instanceof IgnorePlacingRulesItem))
            return;

        event.setUseBlock(TriState.FALSE);
    }
}
